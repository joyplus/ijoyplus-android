package com.joy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.joy.Service.DownLoadService;
import com.joy.Tools.Tools;
import com.joy.weibo.net.AccessToken;
import com.joy.weibo.net.Oauth2AccessTokenHeader;
import com.joy.weibo.net.Utility;
import com.joy.weibo.net.Weibo;
import com.mobclick.android.MobclickAgent;

public class Appstart extends Activity {
	App app;
	private DownLoadService DOWNLOADSERVICE;
	Intent intent;
	private static final String SINA_CONSUMER_KEY = Constant.SINA_CONSUMER_KEY;// 替换为开发者的appkey，例如"1646212960";
	private static final String SINA_CONSUMER_SECRET = Constant.SINA_CONSUMER_SECRET;
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 300:

				Intent intent = new Intent();
				intent.setClass(Appstart.this, JoyActivity.class);
				// intent.setClass(Appstart.this,Login_Activity.class);
				startActivity(intent);
				finish();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.appstart);
		app = (App) getApplicationContext();
		Tools.creat("joy");
		// veteranyu add
		/*
		 * if (!isNetworkAvailable()) { setNetwork(); return; }
		 */
		app.InitService();
		DOWNLOADSERVICE = app.getService();

		// *************************************************************************
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Tools.creat("joy/ijoyplus");
				intent = new Intent();
				app.GetAccessToken();
				app.GetExit();
				/*
				 * //判断是否正常退出 if
				 * (app.getexit().equals("false")){
				 * app.setAccessToken("");
				 * app.setExpires_in("");
				 * app.SaveAccessToken();
				 * app.SaveExpires_in();
				 * app.setQQ_Token("");
				 * app.setOpenID("");
				 * app.SaveQQAccessToken();
				 * app.SaveOpenID(); }
				 */
				app.setlogin_where(getString(R.string.sinawb));
				if (app.getAccessToken().trim().length() == 0) {
					app
							.setlogin_where(getString(R.string.tencent));
					app.GetQQAccessToken();

					if (app.getQQ_Token().trim().length() == 0) {
						// intent.setClass(Appstart.this, Welcome.class);
						intent.setClass(Appstart.this, JoyActivity.class);
						startActivity(intent);
						finish();
					} else {

						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								app
										.setQQ_Token(app
												.getQQ_Token().trim());
								Message msg = new Message();
								msg.what = 300;
								handler.sendMessage(msg);
							}
						}, 1000);
					}
				} else {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							System.out.println("do this canplay");
							Weibo.getInstance().setupConsumerConfig(
									SINA_CONSUMER_KEY, SINA_CONSUMER_SECRET);
							app
									.setAccessToken(app
											.getAccessToken().trim());
							app.GetExpires_in();
							Utility.setAuthorization(new Oauth2AccessTokenHeader());
							final AccessToken accessToken = new AccessToken(
									app.getAccessToken()
											.trim(), SINA_CONSUMER_SECRET);
							accessToken.setExpiresIn(app
									.getExpires_in());
							Weibo.getInstance().setAccessToken(accessToken);
							System.out.println("accesstoken====>"
									+ Weibo.getInstance().getAccessToken()
											.getToken());
							Message msg = new Message();
							msg.what = 300;
							handler.sendMessage(msg);
						}
					}, 1000);
				}
			}
		}, 1000);
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

	// NETWORK
	public boolean isNetworkAvailable() {
		Context context = getApplicationContext();
		ConnectivityManager connect = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connect == null) {
			return false;
		} else// get all network info
		{
			NetworkInfo[] info = connect.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	// setnetwork
	public void setNetwork() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle(R.string.netstate);
		builder.setMessage(R.string.setnetwork);
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
		builder.create();
		builder.show();

	}
}
