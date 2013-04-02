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
import com.joyplus.Adapters.Tab2Page3ListAdapter;
import com.joyplus.Adapters.Tab2Page3ListData;
import com.joyplus.Service.Return.ReturnTops;
import com.joyplus.widget.MyListView;
import com.joyplus.widget.MyListView.OnRefreshListener;
import com.joyplus.widget.RefreshListView;

public class Tab2Page3 extends Activity implements
		android.widget.AdapterView.OnItemClickListener,RefreshListView.IOnRefreshListener,
		RefreshListView.IOnLoadMoreListener {
	private String TAG = "Tab2Page3";
	protected AQuery aq;
	private App app;
	private ReturnTops m_ReturnTops = null;

	private ArrayList dataStruct;
	private RefreshListView ItemsListView;
	private Tab2Page3ListAdapter Tab2Page3Adapter;
	
	private RefreshDataAsynTask mRefreshAsynTask;
	private LoadMoreDataAsynTask mLoadMoreAsynTask;
	private int isLastisNext = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab2page3);
		app = (App) getApplication();
		aq = new AQuery(this);
		// 获取listview对象
		ItemsListView = (RefreshListView) findViewById(R.id.listView1);
		// 设置listview的点击事件监听器
		ItemsListView.setOnItemClickListener(this);
		
		ItemsListView.setOnRefreshListener(this);
		ItemsListView.setOnLoadMoreListener(this);
		dataStruct = new ArrayList();
		CheckSaveData();
	}
	
	class RefreshDataAsynTask extends AsyncTask<Void , Void, Void>
	{

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			isLastisNext = 1;
			GetServiceData(isLastisNext);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			ItemsListView.onRefreshComplete();
		}
	}
	
	class LoadMoreDataAsynTask extends AsyncTask<Void , Void, Void>
	{

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			isLastisNext++;
			GetServiceData(isLastisNext++);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			ItemsListView.onLoadMoreComplete(false);
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
		
		if (m_ReturnTops.tops == null)
			return;
		if(isLastisNext==1)
		{
			dataStruct = new ArrayList();
		}
		if(isLastisNext>1)
		{
			for (int i = 0; i < m_ReturnTops.tops.length; i++) {

				if (m_ReturnTops.tops[i].items != null) {
					for (int j = 0; j < m_ReturnTops.tops[i].items.length; j++) {
						Tab2Page3ListData m_Tab2Page3ListData = new Tab2Page3ListData();
						m_Tab2Page3ListData.Pic_ID = m_ReturnTops.tops[i].items[j].prod_id;
						m_Tab2Page3ListData.Pic_url = m_ReturnTops.tops[i].items[j].prod_pic_url;
						m_Tab2Page3ListData.Pic_name = m_ReturnTops.tops[i].items[j].prod_name;
						m_Tab2Page3ListData.Pic_list1 = m_ReturnTops.tops[i].items[j].cur_item_name;
						if(!dataStruct.contains(m_Tab2Page3ListData))
						{
							dataStruct.add(m_Tab2Page3ListData);
						}
					}
				}
				Tab2Page3Adapter.notifyDataSetChanged();
			}

			return;
		}
		else
		{
			NotifyDataAnalysisFinished();
		}
		for (int i = 0; i < m_ReturnTops.tops.length; i++) {

			if (m_ReturnTops.tops[i].items != null) {
				for (int j = 0; j < m_ReturnTops.tops[i].items.length; j++) {
					Tab2Page3ListData m_Tab2Page3ListData = new Tab2Page3ListData();
					m_Tab2Page3ListData.Pic_ID = m_ReturnTops.tops[i].items[j].prod_id;
					m_Tab2Page3ListData.Pic_url = m_ReturnTops.tops[i].items[j].prod_pic_url;
					m_Tab2Page3ListData.Pic_name = m_ReturnTops.tops[i].items[j].prod_name;
					m_Tab2Page3ListData.Pic_list1 = m_ReturnTops.tops[i].items[j].cur_item_name;
					if(!dataStruct.contains(m_Tab2Page3ListData))
					{
						dataStruct.add(m_Tab2Page3ListData);
					}
				}
			}
			break;
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
			if (isLastisNext > 1)
				m_ReturnTops = null;
			m_ReturnTops = mapper.readValue(json.toString(), ReturnTops.class);
			if (m_ReturnTops.tops.length > 0)
				app.SaveServiceData("show_tops", json.toString());
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
			Tab2Page3ListAdapter listviewdetailadapter = getAdapter();
			ItemsListView.setAdapter(listviewdetailadapter);
		} else {
			app.MyToast(this, "ItemsListView empty.");
		}
	}

	private Tab2Page3ListAdapter getAdapter() {
		if (Tab2Page3Adapter == null) {
			ArrayList arraylist = dataStruct;
			Tab2Page3ListAdapter listviewdetailadapter = new Tab2Page3ListAdapter(
					this, arraylist);
			Tab2Page3Adapter = listviewdetailadapter;
		} else {
			ArrayList arraylist1 = dataStruct;
			Tab2Page3ListAdapter listviewdetailadapter1 = new Tab2Page3ListAdapter(
					this, arraylist1);
			Tab2Page3Adapter = listviewdetailadapter1;
		}
		return Tab2Page3Adapter;
	}

	// listview的点击事件接口函数
	@Override
	public void onItemClick(AdapterView adapterview, View view, int i, long l) {
		Tab2Page3ListData m_Tab2Page3ListData = (Tab2Page3ListData) ItemsListView
				.getItemAtPosition(i);
		if (m_ReturnTops != null) {
			// app.MyToast(this, m_Tab2Page3ListData.Pic_name,
			// Toast.LENGTH_LONG).show();
			Intent intent = new Intent(this, Detail_Show.class);
			// app.setM_ReturnTops(m_ReturnTops);
			intent.putExtra("prod_id", m_Tab2Page3ListData.Pic_ID);
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException ex) {
				Log.e(TAG, "Call Detail_Show failed", ex);
			}
		} else {
			app.MyToast(this, "ReturnTops is empty.");
		}

	}

	private void CheckSaveData() {
		String SaveData = null;
		ObjectMapper mapper = new ObjectMapper();
		SaveData = app.GetServiceData("show_tops");
		if (SaveData == null) {
			isLastisNext = 1;
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

	// InitListData
	public void GetServiceData(int index) {
		String url = Constant.BASE_URL + "show_tops"+"?page_num="
				+ Integer.toString(index) + "&page_size=30";;

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class).weakHandler(this, "InitListData");

		cb.SetHeader(app.getHeaders());
		if(app.GetServiceData("show_tops")==null)
		{
			aq.id(R.id.ProgressText).visible();
			aq.progress(R.id.progress).ajax(cb);
		}
		else
		{
			aq.ajax(cb);
		}

	}

	@Override
	public void OnLoadMore() {
		// TODO Auto-generated method stub
		mLoadMoreAsynTask = new LoadMoreDataAsynTask();
		mLoadMoreAsynTask.execute();
	}

	@Override
	public void OnRefresh() {
		// TODO Auto-generated method stub
		mRefreshAsynTask = new RefreshDataAsynTask();
		mRefreshAsynTask.execute();
	}
}