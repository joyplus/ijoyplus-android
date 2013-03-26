package com.joyplus;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.joyplus.Dlna.DlnaSelectDevice;
import com.parse.PushService;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

@SuppressWarnings("deprecation")
public class Main extends TabActivity {
	private String TAG = "Main";

	private App app;
	private AQuery aq;
	private String TAB_1 = "Tab1";
	private String TAB_2 = "Tab2";
	private String TAB_3 = "Tab3";
	private TabHost mTabHost;

	private Intent mTab1, mTab2, mTab3;
	private Map<String, String> headers;
	private MianZeDialog mianzeDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		app = (App) getApplicationContext();
		aq = new AQuery(this);
		
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
		headers.put("client","android");
		app.setHeaders(headers);
			
		Intent intent = new Intent(Main.this, DlnaSelectDevice.class);
		startService(intent);
		
		PushService.subscribe(this, "", Main.class);
		PushService.setDefaultPushCallback(this, Main.class);
		if(!Constant.TestEnv)
			ReadLocalAppKey();

		CheckLogin();
		setupIntent();
		
		if(app.GetServiceData("mianzeshengming")==null)
		{
			mianzeDialog = new MianZeDialog(Main.this);
			Window dialogWindow = mianzeDialog.getWindow();        
			WindowManager.LayoutParams lp = dialogWindow.getAttributes();        
			dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
			lp.x = 100; // 新位置X坐标        
			lp.y = 100; // 新位置Y坐标        
			lp.width = 300; // 宽度        
			lp.height = 300; // 高度        
			lp.alpha = 0.7f; // 透明度            
			dialogWindow.setAttributes(lp);
			mianzeDialog.show();
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		try {
			/*{"alert":"真爱趁现在","prod_id":"977732","prod_type":"2","badge":"Increment"}  */
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
	//新手引导
	public void OnClickNewGuider_1(View v)
	{
		aq.id(R.id.new_guider_1).gone();
		app.SaveServiceData("new_guider_1","new_guider_1");
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
		mTabHost.addTab(buildTabSpec(TAB_1,
				getResources().getString(R.string.tab1),
				R.drawable.tab1_yuedan, mTab1));
		mTabHost.addTab(buildTabSpec(TAB_2,
				getResources().getString(R.string.tab2),
				R.drawable.tab2_yuebang, mTab2));
		mTabHost.addTab(buildTabSpec(TAB_3,
				getResources().getString(R.string.tab3), R.drawable.tab3_wode,
				mTab3));
		mTabHost.setCurrentTab(0);
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
					//添加是否显示第二个
					if(app.GetServiceData("new_guider_2")==null)
					{
						aq.id(R.id.new_guider_2).visible();
					}
					break;
				case R.id.radio2:
					mTabHost.setCurrentTabByTag(TAB_3);
					break;
				default:
					mTabHost.setCurrentTabByTag(TAB_1);
					break;

				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		if (aq != null)
			aq.dismiss();
		Intent i  = new Intent();
		i.setClass(this, DlnaSelectDevice.class);
		stopService(i);
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		if(app.GetServiceData("new_guider_1")==null)
		{
			aq.id(R.id.new_guider_1).visible();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
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
	/*
	 * 添加之后关于在线的操作不成功
	 */
	
	public void ReadLocalAppKey() {
		// online 获取APPKEY
		MobclickAgent.updateOnlineConfig(this);
		String OnLine_Appkey = MobclickAgent.getConfigParams(this, "APPKEY");
		if (OnLine_Appkey != null && OnLine_Appkey.length() >0) {
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
				app.UserID = json.getString("user_id").trim();
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
			//解决没有网络时程序不能关闭的问题
			//finish();
		}
	}

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
	/*
	 * 免责声明对话框
	 */
	public class MianZeDialog extends Dialog {

		public MianZeDialog(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.mianze_dialog);
			
			Button buttonYes = (Button) findViewById(R.id.btnyes);
			buttonYes.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dismiss();
					//将内容保存在sharedPreference
					app.SaveServiceData("mianzeshengming", "mianzeshengming");
				}
			});
		}
	}
}