package com.ijoyplus;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.ijoyplus.weibo.net.AccessToken;
import com.ijoyplus.weibo.net.DialogError;
import com.ijoyplus.weibo.net.Weibo;
import com.ijoyplus.weibo.net.WeiboDialogListener;
import com.ijoyplus.weibo.net.WeiboException;

public class Setting extends Activity {
	private App app;
	private AQuery aq;
	private String uid = null;
	private String token = null;
	private String expires_in = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		app = (App) getApplication();
		aq = new AQuery(this);
		
		/*
		 * Switch switchTest = (Switch) findViewById(R.id.switch1); switchTest
		 * .setOnCheckedChangeListener(new
		 * CompoundButton.OnCheckedChangeListener() {
		 * 
		 * @Override public void onCheckedChanged(CompoundButton buttonView,
		 * boolean isChecked) {
		 * 
		 * } });
		 */
	}

	@Override
	protected void onDestroy() {
		if (aq != null)
			aq.dismiss();
		if(expires_in != null){
			setResult(101);
		}
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (app.GetServiceData("Sina_Access_Token") != null) 
			aq.id(R.id.checkBox1).getCheckBox().setChecked(true);
		else
			aq.id(R.id.checkBox1).getCheckBox().setChecked(false);
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	public void OnClickTab1TopRight(View v) {
		if(expires_in != null){
			setResult(101);
		}
		finish();

	}

	public void OnClickClearMemery(View v) {
		BitmapAjaxCallback.clearCache();
		app.MyToast(this, "清除缓存成功");

	}

	public void OnClickSug(View v) {
		Intent intent = new Intent(this, Z_Sug.class);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			Log.e("Setting", "Call Z_Sug failed", ex);
		}

	}

	public void OnClickPingjia(View v) {
		Intent intent = new Intent(this, Z_Pingjia.class);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			Log.e("Setting", "Call Z_Pingjia failed", ex);
		}

	}

	public void OnClickAboutUs(View v) {
		Intent intent = new Intent(this, Z_About_us.class);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			Log.e("Setting", "Call Z_About_us failed", ex);
		}

	}

	public void GetServiceData() {

	}

	public void OnClickSinaWeibo(View v) {
		if (app.GetServiceData("Sina_Access_Token") == null) {
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
			weibo.authorize(this, new AuthDialogListener());
		}else{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getResources().getString(R.string.tishi));
			builder.setMessage(getResources().getString(R.string.settingexitsinaweibo))
					.setPositiveButton(
							getResources().getString(R.string.queding),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									app.DeleteServiceData("Sina_Access_Token");
								}
							})
					.setNegativeButton(
							getResources().getString(R.string.quxiao),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {

								}
							});	  
			builder.show();
			
		}

	}

	// 第三方新浪登录
	class AuthDialogListener implements WeiboDialogListener {

		@Override
		public void onComplete(Bundle values) {
			uid = values.getString("uid");
			token = values.getString("access_token");
			expires_in = values.getString("expires_in");
			System.out.println("expires_in=====>" + expires_in);
			AccessToken accessToken = new AccessToken(token,
					Constant.SINA_CONSUMER_SECRET);
			accessToken.setExpiresIn(expires_in);
			Weibo.getInstance().setAccessToken(accessToken);
			// save access_token
			app.SaveServiceData("Sina_Access_Token", token);
			UploadSinaHeadAndScreen_nameUrl(token,uid);
			app.MyToast(getApplicationContext(), "新浪微博已绑定");
		}

	
		@Override
		public void onError(DialogError e) {
			app.MyToast(getApplicationContext(),
					"Auth error : " + e.getMessage());
		}

		@Override
		public void onCancel() {
			app.MyToast(getApplicationContext(), "Auth cancel");
		}

		@Override
		public void onWeiboException(WeiboException e) {
			app.MyToast(getApplicationContext(),
					"Auth exception : " + e.getMessage());
		}

	}
	public boolean UploadSinaHeadAndScreen_nameUrl(String access_token, String uid) {
		String m_GetURL = "https://api.weibo.com/2/users/show.json?access_token="
				+ access_token + "&uid=" + uid;

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(m_GetURL).type(JSONObject.class).weakHandler(this, "UploadSinaHeadAndScreen_nameUrlResult");

		cb.header("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
		cb.header("app_key", Constant.APPKEY);
		cb.header("user_id", app.UserID);

		aq.ajax(cb);

		return false;
	}

	public void UploadSinaHeadAndScreen_nameUrlResult(String url, JSONObject json,
			AjaxStatus status) {
		String head_url = json.optString("avatar_large");
		String screen_name = json.optString("screen_name");
		if (head_url != null && screen_name != null) {
			String m_PostURL = Constant.BASE_URL + "account/bindAccount";

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("source_id", uid);
			params.put("source_type", "1");
			params.put("pic_url", head_url);
			params.put("nickname", screen_name);
			
			//save to local
			AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
			cb.header("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
			cb.header("app_key", Constant.APPKEY);
			cb.header("user_id", app.UserID);

			cb.params(params).url(m_PostURL).type(JSONObject.class)
					.weakHandler(this, "AccountBindAccountResult");

			aq.ajax(cb);
		}
		
		
	}
	public void AccountBindAccountResult(String url, JSONObject json,
			AjaxStatus status) {
		if (json != null) {
			try {
				if (json.getString("res_code").trim().equalsIgnoreCase("00000")){
					
					//reload the userinfo
					String url2 = Constant.BASE_URL + "user/view";

					AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
					cb.url(url2).type(JSONObject.class).weakHandler(this, "AccountBindAccountResult3");

					cb.header("User-Agent",
							"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
					cb.header("app_key", Constant.APPKEY);
					cb.header("user_id", app.UserID);

					aq.ajax(cb);
				}
//				else
//					app.MyToast(this, "更新头像失败!");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {

			// ajax error, show error code
			app.MyToast(this, getResources().getString(R.string.networknotwork));
		}
	}
	public void AccountBindAccountResult3(String url, JSONObject json, AjaxStatus status) {

		if (json != null) {
			app.SaveServiceData("UserInfo", json.toString());
//			app.MyToast(this, "更新头像成功!");

		} else {

			app.MyToast(this, getResources().getString(R.string.networknotwork));
		}
	}
}
