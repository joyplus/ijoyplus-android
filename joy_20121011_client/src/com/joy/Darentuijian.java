package com.joy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.joy.Tools.AsyncImageLoader;
import com.joy.Tools.BitmapZoom;
import com.joy.Tools.AsyncImageLoader.ImageCallback;
import com.joy.view.PullToRefreshView_foot;
import com.joy.view.PullToRefreshView_foot.OnFooterRefreshListener;

import android.app.Activity;
import android.content.Context;
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

public class Darentuijian extends Activity implements OnFooterRefreshListener{
	Context context;
	Button btn_allguanzhu,btn_xiayibu,btn_back;
	private  LinearLayout linearLayout1 = null;
    private  LinearLayout linearLayout2 = null;
    private  LinearLayout linearLayout3 = null;
    private int USE_LINEAR_INTERVAL = 0;
    private int linearlayoutWidth = 0;
	private int page_count = 6;// 每次加载30张图片
	private int current_page = 0;// 当前页数
    private int index =0;
    Bitmap bitmap2;
    GetThird_AccessToken getThird_AccessToken;
    List<String> list;
	private String images[] = {
			"http://i1.cvimage.cn/c/thump/2009/10/09/f5151da65941feb45f3bdefb0928b6da_3_500_100.jpg",
			"http://i1.cvimage.cn/c/thump/2009/10/09/3bd9e4f25d4dd92e8d0d1672a30b2139_3_500_100.jpg",
			"http://www.mobanwang.com/icon/UploadFiles_8971/201009/20100924235742218.png",
			"http://pic4.nipic.com/20091211/3835230_093635079035_2.jpg",
			"http://up.ekoooo.com/uploads2/tubiao/7/20088721564235977804.png",
			"http://www.mobanwang.com/icon/UploadFiles_8971/201009/20100924235740442.png",
			"http://img.article.pchome.net/00/38/18/21/pic_lib/wm/I_like_buttons_001.JPG",
			"http://www.blueidea.com/articleimg/2006/09/4015/01.gif",
			"http://www.85flash.com/Files/BeyondPic/2006-8/1/068118181319408.gif"
			};
	PullToRefreshView_foot mPullToRefreshView;
	AsyncImageLoader asyncImageLoader;
	final Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
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
		setContentView(R.layout.darentuijian);
		getThird_AccessToken=(GetThird_AccessToken)getApplicationContext();
		context = this;
		list=new ArrayList<String>();
		list=Arrays.asList(images);
		asyncImageLoader=new AsyncImageLoader();
		mPullToRefreshView = (PullToRefreshView_foot)findViewById(R.id.darentuijian_main_pull_refresh_view);
        mPullToRefreshView.setOnFooterRefreshListener(this);
        
        linearLayout1 = (LinearLayout)findViewById(R.id.darentuijian_linearlayout1);
        linearLayout2 = (LinearLayout)findViewById(R.id.darentuijian_linearlayout2);
        linearLayout3 = (LinearLayout)findViewById(R.id.darentuijian_linearlayout3);
        linearlayoutWidth =  getWindowManager().getDefaultDisplay().getWidth()/3;
        
        btn_back=(Button)findViewById(R.id.darentuijian_back);
        btn_allguanzhu=(Button)findViewById(R.id.darentuijian_guanzhu);
        btn_xiayibu=(Button)findViewById(R.id.darentuijian_xiayibu);
        if (getThird_AccessToken.getActivitytype().equals("1")) {
        	btn_back.setVisibility(View.GONE);
		}
        else if (getThird_AccessToken.getActivitytype().equals("2")) {
			btn_xiayibu.setVisibility(View.GONE);
		}
        addBitmaps(current_page, page_count);
        
        btn_xiayibu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				intent.setClass(context, JoyActivity.class);
				startActivity(intent);
				finish();
			}
		});
        btn_allguanzhu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
        btn_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void addBitmaps(int pageindex, int pagecount){
		LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	try {
    		for(int i = index; i < pagecount * (pageindex + 1)&&i<images.length; i++){
    			try {
    				final View view=inflater.inflate(R.layout.darenview, null);
    				RelativeLayout rll = (RelativeLayout)view. findViewById(R.id.RelativeLayout03);
    				ImageView imageView = (ImageView)view.findViewById(R.id.darenview_image);
    				TextView textView = (TextView)view.findViewById(R.id.darenview_text);
    				Button button=(Button)view.findViewById(R.id.darenview_button);
    				setViewImage(imageView, list.get(index));
    				textView.setText("第"+(index+1)+"张");
    				button.setId(index*100);
    				imageView.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							int index  =  (Integer)v.getTag();
					    	System.out.println("click index= "+index);
					    	Toast.makeText(context, ""+(index+1), Toast.LENGTH_SHORT).show();
						}
					});
    				button.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							int id=v.getId();
							Toast.makeText(context, ""+(id/100), Toast.LENGTH_SHORT).show();
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
	public void setViewImage(final ImageView v, String url) {
    	asyncImageLoader.loadDrawable(url, new ImageCallback() {
			
			@Override
			public void imageLoaded(Drawable imageDrawable) {
				if(imageDrawable!=null && imageDrawable.getIntrinsicWidth()>0 ) {
	            	BitmapDrawable bd = (BitmapDrawable) imageDrawable;
	            	Bitmap bm = bd.getBitmap();
	            	bitmap2 = BitmapZoom.bitmapZoomByWidth(bm, linearlayoutWidth);
	                v.setImageBitmap(bitmap2);
	                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(bitmap2.getWidth(), bitmap2.getHeight()+40);
	                layoutParams.setMargins(4, 1, 4, 1);
	                v.setLayoutParams(layoutParams);
	            }
	            else {
					v.setImageResource(R.drawable.pic_bg);
				}
			}
		});
    }
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (getThird_AccessToken.getActivitytype().equals("2")) {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	@Override
	public void onFooterRefresh(PullToRefreshView_foot view) {
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
}

