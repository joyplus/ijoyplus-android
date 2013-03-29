package com.joyplus;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.joyplus.weibo.net.AccessToken;
import com.joyplus.weibo.net.DialogError;
import com.joyplus.weibo.net.Weibo;
import com.joyplus.weibo.net.WeiboDialogListener;
import com.joyplus.weibo.net.WeiboException;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.umeng.analytics.MobclickAgent;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

public class MainTopRightDialog extends Activity {
	private AQuery aq;
	private App app;
	private IWXAPI api;

	private String prod_name = null;
	private String uid = null;
	private String token = null;
	private String expires_in = null;
	private Bitmap bitmap;
	private static String ue_wechat_friend_share = "微信好友分享";
	private static String ue_wechat_social_share = "微信朋友圈分享";
	private String prod_id = null;
    private Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_top_right_dialog);
		app = (App) getApplication();
		aq = new AQuery(this);
        mContext = this;
		api = WXAPIFactory.createWXAPI(this, Constant.APP_ID, false);
		api.registerApp(Constant.APP_ID);

		Intent intent = getIntent();
		prod_name = intent.getStringExtra("prod_name");
		bitmap = (Bitmap) intent.getParcelableExtra("bitmapImage");
		prod_id = intent.getStringExtra("prod_id");

	}

	public void OnClickSinaWeiBo(View v) {
		if (app.GetServiceData("Sina_Access_Token") != null) {
			Intent i = new Intent(this, Sina_Share.class);
			i.putExtra("prod_name", prod_name);
			startActivity(i);
		} else {
			GotoSinaWeibo();
		}
	}

	public void GotoSinaWeibo() {
		Weibo weibo = Weibo.getInstance();
		weibo.setupConsumerConfig(Constant.SINA_CONSUMER_KEY,
				Constant.SINA_CONSUMER_SECRET);
		weibo.setRedirectUrl("https://api.weibo.com/oauth2/default.html");
		weibo.authorize(this, new AuthDialogListener());

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
			UploadSinaHeadAndScreen_nameUrl(token, uid);
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
				// else
				// app.MyToast(this, "更新头像失败!");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {

			// ajax error, show error code
			if (status.getCode() == AjaxStatus.NETWORK_ERROR)
				app.MyToast(this,
						getResources().getString(R.string.networknotwork));
		}
	}

	public void AccountBindAccountResult3(String url, JSONObject json,
			AjaxStatus status) {

		if (json != null) {
			try {
				if (json.getString("nickname").trim().length() > 0) {
					app.SaveServiceData("UserInfo", json.toString());
					app.MyToast(getApplicationContext(), "新浪微博已绑定");
					Intent i = new Intent(this, Sina_Share.class);
					i.putExtra("prod_name", aq.id(R.id.program_name).getText()
							.toString());
					startActivity(i);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else {

			if (status.getCode() == AjaxStatus.NETWORK_ERROR)
				app.MyToast(this,
						getResources().getString(R.string.networknotwork));
		}
	}

	public void OnClickWeixinFriends(View v) {
		if(!checkWeixinInstall())
		{
			app.MyToast(mContext, "未安装微信");
			return;
		}
		String url = "weixin.joyplus.tv/info.php?prod_id="+prod_id;// 收到分享的好友点击信息会跳转到这个地址去
		WXWebpageObject localWXWebpageObject = new WXWebpageObject();
		localWXWebpageObject.webpageUrl = url;
		WXMediaMessage localWXMediaMessage = new WXMediaMessage(
				localWXWebpageObject);
		localWXMediaMessage.title = "悦视频分享";// 不能太长，否则微信会提示出错。不过博主没验证过具体能输入多长。
		localWXMediaMessage.description = "我在用#悦视频#Android版观看<" + prod_name
				+ ">，推荐给大家哦！更多精彩尽在悦视频，欢迎下载：http://ums.bz/REGLDb/，快来和我一起看吧！";
		localWXMediaMessage.thumbData = getBitmapBytes(bitmap, false);
		SendMessageToWX.Req localReq = new SendMessageToWX.Req();
		localReq.transaction = System.currentTimeMillis() + "";
		localReq.message = localWXMediaMessage;
		localReq.scene = SendMessageToWX.Req.WXSceneSession;
		api.sendReq(localReq);
		MobclickAgent.onEvent(mContext, ue_wechat_friend_share);
		finish();
	}


	public void OnClickFriendsSocial(View v) {
		if(!checkWeixinInstall())
		{
			app.MyToast(mContext, "未安装微信");
			return;
		}
		api.openWXApp();
		String url = "weixin.joyplus.tv/info.php?prod_id="+prod_id;// 收到分享的好友点击信息会跳转到这个地址去
		WXWebpageObject localWXWebpageObject = new WXWebpageObject();
		localWXWebpageObject.webpageUrl = url;
		WXMediaMessage localWXMediaMessage = new WXMediaMessage(
						localWXWebpageObject);
		localWXMediaMessage.title = "悦视频分享";// 不能太长，否则微信会提示出错。不过博主没验证过具体能输入多长。
		localWXMediaMessage.description = "我在用#悦视频#Android版观看<" + prod_name
						+ ">，推荐给大家哦！更多精彩尽在悦视频，欢迎下载：http://ums.bz/REGLDb/，快来和我一起看吧！";
		localWXMediaMessage.thumbData = getBitmapBytes(bitmap, false);
		SendMessageToWX.Req localReq = new SendMessageToWX.Req();
		localReq.transaction = System.currentTimeMillis() + "";
		localReq.message = localWXMediaMessage;
		localReq.scene = SendMessageToWX.Req.WXSceneTimeline;
		api.sendReq(localReq);
		MobclickAgent.onEvent(mContext, ue_wechat_social_share);
		finish();
	}

	// 需要对图片进行处理，否则微信会在log中输出thumbData检查错误
	public byte[] getBitmapBytes(Bitmap bitmap, boolean paramBoolean) {
		Bitmap localBitmap = Bitmap
				.createBitmap(94, 141, Bitmap.Config.RGB_565);
		Canvas localCanvas = new Canvas(localBitmap);
		int i;
		int j;
		// if (bitmap.getHeight() > bitmap.getWidth()) {
		i = bitmap.getWidth();
		j = bitmap.getHeight();
		// } else {
		// i = bitmap.getHeight();
		// j = bitmap.getHeight();
		// }
		while (true) {
			localCanvas.drawBitmap(bitmap, new Rect(0, 0, i, j), new Rect(0, 0,
					i, j), null);
			if (paramBoolean)
				bitmap.recycle();
			ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
			localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
					localByteArrayOutputStream);
			localBitmap.recycle();
			byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
			try {
				localByteArrayOutputStream.close();
				return arrayOfByte;
			} catch (Exception e) {

			}
			// i = bitmap.getHeight();
			// j = bitmap.getHeight();
		}
	}
	private PackageInfo packageInfo;
	public boolean checkWeixinInstall(){
	
    try {
        packageInfo = this.getPackageManager().getPackageInfo(
                "com.tencent.mm", 0);
    } catch (NameNotFoundException e) {
        packageInfo = null;
        e.printStackTrace();
    }
    if(packageInfo ==null){
        return false;
    }else{
        return true;
    }
	}
	
	public void Cancel(View v) {
		finish();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}
}
