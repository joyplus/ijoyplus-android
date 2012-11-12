package com.joy;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.joy.weibo.net.AccessToken;
import com.joy.weibo.net.DialogError;
import com.joy.weibo.net.Oauth2AccessTokenHeader;
import com.joy.weibo.net.Utility;
import com.joy.weibo.net.Weibo;
import com.joy.weibo.net.WeiboDialogListener;
import com.joy.weibo.net.WeiboException;
import com.mobclick.android.MobclickAgent;
import com.tencent.tauth.TAuthView;
import com.tencent.tauth.TencentOpenAPI;
import com.tencent.tauth.bean.OpenId;
import com.tencent.tauth.http.Callback;
import com.tencent.tauth.http.TDebug;


public class Login_Activity extends Activity implements OnClickListener{
	Context context;
	EditText login_user_edit,login_passwd_edit;
	GetThird_AccessToken getThird_AccessToken;
	private AuthReceiver receiver;
	
	public String mAccessToken, mOpenId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginactivity);
		context=this;
		getThird_AccessToken = (GetThird_AccessToken) getApplicationContext();
		into();
		login_user_edit = (EditText) findViewById(R.id.login_user_edit);
		login_passwd_edit = (EditText) findViewById(R.id.login_passwd_edit);
	}
	/*声明登陆界面所有按键*/
	public void into()
	{
		Button Sina_weibo = (Button) findViewById(R.id.Sina_weibo);
		Sina_weibo.setOnClickListener(this);
		Button QQ_weibo = (Button) findViewById(R.id.QQ_weibo);
		QQ_weibo.setOnClickListener(this);
		Button goin = (Button) findViewById(R.id.goin);
		goin.setOnClickListener(this);
	}
	public void Btn_login_goback(View v){
		Intent intent=new Intent();
		intent.setClass(context, Welcome.class);
		startActivity(intent);
		finish();
	}
	/*所有按键操作*/
	@Override
	public void onClick(View v) {
		Intent intent=new Intent();
		switch(v.getId())
		{
		case R.id.Sina_weibo:
			getThird_AccessToken.setlogin_where(getString(R.string.sinawb));
			getThird_AccessToken.GetAccessToken();
			if (getThird_AccessToken.getAccessToken().trim().length()==0) {
				Weibo weibo = Weibo.getInstance();
				weibo.setupConsumerConfig(getString(R.string.SINA_CONSUMER_KEY), getString(R.string.SINA_CONSUMER_SECRET));
				// Oauth2.0
				// 隐式授权认证方式
				weibo.setRedirectUrl("http://www.sina.com");// 此处回调页内容应该替换为与appkey对应的应用回调页
				// 对应的应用回调页可在开发者登陆新浪微博开发平台之后，
				// 进入我的应用--应用详情--应用信息--高级信息--授权设置--应用回调页进行设置和查看，
				// 应用回调页不可为空
				weibo.authorize(Login_Activity.this,
						new AuthDialogListener());
			}
			else
			{
				Weibo.getInstance().setupConsumerConfig(getString(R.string.SINA_CONSUMER_KEY), getString(R.string.SINA_CONSUMER_SECRET));
				getThird_AccessToken.setAccessToken(getThird_AccessToken.getAccessToken().trim());
				getThird_AccessToken.GetExpires_in();
				Utility.setAuthorization(new Oauth2AccessTokenHeader());
				AccessToken accessToken = new AccessToken(getThird_AccessToken.getAccessToken().trim(), getString(R.string.SINA_CONSUMER_SECRET));
				accessToken.setExpiresIn(getThird_AccessToken.getExpires_in());
				Weibo.getInstance().setAccessToken(accessToken);
				
				System.out.println("Expires_in:"+getThird_AccessToken.getExpires_in());
				System.out.println("token:"+Weibo.getInstance().getAccessToken().getToken());
				System.out.println("Secret:"+Weibo.getInstance().getAccessToken().getSecret());
				
				intent.setClass(context, JoyActivity.class);
				startActivity(intent);
				finish();
			}
			break;
		case R.id.QQ_weibo:
			//Toast.makeText(context, "尚未开放", Toast.LENGTH_SHORT).show();
			getThird_AccessToken.setlogin_where(getString(R.string.tencent));
			getThird_AccessToken.GetQQAccessToken();
			if (getThird_AccessToken.getQQ_Token().trim().length()==0) {
				getThird_AccessToken.setQQ_Token("");
				registerIntentReceivers();
				auth(getString(R.string.mAppid), "_self");
//				conf.initQqData();
//				intent.setClass(context, Third_PartyActivity.class);
//				startActivity(intent);
			}
			else
			{
				getThird_AccessToken.setQQ_Token(getThird_AccessToken.getQQ_Token().trim());
				intent.setClass(context, JoyActivity.class);
				startActivity(intent);
				finish();
			}
			break;
		case R.id.goin:
			//Toast.makeText(context, "尚未开放", Toast.LENGTH_SHORT).show();
//			if (login_user_edit.getText().toString().trim().length()==0
//			||login_passwd_edit.getText().toString().trim().length()==0) {
////				Toast.makeText(context, R.string.loginError, Toast.LENGTH_SHORT).show();
//				getThird_AccessToken.setActivitytype("1");
//				intent.setClass(context, Darentuijian.class);
//				startActivity(intent);
//				finish();
//			}
//			else
//			{
//				intent.setClass(context, JoyActivity.class);
//				startActivity(intent);
//				finish();
//			}
			break;
		}
    }
	//以第三方QQ账户登录
	private void auth(String clientId, String target) {
		Intent intent = new Intent(context, com.tencent.tauth.TAuthView.class);
		
		intent.putExtra(TAuthView.CLIENT_ID, clientId);
		intent.putExtra(TAuthView.SCOPE, getString(R.string.scope));
		intent.putExtra(TAuthView.TARGET, target);
		intent.putExtra(TAuthView.CALLBACK, getString(R.string.CALLBACK));
		startActivity(intent);
		
	}
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if (receiver != null) {
        	unregisterIntentReceivers();
    	}
    }
    
    //初始化广播
	private void registerIntentReceivers() {
		receiver =  new AuthReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(TAuthView.AUTH_BROADCAST);
		registerReceiver(receiver, filter);
	}
	
	private void unregisterIntentReceivers() {
		unregisterReceiver(receiver);
	}
	//调用一个广播以通知PC用户我利用第三方登陆
public class AuthReceiver extends BroadcastReceiver {
    	
    	private static final String TAG="AuthReceiver";

    	@Override
    	public void onReceive(Context context, Intent intent) {
    		final Context mContext = context;
 			Bundle exts = intent.getExtras();
        	String raw =  exts.getString("raw");
        	String access_token =  exts.getString(TAuthView.ACCESS_TOKEN);
        	String expires_in =  exts.getString(TAuthView.EXPIRES_IN);
        	String error_ret =  exts.getString(TAuthView.ERROR_RET);
//        	String error_des =  exts.getString(TAuthView.ERROR_DES);
        	Log.i(TAG, String.format("raw: %s, access_token:%s, expires_in:%s", raw, access_token, expires_in));
        	
        	if (access_token != null) {
        		if(!isFinishing())
        		{
        			System.out.println("do this");
        			showDialog(PROGRESS);
        		}
        		Intent intent2 = new Intent();
        		intent2.putExtra("token", access_token);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent2.setClass(mContext, SupplementaryInformation.class);
            	/*getThird_AccessToken.setVerificationCode(url);*/
            	//intent.putExtra(ConfigUtil.OAUTH_VERIFIER_URL, url);
            	startActivity(intent2);
            	finish();
        		//用access token 来获取open id
				TencentOpenAPI.openid(access_token, new Callback() {
					@Override
					public void onSuccess(final Object obj) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(mContext, getString(R.string.shouquansuess), Toast.LENGTH_SHORT).show();
								getThird_AccessToken.setOpenID(((OpenId)obj).getOpenId());
								getThird_AccessToken.SaveOpenID();
							}
						});
					}
					@Override
					public void onFail(int ret, final String msg) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								try{
									dismissDialog(PROGRESS);
								}catch(Exception e)
								{
									
								}
								TDebug.msg(msg, getApplicationContext());
							}
						});
					}
				});
			}
        	if (error_ret != null) {
        		Toast.makeText(context, getString(R.string.shouquanfalse), Toast.LENGTH_SHORT).show();
			}
    	}

    }
	public boolean satisfyConditions() {
		return 	mAccessToken != null && 
				getString(R.string.mAppid) != null && 
				mOpenId != null && 
				!mAccessToken.equals("") && 
				!getString(R.string.mAppid).equals("") && 
				!mOpenId.equals("");
	}
	
	public static final int PROGRESS = 0;
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case PROGRESS:
			dialog = new ProgressDialog(this);
			((ProgressDialog)dialog).setMessage(getString(R.string.qingqiu));
			break;
		}
		
		return dialog;
	}
//第三方QQ登陆到此
	
//第三方新浪登录
	class AuthDialogListener implements WeiboDialogListener {

		@Override
		public void onComplete(Bundle values) {
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			System.out.println("expires_in=====>"+expires_in);
			AccessToken accessToken = new AccessToken(token, getString(R.string.SINA_CONSUMER_SECRET));
			accessToken.setExpiresIn(expires_in);
			Weibo.getInstance().setAccessToken(accessToken);
			Intent intent = new Intent();
			intent.putExtra("token", token);
			intent.putExtra("expires_in", expires_in);
			intent.setClass(context, SupplementaryInformation.class);
			startActivity(intent);
			finish();
		}

		@Override
		public void onError(DialogError e) {
			Toast.makeText(getApplicationContext(),
					"Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
		}

		@Override
		public void onCancel() {
			Toast.makeText(getApplicationContext(), "Auth cancel",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(getApplicationContext(),
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}

	}
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
    	switch(keyCode){
        case KeyEvent.KEYCODE_BACK:
        	Intent intent=new Intent();
    		intent.setClass(context, Welcome.class);
    		startActivity(intent);
    		finish();
        	break;
    	}
        return true;
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
