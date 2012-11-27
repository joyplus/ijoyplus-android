package com.joy;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.joy.Service.DownLoadService;
import com.mobclick.android.MobclickAgent;

public class JoyActivity extends TabActivity implements OnClickListener {

	public static String TAB_1 = "Tab1";
	public static String TAB_2 = "Tab2";
	public static String TAB_3 = "Tab3";
	public static TabHost mTabHost;
	ImageView mBut1, mBut2, mBut3;
	TextView mCateText1, mCateText2, mCateText3;
	LinearLayout linearLayout1, linearLayout2, linearLayout3;

	Intent mTab1, mTab2, mTab3;

	int mCurTabId = R.id.channel1;
	App app;
	private DownLoadService DOWNLOADSERVICE;
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				mTabHost.setCurrentTabByTag(TAB_1);
				break;
			case 2:
				mTabHost.setCurrentTabByTag(TAB_2);
				break;
			case 3:
				mTabHost.setCurrentTabByTag(TAB_3);
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.joyactivity);
		app = (App) getApplicationContext();
		app.setexit(getString(R.string.exit_false));
		app.SaveExit();
		app.setwhere_gologin(3);

		// veteranyu add]

		DOWNLOADSERVICE = app.getService();
		// *************************************************************************
		prepareIntent();
		setupIntent();
		prepareView();
		// 设置第三方标签的唯一值，用于获取第三放好友列表等功能
		// if (app.getAccessToken().trim().length()==0) {
		// String url = app.getVerificationCode();
		// Uri uri = Uri.parse(url);
		// //匹配验证码
		// String oauth_verifier = uri.getQueryParameter("oauth_verifier");
		// OAuth.getInstance().setOauthVerifier(oauth_verifier);
		// app.setAccessToken(OAuth.getInstance().getAccessToken());
		// }
		// app.SaveAccessToken();
		// System.out.println("AccessToken=====>"+app.getAccessToken());
	}

	private void prepareView() {
		mBut1 = (ImageView) findViewById(R.id.imageView1);
		mBut2 = (ImageView) findViewById(R.id.imageView2);
		mBut3 = (ImageView) findViewById(R.id.imageView3);
		findViewById(R.id.channel1).setOnClickListener(this);
		findViewById(R.id.channel2).setOnClickListener(this);
		findViewById(R.id.channel3).setOnClickListener(this);
		mCateText1 = (TextView) findViewById(R.id.textView1);
		mCateText2 = (TextView) findViewById(R.id.textView2);
		mCateText3 = (TextView) findViewById(R.id.textView3);
		linearLayout1 = (LinearLayout) findViewById(R.id.channel1);
		linearLayout2 = (LinearLayout) findViewById(R.id.channel2);
		linearLayout3 = (LinearLayout) findViewById(R.id.channel3);
	}

	private void prepareIntent() {
		mTab1 = new Intent(this, Tab1.class);
		mTab2 = new Intent(this, Tab2.class);
		mTab3 = new Intent(this, Tab3.class);
	}

	private void setupIntent() {
		mTabHost = getTabHost();
		mTabHost.addTab(buildTabSpec(TAB_1,
				getResources().getString(R.string.zhengzailiuxing),
				R.drawable.icon1, mTab1));
		mTabHost.addTab(buildTabSpec(TAB_2,
				getResources().getString(R.string.haoyoutuijian),
				R.drawable.icon2, mTab2));
		mTabHost.addTab(buildTabSpec(TAB_3,
				getResources().getString(R.string.dongtaitixing),
				R.drawable.icon3, mTab3));
	}

	private TabHost.TabSpec buildTabSpec(String tag, String resLabel,
			int resIcon, final Intent content) {
		return mTabHost.newTabSpec(tag)
				.setIndicator(resLabel, getResources().getDrawable(resIcon))
				.setContent(content);
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	// mBut1.performClick();
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }
	@Override
	public void onClick(View v) {
		if (v.getId() != R.id.channel1 && !CheckLogin()) {

			return;
		}
		if (mCurTabId == v.getId()) {
			return;
		}
		mBut1.setImageResource(R.drawable.icon1);
		mBut2.setImageResource(R.drawable.icon2);
		mBut3.setImageResource(R.drawable.icon3);
		int checkedId = v.getId();
		Message msg = new Message();
		switch (checkedId) {
		case R.id.channel1:
			mBut1.setImageResource(R.drawable.icon1);
			linearLayout1.setBackgroundResource(R.drawable.bottom_onclick);
			linearLayout2.setBackgroundColor(Color.parseColor("#000000"));
			linearLayout3.setBackgroundColor(Color.parseColor("#000000"));
			msg.what = 1;
			handler.sendMessage(msg);

			break;
		case R.id.channel2:

			mBut2.setImageResource(R.drawable.icon2);
			linearLayout2.setBackgroundResource(R.drawable.bottom_onclick);
			linearLayout1.setBackgroundColor(Color.parseColor("#000000"));
			linearLayout3.setBackgroundColor(Color.parseColor("#000000"));

			msg.what = 2;
			handler.sendMessage(msg);

			break;
		case R.id.channel3:

			mBut3.setImageResource(R.drawable.icon3);
			linearLayout3.setBackgroundResource(R.drawable.bottom_onclick);
			linearLayout1.setBackgroundColor(Color.parseColor("#000000"));
			linearLayout2.setBackgroundColor(Color.parseColor("#000000"));

			msg.what = 3;
			handler.sendMessage(msg);

			break;
		}
		mCurTabId = checkedId;
	}

	public void onResume() {
		super.onResume();
		if (!app.IsLogin) {
			mTabHost.setCurrentTabByTag(TAB_1);
			linearLayout1.setBackgroundResource(R.drawable.bottom_onclick);
			linearLayout2.setBackgroundColor(Color.parseColor("#000000"));
			linearLayout3.setBackgroundColor(Color.parseColor("#000000"));
		}

		MobclickAgent.onResume(this);

	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	public boolean CheckLogin() {
		if (app.IsLogin)
			return true;
		SharedPreferences sharedata = getSharedPreferences("IjoyID", 0);
		String email = sharedata.getString("email", "");
		String password = sharedata.getString("password", "");
		if (email.length() == 0 || password.length() == 0)
			GotoLogin_Activity();
		else if (DOWNLOADSERVICE.AccountLogin(email, password)) {
			app.IsLogin = true;
			app.GetInfo();
			return true;
		} else
			GotoLogin_Activity();
		return false;
	}

	public void GotoLogin_Activity() {
		Intent intent = new Intent();
		intent.setClass(this, Login_Activity.class);
		startActivity(intent);
	}
}