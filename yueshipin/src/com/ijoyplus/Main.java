package com.ijoyplus;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class Main extends TabActivity {

	private App app;
	private AQuery aq;
	private String TAB_1 = "Tab1";
	private String TAB_2 = "Tab2";
	private String TAB_3 = "Tab3";
	private TabHost mTabHost;

	private Intent mTab1, mTab2, mTab3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.main);
		app = (App) getApplicationContext();
		aq = new AQuery(this);
		CheckLogin();
		setupIntent();
	
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
					break;
				case R.id.radio2:
					mTabHost.setCurrentTabByTag(TAB_3);
					break;

				default:
					// tabHost.setCurrentTabByTag(TAB_1);
					break;
				}
			}
		});
	}
	@Override
	public void onResume() {
		super.onResume();
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
				params.put("device_type", "Android");

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
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {

			// ajax error, show error code
			aq.id(R.id.ProgressText).gone();
			app.MyToast(
					aq.getContext(),
					getResources().getString(R.string.networknotwork)
							);
			finish();
		}
	}
	@Override 
	public boolean dispatchKeyEvent(KeyEvent event) { 
	    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) { 
	        if (event.getAction() == KeyEvent.ACTION_DOWN 
	                && event.getRepeatCount() == 0) { 
	        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(getResources().getString(R.string.tishi));
				builder.setMessage(getResources().getString(R.string.shifoutuichu))
						.setPositiveButton(
								getResources().getString(R.string.queding),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										 finish();
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
	            return true; 
	        } 
	    } 
	    return super.dispatchKeyEvent(event); 
	} 
	
}