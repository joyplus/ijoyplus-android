package com.joyplus;

import com.umeng.analytics.MobclickAgent;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyplus.Adapters.Tab2Page2ListAdapter;
import com.joyplus.Adapters.Tab2Page2ListData;
import com.joyplus.Service.Return.ReturnTops;
import com.joyplus.widget.MyListView;
import com.joyplus.widget.MyListView.OnRefreshListener;

public class Tab2Page2 extends Activity implements
		android.widget.AdapterView.OnItemClickListener {
	private String TAG = "Tab2Page2";
	protected AQuery aq;
	private App app;
	private ReturnTops m_ReturnTops = null;

	private int Fromepage;
	private ArrayList dataStruct;
	// private ListView ItemsListView;
	private MyListView ItemsListView;
	private Tab2Page2ListAdapter Tab2Page2Adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab2page2);
		app = (App) getApplication();
		aq = new AQuery(this);

		// 获取listview对象
		ItemsListView = (MyListView) findViewById(R.id.listView1);
		// 设置listview的点击事件监听器
		ItemsListView.setOnItemClickListener(this);
		ItemsListView.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				
				new GetDataTask().execute();
				
				GetServiceData();
		}});
		CheckSaveData();
	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				;
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			ItemsListView.onRefreshComplete();
//			Tab2Page2Adapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}
	}

	public void OnClickTab1TopLeft(View v) {
		Intent i = new Intent(this, Search.class);
		startActivity(i);

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
		dataStruct = new ArrayList();

		NotifyDataAnalysisFinished();
		if (m_ReturnTops.tops == null)
			return;
		for (int i = 0; i < m_ReturnTops.tops.length; i++) {
			Tab2Page2ListData m_Tab2Page2ListData = new Tab2Page2ListData();
			m_Tab2Page2ListData.Pic_ID = m_ReturnTops.tops[i].id;
			m_Tab2Page2ListData.Pic_url = m_ReturnTops.tops[i].pic_url;
			m_Tab2Page2ListData.Pic_name = m_ReturnTops.tops[i].name;
			m_Tab2Page2ListData.right = m_ReturnTops.tops[i].prod_type;
			if (m_ReturnTops.tops[i].items != null) {
				for (int j = 0; j < m_ReturnTops.tops[i].items.length; j++) {
					m_j = m_ReturnTops.tops[i].items[j].prod_name;
					if (m_j != null) {
						switch (j) {
						case 0:
							m_Tab2Page2ListData.Pic_list1 = m_j;
							break;
						case 1:
							m_Tab2Page2ListData.Pic_list2 = m_j;
							break;
						case 2:
							m_Tab2Page2ListData.Pic_list3 = m_j;
							break;
						case 3:
							m_Tab2Page2ListData.Pic_list4 = m_j;
							break;
						case 4:
							m_Tab2Page2ListData.Pic_list5 = m_j;
							break;
						case 5:
							m_Tab2Page2ListData.Pic_list6 = m_j;
							break;
						}

					}

				}

			}
			dataStruct.add(m_Tab2Page2ListData);
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
		if (status.getCode() == AjaxStatus.NETWORK_ERROR) {
			aq.id(R.id.ProgressText).gone();
			app.MyToast(aq.getContext(),
					getResources().getString(R.string.networknotwork));
			return;
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			m_ReturnTops = mapper.readValue(json.toString(), ReturnTops.class);
			if (m_ReturnTops.tops.length > 0)
				app.SaveServiceData("movie_tops", json.toString());

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
		Toast toast;
		if (dataStruct != null && ItemsListView != null) {
			Tab2Page2ListAdapter listviewdetailadapter = getAdapter();
			ItemsListView.setAdapter(listviewdetailadapter);
		} else {
			app.MyToast(this, "ItemsListView empty.");
		}
	}

	private Tab2Page2ListAdapter getAdapter() {
		if (Tab2Page2Adapter == null) {
			ArrayList arraylist = dataStruct;
			Tab2Page2ListAdapter listviewdetailadapter = new Tab2Page2ListAdapter(
					this, arraylist);
			Tab2Page2Adapter = listviewdetailadapter;
		} else {
			ArrayList arraylist1 = dataStruct;
			Tab2Page2ListAdapter listviewdetailadapter1 = new Tab2Page2ListAdapter(
					this, arraylist1);
			Tab2Page2Adapter = listviewdetailadapter1;
		}
		return Tab2Page2Adapter;
	}

	// listview的点击事件接口函数
	@Override
	public void onItemClick(AdapterView adapterview, View view, int i, long l) {
		Tab2Page2ListData m_Tab2Page2ListData = (Tab2Page2ListData) ItemsListView
				.getItemAtPosition(i);
		if (m_ReturnTops != null) {
			// app.MyToast(this, m_Tab2Page2ListData.Pic_name,
			// Toast.LENGTH_LONG).show();
			Intent intent = new Intent(this, Detail_BangDan.class);
			intent.putExtra("BangDan_id", m_Tab2Page2ListData.Pic_ID);
			intent.putExtra("BangDan_name", m_Tab2Page2ListData.Pic_name);
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
		SaveData = app.GetServiceData("movie_tops");
		if (SaveData == null) {
			GetServiceData();
		} else {
			try {
				m_ReturnTops = mapper.readValue(SaveData, ReturnTops.class);
				// 创建数据源对象
				GetVideoMovies();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// execute the task
						GetServiceData();
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

	// InitListData
	public void GetServiceData() {
		String url = Constant.BASE_URL + "movie_tops";

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class).weakHandler(this, "InitListData");

		cb.SetHeader(app.getHeaders());
		if(app.GetServiceData("movie_tops")==null)
		{
			aq.id(R.id.ProgressText).visible();
			aq.progress(R.id.progress).ajax(cb);
		}
		else
		{
			aq.ajax(cb);
		}
		

	}
}