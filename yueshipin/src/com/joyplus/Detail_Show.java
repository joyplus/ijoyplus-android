package com.joyplus;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyplus.Service.Return.ReturnProgramView;
import com.joyplus.Video.MovieActivity;
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
	private String PROD_SOURCE = null;
	private String PROD_URI = null;
	private int page_num = 0;
	private int m_FavorityNum = 0;
	private int m_SupportNum = 0;

	private String uid = null;
	private String token = null;
	private String expires_in = null;
	
	private Drawable download_focuse = null;
	private Drawable download_normal = null;
	private Drawable download_press = null;
	//String[] download_names = new String[]{};
	List download_names = new ArrayList();
	private int cur_pos = 0;// 当前显示的一行
	private int count = 0;
	private int[] select = {-1};
	private int[] items_img = {R.drawable.undownload_show,R.drawable.download_show,
			R.drawable.download_show2
			};
	private String[] items_text = { "选项一", "选项二", "选项三","选项一", "选项二", "选项三","选项一", "选项二", "选项三"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_show);
		app = (App) getApplication();
		Intent intent = getIntent();
		prod_id = intent.getStringExtra("prod_id");
		aq = new AQuery(this);

		aq.id(R.id.scrollView1).gone();

		MobclickAgent.updateOnlineConfig(this);
		if (prod_id != null)
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
			cb.header("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
			cb.header("app_key", Constant.APPKEY);
			cb.header("user_id", app.UserID);

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
			app.MyToast(this, getResources().getString(R.string.networknotwork));
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

			app.MyToast(this, getResources().getString(R.string.networknotwork));
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
					m_button.setText(m_ReturnProgramView.show.episodes[i].name);
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
			for (i = 0; i < m_ReturnProgramView.show.episodes.length; i++) {
				if (m_ReturnProgramView.show.episodes[i].down_urls != null) {
					for (int k = 0; k < m_ReturnProgramView.show.episodes[i].down_urls[0].urls.length; k++) {
						if (m_ReturnProgramView.show.episodes[i].down_urls[0].urls[k].url != null
								&& m_ReturnProgramView.show.episodes[i].down_urls[0].urls[k].file
										.equalsIgnoreCase("MP4")
								&& app.IfSupportFormat(m_ReturnProgramView.show.episodes[i].down_urls[0].urls[k].url)) {
							PROD_SOURCE = m_ReturnProgramView.show.episodes[i].down_urls[0].urls[k].url;
							break;
						}
						break;
					}
				}

			}
		}

	}

	public void OnClickImageView(View v) {

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
			m_ReturnProgramView = mapper.readValue(json.toString(),
					ReturnProgramView.class);
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

	// InitListData
	public void GetServiceData() {
		String url = Constant.BASE_URL + "program/view?prod_id=" + prod_id;

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class).weakHandler(this, "InitListData");

		cb.header("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
		cb.header("app_key", Constant.APPKEY);
		cb.header("user_id", app.UserID);

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
					app.MyToast(this, "收藏失败!");
				// Toast.makeText(Detail_Show.this,json.getString("res_code"),Toast.LENGTH_LONG).show();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {

			// ajax error, show error code
			app.MyToast(aq.getContext(),
					getResources().getString(R.string.networknotwork));
		}

	}

	public void OnClickFavorityNum(View v) {
		String url = Constant.BASE_URL + "program/favority";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("prod_id", prod_id);

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.header("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
		cb.header("app_key", Constant.APPKEY);
		cb.header("user_id", app.UserID);

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
					app.MyToast(this, "顶失败!");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			// ajax error, show error code
			app.MyToast(aq.getContext(),
					getResources().getString(R.string.networknotwork));
		}
	}

	public void OnClickSupportNum(View v) {
		String url = Constant.BASE_URL + "program/support";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("prod_id", prod_id);

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.header("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
		cb.header("app_key", Constant.APPKEY);
		cb.header("user_id", app.UserID);

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
		//cb.header("user_id", app.UserID);

		cb.params(params).url(url).type(JSONObject.class)
				.weakHandler(this, "CallServiceResultReportProblem");
		aq.ajax(cb);
		Toast.makeText(Detail_Show.this, "您反馈的问题已提交，我们会尽快处理，感谢您的支持！", Toast.LENGTH_LONG).show();
	}

	public void OnClickPlay(View v) {
		if (MobclickAgent.getConfigParams(this, "playBtnSuppressed").trim()
				.equalsIgnoreCase("1")) {
			app.MyToast(this, "暂无播放链接!");
			return;
		}
		if (PROD_SOURCE != null && PROD_SOURCE.trim().length() > 0) {
			// save to local
			// prod_id|PROD_SOURCE | Pro_url|Pro_name|Pro_name1|Pro_time
			String datainfo = prod_id + "|" + "PROD_SOURCE" + "|"
					+ URLEncoder.encode(PROD_SOURCE) + "|"
					+ m_ReturnProgramView.show.name + "|" + "null" + "|"
					+ "null" + "|3";
			app.SavePlayData(prod_id, datainfo);
			CallVideoPlayActivity(PROD_SOURCE);
		} else if (PROD_URI != null && PROD_URI.trim().length() > 0) {
			// save to local
			// prod_id|PROD_URI | Pro_url|Pro_name|Pro_name1|Pro_time
			String datainfo = prod_id + "|" + "PROD_URI" + "|"
					+ URLEncoder.encode(PROD_URI) + "|"
					+ m_ReturnProgramView.show.name + "|" + "null" + "|"
					+ "null" + "|3";
			app.SavePlayData(prod_id, datainfo);

			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri content_url = Uri.parse(PROD_URI);
			intent.setData(content_url);
			startActivity(intent);
		}
	}

	// OnClickNext4
	public void OnClickNext4(View v) {
		String m_j = null;
		int j = 0;
		int i = 0;

		if (page_num * 4 >= m_ReturnProgramView.show.episodes.length) {
			return;
		}

		page_num++;
		if (m_ReturnProgramView.show.episodes != null) {
			for (i = 4 * page_num; i < m_ReturnProgramView.show.episodes.length
					&& i < 4 * (page_num + 1); i++, j++) {

				m_j = Integer.toString(j);// m_ReturnProgramView.show.episodes[i].name;
				Button m_button = (Button) this.findViewById(getResources()
						.getIdentifier("show_button" + m_j, "id",
								getPackageName()));
				m_button.setTag(i + "");
				m_button.setText(m_ReturnProgramView.show.episodes[i].name);
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
				m_button.setText(m_ReturnProgramView.show.episodes[i].name);
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

		if (MobclickAgent.getConfigParams(this, "playBtnSuppressed").trim()
				.equalsIgnoreCase("1")) {
			app.MyToast(this, "暂无播放链接!");
			return;
		}
		if (m_ReturnProgramView.show.episodes != null
				&& m_ReturnProgramView.show.episodes[index].video_urls != null
				&& m_ReturnProgramView.show.episodes[index].video_urls[0].url != null)
			PROD_URI = m_ReturnProgramView.show.episodes[index].video_urls[0].url;
		if (m_ReturnProgramView.show.episodes[index].down_urls != null
				&& m_ReturnProgramView.show.episodes[index].down_urls[0].urls.length > 0
				&& m_ReturnProgramView.show.episodes[index].down_urls[0].urls[0].url != null
				&& app.IfSupportFormat(m_ReturnProgramView.show.episodes[index].down_urls[0].urls[0].url)) {

			PROD_SOURCE = m_ReturnProgramView.show.episodes[index].down_urls[0].urls[0].url;

		}
		if (PROD_SOURCE != null && PROD_SOURCE.trim().length() > 0) {
			// save to local
			// prod_id|PROD_SOURCE | Pro_url|Pro_name|Pro_name1|Pro_time
			String datainfo = prod_id + "|" + "PROD_SOURCE" + "|"
					+ URLEncoder.encode(PROD_SOURCE) + "|"
					+ m_ReturnProgramView.show.name + "|" + "第"
					+ v.getTag().toString() + "期" + "|" + "null" + "|3";
			app.SavePlayData(prod_id, datainfo);
			CallVideoPlayActivity(PROD_SOURCE);
		} else if (PROD_URI != null && PROD_URI.trim().length() > 0) {
			// save to local
			// prod_id|PROD_URI | Pro_url|Pro_name|Pro_name1|Pro_time
			String datainfo = prod_id + "|" + "PROD_URI" + "|"
					+ URLEncoder.encode(PROD_URI) + "|"
					+ m_ReturnProgramView.show.name + "|" + "第"
					+ v.getTag().toString() + "期" + "|" + "null" + "|3";
			app.SavePlayData(prod_id, datainfo);

			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri content_url = Uri.parse(PROD_URI);
			intent.setData(content_url);
			startActivity(intent);
		}
	}

	public void CallVideoPlayActivity(String m_uri) {

		Intent intent = new Intent(this, MovieActivity.class);
		intent.putExtra("prod_url", m_uri);
		intent.putExtra("prod_id", prod_id);

		try {
			startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			Log.e(TAG, "mp4 fail", ex);
		}

	}
	
	public void OnClickCacheDown(View v) {
		//GotoDownloadPage();
	}

	private void GotoDownloadPage() {
		// TODO Auto-generated method stub
		
		setContentView(R.layout.download_show);
		
		download_focuse = this.getResources().getDrawable(R.drawable.download_show2);
		download_normal = this.getResources().getDrawable(R.drawable.undownload_show);
		download_press = this.getResources().getDrawable(R.drawable.download_show);
		LinearLayout linearbtn = (LinearLayout)findViewById(R.id.btnReturnDetail_Show);
		
		linearbtn.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setContentView(R.layout.detail_show);
				GetServiceData();
			}
		});
		for(int i = 0; i< m_ReturnProgramView.show.episodes.length;i++)
		{
			download_names.add(m_ReturnProgramView.show.episodes[i].name);
		}
		ListView list = (ListView) findViewById(R.id.listViewDownload);
		list.requestFocusFromTouch();
	    MyAdapter adapter = new MyAdapter(this);
	    list.setAdapter(adapter);
	    list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);// 一定要设置这个属性，否则ListView不会刷新
	    list.setTextFilterEnabled(true);
	    list.setItemChecked(0, true);
	    
	    list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				Toast.makeText(Detail_Show.this, "test", Toast.LENGTH_SHORT).show();
				if (m_ReturnProgramView.show.episodes[position].down_urls != null
						&& m_ReturnProgramView.show.episodes[position].down_urls[0].urls.length > 0
						&& m_ReturnProgramView.show.episodes[position].down_urls[0].urls[0].url != null
						&& app.IfSupportFormat(m_ReturnProgramView.show.episodes[position].down_urls[0].urls[0].url)) {

					PROD_SOURCE = m_ReturnProgramView.show.episodes[position].down_urls[0].urls[0].url;

				}
				
				if (PROD_SOURCE != null) {
					String urlstr = PROD_SOURCE;
					String localfile = App.SD_PATH+prod_id+"_"+(position+1)+".mp4";
					//DownloadTask downloadTask = new DownloadTask(arg1,this,Detail_Show.this,prod_id,Integer.toString(position+1),urlstr,localfile);
					//DownloadTask downloadTask = new DownloadTask()
					//downloadTask.execute(prod_id,Integer.toString(position+1),urlstr);
					Toast.makeText(Detail_Show.this,"视频已加入下载队列"+position,Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(Detail_Show.this,"该视频不支持下载",Toast.LENGTH_SHORT).show();
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
			Log.e("TEST", "refresh once");
			convertView = inflater.inflate(R.layout.download_show_item, null, false);
			convertView.setClickable ( true );
			
			//convertView.setOnClickListener(myClickListener); 
			Button btn = (Button)convertView.findViewById(R.id.imageviewtext);
			btn.setText((CharSequence) download_names.get(position));
			if (position == cur_pos) {// 如果当前的行就是ListView中选中的一行，就更改显示样式
				btn.setBackgroundDrawable(download_focuse);
				btn.setTextColor(Color.WHITE);
				btn.setText(" "+download_names.get(position));
			}
			return convertView;	
		}
	}
}
