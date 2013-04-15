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

public class Before_Binding extends Activity {
	private String macAddress = null;
	private String channel = null;
	private JSONObject data;
	private String user_id = null;
	App app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.before_binding);
		app = (App) getApplication();
		user_id = app.UserID;
		WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
		if (info != null) {
			macAddress = info.getMacAddress();
			channel = "CHANNEL_TV_" + macAddress;
		}

		ImageButton confirmBinding = (ImageButton) findViewById(R.id.confirm_binding);
		confirmBinding.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!app.isNetworkAvailable()) {
					app.MyToast(Before_Binding.this, "您的网络有问题！");
					return;
				}
				if (channel != null && user_id != null) {
					try {
						data = new JSONObject(
								"{\"push_type\": \"3\", \"user_id\": user_id}");
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
		
		ImageButton cancel_Binding = (ImageButton)findViewById(R.id.cancel_binding);
		cancel_Binding.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
	
	}

	SendCallback callback = new SendCallback() {

		@Override
		public void done(ParseException arg0) {
			if (arg0 == null) {
				app.MyToast(Before_Binding.this, "已成功绑定");
				app.SaveServiceData("Binding_TV", "Binding_TV");
				finish();
				// Log.d("push", "success!");
			} else {
				app.MyToast(Before_Binding.this, "绑定失败！");
				// Log.d("push", "failure");
			}

		}
	};

	public void OnClickTab1TopLeft(View v) {
		finish();
	}
}


