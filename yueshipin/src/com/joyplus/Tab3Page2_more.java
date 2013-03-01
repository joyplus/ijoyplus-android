package com.joyplus;

import com.umeng.analytics.MobclickAgent;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.androidquery.AQuery;

public class Tab3Page2_more extends Activity {
	private String TAG = "Tab3Page2";
	private AQuery aq;
	private App app;

	private ListView ItemsListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab3page2_more);
		app = (App) getApplication();
		aq = new AQuery(this);
		// 获取listview对象
		ItemsListView = (ListView) findViewById(R.id.listView1);
	
	}

	public void OnClickTab1TopLeft(View v) {
		Intent i = new Intent();

		this.setResult(101, i);
		this.finish();
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

}
