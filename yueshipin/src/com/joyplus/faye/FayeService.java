package com.joyplus.faye;

import java.net.URI;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.joyplus.Constant;
import com.joyplus.faye.FayeClient.FayeListener;
import com.umeng.analytics.MobclickAgent;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class FayeService extends Service {
	public static final String CONNECTIVITY_ACTION = "net.changed";
	// JSONObject mJson;
	static FayeClient mClient;
	// private String tv_channel = null;
	private static String user_id = null;
	protected Handler hanlder;
	static AQuery aq;
	private static boolean IsConnected = false;

	public static void FayeByService(Context context, String channel) {

		// this.mJson = json;
		// this.tv_channel = channel;
		// this.user_id = userid;
		connect_TVChannel(context, channel);
	}

	public static void SendMessageService(Context mcontext, JSONObject json,
			String userid) {
		user_id = userid;
		if (IsConnected) {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					try {
						mClient.connectToServer(null);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}, 500);
		}
		mClient.sendMessage(json);

	}

	public static void CheckBindService(final Context context,
			final String channel, final String userid,
			final Map<String, String> headers) {

		// check_binding(context, userid, channel, headers);

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		aq = new AQuery(this);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		return super.onStartCommand(intent, flags, startId);
	}

	private static void connect_TVChannel(final Context mContext,
			String tv_channel) {
		Handler handler = new Handler();
		if (android.os.Build.VERSION.SDK_INT <= 8)
			return;
		try {
			URI uri = URI.create(Constant.TV_CHANNEL_URL);
			mClient = new FayeClient(handler, uri, tv_channel);
			mClient.connectToServer(null);
			mClient.setFayeListener(new FayeListener() {

				@Override
				public void subscriptionFailedWithError(String error) {
					Log.i("TVChannleListener", "subscriptionFailedWithError>>>"
							+ error);

				}

				@Override
				public void subscribedToChannel(String subscription) {
					Log.i("TVChannleListener", "subscribedToChannel>>>"
							+ subscription);

				}

				@Override
				public void messageReceived(JSONObject json) {
					Log.i("TVChannleListener",
							"messageReceived>>>" + json.toString());
					if (json.toString() == null)
						return;
					int push_type = 0;
					String userid = null;
					String result = null;
					Intent intent = new Intent();
					try {
						push_type = Integer.valueOf(json.getString("push_type"));
						userid = json.getString("user_id");
						result = json.getString("result");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					switch (push_type) {
					case 32: // 确认绑定
						if (userid.equals(user_id) && result.equals("success")) {
							intent.putExtra("status", "success");
							intent.setAction("com.joyplus.update_before_binding");
						} else if (!userid.equals(user_id)) {
							intent.putExtra("status", "fail");
							intent.setAction("com.joyplus.update_before_binding");
						}
						break;
					case 33: // 取消绑定
						if (userid.equals(user_id)) {
							intent.putExtra("status", "fail");
							intent.setAction("com.joyplus.check_binding");
						}
						break;
					case 42: // 确认投放
						if (userid.equals(user_id)) {
							intent.putExtra("yunduan", "success");
							intent.setAction("com.joyplus.yunduan");
						}
						break;
					}
					mContext.sendBroadcast(intent);

				}

				@Override
				public void disconnectedFromServer() {
					IsConnected = false;
					// mClient.connectToServer(null);
					Log.i("TVChannleListener", "disconnectedFromServer>>>");

				}

				@Override
				public void connectedToServer() {
					IsConnected = true;
					Log.i("TVChannleListener", "connectedToServer>>>");

				}
			});

		} catch (Exception ex) {
		}
	}

	// private static void check_binding(Context context, String userid,
	// String channel, Map<String, String> headers) {
	// String url = Constant.CHECK_BINDING + "?tv_channel=" + channel
	// + "&user_id=" + userid;
	// Log.i("", "url>>>" + url);
	// AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
	// cb.SetHeader(headers);
	// cb.url(url).type(JSONObject.class)
	// .weakHandler(context, "CallProgramPlayResult");
	// aq.ajax(cb);
	// }

	// public void CallProgramPlayResult(String url, JSONObject json,
	// AjaxStatus status) {
	// Intent intent = new Intent();
	// try {
	// int result = Integer.valueOf(json.getString("status"));
	// switch (result) {
	// case 1:
	// break;
	// case 0:
	// intent.putExtra("status", "fail");
	// intent.setAction("com.joyplus.check_binding");
	// sendBroadcast(intent);
	// break;
	// }
	//
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

}
