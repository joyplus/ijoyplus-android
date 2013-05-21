package com.joyplus;

import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

public class Weixin_Share extends TabActivity {

	private AQuery aq;
	private App app;
	private String WX_PAGE1 = "WX_PAGE1";
	private String WX_PAGE2 = "WX_PAGE2";
	private String WX_PAGE3 = "WX_PAGE3";
	private TabHost mTabHost;
	private Intent mTab1, mTab2, mTab3;
	private static String PERSONAL  = "微信分享";
	Context mContext;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weixin_share);
		app = (App) getApplication();
		aq = new AQuery(this);
		mContext = this;
		prepareIntent();
		setupIntent();
	}

	private void prepareIntent() {
		mTab1 = new Intent(this, WeixinPage1.class);
		mTab2 = new Intent(this, WeixinPage2.class);
		mTab3 = new Intent(this, WeixinPage3.class);
	}

	private void setupIntent() {
		mTabHost = getTabHost();
		mTabHost.addTab(buildTabSpec(WX_PAGE1,
				getResources().getString(R.string.tab1),
				R.drawable.tab1_yuedan, mTab1));
		mTabHost.addTab(buildTabSpec(WX_PAGE2,
				getResources().getString(R.string.tab2),
				R.drawable.tab2_yuebang, mTab2));
		mTabHost.addTab(buildTabSpec(WX_PAGE3,
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
					mTabHost.setCurrentTabByTag(WX_PAGE1);
					break;
				case R.id.radio1:
					mTabHost.setCurrentTabByTag(WX_PAGE2);
					break;
				case R.id.radio2:
					mTabHost.setCurrentTabByTag(WX_PAGE3);
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
        MobclickAgent.onEventBegin(mContext, PERSONAL);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onEventEnd(mContext, PERSONAL);
	}

	public void OnClickTab1TopLeft(View v) {
		Weixin_Share.this.finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 100 && resultCode == 0) {
			
		}
		if (requestCode == 100 && resultCode == 101) {
			
		} else {
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