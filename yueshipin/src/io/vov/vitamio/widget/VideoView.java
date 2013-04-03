/*
 * Copyright (C) 2011 VOV IO (http://vov.io/)
 */

package io.vov.vitamio.widget;

import io.vov.utils.Log;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnSeekCompleteListener;
import io.vov.vitamio.MediaPlayer.OnSubtitleUpdateListener;
import io.vov.vitamio.MediaPlayer.OnVideoSizeChangedListener;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.dlcs.dlna.Stack.MediaRenderer;
import com.joyplus.App;
import com.joyplus.BuildConfig;
import com.joyplus.Constant;
import com.joyplus.R;
import com.joyplus.Dlna.DlnaSelectDevice;
import com.joyplus.Dlna.DlnaVideoPlay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * Displays a video file. The VideoView class can load images from various
 * sources (such as resources or content providers), takes care of computing its
 * measurement from the video so that it can be used in any layout manager, and
 * provides various display options such as scaling and tinting.
 * 
 * VideoView also provide many wrapper methods for
 * {@link io.vov.vitamio.MediaPlayer}, such as {@link #getVideoWidth()},
 * {@link #setSubShown(boolean)}
 */
public class VideoView extends SurfaceView implements MediaController.MediaPlayerControl {
	private String TAG = "VideoView";
	private App app;
	private Uri mUri;
	private String mTitle;
	private long mDuration;

	private static final int STATE_ERROR = -1;
	private static final int STATE_IDLE = 0;
	private static final int STATE_PREPARING = 1;
	private static final int STATE_PREPARED = 2;
	private static final int STATE_PLAYING = 3;
	private static final int STATE_PAUSED = 4;
	private static final int STATE_PLAYBACK_COMPLETED = 5;
	private static final int STATE_SUSPEND = 6;
	private static final int STATE_RESUME = 7;
	private static final int STATE_SUSPEND_UNSUPPORTED = 8;

	private int mCurrentState = STATE_IDLE;
	private int mTargetState = STATE_IDLE;

	private float mAspectRatio = 0;
	private int mVideoLayout = VIDEO_LAYOUT_SCALE;
	public static final int VIDEO_LAYOUT_ORIGIN = 0;
	public static final int VIDEO_LAYOUT_SCALE = 1;
	public static final int VIDEO_LAYOUT_STRETCH = 2;
	public static final int VIDEO_LAYOUT_ZOOM = 3;

	private SurfaceHolder mSurfaceHolder = null;
	private MediaPlayer mMediaPlayer = null;
	private int mVideoWidth;
	private int mVideoHeight;
	private float mVideoAspectRatio;
	private int mSurfaceWidth;
	private int mSurfaceHeight;
	private MediaController mMediaController;
	private OnCompletionListener mOnCompletionListener;
	private OnPreparedListener mOnPreparedListener;
	private OnErrorListener mOnErrorListener;
	private OnSeekCompleteListener mOnSeekCompleteListener;
	private OnSubtitleUpdateListener mOnSubtitleUpdateListener;
	private OnInfoListener mOnInfoListener;
	private OnBufferingUpdateListener mOnBufferingUpdateListener;
	private int mCurrentBufferPercentage;
	private long mSeekWhenPrepared;
	private long mSeekTime = 0;
	private boolean mCanPause = true;
	private boolean mCanSeekBack = true;
	private boolean mCanSeekForward = true;
	private boolean CONTINUEMODE = false;
	private Context mContext;
	
	private View mLayoutBG;
	private DlnaSelectDevice mMyService;
	private AlertDialog alert = null;
	
	private long[] mRecordTime = {0,0,0,0,0,
													  0,0,0,0,0,
													  0,0,0,0,0,
													  0,0,0,0,0,
													  0,0,0,0,0,
													  0,0,0,0,0};//创建一个长度为30的数组，数组初始值为0
	
	private int mCount = -1;//用来记录次数
	public  static long mSeekTotime = 0;//定义一个全局的播放记录的时间

	public VideoView(Context context) {
		super(context);
		initVideoView(context);
	}

	public VideoView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public VideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initVideoView(context);
	}
	
	

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
		int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
		setMeasuredDimension(width, height);
	}

	/**
	 * Set the display options
	 * 
	 * @param layout <ul>
	 * <li>{@link #VIDEO_LAYOUT_ORIGIN}
	 * <li>{@link #VIDEO_LAYOUT_SCALE}
	 * <li>{@link #VIDEO_LAYOUT_STRETCH}
	 * <li>{@link #VIDEO_LAYOUT_ZOOM}
	 * </ul>
	 * @param aspectRatio video aspect ratio, will audo detect if 0.
	 */
	public void setVideoLayout(int layout, float aspectRatio) {
		LayoutParams lp = getLayoutParams();
		DisplayMetrics disp = mContext.getResources().getDisplayMetrics();
		int windowWidth = disp.widthPixels, windowHeight = disp.heightPixels;
		float windowRatio = windowWidth / (float) windowHeight;
		float videoRatio = aspectRatio <= 0.01f ? mVideoAspectRatio : aspectRatio;
		mSurfaceHeight = mVideoHeight;
		mSurfaceWidth = mVideoWidth;
		if (VIDEO_LAYOUT_ORIGIN == layout && mSurfaceWidth < windowWidth && mSurfaceHeight < windowHeight) {
			lp.width = (int) (mSurfaceHeight * videoRatio);
			lp.height = mSurfaceHeight;
		} else if (layout == VIDEO_LAYOUT_ZOOM) {
			lp.width = windowRatio > videoRatio ? windowWidth : (int) (videoRatio * windowHeight);
			lp.height = windowRatio < videoRatio ? windowHeight : (int) (windowWidth / videoRatio);
		} else {
			boolean full = layout == VIDEO_LAYOUT_STRETCH;
			lp.width = (full || windowRatio < videoRatio) ? windowWidth : (int) (videoRatio * windowHeight);
			lp.height = (full || windowRatio > videoRatio) ? windowHeight : (int) (windowWidth / videoRatio);

		}
		setLayoutParams(lp);
		getHolder().setFixedSize(mSurfaceWidth, mSurfaceHeight);
		Log.d("VIDEO: %dx%dx%f, Surface: %dx%d, LP: %dx%d, Window: %dx%dx%f", mVideoWidth, mVideoHeight, mVideoAspectRatio, mSurfaceWidth, mSurfaceHeight, lp.width, lp.height, windowWidth, windowHeight, windowRatio);
		mVideoLayout = layout;
		mAspectRatio = aspectRatio;
	}

	private void initVideoView(Context ctx) {
		mSeekTotime = 0;
		mContext = ctx;
		mVideoWidth = 0;
		mVideoHeight = 0;
		getHolder().addCallback(mSHCallback);
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
		mCurrentState = STATE_IDLE;
		mTargetState = STATE_IDLE;
		if (ctx instanceof Activity)
			((Activity) ctx).setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	public boolean isValid() {
		return (mSurfaceHolder != null && mSurfaceHolder.getSurface().isValid());
	}
	public void setApp(App app){
		this.app = app;
	}
	public void setVideoPath(String path) {
			Uri uri = Uri.parse(path);
			setVideoURI(uri);
	}

	public void setVideoURI(Uri uri) {
		mUri = uri;
		mSeekWhenPrepared = 0;
				
		openVideo();
		requestLayout();
		invalidate();
		

	}
	public void setTitle(String name) {
		mTitle = name;
	}

	public void stopPlayback() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
			mCurrentState = STATE_IDLE;
			mTargetState = STATE_IDLE;
		}
	}

	private void openVideo() {
		if (mUri == null || mSurfaceHolder == null)
			return;

		Intent i = new Intent("com.android.music.musicservicecommand");
		i.putExtra("command", "pause");
		mContext.sendBroadcast(i);

		release(false);
		try {
			mDuration = -1;
			mCurrentBufferPercentage = 0;
//			mMediaPlayer = new MediaPlayer(mContext,true);
			mMediaPlayer = new MediaPlayer(mContext);
			mMediaPlayer.setOnPreparedListener(mPreparedListener);
			mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
			mMediaPlayer.setOnCompletionListener(mCompletionListener);
			mMediaPlayer.setOnErrorListener(mErrorListener);
			mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
			mMediaPlayer.setOnInfoListener(mInfoListener);
			mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
			mMediaPlayer.setOnSubtitleUpdateListener(mSubtitleUpdateListener);
			mMediaPlayer.setDataSource(mContext, mUri);
			mMediaPlayer.setDisplay(mSurfaceHolder);
			mMediaPlayer.setScreenOnWhilePlaying(true);
			mMediaPlayer.prepareAsync();
			mCurrentState = STATE_PREPARING;
			attachMediaController();
		} catch (IOException ex) {
			Log.e("Unable to open content: " + mUri, ex);
			mCurrentState = STATE_ERROR;
			mTargetState = STATE_ERROR;
			mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
			return;
		} catch (IllegalArgumentException ex) {
			Log.e("Unable to open content: " + mUri, ex);
			mCurrentState = STATE_ERROR;
			mTargetState = STATE_ERROR;
			mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
			return;
		}
	}

	public void setMediaController(MediaController controller) {
		if (mMediaController != null)
			mMediaController.hide();
		mMediaController = controller;
		attachMediaController();
	}

	public void setLayoutBG(View mRelativeLayoutBG) {
		this.mLayoutBG = mRelativeLayoutBG;
		
	}
	
	public void setServiceConnection(DlnaSelectDevice mMyService) {
		this.mMyService = mMyService;
	}

	private void attachMediaController() {

		if (mMediaPlayer != null && mMediaController != null) {
			mMediaController.setMediaPlayer(this);
			if (!CONTINUEMODE) {
				View anchorView = this.getParent() instanceof View ? (View) this
						.getParent() : this;

				mMediaController.setAnchorView(anchorView);
				
				if(URLUtil.isNetworkUrl(mUri.toString())){
					mMediaController.ShowCurrentPlayData(app.getCurrentPlayData());
					mMediaController.setProd_Data(app.get_ReturnProgramView());
					mMediaController.setVideoSource();
				}
				
			}
			mMediaController.setEnabled(isInPlaybackState());
			if(!URLUtil.isNetworkUrl(mUri.toString()))
				mMediaController.DisableButtom();

			// if (mUri != null) {
			// List<String> paths = mUri.getPathSegments();
			// String name = paths == null || paths.isEmpty() ? "null" :
			// paths.get(paths.size() - 1);
			// mMediaController.setFileName(name);
			// }
		}
		// if (mLayoutBG != null){
		// mLayoutBG.setVisibility(View.VISIBLE);
		// View anchorView = this.getParent() instanceof View ? (View)
		// this.getParent() : this;
		//
		// ViewGroup.LayoutParams lp = anchorView.getLayoutParams();
		// //
		// // lp.width = (int)
		// (findViewById(R.id.operation_full).getLayoutParams().width *
		// lpa.screenBrightness);
		// mLayoutBG.setLayoutParams(lp);
		// }

	}

	OnVideoSizeChangedListener mSizeChangedListener = new OnVideoSizeChangedListener() {
		@Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
			Log.d("onVideoSizeChanged: (%dx%d)", width, height);
			mVideoWidth = mp.getVideoWidth();
			mVideoHeight = mp.getVideoHeight();
			mVideoAspectRatio = mp.getVideoAspectRatio();
			if (mVideoWidth != 0 && mVideoHeight != 0)
				setVideoLayout(mVideoLayout, mAspectRatio);
		}
	};

	OnPreparedListener mPreparedListener = new OnPreparedListener() {
		@Override
    public void onPrepared(MediaPlayer mp) {
			Log.d("onPrepared");
			mCurrentState = STATE_PREPARED;
			mTargetState = STATE_PLAYING;

			if (mOnPreparedListener != null)
				mOnPreparedListener.onPrepared(mMediaPlayer);
			if (mMediaController != null)
				mMediaController.setEnabled(true);
			mVideoWidth = mp.getVideoWidth();
			mVideoHeight = mp.getVideoHeight();
			mVideoAspectRatio = mp.getVideoAspectRatio();

			long seekToPosition = mSeekWhenPrepared;

			if (seekToPosition != 0)
				seekTo(seekToPosition);
			if(mSeekTime != 0){
				seekTo(mSeekTime);
				mSeekTime = 0;
			}
			if (mVideoWidth != 0 && mVideoHeight != 0) {
				setVideoLayout(mVideoLayout, mAspectRatio);
				if (mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
					if (mTargetState == STATE_PLAYING) {
						start();
						if (mLayoutBG != null)
							mLayoutBG.setVisibility(View.GONE);
						if (mMediaController != null)
							mMediaController.show();
						
					} else if (!isPlaying() && (seekToPosition != 0 || getCurrentPosition() > 0)) {
						if (mMediaController != null)
							mMediaController.show(0);
					}
				}
			} else if (mTargetState == STATE_PLAYING) {
				start();
			}
		}
	};

	private OnCompletionListener mCompletionListener = new OnCompletionListener() {
		@Override
    public void onCompletion(MediaPlayer mp) {
			Log.d("onCompletion");

			   if(mMediaController != null) {
				   
				   //播放资源有问题的代码 处理
				   long maxTime = -1;//获取数组中的最大值
				   //获取数组中的最大值
				   for(int i=0;i<mRecordTime.length;i++) {
					   
					   maxTime = Math.max(maxTime, mRecordTime[i]);
				   }
				   
				   
				   if(maxTime > mSeekTotime) {//如果临时获取的时间大于前一次播放的时间
					   //把临时获取的时间赋给前一次播放的时间
					   mSeekTotime = maxTime;
				   }
				   
				   //视屏意外播放完成后，重新播放判断代码
				   //前一段的时间
				   if(maxTime < getDuration() && mSeekTotime != 0  && mSeekTotime < getDuration() - 5*1000 ) {
					   
					   mSeekTotime += 1 * 1000;//并且时间增加1秒
					   seekTo(mSeekTotime);//跳转到下一个正常播放
					   return;
				   }
				   
				   
				   //自动播放到下一集的代码
				   if(mMediaController.getCurrentCategory() == 1) {
					   
					   if(mMediaController.getCurrentIndex() >= 0) {
						   
						   if(app.get_ReturnProgramView() != null) {
							   
							   int maxTVNum = app.get_ReturnProgramView().tv.episodes.length;
							   
							   if(mMediaController.getCurrentIndex() <maxTVNum) {
								   
								   mMediaController.OnClickSelect(mMediaController.getCurrentIndex() + 1);
								   
								   return ;
							   }
						   }
					   }
				   }
			   }
			   
				mCurrentState = STATE_PLAYBACK_COMPLETED;
				mTargetState = STATE_PLAYBACK_COMPLETED;
				if (mMediaController != null)
					mMediaController.hide();
				if (mOnCompletionListener != null)
					mOnCompletionListener.onCompletion(mMediaPlayer);
			    
		}
	};

	private OnErrorListener mErrorListener = new OnErrorListener() {
		@Override
    public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
			Log.d("Error: %d, %d", framework_err, impl_err);
			mCurrentState = STATE_ERROR;
			mTargetState = STATE_ERROR;
			if (mMediaController != null)
				mMediaController.hide();

			if (mOnErrorListener != null) {
				if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err))
					return true;
			}

			if (getWindowToken() != null) {
				int message = framework_err == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK ? R.string.VideoView_error_text_invalid_progressive_playback : R.string.addressnotwork;
				
				new AlertDialog.Builder(mContext).setTitle(R.string.netstate).setMessage(message).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
          public void onClick(DialogInterface dialog, int whichButton) {
						OnComplete();
//						if (mOnCompletionListener != null)
//							mOnCompletionListener.onCompletion(mMediaPlayer);
					}
				}).setCancelable(false).show();
			}
			return true;
		}
	};

	private OnBufferingUpdateListener mBufferingUpdateListener = new OnBufferingUpdateListener() {
		@Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
			mCurrentBufferPercentage = percent;
			if (mOnBufferingUpdateListener != null)
				mOnBufferingUpdateListener.onBufferingUpdate(mp, percent);
		}
	};
	
	/**
	 * 在infolistener里面存储播放时间
	 * @param currentTime 当前播放时间
	 */
	private void store30PlayTime(long currentTime) {
		//如果数组的最末一位为0，计数自动加 1
		if(mRecordTime[mRecordTime.length -1] == 0) {
			mCount ++;
		}
		
		//如果数组最末一位不为0，计数初始化为-1，并且
		//对把数组第一位数据删除，把新数据添加到数组的末尾
		if(mRecordTime[mRecordTime.length -1] != 0) {
			mCount = -1;
			
			for(int i=0;i<mRecordTime.length - 1;i++) {
				
				mRecordTime[i] = mRecordTime[i + 1];
			}
			
			mRecordTime[mRecordTime.length -1] = currentTime;
		} else {
			//如果数组最末一位为0，把最新数据保存到当前计数所在的位置
			mRecordTime[mCount] = currentTime;
		}
	}

	private OnInfoListener mInfoListener = new OnInfoListener() {
		@Override
		public boolean onInfo(MediaPlayer mp, int what, int extra) {
			
			if(BuildConfig.DEBUG) android.util.Log.i("VideoViewYangzhg", "state : " + what);
			store30PlayTime(getCurrentPosition());//当状态改变时，保存那一段时间到数组里面，并随时间更新而更新
			
			if (mOnInfoListener != null) {
				mOnInfoListener.onInfo(mp, what, extra);
			} else if (mMediaPlayer != null) {
				if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START)
					mMediaPlayer.pause();
				else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END)
					mMediaPlayer.start();
				else if (what == MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED && mMediaController.isShowing())
					mMediaController.setDownloadRate(extra);
				else 
					Log.d("onInfo: (%d, %d)", what, extra);
				//强制播放拖动地点
				if(what == MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED && 
						!mMediaController.ismIsPausedByHuman()) {
					
					mMediaPlayer.start();
				}
			}
			return true;
		}
	};

	private OnSeekCompleteListener mSeekCompleteListener = new OnSeekCompleteListener() {
		@Override
		public void onSeekComplete(MediaPlayer mp) {
			Log.d("onSeekComplete");
			if (mOnSeekCompleteListener != null)
				mOnSeekCompleteListener.onSeekComplete(mp);
		}
	};

	private OnSubtitleUpdateListener mSubtitleUpdateListener = new OnSubtitleUpdateListener() {
		@Override
		public void onSubtitleUpdate(byte[] pixels, int width, int height) {
			Log.i("onSubtitleUpdate: bitmap subtitle, %dx%d", width, height);
			if (mOnSubtitleUpdateListener != null)
				mOnSubtitleUpdateListener.onSubtitleUpdate(pixels, width, height);
		}

		@Override
		public void onSubtitleUpdate(String text) {
			Log.i("onSubtitleUpdate: %s", text);
			if (mOnSubtitleUpdateListener != null)
				mOnSubtitleUpdateListener.onSubtitleUpdate(text);
		}
	};

	public void setOnPreparedListener(OnPreparedListener l) {
		mOnPreparedListener = l;
	}

	public void setOnCompletionListener(OnCompletionListener l) {
		mOnCompletionListener = l;
	}

	public void setOnErrorListener(OnErrorListener l) {
		mOnErrorListener = l;
	}

	public void setOnBufferingUpdateListener(OnBufferingUpdateListener l) {
		mOnBufferingUpdateListener = l;
	}

	public void setOnSeekCompleteListener(OnSeekCompleteListener l) {
		mOnSeekCompleteListener = l;
	}

	public void setOnSubtitleUpdateListener(OnSubtitleUpdateListener l) {
		mOnSubtitleUpdateListener = l;
	}

	public void setOnInfoListener(OnInfoListener l) {
		mOnInfoListener = l;
	}

	SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
		@Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
			mSurfaceWidth = w;
			mSurfaceHeight = h;
			boolean isValidState = (mTargetState == STATE_PLAYING);
			boolean hasValidSize = (mVideoWidth == w && mVideoHeight == h);
			if (mMediaPlayer != null && isValidState && hasValidSize) {
				if (mSeekWhenPrepared != 0)
					seekTo(mSeekWhenPrepared);
				start();
				if (mMediaController != null) {
					if (mMediaController.isShowing())
						mMediaController.hide();
					mMediaController.show();
				}
			}
		}

		@Override
    public void surfaceCreated(SurfaceHolder holder) {
			mSurfaceHolder = holder;
			if (mMediaPlayer != null && mCurrentState == STATE_SUSPEND && mTargetState == STATE_RESUME) {
				mMediaPlayer.setDisplay(mSurfaceHolder);
				resume();
			} else {
				openVideo();
			}
		}

		@Override
    public void surfaceDestroyed(SurfaceHolder holder) {
			mSurfaceHolder = null;
			if (mMediaController != null)
				mMediaController.hide();
			if (mCurrentState != STATE_SUSPEND)
				release(true);
		}
	};

	private void release(boolean cleartargetstate) {
		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
			mCurrentState = STATE_IDLE;
			if (cleartargetstate)
				mTargetState = STATE_IDLE;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
//		boolean is = isInPlaybackState();
//		if (isInPlaybackState() && mMediaController != null)
//			toggleMediaControlsVisiblity();
		if (mMediaController.isShowing()) {
			mMediaController.hide();
		} else {
			mMediaController.show();
		}
		return false;
	}

	@Override
	public boolean onTrackballEvent(MotionEvent ev) {
		if (isInPlaybackState() && mMediaController != null)
			toggleMediaControlsVisiblity();
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK && keyCode != KeyEvent.KEYCODE_VOLUME_UP && keyCode != KeyEvent.KEYCODE_VOLUME_DOWN && keyCode != KeyEvent.KEYCODE_MENU && keyCode != KeyEvent.KEYCODE_CALL && keyCode != KeyEvent.KEYCODE_ENDCALL;
		if (isInPlaybackState() && isKeyCodeSupported && mMediaController != null) {
			if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE) {
				if (mMediaPlayer.isPlaying()) {
					pause();
					mMediaController.show();
				} else {
					start();
					mMediaController.hide();
				}
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP && mMediaPlayer.isPlaying()) {
				pause();
				mMediaController.show();
			} else {
				toggleMediaControlsVisiblity();
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	private void toggleMediaControlsVisiblity() {
		if (mMediaController.isShowing()) {
			mMediaController.hide();
		} else {
			mMediaController.show();
		}
	}

	@Override
  public void start() {
		if (isInPlaybackState()) {
			mMediaPlayer.start();
			mCurrentState = STATE_PLAYING;
		}
		mTargetState = STATE_PLAYING;
		
	}

	@Override
  public void pause() {
		if (isInPlaybackState()) {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.pause();
				mCurrentState = STATE_PAUSED;
			}
		}
		mTargetState = STATE_PAUSED;
	}

	public void suspend() {
		if (isInPlaybackState()) {
			release(false);
			mCurrentState = STATE_SUSPEND_UNSUPPORTED;
			Log.d("Unable to suspend video. Release MediaPlayer.");
		}
	}

	public void resume() {
		if (mSurfaceHolder == null && mCurrentState == STATE_SUSPEND) {
			mTargetState = STATE_RESUME;
		} else if (mCurrentState == STATE_SUSPEND_UNSUPPORTED) {
			openVideo();
		}
	}

	@Override
  public long getDuration() {
		if (isInPlaybackState()) {
			if (mDuration > 0)
				return mDuration;
			mDuration = mMediaPlayer.getDuration();
			return mDuration;
		}
		mDuration = -1;
		return mDuration;
	}

	@Override
  public long getCurrentPosition() {
		if (isInPlaybackState())
			return mMediaPlayer.getCurrentPosition();
		return 0;
	}

	@Override
  public void seekTo(long msec) {
		if (isInPlaybackState()) {
			mMediaPlayer.seekTo(msec);
			mSeekWhenPrepared = 0;
		} else {
			mSeekWhenPrepared = msec;
		}
	}
	  public void JumpTo(long msec) {
			mSeekTime = msec;
		}

	@Override
  public boolean isPlaying() {
		return isInPlaybackState() && mMediaPlayer.isPlaying();
	}

	@Override
  public int getBufferPercentage() {
		if (mMediaPlayer != null)
			return mCurrentBufferPercentage;
		return 0;
	}

	public void setVolume(float leftVolume, float rightVolume) {
		if (mMediaPlayer != null)
			mMediaPlayer.setVolume(leftVolume, rightVolume);
	}

	public int getVideoWidth() {
		return mVideoWidth;
	}

	public int getVideoHeight() {
		return mVideoHeight;
	}

	public float getVideoAspectRatio() {
		return mVideoAspectRatio;
	}

	public void setVideoQuality(int quality) {
		if (mMediaPlayer != null)
			mMediaPlayer.setVideoQuality(quality);
	}

	public void setBufferSize(int bufSize) {
		if (mMediaPlayer != null)
			mMediaPlayer.setBufferSize(bufSize);
	}

	public boolean isBuffering() {
		if (mMediaPlayer != null)
			return mMediaPlayer.isBuffering();
		return false;
	}

	public void setMetaEncoding(String encoding) {
		if (mMediaPlayer != null)
			mMediaPlayer.setMetaEncoding(encoding);
	}

	public String getMetaEncoding() {
		if (mMediaPlayer != null)
			return mMediaPlayer.getMetaEncoding();
		return null;
	}

	public HashMap<String, Integer> getAudioTrackMap(String encoding) {
		if (mMediaPlayer != null)
			return mMediaPlayer.getAudioTrackMap(encoding);
		return null;
	}

	public int getAudioTrack() {
		if (mMediaPlayer != null)
			return mMediaPlayer.getAudioTrack();
		return -1;
	}

	public void setAudioTrack(int audioIndex) {
		if (mMediaPlayer != null)
			mMediaPlayer.setAudioTrack(audioIndex);
	}

	public void setSubShown(boolean shown) {
		if (mMediaPlayer != null)
			mMediaPlayer.setSubShown(shown);
	}

	public void setSubEncoding(String encoding) {
		if (mMediaPlayer != null)
			mMediaPlayer.setSubEncoding(encoding);
	}

	public int getSubLocation() {
		if (mMediaPlayer != null)
			return mMediaPlayer.getSubLocation();
		return -1;
	}

	public void setSubPath(String subPath) {
		if (mMediaPlayer != null)
			mMediaPlayer.setSubPath(subPath);
	}

	public String getSubPath() {
		if (mMediaPlayer != null)
			return mMediaPlayer.getSubPath();
		return null;
	}

	public void setSubTrack(int trackId) {
		if (mMediaPlayer != null)
			mMediaPlayer.setSubTrack(trackId);
	}

	public int getSubTrack() {
		if (mMediaPlayer != null)
			return mMediaPlayer.getSubTrack();
		return -1;
	}

	public HashMap<String, Integer> getSubTrackMap(String encoding) {
		if (mMediaPlayer != null)
			return mMediaPlayer.getSubTrackMap(encoding);
		return null;
	}

	protected boolean isInPlaybackState() {
		return (mMediaPlayer != null && mCurrentState != STATE_ERROR && mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING);
	}

	@Override
  public boolean canPause() {
		return mCanPause;
	}

	@Override
  public boolean canSeekBackward() {
		return mCanSeekBack;
	}

	@Override
  public boolean canSeekForward() {
		return mCanSeekForward;
	}
	@Override
	public void gotoDlnaVideoPlay() {
		if(android.os.Build.VERSION.SDK_INT>=14)
		{
			if (mMyService != null) {
				ArrayList<MediaRenderer> mDmrCache = mMyService.getDmrCache();
				if (mDmrCache.size() >= 0) {
					CharSequence[] items = new String[mDmrCache.size() + 1];
					items[0] = "我的设备";
					for (int i = 0; i < mDmrCache.size(); i++)
						items[i + 1] = mDmrCache.get(i).friendlyName;

					
					AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
					builder.setTitle("请选择你的设备：");
					builder.setSingleChoiceItems(items, 0,new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							if (item > 0) {
								ArrayList<MediaRenderer> mDmrCache = mMyService
										.getDmrCache();
								MediaRenderer mMediaRenderer = mDmrCache
										.get(item - 1);
								mMyService.SetCurrentDevice(item);
								if (mMediaRenderer != null) {
									alert.dismiss();
									gotoDlnaVideoPlay2();
								}
							}
						}
					});
					alert = builder.create();
					Window window = alert.getWindow();
					WindowManager.LayoutParams lp = window.getAttributes();
					lp.alpha = 0.6f;
					window.setAttributes(lp);
					alert.show();
					
				}
			}
		}
	
		// else {
		// AlertDialog alertDialog = new
		// AlertDialog.Builder(mContext).setMessage(
		// "正在搜索设备 ...").create();
		// Window window = alertDialog.getWindow();
		// WindowManager.LayoutParams lp = window.getAttributes();
		// lp.alpha = 0.6f;
		// window.setAttributes(lp);
		// alertDialog.show();
		// }
	}
	
	private void gotoDlnaVideoPlay2() {
		if(android.os.Build.VERSION.SDK_INT>=14)
		{
			Intent intent = new Intent(mContext, DlnaVideoPlay.class);
			intent.putExtra("prod_url", mUri.toString());
			intent.putExtra("title", mTitle);

			try {
				mContext.startActivity(intent);
			} catch (ActivityNotFoundException ex) {
				Log.e(TAG, "Call DlnaVideoPlay failed", ex);
			}
		}
		
	}
	@Override
	public int GetCurrentVideoLayout() {
		return mVideoLayout;
	}

	@Override
	public void setContinueVideoPath(String Title, String path,boolean PlayContinue){
		// TODO Auto-generated method stub
		CONTINUEMODE = true;
		String mPath = null;
		long saveTime = getCurrentPosition();
		if (mLayoutBG != null){
			if(Title != null && Title.length() >0){
				TextView mTextView1 = (TextView) mLayoutBG.findViewById(R.id.mediacontroller_file_name);
				mTextView1.setText(Title);
			}
			mLayoutBG.setVisibility(View.VISIBLE);
		}
		app.CheckUrlIsValidFromServer(path,"1");
//		setVideoPath(path);
		if( app.getURLPath() != null && app.getURLPath().length() >0)
			mPath = app.getURLPath();
		else 
			mPath = path;
		setVideoPath(mPath);
		if(PlayContinue)
			seekTo(saveTime);
	}
	
	@Override
	public void OnComplete() {
		((Activity) mContext).finish();
		
	}

}
