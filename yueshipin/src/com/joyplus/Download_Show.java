package com.joyplus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.androidquery.AQuery;
import com.joyplus.Service.Return.ReturnProgramView;
import com.umeng.analytics.MobclickAgent;

public class Download_Show extends Activity {
	private AQuery aq;
	private String TAG = "Detail_Show";
	private App app;
	private ReturnProgramView m_ReturnProgramView = null;
	private String prod_id = null;
	private String PROD_SOURCE = null;
	private String PROD_URI = null;
	private int page_num = 0;
	private int m_FavorityNum = 0;
	private int m_SupportNum = 0;

	private String uid = null;
	private String token = null;
	private String expires_in = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download_show);
		app = (App) getApplication();
		Intent intent = getIntent();
		prod_id = intent.getStringExtra("prod_id");
		aq = new AQuery(this);
		
		aq.id(R.id.scrollView1).gone();

		if (prod_id != null)
			//GetServiceData();
			;

	}

	public void OnClickTab1TopLeft(View v) {
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

	public void OnClickImageView(View v) {

	}
}

