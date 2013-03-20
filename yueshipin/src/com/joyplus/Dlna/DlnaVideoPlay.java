package com.joyplus.Dlna;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.androidquery.AQuery;
import com.dlcs.dlna.Mrcp;
import com.dlcs.dlna.Util;
import com.dlcs.dlna.Stack.MediaRenderer;
import com.dlcs.dlna.Util.MediaInfo;
import com.joyplus.App;
import com.joyplus.Constant;
import com.joyplus.R;
import com.joyplus.Dlna.DlnaSelectDevice.ServiceClient;
import com.umeng.analytics.MobclickAgent;

public class DlnaVideoPlay extends Activity implements ServiceClient {
	private String TAG = "DlnaVideoPlay";
	private App app;
	private AQuery aq;
	private boolean isPlaying;
	private SeekBar sb;
	private int Duration;

	private String prod_url = null;
	private String prod_name = null;
	
	private  int current_item = 0;
	private  int current_mediaRenderer = 0;
	private ArrayList<MediaRenderer> mDmrCache = new ArrayList<MediaRenderer>();
	private MediaRenderer mMediaRenderer = null;
	private Mrcp mMrcp = null;
	private boolean isQuit = false;
	
	private boolean			mIsControllingDmr = false;
	 private DlnaServiceConnection mServiceConnection = new DlnaServiceConnection();
	 private DlnaSelectDevice  mMyService = null;
		private final int		Media_Type_Image = 1;
		private final int		Media_Type_Audio = 2;
		private final int		Media_Type_Video = 3;

	class DlnaServiceConnection implements ServiceConnection {

      public void onServiceConnected(ComponentName name, IBinder service) {
      	mMyService = ((DlnaSelectDevice.MyBinder)service).getService();

      	mMyService.setServiceClient(DlnaVideoPlay.this);
      	
      	Message msg = Message.obtain();
  		msg.what = Constant.MSG_DMRCHANGED;
  		mHandler.sendMessage(msg);
  		
      	
      }

      public void onServiceDisconnected(ComponentName name) {
      	mMyService.setServiceClient(null);
      	mMyService = null;

      }
  }
	public BroadcastReceiver volumeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();

			if (action.equals("android.media.VOLUME_CHANGED_ACTION")) {
				int index  = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", 0);
				if(isPlaying)
					SetVolume(index *14 );
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dlna_video_play2);

		app = (App) getApplication();
		aq = new AQuery(this);
		isPlaying = false;
//		handler = new Handler();
		
		bindService(new Intent(this, DlnaSelectDevice.class), mServiceConnection, BIND_AUTO_CREATE);

		Intent intent = getIntent();
		prod_name = intent.getStringExtra("title");
		prod_url = intent.getStringExtra("prod_url");
		

		aq.id(R.id.textView1).text(prod_name);
		aq.id(R.id.imageButton2).background(R.drawable.player_pause);
		sb = (SeekBar) findViewById(R.id.seekBar1);
		sb.setOnSeekBarChangeListener(sbLis);

		IntentFilter counterActionFilter = new IntentFilter(
				"android.media.VOLUME_CHANGED_ACTION");
		registerReceiver(volumeReceiver, counterActionFilter);
//		playMusic();

	}

	private int SetVolume(int mVolume){
		int ret = -1;
		ret = mMrcp.SetVolume(mMediaRenderer.uuid, mVolume, null);
		return ret;
	}

	public void OnClickTopLeft(View v) {
		
			finish();
		
	}

	public void OnClickTopRight(View v) {
		isQuit = true;
		aq.id(R.id.progressBar1).visible();
		mMrcp.MediaStop(mMediaRenderer.uuid, null);

	}

	public void OnClickNext(View v) {
		
	

	}

	public void OnClickResume(View v) {

		if (mMediaRenderer != null) {
			if (!isPlaying) {
				
				int ret = mMrcp.MediaPlay(mMediaRenderer.uuid, null);
				if (ret == 0) {
					/* To reduce the buffering time, stop monitoring */
					StopMonitoring();
				}
//				aq.id(R.id.imageButton2).background(R.drawable.player_pause);

			} else {
				isPlaying = false;

				mMrcp.MediaPause(mMediaRenderer.uuid, null);
//				mIsControllingDmr = false;
//				StopMonitoring();
				aq.id(R.id.imageButton2).background(R.drawable.player_play);
				
			}
		}

	}

	public void OnClickPre(View v) {


	}


	@Override
	public void onDestroy() {
		if (aq != null)
			aq.dismiss();
//		mMrcp.Stop();
//		Stack.Finalize();
//		if(isPlaying)
//			mStackAgent.MediaStop(mWorkingDmr.uuid, null);
//   	StopMonitoring();
//		mIsControllingDmr = false;
//		if(mStackAgent != null){
//			mStackAgent.StopMrcp();
//			mStackAgent.Destroy();
//		}
//		handler.removeCallbacks(updatesb);
   		unregisterReceiver(volumeReceiver);
   		unbindService(mServiceConnection);
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
//		StopMonitoring();
//		mIsControllingDmr = false;
//		mMrcp.Stop();
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

//	Runnable updatesb = new Runnable() {
//
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			int m_p = mp.getCurrentPosition();
//			sb.setProgress(m_p);
//			handler.postDelayed(updatesb, 1000);
//			aq.id(R.id.textViewTime1).text(Utils.formatDuration(m_p));
//			// 每秒钟更新一次
//		}
//
//	};
	private OnSeekBarChangeListener sbLis = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			if(mMediaRenderer != null  && mMrcp != null) {
				long position = sb.getProgress();
				String str = "";
				str +=  Util.Second2Time((int)position);
				mMrcp.MediaSeekTime(mMediaRenderer.uuid, str, null);
			}
//			mp.seekTo(sb.getProgress());
			// SeekBar确定位置后，跳到指定位置
		}

	};
//	private void initDLNA(){
//		mStackAgent = StackAgent.CreateInstance(this);
//		int ret = mStackAgent.StartMrcp(true);
//	}
//
//	private void StartMonitoring()
//	{
//		if (!mHandler.hasMessages(MSG_MONITOR_DMR))
//		{
//			mHandler.sendEmptyMessage(MSG_MONITOR_DMR);
//		}
//		if (!mHandler.hasMessages(MSG_GET_POSITION_TIMER))
//		{
//			mHandler.sendEmptyMessage(MSG_GET_POSITION_TIMER);
//		}
//	}
//	private void StopMonitoring()
//	{
//		mHandler.removeMessages(MSG_MONITOR_DMR);
//		mHandler.removeMessages(MSG_GET_POSITION_TIMER);
//	}

	final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
			switch (msg.what) {
			case Constant.MSG_DMRCHANGED:
				mMrcp = mMyService.getmMrcp();
				mMediaRenderer = mMyService.getMediaRenderer();
				aq.id(R.id.progressBar1).visible();
				mMrcp.MediaStop(mMediaRenderer.uuid, null);
//				mMrcp.SetListener(this);
				break;
			case Constant.MSG_DMR_CHANGED:
			{

				break;
			}
			case Constant.MSG_PUSH_LOCAL_FILE:
			{
//				exitMainScreen();
				mIsControllingDmr = true;
				StartMonitoring();
				break;
			}
			case Constant.MSG_PUSH_INTERNET_MEDIA:
			{
//				exitMainScreen();
				mIsControllingDmr = true;
				StartMonitoring();
				break;
			}
			case Constant.MSG_MONITOR_DMR:
			{
				if(mMediaRenderer!=null && mIsControllingDmr)
				{
					mMrcp.GetCurrentTransportActions(mMediaRenderer.uuid, null);
					mMrcp.GetMediaInfo(mMediaRenderer.uuid, null);
					mMrcp.GetTransportInfo(mMediaRenderer.uuid, null);
					mMrcp.GetVolume(mMediaRenderer.uuid, null);
					mMrcp.GetMute(mMediaRenderer.uuid, null);

					mHandler.sendEmptyMessageDelayed(Constant.MSG_MONITOR_DMR, 2000);
				}
				break;
			}
			
			case Constant.MSG_GET_POSITION_TIMER:
			{
				if(mMediaRenderer!=null && mIsControllingDmr)
				{
					mMrcp.GetPositionInfo(mMediaRenderer.uuid, null);
					
					mHandler.sendEmptyMessageDelayed(Constant.MSG_GET_POSITION_TIMER, 800);
				}
				break;
			}
			
			case Constant.MSG_MEDIA_INFO_UPDATE:
			{
				if (data == null) {
					break;
				}
				String title = data.getString(Constant.MSG_KEY_ID_TITLE);
				String mimetype = data.getString(Constant.MSG_KEY_ID_MIME_TYPE);
//				mTextViewMediaTitle.setText(title);	
//				mTextViewMediaMimeType.setText(mimetype);
				break;
			}
			
			case Constant.MSG_STATE_UPDATE:
			{
				if (data == null) {
					break;
				}
				String state = data.getString(Constant.MSG_KEY_ID_STATE);
				if(state.equalsIgnoreCase("PAUSED_PLAYBACK") || state.equalsIgnoreCase("STOPPED"))
					aq.id(R.id.imageButton2).background(R.drawable.player_play);
				if(state.equalsIgnoreCase("PLAYING"))
					aq.id(R.id.imageButton2).background(R.drawable.player_pause);
//				mTextViewPlayState.setText(state);				
				break;
			}
			
			case Constant.MSG_POSITION_UPDATE:
			{
				if (data == null) {
					break;
				}

				int position = data.getInt(Constant.MSG_KEY_ID_POSITION);
				int duration = data.getInt(Constant.MSG_KEY_ID_DURATION);
//				mCurDuration = duration;
				
//				String strPosition = "";
//				strPosition += Util.Second2Time(position);
//				strPosition += "/";
//				strPosition += Util.Second2Time(duration);
				
				aq.id(R.id.textViewTime1).text(Util.Second2Time(position));
				sb.setMax( duration);
				sb.setProgress(position);
				aq.id(R.id.textViewTime2).text(Util.Second2Time(duration));
//				sb.setMax(duration);
//				mTextViewPosition.setText(strPosition);		
//				mSeekBar.setMax(duration);
//				mSeekBar.setProgress(position);
				break;
			}
			
			case Constant.MSG_VOLUME_UPDATE:
			{
				if (data == null) {
					break;
				}
				int volume = data.getInt(Constant.MSG_KEY_ID_VOLUME);
				String strVol = "";
				strVol += volume;
//				mTextViewVolume.setText(strVol);		
//				mVolumeBar.setMax(100);
//				mVolumeBar.setProgress(volume);
				break;
			}
			
			case Constant.MSG_MUTE_UPDATE:
			{
				if (data == null) {
					break;
				}
				boolean mute = data.getBoolean(Constant.MSG_KEY_ID_MUTE);
//				mToggleButtonMute.setChecked(mute);
				break;
			}
			
			case Constant.MSG_ALLOWED_ACTIONS_UPDATE:
			{
				if (data == null) {
					break;
				}
				String actions = data.getString(Constant.MSG_KEY_ID_ALLOWED_ACTION);
//				mTextViewAllowAction.setText(actions);				
				break;
			}
			
			case Constant.MSG_ACTION_RESULT:
			{
				if (data == null || mMediaRenderer ==null ) {
					break;
				}
			
				String actionName = data.getString(Constant.MSG_KEY_ID_ACTION_NAME);
				int errorCode = data.getInt(Constant.MSG_KEY_ID_ACTION_RESULT);

				if (actionName == "SetAVTransportUri") {
					if (errorCode == 0) {
						int ret = mMrcp.MediaPlay(mMediaRenderer.uuid, null);
						if (ret == 0) {
							/* To reduce the buffering time, stop monitoring */
							StopMonitoring();
						}
					}
				}
				if (actionName == "Play") {
					/* Resume monitoring */
					StartMonitoring();
					mIsControllingDmr = true;
					isPlaying = true;

				} else if (actionName == "Stop") {
					isPlaying = false;

					aq.id(R.id.progressBar1).gone();
					if (isQuit) {
							finish();
							super.handleMessage(msg);
					} else{
						if(URLUtil.isNetworkUrl(prod_url))
							PushUrl(mMediaRenderer.uuid, prod_url, null);
						else 
							PushLocalFile(mMediaRenderer.uuid, prod_url, null);
					}

				}
				break;
			}
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
//	public void enterDmrControlScreen() 
//	{
//		mIsControllingDmr = true;
//		StartMonitoring();
//	}
	private void StartMonitoring()
	{
		if (!mHandler.hasMessages(Constant.MSG_MONITOR_DMR))
		{
			mHandler.sendEmptyMessage(Constant.MSG_MONITOR_DMR);
		}
		if (!mHandler.hasMessages(Constant.MSG_GET_POSITION_TIMER))
		{
			mHandler.sendEmptyMessage(Constant.MSG_GET_POSITION_TIMER);
		}
	}
	private void StopMonitoring()
	{
		mHandler.removeMessages(Constant.MSG_MONITOR_DMR);
		mHandler.removeMessages(Constant.MSG_GET_POSITION_TIMER);
	}
	
	public void onMediaInfoUpdate(String title, String mimeType) {
		// TODO Auto-generated method stub
		Message msg = Message.obtain();
		Bundle data = new Bundle();
		data.putString(Constant.MSG_KEY_ID_TITLE, title);
		data.putString(Constant.MSG_KEY_ID_MIME_TYPE, mimeType);
		msg.setData(data);
		msg.what = Constant.MSG_MEDIA_INFO_UPDATE;
		mHandler.sendMessage(msg);
	}

	public void onVolumeUpdate(int volume) {
		// TODO Auto-generated method stub
		Message msg = Message.obtain();
		Bundle data = new Bundle();
		data.putInt(Constant.MSG_KEY_ID_VOLUME, volume);
		msg.setData(data);
		msg.what = Constant.MSG_VOLUME_UPDATE;
		mHandler.sendMessage(msg);
	}

	public void onDmrChanged(ArrayList<MediaRenderer> dmrCache) {
		// TODO Auto-generated method stub
		if (dmrCache == null || isPlaying)
			return;

		Message msg = Message.obtain();
		msg.what = Constant.MSG_DMR_CHANGED;
		mHandler.sendMessage(msg);
	}

	public void onAllowedActionsUpdate(String actions) {
		// TODO Auto-generated method stub
		Message msg = Message.obtain();
		Bundle data = new Bundle();
		data.putString(Constant.MSG_KEY_ID_ALLOWED_ACTION, actions);
		msg.setData(data);
		msg.what = Constant.MSG_ALLOWED_ACTIONS_UPDATE;
		mHandler.sendMessage(msg);
	}

	public void onActionResult(String actionName, int res) {
		// TODO Auto-generated method stub
			Message msg = Message.obtain();
			Bundle data = new Bundle();
			data.putString(Constant.MSG_KEY_ID_ACTION_NAME, actionName);
			data.putInt(Constant.MSG_KEY_ID_ACTION_RESULT, res);
//			data.putInt(Constant.MSG_KEY_ID_CURRENT_ITEM, current_item);
			msg.setData(data);
			msg.what = Constant.MSG_ACTION_RESULT;
			mHandler.sendMessage(msg);
	}


	public void onPostionInfoUpdate(int position, int duration) {
		// TODO Auto-generated method stub
		Message msg = Message.obtain();
		Bundle data = new Bundle();
		data.putInt(Constant.MSG_KEY_ID_POSITION, position);
		data.putInt(Constant.MSG_KEY_ID_DURATION, duration);
		msg.setData(data);
		msg.what = Constant.MSG_POSITION_UPDATE;
		mHandler.sendMessage(msg);
	}


	public void onPlaybackStateUpdate(String state) {
		// TODO Auto-generated method stub
		Message msg = Message.obtain();
		Bundle data = new Bundle();
		data.putString(Constant.MSG_KEY_ID_STATE, state);
		msg.setData(data);
		msg.what = Constant.MSG_STATE_UPDATE;
		mHandler.sendMessage(msg);
	}


	public void onMuteUpdate(boolean muteState) {
		// TODO Auto-generated method stub
		Message msg = Message.obtain();
		Bundle data = new Bundle();
		data.putBoolean(Constant.MSG_KEY_ID_MUTE, muteState);
		msg.setData(data);
		msg.what = Constant.MSG_MUTE_UPDATE;
		mHandler.sendMessage(msg);
	}

	public int PushLocalFile(String uuid, String filePath, int ticket[])
	{
		int ret = -1;
		String uri = Util.EncodeUri(filePath);
		MediaInfo mediaInfo = GetMediaInfoFromLocalFile(filePath, Media_Type_Video);
		/*due to joy plus don't support video/mp2ts, 
		 * if modifying the mime-type to video/vnd.dlna.mpeg-tts,
		 * joy plus play the ts file correctly.*/
		if (filePath.endsWith(".ts") || mediaInfo.mimeType == "video/mp2ts")
		{
			mediaInfo.mimeType = "video/vnd.dlna.mpeg-tts";
		}
		String metadata = Util.EncodeMetadata(uri, mediaInfo);
		ret = mMrcp.SetAVTransportUri(uuid, uri, metadata, ticket);
		return ret;
	}
	public int PushUrl( String uuid, String mUrl, int ticket[])
	{
		int ret = -1;

		MediaInfo mediaInfo = GetMediaInfoFromUrl( prod_url, prod_name);

		String metadata = Util.EncodeMetadata(mUrl, mediaInfo);
		ret = mMrcp.SetAVTransportUri(uuid, mUrl, metadata, ticket);
		return ret;
	}

	public MediaInfo GetMediaInfoFromUrl(String mUrl, String title) {
		MediaInfo mediaInfo = new MediaInfo();
		//
		// media.url = "http://api.joyplus.tv/joyplus-service/video/t.m3u8";
			mediaInfo.mimeType = "video/m3u8";
			mediaInfo.title = title;
			for (int i = 0; i < Constant.video_dont_support_extensions.length; i++) {

				if (mUrl.trim().toLowerCase()
						.contains(Constant.video_dont_support_extensions[i])) {
					mediaInfo.mimeType = "video/"
							+ Constant.video_dont_support_extensions[i]
									.replace(".", "");
				}
			}

		return mediaInfo;
	}
	private MediaInfo GetMediaInfoFromLocalFile(String filePath, int mediaType)
	{
		MediaInfo info = null;
		Cursor cursor = null;
	
		if(mediaType == Media_Type_Image)
		{
			cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, "_data =? ", new String[]{filePath}, null);
			if (cursor != null)
			{
				if (cursor.moveToFirst())
				{
					info = new MediaInfo();
					int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE);
					if (index >= 0 && !(cursor.isBeforeFirst() || cursor.isAfterLast())) {
						info.size = cursor.getLong(index);
					}
					index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.MIME_TYPE);
					if (index >= 0 && !(cursor.isBeforeFirst() || cursor.isAfterLast())) {
						info.mimeType = cursor.getString(index);
					}
					index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.TITLE);
					if (index >= 0 && !(cursor.isBeforeFirst() || cursor.isAfterLast())) {
						info.title = cursor.getString(index);
					}
					index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_MODIFIED);
					if (index >= 0 && !(cursor.isBeforeFirst() || cursor.isAfterLast())) {
						info.date = cursor.getString(index);
					}
				}
				cursor.close();
				if(info != null)
				{
					return info;
				}
			}	
		}
		else if(mediaType == Media_Type_Audio)
		{
			cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, "_data =? ", new String[]{filePath}, null);
			if (cursor != null)
			{
				if (cursor.moveToFirst())
				{
					info = new MediaInfo();
					int index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.SIZE);
					if (index >= 0 && !(cursor.isBeforeFirst() || cursor.isAfterLast())) {
						info.size = cursor.getLong(index);
					}
					index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION);
					if (index >= 0 && !(cursor.isBeforeFirst() || cursor.isAfterLast())) {
						info.duration = cursor.getLong(index);
					}
					index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.MIME_TYPE);
					if (index >= 0 && !(cursor.isBeforeFirst() || cursor.isAfterLast())) {
						info.mimeType = cursor.getString(index);
					}
					index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE);
					if (index >= 0 && !(cursor.isBeforeFirst() || cursor.isAfterLast())) {
						info.title = cursor.getString(index);
					}
					index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST);
					if (index >= 0 && !(cursor.isBeforeFirst() || cursor.isAfterLast())) {
						info.artist = cursor.getString(index);
					}
					index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATE_MODIFIED);
					if (index >= 0 && !(cursor.isBeforeFirst() || cursor.isAfterLast())) {
						info.date = cursor.getString(index);
					}
					index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM);
					if (index >= 0 && !(cursor.isBeforeFirst() || cursor.isAfterLast())) {
						info.album = cursor.getString(index);
					}
				}
				cursor.close();
				if(info != null)
				{
					return info;
				}
			}
		}
		else if(mediaType == Media_Type_Video)
		{
			cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, "_data =? ", new String[]{filePath}, null);
			if (cursor != null)
			{
				if (cursor.moveToFirst())
				{
					info = new MediaInfo();
					int index = cursor.getColumnIndex(MediaStore.Video.VideoColumns.SIZE);
					if (index >= 0 && !(cursor.isBeforeFirst() || cursor.isAfterLast())) {
						info.size = cursor.getLong(index);
					}
					index = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION);
					if (index >= 0 && !(cursor.isBeforeFirst() || cursor.isAfterLast())) {
						info.duration = cursor.getLong(index);
					}
					index = cursor.getColumnIndex(MediaStore.Video.VideoColumns.MIME_TYPE);
					if (index >= 0 && !(cursor.isBeforeFirst() || cursor.isAfterLast())) {
						info.mimeType = cursor.getString(index);
					}
					index = cursor.getColumnIndex(MediaStore.Video.VideoColumns.TITLE);
					if (index >= 0 && !(cursor.isBeforeFirst() || cursor.isAfterLast())) {
						info.title = cursor.getString(index);
					}
					index = cursor.getColumnIndex(MediaStore.Video.VideoColumns.ARTIST);
					if (index >= 0 && !(cursor.isBeforeFirst() || cursor.isAfterLast())) {
						info.artist = cursor.getString(index);
					}
					index = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATE_MODIFIED);
					if (index >= 0 && !(cursor.isBeforeFirst() || cursor.isAfterLast())) {
						info.date = cursor.getString(index);
					}
					index = cursor.getColumnIndex(MediaStore.Video.VideoColumns.ALBUM);
					if (index >= 0 && !(cursor.isBeforeFirst() || cursor.isAfterLast())) {
						info.album = cursor.getString(index);
					}
				}
				cursor.close();
				if(info != null)
				{
					return info;
				}
			}		
		}

		return null;

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// add here.
		super.onActivityResult(requestCode, resultCode, data);
	}

}
