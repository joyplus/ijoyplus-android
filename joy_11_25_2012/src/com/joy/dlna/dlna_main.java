package com.joy.dlna;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.arcsoft.adk.atv.DLNA;
import com.arcsoft.adk.atv.MRCPCallback.DataOnGetMediaInfo;
import com.arcsoft.adk.atv.MRCPCallback.DataOnGetPositionInfo;
import com.arcsoft.adk.atv.MRCPCallback.DataOnGetProtocolInfo;
import com.arcsoft.adk.atv.MRCPCallback.DataOnGetTransportInfo;
import com.arcsoft.adk.atv.MRCPCallback.DataOnGetTransportSettings;
import com.arcsoft.adk.atv.RenderManager;
import com.arcsoft.adk.atv.RenderManager.IRenderPlayListener;
import com.arcsoft.adk.atv.RenderManager.IRenderStatusListener;
import com.arcsoft.adk.atv.UPnP.MediaRenderDesc;
import com.joy.R;

public class dlna_main extends Activity {
	private ArrayList<MediaRenderDesc> mRenders = new ArrayList<MediaRenderDesc>();
	private ListView mRenderListView;
	private MediaRenderDesc mCurrentRender = null;
	private long mPlayingDuration = 0;
	private static String TAG = dlna_main.class.getSimpleName();

	String META = null;
	String URL = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dlna_main);
		mRenderListView = (ListView) findViewById(R.id.listview);
		findViewById(R.id.btn_play).setEnabled(false);
		findViewById(R.id.btn_pause).setEnabled(false);
		findViewById(R.id.btn_stop).setEnabled(false);
		findViewById(R.id.seekbar_pos).setEnabled(false);

		DLNA.initSingleton(getApplication(), Looper.getMainLooper());
		DLNA.instance().setFileServerEnable(true);
		DLNA.instance().getRenderManager()
				.registerRenderStatusListener(mRenderStatusListener);
		DLNA.instance().getRenderManager()
				.registerRenderPlayListener(mRenderPlayListener);

		// veteranyu add

		Intent intent = getIntent();
		String m_meta = intent.getStringExtra("meta");
		boolean IsLocal = intent.getBooleanExtra("IsLocal", false);
		if (IsLocal) {
			META = m_meta;
			// META = DLNA.instance().getUri(m_meta);
			// META = DLNA.instance().getLocalMediaDidlData(m_meta);
			URL = DLNA.instance().getUri(intent.getStringExtra("url"));
		} else {
			META = m_meta;
			URL = intent.getStringExtra("url");
		}

		mRenderListView.setAdapter(mListAdapter);
		mRenderListView.setOnItemClickListener(mItemClickListener);

		findViewById(R.id.btn_play).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCurrentRender != null) {
					DLNA.instance()
							.getRenderManager()
							.playMediaAsync(mCurrentRender.m_strUuid,
									RenderManager.PlaySpeed.NORMAL);
				}
			}
		});
		findViewById(R.id.btn_pause).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCurrentRender != null) {
					DLNA.instance().getRenderManager()
							.pauseMediaAsync(mCurrentRender.m_strUuid);
				}
			}
		});
		findViewById(R.id.btn_stop).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCurrentRender != null) {
					DLNA.instance().getRenderManager()
							.stopMediaAsync(mCurrentRender.m_strUuid);
				}
			}
		});

		SeekBar sb = ((SeekBar) findViewById(R.id.seekbar_pos));
		sb.setMax(10000);
		sb.setOnSeekBarChangeListener(mSeekBarChangeListener);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		try {
			mProgressHandler.removeCallbacksAndMessages(null);
			// if (mRenderStatusListener != null)
			DLNA.instance().getRenderManager()
					.unregisterRenderStatusListener(mRenderStatusListener);
			DLNA.uninitSingleton();
			Log.e(TAG, "onDestroy OK");
		} catch (Exception ex) {
			Log.e(TAG, "onDestroy failed", ex);
		}

	}

	private IRenderStatusListener mRenderStatusListener = new IRenderStatusListener() {

		@Override
		public void onRenderRemoved(MediaRenderDesc arg0) {
			mRenders.clear();
			mRenders.addAll(DLNA.instance().getRenderManager().getRenderList());
			mRenderListView.setAdapter(mListAdapter);
		}

		@Override
		public void onRenderInstalled(MediaRenderDesc arg0, boolean arg1,
				boolean arg2, boolean arg3) {
			mRenders.clear();
			mRenders.addAll(DLNA.instance().getRenderManager().getRenderList());
			mRenderListView.setAdapter(mListAdapter);
		}

		@Override
		public void onRenderAdded(MediaRenderDesc arg0) {
			mRenders.clear();
			mRenders.addAll(DLNA.instance().getRenderManager().getRenderList());
			mRenderListView.setAdapter(mListAdapter);
		}

		@Override
		public void onGetProtocolInfo(String arg0, DataOnGetProtocolInfo arg1,
				int arg2) {
		}
	};

	private OnItemClickListener mItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			MediaRenderDesc rd = mRenders.get(position);
			if (mCurrentRender != null && !mCurrentRender.equals(rd)) {
				DLNA.instance().getRenderManager()
						.stopMediaAsync(mCurrentRender.m_strUuid);
			}
			mPlayingDuration = 0;
			mCurrentRender = rd;
			((TextView) findViewById(R.id.text_duration)).setText("00:00:00");

			boolean suc = DLNA.instance().getRenderManager()
					.stopMediaAsync(rd.m_strUuid);
			// veteranyu modified
			suc = DLNA.instance().getRenderManager()
					.openMediaAsync(rd.m_strUuid, URL, META);
			mProgressHandler.removeMessages(0);
			Log.e("a", URL);
		}
	};
	private boolean mSeekBarSeeking = false;
	OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			if (mSeekBarSeeking && mCurrentRender != null
					&& mPlayingDuration > 0) {
				int progress = seekBar.getProgress();
				long pos = progress * mPlayingDuration / seekBar.getMax();
				DLNA.instance().getRenderManager()
						.seekMediaAsync(mCurrentRender.m_strUuid, pos * 1000);
			}
			mSeekBarSeeking = false;
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			mSeekBarSeeking = true;
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (!fromUser) {
				return;
			}
			if (mPlayingDuration <= 0) {
				return;
			}
			long pos = progress * mPlayingDuration / seekBar.getMax();
			String text = convertSecToTimeString(pos, true);
			((TextView) findViewById(R.id.text_playpos)).setText(text);
		}
	};

	private ListAdapter mListAdapter = new BaseAdapter() {

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public int getCount() {
			return mRenders.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.render_list_item, null);
			}
			TextView txtName = (TextView) v.findViewById(R.id.device_name);
			MediaRenderDesc rd = mRenders.get(position);
			txtName.setText(rd.m_strFriendlyName);
			return v;
		}
	};
	private IRenderPlayListener mRenderPlayListener = new IRenderPlayListener() {

		@Override
		public void onSetVolume(int errorcode, String renderudn) {
			Log.w(TAG, "RenderPlaybackMessage: onSetVolume - " + errorcode);
		}

		@Override
		public void onSetMute(int errorcode, String renderudn) {
			Log.w(TAG, "RenderPlaybackMessage: onSetMute - " + errorcode);
		}

		@Override
		public void onOpenMedia(int errorcode, String renderudn) {
			Log.w(TAG, "RenderPlaybackMessage: onOpenMedia - " + errorcode);
			boolean enable = (errorcode == 0 && mCurrentRender != null && renderudn
					.equalsIgnoreCase(mCurrentRender.m_strUuid));

			findViewById(R.id.btn_play).setEnabled(enable);
			findViewById(R.id.btn_pause).setEnabled(enable);
			findViewById(R.id.btn_stop).setEnabled(enable);
			findViewById(R.id.seekbar_pos).setEnabled(enable);

			if (enable) {
				DLNA.instance().getRenderManager()
						.getMediaInfoAsync(mCurrentRender.m_strUuid);
			}
		}

		@Override
		public void onMediaStop(int errorcode, String renderudn) {
			Log.w(TAG, "RenderPlaybackMessage: onMediaStop - " + errorcode);
		}

		@Override
		public void onMediaSeek(int errorcode, String renderudn) {
			Log.w(TAG, "RenderPlaybackMessage: onMediaSeek - " + errorcode);
		}

		@Override
		public void onMediaPlay(int errorcode, String renderudn) {
			Log.w(TAG, "RenderPlaybackMessage: onMediaPlay - " + errorcode);
			if (errorcode == 0) {
				mProgressHandler.sendEmptyMessage(0);
			}
		}

		@Override
		public void onMediaPause(int errorcode, String renderudn) {
			Log.w(TAG, "RenderPlaybackMessage: onMediaPause - " + errorcode);
			if (errorcode == 0) {
				mProgressHandler.removeMessages(0);
			}
		}

		@Override
		public void onGetVolume(int errorcode, int volume, String renderudn) {
			Log.w(TAG, "RenderPlaybackMessage: onGetVolume - " + errorcode);
		}

		@Override
		public void onGetTransportSettings(int errorcode,
				DataOnGetTransportSettings info, String renderudn) {
			Log.w(TAG, "RenderPlaybackMessage: onGetTransportSettings - "
					+ errorcode + " Info = " + info.strPlayMode);
		}

		@Override
		public void onGetTransportInfo(int errorcode,
				DataOnGetTransportInfo info, String renderudn) {
			Log.w(TAG, "RenderPlaybackMessage: onGetTransportInfo - "
					+ errorcode + " TransInfo = "
					+ info.strCurrentTransportState + " : "
					+ info.strCurrentTransportStatus);

		}

		@Override
		public void onGetPositionInfo(int errorcode,
				DataOnGetPositionInfo data, String renderudn) {
			if (mSeekBarSeeking) {
				return;
			}
			Log.w(TAG, "RenderPlaybackMessage: onGetPositionInfo - "
					+ errorcode + " RelTime = " + data.m_strRelTime
					+ ", ABSTime = " + data.m_strAbsTime);
			long pos = convertTimeStringToSec(data.m_strRelTime);
			mPlayingDuration = convertTimeStringToSec(data.m_strTrackDuration);
			((TextView) findViewById(R.id.text_playpos))
					.setText(data.m_strRelTime);
			((TextView) findViewById(R.id.text_duration))
					.setText(data.m_strTrackDuration);
			if (mPlayingDuration > 0) {
				pos = (pos * 10000 / mPlayingDuration);
			} else {
				pos = 0;
			}
			((SeekBar) findViewById(R.id.seekbar_pos)).setProgress((int) pos);
		}

		@Override
		public void onGetMute(int errorcode, boolean mute, String renderudn) {
			Log.w(TAG, "RenderPlaybackMessage: onGetMute - " + errorcode);
		}

		@Override
		public void onGetMeidaInfo(int errorcode, DataOnGetMediaInfo info,
				String renderudn) {
			Log.w(TAG, "RenderPlaybackMessage: onGetMeidaInfo - " + errorcode
					+ " CurUri = " + info.m_strCurUri);
		}

		@Override
		public void onGetCurrentTransportActions(int errorcode,
				String allowedactions, String renderudn) {
			Log.w(TAG, "RenderPlaybackMessage: onMediaPlay - " + errorcode);
		}
	};

	/**
	 * ��ʱ���ַ���������
	 * 
	 * @param ԭʼʱ���ַ�hh:mm:ss.xxx
	 * @return ����
	 */
	public static long convertTimeStringToSec(String timestr) {
		if (null == timestr)
			return 0;

		long res = 0;
		String temp = null;

		int nDot = timestr.lastIndexOf('.');
		if (nDot != -1) {
			timestr = timestr.substring(0, nDot);
		}
		int i = 0;
		// sec
		do {
			int nSem = timestr.lastIndexOf(':');
			if (nSem == -1) {
				temp = timestr;
				timestr = null;
			} else {
				temp = timestr.substring(nSem + 1);
				timestr = timestr.substring(0, nSem);
			}

			try {
				res += Integer.valueOf(temp) * Math.pow(60, i++);
			} catch (NumberFormatException e) {
				return 0;
			}
		} while (timestr != null);
		return res;
	}

	/**
	 * ����ת����ʱ���ַ�
	 * 
	 * @param sec
	 * @param fillhour
	 *            ���Сʱ���?�����?
	 * @return ת������ַ�?: hh:mm:ss or mm:ss(fillhour = false)
	 */
	public static String convertSecToTimeString(long sec, boolean fillhour) {
		long nHour = sec / 3600;
		long nMin = sec % 3600;
		long nSec = nMin % 60;
		nMin = nMin / 60;

		return (nHour == 0 && !fillhour) ? String.format("%02d:%02d", nMin,
				nSec) : String.format("%02d:%02d:%02d", nHour, nMin, nSec);
	}

	Handler mProgressHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (mCurrentRender == null) {
				return;
			}
			DLNA.instance().getRenderManager()
					.getCurrentTransportActionsAsync(mCurrentRender.m_strUuid);
			DLNA.instance().getRenderManager()
					.getPositionInfoAsync(mCurrentRender.m_strUuid);
			DLNA.instance().getRenderManager()
					.getTransportInfoAsync(mCurrentRender.m_strUuid);
			sendEmptyMessageDelayed(0, 300);
		};
	};
}
