package com.joyplus;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import com.joyplus.widget.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyplus.Adapters.CurrentPlayData;
import com.joyplus.Adapters.GalleryAdapter;

import com.joyplus.R.color;
import com.joyplus.Service.Return.ReturnProgramComments;
import com.joyplus.Service.Return.ReturnProgramReviews;
import com.joyplus.Service.Return.ReturnProgramView;
import com.joyplus.Service.Return.ReturnProgramView.DOWN_URLS;
import com.joyplus.Service.Return.ReturnProgramView.EPISODES;
import com.joyplus.Video.VideoPlayerActivity;
import com.joyplus.cache.VideoCacheInfo;
import com.joyplus.cache.VideoCacheManager;
import com.joyplus.download.Dao;
import com.joyplus.download.DownloadInfo;
import com.joyplus.download.DownloadTask;
import com.joyplus.playrecord.PlayRecordInfo;
import com.joyplus.playrecord.PlayRecordManager;
import com.parse.ParseInstallation;
import com.parse.PushService;
import com.umeng.analytics.MobclickAgent;

public class Detail_TV extends Activity {
	private AQuery aq;
	private String TAG = "Detail_TV";
	private App app;
	private ReturnProgramView m_ReturnProgramView = null;

	private String prod_id = null;
	private String prod_name = null;
	private String prod_type = null;
	private String PROD_SOURCE = null;
	public String DOWNLOAD_SOURCE = null;
	private String PROD_URI = null;
	private String tv_url = null;
	private String tv_source = null;
	private int current_download_pagenum = 0;
	private int page_num = 0;
	private int m_FavorityNum;
	private int m_SupportNum;
	public List<DownloadInfo> data;

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
	
	private Gallery gallery;
	//视频源
	private ArrayList<Integer> sourceImage;
	private ArrayList<String> sourceText;

	private ReturnProgramReviews m_ReturnProgramReviews = null;
	private ScrollView mScrollView;
	private int isLastisNext = 2;
	private int mLastY = 0;
	// 标示当前有多少个按钮被点击了
	private HashSet<Integer> download_indexs = new HashSet<Integer>();

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

	private CurrentPlayData mCurrentPlayData;
	private static String TV_DETAIL = "电视剧详情";
	Context mContext;
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
		setContentView(R.layout.detail_tv);
		app = (App) getApplication();
		aq = new AQuery(this);
		mContext = this;
		Intent intent = getIntent();
		prod_id = intent.getStringExtra("prod_id");
		prod_name = intent.getStringExtra("prod_name");
		if (intent.getStringExtra("prod_type") != null) {
			prod_type = intent.getStringExtra("prod_type");
		} else {
			prod_type = "2";
		}
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
						// if (mScrollView.getScrollY() != 0)
						// ShowMoreComments();
					} else {
						mLastY = mScrollView.getScrollY();
					}
				}
				return false;
			}
		});

		cacheManager = new VideoCacheManager(Detail_TV.this);
		cacheInfo = new VideoCacheInfo();
		playrecordmanager = new PlayRecordManager(Detail_TV.this);
		playrecordinfo = new PlayRecordInfo();

		aq.id(R.id.textView9).gone();
		aq.id(R.id.textView13).gone();
		aq.id(R.id.scrollView1).gone();
		
		gallery=(Gallery)findViewById(R.id.gallery);
		
		mCurrentPlayData = new CurrentPlayData();
		mCurrentPlayData.prod_id = prod_id;
		InitTVButtom();
		if (app.GetServiceData("new_guider_3") == null) {
			aq.id(R.id.new_guider_3).visible();
		}
		player_select = app.GetServiceData("player_select");
		
	}
	
	/*
	 * 
	 */
	public void showSourceView()
	{
		gallery.setAdapter(new GalleryAdapter(this,sourceImage,sourceText));
        gallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				app.sourceUrl = sourceText.get(position);
//				String temp = selectUrls(sourceText.get(position));
				Toast.makeText(Detail_TV.this, "", Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void OnClickNewGuider_3(View v) {
		aq.id(R.id.new_guider_3).gone();
		app.SaveServiceData("new_guider_3", "new_guider_3");
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
		Intent intent = new Intent(Detail_TV.this, MainTopRightDialog.class);
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
		MobclickAgent.onEventBegin(mContext, TV_DETAIL);
		MobclickAgent.onResume(this);

		if (prod_id != null) {
			// ReadSettingData
			SharedPreferences myPreference = this.getSharedPreferences(
					MY_SETTING, Context.MODE_PRIVATE);
			if (myPreference != null) {
				String temp = null;
				if (prod_id != null) {
					temp = myPreference.getString(prod_id, "");
				}
				if (temp != "") // myPreference.getString's return value is
								// "",not
								// null
				{
					current_index = Integer.parseInt(temp);
				}
			}
			CheckSaveData();
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onEventEnd(mContext, TV_DETAIL);
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	// added by yyc,for sort the episodesArray
	@SuppressWarnings("rawtypes")
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

	@SuppressWarnings("unchecked")
	public void InitData() {
		String m_j = null;
		int i = 0;
		int j = 0;
		if (m_ReturnProgramView.tv != null) {
			aq.id(R.id.program_name).text(m_ReturnProgramView.tv.name);
			if (m_ReturnProgramView.tv.poster != null) {
				aq.id(R.id.imageView3).image(
						m_ReturnProgramView.tv.poster.trim(), true, true);
			}
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

				int m = 15 * (page_num + 1);
				if ((m_ReturnProgramView.tv.episodes.length > 15)
						&& (m_ReturnProgramView.tv.episodes.length - m > 15)) {
					aq.id(R.id.textView9).visible();

				} else if ((m_ReturnProgramView.tv.episodes.length > 15)
						&& (m_ReturnProgramView.tv.episodes.length - m <= 15)) {
					aq.id(R.id.textView9)
							.text(String.format("后%s集 >",
									m_ReturnProgramView.tv.episodes.length - m));
					aq.id(R.id.textView9).visible();
				}

				for (i = 0; i < m_ReturnProgramView.tv.episodes.length
						&& i < 15; i++) {
					m_j = Integer.toString(i + 4);// m_ReturnProgramView.tv.episodes[i].name;
					// String str = m_ReturnProgramView.tv.episodes[i].name;
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
						m_button.setVisibility(View.INVISIBLE);
						aq.id(R.id.textView9).gone();
					}
				}

			}
			InitSourceData();
			showSourceView();
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
				if (m_ReturnProgramView.tv.episodes[0].down_urls == null
						|| m_ReturnProgramView.tv.episodes[0].down_urls[0].urls.length <= 0) {
					aq.id(R.id.button1).gone();
					aq.id(R.id.xiangkan_num).visible();
					aq.id(R.id.xiangkan_num).text("  (" + m_FavorityNum + ")");
					//#566
					aq.id(R.id.report_button).background(R.drawable.report_focuse);
					aq.id(R.id.report_button).clickable(false);
				}
				if (cacheManager != null && cacheInfoTemp != null) {

					String temp = cacheInfoTemp.getComments();
					if (temp != null && temp.toString().length() > 10) {
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
						if (m_ReturnProgramReviews == null) {
							GetReviews();
						}
						ShowComments();
					} else {
						GetReviews();
					}
				} else {
					GetReviews();
				}
			} else {
				aq.id(R.id.button20).background(R.drawable.zan_wu_xia_zai);
				aq.id(R.id.button20).clickable(false);
			}
		} else {
			GetServiceData();
		}
	}

	public void OnClickImageView(View v) {

	}

	// 初始化list数据函数
	public void InitListData(String url, JSONObject json, AjaxStatus status) {
		// android.util.Log.i("yanyuchuang",status.getCode()+"");
		// ||json == null||!json.has("tv")
		android.util.Log.i("JSONObject.AjaxStatus", status.getCode() + "");
		if (status.getCode() == AjaxStatus.NETWORK_ERROR) {
			aq.id(R.id.ProgressText).gone();
			app.MyToast(aq.getContext(),
					getResources().getString(R.string.networknotwork));
			if (cacheInfoTemp == null) {
				aq.id(R.id.none_net).visible();
			}
			return;
		}
		if (json == null || !json.has("tv")) {
			// aq.id(R.id.ProgressText).gone();
			// app.MyToast(aq.getContext(),
			// getResources().getString(R.string.networkispoor));
			// if (cacheInfoTemp == null) {
			// aq.id(R.id.none_net).visible();
			// }
			GetServiceData();
			return;
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			m_ReturnProgramView = mapper.readValue(json.toString(),
					ReturnProgramView.class);
			if (m_ReturnProgramView != null && prod_id != null) {
				cacheInfo.setProd_id(prod_id);
				cacheInfo.setProd_type("2");
				cacheInfo.setProd_value(json.toString());
				cacheInfo.setProd_subname("");
				cacheInfo.setLast_playtime("");
				cacheInfo.setCreate_date("");
				cacheManager.saveVideoCache(cacheInfo);
			}
			// 创建数据源对象
			InitData();
			aq.id(R.id.ProgressText).gone();
			aq.id(R.id.scrollView1).visible();
			// TV_String = json.toString();

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
		cb.timeout(30 * 1000);
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
					if (m_ReturnProgramView.tv.episodes[0].down_urls == null
							|| m_ReturnProgramView.tv.episodes[0].down_urls[0].urls.length <= 0) {
						aq.id(R.id.xiangkan_num).text(
								"  (" + Integer.toString(m_FavorityNum) + ")");
					}
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
		ParseInstallation installation = ParseInstallation
				.getCurrentInstallation();
		installation.addAllUnique("channels",
				Arrays.asList("CHANNEL_PROD_" + prod_id));
		installation.saveInBackground();

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
		if (!app.isNetworkAvailable()) {
			app.MyToast(this, "您当前网络有问题!");
			return;
		}
		popupReportProblem();
	}

	public void OnClickXiangkan(View v) {
		ParseInstallation installation = ParseInstallation
				.getCurrentInstallation();
		installation.addAllUnique("channels",
				Arrays.asList("CHANNEL_PROD_" + prod_id));
		installation.saveInBackground();

		String url = Constant.BASE_URL + "program/favority";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("prod_id", prod_id);

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.SetHeader(app.getHeaders());

		cb.params(params).url(url).type(JSONObject.class)
				.weakHandler(this, "CallServiceXiangkanResult");
		aq.ajax(cb);

	}

	public void CallServiceXiangkanResult(String url, JSONObject json,
			AjaxStatus status) {

		if (json != null) {
			try {
				// woof is "00000",now "20024",by yyc
				if (json.getString("res_code").trim().equalsIgnoreCase("00000")) {
					m_FavorityNum++;
					aq.id(R.id.button2).text(
							"收藏(" + Integer.toString(m_FavorityNum) + ")");
					aq.id(R.id.xiangkan_num).text(
							"  (" + Integer.toString(m_FavorityNum) + ")");
					app.MyToast(mContext, "操作成功");
				} else
					app.MyToast(this, "想看的影片已加入收藏列表");
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

	public void OnClickPlay(View v) {
		if (MobclickAgent.getConfigParams(this, "playBtnSuppressed").trim()
				.equalsIgnoreCase("1")) {
			app.MyToast(this, "暂无播放链接!");
			return;
		}

		if (!app.isNetworkAvailable()) {
			app.MyToast(this, "您当前网络有问题!");
			return;
		}

		if (player_select == null
				&& m_ReturnProgramView.tv.episodes.length <= 200) {
			{
				LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				final ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
						R.layout.player_select, null, true);
				popup_player_select = new PopupWindow(menuView,
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
						true);
				Button default_btn = (Button) menuView
						.findViewById(R.id.neizhibtn);
				default_btn.setOnClickListener(new Button.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						player_select = "default";
						app.SaveServiceData("player_select", "default");
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
						app.SaveServiceData("player_select", "third");
						popup_player_select.dismiss();
						StartIntentToPlayer();
					}
				});
				popup_player_select.setBackgroundDrawable(new BitmapDrawable());
				popup_player_select.showAtLocation(
						Detail_TV.this.findViewById(R.id.parent),
						Gravity.CENTER | Gravity.CENTER, 0, 40);
				popup_player_select.update();
			}
		} else {
			StartIntentToPlayer();
		}
	}

	public void StartIntentToPlayer() {
		app.checkUserSelect(Detail_TV.this);
		if (app.use2G3G) {
			// 电视剧type为2 ，sbuname 为当前集数
			if (MobclickAgent.getConfigParams(this, "playBtnSuppressed").trim()
					.equalsIgnoreCase("1")) {
				app.MyToast(this, "暂无播放链接!");
				return;
			}

			playrecordinfo = playrecordmanager.getPlayRecord(prod_id);
			current_time = 0;
			if (playrecordinfo != null
					&& playrecordinfo.getLast_playtime() != null
					&& playrecordinfo.getLast_playtime().length() > 0) {
				current_time = Long
						.parseLong(playrecordinfo.getLast_playtime());
				current_index = Integer.parseInt(playrecordinfo
						.getProd_subname()) - 1;
			} else {
				current_index = 0;
			}

			StatisticsUtils.StatisticsClicksShow(aq, app, prod_id, prod_name,
					(current_index + 1) + "", 2);
			SharedPreferences myPreference = this.getSharedPreferences(
					MY_SETTING, Context.MODE_PRIVATE);
			myPreference.edit()
					.putString(prod_id, Integer.toString(current_index))
					.commit();
			SetPlayBtnFlag(current_index);
			videoSourceSort(current_index);
			if (m_ReturnProgramView.tv.episodes != null
					&& m_ReturnProgramView.tv.episodes[current_index].video_urls != null
					&& m_ReturnProgramView.tv.episodes[current_index].video_urls.length > 0)
				PROD_URI = m_ReturnProgramView.tv.episodes[current_index].video_urls[0].url;
			PROD_SOURCE = null;
			if (m_ReturnProgramView.tv.episodes[current_index].down_urls != null) {
				for (int i = 0; i < m_ReturnProgramView.tv.episodes[current_index].down_urls.length; i++) {
					for (int k = 0; k < m_ReturnProgramView.tv.episodes[current_index].down_urls[i].urls.length; k++) {
						ReturnProgramView.DOWN_URLS.URLS urls = m_ReturnProgramView.tv.episodes[current_index].down_urls[i].urls[k];
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
							if (PROD_SOURCE != null)
								break;
						}
						if (PROD_SOURCE != null)
							break;
					}
				}
			}
			if (PROD_URI != null && PROD_URI.trim().length() > 0) {
				SaveToServer(2, PROD_URI, 1);
				Intent intent = new Intent(this, Webview_Play.class);
				Bundle bundle = new Bundle();
				bundle.putString("PROD_URI", PROD_URI);
				bundle.putString("NAME", m_ReturnProgramView.tv.name);
				bundle.putString("prod_subname",
						m_ReturnProgramView.tv.episodes[current_index].name);

				if (PROD_SOURCE != null && PROD_SOURCE.trim().length() > 0) {
					bundle.putString("prod_id", prod_id);
					bundle.putInt("CurrentIndex", current_index);
					bundle.putInt("CurrentCategory", 1);
					bundle.putString("PROD_SOURCE", PROD_SOURCE);
					bundle.putString("prod_type", prod_type);
					bundle.putLong("current_time", current_time);
				}
				intent.putExtras(bundle);
				if ("third".equalsIgnoreCase(player_select)
						|| m_ReturnProgramView.tv.episodes.length > 200) {
					Intent it = new Intent(Intent.ACTION_VIEW);
					Uri uri = Uri.parse(PROD_SOURCE);
					it.setDataAndType(uri, "video/*");
					startActivity(it);
				} else {
					startActivity(intent);
				}

			}
		}
	}

	// OnClickNext15
	public void OnClickNext15(View v) {
		String m_j = null;
		int j = 0;
		int i = 0;
		page_num++;
		int m = 15 * (page_num + 1);

		if ((m_ReturnProgramView.tv.episodes.length - m > 0)
				&& (m_ReturnProgramView.tv.episodes.length - m <= 15)) {
			aq.id(R.id.textView9).text(
					String.format("后%s集 >",
							m_ReturnProgramView.tv.episodes.length - m));
			aq.id(R.id.textView9).visible();
		}
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
					m_button.setVisibility(View.INVISIBLE);
					aq.id(R.id.textView9).gone();
				}
			}
		}
	}

	public void OnClickMoreReviews(View v) {
		String url = "http://movie.douban.com/subject/"
				+ m_ReturnProgramView.tv.douban_id + "/reviews";
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse(url);
		intent.setData(content_url);
		startActivity(intent);
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
			aq.id(R.id.textView9).text("后15集 >");
			aq.id(R.id.textView9).visible();
		}
		int m = 15 * page_num;

		if ((m_ReturnProgramView.tv.episodes.length - m > 0)
				&& (m_ReturnProgramView.tv.episodes.length - m <= 15)) {
			aq.id(R.id.textView9).text(
					String.format("后%s集 >",
							m_ReturnProgramView.tv.episodes.length - m));
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
					m_button.setVisibility(View.INVISIBLE);
				}
			}
		}
	}

	public void OnClickTVPlay(View v) {

		final int index = Integer.parseInt(v.getTag().toString());

		if (!app.isNetworkAvailable()) {
			app.MyToast(this, "您当前网络有问题!");
			return;
		}

		if (player_select == null
				&& m_ReturnProgramView.tv.episodes.length <= 200) {
			{
				LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				final ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
						R.layout.player_select, null, true);
				popup_player_select = new PopupWindow(menuView,
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
						true);
				Button default_btn = (Button) menuView
						.findViewById(R.id.neizhibtn);
				default_btn.setOnClickListener(new Button.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						player_select = "default";
						app.SaveServiceData("player_select", "default");
						popup_player_select.dismiss();
						StartIntentToPlayerTv(index);
					}
				});
				Button third_btn = (Button) menuView
						.findViewById(R.id.disanfangbtn);
				third_btn.setOnClickListener(new Button.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						player_select = "third";
						app.SaveServiceData("player_select", "third");
						popup_player_select.dismiss();
						StartIntentToPlayerTv(index);
					}
				});
				popup_player_select.setBackgroundDrawable(new BitmapDrawable());
				popup_player_select.showAtLocation(
						Detail_TV.this.findViewById(R.id.parent),
						Gravity.CENTER | Gravity.CENTER, 0, 40);
				popup_player_select.update();
			}
		} else {
			StartIntentToPlayerTv(index);
		}

	}

	public void StartIntentToPlayerTv(int index) {
		app.checkUserSelect(Detail_TV.this);
		if (app.use2G3G) {
			current_index = index;

			// 电视剧type为2 ，sbuname 为当前集数
			StatisticsUtils.StatisticsClicksShow(aq, app, prod_id, prod_name,
					(index + 1) + "", 2);

			SetPlayBtnFlag(current_index);

			// write current_index to myTvSetting file
			SharedPreferences myPreference = this.getSharedPreferences(
					MY_SETTING, Context.MODE_PRIVATE);
			myPreference.edit()
					.putString(prod_id, Integer.toString(current_index))
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
							if (PROD_SOURCE != null)
								break;
						}
						if (PROD_SOURCE != null)
							break;
					}
				}
			}
			if (PROD_URI != null && PROD_URI.trim().length() > 0) {
				SaveToServer(2, PROD_URI, index + 1);
				Intent intent = new Intent(this, Webview_Play.class);
				Bundle bundle = new Bundle();
				bundle.putString("PROD_URI", PROD_URI);
				bundle.putString("NAME", m_ReturnProgramView.tv.name);
				bundle.putString("prod_subname",
						m_ReturnProgramView.tv.episodes[current_index].name);

				if (PROD_SOURCE != null && PROD_SOURCE.trim().length() > 0) {
					mCurrentPlayData.CurrentIndex = index;
					playrecordinfo = playrecordmanager.getPlayRecord(prod_id,
							Integer.toString(index + 1));
					current_time = 0;
					if (playrecordinfo != null
							&& playrecordinfo.getLast_playtime() != null
							&& playrecordinfo.getLast_playtime().length() > 0) {
						current_time = Long.parseLong(playrecordinfo
								.getLast_playtime());
					}
					bundle.putString("prod_id", prod_id);
					bundle.putInt("CurrentIndex", index);
					bundle.putInt("CurrentCategory", 1);
					bundle.putString("PROD_SOURCE", PROD_SOURCE);
					bundle.putString("prod_type", prod_type);
					bundle.putLong("current_time", current_time);
				}
				intent.putExtras(bundle);
				if ("third".equalsIgnoreCase(player_select)
						|| m_ReturnProgramView.tv.episodes.length > 200) {
					Intent it = new Intent(Intent.ACTION_VIEW);
					Uri uri = Uri.parse(PROD_SOURCE);
					it.setDataAndType(uri, "video/*");
					startActivity(it);
				} else {
					startActivity(intent);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
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
	
	/*
	 * 填充片源
	 */
	public void InitSourceData()
	{
		sourceImage = new ArrayList<Integer>();
		sourceText = new ArrayList<String>();
		
		if (m_ReturnProgramView.tv.episodes[0].down_urls != null) {
			for (int j = 0; j < m_ReturnProgramView.tv.episodes[0].down_urls.length; j++) {
				if(m_ReturnProgramView.tv.episodes[0].down_urls[j].source
						.equalsIgnoreCase("wangpan"))
				{
					sourceImage.add(R.drawable.pptv);
					sourceText.add("wangpan");
				} else if(m_ReturnProgramView.tv.episodes[0].down_urls[j].source
						.equalsIgnoreCase("le_tv_fee"))
				{
					sourceImage.add(R.drawable.leshi);
					sourceText.add("le_tv_fee");
				}
				if (m_ReturnProgramView.tv.episodes[0].down_urls[j].source
						.equalsIgnoreCase("letv")) {
					sourceImage.add(R.drawable.leshi);
					sourceText.add("letv");
				} else if (m_ReturnProgramView.tv.episodes[0].down_urls[j].source
						.equalsIgnoreCase("fengxing")) {
					sourceImage.add(R.drawable.fengxing);
					sourceText.add("fengxing");
				} else if (m_ReturnProgramView.tv.episodes[0].down_urls[j].source
						.equalsIgnoreCase("qiyi")) {
					sourceImage.add(R.drawable.qiyi);
					sourceText.add("qiyi");
				} else if (m_ReturnProgramView.tv.episodes[0].down_urls[j].source
						.equalsIgnoreCase("youku")) {
					sourceImage.add(R.drawable.youku);
					sourceText.add("youku");
				} else if (m_ReturnProgramView.tv.episodes[0].down_urls[j].source
						.equalsIgnoreCase("sinahd")) {
					sourceImage.add(R.drawable.xinlang);
					sourceText.add("sinahd");
				} else if (m_ReturnProgramView.tv.episodes[0].down_urls[j].source
						.equalsIgnoreCase("sohu")) {
					sourceImage.add(R.drawable.souhu);
					sourceText.add("souhu");
				} else if (m_ReturnProgramView.tv.episodes[0].down_urls[j].source
						.equalsIgnoreCase("56")) {
					sourceImage.add(R.drawable.s56);
					sourceText.add("56");
				} else if (m_ReturnProgramView.tv.episodes[0].down_urls[j].source
						.equalsIgnoreCase("qq")) {
					sourceImage.add(R.drawable.qq);
					sourceText.add("qq");
				} else if (m_ReturnProgramView.tv.episodes[0].down_urls[j].source
						.equalsIgnoreCase("pptv")) {
					sourceImage.add(R.drawable.pptv);
					sourceText.add("pptv");
				} else if (m_ReturnProgramView.tv.episodes[0].down_urls[j].source
						.equalsIgnoreCase("pps"))
				{
					sourceImage.add(R.drawable.pps);
					sourceText.add("pps");
				} else if (m_ReturnProgramView.tv.episodes[0].down_urls[j].source
						.equalsIgnoreCase("m1905")) {
					sourceImage.add(R.drawable.m1905);
					sourceText.add("m1905");
				}
			}
		}
	}
	// 将片源排序
	@SuppressWarnings("rawtypes")
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

	// //
	// public void CallVideoPlayActivity() {
	//
	// if (PROD_SOURCE != null && PROD_SOURCE.trim().length() > 0) {
	// GetVideoSource(0, PROD_SOURCE);
	//
	// } else if (PROD_URI != null && PROD_URI.trim().length() > 0) {
	// GetVideoSource(1, PROD_URI);
	//
	// Intent intent = new Intent();
	// intent.setAction("android.intent.action.VIEW");
	// Uri content_url = Uri.parse(PROD_URI);
	// intent.setData(content_url);
	// startActivity(intent);
	// }
	// }

	//
	// public void CallVideoPlayActivity() {
	//
	// if (PROD_SOURCE != null && PROD_SOURCE.trim().length() > 0) {
	// GetVideoSource(0, PROD_SOURCE);
	//
	// } else if (PROD_URI != null && PROD_URI.trim().length() > 0) {
	// GetVideoSource(1, PROD_URI);
	//
	// Intent intent = new Intent();
	// intent.setAction("android.intent.action.VIEW");
	// Uri content_url = Uri.parse(PROD_URI);
	// intent.setData(content_url);
	// startActivity(intent);
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
					&& m_ReturnProgramView.tv.douban_id != null) {
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
							// ViewTreeObserver obs =
							// review2Content.getViewTreeObserver();
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
							// ViewTreeObserver obs =
							// review3Content.getViewTreeObserver();
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
		if (popupReviewDetail != null) {
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
			if (json != null && cacheManager != null) {
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
		params.put("prod_type", prod_type);// required int 视频类别
											// 1：电影，2：电视剧，3：综艺，4：视频
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
		if (!app.isNetworkAvailable()) {
			app.MyToast(this, "您当前网络有问题!");
			return;
		}
		if (downloadpopup != null) {
			downloadpopup.showAtLocation(findViewById(R.id.parent),
					Gravity.CENTER | Gravity.CENTER, 0, 78);
			downloadpopup.update();
			return;
		}
		popupview = OpenDownloadPapup();
	}

	private ViewGroup OpenDownloadPapup() {
		// TODO Auto-generated method stub
		LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
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
			m_button.setVisibility(View.INVISIBLE);
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
			m_button.setTextColor(Color.BLACK);// 设置颜色和文字的位置
			m_button.setGravity(Gravity.CENTER);
			m_button.setEnabled(true);
			for (int m = 0; m < data.size(); m++) {
				if (data.get(m).getMy_index().equalsIgnoreCase(m_j)) { // 设置已缓存背景
					m_button.setBackgroundDrawable(download_been);
					m_button.setEnabled(false);
					m_button.setTextColor(Color.WHITE);// 设置颜色和文字的位置
					m_button.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
				}
			}
			if (download_indexs.contains(m_j)) {
				m_button.setBackgroundDrawable(download_been);
				m_button.setEnabled(false);
				m_button.setTextColor(Color.WHITE);// 设置颜色和文字的位置
				m_button.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
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
		if (app.use2G3G) {
			if (DOWNLOAD_SOURCE != null) {
				String urlstr = DOWNLOAD_SOURCE;
				String localfile = Constant.PATH_VIDEO + prod_id + "_"
						+ (index + 1) + ".mp4";
				String my_name = m_ReturnProgramView.tv.name;
				String download_state = "wait";
				DownloadTask downloadTask = new DownloadTask(v, this,
						Detail_TV.this, prod_id, Integer.toString(index + 1),
						urlstr, localfile);
				downloadTask.execute(prod_id, Integer.toString(index + 1),
						urlstr, m_ReturnProgramView.tv.poster, my_name,
						download_state);
				Toast.makeText(Detail_TV.this, "视频已加入下载队列", Toast.LENGTH_SHORT)
						.show();
				// 将按钮的背景色改成已缓存
				v.setBackgroundDrawable(download_been);
				v.setEnabled(false);
				((Button) v).setTextColor(Color.WHITE);// 设置颜色和文字的位置
				((Button) v)
						.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
				download_indexs.add(index);
				android.util.Log.i("download_indexs",
						download_indexs.toString());
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
			m_button.setVisibility(View.INVISIBLE);
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
			m_button.setTextColor(Color.BLACK);// 设置颜色和文字的位置
			m_button.setGravity(Gravity.CENTER);
			m_button.setEnabled(true);
			for (int m = 0; m < data.size(); m++) {
				if (data.get(m).getMy_index().equalsIgnoreCase(m_j)) { // 设置已缓存背景
					m_button.setBackgroundDrawable(download_been);
					m_button.setEnabled(false);
					m_button.setTextColor(Color.WHITE);// 设置颜色和文字的位置
					m_button.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
				}
			}
			android.util.Log.i("download_indexs", download_indexs.toString());
			if (download_indexs.contains(Integer.parseInt(m_j))) {
				m_button.setBackgroundDrawable(download_been);
				m_button.setEnabled(false);
				m_button.setTextColor(Color.WHITE);// 设置颜色和文字的位置
				m_button.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
			}
			m_button.setVisibility(View.VISIBLE);
		}
		if (i < 15) {
			for (j = i; j < 15; j++) {
				m_j = Integer.toString(j + 4);
				Button m_button = (Button) menuView.findViewById(getResources()
						.getIdentifier("download_button" + m_j, "id",
								getPackageName()));
				m_button.setVisibility(View.INVISIBLE);
				aq.id(R.id.textView9).gone();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
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
				| Gravity.CENTER, 0, 40);
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
				Toast.makeText(Detail_TV.this, "亲，必须选择一个理由啊！",
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
		Toast.makeText(Detail_TV.this, "您反馈的问题已提交，我们会尽快处理，感谢您的支持！",
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
