package com.joy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.joy.Tools.AsyncBitmapLoader;
import com.joy.Tools.BitmapZoom;
import com.joy.Tools.Tools;
import com.joy.Tools.AsyncBitmapLoader.ImageCallBack;
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
	Button btn_allguanzhu,btn_xiayibu;
	RelativeLayout btn_back;;
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
	AsyncBitmapLoader asyncBitmapLoader=new AsyncBitmapLoader();
	final Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1500:
				addBitmaps(++current_page, page_count,images);
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
		
		mPullToRefreshView = (PullToRefreshView_foot)findViewById(R.id.darentuijian_main_pull_refresh_view);
        mPullToRefreshView.setOnFooterRefreshListener(this);
        
        linearLayout1 = (LinearLayout)findViewById(R.id.darentuijian_linearlayout1);
        linearLayout2 = (LinearLayout)findViewById(R.id.darentuijian_linearlayout2);
        linearLayout3 = (LinearLayout)findViewById(R.id.darentuijian_linearlayout3);
        linearlayoutWidth =  getWindowManager().getDefaultDisplay().getWidth()/3;
        
        btn_back=(RelativeLayout)findViewById(R.id.darentuijian_back);
        btn_allguanzhu=(Button)findViewById(R.id.darentuijian_guanzhu);
        btn_xiayibu=(Button)findViewById(R.id.darentuijian_xiayibu);
        if (getThird_AccessToken.getActivitytype().equals("1")) {
        	btn_back.setVisibility(View.GONE);
		}
        else if (getThird_AccessToken.getActivitytype().equals("2")) {
			btn_xiayibu.setVisibility(View.GONE);
		}
        
        images=SetSaveData("where_daren", images);
        addBitmaps(current_page, page_count,images);
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

	private void addBitmaps(int pageindex, int pagecount,String img[]){
		list=Arrays.asList(img);
		LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	try {
    		for(int i = index; i < pagecount * (pageindex + 1)&&i<img.length; i++){
    			try {
    				final View view=inflater.inflate(R.layout.darenview, null);
    				RelativeLayout rll = (RelativeLayout)view. findViewById(R.id.RelativeLayout03);
    				ImageView imageView = (ImageView)view.findViewById(R.id.darenview_image);
    				TextView textView = (TextView)view.findViewById(R.id.darenview_text);
    				Button button=(Button)view.findViewById(R.id.darenview_button);
    				Bitmap bitmap=asyncBitmapLoader.loadBitmap(imageView, list.get(index), new ImageCallBack() {  
  	                  
    	                @Override  
    	                public void imageLoad(ImageView imageView, Bitmap bitmap) {  
    	                	Bitmap bitmap2 = BitmapZoom.bitmapZoomByWidth(bitmap, linearlayoutWidth);
        					imageView.setImageBitmap(bitmap2);
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(bitmap2.getWidth(), bitmap2.getHeight()+40);
                            layoutParams.setMargins(4, 1, 4, 1);
                            imageView.setLayoutParams(layoutParams);
        					imageView.setImageBitmap(bitmap);
    	                }  
    	            });  
    				if (bitmap==null) {
    					imageView.setImageResource(R.drawable.pic_bg);
					}
    				else {
    					Bitmap bitmap2 = BitmapZoom.bitmapZoomByWidth(bitmap, linearlayoutWidth);
    					imageView.setImageBitmap(bitmap2);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(bitmap2.getWidth(), bitmap2.getHeight()+40);
                        layoutParams.setMargins(4, 1, 4, 1);
                        imageView.setLayoutParams(layoutParams);
    					imageView.setImageBitmap(bitmap);
					}
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

