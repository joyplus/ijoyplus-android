package com.joy;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

public class Xunzhaohaoyou extends Activity {
	Context context;
	GetThird_AccessToken getThird_AccessToken;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xunzhaohaoyou);
		getThird_AccessToken=(GetThird_AccessToken)getApplicationContext();
		context=this;
	}
	//返回按钮
	public void Btnback(View v){
		finish();
	}
	//达人推荐按钮
	public void Btndaren(View v){
		getThird_AccessToken.setActivitytype("2");
		Intent intent=new Intent();
		intent.setClass(context, Darentuijian.class);
		startActivity(intent);
	}
	//新浪微博好友按钮
	public void Btnsina(View v){
		Intent intent=new Intent();
		intent.setClass(context, Friend.class);
		startActivity(intent);
	}
	//腾讯微博好友
	public void Btntengxun(View v){
		Intent intent=new Intent();
		intent.setClass(context, Friend.class);
		startActivity(intent);
	}
	//查找通讯录好友按钮
	public void Btntongxunlu(View v){
		Intent intent=new Intent();
		intent.setClass(context, TongxunluList.class);
		startActivity(intent);
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public void onResume() { 
		super.onResume();
		MobclickAgent.onResume(this); 
	} 
	@Override
	public void onPause() { 
		super.onPause(); 
		MobclickAgent.onPause(this); 
	}
}
