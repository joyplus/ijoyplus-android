package com.joyplus;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
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
import android.os.Handler;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.joyplus.R.color;
import com.joyplus.Service.Return.ReturnProgramComments;
import com.joyplus.Service.Return.ReturnProgramReviews;
import com.joyplus.Service.Return.ReturnProgramView;
import com.joyplus.Service.Return.ReturnProgramView.DOWN_URLS;
import com.joyplus.Video.VideoPlayerActivity;
import com.joyplus.cache.VideoCacheInfo;
import com.joyplus.cache.VideoCacheManager;
import com.joyplus.download.Dao;
import com.joyplus.download.DownloadTask;
import com.umeng.analytics.MobclickAgent;

public class Detail_Movie extends Activity {
	private AQuery aq;
	private App app;
	private String TAG = "Detail_Movie";
	private ReturnProgramView m_ReturnProgramView = null;
	private String prod_id = null;
	private String prod_name = null;
	public String PROD_SOURCE = null;
	public String DOWNLOAD_SOURCE = null;
	private String PROD_URI = null;
	private String download_index = "movie";
	private int m_FavorityNum = 0;
	private int m_SupportNum = 0;

	private ReturnProgramReviews m_ReturnProgramReviews = null;
	private ScrollView mScrollView;
	private int isLastisNext = 1;
	private int mLastY = 0;
	private Bitmap bitmap;
	String name;
	private PopupWindow popup_report = null;
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
	// 播放记录变量
	public static int REQUESTPLAYTIME = 200;
	public static int RETURN_CURRENT_TIME = 150;

	private CurrentPlayData mCurrentPlayData;

	VideoCacheInfo cacheInfo;
	VideoCacheInfo cacheInfoTemp;
	VideoCacheManager cacheManager;
	/**
	 * 利用消息处理机制适时更新APP里的数据
	 */
	private static String MOVIE_DETAIL = "电影详情";
	Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_movie);
		app = (App) getApplication();
		Intent intent = getIntent();
		prod_id = intent.getStringExtra("prod_id");
		prod_name = intent.getStringExtra("prod_name");
		mContext = this;
		aq = new AQuery(this);
		aq.id(R.id.scrollView1).gone();
		if (prod_name != null)
			aq.id(R.id.program_name).text(prod_name);

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
		cacheManager = new VideoCacheManager(Detail_Movie.this);
		cacheInfo = new VideoCacheInfo();
		mCurrentPlayData = new CurrentPlayData();
		mCurrentPlayData.prod_id = prod_id;
		if (prod_id != null)
			CheckSaveData();
	}

	public void OnClickTab1TopLeft(View v) {
		finish();
	}

	public void OnClickTab1TopRight(View v) {
		Intent intent = new Intent(Detail_Movie.this, MainTopRightDialog.class);
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
		// 取 drawable 的长宽
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();

		// 取 drawable 的颜色格式
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		// 建立对应 bitmap
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		// 建立对应 bitmap 的画布
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		// 把 drawable 内容画到画布中
		drawable.draw(canvas);
		return bitmap;
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
		MobclickAgent.onEventBegin(mContext, MOVIE_DETAIL);
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onEventEnd(mContext, MOVIE_DETAIL);
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
			if (m_ReturnProgramView.movie.poster != null) {
				aq.id(R.id.imageView3).image(
						m_ReturnProgramView.movie.poster.trim(), true, true);
			}
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
			if (m_ReturnProgramView.movie.episodes != null
					&& m_ReturnProgramView.movie.episodes[0].video_urls != null
					&& m_ReturnProgramView.movie.episodes[0].video_urls[0].url != null)
				PROD_URI = m_ReturnProgramView.movie.episodes[0].video_urls[0].url;
			videoSourceSort(0);
			if (m_ReturnProgramView.movie.episodes[0].down_urls != null) {
				for (int i = 0; i < m_ReturnProgramView.movie.episodes[0].down_urls.length; i++) {
					for (int k = 0; k < m_ReturnProgramView.movie.episodes[0].down_urls[i].urls.length; k++) {
						ReturnProgramView.DOWN_URLS.URLS urls = m_ReturnProgramView.movie.episodes[0].down_urls[i].urls[k];
						if (urls != null) {
							/*
							 * #define GAO_QING @"mp4" #define BIAO_QING @"flv"
							 * #define CHAO_QING @"hd2" #define LIU_CHANG @"3gp"
							 */
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
			}
			if (DOWNLOAD_SOURCE == null) {
				aq.id(R.id.button9).background(R.drawable.zan_wu_xia_zai);
				aq.id(R.id.button9).clickable(false);
			}

			if (Dao.getInstance(Detail_Movie.this).getInfosOfProd_id(prod_id)
					.size() != 0) {
				aq.id(R.id.button9).background(R.drawable.yi_huan_cun);
				aq.id(R.id.button9).clickable(false);
			}

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
			if(m_ReturnProgramView.movie.douban_id==null)
			{
				//显示豆瓣更多
				aq.id(R.id.moreReviews).gone();
			}
			
			if (m_ReturnProgramView.topics != null
					&& m_ReturnProgramView.topics.length >= 1) {
				ShowTopics();
			} else {
				aq.id(R.id.LinearLayoutXGYD).gone();
			}
			
			GetReviews();
		} else {
			GetServiceData();
		}

	}

	public void videoSourceSort(int source_index) {
		if (m_ReturnProgramView.movie.episodes[source_index].down_urls != null) {
			for (int j = 0; j < m_ReturnProgramView.movie.episodes[source_index].down_urls.length; j++) {
				if (m_ReturnProgramView.movie.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("letv")) {
					m_ReturnProgramView.movie.episodes[source_index].down_urls[j].index = 0;
				} else if (m_ReturnProgramView.movie.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("fengxing")) {
					m_ReturnProgramView.movie.episodes[source_index].down_urls[j].index = 1;
				} else if (m_ReturnProgramView.movie.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("qiyi")) {
					m_ReturnProgramView.movie.episodes[source_index].down_urls[j].index = 2;
				} else if (m_ReturnProgramView.movie.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("youku")) {
					m_ReturnProgramView.movie.episodes[source_index].down_urls[j].index = 3;
				} else if (m_ReturnProgramView.movie.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("sinahd")) {
					m_ReturnProgramView.movie.episodes[source_index].down_urls[j].index = 4;
				} else if (m_ReturnProgramView.movie.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("sohu")) {
					m_ReturnProgramView.movie.episodes[source_index].down_urls[j].index = 5;
				} else if (m_ReturnProgramView.movie.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("56")) {
					m_ReturnProgramView.movie.episodes[source_index].down_urls[j].index = 6;
				} else if (m_ReturnProgramView.movie.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("qq")) {
					m_ReturnProgramView.movie.episodes[source_index].down_urls[j].index = 7;
				} else if (m_ReturnProgramView.movie.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("pptv")) {
					m_ReturnProgramView.movie.episodes[source_index].down_urls[j].index = 8;
				} else if (m_ReturnProgramView.movie.episodes[source_index].down_urls[j].source
						.equalsIgnoreCase("m1905")) {
					m_ReturnProgramView.movie.episodes[source_index].down_urls[j].index = 9;
				}
			}
			if (m_ReturnProgramView.movie.episodes[source_index].down_urls.length > 1) {
				Arrays.sort(
						m_ReturnProgramView.movie.episodes[source_index].down_urls,
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

	// 初始化list数据函数
	public void InitListData(String url, JSONObject json, AjaxStatus status) {
		if (status.getCode() == AjaxStatus.NETWORK_ERROR || json == null) {
			aq.id(R.id.ProgressText).gone();
			app.MyToast(aq.getContext(),
					getResources().getString(R.string.networknotwork));
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
					cacheInfo.setProd_type("1");
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
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// execute the task
						GetServiceData();
					}
				}, 2000);

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

	public void OnClickCacheDown(View v) {
		app.checkUserSelect(Detail_Movie.this);
		if (app.use2G3G) {
			if (DOWNLOAD_SOURCE != null) {
				// String urlstr = DOWNLOAD_SOURCE;
				String urlposter = m_ReturnProgramView.movie.poster;
				String localfile = Constant.PATH_VIDEO + prod_id + "_"
						+ download_index + ".mp4";
				String my_name = m_ReturnProgramView.movie.name;
				String download_state = "wait";
				DownloadTask downloadTask = new DownloadTask(v, this,
						Detail_Movie.this, prod_id, download_index,
						DOWNLOAD_SOURCE, localfile);
				downloadTask.execute(prod_id, download_index, DOWNLOAD_SOURCE,
						urlposter, my_name, download_state);
				aq.id(R.id.button9).background(R.drawable.yi_huan_cun);// 点击下载后直接把下载按钮的状态改变掉
				aq.id(R.id.button9).clickable(false);
				Toast.makeText(Detail_Movie.this, "视频已加入下载队列",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(Detail_Movie.this, "该视频不支持下载",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	// 看服务列表到底是什么东西,为什么不行
	public void OnClickReportProblem(View v) {
		popupReportProblem();
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

	public void CallProgramPlayResult(String url, JSONObject json,
			AjaxStatus status) {
		/*
		 * 
		 */
	}

	public void OnClickPlay(View v) throws JSONException {
		if (MobclickAgent.getConfigParams(this, "playBtnSuppressed").trim()
				.equalsIgnoreCase("1")) {
			app.MyToast(this, "暂无播放链接!");
			return;
		}

		app.checkUserSelect(Detail_Movie.this);
		if (app.use2G3G) {

			// 统计点击次数
			// 因为电影 只有一集，所以为“”，电影type为1
			StatisticsUtils.StatisticsClicksShow(aq, app, prod_id, prod_name,
					"", 1);

			if (PROD_SOURCE != null && PROD_SOURCE.trim().length() > 0) {
				if (PROD_SOURCE.contains("test=m3u8")) {
					PROD_SOURCE = PROD_SOURCE.replace("tag=ios", "tag=android");
				}
				CallVideoPlayActivity(PROD_SOURCE,
						m_ReturnProgramView.movie.name);

			} else if (PROD_URI != null && PROD_URI.trim().length() > 0) {

				SaveToServer(2, PROD_URI);
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse(PROD_URI);
				intent.setData(content_url);
				startActivity(intent);
			}
		}
	}

	/*
	 * 将播放数据保存在服务器
	 */
	public void SaveToServer(int play_type, String SourceUrl) {
		String url = Constant.BASE_URL + "program/play";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("app_key", Constant.APPKEY);// required string
												// 申请应用时分配的AppKey。
		params.put("prod_id", m_ReturnProgramView.movie.id);// required string
															// 视频id
		params.put("prod_name", m_ReturnProgramView.movie.name);// required
																// string 视频名字
																// string
																// 视频的集数
		params.put("prod_type", 1);// required int 视频类别 1：电影，2：电视剧，3：综艺，4：视频
		params.put("playback_time", 0);// _time required int 上次播放时间，单位：秒
		params.put("duration", 0);// required int 视频时长， 单位：秒
		// 播放的类别 1: 视频地址播放
		// 2:webview播放
		if (play_type == 1) {
			params.put("play_type", "1");
		} else {
			params.put("play_type", "2");// required string
		}
		params.put("video_url", SourceUrl);
		// string
		// 视频url
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.SetHeader(app.getHeaders());
		cb.params(params).url(url).type(JSONObject.class)
				.weakHandler(this, "CallProgramPlayResult");
		// cb.params(params).url(url);
		aq.ajax(cb);
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
		if(m_ReturnProgramReviews == null)
		{
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
							ViewTreeObserver obs = review3Content
									.getViewTreeObserver();
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
		AlertDialog alertDialog = new AlertDialog.Builder(this).setMessage(
				m_ReturnProgramReviews.reviews[Integer.parseInt(v.getTag()
						.toString())].comments).create();
		Window window = alertDialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.alpha = 0.6f;
		window.setAttributes(lp);
		alertDialog.show();
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

//		aq.id(R.id.ProgressText).visible();
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
	
	public void OnClickMoreReviews(View v)
	{
		String url = "http://movie.douban.com/subject/"+m_ReturnProgramView.movie.douban_id+"/reviews";
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse(url);
		intent.setData(content_url);
		startActivity(intent);
	}
	
	public void CallVideoPlayActivity(String m_uri, String title) {

		int sourceId = -1;// 如果是风行那值为1,如果不是那就为其他的值

		if (m_ReturnProgramView.movie.episodes[0].down_urls != null) {

			for (int j = 0; j < m_ReturnProgramView.movie.episodes[0].down_urls.length; j++) {

				if (m_ReturnProgramView.movie.episodes[0].down_urls[j].source
						.equalsIgnoreCase("fengxing")) {
					sourceId = 1;
				}
			}
		}

		if (BuildConfig.DEBUG)
			Log.i(TAG, "CallVideoPlayActivity--->>sourceId : " + sourceId);

		mCurrentPlayData.CurrentCategory = 0;
		mCurrentPlayData.CurrentIndex = 0;
		app.setCurrentPlayData(mCurrentPlayData);
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("path", m_uri);
		bundle.putString("title", title);
		bundle.putString("prod_id", prod_id);
		bundle.putString("prod_type", "1");
		bundle.putLong("current_time", 0);
		intent.putExtras(bundle);
		intent.setClass(Detail_Movie.this, VideoPlayerActivity.class);
		try {
			startActivity(intent);
			
		} catch (ActivityNotFoundException ex) {
			Log.e(TAG, "mp4 fail", ex);
		}
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
				| Gravity.CENTER, 40, 80);
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
				Toast.makeText(Detail_Movie.this, "亲，必须选择一个理由啊！",
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
		Toast.makeText(Detail_Movie.this, "您反馈的问题已提交，我们会尽快处理，感谢您的支持！",
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
