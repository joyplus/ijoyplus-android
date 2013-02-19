package com.joyplus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
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
import com.joyplus.Adapters.Tab3Page3ListAdapter;
import com.joyplus.Adapters.Tab3Page3ListData;
import com.joyplus.Service.Return.ReturnTops;
import com.umeng.analytics.MobclickAgent;

public class Tab3Page3 extends Activity implements OnTabActivityResultListener {
	private String TAG = "Tab3Page3";
	private AQuery aq;
	private App app;
	private ReturnTops m_ReturnTops = null;
	private static Context context;

	private ArrayList dataStruct;
	private ListView ItemsListView;
	private Tab3Page3ListAdapter Tab3Page3Adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab3page3);
		app = (App) getApplication();
		aq = new AQuery(this);
		aq.id(R.id.linearLayout1).gone();
		aq.id(R.id.button2).gone();
		context = this;
		// 获取listview对象
		ItemsListView = (ListView) findViewById(R.id.listView1);
		// 设置listview的点击事件监听器
		ItemsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Goto_Tab3Page3_Create2(position);

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
		GetServiceData();
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

		// if (m_ReturnTops.tops.length < 4)
		// aq.id(R.id.button2).gone();
		for (int i = 0; i < m_ReturnTops.tops.length && i < 3; i++) {
			Tab3Page3ListData m_Tab3Page3ListData = new Tab3Page3ListData();
			m_Tab3Page3ListData.Pic_ID = m_ReturnTops.tops[i].id;
			m_Tab3Page3ListData.Pic_url = m_ReturnTops.tops[i].pic_url;
			m_Tab3Page3ListData.Pic_name = m_ReturnTops.tops[i].name;
			m_Tab3Page3ListData.content = m_ReturnTops.tops[i].content;
			if (m_ReturnTops.tops[i].items != null) {
				for (int j = 0; j < m_ReturnTops.tops[i].items.length; j++) {
					m_j = m_ReturnTops.tops[i].items[j].prod_name;
					if (m_j != null) {
						m_j = "• " + m_j;
						switch (j) {
						case 0:
							m_Tab3Page3ListData.Pic_list1 = m_j;
							break;
						case 1:
							m_Tab3Page3ListData.Pic_list2 = m_j;
							break;
						}

					}

				}

			}
			dataStruct.add(m_Tab3Page3ListData);
		}
		if (m_ReturnTops.tops.length == 0) {
			aq.id(R.id.listView1).gone();
			aq.id(R.id.button2).gone();
		} else if (m_ReturnTops.tops.length <= 3) {
			aq.id(R.id.button2).gone();
		} else {
			aq.id(R.id.listView1).visible();
			aq.id(R.id.button2).visible();
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
			m_ReturnTops = mapper.readValue(json.toString(), ReturnTops.class);
			app.SaveServiceData("user_tops33", json.toString());
			aq.id(R.id.linearLayout1).visible();
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

	// 数据更新
	public void NotifyDataAnalysisFinished() {
		Toast toast;
		if (dataStruct != null && ItemsListView != null) {
			Tab3Page3ListAdapter listviewdetailadapter = getAdapter();
			ItemsListView.setAdapter(listviewdetailadapter);
		} else {
			app.MyToast(this, "ItemsListView empty.");
		}
	}

	private Tab3Page3ListAdapter getAdapter() {
		if (Tab3Page3Adapter == null) {
			ArrayList arraylist = dataStruct;
			Tab3Page3ListAdapter listviewdetailadapter = new Tab3Page3ListAdapter(
					this, arraylist);
			Tab3Page3Adapter = listviewdetailadapter;
		} else {
			ArrayList arraylist1 = dataStruct;
			Tab3Page3ListAdapter listviewdetailadapter1 = new Tab3Page3ListAdapter(
					this, arraylist1);
			Tab3Page3Adapter = listviewdetailadapter1;
		}
		return Tab3Page3Adapter;
	}

	// listview的点击事件接口函数
	public void onItemClick(AdapterView adapterview, View view, int i, long l) {

	}

	// InitListData
	public void GetServiceData() {
		String url = Constant.BASE_URL + "user/tops";

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class).weakHandler(this, "InitListData");

		cb.header("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
		cb.header("app_key", Constant.APPKEY);
		cb.header("user_id", app.UserID);

		aq.ajax(cb);

	}

	private void CheckSaveData() {
		String SaveData = null;
		ObjectMapper mapper = new ObjectMapper();
		SaveData = app.GetServiceData("user_tops33");
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
				}, 10000);

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

	public void OnClickCreate(View v) {

		Intent intent = new Intent(getParent(), Tab3Page3_Create1.class);
		getParent().startActivityForResult(intent, 1);

	}

	public void OnClickMore(View v) {

		Intent intent = new Intent(this, Tab3Page3_more.class);
		getParent().startActivityForResult(intent, 2);

	}

	private void TopDel(String topic_id) {
		String url = Constant.BASE_URL + "top/del";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("topic_id", topic_id);

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.header("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
		cb.header("app_key", Constant.APPKEY);
		cb.header("user_id", app.UserID);

		cb.params(params).url(url).type(JSONObject.class)
				.weakHandler(this, "TopDelResult");

		aq.ajax(cb);
	}

	public void TopDelResult(String url, JSONObject json, AjaxStatus status) {
		if (json != null) {
			try {
				if (json.getString("res_code").trim().equalsIgnoreCase("00000")) {
					app.MyToast(this, "删除成功!");
					GetServiceData();
				} else
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

	private void OnDeleteListItem(final int item) {
		final Tab3Page3ListData m_Tab3Page3ListData = (Tab3Page3ListData) ItemsListView
				.getItemAtPosition(item);
		String program_name = "你确定删除  " + m_Tab3Page3ListData.Pic_name + "  吗？";
		AlertDialog.Builder builder = new AlertDialog.Builder(
				Tab3Page3.this.getParent());
		builder.setTitle("我的悦单").setMessage(program_name)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dataStruct.remove(item);
						Tab3Page3Adapter.notifyDataSetChanged();

						ItemsListView.invalidate();
						// if(m_ReturnTops.tops.length > 3){
						// GetServiceData();
						// }

						// 删除数据
						TopDel(m_Tab3Page3ListData.Pic_ID);

					}
				}).setNegativeButton("取消", null).create();
		builder.show();
	}

	private void Goto_Tab3Page3_Create2(int item) {
		Tab3Page3ListData m_Tab3Page3ListData = (Tab3Page3ListData) ItemsListView
				.getItemAtPosition(item);
		Intent intent = new Intent(this, Tab3Page3_Create2.class);
		intent.putExtra("topic_id", m_Tab3Page3ListData.Pic_ID);
		intent.putExtra("title", m_Tab3Page3ListData.Pic_name);
		intent.putExtra("content", m_Tab3Page3ListData.content);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			Log.e(TAG, "Call Tab3Page3_Create2 failed", ex);
		}
	}

	@Override
	public void onTabActivityResult(int requestCode, int resultCode, Intent data) {
		GetServiceData();
	}

}
