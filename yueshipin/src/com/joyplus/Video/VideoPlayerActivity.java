/*
 * Copyright (C) 2011 VOV IO (http://vov.io/)
 */

package com.joyplus.Video;

import java.io.IOException;

import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyplus.App;
import com.joyplus.Constant;
import com.joyplus.R;
import com.joyplus.Dlna.DlnaSelectDevice;
import com.joyplus.Service.Return.ReturnProgramView;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class VideoPlayerActivity extends Activity implements OnCompletionListener {

	private AQuery aq;
	private App app;
	private ReturnProgramView m_ReturnProgramView = null;
	private String mPath;
	private String mTitle;
	private String prod_id;
	private boolean checkBind = false;
	private VideoView mVideoView;
	private View mVolumeBrightnessLayout;
	private ImageView mOperationBg;
	private ImageView mOperationPercent;
	private AudioManager mAudioManager;
	private View mRelativeLayoutBG;
	private ImageView mImage_preload_bg;
	long current_time = 0;
	public static int RETURN_CURRENT_TIME = 150;
	
	/** 最大声音 */
	private int mMaxVolume;
	/** 当前声音 */
	private int mVolume = -1;
	/** 当前亮度 */
	private float mBrightness = -1f;
	/** 当前缩放模式 */
	private int mLayout = VideoView.VIDEO_LAYOUT_STRETCH;//VIDEO_LAYOUT_ZOOM;
	private GestureDetector mGestureDetector;
	private MediaController mMediaController;

	private DlnaSelectDevice mMyService;

	private ServiceConnection mServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mMyService = ((DlnaSelectDevice.MyBinder) service).getService();
			mVideoView.setServiceConnection(mMyService);
		}

		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub

		}
	};
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		if (!LibsChecker.checkVitamioLibs(this, R.string.init_decoders))
			return;

		app = (App) getApplication();
		aq = new AQuery(this);
		
		Intent intent = getIntent();
		mPath = intent.getStringExtra("path");
		mTitle = intent.getStringExtra("title");
		prod_id = intent.getStringExtra("pro_id");
	
//		mPath = "http://122.228.96.172/20/40/95/letv-uts/1340994301-AVC-249889-AAC-31999-5431055-192873082-4b45f1fd5362d980a1dae9c44b2b1c6b-1340994301.mp4?crypt=3eb0ad42aa7f2e559&b=2000&gn=860&nc=1&bf=22&p2p=1&video_type=mp4&check=0&tm=1354698000&key=78cc2270a7e5dfe3187c1608c99e65c0&lgn=letv&proxy=1945014819&cipi=1034815956&tag=mobile&np=1&vtype=mp4&ptype=s1&level=350&t=1354601822&cid=&vid=&sign=mb&dname=mobile";
//		mPath = "http://122.228.96.172/20/40/95/letv-uts/1340994301-AVC-249889-AAC-31999-5431055-192873082-4b45f1fd5362d980a1dae9c44b2b1c6b-1340994301.mp4?crypt=3eb0ad42aa7f2e559&b=2000&gn=860&nc=1&bf=22&p2p=1&video_type=mp4&check=0&tm=1354698000&key=78cc2270a7e5dfe3187c1608c99e65c0&lgn=letv&proxy=1945014819&cipi=1034815956&tag=mobile&np=1&vtype=mp4&ptype=s1&level=350&t=1354601822&cid=&vid=&sign=mb&dname=mobile";
//		mPath = "http://114.80.187.218/25/36/53/kingsoft/movie/47978987920B0079FF686B6370B4E039-xiyoupian.mp4?crypt=3a3fb98daa7f2e300&b=800&gn=812&nc=1&bf=30&p2p=1&video_type=mp4&check=0&tm=1363662000&key=19234b660387c681a8f47a30cd2f21cb&opck=1&lgn=letv&proxy=2002892265&cipi=2085452187&tsnp=1&tag=ios&tag=kingsoft&sign=coopdown&realext=.mp4&test=m3u8";
//		mTitle = "x3";
		if (TextUtils.isEmpty(mPath))
			mPath = Environment.getExternalStorageDirectory() + "/mnt/sdcard/t.mp4";
		else if (intent.getData() != null)
			mPath = intent.getData().toString();

		setContentView(R.layout.videoview);
		mVideoView = (VideoView) findViewById(R.id.surface_view);
		mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
		mOperationBg = (ImageView) findViewById(R.id.operation_bg);
		mOperationPercent = (ImageView) findViewById(R.id.operation_percent);
		
		mImage_preload_bg = (ImageView) findViewById(R.id.layout_preload_bg);
		mRelativeLayoutBG = findViewById(R.id.relativeLayout_preload);

		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		mImage_preload_bg.setBackgroundResource(R.drawable.player_bg);
		mRelativeLayoutBG.setVisibility(View.VISIBLE);
		mVideoView.setLayoutBG(mRelativeLayoutBG);
		
		if(mTitle != null && mTitle.length()>0){
			aq.id(R.id.mediacontroller_file_name).text(mTitle);
			aq.id(R.id.textView1).text("正在载入 "+ mTitle + "，请稍后 ...");
		}
		if (prod_id != null)
			GetServiceData();
		
		if (mPath.startsWith("http:") || mPath.startsWith("https:"))
			mVideoView.setVideoURI(Uri.parse(mPath));
		else
			mVideoView.setVideoPath(mPath);
		//
		mVideoView.setOnCompletionListener(this);

		mMediaController = new MediaController(this);
		//设置显示名称
		mVideoView.setTitle(mTitle);
		mMediaController.setFileName(mTitle);
		mVideoView.setMediaController(mMediaController);
		
		mVideoView.requestFocus();

		mGestureDetector = new GestureDetector(this, new MyGestureListener());

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		Intent i = new Intent();
		i.setClass(this, DlnaSelectDevice.class);
		bindService(i, mServiceConnection, BIND_AUTO_CREATE);
		checkBind = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mVideoView != null)
		{
			mVideoView.pause();
		}	
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mVideoView != null)
		{
			//current_time = mVideoView.getCurrentPosition();
			mVideoView.resume();
		}
			
	}

	@Override
	protected void onDestroy() {
		if (aq != null)
			aq.dismiss();  
		  
		if (mVideoView != null){
			mVideoView.stopPlayback();
		}
		if(checkBind)  
			unbindService(mServiceConnection);
		super.onDestroy();
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mGestureDetector.onTouchEvent(event))
			return true;

		// 处理手势结束
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_UP:
			endGesture();
			break;
		}

		return super.onTouchEvent(event);
	}
	public void OnClickReturn(View v) {
	
		finish();

	}
	public void OnClickSelect(View v) {

	}
	
	/** 手势结束 */
	private void endGesture() {
		mVolume = -1;
		mBrightness = -1f;

		// 隐藏
		mDismissHandler.removeMessages(0);
		mDismissHandler.sendEmptyMessageDelayed(0, 500);
	}

	private class MyGestureListener extends SimpleOnGestureListener {

		/** 双击 */
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			mLayout++;
			if(mLayout >VideoView.VIDEO_LAYOUT_ZOOM)
				mLayout = VideoView.VIDEO_LAYOUT_ORIGIN;
			if (mVideoView != null)
				mVideoView.setVideoLayout(mLayout, 0);
			return true;
		}

		/** 滑动 */
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			float mOldX = e1.getX(), mOldY = e1.getY();
			int y = (int) e2.getRawY();
			Display disp = getWindowManager().getDefaultDisplay();
			int windowWidth = disp.getWidth();
			int windowHeight = disp.getHeight();

			if (mOldX > windowWidth * 4.0 / 5)// 右边滑动
				onVolumeSlide((mOldY - y) / windowHeight);
			else if (mOldX < windowWidth / 5.0)// 左边滑动
				onBrightnessSlide((mOldY - y) / windowHeight);

			return super.onScroll(e1, e2, distanceX, distanceY);
		}
	}

	/** 定时隐藏 */
	private Handler mDismissHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mVolumeBrightnessLayout.setVisibility(View.GONE);
		}
	};

	/**
	 * 滑动改变声音大小
	 * 
	 * @param percent
	 */
	private void onVolumeSlide(float percent) {
		if (mVolume == -1) {
			mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (mVolume < 0)
				mVolume = 0;

			// 显示
			mOperationBg.setImageResource(R.drawable.video_volumn_bg);
			mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
		}

		int index = (int) (percent * mMaxVolume) + mVolume;
		if (index > mMaxVolume)
			index = mMaxVolume;
		else if (index < 0)
			index = 0;

		// 变更声音
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

		// 变更进度条
		ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
		lp.width = findViewById(R.id.operation_full).getLayoutParams().width * index / mMaxVolume;
		mOperationPercent.setLayoutParams(lp);
	}

	/**
	 * 滑动改变亮度
	 * 
	 * @param percent
	 */
	private void onBrightnessSlide(float percent) {
		if (mBrightness < 0) {
			mBrightness = getWindow().getAttributes().screenBrightness;
			if (mBrightness <= 0.00f)
				mBrightness = 0.50f;
			if (mBrightness < 0.01f)
				mBrightness = 0.01f;

			// 显示
			mOperationBg.setImageResource(R.drawable.video_brightness_bg);
			mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
		}
		WindowManager.LayoutParams lpa = getWindow().getAttributes();
		lpa.screenBrightness = mBrightness + percent;
		if (lpa.screenBrightness > 1.0f)
			lpa.screenBrightness = 1.0f;
		else if (lpa.screenBrightness < 0.01f)
			lpa.screenBrightness = 0.01f;
		getWindow().setAttributes(lpa);

		ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
		lp.width = (int) (findViewById(R.id.operation_full).getLayoutParams().width * lpa.screenBrightness);
		mOperationPercent.setLayoutParams(lp);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (mVideoView != null)
			mVideoView.setVideoLayout(mLayout, 0);
		super.onConfigurationChanged(newConfig);
	}
	
//	
//	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//		if(keyCode == 4)
//		{
//			if(mVideoView!=null)
//			{
//				Intent intent = new Intent();
//				Bundle bundle = new Bundle();
//				bundle.putInt("current_time", (int)(mVideoView.getCurrentPosition()));
//				bundle.putInt("total_time", (int)(mVideoView.getDuration()));
//				intent.putExtras(bundle);
//				setResult(RETURN_CURRENT_TIME,intent);
//				mVideoView.stopPlayback();
//			}
//		}
//		finish();
//		return super.onKeyDown(keyCode, event);
//	}

	@Override
	public void onCompletion(MediaPlayer player) {
//		finish();
	}
	public void GetServiceData() {
		String url = Constant.BASE_URL + "program/view?prod_id=" + prod_id;

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class).weakHandler(this, "GetServiceDataResult");

		cb.header("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
		cb.header("app_key", Constant.APPKEY);
		cb.header("user_id", app.UserID);

		aq.ajax(cb);

	}
	// 初始化list数据函数
	public void GetServiceDataResult(String url, JSONObject json, AjaxStatus status) {
		if (status.getCode() == AjaxStatus.NETWORK_ERROR) {
			app.MyToast(aq.getContext(),
					getResources().getString(R.string.networknotwork));
			return;
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			m_ReturnProgramView = mapper.readValue(json.toString(),
					ReturnProgramView.class);
			if(mMediaController != null)
				mMediaController.setProd_Data(m_ReturnProgramView);
			// 创建数据源对象
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
