/*
 * Copyright (C) 2011 VOV IO (http://vov.io/)
 */

package com.joyplus.Video;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
import com.joyplus.download.Dao;

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

public class VideoPlayerActivity extends Activity implements
		OnCompletionListener {

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
	long play_current_time = 0;
	public static int RETURN_CURRENT_TIME = 150;

	/** 最大声音 */
	private int mMaxVolume;
	/** 当前声音 */
	private int mVolume = -1;
	/** 当前亮度 */
	private float mBrightness = -1f;
	/** 当前缩放模式 */
	private int mLayout = VideoView.VIDEO_LAYOUT_STRETCH;// VIDEO_LAYOUT_ZOOM;
	private GestureDetector mGestureDetector;
	private MediaController mMediaController;

	private DlnaSelectDevice mMyService;

	/*
	 * playHistoryData
	 */
	private PlayHistory playhistory = null;
	private String playProdId = null;// 视频id
	private String playProdName = null;// 视频名字
	private String playProdSubName = null;// 视频的集数
	private String playPlayType = null;// 播放的类别 1: 视频地址播放 2:webview播放
	private String playVideoUrl = null;// 视频url
	private int playProdType = 0;// 视频类别 1：电影，2：电视剧，3：综艺，4：视频

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
		InitPlayData();
		setContentView(R.layout.videoview);
		app = (App) getApplication();
		aq = new AQuery(this);
		// 每次播放时及时把播放的flag清除为0
		if (app.use2G3G) {
			app.use2G3G = false;
		}
		mVideoView = (VideoView) findViewById(R.id.surface_view);
		mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
		mOperationBg = (ImageView) findViewById(R.id.operation_bg);
		mOperationPercent = (ImageView) findViewById(R.id.operation_percent);

		mImage_preload_bg = (ImageView) findViewById(R.id.layout_preload_bg);
		mRelativeLayoutBG = findViewById(R.id.relativeLayout_preload);

		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mAudioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		mImage_preload_bg.setBackgroundResource(R.drawable.player_bg);
		mRelativeLayoutBG.setVisibility(View.VISIBLE);
		mVideoView.setLayoutBG(mRelativeLayoutBG);

		if (mTitle != null && mTitle.length() > 0) {
			aq.id(R.id.mediacontroller_file_name).text(mTitle);
			aq.id(R.id.textView1).text("正在载入 " + mTitle + "，请稍后 ...");
		}
		if (prod_id != null)
			GetServiceData();

		if (mPath.startsWith("http:") || mPath.startsWith("https:"))
			mVideoView.setVideoURI(Uri.parse(mPath));
		else
			mVideoView.setVideoPath(mPath);
		//
		mVideoView.setOnCompletionListener(this);
		if (play_current_time > 0)
			mVideoView.seekTo(play_current_time);
		mMediaController = new MediaController(this);
		// 设置显示名称
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

	public void InitPlayData() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mPath = bundle.getString("path");
		mTitle = bundle.getString("title");
		playProdName = mTitle;
		playVideoUrl = mPath;
		playProdId = bundle.getString("prod_id");
		playProdSubName = bundle.getString("prod_subname");
		playProdType = Integer.parseInt(bundle.getString("prod_type"));
		play_current_time = bundle.getLong("current_time");
		playhistory = new PlayHistory(playProdId, playProdSubName,//这个历史播放记录变量总是要初始化
				play_current_time + "");
		if(play_current_time == 0)
		{
			if(Dao.getInstance(VideoPlayerActivity.this)
					.queryPlayHistory(playhistory) == null)
			{
				Dao.getInstance(VideoPlayerActivity.this)
						.addPlayHistory(playhistory);
			}
			else
			{
				playhistory = Dao.getInstance(VideoPlayerActivity.this)
						.queryPlayHistory(playhistory);
				play_current_time = Long.parseLong(playhistory.getPlay_time());
			}
			
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mVideoView != null) {
			/*
			 * 获取当前播放时间和总时间,将播放时间和总时间放在服务器上
			 */
			long current_time = mVideoView.getCurrentPosition();
			long total_time = mVideoView.getDuration();
			SaveToServer(current_time, total_time);
			if(current_time >0)
			{
				// 保存播放记录在本地
				playhistory.setPlay_time(current_time+"");
				Dao.getInstance(VideoPlayerActivity.this).updatePlayHistory(playhistory);
			}	 
			mVideoView.pause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mVideoView != null) {
			/*
			 * 取得播放时间,设置播放时间,进行播放
			 */
			mVideoView.resume();
			if (play_current_time > 0)//当用户点击home键以后要回来就得调用这个
				mVideoView.seekTo(play_current_time);
		}

	}

	@Override
	protected void onDestroy() {
		if (aq != null)
			aq.dismiss();
		super.onDestroy();
		// github.com/joyplus/ijoyplus-android.git
		if (mVideoView != null) {
			mVideoView.stopPlayback();
		}
		if (checkBind)
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
			if (mLayout > VideoView.VIDEO_LAYOUT_ZOOM)
				mLayout = VideoView.VIDEO_LAYOUT_ORIGIN;
			if (mVideoView != null)
				mVideoView.setVideoLayout(mLayout, 0);
			return true;
		}

		/** 滑动 */
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
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
		lp.width = findViewById(R.id.operation_full).getLayoutParams().width
				* index / mMaxVolume;
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

	@Override
	public void onCompletion(MediaPlayer player) {
		finish();
	}

	public void GetServiceData() {
		String url = Constant.BASE_URL + "program/view?prod_id=" + prod_id;

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class)
				.weakHandler(this, "GetServiceDataResult");

		cb.header("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
		cb.header("app_key", Constant.APPKEY);
		cb.header("user_id", app.UserID);

		aq.ajax(cb);

	}

	// 初始化list数据函数
	public void GetServiceDataResult(String url, JSONObject json,
			AjaxStatus status) {
		if (status.getCode() == AjaxStatus.NETWORK_ERROR) {
			app.MyToast(aq.getContext(),
					getResources().getString(R.string.networknotwork));
			return;
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			m_ReturnProgramView = mapper.readValue(json.toString(),
					ReturnProgramView.class);
			if (mMediaController != null)
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

	public long getHistoryPlayTime() {
		return 0;
	}

	public void SaveToServer(long playback_time, long duration) {
		String url = Constant.BASE_URL + "program/play";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("app_key", Constant.APPKEY);// required string
												// 申请应用时分配的AppKey。
		params.put("prod_id", playProdId);// required string
											// 视频id
		params.put("prod_name", playProdName);// required
												// string 视频名字
		params.put("prod_subname", playProdSubName);// required
													// string
													// 视频的集数
		params.put("prod_type", playProdType);// required int 视频类别
												// 1：电影，2：电视剧，3：综艺，4：视频
		params.put("playback_time", playback_time);// _time required int
													// 上次播放时间，单位：秒
		params.put("duration", duration);// required int 视频时长， 单位：秒
		params.put("play_type", "1");// required string
		// 播放的类别 1: 视频地址播放
		// 2:webview播放
		params.put("video_url", playVideoUrl);// required
		// string
		// 视频url
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.header("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
		cb.header("app_key", Constant.APPKEY);
		cb.header("user_id", app.UserID);
		cb.params(params).url(url).type(JSONObject.class)
				.weakHandler(this, "CallProgramPlayResult");
		aq.ajax(cb);

		/*
		 * 怎么把数据保存在本地
		 */
	}

	public void CallProgramPlayResult(String url, JSONObject json,
			AjaxStatus status) {
		/*
		 * 保存历史播放记录的回调函数 prod_id index 播放时间
		 */
	}
}
