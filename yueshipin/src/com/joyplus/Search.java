package com.joyplus;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.color;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyplus.Adapters.SearchListAdapter;
import com.joyplus.Adapters.SearchListData;
import com.joyplus.Service.Return.ReturnSearch;
import com.umeng.analytics.MobclickAgent;

public class Search extends Activity implements
		android.widget.AdapterView.OnItemClickListener {
	private String TAG = "Search";
	private AQuery aq;
	private App app;
	private ReturnSearch m_ReturnSearch = null;
	private String topic_id = null;
	private String type = null;
	private String topic_id_ready_have = null;

	private ArrayList dataStruct;
	private ListView ItemsListView, listHistory;
	private EditText searchtext;
	private SearchListAdapter SearchAdapter;
	
	private static String SEARCH = "查询";
	private static String SEARCH_LIST = "查询结果";
	Context mContext;

	private String[] st = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		// 获取listview对象
		ItemsListView = (ListView) findViewById(R.id.listView1);
		ItemsListView.setOnItemClickListener(this);
		searchtext = (EditText)findViewById(R.id.editText1);
		
		listHistory = (ListView) findViewById(R.id.listView2);
		
		mContext = this;
		app = (App) getApplication();
		aq = new AQuery(this);
		Intent intent = getIntent();
		topic_id = intent.getStringExtra("topic_id");
		type = intent.getStringExtra("type");
		topic_id_ready_have = intent.getStringExtra("topic_id_ready_have");
		if (app.isNetworkAvailable() == false) {
			app.MyToast(aq.getContext(),
					getResources().getString(R.string.networknotwork));
		}
		searchtext.addTextChangedListener(mTextWatcher);
		
		
		showHistory();
		listHistory.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String content = st[arg2].trim();
				doSearch(content);
			}
		});
		}

	
	private void showHistory() {
		aq.id(R.id.listView1).gone();
		aq.id(R.id.textViewNoResult).gone();
		aq.id(R.id.ProgressText).gone();
		aq.id(R.id.progress).gone();
		aq.id(R.id.removehistory).visible();
		aq.id(R.id.listView2).visible();

		String content = app.GetSearchData();
		content = content.replaceAll("\\[", "");
		content = content.replaceAll("\\]", "");
		st = content.split(",");
		st = checkarray(st);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.search_record, st);
		
		listHistory.setAdapter(adapter);

	}

	public String[] checkarray(String[] st){
		Set<String> set = new TreeSet<String>();
        for (int i = 0; i < st.length; i++)
        {
        	set.add(st[i].trim()); 
        }
        return (String[])set.toArray(new String[0]);
	}
	
	public void OnClickAdd(View v) {
		int index = Integer.parseInt(v.getTag().toString());
		SearchListData m_SearchListData = (SearchListData) ItemsListView
				.getItemAtPosition(index);
		if (m_ReturnSearch != null) {
			AddVideo(topic_id, m_SearchListData.Pic_ID);
			v.setVisibility(View.GONE);

		} else {
			app.MyToast(this, "ReturnSearch is empty.");
		}
	}

	public void OnClickSearch(View v) {
		String search_word = null;
		if (aq.id(R.id.editText1).getText() != null) {
			search_word = aq.id(R.id.editText1).getText().toString().trim();
		}
		if (search_word.length() > 0) {
			doSearch(search_word);
		} else {
			app.MyToast(this, "请输入你要搜索的内容.");
		}
	}

	public void doSearch(String search_word){
		aq.id(R.id.removehistory).gone();
		aq.id(R.id.listView2).gone();
		 //保存搜索记录到SharedPreferce
		app.SaveSearchData(search_word, search_word);


		// clear
		if (dataStruct != null && dataStruct.size() > 0) {
			dataStruct.clear();
			SearchAdapter.notifyDataSetChanged();
			ItemsListView.invalidate();
		}
		aq.id(R.id.textViewNoResult).gone();
		InputMethodManager imm = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		aq.id(R.id.editText1).getTextView().setCursorVisible(false);// 失去光标
		imm.hideSoftInputFromWindow(searchtext.getWindowToken(), 0);
		GetServiceData(search_word);
	}
	public void OnClickTab1TopRight(View v) {
		Intent i = new Intent(this, Setting.class);
		startActivity(i);

	}

	public void OnClickFinished(View v) {
		finish();
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
		MobclickAgent.onEventBegin(mContext, SEARCH);
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onEventEnd(mContext, SEARCH_LIST);
		MobclickAgent.onEventEnd(mContext, SEARCH);
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	public void OnClickremoveHistory(View v) {
		Dialog dialog = new AlertDialog.Builder(this).
				setMessage("确定清除历史记录？").setPositiveButton("确定", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						app.DeleteSearchData();
						showHistory();
						dialog.dismiss();
					}
				}).setNegativeButton("取消", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						
					}
				}).create();
		dialog.show();
	}

	public void GetVideoMovies() {
		dataStruct = new ArrayList();
		NotifyDataAnalysisFinished();
		if (m_ReturnSearch.results == null
				|| m_ReturnSearch.results.length == 0) {
			aq.id(R.id.listView1).gone();
			aq.id(R.id.textViewNoResult).visible();
			return;
		} else
			aq.id(R.id.textViewNoResult).gone();

		for (int i = 0; i < m_ReturnSearch.results.length; i++) {
			if (Integer.parseInt(m_ReturnSearch.results[i].prod_type) < 4) {
				SearchListData m_SearchListData = new SearchListData();

				m_SearchListData.Pic_ID = m_ReturnSearch.results[i].prod_id;
				if (topic_id_ready_have != null
						&& topic_id_ready_have
								.indexOf(m_ReturnSearch.results[i].prod_id
										+ "|") != -1) {
					m_SearchListData.Is_Ready_Have = true;
				} else
					m_SearchListData.Is_Ready_Have = false;
				if (m_ReturnSearch.results[i].prod_pic_url != null) {
					m_SearchListData.Pic_url = m_ReturnSearch.results[i].prod_pic_url;
				}
				m_SearchListData.Pic_name = m_ReturnSearch.results[i].prod_name;
				m_SearchListData.prod_type = m_ReturnSearch.results[i].prod_type;
				if (Integer.valueOf(m_ReturnSearch.results[i].prod_type) == 3) {
					if (m_ReturnSearch.results[i].star.trim().length() > 0)
						m_SearchListData.Text_Zhuyan = m_ReturnSearch.results[i].star;
					else
						m_SearchListData.Text_Zhuyan = m_ReturnSearch.results[i].director;
				} else
					m_SearchListData.Text_Zhuyan = m_ReturnSearch.results[i].star;

				m_SearchListData.Text_Year = m_ReturnSearch.results[i].publish_date;
				m_SearchListData.Text_Area = m_ReturnSearch.results[i].area;
				m_SearchListData.Text_Ding = m_ReturnSearch.results[i].support_num;
				m_SearchListData.Text_Score = m_ReturnSearch.results[i].score;
				dataStruct.add(m_SearchListData);
			}
		}
		if (dataStruct.size() == 0) {
			aq.id(R.id.listView1).gone();
			aq.id(R.id.textViewNoResult).visible();
		} else {
			aq.id(R.id.listView1).visible();
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
		aq.id(R.id.ProgressText).gone();
		aq.id(R.id.listView2).gone();
		aq.id(R.id.removehistory).gone();
		if (status.getCode() == AjaxStatus.NETWORK_ERROR) {
			if (app.isNetworkAvailable()) {
				aq.id(R.id.editText1).getTextView().setCursorVisible(true);
				aq.id(R.id.listView1).gone();
				aq.id(R.id.textViewNoResult).visible();
			} else {
				app.MyToast(aq.getContext(),
						getResources().getString(R.string.networknotwork));
				aq.id(R.id.editText1).getTextView().setCursorVisible(true);
			}
			return;
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			m_ReturnSearch = mapper.readValue(json.toString(),
					ReturnSearch.class);

			// 创建数据源对象
			GetVideoMovies();

			aq.id(R.id.editText1).getTextView().setCursorVisible(true);
			if (topic_id != null) {
				// aq.id(R.id.Tab1TopRightImage).background(R.drawable.tab3_p3_c2_top_right);
				aq.id(R.id.Tab1TopRightImage).gone();
				aq.id(R.id.editText1).gone();
				aq.id(R.id.Tab1TopRightImage2).visible();
				aq.id(R.id.imageView1).visible();
			}
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
			MobclickAgent.onEventBegin(mContext, SEARCH_LIST);
			SearchListAdapter listviewdetailadapter = getAdapter();
			ItemsListView.setAdapter(listviewdetailadapter);
		} else {
			app.MyToast(this, "ItemsListView empty.");
		}
	}

	private SearchListAdapter getAdapter() {
		if (SearchAdapter == null) {
			ArrayList arraylist = dataStruct;
			SearchListAdapter listviewdetailadapter;
			if (topic_id == null)
				listviewdetailadapter = new SearchListAdapter(this, arraylist,
						false);
			else
				listviewdetailadapter = new SearchListAdapter(this, arraylist,
						true);
			SearchAdapter = listviewdetailadapter;
		} else {
			ArrayList arraylist1 = dataStruct;
			SearchListAdapter listviewdetailadapter1;
			if (topic_id == null)
				listviewdetailadapter1 = new SearchListAdapter(this,
						arraylist1, false);
			else
				listviewdetailadapter1 = new SearchListAdapter(this,
						arraylist1, true);
			SearchAdapter = listviewdetailadapter1;
		}
		return SearchAdapter;
	}
	
	

	// listview的点击事件接口函数
	@Override
	public void onItemClick(AdapterView adapterview, View view, int i, long l) {
		SearchListData m_SearchListData = (SearchListData) ItemsListView
				.getItemAtPosition(i);
		if (topic_id != null) {
			ImageView m_image = (ImageView) view.findViewById(R.id.button1);
			m_image.setImageResource(R.drawable.search_addon_icon);
			view.setBackgroundColor(color.darker_gray);

			AddVideo(topic_id, m_SearchListData.Pic_ID);

			return;
		}
		if (m_ReturnSearch != null) {
			// app.MyToast(this, m_SearchListData.Pic_name);
			Intent intent = new Intent();
			// 1：电影，2：电视剧，3：综艺，4：视频
			switch (Integer.valueOf(m_SearchListData.prod_type)) {
			case 1:
				intent.setClass(this, Detail_Movie.class);
				intent.putExtra("prod_id", m_SearchListData.Pic_ID);
				intent.putExtra("prod_name", m_SearchListData.Pic_name);
				try {
					startActivity(intent);
				} catch (ActivityNotFoundException ex) {
					Log.e(TAG, "Call Detail_Movie failed", ex);
				}
				break;
			case 2:
				intent.setClass(this, Detail_TV.class);
				intent.putExtra("prod_id", m_SearchListData.Pic_ID);
				intent.putExtra("prod_name", m_SearchListData.Pic_name);
				try {
					startActivity(intent);
				} catch (ActivityNotFoundException ex) {
					Log.e(TAG, "Call Detail_TV failed", ex);
				}
				break;
			case 3:
				intent.setClass(this, Detail_Show.class);
				intent.putExtra("prod_id", m_SearchListData.Pic_ID);
				intent.putExtra("prod_name", m_SearchListData.Pic_name);
				try {
					startActivity(intent);
				} catch (ActivityNotFoundException ex) {
					Log.e(TAG, "Call Detail_Show failed", ex);
				}
				break;
			}

		} else {
			app.MyToast(this, "ReturnSearch is empty.");
		}

	}

	// InitListData
	public void GetServiceData(String search_word) {
		/*
		 * 搜索关键字如果有空格不会返回结果
		 */
		String url = Constant.BASE_URL + "search?keyword="
				+ URLEncoder.encode(search_word) + "&page_num=1&page_size=50";

		Map<String, Object> params = new HashMap<String, Object>();
		if (type != null && type.equalsIgnoreCase("tv")) {
			params.put("type", 2);
		} else if (type != null && type.equalsIgnoreCase("movie"))
			params.put("type", 1);

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.SetHeader(app.getHeaders());

		cb.params(params).url(url).type(JSONObject.class)
				.weakHandler(this, "InitListData");

		aq.id(R.id.ProgressText).visible();
		aq.progress(R.id.progress).ajax(cb);

	}

	// 添加视频
	public void AddVideo(String topic_id, String prod_id) {
		String url = Constant.BASE_URL + "top/addItem";
		/*
		 * app_key required string 申请应用时分配的AppKey。 topic_id required string 榜单id
		 * prod_id required string 视频id,可以多个，以逗号(,)隔开
		 */
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("topic_id", topic_id);
		params.put("prod_id", prod_id);

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.SetHeader(app.getHeaders());

		cb.params(params).url(url).type(JSONObject.class)
				.weakHandler(this, "AddVideoResult");

		aq.ajax(cb);

	}

	public void AddVideoResult(String url, JSONObject json, AjaxStatus status) {
		try {
			if (json.getString("res_code").trim().equalsIgnoreCase("00000"))
				app.MyToast(this, "添加影片成功");
			else
				app.MyToast(this, "添加影片失败");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
  private  TextWatcher mTextWatcher = new TextWatcher() {
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		String value = searchtext.getText().toString().trim();
		if(value == null || value.length() <=0 ){
			showHistory();
		}
		
	}
};
	
}
