package com.joyplus;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.ShowMessageFromWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXAppExtendObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.umeng.analytics.MobclickAgent;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class Logo extends Activity  implements IWXAPIEventHandler{
	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	// IWXAPI 微信实例
    private IWXAPI api;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.onError(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示标题
		setContentView(R.layout.logo);// 显示welcom.xml
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏显示
		//微信实例
		api = WXAPIFactory.createWXAPI(this, Constant.APP_ID, false);
		api.handleIntent(getIntent(), this);
		
		final Intent intent = new Intent(Logo.this, Main.class);// AndroidMainScreen为主界面
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				startActivity(intent);
				finish();
			}
		};
		timer.schedule(task, 1500); // 显示Logo图片2S后，自动跳转到主界面
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
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onNewIntent(android.content.Intent)
	 * 微信新的intent
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		setIntent(intent);
        api.handleIntent(intent, this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.tencent.mm.sdk.openapi.IWXAPIEventHandler#onReq(com.tencent.mm.sdk.openapi.BaseReq)
	 */
	@Override
	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getType()) {
		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
			goToGetMsg();		
			break;
		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
			goToShowMsg((ShowMessageFromWX.Req) arg0);
			Toast.makeText(Logo.this, "悦视频", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.tencent.mm.sdk.openapi.IWXAPIEventHandler#onResp(com.tencent.mm.sdk.openapi.BaseResp)
	 */
	@Override
	public void onResp(BaseResp arg0) {
		// TODO Auto-generated method stub
		int result = 0;
		
		switch (arg0.errCode) {

		}
	}
	
	private void goToGetMsg() {
		Intent intent = new Intent(this, Weixin_Share.class);//显示你要显示的activity
		intent.putExtras(getIntent());
		startActivity(intent);
		finish();
	}
	
	private void goToShowMsg(ShowMessageFromWX.Req showReq) {
//		WXMediaMessage wxMsg = showReq.message;		
//		WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;
//		
//		StringBuffer msg = new StringBuffer(); // ��֯һ������ʾ����Ϣ����
//		msg.append("description: ");
//		msg.append(wxMsg.description);
//		msg.append("\n");
//		msg.append("extInfo: ");
//		msg.append(obj.extInfo);
//		msg.append("\n");
//		msg.append("filePath: ");
//		msg.append(obj.filePath);
		
//		Intent intent = new Intent(this, ShowFromWXActivity.class);// 显示当前的消息
//		intent.putExtra(Constant.ShowMsgActivity.STitle, wxMsg.title);
//		intent.putExtra(Constant.ShowMsgActivity.SMessage, msg.toString());
//		intent.putExtra(Constant.ShowMsgActivity.BAThumbData, wxMsg.thumbData);
//		startActivity(intent);
		finish();
	}
	
}