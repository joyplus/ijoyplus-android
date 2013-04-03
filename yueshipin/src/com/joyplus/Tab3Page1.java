package com.joyplus;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyplus.Adapters.CurrentPlayData;
import com.joyplus.Adapters.Tab3Page1ListData;
import com.joyplus.Service.Return.ReturnTops;
import com.joyplus.Service.Return.ReturnUserPlayHistories;
import com.joyplus.Video.VideoPlayerActivity;
import com.joyplus.download.Dao;
import com.umeng.analytics.MobclickAgent;

public class Tab3Page1 extends Activity implements OnTabActivityResultListener {
	private String TAG = "Tab3Page1";
	private AQuery aq;
	private App app;
	private String datainfo = null;
	private ArrayList dataStruct;
	private ListView ItemsListView;
	private Tab3Page1ListAdapter Tab3Page1Adapter;
	private ReturnUserPlayHistories m_ReturnUserPlayHistories = null;
	private int isLastisNext = 1;

	// 播放记录变量
	private long current_play_time = 0;
	Tab3Page1ListData tempPlayHistoryData = null;
	private CurrentPlayData mCurrentPlayData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab3page1);
		// 获取listview对象
		ItemsListView = (ListView) findViewById(R.id.listView1);

		ItemsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				OnClickPlayIndex(position);
			}
		});
		ItemsListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				OnDeleteListItem(arg2);
				return false;
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
		app = (App) getApplication();
		aq = new AQuery(this);
		mCurrentPlayData = new CurrentPlayData();
		aq.id(R.id.Layout1).gone();
	}

	public void OnClickTab1TopLeft(View v) {
		Intent i = new Intent(this, Search.class);
		startActivity(i);

	}

	public void OnClickTab1TopRight(View v) {
		Intent i = new Intent(this, Setting.class);
		startActivity(i);

	}

	public void OnClickMore(View v) {
		// Intent i = new Intent(this, Tab3Page1_more.class);
		// startActivityForResult(i, 1);
	}

	public void OnClickContinue(int position) {
		Tab3Page1ListData m_Tab3Page1ListData = (Tab3Page1ListData) ItemsListView
				.getItemAtPosition(position);
		tempPlayHistoryData = m_Tab3Page1ListData;
		if ((m_Tab3Page1ListData.Pro_time) > 0
				&& (m_Tab3Page1ListData.Pro_duration > m_Tab3Page1ListData.Pro_time)) {
			current_play_time = m_Tab3Page1ListData.Pro_time*1000;
		}
		if (m_Tab3Page1ListData != null) {
			app.checkUserSelect(Tab3Page1.this.getParent());// 创建对话框必须在看见的最低层的Activity
			if (app.use2G3G) {
				//历史记录
				StatisticsUtils.StatisticsClicksShow(aq, app, m_Tab3Page1ListData.Pro_ID
						, m_Tab3Page1ListData.Pro_name, 
						m_Tab3Page1ListData.Pro_name1, 
						m_Tab3Page1ListData.Pro_type);
				if (m_Tab3Page1ListData.Pro_urlType.equalsIgnoreCase("1")) {
					// 1：电影，2：电视剧，3：综艺，4：视频
					mCurrentPlayData.prod_id = m_Tab3Page1ListData.Pro_ID;
					mCurrentPlayData.CurrentCategory =m_Tab3Page1ListData.Pro_type-1;
					if(m_Tab3Page1ListData.Pro_type == 2 || m_Tab3Page1ListData.Pro_type ==3)
						mCurrentPlayData.CurrentIndex = Integer.parseInt(m_Tab3Page1ListData.Pro_name1) -1;
					
					CallVideoPlayActivity(m_Tab3Page1ListData.Pro_ID,
							m_Tab3Page1ListData.Pro_url,
							m_Tab3Page1ListData.Pro_name);
				} else if (m_Tab3Page1ListData.Pro_urlType
						.equalsIgnoreCase("2")) {
					/*
					 * 网页播放地址不需要记录时间
					 */
					Intent intent = new Intent();
					intent.setAction("android.intent.action.VIEW");
					Uri content_url = Uri.parse(m_Tab3Page1ListData.Pro_url);
					intent.setData(content_url);
					startActivity(intent);
				}
				
			} else {
//				app.MyToast(this, "m_Tab3Page1ListData is empty.");
			}
		}
	}

	private OnClickListener mContinueClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			final int position = ItemsListView.getPositionForView(v);
			if (position != ListView.INVALID_POSITION) {
				OnClickContinue(position);
			}
		}
	};

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
//		isLastisNext = 1;
		CheckSaveData();
//		GetServiceData(isLastisNext);
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
		cb.url(url).type(JSONObject.class).weakHandler(this, "InitListData");
		String str = app.UserID;
		cb.SetHeader(app.getHeaders());
		aq.ajax(cb);
	}

	// 初始化list数据函数
	public void InitListData(String url, JSONObject json, AjaxStatus status) {
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
			for(int i = 0;i<dataStruct.size();i++)
			{
				dataStruct.remove(i);
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

	private void OnDeleteListItem(final int item) {
		final Tab3Page1ListData m_Tab3Page1ListData = (Tab3Page1ListData) ItemsListView
				.getItemAtPosition(item);
		String program_name = "你确定删除  " + m_Tab3Page1ListData.Pro_name + "  吗？";
		AlertDialog.Builder builder = new AlertDialog.Builder(
				Tab3Page1.this.getParent());
		builder.setTitle("播放记录").setMessage(program_name)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 删除数据
						// app.DeletePlayData(m_Tab3Page1ListData.Pro_ID);

						dataStruct.remove(item);
						Tab3Page1Adapter.notifyDataSetChanged();
						ItemsListView.invalidate();
						if (dataStruct.size() == 0) {
							aq.id(R.id.imageNoitemBG).visible();
							aq.id(R.id.Layout1).gone();
						}
						// 删除数据
						DeleteHistory(m_Tab3Page1ListData.Pro_ID);
						//删除缓存中的数据
					}
				}).setNegativeButton("取消", null).create();
		builder.show();
	}

	private void DeleteHistory(String prod_id) {
		String url = Constant.BASE_URL + "program/hiddenPlay";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("prod_id", prod_id);

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.SetHeader(app.getHeaders());

		cb.params(params).url(url).type(JSONObject.class)
				.weakHandler(this, "UnfavorityResult");
		aq.ajax(cb);
	}

	public void UnfavorityResult(String url, JSONObject json, AjaxStatus status) {
		if (json != null) {
			try {
				if (json.getString("res_code").trim().equalsIgnoreCase("00000")) {
					app.MyToast(this, "删除成功!");
					// GetServiceData(isLastisNext);
				} else
					app.MyToast(this, "删除失败!");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {

			// ajax error, show error code
			if (status.getCode() == AjaxStatus.NETWORK_ERROR)
				app.MyToast(this,
						getResources().getString(R.string.networknotwork));
		}
	}

	public void OnClickPlayIndex(int index) {
		Tab3Page1ListData m_Tab3Page1ListData = (Tab3Page1ListData) ItemsListView
				.getItemAtPosition(index);
		if (m_Tab3Page1ListData != null) {
			Intent intent = new Intent();
			// 1：电影，2：电视剧，3：综艺，4：视频
			switch (m_Tab3Page1ListData.Pro_type) {
			case 1:
				intent.setClass(this, Detail_Movie.class);
				intent.putExtra("prod_id", m_Tab3Page1ListData.Pro_ID);
				intent.putExtra("prod_name", m_Tab3Page1ListData.Pro_name);
				try {
					startActivity(intent);
				} catch (ActivityNotFoundException ex) {
					Log.e(TAG, "Call Detail_Movie failed", ex);
				}
				break;
			case 2:
				intent.setClass(this, Detail_TV.class);
				intent.putExtra("prod_id", m_Tab3Page1ListData.Pro_ID);
				intent.putExtra("prod_name", m_Tab3Page1ListData.Pro_name);
				try {
					startActivity(intent);
				} catch (ActivityNotFoundException ex) {
					Log.e(TAG, "Call Detail_TV failed", ex);
				}
				break;
			case 3:
				intent.setClass(this, Detail_Show.class);
				intent.putExtra("prod_id", m_Tab3Page1ListData.Pro_ID);
				intent.putExtra("prod_name", m_Tab3Page1ListData.Pro_name);
				try {
					startActivity(intent);
				} catch (ActivityNotFoundException ex) {
					Log.e(TAG, "Call Detail_Show failed", ex);
				}
				break;
			}

		} else {
			// app.MyToast(this, "m_Tab3Page1ListData is empty.");
			// 不加
		}
	}

	public void CallVideoPlayActivity(String prod_id, String m_uri, String title) {
		app.IfSupportFormat(m_uri);

		app.setCurrentPlayData(mCurrentPlayData);
		
		Intent intent = new Intent(this, VideoPlayerActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("path", m_uri);
		bundle.putString("title", title);
		bundle.putString("prod_id", prod_id);
		if(!tempPlayHistoryData.Pro_name1.equalsIgnoreCase("EMPTY")){
			bundle.putString("prod_subname",tempPlayHistoryData.Pro_name1);
		}
		bundle.putString("prod_type",
				Integer.toString(tempPlayHistoryData.Pro_type));
		bundle.putLong("current_time", current_play_time);
		intent.putExtras(bundle);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			Log.e(TAG, "mp4 fail", ex);
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
				
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// execute the task
						isLastisNext = 1;
						GetServiceData(isLastisNext);
					}
				}, 2000);
				
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
	private static class AccessoriesViewHolder {
		public TextView video_caption;
		public TextView textView03;
		public TextView textView04;
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
			AccessoriesViewHolder holder = null;

			if (view == null) {

				view = getLayoutInflater().inflate(
						R.layout.tab3_page1_detail_list, viewgroup, false);

				holder = new AccessoriesViewHolder();

				((Button) view.findViewById(R.id.button1))
						.setOnClickListener(mContinueClickListener);

				holder.video_caption = (TextView) view
						.findViewById(R.id.txt_video_caption);
				holder.textView03 = (TextView) view
						.findViewById(R.id.TextView03);
				holder.textView04 = (TextView) view
						.findViewById(R.id.TextView04);
				view.setTag(holder);
			} else {
				holder = (AccessoriesViewHolder) view.getTag();
			}

			// 获取当前数据项的数据
			Tab3Page1ListData m_Tab3Page1ListData = (Tab3Page1ListData) getItem(i);

			holder.video_caption.setText(m_Tab3Page1ListData.Pro_name);
			// 1：电影，2：电视剧，3：综艺节目，4：视频
			switch (m_Tab3Page1ListData.Pro_type) {
			case 1:
				if ((m_Tab3Page1ListData.Pro_time > 0)
						&& (m_Tab3Page1ListData.Pro_duration > m_Tab3Page1ListData.Pro_time)) {
					holder.textView03
							.setText(stringForTime(m_Tab3Page1ListData.Pro_time*1000));
					holder.textView04.setText("");
				}

				else {
					holder.textView03.setText("");
					holder.textView04.setText("");
				}
				break;
			case 2:
				if (m_Tab3Page1ListData.Pro_name1 != null
						&& m_Tab3Page1ListData.Pro_name1.length() > 0) {
					if ((m_Tab3Page1ListData.Pro_time > 0)
							&& (m_Tab3Page1ListData.Pro_duration > m_Tab3Page1ListData.Pro_time)) {
						holder.textView03.setText("第"+m_Tab3Page1ListData.Pro_name1+"集");
						holder.textView04
								.setText(stringForTime(m_Tab3Page1ListData.Pro_time*1000));
					} else {
						holder.textView03.setText("第"+m_Tab3Page1ListData.Pro_name1+"集");
						holder.textView04.setText("");
					}
				} else {
					holder.textView03.setText("");
					holder.textView04.setText("");
				}
				break;
			case 3:
				if (m_Tab3Page1ListData.Pro_name1 != null
						&& m_Tab3Page1ListData.Pro_name1.length() > 0) {
					// Pro_name1要是二级标题
					if ((m_Tab3Page1ListData.Pro_time > 0)
							&& (m_Tab3Page1ListData.Pro_duration > m_Tab3Page1ListData.Pro_time)) {
						holder.textView03
								.setText("第"+m_Tab3Page1ListData.Pro_name1+"期");
						holder.textView04
								.setText(stringForTime(m_Tab3Page1ListData.Pro_time*1000));
					} else {
						holder.textView03.setText("");
						holder.textView04.setText("");
					}
				}
				break;

			default:
				break;
			}
			if (m_Tab3Page1ListData.Pro_time > 0
					&& m_Tab3Page1ListData.Pro_time < m_Tab3Page1ListData.Pro_duration)
				{((Button) view.findViewById(R.id.button1))
				.setBackgroundResource(R.drawable.tab3_page1_icon_see);
				}
			else
			{
				((Button) view.findViewById(R.id.button1))
				.setBackgroundResource(R.drawable.tab3_page1_replay_icon_see);
			}
				
			return view;
		}
	}

	@Override
	public void onTabActivityResult(int requestCode, int resultCode, Intent data) {
	}

	public void CallProgramPlayResult(String url, JSONObject json,
			AjaxStatus status) {
	}
}
