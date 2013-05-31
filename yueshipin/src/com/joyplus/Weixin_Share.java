package com.joyplus;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyplus.Adapters.SearchListAdapter;
import com.joyplus.Adapters.SearchListData;
import com.joyplus.Service.Return.ReturnSearch;
import com.joyplus.widget.Log;
import com.umeng.analytics.MobclickAgent;

import android.R.color;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

public class Weixin_Share extends TabActivity implements
		android.widget.AdapterView.OnItemClickListener {

	private static final String TAG = "Weixin_Share";
	private AQuery aq;
	private App app;
	private String WX_PAGE1 = "WX_PAGE1";
	private String WX_PAGE2 = "WX_PAGE2";
	private String WX_PAGE3 = "WX_PAGE3";
	private TabHost mTabHost;
	private Intent mTab1, mTab2, mTab3;
	private static String PERSONAL = "微信分享";
	Context mContext;
	private LinearLayout LinearLayoutTab;
	private ReturnSearch m_ReturnSearch = null;
	private ArrayList dataStruct;
	private ListView ItemsListView;
	private EditText searchtext;
	private SearchListAdapter SearchAdapter;
	private boolean pageFlag = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weixin_share);
		// 获取listview对象
		ItemsListView = (ListView) findViewById(R.id.listView1);
		ItemsListView.setOnItemClickListener(this);
		searchtext = (EditText) findViewById(R.id.editText1);

		app = (App) getApplication();
		aq = new AQuery(this);
		mContext = this;
		LinearLayoutTab = (LinearLayout) findViewById(R.id.LinearLayoutTab);
		searchtext.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					searchtext.setFocusable(false);// EditText 失去焦点
					LinearLayoutTab.setVisibility(View.VISIBLE);
					pageFlag = true;

				} else {
					searchtext.setFocusable(true);
					LinearLayoutTab.setVisibility(View.INVISIBLE);
					ItemsListView.setVisibility(View.VISIBLE);
					pageFlag = true;
				}
				return false;
			}
		});
		prepareIntent();
		setupIntent();

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

	public void doSearch(String search_word) {
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

	// InitListData
	public void GetServiceData(String search_word) {
		/*
		 * 搜索关键字如果有空格不会返回结果
		 */
		String url = Constant.BASE_URL + "search?keyword="
				+ URLEncoder.encode(search_word) + "&page_num=1&page_size=50";

		Map<String, Object> params = new HashMap<String, Object>();

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.SetHeader(app.getHeaders());

		cb.params(params).url(url).type(JSONObject.class)
				.weakHandler(this, "InitListData");

		aq.id(R.id.ProgressText).visible();
		aq.progress(R.id.progressSearch).ajax(cb);

	}

	// 初始化list数据函数
	public void InitListData(String url, JSONObject json, AjaxStatus status) {
		aq.id(R.id.ProgressText).gone();
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
			// if (Integer.parseInt(m_ReturnSearch.results[i].prod_type) < 4) {
			if (Integer.parseInt(m_ReturnSearch.results[i].prod_type) > 0) {
				SearchListData m_SearchListData = new SearchListData();

				m_SearchListData.Pic_ID = m_ReturnSearch.results[i].prod_id;

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

	// 数据更新
	public void NotifyDataAnalysisFinished() {
		if (dataStruct != null && ItemsListView != null) {
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

			listviewdetailadapter = new SearchListAdapter(this, arraylist,
					false);

			SearchAdapter = listviewdetailadapter;
		} else {
			ArrayList arraylist1 = dataStruct;
			SearchListAdapter listviewdetailadapter1;

			listviewdetailadapter1 = new SearchListAdapter(this, arraylist1,
					false);

			SearchAdapter = listviewdetailadapter1;
		}
		return SearchAdapter;
	}

	// listview的点击事件接口函数
	@Override
	public void onItemClick(AdapterView adapterview, View view, int i, long l) {
		SearchListData m_SearchListData = (SearchListData) ItemsListView
				.getItemAtPosition(i);

		if (m_ReturnSearch != null) {
			// app.MyToast(this, m_SearchListData.Pic_name);
			Intent intent = new Intent();
			// 1：电影，2：电视剧，3：综艺，4：视频

			intent.setClass(this, Weixin_ShareVideo.class);
			intent.putExtra("prod_id", m_SearchListData.Pic_ID);
			intent.putExtra("prod_name", m_SearchListData.Pic_name);
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException ex) {
				Log.e(TAG, "Call Detail_Movie failed", ex);
			}
		}

	}

	private void prepareIntent() {
		mTab1 = new Intent(this, WeixinPage1.class);
		mTab2 = new Intent(this, WeixinPage2.class);
		mTab3 = new Intent(this, WeixinPage3.class);
	}

	private void setupIntent() {
		mTabHost = getTabHost();
		mTabHost.addTab(buildTabSpec(WX_PAGE1,
				getResources().getString(R.string.tab1),
				R.drawable.tab1_yuedan, mTab1));
		mTabHost.addTab(buildTabSpec(WX_PAGE2,
				getResources().getString(R.string.tab2),
				R.drawable.tab2_yuebang, mTab2));
		mTabHost.addTab(buildTabSpec(WX_PAGE3,
				getResources().getString(R.string.tab3), R.drawable.tab3_wode,
				mTab3));
		mTabHost.setCurrentTab(0);
		RadioGroup radioGroup = (RadioGroup) this
				.findViewById(R.id.radioGroup1);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.radio0:
					mTabHost.setCurrentTabByTag(WX_PAGE1);
					break;
				case R.id.radio1:
					mTabHost.setCurrentTabByTag(WX_PAGE2);
					break;
				case R.id.radio2:
					mTabHost.setCurrentTabByTag(WX_PAGE3);
					break;

				default:
					// tabHost.setCurrentTabByTag(TAB_1);
					break;
				}
			}
		});
	}

	private TabHost.TabSpec buildTabSpec(String tag, String resLabel,
			int resIcon, final Intent content) {
		return mTabHost.newTabSpec(tag)
				.setIndicator(resLabel, getResources().getDrawable(resIcon))
				.setContent(content);
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
		MobclickAgent.onEventBegin(mContext, PERSONAL);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onEventEnd(mContext, PERSONAL);
	}

	public void OnClickTab1TopLeft(View v) {
		if(pageFlag)
		{
			ItemsListView.setVisibility(View.INVISIBLE);
			LinearLayoutTab.setVisibility(View.VISIBLE);
			pageFlag = false;
		}else
		{
			Weixin_Share.this.finish();
		}
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getRepeatCount() == 0) {
				if(pageFlag)
				{
					ItemsListView.setVisibility(View.INVISIBLE);
					LinearLayoutTab.setVisibility(View.VISIBLE);
					pageFlag = false;
				}else
				{
					Weixin_Share.this.finish();
				}
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 100 && resultCode == 0) {

		}
		if (requestCode == 100 && resultCode == 101) {

		} else {
			// 获取当前活动的Activity实例
			Activity subActivity = getLocalActivityManager()
					.getCurrentActivity();
			// 判断是否实现返回值接口
			if (subActivity instanceof OnTabActivityResultListener) {
				// 获取返回值接口实例
				OnTabActivityResultListener listener = (OnTabActivityResultListener) subActivity;
				// 转发请求到子Activity
				listener.onTabActivityResult(requestCode, resultCode, data);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}