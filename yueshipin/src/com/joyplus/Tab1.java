package com.joyplus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyplus.Adapters.Tab1ListAdapter;
import com.joyplus.Adapters.Tab1ListData;
import com.joyplus.Service.Return.ReturnTops;
import com.parse.Parse;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

public class Tab1 extends Activity implements
		android.widget.AdapterView.OnItemClickListener {
	private String TAG = "Tab1";
	private AQuery aq;
	private App app;
	private ReturnTops m_ReturnTops = null;

	private int Fromepage;
	private ArrayList dataStruct;
	private ListView ItemsListView;
	private Tab1ListAdapter Tab1Adapter;
	private int isLastisNext = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab1);
		app = (App) getApplication();
		aq = new AQuery(this);
		dataStruct = new ArrayList();
		
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setOnDownloadListener(null);
		UmengUpdateAgent.update(this);
		Parse.initialize(this, "FtAzML5ln4zKkcL28zc9XR6kSlSGwXLdnsQ2WESB", "YzMYsyKNV7ibjZMfIDSGoV5zxsylV4evtO8x64tl");
		
		// 获取listview对象
		ItemsListView = (ListView) findViewById(R.id.listView1);
		// 设置listview的点击事件监听器
		ItemsListView.setOnItemClickListener(this);
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
		CheckSaveData();
		// MobclickAgent.setDebugMode(true);
	}

	public void OnClickTab1TopLeft(View v) {
		
		Intent i = new Intent(this, Search.class);
		startActivity(i);
	}

	public void OnClickDownloadTopRight(View v) {
		Intent intent = new Intent(this, Video_Cache.class);
		startActivity(intent);
	}

	public void OnClickTab1TopRight(View v) {
		Intent i = new Intent(this, Setting.class);
		startActivity(i);
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

	public void GetVideoMovies() {
		String m_j = null;

		if (m_ReturnTops.tops == null)
			return;
		if (isLastisNext > 1) {
			for (int i = 0; i < m_ReturnTops.tops.length; i++) {
				Tab1ListData m_Tab1ListData = new Tab1ListData();
				m_Tab1ListData.Pic_ID = m_ReturnTops.tops[i].id;
				m_Tab1ListData.Pic_url = m_ReturnTops.tops[i].pic_url;
				m_Tab1ListData.Pic_name = m_ReturnTops.tops[i].name;
				m_Tab1ListData.right = m_ReturnTops.tops[i].prod_type;
				if (m_ReturnTops.tops[i].items != null) {
					for (int j = 0; j < m_ReturnTops.tops[i].items.length; j++) {
						m_j = m_ReturnTops.tops[i].items[j].prod_name;
						if (m_j != null) {
							m_j = "• " + m_j;
							switch (j) {
							case 0:
								m_Tab1ListData.Pic_list1 = m_j;
								break;
							case 1:
								m_Tab1ListData.Pic_list2 = m_j;
								break;
							case 2:
								m_Tab1ListData.Pic_list3 = m_j;
								break;
							case 3:
								m_Tab1ListData.Pic_list4 = m_j;
								break;
							case 4:
								m_Tab1ListData.Pic_list5 = m_j;
								break;
							case 5:
								m_Tab1ListData.Pic_list6 = m_j;
								break;
							}

						}

					}

				}
				dataStruct.add(m_Tab1ListData);
				Tab1Adapter.notifyDataSetChanged();
			}
			return;
			
		} else
		{
			NotifyDataAnalysisFinished();
		}
		
		for (int i = 0; i < m_ReturnTops.tops.length; i++) {
			Tab1ListData m_Tab1ListData = new Tab1ListData();
			m_Tab1ListData.Pic_ID = m_ReturnTops.tops[i].id;
			m_Tab1ListData.Pic_url = m_ReturnTops.tops[i].pic_url;
			m_Tab1ListData.Pic_name = m_ReturnTops.tops[i].name;
			m_Tab1ListData.right = m_ReturnTops.tops[i].prod_type;
			if (m_ReturnTops.tops[i].items != null) {
				for (int j = 0; j < m_ReturnTops.tops[i].items.length; j++) {
					m_j = m_ReturnTops.tops[i].items[j].prod_name;
					if (m_j != null) {
						m_j = "• " + m_j;
						switch (j) {
						case 0:
							m_Tab1ListData.Pic_list1 = m_j;
							break;
						case 1:
							m_Tab1ListData.Pic_list2 = m_j;
							break;
						case 2:
							m_Tab1ListData.Pic_list3 = m_j;
							break;
						case 3:
							m_Tab1ListData.Pic_list4 = m_j;
							break;
						case 4:
							m_Tab1ListData.Pic_list5 = m_j;
							break;
						case 5:
							m_Tab1ListData.Pic_list6 = m_j;
							break;
						}

					}

				}

			}
			dataStruct.add(m_Tab1ListData);
		}

	}

	public void OnClickImageView(View v) {
		/*
		 * Intent intent = new Intent(this, BuChongGeRenZhiLiao.class);
		 * intent.putExtra("prod_id", m_prod_id); intent.putExtra("prod_type",
		 * m_prod_type); try { startActivity(intent); } catch
		 * (ActivityNotFoundException ex) { Log.e(TAG,
		 * "OnClickImageView failed", ex); }
		 */
	}

	// 初始化list数据函数
	public void InitListData(String url, JSONObject json, AjaxStatus status) {
		
		if (status.getCode() == AjaxStatus.NETWORK_ERROR)  {
			aq.id(R.id.ProgressText).gone();
			app.MyToast(aq.getContext(),
					getResources().getString(R.string.networknotwork));
			return;
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (isLastisNext > 1)
				m_ReturnTops = null;
			m_ReturnTops = mapper.readValue(json.toString(), ReturnTops.class);
			if (m_ReturnTops.tops.length > 0)
				app.SaveServiceData("tops" + Integer.toString(isLastisNext),
						json.toString());
			// 创建数据源对象
			GetVideoMovies();
			aq.id(R.id.ProgressText).gone();
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

	// 数据更新
	public void NotifyDataAnalysisFinished() {
		if (dataStruct != null && ItemsListView != null) {
			Tab1ListAdapter listviewdetailadapter = getAdapter();
			ItemsListView.setAdapter(listviewdetailadapter);
		} else {
			app.MyToast(this, "ItemsListView empty.");
		}
	}

	private Tab1ListAdapter getAdapter() {
		if (Tab1Adapter == null) {
			ArrayList arraylist = dataStruct;
			Tab1ListAdapter listviewdetailadapter = new Tab1ListAdapter(this,
					arraylist);
			Tab1Adapter = listviewdetailadapter;
		} else {
			ArrayList arraylist1 = dataStruct;
			Tab1ListAdapter listviewdetailadapter1 = new Tab1ListAdapter(this,
					arraylist1);
			Tab1Adapter = listviewdetailadapter1;
		}
		return Tab1Adapter;
	}

	// listview的点击事件接口函数
	@Override
	public void onItemClick(AdapterView adapterview, View view, int i, long l) {
		Tab1ListData m_Tab1ListData = (Tab1ListData) ItemsListView
				.getItemAtPosition(i);
		if (m_ReturnTops != null) {
			// app.MyToast(this, m_Tab1ListData.Pic_name, Toast.LENGTH_LONG)
			// .show();
			Intent intent = new Intent(this, Detail_BangDan.class);
			intent.putExtra("BangDan_id", m_Tab1ListData.Pic_ID);
			intent.putExtra("BangDan_name", m_Tab1ListData.Pic_name);
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException ex) {
				Log.e(TAG, "Call Detail_BangDan failed", ex);
			}
		} else {
			app.MyToast(this, "ReturnTops is empty.");
		}

	}

	private void CheckSaveData() {
		String SaveData = null;
		ObjectMapper mapper = new ObjectMapper();
		SaveData = app.GetServiceData("tops" + Integer.toString(isLastisNext));
		if (SaveData == null) {
			GetServiceData(isLastisNext);
		} else {
			try {
				m_ReturnTops = mapper.readValue(SaveData, ReturnTops.class);
				// 创建数据源对象
				GetVideoMovies();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// execute the task
						dataStruct = null;
						dataStruct = new ArrayList();
						GetServiceData(isLastisNext);
					}
				}, 100000);
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
	
	public void GetServiceData(int index) {
		String url = Constant.BASE_URL + "tops" + "?page_num="
				+ Integer.toString(index) + "&page_size=30";

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class).weakHandler(this, "InitListData");

		cb.SetHeader(app.getHeaders());
//		cb.header("User-Agent",
//				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
//		cb.header("app_key", Constant.APPKEY);
//		cb.header("user_id", app.UserID);

		aq.id(R.id.ProgressText).visible();
		aq.progress(R.id.progress).ajax(cb);

	}
}
