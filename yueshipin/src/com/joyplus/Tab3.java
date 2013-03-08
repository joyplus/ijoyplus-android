package com.joyplus;

import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

public class Tab3 extends TabActivity {

	private AQuery aq;
	private App app;
	private String TAB3_PAGE1 = "TAB3_PAGE1";
	private String TAB3_PAGE2 = "TAB3_PAGE2";
	private String TAB3_PAGE3 = "TAB3_PAGE3";
	private TabHost mTabHost;
	private Intent mTab1, mTab2, mTab3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tab3);
		app = (App) getApplication();
		aq = new AQuery(this);
		prepareIntent();
		setupIntent();
		CheckLogin();
	}

	private void prepareIntent() {
		mTab1 = new Intent(this, Tab3Page1.class);
		mTab2 = new Intent(this, Tab3Page2.class);
		mTab3 = new Intent(this, Tab3Page3.class);
	}

	private void setupIntent() {
		mTabHost = getTabHost();
		mTabHost.addTab(buildTabSpec(TAB3_PAGE1,
				getResources().getString(R.string.tab1),
				R.drawable.tab1_yuedan, mTab1));
		mTabHost.addTab(buildTabSpec(TAB3_PAGE2,
				getResources().getString(R.string.tab2),
				R.drawable.tab2_yuebang, mTab2));
		mTabHost.addTab(buildTabSpec(TAB3_PAGE3,
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
					mTabHost.setCurrentTabByTag(TAB3_PAGE1);
					break;
				case R.id.radio1:
					mTabHost.setCurrentTabByTag(TAB3_PAGE2);
					break;
				case R.id.radio2:
					mTabHost.setCurrentTabByTag(TAB3_PAGE3);
					break;

				default:
					// tabHost.setCurrentTabByTag(TAB_1);
					break;
				}
			}
		});
	}

	private TabHost.TabSpec buildTabSpec(String tag, String resLabel,
			int resIcon, final Intent content) {
		return mTabHost.newTabSpec(tag)
				.setIndicator(resLabel, getResources().getDrawable(resIcon))
				.setContent(content);
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

	public void OnClickTab1TopLeft(View v) {
		Intent i = new Intent(this, Search.class);
		startActivity(i);

	}

	public void OnClickTab1TopRight(View v) {
		Intent i = new Intent(this, Setting.class);
		startActivityForResult(i, 100);
	}
	public boolean CheckLogin() {
		String UserInfo = null;
		UserInfo = app.GetServiceData("UserInfo");
		if (UserInfo != null) {
			JSONObject json;
			try {
				json = new JSONObject(UserInfo);
				if (json.getString("nickname").trim().length() > 0) {
					aq.id(R.id.textView4).text(
							json.getString("nickname").trim());
					aq.id(R.id.textView5).gone();
					String temp = json.getString("pic_url").trim();
					aq.id(R.id.imageView4).image(
							json.getString("pic_url").trim(), true, true, 0,
							R.drawable.default_header);
				} else {
					aq.id(R.id.textView5)
							.text(json.getString("user_id").trim());
					aq.id(R.id.imageView4).image(
							json.getString("pic_url").trim(), true, true, 0,
							R.drawable.default_header);
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 100&&resultCode==0)
		{
			CheckLogin();
		}
		if (requestCode == 100&&resultCode == 101){
				CheckLogin();
		} 
		else {
			// 获取当前活动的Activity实例
			Activity subActivity = getLocalActivityManager()
					.getCurrentActivity();
			// 判断是否实现返回值接口
			if (subActivity instanceof OnTabActivityResultListener) {
				// 获取返回值接口实例
				OnTabActivityResultListener listener = (OnTabActivityResultListener) subActivity;
				// 转发请求到子Activity
				listener.onTabActivityResult(requestCode, resultCode, data);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}