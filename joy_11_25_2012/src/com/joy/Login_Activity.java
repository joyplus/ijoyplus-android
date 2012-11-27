package com.joy;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.joy.Service.DownLoadService;
import com.joy.Tools.AsyncBitmapLoader;
import com.joy.Tools.AsyncBitmapLoader.ImageCallBack;
import com.joy.Tools.BitmapZoom;
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

public class Login_Activity extends Activity implements OnClickListener {
	protected AQuery aq;
	Context context;
	EditText login_user_edit, login_passwd_edit;
	App app;
	private AuthReceiver receiver;

	public String mAccessToken, mOpenId;
	private DownLoadService DOWNLOADSERVICE;
	private boolean QQisDarentuijian = false;
	private String head_url = null;
	AsyncBitmapLoader asyncBitmapLoader = new AsyncBitmapLoader();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		context = this;
		app = (App) getApplicationContext();
		into();
		login_user_edit = (EditText) findViewById(R.id.login_user_edit);
		login_passwd_edit = (EditText) findViewById(R.id.login_passwd_edit);
		// veteranyu add
		aq = new AQuery(this);
		DOWNLOADSERVICE = app.getService();
		QQisDarentuijian = false;
		// *************************************************************************
	}

	/* 声明登陆界面所有按键 */
	public void into() {
		Button Sina_weibo = (Button) findViewById(R.id.Sina_weibo);
		Sina_weibo.setOnClickListener(this);
		Button QQ_weibo = (Button) findViewById(R.id.QQ_weibo);
		QQ_weibo.setOnClickListener(this);
		Button goin = (Button) findViewById(R.id.goin);
		goin.setOnClickListener(this);
	}

	public void Btn_login_goback(View v) {
		/*
		 * Intent intent=new Intent(); //intent.setClass(context,
		 * Welcome.class); intent.setClass(context, JoyActivity.class);
		 * startActivity(intent);
		 */
		finish();
	}

	/* 所有按键操作 */
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.Sina_weibo:
			app.setlogin_where(getString(R.string.sinawb));
			app.GetAccessToken();
			if (app.getAccessToken().trim().length() == 0) {
				Weibo weibo = Weibo.getInstance();
				weibo.setupConsumerConfig(Constant.SINA_CONSUMER_KEY,
						Constant.SINA_CONSUMER_SECRET);
				// Oauth2.0
				// 隐式授权认证方式
				// weibo.setRedirectUrl("http://www.sina.com");//
				// 此处回调页内容应该替换为与appkey对应的应用回调页
				// 对应的应用回调页可在开发者登陆新浪微博开发平台之后，
				// 进入我的应用--应用详情--应用信息--高级信息--授权设置--应用回调页进行设置和查看，
				// 应用回调页不可为空
				// 新浪微博redirecturl改为：https://api.weibo.com/oauth2/default.html
				// zenius2004@hotmail.com/yuebao
				weibo.setRedirectUrl("https://api.weibo.com/oauth2/default.html");
				weibo.authorize(Login_Activity.this, new AuthDialogListener());
			} else {
				Weibo.getInstance().setupConsumerConfig(
						Constant.SINA_CONSUMER_KEY,
						Constant.SINA_CONSUMER_SECRET);
				app.setAccessToken(app
						.getAccessToken().trim());
				app.GetExpires_in();
				Utility.setAuthorization(new Oauth2AccessTokenHeader());
				AccessToken accessToken = new AccessToken(app
						.getAccessToken().trim(), Constant.SINA_CONSUMER_SECRET);
				accessToken.setExpiresIn(app.getExpires_in());
				Weibo.getInstance().setAccessToken(accessToken);

				System.out.println("Expires_in:"
						+ app.getExpires_in());
				System.out.println("token:"
						+ Weibo.getInstance().getAccessToken().getToken());
				System.out.println("Secret:"
						+ Weibo.getInstance().getAccessToken().getSecret());

				String name = app.getExpires_in();
				String email = name + "@sina.com";
				String password = name;

				if (DOWNLOADSERVICE.AccountLogin(email, password)) {
					app.IsLogin = true;
					// 1. save to local
					app.SaveIjoyID(email, password);
					// intent.setClass(context, JoyActivity.class);
					// startActivity(intent);
					finish();
				}

			}
			break;
		case R.id.QQ_weibo:
			// Toast.makeText(context, "尚未开放", Toast.LENGTH_SHORT).show();
			app.setlogin_where(getString(R.string.tencent));
			app.GetQQAccessToken();
			if (app.getQQ_Token().trim().length() == 0) {
				app.setQQ_Token("");
				registerIntentReceivers();
				auth(Constant.TECENTAPPID, "_self");
				// conf.initQqData();
				// intent.setClass(context, Third_PartyActivity.class);
				// startActivity(intent);
			} else {
				app.setQQ_Token(app
						.getQQ_Token().trim());
				intent.setClass(context, JoyActivity.class);
				startActivity(intent);
				finish();
			}
			break;
		case R.id.goin:
			// Toast.makeText(context, "尚未开放", Toast.LENGTH_SHORT).show();
			// if (login_user_edit.getText().toString().trim().length()==0
			// ||login_passwd_edit.getText().toString().trim().length()==0) {
			// // Toast.makeText(context, R.string.loginError,
			// Toast.LENGTH_SHORT).show();
			// app.setActivitytype("1");
			// intent.setClass(context, Darentuijian.class);
			// startActivity(intent);
			// finish();
			// }
			// else
			// {
			// intent.setClass(context, JoyActivity.class);
			// startActivity(intent);
			// finish();
			// }
			break;
		}
	}

	// 以第三方QQ账户登录
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
		aq.dismiss();
		super.onDestroy();
		if (receiver != null) {
			unregisterIntentReceivers();
		}
	}

	// 初始化广播
	private void registerIntentReceivers() {
		receiver = new AuthReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(TAuthView.AUTH_BROADCAST);
		registerReceiver(receiver, filter);
	}

	private void unregisterIntentReceivers() {
		unregisterReceiver(receiver);
	}

	// 调用一个广播以通知PC用户我利用第三方登陆
	public class AuthReceiver extends BroadcastReceiver {

		private static final String TAG = "AuthReceiver";

		// 异步加载图片
		public Bitmap setImage(ImageView imageView, String URL) {
			return asyncBitmapLoader.loadBitmap(imageView, URL, 83,
					new ImageCallBack() {

						@Override
						public void imageLoad(ImageView imageView, Bitmap bitmap) {
							if (bitmap != null) {
								BitmapZoom.bitmapZoomByWidthA(bitmap, 83);
							}
						}
					});
		}

		private String UploadQQHeadUrl(String ACCESS_TOKEN, String APP_ID,
				String OPENID) {
			String m_GetURL = "https://graph.qq.com/user/get_user_info?access_token="
					+ ACCESS_TOKEN
					+ "&oauth_consumer_key="
					+ APP_ID
					+ "&openid=" + OPENID;
			String rltString = DOWNLOADSERVICE.OnHttpGet(m_GetURL);

			try {
				JSONObject json = new JSONObject(rltString);
				head_url = json.optString("figureurl");
				// save to local
				if (head_url != null) {
					File target = new File(Constant.PATH_HEAD + "head.png");
					aq.download(head_url, target, new AjaxCallback<File>() {

						public void callback(String url, File file,
								AjaxStatus status) {
							if (file != null) {
								Log.i(TAG, "File:" + file.length() + ":" + file
										+ ",status:" + status);
							} else {
								Log.i(TAG, "Failed,status:" + status);
							}
						}
					});
				}
				// sss
			} catch (JSONException e) {
				// 不处理
			}
			return head_url;
		}

		@Override
		public void onReceive(Context context, final Intent intent) {
			final Context mContext = context;
			Bundle exts = intent.getExtras();
			String raw = exts.getString("raw");
			final String access_token = exts.getString(TAuthView.ACCESS_TOKEN);
			String expires_in = exts.getString(TAuthView.EXPIRES_IN);
			String error_ret = exts.getString(TAuthView.ERROR_RET);
			// String error_des = exts.getString(TAuthView.ERROR_DES);
			Log.i(TAG, String.format("raw: %s, access_token:%s, expires_in:%s",
					raw, access_token, expires_in));

			if (access_token != null) {
				if (!isFinishing()) {
					System.out.println("do this");
					showDialog(PROGRESS);
				}

				// 用access token 来获取open id
				TencentOpenAPI.openid(access_token, new Callback() {
					@Override
					public void onSuccess(final Object obj) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(mContext,
										getString(R.string.shouquansuess),
										Toast.LENGTH_SHORT).show();
								app.setOpenID(((OpenId) obj)
										.getOpenId());
								app.SaveOpenID();
								if (!QQisDarentuijian) {
									String UserHeadUrl = UploadQQHeadUrl(
											access_token,
											Constant.SINA_CONSUMER_KEY,
											((OpenId) obj).getOpenId());
									if (((OpenId) obj).getOpenId().length() > 0
											&& !Check_ifAccountQQExist(
													access_token,
													((OpenId) obj).getOpenId())) {
										Intent intent2 = new Intent();
										intent2.putExtra("Login", true);
										intent2.putExtra("token", access_token);
										intent2.putExtra("UserHeadUrl",
												UserHeadUrl);
										intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										intent2.setClass(mContext,
												Darentuijian.class);
										startActivity(intent2);
									}
								}
							}
						});
					}

					@Override
					public void onFail(int ret, final String msg) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								try {
									dismissDialog(PROGRESS);
								} catch (Exception e) {

								}
								TDebug.msg(msg, getApplicationContext());
							}
						});
					}
				});
				String OpenID = app.GetOpenID();
				String UserHeadUrl = UploadQQHeadUrl(access_token,
						Constant.SINA_CONSUMER_KEY, OpenID);
				if (OpenID.length() > 0
						&& !Check_ifAccountQQExist(access_token, OpenID)) {
					QQisDarentuijian = true;
					Intent intent2 = new Intent();
					intent2.putExtra("Login", true);
					intent2.putExtra("token", access_token);
					intent2.putExtra("UserHeadUrl", UserHeadUrl);
					intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent2.setClass(mContext, Darentuijian.class);
					// intent2.setClass(mContext,
					// SupplementaryInformation.class);
					/* app.setVerificationCode(url); */
					// intent.putExtra(ConfigUtil.OAUTH_VERIFIER_URL, url);
					startActivity(intent2);
				}
				finish();
			}
			if (error_ret != null) {
				Toast.makeText(context, getString(R.string.shouquanfalse),
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	public boolean satisfyConditions() {
		return mAccessToken != null && Constant.TECENTAPPID != null
				&& mOpenId != null && !mAccessToken.equals("")
				&& !Constant.TECENTAPPID.equals("") && !mOpenId.equals("");
	}

	public static final int PROGRESS = 0;

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case PROGRESS:
			dialog = new ProgressDialog(this);
			((ProgressDialog) dialog).setMessage(getString(R.string.qingqiu));
			break;
		}

		return dialog;
	}

	// 第三方QQ登陆到此

	// 第三方新浪登录
	class AuthDialogListener implements WeiboDialogListener {

		@Override
		public void onComplete(Bundle values) {
			String uid = values.getString("uid");
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			System.out.println("expires_in=====>" + expires_in);
			AccessToken accessToken = new AccessToken(token,
					Constant.SINA_CONSUMER_SECRET);
			accessToken.setExpiresIn(expires_in);
			Weibo.getInstance().setAccessToken(accessToken);
			if (!Check_ifAccountSinaExist(uid, token, expires_in)) {
				Intent intent = new Intent();
				intent.putExtra("Login", true);
				intent.putExtra("uid", uid);
				intent.putExtra("token", token);
				intent.putExtra("expires_in", expires_in);
				// intent.setClass(context, SupplementaryInformation.class);
				intent.setClass(context, Darentuijian.class);
				startActivity(intent);
			}
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

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:

			finish();
			/*
			 * JoyActivity joy = null; joy.mTabHost.setCurrentTab(0);
			 * 
			 * app.setwhere_gologin(1); Intent intent=new
			 * Intent(); //intent.setClass(context, Welcome.class);
			 * intent.setClass(context, JoyActivity.class);
			 * startActivity(intent);
			 */
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

	// uid,token,expires_in
	public boolean Check_ifAccountSinaExist(String uid, String access_token,
			String expires_in) {
		String name = uid;
		String email = name + "@sina.com";
		String password = name;
		// 3.LOGIN
		if (DOWNLOADSERVICE.AccountLogin(email, password)) {
			app.setAccessToken(access_token);
			app.setExpires_in(expires_in);
			app.SaveAccessToken();
			app.SaveExpires_in();
			// 1. save to local
			app.SaveIjoyID(email, password);
			app.IsLogin = true;
			return true;
		}
		return false;
	}

	public boolean Check_ifAccountQQExist(String token, String OpenID) {
		String name = OpenID;
		String email = name + "@qq.com";
		String password = name;

		// 3.LOGIN
		if (DOWNLOADSERVICE.AccountLogin(email, password)) {
			app.setQQ_Token(getIntent()
					.getStringExtra("token"));
			app.SaveQQAccessToken();
			// 1. save to local
			app.SaveIjoyID(email, password);
			return true;
		}

		return false;
	}

}
