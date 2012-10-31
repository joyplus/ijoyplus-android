package com.joy;

import com.joy.Tools.Tools;
import com.joy.oauthTools.ConfigUtil;
import com.joy.oauthTools.OAuth;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

public class JoyActivity extends TabActivity implements OnClickListener{
	private final String LOGTAG = "AccessToken";
	ProgressDialog progressBar;
	public static String TAB_TAG_HOME = "zhengzailiuxing";
	public static String TAB_TAG_CHANNEL = "haoyoutuijian";
	public static String TAB_TAG_ACCOUNT = "donttaitixing";
	public static String TAB_TAG_SEARCH = "gerenzhuye";
	public static TabHost mTabHost;
	ImageView mBut1, mBut2, mBut3, mBut4;
	TextView mCateText1, mCateText2, mCateText3, mCateText4;
	LinearLayout linearLayout1,linearLayout2,linearLayout3,linearLayout4;

	Intent mzhengzailiuxingItent, mhaoyoutuijianIntent, mdongtaitixingIntent, mgerenzhuyeIntent;

	int mCurTabId = R.id.channel1;
	GetThird_AccessToken getThird_AccessToken;
	final Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				mTabHost.setCurrentTabByTag(TAB_TAG_HOME);
				progressBar.dismiss();
				break;
			case 2:
				mTabHost.setCurrentTabByTag(TAB_TAG_CHANNEL);
				progressBar.dismiss();
				break;
			case 3:
				mTabHost.setCurrentTabByTag(TAB_TAG_SEARCH);
				progressBar.dismiss();
				break;
			case 4:
				mTabHost.setCurrentTabByTag(TAB_TAG_ACCOUNT);
				progressBar.dismiss();
				break;
			}
		}
	};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tab);
        getThird_AccessToken = (GetThird_AccessToken) getApplicationContext();
        getThird_AccessToken.setexit(getString(R.string.exit_false));
        getThird_AccessToken.SaveExit();
        getThird_AccessToken.setwhere_gologin(2);
        prepareIntent();
		setupIntent();
		prepareView();
		//设置第三方标签的唯一值，用于获取第三放好友列表等功能
//		if (getThird_AccessToken.getAccessToken().trim().length()==0) {
//			String url = getThird_AccessToken.getVerificationCode();
//	    	Uri uri = Uri.parse(url);
//	    	//匹配验证码
//			String oauth_verifier = uri.getQueryParameter("oauth_verifier");
//			OAuth.getInstance().setOauthVerifier(oauth_verifier);
//			getThird_AccessToken.setAccessToken(OAuth.getInstance().getAccessToken());
//		}
//		getThird_AccessToken.SaveAccessToken();
//		System.out.println("AccessToken=====>"+getThird_AccessToken.getAccessToken());
    }
    private void prepareView() {
		mBut1 = (ImageView) findViewById(R.id.imageView1);
		mBut2 = (ImageView) findViewById(R.id.imageView2);
		mBut3 = (ImageView) findViewById(R.id.imageView3);
		mBut4 = (ImageView) findViewById(R.id.imageView4);
		findViewById(R.id.channel1).setOnClickListener(this);
		findViewById(R.id.channel2).setOnClickListener(this);
		findViewById(R.id.channel3).setOnClickListener(this);
		findViewById(R.id.channel4).setOnClickListener(this);
		mCateText1 = (TextView) findViewById(R.id.textView1);
		mCateText2 = (TextView) findViewById(R.id.textView2);
		mCateText3 = (TextView) findViewById(R.id.textView3);
		mCateText4 = (TextView) findViewById(R.id.textView4);
		linearLayout1=(LinearLayout)findViewById(R.id.channel1);
		linearLayout2=(LinearLayout)findViewById(R.id.channel2);
		linearLayout3=(LinearLayout)findViewById(R.id.channel3);
		linearLayout4=(LinearLayout)findViewById(R.id.channel4);
	}
    private void prepareIntent() {
    	mzhengzailiuxingItent = new Intent(this, Activity01.class);
		mhaoyoutuijianIntent = new Intent(this, Activity02.class);
		mdongtaitixingIntent = new Intent(this, Activity03.class);
		mgerenzhuyeIntent = new Intent(this, Activity04.class);
	}
    private void setupIntent() {
		mTabHost = getTabHost();
		mTabHost.addTab(buildTabSpec(TAB_TAG_HOME, getResources().getString(R.string.zhengzailiuxing),R.drawable.icon1, mzhengzailiuxingItent));
		mTabHost.addTab(buildTabSpec(TAB_TAG_CHANNEL,getResources().getString(R.string.haoyoutuijian), R.drawable.icon2, mhaoyoutuijianIntent));
		mTabHost.addTab(buildTabSpec(TAB_TAG_SEARCH, getResources().getString(R.string.dongtaitixing),R.drawable.icon3, mdongtaitixingIntent));
		mTabHost.addTab(buildTabSpec(TAB_TAG_ACCOUNT,getResources().getString(R.string.gerenzhuye), R.drawable.icon4, mgerenzhuyeIntent));
	}
    private TabHost.TabSpec buildTabSpec(String tag, String resLabel, int resIcon,
			final Intent content) {
		return mTabHost
				.newTabSpec(tag)
				.setIndicator(resLabel,
						getResources().getDrawable(resIcon))
				.setContent(content);
	}
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			mBut1.performClick();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public void onClick(View v) {
		if (mCurTabId == v.getId()) {
			return;
		}
		mBut1.setImageResource(R.drawable.icon1);
		mBut2.setImageResource(R.drawable.icon2);
		mBut3.setImageResource(R.drawable.icon3);
		mBut4.setImageResource(R.drawable.icon4);
		int checkedId = v.getId();
		switch (checkedId) {
		case R.id.channel1:
			mBut1.setImageResource(R.drawable.icon1);
			linearLayout1.setBackgroundResource(R.drawable.bottom_onclick);
			linearLayout2.setBackgroundColor(Color.parseColor("#000000"));
			linearLayout3.setBackgroundColor(Color.parseColor("#000000"));
			linearLayout4.setBackgroundColor(Color.parseColor("#000000"));
			progressBar = ProgressDialog.show(JoyActivity.this, getResources().getString(R.string.shaohou), getResources().getString(R.string.pull_to_refresh_footer_refreshing_label));
			new Handler().postDelayed(new Runnable(){
				@Override
				public void run(){
					Message msg = new Message(); 
	                msg.what = 1; 
	                handler.sendMessage(msg); 
				}
			}, 1000);
			
			break;
		case R.id.channel2:
			mBut2.setImageResource(R.drawable.icon2);
			linearLayout2.setBackgroundResource(R.drawable.bottom_onclick);
			linearLayout1.setBackgroundColor(Color.parseColor("#000000"));
			linearLayout3.setBackgroundColor(Color.parseColor("#000000"));
			linearLayout4.setBackgroundColor(Color.parseColor("#000000"));
			progressBar = ProgressDialog.show(JoyActivity.this, getResources().getString(R.string.shaohou), getResources().getString(R.string.pull_to_refresh_footer_refreshing_label));
			new Handler().postDelayed(new Runnable(){
				@Override
				public void run(){
					Message msg = new Message(); 
	                msg.what = 2; 
	                handler.sendMessage(msg); 
				}
			}, 1000);
			
			break;
		case R.id.channel3:
			mBut3.setImageResource(R.drawable.icon3);
			linearLayout3.setBackgroundResource(R.drawable.bottom_onclick);
			linearLayout1.setBackgroundColor(Color.parseColor("#000000"));
			linearLayout2.setBackgroundColor(Color.parseColor("#000000"));
			linearLayout4.setBackgroundColor(Color.parseColor("#000000"));
			progressBar = ProgressDialog.show(JoyActivity.this, getResources().getString(R.string.shaohou), getResources().getString(R.string.pull_to_refresh_footer_refreshing_label));
			new Handler().postDelayed(new Runnable(){
				@Override
				public void run(){
					Message msg = new Message(); 
	                msg.what = 3; 
	                handler.sendMessage(msg); 
				}
			}, 1000);
			
			break;
		case R.id.channel4:
			mBut4.setImageResource(R.drawable.icon4);
			linearLayout4.setBackgroundResource(R.drawable.bottom_onclick);
			linearLayout1.setBackgroundColor(Color.parseColor("#000000"));
			linearLayout3.setBackgroundColor(Color.parseColor("#000000"));
			linearLayout2.setBackgroundColor(Color.parseColor("#000000"));
			progressBar = ProgressDialog.show(JoyActivity.this, getResources().getString(R.string.shaohou), getResources().getString(R.string.pull_to_refresh_footer_refreshing_label));
			new Handler().postDelayed(new Runnable(){
				@Override
				public void run(){
					Message msg = new Message(); 
	                msg.what = 4; 
	                handler.sendMessage(msg); 
				}
			}, 1000);
			
			break;
		}
		mCurTabId = checkedId;
	}
	public void onResume() { 
		super.onResume();
		MobclickAgent.onResume(this); 
	} 
	public void onPause() { 
		super.onPause(); 
		MobclickAgent.onPause(this); 
	}
}