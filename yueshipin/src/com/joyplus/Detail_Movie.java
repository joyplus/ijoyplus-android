package com.joyplus;

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
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

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

public class Detail_Movie extends Activity {
	private AQuery aq;
	private App app;
	private String TAG = "Detail_Movie";
	private ReturnProgramView m_ReturnProgramView = null;
	private String prod_id = null;
	private String PROD_SOURCE = null;
	private String PROD_URI = null;
	private int m_FavorityNum = 0;
	private int m_SupportNum = 0;

	private String uid = null;
	private String token = null;
	private String expires_in = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_movie);
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

	public boolean UploadSinaHeadAndScreen_nameUrl(String access_token, String uid) {
		String m_GetURL = "https://api.weibo.com/2/users/show.json?access_token="
				+ access_token + "&uid=" + uid;

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(m_GetURL).type(JSONObject.class).weakHandler(this, "UploadSinaHeadAndScreen_nameUrlResult");

		cb.header("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");

		aq.ajax(cb);

		return false;
	}

	public void UploadSinaHeadAndScreen_nameUrlResult(String url, JSONObject json,
			AjaxStatus status) {
		String head_url = json.optString("avatar_large");
		String screen_name = json.optString("screen_name");
		if (head_url != null && screen_name != null) {
			String m_PostURL = Constant.BASE_URL + "account/bindAccount";

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("source_id", uid);
			params.put("source_type", "1");
			params.put("pic_url", head_url);
			params.put("nickname", screen_name);
			
			//save to local
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
				if (json.getString("res_code").trim().equalsIgnoreCase("00000")){
					
					//reload the userinfo
					String url2 = Constant.BASE_URL + "user/view?userid="+ app.UserID;

					AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
					cb.url(url2).type(JSONObject.class).weakHandler(this, "AccountBindAccountResult3");

					cb.header("User-Agent",
							"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
					cb.header("app_key", Constant.APPKEY);
					//cb.header("user_id", app.UserID);

					aq.ajax(cb);
				}
//				else
//					app.MyToast(this, "更新头像失败!");
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
			if (json.getString("nickname").trim().length() >0) {
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

	public void OnClickContent(View v) throws JSONException {

		AlertDialog alertDialog = new AlertDialog.Builder(this).setMessage(
				m_ReturnProgramView.movie.summary).create();
		Window window = alertDialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.alpha = 0.6f;
		window.setAttributes(lp);
		alertDialog.show();

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

	public void OnClickImageView(View v) {

	}

	public void InitData() {
		String m_j = null;
		if (m_ReturnProgramView.movie != null) {
			aq.id(R.id.program_name).text(m_ReturnProgramView.movie.name);
			aq.id(R.id.imageView3)
					.image(m_ReturnProgramView.movie.poster, true, true);
			// m_j = m_ReturnProgramView.movie.stars;
			// m_j.replace(" ", "\n");
			aq.id(R.id.textView5).text(m_ReturnProgramView.movie.stars);
			aq.id(R.id.textView6).text(m_ReturnProgramView.movie.area);
			aq.id(R.id.textView7).text(m_ReturnProgramView.movie.directors);
			aq.id(R.id.textView8).text(m_ReturnProgramView.movie.publish_date);
			m_FavorityNum = Integer
					.parseInt(m_ReturnProgramView.movie.favority_num);
			aq.id(R.id.button2).text("收藏(" + m_FavorityNum + ")");
			m_SupportNum = Integer
					.parseInt(m_ReturnProgramView.movie.support_num);
			aq.id(R.id.button3).text("顶(" + m_SupportNum + ")");
			aq.id(R.id.textView11).text(
					"    " + m_ReturnProgramView.movie.summary);
			// aq.id(R.id.Layout_comment).text(m_ReturnProgramView.movie.comments.);

			if (m_ReturnProgramView.movie.episodes != null
					&& m_ReturnProgramView.movie.episodes[0].video_urls != null
					&& m_ReturnProgramView.movie.episodes[0].video_urls[0].url != null)
				PROD_URI = m_ReturnProgramView.movie.episodes[0].video_urls[0].url;
			for (int i = 0; i < m_ReturnProgramView.movie.episodes.length; i++) {

				if (m_ReturnProgramView.movie.episodes[i].down_urls != null
						&& m_ReturnProgramView.movie.episodes[i].down_urls[0].urls.length > 0
						&& m_ReturnProgramView.movie.episodes[i].down_urls[0].urls[0].url != null
						&& app.IfSupportFormat(m_ReturnProgramView.movie.episodes[i].down_urls[0].urls[0].url)) {

					PROD_SOURCE = m_ReturnProgramView.movie.episodes[i].down_urls[0].urls[0].url;
					break;
				}
			}
//			if (m_ReturnProgramView.movie.episodes != null
//					&& m_ReturnProgramView.movie.episodes[0].video_urls != null
//					&& m_ReturnProgramView.movie.episodes[0].video_urls[0].url != null)
//				PROD_URI = m_ReturnProgramView.movie.episodes[0].video_urls[0].url;
//
//			if (m_ReturnProgramView.movie.episodes[0].down_urls != null
//					&& m_ReturnProgramView.movie.episodes[0].down_urls[0].urls.length > 0
//					&& m_ReturnProgramView.movie.episodes[0].down_urls[0].urls[0].url != null
//					&& app.IfSupportFormat(m_ReturnProgramView.movie.episodes[0].down_urls[0].urls[0].url)) {
//
//				PROD_SOURCE = m_ReturnProgramView.movie.episodes[0].down_urls[0].urls[0].url;
//
//			}

			if (m_ReturnProgramView.topics != null) {
				for (int i = 0; i < m_ReturnProgramView.topics.length && i < 4; i++) {
					m_j = m_ReturnProgramView.topics[i].t_name;
					switch (i) {
					case 1:
						if (m_j != null) {
							aq.id(R.id.imageView_about).visible();
							aq.id(R.id.button5).text(m_j);
							aq.id(R.id.button5).visible();
						}
						break;
					case 2:
						if (m_j != null) {
							aq.id(R.id.button6).text(m_j);
							aq.id(R.id.button6).visible();
						}
					case 3:
						if (m_j != null) {
							aq.id(R.id.button7).text(m_j);
							aq.id(R.id.button7).visible();
						}
						break;
					case 4:
						if (m_j != null) {
							aq.id(R.id.button8).text(m_j);
							aq.id(R.id.button8).visible();
						}
						break;

					}

				}
			}

			if (m_ReturnProgramView.topics != null
					&& m_ReturnProgramView.topics.length >= 1) {
				ShowTopics();
			} else {
				aq.id(R.id.LinearLayoutXGYD).gone();
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
			app.MyToast(aq.getContext(), getResources().getString(R.string.networknotwork));
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

	public void OnClickMovieBangdan(View v) throws JSONException {
		int index = Integer.parseInt(v.getTag().toString());
		Intent intent = new Intent(this, Detail_BangDan.class);
		intent.putExtra("BangDan_id", m_ReturnProgramView.topics[index].t_id);
		intent.putExtra("BangDan_name",
				m_ReturnProgramView.topics[index].t_name);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			Log.e(TAG, "Call Detail_BangDan failed", ex);
		}
	}

	public void OnClickPlay(View v) throws JSONException {
		String m_str = MobclickAgent.getConfigParams(this, "playBtnSuppressed");
		if(MobclickAgent.getConfigParams(this, "playBtnSuppressed").trim().equalsIgnoreCase("1")){
			app.MyToast(this, "暂无播放链接!");
			return;
		}
		if (PROD_SOURCE != null && PROD_SOURCE.trim().length() > 0) {
			// save to local
			// prod_id|PROD_SOURCE |
			// Pro_url|Pro_name|Pro_name1|Pro_time|Pro_type
			// 1：电影，2：电视剧，3：综艺节目，4：视频
			String datainfo = prod_id + "|" + "PROD_SOURCE" + "|"
					+ URLEncoder.encode(PROD_SOURCE) + "|"
					+ m_ReturnProgramView.movie.name + "|" + "null" + "|"
					+ "null" + "|1";
			app.SavePlayData(prod_id, datainfo);
			CallVideoPlayActivity(PROD_SOURCE);

		} else if (PROD_URI != null && PROD_URI.trim().length() > 0) {
			// save to local
			// prod_id|PROD_URI | Pro_url|Pro_name|Pro_name1|Pro_time
			String datainfo = prod_id + "|" + "PROD_URI" + "|"
					+ URLEncoder.encode(PROD_URI) + "|"
					+ m_ReturnProgramView.movie.name + "|" + "null" + "|"
					+ "null" + "|1";
			app.SavePlayData(prod_id, datainfo);

			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri content_url = Uri.parse(PROD_URI);
			intent.setData(content_url);
			startActivity(intent);
		}
	}

	public void ShowTopics() {
		String m_j = null;
		int i = 0;
		if (m_ReturnProgramView.topics == null) {
			aq.id(R.id.LinearLayoutXGYD).gone();
			return;
		}
		for (i = 0; i < m_ReturnProgramView.topics.length && i < 4; i++) {
			m_j = "    " + m_ReturnProgramView.topics[i].t_name;
			switch (i) {
			case 0:
				if (m_j != null) {
					aq.id(R.id.imageView_about).visible();
					aq.id(R.id.button5).getButton().setTag(i + "");
					aq.id(R.id.button5).text(m_j);
					aq.id(R.id.button5).visible();
				}
				break;
			case 1:
				if (m_j != null) {
					aq.id(R.id.button6).getButton().setTag(i + "");
					aq.id(R.id.button6).text(m_j);
					aq.id(R.id.button6).visible();
				}
			case 2:
				if (m_j != null) {
					aq.id(R.id.button7).getButton().setTag(i + "");
					aq.id(R.id.button7).text(m_j);
					aq.id(R.id.button7).visible();
				}
				break;
			case 3:
				if (m_j != null) {
					aq.id(R.id.button8).getButton().setTag(i + "");
					aq.id(R.id.button8).text(m_j);
					aq.id(R.id.button8).visible();
				}
				break;

			}
		}

		if (i < 4) {
			i--;
			Button m_button;
			for (int j = i; j < 3; j++) {
				m_j = "button" + Integer.toString(j + 5); // from button5
				m_button = (Button) this.findViewById(getResources()
						.getIdentifier(m_j, "id", getPackageName()));
				m_button.setVisibility(View.GONE);
			}

			m_j = "button" + Integer.toString(i + 5);
			m_button = (Button) this.findViewById(getResources().getIdentifier(
					m_j, "id", getPackageName()));
			m_button.setVisibility(View.GONE);
			aq.id(R.id.button8).getButton().setTag(i + "");
			aq.id(R.id.button8).text(
					"    " + m_ReturnProgramView.topics[i].t_name);
			aq.id(R.id.button8).visible();

		}
	}

	public void ShowComments() {
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.Layout_comment);
		if (m_ReturnProgramView.comments != null) {
			for (int i = 0; i < m_ReturnProgramView.comments.length; i++) {
				RelativeLayout subLayout = new RelativeLayout(this);

				RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
						RelativeLayout.TRUE);

				TextView valueName = new TextView(this);
				//valueName.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
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
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
						RelativeLayout.TRUE);

				TextView valueTime = new TextView(this);
				valueTime.setText(m_ReturnProgramView.comments[i].create_date
						.replaceAll(" 00:00:00", ""));
				subLayout.addView(valueTime, params2);

				LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params3.topMargin = 10;
				
				linearLayout.addView(subLayout,params3);
				
				TextView valueContent = new TextView(this);
				valueContent.setText(m_ReturnProgramView.comments[i].content);
				linearLayout.addView(valueContent);

				if (i != m_ReturnProgramView.comments.length - 1) {
					LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params4.topMargin = 10;
					
					ImageView m_image = new ImageView(this);
					m_image.setBackgroundResource(R.drawable.tab1_divider);
					
					linearLayout.addView(m_image,params4);
				}
			}
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
