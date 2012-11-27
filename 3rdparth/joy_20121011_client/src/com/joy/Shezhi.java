package com.joy;



import com.mobclick.android.MobclickAgent;
import com.umeng.fb.UMFeedbackService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Shezhi extends Activity {
	Context context;
	GetThird_AccessToken getThird_AccessToken;
	TextView name;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shezhi);
		context=this;
		getThird_AccessToken = (GetThird_AccessToken) getApplicationContext();
		name=(TextView)findViewById(R.id.shezhi_name);
		name.setText(getResources().getString(R.string.zhuxiao)+"Name");//Name是你的帐号
	}
	public void Btn_back(View v){
		finish();
	}
	public void Btn_zhuxiao(View v){
		//清空新浪token值
		getThird_AccessToken.setAccessToken("");
		getThird_AccessToken.setExpires_in("");
		getThird_AccessToken.SaveAccessToken();
		getThird_AccessToken.SaveExpires_in();
		//清空腾讯Token值
		getThird_AccessToken.setQQ_Token("");
		getThird_AccessToken.setOpenID("");
		getThird_AccessToken.SaveQQAccessToken();
		getThird_AccessToken.SaveOpenID();
		Toast.makeText(context, getString(R.string.zhuxiaochenggong), Toast.LENGTH_SHORT).show();
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setClass(context, Welcome.class);
		startActivity(intent);
		((Activity04) getThird_AccessToken.getcontext()).finish();
		finish();
	}
	//查找/添加好友按钮
	public void Btn_chazhao(View v){
		Intent intent=new Intent();
		intent.setClass(context, Xunzhaohaoyou.class);
		startActivity(intent);
	}
	//意见反馈按钮
	public void Btn_yijianfankui(View v){
		UMFeedbackService.setGoBackButtonVisible();
		UMFeedbackService
		.openUmengFeedbackSDK(Shezhi.this);
	}
	//关于我们按钮
	public void Btn_guanyuwomen(View v){
		
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
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
