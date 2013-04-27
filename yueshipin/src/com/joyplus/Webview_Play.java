package com.joyplus;

import com.androidquery.AQuery;
import com.joyplus.Adapters.CurrentPlayData;
import com.joyplus.Video.VideoPlayerActivity;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

public class Webview_Play extends Activity {
	private static final String TAG = "Webview_Play";
	private static final int FINISH_ACTTIVITY = 10;
	private WebView webView;
	private String name;
	private String prod_uri;

	private String prod_subname;
	private String prod_id;
	private int CurrentIndex;
	private int CurrentCategory;
	private String PROD_SOURCE;
	private String prod_type;
	private long current_time;
	public Handler fHandler;
	private ImageView imageview;
	private App app;
	private CurrentPlayData mCurrentPlayData;
	AlphaAnimation mAnimation = null;
	AQuery aq;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 取消标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 进行全屏

		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.webviewplay);
		// 保持常亮
		findViewById(R.id.webview_layout).setKeepScreenOn(true);
		app = (App) getApplication();
		aq = new AQuery(this);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		// WebView界面信息
		prod_uri = bundle.getString("PROD_URI");
		name = bundle.getString("NAME");
		prod_subname = bundle.getString("prod_subname");

		// 播放器界面信息
		prod_id = bundle.getString("prod_id");
		CurrentIndex = bundle.getInt("CurrentIndex");
		CurrentCategory = bundle.getInt("CurrentCategory");
		PROD_SOURCE = bundle.getString("PROD_SOURCE");
		prod_type = bundle.getString("prod_type");
		current_time = bundle.getLong("current_time");

		mCurrentPlayData = new CurrentPlayData();
		mCurrentPlayData.prod_id = prod_id;
		mCurrentPlayData.CurrentIndex = CurrentIndex;
		// 实例化WebView
		webView = (WebView) this.findViewById(R.id.webView1);
		TextView textview = (TextView) findViewById(R.id.program_name);
		textview.setText(name + " " + "第" + prod_subname + "集");
		if (prod_subname == null) {
			textview.setText(name);
		}

		imageview = (ImageView) findViewById(R.id.image_view);

		// 设置加载进来的页面自适应手机屏幕
		WebSettings settings = webView.getSettings();
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);
		/**
		 * 调用loadUrl()方法进行加载内容
		 */
		webView.loadUrl(prod_uri);
		/**
		 * 设置WebView的属性，此时可以去执行JavaScript脚本
		 */
		settings.setJavaScriptEnabled(true);

		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		newHandler();
	}

	public void newHandler() {
		new Handler().postDelayed(new Runnable() {

			public void run() {
					if (PROD_SOURCE != null && PROD_SOURCE.trim().length() > 0) {
							aq.id(R.id.image_view).visible();
							mAnimation = new AlphaAnimation(0f, 1.0f);
							mAnimation.setDuration(500);
							imageview.startAnimation(mAnimation);
							mCurrentPlayData.CurrentIndex = CurrentIndex;
							CallVideoPlayActivity(PROD_SOURCE, name);
					}
				aq.id(R.id.image_view).gone();
			}
		}, 500);
	}

	@Override
	protected void onDestroy() {
		webView.destroy();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		webView.pauseTimers();
		super.onPause();
	}

	@Override
	public void onResume() {
		webView.resumeTimers();
		super.onResume();
	}

	public void OnClickTab1TopLeft(View v) {
		finish();
	}

	public void CallVideoPlayActivity(String m_uri, String title) {
		app.IfSupportFormat(m_uri);
		mCurrentPlayData.CurrentCategory = CurrentCategory;

		app.setCurrentPlayData(mCurrentPlayData);

		Intent intent = new Intent(this, VideoPlayerActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("path", m_uri);
		bundle.putString("title", title);
		bundle.putString("prod_id", prod_id);
		bundle.putString("prod_subname", prod_subname);
		bundle.putString("prod_type", prod_type);
		bundle.putLong("current_time", current_time);
		intent.putExtras(bundle);
		startActivityForResult(intent, FINISH_ACTTIVITY);
		if (app.GetServiceData("firstplayvideo") == null)
		{
			app.SaveServiceData("firstplayvideo", "firstplayvideo");
			Webview_Play.this.finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == FINISH_ACTTIVITY) {
			Webview_Play.this.finish();
		}
	}
}
