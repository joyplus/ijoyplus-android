package com.joyplus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import com.joyplus.widget.Log;
import com.joyplus.widget.MyGallery;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.joyplus.Service.Return.ReturnProgramReviews;
import com.joyplus.Service.Return.ReturnProgramView;
import com.joyplus.Service.Return.ReturnProgramView.DOWN_URLS;
import com.joyplus.Video.VideoPlayerActivity;
import com.joyplus.cache.VideoCacheInfo;
import com.joyplus.cache.VideoCacheManager;
import com.joyplus.download.Dao;
import com.joyplus.download.DownloadTask;
import com.parse.ParseInstallation;
import com.parse.PushService;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;
import com.umeng.analytics.MobclickAgent;

public class Weixin_ShareVideo extends Activity {
	private AQuery aq;
	private App app;
	private ReturnProgramView m_ReturnProgramView = null;
	private String prod_id = null;
	private String prod_name = null;
	public String PROD_SOURCE = null;
	public String DOWNLOAD_SOURCE = null;


	private ScrollView mScrollView;
	private int mLastY = 0;
	String name;
	private IWXAPI api;
	private Bitmap bitmap;
	// 播放记录变量
	public static int REQUESTPLAYTIME = 200;
	public static int RETURN_CURRENT_TIME = 150;

	private static String MOVIE_DETAIL = "电影详情";
	Context mContext;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weixin_sharevideo);
		app = (App) getApplication();
		Intent intent = getIntent();
		prod_id = intent.getStringExtra("prod_id");
		prod_name = intent.getStringExtra("prod_name");
		mContext = this;
		aq = new AQuery(this);
		aq.id(R.id.scrollView1).gone();
		if (prod_name != null)
			aq.id(R.id.program_name).text(prod_name);
		
		api = WXAPIFactory.createWXAPI(this, Constant.APP_ID, false);
		api.registerApp(Constant.APP_ID);
		
		mScrollView = (ScrollView) findViewById(R.id.scrollView1);
		mScrollView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (mLastY == mScrollView.getScrollY()) {
						// TODO
					} else {
						mLastY = mScrollView.getScrollY();
					}
				}
				return false;
			}
		});
		
		GetServiceData();
	}
	

	public void OnClickTab1TopLeft(View v) {
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
			
			aq.id(R.id.textView11).text(
					"    " + m_ReturnProgramView.movie.summary);
		} 
		if (m_ReturnProgramView.show != null) {
			aq.id(R.id.program_name).text(m_ReturnProgramView.show.name);
			if (m_ReturnProgramView.show.poster != null) {
				aq.id(R.id.imageView3).image(
						m_ReturnProgramView.show.poster.trim(), true, true);
			}
			aq.id(R.id.textView5).text(m_ReturnProgramView.show.stars);
			aq.id(R.id.textView6).text(m_ReturnProgramView.show.area);
			aq.id(R.id.textView7).text(m_ReturnProgramView.show.directors);
			aq.id(R.id.textView8).text(m_ReturnProgramView.show.publish_date);
			
			aq.id(R.id.textView11).text(
					"    " + m_ReturnProgramView.show.summary);
		} 
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
			
			aq.id(R.id.textView11).text(
					"    " + m_ReturnProgramView.tv.summary);
		} 
		
		ImageView imageView3 = (ImageView) findViewById(R.id.imageView3);
		Drawable drawable = imageView3.getDrawable();
		if (drawable == null) {
			drawable = getResources().getDrawable(R.drawable.detail_picture_bg);
		}
		bitmap = drawableToBitmap(drawable);
	}
	

	// 初始化list数据函数
	public void InitListData(String url, JSONObject json, AjaxStatus status) {
		android.util.Log.i("JSONObject.AjaxStatus", status.getCode() + "");
		if (status.getCode() == AjaxStatus.NETWORK_ERROR) {
			aq.id(R.id.ProgressText).gone();
			app.MyToast(aq.getContext(),
					getResources().getString(R.string.networknotwork));
			
			return;
		}
		if (json == null) {
			aq.id(R.id.ProgressText).gone();
			
			GetServiceData();
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
		cb.SetHeader(app.getHeaders());
		cb.timeout(30 * 1000);
		aq.id(R.id.ProgressText).visible();
		aq.progress(R.id.progress).ajax(cb);
		
	}
	
	public void OnClickWeixinShareVideo(View v)
	{
		if (!checkWeixinInstall()) {
			app.MyToast(mContext, "未安装微信");
			return;
		}
		String url = "weixin.joyplus.tv/info.php?prod_id=" + prod_id;// 收到分享的好友点击信息会跳转到这个地址去
		WXWebpageObject localWXWebpageObject = new WXWebpageObject();
		localWXWebpageObject.webpageUrl = url;
		WXMediaMessage localWXMediaMessage = new WXMediaMessage(
				localWXWebpageObject);
		localWXMediaMessage.title = "悦视频分享";// 不能太长，否则微信会提示出错。不过博主没验证过具体能输入多长。
		localWXMediaMessage.description = "我在用#悦视频#Android版观看<" + prod_name
				+ ">，推荐给大家哦！更多精彩尽在悦视频，欢迎下载：http://ums.bz/REGLDb/，快来和我一起看吧！";
		localWXMediaMessage.thumbData = Util.bmpToByteArray(bitmap, true);
		SendMessageToWX.Req localReq = new SendMessageToWX.Req();
		localReq.transaction = String.valueOf(System.currentTimeMillis());
		localReq.message = localWXMediaMessage;
		localReq.scene = SendMessageToWX.Req.WXSceneSession;
		api.sendReq(localReq);
		finish();
	}
	
	private PackageInfo packageInfo;

	public boolean checkWeixinInstall() {

		try {
			packageInfo = this.getPackageManager().getPackageInfo(
					"com.tencent.mm", 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		if (packageInfo == null) {
			return false;
		} else {
			return true;
		}
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

}
