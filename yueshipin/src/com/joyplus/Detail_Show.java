package com.joyplus;

import java.io.IOException;
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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
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
import com.joyplus.Service.Return.ReturnProgramReviews;
import com.joyplus.Service.Return.ReturnProgramView;
import com.joyplus.Service.Return.ReturnProgramView.DOWN_URLS;
import com.joyplus.Video.VideoPlayerActivity;
import com.joyplus.cache.VideoCacheInfo;
import com.joyplus.cache.VideoCacheManager;
import com.joyplus.download.Dao;
import com.joyplus.download.DownloadInfo;
import com.joyplus.download.DownloadTask;
import com.joyplus.playrecord.PlayRecordInfo;
import com.joyplus.playrecord.PlayRecordManager;
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
	private int m_FavorityNum;
	private int m_SupportNum;

	private ReturnProgramReviews m_ReturnProgramReviews = null;
	private ScrollView mScrollView;
	private int isLastisNext = 2;
	private int mLastY = 0;

	public List<DownloadInfo> data;
	private Drawable download_focuse = null;
	List download_names = new ArrayList();
	private ArrayList download_indexs = new ArrayList();
	private int current_index = -1; // yy
	boolean pageShow = true;
	private CurrentPlayData mCurrentPlayData;
	private static String SHOW_DETAIL = "综艺详情";
	Context mContext;
	private String player_select;
	private PopupWindow popup_player_select = null;
	
	private PopupWindow popup_report = null;
	private PopupWindow popupReviewDetail = null;
	private String invalid_type = null;
	private String problemContext = null;
	CheckBox checkbox1;
	CheckBox checkbox2;
	CheckBox checkbox3;
	CheckBox checkbox4;
	CheckBox checkbox5;
	CheckBox checkbox6;
	CheckBox checkbox7;
	EditText problem_edit;

	VideoCacheInfo cacheInfo;
	VideoCacheInfo cacheInfoTemp;
	VideoCacheManager cacheManager;
	// 播放记录
	PlayRecordInfo playrecordinfo;
	PlayRecordManager playrecordmanager;
	long current_time = 0;

	private Bitmap bitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_show);
		app = (App) getApplication();
		aq = new AQuery(this);
		mContext = this;
		Intent intent = getIntent();
		prod_id = intent.getStringExtra("prod_id");
		prod_name = intent.getStringExtra("prod_name");

		if (prod_name != null)
			aq.id(R.id.program_name).text(prod_name);

		aq.id(R.id.scrollView1).gone();
		mScrollView = (ScrollView) findViewById(R.id.scrollView1);
		mScrollView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (mLastY == mScrollView.getScrollY()) {
						// TODO
						// if (mScrollView.getScrollY() != 0)
						// ShowMoreComments();
					} else {
						mLastY = mScrollView.getScrollY();
					}
				}
				return false;
			}
		});

		download_focuse = this.getResources().getDrawable(R.drawable.download2);

		cacheManager = new VideoCacheManager(Detail_Show.this);
		cacheInfo = new VideoCacheInfo();
		playrecordmanager = new PlayRecordManager(Detail_Show.this);
		playrecordinfo = new PlayRecordInfo();

		mCurrentPlayData = new CurrentPlayData();
		mCurrentPlayData.prod_id = prod_id;
		if (prod_id != null)
			CheckSaveData();
		player_select = app.GetServiceData("player_select");
		// 是否显示新手引导
		if (app.GetServiceData("new_guider_3") == null) {
			aq.id(R.id.new_guider_3).visible();
		}
		
	}

	public void OnClickNewGuider_3(View v) {
		aq.id(R.id.new_guider_3).gone();
		app.SaveServiceData("new_guider_3", "new_guider_3");
	}

	public void OnClickTab1TopLeft(View v) {
		finish();
	}

	public void OnClickTab1TopRight(View v) {
		Intent intent = new Intent(Detail_Show.this, MainTopRightDialog.class);
		intent.putExtra("prod_name", aq.id(R.id.program_name).getText()
				.toString());
		ImageView imageView3 = (ImageView) findViewById(R.id.imageView3);

		Drawable drawable = imageView3.getDrawable();
		if (drawable == null) {
			drawable = getResources().getDrawable(R.drawable.detail_picture_bg);
		}
		bitmap = drawableToBitmap(drawable);
		intent.putExtra("bitmapImage", bitmap);
		String video_prod_id = "1007955";
		if (prod_id != null) {
			video_prod_id = prod_id;
		}
		intent.putExtra("prod_id", video_prod_id);
		startActivity(intent);
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		Drawable clone = drawable.getConstantState().newDrawable();
		// 取 drawable 的长宽
		int w = clone.getIntrinsicWidth();
		int h = clone.getIntrinsicHeight();

		// 取 drawable 的颜色格式
		Bitmap.Config config = clone.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		// 建立对应 bitmap
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		// 建立对应 bitmap 的画布
		Canvas canvas = new Canvas(bitmap);
		clone.setBounds(0, 0, w, h);
		// 把 drawable 内容画到画布中
		clone.draw(canvas);
		return bitmap;
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
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
		System.gc();
		if (aq != null)
			aq.dismiss();
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onEventBegin(mContext, SHOW_DETAIL);
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onEventEnd(mContext, SHOW_DETAIL);
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
			if (m_ReturnProgramView.show.poster != null) {
				aq.id(R.id.imageView3).image(
						m_ReturnProgramView.show.poster.trim(), true, true);
			}
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
					m_button.setText(" "
							+ m_ReturnProgramView.show.episodes[i].name);
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

			if(cacheManager!=null&&cacheInfoTemp!=null)
			{
				
				String temp = cacheInfoTemp.getComments();
				if(temp!=null&&temp.toString().length()>10)
				{
					ObjectMapper mapper = new ObjectMapper();
					m_ReturnProgramReviews = null;
					try {
						m_ReturnProgramReviews = mapper.readValue(temp,
								ReturnProgramReviews.class);
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
					// 创建数据源对象
					if(m_ReturnProgramReviews==null)
					{
						GetReviews();
					}
					ShowComments();
				}
				else
				{
					GetReviews();
				}
			}
			else
			{
				GetReviews();
			}

		} else {
			GetServiceData();
		}

	}

	public void OnClickImageView(View v) {

	}

	// 初始化list数据函数
	public void InitListData(String url, JSONObject json, AjaxStatus status) {
//		android.util.Log.i("yanyuchuang",status.getCode()+"");
		// || json == null|| !json.has("show")
		if (status.getCode() == AjaxStatus.NETWORK_ERROR) {
			aq.id(R.id.ProgressText).gone();
			app.MyToast(aq.getContext(),
					getResources().getString(R.string.networknotwork));
			if (cacheInfoTemp == null) {
				aq.id(R.id.none_net).visible();
			}
			return;
		}
		if(json == null|| !json.has("show"))
		{
			aq.id(R.id.ProgressText).gone();
			app.MyToast(aq.getContext(),
					getResources().getString(R.string.networkispoor));
			if (cacheInfoTemp == null) {
				aq.id(R.id.none_net).visible();
			}
			return;
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			m_ReturnProgramView = mapper.readValue(json.toString(),
					ReturnProgramView.class);
			if (m_ReturnProgramView != null && prod_id != null) {
				if (cacheInfoTemp != null) {
					cacheInfoTemp.setProd_value(json.toString());
					cacheManager.saveVideoCache(cacheInfoTemp);
				} else {
					cacheInfo.setProd_id(prod_id);
					cacheInfo.setProd_type("3");
					cacheInfo.setProd_value(json.toString());
					cacheInfo.setProd_subname("");
					cacheInfo.setLast_playtime("");
					cacheInfo.setCreate_date("");
					cacheManager.saveVideoCache(cacheInfo);
				}
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
		// SaveData = app.GetServiceData(prod_id);
		cacheInfoTemp = cacheManager.getVideoCache(prod_id);
		if (cacheInfoTemp != null) {
			SaveData = cacheInfoTemp.getProd_value();
		}
		if (SaveData == null) {
			GetServiceData();
		} else {
			try {
				m_ReturnProgramView = mapper.readValue(SaveData,
						ReturnProgramView.class);
				// 创建数据源对象
				// 创建数据源对象
				InitData();
				aq.id(R.id.ProgressText).gone();
				aq.id(R.id.scrollView1).visible();
				GetServiceData();
				// new Handler().postDelayed(new Runnable() {
				// @Override
				// public void run() {
				// // execute the task
				// GetServiceData();
				// }
				// }, 2000);

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
		cb.timeout(60*1000);
		cb.SetHeader(app.getHeaders());
		if (cacheInfoTemp == null) {
			aq.id(R.id.ProgressText).visible();
			aq.progress(R.id.progress).ajax(cb);
		} else {
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
				} else {
					m_SupportNum++;
					aq.id(R.id.button3).text(
							"顶(" + Integer.toString(m_SupportNum) + ")");
					app.MyToast(this, "顶成功!");
				}
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
		popupReportProblem();
	}

	public void OnClickPlay(View v) {
		if (MobclickAgent.getConfigParams(this, "playBtnSuppressed").trim()
				.equalsIgnoreCase("1")) {
			app.MyToast(this, "暂无播放链接!");
			return;
		}

		if(player_select==null && m_ReturnProgramView.show.episodes.length <= 200)
		{
			{
				LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				final ViewGroup menuView = (ViewGroup) mLayoutInflater
						.inflate(R.layout.player_select, null, true);
				popup_player_select = new PopupWindow(menuView,
						LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT, true);
				Button default_btn = (Button) menuView
						.findViewById(R.id.neizhibtn);
				default_btn
						.setOnClickListener(new Button.OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								player_select = "default";
								app.SaveServiceData("player_select",
										"default");
								popup_player_select.dismiss();
								StartIntentToPlayer();
							}
						});
				Button third_btn = (Button) menuView
						.findViewById(R.id.disanfangbtn);
				third_btn.setOnClickListener(new Button.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						player_select = "third";
						app.SaveServiceData("player_select","third");
						popup_player_select.dismiss();
						StartIntentToPlayer();
						}
				});
				popup_player_select
						.setBackgroundDrawable(new BitmapDrawable());
				popup_player_select
						.showAtLocation(Detail_Show.this
								.findViewById(R.id.parent),
								Gravity.CENTER | Gravity.CENTER, 0, 40);
				popup_player_select.update();
			}
		}
		else
		{
			StartIntentToPlayer();
		}
	}
	
	public void StartIntentToPlayer()
	{
		app.checkUserSelect(Detail_Show.this);
		if (app.use2G3G) {
			// 综艺type为3 ，sbuname 为当前集数
			StatisticsUtils.StatisticsClicksShow(aq, app, prod_id, prod_name,
					m_ReturnProgramView.show.episodes[0].name, 3);
			if (PROD_URI != null && PROD_URI.trim().length() > 0) {

				SaveToServer(2, PROD_URI, 1);

				Intent intent = new Intent(this, Webview_Play.class);
				Bundle bundle = new Bundle();
				bundle.putString("PROD_URI", PROD_URI);
				bundle.putString("NAME", m_ReturnProgramView.show.name);
				bundle.putString("prod_subname",
						m_ReturnProgramView.show.episodes[0].name);

				if (PROD_SOURCE != null && PROD_SOURCE.trim().length() > 0) {
					bundle.putString("prod_id", prod_id);
					bundle.putInt("CurrentIndex", 0);
					bundle.putInt("CurrentCategory", 2);
					bundle.putString("PROD_SOURCE", PROD_SOURCE);
					bundle.putString("prod_type", "3");
					bundle.putLong("current_time", current_time);
				}
				intent.putExtras(bundle);
				if("third".equalsIgnoreCase(player_select)|| m_ReturnProgramView.show.episodes.length > 200)
				{
					Intent it = new Intent(Intent.ACTION_VIEW);
					Uri uri = Uri.parse(PROD_SOURCE);
					it.setDataAndType(uri, "video/*");
					startActivity(it);
				}
				else
				{
					startActivity(intent);
				}
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
				m_button.setText(" "
						+ m_ReturnProgramView.show.episodes[i].name);
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
				m_button.setText(" "
						+ m_ReturnProgramView.show.episodes[i].name);
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
		final int index = Integer.parseInt(v.getTag().toString());
		current_index = index;
		if (MobclickAgent.getConfigParams(this, "playBtnSuppressed").trim()
				.equalsIgnoreCase("1")) {
			app.MyToast(this, "暂无播放链接!");
			return;
		}
		if (m_ReturnProgramView.show.episodes != null
				&& m_ReturnProgramView.show.episodes[index].video_urls != null
				&& m_ReturnProgramView.show.episodes[index].video_urls[0].url != null)
			PROD_URI = m_ReturnProgramView.show.episodes[index].video_urls[0].url;
		PROD_SOURCE = null;
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
		if(player_select==null && m_ReturnProgramView.show.episodes.length <= 200)
		{
			{
				LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				final ViewGroup menuView = (ViewGroup) mLayoutInflater
						.inflate(R.layout.player_select, null, true);
				popup_player_select = new PopupWindow(menuView,
						LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT, true);
				Button default_btn = (Button) menuView
						.findViewById(R.id.neizhibtn);
				default_btn
						.setOnClickListener(new Button.OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								player_select = "default";
								app.SaveServiceData("player_select",
										"default");
								popup_player_select.dismiss();
								StartIntentToPlayerShow(index);
							}
						});
				Button third_btn = (Button) menuView
						.findViewById(R.id.disanfangbtn);
				third_btn.setOnClickListener(new Button.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						player_select = "third";
						app.SaveServiceData("player_select","third");
						popup_player_select.dismiss();
						StartIntentToPlayerShow(index);
						}
				});
				popup_player_select
						.setBackgroundDrawable(new BitmapDrawable());
				popup_player_select
						.showAtLocation(Detail_Show.this
								.findViewById(R.id.parent),
								Gravity.CENTER | Gravity.CENTER, 0, 40);
				popup_player_select.update();
			}
		}
		else
		{
			StartIntentToPlayerShow(index);
		}
	}
	
	public void StartIntentToPlayerShow(int index)
	{
		app.checkUserSelect(Detail_Show.this);
		if (app.use2G3G) {

			// 综艺type为3 ，sbuname 为当前集数
			StatisticsUtils.StatisticsClicksShow(aq, app, prod_id, prod_name,
					m_ReturnProgramView.show.episodes[current_index].name, 3);

			playrecordinfo = playrecordmanager.getPlayRecord(prod_id,
					m_ReturnProgramView.show.episodes[current_index].name);
			current_time = 0;
			if (playrecordinfo != null
					&& playrecordinfo.getLast_playtime() != null
					&& playrecordinfo.getLast_playtime().length() > 0) {
				current_time = Long
						.parseLong(playrecordinfo.getLast_playtime());
			}

			if (PROD_URI != null && PROD_URI.trim().length() > 0) {

				SaveToServer(2, PROD_URI, index + 1);

				Intent intent = new Intent(this, Webview_Play.class);
				Bundle bundle = new Bundle();
				bundle.putString("PROD_URI", PROD_URI);
				bundle.putString("NAME", m_ReturnProgramView.show.name);
				bundle.putString("prod_subname",
						m_ReturnProgramView.show.episodes[current_index].name);

				if (PROD_SOURCE != null && PROD_SOURCE.trim().length() > 0) {

					bundle.putString("prod_id", prod_id);
					bundle.putInt("CurrentIndex", current_index);
					bundle.putInt("CurrentCategory", 2);
					bundle.putString("PROD_SOURCE", PROD_SOURCE);
					bundle.putString("prod_type", "3");
					bundle.putLong("current_time", current_time);
				}
				intent.putExtras(bundle);
				if("third".equalsIgnoreCase(player_select)|| m_ReturnProgramView.show.episodes.length > 200)
				{
					Intent it = new Intent(Intent.ACTION_VIEW);
					Uri uri = Uri.parse(PROD_SOURCE);
					it.setDataAndType(uri, "video/*");
					startActivity(it);
				}
				else
				{
					startActivity(intent);
				}
			}
		}
	}
	
	public void videoSourceSort(int source_index) {
		if (m_ReturnProgramView.show.episodes[source_index].down_urls != null) {
			for (int j = 0; j < m_ReturnProgramView.show.episodes[source_index].down_urls.length; j++) {
				if (m_ReturnProgramView.show.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("letv")) {
					m_ReturnProgramView.show.episodes[source_index].down_urls[j].index = 0;
				} else if (m_ReturnProgramView.show.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("fengxing")) {
					m_ReturnProgramView.show.episodes[source_index].down_urls[j].index = 1;
				} else if (m_ReturnProgramView.show.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("qiyi")) {
					m_ReturnProgramView.show.episodes[source_index].down_urls[j].index = 2;
				} else if (m_ReturnProgramView.show.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("youku")) {
					m_ReturnProgramView.show.episodes[source_index].down_urls[j].index = 3;
				} else if (m_ReturnProgramView.show.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("sinahd")) {
					m_ReturnProgramView.show.episodes[source_index].down_urls[j].index = 4;
				} else if (m_ReturnProgramView.show.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("sohu")) {
					m_ReturnProgramView.show.episodes[source_index].down_urls[j].index = 5;
				} else if (m_ReturnProgramView.show.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("56")) {
					m_ReturnProgramView.show.episodes[source_index].down_urls[j].index = 6;
				} else if (m_ReturnProgramView.show.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("qq")) {
					m_ReturnProgramView.show.episodes[source_index].down_urls[j].index = 7;
				} else if (m_ReturnProgramView.show.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("pptv")) {
					m_ReturnProgramView.show.episodes[source_index].down_urls[j].index = 8;
				} else if (m_ReturnProgramView.show.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("m1905")) {
					m_ReturnProgramView.show.episodes[source_index].down_urls[j].index = 9;
				}
			}
			if (m_ReturnProgramView.tv.episodes[source_index].down_urls.length > 1) {
				Arrays.sort(
						m_ReturnProgramView.show.episodes[source_index].down_urls,
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

	// public void CallVideoPlayActivity(String m_uri, String title) {
	// app.IfSupportFormat(m_uri);
	// mCurrentPlayData.CurrentCategory = 2;
	// mCurrentPlayData.CurrentIndex = current_index;
	// app.setCurrentPlayData(mCurrentPlayData);
	//
	// Intent intent = new Intent(this, VideoPlayerActivity.class);
	// Bundle bundle = new Bundle();
	// bundle.putString("path", m_uri);
	// bundle.putString("title", title);
	// bundle.putString("prod_id", prod_id);
	// bundle.putString("prod_subname",
	// m_ReturnProgramView.show.episodes[current_index].name);
	// bundle.putString("prod_type", "3");
	// bundle.putLong("current_time", current_time);
	// intent.putExtras(bundle);
	//
	// try {
	// startActivity(intent);
	// } catch (ActivityNotFoundException ex) {
	// Log.e(TAG, "VideoPlayerActivity fail", ex);
	// }
	// }

	public void ShowComments() {
		if (m_ReturnProgramReviews == null) {
			aq.id(R.id.imageView_comment).gone();
		}
		LinearLayout review1 = (LinearLayout) findViewById(R.id.review1);
		LinearLayout review2 = (LinearLayout) findViewById(R.id.review2);
		LinearLayout review3 = (LinearLayout) findViewById(R.id.review3);
		if (m_ReturnProgramReviews != null
				&& m_ReturnProgramReviews.reviews != null) {
			if (m_ReturnProgramReviews.reviews.length == 1) {
				review1.setVisibility(View.VISIBLE);
			} else if (m_ReturnProgramReviews.reviews.length == 2) {
				review1.setVisibility(View.VISIBLE);
				review2.setVisibility(View.VISIBLE);
			} else if (m_ReturnProgramReviews.reviews.length == 3) {
				review1.setVisibility(View.VISIBLE);
				review2.setVisibility(View.VISIBLE);
				review3.setVisibility(View.VISIBLE);
			}
			if (m_ReturnProgramReviews.reviews.length > 0
					&& m_ReturnProgramView.show.douban_id != null) {
				aq.id(R.id.moreReviews).visible();
			}
		}
		TextView review1Title = (TextView) findViewById(R.id.review1Title);
		final TextView review1Content = (TextView) findViewById(R.id.review1Content);
		TextView review2Title = (TextView) findViewById(R.id.review2Title);
		final TextView review2Content = (TextView) findViewById(R.id.review2Content);
		TextView review3Title = (TextView) findViewById(R.id.review3Title);
		final TextView review3Content = (TextView) findViewById(R.id.review3Content);
		if (m_ReturnProgramReviews != null
				&& m_ReturnProgramReviews.reviews != null) {
			for (int i = 0; i < m_ReturnProgramReviews.reviews.length; i++) {
				if (i == 0) {
					review1Title
							.setText(m_ReturnProgramReviews.reviews[0].title);
					review1Content
							.setText(m_ReturnProgramReviews.reviews[0].comments);
					ViewTreeObserver vto = review1Content.getViewTreeObserver();
					vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
						@Override
						public void onGlobalLayout() {
							// TODO Auto-generated method stub
							ViewTreeObserver obs = review1Content
									.getViewTreeObserver();
							if (review1Content.getLineCount() > 5) {
								int lineEndIndex = review1Content.getLayout()
										.getLineEnd(4);
								String text = review1Content.getText()
										.subSequence(0, lineEndIndex - 3)
										+ "...";
								review1Content.setText(text);
							}
						}
					});
					review1.setTag(i);
				}
				if (i == 1) {
					review2Title
							.setText(m_ReturnProgramReviews.reviews[1].title);
					review2Content
							.setText(m_ReturnProgramReviews.reviews[1].comments);
					ViewTreeObserver vto = review2Content.getViewTreeObserver();
					vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
						@Override
						public void onGlobalLayout() {
							// TODO Auto-generated method stub
							ViewTreeObserver obs = review2Content
									.getViewTreeObserver();
							if (review2Content.getLineCount() > 5) {
								int lineEndIndex = review2Content.getLayout()
										.getLineEnd(4);
								String text = review2Content.getText()
										.subSequence(0, lineEndIndex - 3)
										+ "...";
								review2Content.setText(text);
							}
						}
					});
					review2.setTag(i);
				}
				if (i == 2) {
					review3Title
							.setText(m_ReturnProgramReviews.reviews[2].title);
					review3Content
							.setText(m_ReturnProgramReviews.reviews[2].comments);
					ViewTreeObserver vto = review3Content.getViewTreeObserver();
					vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
						@Override
						public void onGlobalLayout() {
							// TODO Auto-generated method stub
							if (review3Content.getLineCount() > 5) {
								int lineEndIndex = review3Content.getLayout()
										.getLineEnd(4);
								String text = review3Content.getText()
										.subSequence(0, lineEndIndex - 3)
										+ "...";
								review3Content.setText(text);
							}
						}
					});
					review3.setTag(i);
				}
			}
		}
	}

	public void OnClickReviewComments(View v) {
		if(popupReviewDetail!=null)
		{
			popupReviewDetail.dismiss();
		}
		LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		final ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
				R.layout.reviews, null, true);
		TextView title = (TextView) menuView.findViewById(R.id.title);
		TextView content = (TextView) menuView.findViewById(R.id.content);
		title.setText(m_ReturnProgramReviews.reviews[Integer.parseInt(v
				.getTag().toString())].title);
		content.setText(m_ReturnProgramReviews.reviews[Integer.parseInt(v
				.getTag().toString())].comments);
		popupReviewDetail = new PopupWindow(menuView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		popupReviewDetail.setBackgroundDrawable(new BitmapDrawable());
		popupReviewDetail.setAnimationStyle(R.style.PopupAnimation);
		popupReviewDetail.showAtLocation(findViewById(R.id.parent),
				Gravity.CENTER | Gravity.CENTER, 0, 40);
		popupReviewDetail.update();
	}

	public void GetReviews() {
		/*
		 * app_key required string 申请应用时分配的AppKey。 prod_id required string 节目id
		 * page_num = 需要请求的页码（可选），默认为1 page_size = 每一页包含的记录数（可选），默认为10
		 */
		isLastisNext = 1;
		String url = Constant.BASE_URL + "program/reviews" + "?prod_id="
				+ prod_id + "&page_num=1" + "&page_size=3";

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class)
				.weakHandler(this, "CallCommentsResult");

		cb.SetHeader(app.getHeaders());

		// aq.id(R.id.ProgressText).visible();
		aq.ajax(cb);
	}

	public void CallCommentsResult(String url, JSONObject json,
			AjaxStatus status) {
		if (json == null) {
			return;
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (isLastisNext > 1)
				m_ReturnProgramReviews = null;
			m_ReturnProgramReviews = mapper.readValue(json.toString(),
					ReturnProgramReviews.class);
			if(json!=null&&cacheManager!=null)
			{
				cacheManager.saveVideoCacheComments(json.toString(), prod_id);
			}
			// 创建数据源对象
			ShowComments();
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

	public void OnClickMoreReviews(View v) {
		String url = "http://movie.douban.com/subject/"
				+ m_ReturnProgramView.show.douban_id + "/reviews";
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse(url);
		intent.setData(content_url);
		startActivity(intent);
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
		bundle.putString("prod_subname",
				m_ReturnProgramView.show.episodes[current_index].name);
		bundle.putString("prod_type", "3");
		bundle.putLong("current_time", current_time);
		intent.putExtras(bundle);

		try {
			startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			Log.e(TAG, "VideoPlayerActivity fail", ex);
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

	// private void GetVideoSource(final int episodeNum, String url) {
	//
	// aq.progress(R.id.progress).ajax(url, InputStream.class,
	// new AjaxCallback<InputStream>() {
	//
	// public void callback(String url, InputStream is,
	// AjaxStatus status) {
	// String urlsave = Constant.BASE_URL + "program/play";
	// if (is != null) {
	//
	// Map<String, Object> params = new HashMap<String, Object>();
	// params.put("app_key", Constant.APPKEY);// required
	// // string
	// // 申请应用时分配的AppKey。
	// params.put("prod_id", m_ReturnProgramView.show.id);// required
	// // string
	// // 视频id
	// params.put("prod_name",
	// m_ReturnProgramView.show.name);// required
	// // string
	// // 视频名字
	// params.put("prod_subname",
	// m_ReturnProgramView.show.episodes.length);// required
	// // string
	// // 视频的集数
	// params.put("prod_type", 3);// required int 视频类别
	// // 1：电影，2：电视剧，3：综艺，4：视频
	// params.put("playback_time", 0);// _time required int
	// // 上次播放时间，单位：秒
	// params.put("duration", 0);// required int 视频时长， 单位：秒
	// params.put("play_type", "1");// required string
	// // 播放的类别 1: 视频地址播放
	// // 2:webview播放
	// params.put("video_url", url);// required
	// // string
	// // 视频url
	//
	// AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
	// cb.SetHeader(app.getHeaders());
	//
	// cb.params(params).url(urlsave);
	// aq.ajax(cb);
	//
	// CallVideoPlayActivity(url,
	// m_ReturnProgramView.show.name);
	// } else {
	// if (m_ReturnProgramView.show.episodes[episodeNum].down_urls != null) {
	// for (int k = 0; k <
	// m_ReturnProgramView.show.episodes[episodeNum].down_urls[0].urls.length;
	// k++) {
	// ReturnProgramView.DOWN_URLS.URLS urls =
	// m_ReturnProgramView.show.episodes[episodeNum].down_urls[0].urls[k];
	// if (urls != null) {
	// if (urls.url != null) {
	// if (urls.type.trim()
	// .equalsIgnoreCase("mp4"))
	// PROD_SOURCE = urls.url.trim();
	// else if (urls.type.trim()
	// .equalsIgnoreCase("flv"))
	// PROD_SOURCE = urls.url.trim();
	// else if (urls.type.trim()
	// .equalsIgnoreCase("hd2"))
	// PROD_SOURCE = urls.url.trim();
	// else if (urls.type.trim()
	// .equalsIgnoreCase("3gp"))
	// PROD_SOURCE = urls.url.trim();
	// }
	// if (PROD_SOURCE != null) {
	// GetVideoSource(episodeNum,
	// PROD_SOURCE);
	// }
	// }
	// }
	// }
	// }
	// }
	//
	// });
	// }

	public void OnClickCacheDown(View v) {
		GotoDownloadPage();
		pageShow = false;
	}

	private void GotoDownloadPage() {
		// TODO Auto-generated method stub
		setContentView(R.layout.download_show);
		download_focuse = this.getResources().getDrawable(
				R.drawable.download_show2);
		
		LinearLayout linearbtn = (LinearLayout) findViewById(R.id.btnReturnDetail_Show);

		linearbtn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setContentView(R.layout.detail_show);
				// GetServiceData();
				if (prod_id != null)
					CheckSaveData();
			}
		});
		aq.id(R.id.textView2).text(m_ReturnProgramView.show.name);
		if (download_names.size() == 0)// 如果当前的download_names不为空说明不是第一次进入
		{
			for (int i = 0; i < m_ReturnProgramView.show.episodes.length; i++) {
				download_names.add(m_ReturnProgramView.show.episodes[i].name);
			}
		}
		// 获取当前综艺有多少集在数据库里,根据电视剧的my_index显示不一样的下载按钮
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
								if (DOWNLOAD_SOURCE == null
										&& urls.file != null
										&& app.IfSupportFormat(urls.url)
										&& urls.file.trim().equalsIgnoreCase(
												"mp4"))
									DOWNLOAD_SOURCE = urls.url.trim();
								if (DOWNLOAD_SOURCE != null)
									break;
							}
						}
					}
					app.checkUserSelect(Detail_Show.this);
					if (app.use2G3G) {
						if (DOWNLOAD_SOURCE != null) {
							String urlstr = DOWNLOAD_SOURCE;
							download_index = (position + 1) + "_show";
							String localfile = Constant.PATH_VIDEO + prod_id
									+ "_" + download_index + ".mp4";
							String my_name = m_ReturnProgramView.show.episodes[position].name;
							String download_state = "wait";
							DownloadTask downloadTask = new DownloadTask(arg1,
									Detail_Show.this, Detail_Show.this,
									prod_id, download_index, urlstr, localfile);
							downloadTask.execute(prod_id, download_index,
									urlstr, m_ReturnProgramView.show.poster,
									my_name, download_state);
							Toast.makeText(Detail_Show.this, "视频已加入下载队列",
									Toast.LENGTH_SHORT).show();
							if (download_indexs.contains(position)) {

							} else {
								download_indexs.add(position);
							}
							// 获取当前综艺有多少集在数据库里,根据电视剧的my_index显示不一样的下载按钮
							data = Dao.getInstance(Detail_Show.this)
									.getInfosOfProd_id(prod_id);
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
			textview.setText("  " + (CharSequence) download_names.get(position));// 加两个空格是为了让字体显示时不至于太靠左边
			for (int i = 0; i < data.size(); i++) {
				if (data.get(i)
						.getMy_name()
						.equalsIgnoreCase((String) download_names.get(position))) {
					textview.setTextColor(Color.WHITE);
					textview.setBackgroundDrawable(download_focuse);// 设置为已缓存
					textview.setText("  "
							+ (CharSequence) download_names.get(position));// 加两个空格是为了让字体显示时不至于太靠左边
				}
			}
			if (download_indexs.contains(position)) {
				textview.setTextColor(Color.WHITE);
				textview.setBackgroundDrawable(download_focuse);// 设置为已缓存
				textview.setText("  "
						+ (CharSequence) download_names.get(position));// 加两个空格是为了让字体显示时不至于太靠左边
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
			if (downloadStr == null) {
				textview.setTextColor(Color.rgb(204, 204, 204));// 设置为不可用
			}
			// if(m_ReturnProgramView.show.episodes[position].down_urls == null)
			// {
			// textview.setTextColor(Color.rgb(204, 204, 204));//设置为不可用
			// }
			return convertView;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (pageShow) {
				finish();
				return super.onKeyDown(keyCode, event);
			} else {
				pageShow = true;
				setContentView(R.layout.detail_show);
				// 调用时有问题
				if (prod_id != null)
					CheckSaveData();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void popupReportProblem() {
		LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		final ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
				R.layout.report_problem, null, true);
		checkbox1 = (CheckBox) menuView.findViewById(R.id.checkbox1);
		checkbox2 = (CheckBox) menuView.findViewById(R.id.checkbox2);
		checkbox3 = (CheckBox) menuView.findViewById(R.id.checkbox3);
		checkbox4 = (CheckBox) menuView.findViewById(R.id.checkbox4);
		checkbox5 = (CheckBox) menuView.findViewById(R.id.checkbox5);
		checkbox6 = (CheckBox) menuView.findViewById(R.id.checkbox6);
		checkbox7 = (CheckBox) menuView.findViewById(R.id.checkbox7);
		problem_edit = (EditText) menuView.findViewById(R.id.problem_edit);
		problemContext = problem_edit.getText().toString();
		popup_report = new PopupWindow(menuView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		popup_report.setBackgroundDrawable(new BitmapDrawable());
		popup_report.setAnimationStyle(R.style.PopupAnimation);
		popup_report.showAtLocation(findViewById(R.id.parent), Gravity.CENTER
				| Gravity.CENTER, 40, 40);
		popup_report.update();
	}

	public void OnClickCloseReprot(View v) {
		popup_report.dismiss();
	}

	public void OnClickSubmitProblem(View v) {
		initInvalid_type();
		if (invalid_type == null) {
			problemContext = problem_edit.getText().toString();
			if (problemContext == null || problemContext.length() < 1) {
				Toast.makeText(Detail_Show.this, "亲，必须选择一个理由啊！",
						Toast.LENGTH_LONG).show();
				return;
			}
		}
		String url = Constant.BASE_URL + "program/invalid";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("prod_id", prod_id);
		if (problemContext == null || problemContext.length() < 1) {
			params.put("invalid_type", invalid_type);
		} else {
			params.put("invalid_type", 8);
			params.put("memo", problemContext);
		}

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.SetHeader(app.getHeaders());

		cb.params(params).url(url).type(JSONObject.class)
				.weakHandler(this, "CallServiceResultReportProblem");
		aq.ajax(cb);
		Toast.makeText(Detail_Show.this, "您反馈的问题已提交，我们会尽快处理，感谢您的支持！",
				Toast.LENGTH_LONG).show();
		popup_report.dismiss();
	}

	public void initInvalid_type() {
		if (checkbox1.isChecked()) {
			if (invalid_type == null) {
				invalid_type = "1";
			} else {
				invalid_type = invalid_type + "," + "1";
			}
		}
		if (checkbox2.isChecked()) {
			if (invalid_type == null) {
				invalid_type = "2";
			} else {
				invalid_type = invalid_type + "," + "2";
			}
		}
		if (checkbox3.isChecked()) {
			if (invalid_type == null) {
				invalid_type = "3";
			} else {
				invalid_type = invalid_type + "," + "3";
			}
		}
		if (checkbox4.isChecked()) {
			if (invalid_type == null) {
				invalid_type = "4";
			} else {
				invalid_type = invalid_type + "," + "4";
			}
		}
		if (checkbox5.isChecked()) {
			if (invalid_type == null) {
				invalid_type = "5";
			} else {
				invalid_type = invalid_type + "," + "5";
			}
		}
		if (checkbox6.isChecked()) {
			if (invalid_type == null) {
				invalid_type = "6";
			} else {
				invalid_type = invalid_type + "," + "6";
			}
		}
		if (checkbox7.isChecked()) {
			if (invalid_type == null) {
				invalid_type = "7";
			} else {
				invalid_type = invalid_type + "," + "7";
			}
		}
	}
}
