package com.ijoyplus;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ijoyplus.Service.Return.ReturnProgramView;
import com.ijoyplus.Video.MovieActivity;
import com.ijoyplus.weibo.net.AccessToken;
import com.ijoyplus.weibo.net.DialogError;
import com.ijoyplus.weibo.net.Weibo;
import com.ijoyplus.weibo.net.WeiboDialogListener;
import com.ijoyplus.weibo.net.WeiboException;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_show);
		app = (App) getApplication();
		Intent intent = getIntent();
		prod_id = intent.getStringExtra("prod_id");
		aq = new AQuery(this);

		aq.id(R.id.scrollView1).gone();

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
		cb.header("app_key", Constant.APPKEY);
		cb.header("user_id", app.UserID);

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
					String url2 = Constant.BASE_URL + "user/view";

					AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
					cb.url(url2).type(JSONObject.class)
							.weakHandler(this, "AccountBindAccountResult3");

					cb.header("User-Agent",
							"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
					cb.header("app_key", Constant.APPKEY);
					cb.header("user_id", app.UserID);

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
			app.SaveServiceData("UserInfo", json.toString());
			Intent i = new Intent(this, Sina_Share.class);
			i.putExtra("prod_name", aq.id(R.id.program_name).getText()
					.toString());
			startActivity(i);
			// app.MyToast(this, "更新头像成功!");

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
			aq.id(R.id.imageView3)
					.image(m_ReturnProgramView.show.poster, true, true);
			aq.id(R.id.textView5).text(m_ReturnProgramView.show.publish_date);
			aq.id(R.id.textView6).text(m_ReturnProgramView.show.area);
			if(m_ReturnProgramView.show.stars.trim().length()>0)
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
			if(m_ReturnProgramView.show.episodes.length <= 1 && m_ReturnProgramView.show.episodes[0].name.trim().equalsIgnoreCase("1")){
				aq.id(R.id.LinearLayoutXGYD).gone();
			}
			if (m_ReturnProgramView.show.episodes != null
					&& m_ReturnProgramView.show.episodes[0].video_urls != null
					&& m_ReturnProgramView.show.episodes[0].video_urls[0].url != null)
				PROD_URI = m_ReturnProgramView.show.episodes[0].video_urls[0].url;
			for (i = 0; i < m_ReturnProgramView.show.episodes.length; i++) {

				if (m_ReturnProgramView.show.episodes[i].down_urls != null
						&& m_ReturnProgramView.show.episodes[i].down_urls[0].urls.length > 0
						&& m_ReturnProgramView.show.episodes[i].down_urls[0].urls[0].url != null
						&& app.IfSupportFormat(m_ReturnProgramView.show.episodes[i].down_urls[0].urls[0].url)) {

					PROD_SOURCE = m_ReturnProgramView.show.episodes[i].down_urls[0].urls[0].url;
					break;

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
				if (json.getString("res_code").trim().equalsIgnoreCase("00000")) {
					m_FavorityNum++;
					aq.id(R.id.button2).text(
							"收藏(" + Integer.toString(m_FavorityNum) + ")");
					app.MyToast(this, "收藏成功!");
				} else
					app.MyToast(this, "收藏失败!");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {

			// ajax error, show error code
			app.MyToast(aq.getContext(),getResources().getString(R.string.networknotwork));
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
			app.MyToast(aq.getContext(), getResources().getString(R.string.networknotwork));
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

	public void OnClickPlay(View v) {
		//
		if (PROD_SOURCE != null && PROD_SOURCE.trim().length() > 0) {
			// save to local
			// prod_id|PROD_SOURCE | Pro_url|Pro_name|Pro_name1|Pro_time
			String datainfo = prod_id + "|" + "PROD_SOURCE" + "|"
					+ URLEncoder.encode(PROD_SOURCE) + "|"
					+ m_ReturnProgramView.show.name + "|" + "null" + "|"
					+ "null" + "|3";
			app.SavePlayData(prod_id, datainfo);
			CallVideoPlayActivity(PROD_SOURCE);
			/*
			 * intent = new Intent(); intent.putExtra("SOURCE", PROD_SOURCE);
			 * //intent.setClass(context, MediaActivity.class);
			 * intent.setClass(context, VideoPlayerActivity.class);
			 * startActivity(intent);
			 */
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
			/*
			 * intent = new Intent(); intent.putExtra("SOURCE", PROD_SOURCE);
			 * //intent.setClass(context, MediaActivity.class);
			 * intent.setClass(context, VideoPlayerActivity.class);
			 * startActivity(intent);
			 */
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
}
