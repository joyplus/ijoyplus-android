package com.joyplus;

import java.net.URI;

import org.json.JSONException;
import org.json.JSONObject;

import com.joyplus.faye.FayeClient;
import com.joyplus.faye.FayeClient.FayeListener;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class Relieve_Binding extends Activity {
	FayeClient mClient;
	private String macAddress = null;
	private String tv_channel = null;

	private String user_id = null;
	App app;
	ProgressDialog pb;
	Handler mhandler;
	Context mContext;
	private static final String ue_screencast_unbinded = "解除绑定事件";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.relieve_binding);
		app = (App) getApplication();
		mContext = this;
		user_id = app.UserID;
		macAddress = app.GetServiceData("Binding_TV_Channal");
		tv_channel = "/screencast/" + macAddress;

		pb = new ProgressDialog(this);
		pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pb.setCanceledOnTouchOutside(false);
		pb.setCancelable(true);

		connect_TVChannel(tv_channel);

		ImageButton relieveBinding = (ImageButton) findViewById(R.id.relieve_binding);
		relieveBinding.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pb.show();
				if (!app.isNetworkAvailable()) {
					app.MyToast(Relieve_Binding.this, "您的网络有问题！");
					return;
				}
				if (tv_channel != null && user_id != null) {
						try {
							JSONObject et = new JSONObject();
							et.put("user_id", user_id);
							et.put("push_type", "33");
							et.put("tv_channel", macAddress);
							mClient.sendMessage(et);
							app.DeleteServiceData("Binding_TV");
							app.MyToast(Relieve_Binding.this, "已解除绑定");
							MobclickAgent.onEvent(mContext, ue_screencast_unbinded);
							finish();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}

			}
		});
		
//         mhandler = new Handler(){
//			
//			public void handleMessage(Message msg){
//				switch(msg.what){
//				case 1:
//					app.MyToast(Relieve_Binding.this, "已取消绑定");
//					break;
//				case 2:
//					app.MyToast(Relieve_Binding.this, "取消绑定失败");
//					break;
//				}
//			}
//		};

	}

	private void connect_TVChannel(String channel) {
		if (android.os.Build.VERSION.SDK_INT <= 8)
			return;
		try {
			URI uri = URI.create(Constant.TV_CHANNEL_URL);
			mClient = new FayeClient(null, uri, channel);
			mClient.setFayeListener(TVChannleListener);
			mClient.connectToServer(null);
		} catch (Exception ex) {
		}
	}

	public void OnClickTab1TopLeft(View v) {
		finish();
	}

	FayeListener TVChannleListener = new FayeListener() {

		@Override
		public void subscriptionFailedWithError(String error) {
			Log.i("TVChannleListener", "subscriptionFailedWithError" + error);

		}

		@Override
		public void subscribedToChannel(String subscription) {
			Log.i("TVChannleListener", "subscribedToChannel" + subscription);

		}

		@Override
		public void messageReceived(JSONObject json) {
			Log.i("TVChannleListener", "messageReceived>>>" + json.toString());
			if(json.toString() == null)
				return;
			String push_type = null;
//			push_type = (String) json.ge
//			Message message = new Message();
			try {
				push_type = json.getString("push_type");
			} catch (JSONException e) {
				e.printStackTrace();
			}
//			pb.dismiss();
//			if (push_type.equals("33")) {
//				message.what = 1;
//				app.DeleteServiceData("Binding_TV_Channal");
//				Log.i("TVChannleListener", "messageReceived" + json.toString());
//				finish();
//			}else if(!push_type.equals("33")){
//				message.what = 2;
//			}
//			mhandler.sendMessage(message);
		}

		@Override
		public void disconnectedFromServer() {
			Log.i("TVChannleListener", "disconnectedFromServer");

		}

		@Override
		public void connectedToServer() {
			Log.i("TVChannleListener", "connectedToServer");

		}
	};
}
