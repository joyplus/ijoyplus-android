package com.joy;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
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


public class Activity04 extends Activity implements OnFooterRefreshListener{
	private  LinearLayout linearLayout1 = null;
    private  LinearLayout linearLayout2 = null;
    private  LinearLayout linearLayout3 = null;
    Button btn_kanguodeyingpian,btn_shoucangdeyingpian,btn_tuijiandeyingpian;
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
    private File mCurrentPhotoFile;
    GetThird_AccessToken getThird_AccessToken;
	private String images_kanguodeyingpian[] = {
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
	private String images_shoucangdeyingpian[] = {
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
	private String images_tuijiandeyingpian[] = {
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
	PullToRefreshView_foot mPullToRefreshView;
	Bitmap BigBitmap;
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
		setContentView(R.layout.activity04);
		context=this;
		getThird_AccessToken=(GetThird_AccessToken)getApplicationContext();
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
        btn_kanguodeyingpian.setBackgroundResource(R.drawable.topleft1);
        btn_shoucangdeyingpian=(Button)findViewById(R.id.act04_shoucangdeyingpian);
        btn_tuijiandeyingpian=(Button)findViewById(R.id.act04_tuijiandeyingpian);
		linearLayout1 = (LinearLayout)findViewById(R.id.act04_linearlayout1);
        linearLayout2 = (LinearLayout)findViewById(R.id.act04_linearlayout2);
        linearLayout3 = (LinearLayout)findViewById(R.id.act04_linearlayout3);
        linearlayoutWidth =  getWindowManager().getDefaultDisplay().getWidth()/3;
        
        images_kanguodeyingpian=SetSaveData("where_4_1", images_kanguodeyingpian);
        addBitmaps(current_page, page_count,images_kanguodeyingpian);
        String path_bg=Environment.getExternalStorageDirectory()+"/joy/admin/bg.png";
        String path_head=Environment.getExternalStorageDirectory()+"/joy/admin/head.png";
        BitmapFactory.Options opts = new BitmapFactory.Options();  
        opts.inSampleSize = 1;  
        Bitmap bitmap = BitmapFactory.decodeFile(path_bg, opts);  
        
        BitmapFactory.Options opts1 = new BitmapFactory.Options();  
        opts1.inSampleSize = 1;  
        Bitmap bitmap2 = BitmapFactory.decodeFile(path_head, opts1);  
        if (bitmap!=null) {
        	Drawable drawable=new BitmapDrawable(bitmap);
        	beijing.setBackgroundDrawable(drawable);
        	beijing.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		}
        if (bitmap2!=null) {
        	Bitmap bitmap3=Tools.toRoundCorner(bitmap2, 360);
        	head.setImageBitmap(bitmap3);
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
							Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
							mCurrentPhotoFile = new File("mnt/sdcard/joy/admin/","head.png");
							intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhotoFile));
							startActivityForResult(intent,Activity.DEFAULT_KEYS_DIALER);
						}
						else {
							Intent intent1=new Intent();
							intent1.setType("image/*");
							intent1.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intent1,200);
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
				images_kanguodeyingpian=SetSaveData("where_4_1", images_kanguodeyingpian);
				btn_kanguodeyingpian.setEnabled(false);
				btn_shoucangdeyingpian.setEnabled(true);
				btn_tuijiandeyingpian.setEnabled(true);
				btn_kanguodeyingpian.setBackgroundResource(R.drawable.topleft1);
				btn_shoucangdeyingpian.setBackgroundResource(R.drawable.topbarmid);
				btn_tuijiandeyingpian.setBackgroundResource(R.drawable.topbarright);
				linearLayout1.removeAllViews();
				linearLayout2.removeAllViews();
				linearLayout3.removeAllViews();
				Tools.ClearBitmap(BigBitmap);
				current_page=0;
				index=0;
				addBitmaps(current_page, page_count,images_kanguodeyingpian);
			}
		});
        btn_shoucangdeyingpian.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				selectIndex=2;
				images_shoucangdeyingpian=SetSaveData("where_4_2", images_shoucangdeyingpian);
				btn_kanguodeyingpian.setEnabled(true);
				btn_shoucangdeyingpian.setEnabled(false);
				btn_tuijiandeyingpian.setEnabled(true);
				btn_kanguodeyingpian.setBackgroundResource(R.drawable.topleft);
				btn_shoucangdeyingpian.setBackgroundResource(R.drawable.topbarmid1);
				btn_tuijiandeyingpian.setBackgroundResource(R.drawable.topbarright);
				linearLayout1.removeAllViews();
				linearLayout2.removeAllViews();
				linearLayout3.removeAllViews();
				Tools.ClearBitmap(BigBitmap);
				current_page=0;
				index=0;
				addBitmaps(current_page, page_count,images_shoucangdeyingpian);
			}
		});
        btn_tuijiandeyingpian.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				selectIndex=3;
				images_tuijiandeyingpian=SetSaveData("where_4_3", images_tuijiandeyingpian);
				btn_kanguodeyingpian.setEnabled(true);
				btn_shoucangdeyingpian.setEnabled(true);
				btn_tuijiandeyingpian.setEnabled(false);
				btn_kanguodeyingpian.setBackgroundResource(R.drawable.topleft);
				btn_shoucangdeyingpian.setBackgroundResource(R.drawable.topbarmid);
				btn_tuijiandeyingpian.setBackgroundResource(R.drawable.topbarright1);
				linearLayout1.removeAllViews();
				linearLayout2.removeAllViews();
				linearLayout3.removeAllViews();
				Tools.ClearBitmap(BigBitmap);
				current_page=0;
				index=0;
				addBitmaps(current_page, page_count,images_tuijiandeyingpian);
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
        case 1:
			Uri imgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
			ContentResolver cr = context.getContentResolver();

			Uri fileUri = Uri.fromFile(mCurrentPhotoFile);
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
					fileUri));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Cursor cursor = cr.query(imgUri, null,MediaStore.Images.Media.DISPLAY_NAME + "='"+ mCurrentPhotoFile.getName() + "'",null, null);
			Uri uri = null;
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToLast();
				long id = cursor.getLong(0);
				uri = ContentUris.withAppendedId(imgUri, id);
			}
			Intent in = new Intent("com.android.camera.action.CROP");
			in.setDataAndType(uri, "image/*");
			in.putExtra("crop", "true");
			in.putExtra("aspectX", 1);
			in.putExtra("aspectY", 1);
			in.putExtra("outputX", getWindowManager().getDefaultDisplay().getWidth()/5);
			in.putExtra("outputY", getWindowManager().getDefaultDisplay().getWidth()/5);
			in.putExtra("return-data", true);
			startActivityForResult(in, 3);
			break;
        case 3:
        	Bundle ex = data.getExtras();
        	if(ex != null ) {
        		Bitmap photo = ex.getParcelable("data");
			    Bitmap bitmap=Tools.toRoundCorner(photo, 360);
			    Tools.saveMyBitmap("joy/admin", "head.png", bitmap);
			    head.setImageBitmap(bitmap);
			}
			break;
		case 100:
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
		case 200:
        	Intent intent1 = new Intent("com.android.camera.action.CROP");
			intent1.setData(data.getData());     //data是图库选取文件传回的参数
			intent1.putExtra("crop", "true");
			intent1.putExtra("aspectX", 1);
			intent1.putExtra("aspectY", 1);
			intent1.putExtra("outputX", getWindowManager().getDefaultDisplay().getWidth()/5);
			intent1.putExtra("outputY", getWindowManager().getDefaultDisplay().getWidth()/5);
			intent1.putExtra("noFaceDetection", true);
			intent1.putExtra("return-data", true);
			startActivityForResult(intent1, 201);
			break;
		case 201:
			Bundle extras1 = data.getExtras();
			if(extras1 != null ) {
			    Bitmap photo = extras1.getParcelable("data");
			    Bitmap bitmap=Tools.toRoundCorner(photo, 360);
			    Tools.saveMyBitmap("joy/admin", "head.png", bitmap);
			    head.setImageBitmap(bitmap);
//			    head.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
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
	public Bitmap setImage(ImageView imageView,String URL){
		return asyncBitmapLoader.loadBitmap(imageView, URL, new ImageCallBack() {
			
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
