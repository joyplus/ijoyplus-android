package com.joyplus;
import com.umeng.analytics.MobclickAgent;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class Sina_Share extends Activity {
	private App app;
	private AQuery aq;
	private String prod_name = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sina_share);
		app = (App) getApplication();
		aq = new AQuery(this);
		Intent intent = getIntent();
		prod_name = intent.getStringExtra("prod_name");
		aq.id(R.id.program_name).text(prod_name);
		prod_name = "我刚看了<" + prod_name + ">,分享一下吧。";

		aq.id(R.id.multiAutoCompleteTextView1).text(prod_name);
		aq.id(R.id.multiAutoCompleteTextView1).getEditText()
				.setSelection(prod_name.length());

	}

	public void OnClickTab1TopLeft(View v) {
		finish();

	}

	// source false string 采用OAuth授权方式不需要此参数，其他授权方式为必填参数，数值为应用的AppKey。
	// access_token false string 采用OAuth授权方式为必填参数，其他授权方式不需要此参数，OAuth授权后获得。
	// status true string 要发布的微博文本内容，必须做URLencode，内容不超过140个汉字。
	// visible false int 微博的可见性，0：所有人能看，1：仅自己可见，2：密友可见，3：指定分组可见，默认为0。
	// list_id false string 微博的保护投递指定分组ID，只有当visible参数为3时生效且必选。
	// lat false float 纬度，有效范围：-90.0到+90.0，+表示北纬，默认为0.0。
	// long false float 经度，有效范围：-180.0到+180.0，+表示东经，默认为0.0。
	// annotations false string
	// 元数据，主要是为了方便第三方应用记录一些适合于自己使用的信息，每条微博可以包含一个或者多个元数据，必须以json字串的形式提交，字串长度不超过512个字符，具体内容可以自定。
	public void OnClickShare(View v) {
		InputMethodManager imm = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		aq.id(R.id.multiAutoCompleteTextView1).getTextView()
				.setCursorVisible(false);// 失去光标
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

		String share_word = aq.id(R.id.multiAutoCompleteTextView1).getText()
				.toString();
		String accessToken = null;
		accessToken = app.GetServiceData("Sina_Access_Token");

		String url = "https://api.weibo.com/2/statuses/update.json";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("access_token", accessToken);
		params.put("status", share_word);

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.header("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");

		cb.params(params).url(url).type(JSONObject.class)
				.weakHandler(this, "ShareResult");

		aq.progress(R.id.progress).ajax(cb);

	}

	public void ShareResult(String url, JSONObject json, AjaxStatus status) {

		app.MyToast(this, "分享成功!");
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
	@Override
	protected void onStart() {
		super.onStart();
	}
}
