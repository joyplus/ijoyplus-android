package com.joyplus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
//import java.util.List;
import java.util.Map;

import org.json.JSONArray;
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
//import android.os.Handler;
import com.joyplus.widget.Log;
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
import com.joyplus.Adapters.Tab3Page2ListData;
import com.joyplus.Service.Return.ReturnUserFavorities;
import com.parse.ParseInstallation;
import com.umeng.analytics.MobclickAgent;

public class WeixinPage2 extends Activity implements OnTabActivityResultListener {
	private String TAG = "Tab3Page2";
	private AQuery aq;
	protected AQuery listAq;
	private App app;
	private ReturnUserFavorities m_ReturnUserFavorities = null;

	private ArrayList dataStruct;
	private ListView ItemsListView;
	private Tab3Page2ListAdapter Tab3Page2Adapter;
	private int isLastisNext = 1;
	private static String COLLECTION_LIST  = "我的收藏";
	Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weixinpage2);
		app = (App) getApplication();
		mContext = this;
		aq = new AQuery(this);
		aq.id(R.id.Layout1).gone();
		aq.id(R.id.Layout2).gone();

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
		aq.id(R.id.Layout1).gone();
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
		Tab3Page2Adapter = new Tab3Page2ListAdapter();
		ItemsListView.setAdapter(Tab3Page2Adapter);
		isLastisNext = 1;
		CheckSaveData();
		GetServiceData(isLastisNext);
		MobclickAgent.onEventBegin(mContext, COLLECTION_LIST);
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onEventEnd(mContext, COLLECTION_LIST);
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	public void GetVideoMovies() {
		// String m_j = null;

		if (m_ReturnUserFavorities.favorities == null) {
			if (isLastisNext == 1) {
				aq.id(R.id.imageNoitemBG).visible();
				aq.id(R.id.Layout1).gone();
				aq.id(R.id.Layout2).gone();
			}
			return;
		}
		if(isLastisNext == 1)
		{
			for(int j = dataStruct.size()-1;j>-1;j--)
			{
				dataStruct.remove(j);
			}
			dataStruct.clear();
		}
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
			if (dataStruct.contains(m_Tab3Page2ListData)) {

			} else {
				dataStruct.add(m_Tab3Page2ListData);
			}
		}
		Tab3Page2Adapter.notifyDataSetChanged();

		int m_num = dataStruct.size();

		if (m_num == 0) {
			aq.id(R.id.imageNoitemBG).visible();
			aq.id(R.id.Layout1).gone();
			aq.id(R.id.Layout2).gone();
		} else {
			aq.id(R.id.imageNoitemBG).gone();
			aq.id(R.id.Layout1).visible();
			aq.id(R.id.Layout2).gone();
		}

	}

	public void OnClickImageView(View v) {
		
	}

	// 初始化list数据函数
	public void InitListData(String url, JSONObject json, AjaxStatus status) {
		if (status.getCode() == AjaxStatus.NETWORK_ERROR&&app.GetServiceData("user_favorities")==null) {
			aq.id(R.id.ProgressText).gone();
			app.MyToast(aq.getContext(),
					getResources().getString(R.string.networknotwork));
			return;
		}
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (isLastisNext == 1) {
				m_ReturnUserFavorities = mapper.readValue(json.toString(),
						ReturnUserFavorities.class);
				app.SaveServiceData("user_favorities", json.toString());
			}
			else if (isLastisNext > 1)
			{
				m_ReturnUserFavorities = null;
				app.SaveServiceData("user_favorities", json.toString());
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

	private void CheckSaveData() {
		String SaveData = null;
		ObjectMapper mapper = new ObjectMapper();
		SaveData = app.GetServiceData("user_favorities");
		if (SaveData == null) {
			isLastisNext = 1;
			GetServiceData(isLastisNext);
		} else {
			try {
				m_ReturnUserFavorities = mapper.readValue(SaveData,
						ReturnUserFavorities.class);
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

	// InitListData
	public void GetServiceData(int index) {
		String url = Constant.BASE_URL + "user/favorities" + "?page_num="
				+ Integer.toString(index) + "&page_size=10";

		// String url = Constant.BASE_URL + "user/favorities";

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();

		cb.url(url).type(JSONObject.class).weakHandler(this, "InitListData");

		cb.SetHeader(app.getHeaders());

		aq.ajax(cb);
	}

	private void GotoDetail(int item) {
		Tab3Page2ListData m_Tab3Page2ListData = (Tab3Page2ListData) ItemsListView
				.getItemAtPosition(item);
		Intent intent = new Intent();

		intent.setClass(this, Weixin_ShareVideo.class);
		intent.putExtra("prod_id", m_Tab3Page2ListData.Pic_ID);
		intent.putExtra("prod_name", m_Tab3Page2ListData.Pic_name);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			Log.e(TAG, "Call Detail_Movie failed", ex);
		}
	}

	private static class ViewHolder {
		public ImageView mImageView;
		public TextView mName;
		public TextView mName1;
		public TextView mYear;
	}

	public class Tab3Page2ListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return dataStruct.size();
		}

		@Override
		public Tab3Page2ListData getItem(int position) {
			return (Tab3Page2ListData) dataStruct.get(position);
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
						R.layout.tab3page2_detail_list, viewgroup, false);

				holder = new ViewHolder();

				holder.mImageView = (ImageView) view
						.findViewById(R.id.video_preview_img);
				holder.mName = (TextView) view
						.findViewById(R.id.txt_video_caption);
				holder.mName1 = (TextView) view.findViewById(R.id.TextView03);
				holder.mYear = (TextView) view.findViewById(R.id.TextView04);

				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			// 获取当前数据项的数据
			Tab3Page2ListData m_Tab3Page2ListData = (Tab3Page2ListData) getItem(i);

			AQuery aqlist = aq.recycle(view);
			aqlist.id(holder.mName).text(m_Tab3Page2ListData.Pic_name);
			aqlist.id(holder.mName1).text(m_Tab3Page2ListData.Text_Zhuyan);
			aqlist.id(holder.mYear).text(m_Tab3Page2ListData.Text_Year);
			aqlist.id(holder.mImageView).image(m_Tab3Page2ListData.Pic_url,
					true, true);
			return view;
		}
	}

	@Override
	public void onTabActivityResult(int requestCode, int resultCode, Intent data) {

	}

}
