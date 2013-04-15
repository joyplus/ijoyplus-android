package com.joyplus;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SendCallback;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class Relieve_Binding extends Activity {
	private String macAddress = null;
	private String channel = null;
	private JSONObject data;
	private String user_id = null;
	App app;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.relieve_binding);
		app = (App) getApplication();
		user_id = app.UserID;
		WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
		if (info != null) {
			macAddress = info.getMacAddress();
			channel = "CHANNEL_TV_" + macAddress;
		}

		ImageButton relieveBinding = (ImageButton) findViewById(R.id.relieve_binding);
		relieveBinding.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!app.isNetworkAvailable()) {
					app.MyToast(Relieve_Binding.this, "您的网络有问题！");
					return;
				}
				if (channel != null && user_id != null) {
					try {
						data = new JSONObject(
								"{\"push_type\": \"5\", \"user_id\": user_id}");
						ParsePush push = new ParsePush();

						push.setChannel(channel);
						push.setData(data);
						push.sendInBackground(callback);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		});
	}
	SendCallback callback = new SendCallback() {

		@Override
		public void done(ParseException arg0) {
			if (arg0 == null) {
				app.MyToast(Relieve_Binding.this, "已解除绑定");
				app.DeleteServiceData("Binding_TV");
				finish();
				// Log.d("push", "success!");
			} else {
				app.MyToast(Relieve_Binding.this, "操作失败！");
				// Log.d("push", "failure");
			}

		}
	};
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	public void OnClickTab1TopLeft(View v){
		finish();
	}
}
