package com.joyplus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyplus.Detail_Movie.EComparatorIndex;
import com.joyplus.Service.Return.ReturnProgramComments;
import com.joyplus.Service.Return.ReturnProgramView;
import com.joyplus.Service.Return.ReturnUserPlayHistories;
import com.joyplus.Service.Return.ReturnProgramView.DOWN_URLS;
import com.joyplus.Service.Return.ReturnProgramView.EPISODES;
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

public class Detail_TV extends Activity {
	private AQuery aq;
	private String TAG = "Detail_TV";
	private App app;
	private ReturnProgramView m_ReturnProgramView = null;

	private String prod_id = null;
	private String prod_name = null;
	private String PROD_SOURCE = null;
	public String DOWNLOAD_SOURCE = null;
	private String PROD_URI = null;
	private String tv_url = null;
	private String tv_source = null;
	private int current_download_pagenum = 0;
	private int page_num = 0;
	private int m_FavorityNum = 0;
	private int m_SupportNum = 0;
	public List<DownloadInfo> data;

	private String uid = null;
	private String token = null;
	private String expires_in = null;

	private String TV_String = null;

	private ReturnProgramComments m_ReturnProgramComments = null;
	private ScrollView mScrollView;
	private int isLastisNext = 2;
	private int mLastY = 0;

	// added by yyc,in order to flag the playing tv's index btn
	Drawable focuse = null;
	Drawable normal = null;
	Drawable press = null;
	// Drawable download_none = null;
	Drawable download_normal = null;
	Drawable download_been = null;
	private PopupWindow downloadpopup = null;
	ViewGroup popupview;

	private int current_index = -1; // yy
	private static final String MY_SETTING = "myTvSetting";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_tv);
		app = (App) getApplication();
		aq = new AQuery(this);

		Intent intent = getIntent();
		prod_id = intent.getStringExtra("prod_id");
		prod_name = intent.getStringExtra("prod_name");

		if (prod_name != null)
			aq.id(R.id.program_name).text(prod_name);

		// modify by yyc
		focuse = this.getResources().getDrawable(R.drawable.play_focuse);
		normal = this.getResources().getDrawable(R.drawable.play_normal);
		press = this.getResources().getDrawable(R.drawable.play_press);
		download_been = this.getResources()
				.getDrawable(R.drawable.downloaded_2);
		download_normal = this.getResources().getDrawable(
				R.drawable.undownload_tv);
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
		// ReadSettingData
		SharedPreferences myPreference = this.getSharedPreferences(MY_SETTING,
				Context.MODE_PRIVATE);
		if (myPreference != null) {
			String temp = null;
			if (prod_id != null) {
				temp = myPreference.getString(prod_id, "");
			}
			if (temp != "") // myPreference.getString's return value is "",not
							// null
			{
				current_index = Integer.parseInt(temp);
			}
		}

		aq.id(R.id.textView9).gone();
		aq.id(R.id.textView13).gone();
		aq.id(R.id.scrollView1).gone();

		InitTVButtom();
		if (prod_id != null)
			CheckSaveData();
//			GetServiceData();
	}

	public void InitTVButtom() {
		String m_j = null;
		for (int i = 0; i < 15; i++) {
			m_j = Integer.toString(i + 4);
			Button m_button = (Button) this.findViewById(getResources()
					.getIdentifier("tv_button" + m_j, "id", getPackageName()));
			m_button.setVisibility(View.GONE);
		}
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
		if (m_ReturnProgramView.tv != null) {
			AlertDialog alertDialog = new AlertDialog.Builder(this).setMessage(
					m_ReturnProgramView.tv.summary).create();
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

	// added by yyc,for sort the episodesArray
	class EComparator implements Comparator {

		@Override
		public int compare(Object first, Object second) {
			// TODO Auto-generated method stub
			int first_name = Integer.parseInt(((EPISODES) first).name);
			int second_name = Integer.parseInt(((EPISODES) second).name);
			if (first_name - second_name < 0) {
				return -1;
			} else {
				return 1;
			}
		}
	}

	public void InitData() {
		String m_j = null;
		int i = 0;
		int j = 0;
		if (m_ReturnProgramView.tv != null) {
			aq.id(R.id.program_name).text(m_ReturnProgramView.tv.name);
			aq.id(R.id.imageView3).image(m_ReturnProgramView.tv.poster, true,
					true);
			aq.id(R.id.textView5).text(m_ReturnProgramView.tv.stars);
			aq.id(R.id.textView6).text(m_ReturnProgramView.tv.area);
			aq.id(R.id.textView7).text(m_ReturnProgramView.tv.directors);
			aq.id(R.id.textView8).text(m_ReturnProgramView.tv.publish_date);

			m_FavorityNum = Integer
					.parseInt(m_ReturnProgramView.tv.favority_num);
			aq.id(R.id.button2).text("收藏(" + m_FavorityNum + ")");
			m_SupportNum = Integer.parseInt(m_ReturnProgramView.tv.support_num);
			aq.id(R.id.button3).text("顶(" + m_SupportNum + ")");

			aq.id(R.id.textView11)
					.text("    " + m_ReturnProgramView.tv.summary);

			if (m_ReturnProgramView.tv.episodes != null) {
				m_ReturnProgramView.tv.current_play = 0;
				if (m_ReturnProgramView.tv.episodes.length > 0) {
					aq.id(R.id.textView13)
							.text("共("
									+ Integer
											.toString(m_ReturnProgramView.tv.episodes.length)
									+ "集)");
					aq.id(R.id.textView13).visible();
				}
				aq.id(R.id.imageView_zxbf).visible();
				aq.id(R.id.textView13).visible();

				// sort the tv's playStateIndex by yyc
				Arrays.sort(m_ReturnProgramView.tv.episodes, new EComparator());

				if (m_ReturnProgramView.tv.episodes.length > 15) {
					aq.id(R.id.textView9).visible();
				}
				for (i = 0; i < m_ReturnProgramView.tv.episodes.length
						&& i < 15; i++) {
					m_j = Integer.toString(i + 4);// m_ReturnProgramView.tv.episodes[i].name;
					String str = m_ReturnProgramView.tv.episodes[i].name;
					Button m_button = (Button) this.findViewById(getResources()
							.getIdentifier("tv_button" + m_j, "id",
									getPackageName()));
					m_j = Integer.toString(i + 1);
					m_button.setTag(i + "");
					m_button.setText(m_j);
					// yy
					if (current_index == i) {
						m_button.setBackgroundDrawable(focuse);
						m_button.setText("");
					} else {
						m_button.setBackgroundDrawable(normal);
					}
					if (m_ReturnProgramView.tv.episodes != null
							&& m_ReturnProgramView.tv.episodes[i].video_urls != null
							&& m_ReturnProgramView.tv.episodes[i].video_urls.length > 0) {
						tv_url = m_ReturnProgramView.tv.episodes[i].video_urls[0].url;

					}
					if (m_ReturnProgramView.tv.episodes[i].down_urls != null
							&& m_ReturnProgramView.tv.episodes[i].down_urls[0].urls.length > 0
							&& m_ReturnProgramView.tv.episodes[i].down_urls[0].urls[0].url != null) {

						tv_source = m_ReturnProgramView.tv.episodes[i].down_urls[0].urls[0].url;
					}
					// lost one tv
					if (tv_url == null && tv_source == null) {
						m_button.setEnabled(false);
					} else {
						m_button.setEnabled(true);
					}
					m_button.setVisibility(View.VISIBLE);
				}
				if (i < 15) {
					for (j = i; j < 15; j++) {
						m_j = Integer.toString(j + 4);// m_ReturnProgramView.tv.episodes[i].name;
						Button m_button = (Button) this
								.findViewById(getResources().getIdentifier(
										"tv_button" + m_j, "id",
										getPackageName()));
						m_button.setVisibility(View.GONE);
						aq.id(R.id.textView9).gone();
					}
				}

			}
			if (m_ReturnProgramView.tv.episodes != null
					&& m_ReturnProgramView.tv.episodes[0].video_urls != null
					&& m_ReturnProgramView.tv.episodes[0].video_urls[0].url != null)
				PROD_URI = m_ReturnProgramView.tv.episodes[0].video_urls[0].url;
			videoSourceSort(0);
			if (m_ReturnProgramView.tv.episodes[0].down_urls != null) {
				for (i = 0; i < m_ReturnProgramView.tv.episodes[0].down_urls.length; i++) {
					for (int k = 0; k < m_ReturnProgramView.tv.episodes[0].down_urls[i].urls.length; k++) {
						ReturnProgramView.DOWN_URLS.URLS urls = m_ReturnProgramView.tv.episodes[0].down_urls[i].urls[k];
						if (urls != null) {
							if (urls.url != null
									&& app.IfSupportFormat(urls.url)) {
								if (PROD_SOURCE == null
										&& !app.IfIncludeM3U(urls.url))
									PROD_SOURCE = urls.url.trim();
								if (PROD_SOURCE == null
										&& urls.type.trim().equalsIgnoreCase(
												"mp4"))
									PROD_SOURCE = urls.url.trim();
								else if (PROD_SOURCE == null
										&& urls.type.trim().equalsIgnoreCase(
												"flv"))
									PROD_SOURCE = urls.url.trim();
								else if (PROD_SOURCE == null
										&& urls.type.trim().equalsIgnoreCase(
												"hd2"))
									PROD_SOURCE = urls.url.trim();
								else if (PROD_SOURCE == null
										&& urls.type.trim().equalsIgnoreCase(
												"3gp"))
									PROD_SOURCE = urls.url.trim();
							}
							if (DOWNLOAD_SOURCE == null && urls.file != null
									&& app.IfSupportFormat(urls.url)
									&& urls.file.trim().equalsIgnoreCase("mp4"))
								DOWNLOAD_SOURCE = urls.url.trim();
							if (PROD_SOURCE != null && DOWNLOAD_SOURCE != null)
								break;
						}
						if (PROD_SOURCE != null && DOWNLOAD_SOURCE != null)
							break;
					}
				}

				if (DOWNLOAD_SOURCE == null) {
					aq.id(R.id.button20).background(R.drawable.zan_wu_xia_zai);
					aq.id(R.id.button20).clickable(false);
				}

				if (m_ReturnProgramView.comments != null
						&& m_ReturnProgramView.comments.length >= 1) {
					ShowComments();
				} else {
					aq.id(R.id.imageView_comment).gone();
					aq.id(R.id.Layout_comment).gone();
				}
			} else {
				aq.id(R.id.button20).background(R.drawable.zan_wu_xia_zai);
				aq.id(R.id.button20).clickable(false);
			}
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
			TV_String = json.toString();

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
	
	// InitListData
	public void GetServiceData() {
		String url = Constant.BASE_URL + "program/view?prod_id=" + prod_id;

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class).weakHandler(this, "InitListData");

		cb.SetHeader(app.getHeaders());

		aq.id(R.id.ProgressText).visible();
		aq.progress(R.id.progress).ajax(cb);

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
		Toast.makeText(Detail_TV.this, "您反馈的问题已提交，我们会尽快处理，感谢您的支持！",
				Toast.LENGTH_LONG).show();
	}

	public void OnClickPlay(View v) {
		if (MobclickAgent.getConfigParams(this, "playBtnSuppressed").trim()
				.equalsIgnoreCase("1")) {
			app.MyToast(this, "暂无播放链接!");
			return;
		}
		app.checkUserSelect(Detail_TV.this);
		if(app.use2G3G)
		{
			// write current_index to myTvSetting file
			current_index = 0;
			SharedPreferences myPreference = this.getSharedPreferences(MY_SETTING,
					Context.MODE_PRIVATE);
			myPreference.edit().putString(prod_id, Integer.toString(current_index))
					.commit();

			if (PROD_SOURCE != null && PROD_SOURCE.trim().length() > 0) {
				CallVideoPlayActivity(PROD_SOURCE, m_ReturnProgramView.tv.name);
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
	
	// OnClickNext15
	public void OnClickNext15(View v) {
		String m_j = null;
		int j = 0;
		int i = 0;
		page_num++;
		if ((page_num + 1) * 15 >= m_ReturnProgramView.tv.episodes.length) {
			aq.id(R.id.textView9).gone();
		}
		aq.id(R.id.textView15).visible();
		if (m_ReturnProgramView.tv.episodes != null) {
			for (i = 15 * page_num; i < m_ReturnProgramView.tv.episodes.length
					&& i < 15 * (page_num + 1); i++, j++) {

				m_j = Integer.toString(j + 4);// m_ReturnProgramView.tv.episodes[i].name;
				Button m_button = (Button) this.findViewById(getResources()
						.getIdentifier("tv_button" + m_j, "id",
								getPackageName()));
				m_j = Integer.toString(i + 1);
				m_button.setTag(i + "");
				m_button.setText(m_j);
				// yy
				if (current_index == i) {
					m_button.setBackgroundDrawable(focuse);
					m_button.setText("");
				} else {
					m_button.setBackgroundDrawable(normal);
				}
				// lost one tv
				if (m_ReturnProgramView.tv.episodes[i].video_urls == null
						&& m_ReturnProgramView.tv.episodes[i].down_urls == null) {
					m_button.setEnabled(false);
				} else {
					m_button.setEnabled(true);
				}
				m_button.setVisibility(View.VISIBLE);
			}
			if (j < 15) {
				for (i = j; i < 15; i++) {
					m_j = Integer.toString(i + 4);// m_ReturnProgramView.tv.episodes[i].name;
					Button m_button = (Button) this.findViewById(getResources()
							.getIdentifier("tv_button" + m_j, "id",
									getPackageName()));
					m_button.setVisibility(View.GONE);
					aq.id(R.id.textView9).gone();
				}
			}
		}
	}

	public void OnClickPre15(View v) {
		String m_j = null;
		int j = 0;
		int i = 0;

		if (page_num == 0) {
			return;

		} else if (page_num == 1) {
			aq.id(R.id.textView15).gone();

		}
		if (page_num * 15 < m_ReturnProgramView.tv.episodes.length) {
			aq.id(R.id.textView9).visible();
		}
		page_num--;
		if (m_ReturnProgramView.tv.episodes != null && page_num >= 0) {
			for (i = 15 * page_num; i < m_ReturnProgramView.tv.episodes.length
					&& i < 15 * (page_num + 1); i++, j++) {

				m_j = Integer.toString(j + 4);// m_ReturnProgramView.tv.episodes[i].name;
				Button m_button = (Button) this.findViewById(getResources()
						.getIdentifier("tv_button" + m_j, "id",
								getPackageName()));
				m_j = Integer.toString(i + 1);
				m_button.setTag(i + "");
				m_button.setText(m_j);
				// yy
				if (current_index == i) {
					m_button.setBackgroundDrawable(focuse);
					m_button.setText("");
				} else {
					m_button.setBackgroundDrawable(normal);
				}
				// lost one tv
				if (m_ReturnProgramView.tv.episodes[i].video_urls == null
						&& m_ReturnProgramView.tv.episodes[i].down_urls == null)// one

				{
					m_button.setEnabled(false);
				} else {
					m_button.setEnabled(true);
				}
				m_button.setVisibility(View.VISIBLE);
			}
			if (j < 15) {
				for (i = j; i < 15; i++) {
					m_j = Integer.toString(i + 4);// m_ReturnProgramView.tv.episodes[i].name;
					Button m_button = (Button) this.findViewById(getResources()
							.getIdentifier("tv_button" + m_j, "id",
									getPackageName()));
					m_button.setVisibility(View.GONE);
				}
			}
		}
	}

	public void OnClickTVPlay(View v) {

		int index = Integer.parseInt(v.getTag().toString());
		app.checkUserSelect(Detail_TV.this);
		if(app.use2G3G)
		{
			current_index = index;
			SetPlayBtnFlag(current_index);

			// write current_index to myTvSetting file
			SharedPreferences myPreference = this.getSharedPreferences(MY_SETTING,
					Context.MODE_PRIVATE);
			myPreference.edit().putString(prod_id, Integer.toString(current_index))
					.commit();

			if (MobclickAgent.getConfigParams(this, "playBtnSuppressed").trim()
					.equalsIgnoreCase("1")) {
				app.MyToast(this, "暂无播放链接!");
				return;
			}
			videoSourceSort(index);
			if (m_ReturnProgramView.tv.episodes != null
					&& m_ReturnProgramView.tv.episodes[index].video_urls != null
					&& m_ReturnProgramView.tv.episodes[index].video_urls.length > 0)
				PROD_URI = m_ReturnProgramView.tv.episodes[index].video_urls[0].url;
			PROD_SOURCE = null;
			if (m_ReturnProgramView.tv.episodes[index].down_urls != null) {
				for (int i = 0; i < m_ReturnProgramView.tv.episodes[index].down_urls.length; i++) {
					for (int k = 0; k < m_ReturnProgramView.tv.episodes[index].down_urls[i].urls.length; k++) {
						ReturnProgramView.DOWN_URLS.URLS urls = m_ReturnProgramView.tv.episodes[index].down_urls[i].urls[k];
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

			if (PROD_SOURCE != null && PROD_SOURCE.trim().length() > 0) {
				CallVideoPlayActivity(PROD_SOURCE, m_ReturnProgramView.tv.name);
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

	public void videoSourceSort(int source_index) {
		if (m_ReturnProgramView.tv.episodes[source_index].down_urls != null) {
			for (int j = 0; j < m_ReturnProgramView.tv.episodes[source_index].down_urls.length; j++) {
				if (m_ReturnProgramView.tv.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("letv")) {
					m_ReturnProgramView.tv.episodes[source_index].down_urls[j].index = 0;
				} else if (m_ReturnProgramView.tv.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("fengxing")) {
					m_ReturnProgramView.tv.episodes[source_index].down_urls[j].index = 1;
				} else if (m_ReturnProgramView.tv.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("qiyi")) {
					m_ReturnProgramView.tv.episodes[source_index].down_urls[j].index = 2;
				} else if (m_ReturnProgramView.tv.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("youku")) {
					m_ReturnProgramView.tv.episodes[source_index].down_urls[j].index = 3;
				} else if (m_ReturnProgramView.tv.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("sinahd")) {
					m_ReturnProgramView.tv.episodes[source_index].down_urls[j].index = 4;
				} else if (m_ReturnProgramView.tv.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("sohu")) {
					m_ReturnProgramView.tv.episodes[source_index].down_urls[j].index = 5;
				} else if (m_ReturnProgramView.tv.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("56")) {
					m_ReturnProgramView.tv.episodes[source_index].down_urls[j].index = 6;
				} else if (m_ReturnProgramView.tv.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("qq")) {
					m_ReturnProgramView.tv.episodes[source_index].down_urls[j].index = 7;
				} else if (m_ReturnProgramView.tv.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("pptv")) {
					m_ReturnProgramView.tv.episodes[source_index].down_urls[j].index = 8;
				} else if (m_ReturnProgramView.tv.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("m1905")) {
					m_ReturnProgramView.tv.episodes[source_index].down_urls[j].index = 9;
				}
			}
			if (m_ReturnProgramView.tv.episodes[source_index].down_urls.length > 1) {
				Arrays.sort(
						m_ReturnProgramView.tv.episodes[source_index].down_urls,
						new EComparatorIndex());
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

	//
	public void CallVideoPlayActivity() {

		if (PROD_SOURCE != null && PROD_SOURCE.trim().length() > 0) {
			GetVideoSource(0, PROD_SOURCE);

		} else if (PROD_URI != null && PROD_URI.trim().length() > 0) {
			GetVideoSource(1, PROD_URI);

			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri content_url = Uri.parse(PROD_URI);
			intent.setData(content_url);
			startActivity(intent);
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

	public void CallVideoPlayActivity(String m_uri, String title) {
		app.IfSupportFormat(m_uri);
		
		Intent intent = new Intent(this, VideoPlayerActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("path", m_uri);
		bundle.putString("title", title);
		bundle.putString("prod_id", prod_id);
		bundle.putInt("prod_quality", 0);
		bundle.putString("prod_subname","第"+ m_ReturnProgramView.tv.episodes[current_index].name  + "集");
		bundle.putString("prod_type", "2");
		bundle.putLong("current_time", 0);
		intent.putExtras(bundle);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			Log.e(TAG, "VideoPlayerActivity fail", ex);
		}
	}

	/*
	 * 保存网页播放地址,不需要保存时间
	 */
	private void SaveToServer(int play_type, String SourceUrl, int episodesNum) {

		String url = Constant.BASE_URL + "program/play";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("app_key", Constant.APPKEY);// required string
												// 申请应用时分配的AppKey。
		params.put("prod_id", m_ReturnProgramView.tv.id);// required string
															// 视频id
		params.put("prod_name", m_ReturnProgramView.tv.name);// required
																// string 视频名字
		params.put("prod_subname", Integer.toString(episodesNum));// required
																	// string
																	// 视频的集数
		params.put("prod_type", 2);// required int 视频类别 1：电影，2：电视剧，3：综艺，4：视频
		params.put("playback_time", 0);// _time required int 上次播放时间，单位：秒
		params.put("duration", 0);// required int 视频时长， 单位：秒

		if (play_type == 1)
			params.put("play_type", "1");// required string
		else
			params.put("play_type", "2");

		// 播放的类别 1: 视频地址播放
		// 2:webview播放
		params.put("video_url", SourceUrl);// required
		// string
		// 视频url

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.SetHeader(app.getHeaders());

		cb.params(params).url(url).type(JSONObject.class)
				.weakHandler(this, "CallProgramPlayResult");
		// cb.params(params).url(url);
		aq.ajax(cb);
	}

	public void CallProgramPlayResult(String url, JSONObject json,
			AjaxStatus status) {
		/*
		 * 保存播放记录的回调函数
		 */
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
							params.put("prod_id", m_ReturnProgramView.tv.id);// required
																				// string
																				// 视频id
							params.put("prod_name", m_ReturnProgramView.tv.name);// required
																					// string
																					// 视频名字
							params.put("prod_subname",
									m_ReturnProgramView.tv.episodes.length);// required
																			// string
																			// 视频的集数
							params.put("prod_type", 2);// required int 视频类别
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
									m_ReturnProgramView.tv.name);
						} else {
							if (m_ReturnProgramView.tv.episodes[episodeNum].down_urls != null) {
								for (int k = 0; k < m_ReturnProgramView.tv.episodes[episodeNum].down_urls[0].urls.length; k++) {
									ReturnProgramView.DOWN_URLS.URLS urls = m_ReturnProgramView.tv.episodes[episodeNum].down_urls[0].urls[k];
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

	// click which btn flag that one yy
	public void SetPlayBtnFlag(int current_index) {
		String m_j = null;
		if (current_index / 15 != m_ReturnProgramView.tv.episodes.length / 15) {
			for (int i = 0; (i < 15); i++) {

				m_j = Integer.toString(i + 4);
				Button m_button = (Button) this.findViewById(getResources()
						.getIdentifier("tv_button" + m_j, "id",
								getPackageName()));
				if (i == current_index % 15) {
					// button's shape'll change
					m_button.setBackgroundDrawable(focuse);
					m_button.setText("");
				} else {
					m_button.setBackgroundDrawable(normal);
					m_button.setText(Integer.toString(i + 1));
				}
			}
		} else {
			for (int i = 0; (i < m_ReturnProgramView.tv.episodes.length % 15); i++) {
				m_j = Integer.toString(i + 4);
				Button m_button = (Button) this.findViewById(getResources()
						.getIdentifier("tv_button" + m_j, "id",
								getPackageName()));
				if (i == current_index % 15) {
					m_button.setBackgroundDrawable(focuse);
					m_button.setText("");
				} else {
					m_button.setBackgroundDrawable(normal);
					m_button.setText(Integer.toString(i + 1));
				}
			}
		}

	}

	public void OnClickCacheDown(View v) {
		popupview = OpenDownloadPapup();
	}

	private ViewGroup OpenDownloadPapup() {
		// TODO Auto-generated method stub
		LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		final ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
				R.layout.download_tv, null, true);
		Button download_prevbtn = (Button) menuView
				.findViewById(R.id.download_prevbtn);
		Button download_nextbtn = (Button) menuView
				.findViewById(R.id.download_nextbtn);
		Button download_btn_page1 = (Button) menuView
				.findViewById(R.id.download_btn_page1);
		Button download_btn_page2 = (Button) menuView
				.findViewById(R.id.download_btn_page2);
		Button download_btn_page3 = (Button) menuView
				.findViewById(R.id.download_btn_page3);
		Button download_btn_page4 = (Button) menuView
				.findViewById(R.id.download_btn_page4);
		download_btn_page1.setOnClickListener(listener);
		download_btn_page2.setOnClickListener(listener);
		download_btn_page3.setOnClickListener(listener);
		download_btn_page4.setOnClickListener(listener);
		download_prevbtn.setOnClickListener(listener);
		download_nextbtn.setOnClickListener(listener);
		InitDownloadData(menuView);
		downloadpopup = new PopupWindow(menuView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		downloadpopup.setBackgroundDrawable(new BitmapDrawable());
		downloadpopup.setAnimationStyle(R.style.PopupAnimation);
		downloadpopup.showAtLocation(findViewById(R.id.parent), Gravity.CENTER
				| Gravity.CENTER, 0, 78);
		downloadpopup.update();
		return menuView;
	}

	public OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stu
			switch (v.getId()) {
			case R.id.download_btn_page1:
				switchPageOfDownloadIndex(Integer.parseInt(v.getTag()
						.toString()));
				break;
			case R.id.download_btn_page2:
				switchPageOfDownloadIndex(Integer.parseInt(v.getTag()
						.toString()));
				break;
			case R.id.download_btn_page3:
				switchPageOfDownloadIndex(Integer.parseInt(v.getTag()
						.toString()));
				break;
			case R.id.download_btn_page4:
				switchPageOfDownloadIndex(Integer.parseInt(v.getTag()
						.toString()));
				break;
			case R.id.download_prevbtn:
				if (current_download_pagenum > 0) {
					current_download_pagenum--;
				}
				InitDownloadData(popupview);
				break;
			case R.id.download_nextbtn:

				if (m_ReturnProgramView.tv.episodes.length < 60) {
					return;
				} else {
					if (m_ReturnProgramView.tv.episodes.length % 60 == 0) {
						if (current_download_pagenum < (m_ReturnProgramView.tv.episodes.length / 60 - 1)) {
							current_download_pagenum++;
						}
					} else {
						if (current_download_pagenum < (m_ReturnProgramView.tv.episodes.length / 60)) {
							current_download_pagenum++;
						}
					}
				}
				InitDownloadData(popupview);
				break;
			}
		}
	};

	void switchPageOfDownloadIndex(int index) {
		// 获取当前电视剧有多少集在数据库里,根据电视剧的my_index显示不一样的下载按钮
		data = Dao.getInstance(Detail_TV.this).getInfosOfProd_id(prod_id);
		String m_j = null;
		int i = 0;
		for (i = 0; i < 15; i++) {
			m_j = Integer.toString(i + 4);
			Button m_button = (Button) popupview.findViewById(getResources()
					.getIdentifier("download_button" + m_j, "id",
							getPackageName()));
			m_button.setVisibility(View.GONE);
		}
		for (i = 0; (i + index * 15 + current_download_pagenum * 60) < m_ReturnProgramView.tv.episodes.length
				&& i < 15; i++) {
			m_j = Integer.toString(i + 4);// m_ReturnProgramView.tv.episodes[i].name;
			Button m_button = (Button) popupview.findViewById(getResources()
					.getIdentifier("download_button" + m_j, "id",
							getPackageName()));
			m_j = Integer.toString(i + 1 + index * 15
					+ current_download_pagenum * 60);
			m_button.setTag(m_j + "");
			m_button.setText(m_j);
			m_button.setBackgroundDrawable(download_normal);// 显示之前将背景设置为正常背景色
			m_button.setTextColor(Color.BLACK);//设置颜色和文字的位置
			m_button.setGravity(Gravity.CENTER);
			m_button.setEnabled(true);
			for (int m = 0; m < data.size(); m++) {
				if (data.get(m).getIndex().equalsIgnoreCase(m_j)) { // 设置已缓存背景
					m_button.setBackgroundDrawable(download_been);
					m_button.setEnabled(false);
					m_button.setTextColor(Color.WHITE);//设置颜色和文字的位置
					m_button.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
				}
			}
			m_button.setVisibility(View.VISIBLE);
		}
	}

	public void OnClickDownloadCacheVideo(View v) {
		int index = Integer.parseInt(v.getTag().toString());
		index--;
		DOWNLOAD_SOURCE = null;
		if (m_ReturnProgramView.tv.episodes[index].down_urls != null) {
			for (int i = 0; i < m_ReturnProgramView.tv.episodes[index].down_urls.length; i++) {
				for (int k = 0; k < m_ReturnProgramView.tv.episodes[index].down_urls[i].urls.length; k++) {
					ReturnProgramView.DOWN_URLS.URLS urls = m_ReturnProgramView.tv.episodes[index].down_urls[i].urls[k];
					if (urls != null) {

						if (DOWNLOAD_SOURCE == null && urls.file != null
								&& app.IfSupportFormat(urls.url)
								&& urls.file.trim().equalsIgnoreCase("mp4"))
							// DOWNLOAD_SOURCE = urls.url.trim();
							DOWNLOAD_SOURCE = urls.url;
						if (DOWNLOAD_SOURCE != null)
							break;
					}
					if (DOWNLOAD_SOURCE != null)
						break;
				}
			}
		}
		if (DOWNLOAD_SOURCE == null)
			return;
		app.checkUserSelect(Detail_TV.this);
		if(app.use2G3G)
		{
			if (DOWNLOAD_SOURCE != null) {
				String urlstr = DOWNLOAD_SOURCE;
				String localfile = Constant.PATH_VIDEO + prod_id + "_"
						+ (index + 1) + ".mp4";
				String my_name = m_ReturnProgramView.tv.name;
				String download_state = "wait";
				DownloadTask downloadTask = new DownloadTask(v, this,
						Detail_TV.this, prod_id, Integer.toString(index + 1),
						urlstr, localfile);
				downloadTask.execute(prod_id, Integer.toString(index + 1), urlstr,
						m_ReturnProgramView.tv.poster, my_name, download_state);
				Toast.makeText(Detail_TV.this, "视频已加入下载队列", Toast.LENGTH_SHORT)
						.show();
				// 将按钮的背景色改成已缓存
				v.setBackgroundDrawable(download_been);
				v.setEnabled(false);
				((Button) v).setTextColor(Color.WHITE);//设置颜色和文字的位置
				((Button) v).setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);

			} else {
				Toast.makeText(Detail_TV.this, "该视频不支持下载", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	// reflash the download btns
	public void InitDownloadData(ViewGroup menuView) {
		String m_j = null;
		int i = 0;
		int j = 0;
		int k = 0;
		int total = m_ReturnProgramView.tv.episodes.length;
		// 获取当前电视剧有多少集在数据库里,根据电视剧的my_index显示不一样的下载按钮
		data = Dao.getInstance(Detail_TV.this).getInfosOfProd_id(prod_id);
		for (int m = 0; m < 4; m++) {
			m_j = Integer.toString(m + 1);// m_ReturnProgramView.tv.episodes[i].name;
			Button m_button = (Button) menuView.findViewById(getResources()
					.getIdentifier("download_btn_page" + m_j, "id",
							getPackageName()));
			m_button.setVisibility(View.GONE);
		}
		for (k = 0; (k < 4)
				&& (k < (total - (current_download_pagenum) * 60) / 15); k++) {
			m_j = Integer.toString(k + 1);// m_ReturnProgramView.tv.episodes[i].name;
			Button m_button = (Button) menuView.findViewById(getResources()
					.getIdentifier("download_btn_page" + m_j, "id",
							getPackageName()));
			m_j = Integer.toString(k + 1);
			m_j = Integer.toString(k * 15 + 1 + current_download_pagenum * 60)
					+ "-"
					+ Integer.toString((k + 1) * 15 + current_download_pagenum
							* 60);
			m_button.setTag(k + "");
			m_button.setText(m_j);
			m_button.setVisibility(View.VISIBLE);
		}
		if ((k * 15 + current_download_pagenum * 60) < (total) && (k < 4)) {
			k++;
			m_j = Integer.toString(k);// m_ReturnProgramView.tv.episodes[i].name;
			Button m_button = (Button) menuView.findViewById(getResources()
					.getIdentifier("download_btn_page" + m_j, "id",
							getPackageName()));
			m_j = Integer.toString(k + 1);
			m_j = Integer.toString((k - 1) * 15 + 1 + current_download_pagenum
					* 60)
					+ "-" + Integer.toString(total);
			m_button.setTag(k - 1 + "");
			m_button.setText(m_j);
			m_button.setVisibility(View.VISIBLE);
		}

		for (i = 0; i < m_ReturnProgramView.tv.episodes.length && i < 15; i++) {
			m_j = Integer.toString(i + 4);
			Button m_button = (Button) menuView.findViewById(getResources()
					.getIdentifier("download_button" + m_j, "id",
							getPackageName()));
			m_j = Integer.toString(i + 1 + current_download_pagenum * 60);// 特别加上的
			m_button.setTag(i + 1 + current_download_pagenum * 60 + "");
			m_button.setText(m_j);
			m_button.setBackgroundDrawable(download_normal);// 显示之前将背景设置为正常背景色
			m_button.setTextColor(Color.BLACK);//设置颜色和文字的位置
			m_button.setGravity(Gravity.CENTER);
			m_button.setEnabled(true);
			for (int m = 0; m < data.size(); m++) {
				if (data.get(m).getIndex().equalsIgnoreCase(m_j)) { // 设置已缓存背景
					m_button.setBackgroundDrawable(download_been);
					m_button.setEnabled(false);
					m_button.setTextColor(Color.WHITE);//设置颜色和文字的位置
					m_button.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
				}
			}
			m_button.setVisibility(View.VISIBLE);
		}
		if (i < 15) {
			for (j = i; j < 15; j++) {
				m_j = Integer.toString(j + 4);// m_ReturnProgramView.tv.episodes[i].name;
				Button m_button = (Button) menuView.findViewById(getResources()
						.getIdentifier("download_button" + m_j, "id",
								getPackageName()));
				m_button.setVisibility(View.GONE);
				aq.id(R.id.textView9).gone();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

}
