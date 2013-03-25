package com.joyplus;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.NotificationType;
import com.umeng.fb.UMFeedbackService;
import com.umeng.xp.common.ExchangeConstants;
import com.umeng.xp.controller.ExchangeDataService;
import com.umeng.xp.controller.XpListenersCenter.NTipsChangedListener;
import com.umeng.xp.view.ExchangeViewManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.joyplus.weibo.net.AccessToken;
import com.joyplus.weibo.net.DialogError;
import com.joyplus.weibo.net.Weibo;
import com.joyplus.weibo.net.WeiboDialogListener;
import com.joyplus.weibo.net.WeiboException;
import com.joyplus.widget.InnerListView;

public class Setting extends Activity {
	private App app;
	private AQuery aq;
	private String uid = null;
	private String token = null;
	private String expires_in = null;
	
	//应用推荐
	public static ExchangeDataService preloadDataService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		app = (App) getApplication();
		aq = new AQuery(this);
		UMFeedbackService.enableNewReplyNotification(this,
				NotificationType.AlertDialog);
		//appRecommend();
		ViewGroup fatherLayout = (ViewGroup)findViewById(R.id.ad);
		InnerListView listView = (InnerListView) this.findViewById(R.id.list);
		listView.setMaxHeight(400);
		ScrollView scrollView = (ScrollView)findViewById(R.id.scrollView1);
		listView.setParentScrollView(scrollView);
		
		//赋值preloadDataService,添加newTips 回调
	    preloadDataService = new ExchangeDataService();
	    preloadDataService.preloadData(Setting.this, new NTipsChangedListener() {
	        @Override
	        public void onChanged(int flag) {
	           // TextView view = (TextView) root.findViewById(R.id.umeng_example_xp_container_tips);
	            if(flag == -1){
	                //没有new广告
	            }else if(flag > 1){
	                //第一页new广告数量
	            }else if(flag == 0){
	                //第一页全部为new 广告
	            }
	        };
	    }, ExchangeConstants.type_container);
	    ExchangeDataService exchangeDataService = preloadDataService != null ?preloadDataService : new ExchangeDataService("");
	    ExchangeViewManager exchangeViewManager = new ExchangeViewManager(this,new ExchangeDataService());
		exchangeViewManager.addView(fatherLayout, listView);
	}

	@Override
	protected void onDestroy() {
		if (aq != null)
			aq.dismiss();
		if (expires_in != null) {
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
		if (expires_in != null) {
			setResult(101);
		}
		finish();

	}

	public void OnClickMianZhe(View v) {
		Intent intent = new Intent(this, Z_About_mianzhe.class);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			Log.e("Setting", "Call OnClickMianZhe failed", ex);
		}

	}

	public void OnClickGuanzhu(View v) {
		if (app.GetServiceData("Sina_Access_Token") != null) {

//			int uid = 3058636171;
//			uid = Integer.parseInt(app.GetServiceData("Sina_Access_UID"));
//			if (uid >0) {
					String m_PostURL = "https://api.weibo.com/2/friendships/create.json";

					Map<String, Object> params = new HashMap<String, Object>();
					params.put("access_token", app.GetServiceData("Sina_Access_Token"));
//					params.put("uid","3058636171");
					params.put("screen_name", "悦视频");
					// save to local
					AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
					cb.header("User-Agent",
							"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
					cb.params(params).url(m_PostURL).type(JSONObject.class)
							.weakHandler(this, "GuanzhuResult");
					aq.ajax(cb);
//			}
		
		} else {
			String m_URI = "http://weibo.com/signup/signup.php?inviteCode=3058636171";
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri content_url = Uri.parse(m_URI);
			intent.setData(content_url);
			startActivity(intent);
		}

	}

	public void GuanzhuResult(String url, JSONObject json, AjaxStatus status) {
		if (json != null) {
			try {
				if (json.getString("id").trim().length() > 0) {
					app.MyToast(this, "关注成功");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			app.MyToast(this, "你已关注过了，谢谢!");
		}
	}

	public void OnClickClearMemery(View v) {
		BitmapAjaxCallback.clearCache();
		app.MyToast(this, "清除缓存成功");
	}

	public void OnClickSug(View v) {
		// UMFeedbackService.enableNewReplyNotification(
		// this, NotificationType.AlertDialog);
		// //
		// 如果您程序界面是iOS风格，我们还提供了左上角的“返回”按钮，用于退出友盟反馈模块。启动友盟反馈模块前，您需要增加如下语句来设置“返回”按钮可见：
		UMFeedbackService.setGoBackButtonVisible();
		UMFeedbackService.openUmengFeedbackSDK(this);
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
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getResources().getString(R.string.tishi));
			builder.setMessage(
					getResources().getString(R.string.settingexitsinaweibo))
					.setPositiveButton(
							getResources().getString(R.string.queding),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									app.DeleteServiceData("Sina_Access_Token");
									ReGenerateUuid();
								}
							})
					.setNegativeButton(
							getResources().getString(R.string.quxiao),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							});
			builder.show();

		}
	}
	protected void ReGenerateUuid() {
		// TODO Auto-generated method stub
		String macAddress = null;
		WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = (null == wifiMgr ? null : wifiMgr
				.getConnectionInfo());
		if (info != null) {
			macAddress = info.getMacAddress();
			// 2. 通过调用 service account/generateUIID把UUID传递到服务器
			String url = Constant.BASE_URL + "account/generateUIID";

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("uiid", macAddress);
			params.put("device_type", "Android");

			AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
			cb.SetHeader(app.getHeaders());

			cb.params(params).url(url).type(JSONObject.class)
					.weakHandler(this, "CallServiceResult");
			aq.ajax(cb);
		}
	}

	public void CallServiceResult(String url, JSONObject json, AjaxStatus status) {
		if (json != null) {
			app.SaveServiceData("UserInfo", json.toString());
			try {
				app.UserID = json.getString("user_id").trim();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			// ajax error, show error code
			if (status.getCode() == AjaxStatus.NETWORK_ERROR) 
			app.MyToast(aq.getContext(),
					getResources().getString(R.string.networknotwork));
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
			app.SaveServiceData("Sina_Access_UID", uid);
			/*
			 * 判断当前的微博账户是否已经绑定
			 */
			IsBindWeibo();
		}
		
		public void IsBindWeibo()
		{
			String m_PostURL = Constant.BASE_URL + "account/validateThirdParty";

			Map<String, Object> params = new HashMap<String, Object>();
//			params.put("app_key",Constant.APPKEY);
			params.put("source_id", uid);
			params.put("source_type", "1");
			params.put("pre_user_id", app.UserID);
			// save to local
			AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
			cb.SetHeader(app.getHeaders());
			cb.params(params).url(m_PostURL).type(JSONObject.class)
					.weakHandler(this, "IsHasBindWeiboResult");
			aq.ajax(cb);	
		}
		
		public void IsHasBindWeiboResult(String url, JSONObject json,
			AjaxStatus status)
		{
			if (json != null) {
				try {
					if(json.has("user_id"))
					{
						if(json.getString("user_id")!=null)
						{
							//app.DeleteServiceData("UserInfo");
							app.UserID = json.getString("user_id");
							Map<String, String> headers = app.getHeaders();
							headers.remove("user_id");
							headers.put("user_id", app.UserID);
							app.setHeaders(headers);
							
							//将这个UserID保存在本地
							//app.SaveServiceData("UserInfo", json.toString());
							//app.MyToast(aq.getContext(),
									//"账号登陆成功!");
							UploadSinaHeadAndScreen_nameUrl(token, uid);
						}
					}
					if(json.has("res_code"))
					{
						if(json.getString("res_code")!=null)
						{
							UploadSinaHeadAndScreen_nameUrl(token, uid);
						}
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				// ajax error, show error code
				
			}
		}
		
		@Override
		public void onError(DialogError e) {
			app.MyToast(getApplicationContext(),
					getResources().getString(R.string.networknotwork));
		}

		@Override
		public void onCancel() {
			app.MyToast(getApplicationContext(), "绑定取消");
		}

		@Override
		public void onWeiboException(WeiboException e) {
			app.MyToast(getApplicationContext(),
					getResources().getString(R.string.networknotwork));
		}
	}

	public boolean UploadSinaHeadAndScreen_nameUrl(String access_token,
			String uid) {
		String m_GetURL = "https://api.weibo.com/2/users/show.json?access_token="
				+ access_token + "&uid=" + uid;

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(m_GetURL).type(JSONObject.class)
				.weakHandler(this, "UploadSinaHeadAndScreen_nameUrlResult");
		cb.header("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
		aq.ajax(cb);
		return false;
	}

	public void UploadSinaHeadAndScreen_nameUrlResult(String url,
			JSONObject json, AjaxStatus status) {
		String head_url = json.optString("avatar_large");
		String screen_name = json.optString("screen_name");
		// normal
		if (head_url != null && screen_name != null) {
			String m_PostURL = Constant.BASE_URL + "account/bindAccount";

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("source_id", uid);
			params.put("source_type", "1");
			params.put("pic_url", head_url);
			params.put("nickname", screen_name);

			// save to local
			AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
			cb.SetHeader(app.getHeaders());
			cb.params(params).url(m_PostURL).type(JSONObject.class)
					.weakHandler(this, "AccountBindAccountResult");
			aq.ajax(cb);
		}
		// test
	}

	public void AccountBindAccountResult(String url, JSONObject json,
			AjaxStatus status) {
		if (json != null) {
			try {
				if (json.getString("res_code").trim().equalsIgnoreCase("00000")) {
					// reload the userinfo
					String url2 = Constant.BASE_URL + "user/view?userid="
							+ app.UserID;
					AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
					cb.url(url2).type(JSONObject.class)
							.weakHandler(this, "AccountBindAccountResult3");
					cb.SetHeader(app.getHeaders());
					aq.ajax(cb);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			// ajax error, show error code
			if (status.getCode() == AjaxStatus.NETWORK_ERROR) 
			app.MyToast(this, getResources().getString(R.string.networknotwork));
		}
	}

	public void AccountBindAccountResult3(String url, JSONObject json,
			AjaxStatus status) {

		if (json != null) {
			try {
				if (json.getString("nickname").trim().length() > 0) {
					app.SaveServiceData("UserInfo", json.toString());
					app.MyToast(getApplicationContext(), "新浪微博已绑定");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else {
			if (status.getCode() == AjaxStatus.NETWORK_ERROR) 
			app.MyToast(this, getResources().getString(R.string.networknotwork));
		}
	}
}
