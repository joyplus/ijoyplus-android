package com.joyplus;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class Tab3Page3_Create1 extends Activity {
	private AQuery aq;
	private App app;
	private int m_RadioSelect = 0;
	private String title = null;
	private String content = null;
	private String topic_id = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab3page3_create1);

		app = (App) getApplication();
		aq = new AQuery(this);
		RadioGroup radioGroup = (RadioGroup) this
				.findViewById(R.id.radioGroup1);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.radio0:
					m_RadioSelect = 0;
					break;
				case R.id.radio1:
					m_RadioSelect = 1;
					break;
				default:
					break;
				}
			}
		});
	}

	public void OnClickCancle(View v) {
		finish();
	}

	public void OnClickTab1TopRight(View v) {
		Intent i = new Intent(this, Setting.class);
		startActivity(i);

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

	@Override
	protected void onStart() {
		super.onStart();
	}

	public void OnClickNext(View v) {
		title = aq.id(R.id.editText1).getText().toString().trim();
		content = aq.id(R.id.editText2).getText().toString();

		if (title == null || title.length() == 0) {
			app.MyToast(this, "请输入标题.");
			return;
		}
		if (content == null || content.length() == 0)
			content = title;

		// 添加榜单
		String url = Constant.BASE_URL + "top/new";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", title);
		params.put("content", content);

		if (m_RadioSelect == 1) {// 电视剧
			params.put("type", 2);
		} else if (m_RadioSelect == 0) // 电影
			params.put("type", 1);

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.header("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
		cb.header("app_key", Constant.APPKEY);
		cb.header("user_id", app.UserID);

		cb.params(params).url(url).type(JSONObject.class)
				.weakHandler(this, "AddBangDanResult");

		aq.ajax(cb);

	}

	public void AddBangDanResult(String url, JSONObject json, AjaxStatus status) {
		if (json != null) {
			try {
				topic_id = json.getString("topic_id").trim();
				if (topic_id.length() > 0) {
					// topic_id = json.getString("topic_id").trim();
					app.MyToast(this, "悦单新增成功");
					Intent intent = new Intent(this, Tab3Page3_Create2.class);
					intent.putExtra("title", title);
					intent.putExtra("content", content);
					intent.putExtra("topic_id", topic_id);
					intent.putExtra("Create", true);
					startActivityForResult(intent, 1);
					finish();
				} else {
					app.MyToast(aq.getContext(), "不能建立同名悦单");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				app.MyToast(aq.getContext(), "不能建立同名悦单");
			}

		} else {
			app.MyToast(aq.getContext(), "不能建立同名悦单");
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		setResult(101);
		finish();
	}
}
