package com.joyplus.faye;



import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import com.joyplus.widget.Log;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.BasicNameValuePair;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class WebSocketClient {

    private static final String TAG = "WebSocketClient";

    private URI                      mURI;
    private Listener                 mListener;
    private Socket                   mSocket;
    private Thread                   mThread;
    private HandlerThread            mHandlerThread;
    private Handler                  mHandler;
    private Handler                  mUiHandler;
    private List<BasicNameValuePair> mExtraHeaders;
    private HybiParser               mParser;

    private final Object mSendLock = new Object();

    private static TrustManager[] sTrustManagers;

    public static void setTrustManagers(TrustManager[] tm) {
        sTrustManagers = tm;
    }

    public WebSocketClient(Handler uiHandler, URI uri, Listener listener, List<BasicNameValuePair> extraHeaders) {
        mUiHandler = uiHandler;
        mURI = uri;
        mListener = listener;
        mExtraHeaders = extraHeaders;
        mParser = new HybiParser(this);

        mHandlerThread = new HandlerThread("websocket-thread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    public Listener getListener() {
        return mListener;
    }

    public void connect() {

        if (mThread != null && mThread.isAlive()) {
            return;
        }

        mThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    int port = (mURI.getPort() != -1) ? mURI.getPort() : (mURI.getScheme().equals("wss") ? 443 : 80);

                    String path = TextUtils.isEmpty(mURI.getPath()) ? "/" : mURI.getPath();
                    if (!TextUtils.isEmpty(mURI.getQuery())) {
                        path += "?" + mURI.getQuery();
                    }

                    String originScheme = mURI.getScheme().equals("wss") ? "https" : "http";
                    URI origin = new URI(originScheme, "//" + mURI.getHost(), null);

                    SocketFactory factory = mURI.getScheme().equals("wss")
                            ? getSSLSocketFactory() : SocketFactory.getDefault();

                    mSocket = factory.createSocket(mURI.getHost(), port);

                    String secret = createSecret();

                    PrintWriter out = new PrintWriter(mSocket.getOutputStream());
                    out.print("GET " + path + " HTTP/1.1\r\n");
                    out.print("Host: " + mURI.getHost() + "\r\n");
                    out.print("Upgrade: websocket\r\n");
                    out.print("Connection: Upgrade\r\n");
                    out.print("Sec-WebSocket-Key: " + secret + "\r\n");
                    out.print("Sec-WebSocket-Version: 13\r\n");
                    out.print("Origin: " + origin.toString() + "\r\n");

                    if (mExtraHeaders != null) {
                        for (NameValuePair pair : mExtraHeaders) {
                            out.print(String.format("%s: %s\r\n", pair.getName(), pair.getValue()));
                        }
                    }

                    out.print("\r\n");
                    out.flush();

                    HybiParser.HappyDataInputStream stream =
                            new HybiParser.HappyDataInputStream(mSocket.getInputStream());

                    // Read HTTP response status line.
                    StatusLine statusLine = parseStatusLine(readLine(stream));

                    if (statusLine == null) {
                        throw new HttpException("Received no reply from server.");
                    } else if (statusLine.getStatusCode() != HttpStatus.SC_SWITCHING_PROTOCOLS) {
                        throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
                    }

                    // Read HTTP response headers.
                    String line;
                    boolean validated = false;

                    while (!TextUtils.isEmpty(line = readLine(stream))) {

                        Header header = parseHeader(line);

                        if (header.getName().equals("Sec-WebSocket-Accept")) {

                            String expected = createSecretValidation(secret);
                            String actual = header.getValue().trim();

                            if (!expected.equals(actual)) {
                                throw new HttpException("Bad Sec-WebSocket-Accept header value.");
                            }

                            validated = true;
                        }
                    }

                    if (!validated) {
                        throw new HttpException("No Sec-WebSocket-Accept header.");
                    }

                    mListener.onConnect();

                    // Now decode websocket frames.
                    mParser.start(stream);

                } catch (EOFException ex) {

                    Log.e(TAG, "WebSocket EOF!", ex);
                    onError(ex);

                } catch (SSLException ex) {

                    // Connection reset by peer
                    Log.e(TAG, "Websocket SSL error!", ex);
                    onError(ex);

                } catch (Exception ex) {
                    onError(ex);
                }
            }
        });
        mThread.start();
    }

    private void onError(final Exception ex) {

        mUiHandler.post(new Runnable() {

            @Override
            public void run() {

                mListener.onError(ex);
            }
        });
    }

    private String createSecretValidation(String secret) {

        MessageDigest md;

        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        md.update((secret + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes());

        return Base64.encodeToString(md.digest(), Base64.DEFAULT).trim();
    }

    public void disconnect() {

        if (mSocket != null) {

            mHandler.post(new Runnable() {

                @Override
                public void run() {

                    try {

                        mSocket.close();
                        mSocket = null;

                        Log.i(TAG, "socket closed");

                    } catch (IOException ex) {
                        Log.e(TAG, "Error while disconnecting", ex);
                        onError(ex);
                    }
                }
            });
        }
    }

    public void send(String data) {
        sendFrame(mParser.frame(data));
    }

    public void send(byte[] data) {
        sendFrame(mParser.frame(data));
    }

    private StatusLine parseStatusLine(String line) {

        if (TextUtils.isEmpty(line)) {
            return null;
        }

        return BasicLineParser.parseStatusLine(line, new BasicLineParser());
    }

    private Header parseHeader(String line) {
        return BasicLineParser.parseHeader(line, new BasicLineParser());
    }

    // Can't use BufferedReader because it buffers past the HTTP data.
    private String readLine(HybiParser.HappyDataInputStream reader) throws IOException {

        int readChar = reader.read();

        if (readChar == -1) {
            return null;
        }

        StringBuilder string = new StringBuilder("");

        while (readChar != '\n') {

            if (readChar != '\r') {
                string.append((char) readChar);
            }

            readChar = reader.read();

            if (readChar == -1) {
                return null;
            }
        }

        return string.toString();
    }

    private String createSecret() {

        byte[] nonce = new byte[16];

        for (int i = 0; i < 16; i++) {
            nonce[i] = (byte) (Math.random() * 256);
        }

        return Base64.encodeToString(nonce, Base64.DEFAULT).trim();
    }

    void sendFrame(final byte[] frame) {

        if (mSocket != null) {

            mHandler.post(new Runnable() {

                @Override
                public void run() {

                    try {

                        synchronized (mSendLock) {
                            OutputStream outputStream = mSocket.getOutputStream();
                            outputStream.write(frame);
                            outputStream.flush();
                        }

                    } catch (Exception e) {
                        onError(e);
                    }
                }
            });
        }
    }

    public interface Listener {
        void onConnect();
        void onMessage(String message);
        void onMessage(byte[] data);
        void onDisconnect(int code, String reason);
        void onError(Exception error);
    }

    private SSLSocketFactory getSSLSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {

        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, sTrustManagers, null);

        return context.getSocketFactory();
    }
}
