package com.joyplus;

import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Logo extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//
		//ReadLocalAppKey();

		requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示标题
		setContentView(R.layout.logo);// 显示welcom.xml
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏显示
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setOnDownloadListener(null);
		UmengUpdateAgent.update(this);
		final Intent intent = new Intent(Logo.this, Main.class);// AndroidMainScreen为主界面
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				startActivity(intent);
				//添加新手引导程序处理过程
				/*if(true)
				{
					Intent guider = new Intent(Logo.this,GuiderHelper.class);
					startActivity(guider);
				}*/
				finish();
			}
		};
		timer.schedule(task, 1500); // 显示Logo图片2S后，自动跳转到主界面
		
	}

	public void ReadLocalAppKey() {
		// TODO Auto-generated method stub
		// online 获取APPKEY
		MobclickAgent.updateOnlineConfig(this);
		String OnLine_Appkey = MobclickAgent.getConfigParams(this, "APPKEY");
		String Local_Appkey = null;
		// if have time ,changing this's SharedPreferences to the ServiceData
		SharedPreferences APPKEY_Setting = getSharedPreferences("LOCAL_APPKEY",
				0);
		Local_Appkey = APPKEY_Setting.getString("APPKEY", ""); // 取出保存的 APPKEY
		if (OnLine_Appkey == null) {
			if (Local_Appkey == "") {
				Constant.APPKEY = Constant.DEFAULT_APPKEY;
				APPKEY_Setting.edit().putString("APPKEY", Constant.APPKEY)
						.commit();
			} else {
				Constant.APPKEY = Local_Appkey;
			}
		} else {
			// if(OnLine_Appkey == Local_Appkey)
			if (OnLine_Appkey.equalsIgnoreCase(Local_Appkey)) {
				Constant.APPKEY = Local_Appkey;
			} else {
				Constant.APPKEY = OnLine_Appkey;
				APPKEY_Setting.edit().putString("APPKEY", Constant.APPKEY)
						.commit();
			}
		}
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