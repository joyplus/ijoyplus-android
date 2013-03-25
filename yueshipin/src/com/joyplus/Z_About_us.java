package com.joyplus;

import com.umeng.analytics.MobclickAgent;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class Z_About_us extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.z_about_us);
	}

	public void OnClickTab1TopLeft(View v) {
		finish();

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