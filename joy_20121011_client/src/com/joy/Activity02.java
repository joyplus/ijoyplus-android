package com.joy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.joy.Tools.AsyncImageLoader;
import com.joy.Tools.BitmapZoom;
import com.joy.Tools.AsyncImageLoader.ImageCallback;
import com.joy.view.PullToRefreshView;
import com.joy.view.PullToRefreshView.OnFooterRefreshListener;
import com.joy.view.PullToRefreshView.OnHeaderRefreshListener;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
	private int page_count = 6;// 每次加载30张图片
	private int current_page = 0;// 当前页数
    private int index =0;
    List<String> list;
    Context context;
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
	AsyncImageLoader asyncImageLoader;
	final Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			System.out.println("msg.what===========>"+msg.what);
			switch (msg.what) {
			case 1500:
				addBitmaps(++current_page, page_count);
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity02);
		context=this;
		asyncImageLoader=new AsyncImageLoader();
		list=new ArrayList<String>();
		list=Arrays.asList(images);
		mPullToRefreshView = (PullToRefreshView)findViewById(R.id.act02_main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setOnFooterRefreshListener(this);
        
        btn_xunzhaohaoyou=(Button)findViewById(R.id.act02_xunzhaohaoyou);
		linearLayout1 = (LinearLayout)findViewById(R.id.act02_linearlayout1);
        linearLayout2 = (LinearLayout)findViewById(R.id.act02_linearlayout2);
        linearLayout3 = (LinearLayout)findViewById(R.id.act02_linearlayout3);
        linearlayoutWidth =  getWindowManager().getDefaultDisplay().getWidth()/3;
        addBitmaps(current_page, page_count);
        
        btn_xunzhaohaoyou.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				intent.setClass(context, Xunzhaohaoyou.class);
				startActivity(intent);
				
			}
		});
	}
	private void addBitmaps(int pageindex, int pagecount){
		LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	try {
    		for(int i = index; i < pagecount * (pageindex + 1)&&i<images.length; i++){
    			try {
    				final View view=inflater.inflate(R.layout.wall, null);
    				RelativeLayout rll = (RelativeLayout)view.findViewById(R.id.RelativeLayout02);
    				ImageView imageView = (ImageView)view.findViewById(R.id.wall_image);
    				TextView textView = (TextView)view.findViewById(R.id.wall_text);
    				setimage(imageView, list.get(index));
    				textView.setText("第"+(index+1)+"张");
    				imageView.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							int index  =  (Integer)v.getTag();
					    	System.out.println("click index= "+index);
					    	Toast.makeText(context, ""+(index+1), Toast.LENGTH_SHORT).show();
						}
					});
    				imageView.setTag(new Integer(index));
    				imageView.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							int index  =  (Integer)v.getTag();
					    	System.out.println("click index= "+index);
					    	Toast.makeText(context, ""+(index+1), Toast.LENGTH_SHORT).show();
					    	Intent intent = new Intent();
					    	intent.setClass(context, DetailActivity.class);
					    	startActivity(intent);
					    	//finish();
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
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		},1000);
	}
	public void setimage(final ImageView v, String url){
		asyncImageLoader.loadDrawable(url, new ImageCallback() {
			
			@Override
			public void imageLoaded(Drawable imageDrawable) {
				if (imageDrawable!=null&&imageDrawable.getIntrinsicWidth()>0) {
					BitmapDrawable bd = (BitmapDrawable) imageDrawable;
                	Bitmap bm = bd.getBitmap();
                	Bitmap bitmap2 = BitmapZoom.bitmapZoomByWidth(bm, linearlayoutWidth);
                    v.setImageBitmap(bitmap2);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(bitmap2.getWidth(), bitmap2.getHeight()+40);
                    layoutParams.setMargins(4, 1, 4, 1);
                    v.setLayoutParams(layoutParams);
				}else {
					v.setImageResource(R.drawable.pic_bg);
				}
			}
		});
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
