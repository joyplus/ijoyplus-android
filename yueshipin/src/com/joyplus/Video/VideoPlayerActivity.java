/*
 * Copyright (C) 2011 VOV IO (http://vov.io/)
 */

package com.joyplus.Video;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.TrafficStats;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyplus.App;
import com.joyplus.BuildConfig;
import com.joyplus.Constant;
import com.joyplus.R;
import com.joyplus.Adapters.CurrentPlayData;
import com.joyplus.Dlna.DlnaSelectDevice;
import com.joyplus.Service.Return.ReturnProgramView;
import com.joyplus.Service.Return.ReturnProgramView.DOWN_URLS;
import com.joyplus.cache.VideoCacheInfo;
import com.joyplus.cache.VideoCacheManager;
import com.joyplus.faye.FayeService;
import com.joyplus.playrecord.PlayRecordInfo;
import com.joyplus.playrecord.PlayRecordManager;
import com.umeng.analytics.MobclickAgent;
import com.yixia.zi.utils.Log;

public class VideoPlayerActivity extends Activity implements
		OnCompletionListener{
	private AQuery aq;
	private App app;
	private ReturnProgramView m_ReturnProgramView = null;
	private String mPath;
	private String mTitle;
	private boolean checkBind = false;
	private VideoView mVideoView;
	private View mVolumeBrightnessLayout;
	private ImageView mOperationBg;
	private ImageView mOperationPercent;
	private AudioManager mAudioManager;
	private View mRelativeLayoutBG;
	private ImageView mImage_preload_bg;
	long current_time = 0;
	long total_time = 0;
	long play_current_time = 0;
	public static int RETURN_CURRENT_TIME = 150;

	private Handler mvediohandler;
	public static final int VideoPlay = 0;
	private boolean IsPlaying = false;
	public Handler fHanlder;
	/** 最大声音 */
	private int mMaxVolume;
	/** 当前声音 */
	private int mVolume = -1;
	/** 当前亮度 */
	// private float mBrightness = -1f;
	/** 当前缩放模式 */
	private int mLayout = VideoView.VIDEO_LAYOUT_STRETCH;// VIDEO_LAYOUT_ZOOM;
	private GestureDetector mGestureDetector;
	private MediaController mMediaController;

	private DlnaSelectDevice mMyService;
	private Handler mHandler = new Handler();
	private long mStartRX = 0;
	private long mStartTX = 0;
	private CurrentPlayData mCurrentPlayData;

	private static String MOVIE_PLAY = "电影播放";
	private static String TV_PLAY = "电视剧播放";
	private static String SHOW_PLAY = "综艺播放";
	Context mContext;
	/*
	 * playHistoryData
	 */
	VideoCacheInfo cacheInfo;
	VideoCacheInfo cacheInfoTemp;
	VideoCacheManager cacheManager;
	PlayRecordInfo playrecordinfo;
	PlayRecordManager playrecordmanager;
	private String tvsubname = null;

	private String playProdId = null;// 视频id
	private String playProdName = null;// 视频名字
	private String playProdSubName = null;// 视频的集数
	// private String playPlayType = null;// 播放的类别 1: 视频地址播放 2:webview播放
	private String playVideoUrl = null;// 视频url
	private int playProdType = 0;// 视频类别 1：电影，2：电视剧，3：综艺，4：视频
	private static final int FINISH_ACTTIVITY = 10;
	public static boolean IsFinish = false;
	public static boolean IsYunduanPlay = false;

	String user_id = null;
	String macAddress = null;
	String tv_channel = null;
	String prod_url = null;
	YunduanReceiver bindingReceiver;
	
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			if (android.os.Build.VERSION.SDK_INT >= 14) {
				mMyService = ((DlnaSelectDevice.MyBinder) service).getService();
				mVideoView.setServiceConnection(mMyService);
			}
		}

		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
		}
	};

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// android.util.Log.i("player_yy", "onCreate");
		if (!LibsChecker.checkVitamioLibs(this, R.string.init_decoders))
			return;

		setContentView(R.layout.videoview);
		// 保持常亮
		findViewById(R.id.layout).setKeepScreenOn(true);
		mContext = this;
		app = (App) getApplication();
		aq = new AQuery(this);
		Constant.select_index = -1;// 保证每次进来当前没有任何集数记录
		cacheManager = new VideoCacheManager(VideoPlayerActivity.this);
		cacheInfo = new VideoCacheInfo();
		playrecordmanager = new PlayRecordManager(VideoPlayerActivity.this);
		playrecordinfo = new PlayRecordInfo();

		user_id = app.UserID;
		macAddress = app.GetServiceData("Binding_TV_Channal");
		tv_channel = "/screencast/" + macAddress;
		if (macAddress != null) {
			FayeService.FayeByService(mContext, tv_channel);
			registerBinding();
		}
		InitPlayData();
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

		app = (App) getApplication();
		aq = new AQuery(this);

		mStartRX = TrafficStats.getTotalRxBytes();
		mStartTX = TrafficStats.getTotalTxBytes();
		if (mStartRX == TrafficStats.UNSUPPORTED
				|| mStartTX == TrafficStats.UNSUPPORTED) {
			aq.id(R.id.textViewRate).text(
					"Your device does not support traffic stat monitoring.");
		} else {
			mHandler.postDelayed(mRunnable, 1000);// test,yy
		}

		mMediaController = new MediaController(this, user_id, tv_channel);

		if (mTitle != null && mTitle.length() > 0) {
			aq.id(R.id.textView1).text("正在载入 ...");
			if (playProdSubName != null && playProdSubName.length() > 0) {
				aq.id(R.id.mediacontroller_file_name).text(
						mTitle + playProdSubName);
				mVideoView.setTitle(mTitle + playProdSubName);
				mMediaController.setFileName(mTitle + playProdSubName);
				mMediaController.setSubName(playProdSubName);
			} else {
				aq.id(R.id.mediacontroller_file_name).text(mTitle);
				mVideoView.setTitle(mTitle);
				mMediaController.setFileName(mTitle);
			}

		}
		mVideoView.setApp(app);
		mMediaController.setApp(app);
		mVideoView.setOnCompletionListener(this);

		// 设置显示名称
		if (play_current_time > 0) {
			aq.id(R.id.textView2).text(
					"上次播放到 " + stringForTime(play_current_time));
			mVideoView.JumpTo(play_current_time);
		} else {
			aq.id(R.id.textView2).invisible();
		}

		mVideoView.setMediaController(mMediaController);

		mVideoView.requestFocus();

		mGestureDetector = new GestureDetector(this, new MyGestureListener());

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		if (android.os.Build.VERSION.SDK_INT >= 14) {
			Intent i = new Intent();
			i.setClass(this, DlnaSelectDevice.class);
			bindService(i, mServiceConnection, BIND_AUTO_CREATE);
		}
		checkBind = true;
		
		if (!URLUtil.isNetworkUrl(mPath)) {
			aq.id(R.id.textViewRate).gone();

			mVideoView.setVideoPath(mPath);

		} else {
			mCurrentPlayData = app.getCurrentPlayData();
			if (playProdId != null)
				GetServiceData();
		}
		
		mvediohandler = new Handler() {
			public void handleMessage(Message msg) {
				// android.util.Log.i("player_yy",msg.what+"");
				switch (msg.what) {
				case VideoPlay:
					if (msg.obj.toString().contains("{now_date}")) {
						long time = System.currentTimeMillis()/1000;
						String msgUrl = msg.obj.toString().replace("{now_date}",
								time+"");
						videoplay(msgUrl);
					}else
					{
						videoplay(msg.obj.toString());
					}
					break;
				default:
					android.util.Log.i("player_yy", "error");
				}
			}
		};
		
	}

	@SuppressLint("DefaultLocale")
	private String stringForTime(long time) {

		long totalSeconds = time / 1000;
		long seconds = totalSeconds % 60;
		long minutes = (totalSeconds / 60) % 60;
		long hours = totalSeconds / 3600;
		if (hours > 0) {
			return String.format("%d:%02d:%02d", hours, minutes, seconds)
					.toString();
		} else {
			return String.format("%02d:%02d", minutes, seconds).toString();
		}
	}

	public void InitPlayData() {
		// android.util.Log.i("player_yy", "InitPlayData");
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();

		mPath = bundle.getString("path");

		mTitle = bundle.getString("title");
		playProdName = mTitle;
		playVideoUrl = mPath;
		playProdId = bundle.getString("prod_id");
		playProdSubName = bundle.getString("prod_subname");
		if (bundle.getString("prod_type") != null) {
			try {
				playProdType = Integer.parseInt(bundle.getString("prod_type"));
			} finally {
			}
		}
		if (playProdType == 2 || playProdType == 131) {
			tvsubname = playProdSubName;
			playProdSubName = "第" + playProdSubName + "集";
			playrecordmanager.deletePlayRecord(playProdId);
		} else if (playProdType == 3)// 综艺
		{
			tvsubname = playProdSubName;
			playrecordmanager.deletePlayRecord(playProdId);
		}

		play_current_time = bundle.getLong("current_time");

		if (playProdType == 1) {
			// 播放记录
			cacheInfo = cacheManager.getVideoCache(playProdId);
			if (cacheInfo != null && cacheInfo.getLast_playtime() != null
					&& cacheInfo.getLast_playtime().length() > 0) {
				play_current_time = Long
						.parseLong(cacheInfo.getLast_playtime());
			}
		}
		aq.id(R.id.imageButton6).gone();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onEventEnd(mContext, MOVIE_PLAY);
		MobclickAgent.onEventEnd(mContext, TV_PLAY);
		MobclickAgent.onEventEnd(mContext, SHOW_PLAY);
		MobclickAgent.onPause(this);
		mHandler.removeCallbacks(mRunnable);// yy
		if (mVideoView != null) {
			/*
			 * 获取当前播放时间和总时间,将播放时间和总时间放在服务器上
			 */
			current_time = mVideoView.getCurrentPosition();
			total_time = mVideoView.getDuration();
			if (URLUtil.isNetworkUrl(mPath))
				SaveToServer(current_time / 1000, total_time / 1000);

			if (current_time > 0) {

					if (playProdType == 2 || playProdType == 131) {
						playrecordinfo.setProd_id(playProdId);
						if (Constant.select_index > -1) {
							tvsubname = Integer
									.toString(Constant.select_index + 1);// 更新本地数据库
							SharedPreferences myPreference = this
									.getSharedPreferences("myTvSetting",
											Context.MODE_PRIVATE);
							myPreference
									.edit()
									.putString(
											playProdId,
											Integer.toString(Constant.select_index))
									.commit();
						}
						playrecordinfo.setProd_subname(tvsubname);
						playrecordinfo.setLast_playtime(current_time + "");
						playrecordmanager.savePlayRecord(playrecordinfo);
					} else if (playProdType == 3) {
						playrecordinfo.setProd_id(playProdId);
						playrecordinfo.setProd_subname(tvsubname);
						playrecordinfo.setLast_playtime(current_time + "");
						playrecordmanager.savePlayRecord(playrecordinfo);
					} else {
						// 保存播放记录在本地
						cacheInfo = cacheManager.getVideoCache(playProdId);
						if (cacheInfo != null) {
							cacheInfo.setLast_playtime(current_time + "");
							cacheManager.saveVideoCache(cacheInfo);
						}
					}
					play_current_time = current_time;
					
				mVideoView.pause();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		if (mVideoView != null) {
			/*
			 * 取得播放时间,设置播放时间,进行播放
			 */
			mVideoView.resume();
			if (play_current_time > 0)// 当用户点击home键以后要回来就得调用这个
				mVideoView.seekTo(play_current_time);
		}
	}

	@Override
	protected void onDestroy() {
		IsYunduanPlay = false;
		m_ReturnProgramView = null;
		unregisterBinding();
		if (aq != null)
			aq.dismiss();
		if (mVideoView != null) {
			mVideoView.stopPlayback();
		}

		if (checkBind) {
			if (android.os.Build.VERSION.SDK_INT >= 14) {
				unbindService(mServiceConnection);
			}
		}
		super.onDestroy();
		if (mHandler != null) {
			mHandler.removeCallbacks(mRunnable);
		}
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
		MobclickAgent.onEventEnd(mContext, MOVIE_PLAY);
		MobclickAgent.onEventEnd(mContext, TV_PLAY);
		MobclickAgent.onEventEnd(mContext, SHOW_PLAY);
		Intent mIntent = new Intent();
		VideoPlayerActivity.this.setResult(FINISH_ACTTIVITY, mIntent);
		sendQuitMessage();
		finish();

	}

	@Override
	public void finish() {
		if (IsFinish) {
			Intent mIntent = new Intent();
			VideoPlayerActivity.this.setResult(FINISH_ACTTIVITY, mIntent);
			IsFinish = false;
		}
		super.finish();
	}

	public void OnClickSelect(View v) {

	}

	/** 手势结束 */
	private void endGesture() {
		mVolume = -1;
		// mBrightness = -1f;

		// 隐藏
		mDismissHandler.removeMessages(0);
		mDismissHandler.sendEmptyMessageDelayed(0, 500);
	}

	private class MyGestureListener extends SimpleOnGestureListener {

		/** 双击 */
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			return true;
		}

		/** 滑动 */
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			float mOldX = e1.getX();
			float mOldY = e1.getY();
			int y = (int) e2.getRawY();
			Display disp = getWindowManager().getDefaultDisplay();
			int windowWidth = disp.getWidth();
			int windowHeight = disp.getHeight();

			if (mOldX > windowWidth * 4.0 / 5)// 右边滑动
				onVolumeSlide((mOldY - y) / windowHeight);
			// else if (mOldX < windowWidth / 5.0)// 左边滑动
			// onBrightnessSlide((mOldY - y) / windowHeight);

			return super.onScroll(e1, e2, distanceX, distanceY);
		}
	}

	/** 定时隐藏 */
	@SuppressLint("HandlerLeak")
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

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (mVideoView != null)
			mVideoView.setVideoLayout(mLayout, 0);
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCompletion(MediaPlayer player) {
		MobclickAgent.onEventEnd(mContext, MOVIE_PLAY);
		MobclickAgent.onEventEnd(mContext, TV_PLAY);
		MobclickAgent.onEventEnd(mContext, SHOW_PLAY);
		finish();
	}
	
	
	
	public void GetServiceData() {
		// android.util.Log.i("player_yy", "GetServiceData");
		String url = Constant.BASE_URL + "program/view?prod_id=" + playProdId;

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class)
				.weakHandler(this, "GetServiceDataResult");

		cb.SetHeader(app.getHeaders());

		aq.ajax(cb);
	}

	// 初始化list数据函数
	public void GetServiceDataResult(String url, JSONObject json,
			AjaxStatus status) {
		// android.util.Log.i("player_yy", "GetServiceDataResult");
		if (status.getCode() == AjaxStatus.NETWORK_ERROR) {
			app.MyToast(aq.getContext(),
					getResources().getString(R.string.networknotwork));
			return;
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			m_ReturnProgramView = mapper.readValue(json.toString(),
					ReturnProgramView.class);
			if (m_ReturnProgramView == null)
				finish();
			
			app.listUrl.clear();
			
			GetRedirectURL();// 获取重定向的数据,source_yy
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

	private void videoplay(String mPath) {
		// android.util.Log.i("player_yy","videoplay");
		if (IsPlaying)
			return;
		if (mMediaController != null) {

			app.setCurrentPlayData(mCurrentPlayData);
			app.set_ReturnProgramView(m_ReturnProgramView);
			// mMediaController.ShowCurrentPlayData(mCurrentPlayData);
			// mMediaController.setProd_Data(m_ReturnProgramView);
		}
		if (mPath != null && mPath.length() > 0) {
			IsPlaying = true;
			prod_url = mPath;
			mVideoView.setVideoPath(mPath);
		}
	}

	private void GetRedirectURL() {
		String PROD_SOURCE = null;
		mCurrentPlayData.CurrentCategory = playProdType - 1;
		switch (playProdType) {
		case 1: {
			/*
			 * @author yyc
			 * 根据当前源进行选择地址把地址放到里面进行检测，这个很好做
			 */
			if(app.sourceUrl==null&&mPath!=null)
			{
				for(int i = 0;i<m_ReturnProgramView.movie.episodes[0].down_urls.length;i++)
				{
					for(int j = 0;j<m_ReturnProgramView.movie.episodes[0].down_urls[i].urls.length;j++)
					{
						if(m_ReturnProgramView.movie.episodes[0].down_urls[i].urls[j].url.equalsIgnoreCase(mPath))
						{
							app.sourceUrl = m_ReturnProgramView.movie.episodes[0].down_urls[i].source;
						}
						
					}
				}
			}
			
			if (m_ReturnProgramView.movie.episodes[0].down_urls != null) {
				// videoSourceSort(m_ReturnProgramView.movie.episodes[0].down_urls);
				for (int i = 0; i < m_ReturnProgramView.movie.episodes[0].down_urls.length; i++) {
					if(m_ReturnProgramView.movie.episodes[0].down_urls[i].source.equalsIgnoreCase(app.sourceUrl))
					{
						for (int k = 0; k < m_ReturnProgramView.movie.episodes[0].down_urls[i].urls.length; k++) {

							for (int qi = 0; qi < Constant.player_quality_index.length; qi++) {
								if (PROD_SOURCE == null)
									for (int ki = 0; ki < m_ReturnProgramView.movie.episodes[0].down_urls[i].urls.length; ki++) {
										if (m_ReturnProgramView.movie.episodes[0].down_urls[i].urls[ki].type
												.equalsIgnoreCase(Constant.player_quality_index[qi])) {
											ReturnProgramView.DOWN_URLS.URLS urls = m_ReturnProgramView.movie.episodes[0].down_urls[i].urls[ki];
											/*
											 * #define GAO_QING @"mp4" #define
											 * BIAO_QING @"flv" #define CHAO_QING
											 * 
											 * @"hd2" #define LIU_CHANG @"3gp"
											 */
											//app.CheckUrlIsValidFromServer(urls.url, "1")
											if (urls != null && urls.url != null
													&& !IsPlaying) {
												PROD_SOURCE = urls.url.trim();
												HttpThreadPoolUtils
														.execute(new HttpTread(
																urls.url, "1", i,
																ki, qi, PROD_SOURCE));
//												Message message = mvediohandler.obtainMessage(VideoPlay,
//														PROD_SOURCE);
//												mvediohandler.sendMessage(message);
												MobclickAgent.onEventBegin(
														mContext, MOVIE_PLAY);
											}
										}
									}
							}

						}
					}
				}
			}

		}
			break;
		case 2: {
			if(app.sourceUrl==null&&mPath!=null)
			{
				for(int i = 0;i<m_ReturnProgramView.tv.episodes[mCurrentPlayData.CurrentIndex].down_urls.length;i++)
				{
					for(int j = 0;j<m_ReturnProgramView.tv.episodes[mCurrentPlayData.CurrentIndex].down_urls[i].urls.length;j++)
					{
						if(m_ReturnProgramView.tv.episodes[mCurrentPlayData.CurrentIndex].down_urls[i].urls[j].url.equalsIgnoreCase(mPath))
						{
							app.sourceUrl = m_ReturnProgramView.tv.episodes[mCurrentPlayData.CurrentIndex].down_urls[i].source;
						}
						
					}
				}
			}
			
			if (m_ReturnProgramView.tv.episodes[mCurrentPlayData.CurrentIndex].down_urls != null) {
//				videoSourceSort(m_ReturnProgramView.tv.episodes[mCurrentPlayData.CurrentIndex].down_urls);
				
				for (int i = 0; i < m_ReturnProgramView.tv.episodes[mCurrentPlayData.CurrentIndex].down_urls.length; i++) {
					if(m_ReturnProgramView.tv.episodes[mCurrentPlayData.CurrentIndex].down_urls[i].source.equalsIgnoreCase(app.sourceUrl))
					{
						for (int qi = 0; qi < Constant.player_quality_index.length; qi++) {
							if (PROD_SOURCE == null)
								for (int ki = 0; ki < m_ReturnProgramView.tv.episodes[mCurrentPlayData.CurrentIndex].down_urls[i].urls.length; ki++) {// 原来字典里的值为0yy
									if (m_ReturnProgramView.tv.episodes[mCurrentPlayData.CurrentIndex].down_urls[i].urls[ki].type
											.equalsIgnoreCase(Constant.player_quality_index[qi])) {
										ReturnProgramView.DOWN_URLS.URLS urls = m_ReturnProgramView.tv.episodes[mCurrentPlayData.CurrentIndex].down_urls[i].urls[ki];
										//&& app.CheckUrlIsValidFromServer(urls.url, "1")
										if (urls != null && urls.url != null
												&& !IsPlaying) {
											mCurrentPlayData.CurrentSource = i;
											mCurrentPlayData.CurrentQuality = ki;
											PROD_SOURCE = urls.url.trim();
//											Message message = mvediohandler.obtainMessage(VideoPlay,
//													PROD_SOURCE);
//											mvediohandler.sendMessage(message);
											HttpThreadPoolUtils
													.execute(new HttpTread(
															urls.url, "1", i, ki,
															qi, PROD_SOURCE));
											MobclickAgent.onEventBegin(mContext,
													TV_PLAY);
										}
									}
								}
							// if (PROD_SOURCE != null)
							// break;
						}
					}
				}
			}

		}
			break;
		case 3: {
			if(app.sourceUrl==null&&mPath!=null)
			{
				for(int i = 0;i<m_ReturnProgramView.show.episodes[mCurrentPlayData.CurrentIndex].down_urls.length;i++)
				{
					for(int j = 0;j<m_ReturnProgramView.show.episodes[mCurrentPlayData.CurrentIndex].down_urls[i].urls.length;j++)
					{
						if(m_ReturnProgramView.show.episodes[mCurrentPlayData.CurrentIndex].down_urls[i].urls[j].url.equalsIgnoreCase(mPath))
						{
							app.sourceUrl = m_ReturnProgramView.show.episodes[mCurrentPlayData.CurrentIndex].down_urls[i].source;
						}
						
					}
				}
			}
			
			if (m_ReturnProgramView.show.episodes[mCurrentPlayData.CurrentIndex].down_urls != null) {
//				videoSourceSort(m_ReturnProgramView.show.episodes[mCurrentPlayData.CurrentIndex].down_urls);
				for (int i = 0; i < m_ReturnProgramView.show.episodes[mCurrentPlayData.CurrentIndex].down_urls.length; i++) {
					if(m_ReturnProgramView.show.episodes[mCurrentPlayData.CurrentIndex].down_urls[i].source.equalsIgnoreCase(app.sourceUrl))
					{
						for (int qi = 0; qi < Constant.player_quality_index.length; qi++) {
							if (PROD_SOURCE == null)
								for (int ki = 0; ki < m_ReturnProgramView.show.episodes[mCurrentPlayData.CurrentIndex].down_urls[i].urls.length; ki++) {
									if (m_ReturnProgramView.show.episodes[mCurrentPlayData.CurrentIndex].down_urls[i].urls[ki].type
											.equalsIgnoreCase(Constant.player_quality_index[qi])) {
										ReturnProgramView.DOWN_URLS.URLS urls = m_ReturnProgramView.show.episodes[mCurrentPlayData.CurrentIndex].down_urls[i].urls[ki];
										//&& app.CheckUrlIsValidFromServer(urls.url, "1")
										if (urls != null && urls.url != null
												&& !IsPlaying) {
											mCurrentPlayData.CurrentSource = i;
											mCurrentPlayData.CurrentQuality = ki;
											PROD_SOURCE = urls.url.trim();
//											Message message = mvediohandler.obtainMessage(VideoPlay,
//													PROD_SOURCE);
//											mvediohandler.sendMessage(message);
											HttpThreadPoolUtils
													.execute(new HttpTread(
															urls.url, "1", i, ki,
															qi, PROD_SOURCE));
											MobclickAgent.onEventBegin(mContext,
													SHOW_PLAY);
										}
									}
								}
						}
					}
				}
			}

		}
			break;
		}

	}

	// 给片源赋权值
	@SuppressWarnings("unchecked")
	public void videoSourceSort(DOWN_URLS[] down_urls) {

		if (down_urls != null) {
			for (int j = 0; j < down_urls.length; j++) {
				if (down_urls[j].source.equalsIgnoreCase("letv")) {
					down_urls[j].index = 0;
				} else if (down_urls[j].source.equalsIgnoreCase("fengxing")) {
					down_urls[j].index = 1;
				} else if (down_urls[j].source.equalsIgnoreCase("qiyi")) {
					down_urls[j].index = 2;
				} else if (down_urls[j].source.equalsIgnoreCase("youku")) {
					down_urls[j].index = 3;
				} else if (down_urls[j].source.equalsIgnoreCase("sinahd")) {
					down_urls[j].index = 4;
				} else if (down_urls[j].source.equalsIgnoreCase("sohu")) {
					down_urls[j].index = 5;
				} else if (down_urls[j].source.equalsIgnoreCase("56")) {
					down_urls[j].index = 6;
				} else if (down_urls[j].source.equalsIgnoreCase("qq")) {
					down_urls[j].index = 7;
				} else if (down_urls[j].source.equalsIgnoreCase("pptv")) {
					down_urls[j].index = 8;
				} else if (down_urls[j].source.equalsIgnoreCase("m1905")) {
					down_urls[j].index = 9;
				}
			}
			if (down_urls.length > 1) {
				Arrays.sort(down_urls, new EComparatorIndex());
			}
		}
	}

	// 将片源排序
	@SuppressWarnings("rawtypes")
	class EComparatorIndex implements Comparator {

		@Override
		public int compare(Object first, Object second) {
			// TODO Auto-generated method stub
			int first_name = ((DOWN_URLS) first).index;
			int second_name = ((DOWN_URLS) second).index;
			if (first_name - second_name < 0) {
				return -1;
			} else {
				return 1;
			}
		}
	}

	private final Runnable mRunnable = new Runnable() {
		@SuppressWarnings("unused")
		long beginTimeMillis, timeTakenMillis, timeLeftMillis, rxByteslast,
				m_bitrate;

		public void run() {
			TextView RX = (TextView) findViewById(R.id.textViewRate);
			// long txBytes = TrafficStats.getTotalTxBytes()- mStartTX;
			// TX.setText(Long.toString(txBytes));
			long rxBytes = TrafficStats.getTotalRxBytes() - mStartRX;

			timeTakenMillis = System.currentTimeMillis() - beginTimeMillis;
			beginTimeMillis = System.currentTimeMillis();
			// check how long there is until we reach the desired refresh rate
			m_bitrate = (rxBytes - rxByteslast) * 8 * 1000 / timeTakenMillis;
			rxByteslast = rxBytes;

			RX.setText(Long.toString(m_bitrate / 8000) + "kb/s");

			// Fun_downloadrate();
			mHandler.postDelayed(mRunnable, 1000);
		}
	};

	public void SaveToServer(long playback_time, long duration) {
		String url = Constant.BASE_URL + "program/play";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("app_key", Constant.APPKEY);// required string
												// 申请应用时分配的AppKey。
		params.put("prod_id", playProdId);// required string
											// 视频id
		params.put("prod_name", playProdName);// required
		if (playProdType != 3 && playProdType != 1)// string 视频名字
		{
			if (Constant.select_index > -1) {
				params.put("prod_subname",
						Integer.toString(Constant.select_index + 1));// required
			} else {
				params.put("prod_subname",
						Integer.toString(mCurrentPlayData.CurrentIndex + 1));// required
			}
		} else {
			params.put("prod_subname", tvsubname);
		}

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
		cb.SetHeader(app.getHeaders());
		cb.params(params).url(url).type(JSONObject.class)
				.weakHandler(this, "CallProgramPlayResult");
		aq.ajax(cb);
	}

	/*
	 * 怎么把数据保存在本地
	 */
	// }

	public void CallProgramPlayResult(String url, JSONObject json,
			AjaxStatus status) {
		/*
		 * 保存历史播放记录的回调函数 prod_id index 播放时间
		 */
	}

	// 返回键
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getRepeatCount() == 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(getResources().getString(R.string.tishi));
				builder.setMessage(
						getResources().getString(R.string.shifoutuichubofang))
						.setPositiveButton(
								getResources().getString(R.string.queding),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										MobclickAgent.onEventEnd(mContext,
												MOVIE_PLAY);
										MobclickAgent.onEventEnd(mContext,
												TV_PLAY);
										MobclickAgent.onEventEnd(mContext,
												SHOW_PLAY);
										Intent mIntent = new Intent();
										VideoPlayerActivity.this.setResult(
												FINISH_ACTTIVITY, mIntent);
										sendQuitMessage();
										finish();
									}
								})
						.setNegativeButton(
								getResources().getString(R.string.quxiao),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								});
				builder.show();
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}

	private void sendQuitMessage() {
		if (!IsYunduanPlay)
			return;
		try {
			JSONObject json = new JSONObject();
			json.put("push_type", "409");
			json.put("tv_channel", tv_channel);
			json.put("user_id", user_id);
			json.put("prod_id", playProdId);
			json.put("prod_url", prod_url);
			FayeService.SendMessageService(mContext, json, user_id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * id 对应播放源 letv 0、fengxing 1、qiyi 2、youku 3、sinahd 4、 sohu 5、56 6、qq 7、pptv
	 * 8、m1905 9. 启动一个异步任务，把网络相关放在此任务中 重定向新的链接，直到拿到资源URL
	 * 
	 * 注意：因为网络或者服务器原因，重定向时间有可能比较长 因此需要较长时间等待
	 * 
	 * @param url
	 * @param id
	 * @return 字符串
	 */
	private static final String NOT_VALID_LINK = "NULL";
	private static final String FENGXING = "1";

	class HttpTread implements Runnable {
		private static final String TAG = "HttpTread";
		public int CurrentSource;
		public int CurrentQuality;
		public int ShowQuality;
		String[] params;

		public HttpTread(String url, String sourceId, int CurrentSource,
				int CurrentQuality, int ShowQuality, String pROD_SOURCE) {
			this.CurrentSource = CurrentSource;
			this.CurrentQuality = CurrentQuality;
			this.ShowQuality = ShowQuality;
			params = new String[] { url, "" + sourceId };
		}

		@Override
		public void run() {
			if (IsPlaying)
				return;
			List<String> list = new ArrayList<String>();
			String dstUrl = params[0];
			if (BuildConfig.DEBUG)
				Log.i(TAG, "newATask--->>params : " + params[0] + params[1]);
			try {
				simulateFirfoxRequest(Constant.USER_AGENT_IOS, params, list);// 使用递归，并把得到的链接放在集合中，取最后一次得到的链接即可
				if(list.size()>0)//当只有一个地址，并且该地址在模拟检测无效时不加这个判断会出异常
					dstUrl = list.get(list.size() - 1);
				if (BuildConfig.DEBUG)
					Log.i(TAG, "AsyncTask----->>URL : " + dstUrl);
				list.clear();
				// android.util.Log.i("player_yy", "HttpThreadPoolUtils.run1");
				if (!dstUrl.equalsIgnoreCase(NOT_VALID_LINK)) {
					mCurrentPlayData.CurrentSource = CurrentSource;
					mCurrentPlayData.CurrentQuality = CurrentQuality;
					mCurrentPlayData.ShowQuality = ShowQuality;
					
					app.listUrl.add(dstUrl);
					
					Message message = mvediohandler.obtainMessage(VideoPlay,
							dstUrl);
					mvediohandler.sendMessage(message);
				}
			} catch (Exception e) {
				if (BuildConfig.DEBUG)
					Log.i(TAG, "TimeOut!!!!!! : " + e);
				e.printStackTrace();
			}
		}

		/**
		 * 模拟火狐浏览器给服务器发送不同请求，有火狐本身请求，IOS请求，Android请求
		 * 
		 * @param userAgent
		 *            firfox ios android
		 * @param params包括srcUrl
		 *            原始地址【可能可以播放，可能需要跳转】和 sourceID 例："1"
		 * @param list
		 *            存储播放地址
		 */
		private void simulateFirfoxRequest(String userAgent, String[] params,
				List<String> list) {
			if (params == null || params.length < 2) {

				if (BuildConfig.DEBUG)
					Log.i(TAG, "Params Wrong");
				list.add(NOT_VALID_LINK);
				return;
			}

			String srcUrl = params[0];// 源地址
			String sourceId = params[1];// 资源来源id

			// 模拟火狐ios发用请求 使用userAgent
			AndroidHttpClient mAndroidHttpClient = AndroidHttpClient
					.newInstance(userAgent);

			HttpParams httpParams = mAndroidHttpClient.getParams();
			// 连接时间最长5秒，可以更改
			HttpConnectionParams.setConnectionTimeout(httpParams, 20000);

			try {
				URL url = new URL(srcUrl);
				URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(),null);//处理特殊字符
				HttpGet mHttpGet = new HttpGet(uri);
				HttpResponse response = mAndroidHttpClient.execute(mHttpGet);

				// 限定连接时间
				StatusLine statusLine = response.getStatusLine();
				int status = statusLine.getStatusCode();

				Header headertop = response.getFirstHeader("Content-Type");// 拿到重新定位后的header
				String type = headertop.getValue().toLowerCase();// 从header重新取出信息
				Header header_length = response
						.getFirstHeader("Content-Length");
				String lengthStr = header_length.getValue();
				int length = 0;
				try {
					length = Integer.parseInt(lengthStr);
				} finally {
				}

				if (BuildConfig.DEBUG)
					Log.i(TAG, "HTTP STATUS : " + status);

				// 如果资源来源为风行，那就对url进行重定向 如果不是就只是简单判断
				// 风行资源id 为 1
				// 如果拿到资源直接返回url 如果没有拿到资源，并且要进行跳转,那就使用递归跳转
				if (!type.startsWith("text/html") && status >= 200
						&& status <= 299 && length > 100) {
					// 正确的话直接返回，不进行下面的步骤
					mAndroidHttpClient.close();
					list.add(srcUrl);

				} else if (status > 299 && status < 400) {
					if (BuildConfig.DEBUG)
						Log.i(TAG, "NOT OK   start");

					if (BuildConfig.DEBUG)
						Log.i(TAG, "NOT OK start");
					if (status == HttpStatus.SC_MOVED_PERMANENTLY || // 网址被永久移除
							status == HttpStatus.SC_MOVED_TEMPORARILY || // 网址暂时性移除
							status == HttpStatus.SC_SEE_OTHER || // 重新定位资源
							status == HttpStatus.SC_TEMPORARY_REDIRECT) {// 暂时定向

						Header header = response.getFirstHeader("Location");// 拿到重新定位后的header
						String location = header.getValue();// 从header重新取出信息
						list.add(location);

						mAndroidHttpClient.close();// 关闭此次连接

						if (BuildConfig.DEBUG)
							Log.i(TAG, "Location: " + location);
						// 进行下一次递归
						simulateFirfoxRequest(userAgent, new String[] {
								location, FENGXING }, list);
					} else {

						// 如果地址真的不存在，那就往里面加NULL字符串
						mAndroidHttpClient.close();
						list.add(NOT_VALID_LINK);
					}
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				if (BuildConfig.DEBUG)
					Log.i(TAG, "NOT OK" + e);
				// 如果地址真的不存在，那就往里面加NULL字符串
				mAndroidHttpClient.close();
				list.add(params[0]);
				e.printStackTrace();
			}
		}

	}

	/* 注册监听 */
	private void registerBinding() {
		bindingReceiver = new YunduanReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.joyplus.yunduan");
		registerReceiver(bindingReceiver, filter);

	}

	/* 取消监听 */
	private void unregisterBinding() {
		if (bindingReceiver != null) {
			this.unregisterReceiver(bindingReceiver);
		}
	}

	/* Broadcast监听 */
	public class YunduanReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			String status = bundle.getString("yunduan");
			Log.i("YunduanReceiver", "result>>>>>" + status);
			if ("success".equals(status)) {
				IsYunduanPlay = true;
			}
		}
	}
}
