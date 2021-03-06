package com.joyplus;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyplus.Adapters.Tab2Page4ListAdapter;
import com.joyplus.Adapters.Tab2Page4ListData;
import com.joyplus.Service.Return.ReturnTops;
import com.joyplus.Tab2Page1.RefreshDataAsynTask;
import com.joyplus.widget.MyListView;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import com.joyplus.widget.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

public class Tab2Page4 extends Activity implements
		android.widget.AdapterView.OnItemClickListener,MyListView.IOnRefreshListener  {
	private String TAG = "Tab2Page4";
	private AQuery aq;
	private App app;
	private ReturnTops m_ReturnTops = null;

	private int Fromepage;
	private ArrayList dataStruct;
	private MyListView ItemsListView;
	private Tab2Page4ListAdapter Tab2Page4Adapter;
	private RefreshDataAsynTask mRefreshAsynTask;
	
	Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab2page4);
		app = (App) getApplication();
		aq = new AQuery(this);
		mContext = this;
		ItemsListView = (MyListView) findViewById(R.id.listView1);
		ItemsListView.setOnItemClickListener(this);
		ItemsListView.setOnRefreshListener(this);
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
			GetServiceData();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			ItemsListView.onRefreshComplete();
		}
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
			Tab2Page4ListData m_Tab2Page4ListData = new Tab2Page4ListData();
			m_Tab2Page4ListData.Pic_ID = m_ReturnTops.tops[i].id;
			m_Tab2Page4ListData.Pic_url = m_ReturnTops.tops[i].pic_url;
			m_Tab2Page4ListData.Pic_name = m_ReturnTops.tops[i].name;
			m_Tab2Page4ListData.right = m_ReturnTops.tops[i].prod_type;
			if (m_ReturnTops.tops[i].items != null) {
				for (int j = 0; j < m_ReturnTops.tops[i].items.length; j++) {
					m_j = m_ReturnTops.tops[i].items[j].prod_name;
					if (m_j != null) {
						switch (j) {
						case 0:
							m_Tab2Page4ListData.Pic_list1 = m_j;
							break;
						case 1:
							m_Tab2Page4ListData.Pic_list2 = m_j;
							break;
						case 2:
							m_Tab2Page4ListData.Pic_list3 = m_j;
							break;
						case 3:
							m_Tab2Page4ListData.Pic_list4 = m_j;
							break;
						case 4:
							m_Tab2Page4ListData.Pic_list5 = m_j;
							break;
						case 5:
							m_Tab2Page4ListData.Pic_list6 = m_j;
							break;
						}

					}

				}

			}
			dataStruct.add(m_Tab2Page4ListData);
		}

	}

	public void OnClickImageView(View v) {

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
				app.SaveServiceData("cart_tops", json.toString());
			// 创建视频源
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
			Tab2Page4ListAdapter listviewdetailadapter = getAdapter();
			ItemsListView.setAdapter(listviewdetailadapter);
		} else {
			app.MyToast(this, "ItemsListView empty.");
		}
	}

	private Tab2Page4ListAdapter getAdapter() {
		if (Tab2Page4Adapter == null) {
			ArrayList arraylist = dataStruct;
			Tab2Page4ListAdapter listviewdetailadapter = new Tab2Page4ListAdapter(
					this, arraylist);
			Tab2Page4Adapter = listviewdetailadapter;
		} else {
			ArrayList arraylist1 = dataStruct;
			Tab2Page4ListAdapter listviewdetailadapter1 = new Tab2Page4ListAdapter(
					this, arraylist1);
			Tab2Page4Adapter = listviewdetailadapter1;
		}
		return Tab2Page4Adapter;
	}

	// listview的点击事件接口函数
	@Override
	public void onItemClick(AdapterView adapterview, View view, int i, long l) {
		Tab2Page4ListData m_Tab2Page4ListData = (Tab2Page4ListData) ItemsListView
				.getItemAtPosition(i);
		if (m_ReturnTops != null) {
			// Toast.LENGTH_LONG).show();
			Intent intent = new Intent(this, Detail_BangDan.class);
			intent.putExtra("BangDan_id", m_Tab2Page4ListData.Pic_ID);
			intent.putExtra("BangDan_name", m_Tab2Page4ListData.Pic_name);
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
		SaveData = app.GetServiceData("cart_tops");
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
		String url = Constant.BASE_URL + "cart_tops";

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class).weakHandler(this, "InitListData");

		cb.SetHeader(app.getHeaders());
		if (app.GetServiceData("cart_tops") == null) {
			aq.id(R.id.ProgressText).visible();
			aq.progress(R.id.progress).ajax(cb);
		} else {
			aq.ajax(cb);
		}

	}

	@Override
	public void OnRefresh() {
		// TODO Auto-generated method stub
		mRefreshAsynTask = new RefreshDataAsynTask();
		mRefreshAsynTask.execute();
	}
}
