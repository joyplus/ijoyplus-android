package com.joy;

import com.joy.Tools.Tools;
import com.joy.weibo.net.AccessToken;
import com.joy.weibo.net.Oauth2AccessTokenHeader;
import com.joy.weibo.net.Utility;
import com.joy.weibo.net.Weibo;
import com.tencent.tauth.bean.OpenId;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

public class Appstart extends Activity {
	GetThird_AccessToken getThird_AccessToken;
	Intent intent;
	private static final String SINA_CONSUMER_KEY = "3069972161";// 替换为开发者的appkey，例如"1646212960";
	private static final String SINA_CONSUMER_SECRET = "eea5ede316c6a283c6bae57e52c9a877";
	final Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 300:
				
					Intent intent=new Intent();
					intent.setClass(Appstart.this, JoyActivity.class);
					startActivity(intent);
					finish();
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appstart);
		getThird_AccessToken = (GetThird_AccessToken) getApplicationContext();
		Tools.creat("joy");
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run(){
				Tools.creat("joy/ijoyplus");
				intent = new Intent();
				getThird_AccessToken.GetAccessToken();
				getThird_AccessToken.GetExit();
				//判断是否正常退出
				if (getThird_AccessToken.getexit().equals("false")){
					getThird_AccessToken.setAccessToken("");
					getThird_AccessToken.setExpires_in("");
					getThird_AccessToken.SaveAccessToken();
					getThird_AccessToken.SaveExpires_in();
					getThird_AccessToken.setQQ_Token("");
					getThird_AccessToken.setOpenID("");
					getThird_AccessToken.SaveQQAccessToken();
					getThird_AccessToken.SaveOpenID();
				}
				getThird_AccessToken.setlogin_where(getString(R.string.sinawb));
				if (getThird_AccessToken.getAccessToken().trim().length()==0) {
					getThird_AccessToken.setlogin_where(getString(R.string.tencent));
					getThird_AccessToken.GetQQAccessToken();
					if (getThird_AccessToken.getQQ_Token().trim().length()==0) {
						intent.setClass(Appstart.this, Welcome.class);
						startActivity(intent);
						finish();
					}
					else
					{
						new Handler().postDelayed(new Runnable(){
							@Override
							public void run(){
								getThird_AccessToken.setQQ_Token(getThird_AccessToken.getQQ_Token().trim());
								Message msg = new Message(); 
				                msg.what = 300; 
				                handler.sendMessage(msg); 
							}
						}, 1000);
					}
				}
				else
				{
					new Handler().postDelayed(new Runnable(){
						@Override
						public void run(){
							System.out.println("do this canplay");
							Weibo.getInstance().setupConsumerConfig(SINA_CONSUMER_KEY, SINA_CONSUMER_SECRET);
							getThird_AccessToken.setAccessToken(getThird_AccessToken.getAccessToken().trim());
							getThird_AccessToken.GetExpires_in();
							Utility.setAuthorization(new Oauth2AccessTokenHeader());
							final AccessToken accessToken = new AccessToken(getThird_AccessToken.getAccessToken().trim(), SINA_CONSUMER_SECRET);
							accessToken.setExpiresIn(getThird_AccessToken.getExpires_in());
							Weibo.getInstance().setAccessToken(accessToken);
							System.out.println("accesstoken====>"+Weibo.getInstance().getAccessToken().getToken());
							Message msg = new Message(); 
			                msg.what = 300; 
			                handler.sendMessage(msg); 
						}
					}, 1000);
				}
			}
		}, 2000);
		com.umeng.common.Log.LOG = true;
		MobclickAgent.onError(this);
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
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
