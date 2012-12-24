package com.ijoyplus;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ijoyplus.Service.Return.ReturnTops;
import com.umeng.analytics.MobclickAgent;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class Z_Sug extends Activity {
	/** Called when the activity is first created. */
	private AQuery aq;
	private App app;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示标题
		setContentView(R.layout.z_sug);
		app = (App) getApplication();
		aq = new AQuery(this);
	}

	public void OnClickTab1TopLeft(View v) {
		finish();

	}
	public void OnClickSend(View v) {
		String content = aq.id(R.id.editText2).getText().toString();
		String url = Constant.BASE_URL + "user/feedback";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("content", content);

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.header("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
		cb.header("app_key", Constant.APPKEY);
		cb.header("user_id", app.UserID);

		cb.params(params).url(url).type(JSONObject.class)
				.weakHandler(this, "SugResult");

		aq.ajax(cb);

	}
	public void SugResult(String url, JSONObject json, AjaxStatus status) {
		if (json != null) {
			try {
				if (json.getString("res_code").trim().equalsIgnoreCase("00000")){
					app.MyToast(this, "提交成功，感谢你对我们工作的支持!");
					finish();
				}
				else
					app.MyToast(this, "提交失败，请检查网络!");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {

			// ajax error, show error code
			app.MyToast(aq.getContext(), getResources().getString(R.string.networknotwork));
		}

	}
	@Override
	protected void onDestroy() {
		if (aq != null)
			aq.dismiss();
		super.onDestroy();
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