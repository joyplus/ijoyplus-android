package com.joyplus.wxapi;


import com.joyplus.Constant;
import com.joyplus.R;
import com.joyplus.Weixin_Share;
import com.joyplus.widget.Log;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.ShowMessageFromWX;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXAppExtendObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	
	// IWXAPI �ǵ���app��΢��ͨ�ŵ�openapi�ӿ�
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i("WXEntryActivity","WXEntryActivity-->onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry);
        
        // ͨ��WXAPIFactory��������ȡIWXAPI��ʵ��
    	api = WXAPIFactory.createWXAPI(this, Constant.APP_ID, false);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		Log.i("WXEntryActivity","WXEntryActivity-->onNewIntent");
		super.onNewIntent(intent);
		
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	// ΢�ŷ������󵽵���Ӧ��ʱ����ص����÷���
	@Override
	public void onReq(BaseReq req) {
		Log.i("WXEntryActivity","WXEntryActivity-->onReq");
		switch (req.getType()) {
		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
			goToGetMsg();		
			break;
		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
			goToShowMsg((ShowMessageFromWX.Req) req);
			break;
		default:
			break;
		}
	}

	// ����Ӧ�÷��͵�΢�ŵ�����������Ӧ����ص����÷���
	@Override
	public void onResp(BaseResp resp) {
		Log.i("WXEntryActivity","WXEntryActivity-->onResp");
		int result = 0;
		
//		switch (resp.errCode) {
//		case BaseResp.ErrCode.ERR_OK:
//			result = R.string.errcode_success;
//			break;
//		case BaseResp.ErrCode.ERR_USER_CANCEL:
//			result = R.string.errcode_cancel;
//			break;
//		case BaseResp.ErrCode.ERR_AUTH_DENIED:
//			result = R.string.errcode_deny;
//			break;
//		default:
//			result = R.string.errcode_unknown;
//			break;
//		}
//		
//		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
	}
	
	private void goToGetMsg() {
		Log.i("WXEntryActivity","WXEntryActivity-->goToGetMsg");
		Intent intent = new Intent(this, Weixin_Share.class);
		intent.putExtras(getIntent());
		startActivity(intent);
		finish();
	}
	
	private void goToShowMsg(ShowMessageFromWX.Req showReq) {
		Log.i("WXEntryActivity","WXEntryActivity-->goToShowMsg");
		WXMediaMessage wxMsg = showReq.message;		
		WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;
		
		StringBuffer msg = new StringBuffer(); // ��֯һ������ʾ����Ϣ����
		msg.append("description: ");
		msg.append(wxMsg.description);
		msg.append("\n");
		msg.append("extInfo: ");
		msg.append(obj.extInfo);
		msg.append("\n");
		msg.append("filePath: ");
		msg.append(obj.filePath);
		
//		Intent intent = new Intent(this, ShowFromWXActivity.class);
//		intent.putExtra(Constants.ShowMsgActivity.STitle, wxMsg.title);
//		intent.putExtra(Constants.ShowMsgActivity.SMessage, msg.toString());
//		intent.putExtra(Constants.ShowMsgActivity.BAThumbData, wxMsg.thumbData);
//		startActivity(intent);
		finish();
	}
}