package com.joy;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.joy.Tools.AsyncBitmapLoader;
import com.joy.Tools.Tools;
import com.joy.Tools.BitmapZoom;
import com.joy.Tools.AsyncBitmapLoader.ImageCallBack;
import com.joy.view.PullToRefreshView_foot;
import com.joy.view.PullToRefreshView_foot.OnFooterRefreshListener;


public class OtherPersonActivity extends Activity implements OnFooterRefreshListener{
	private  LinearLayout linearLayout1 = null;
    private  LinearLayout linearLayout2 = null;
    private  LinearLayout linearLayout3 = null;
    Button btn_kanguodeyingpian,btn_shoucangdeyingpian,btn_tuijiandeyingpian,login_goback;
    LinearLayout guanzhu,fensi;
    private int USE_LINEAR_INTERVAL = 0;
    private int linearlayoutWidth = 0;
	private int page_count = 6;// 每次加载30张图片
	private int current_page = 0;// 当前页数
    private int index =0;
    List<String> list;
    Context context;
    ImageView beijing,head;
    int selectIndex=1;
    String bitString[]={"拍照","相册"};
    Bitmap bitmap2;
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
			"http://img2.mtime.com/mg/2007/37/c452c701-5159-4605-b8d5-7e4662c116a7.jpg",
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
			"http://imgsrc.baidu.com/baike/pic/item/0d338744ebf81a4c8e05b480d72a6059252da640.jpg",
			"http://s1.it.itc.cn/z/forum_attachment/day_090819/09081912021d7322eaa70d6878.jpg"
			};
	PullToRefreshView_foot mPullToRefreshView;
	Bitmap mBitmap;
	AsyncBitmapLoader asyncBitmapLoader=new AsyncBitmapLoader();
	final Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				addBitmaps(++current_page, page_count,images_kanguodeyingpian);
				break;
			case 2:
				addBitmaps(++current_page, page_count,images_shoucangdeyingpian);
				break;
			case 3:
				addBitmaps(++current_page, page_count,images_tuijiandeyingpian);
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.oherperson);
		context=this;
		Tools.creat("joy/admin");
		mPullToRefreshView = (PullToRefreshView_foot)findViewById(R.id.act04_main_pull_refresh_view);
//		mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setOnFooterRefreshListener(this);
		
        list=new ArrayList<String>();
        
        beijing=(ImageView)findViewById(R.id.act04_beijing);
        head=(ImageView)findViewById(R.id.act04_hand);
        guanzhu = (LinearLayout)findViewById(R.id.act04_guanzhu);
        fensi= (LinearLayout)findViewById(R.id.act04_fensi);
        btn_kanguodeyingpian=(Button)findViewById(R.id.act04_kanguodeyingpian);
        btn_kanguodeyingpian.setEnabled(false);
        btn_shoucangdeyingpian=(Button)findViewById(R.id.act04_shoucangdeyingpian);
        btn_tuijiandeyingpian=(Button)findViewById(R.id.act04_tuijiandeyingpian);
        login_goback = (Button) findViewById(R.id.login_goback);
		linearLayout1 = (LinearLayout)findViewById(R.id.act04_linearlayout1);
        linearLayout2 = (LinearLayout)findViewById(R.id.act04_linearlayout2);
        linearLayout3 = (LinearLayout)findViewById(R.id.act04_linearlayout3);
        linearlayoutWidth =  getWindowManager().getDefaultDisplay().getWidth()/3;
        addBitmaps(current_page, page_count,images_kanguodeyingpian);
        String path=Environment.getExternalStorageDirectory()+"/joy/admin/bg.png";
        Bitmap mBitmap=BitmapFactory.decodeFile(path);
        if (mBitmap!=null) {
        	Drawable drawable=new BitmapDrawable(mBitmap);
        	beijing.setBackgroundDrawable(drawable);
        	beijing.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		}
        beijing.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent1=new Intent();
				intent1.setType("image/*");
				intent1.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent1,100);
			}
		});
        head.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder=new AlertDialog.Builder(context);
		  		  builder.setTitle("选择照片类型").setItems(bitString, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which==0){
							Toast.makeText(context, "拍照", Toast.LENGTH_SHORT).show();
						}
						else {
							Toast.makeText(context, "相册", Toast.LENGTH_SHORT).show();
						}
					}
				});
					AlertDialog ad = builder.create();
					ad.show();
			}
		});
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
				btn_kanguodeyingpian.setEnabled(false);
				btn_shoucangdeyingpian.setEnabled(true);
				btn_tuijiandeyingpian.setEnabled(true);
				linearLayout1.removeAllViews();
				linearLayout2.removeAllViews();
				linearLayout3.removeAllViews();
				current_page=0;
				index=0;
				addBitmaps(current_page, page_count,images_kanguodeyingpian);
			}
		});
        btn_shoucangdeyingpian.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				selectIndex=2;
				btn_kanguodeyingpian.setEnabled(true);
				btn_shoucangdeyingpian.setEnabled(false);
				btn_tuijiandeyingpian.setEnabled(true);
				linearLayout1.removeAllViews();
				linearLayout2.removeAllViews();
				linearLayout3.removeAllViews();
				current_page=0;
				index=0;
				addBitmaps(current_page, page_count,images_shoucangdeyingpian);
			}
		});
        btn_tuijiandeyingpian.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				selectIndex=3;
				btn_kanguodeyingpian.setEnabled(true);
				btn_shoucangdeyingpian.setEnabled(true);
				btn_tuijiandeyingpian.setEnabled(false);
				linearLayout1.removeAllViews();
				linearLayout2.removeAllViews();
				linearLayout3.removeAllViews();
				current_page=0;
				index=0;
				addBitmaps(current_page, page_count,images_tuijiandeyingpian);
			}
		});
        login_goback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, ReplyActivity.class);
				startActivity(intent);
				finish();
			}
		});

	}
	public void Btn_shezhi(View v){
		Intent intent=new Intent();
		intent.setClass(context, Shezhi.class);
		startActivity(intent);
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
        	return;
        }
        switch (requestCode) {
		case 100:
			Uri uri = data.getData();
        	ContentResolver cr = this.getContentResolver();    
        	Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setData(data.getData());     //data是图库选取文件传回的参数
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 2);
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", getWindowManager().getDefaultDisplay().getWidth());
			intent.putExtra("outputY", getWindowManager().getDefaultDisplay().getWidth()/2);
			intent.putExtra("noFaceDetection", true);
			intent.putExtra("return-data", true);
			startActivityForResult(intent, 101);
			break;
		case 101:
			Bundle extras = data.getExtras();
			if(extras != null ) {
			    Bitmap photo = extras.getParcelable("data");
//			    Bitmap bitmap=BitmapZoom.bitmapZoomByWidth(photo, getWindowManager().getDefaultDisplay().getWidth());
			    Drawable drawable=new BitmapDrawable(photo);
			    Tools.saveMyBitmap("joy/admin", "bg.png", photo);
			    beijing.setBackgroundDrawable(drawable);
			    beijing.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			}
			break;
		}
        super.onActivityResult(requestCode, resultCode, data);    
    }
	private void addBitmaps(int pageindex, int pagecount,String img[]){
		list=Arrays.asList(img);
		LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	try {
    		for(int i = index; i < pagecount * (pageindex + 1)&&i<img.length; i++){
    			try {
    				final View view=inflater.inflate(R.layout.wall, null);
    				RelativeLayout rll = (RelativeLayout)view. findViewById(R.id.RelativeLayout02);
    				ImageView imageView = (ImageView)view.findViewById(R.id.wall_image);
    				TextView textView = (TextView)view.findViewById(R.id.wall_text);
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
					    	finish();
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
