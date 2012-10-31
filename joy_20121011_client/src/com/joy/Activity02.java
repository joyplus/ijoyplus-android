package com.joy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.joy.Tools.AsyncBitmapLoader;
import com.joy.Tools.BitmapZoom;
import com.joy.Tools.Tools;
import com.joy.Tools.AsyncBitmapLoader.ImageCallBack;
import com.joy.view.PullToRefreshView;
import com.joy.view.PullToRefreshView.OnFooterRefreshListener;
import com.joy.view.PullToRefreshView.OnHeaderRefreshListener;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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

public class Activity02 extends Activity implements OnHeaderRefreshListener,OnFooterRefreshListener{
	Button btn_xunzhaohaoyou;
	private  LinearLayout linearLayout1 = null;
    private  LinearLayout linearLayout2 = null;
    private  LinearLayout linearLayout3 = null;
	PullToRefreshView mPullToRefreshView;
	private int USE_LINEAR_INTERVAL = 0;
    private int linearlayoutWidth = 0;
	private int page_count = 6;// 每次加载x张图片
	private int current_page = 0;// 当前页数
    private int index =0;
    List<String> list;
    Context context;
    Bitmap bitmap2;
    GetThird_AccessToken getThird_AccessToken;
    private String images[] = {
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
    private String name_dianying[] = {
			"电影1",
			"电影2",
			"电影3",
			"电影4",
			"电影5",
			"电影6",
			"电影7",
			"电影8",
			"电影9"
	};
    AsyncBitmapLoader asyncBitmapLoader=new AsyncBitmapLoader();
    ProgressDialog progressBar;
	final Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1500:
				addBitmaps(++current_page, page_count,images,name_dianying);
				break;
			case 1501:
				linearLayout1.removeAllViews();
				linearLayout2.removeAllViews();
				linearLayout3.removeAllViews();
				Tools.ClearBitmap(bitmap2);
				current_page=0;
				index=0;
				addBitmaps(current_page, page_count,images,name_dianying);
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
		setContentView(R.layout.activity02);
		context=this;
		getThird_AccessToken=(GetThird_AccessToken)getApplicationContext();
		list=new ArrayList<String>();
		mPullToRefreshView = (PullToRefreshView)findViewById(R.id.act02_main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setOnFooterRefreshListener(this);
        
        btn_xunzhaohaoyou=(Button)findViewById(R.id.act02_xunzhaohaoyou);
		linearLayout1 = (LinearLayout)findViewById(R.id.act02_linearlayout1);
        linearLayout2 = (LinearLayout)findViewById(R.id.act02_linearlayout2);
        linearLayout3 = (LinearLayout)findViewById(R.id.act02_linearlayout3);
        linearlayoutWidth =  getWindowManager().getDefaultDisplay().getWidth()/3;
        
        images=SetSaveData("where_2_1", images);
        name_dianying=SetSaveName("where_2_1", name_dianying);
        
        addBitmaps(current_page, page_count,images,name_dianying);
        
        btn_xunzhaohaoyou.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				intent.setClass(context, Xunzhaohaoyou.class);
				startActivity(intent);
				
			}
		});
	}
	@Override
	protected void onDestroy() {
		Tools.ClearBitmap(bitmap2);
		super.onDestroy();
	}
	//界面中加载图片
	private void addBitmaps(int pageindex, int pagecount,String img[],String name[]){
		list=Arrays.asList(img);
		LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	try {
    		for(int i = index; i < pagecount * (pageindex + 1)&&i<img.length; i++){
    			try {
    				final View view=inflater.inflate(R.layout.wall, null);
    				RelativeLayout rll = (RelativeLayout)view.findViewById(R.id.RelativeLayout02);
    				final ImageView imageView = (ImageView)view.findViewById(R.id.wall_image);
    				TextView textView = (TextView)view.findViewById(R.id.wall_text);
    				Bitmap bitmap=setImage(imageView, list.get(index));
    				if (bitmap==null) {
    					imageView.setImageResource(R.drawable.pic_bg);
					}
    				else {
    					bitmap2 = BitmapZoom.bitmapZoomByWidth(bitmap, linearlayoutWidth);
    					imageView.setImageBitmap(bitmap2);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(bitmap2.getWidth(), bitmap2.getHeight()+40);
                        layoutParams.setMargins(4, 1, 4, 1);
                        imageView.setLayoutParams(layoutParams);
    					imageView.setImageBitmap(bitmap);
					}
    				textView.setText(name[index]);
    				
    				imageView.setTag(new Integer(index));
    				//点击了影片，用到ontouch方法是为了有点击效果
    				imageView.setOnTouchListener(new OnTouchListener() {
						
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							switch (event.getAction()) {
							case MotionEvent.ACTION_UP:
								int index  =  (Integer)v.getTag();
								getThird_AccessToken.setPicURL(images[index]);
								getThird_AccessToken.setPicName(name_dianying[index]);
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
	//保存URL地址，没网络的情况从内存拿之前保存过的地址来显示图片
	public String[] SetSaveData(String where,String URL[]){
		if (Tools.isNetworkAvailable(context)==false) {
        	getThird_AccessToken.GetImageName(where);
        	String Img_Name=getThird_AccessToken.getIMG_Name();
        	URL=Tools.Split(Img_Name, "$URL$");
        	for (int i = 0; i < URL.length; i++) {
				System.out.println("URL==>"+URL[i]);
			}
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
                msg.what = 1500; 
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
                msg.what = 1501; 
                handler.sendMessage(msg); 
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		},1000);
	}
	//异步加载图片
	public Bitmap setImage(ImageView imageView,String URL){
		return asyncBitmapLoader.loadBitmap(imageView, URL, linearlayoutWidth,new ImageCallBack() {
			
			@Override
			public void imageLoad(ImageView imageView, Bitmap bitmap) {
				if (bitmap!=null) {
					bitmap2 = BitmapZoom.bitmapZoomByWidth(bitmap, linearlayoutWidth);
					imageView.setImageBitmap(bitmap2);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(bitmap2.getWidth(), bitmap2.getHeight()+40);
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
		super.onPause(); 
		MobclickAgent.onPause(this); 
	}
}
