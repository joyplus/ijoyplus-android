package com.joy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.joy.Tools.AsyncBitmapLoader;
import com.joy.Tools.AsyncBitmapLoader.ImageCallBack;
import com.joy.Tools.BitmapZoom;
import com.joy.Tools.ImageAndText;
import com.joy.Tools.Tools;
import com.joy.view.PullToRefreshView;
import com.joy.view.PullToRefreshView.OnFooterRefreshListener;
import com.joy.view.PullToRefreshView.OnHeaderRefreshListener;
import com.joy.weibo.net.AccessToken;
import com.joy.weibo.net.Oauth2AccessTokenHeader;
import com.joy.weibo.net.Utility;
import com.joy.weibo.net.Weibo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Welcome extends Activity implements OnHeaderRefreshListener,OnFooterRefreshListener{
	private static final String SINA_CONSUMER_KEY = "3069972161";// 替换为开发者的appkey，例如"1646212960";
	private static final String SINA_CONSUMER_SECRET = "eea5ede316c6a283c6bae57e52c9a877";
	Button btn_dianying,btn_juji,btn_shipin,btn_zongyi;
	private  LinearLayout linearLayout1 = null;
    private  LinearLayout linearLayout2 = null;
    private  LinearLayout linearLayout3 = null;
	PullToRefreshView mPullToRefreshView;
	List<String> list;
	Context context;
	AsyncBitmapLoader asyncBitmapLoader;
	private int USE_LINEAR_INTERVAL = 0;
    private int linearlayoutWidth = 0;
	private int page_count = 6;// 每次加载x张图片
	private int current_page = 0;// 当前页数
    private int index =0;
    int select_index=1;
    Bitmap BigBitmap;
    private String images_dianying[] = {
			"http://imgsrc.baidu.com/forum/pic/item/06509e4472138361500ffe18.jpg",
			"http://imgsrc.baidu.com/forum/pic/item/c99ee50389ab4fa5d53f7c1a.jpg",
			"http://imgsrc.baidu.com/forum/pic/item/f77c583494e1a963241f141f.jpg",
			"http://imgsrc.baidu.com/forum/pic/item/dbabbe86c0ede61366096eee.jpg",
			"http://wenwen.soso.com/p/20100708/20100708050003-665156019.jpg",
			"http://movie.yntv.cn/category/2021302/2009/07/10/images/2021302_20090710_802.jpg",
			"http://www.sznews.com/rollnews/images/20110601/19/10978230170352392435.jpg",
			"http://img2.mtime.com/mg/2008/25/9efc5c01-6d6a-4aa5-b09e-f49359eb7ea8.jpg",
			"http://epaper.loone.cn/site1/czrb/res/1/20080616/7271213581523254.jpg"
	};
	private String images_juji[] = {
			"http://www.95hz.com/uploads/allimg/090522/1654454.jpg",
			"http://www.gog.com.cn/pic/0/10/30/68/10306846_997429.jpg",
			"http://img2.mtime.com/mg/2009/33/d7a6ed3b-18bb-431f-b49f-fef4a92cb896.jpg",
			"http://www.nen.com.cn/73749755317977088/20080616/1054206.jpg",
			"http://img.daqi.com/upload/slidepic/2007-12-26/110_1198628708_1778481.jpg",
			"http://imgsrc.baidu.com/forum/pic/item/0a443eefd9e73142c45cc35a.jpg",
			"http://www.m1905.com/UploadFile/pics/2008/12/12/092848264.jpg",
			"http://img.daqi.com/upload/slidepic/2007-12-26/332_1198628708_1778481.jpg",
			"http://img.daqi.com/upload/slidepic/2007-12-26/151_1198628708_1778481.jpg"
	};
	private String images_zongyi[] = {
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
	private String images_shipin[]={
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
    /*private String images_dianying[];
	private String images_juji[];
	private String images_zongyi[];
	private String images_shipin[];*/
	GetThird_AccessToken getThird_AccessToken;
	String where="where_0_1";
	final Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				addBitmaps(++current_page, page_count,images_dianying);
				break;
			case 2:
				addBitmaps(++current_page, page_count,images_juji);
				break;
			case 3:
				addBitmaps(++current_page, page_count,images_zongyi);
				break;
			case 4:
				addBitmaps(++current_page, page_count,images_shipin);
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		getThird_AccessToken = (GetThird_AccessToken) getApplicationContext();
		context=this;
		getThird_AccessToken=(GetThird_AccessToken)getApplicationContext();
		asyncBitmapLoader=new AsyncBitmapLoader();
		list=new ArrayList<String>();
		mPullToRefreshView = (PullToRefreshView)findViewById(R.id.welcome_main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setOnFooterRefreshListener(this);
        btn_dianying=(Button)findViewById(R.id.welcome_dianying);
        btn_dianying.setEnabled(false);
        btn_dianying.setBackgroundResource(R.drawable.topleft1);
        btn_juji=(Button)findViewById(R.id.welcome_juji);
        btn_shipin=(Button)findViewById(R.id.welcome_shipin);
        btn_zongyi=(Button)findViewById(R.id.welcome_zongyi);
        linearLayout1 = (LinearLayout)findViewById(R.id.welcome_linearlayout1);
        linearLayout2 = (LinearLayout)findViewById(R.id.welcome_linearlayout2);
        linearLayout3 = (LinearLayout)findViewById(R.id.welcome_linearlayout3);
        linearlayoutWidth =  getWindowManager().getDefaultDisplay().getWidth()/3;
        
        images_dianying=SetSaveData(where,images_dianying);
        
        addBitmaps(current_page, page_count,images_dianying);
        btn_dianying.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				where="where_0_1";
				images_dianying=SetSaveData(where,images_dianying);
				select_index=1;
				btn_dianying.setBackgroundResource(R.drawable.topleft1);
				btn_juji.setBackgroundResource(R.drawable.topbarmid);
				btn_zongyi.setBackgroundResource(R.drawable.topbarmid);
				btn_shipin.setBackgroundResource(R.drawable.topbarright);
				btn_dianying.setEnabled(false);
				btn_juji.setEnabled(true);
				btn_zongyi.setEnabled(true);
				btn_shipin.setEnabled(true);
				linearLayout1.removeAllViews();
				linearLayout2.removeAllViews();
				linearLayout3.removeAllViews();
				Tools.ClearBitmap(BigBitmap);
				current_page=0;
				index=0;
				addBitmaps(current_page, page_count,images_dianying);
			}
		});
        btn_juji.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				where="where_0_2";
				images_juji=SetSaveData(where,images_juji);
				select_index=2;
				btn_dianying.setBackgroundResource(R.drawable.topleft);
				btn_juji.setBackgroundResource(R.drawable.topbarmid1);
				btn_zongyi.setBackgroundResource(R.drawable.topbarmid);
				btn_shipin.setBackgroundResource(R.drawable.topbarright);
				btn_dianying.setEnabled(true);
				btn_zongyi.setEnabled(true);
				btn_juji.setEnabled(false);
				btn_shipin.setEnabled(true);
				linearLayout1.removeAllViews();
				linearLayout2.removeAllViews();
				linearLayout3.removeAllViews();
				Tools.ClearBitmap(BigBitmap);
				current_page=0;
				index=0;
				addBitmaps(current_page, page_count,images_juji);
			}
		});
        btn_zongyi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				where="where_0_3";
				images_zongyi=SetSaveData(where,images_zongyi);
				select_index=3;
				btn_dianying.setBackgroundResource(R.drawable.topleft);
				btn_juji.setBackgroundResource(R.drawable.topbarmid);
				btn_zongyi.setBackgroundResource(R.drawable.topbarmid1);
				btn_shipin.setBackgroundResource(R.drawable.topbarright);
				btn_dianying.setEnabled(true);
				btn_juji.setEnabled(true);
				btn_shipin.setEnabled(true);
				btn_zongyi.setEnabled(false);
				linearLayout1.removeAllViews();
				linearLayout2.removeAllViews();
				linearLayout3.removeAllViews();
				Tools.ClearBitmap(BigBitmap);
				current_page=0;
				index=0;
				addBitmaps(current_page, page_count,images_zongyi);
			}
		});
        btn_shipin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				where="where_0_4";
				images_shipin=SetSaveData(where,images_shipin);
				select_index=4;
				btn_dianying.setBackgroundResource(R.drawable.topleft);
				btn_juji.setBackgroundResource(R.drawable.topbarmid);
				btn_zongyi.setBackgroundResource(R.drawable.topbarmid);
				btn_shipin.setBackgroundResource(R.drawable.topbarright1);
				btn_zongyi.setEnabled(true);
				btn_dianying.setEnabled(true);
				btn_juji.setEnabled(true);
				btn_shipin.setEnabled(false);
				linearLayout1.removeAllViews();
				linearLayout2.removeAllViews();
				linearLayout3.removeAllViews();
				Tools.ClearBitmap(BigBitmap);
				current_page=0;
				index=0;
				addBitmaps(current_page, page_count,images_shipin);
			}
		});
	}
	private void addBitmaps(int pageindex, int pagecount,String img[]){
		list=Arrays.asList(img);
		LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	try {
    		for(int i = index; i < pagecount * (pageindex + 1)&&i<img.length; i++){
    			try {
    				final View view=inflater.inflate(R.layout.wall, null);
    				RelativeLayout rll = (RelativeLayout)view.findViewById(R.id.RelativeLayout02);
    				ImageView imageView = (ImageView)view.findViewById(R.id.wall_image);
    				TextView textView = (TextView)view.findViewById(R.id.wall_text);
    				
    				Bitmap bitmap=setImage(imageView, list.get(index));
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
    				textView.setText("第"+(index+1)+"张");
    				imageView.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							int index  =  (Integer)v.getTag();
							Intent intent = new Intent();
					    	intent.setClass(context, DetailActivity.class);
					    	startActivity(intent);
					    	//finish();
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
	public Bitmap setImage(ImageView imageView,String imageURL){
		return asyncBitmapLoader.loadBitmap(imageView, imageURL, new ImageCallBack() {
			
			@Override
			public void imageLoad(ImageView imageView, Bitmap bitmap) {
				if (bitmap!=null) {
            		BigBitmap = BitmapZoom.bitmapZoomByWidth(bitmap, linearlayoutWidth);
            		imageView.setImageBitmap(BigBitmap);
            		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(BigBitmap.getWidth(), BigBitmap.getHeight()+40);
            		layoutParams.setMargins(4, 1, 4, 1);
            		imageView.setLayoutParams(layoutParams);
            		imageView.setImageBitmap(bitmap);
				}
            	else {
            		imageView.setImageResource(R.drawable.pic_bg);
				}
			}
		});
	}
	public void Btndenglu(View v){
		getThird_AccessToken.setlogin_where(getString(R.string.sinawb));
		getThird_AccessToken.GetAccessToken();
		Intent intent=new Intent();
		if (getThird_AccessToken.getAccessToken().trim().length()==0) {
			getThird_AccessToken.setlogin_where(getString(R.string.tencent));
			getThird_AccessToken.GetQQAccessToken();
			if (getThird_AccessToken.getQQ_Token().trim().length()==0) {
				intent.setClass(Welcome.this, Login_Activity.class);
				startActivity(intent);
				finish();
			}
			else
			{
				getThird_AccessToken.setQQ_Token(getThird_AccessToken.getQQ_Token().trim());
				getThird_AccessToken.GetOpenID();
				getThird_AccessToken.setOpenID(getThird_AccessToken.getOpenID());
				intent.setClass(context, JoyActivity.class);
				startActivity(intent);
				finish();
			}
		}
		else
		{
			Weibo.getInstance().setupConsumerConfig(SINA_CONSUMER_KEY, SINA_CONSUMER_SECRET);
			getThird_AccessToken.setAccessToken(getThird_AccessToken.getAccessToken().trim());
			getThird_AccessToken.GetExpires_in();
			Utility.setAuthorization(new Oauth2AccessTokenHeader());
			AccessToken accessToken = new AccessToken(getThird_AccessToken.getAccessToken().trim(), SINA_CONSUMER_SECRET);
			accessToken.setExpiresIn(getThird_AccessToken.getExpires_in());
			Weibo.getInstance().setAccessToken(accessToken);
			intent.setClass(context, JoyActivity.class);
			startActivity(intent);
			finish();
		}
	}
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Message msg = new Message(); 
                msg.what = select_index; 
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
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		},1000);
	}
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			AlertDialog.Builder builder=new AlertDialog.Builder(context);
	  		  builder.setTitle(getResources().getString(R.string.tishi));
	  		  builder.setMessage(getResources().getString(R.string.shifoutuichu)).setPositiveButton(getResources().getString(R.string.queding), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
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
}
