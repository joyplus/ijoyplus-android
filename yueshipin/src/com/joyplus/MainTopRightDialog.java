package com.joyplus;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.joyplus.faye.FayeService;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.MultiStatus;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMInfoAgent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.MulStatusListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

public class MainTopRightDialog extends Activity {
	private AQuery aq;
	private App app;
	private IWXAPI api;

	private String prod_name = null;
	private String uid = null;
	private Bitmap bitmap;
	private static String ue_wechat_friend_share = "微信好友分享";
	private static String ue_wechat_social_share = "微信朋友圈分享";
	private String prod_id = null;
	private Context mContext;
	private String ue_screencast_unbinded = "解除绑定事件";
	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;

	public static final String DESCRIPTOR = "joyplus";
	final SHARE_MEDIA sinaMedia = SHARE_MEDIA.SINA;
	UMSocialService controller;

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

		controller = UMServiceFactory.getUMSocialService(DESCRIPTOR,
				RequestType.SOCIAL);
		
		
		//关注我们
		SocializeConfig config = new SocializeConfig();
		//添加关注对象
		config.addFollow(SHARE_MEDIA.SINA, "3058636171");
		//添加follow 时的回调
		config.setOauthDialogFollowListener(new MulStatusListener() {
		    @Override
		    public void onStart() {
		        Log.d("TestData", "Follow Start");
		    }

		    @Override
		    public void onComplete(MultiStatus multiStatus, int st, SocializeEntity entity) {
		        if(st == 200){//follow 成功
		            Map<String, Integer> allChildren = multiStatus.getAllChildren();
		            Set<String> set = allChildren.keySet();
		            for(String fid : set)
		                Log.i("TestData", fid + "    "+allChildren.get(fid));
		        }
		    }
		});

		//更新config
		controller.setConfig(config);
	}

	public void OnClickSinaWeiBo(View v) {
		if (UMInfoAgent.isOauthed(mContext, sinaMedia)) {
			Intent i = new Intent(this, Sina_Share.class);
			i.putExtra("prod_name", prod_name);
			startActivity(i);
			MainTopRightDialog.this.finish();
		} else {
			GotoSinaWeibo();
		}
	}

	private void GotoSinaWeibo() {

		controller.doOauthVerify(mContext, sinaMedia, new UMAuthListener() {
			@Override
			public void onError(SocializeException e, SHARE_MEDIA platform) {
				app.MyToast(getApplicationContext(),
						getResources().getString(R.string.networknotwork));
			}

			@Override
			public void onComplete(Bundle value, SHARE_MEDIA platform) {
				if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {
					uid = value.getString("uid");
					
					Intent i = new Intent(MainTopRightDialog.this, Sina_Share.class);
					i.putExtra("prod_name", prod_name);
					startActivity(i);
					BindWeibo();
				} else {
					app.MyToast(getApplicationContext(),
							getResources().getString(R.string.networknotwork));
				}
			}

            //绑定新浪微博
			private void BindWeibo() {
				String m_PostURL = Constant.BASE_URL
						+ "account/validateThirdParty";
				Map<String, Object> params = new HashMap<String, Object>();
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

			@SuppressWarnings("unused")
			public void IsHasBindWeiboResult(String url, JSONObject json,
					AjaxStatus status) {
				if (json != null) {
					try {
						if (json.has("user_id")) {
							if (json.getString("user_id") != null) {
								app.UserID = json.getString("user_id");
								Map<String, String> headers = app.getHeaders();
								headers.remove("user_id");
								headers.put("user_id", app.UserID);
								app.setHeaders(headers);
								// 将这个UserID保存在本地
								UploadSinaHeadAndScreen_nameUrl();
							}
						}
						if (json.has("res_code")) {
							if (json.getString("res_code") != null) {
								UploadSinaHeadAndScreen_nameUrl();
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
			public void onCancel(SHARE_MEDIA arg0) {
				app.MyToast(getApplicationContext(), "绑定取消");
			}

			@Override
			public void onStart(SHARE_MEDIA arg0) {
			}

		});

	}

	public void UploadSinaHeadAndScreen_nameUrl() {
		controller.getPlatformInfo(mContext, sinaMedia, new UMDataListener() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onComplete(int status, Map<String, Object> info) {
				if (status == 200 && info != null) {
					app.SaveServiceData("Sina_Access_Token", info.get("access_token").toString());
					String m_PostURL = Constant.BASE_URL + "account/bindAccount";
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("source_id", info.get("uid").toString());
					params.put("source_type", "1");
					params.put("pic_url", info.get("profile_image_url")
							.toString().trim());
					params.put("nickname", info.get("screen_name").toString());
					// save to local
					AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
					cb.SetHeader(app.getHeaders());
					cb.params(params).url(m_PostURL).type(JSONObject.class)
							.weakHandler(this, "AccountBindAccountResult");
					aq.ajax(cb);

				} else
					Log.i("TestData", "发生错误：" + status);
			}
			
			@SuppressWarnings("unused")
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
					app.MyToast(getApplicationContext(), getResources().getString(R.string.networknotwork));
				}
			}

			@SuppressWarnings("unused")
			public void AccountBindAccountResult3(String url, JSONObject json,
					AjaxStatus status) {

				if (json != null) {
					try {
						if (json.getString("nickname").trim().length() > 0) {
							app.SaveServiceData("UserInfo", json.toString());
							app.MyToast(getApplicationContext(), "新浪微博已绑定");
							if (app.GetServiceData("Binding_TV") != null) {
								relieve_binding();
							}
							MainTopRightDialog.this.finish();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

				} else {
					if (status.getCode() == AjaxStatus.NETWORK_ERROR) 
					app.MyToast(getApplicationContext(), getResources().getString(R.string.networknotwork));
				}
			}
			
			
		});
	}

	public void OnClickWeixinFriends(View v) {
		if (!checkWeixinInstall()) {
			app.MyToast(mContext, "未安装微信");
			return;
		}
		String url = "weixin.joyplus.tv/info.php?prod_id=" + prod_id;// 收到分享的好友点击信息会跳转到这个地址去
		WXWebpageObject localWXWebpageObject = new WXWebpageObject();
		localWXWebpageObject.webpageUrl = url;
		WXMediaMessage localWXMediaMessage = new WXMediaMessage(
				localWXWebpageObject);
		localWXMediaMessage.title = "悦视频分享";// 不能太长，否则微信会提示出错。不过博主没验证过具体能输入多长。
		localWXMediaMessage.description = "我在用#悦视频#Android版观看<" + prod_name
				+ ">，推荐给大家哦！更多精彩尽在悦视频，欢迎下载：http://ums.bz/REGLDb/，快来和我一起看吧！";
		localWXMediaMessage.thumbData = getBitmapBytes(bitmap, false);
		SendMessageToWX.Req localReq = new SendMessageToWX.Req();
		localReq.transaction = String.valueOf(System.currentTimeMillis());
		localReq.message = localWXMediaMessage;
		localReq.scene = SendMessageToWX.Req.WXSceneSession;
		api.sendReq(localReq);
		MobclickAgent.onEvent(mContext, ue_wechat_friend_share);
		finish();

	}

	public void OnClickFriendsSocial(View v) {
		if (!checkWeixinInstall()) {
			app.MyToast(mContext, "未安装微信");
			return;
		}
		int wxSdkVersion = api.getWXAppSupportAPI();
		if (wxSdkVersion < TIMELINE_SUPPORTED_VERSION) {
			app.MyToast(mContext, "微信版本为： " + Integer.toHexString(wxSdkVersion)
					+ "\n该版本不支持分享到朋友圈");
			return;
		}
		// api.openWXApp();
		String url = "weixin.joyplus.tv/info.php?prod_id=" + prod_id;// 收到分享的好友点击信息会跳转到这个地址去
		WXWebpageObject localWXWebpageObject = new WXWebpageObject();
		localWXWebpageObject.webpageUrl = url;
		WXMediaMessage localWXMediaMessage = new WXMediaMessage(
				localWXWebpageObject);
		localWXMediaMessage.title = "悦视频分享";// 不能太长，否则微信会提示出错。不过博主没验证过具体能输入多长。
		localWXMediaMessage.description = "我在用#悦视频#Android版观看<" + prod_name
				+ ">，推荐给大家哦！更多精彩尽在悦视频，欢迎下载：http://ums.bz/REGLDb/，快来和我一起看吧！";
		localWXMediaMessage.thumbData = getBitmapBytes(bitmap, false);
		SendMessageToWX.Req localReq = new SendMessageToWX.Req();
		localReq.transaction = String.valueOf(System.currentTimeMillis());
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

	public void relieve_binding() {

		FayeService.FayeByService(mContext,
				"/screencast/" + app.GetServiceData("Binding_TV_Channal"));
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				try {
					JSONObject et = new JSONObject();
					et.put("user_id", app.GetServiceData("Binding_Userid"));
					et.put("push_type", "33");
					et.put("tv_channel",
							app.GetServiceData("Binding_TV_Channal"));
					FayeService.SendMessageService(mContext, et,
							app.GetServiceData("Binding_Userid"));
					app.DeleteServiceData("Binding_Userid");
					app.DeleteServiceData("Binding_TV");
					MobclickAgent.onEvent(mContext, ue_screencast_unbinded);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}, 500);

	}

	private PackageInfo packageInfo;

	public boolean checkWeixinInstall() {

		try {
			packageInfo = this.getPackageManager().getPackageInfo(
					"com.tencent.mm", 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		if (packageInfo == null) {
			return false;
		} else {
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
