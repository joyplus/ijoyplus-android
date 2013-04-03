package com.joyplus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
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
import com.joyplus.Adapters.Tab3Page3ListData;
import com.joyplus.Service.Return.ReturnTops;
import com.joyplus.Service.Return.ReturnUserPlayHistories;
import com.umeng.analytics.MobclickAgent;

public class Tab3Page3 extends Activity implements OnTabActivityResultListener {
	private String TAG = "Tab3Page3";
	private AQuery aq;
	private App app;
	private ReturnTops m_ReturnTops = null;

	private ArrayList dataStruct;
	private ListView ItemsListView;
	private Tab3Page3ListAdapter Tab3Page3Adapter;
	private int isLastisNext = 1;
	private static String MY_TOP_LIST  = "我的悦单";
	Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab3page3);
		app = (App) getApplication();
		aq = new AQuery(this);
		mContext = this;
		aq.id(R.id.linearLayout1).gone();
		aq.id(R.id.button2).gone();
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
		ItemsListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				OnDeleteListItem(arg2);
				return true;// 如果返回false那么onItemClick仍然会被调用
			}
		});
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
		dataStruct = new ArrayList();
		Tab3Page3Adapter = new Tab3Page3ListAdapter();
		ItemsListView.setAdapter(Tab3Page3Adapter);
		isLastisNext=1;
		CheckSaveData();
		GetServiceData(isLastisNext);
		MobclickAgent.onEventBegin(mContext, MY_TOP_LIST);
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onEventEnd(mContext, MY_TOP_LIST);
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
		if(isLastisNext == 1)
		{
			for(int i = 0;i<dataStruct.size();i++)
			{
				dataStruct.remove(i);
			}
			dataStruct.clear();
		}
		for (int i = 0; i < m_ReturnTops.tops.length; i++) {
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
			if (dataStruct.contains(m_Tab3Page3ListData)) {

			} else {
				dataStruct.add(m_Tab3Page3ListData);
			}
		}
		Tab3Page3Adapter.notifyDataSetChanged();
		int m_num = dataStruct.size();
		if (m_num == 0) {
			aq.id(R.id.linearLayout1).gone();
		} else
			aq.id(R.id.linearLayout1).visible();

		aq.id(R.id.Layout2).gone();

	}

	public void OnClickImageView(View v) {
		/*
		 * 
		 */
	}

	// 初始化list数据函数
	public void InitListData(String url, JSONObject json, AjaxStatus status) {
		if (status.getCode() == AjaxStatus.NETWORK_ERROR&&app.GetServiceData("user_tops33")==null) {
			aq.id(R.id.ProgressText).gone();
			app.MyToast(aq.getContext(),
					getResources().getString(R.string.networknotwork));
			return;
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			if(isLastisNext == 1)
			{
				m_ReturnTops = mapper.readValue(json.toString(), ReturnTops.class);
				app.SaveServiceData("user_tops33", json.toString());
			}
			else if (isLastisNext > 1)
			{
				m_ReturnTops = null;
				m_ReturnTops = mapper.readValue(json.toString(), ReturnTops.class);
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

	// listview的点击事件接口函数
	public void onItemClick(AdapterView adapterview, View view, int i, long l) {

	}

	// InitListData
	public void GetServiceData(int index) {
		String url = Constant.BASE_URL + "user/tops" + "?page_num="
				+ Integer.toString(index) + "&page_size=10";
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class).weakHandler(this, "InitListData");
		cb.SetHeader(app.getHeaders());
		aq.ajax(cb);
	}

	/*
	 * 从本地缓存取数据,然后从服务器抓数据下来
	 */
	private void CheckSaveData() {
		String SaveData = null;
		ObjectMapper mapper = new ObjectMapper();
		SaveData = app.GetServiceData("user_tops33");
		if (SaveData == null) {
			isLastisNext = 1;
			GetServiceData(isLastisNext);
		} else {
			try {
				m_ReturnTops = mapper.readValue(SaveData, ReturnTops.class);
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
	}

	public void OnClickCreate(View v) {

		Intent intent = new Intent(getParent(), Tab3Page3_Create1.class);
		getParent().startActivityForResult(intent, 1);
	}

	private void TopDel(String topic_id) {
		String url = Constant.BASE_URL + "top/del";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("topic_id", topic_id);

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.SetHeader(app.getHeaders());

		cb.params(params).url(url).type(JSONObject.class)
				.weakHandler(this, "TopDelResult");
		aq.ajax(cb);
	}

	public void TopDelResult(String url, JSONObject json, AjaxStatus status) {
		if (json != null) {
			try {
				if (json.getString("res_code").trim().equalsIgnoreCase("00000")) {
					app.MyToast(this, "删除成功!");
					// GetServiceData(1);
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

	private static class ViewHolder {
		public ImageView mImageView;
		public TextView mName;
		public TextView mName1;
	}

	public class Tab3Page3ListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return dataStruct.size();
		}

		@Override
		public Tab3Page3ListData getItem(int position) {
			return (Tab3Page3ListData) dataStruct.get(position);
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
						R.layout.tab3page3_detail_list, viewgroup, false);

				holder = new ViewHolder();

				holder.mImageView = (ImageView) view
						.findViewById(R.id.video_preview_img);
				holder.mName = (TextView) view
						.findViewById(R.id.txt_video_caption);
				holder.mName1 = (TextView) view.findViewById(R.id.txt_1);

				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			// 获取当前数据项的数据
			Tab3Page3ListData m_Tab3Page3ListData = (Tab3Page3ListData) getItem(i);

			AQuery aqlist = aq.recycle(view);
			aqlist.id(holder.mName).text(m_Tab3Page3ListData.Pic_name);
			aqlist.id(holder.mName1).text(m_Tab3Page3ListData.Pic_list1);
			aqlist.id(holder.mImageView).image(m_Tab3Page3ListData.Pic_url,
					true, true);
			return view;
		}
	}

	@Override
	public void onTabActivityResult(int requestCode, int resultCode, Intent data) {
		/*
		 * 
		 */
	}
}
