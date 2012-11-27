package com.joy;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mobclick.android.MobclickAgent;

public class Yaoqing extends Activity {
	Context context;
	String name,phone;
	GetThird_AccessToken GetThird_AccessToken;
	TextView nameTextView,phoneTextView,textView;
	Button btn_yaoqing;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yaoqing);
		context=this;
		GetThird_AccessToken=(GetThird_AccessToken)getApplicationContext();
		nameTextView=(TextView)findViewById(R.id.yaoqing_name);
		phoneTextView=(TextView)findViewById(R.id.yaoqing_num);
		textView=(TextView)findViewById(R.id.yaoqing_text);
		btn_yaoqing=(Button)findViewById(R.id.btn_Yaoqing);
		if (GetThird_AccessToken.getActivitytype().equals("5")) {
			nameTextView.setText(getResources().getString(R.string.imformation_name)+":"+GetThird_AccessToken.getphoneName());
			phoneTextView.setText("XXXXXXXXXXXX");
			btn_yaoqing.setText(getResources().getString(R.string.yaoqing));
		}
		else {
			nameTextView.setText(getResources().getString(R.string.name)+GetThird_AccessToken.getphoneName());
			phoneTextView.setText(getResources().getString(R.string.phone)+GetThird_AccessToken.getphoneNum());
			btn_yaoqing.setText(getResources().getString(R.string.duanxinyaoqing));
		}
		textView.setText(GetThird_AccessToken.getphoneName()+getResources().getString(R.string.yaoqing_text));
		
		btn_yaoqing.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (GetThird_AccessToken.getActivitytype().equals("5")) {
					
				}
				else {
					String strDestAddress=GetThird_AccessToken.getphoneNum();
					String strMessage=getResources().getString(R.string.duanxinneirong);
					try {
						SmsManager smsManager = SmsManager.getDefault();
						PendingIntent mPI = PendingIntent.getBroadcast(
								context, 0, new Intent(), 0);
						smsManager.sendTextMessage(strDestAddress, null,
								strMessage, mPI, null);
						Toast.makeText(context, "发送成功", Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						Toast.makeText(context, "发送失败", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}
	//返回按钮
	public void Btn_back(View v){
		GetThird_AccessToken.setActivitytype("");
		finish();
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			GetThird_AccessToken.setActivitytype("");
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
