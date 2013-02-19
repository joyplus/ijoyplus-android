package com.joyplus;

import com.umeng.analytics.MobclickAgent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyplus.Adapters.Tab3Page2ListAdapter;
import com.joyplus.Adapters.Tab3Page2ListData;
import com.joyplus.Service.Return.ReturnUserFavorities;

public class Tab3Page2_more extends Activity {
	private String TAG = "Tab3Page2";
	private AQuery aq;
	private App app;
	private ReturnUserFavorities m_ReturnUserFavorities = null;

	private int Fromepage;
	private ArrayList dataStruct;
	private ListView ItemsListView;
	private Tab3Page2ListAdapter Tab3Page2Adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab3page2_more);
		app = (App) getApplication();
		aq = new AQuery(this);
		// 获取listview对象
		ItemsListView = (ListView) findViewById(R.id.listView1);
		// 设置listview的点击事件监听器
		ItemsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				GotoDetail(position);

			}
		});
		ItemsListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				OnDeleteListItem(arg2);
				return true;// 如果返回false那么onItemClick仍然会被调用
			}
		});
		// CheckSaveData();
		GetServiceData();
	}

	public void OnClickTab1TopLeft(View v) {
		Intent i = new Intent();

		this.setResult(101, i);
		this.finish();
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
		if (m_ReturnUserFavorities.favorities == null)
			return;
		for (int i = 0; i < m_ReturnUserFavorities.favorities.length; i++) {
			Tab3Page2ListData m_Tab3Page2ListData = new Tab3Page2ListData();

			m_Tab3Page2ListData.Pic_ID = m_ReturnUserFavorities.favorities[i].content_id;
			m_Tab3Page2ListData.Pic_url = m_ReturnUserFavorities.favorities[i].content_pic_url;
			m_Tab3Page2ListData.Pic_name = m_ReturnUserFavorities.favorities[i].content_name;
			m_Tab3Page2ListData.prod_type = m_ReturnUserFavorities.favorities[i].content_type;
			m_Tab3Page2ListData.Text_Zhuyan = m_ReturnUserFavorities.favorities[i].stars;
			m_Tab3Page2ListData.Text_Year = m_ReturnUserFavorities.favorities[i].publish_date;

			m_Tab3Page2ListData.Text_Area = m_ReturnUserFavorities.favorities[i].area;
			m_Tab3Page2ListData.Text_Score = m_ReturnUserFavorities.favorities[i].score;

			dataStruct.add(m_Tab3Page2ListData);
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
		if (json == null) {
			aq.id(R.id.ProgressText).gone();
			app.MyToast(aq.getContext(),
					getResources().getString(R.string.networknotwork));
			return;
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			m_ReturnUserFavorities = mapper.readValue(json.toString(),
					ReturnUserFavorities.class);
			app.SaveServiceData("user_favorities", json.toString());

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
			Tab3Page2ListAdapter listviewdetailadapter = getAdapter();
			ItemsListView.setAdapter(listviewdetailadapter);
		} else {
			app.MyToast(this, "ItemsListView empty.");
		}
	}

	private Tab3Page2ListAdapter getAdapter() {
		if (Tab3Page2Adapter == null) {
			ArrayList arraylist = dataStruct;
			Tab3Page2ListAdapter listviewdetailadapter = new Tab3Page2ListAdapter(
					this, arraylist);
			Tab3Page2Adapter = listviewdetailadapter;
		} else {
			ArrayList arraylist1 = dataStruct;
			Tab3Page2ListAdapter listviewdetailadapter1 = new Tab3Page2ListAdapter(
					this, arraylist1);
			Tab3Page2Adapter = listviewdetailadapter1;
		}
		return Tab3Page2Adapter;
	}

	// // listview的点击事件接口函数
	// public void onItemClick(AdapterView adapterview, View view, int i, long
	// l) {
	// Tab3Page2ListData m_Tab3Page2ListData = (Tab3Page2ListData) ItemsListView
	// .getItemAtPosition(i);
	// if (m_ReturnUserFavorities != null) {
	// app.MyToast(this, m_Tab3Page2ListData.Pic_name,
	// Toast.LENGTH_LONG).show();
	//
	// } else {
	// app.MyToast(this, "ReturnUserFavorities is empty.",
	// Toast.LENGTH_LONG).show();
	// }
	//
	// }

	private void CheckSaveData() {
		String SaveData = null;
		ObjectMapper mapper = new ObjectMapper();
		SaveData = app.GetServiceData("user_favorities");
		if (SaveData == null) {
			GetServiceData();
		} else {
			try {
				m_ReturnUserFavorities = mapper.readValue(SaveData,
						ReturnUserFavorities.class);
				// 创建数据源对象
				GetVideoMovies();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// execute the task
						GetServiceData();
					}
				}, 150000);

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

	private void OnDeleteListItem(final int item) {
		final Tab3Page2ListData m_Tab3Page2ListData = (Tab3Page2ListData) ItemsListView
				.getItemAtPosition(item);
		String program_name = "你确定删除  " + m_Tab3Page2ListData.Pic_name + "  吗？";
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("我的收藏").setMessage(program_name)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dataStruct.remove(item);
						Tab3Page2Adapter.notifyDataSetChanged();

						ItemsListView.invalidate();
						// 删除数据
						Unfavority(m_Tab3Page2ListData.Pic_ID);

					}
				}).setNegativeButton("取消", null).create();
		builder.show();
	}

	private void Unfavority(String prod_id) {
		String url = Constant.BASE_URL + "program/unfavority";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("prod_id", prod_id);

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.header("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
		cb.header("app_key", Constant.APPKEY);
		cb.header("user_id", app.UserID);

		cb.params(params).url(url).type(JSONObject.class)
				.weakHandler(this, "UnfavorityResult");

		aq.ajax(cb);
	}

	public void UnfavorityResult(String url, JSONObject json, AjaxStatus status) {
		if (json != null) {
			try {
				if (json.getString("res_code").trim().equalsIgnoreCase("00000"))
					app.MyToast(this, "删除收藏成功!");
				else
					app.MyToast(this, "删除收藏失败!");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {

			// ajax error, show error code
			app.MyToast(this, getResources().getString(R.string.networknotwork));
		}
	}

	// InitListData
	public void GetServiceData() {
		String url = Constant.BASE_URL
				+ "/user/favorities?page_num=1&page_size=60";

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class).weakHandler(this, "InitListData");

		cb.header("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
		cb.header("app_key", Constant.APPKEY);
		cb.header("user_id", app.UserID);

		aq.id(R.id.ProgressText).visible();
		aq.progress(R.id.progress).ajax(cb);

	}

	private void GotoDetail(int item) {
		Tab3Page2ListData m_Tab3Page2ListData = (Tab3Page2ListData) ItemsListView
				.getItemAtPosition(item);
		// System.out.println("Item clicked. Position:" + item);
		// app.MyToast(this, m_Tab3Page2ListData.Pic_name, Toast.LENGTH_LONG)
		// .show();
		Intent intent = new Intent();
		// 1：电影，2：电视剧，3：综艺，4：视频
		switch (Integer.valueOf(m_Tab3Page2ListData.prod_type)) {
		case 1:
			intent.setClass(this, Detail_Movie.class);
			intent.putExtra("prod_id", m_Tab3Page2ListData.Pic_ID);
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException ex) {
				Log.e(TAG, "Call Detail_Movie failed", ex);
			}
			break;
		case 2:
			intent.setClass(this, Detail_TV.class);
			intent.putExtra("prod_id", m_Tab3Page2ListData.Pic_ID);
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException ex) {
				Log.e(TAG, "Call Detail_TV failed", ex);
			}
			break;
		case 3:
			intent.setClass(this, Detail_Show.class);
			intent.putExtra("prod_id", m_Tab3Page2ListData.Pic_ID);
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException ex) {
				Log.e(TAG, "Call Detail_Show failed", ex);
			}
			break;
		}
	}
}
