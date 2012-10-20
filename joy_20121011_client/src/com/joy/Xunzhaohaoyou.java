package com.joy;

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
	public void Btnback(View v){
		finish();
	}
	public void Btndaren(View v){
		getThird_AccessToken.setActivitytype("2");
		Intent intent=new Intent();
		intent.setClass(context, Darentuijian.class);
		startActivity(intent);
	}
	public void Btnsina(View v){
		
	}
	public void Btntengxun(View v){
		
	}
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
}
