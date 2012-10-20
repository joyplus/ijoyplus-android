package com.joy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

public class Shezhi extends Activity {
	Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shezhi);
		context=this;
	}
	public void Btn_back(View v){
		finish();
	}
	public void Btn_zhuxiao(View v){
		
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
