package com.joyplus;

import com.umeng.analytics.MobclickAgent;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.androidquery.AQuery;

public class Tab3Page3_more extends Activity {
	private AQuery aq;
	private App app;
	private ListView ItemsListView;
	private static String MY_TOP_LIST_DETAI  = "我的悦单详细";
	Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab3page3_more);
		app = (App) getApplication();
		aq = new AQuery(this);
		mContext = this;
		// 获取listview对象
		ItemsListView = (ListView) findViewById(R.id.listView1);
	
	}

	public void OnClickTab1TopLeft(View v) {
		setResult(101);
		finish();
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
		MobclickAgent.onEventBegin(mContext, MY_TOP_LIST_DETAI);
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onEventEnd(mContext, MY_TOP_LIST_DETAI);
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

}
