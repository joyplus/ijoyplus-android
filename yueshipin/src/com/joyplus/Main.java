package com.joyplus;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bodong.dianju.sdk.DianJuPlatform;
import com.joyplus.Dlna.DlnaSelectDevice;
import com.joyplus.faye.FayeService;
import com.parse.PushService;
import com.umeng.analytics.MobclickAgent;

@SuppressWarnings("deprecation")
public class Main extends TabActivity {
	private String TAG = "Main";

	private App app;
	private AQuery aq;
	private String TAB_1 = "Tab1";
	private String TAB_2 = "Tab2";
	private String TAB_3 = "Tab3";
	private String TAB_4 = "Tab4";   
	private TabHost mTabHost;

	private Intent mTab1, mTab2, mTab3, mTab4;
	private Map<String, String> headers;
	private MianZeDialog mianzeDialog;
	CheckBindDingReceiver bindingReceiver;
	Context mContext;
	Handler locationHandler;
	private Handler mHandler = new Handler();
	private boolean DialogIsViewed = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 91互推平台
		DianJuPlatform.init(this);
		setContentView(R.layout.main);
		registerBinding();
		app = (App) getApplicationContext();
		aq = new AQuery(this);
		mContext = this;
		headers = new HashMap<String, String>();
		headers.put("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
		PackageInfo pInfo;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			headers.put("version", pInfo.versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		headers.put("app_key", Constant.APPKEY);
		headers.put("client", "android");
		app.setHeaders(headers);
		if (android.os.Build.VERSION.SDK_INT >= 14) {
			Intent intent = new Intent(Main.this, DlnaSelectDevice.class);
			startService(intent);
		}

////		if (app.GetServiceData("Binding_TV") != null) {
//
//			Intent service = new Intent(Main.this, FayeService.class);
//			startService(service);
//			check_binding(app.GetServiceData("Binding_TV_Channal"),
//					app.GetServiceData("Binding_Userid"), app.getHeaders());
//
////		}
//
//		PushService.subscribe(this, "", Main.class);
//		PushService.subscribe(this, "CHANNEL_ANDROID", Main.class);
//		PushService.setDefaultPushCallback(this, Main.class);
		mHandler.postDelayed(mRunnable, 2000);
//		new MyThread(Main.this).start();
		if (!Constant.TestEnv)
			ReadLocalAppKey();

		CheckLogin();
		setupIntent();

		if (app.GetServiceData("mianzeshengming") == null) {
			mianzeDialog = new MianZeDialog(Main.this);
			mianzeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mianzeDialog.setCanceledOnTouchOutside(false);
			mianzeDialog.show();
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		try {
			JSONObject json = new JSONObject(intent.getExtras().getString(
					"com.parse.Data"));
			String Prod_ID = json.getString("prod_id").trim();
			String Prod_Type = json.getString("prod_type").trim();
			int Type = Integer.parseInt(Prod_Type);
			// 1：电影，2：电视剧，3：综艺，4：视频
			switch (Type) {
			case 1:
				intent.setClass(this, Detail_Movie.class);
				intent.putExtra("prod_id", Prod_ID);
				try {
					startActivity(intent);
				} catch (ActivityNotFoundException ex) {
					Log.e(TAG, "Call Detail_Movie failed", ex);
				}
				break;
			case 2:
				intent.setClass(this, Detail_TV.class);
				intent.putExtra("prod_id", Prod_ID);
				try {
					startActivity(intent);
				} catch (ActivityNotFoundException ex) {
					Log.e(TAG, "Call Detail_TV failed", ex);
				}
				break;
			case 3:
				intent.setClass(this, Detail_Show.class);
				intent.putExtra("prod_id", Prod_ID);
				try {
					startActivity(intent);
				} catch (ActivityNotFoundException ex) {
					Log.e(TAG, "Call Detail_Show failed", ex);
				}
				break;
			}

		} catch (JSONException e) {
			Log.d(TAG, "JSONException: " + e.getMessage());
		}
		super.onNewIntent(intent);

	}

	// 新手引导
	public void OnClickNewGuider_1(View v) {
		aq.id(R.id.new_guider_1).gone();
		app.SaveServiceData("new_guider_1", "new_guider_1");
	}

	// 新手引导
	public void OnClickNewGuider_2(View v) {
		aq.id(R.id.new_guider_2).gone();
		app.SaveServiceData("new_guider_2", "new_guider_2");
	}

	private TabHost.TabSpec buildTabSpec(String tag, String resLabel,
			int resIcon, final Intent content) {
		return mTabHost.newTabSpec(tag)
				.setIndicator(resLabel, getResources().getDrawable(resIcon))
				.setContent(content);
	}

	private void setupIntent() {
		mTabHost = getTabHost();
		mTab1 = new Intent(this, Tab1.class);
		mTab2 = new Intent(this, Tab2.class);
		mTab3 = new Intent(this, Tab3.class);
		mTab4 = new Intent(this, Video_Cache.class);
		mTabHost.addTab(buildTabSpec(TAB_1,
				getResources().getString(R.string.tab1), R.drawable.yuedan,
				mTab1));
		mTabHost.addTab(buildTabSpec(TAB_2,
				getResources().getString(R.string.tab2), R.drawable.yuebang,
				mTab2));
		mTabHost.addTab(buildTabSpec(TAB_3,
				getResources().getString(R.string.tab3), R.drawable.my, mTab3));
		mTabHost.addTab(buildTabSpec(TAB_4,
				getResources().getString(R.string.tab4), R.drawable.xiazai,
				mTab4));
		mTabHost.setCurrentTab(0);// 默认显示悦榜
		RadioGroup radioGroup = (RadioGroup) this
				.findViewById(R.id.radioGroup1);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {

				case R.id.radio0:
					mTabHost.setCurrentTabByTag(TAB_1);
					break;
				case R.id.radio1:
					mTabHost.setCurrentTabByTag(TAB_2);
					// 添加是否显示第二个
					if (app.GetServiceData("new_guider_2") == null) {
						aq.id(R.id.new_guider_2).visible();
					}
					break;
				case R.id.radio2:
					mTabHost.setCurrentTabByTag(TAB_3);
					break;
				case R.id.radio3:
					mTabHost.setCurrentTabByTag(TAB_4);
					break;
				default:
					mTabHost.setCurrentTabByTag(TAB_2);
					break;
				}
			}
		});
	}

	@Override
	protected void onDestroy() {

		// 需要在退出程序时调用平台的destroy方法关闭SDK
		DianJuPlatform.destroy(this);
		if (aq != null)
			aq.dismiss();
		if (android.os.Build.VERSION.SDK_INT >= 14) {
			Intent i = new Intent();
			i.setClass(this, DlnaSelectDevice.class);
			stopService(i);
		}
		stopService(new Intent(Main.this, FayeService.class));
		mHandler.removeCallbacks(mRunnable);
		unregisterBinding();
		super.onDestroy();
		
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();
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

	/*
	 * 添加之后关于在线的操作不成功
	 */

	public void ReadLocalAppKey() {
		// online 获取APPKEY
		MobclickAgent.updateOnlineConfig(this);
		String OnLine_Appkey = MobclickAgent.getConfigParams(this, "APPKEY");
		if (OnLine_Appkey != null && OnLine_Appkey.length() > 0) {
			Constant.APPKEY = OnLine_Appkey;
			headers.remove("app_key");
			headers.put("app_key", OnLine_Appkey);
			app.setHeaders(headers);
		}
	}

	public boolean CheckLogin() {
		String UserInfo = null;
		UserInfo = app.GetServiceData("UserInfo");
		if (UserInfo == null) {
			// 1. 在客户端生成一个唯一的UUID
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
				params.put("device_type", "android-mobile");

				AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
				cb.header("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
				cb.header("app_key", Constant.APPKEY);

				cb.params(params).url(url).type(JSONObject.class)
						.weakHandler(this, "CallServiceResult");
				aq.id(R.id.ProgressText).visible();
				aq.progress(R.id.progress).ajax(cb);
			}
		} else {
			JSONObject json;
			try {
				json = new JSONObject(UserInfo);
				if (json.has("user_id")) {
					app.UserID = json.getString("user_id").trim();
				} else {
					app.UserID = json.getString("id").trim();// user_id
				}
				headers.put("user_id", app.UserID);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		return false;
	}

	public void CallServiceResult(String url, JSONObject json, AjaxStatus status) {

		if (json != null) {
			app.SaveServiceData("UserInfo", json.toString());
			try {
				app.UserID = json.getString("user_id").trim();
				headers.put("user_id", app.UserID);
				app.setHeaders(headers);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			// ajax error, show error code
			if (status.getCode() == AjaxStatus.NETWORK_ERROR) {
				aq.id(R.id.ProgressText).gone();
				app.MyToast(aq.getContext(),
						getResources().getString(R.string.networknotwork));
			}
			// 解决没有网络时程序不能关闭的问题
		}
	}
	
	private final Runnable mRunnable = new Runnable() {
		public void run() {
			Intent service = new Intent(Main.this, FayeService.class);
			startService(service);
			check_binding(app.GetServiceData("Binding_TV_Channal"),
					app.GetServiceData("Binding_Userid"), app.getHeaders());

			PushService.subscribe(Main.this, "", Main.class);
			PushService.subscribe(Main.this, "CHANNEL_ANDROID", Main.class);
			PushService.setDefaultPushCallback(Main.this, Main.class);
		}
	};
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getRepeatCount() == 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(getResources().getString(R.string.tishi));
				builder.setMessage(
						getResources().getString(R.string.shifoutuichu))
						.setPositiveButton(
								getResources().getString(R.string.queding),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										finish();
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
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}
	
//	class MyThread extends Thread{
//		private Context context;
//		public MyThread(Context context)
//		{
//			this.context = context;
//		}
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			Intent service = new Intent(Main.this, FayeService.class);
//			context.startService(service);
//			check_binding(app.GetServiceData("Binding_TV_Channal"),
//					app.GetServiceData("Binding_Userid"), app.getHeaders());
//
//			PushService.subscribe(context, "", Main.class);
//			PushService.subscribe(context, "CHANNEL_ANDROID", Main.class);
//			PushService.setDefaultPushCallback(context, Main.class);
//			
//		}
//		
//	}
	
	// 免责声明对话框

	public class MianZeDialog extends Dialog {

		public MianZeDialog(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.mianze_dialog);

			Window mWindow = getWindow();
			WindowManager.LayoutParams lp = mWindow.getAttributes();
			lp.dimAmount = 0f;
			mWindow.setAttributes(lp);

			Button buttonYes = (Button) findViewById(R.id.btnyes);
			buttonYes.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dismiss();
					// 将内容保存在sharedPreference
					app.SaveServiceData("mianzeshengming", "mianzeshengming");
					if (app.GetServiceData("new_guider_1") == null) {
						aq.id(R.id.new_guider_1).visible();
					}
				}
			});
		}
	}

	/* 注册监听 */
	private void registerBinding() {
		bindingReceiver = new CheckBindDingReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.joyplus.check_binding");
		registerReceiver(bindingReceiver, filter);

	}

	/* 取消监听 */
	private void unregisterBinding() {
		if (bindingReceiver != null) {
			this.unregisterReceiver(bindingReceiver);
		}
	}

	/* Broadcast监听 */
	public class CheckBindDingReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			String status = bundle.getString("status");
			Log.i("CheckBindDingReceiver", "result>>>>>" + status);
			if (status.equals("fail")) {
				app.DeleteServiceData("Binding_TV");
				showDialog();
			}
			if (status.equals("check_bind")) {
				check_binding(app.GetServiceData("Binding_TV_Channal"),
						app.GetServiceData("Binding_Userid"), app.getHeaders());
			}
		}
	}

	private void showDialog() {
		if (DialogIsViewed)
			return;
		AlertDialog.Builder builder = new Builder(Main.this);
		builder.setTitle("提示");
		builder.setMessage("已断开与电视端的绑定");
		builder.setNegativeButton("确定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				DialogIsViewed = false;
				dialog.dismiss();
			}
		});
		DialogIsViewed = true;
		builder.create().show();
	}

	private void check_binding(String channel, String userid,
			Map<String, String> headers) {
		if(userid == null || channel == null)
			return;
		String url = Constant.CHECK_BINDING + "?tv_channel=" + channel
				+ "&user_id=" + userid;
		Log.i("", "url>>>" + url);
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.SetHeader(headers);
		cb.url(url).type(JSONObject.class)
				.weakHandler(this, "CallProgramPlayResult");
		aq.ajax(cb);

	}

	public void CallProgramPlayResult(String url, JSONObject json,
			AjaxStatus status) {
		try {
			int result = Integer.valueOf(json.getString("status"));
			Log.i("check", "status>>>"+result);
			switch (result) {
			case 1:
				app.SaveServiceData("Binding_TV", "success");
				break;
			case 0:
				app.DeleteServiceData("Binding_TV");
				break;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}