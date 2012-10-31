package com.joy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
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
import com.joy.view.PullToRefreshView_foot;
import com.joy.view.PullToRefreshView_foot.OnFooterRefreshListener;
import com.umeng.analytics.MobclickAgent;


public class OtherPersonActivity extends Activity implements OnFooterRefreshListener{
	private  LinearLayout linearLayout1 = null;
    private  LinearLayout linearLayout2 = null;
    private  LinearLayout linearLayout3 = null;
    private ScrollView	scrollView;
    Button btn_kanguodeyingpian,btn_shoucangdeyingpian,btn_tuijiandeyingpian;
    LinearLayout guanzhu,fensi;
    TextView title;
    private int USE_LINEAR_INTERVAL = 0;
    private int linearlayoutWidth = 0;
	private int page_count = 6;// 每次加载30张图片
	private int current_page = 0;// 当前页数
    private int index =0;
    List<String> list;
    Context context;
    ImageView beijing,head;
    int selectIndex=1;
    Bitmap BigBitmap;
    GetThird_AccessToken getThird_AccessToken;
	private String images_kanguodeyingpian[] = {
			"http://pic1a.nipic.com/20090319/1988006_183521008_2.jpg",
			"http://pic5.nipic.com/20100223/2167235_193708431126_2.jpg",
			"http://pic16.nipic.com/20110917/8341162_184033173385_2.jpg",
			"http://pic6.nipic.com/20100402/4452376_165208012280_2.jpg",
			"http://www.sc126.com/uploads/shows/10/201107061033236479.jpg",
			"http://pic12.nipic.com/20110116/479029_224945625156_2.jpg",
			"http://pic14.nipic.com/20110511/3270139_162530239384_2.jpg",
			"http://pic5.nipic.com/20100126/2167235_040949808618_2.jpg",
			"http://pic4.nipic.com/20091213/3805676_123138496063_2.jpg"
			};
	private String images_shoucangdeyingpian[] = {
			"http://www.tradeduck.com/uploads/60904_10771262.jpg",
			"http://imgsrc.baidu.com/baike/pic/item/a583631e26c3c4b81ad57602.jpg",
			"http://imgsrc.baidu.com/forum/abpic/item/bf41aac3902cc747b319a8cc.jpg",
			"http://imgsrc.baidu.com/forum/abpic/item/02f2d7cab9d2766df21fe722.jpg",
			"http://imgsrc.baidu.com/forum/abpic/item/0ba9b044ed5dd3b9b3b7dc7d.jpg",
			"http://a2.att.hudong.com/62/60/01300001066391129128609720365.jpg",
			"http://img02.bibimai.com/product_big/42/98/40/5429840.jpg",
			"http://circler.cn.idc.weicent.com/uploads/allimg/110304/2-1103042000040-L.jpg",
			"http://imgsrc.baidu.com/baike/pic/item/6648d73da427ed8c9e3d6211.jpg"
			};
	private String images_tuijiandeyingpian[] = {
			"http://imgsrc.baidu.com/baike/pic/item/c856613eb5e11f7671cf6c11.jpg",
			"http://imgsrc.baidu.com/forum/abpic/item/cc3f0938e3ba62dfb311c7ee.jpg",
			"http://imgsrc.baidu.com/baike/pic/item/964b2e4ee9f74381d0c86a11.jpg",
			"http://imgsrc.baidu.com/baike/pic/item/cc506c8b9bf86f59c9fc7a11.jpg",
			"http://imgsrc.baidu.com/baike/pic/item/245e8bca1f2071cdc8176811.jpg",
			"http://imgsrc.baidu.com/baike/pic/item/d041a4a117f9a4c046106411.jpg",
			"http://imgsrc.baidu.com/baike/pic/item/f7246b600c33874436f27735510fd9f9d72aa040.jpg",
			"http://www.circler.cn/uploads/allimg/100113/1-100113232T1-lp.jpg",
			"http://s1.it.itc.cn/z/forum_attachment/day_090819/09081912021d7322eaa70d6878.jpg"
			};
	private String name_kanguodeyingpian[] = {
			"看过的影片1",
			"看过的影片2",
			"看过的影片3",
			"看过的影片4",
			"看过的影片5",
			"看过的影片6",
			"看过的影片7",
			"看过的影片8",
			"看过的影片9"
	};
	private String name_shoucangdeyingpian[] = {
			"收藏的影片1",
			"收藏的影片2",
			"收藏的影片3",
			"收藏的影片4",
			"收藏的影片5",
			"收藏的影片6",
			"收藏的影片7",
			"收藏的影片8",
			"收藏的影片9"
	};
	private String name_tuijiandeyingpian[] = {
			"推荐的影片1",
			"推荐的影片2",
			"推荐的影片3",
			"推荐的影片4",
			"推荐的影片5",
			"推荐的影片6",
			"推荐的影片7",
			"推荐的影片8",
			"推荐的影片9"
	};
	ProgressDialog progressBar;
	PullToRefreshView_foot mPullToRefreshView;
	Bitmap mBitmap;
	AsyncBitmapLoader asyncBitmapLoader;
	final Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				addBitmaps(++current_page, page_count,images_kanguodeyingpian,name_kanguodeyingpian);
				break;
			case 2:
				addBitmaps(++current_page, page_count,images_shoucangdeyingpian,name_shoucangdeyingpian);
				break;
			case 3:
				addBitmaps(++current_page, page_count,images_tuijiandeyingpian,name_tuijiandeyingpian);
				break;
			case 11:
				linearLayout1.removeAllViews();
				linearLayout2.removeAllViews();
				linearLayout3.removeAllViews();
				Tools.ClearBitmap(BigBitmap);
				current_page=0;
				index=0;
				scrollView.fullScroll(ScrollView.FOCUS_UP);
				addBitmaps(current_page, page_count,images_kanguodeyingpian,name_kanguodeyingpian);
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
				addBitmaps(current_page, page_count,images_shoucangdeyingpian,name_shoucangdeyingpian);
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
				addBitmaps(current_page, page_count,images_tuijiandeyingpian,name_tuijiandeyingpian);
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
		setContentView(R.layout.oherperson);
		getThird_AccessToken=(GetThird_AccessToken)getApplicationContext();
		context=this;
		asyncBitmapLoader=new AsyncBitmapLoader();
		Tools.creat("joy/admin");
		mPullToRefreshView = (PullToRefreshView_foot)findViewById(R.id.oherperson_main_pull_refresh_view);
//		mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setOnFooterRefreshListener(this);
		
        list=new ArrayList<String>();
        
        title=(TextView)findViewById(R.id.oherperson_title);
        beijing=(ImageView)findViewById(R.id.oherperson_beijing);
        head=(ImageView)findViewById(R.id.oherperson_hand);
        guanzhu = (LinearLayout)findViewById(R.id.oherperson_guanzhu);
        fensi= (LinearLayout)findViewById(R.id.oherperson_fensi);
        btn_kanguodeyingpian=(Button)findViewById(R.id.oherperson_kanguodeyingpian);
        btn_kanguodeyingpian.setEnabled(false);
        btn_kanguodeyingpian.setBackgroundResource(R.drawable.topleft1);
        btn_shoucangdeyingpian=(Button)findViewById(R.id.oherperson_shoucangdeyingpian);
        btn_tuijiandeyingpian=(Button)findViewById(R.id.oherperson_tuijiandeyingpian);
		linearLayout1 = (LinearLayout)findViewById(R.id.oherperson_linearlayout1);
        linearLayout2 = (LinearLayout)findViewById(R.id.oherperson_linearlayout2);
        linearLayout3 = (LinearLayout)findViewById(R.id.oherperson_linearlayout3);
        scrollView=(ScrollView)findViewById(R.id.other_sco);
        linearlayoutWidth =  getWindowManager().getDefaultDisplay().getWidth()/3;
        title.setText("Name");//对方的名字
        
        images_kanguodeyingpian=SetSaveData("where_5_1", images_kanguodeyingpian);
        name_kanguodeyingpian=SetSaveName("where_5_1", name_kanguodeyingpian);
        addBitmaps(current_page, page_count,images_kanguodeyingpian,name_kanguodeyingpian);
        
        guanzhu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "关注", Toast.LENGTH_SHORT).show();
			}
		});
        fensi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "粉丝", Toast.LENGTH_SHORT).show();
			}
		});
        btn_kanguodeyingpian.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				selectIndex=1;
				images_kanguodeyingpian=SetSaveData("where_5_1", images_kanguodeyingpian);
				name_kanguodeyingpian=SetSaveName("where_5_1", name_kanguodeyingpian);
				btn_kanguodeyingpian.setEnabled(false);
				btn_shoucangdeyingpian.setEnabled(true);
				btn_tuijiandeyingpian.setEnabled(true);
				btn_kanguodeyingpian.setBackgroundResource(R.drawable.topleft1);
				btn_shoucangdeyingpian.setBackgroundResource(R.drawable.topbarmid);
				btn_tuijiandeyingpian.setBackgroundResource(R.drawable.topbarright);
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
        btn_shoucangdeyingpian.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				selectIndex=2;
				images_shoucangdeyingpian=SetSaveData("where_5_2", images_shoucangdeyingpian);
				name_shoucangdeyingpian=SetSaveName("where_5_2", name_shoucangdeyingpian);
				btn_kanguodeyingpian.setEnabled(true);
				btn_shoucangdeyingpian.setEnabled(false);
				btn_tuijiandeyingpian.setEnabled(true);
				btn_kanguodeyingpian.setBackgroundResource(R.drawable.topleft);
				btn_shoucangdeyingpian.setBackgroundResource(R.drawable.topbarmid1);
				btn_tuijiandeyingpian.setBackgroundResource(R.drawable.topbarright);
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
        btn_tuijiandeyingpian.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				selectIndex=3;
				images_tuijiandeyingpian=SetSaveData("where_5_3", images_tuijiandeyingpian);
				name_tuijiandeyingpian=SetSaveName("where_5_3", name_tuijiandeyingpian);
				btn_kanguodeyingpian.setEnabled(true);
				btn_shoucangdeyingpian.setEnabled(true);
				btn_tuijiandeyingpian.setEnabled(false);
				btn_kanguodeyingpian.setBackgroundResource(R.drawable.topleft);
				btn_shoucangdeyingpian.setBackgroundResource(R.drawable.topbarmid);
				btn_tuijiandeyingpian.setBackgroundResource(R.drawable.topbarright1);
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

	}
	@Override
	protected void onDestroy() {
		Tools.ClearBitmap(BigBitmap);
		super.onDestroy();
	}
	//返回按钮
	public void Btn_oherperson_back(View v){
		/*Intent intent = new Intent();
		intent.setClass(context, ReplyActivity.class);
		startActivity(intent);*/
		finish();
	}
	//关注按钮
	public void Btn_oherperson_guanzhu(View v){
		
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
	//添加主界面中的图片
	private void addBitmaps(int pageindex, int pagecount,String img[],String name[]){
		list=Arrays.asList(img);
		LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	try {
    		for(int i = index; i < pagecount * (pageindex + 1)&&i<img.length; i++){
    			try {
    				final View view=inflater.inflate(R.layout.wall, null);
    				RelativeLayout rll = (RelativeLayout)view. findViewById(R.id.RelativeLayout02);
    				final ImageView imageView = (ImageView)view.findViewById(R.id.wall_image);
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
								switch (selectIndex) {
								case 1:
									getThird_AccessToken.setPicURL(images_kanguodeyingpian[index]);
									getThird_AccessToken.setPicName(name_kanguodeyingpian[index]);
									break;
								case 2:
									getThird_AccessToken.setPicURL(images_shoucangdeyingpian[index]);
									getThird_AccessToken.setPicName(name_shoucangdeyingpian[index]);
									break;
								case 3:
									getThird_AccessToken.setPicURL(images_tuijiandeyingpian[index]);
									getThird_AccessToken.setPicName(name_tuijiandeyingpian[index]);
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
	        	String Img_URL=getThird_AccessToken.getIMG_Name();
	        	URL=Tools.Split(Img_URL, "$URL$");
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
	public void onFooterRefresh(PullToRefreshView_foot view) {
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
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
