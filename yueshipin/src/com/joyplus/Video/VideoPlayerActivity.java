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
import com.joyplus.Adapters.CurrentPlayData;
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
import android.net.TrafficStats;
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
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class VideoPlayerActivity extends Activity implements
		OnCompletionListener {
	private String TAG = "VideoPlayerActivity";
	private AQuery aq;
	private App app;
	private ReturnProgramView m_ReturnProgramView = null;
	private String mPath;
	private String mTitle;
	private boolean checkBind = false;
	private boolean isShowingDLNA = false;
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
	private Handler mHandler = new Handler();
	private long mStartRX = 0;
	private long mStartTX = 0;
	private CurrentPlayData mCurrentPlayData;
//	private int CurrentCategory = 0;
//	private int CurrentIndex = 0;
//	private int CurrentSource = 0;
//	private int CurrentQuality = 0;


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
		
		setContentView(R.layout.videoview);
		app = (App) getApplication();
		aq = new AQuery(this);
		
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
			aq.id(R.id.textViewRate).text("Your device does not support traffic stat monitoring.");
		} else {
			mHandler.postDelayed(mRunnable, 1000);
		}

		// mPath =
		// "http://122.228.96.172/20/40/95/letv-uts/1340994301-AVC-249889-AAC-31999-5431055-192873082-4b45f1fd5362d980a1dae9c44b2b1c6b-1340994301.mp4?crypt=3eb0ad42aa7f2e559&b=2000&gn=860&nc=1&bf=22&p2p=1&video_type=mp4&check=0&tm=1354698000&key=78cc2270a7e5dfe3187c1608c99e65c0&lgn=letv&proxy=1945014819&cipi=1034815956&tag=mobile&np=1&vtype=mp4&ptype=s1&level=350&t=1354601822&cid=&vid=&sign=mb&dname=mobile";
		// mPath =
		// "http://122.228.96.172/20/40/95/letv-uts/1340994301-AVC-249889-AAC-31999-5431055-192873082-4b45f1fd5362d980a1dae9c44b2b1c6b-1340994301.mp4?crypt=3eb0ad42aa7f2e559&b=2000&gn=860&nc=1&bf=22&p2p=1&video_type=mp4&check=0&tm=1354698000&key=78cc2270a7e5dfe3187c1608c99e65c0&lgn=letv&proxy=1945014819&cipi=1034815956&tag=mobile&np=1&vtype=mp4&ptype=s1&level=350&t=1354601822&cid=&vid=&sign=mb&dname=mobile";
		// mPath =
		// "http://117.27.153.51:80/83B516E8091FC8ADAF9C1BB64758CC84ABE0A231/playlist.m3u8";
		// mPath = "http://api.joyplus.tv/joyplus-service/video/t.mp4";
		// mTitle = "x3";

		mMediaController = new MediaController(this);

		if (mTitle != null && mTitle.length() > 0) {
			aq.id(R.id.textView1).text("正在载入 ...");
			if (playProdSubName != null && playProdSubName.length() > 0) {
				aq.id(R.id.mediacontroller_file_name).text(mTitle + playProdSubName);
				mVideoView.setTitle(mTitle + playProdSubName);
				mMediaController.setFileName(playProdSubName);
				mMediaController.setSubName(playProdSubName);
			} else {
				aq.id(R.id.mediacontroller_file_name).text(mTitle);
				mVideoView.setTitle(mTitle);
				mMediaController.setFileName(mTitle);
			}

		}
		mVideoView.setApp(app);
		mMediaController.setApp(app);
	
//		mVideoView.setVideoPath(mPath);
		//
		mVideoView.setOnCompletionListener(this);


		// mVideoView.setBackgroundColor(color.black);

		// 设置显示名称
		if (play_current_time > 0){
			aq.id(R.id.textView2).text("上次播放到 "+stringForTime(play_current_time));
			mVideoView.seekTo(play_current_time);
		}
		else {
			aq.id(R.id.textView2).invisible();
		}

		mVideoView.setMediaController(mMediaController);

		mVideoView.requestFocus();

		mGestureDetector = new GestureDetector(this, new MyGestureListener());

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		Intent i = new Intent();
		i.setClass(this, DlnaSelectDevice.class);
		bindService(i, mServiceConnection, BIND_AUTO_CREATE);
		checkBind = true;
		
		if(!URLUtil.isNetworkUrl(mPath)){
			aq.id(R.id.textViewRate).gone();
			
			mVideoView.setVideoPath(mPath);
			
		}else {
			mCurrentPlayData = app.getCurrentPlayData();
			if (playProdId != null)
				GetServiceData();
		}

	}

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
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		
//		mPath = bundle.getString("path");
		if( app.getURLPath() != null && app.getURLPath().length() >0)
			mPath = app.getURLPath();
		else 
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
//		if( playProdType == 1)
			aq.id(R.id.imageButton6).gone();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mVideoView != null) {
			/*
			 * 获取当前播放时间和总时间,将播放时间和总时间放在服务器上
			 */
			current_time = mVideoView.getCurrentPosition();
			long total_time = mVideoView.getDuration();
			SaveToServer(current_time, total_time);
			if(current_time >0)
			{
				// 保存播放记录在本地
				playhistory.setPlay_time(current_time+"");
				Dao.getInstance(VideoPlayerActivity.this).updatePlayHistory(playhistory);
				play_current_time = current_time;
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
			// mLayout++;
			// if(mLayout >VideoView.VIDEO_LAYOUT_ZOOM)
			// mLayout = VideoView.VIDEO_LAYOUT_ORIGIN;
			// if (mVideoView != null)
			// mVideoView.setVideoLayout(mLayout, 0);
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
		if (status.getCode() == AjaxStatus.NETWORK_ERROR) {
			app.MyToast(aq.getContext(),
					getResources().getString(R.string.networknotwork));
			return;
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			m_ReturnProgramView = mapper.readValue(json.toString(),
					ReturnProgramView.class);
			mPath = GetRedirectURL();
			if(mPath != null && mPath.length() >0)
				mVideoView.setVideoPath(app.getURLPath());
			
			if (mMediaController != null){
			
				app.setCurrentPlayData(mCurrentPlayData);
				app.set_ReturnProgramView(m_ReturnProgramView);
//				mMediaController.ShowCurrentPlayData(mCurrentPlayData);
//				mMediaController.setProd_Data(m_ReturnProgramView);
			}
	
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
   private String GetRedirectURL(){
	   String PROD_SOURCE = null;
	  
	   mCurrentPlayData.CurrentCategory = playProdType -1;
	   switch (playProdType) {
	case 1:
	{
		   if (m_ReturnProgramView.movie.episodes[0].down_urls != null) {
				for (int i = 0; i < m_ReturnProgramView.movie.episodes[0].down_urls.length; i++) {
					for (int k = 0; k < m_ReturnProgramView.movie.episodes[0].down_urls[i].urls.length; k++) {
						ReturnProgramView.DOWN_URLS.URLS urls = m_ReturnProgramView.movie.episodes[0].down_urls[i].urls[k];
						if (urls != null) {
							/*
							 * #define GAO_QING @"mp4" #define BIAO_QING @"flv"
							 * #define CHAO_QING @"hd2" #define LIU_CHANG @"3gp"
							 */
							if (urls.url != null
									&& app.CheckUrlIsValidFromServer(urls.url,"1")) {
								for(int qindex = 0;qindex<Constant.quality_index.length;qindex++){
									if (PROD_SOURCE == null
											&& urls.type.trim().equalsIgnoreCase(
													Constant.quality_index[qindex])){
										mCurrentPlayData.CurrentSource = i;
										mCurrentPlayData.CurrentQuality = qindex;
										PROD_SOURCE = urls.url.trim();
										break;
									}
								}
							}
							if (PROD_SOURCE != null)
								break;
						}
						if (PROD_SOURCE != null )
							break;
					}
				}
			}
		
	}
		break;
	case 2:
	{
		   if (m_ReturnProgramView.tv.episodes[mCurrentPlayData.CurrentIndex].down_urls != null) {
				for (int i = 0; i < m_ReturnProgramView.tv.episodes[mCurrentPlayData.CurrentIndex].down_urls.length; i++) {
					for (int k = 0; k < m_ReturnProgramView.tv.episodes[mCurrentPlayData.CurrentIndex].down_urls[i].urls.length; k++) {
						ReturnProgramView.DOWN_URLS.URLS urls = m_ReturnProgramView.tv.episodes[mCurrentPlayData.CurrentIndex].down_urls[i].urls[k];
						if (urls != null) {
							/*
							 * #define GAO_QING @"mp4" #define BIAO_QING @"flv"
							 * #define CHAO_QING @"hd2" #define LIU_CHANG @"3gp"
							 */
							if (urls.url != null
									&& app.CheckUrlIsValidFromServer(urls.url,"1")) {
								for(int qindex = 0;qindex<Constant.quality_index.length;qindex++){
									if (PROD_SOURCE == null
											&& urls.type.trim().equalsIgnoreCase(
													Constant.quality_index[qindex])){
										mCurrentPlayData.CurrentSource = i;
										mCurrentPlayData.CurrentQuality = qindex;
										PROD_SOURCE = urls.url.trim();
										 
										break;
									}
								}
							}
							if (PROD_SOURCE != null)
								break;
						}
						if (PROD_SOURCE != null )
							break;
					}
				}
			}
		
	}
		break;
	case 3:
	{
		   if (m_ReturnProgramView.show.episodes[0].down_urls != null) {
				for (int i = 0; i < m_ReturnProgramView.show.episodes[0].down_urls.length; i++) {
					for (int k = 0; k < m_ReturnProgramView.show.episodes[0].down_urls[i].urls.length; k++) {
						ReturnProgramView.DOWN_URLS.URLS urls = m_ReturnProgramView.show.episodes[0].down_urls[i].urls[k];
						if (urls != null) {
							/*
							 * #define GAO_QING @"mp4" #define BIAO_QING @"flv"
							 * #define CHAO_QING @"hd2" #define LIU_CHANG @"3gp"
							 */
							if (urls.url != null
									&& app.CheckUrlIsValidFromServer(urls.url,"1")) {
//								if (PROD_SOURCE == null
//										&& !app.IfIncludeM3U(urls.url))
//									PROD_SOURCE = urls.url.trim();
								for(int qindex = 0;qindex<Constant.quality_index.length;qindex++){
									if (PROD_SOURCE == null
											&& urls.type.trim().equalsIgnoreCase(
													Constant.quality_index[qindex])){
										mCurrentPlayData.CurrentSource = i;
										mCurrentPlayData.CurrentQuality = qindex;
										PROD_SOURCE = urls.url.trim();
										break;
									}
								}
								
							}
							if (PROD_SOURCE != null)
								break;
						}
						if (PROD_SOURCE != null )
							break;
					}
				}
			}
		
	}
		break;
	}
	
	   return PROD_SOURCE;
   }
	private final Runnable mRunnable = new Runnable() {
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

			RX.setText(Long.toString(m_bitrate/8000)+"kb/s");

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
