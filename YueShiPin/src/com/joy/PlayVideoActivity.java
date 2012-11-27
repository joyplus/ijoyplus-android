package com.joy;




import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.mobclick.android.MobclickAgent;

public class PlayVideoActivity extends Activity {
	ProgressDialog progressBar;
	WebView webview;
	Context context;
	TextView textName;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.webview);
        context = this;
        textName = (TextView)findViewById(R.id.textName);
        textName.setText(getString(R.string.move_name));
        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setPluginsEnabled(true);
        webview.getSettings().setPluginState(PluginState.ON);
        webview.getSettings().setSupportZoom(true);
        // 
       // webview.loadUrl("http://v.youku.com/v_show/id_XNDY1OTU5NjUy.html?f=18450484");
        webview.loadUrl("http://video.sina.cn/?sa=t424d736959v456&pos=23&vt=4");
        progressBar = ProgressDialog.show(context, getString(R.string.pleasewait), getString(R.string.loading));
        webview.setDownloadListener(new DownloadListener() {
			
			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype, long contentLength) {
				System.out.println(url);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse(url), mimetype);
				try {
					startActivity(intent);
				}catch (ActivityNotFoundException e){
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
        webview.setWebViewClient(new MainWebViewClient()
//        {
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            	System.out.println(url);
//                                            view.loadUrl(url); 
//                                            return true;  
//            }
//            public void onPageFinished(WebView view, String url) {  
//                if (progressBar.isShowing()) {  
//                    progressBar.dismiss();  
//                }
//            }
//        }
        );
	}
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		switch(keyCode)
		{
	    case KeyEvent.KEYCODE_BACK:
	    	if (webview.canGoBack()) {
	    		webview.goBack();
	    	}
	    	else
	    	{
	    		finish();
	    	}
	    	break;
		}
		   return true;
	}
	private class MainWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			System.out.println("url===>"+url);
//			  if (url.endsWith(".3gp")||url.endsWith(".mp4")||url.endsWith(".webm")||url.endsWith(".ogv")) {
//				  Intent intent = new Intent(Intent.ACTION_VIEW);
//				  intent.setData(Uri.parse(url));
//			        startActivity(intent);
//		            return true;

		        //} else {
					if (progressBar.isShowing()) {
						
					}
					else
					{
						progressBar = ProgressDialog.show(context, getString(R.string.pleasewait), getString(R.string.loading));
					}
		        	view.loadUrl(url);
		        //}
			
			return true;
		}
		public void onPageFinished(WebView view, String url) {  
            if (progressBar.isShowing()) {  
                progressBar.dismiss();  
            }
        }

	}
	public void Btn_login_goback(View v)
	{
		if (webview.canGoBack()) {
    		webview.goBack();
    	}
    	else
    	{
    		finish();
    	}
	}
	public void onResume() { 
		super.onResume();
		MobclickAgent.onResume(this); 
	} 
	public void onPause() { 
		super.onPause(); 
		MobclickAgent.onPause(this); 
	}
}
