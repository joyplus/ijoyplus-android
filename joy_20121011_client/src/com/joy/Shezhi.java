package com.joy;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

public class Shezhi extends Activity {
	Context context;
	GetThird_AccessToken getThird_AccessToken;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shezhi);
		context=this;
		getThird_AccessToken = (GetThird_AccessToken) getApplicationContext();
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
		intent.setClass(context, Welcome.class);
		startActivity(intent);
		finish();
	}
	public void Btn_chazhao(View v){
		
	}
	public void Btn_yijianfankui(View v){
		
	}
	public void Btn_guanyuwomen(View v){
		
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
