package com.ijoyplus;
import com.umeng.analytics.MobclickAgent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ijoyplus.Adapters.BangDanListAdapter;
import com.ijoyplus.Adapters.BangDanListData;
import com.ijoyplus.Adapters.BangDanListData;
import com.ijoyplus.Adapters.BangDanListData;
import com.ijoyplus.Service.Return.ReturnTops;

public class Tab3Page3_Create2 extends Activity {
	private String TAG = "Tab3Page3_Create2";
	private AQuery aq;
	private App app;
	//private ReturnTops m_ReturnTops = null;
	private String title = null;
	private String content = null;
	private String topic_id = null;
	private String type = null;
	private String topic_id_ready_have = null;

	private ArrayList dataStruct;
	private ListView ItemsListView;
	private BangDanListAdapter BangDanAdapter;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab3page3_create2);
		app = (App) getApplication();
		aq = new AQuery(this);
		
		ItemsListView = (ListView) findViewById(R.id.listView1);
		
		Intent intent = getIntent();
		title = intent.getStringExtra("title");
		content = intent.getStringExtra("content");
		//use topic_id 区分是否是新添加的悦单
		topic_id  = intent.getStringExtra("topic_id");
		type =  intent.getStringExtra("type");
		aq.id(R.id.textView2).text(title);
		aq.id(R.id.textView1).text(content);
		if (intent.getBooleanExtra("Create", false) ) {
			aq.id(R.id.Tab1TopLeftImage).gone();
			aq.id(R.id.button1).visible();
			aq.id(R.id.Tab1TopRightImage).visible();
//			AddBangDan();
		}
		else {
			aq.id(R.id.Tab1TopLeftImage).visible();
			aq.id(R.id.Tab1TopRightImage).gone();
			GetServiceData();
		}
		
		// 设置listview的点击事件监听器
		ItemsListView.setOnItemClickListener(new OnItemClickListener() {
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
		// GetServiceData();
	}

	public void OnClickAddMore(View v) {
		if (topic_id != null) {
			Intent i = new Intent(this, Search.class);
			i.putExtra("topic_id", topic_id);
			i.putExtra("type", type);
			if(topic_id_ready_have != null)
				i.putExtra("topic_id_ready_have", topic_id_ready_have);
			startActivityForResult(i, 1);
		}

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

	// 初始化list数据函数
	public void InitListData(String url, JSONObject json, AjaxStatus status) {
		if (json == null) {
			aq.id(R.id.ProgressText).gone();
			app.MyToast(
					aq.getContext(),
					getResources().getString(R.string.networknotwork));
			return;
		}
		try {
			if (json.getJSONArray("items") != null) {
				// 创建数据源对象
				try {
					ShowList(json.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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


	// InitListData
	public void GetServiceData() {
		String url = Constant.BASE_URL + "top_items?top_id=" + topic_id;

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class).weakHandler(this, "InitListData");

		cb.header("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
		cb.header("app_key", Constant.APPKEY);
		cb.header("user_id", app.UserID);

		aq.ajax(cb);

	}

	public void OnClickFinished(View v) {
		setResult(101);
		finish();
		// String search_word = aq.id(R.id.editText1).getText().toString();

		// GetServiceData(search_word);

	}
	private void ShowList(String SaveData) throws JSONException {
		dataStruct = new ArrayList();

		NotifyDataAnalysisFinished();
		String m_j = null;

		JSONObject json = new JSONObject(SaveData);
		JSONArray JSON_items = json.getJSONArray("items");

		for (int i = 0; i < JSON_items.length(); i++) {
			BangDanListData m_BangDanListData = new BangDanListData();
			m_BangDanListData.Item_ID = JSON_items.getJSONObject(i).getString(
					"id");
			m_BangDanListData.Pic_ID = JSON_items.getJSONObject(i).getString(
					"prod_id");
			topic_id_ready_have = topic_id_ready_have +m_BangDanListData.Pic_ID +"|";
			m_BangDanListData.Pic_url = JSON_items.getJSONObject(i).getString(
					"prod_pic_url");
			m_BangDanListData.Pic_name = JSON_items.getJSONObject(i).getString(
					"prod_name");
			m_BangDanListData.prod_type = JSON_items.getJSONObject(i)
					.getString("prod_type");
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
	private void GotoDetail(int item) {
		BangDanListData m_BangDanListData = (BangDanListData) ItemsListView
				.getItemAtPosition(item);
		System.out.println("Item clicked. Position:" + item);
		app.MyToast(this, m_BangDanListData.Pic_name);
		Intent intent = new Intent();
		// 1：电影，2：电视剧，3：综艺，4：视频
		switch (Integer.valueOf(m_BangDanListData.prod_type)) {
		case 1:
			intent.setClass(this, Detail_Movie.class);
			intent.putExtra("prod_id", m_BangDanListData.Pic_ID);
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException ex) {
				Log.e("Tab1", "Call Detail_Movie failed", ex);
			}
			break;
		case 2:
			intent.setClass(this, Detail_TV.class);
			intent.putExtra("prod_id", m_BangDanListData.Pic_ID);
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException ex) {
				Log.e("Tab1", "Call Detail_TV failed", ex);
			}
			break;
		case 3:
			intent.setClass(this, Detail_Show.class);
			intent.putExtra("prod_id", m_BangDanListData.Pic_ID);
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException ex) {
				Log.e("Tab1", "Call Detail_Show failed", ex);
			}
			break;
		}
	}
	private void OnDeleteListItem(final int item) {
		final BangDanListData m_BangDanListData = (BangDanListData) ItemsListView
				.getItemAtPosition(item);
		String program_name = "你确定删除  " + m_BangDanListData.Pic_name + "  吗？";
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("我的悦单").setMessage(program_name)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dataStruct= null;
						BangDanAdapter.notifyDataSetChanged();
						ItemsListView.invalidate();
						
						// 删除数据
						DeleteVideo(m_BangDanListData.Item_ID);
						
					}
				}).setNegativeButton("取消", null).create();
		builder.show();
	}
	private void DeleteVideo(String prod_id) {
		String url = Constant.BASE_URL + "top/removeItem";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("item_id", prod_id);

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.header("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
		cb.header("app_key", Constant.APPKEY);
		cb.header("user_id", app.UserID);

		cb.params(params).url(url).type(JSONObject.class)
				.weakHandler(this, "DeleteVideoResult");

		aq.ajax(cb);
	}
	public void DeleteVideoResult(String url, JSONObject json, AjaxStatus status) {
		if (json != null) {
			try {
				if (json.getString("res_code").trim().equalsIgnoreCase("00000")){
					app.MyToast(this, "删除成功!");
					GetServiceData();
				}
				else
					app.MyToast(this, "删除失败!");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {

			// ajax error, show error code
			app.MyToast(this, getResources().getString(R.string.networknotwork));
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		GetServiceData();
		super.onActivityResult(requestCode, resultCode, data);
	}
}
