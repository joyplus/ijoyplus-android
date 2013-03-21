package com.joyplus;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyplus.Adapters.CurrentPlayData;
import com.joyplus.Detail_TV.EComparatorIndex;
import com.joyplus.Service.Return.ReturnProgramComments;
import com.joyplus.Service.Return.ReturnProgramView;
import com.joyplus.Service.Return.ReturnProgramView.DOWN_URLS;
import com.joyplus.Video.VideoPlayerActivity;
import com.joyplus.download.Dao;
import com.joyplus.download.DownloadInfo;
import com.joyplus.download.DownloadTask;
import com.joyplus.weibo.net.AccessToken;
import com.joyplus.weibo.net.DialogError;
import com.joyplus.weibo.net.Weibo;
import com.joyplus.weibo.net.WeiboDialogListener;
import com.joyplus.weibo.net.WeiboException;
import com.umeng.analytics.MobclickAgent;

public class Detail_Show extends Activity {
	private AQuery aq;
	private String TAG = "Detail_Show";
	private App app;
	private ReturnProgramView m_ReturnProgramView = null;
	private String prod_id = null;
	private String prod_name = null;
	private String PROD_SOURCE = null;
	public String DOWNLOAD_SOURCE = null;
	private String PROD_URI = null;
	private String download_index = null;
	private int page_num = 0;
	private int m_FavorityNum = 0;
	private int m_SupportNum = 0;

	private ReturnProgramComments m_ReturnProgramComments = null;
	private ScrollView mScrollView;
	private int isLastisNext = 2;
	private int mLastY = 0;

	private String uid = null;
	private String token = null;
	private String expires_in = null;

	public List<DownloadInfo> data;
	private Drawable download_focuse = null;
	private Drawable download_normal = null;
	private Drawable download_press = null;
	List download_names = new ArrayList();
	private ArrayList download_indexs = new ArrayList();
	private int current_index = -1; // yy
	boolean pageShow = true;
	private CurrentPlayData mCurrentPlayData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_show);
		app = (App) getApplication();
		aq = new AQuery(this);

		Intent intent = getIntent();
		prod_id = intent.getStringExtra("prod_id");
		prod_name = intent.getStringExtra("prod_name");
		
		if(prod_name != null)
			aq.id(R.id.program_name).text(prod_name);

		aq.id(R.id.scrollView1).gone();
		mScrollView = (ScrollView) findViewById(R.id.scrollView1);
		mScrollView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (mLastY == mScrollView.getScrollY()) {
						// TODO
						if (mScrollView.getScrollY() != 0)
							ShowMoreComments();
					} else {
						mLastY = mScrollView.getScrollY();
					}
				}
				return false;
			}
		});
		
		download_normal = this.getResources().getDrawable(R.drawable.undownload);
		download_press = this.getResources().getDrawable(R.drawable.download);
		download_focuse = this.getResources().getDrawable(R.drawable.download2);

		mCurrentPlayData = new CurrentPlayData();
		mCurrentPlayData.prod_id = prod_id;
		if (prod_id != null)
			CheckSaveData();
			GetServiceData();

	}

	public void OnClickTab1TopLeft(View v) {
		finish();
	}

	public void OnClickTab1TopRight(View v) {
		if (app.GetServiceData("Sina_Access_Token") != null) {
			Intent i = new Intent(this, Sina_Share.class);
			i.putExtra("prod_name", aq.id(R.id.program_name).getText()
					.toString());
			startActivity(i);
		} else {
			GotoSinaWeibo();
		}

	}

	public void GotoSinaWeibo() {
		Weibo weibo = Weibo.getInstance();
		weibo.setupConsumerConfig(Constant.SINA_CONSUMER_KEY,
				Constant.SINA_CONSUMER_SECRET);
		weibo.setRedirectUrl("https://api.weibo.com/oauth2/default.html");
		weibo.authorize(this, new AuthDialogListener());

	}

	// 第三方新浪登录
	class AuthDialogListener implements WeiboDialogListener {

		@Override
		public void onComplete(Bundle values) {
			uid = values.getString("uid");
			token = values.getString("access_token");
			expires_in = values.getString("expires_in");
			System.out.println("expires_in=====>" + expires_in);
			AccessToken accessToken = new AccessToken(token,
					Constant.SINA_CONSUMER_SECRET);
			accessToken.setExpiresIn(expires_in);
			Weibo.getInstance().setAccessToken(accessToken);
			// save access_token
			app.SaveServiceData("Sina_Access_Token", token);
			app.SaveServiceData("Sina_Access_UID", uid);
			UploadSinaHeadAndScreen_nameUrl(token, uid);
			app.MyToast(getApplicationContext(), "新浪微博已绑定");
		}

		@Override
		public void onError(DialogError e) {
			app.MyToast(getApplicationContext(),
					"Auth error : " + e.getMessage());
		}

		@Override
		public void onCancel() {
			app.MyToast(getApplicationContext(), "Auth cancel");
		}

		@Override
		public void onWeiboException(WeiboException e) {
			app.MyToast(getApplicationContext(),
					"Auth exception : " + e.getMessage());
		}

	}

	public boolean UploadSinaHeadAndScreen_nameUrl(String access_token,
			String uid) {
		String m_GetURL = "https://api.weibo.com/2/users/show.json?access_token="
				+ access_token + "&uid=" + uid;

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(m_GetURL).type(JSONObject.class)
				.weakHandler(this, "UploadSinaHeadAndScreen_nameUrlResult");

		cb.header("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");

		aq.ajax(cb);

		return false;
	}

	public void UploadSinaHeadAndScreen_nameUrlResult(String url,
			JSONObject json, AjaxStatus status) {
		String head_url = json.optString("avatar_large");
		String screen_name = json.optString("screen_name");
		if (head_url != null && screen_name != null) {
			String m_PostURL = Constant.BASE_URL + "account/bindAccount";

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("source_id", uid);
			params.put("source_type", "1");
			params.put("pic_url", head_url);
			params.put("nickname", screen_name);

			// save to local
			AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
			cb.SetHeader(app.getHeaders());

			cb.params(params).url(m_PostURL).type(JSONObject.class)
					.weakHandler(this, "AccountBindAccountResult");

			aq.ajax(cb);
		}

	}

	public void AccountBindAccountResult(String url, JSONObject json,
			AjaxStatus status) {
		if (json != null) {
			try {
				if (json.getString("res_code").trim().equalsIgnoreCase("00000")) {

					// reload the userinfo
					String url2 = Constant.BASE_URL + "user/view?userid="
							+ app.UserID;

					AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
					cb.url(url2).type(JSONObject.class)
							.weakHandler(this, "AccountBindAccountResult3");

					cb.header("User-Agent",
							"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
					cb.header("app_key", Constant.APPKEY);
					// cb.header("user_id", app.UserID);

					aq.ajax(cb);
				}
				// else
				// app.MyToast(this, "更新头像失败!");
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

	public void AccountBindAccountResult3(String url, JSONObject json,
			AjaxStatus status) {

		if (json != null) {
			try {
				if (json.getString("nickname").trim().length() > 0) {
					app.SaveServiceData("UserInfo", json.toString());
					app.MyToast(getApplicationContext(), "新浪微博已绑定");
					Intent i = new Intent(this, Sina_Share.class);
					i.putExtra("prod_name", aq.id(R.id.program_name).getText()
							.toString());
					startActivity(i);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else {
			if (status.getCode() == AjaxStatus.NETWORK_ERROR)
				app.MyToast(this,
						getResources().getString(R.string.networknotwork));
		}
	}

	public void OnClickContent(View v) {
		if (m_ReturnProgramView.show != null) {
			AlertDialog alertDialog = new AlertDialog.Builder(this).setMessage(
					m_ReturnProgramView.show.summary).create();
			Window window = alertDialog.getWindow();
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.alpha = 0.6f;
			window.setAttributes(lp);
			alertDialog.show();
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

	public void InitData() {
		String m_j = null;
		int i = 0;
		int j = 0;
		if (m_ReturnProgramView.show != null) {
			aq.id(R.id.program_name).text(m_ReturnProgramView.show.name);
			aq.id(R.id.imageView3).image(m_ReturnProgramView.show.poster, true,
					true);
			aq.id(R.id.textView5).text(m_ReturnProgramView.show.publish_date);
			aq.id(R.id.textView6).text(m_ReturnProgramView.show.area);
			if (m_ReturnProgramView.show.stars.trim().length() > 0)
				aq.id(R.id.textView7).text(m_ReturnProgramView.show.stars);
			else
				aq.id(R.id.textView7).text(m_ReturnProgramView.show.directors);
			m_FavorityNum = Integer
					.parseInt(m_ReturnProgramView.show.favority_num);
			aq.id(R.id.button2).text("收藏(" + m_FavorityNum + ")");
			m_SupportNum = Integer
					.parseInt(m_ReturnProgramView.show.support_num);
			aq.id(R.id.button3).text("顶(" + m_SupportNum + ")");
			aq.id(R.id.textView11).text(
					"    " + m_ReturnProgramView.show.summary);
			// aq.id(R.id.textView13).text(m_ReturnProgramView.show.comments.);
			if (m_ReturnProgramView.show.episodes != null) {
				aq.id(R.id.imageView_zxbf).visible();
				if (m_ReturnProgramView.show.episodes.length > 4) {
					aq.id(R.id.textView9).visible();
				}
				for (i = 0; i < m_ReturnProgramView.show.episodes.length
						&& i < 4; i++) {

					m_j = Integer.toString(i);// m_ReturnProgramView.show.episodes[i].name;
					Button m_button = (Button) this.findViewById(getResources()
							.getIdentifier("show_button" + m_j, "id",
									getPackageName()));
					m_j = Integer.toString(i + 1);
					m_button.setTag(i + "");
					m_button.setText(" "+m_ReturnProgramView.show.episodes[i].name);
					m_button.setVisibility(View.VISIBLE);
				}
				if (i < 4) {
					for (j = i; j < 4; j++) {
						m_j = Integer.toString(j);// m_ReturnProgramView.show.episodes[i].name;
						Button m_button = (Button) this
								.findViewById(getResources().getIdentifier(
										"show_button" + m_j, "id",
										getPackageName()));
						m_button.setVisibility(View.GONE);
					}
				}

			}
			if (m_ReturnProgramView.show.episodes.length <= 1
					&& m_ReturnProgramView.show.episodes[0].name.trim()
							.equalsIgnoreCase("1")) {
				aq.id(R.id.LinearLayoutXGYD).gone();
			}
			if (m_ReturnProgramView.show.episodes != null
					&& m_ReturnProgramView.show.episodes[0].video_urls != null
					&& m_ReturnProgramView.show.episodes[0].video_urls[0].url != null)
				PROD_URI = m_ReturnProgramView.show.episodes[0].video_urls[0].url;

			if (m_ReturnProgramView.show.episodes[0].down_urls != null
					&& m_ReturnProgramView.show.episodes[0].down_urls[0].urls.length > 0
					&& m_ReturnProgramView.show.episodes[0].down_urls[0].urls[0].url != null)
				PROD_SOURCE = m_ReturnProgramView.show.episodes[0].down_urls[0].urls[0].url;

			/*
			 * 暂无下载按钮
			 */
			for (i = 0; i < m_ReturnProgramView.show.episodes.length; i++) {
				if (m_ReturnProgramView.show.episodes[i].down_urls != null) {
					for (int k = 0; k < m_ReturnProgramView.show.episodes[i].down_urls[0].urls.length; k++) {
						ReturnProgramView.DOWN_URLS.URLS urls = m_ReturnProgramView.show.episodes[i].down_urls[0].urls[k];
						if (urls != null) {
							if (DOWNLOAD_SOURCE == null && urls.file != null
									&& app.IfSupportFormat(urls.url)
									&& urls.file.trim().equalsIgnoreCase("mp4"))
								DOWNLOAD_SOURCE = urls.url.trim();
						}
					}
					if (PROD_SOURCE != null && DOWNLOAD_SOURCE != null)
						break;
				}
			}

			if (DOWNLOAD_SOURCE == null) {
				aq.id(R.id.cache_button9).background(R.drawable.zan_wu_xia_zai);
				aq.id(R.id.cache_button9).clickable(false);
			}

			if (m_ReturnProgramView.comments != null
					&& m_ReturnProgramView.comments.length >= 1) {
				ShowComments();
			} else {
				aq.id(R.id.imageView_comment).gone();
				aq.id(R.id.Layout_comment).gone();
			}
		}

	}

	public void OnClickImageView(View v) {

	}

	// 初始化list数据函数
	public void InitListData(String url, JSONObject json, AjaxStatus status) {
		if (status.getCode() == AjaxStatus.NETWORK_ERROR&&app.GetServiceData(prod_id) == null) {
			aq.id(R.id.ProgressText).gone();
			app.MyToast(aq.getContext(),
					getResources().getString(R.string.networknotwork));
			aq.id(R.id.none_net).visible();
			return;
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			m_ReturnProgramView = mapper.readValue(json.toString(),
					ReturnProgramView.class);
			if(m_ReturnProgramView != null&&prod_id!=null)
			{
				app.SaveServiceData(prod_id, json.toString());//根据id保存住
			}
			// 创建数据源对象
			InitData();
			aq.id(R.id.ProgressText).gone();

			aq.id(R.id.scrollView1).visible();
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
		SaveData = app.GetServiceData(prod_id);
		if (SaveData == null) {
			GetServiceData();
		} else {
			try {
				m_ReturnProgramView = mapper.readValue(SaveData, ReturnProgramView.class);
				// 创建数据源对象
				// 创建数据源对象
				InitData();
				aq.id(R.id.ProgressText).gone();
				aq.id(R.id.scrollView1).visible();
//				new Handler().postDelayed(new Runnable() {
//					@Override
//					public void run() {
//						// execute the task
//						GetServiceData();
//					}
//				}, 10000);

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
		String url = Constant.BASE_URL + "program/view?prod_id=" + prod_id;

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class).weakHandler(this, "InitListData");

		cb.SetHeader(app.getHeaders());
		if(app.GetServiceData(prod_id) == null)
		{
			aq.id(R.id.ProgressText).visible();
			aq.progress(R.id.progress).ajax(cb);
		}
		else
		{
			aq.ajax(cb);
		}

	}

	public void CallServiceFavorityResult(String url, JSONObject json,
			AjaxStatus status) {

		if (json != null) {
			try {
				// woof is "00000",now "20024",by yyc
				if (json.getString("res_code").trim().equalsIgnoreCase("00000")) {
					m_FavorityNum++;
					aq.id(R.id.button2).text(
							"收藏(" + Integer.toString(m_FavorityNum) + ")");
					app.MyToast(this, "收藏成功!");
				} else
					app.MyToast(this, "已收藏!");
				// Toast.makeText(Detail_Show.this,json.getString("res_code"),Toast.LENGTH_LONG).show();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {

			// ajax error, show error code
			if (status.getCode() == AjaxStatus.NETWORK_ERROR)
				app.MyToast(aq.getContext(),
						getResources().getString(R.string.networknotwork));
		}

	}

	public void OnClickFavorityNum(View v) {
		String url = Constant.BASE_URL + "program/favority";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("prod_id", prod_id);

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.SetHeader(app.getHeaders());

		cb.params(params).url(url).type(JSONObject.class)
				.weakHandler(this, "CallServiceFavorityResult");

		aq.ajax(cb);
	}

	public void CallServiceResultSupportNum(String url, JSONObject json,
			AjaxStatus status) {

		if (json != null) {
			try {
				if (json.getString("res_code").trim().equalsIgnoreCase("00000")) {
					m_SupportNum++;
					aq.id(R.id.button3).text(
							"顶(" + Integer.toString(m_SupportNum) + ")");
					app.MyToast(this, "顶成功!");
				} else
					app.MyToast(this, "已顶过!");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			// ajax error, show error code
			if (status.getCode() == AjaxStatus.NETWORK_ERROR)
				app.MyToast(aq.getContext(),
						getResources().getString(R.string.networknotwork));
		}
	}

	public void OnClickSupportNum(View v) {
		String url = Constant.BASE_URL + "program/support";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("prod_id", prod_id);

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.SetHeader(app.getHeaders());

		cb.params(params).url(url).type(JSONObject.class)
				.weakHandler(this, "CallServiceResultSupportNum");

		aq.ajax(cb);

	}

	public void OnClickReportProblem(View v) {
		String url = Constant.BASE_URL + "program/invalid";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("prod_id", prod_id);

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.header("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
		cb.header("app_key", Constant.APPKEY);
		// cb.header("user_id", app.UserID);

		cb.params(params).url(url).type(JSONObject.class)
				.weakHandler(this, "CallServiceResultReportProblem");
		aq.ajax(cb);
		Toast.makeText(Detail_Show.this, "您反馈的问题已提交，我们会尽快处理，感谢您的支持！",
				Toast.LENGTH_LONG).show();
	}

	public void OnClickPlay(View v) {
		if (MobclickAgent.getConfigParams(this, "playBtnSuppressed").trim()
				.equalsIgnoreCase("1")) {
			app.MyToast(this, "暂无播放链接!");
			return;
		}
		
		app.checkUserSelect(Detail_Show.this);
		if(app.use2G3G)
		{
			
			//综艺type为3 ，sbuname 为当前集数
			StatisticsUtils.StatisticsClicksShow(aq,app,prod_id, prod_name,
					m_ReturnProgramView.show.episodes[0].name , 3);
			
			if (PROD_SOURCE != null && PROD_SOURCE.trim().length() > 0) {
				current_index = 0;
				CallVideoPlayActivity(PROD_SOURCE, m_ReturnProgramView.show.name);

			} else if (PROD_URI != null && PROD_URI.trim().length() > 0) {

				SaveToServer(2, PROD_URI, 1);

				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse(PROD_URI);
				intent.setData(content_url);
				startActivity(intent);
			}
		}
	}
	
	// OnClickNext4
	public void OnClickNext4(View v) {
		String m_j = null;
		int j = 0;
		int i = 0;
		page_num++;
		if (page_num * 4 >= m_ReturnProgramView.show.episodes.length) {
			page_num--;
			return;
		}
		if (m_ReturnProgramView.show.episodes != null) {
			for (i = 4 * page_num; i < m_ReturnProgramView.show.episodes.length
					&& i < 4 * (page_num + 1); i++, j++) {

				m_j = Integer.toString(j);// m_ReturnProgramView.show.episodes[i].name;
				Button m_button = (Button) this.findViewById(getResources()
						.getIdentifier("show_button" + m_j, "id",
								getPackageName()));
				m_button.setTag(i + "");
				m_button.setText(" "+m_ReturnProgramView.show.episodes[i].name);
				m_button.setVisibility(View.VISIBLE);
			}
			if (j < 4) {
				for (i = j; i < 4; i++) {
					m_j = Integer.toString(i);// m_ReturnProgramView.show.episodes[i].name;
					Button m_button = (Button) this.findViewById(getResources()
							.getIdentifier("show_button" + m_j, "id",
									getPackageName()));
					m_button.setVisibility(View.GONE);
				}
			}

		}
	}

	public void OnClickPre4(View v) {
		String m_j = null;
		int j = 0;
		int i = 0;
		if (page_num == 0) {
			return;

		}
		page_num--;
		if (m_ReturnProgramView.show.episodes != null && page_num >= 0) {
			for (i = 4 * page_num; i < m_ReturnProgramView.show.episodes.length
					&& i < 4 * (page_num + 1); i++, j++) {

				m_j = Integer.toString(j);// m_ReturnProgramView.show.episodes[i].name;
				Button m_button = (Button) this.findViewById(getResources()
						.getIdentifier("show_button" + m_j, "id",
								getPackageName()));
				m_j = Integer.toString(i + 1);
				m_button.setTag(i + "");
				m_button.setText(" "+m_ReturnProgramView.show.episodes[i].name);
				m_button.setVisibility(View.VISIBLE);
			}
			if (j < 4) {
				for (i = j; i < 4; i++) {
					m_j = Integer.toString(i);// m_ReturnProgramView.show.episodes[i].name;
					Button m_button = (Button) this.findViewById(getResources()
							.getIdentifier("show_button" + m_j, "id",
									getPackageName()));
					m_button.setVisibility(View.GONE);
				}
			}

		}
	}

	public void OnClickShowPlay(View v) {
		int index = Integer.parseInt(v.getTag().toString());
		current_index = index;
		if (MobclickAgent.getConfigParams(this, "playBtnSuppressed").trim()
				.equalsIgnoreCase("1")) {
			app.MyToast(this, "暂无播放链接!");
			return;
		}
		
		//videoSourceSort(index);
		if (m_ReturnProgramView.show.episodes != null
				&& m_ReturnProgramView.show.episodes[index].video_urls != null
				&& m_ReturnProgramView.show.episodes[index].video_urls[0].url != null)
			PROD_URI = m_ReturnProgramView.show.episodes[index].video_urls[0].url;
		PROD_SOURCE = null;
//		videoSourceSort(index);
		if (m_ReturnProgramView.show.episodes[index].down_urls != null) {
			for (int i = 0; i < m_ReturnProgramView.show.episodes[index].down_urls.length; i++) {
				for (int k = 0; k < m_ReturnProgramView.show.episodes[index].down_urls[i].urls.length; k++) {
					ReturnProgramView.DOWN_URLS.URLS urls = m_ReturnProgramView.show.episodes[index].down_urls[i].urls[k];
					if (urls != null) {
						if (urls.url != null && app.IfSupportFormat(urls.url)) {
							if (PROD_SOURCE == null
									&& !app.IfIncludeM3U(urls.url))
								PROD_SOURCE = urls.url.trim();
							if (PROD_SOURCE == null
									&& urls.type.trim().equalsIgnoreCase("mp4"))
								PROD_SOURCE = urls.url.trim();
							else if (PROD_SOURCE == null
									&& urls.type.trim().equalsIgnoreCase("flv"))
								PROD_SOURCE = urls.url.trim();
							else if (PROD_SOURCE == null
									&& urls.type.trim().equalsIgnoreCase("hd2"))
								PROD_SOURCE = urls.url.trim();
							else if (PROD_SOURCE == null
									&& urls.type.trim().equalsIgnoreCase("3gp"))
								PROD_SOURCE = urls.url.trim();
						}
						if (PROD_SOURCE != null)
							break;
					}
					if (PROD_SOURCE != null)
						break;
				}
			}
		}
		app.checkUserSelect(Detail_Show.this);
		if(app.use2G3G)
		{
			
			//综艺type为3 ，sbuname 为当前集数
			StatisticsUtils.StatisticsClicksShow(aq,app,prod_id, prod_name,
					m_ReturnProgramView.show.episodes[current_index].name, 3);
			
			if (PROD_SOURCE != null && PROD_SOURCE.trim().length() > 0) {
				CallVideoPlayActivity(PROD_SOURCE, m_ReturnProgramView.show.name);
			} else if (PROD_URI != null && PROD_URI.trim().length() > 0) {

				SaveToServer(2, PROD_URI, index + 1);

				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse(PROD_URI);
				intent.setData(content_url);
				startActivity(intent);
			}
		}
	}
	
	public void videoSourceSort(int source_index)
	{
		if(m_ReturnProgramView.show.episodes[source_index].down_urls!=null)
		{
			for(int j = 0;j<m_ReturnProgramView.show.episodes[source_index].down_urls.length;j++)
			{
				if(m_ReturnProgramView.show.episodes[source_index].down_urls[j].source.equalsIgnoreCase("letv"))
				{
					m_ReturnProgramView.show.episodes[source_index].down_urls[j].index = 0;
				}
				else if(m_ReturnProgramView.show.episodes[source_index].down_urls[j].source.equalsIgnoreCase("fengxing"))
				{
					m_ReturnProgramView.show.episodes[source_index].down_urls[j].index = 1;
				}
				else if(m_ReturnProgramView.show.episodes[source_index].down_urls[j].source.equalsIgnoreCase("qiyi"))
				{
					m_ReturnProgramView.show.episodes[source_index].down_urls[j].index = 2;
				}
				else if(m_ReturnProgramView.show.episodes[source_index].down_urls[j].source.equalsIgnoreCase("youku"))
				{
					m_ReturnProgramView.show.episodes[source_index].down_urls[j].index = 3;
				}
				else if(m_ReturnProgramView.show.episodes[source_index].down_urls[j].source.equalsIgnoreCase("sinahd"))
				{
					m_ReturnProgramView.show.episodes[source_index].down_urls[j].index = 4;
				}
				else if(m_ReturnProgramView.show.episodes[source_index].down_urls[j].source.equalsIgnoreCase("sohu"))
				{
					m_ReturnProgramView.show.episodes[source_index].down_urls[j].index = 5;
				}
				else if(m_ReturnProgramView.show.episodes[source_index].down_urls[j].source.equalsIgnoreCase("56"))
				{
					m_ReturnProgramView.show.episodes[source_index].down_urls[j].index = 6;
				}
				else if(m_ReturnProgramView.show.episodes[source_index].down_urls[j].source.equalsIgnoreCase("qq"))
				{
					m_ReturnProgramView.show.episodes[source_index].down_urls[j].index = 7;
				}
				else if(m_ReturnProgramView.show.episodes[source_index].down_urls[j].source.equalsIgnoreCase("pptv"))
				{
					m_ReturnProgramView.show.episodes[source_index].down_urls[j].index = 8;
				}
				else if(m_ReturnProgramView.show.episodes[source_index].down_urls[j].source.equalsIgnoreCase("m1905"))
				{
					m_ReturnProgramView.show.episodes[source_index].down_urls[j].index = 9;
				}
			}
			if(m_ReturnProgramView.tv.episodes[source_index].down_urls.length>1)
			{
				Arrays.sort(m_ReturnProgramView.show.episodes[source_index].down_urls, new EComparatorIndex());
			}	
		}
	}
	// 将片源排序
	class EComparatorIndex implements Comparator {

		@Override
		public int compare(Object first, Object second) {
			// TODO Auto-generated method stub
			int first_name = ((DOWN_URLS) first).index;
			int second_name = ((DOWN_URLS) second).index;
			if (first_name - second_name < 0) {
				return -1;
			} else {
				return 1;
			}
		}
	}
		
	public void CallVideoPlayActivity(String m_uri, String title) {
		app.IfSupportFormat(m_uri);
		mCurrentPlayData.CurrentCategory = 2;
		mCurrentPlayData.CurrentIndex = current_index;
		app.setCurrentPlayData(mCurrentPlayData);
		
		Intent intent = new Intent(this, VideoPlayerActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("path", m_uri);
		bundle.putString("title", title);
		bundle.putString("prod_id", prod_id);
		bundle.putString("prod_subname", m_ReturnProgramView.show.episodes[current_index].name);
		bundle.putString("prod_type", "3");
		bundle.putLong("current_time", 0);
		intent.putExtras(bundle);

		try {
			startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			Log.e(TAG, "VideoPlayerActivity fail", ex);
		}
	}

	public void ShowComments() {
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.Layout_comment);
		if (m_ReturnProgramView.comments != null) {
			for (int i = 0; i < m_ReturnProgramView.comments.length; i++) {
				RelativeLayout subLayout = new RelativeLayout(this);

				RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
				params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
						RelativeLayout.TRUE);

				TextView valueName = new TextView(this);
				// valueName.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
				valueName.setTextColor(Color.BLACK);
				if (!m_ReturnProgramView.comments[i].owner_name
						.equalsIgnoreCase("EMPTY"))
					valueName
							.setText(m_ReturnProgramView.comments[i].owner_name
									+ ":");
				else
					valueName.setText("网络用户:");
				subLayout.addView(valueName, params1);

				RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
				params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
						RelativeLayout.TRUE);

				TextView valueTime = new TextView(this);
				valueTime.setText(m_ReturnProgramView.comments[i].create_date
						.replaceAll(" 00:00:00", ""));
				subLayout.addView(valueTime, params2);

				LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
				params3.topMargin = 10;

				linearLayout.addView(subLayout, params3);

				TextView valueContent = new TextView(this);
				valueContent.setText(m_ReturnProgramView.comments[i].content);
				linearLayout.addView(valueContent);

				if (i != m_ReturnProgramView.comments.length - 1) {
					LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
					params4.topMargin = 10;

					ImageView m_image = new ImageView(this);
					m_image.setBackgroundResource(R.drawable.tab1_divider);

					linearLayout.addView(m_image, params4);
				}
			}
		}
	}

	public void ShowMoreComments() {
		/*
		 * app_key required string 申请应用时分配的AppKey。 prod_id required string 节目id
		 * page_num = 需要请求的页码（可选），默认为1 page_size = 每一页包含的记录数（可选），默认为10
		 */
		String url = Constant.BASE_URL + "program/comments" + "?prod_id="
				+ prod_id + "&page_num=" + Integer.toString(isLastisNext)
				+ "&page_size=10";

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class)
				.weakHandler(this, "MoreCommentsResult");

		cb.SetHeader(app.getHeaders());

		aq.id(R.id.ProgressText).visible();
		aq.progress(R.id.progress).ajax(cb);
	}

	public void MoreCommentsResult(String url, JSONObject json,
			AjaxStatus status) {
		if (json == null) {
			return;
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (isLastisNext > 2)
				m_ReturnProgramComments = null;
			m_ReturnProgramComments = mapper.readValue(json.toString(),
					ReturnProgramComments.class);
			// 创建数据源对象
			AddMoreComments();

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

	public void AddMoreComments() {
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.Layout_comment);
		if (m_ReturnProgramComments != null) {
			for (int i = 0; i < m_ReturnProgramComments.comments.length; i++) {
				RelativeLayout subLayout = new RelativeLayout(this);

				LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
				params4.topMargin = 10;

				ImageView m_image = new ImageView(this);
				m_image.setBackgroundResource(R.drawable.tab1_divider);

				linearLayout.addView(m_image, params4);

				RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
				params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
						RelativeLayout.TRUE);

				TextView valueName = new TextView(this);
				// valueName.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
				valueName.setTextColor(Color.BLACK);
				if (!m_ReturnProgramComments.comments[i].owner_name
						.equalsIgnoreCase("EMPTY"))
					valueName
							.setText(m_ReturnProgramComments.comments[i].owner_name
									+ ":");
				else
					valueName.setText("网络用户:");
				subLayout.addView(valueName, params1);

				RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
				params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
						RelativeLayout.TRUE);

				TextView valueTime = new TextView(this);
				valueTime
						.setText(m_ReturnProgramComments.comments[i].create_date
								.replaceAll(" 00:00:00", ""));
				subLayout.addView(valueTime, params2);

				LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
				params3.topMargin = 10;

				linearLayout.addView(subLayout, params3);

				TextView valueContent = new TextView(this);
				valueContent
						.setText(m_ReturnProgramComments.comments[i].content);
				linearLayout.addView(valueContent);

			}
		}
	}

	private void SaveToServer(int play_type, String SourceUrl, int episodesNum) {
		String url = Constant.BASE_URL + "program/play";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("app_key", Constant.APPKEY);// required string
												// 申请应用时分配的AppKey。
		params.put("prod_id", m_ReturnProgramView.show.id);// required string
															// 视频id
		params.put("prod_name", m_ReturnProgramView.show.name);// required
																// string 视频名字
		params.put("prod_subname", Integer.toString(episodesNum));// required
																	// string
																	// 视频的集数
		params.put("prod_type", 3);// required int 视频类别 1：电影，2：电视剧，3：综艺，4：视频
		params.put("playback_time", 0);// _time required int 上次播放时间，单位：秒
		params.put("duration", 0);// required int 视频时长， 单位：秒

		if (play_type == 1)
			params.put("play_type", "1");// required string
		else
			params.put("play_type", "2");

		// 播放的类别 1: 视频地址播放
		// 2:webview播放
		params.put("video_url", SourceUrl);// required

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.SetHeader(app.getHeaders());

		cb.params(params).url(url).type(JSONObject.class)
				.weakHandler(this, "CallProgramPlayResult");
		// cb.params(params).url(url);
		aq.ajax(cb);
	}

	public void CallProgramPlayResult(String url, JSONObject json,
			AjaxStatus status) {
	
	}

	private void GetVideoSource(final int episodeNum, String url) {

		aq.progress(R.id.progress).ajax(url, InputStream.class,
				new AjaxCallback<InputStream>() {

					public void callback(String url, InputStream is,
							AjaxStatus status) {
						String urlsave = Constant.BASE_URL + "program/play";
						if (is != null) {

							Map<String, Object> params = new HashMap<String, Object>();
							params.put("app_key", Constant.APPKEY);// required
																	// string
																	// 申请应用时分配的AppKey。
							params.put("prod_id", m_ReturnProgramView.show.id);// required
																				// string
																				// 视频id
							params.put("prod_name",
									m_ReturnProgramView.show.name);// required
																	// string
																	// 视频名字
							params.put("prod_subname",
									m_ReturnProgramView.show.episodes.length);// required
																				// string
																				// 视频的集数
							params.put("prod_type", 3);// required int 视频类别
														// 1：电影，2：电视剧，3：综艺，4：视频
							params.put("playback_time", 0);// _time required int
															// 上次播放时间，单位：秒
							params.put("duration", 0);// required int 视频时长， 单位：秒
							params.put("play_type", "1");// required string
															// 播放的类别 1: 视频地址播放
							// 2:webview播放
							params.put("video_url", url);// required
															// string
															// 视频url

							AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
							cb.SetHeader(app.getHeaders());

							cb.params(params).url(urlsave);
							aq.ajax(cb);

							CallVideoPlayActivity(url,
									m_ReturnProgramView.show.name);
						} else {
							if (m_ReturnProgramView.show.episodes[episodeNum].down_urls != null) {
								for (int k = 0; k < m_ReturnProgramView.show.episodes[episodeNum].down_urls[0].urls.length; k++) {
									ReturnProgramView.DOWN_URLS.URLS urls = m_ReturnProgramView.show.episodes[episodeNum].down_urls[0].urls[k];
									if (urls != null) {
										if (urls.url != null) {
											if (urls.type.trim()
													.equalsIgnoreCase("mp4"))
												PROD_SOURCE = urls.url.trim();
											else if (urls.type.trim()
													.equalsIgnoreCase("flv"))
												PROD_SOURCE = urls.url.trim();
											else if (urls.type.trim()
													.equalsIgnoreCase("hd2"))
												PROD_SOURCE = urls.url.trim();
											else if (urls.type.trim()
													.equalsIgnoreCase("3gp"))
												PROD_SOURCE = urls.url.trim();
										}
										if (PROD_SOURCE != null) {
											GetVideoSource(episodeNum,
													PROD_SOURCE);
										}
									}
								}
							}
						}
					}

				});
	}
	
	public void OnClickCacheDown(View v) {
		GotoDownloadPage();
		pageShow = false;
	}

	private void GotoDownloadPage() {
		// TODO Auto-generated method stub
		setContentView(R.layout.download_show);
		download_focuse = this.getResources().getDrawable(
				R.drawable.download_show2);
		download_normal = this.getResources().getDrawable(
				R.drawable.undownload_show);
		download_press = this.getResources().getDrawable(
				R.drawable.download_show);
		LinearLayout linearbtn = (LinearLayout) findViewById(R.id.btnReturnDetail_Show);

		linearbtn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setContentView(R.layout.detail_show);
//				GetServiceData();
				if (prod_id != null)
					CheckSaveData();
			}
		});
		aq.id(R.id.textView2).text(m_ReturnProgramView.show.name);
		if(download_names.size()==0)//如果当前的download_names不为空说明不是第一次进入
		{
			for (int i = 0; i < m_ReturnProgramView.show.episodes.length; i++) {
				download_names.add(m_ReturnProgramView.show.episodes[i].name);
			}
		}
		//获取当前综艺有多少集在数据库里,根据电视剧的my_index显示不一样的下载按钮
		data = Dao.getInstance(Detail_Show.this).getInfosOfProd_id(prod_id);
		ListView list = (ListView) findViewById(R.id.listViewDownload);
		list.requestFocusFromTouch();
		MyAdapter adapter = new MyAdapter(Detail_Show.this);
		list.setAdapter(adapter);
		list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);// 一定要设置这个属性，否则ListView不会刷新
		list.setTextFilterEnabled(true);
		list.setItemChecked(0, true);

		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				DOWNLOAD_SOURCE = null;
				if (m_ReturnProgramView.show.episodes[position].down_urls != null) {
					for (int i = 0; i < m_ReturnProgramView.show.episodes[position].down_urls.length; i++) {
					for (int k = 0; k < m_ReturnProgramView.show.episodes[position].down_urls[i].urls.length; k++) {
						ReturnProgramView.DOWN_URLS.URLS urls = m_ReturnProgramView.show.episodes[position].down_urls[i].urls[k];
						if (urls != null) {
							if (DOWNLOAD_SOURCE == null && urls.file != null
									&& app.IfSupportFormat(urls.url)
									&& urls.file.trim().equalsIgnoreCase("mp4"))
								DOWNLOAD_SOURCE = urls.url.trim();
							if (DOWNLOAD_SOURCE != null)
								break;
						}
					}
				}
				app.checkUserSelect(Detail_Show.this);
				if(app.use2G3G)
				{
					if (DOWNLOAD_SOURCE != null) {
						String urlstr = DOWNLOAD_SOURCE;
						download_index = (position + 1)+"_show";
						String localfile = Constant.PATH_VIDEO + prod_id + "_"
								+ download_index + ".mp4";
						String my_name = m_ReturnProgramView.show.episodes[position].name;
						String download_state = "wait";
						DownloadTask downloadTask = new DownloadTask(arg1,
								Detail_Show.this, Detail_Show.this, prod_id,
								download_index, urlstr, localfile);
						downloadTask.execute(prod_id,
								download_index, urlstr,
								m_ReturnProgramView.show.poster, my_name,
								download_state);
						Toast.makeText(Detail_Show.this, "视频已加入下载队列",
								Toast.LENGTH_SHORT).show();
						if(download_indexs.contains(position))
						{
							
						}
						else
						{
							download_indexs.add(position);
						}
						//获取当前综艺有多少集在数据库里,根据电视剧的my_index显示不一样的下载按钮
						data = Dao.getInstance(Detail_Show.this).getInfosOfProd_id(prod_id);
					} else {
						Toast.makeText(Detail_Show.this, "该视频不支持下载",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
		});
	}
	
	private class MyAdapter extends BaseAdapter {
		private LayoutInflater inflater;

		public MyAdapter(Context context) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {

			return download_names.size();
		}

		@Override
		public Object getItem(int position) {

			return download_names.get(position);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			String downloadStr = null;
			convertView = inflater.inflate(R.layout.download_show_item, null,
					false);
			TextView textview = (TextView) convertView
					.findViewById(R.id.text_name);
			textview.setText("  "+(CharSequence) download_names.get(position));//加两个空格是为了让字体显示时不至于太靠左边
			for(int i = 0;i<data.size();i++)
			{
				if(data.get(i).getMy_name().equalsIgnoreCase((String) download_names.get(position)))
				{
					textview.setTextColor(Color.WHITE);
					textview.setBackgroundDrawable(download_focuse);//设置为已缓存
					textview.setText("  "+(CharSequence) download_names.get(position));//加两个空格是为了让字体显示时不至于太靠左边
				}
			}
			if(download_indexs.contains(position))
			{
				textview.setTextColor(Color.WHITE);
				textview.setBackgroundDrawable(download_focuse);//设置为已缓存
				textview.setText("  "+(CharSequence) download_names.get(position));//加两个空格是为了让字体显示时不至于太靠左边
			}
			if (m_ReturnProgramView.show.episodes[position].down_urls != null) {
				for (int k = 0; k < m_ReturnProgramView.show.episodes[position].down_urls[0].urls.length; k++) {
					ReturnProgramView.DOWN_URLS.URLS urls = m_ReturnProgramView.show.episodes[position].down_urls[0].urls[k];
					if (urls != null) {
						if (downloadStr == null && urls.file != null
								&& app.IfSupportFormat(urls.url)
								&& urls.file.trim().equalsIgnoreCase("mp4"))
							downloadStr = urls.url.trim();
					}
				}
			}
			if(downloadStr == null)
			{
				textview.setTextColor(Color.rgb(204, 204, 204));//设置为不可用
			}
//			if(m_ReturnProgramView.show.episodes[position].down_urls == null)
//			{
//				textview.setTextColor(Color.rgb(204, 204, 204));//设置为不可用
//			}
			return convertView;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			if(pageShow)
			{
				finish();
				return super.onKeyDown(keyCode, event);
			}
			else
			{
				pageShow = true;
				setContentView(R.layout.detail_show);
				//调用时有问题
				if (prod_id != null)
					CheckSaveData();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
