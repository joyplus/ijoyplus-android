package com.joyplus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.joyplus.widget.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyplus.Adapters.Tab3Page1ListData;
import com.joyplus.Service.Return.ReturnUserPlayHistories;
import com.joyplus.Video.VideoPlayerActivity;
import com.joyplus.cache.VideoCacheInfo;
import com.joyplus.cache.VideoCacheManager;
import com.joyplus.playrecord.PlayRecordInfo;
import com.joyplus.playrecord.PlayRecordManager;
import com.umeng.analytics.MobclickAgent;

public class WeixinPage3 extends Activity implements OnTabActivityResultListener {
	private String TAG = "Tab3Page1";
	private AQuery aq;
	private App app;
	private ArrayList dataStruct;
	private ListView ItemsListView;
	private Tab3Page1ListAdapter Tab3Page1Adapter;
	private ReturnUserPlayHistories m_ReturnUserPlayHistories = null;
	private int isLastisNext = 1;
	
	Tab3Page1ListData tempPlayHistoryData = null;

	/*
	 * playHistoryData
	 */
	VideoCacheInfo cacheInfo;
	VideoCacheInfo cacheInfoTemp;
	VideoCacheManager cacheManager;
	PlayRecordInfo playrecordinfo;
	PlayRecordManager playrecordmanager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weixinpage3);
		// 获取listview对象
		ItemsListView = (ListView) findViewById(R.id.listView1);

		ItemsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				OnClickPlayIndex(position);
			}
		});
		ItemsListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				// 当不滚动时
				case OnScrollListener.SCROLL_STATE_IDLE:
					// 判断滚动到底部
					if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
						isLastisNext++;
						GetServiceData(isLastisNext);
					}
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
		
		cacheManager = new VideoCacheManager(WeixinPage3.this);//电影的
		cacheInfo = new VideoCacheInfo();
		playrecordmanager = new PlayRecordManager(WeixinPage3.this);//播放记录
		playrecordinfo = new PlayRecordInfo();
		
		app = (App) getApplication();
		aq = new AQuery(this);
		aq.id(R.id.Layout1).gone();
	}
	
	@Override
	protected void onDestroy() {
		if (aq != null)
			aq.dismiss();
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		dataStruct = new ArrayList();
		Tab3Page1Adapter = new Tab3Page1ListAdapter();
		ItemsListView.setAdapter(Tab3Page1Adapter);
		CheckSaveData();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	public void OnClickImageView(View v) {
		/*
		 * 
		 */
	}

	// InitListData
	public void GetServiceData(int index) {
		String url = Constant.BASE_URL + "user/playHistories" + "?page_num="
				+ Integer.toString(index) + "&page_size=10";
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class).weakHandler(this, "InitListDataForHistory");
		cb.SetHeader(app.getHeaders());
		aq.ajax(cb);
	}

	// 初始化list数据函数
	public void InitListDataForHistory(String url, JSONObject json, AjaxStatus status) {
		if (status.getCode() == AjaxStatus.NETWORK_ERROR&&app.GetServiceData("user_Histories")==null) {
			aq.id(R.id.ProgressText).gone();
			app.MyToast(aq.getContext(),
					getResources().getString(R.string.networknotwork));
			return;
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			if(isLastisNext == 1)
			{
				m_ReturnUserPlayHistories = mapper.readValue(json.toString(),
						ReturnUserPlayHistories.class);
				app.SaveServiceData("user_Histories", json.toString());
			}
			else if (isLastisNext > 1)
			{
				m_ReturnUserPlayHistories = null;
				m_ReturnUserPlayHistories = mapper.readValue(json.toString(),
						ReturnUserPlayHistories.class);
			}
			// 创建数据源对象
			GetVideoMovies();

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

	public void GetVideoMovies() {
		if (m_ReturnUserPlayHistories.histories == null) {
			if (isLastisNext == 1) {
				aq.id(R.id.imageNoitemBG).visible();
				aq.id(R.id.Layout1).gone();
			}
			return;
		}
		if(isLastisNext == 1)
		{
			//考虑会不会越界
			for(int j=dataStruct.size()-1;j>-1;j--)
			{
				dataStruct.remove(j);
			}
			dataStruct.clear();
		}
		for (int i = 0; i < m_ReturnUserPlayHistories.histories.length; i++) {
			Tab3Page1ListData m_Tab3Page1ListData = new Tab3Page1ListData();

			m_Tab3Page1ListData.Pro_ID = m_ReturnUserPlayHistories.histories[i].prod_id;
			m_Tab3Page1ListData.Pro_name1 = m_ReturnUserPlayHistories.histories[i].prod_subname;
			m_Tab3Page1ListData.Pro_name = m_ReturnUserPlayHistories.histories[i].prod_name;
			m_Tab3Page1ListData.Pro_url = m_ReturnUserPlayHistories.histories[i].video_url;
			m_Tab3Page1ListData.Pro_type = m_ReturnUserPlayHistories.histories[i].prod_type;
			m_Tab3Page1ListData.Pro_urlType = m_ReturnUserPlayHistories.histories[i].play_type;
			m_Tab3Page1ListData.Pro_time = m_ReturnUserPlayHistories.histories[i].playback_time;
			m_Tab3Page1ListData.Pro_duration = m_ReturnUserPlayHistories.histories[i].duration;
			m_Tab3Page1ListData.Pro_pic_url = m_ReturnUserPlayHistories.histories[i].prod_pic_url;
			if (dataStruct.contains(m_Tab3Page1ListData)) {

			} else {
				dataStruct.add(m_Tab3Page1ListData);
			}

		}
		Tab3Page1Adapter.notifyDataSetChanged();

		int m_num = dataStruct.size();

		if (m_num == 0) {
			aq.id(R.id.imageNoitemBG).visible();
			aq.id(R.id.Layout1).gone();
		}
		if (isLastisNext == 1 && m_num > 0) {
			aq.id(R.id.imageNoitemBG).gone();
			aq.id(R.id.Layout1).visible();
		}
	}


	public void OnClickPlayIndex(int index) {
		Tab3Page1ListData m_Tab3Page1ListData = (Tab3Page1ListData) ItemsListView
				.getItemAtPosition(index);
		if (m_Tab3Page1ListData != null) {
			Intent intent = new Intent();
				intent.setClass(this, Weixin_ShareVideo.class);
				intent.putExtra("prod_id", m_Tab3Page1ListData.Pro_ID);
				intent.putExtra("prod_name", m_Tab3Page1ListData.Pro_name);
				try {
					startActivity(intent);
				} catch (ActivityNotFoundException ex) {
					Log.e(TAG, "Call Detail_Movie failed", ex);
				}
		}
	}

	private String stringForTime(int time) {

		int totalSeconds = time / 1000;
		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;
		if (hours > 0) {
			return String.format("%d:%02d:%02d", hours, minutes, seconds)
					.toString();
		} else {
			return String.format("%02d:%02d", minutes, seconds).toString();
		}
	}

	/*
	 * 从本地缓存取数据,然后从服务器抓数据下来
	 */
	private void CheckSaveData() {
		String SaveData = null;
		ObjectMapper mapper = new ObjectMapper();
		SaveData = app.GetServiceData("user_Histories");
		if (SaveData == null) {
			isLastisNext = 1;
			GetServiceData(isLastisNext);
		} else {
			try {
				m_ReturnUserPlayHistories = mapper.readValue(SaveData,
						ReturnUserPlayHistories.class);
				// 创建数据源对象
				GetVideoMovies();
				isLastisNext = 1;
				GetServiceData(isLastisNext);
				
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

	/**
	 * A pretty basic ViewHolder used to keep references on children
	 * {@link View}s.
	 * 
	 * @author Cyril Mottier
	 */
	private static class ViewHolder {
		public ImageView mImageView;
		public TextView mName;
		public TextView mName1;
		public TextView mYear;
	}
	
	public class Tab3Page1ListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return dataStruct.size();
		}

		@Override
		public Tab3Page1ListData getItem(int position) {
			return (Tab3Page1ListData) dataStruct.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		// 获取显示当前的view
		@Override
		public View getView(int i, View view, ViewGroup viewgroup) {
			ViewHolder holder = null;
			if (view == null) {

				view = getLayoutInflater().inflate(
						R.layout.weixinpage3_item, viewgroup, false);

				holder = new ViewHolder();

				holder.mImageView = (ImageView) view
						.findViewById(R.id.video_preview_img);
				holder.mName = (TextView) view
						.findViewById(R.id.txt_video_caption);
				holder.mName1 = (TextView) view.findViewById(R.id.TextView03);
				holder.mYear = (TextView) view.findViewById(R.id.txt_1);

				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			// 获取当前数据项的数据
			Tab3Page1ListData m_Tab3Page1ListData = (Tab3Page1ListData) getItem(i);

			AQuery aqlist = aq.recycle(view);
			aqlist.id(holder.mName).text(m_Tab3Page1ListData.Pro_name);
			aqlist.id(holder.mName1).text(stringForTime(m_Tab3Page1ListData.Pro_duration));
			aqlist.id(holder.mYear).visible();
			aqlist.id(holder.mImageView).image(m_Tab3Page1ListData.Pro_pic_url,
					true, true);
			return view;
		}
	}

	@Override
	public void onTabActivityResult(int requestCode, int resultCode, Intent data) {
	}
}
