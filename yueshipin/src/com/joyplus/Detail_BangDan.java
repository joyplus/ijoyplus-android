package com.joyplus;

import com.umeng.analytics.MobclickAgent;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyplus.Adapters.BangDanListAdapter;
import com.joyplus.Adapters.BangDanListData;
import com.joyplus.Service.Return.ReturnTops;

public class Detail_BangDan extends Activity implements
		android.widget.AdapterView.OnItemClickListener {
	private String TAG = "Detail_BangDan";
	private AQuery aq;
	private App app;
	private ReturnTops m_ReturnTops = null;
	private String BangDan_id = null;
	private String BangDan_name = null;

	private ArrayList dataStruct;
	private ListView ItemsListView;
	private BangDanListAdapter BangDanAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_bangdan);
		Intent intent = getIntent();
		BangDan_id = intent.getStringExtra("BangDan_id");
		BangDan_name = intent.getStringExtra("BangDan_name");

		app = (App) getApplication();
		aq = new AQuery(this);
		if (BangDan_name != null)
			aq.id(R.id.textView2).text(BangDan_name);
		// 获取listview对象
		ItemsListView = (ListView) findViewById(R.id.listView1);
		// 设置listview的点击事件监听器
		ItemsListView.setOnItemClickListener(this);
		CheckSaveData();
	}

	public void OnClickTab1TopLeft(View v) {
		finish();
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
			app.MyToast(this, getResources().getString(R.string.networknotwork));
			if(app.GetServiceData("top_items_" + BangDan_id)==null)
			{
				aq.id(R.id.none_net).visible();
			}
			return;
		}
		try {
			if (json.getJSONArray("items") != null) {
				app.SaveServiceData("top_items_" + BangDan_id, json.toString());

				// 创建数据源对象
				try {
					ShowList(json.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				aq.id(R.id.ProgressText).gone();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 数据更新
	public void NotifyDataAnalysisFinished() {
		Toast toast;
		if (dataStruct != null && ItemsListView != null) {
			BangDanListAdapter listviewdetailadapter = getAdapter();
			ItemsListView.setAdapter(listviewdetailadapter);
		} else {
			app.MyToast(this, "ItemsListView empty.");
		}
	}

	private BangDanListAdapter getAdapter() {
		if (BangDanAdapter == null) {
			ArrayList arraylist = dataStruct;
			BangDanListAdapter listviewdetailadapter = new BangDanListAdapter(
					this, arraylist);
			BangDanAdapter = listviewdetailadapter;
		} else {
			ArrayList arraylist1 = dataStruct;
			BangDanListAdapter listviewdetailadapter1 = new BangDanListAdapter(
					this, arraylist1);
			BangDanAdapter = listviewdetailadapter1;
		}
		return BangDanAdapter;
	}

	// listview的点击事件接口函数
	@Override
	public void onItemClick(AdapterView adapterview, View view, int i, long l) {
		BangDanListData m_BangDanListData = (BangDanListData) ItemsListView
				.getItemAtPosition(i);
		// app.MyToast(this, m_BangDanListData.Pic_name, Toast.LENGTH_LONG)
		// .show();
		Intent intent = new Intent();
		// 1：电影，2：电视剧，3：综艺，4：视频
		switch (Integer.valueOf(m_BangDanListData.prod_type)) {
		case 1:
			intent.setClass(this, Detail_Movie.class);
			intent.putExtra("prod_id", m_BangDanListData.Pic_ID);
			intent.putExtra("prod_name", m_BangDanListData.Pic_name);
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException ex) {
				Log.e(TAG, "Call Detail_Movie failed", ex);
			}
			break;
		case 2:
			intent.setClass(this, Detail_TV.class);
			intent.putExtra("prod_id", m_BangDanListData.Pic_ID);
			intent.putExtra("prod_name", m_BangDanListData.Pic_name);
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException ex) {
				Log.e(TAG, "Call Detail_TV failed", ex);
			}
			break;
		case 3:
			intent.setClass(this, Detail_Show.class);
			intent.putExtra("prod_id", m_BangDanListData.Pic_ID);
			intent.putExtra("prod_name", m_BangDanListData.Pic_name);
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException ex) {
				Log.e(TAG, "Call Detail_Show failed", ex);
			}
			break;
		}

	}

	private void CheckSaveData() {
		String SaveData = null;
		ObjectMapper mapper = new ObjectMapper();
		SaveData = app.GetServiceData("top_items_" + BangDan_id);
		if (SaveData == null) {
			GetServiceData();
		} else {
			try {
				m_ReturnTops = mapper.readValue(SaveData, ReturnTops.class);
				// 创建数据源对象
				try {
					ShowList(SaveData);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
		String url = Constant.BASE_URL + "top_items?top_id=" + BangDan_id
				+ "&page_num=1&page_size=50";

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class).weakHandler(this, "InitListData");

		cb.SetHeader(app.getHeaders());
		if(app.GetServiceData("top_items_" + BangDan_id)==null)
		{
			aq.id(R.id.ProgressText).visible();
			aq.progress(R.id.progress).ajax(cb);
		}
		else
		{
			aq.ajax(cb);
		}

	}

	private void ShowList(String SaveData) throws JSONException {
		dataStruct = new ArrayList();

		NotifyDataAnalysisFinished();
		String m_j = null;

		JSONObject json = new JSONObject(SaveData);
		JSONArray JSON_items = json.getJSONArray("items");

		for (int i = 0; i < JSON_items.length(); i++) {
			BangDanListData m_BangDanListData = new BangDanListData();
			m_BangDanListData.Pic_ID = JSON_items.getJSONObject(i).getString(
					"prod_id");
			m_BangDanListData.Pic_url = JSON_items.getJSONObject(i).getString(
					"prod_pic_url");
			m_BangDanListData.Pic_name = JSON_items.getJSONObject(i).getString(
					"prod_name");
			m_BangDanListData.prod_type = JSON_items.getJSONObject(i)
					.getString("prod_type");
			if (Integer.valueOf(JSON_items.getJSONObject(i).getString(
					"prod_type")) == 3) {
				if (JSON_items.getJSONObject(i).getString("stars").trim()
						.length() > 0)
					m_BangDanListData.Text_Zhuyan = JSON_items.getJSONObject(i)
							.getString("stars");
				else
					m_BangDanListData.Text_Zhuyan = JSON_items.getJSONObject(i)
							.getString("director");
			} else
				m_BangDanListData.Text_Zhuyan = JSON_items.getJSONObject(i)
						.getString("stars");

			m_BangDanListData.Text_Year = JSON_items.getJSONObject(i)
					.getString("publish_date");
			m_BangDanListData.Text_Area = JSON_items.getJSONObject(i)
					.getString("area");
			m_BangDanListData.Text_Ding = JSON_items.getJSONObject(i)
					.getString("support_num");
			m_BangDanListData.Text_Favority = JSON_items.getJSONObject(i)
					.getString("favority_num");
			m_BangDanListData.Text_Score = JSON_items.getJSONObject(i)
					.getString("score");
			dataStruct.add(m_BangDanListData);
		}

	}
}
