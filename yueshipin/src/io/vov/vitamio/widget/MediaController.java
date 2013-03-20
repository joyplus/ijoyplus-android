/*
 * Copyright (C) 2011 VOV IO (http://vov.io/)
 */

package io.vov.vitamio.widget;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.joyplus.App;
import com.joyplus.BuildConfig;
import com.joyplus.Constant;
import com.joyplus.R;
import com.joyplus.Adapters.Tab3Page1ListData;
import com.joyplus.Service.Return.ReturnProgramView;

import io.vov.utils.Log;
import io.vov.utils.StringUtils;
import android.R.integer;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.VoicemailContract;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
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
public class MediaController extends FrameLayout  {
	private final String TAG = "App";
	private App app;
	private MediaPlayerControl mPlayer;
	private ReturnProgramView m_ReturnProgramView = null;
	private Context mContext;
	private PopupWindow mWindow;
//	private PopupWindow mWindowBottomRight;
//	private PopupWindow mWindowTopRight;
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
	private boolean mTopRightShowing = false;
	private boolean mBottomRightShowing = false;
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
	private ImageButton mReturnButton;
	private ImageButton mReduceButton;
	private ImageButton mPreButton;
	private ImageButton mNextButton;
	private ImageButton mQualityButton;
	private ImageButton mSelectButton;
	private TextView mTextView1;
	private TextView mTextView2;
	private TextView mTextViewDownloadRate;
	

	private AudioManager mAM;
	
	private int CurrentCategory = 0;
	private int CurrentIndex = 0;
	private int CurrentSource = 0;
	private int CurrentQuality = 0;
	
//	private boolean DLNAMODE = false;
	
	public void setApp(App app){
		this.app = app;
	}
	public MediaController(Context context, AttributeSet attrs) {
		super(context, attrs);
		mRoot = this;
		mFromXml = true;
		initController(context);
	}

	public MediaController(Context context) {
		super(context);
		if (!mFromXml && initController(context))
			initFloatingWindow();
	}

//	private void initPopWindows() {
//		LayoutInflater mLayoutInflater = (LayoutInflater) mContext
//				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		mViewTopRight = mLayoutInflater.inflate(R.layout.mediacontroller_top,
//				null);
//		mWindowTopRight = new PopupWindow(mViewTopRight, 194,
//				LayoutParams.WRAP_CONTENT);
//		lv_group = (ListView) mViewTopRight.findViewById(R.id.listView1);
//		// 加载数据
//		dataStruct = new ArrayList<String>();
//
//		groupAdapter = new GroupAdapter(mContext, dataStruct);
//		lv_group.setAdapter(groupAdapter);
//
//		mViewTopRight.setVisibility(View.GONE);
//
//		mViewBottomRight = mLayoutInflater.inflate(R.layout.mediacontroller2,
//				null);
//		mWindowBottomRight = new PopupWindow(mViewBottomRight, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//
//		mViewBottomRight.setVisibility(View.GONE);
//
//	}

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
		mWindow = new PopupWindow(mContext);
//		mWindow.setFocusable(false);
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
			
//			initPopWindows();

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
		mReturnButton = (ImageButton) v.findViewById(R.id.imageButton1);
		mReduceButton = (ImageButton) v.findViewById(R.id.imageButton2);
		mPreButton = (ImageButton) v.findViewById(R.id.imageButton3);
		mNextButton = (ImageButton) v.findViewById(R.id.imageButton4);
		mQualityButton = (ImageButton) v.findViewById(R.id.imageButton5);
		mSelectButton = (ImageButton) v.findViewById(R.id.imageButton6);

		mTextView1 = (TextView) v.findViewById(R.id.textView1);
		mTextView2 = (TextView) v.findViewById(R.id.textView2);
		mTextViewDownloadRate = (TextView) v.findViewById(R.id.textViewDownloadRate);
		mimageView33 =  v.findViewById(R.id.imageView33);
		mViewTopRight = v.findViewById(R.id.relativeLayoutTopRight);
		mViewBottomRight = v.findViewById(R.id.relativeLayoutBottomRight);
		
		lv_group = (ListView) v.findViewById(R.id.listView1);
		// 加载数据
		dataStruct = new ArrayList<String>();

//		dataStruct.add("第一集");
//		dataStruct.add("第二集");
//		dataStruct.add("第三集");
//		dataStruct.add("第四集");
		groupAdapter = new GroupAdapter(mContext, dataStruct);
//		lv_group.setItemsCanFocus(false);  
		lv_group.setAdapter(groupAdapter);

		if (lv_group != null){
			lv_group.setOnItemClickListener(new OnItemClickListener() {
		 			@Override
		 			public void onItemClick(AdapterView<?> parent, View view,
		 					int position, long id) {
		 					
		 					OnClickSelect(position);
		 			}
		 		});
			lv_group.setOnScrollListener(new OnScrollListener() {
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					switch (scrollState) {
					// 当不滚动时
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
			lv_radio0 = (RadioButton)v.findViewById(R.id.radio0);
			lv_radio1 = (RadioButton)v.findViewById(R.id.radio1);
			lv_radio2 = (RadioButton)v.findViewById(R.id.radio2);
//			CurrentQuality = 1;
//			lv_radio1.setChecked(true);
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
		if (mDlnaButton != null){
//			mDlnaButton.setVisibility(View.INVISIBLE);
			mDlnaButton.setOnClickListener(mDlnaListener);
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
		
		groupAdapter.notifyDataSetChanged();
		lv_group.invalidate();
				
		String PROD_SOURCE = null;
		String title = null;

		switch (CurrentCategory) {
		case 0:
			break;
		case 1:
			if (m_ReturnProgramView.tv.episodes[index].down_urls != null) {
				for (int i = 0; i < m_ReturnProgramView.tv.episodes[index].down_urls.length; i++) {
					CurrentSource = i;
					
					for (int j = 0; j < Constant.video_index.length; j++) {
						if (PROD_SOURCE == null &&m_ReturnProgramView.tv.episodes[index].down_urls[i].source.trim().equalsIgnoreCase(Constant.video_index[j])) {
							title = "第" + m_ReturnProgramView.tv.episodes[CurrentIndex].name + "集";
							mFileName.setText(title);
							PROD_SOURCE =  GetSource(index,i);
							break;
						}
					}			
				}
			}
			break;
		case 2:
			if (m_ReturnProgramView.show.episodes[index].down_urls != null) {
				for (int i = 0; i < m_ReturnProgramView.show.episodes[index].down_urls.length; i++) {
					CurrentSource = i;
					
					for (int j = 0; j < Constant.video_index.length; j++) {
						if (PROD_SOURCE == null &&m_ReturnProgramView.show.episodes[index].down_urls[i].source.trim().equalsIgnoreCase(Constant.video_index[j])) {
							title = m_ReturnProgramView.show.episodes[index].name;
							mFileName.setText(title);
							PROD_SOURCE =  GetSource(index,i);
							break;
						}
					}			
				}
			}
			break;
		}
		
		ShowQuality();
		
		if (PROD_SOURCE != null )
			mPlayer.setContinueVideoPath(title,PROD_SOURCE);
	}
 private String GetSource(int proi_index, int sourceIndex){
	 String PROD_SOURCE = null;
	 switch (CurrentCategory) {
	case 0:
		break;
	case 1:
		for (int k = 0; k < m_ReturnProgramView.tv.episodes[proi_index].down_urls[sourceIndex].urls.length; k++) {
			CurrentQuality = k;
			ReturnProgramView.DOWN_URLS.URLS CurrentURLS = m_ReturnProgramView.tv.episodes[proi_index].down_urls[sourceIndex].urls[k];
			if (CurrentURLS != null && CurrentURLS.url != null && app.CheckUrlIsValidFromServer(CurrentURLS.url.trim(),"1")) {
					for (int i = 0; i < Constant.quality_index.length; i++) {
						if (PROD_SOURCE == null && CurrentURLS.type.trim().equalsIgnoreCase(Constant.quality_index[i])) {
							PROD_SOURCE = CurrentURLS.url.trim();
							break;
						}
					}
			}
			if (PROD_SOURCE != null )
				break;
		}
		break;
	case 2:
		for (int k = 0; k < m_ReturnProgramView.show.episodes[proi_index].down_urls[sourceIndex].urls.length; k++) {
			CurrentQuality = k;
			ReturnProgramView.DOWN_URLS.URLS CurrentURLS = m_ReturnProgramView.show.episodes[proi_index].down_urls[sourceIndex].urls[k];
			if (CurrentURLS != null && CurrentURLS.url != null && app.CheckUrlIsValidFromServer(CurrentURLS.url.trim(),"1")) {
					for (int i = 0; i < Constant.quality_index.length; i++) {
						if (PROD_SOURCE == null && CurrentURLS.type.trim().equalsIgnoreCase(Constant.quality_index[i])) {
							PROD_SOURCE = CurrentURLS.url.trim();
							break;
						}
					}
			}
			if (PROD_SOURCE != null )
				break;
		}
	break;

	}
	
	return PROD_SOURCE;
	 
 }
 public void ShowQuality(int index) {
	 CurrentQuality = index;
	 switch (CurrentCategory) {
	 case 0:
		 lv_radio0.setChecked(true);
		 break;
	 case 1:
		 lv_radio1.setChecked(true);
		 break;
	 case 2:
		 lv_radio2.setChecked(true);
		 break;
	 }
	 
 }
	public void SelectQuality(int index) {
		mPlayer.pause();

		CurrentQuality = index;
		String PROD_SOURCE = null;

		ReturnProgramView.DOWN_URLS.URLS CurrentURLS = null;
		switch (CurrentCategory) {
		case 0:
			for (int k = 0; k < m_ReturnProgramView.movie.episodes[CurrentIndex].down_urls[CurrentSource].urls.length; k++) {
				if(m_ReturnProgramView.movie.episodes[CurrentIndex].down_urls[CurrentSource].urls[k].type.equalsIgnoreCase(Constant.quality_index[index])){
					CurrentURLS = m_ReturnProgramView.movie.episodes[CurrentIndex].down_urls[CurrentSource].urls[k];
					break;
				}
			}
			
			break;
		case 1:
			for (int k = 0; k < m_ReturnProgramView.tv.episodes[CurrentIndex].down_urls[CurrentSource].urls.length; k++) {
				if(m_ReturnProgramView.tv.episodes[CurrentIndex].down_urls[CurrentSource].urls[k].type.equalsIgnoreCase(Constant.quality_index[index])){
					CurrentURLS = m_ReturnProgramView.tv.episodes[CurrentIndex].down_urls[CurrentSource].urls[k];
					break;
				}
			}
			break;
		case 2:
			for (int k = 0; k < m_ReturnProgramView.show.episodes[CurrentIndex].down_urls[CurrentSource].urls.length; k++) {
				if(m_ReturnProgramView.show.episodes[CurrentIndex].down_urls[CurrentSource].urls[k].type.equalsIgnoreCase(Constant.quality_index[index])){
					CurrentURLS = m_ReturnProgramView.show.episodes[CurrentIndex].down_urls[CurrentSource].urls[k];
					break;
				}
			}
			
			break;

		}
		
		if (CurrentURLS != null && CurrentURLS.url != null)  {
					PROD_SOURCE = CurrentURLS.url.trim();
					app.CheckUrlIsValidFromServer(PROD_SOURCE,"1");
		}
		if (PROD_SOURCE != null)
			mPlayer.setContinueVideoPath(null,PROD_SOURCE);
	}
//	public void SetMediaPlayerControlBGGone() {
//		if (mAnchor != null) {
//			mAnchor.setBackgroundResource(0);
//			mTextView1.setVisibility(View.GONE);
//			mTextView2.setVisibility(View.GONE);
//		}
//	}

//	public void showDLNAButtom(boolean isShow){
//		if(mDlnaButton != null){
//			if(isShow){
//				mHandler.removeMessages(FADE_OUT);
//				mHandler.sendEmptyMessageDelayed(SHOW_DLNABUTTOM, 500);
//			}else{
//				mHandler.removeMessages(FADE_OUT);
//				mHandler.sendEmptyMessageDelayed(HIDE_DLNABUTTOM, 500);
//			}
//
//		}
//	}
	public void setDownloadRate(int rate){
		if(mTextViewDownloadRate != null)
			mTextViewDownloadRate.setText(Integer.toString(rate)+"kb/s");
	}
	public void setMediaPlayer(MediaPlayerControl player) {
		mPlayer = player;
		updatePausePlay();
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
//		if (mFileName != null)
//			mFileName.setText(mTitle);
	}
	public void DisableButtom(){
		mNextButton.setVisibility(View.INVISIBLE);
		mTextViewDownloadRate.setVisibility(View.INVISIBLE);
		mimageView33.setVisibility(View.INVISIBLE);
		mQualityButton.setVisibility(View.INVISIBLE);
		mSelectButton.setVisibility(View.INVISIBLE);
	}
	public void setSubName(String name) {
		this.mSubName = name;
	}
	private void ShowQuality(){
		
		lv_radio0.setVisibility(View.INVISIBLE);
		lv_radio1.setVisibility(View.INVISIBLE);
		lv_radio2.setVisibility(View.INVISIBLE);
		
		switch (CurrentCategory) {
		case 0:
			for(int i = 0; i<m_ReturnProgramView.movie.episodes[CurrentIndex].down_urls[CurrentSource].urls.length;i++){
				if(m_ReturnProgramView.movie.episodes[CurrentIndex].down_urls[CurrentSource].urls[i].type.equalsIgnoreCase("mp4"))
					lv_radio1.setVisibility(View.VISIBLE);
				
				if(m_ReturnProgramView.movie.episodes[CurrentIndex].down_urls[CurrentSource].urls[i].type.equalsIgnoreCase("flv"))
					lv_radio0.setVisibility(View.VISIBLE);
				
				if(m_ReturnProgramView.movie.episodes[CurrentIndex].down_urls[CurrentSource].urls[i].type.equalsIgnoreCase("hd2"))
					lv_radio0.setVisibility(View.VISIBLE);
			}
			break;
		case 1:
			for(int i = 0; i<m_ReturnProgramView.tv.episodes[CurrentIndex].down_urls[CurrentSource].urls.length;i++){
				if(m_ReturnProgramView.tv.episodes[CurrentIndex].down_urls[CurrentSource].urls[i].type.equalsIgnoreCase("mp4"))
					lv_radio1.setVisibility(View.VISIBLE);
				
				if(m_ReturnProgramView.tv.episodes[CurrentIndex].down_urls[CurrentSource].urls[i].type.equalsIgnoreCase("flv"))
					lv_radio0.setVisibility(View.VISIBLE);
				
				if(m_ReturnProgramView.tv.episodes[CurrentIndex].down_urls[CurrentSource].urls[i].type.equalsIgnoreCase("hd2"))
					lv_radio2.setVisibility(View.VISIBLE);
			}
			break;
		case 2:
			for(int i = 0; i<m_ReturnProgramView.show.episodes[CurrentIndex].down_urls[CurrentSource].urls.length;i++){
				if(m_ReturnProgramView.show.episodes[CurrentIndex].down_urls[CurrentSource].urls[i].type.equalsIgnoreCase("mp4"))
					lv_radio1.setVisibility(View.VISIBLE);
				
				if(m_ReturnProgramView.show.episodes[CurrentIndex].down_urls[CurrentSource].urls[i].type.equalsIgnoreCase("flv"))
					lv_radio0.setVisibility(View.VISIBLE);
				
				if(m_ReturnProgramView.show.episodes[CurrentIndex].down_urls[CurrentSource].urls[i].type.equalsIgnoreCase("hd2"))
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
				CurrentIndex = 0;
				if(mNextButton != null)
					mNextButton.setVisibility(View.INVISIBLE);
				if(mSelectButton != null)
					mSelectButton.setVisibility(View.INVISIBLE);
			} else if (m_ReturnProgramView.tv != null) {
				CurrentCategory = 1;
				if (mSubName != null) {
					this.mSubName = mSubName.replace("第", "");
					this.mSubName = mSubName.replace("集", "").trim();
				}
//				mFileName.setText(mTitle+"第" + m_ReturnProgramView.tv.episodes[CurrentIndex].name + "集");
				if (dataStruct != null) {
					for (int i = 0; i < m_ReturnProgramView.tv.episodes.length; i++) {
						if(mSubName.equalsIgnoreCase(m_ReturnProgramView.tv.episodes[i].name))
							CurrentIndex = i;
						dataStruct.add("第" + Integer.toString(i+1) + "集");
						String str = m_ReturnProgramView.tv.episodes[i].name;
					}
					groupAdapter.notifyDataSetChanged();
				}
			} else if (m_ReturnProgramView.show != null) {
				CurrentCategory = 2;
//				mFileName.setText(mTitle +"-"+m_ReturnProgramView.show.episodes[CurrentIndex].name);
				if (dataStruct != null) {
					for (int i = 0; i < m_ReturnProgramView.show.episodes.length; i++) {
						if(mSubName.equalsIgnoreCase(m_ReturnProgramView.show.episodes[i].name))
							CurrentIndex = i;
						dataStruct
								.add(m_ReturnProgramView.show.episodes[i].name);
					}

					groupAdapter.notifyDataSetChanged();
				}
			}
			ShowQuality();
		}
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
				mHiddenListener.onHidden();
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
//			int percent = mPlayer.getBufferPercentage();
//			mSeekBar.setSecondaryProgress(percent * 10);
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
//		show(sDefaultTimeout);
		if(mShowing)
			hide();
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
//			DLNAMODE = true;
			mPlayer.gotoDlnaVideoPlay();
			// doPauseResume();
			// show(sDefaultTimeout);
		}
	};

	private View.OnClickListener mReturnListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
//			if(DLNAMODE)
//				DLNAMODE = false;
//			else
				mPlayer.OnComplete();
		}
	};
	private View.OnClickListener mReduceListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int mLayout = mPlayer.GetCurrentVideoLayout();
			mLayout++;
			if (mLayout > VideoView.VIDEO_LAYOUT_ZOOM)
				mLayout = VideoView.VIDEO_LAYOUT_ORIGIN;
			mPlayer.setVideoLayout(mLayout, 0);
			if(mLayout == VideoView.VIDEO_LAYOUT_ZOOM)
				mReduceButton.setBackgroundResource(R.drawable.player_full);
			else 
				mReduceButton.setBackgroundResource(R.drawable.player_reduce);
		}
	};
	private View.OnClickListener mPreListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mPlayer.canSeekBackward()) {
				long current = mPlayer.getCurrentPosition();
				if (current >= 30000)// 30s
					mPlayer.seekTo(current - 30000);
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
				if(CurrentQuality != 0){
					SelectQuality(0);
				}
				break;
			case R.id.radio1:
				if(CurrentQuality != 1){
					SelectQuality(1);
				}
				break;
			case R.id.radio2:
				if(CurrentQuality != 2){
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

	private void updateBottomRight(){
		if (mViewBottomRight.getVisibility() == View.VISIBLE) 
			mViewBottomRight.setVisibility(View.GONE);
		else 
			mViewBottomRight.setVisibility(View.VISIBLE);
		mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT),
				sDefaultTimeout);
//		if (mRoot.getVisibility() == View.VISIBLE) {
//			mHandler.removeMessages(FADE_OUT);
//			
//
//			if (!mBottomRightShowing) {
//				mBottomRightShowing = true;
//				mWindowBottomRight.showAtLocation(mAnchor, Gravity.RIGHT
//						| Gravity.BOTTOM, 28, 82);
////				mWindowBottomRight.showAtLocation(mRoot.findViewById(R.id.imageButton5), Gravity.RIGHT
////						| Gravity.BOTTOM, 0, 0);
//
//			}
//			if (mViewBottomRight.getVisibility() == View.VISIBLE)
//				mViewBottomRight.setVisibility(View.GONE);
//			else
//				mViewBottomRight.setVisibility(View.VISIBLE);
//			
//			mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT),
//					sDefaultTimeout);
//		}
	}
	private void updateTopRight(){
		if (mViewTopRight.getVisibility() == View.VISIBLE) 
			mViewTopRight.setVisibility(View.GONE);
		else 
			mViewTopRight.setVisibility(View.VISIBLE);
		mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT),
				sDefaultTimeout);
//		if (mRoot.getVisibility() == View.VISIBLE) {
//			mHandler.removeMessages(FADE_OUT);
//			
//			if (!mTopRightShowing) {
//				mTopRightShowing = true;
//				 mWindowTopRight.showAtLocation(mAnchor, Gravity.RIGHT|Gravity.TOP, 25, 70);
////				mWindowTopRight.showAtLocation(mRoot.findViewById(R.id.imageButton6), Gravity.RIGHT
////						| Gravity.TOP, 0, 0);
//			}
//			if (mViewTopRight.getVisibility() == View.VISIBLE)
//				mViewTopRight.setVisibility(View.GONE);
//			else
//				mViewTopRight.setVisibility(View.VISIBLE);
//			
//			mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT),
//					sDefaultTimeout);
//		}
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
		if (mPlayer.isPlaying())
			mPlayer.pause();
		else
			mPlayer.start();
		updatePausePlay();
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
			if (mInstantSeeking)
				mPlayer.seekTo(newposition);
			if (mInfoView != null)
				mInfoView.setText(time);
			if (mCurrentTime != null)
				mCurrentTime.setText(time);
		}

		@Override
		public void onStopTrackingTouch(SeekBar bar) {
			if (!mInstantSeeking)
				mPlayer.seekTo((mDuration * bar.getProgress()) / 1000);
			if (mInfoView != null) {
				mInfoView.setText("");
				mInfoView.setVisibility(View.GONE);
			}
			show(sDefaultTimeout);
			mHandler.removeMessages(SHOW_PROGRESS);
			mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
			mDragging = false;
			mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
//			mSeekBar.setMax( duration);
//			mSeekBar.setProgress(position);
//			mPlayer.seekTo((mDuration * bar.getProgress()) / 1000);
		}
	};

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
				
				if(CurrentIndex == position)
					convertView.setBackgroundColor(0xC57627);
				holder = new ViewHolder();

				convertView.setTag(holder);

				holder.groupItem = (TextView) convertView
						.findViewById(R.id.txt_video_caption);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.groupItem.setText(list.get(position));

			return convertView;
		}

		/**
		 * 检查urlLink文本是否正常
		 * @param urlLink
		 * @return
		 */
		private boolean CheckUrl(String urlLink) {
			
			//url本身不正常 直接返回
			   if (urlLink == null || urlLink.length() <= 0) {     
				   
				    return false;                   
				  }   else {
					  
					  if(!URLUtil.isValidUrl(urlLink)) {
						  
						  return false;
					  }
				  }
			   
			   return true;
		}
		
		/**
		 * 启动一个异步任务，把网络相关放在此任务中
		 * 重定向新的链接，直到拿到资源URL
		 * 
		 * 注意：因为网络或者服务器原因，重定向时间有可能比较长
		 * 因此需要较长时间等待
		 * @param url
		 * @return 字符串
		 */
		private String newATask(String url) {
			
			AsyncTask<String,Void,String> aynAsyncTask = new AsyncTask<String, Void, String>(){

				@Override
				protected String doInBackground(String... params) {
					// TODO Auto-generated method stub
					
					List<String> list = new ArrayList<String>();
					String dstUrl = null;
					try {
						simulateFirfoxRequest(Constant.USER_AGENT_IOS,params[0] ,list);//使用递归，并把得到的链接放在集合中，取最后一次得到的链接即可
						
						dstUrl = list.get(list.size() - 1);
						if(BuildConfig.DEBUG) Log.i(TAG, "AsyncTask----->>URL : " + dstUrl);
						list.clear();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						if(BuildConfig.DEBUG) Log.i(TAG, "TimeOut!!!!!! : " + e);
						e.printStackTrace();
					}
					
					return dstUrl;
				}
				
			}.execute(url);
			try {
				String redirectUrl = aynAsyncTask.get();//从异步任务中获取结果
				
				return redirectUrl;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
		
		/**
		 * 模拟火狐浏览器给服务器发送不同请求，有火狐本身请求，IOS请求，Android请求
		 * @param userAgent firfox ios android
		 * @param srcUrl 原始地址【可能可以播放，可能需要跳转】
		 * @param list 存储播放地址
		 */
		private void simulateFirfoxRequest(String userAgent,String srcUrl , List<String> list) {
			//模拟火狐ios发用请求  使用userAgent
			AndroidHttpClient mAndroidHttpClient = AndroidHttpClient.newInstance(userAgent);
			
			HttpParams httpParams =  mAndroidHttpClient.getParams();
			//连接时间最长3秒，可以更改
			HttpConnectionParams.setConnectionTimeout(httpParams, 3000 * 1);
					
			try {
				URL url = new URL(srcUrl);
				HttpGet mHttpGet = new HttpGet(url.toURI());
				HttpResponse response = mAndroidHttpClient.execute(mHttpGet);
				
				//限定连接时间
				
				StatusLine statusLine = response.getStatusLine();
				int status = statusLine.getStatusCode();
				
				if(BuildConfig.DEBUG) Log.i(TAG, "HTTP STATUS : " + status);
				
				//如果拿到资源直接返回url  如果没有拿到资源，并且要进行跳转,那就使用递归跳转
				if(status != HttpStatus.SC_OK) {
					if(BuildConfig.DEBUG) Log.i(TAG, "NOT OK   start");
					
					if(BuildConfig.DEBUG) Log.i(TAG, "NOT OK start");
					if(status == HttpStatus.SC_MOVED_PERMANENTLY ||//网址被永久移除
							status == HttpStatus.SC_MOVED_TEMPORARILY ||//网址暂时性移除
							status ==HttpStatus.SC_SEE_OTHER ||//重新定位资源
							status == HttpStatus.SC_TEMPORARY_REDIRECT) {//暂时定向
						
						Header header = response.getFirstHeader("Location");//拿到重新定位后的header
						String location = header.getValue();//从header重新取出信息
						list.add(location);
						
						mAndroidHttpClient.close();//关闭此次连接
						
						if(BuildConfig.DEBUG) Log.i(TAG, "Location: " + location);
						//进行下一次递归
						simulateFirfoxRequest(userAgent,location , list);
					} else {
						//如果地址真的不存在，那就往里面加NULL字符串
						list.add("NULL");
					}
					
				} else {
					list.add(srcUrl);
					mAndroidHttpClient.close();
				}
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				if(BuildConfig.DEBUG) Log.i(TAG, "NOT OK" + e);
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
		
		 void setContinueVideoPath(String Title, String path);

	}

}
