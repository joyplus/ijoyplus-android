package com.joy;



import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.joy.oauthTools.ConfigUtil;
import com.joy.oauthTools.OAuth;
import com.mobclick.android.MobclickAgent;

public class Third_PartyActivity extends Activity {
	ProgressDialog progressBar;
	Context context;
	App app;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.third_party);
	    app = (App) getApplicationContext();
	    context = this;
	    progressBar = ProgressDialog.show(context, getString(R.string.pleasewait), getString(R.string.loading));
	    //init
	    OAuth  oAuth = OAuth.getInstance();
	    oAuth.clear();
	    
	  //获取被操作app的key、secret
	    String appKey = ConfigUtil.getInstance().getAppKey();
	    String appSecret = ConfigUtil.getInstance().getAppSecret();
	    oAuth.setKeyAndSecret(appKey, appSecret);
	    
	    String url = oAuth.getAuthorizUrl();
	   System.out.println("url====>"+url);
	    initWebView(url);
    }
	
	private void initWebView(String url) {
		WebView authorizationView = (WebView) findViewById(R.id.third_partyWebView);
	    authorizationView.clearCache(true);
	    authorizationView.getSettings().setJavaScriptEnabled(true);
	    authorizationView.getSettings().setSupportZoom(true);
	    authorizationView.getSettings().setBuiltInZoomControls(true);
	    authorizationView.setWebViewClient(new WebViewC()); 
	    authorizationView.loadUrl(url);
	}
	class WebViewC extends WebViewClient{
		private int index = 0;
		@Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
			progressBar = ProgressDialog.show(context, getString(R.string.pleasewait), getString(R.string.loading));
			view.loadUrl(url);
			view.reload();
			return true;
        }
		
		/**
		 * 由于腾讯授权页面采用https协议
		 * 		执行此方法接受所有证书
		 */
		public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error) {
			 handler.proceed() ;
		 }

		@Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
	        super.onPageStarted(view, url, favicon);
	        
	        /**
	         *  url.contains(ConfigUtil.callBackUrl)
	         *  如果授权成功url中包含之前设置的callbackurl
	         *  		包含：授权成功
	         *
	         *index == 0
	         *由于该方法onPageStarted可能被多次调用造成重复跳转
	         *		则添加此标示
	         */
	        
	        if( url.contains(ConfigUtil.callBackUrl) && index == 0){
	        	index ++;
            	//Intent intent = new Intent(context,SupplementaryInformation.class);
	        	Intent intent = new Intent(context,Darentuijian.class);
            	app.setVerificationCode(url);
            	//intent.putExtra(ConfigUtil.OAUTH_VERIFIER_URL, url);
            	startActivity(intent);
            	finish();
           }
        }

		@Override
        public void onPageFinished(WebView view, String url) {
	        // TODO Auto-generated method stub
	        super.onPageFinished(view, url);
	        if (progressBar.isShowing()) {  
                progressBar.dismiss();  
            }
        }
	}
	@Override
	public void onResume() { 
		super.onResume();
		MobclickAgent.onResume(this); 
	} 
	@Override
	public void onPause() { 
		super.onPause(); 
		MobclickAgent.onPause(this); 
	}
}
