package com.joyhome;

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
import android.view.View;
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
				R.drawable.tab1_picture, mTab1));
		mTabHost.addTab(buildTabSpec(TAB_2,
				getResources().getString(R.string.tab2),
				R.drawable.tab2_video, mTab2));
		mTabHost.addTab(buildTabSpec(TAB_3,
				getResources().getString(R.string.tab3), R.drawable.tab3_music,
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
	protected void onDestroy() {
		if (aq != null)
			aq.dismiss();
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

	public void OnClickTopLeft(View v) {
		Intent i = new Intent(this, Setting.class);
		startActivity(i);

	}

	public void OnClickTopRight(View v) {
		

	}
	
}