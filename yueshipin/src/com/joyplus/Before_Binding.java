package com.joyplus;

import org.json.JSONException;
import org.json.JSONObject;

import com.joyplus.faye.FayeClient;
import com.joyplus.faye.FayeService;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class Before_Binding extends Activity {
	FayeClient mClient;
	private String macAddress = null;
	private String tv_channel = null;
	BindDingReceiver bindingReceiver;

	private String user_id = null;
	App app;
	ProgressDialog pb;
	Handler mhandler;
	Context mContext;
	private static final String ue_screencast_binded = "绑定成功";
	private static final String ue_screencast_binding = "发出绑定消息";
	private static final String ue_screencast_unbinded = "解除绑定事件";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.before_binding);
		registerBinding();
		Intent service = new Intent(this, FayeService.class);
		startService(service);
		app = (App) getApplication();
		mContext = this;
		user_id = app.UserID;
		Intent intent = getIntent();
		macAddress = intent.getStringExtra("SaoMiao_result");
		tv_channel = Constant.TV_CHANNEL + macAddress;

		pb = new ProgressDialog(this);
		pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pb.setCanceledOnTouchOutside(false);
		pb.setCancelable(true);

		

		ImageButton confirmBinding = (ImageButton) findViewById(R.id.confirm_binding);
		confirmBinding.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pb.show();
				if (!app.isNetworkAvailable()) {
					app.MyToast(Before_Binding.this, "您的网络有问题！");
					pb.dismiss();
					return;
				}
				if(app.GetServiceData("Binding_TV") != null){
					FayeService.FayeByService(mContext, "/screencast/"+app.GetServiceData("Binding_TV_Channal"));
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							try {
								JSONObject json = new JSONObject();
								json.put("user_id", user_id);
								json.put("push_type", "33");
								json.put("tv_channel", app.GetServiceData("Binding_TV_Channal"));
								FayeService.SendMessageService(mContext, json,user_id);
								MobclickAgent.onEvent(mContext,
										ue_screencast_unbinded);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}, 500);
				}
				if (tv_channel != null && user_id != null) {
					
					FayeService.FayeByService(mContext, tv_channel);
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							try {
								
								JSONObject et = new JSONObject();
								et.put("user_id", user_id);
								et.put("push_type", "31");
								et.put("tv_channel", "CHANNEL_TV_" + macAddress);
								FayeService.SendMessageService(mContext, et,
										user_id);
								MobclickAgent.onEvent(mContext,
										ue_screencast_binding);
					
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}, 500);
				}

			}
		});

		ImageButton cancel_Binding = (ImageButton) findViewById(R.id.cancel_binding);
		cancel_Binding.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

		// mhandler = new Handler() {
		//
		// public void handleMessage(Message msg) {
		// switch (msg.what) {
		// case 1:
		// app.MyToast(Before_Binding.this, "绑定成功");
		// break;
		// case 2:
		// app.MyToast(Before_Binding.this, "绑定失败");
		// break;
		// case 3:
		// app.MyToast(Before_Binding.this, "已绑定");
		// }
		// finish();
		// }
		// };

	}

	// private void connect_TVChannel(String channel) {
	// if (android.os.Build.VERSION.SDK_INT <= 8)
	// return;
	// try {
	// URI uri = URI.create(Constant.TV_CHANNEL_URL);
	// mClient = new FayeClient(null, uri, channel);
	// mClient.setFayeListener(TVChannleListener);
	// mClient.connectToServer(null);
	// } catch (Exception ex) {
	// }
	// }

	public void OnClickTab1TopLeft(View v) {
		finish();
	}

	// FayeListener TVChannleListener = new FayeListener() {
	//
	// @Override
	// public void subscriptionFailedWithError(String error) {
	// Log.i("TVChannleListener", "subscriptionFailedWithError" + error);
	//
	// }
	//
	// @Override
	// public void subscribedToChannel(String subscription) {
	// Log.i("TVChannleListener", "subscribedToChannel" + subscription);
	//
	// }
	//
	// @Override
	// public void messageReceived(JSONObject json) {
	// if (json.toString() == null)
	// return;
	// Message message = new Message();
	// String push_type = null;
	// String userid = null;
	// String result = null;
	// try {
	// push_type = (String) json.get("push_type");
	// userid = (String) json.getString("user_id");
	// result = json.getString("result");
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// pb.dismiss();
	// if (push_type.equals("32") && userid.equals(user_id)&&
	// result.equals("success")) {
	// app.SaveServiceData("Binding_TV_Channal", "CHANNEL_TV_"+macAddress);
	// app.SaveServiceData("Binding_TV", "success");
	// message.what = 1;
	// MobclickAgent.onEvent(mContext, ue_screencast_binded);
	// Log.i("TVChannleListener", "messageReceived>>>" + json.toString());
	// } else if (push_type.equals("32") && !userid.equals(user_id)) {
	// message.what = 2;
	// }
	// else if(push_type.equals("32") && userid.equals(user_id)&&
	// result.equals("fail")){
	// message.what = 3;
	// }
	// mhandler.sendMessage(message);
	// }
	//
	// @Override
	// public void disconnectedFromServer() {
	// Log.i("TVChannleListener", "disconnectedFromServer");
	//
	// }
	//
	// @Override
	// public void connectedToServer() {
	// Log.i("TVChannleListener", "connectedToServer");
	//
	// }
	// };

	@Override
	protected void onDestroy() {
		unregisterBinding();
		super.onDestroy();
	}

	/* 注册监听 */
	private void registerBinding() {
		bindingReceiver = new BindDingReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.joyplus.update_before_binding");
		registerReceiver(bindingReceiver, filter);

	}

	/* 取消监听 */
	private void unregisterBinding() {
		if (bindingReceiver != null) {
			this.unregisterReceiver(bindingReceiver);
		}
	}

	/* Broadcast监听 */
	public class BindDingReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			String status = bundle.getString("status");
			Log.i("", "BINDING_result>>>>>" + status);
			pb.dismiss();
			if (status.equals("success")) {
				app.SaveServiceData("Binding_TV_Channal", "CHANNEL_TV_"
						+ macAddress);
				app.SaveServiceData("Binding_Userid", user_id);
				app.SaveServiceData("Binding_TV", "success");
				app.MyToast(Before_Binding.this, "绑定成功");
				MobclickAgent.onEvent(mContext, ue_screencast_binded);
			} else {
				app.MyToast(Before_Binding.this, "绑定失败");
			}
			finish();
		}
	}
}
