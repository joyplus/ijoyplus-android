/*
 * Copyright (C) 2011 VOV IO (http://vov.io/)
 */

package io.vov.vitamio.widget;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.joyplus.App;
import com.joyplus.BuildConfig;
import com.joyplus.Constant;
import com.joyplus.R;
import com.joyplus.StatisticsUtils;
import com.joyplus.Adapters.CurrentPlayData;
import com.joyplus.Service.Return.ReturnProgramView;
import com.joyplus.Service.Return.ReturnProgramView.DOWN_URLS;
import com.joyplus.Video.VideoPlayerActivity;
import com.joyplus.faye.FayeClient;
import com.joyplus.faye.FayeService;
import com.umeng.analytics.MobclickAgent;

import io.vov.utils.Log;
import io.vov.utils.StringUtils;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * A view containing controls for a MediaPlayer. Typically contains the buttons
 * like "Play/Pause" and a progress slider. It takes care of synchronizing the
 * controls with the state of the MediaPlayer.
 * <p>
 * The way to use this class is to a) instantiate it programatically or b)
 * create it in your xml layout.
 * 
 * a) The MediaController will create a default set of controls and put them in
 * a window floating above your application. Specifically, the controls will
 * float above the view specified with setAnchorView(). By default, the window
 * will disappear if left idle for three seconds and reappear when the user
 * touches the anchor view. To customize the MediaController's style, layout and
 * controls you should extend MediaController and override the {#link
 * {@link #makeControllerView()} method.
 * 
 * b) The MediaController is a FrameLayout, you can put it in your layout xml
 * and get it through {@link #findViewById(int)}.
 * 
 * NOTES: In each way, if you want customize the MediaController, the SeekBar's
 * id must be mediacontroller_progress, the Play/Pause's must be
 * mediacontroller_pause, current time's must be mediacontroller_time_current,
 * total time's must be mediacontroller_time_total, file name's must be
 * mediacontroller_file_name. And your resources must have a pause_button
 * drawable and a play_button drawable.
 * <p>
 * Functions like show() and hide() have no effect when MediaController is
 * created in an xml layout.
 */
public class MediaController extends FrameLayout {
	private final String TAG = "App";
	private App app;
	private MediaPlayerControl mPlayer;
	private ReturnProgramView m_ReturnProgramView = null;
	private Context mContext;
	private PopupWindow mWindow;
	private ListView lv_group;
	private RadioButton lv_radio0;
	private RadioButton lv_radio1;
	private RadioButton lv_radio2;
	private GroupAdapter groupAdapter;
	private ArrayList dataStruct;
	private int mAnimStyle;
	private View mAnchor;
	private View mRoot;
	private View mViewBottomRight;
	private View mViewTopRight;
	private View mimageView33;
	private SeekBar mSeekBar;
	private TextView mEndTime, mCurrentTime;
	private TextView mFileName;
	private OutlineTextView mInfoView;
	private String mTitle;
	private String mSubName;
	private long mDuration;
	private boolean mShowing;
	private boolean mDragging;
	private boolean mInstantSeeking = true;
	private static final int sDefaultTimeout = 3000;
	private static final int FADE_OUT = 1;
	private static final int SHOW_PROGRESS = 2;
	private static final int SHOW_TOPRIGHT = 3;
	private static final int SHOW_BOTTOMRIGHT = 4;
	private static final int SHOW_PRODDATA = 5;
	private boolean mFromXml = false;
	private ImageButton mPauseButton;
	private ImageButton mDlnaButton;
	private ImageButton mYunduanButton;
	private ImageButton mReturnButton;
	private ImageButton mReduceButton;
	private ImageButton mPreButton;
	private ImageButton mNextButton;
	private ImageButton mQualityButton;
	private ImageButton mSelectButton;
	private ImageView videosource;
	private TextView mTextViewDownloadRate;
	private TextView videosource_tv;

	private RelativeLayout mTopBlockLayout;// 播放器顶部模块
	private RelativeLayout mBottomBlockLayout;// 播放器底部模块

	private AudioManager mAM;

	private CurrentPlayData mCurrentPlayData;

	private int CurrentCategory = 0;

	private int CurrentIndex = 0;

	private int CurrentSource = 0;
	private int CurrentQuality = 0;
	private int ShowQuality = 0;
	private boolean mIsPausedByHuman = false;

	// 视频云端投放信息
	FayeClient mClient;
	private String tv_channel;
	private String user_id;
	private String prod_id;// 视频ID
	private int prod_type;// 视频类型,视频类别 1：电影，2：电视剧，3：综艺，131动漫
	private String prod_name;// 视频名称，就是显示在播放器最左上角的名称
	private String prod_url;// 视频播放地址
	private String prod_src;// 视频来源
	private long prod_time;// 视频开始播放时间 :秒*1000
	private int prod_qua; // 720P ：0 还是1080P：1
	private static final String ue_screencast_video_push = "云端推送视频";

	// private boolean DLNAMODE = false;

	public int getCurrentIndex() {
		return CurrentIndex;
	}

	public int getCurrentCategory() {
		return CurrentCategory;
	}

	public boolean ismIsPausedByHuman() {
		return mIsPausedByHuman;
	}

	public void setApp(App app) {
		this.app = app;
	}

	public MediaController(Context context, AttributeSet attrs) {
		super(context, attrs);
		mRoot = this;
		mFromXml = true;
		initController(context);
	}

	public MediaController(Context context, FayeClient mClient, String user_id,
			String channel) {
		super(context);
		this.mClient = mClient;
		this.user_id = user_id;
		this.tv_channel = channel;
		if (!mFromXml && initController(context))
			initFloatingWindow();
	}

	private boolean initController(Context context) {
		mContext = context;
		mAM = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		return true;
	}

	@Override
	public void onFinishInflate() {
		if (mRoot != null)
			initControllerView(mRoot);
	}

	private void initFloatingWindow() {
		android.util.Log.i("player_yy", "initFloatingWindow");
		mWindow = new PopupWindow(mContext);
		// mWindow.setFocusable(false);
		mWindow.setFocusable(true);
		mWindow.setBackgroundDrawable(null);
		mWindow.setOutsideTouchable(true);
		mAnimStyle = android.R.style.Animation;

	}

	/**
	 * Set the view that acts as the anchor for the control view. This can for
	 * example be a VideoView, or your Activity's main view.
	 * 
	 * @param view
	 *            The view to which to anchor the controller when it is visible.
	 */
	public void setAnchorView(View view) {
		mAnchor = view;
		if (!mFromXml) {
			removeAllViews();
			mRoot = makeControllerView();
			mWindow.setContentView(mRoot);
			mWindow.setWidth(android.view.ViewGroup.LayoutParams.MATCH_PARENT);
			mWindow.setHeight(android.view.ViewGroup.LayoutParams.MATCH_PARENT);

			// initPopWindows();

		}
		initControllerView(mRoot);
	}

	/**
	 * Create the view that holds the widgets that control playback. Derived
	 * classes can override this to create their own.
	 * 
	 * @return The controller view.
	 */
	protected View makeControllerView() {
		return ((LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.mediacontroller, this);
	}

	private void initControllerView(View v) {
		mPauseButton = (ImageButton) v
				.findViewById(R.id.mediacontroller_play_pause);
		mDlnaButton = (ImageButton) v.findViewById(R.id.mediacontroller_dlna);
		mYunduanButton = (ImageButton) v.findViewById(R.id.yunduan_toufang);
		if (app.GetServiceData("Binding_TV") != null) {
			mYunduanButton.setVisibility(View.VISIBLE);
		} else {
			mYunduanButton.setVisibility(View.INVISIBLE);
		}
		mReturnButton = (ImageButton) v.findViewById(R.id.imageButton1);
		mReduceButton = (ImageButton) v.findViewById(R.id.imageButton2);
		mPreButton = (ImageButton) v.findViewById(R.id.imageButton3);
		mNextButton = (ImageButton) v.findViewById(R.id.imageButton4);
		mQualityButton = (ImageButton) v.findViewById(R.id.imageButton5);
		mSelectButton = (ImageButton) v.findViewById(R.id.imageButton6);

		videosource = (ImageView) v.findViewById(R.id.videosource_img);
		videosource_tv = (TextView) v.findViewById(R.id.videosource_tv);

		// mTextView1 = (TextView) v.findViewById(R.id.textView1);
		// mTextView2 = (TextView) v.findViewById(R.id.textView2);
		mTextViewDownloadRate = (TextView) v
				.findViewById(R.id.textViewDownloadRate);
		mimageView33 = v.findViewById(R.id.imageView33);
		mViewTopRight = v.findViewById(R.id.relativeLayoutTopRight);
		mViewBottomRight = v.findViewById(R.id.relativeLayoutBottomRight);

		mTopBlockLayout = (RelativeLayout) v.findViewById(R.id.relativeLayout1);
		mBottomBlockLayout = (RelativeLayout) v
				.findViewById(R.id.relativeLayoutBottom);

		lv_group = (ListView) v.findViewById(R.id.listView1);
		// 加载数据
		dataStruct = new ArrayList<String>();

		groupAdapter = new GroupAdapter(mContext, dataStruct);
		// lv_group.setItemsCanFocus(false);
		lv_group.setAdapter(groupAdapter);

		if (lv_group != null) {
			lv_group.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					OnClickSelect(position);
				}
			});
			lv_group.setOnScrollListener(new OnScrollListener() {
				@Override
				public void onScrollStateChanged(AbsListView view,
						int scrollState) {
					switch (scrollState) {
					// 当滚动时
					case OnScrollListener.SCROLL_STATE_IDLE:
						show(sDefaultTimeout);
						break;
					}
				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {

				}
			});
		}
		RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup1);
		if (radioGroup != null) {
			lv_radio0 = (RadioButton) v.findViewById(R.id.radio0);
			lv_radio1 = (RadioButton) v.findViewById(R.id.radio1);
			lv_radio2 = (RadioButton) v.findViewById(R.id.radio2);
			// CurrentQuality = 1;
			// lv_radio1.setChecked(true);
			radioGroup.setOnCheckedChangeListener(mRadioGroupListener);
			if (m_ReturnProgramView != null) {
				mHandler.removeMessages(FADE_OUT);
				mHandler.sendEmptyMessageDelayed(SHOW_PRODDATA, 100);
			}
		}

		if (mPauseButton != null) {
			mPauseButton.requestFocus();
			mPauseButton.setOnClickListener(mPauseListener);
		}
		if (mDlnaButton != null) {
			// mDlnaButton.setVisibility(View.INVISIBLE);
			mDlnaButton.setOnClickListener(mDlnaListener);
		}
		if (mYunduanButton != null) {
			// mDlnaButton.setVisibility(View.INVISIBLE);
			mYunduanButton.setOnClickListener(mYunduanListener);
		}

		if (mReturnButton != null)
			mReturnButton.setOnClickListener(mReturnListener);
		if (mReduceButton != null)
			mReduceButton.setOnClickListener(mReduceListener);
		if (mPreButton != null)
			mPreButton.setOnClickListener(mPreListener);
		if (mNextButton != null)
			mNextButton.setOnClickListener(mNextListener);
		if (mQualityButton != null)
			mQualityButton.setOnClickListener(mQualityListener);
		if (mSelectButton != null)
			mSelectButton.setOnClickListener(mSelectListener);

		mSeekBar = (SeekBar) v.findViewById(R.id.mediacontroller_seekbar);
		if (mSeekBar != null) {
			mSeekBar.setOnSeekBarChangeListener(mSeekListener);
			mSeekBar.setThumbOffset(1);
			mSeekBar.setMax(1000);
		}

		mEndTime = (TextView) v.findViewById(R.id.mediacontroller_time_total);
		mCurrentTime = (TextView) v
				.findViewById(R.id.mediacontroller_time_current);
		mFileName = (TextView) v.findViewById(R.id.mediacontroller_file_name);
		if (mFileName != null)
			mFileName.setText(mTitle);

	}

	public void OnClickSelect(int index) {
		mPlayer.pause();
		CurrentIndex = index;
		Constant.select_index = index;
		groupAdapter.notifyDataSetChanged();
		lv_group.invalidate();

		String PROD_SOURCE = null;
		String title = null;

		switch (CurrentCategory) {
		case 0:
			break;
		case 1:
			if (m_ReturnProgramView.tv.episodes[index].down_urls != null) {
				videoSourceSort(m_ReturnProgramView.tv.episodes[index].down_urls);
				for (int i = 0; i < m_ReturnProgramView.tv.episodes[index].down_urls.length; i++) {
					CurrentSource = i;

					for (int j = 0; j < Constant.video_index.length; j++) {
						if (PROD_SOURCE == null
								&& m_ReturnProgramView.tv.episodes[index].down_urls[i].source
										.trim().equalsIgnoreCase(
												Constant.video_index[j])) {
							String name = m_ReturnProgramView.tv.name;
							title = "第"
									+ m_ReturnProgramView.tv.episodes[CurrentIndex].name
									+ "集";
							mFileName.setText(name + title);
							PROD_SOURCE = GetSource(index, i);

							// yangzhg
							StatisticsUtils
									.StatisticsClicksShow(
											new AQuery(mContext),
											app,
											m_ReturnProgramView.tv.id,
											m_ReturnProgramView.tv.name,
											m_ReturnProgramView.tv.episodes[CurrentIndex].name,
											2);
							break;
						}
					}
				}
			}
			break;
		case 2:
			if (m_ReturnProgramView.show.episodes[index].down_urls != null) {
				videoSourceSort(m_ReturnProgramView.show.episodes[index].down_urls);
				for (int i = 0; i < m_ReturnProgramView.show.episodes[index].down_urls.length; i++) {
					CurrentSource = i;

					for (int j = 0; j < Constant.video_index.length; j++) {

						if (PROD_SOURCE == null
								&& m_ReturnProgramView.show.episodes[index].down_urls[i].source
										.trim().equalsIgnoreCase(
												Constant.video_index[j])) {
							String name = m_ReturnProgramView.show.name;
							title = m_ReturnProgramView.show.episodes[index].name;

							mFileName.setText(name + title);
							PROD_SOURCE = GetSource(index, i);

							// yangzhg
							StatisticsUtils
									.StatisticsClicksShow(
											new AQuery(mContext),
											app,
											m_ReturnProgramView.show.id,
											m_ReturnProgramView.show.name,
											m_ReturnProgramView.show.episodes[CurrentIndex].name,
											3);
							break;
						}
					}
				}

			}
			break;
		}

		ShowQuality();

		if (PROD_SOURCE != null)
			mPlayer.setContinueVideoPath(title, PROD_SOURCE, false);

	}

	// 给片源赋权值
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

	private String GetSource(int proi_index, int sourceIndex) {
		String PROD_SOURCE = null;
		switch (CurrentCategory) {
		case 0:
			break;
		case 1:
			for (int k = 0; k < m_ReturnProgramView.tv.episodes[proi_index].down_urls[sourceIndex].urls.length; k++) {
				CurrentQuality = k;
				ReturnProgramView.DOWN_URLS.URLS CurrentURLS = m_ReturnProgramView.tv.episodes[proi_index].down_urls[sourceIndex].urls[k];
				if (CurrentURLS != null
						&& CurrentURLS.url != null
						&& app.CheckUrlIsValidFromServer(
								CurrentURLS.url.trim(), "1")) {
					for (int i = 0; i < Constant.quality_index.length; i++) {
						if (PROD_SOURCE == null
								&& CurrentURLS.type.trim().equalsIgnoreCase(
										Constant.quality_index[i])) {
							PROD_SOURCE = CurrentURLS.url.trim();
							break;
						}
					}
				}
				if (PROD_SOURCE != null)
					break;
			}
			break;
		case 2:
			for (int k = 0; k < m_ReturnProgramView.show.episodes[proi_index].down_urls[sourceIndex].urls.length; k++) {
				CurrentQuality = k;
				ReturnProgramView.DOWN_URLS.URLS CurrentURLS = m_ReturnProgramView.show.episodes[proi_index].down_urls[sourceIndex].urls[k];
				if (CurrentURLS != null
						&& CurrentURLS.url != null
						&& app.CheckUrlIsValidFromServer(
								CurrentURLS.url.trim(), "1")) {
					for (int i = 0; i < Constant.quality_index.length; i++) {
						if (PROD_SOURCE == null
								&& CurrentURLS.type.trim().equalsIgnoreCase(
										Constant.quality_index[i])) {
							PROD_SOURCE = CurrentURLS.url.trim();
							break;
						}
					}
				}
				if (PROD_SOURCE != null)
					break;
			}
			break;

		}

		return PROD_SOURCE;

	}

	public void ShowCurrentPlayData(CurrentPlayData mCurrentPlayData) {
		CurrentCategory = mCurrentPlayData.CurrentCategory;
		CurrentIndex = mCurrentPlayData.CurrentIndex;
		CurrentSource = mCurrentPlayData.CurrentSource;
		CurrentQuality = mCurrentPlayData.CurrentQuality;
		ShowQuality = mCurrentPlayData.ShowQuality;

	}

	public void SelectQuality(int index) {
		mPlayer.pause();

		ShowQuality = index;
		String PROD_SOURCE = null;

		ReturnProgramView.DOWN_URLS.URLS CurrentURLS = null;
		switch (CurrentCategory) {
		case 0:
			for (int k = 0; k < m_ReturnProgramView.movie.episodes[CurrentIndex].down_urls[CurrentSource].urls.length; k++) {
				if (m_ReturnProgramView.movie.episodes[CurrentIndex].down_urls[CurrentSource].urls[k].type
						.equalsIgnoreCase(Constant.quality_index[index])) {
					CurrentURLS = m_ReturnProgramView.movie.episodes[CurrentIndex].down_urls[CurrentSource].urls[k];
					break;
				}
			}

			break;
		case 1:
			for (int k = 0; k < m_ReturnProgramView.tv.episodes[CurrentIndex].down_urls[CurrentSource].urls.length; k++) {
				if (m_ReturnProgramView.tv.episodes[CurrentIndex].down_urls[CurrentSource].urls[k].type
						.equalsIgnoreCase(Constant.quality_index[index])) {
					CurrentURLS = m_ReturnProgramView.tv.episodes[CurrentIndex].down_urls[CurrentSource].urls[k];
					break;
				}
			}
			break;
		case 2:
			for (int k = 0; k < m_ReturnProgramView.show.episodes[CurrentIndex].down_urls[CurrentSource].urls.length; k++) {
				if (m_ReturnProgramView.show.episodes[CurrentIndex].down_urls[CurrentSource].urls[k].type
						.equalsIgnoreCase(Constant.quality_index[index])) {
					CurrentURLS = m_ReturnProgramView.show.episodes[CurrentIndex].down_urls[CurrentSource].urls[k];
					break;
				}
			}

			break;

		}

		if (CurrentURLS != null && CurrentURLS.url != null) {
			PROD_SOURCE = CurrentURLS.url.trim();
			// app.CheckUrlIsValidFromServer(PROD_SOURCE,"1");
		}
		if (PROD_SOURCE != null)
			mPlayer.setContinueVideoPath(null, PROD_SOURCE, true);
	}

	public void setDownloadRate(int rate) {
		if (mTextViewDownloadRate != null)
			mTextViewDownloadRate.setText(Integer.toString(rate) + "kb/s");
	}

	public void setMediaPlayer(MediaPlayerControl player) {
		mPlayer = player;
		updatePausePlay();
	}

	public void setVideoSource() {
		String source = null;
		switch (CurrentCategory) {
		case 0:
			source = m_ReturnProgramView.movie.episodes[0].down_urls[CurrentSource].source;
			if (source.equalsIgnoreCase("wangpan")) {
				source = m_ReturnProgramView.movie.episodes[0].video_urls[CurrentSource].source;
			}
			break;
		case 1:
			source = m_ReturnProgramView.tv.episodes[CurrentIndex].down_urls[CurrentSource].source;
			if (source.equalsIgnoreCase("wangpan")) {
				source = m_ReturnProgramView.tv.episodes[CurrentIndex].video_urls[CurrentSource].source;
			}
			break;
		case 2:
			source = m_ReturnProgramView.show.episodes[CurrentIndex].down_urls[CurrentSource].source;
			if (source.equalsIgnoreCase("wangpan")) {
				source = m_ReturnProgramView.show.episodes[CurrentIndex].video_urls[CurrentSource].source;
			}
			break;
		}
		if (source != null) {
			videosource_tv.setVisibility(View.VISIBLE);
		}

		if (source.equalsIgnoreCase("letv")
				|| source.equalsIgnoreCase("le_tv_fee")) {
			videosource.setBackgroundResource(R.drawable.logo_letv);
		} else if (source.equalsIgnoreCase("fengxing")) {
			videosource.setBackgroundResource(R.drawable.logo_fengxing);
		} else if (source.equalsIgnoreCase("qiyi")) {
			videosource.setBackgroundResource(R.drawable.logo_qiyi);
		} else if (source.equalsIgnoreCase("youku")) {
			videosource.setBackgroundResource(R.drawable.logo_youku);
		} else if (source.equalsIgnoreCase("sinahd")) {
			videosource.setBackgroundResource(R.drawable.logo_sinahd);
		} else if (source.equalsIgnoreCase("sohu")) {
			videosource.setBackgroundResource(R.drawable.logo_sohu);
		} else if (source.equalsIgnoreCase("56")) {
			videosource.setBackgroundResource(R.drawable.logo_56);
		} else if (source.equalsIgnoreCase("qq")) {
			videosource.setBackgroundResource(R.drawable.logo_qq);
		} else if (source.equalsIgnoreCase("pptv")) {
			videosource.setBackgroundResource(R.drawable.logo_pptv);
		} else if (source.equalsIgnoreCase("m1905")) {
			videosource.setBackgroundResource(R.drawable.logo_m1905);
		} else {
			videosource.setBackgroundResource(R.drawable.logo_pptv);
		}
	}

	/**
	 * Control the action when the seekbar dragged by user
	 * 
	 * @param seekWhenDragging
	 *            True the media will seek periodically
	 */
	public void setInstantSeeking(boolean seekWhenDragging) {
		mInstantSeeking = seekWhenDragging;
	}

	public void show() {
		show(sDefaultTimeout);
	}

	/**
	 * Set the content of the file_name TextView
	 * 
	 * @param name
	 */
	public void setFileName(String name) {
		this.mTitle = name;
		// if (mFileName != null)
		// mFileName.setText(mTitle);
	}

	public void DisableButtom() {
		mNextButton.setVisibility(View.INVISIBLE);
		mTextViewDownloadRate.setVisibility(View.INVISIBLE);
		mimageView33.setVisibility(View.INVISIBLE);
		mQualityButton.setVisibility(View.INVISIBLE);
		mSelectButton.setVisibility(View.INVISIBLE);
	}

	public void setSubName(String name) {
		this.mSubName = name;
	}

	private void ShowQuality() {

		lv_radio0.setVisibility(View.INVISIBLE);
		lv_radio1.setVisibility(View.INVISIBLE);
		lv_radio2.setVisibility(View.INVISIBLE);

		switch (CurrentCategory) {
		case 0:
			for (int i = 0; i < m_ReturnProgramView.movie.episodes[CurrentIndex].down_urls[CurrentSource].urls.length; i++) {
				if (m_ReturnProgramView.movie.episodes[CurrentIndex].down_urls[CurrentSource].urls[i].type
						.equalsIgnoreCase("mp4"))
					lv_radio1.setVisibility(View.VISIBLE);

				if (m_ReturnProgramView.movie.episodes[CurrentIndex].down_urls[CurrentSource].urls[i].type
						.equalsIgnoreCase("flv"))
					lv_radio0.setVisibility(View.VISIBLE);

				if (m_ReturnProgramView.movie.episodes[CurrentIndex].down_urls[CurrentSource].urls[i].type
						.equalsIgnoreCase("hd2"))
					lv_radio2.setVisibility(View.VISIBLE);
			}
			break;
		case 1:
			for (int i = 0; i < m_ReturnProgramView.tv.episodes[CurrentIndex].down_urls[CurrentSource].urls.length; i++) {
				if (m_ReturnProgramView.tv.episodes[CurrentIndex].down_urls[CurrentSource].urls[i].type
						.equalsIgnoreCase("mp4"))
					lv_radio1.setVisibility(View.VISIBLE);

				if (m_ReturnProgramView.tv.episodes[CurrentIndex].down_urls[CurrentSource].urls[i].type
						.equalsIgnoreCase("flv"))
					lv_radio0.setVisibility(View.VISIBLE);

				if (m_ReturnProgramView.tv.episodes[CurrentIndex].down_urls[CurrentSource].urls[i].type
						.equalsIgnoreCase("hd2"))
					lv_radio2.setVisibility(View.VISIBLE);
			}
			break;
		case 2:
			for (int i = 0; i < m_ReturnProgramView.show.episodes[CurrentIndex].down_urls[CurrentSource].urls.length; i++) {
				if (m_ReturnProgramView.show.episodes[CurrentIndex].down_urls[CurrentSource].urls[i].type
						.equalsIgnoreCase("mp4"))
					lv_radio1.setVisibility(View.VISIBLE);

				if (m_ReturnProgramView.show.episodes[CurrentIndex].down_urls[CurrentSource].urls[i].type
						.equalsIgnoreCase("flv"))
					lv_radio0.setVisibility(View.VISIBLE);

				if (m_ReturnProgramView.show.episodes[CurrentIndex].down_urls[CurrentSource].urls[i].type
						.equalsIgnoreCase("hd2"))
					lv_radio2.setVisibility(View.VISIBLE);
			}
			break;

		}
	}

	public void setProd_Data(ReturnProgramView m_ReturnProgramView) {
		this.m_ReturnProgramView = m_ReturnProgramView;
		if (this.m_ReturnProgramView != null) {
			if (m_ReturnProgramView.movie != null) {
				CurrentCategory = 0;
				if (mNextButton != null)
					mNextButton.setVisibility(View.INVISIBLE);
				if (mSelectButton != null)
					mSelectButton.setVisibility(View.INVISIBLE);
			} else if (m_ReturnProgramView.tv != null) {
				CurrentCategory = 1;
				if (mSubName != null) {
					this.mSubName = mSubName.replace("第", "");
					this.mSubName = mSubName.replace("集", "").trim();
				}
				// mFileName.setText(mTitle+"第" +
				// m_ReturnProgramView.tv.episodes[CurrentIndex].name + "集");
				if (dataStruct != null) {
					for (int i = 0; i < m_ReturnProgramView.tv.episodes.length; i++) {
						// if(mSubName.equalsIgnoreCase(m_ReturnProgramView.tv.episodes[i].name))
						dataStruct.add("第" + Integer.toString(i + 1) + "集");
						String str = m_ReturnProgramView.tv.episodes[i].name;
					}
					groupAdapter.notifyDataSetChanged();
				}
			} else if (m_ReturnProgramView.show != null) {
				CurrentCategory = 2;
				// mFileName.setText(mTitle
				// +"-"+m_ReturnProgramView.show.episodes[CurrentIndex].name);
				if (dataStruct != null) {
					for (int i = 0; i < m_ReturnProgramView.show.episodes.length; i++) {
						// if(mSubName.equalsIgnoreCase(m_ReturnProgramView.show.episodes[i].name))
						dataStruct
								.add(m_ReturnProgramView.show.episodes[i].name);
					}

					groupAdapter.notifyDataSetChanged();
				}
			}
			ShowQuality();
		}
		// ShowQuality
		if (Constant.player_quality_index[ShowQuality].equalsIgnoreCase("flv")
				|| Constant.player_quality_index[ShowQuality]
						.equalsIgnoreCase("3gp"))
			lv_radio0.setChecked(true);
		else if (Constant.player_quality_index[ShowQuality]
				.equalsIgnoreCase("mp4"))
			lv_radio1.setChecked(true);
		else if (Constant.player_quality_index[ShowQuality]
				.equalsIgnoreCase("hd2"))
			lv_radio2.setChecked(true);
		// if (CurrentQuality == 3) {
		// // if (lv_radio2.getVisibility() == View.VISIBLE)
		// // lv_radio2.setChecked(true);
		// // else if (lv_radio1.getVisibility() == View.VISIBLE)
		// lv_radio0.setChecked(true);
		// // else if (lv_radio0.getVisibility() == View.VISIBLE)
		// // lv_radio0.setChecked(true);
		// }
	}

	/**
	 * Set the View to hold some information when interact with the
	 * MediaController
	 * 
	 * @param v
	 */
	public void setInfoView(OutlineTextView v) {
		mInfoView = v;
	}

	private void disableUnsupportedButtons() {
		try {
			if (mPauseButton != null && !mPlayer.canPause())
				mPauseButton.setEnabled(false);
		} catch (IncompatibleClassChangeError ex) {
		}
	}

	/**
	 * <p>
	 * Change the animation style resource for this controller.
	 * </p>
	 * 
	 * <p>
	 * If the controller is showing, calling this method will take effect only
	 * the next time the controller is shown.
	 * </p>
	 * 
	 * @param animationStyle
	 *            animation style to use when the controller appears and
	 *            disappears. Set to -1 for the default animation, 0 for no
	 *            animation, or a resource identifier for an explicit animation.
	 * 
	 */
	public void setAnimationStyle(int animationStyle) {
		mAnimStyle = animationStyle;
	}

	/**
	 * Show the controller on screen. It will go away automatically after
	 * 'timeout' milliseconds of inactivity.
	 * 
	 * @param timeout
	 *            The timeout in milliseconds. Use 0 to show the controller
	 *            until hide() is called.
	 */
	public void show(int timeout) {
		if (!mShowing && mAnchor != null && mAnchor.getWindowToken() != null) {
			if (mPauseButton != null)
				mPauseButton.requestFocus();
			disableUnsupportedButtons();

			if (mFromXml) {
				setVisibility(View.VISIBLE);
			} else {
				int[] location = new int[2];

				mAnchor.getLocationOnScreen(location);
				Rect anchorRect = new Rect(location[0], location[1],
						location[0] + mAnchor.getWidth(), location[1]
								+ mAnchor.getHeight());

				mWindow.setAnimationStyle(mAnimStyle);
				mWindow.showAtLocation(mAnchor, Gravity.NO_GRAVITY,
						anchorRect.left, anchorRect.bottom);
				// mWindow.showAtLocation(mAnchor, Gravity.NO_GRAVITY, 0, 0);
			}
			mShowing = true;
			if (mShownListener != null)
				mShownListener.onShown();
		}
		updatePausePlay();
		mHandler.sendEmptyMessage(SHOW_PROGRESS);

		if (timeout != 0) {
			mHandler.removeMessages(FADE_OUT);
			mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT),
					timeout);
		}
	}

	public boolean isShowing() {
		return mShowing;
	}

	public void hideNow() {

	}

	public void hide() {
		if (mAnchor == null)
			return;

		if (mShowing) {
			try {
				mHandler.removeMessages(SHOW_PROGRESS);
				if (mFromXml)
					setVisibility(View.GONE);
				else
					mWindow.dismiss();
				if (mViewBottomRight.getVisibility() == View.VISIBLE)
					mViewBottomRight.setVisibility(View.GONE);

				if (mViewTopRight.getVisibility() == View.VISIBLE)
					mViewTopRight.setVisibility(View.GONE);
			} catch (IllegalArgumentException ex) {
				Log.d("MediaController already removed");
			}
			mShowing = false;
			if (mHiddenListener != null)
				mHiddenListener.onHidden();// 方便调试不隐藏，yy
		}
	}

	public interface OnShownListener {
		public void onShown();
	}

	private OnShownListener mShownListener;

	public void setOnShownListener(OnShownListener l) {
		mShownListener = l;
	}

	public interface OnHiddenListener {
		public void onHidden();
	}

	private OnHiddenListener mHiddenListener;

	public void setOnHiddenListener(OnHiddenListener l) {
		mHiddenListener = l;
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			long pos;
			switch (msg.what) {
			case FADE_OUT:
				hide();
				break;
			case SHOW_PROGRESS:
				pos = setProgress();
				if (!mDragging && mShowing) {
					msg = obtainMessage(SHOW_PROGRESS);
					sendMessageDelayed(msg, 1000 - (pos % 1000));
					updatePausePlay();
				}
				break;
			case SHOW_TOPRIGHT:
				updateTopRight();
				break;
			case SHOW_BOTTOMRIGHT:
				updateBottomRight();
				break;
			}

		}
	};

	private long setProgress() {
		if (mPlayer == null || mDragging)
			return 0;

		long position = mPlayer.getCurrentPosition();
		long duration = mPlayer.getDuration();
		if (mSeekBar != null) {
			if (duration > 0) {
				long pos = 1000L * position / duration;
				mSeekBar.setProgress((int) pos);
			}
			// int percent = mPlayer.getBufferPercentage();
			// mSeekBar.setSecondaryProgress(percent * 10);
		}

		mDuration = duration;

		if (mEndTime != null)
			mEndTime.setText(StringUtils.generateTime(mDuration));
		if (mCurrentTime != null)
			mCurrentTime.setText(StringUtils.generateTime(position));

		return position;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// show(sDefaultTimeout);
		if (mShowing) {

			float locationY = event.getY();
			if (locationY >= mTopBlockLayout.getHeight()
					&& locationY <= ((float) getHeight())
							- mBottomBlockLayout.getHeight()) {

				hide();
			}
		}
		return true;
	}

	@Override
	public boolean onTrackballEvent(MotionEvent ev) {
		show(sDefaultTimeout);
		return false;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();
		if (event.getRepeatCount() == 0
				&& (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
						|| keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE)) {
			doPauseResume();
			show(sDefaultTimeout);
			if (mPauseButton != null)
				mPauseButton.requestFocus();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
			if (mPlayer.isPlaying()) {
				mPlayer.pause();
				updatePausePlay();
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_MENU) {
			hide();
			return true;
		} else {
			show(sDefaultTimeout);
		}
		return super.dispatchKeyEvent(event);
	}

	private View.OnClickListener mPauseListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			doPauseResume();
			show(sDefaultTimeout);
		}
	};

	private View.OnClickListener mDlnaListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mPlayer.isPlaying()) {
				mPlayer.pause();
				updatePausePlay();
			}
			// DLNAMODE = true;
			mPlayer.gotoDlnaVideoPlay();
			// doPauseResume();
			// show(sDefaultTimeout);
		}
	};

	private View.OnClickListener mYunduanListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// doPauseResume();
			// show(sDefaultTimeout);
//			sendYunduanMessage();
		}
	};

	private void sendYunduanMessage() {
		switch (CurrentCategory) {
		case 0:
			prod_id = m_ReturnProgramView.movie.id;
			prod_type = 1;
			prod_name = m_ReturnProgramView.movie.name;
			prod_url = m_ReturnProgramView.movie.episodes[CurrentIndex].down_urls[CurrentSource].urls[CurrentQuality].url;
			prod_src = m_ReturnProgramView.movie.episodes[CurrentIndex].down_urls[CurrentSource].source;
			prod_time = mPlayer.getCurrentPosition() / 1000;
			if (m_ReturnProgramView.movie.episodes[CurrentIndex].down_urls[CurrentSource].urls[CurrentQuality].type
					.equals("hd2")) {
				prod_qua = 1;
			} else {
				prod_qua = 0;
			}
			break;
		case 1:
			prod_id = m_ReturnProgramView.tv.id;
			prod_type = 2;
			prod_name = m_ReturnProgramView.tv.name + "第"
					+ m_ReturnProgramView.tv.episodes[CurrentIndex].name + "集";
			prod_url = m_ReturnProgramView.tv.episodes[CurrentIndex].down_urls[CurrentSource].urls[CurrentQuality].url;
			prod_src = m_ReturnProgramView.tv.episodes[CurrentIndex].down_urls[CurrentSource].source;
			prod_time = mPlayer.getCurrentPosition() / 1000;
			if (m_ReturnProgramView.tv.episodes[CurrentIndex].down_urls[CurrentSource].urls[CurrentQuality].type
					.equals("hd2")) {
				prod_qua = 1;
			} else {
				prod_qua = 0;
			}
			break;
		case 2:
			prod_id = m_ReturnProgramView.show.id;
			prod_type = 3;
			prod_name = m_ReturnProgramView.show.name
					+ m_ReturnProgramView.show.episodes[CurrentIndex].name;
			prod_url = m_ReturnProgramView.show.episodes[CurrentIndex].down_urls[CurrentSource].urls[CurrentQuality].url;
			prod_src = m_ReturnProgramView.show.episodes[CurrentIndex].down_urls[CurrentSource].source;
			prod_time = mPlayer.getCurrentPosition() / 1000;
			if (m_ReturnProgramView.show.episodes[CurrentIndex].down_urls[CurrentSource].urls[CurrentQuality].type
					.equals("hd2")) {
				prod_qua = 1;
			} else {
				prod_qua = 0;
			}
			break;
		}

		JSONObject json = new JSONObject();
		try {
			json.put("push_type", "41");
			json.put("tv_channel", tv_channel);
			json.put("user_id", user_id);
			json.put("prod_id", prod_id);
			json.put("prod_type", prod_type);
			json.put("prod_name", prod_name);
			json.put("prod_url", prod_url);
			json.put("prod_src", prod_src);
			json.put("prod_time", prod_time);
			json.put("prod_qua", prod_qua);
			FayeService.SendMessageService(mContext, json, user_id);
			MobclickAgent.onEvent(mContext, ue_screencast_video_push);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private View.OnClickListener mReturnListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// if(DLNAMODE)
			// DLNAMODE = false;
			// else
			VideoPlayerActivity.IsFinish = true;
			mPlayer.OnComplete();
		}
	};
	private View.OnClickListener mReduceListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int mLayout = mPlayer.GetCurrentVideoLayout();
			if (mLayout == VideoView.VIDEO_LAYOUT_SCALE) {
				mLayout = VideoView.VIDEO_LAYOUT_ZOOM;
				mReduceButton.setBackgroundResource(R.drawable.player_full);
			} else {
				mLayout = VideoView.VIDEO_LAYOUT_SCALE;
				mReduceButton.setBackgroundResource(R.drawable.player_reduce);
			}
			mPlayer.setVideoLayout(mLayout, 0);

		}
	};
	private View.OnClickListener mPreListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mPlayer.canSeekBackward()) {
				long current = mPlayer.getCurrentPosition();
				if (current >= 30000)// 30s
					mPlayer.seekTo(current - 30000);
//				sendSeekChangedMessage(current - 30000);
			}
		}
	};
	private View.OnClickListener mNextListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (CurrentCategory) {
			case 0:
				break;
			case 1:

				OnClickSelect(++CurrentIndex);
				break;
			case 2:

				OnClickSelect(++CurrentIndex);
				break;

			}
		}
	};
	private RadioGroup.OnCheckedChangeListener mRadioGroupListener = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			switch (checkedId) {

			case R.id.radio0:
				if (ShowQuality != 0) {
					SelectQuality(0);
				}
				break;
			case R.id.radio1:
				if (ShowQuality != 1) {
					SelectQuality(1);
				}
				break;
			case R.id.radio2:
				if (ShowQuality != 2) {
					SelectQuality(2);
				}
				break;
			default:
				break;

			}
		}
	};
	private View.OnClickListener mQualityListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mHandler.removeMessages(FADE_OUT);
			mHandler.sendEmptyMessageDelayed(SHOW_BOTTOMRIGHT, 500);

		}
	};
	private View.OnClickListener mSelectListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mHandler.removeMessages(FADE_OUT);
			mHandler.sendEmptyMessageDelayed(SHOW_TOPRIGHT, 500);

		}
	};

	private void updateBottomRight() {
		if (mViewBottomRight.getVisibility() == View.VISIBLE)
			mViewBottomRight.setVisibility(View.GONE);
		else
			mViewBottomRight.setVisibility(View.VISIBLE);
		mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT),
				sDefaultTimeout);
	}

	private void updateTopRight() {
		if (mViewTopRight.getVisibility() == View.VISIBLE)
			mViewTopRight.setVisibility(View.GONE);
		else
			mViewTopRight.setVisibility(View.VISIBLE);
		mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT),
				sDefaultTimeout);
	}

	private void updatePausePlay() {
		if (mRoot == null || mPauseButton == null)
			return;

		if (mPlayer != null && mPlayer.isPlaying())
			mPauseButton.setBackgroundResource(R.drawable.player_pause);
		else
			mPauseButton.setBackgroundResource(R.drawable.player_play);
	}

	private void doPauseResume() {
		if (mPlayer.isPlaying()) {
			mIsPausedByHuman = true;
			mPlayer.pause();
//			sendPauseMessage();
		} else {
			mIsPausedByHuman = false;
			mPlayer.start();
//			sendPlayMessage();
		}
		updatePausePlay();
	}

	private void sendPlayMessage() {
		try {
			JSONObject json = new JSONObject();
			json.put("push_type", "403");
			json.put("tv_channel", tv_channel);
			json.put("user_id", user_id);
			json.put("prod_id", prod_id);
			json.put("prod_url", prod_url);
			FayeService.SendMessageService(mContext, json, user_id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void sendPauseMessage() {
		try {
			JSONObject json = new JSONObject();
			json.put("push_type", "405");
			json.put("tv_channel", tv_channel);
			json.put("user_id", user_id);
			json.put("prod_id", prod_id);
			json.put("prod_url", prod_url);
			FayeService.SendMessageService(mContext, json, user_id);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
		@Override
		public void onStartTrackingTouch(SeekBar bar) {
			mDragging = true;
			show(3600000);
			mHandler.removeMessages(SHOW_PROGRESS);
			if (mInstantSeeking)
				mAM.setStreamMute(AudioManager.STREAM_MUSIC, true);
			if (mInfoView != null) {
				mInfoView.setText("");
				mInfoView.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onProgressChanged(SeekBar bar, int progress,
				boolean fromuser) {
			if (!fromuser)
				return;

			long newposition = (mDuration * progress) / 1000;
			String time = StringUtils.generateTime(newposition);
			Log.d("newposition_time", time);
			if (mInstantSeeking) {
				mPlayer.seekTo(newposition);

			}
			if (mInfoView != null)
				mInfoView.setText(time);
			if (mCurrentTime != null)
				mCurrentTime.setText(time);
		}

		@Override
		public void onStopTrackingTouch(SeekBar bar) {
			if (!mInstantSeeking) {
				mPlayer.seekTo((mDuration * bar.getProgress()) / 1000);
//				sendSeekChangedMessage(mDuration * bar.getProgress() / 1000);
				// mPlayer.pause();
			}
			if (mInfoView != null) {
				mInfoView.setText("");
				mInfoView.setVisibility(View.GONE);
			}

			show(sDefaultTimeout);
			mHandler.removeMessages(SHOW_PROGRESS);
			mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
			mDragging = false;
			mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
			// mSeekBar.setMax( duration);
			// mSeekBar.setProgress(position);
			// mPlayer.seekTo((mDuration * bar.getProgress()) / 1000);
		}
	};

	private void sendSeekChangedMessage(long time) {
		try {
			JSONObject json = new JSONObject();
			json.put("push_type", "407");
			json.put("tv_channel", tv_channel);
			json.put("user_id", user_id);
			json.put("prod_id", prod_id);
			json.put("prod_url", prod_url);
			json.put("prod_time", time);
			FayeService.SendMessageService(mContext, json, user_id);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		if (mPauseButton != null)
			mPauseButton.setEnabled(enabled);
		if (mSeekBar != null)
			mSeekBar.setEnabled(enabled);
		disableUnsupportedButtons();
		super.setEnabled(enabled);
	}

	static class ViewHolder {
		TextView groupItem;
	}

	public class GroupAdapter extends BaseAdapter {

		private Context context;

		private List<String> list;

		public GroupAdapter(Context context, List<String> list) {

			this.context = context;
			this.list = list;

		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {

			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup viewGroup) {

			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.player_detail_list, null);
				holder = new ViewHolder();

				holder.groupItem = (TextView) convertView
						.findViewById(R.id.txt_video_caption);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (CurrentIndex == position) {

				holder.groupItem.setTextColor(Color.YELLOW);
			} else {
				holder.groupItem.setTextColor(Color.WHITE);
			}

			holder.groupItem.setText(list.get(position));

			return convertView;
		}

		/**
		 * 检查urlLink文本是否正常
		 * 
		 * @param urlLink
		 * @return
		 */
		private boolean CheckUrl(String urlLink) {

			// url本身不正�?直接返回
			if (urlLink == null || urlLink.length() <= 0) {

				return false;
			} else {

				if (!URLUtil.isValidUrl(urlLink)) {

					return false;
				}
			}

			return true;
		}

		/**
		 * 启动一个异步任务，把网络相关放在此任务�? * 重定向新的链接，直到拿到资源URL
		 * 
		 * 注意：因为网络或者服务器原因，重定向时间有可能比较长 因此需要较长时间等�? * @param url
		 * 
		 * @return 字符�?
		 */
		private String newATask(String url) {

			AsyncTask<String, Void, String> aynAsyncTask = new AsyncTask<String, Void, String>() {

				@Override
				protected String doInBackground(String... params) {
					// TODO Auto-generated method stub

					List<String> list = new ArrayList<String>();
					String dstUrl = null;
					try {
						simulateFirfoxRequest(Constant.USER_AGENT_IOS,
								params[0], list);// 使用递归，并把得到的链接放在集合中，取最后一次得到的链接即可

						dstUrl = list.get(list.size() - 1);
						if (BuildConfig.DEBUG)
							Log.i(TAG, "AsyncTask----->>URL : " + dstUrl);
						list.clear();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						if (BuildConfig.DEBUG)
							Log.i(TAG, "TimeOut!!!!!! : " + e);
						e.printStackTrace();
					}

					return dstUrl;
				}

			}.execute(url);
			try {
				String redirectUrl = aynAsyncTask.get();// 从异步任务中获取结果

				return redirectUrl;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * 模拟火狐浏览器给服务器发送不同请求，有火狐本身请求，IOS请求，Android请求
		 * 
		 * @param userAgent
		 *            firfox ios android
		 * @param srcUrl
		 *            原始地址【可能可以播放，可能需要跳转�? * @param list 存储播放地址
		 */
		private void simulateFirfoxRequest(String userAgent, String srcUrl,
				List<String> list) {
			// 模拟火狐ios发用请求 使用userAgent
			AndroidHttpClient mAndroidHttpClient = AndroidHttpClient
					.newInstance(userAgent);

			HttpParams httpParams = mAndroidHttpClient.getParams();
			// 连接时间最�?秒，可以更改
			HttpConnectionParams.setConnectionTimeout(httpParams, 3000 * 1);

			try {
				URL url = new URL(srcUrl);
				HttpGet mHttpGet = new HttpGet(url.toURI());
				HttpResponse response = mAndroidHttpClient.execute(mHttpGet);

				// 限定连接时间

				StatusLine statusLine = response.getStatusLine();
				int status = statusLine.getStatusCode();

				if (BuildConfig.DEBUG)
					Log.i(TAG, "HTTP STATUS : " + status);

				// 如果拿到资源直接返回url 如果没有拿到资源，并且要进行跳转,那就使用递归跳转
				if (status != HttpStatus.SC_OK) {
					if (BuildConfig.DEBUG)
						Log.i(TAG, "NOT OK   start");

					if (BuildConfig.DEBUG)
						Log.i(TAG, "NOT OK start");
					if (status == HttpStatus.SC_MOVED_PERMANENTLY || // 网址被永久移�?
																		// status
																		// ==
																		// HttpStatus.SC_MOVED_TEMPORARILY
																		// ||//网址暂时性移�?
																		// status
																		// ==HttpStatus.SC_SEE_OTHER
																		// ||//重新定位资源
							status == HttpStatus.SC_TEMPORARY_REDIRECT) {// 暂时定向

						Header header = response.getFirstHeader("Location");// 拿到重新定位后的header
						String location = header.getValue();// 从header重新取出信息
						list.add(location);

						mAndroidHttpClient.close();// 关闭此次连接

						if (BuildConfig.DEBUG)
							Log.i(TAG, "Location: " + location);
						// 进行下一次递归
						simulateFirfoxRequest(userAgent, location, list);
					} else {
						// 如果地址真的不存在，那就往里面加NULL字符�? list.add("NULL");
					}

				} else {
					list.add(srcUrl);
					mAndroidHttpClient.close();
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				if (BuildConfig.DEBUG)
					Log.i(TAG, "NOT OK" + e);
				mAndroidHttpClient.close();
				e.printStackTrace();
			}

		}

	}

	public interface MediaPlayerControl {
		void start();

		void pause();

		long getDuration();

		long getCurrentPosition();

		void seekTo(long pos);

		boolean isPlaying();

		int getBufferPercentage();

		boolean canPause();

		boolean canSeekBackward();

		boolean canSeekForward();

		void gotoDlnaVideoPlay();

		void OnComplete();

		void setVideoLayout(int layout, float aspectRatio);

		int GetCurrentVideoLayout();

		void setContinueVideoPath(String Title, String path,
				boolean PlayContinue);

	}
}
