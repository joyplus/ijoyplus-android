package com.joy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.joy.Tools.AsyncBitmapLoader;
import com.joy.Tools.BitmapZoom;
import com.joy.Tools.Tools;
import com.joy.Tools.AsyncBitmapLoader.ImageCallBack;
import com.joy.view.PullToRefreshView;
import com.joy.view.PullToRefreshView.OnFooterRefreshListener;
import com.joy.view.PullToRefreshView.OnHeaderRefreshListener;
import com.mobclick.android.MobclickAgent;

public class Activity01 extends Activity implements OnHeaderRefreshListener,OnFooterRefreshListener{
	Button btn_dianying,btn_juji,btn_shipin,btn_zongyi,btn_sousuo;
	private  LinearLayout linearLayout1 = null;
    private  LinearLayout linearLayout2 = null;
    private  LinearLayout linearLayout3 = null;
    private ScrollView	scrollView;
	PullToRefreshView mPullToRefreshView;
	private int USE_LINEAR_INTERVAL = 0;
    private int linearlayoutWidth = 0;
	private int page_count = 9;// 每次加载x张图片
	private int current_page = 0;// 当前页数
    private int index =0;
    List<String> IMG_list;
    Context context;
    int selectIndex=1;
    Bitmap BigBitmap;
    GetThird_AccessToken getThird_AccessToken;
    String where="where_1_1";
    private String images_dianying[] = {
			"http://img16.pplive.cn/2009/12/08/13521044515_230X306.jpg",
			"http://img15.pplive.cn/2009/11/13/18032661617_230X306.jpg",
			"http://img5.pplive.cn/2008/11/26/15290531087_230X306.jpg",
			"http://img11.pplive.cn/2009/05/15/17152279731_230X306.jpg",
			"http://img5.pplive.cn/2011/09/23/10405710241_230X306.jpg",
			"http://img11.pplive.cn/2009/01/29/14123973014_230X306.jpg",
			"http://img15.pplive.cn/2010/04/06/13492503957_230X306.jpg",
			"http://img11.pplive.cn/2010/05/18/14370589655_230X306.jpg",
			"http://img7.pplive.cn/2010/05/08/10045437836_230X306.jpg",
			"http://img16.pplive.cn/2009/12/08/13521044515_230X306.jpg",
			"http://img15.pplive.cn/2009/11/13/18032661617_230X306.jpg",
			"http://img11.pplive.cn/2009/01/29/14123973014_230X306.jpg",
			"http://img5.pplive.cn/2008/11/26/15290531087_230X306.jpg",
			"http://img11.pplive.cn/2009/05/15/17152279731_230X306.jpg",
			"http://img5.pplive.cn/2011/09/23/10405710241_230X306.jpg",
			"http://img15.pplive.cn/2010/04/06/13492503957_230X306.jpg",
			"http://img11.pplive.cn/2010/05/18/14370589655_230X306.jpg",
			"http://img7.pplive.cn/2010/05/08/10045437836_230X306.jpg",
			"http://img16.pplive.cn/2009/12/08/13521044515_230X306.jpg",
			"http://img15.pplive.cn/2009/11/13/18032661617_230X306.jpg",
			"http://img11.pplive.cn/2009/01/29/14123973014_230X306.jpg",
			"http://img5.pplive.cn/2008/11/26/15290531087_230X306.jpg",
			"http://img11.pplive.cn/2009/05/15/17152279731_230X306.jpg",
			"http://img5.pplive.cn/2011/09/23/10405710241_230X306.jpg",
			"http://img15.pplive.cn/2010/04/06/13492503957_230X306.jpg",
			"http://img11.pplive.cn/2010/05/18/14370589655_230X306.jpg",
			"http://img7.pplive.cn/2010/05/08/10045437836_230X306.jpg"
	};
	private String images_juji[] = {
			"http://img15.pplive.cn/2009/11/13/18032661617_230X306.jpg",
			"http://img11.pplive.cn/2009/01/29/14123973014_230X306.jpg",
			"http://img16.pplive.cn/2009/12/08/13521044515_230X306.jpg",
			"http://img5.pplive.cn/2008/11/26/15290531087_230X306.jpg",
			"http://img11.pplive.cn/2009/05/15/17152279731_230X306.jpg",
			"http://img5.pplive.cn/2011/09/23/10405710241_230X306.jpg",
			"http://img15.pplive.cn/2010/04/06/13492503957_230X306.jpg",
			"http://img11.pplive.cn/2010/05/18/14370589655_230X306.jpg",
			"http://img7.pplive.cn/2010/05/08/10045437836_230X306.jpg",
			"http://img16.pplive.cn/2009/12/08/13521044515_230X306.jpg",
			"http://img15.pplive.cn/2009/11/13/18032661617_230X306.jpg",
			"http://img11.pplive.cn/2009/01/29/14123973014_230X306.jpg",
			"http://img5.pplive.cn/2008/11/26/15290531087_230X306.jpg",
			"http://img11.pplive.cn/2009/05/15/17152279731_230X306.jpg",
			"http://img5.pplive.cn/2011/09/23/10405710241_230X306.jpg",
			"http://img15.pplive.cn/2010/04/06/13492503957_230X306.jpg",
			"http://img11.pplive.cn/2010/05/18/14370589655_230X306.jpg",
			"http://img7.pplive.cn/2010/05/08/10045437836_230X306.jpg",
			"http://img16.pplive.cn/2009/12/08/13521044515_230X306.jpg",
			"http://img15.pplive.cn/2009/11/13/18032661617_230X306.jpg",
			"http://img11.pplive.cn/2009/01/29/14123973014_230X306.jpg",
			"http://img5.pplive.cn/2008/11/26/15290531087_230X306.jpg",
			"http://img11.pplive.cn/2009/05/15/17152279731_230X306.jpg",
			"http://img5.pplive.cn/2011/09/23/10405710241_230X306.jpg",
			"http://img15.pplive.cn/2010/04/06/13492503957_230X306.jpg",
			"http://img11.pplive.cn/2010/05/18/14370589655_230X306.jpg",
			"http://img7.pplive.cn/2010/05/08/10045437836_230X306.jpg"
	};
	private String images_zongyi[] = {
			"http://img16.pplive.cn/2009/12/08/13521044515_230X306.jpg",
			"http://img11.pplive.cn/2009/01/29/14123973014_230X306.jpg",
			"http://img5.pplive.cn/2008/11/26/15290531087_230X306.jpg",
			"http://img15.pplive.cn/2009/11/13/18032661617_230X306.jpg",
			"http://img11.pplive.cn/2009/05/15/17152279731_230X306.jpg",
			"http://img5.pplive.cn/2011/09/23/10405710241_230X306.jpg",
			"http://img15.pplive.cn/2010/04/06/13492503957_230X306.jpg",
			"http://img11.pplive.cn/2010/05/18/14370589655_230X306.jpg",
			"http://img7.pplive.cn/2010/05/08/10045437836_230X306.jpg",
			"http://img16.pplive.cn/2009/12/08/13521044515_230X306.jpg",
			"http://img15.pplive.cn/2009/11/13/18032661617_230X306.jpg",
			"http://img11.pplive.cn/2009/01/29/14123973014_230X306.jpg",
			"http://img5.pplive.cn/2008/11/26/15290531087_230X306.jpg",
			"http://img11.pplive.cn/2009/05/15/17152279731_230X306.jpg",
			"http://img5.pplive.cn/2011/09/23/10405710241_230X306.jpg",
			"http://img15.pplive.cn/2010/04/06/13492503957_230X306.jpg",
			"http://img11.pplive.cn/2010/05/18/14370589655_230X306.jpg",
			"http://img7.pplive.cn/2010/05/08/10045437836_230X306.jpg",
			"http://img16.pplive.cn/2009/12/08/13521044515_230X306.jpg",
			"http://img15.pplive.cn/2009/11/13/18032661617_230X306.jpg",
			"http://img11.pplive.cn/2009/01/29/14123973014_230X306.jpg",
			"http://img15.pplive.cn/2010/04/06/13492503957_230X306.jpg",
			"http://img5.pplive.cn/2008/11/26/15290531087_230X306.jpg",
			"http://img11.pplive.cn/2009/05/15/17152279731_230X306.jpg",
			"http://img5.pplive.cn/2011/09/23/10405710241_230X306.jpg",
			"http://img11.pplive.cn/2010/05/18/14370589655_230X306.jpg",
			"http://img7.pplive.cn/2010/05/08/10045437836_230X306.jpg"
	};
	private String images_shipin[] = {
			"http://img16.pplive.cn/2009/12/08/13521044515_230X306.jpg",
			"http://img15.pplive.cn/2009/11/13/18032661617_230X306.jpg",
			"http://img11.pplive.cn/2009/01/29/14123973014_230X306.jpg",
			"http://img5.pplive.cn/2008/11/26/15290531087_230X306.jpg",
			"http://img11.pplive.cn/2009/05/15/17152279731_230X306.jpg",
			"http://img5.pplive.cn/2011/09/23/10405710241_230X306.jpg",
			"http://img15.pplive.cn/2010/04/06/13492503957_230X306.jpg",
			"http://img11.pplive.cn/2010/05/18/14370589655_230X306.jpg",
			"http://img7.pplive.cn/2010/05/08/10045437836_230X306.jpg",
			"http://img16.pplive.cn/2009/12/08/13521044515_230X306.jpg",
			"http://img15.pplive.cn/2009/11/13/18032661617_230X306.jpg",
			"http://img11.pplive.cn/2009/01/29/14123973014_230X306.jpg",
			"http://img5.pplive.cn/2008/11/26/15290531087_230X306.jpg",
			"http://img11.pplive.cn/2009/05/15/17152279731_230X306.jpg",
			"http://img5.pplive.cn/2011/09/23/10405710241_230X306.jpg",
			"http://img15.pplive.cn/2010/04/06/13492503957_230X306.jpg",
			"http://img11.pplive.cn/2010/05/18/14370589655_230X306.jpg",
			"http://img7.pplive.cn/2010/05/08/10045437836_230X306.jpg",
			"http://img16.pplive.cn/2009/12/08/13521044515_230X306.jpg",
			"http://img15.pplive.cn/2009/11/13/18032661617_230X306.jpg",
			"http://img11.pplive.cn/2009/01/29/14123973014_230X306.jpg",
			"http://img5.pplive.cn/2008/11/26/15290531087_230X306.jpg",
			"http://img11.pplive.cn/2009/05/15/17152279731_230X306.jpg",
			"http://img5.pplive.cn/2011/09/23/10405710241_230X306.jpg",
			"http://img15.pplive.cn/2010/04/06/13492503957_230X306.jpg",
			"http://img11.pplive.cn/2010/05/18/14370589655_230X306.jpg",
			"http://img7.pplive.cn/2010/05/08/10045437836_230X306.jpg"
	};
//	private String images_dianying[];
//	private String name_dianying[];*/
	private String name_dianying[] = {
			"电影1",
			"电影2",
			"电影3",
			"电影4",
			"电影5",
			"电影6",
			"电影7",
			"电影8",
			"电影9",
			"电影10",
			"电影11",
			"电影12",
			"电影13",
			"电影14",
			"电影15",
			"电影16",
			"电影17",
			"电影18",
			"电影19",
			"电影20",
			"电影21",
			"电影22",
			"电影23",
			"电影24",
			"电影25",
			"电影26",
			"电影27"
	};
	private String name_juji[] = {
			"剧集1",
			"剧集2",
			"剧集3",
			"剧集4",
			"剧集5",
			"剧集6",
			"剧集7",
			"剧集8",
			"剧集9",
			"剧集10",
			"剧集11",
			"剧集12",
			"剧集13",
			"剧集14",
			"剧集15",
			"剧集16",
			"剧集17",
			"剧集18",
			"剧集19",
			"剧集20",
			"剧集21",
			"剧集22",
			"剧集23",
			"剧集24",
			"剧集25",
			"剧集26",
			"剧集27"
	};
	private String name_zongyi[] = {
			"综艺1",
			"综艺2",
			"综艺3",
			"综艺4",
			"综艺5",
			"综艺6",
			"综艺7",
			"综艺8",
			"综艺9",
			"综艺10",
			"综艺11",
			"综艺12",
			"综艺13",
			"综艺14",
			"综艺15",
			"综艺16",
			"综艺17",
			"综艺18",
			"综艺19",
			"综艺20",
			"综艺21",
			"综艺22",
			"综艺23",
			"综艺24",
			"综艺25",
			"综艺26",
			"综艺27"
	};
	private String name_shipin[]={
			"视频1",
			"视频2",
			"视频3",
			"视频4",
			"视频5",
			"视频6",
			"视频7",
			"视频8",
			"视频9",
			"视频10",
			"视频11",
			"视频12",
			"视频13",
			"视频14",
			"视频15",
			"视频16",
			"视频17",
			"视频18",
			"视频19",
			"视频20",
			"视频21",
			"视频22",
			"视频23",
			"视频24",
			"视频25",
			"视频26",
			"视频27"	
	};
	AsyncBitmapLoader asyncBitmapLoader;
	ProgressDialog progressBar;
	Random random;
	final Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				addBitmaps(++current_page, page_count,images_dianying,name_dianying);
				break;
			case 2:
				addBitmaps(++current_page, page_count,images_juji,name_juji);
				break;
			case 3:
				addBitmaps(++current_page, page_count,images_zongyi,name_zongyi);
				break;
			case 4:
				addBitmaps(++current_page, page_count,images_shipin,name_shipin);
				break;
			case 10:
				linearLayout1.removeAllViews();
				linearLayout2.removeAllViews();
				linearLayout3.removeAllViews();
				Tools.ClearBitmap(BigBitmap);
				current_page=0;
				index=0;
				scrollView.fullScroll(ScrollView.FOCUS_UP);
				addBitmaps(current_page, page_count,images_dianying,name_dianying);
				Toast.makeText(context, getResources().getString(R.string.shuaxin), Toast.LENGTH_SHORT).show();
				break;
			case 20:
				linearLayout1.removeAllViews();
				linearLayout2.removeAllViews();
				linearLayout3.removeAllViews();
				Tools.ClearBitmap(BigBitmap);
				current_page=0;
				index=0;
				scrollView.fullScroll(ScrollView.FOCUS_UP);
				addBitmaps(current_page, page_count,images_juji,name_juji);
				Toast.makeText(context, getResources().getString(R.string.shuaxin), Toast.LENGTH_SHORT).show();
				break;
			case 30:
				linearLayout1.removeAllViews();
				linearLayout2.removeAllViews();
				linearLayout3.removeAllViews();
				Tools.ClearBitmap(BigBitmap);
				current_page=0;
				index=0;
				scrollView.fullScroll(ScrollView.FOCUS_UP);
				addBitmaps(current_page, page_count,images_zongyi,name_zongyi);
				Toast.makeText(context, getResources().getString(R.string.shuaxin), Toast.LENGTH_SHORT).show();
				break;
			case 40:
				linearLayout1.removeAllViews();
				linearLayout2.removeAllViews();
				linearLayout3.removeAllViews();
				Tools.ClearBitmap(BigBitmap);
				current_page=0;
				index=0;
				scrollView.fullScroll(ScrollView.FOCUS_UP);
				addBitmaps(current_page, page_count,images_shipin,name_shipin);
				Toast.makeText(context, getResources().getString(R.string.shuaxin), Toast.LENGTH_SHORT).show();
				break;
			case 11:
				linearLayout1.removeAllViews();
				linearLayout2.removeAllViews();
				linearLayout3.removeAllViews();
				Tools.ClearBitmap(BigBitmap);
				current_page=0;
				index=0;
				scrollView.fullScroll(ScrollView.FOCUS_UP);
				addBitmaps(current_page, page_count,images_dianying,name_dianying);
				progressBar.dismiss();
				break;
			case 12:
				linearLayout1.removeAllViews();
				linearLayout2.removeAllViews();
				linearLayout3.removeAllViews();
				Tools.ClearBitmap(BigBitmap);
				current_page=0;
				index=0;
				scrollView.fullScroll(ScrollView.FOCUS_UP);
				addBitmaps(current_page, page_count,images_juji,name_juji);
				progressBar.dismiss();
				break;
			case 13:
				linearLayout1.removeAllViews();
				linearLayout2.removeAllViews();
				linearLayout3.removeAllViews();
				Tools.ClearBitmap(BigBitmap);
				current_page=0;
				index=0;
				scrollView.fullScroll(ScrollView.FOCUS_UP);
				addBitmaps(current_page, page_count,images_zongyi,name_zongyi);
				progressBar.dismiss();
				break;
			case 14:
				linearLayout1.removeAllViews();
				linearLayout2.removeAllViews();
				linearLayout3.removeAllViews();
				Tools.ClearBitmap(BigBitmap);
				current_page=0;
				index=0;
				scrollView.fullScroll(ScrollView.FOCUS_UP);
				addBitmaps(current_page, page_count,images_shipin,name_shipin);
				progressBar.dismiss();
				break;
			case 999:
				Intent intent = new Intent();
		    	intent.setClass(context, DetailActivity.class);
		    	startActivity(intent);
		    	progressBar.dismiss();
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity01);
		context=this;
		getThird_AccessToken=(GetThird_AccessToken)getApplicationContext();
		asyncBitmapLoader=new AsyncBitmapLoader();
		IMG_list=new ArrayList<String>();
		mPullToRefreshView = (PullToRefreshView)findViewById(R.id.act01_main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setOnFooterRefreshListener(this);
        
        random=new Random();
		btn_dianying=(Button)findViewById(R.id.act01_dianying);
		btn_dianying.setEnabled(false);
		btn_dianying.setBackgroundResource(R.drawable.topleft1);
		btn_juji=(Button)findViewById(R.id.act01_juji);
		btn_shipin=(Button)findViewById(R.id.act01_shipin);
		btn_zongyi=(Button)findViewById(R.id.act01_zongyi);
		btn_sousuo=(Button)findViewById(R.id.act01_sousuo);
		linearLayout1 = (LinearLayout)findViewById(R.id.act01_linearlayout1);
        linearLayout2 = (LinearLayout)findViewById(R.id.act01_linearlayout2);
        linearLayout3 = (LinearLayout)findViewById(R.id.act01_linearlayout3);
        scrollView=(ScrollView)findViewById(R.id.act01_sco);
        linearlayoutWidth =  getWindowManager().getDefaultDisplay().getWidth()/3;
        
        /*images_dianying=new String[100];
        name_dianying=new String[100];
        for (int i = 0; i < 100; i++) {
			images_dianying[i]=images_juji[Math.abs(random.nextInt()%images_juji.length)];
			name_dianying[i]="电影"+(i+1);
		}*/
        
        
        images_dianying=SetSaveData(where, images_dianying);
        name_dianying=SetSaveName(where, name_dianying);
        addBitmaps(current_page, page_count,images_dianying,name_dianying);
		
        btn_dianying.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getThird_AccessToken.setjujiliebiaoXianshi(0);
				selectIndex=1;
				where="where_1_1";
		        images_dianying=SetSaveData(where, images_dianying);
		        name_dianying=SetSaveName(where, name_dianying);
				btn_dianying.setBackgroundResource(R.drawable.topleft1);
				btn_juji.setBackgroundResource(R.drawable.topbarmid);
				btn_zongyi.setBackgroundResource(R.drawable.topbarmid);
				btn_shipin.setBackgroundResource(R.drawable.topbarright);
				btn_dianying.setEnabled(false);
				btn_juji.setEnabled(true);
				btn_shipin.setEnabled(true);
				btn_zongyi.setEnabled(true);
				progressBar = ProgressDialog.show(context, getResources().getString(R.string.shaohou), getResources().getString(R.string.pull_to_refresh_footer_refreshing_label));
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run(){
						Message msg = new Message(); 
		                msg.what = 11; 
		                handler.sendMessage(msg); 
					}
				}, 1000);
				
			}
		});
		btn_juji.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getThird_AccessToken.setjujiliebiaoXianshi(1);
				selectIndex=2;
				where="where_1_2";
				images_juji=SetSaveData(where, images_juji);
				name_juji=SetSaveName(where, name_juji);
				btn_dianying.setBackgroundResource(R.drawable.topleft);
				btn_juji.setBackgroundResource(R.drawable.topbarmid1);
				btn_zongyi.setBackgroundResource(R.drawable.topbarmid);
				btn_shipin.setBackgroundResource(R.drawable.topbarright);
				btn_dianying.setEnabled(true);
				btn_juji.setEnabled(false);
				btn_zongyi.setEnabled(true);
				btn_shipin.setEnabled(true);
				progressBar = ProgressDialog.show(context, getResources().getString(R.string.shaohou), getResources().getString(R.string.pull_to_refresh_footer_refreshing_label));
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run(){
						Message msg = new Message(); 
		                msg.what = 12; 
		                handler.sendMessage(msg); 
					}
				}, 1000);
				
			}
		});
		btn_zongyi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getThird_AccessToken.setjujiliebiaoXianshi(2);
				selectIndex=3;
				where="where_1_3";
				images_zongyi=SetSaveData(where, images_zongyi);
				name_zongyi=SetSaveName(where, name_zongyi);
				btn_dianying.setBackgroundResource(R.drawable.topleft);
				btn_juji.setBackgroundResource(R.drawable.topbarmid);
				btn_zongyi.setBackgroundResource(R.drawable.topbarmid1);
				btn_shipin.setBackgroundResource(R.drawable.topbarright);
				btn_dianying.setEnabled(true);
				btn_juji.setEnabled(true);
				btn_zongyi.setEnabled(false);
				btn_shipin.setEnabled(true);
				progressBar = ProgressDialog.show(context, getResources().getString(R.string.shaohou), getResources().getString(R.string.pull_to_refresh_footer_refreshing_label));
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run(){
						Message msg = new Message(); 
		                msg.what = 13; 
		                handler.sendMessage(msg); 
					}
				}, 1000);
				
				
			}
		});
		btn_shipin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getThird_AccessToken.setjujiliebiaoXianshi(0);
				selectIndex=4;
				where="where_1_4";
				images_shipin=SetSaveData(where, images_shipin);
				name_shipin=SetSaveName(where, name_shipin);
				btn_dianying.setBackgroundResource(R.drawable.topleft);
				btn_juji.setBackgroundResource(R.drawable.topbarmid);
				btn_zongyi.setBackgroundResource(R.drawable.topbarmid);
				btn_shipin.setBackgroundResource(R.drawable.topbarright1);
				btn_dianying.setEnabled(true);
				btn_juji.setEnabled(true);
				btn_zongyi.setEnabled(true);
				btn_shipin.setEnabled(false);
				progressBar = ProgressDialog.show(context, getResources().getString(R.string.shaohou), getResources().getString(R.string.pull_to_refresh_footer_refreshing_label));
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run(){
						Message msg = new Message(); 
		                msg.what = 14; 
		                handler.sendMessage(msg); 
					}
				}, 1000);
				
			}
		});
		btn_sousuo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				intent.setClass(context, Sousuo.class);
				startActivity(intent);
			}
		});
	}
	@Override
	protected void onDestroy() {
		Tools.ClearBitmap(BigBitmap);
		super.onDestroy();
	}
	//界面中加载图片
	private void addBitmaps(int pageindex, int pagecount,String img[],String name[]){
		IMG_list=Arrays.asList(img);
		LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	try {
    		for(int i = index; i < pagecount * (pageindex + 1)&&i<img.length; i++){
    			try {
    				final View view=inflater.inflate(R.layout.wall, null);
    				RelativeLayout rll = (RelativeLayout)view.findViewById(R.id.RelativeLayout02);
    				final ImageView imageView = (ImageView)view.findViewById(R.id.wall_image);
    				TextView textView = (TextView)view.findViewById(R.id.wall_text);
    				Bitmap bitmap=setImage(imageView, IMG_list.get(index));
    				if (bitmap==null) {
    					imageView.setImageResource(R.drawable.pic_bg);
					}
    				else {
    					BigBitmap = BitmapZoom.bitmapZoomByWidth(bitmap, linearlayoutWidth);
    					imageView.setImageBitmap(BigBitmap);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(BigBitmap.getWidth(), BigBitmap.getHeight()+40);
                        layoutParams.setMargins(4, 1, 4, 1);
                        imageView.setLayoutParams(layoutParams);
    					imageView.setImageBitmap(bitmap);
					}
    				textView.setText(name[index]);
    				//点击了影片，用到ontouch方法是为了有点击效果
    				imageView.setOnTouchListener(new OnTouchListener() {
						
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							switch (event.getAction()) {
							case MotionEvent.ACTION_UP:
								int index  =  (Integer)v.getTag();
								switch (selectIndex) {
								case 1:
									getThird_AccessToken.setPicURL(images_dianying[index]);
									getThird_AccessToken.setPicName(name_dianying[index]);
									break;
								case 2:
									getThird_AccessToken.setPicURL(images_juji[index]);
									getThird_AccessToken.setPicName(name_juji[index]);
									break;
								case 3:
									getThird_AccessToken.setPicURL(images_zongyi[index]);
									getThird_AccessToken.setPicName(name_zongyi[index]);
									break;
								case 4:
									getThird_AccessToken.setPicURL(images_shipin[index]);
									getThird_AccessToken.setPicName(name_shipin[index]);
									break;
								}
						    	Toast.makeText(context, ""+(index+1), Toast.LENGTH_SHORT).show();
						    	Tools.changeLight(imageView, 0);
						    	progressBar = ProgressDialog.show(context, getResources().getString(R.string.shaohou), getResources().getString(R.string.pull_to_refresh_footer_refreshing_label));
								new Handler().postDelayed(new Runnable(){
									@Override
									public void run(){
										Message msg = new Message(); 
						                msg.what = 999; 
						                handler.sendMessage(msg); 
									}
								}, 1000);
								break;
							case MotionEvent.ACTION_DOWN:
								Tools.changeLight(imageView, -50);
								break;
							case MotionEvent.ACTION_CANCEL:
								Tools.changeLight(imageView, 0);
								break;
							}
							return true;
						}
					});
    				imageView.setTag(new Integer(index));
    				switch (USE_LINEAR_INTERVAL) 
    				{
						case 0:
							linearLayout1.addView(rll);
							break;
						case 1:
							linearLayout2.addView(rll);
							break;
						case 2:
							linearLayout3.addView(rll);
							break;
						default:
							break;
					}
    				index++;
    				if (index>=img.length) {
    					Toast.makeText(context, getResources().getString(R.string.jiazai), Toast.LENGTH_SHORT).show();
					}
    				System.out.println("index====>"+index);
    				USE_LINEAR_INTERVAL++;
    				USE_LINEAR_INTERVAL= USE_LINEAR_INTERVAL%3;
    				
				} catch (Exception e) {
					System.out.println(e.toString());
			}	
    	}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.toString());
		}
    }
	//异步加载图片
	public Bitmap setImage(ImageView imageView,String URL){
		return asyncBitmapLoader.loadBitmap(imageView, URL, linearlayoutWidth,new ImageCallBack() {
			
			@Override
			public void imageLoad(ImageView imageView, Bitmap bitmap) {
				if (bitmap!=null) {
					BigBitmap = BitmapZoom.bitmapZoomByWidth(bitmap, linearlayoutWidth);
					imageView.setImageBitmap(BigBitmap);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(BigBitmap.getWidth(), BigBitmap.getHeight()+40);
                    layoutParams.setMargins(4, 1, 4, 1);
                    imageView.setLayoutParams(layoutParams);
				}
            	else {
            		imageView.setImageResource(R.drawable.pic_bg);
				}
			}
		});
	}
	//保存URL地址，没网络的情况从内存拿之前保存过的地址来显示图片
	public String[] SetSaveData(String where,String URL[]){
		if (Tools.isNetworkAvailable(context)==false) {
        	getThird_AccessToken.GetImageName(where);
        	String Img_Name=getThird_AccessToken.getIMG_Name();
        	URL=Tools.Split(Img_Name, "$URL$");
		}
		else {
			int a=0;
			String iMG_Name="";
			for (int i = 0; i < URL.length; i++) {
				if (a==0) {
					iMG_Name+=URL[i];
					a=1;
				}
				else {
					iMG_Name+="$URL$"+URL[i];
				}
			}
			getThird_AccessToken.setIMG_Name(iMG_Name);
			getThird_AccessToken.SaveImageName(where);
		}
		return URL;
	}
	public String[] SetSaveName(String where,String Name[]){
		if (Tools.isNetworkAvailable(context)==false) {
        	getThird_AccessToken.GetName(where);
        	String Name_URL=getThird_AccessToken.getName_URL();
        	Name=Tools.Split(Name_URL, "$URL$");
		}
		else {
			int a=0;
			String Name_URL="";
			for (int i = 0; i < Name.length; i++) {
				if (a==0) {
					Name_URL+=Name[i];
					a=1;
				}
				else {
					Name_URL+="$URL$"+Name[i];
				}
			}
			getThird_AccessToken.setName_URL(Name_URL);
			getThird_AccessToken.SaveName(where);
		}
		return Name;
	}
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Message msg = new Message(); 
                msg.what = selectIndex; 
                handler.sendMessage(msg); 
				mPullToRefreshView.onFooterRefreshComplete();
			}
		}, 1000);
	}
	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Message msg = new Message(); 
                msg.what = selectIndex*10; 
                handler.sendMessage(msg); 
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		},1000);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			AlertDialog.Builder builder=new AlertDialog.Builder(context);
	  		  builder.setTitle(getResources().getString(R.string.tishi));
	  		  builder.setMessage(getResources().getString(R.string.shifoutuichu)).setPositiveButton(getResources().getString(R.string.queding), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							getThird_AccessToken.setexit(getString(R.string.exit_true));
							getThird_AccessToken.SaveExit();
							finish();
				        	android.os.Process.killProcess(android.os.Process.myPid()); 
							System.exit(0);
						}
					})
				   .setNegativeButton(getResources().getString(R.string.quxiao), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							
						}
					});
				AlertDialog ad = builder.create();
				ad.show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	public void onResume() { 
		super.onResume();
		MobclickAgent.onResume(this); 
	} 
	public void onPause() { 
		System.out.println("selectIndex====>"+selectIndex);
		switch (selectIndex) {
		case 1:
			linearLayout1.removeAllViews();
			linearLayout2.removeAllViews();
			linearLayout3.removeAllViews();
			Tools.ClearBitmap(BigBitmap);
			current_page=0;
			index=0;
			scrollView.fullScroll(ScrollView.FOCUS_UP);
			addBitmaps(current_page, page_count,images_dianying,name_dianying);
			break;
		case 2:
			linearLayout1.removeAllViews();
			linearLayout2.removeAllViews();
			linearLayout3.removeAllViews();
			Tools.ClearBitmap(BigBitmap);
			current_page=0;
			index=0;
			scrollView.fullScroll(ScrollView.FOCUS_UP);
			addBitmaps(current_page, page_count,images_juji,name_juji);
			break;
		case 3:
			linearLayout1.removeAllViews();
			linearLayout2.removeAllViews();
			linearLayout3.removeAllViews();
			Tools.ClearBitmap(BigBitmap);
			current_page=0;
			index=0;
			scrollView.fullScroll(ScrollView.FOCUS_UP);
			addBitmaps(current_page, page_count,images_zongyi,name_zongyi);
			break;
		case 4:
			linearLayout1.removeAllViews();
			linearLayout2.removeAllViews();
			linearLayout3.removeAllViews();
			Tools.ClearBitmap(BigBitmap);
			current_page=0;
			index=0;
			scrollView.fullScroll(ScrollView.FOCUS_UP);
			addBitmaps(current_page, page_count,images_shipin,name_shipin);
			break;
		}
		super.onPause(); 
		MobclickAgent.onPause(this); 
	}
	
}
