package com.joy;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import com.joy.Tools.AsyncBitmapLoader.ImageCallBack;
import com.joy.Tools.BitmapZoom;
import com.joy.Tools.Tools;
import com.joy.view.PullToRefreshView_foot;
import com.joy.view.PullToRefreshView_foot.OnFooterRefreshListener;
import com.mobclick.android.MobclickAgent;


public class Activity04 extends Activity implements OnFooterRefreshListener{
	private  LinearLayout linearLayout1 = null;
    private  LinearLayout linearLayout2 = null;
    private  LinearLayout linearLayout3 = null;
    private ScrollView	scrollView;
    Button btn_kanguodeyingpian,btn_shoucangdeyingpian,btn_tuijiandeyingpian;
    LinearLayout guanzhu,fensi;
    private int USE_LINEAR_INTERVAL = 0;
    private int linearlayoutWidth = 0;
	private int page_count = 6;// 每次加载x张图片
	private int current_page = 0;// 当前页数
    private int index =0;
    List<String> list;
    public Context context;
    ImageView beijing,head;
    int selectIndex=1;
    String bitString[]={"拍照","相册"};
    private File mCurrentPhotoFile;
    GetThird_AccessToken getThird_AccessToken;
    ProgressDialog progressBar;
	private String images_kanguodeyingpian[] = {
			"http://online.sccnn.com/img2/384/s010.jpg",
			"http://img01.tooopen.com/Product/thumbnails/2009/11/12/x_20091112173541287022.jpg",
			"http://imgsrc.baidu.com/forum/abpic/item/cc4967f0605cd3f87931aac9.jpg",
			"http://img9.nipic.com/20090827/2615908_093838500191_1.jpg",
			"http://online.sccnn.com/img2/818/xfkp_s05.jpg",
			"http://circler.cn.idc.weicent.com/uploads/allimg/110426/2-110426160227-lp.jpg",
			"http://www.circler.cn/uploads/allimg/100526/2-1005261636080-L.jpg",
			"http://web.2008php.com/09_jietu/09-12-16/20091216151830.jpg",
			"http://img7.nipic.com/20090514/1988006_000103186_1.jpg",
			"http://online.sccnn.com/img2/384/s010.jpg",
			"http://img01.tooopen.com/Product/thumbnails/2009/11/12/x_20091112173541287022.jpg",
			"http://imgsrc.baidu.com/forum/abpic/item/cc4967f0605cd3f87931aac9.jpg",
			"http://img9.nipic.com/20090827/2615908_093838500191_1.jpg",
			"http://online.sccnn.com/img2/818/xfkp_s05.jpg",
			"http://circler.cn.idc.weicent.com/uploads/allimg/110426/2-110426160227-lp.jpg",
			"http://www.circler.cn/uploads/allimg/100526/2-1005261636080-L.jpg",
			"http://web.2008php.com/09_jietu/09-12-16/20091216151830.jpg",
			"http://img7.nipic.com/20090514/1988006_000103186_1.jpg",
			"http://online.sccnn.com/img2/384/s010.jpg",
			"http://img01.tooopen.com/Product/thumbnails/2009/11/12/x_20091112173541287022.jpg",
			"http://imgsrc.baidu.com/forum/abpic/item/cc4967f0605cd3f87931aac9.jpg",
			"http://img9.nipic.com/20090827/2615908_093838500191_1.jpg",
			"http://online.sccnn.com/img2/818/xfkp_s05.jpg",
			"http://circler.cn.idc.weicent.com/uploads/allimg/110426/2-110426160227-lp.jpg",
			"http://www.circler.cn/uploads/allimg/100526/2-1005261636080-L.jpg",
			"http://web.2008php.com/09_jietu/09-12-16/20091216151830.jpg",
			"http://img7.nipic.com/20090514/1988006_000103186_1.jpg"
			};
	private String images_shoucangdeyingpian[] = {
			"http://www.circler.cn/uploads/allimg/100905/2-100Z51524380-L.jpg",
			"http://www.circler.cn/uploads/allimg/100823/2-100R31940130-L.jpg",
			"http://circler.cn.idc.weicent.com/uploads/allimg/100324/1-100324232508.jpg",
			"http://www.circler.cn/uploads/allimg/100903/2-100Z3031Q20-L.jpg",
			"http://www.circler.cn/uploads/allimg/110304/2-110304144K9-50-lp.jpg",
			"http://www.circler.cn/uploads/allimg/100329/1-100329153Q30-L.jpg",
			"http://www.circler.cn/uploads/allimg/100726/1-100H61453390-L.jpg",
			"http://www.circler.cn/uploads/allimg/100310/2-1003100101180-L.jpg",
			"http://www.circler.cn/uploads/allimg/100403/1-1004031959480-l.jpg",
			"http://www.circler.cn/uploads/allimg/100905/2-100Z51524380-L.jpg",
			"http://www.circler.cn/uploads/allimg/100823/2-100R31940130-L.jpg",
			"http://circler.cn.idc.weicent.com/uploads/allimg/100324/1-100324232508.jpg",
			"http://www.circler.cn/uploads/allimg/100903/2-100Z3031Q20-L.jpg",
			"http://www.circler.cn/uploads/allimg/110304/2-110304144K9-50-lp.jpg",
			"http://www.circler.cn/uploads/allimg/100329/1-100329153Q30-L.jpg",
			"http://www.circler.cn/uploads/allimg/100726/1-100H61453390-L.jpg",
			"http://www.circler.cn/uploads/allimg/100310/2-1003100101180-L.jpg",
			"http://www.circler.cn/uploads/allimg/100403/1-1004031959480-l.jpg",
			"http://www.circler.cn/uploads/allimg/100905/2-100Z51524380-L.jpg",
			"http://www.circler.cn/uploads/allimg/100823/2-100R31940130-L.jpg",
			"http://circler.cn.idc.weicent.com/uploads/allimg/100324/1-100324232508.jpg",
			"http://www.circler.cn/uploads/allimg/100903/2-100Z3031Q20-L.jpg",
			"http://www.circler.cn/uploads/allimg/110304/2-110304144K9-50-lp.jpg",
			"http://www.circler.cn/uploads/allimg/100329/1-100329153Q30-L.jpg",
			"http://www.circler.cn/uploads/allimg/100726/1-100H61453390-L.jpg",
			"http://www.circler.cn/uploads/allimg/100310/2-1003100101180-L.jpg",
			"http://www.circler.cn/uploads/allimg/100403/1-1004031959480-l.jpg"
			};
	private String images_tuijiandeyingpian[] = {
			"http://www.circler.cn/uploads/allimg/100423/1-1004231602500-l.jpg",
			"http://www.circler.cn/uploads/allimg/110219/1-11021Z004060-L.jpg",
			"http://www.circler.cn/uploads/allimg/100909/2-100ZZ434410-L.jpg",
			"http://www.circler.cn/uploads/allimg/100315/2-1003151633250-l.jpg",
			"http://www.circler.cn/uploads/allimg/100627/1-10062H21F40-L.jpg",
			"http://www.circler.cn/uploads/100322/1-100322121J54D.jpg",
			"http://www.circler.cn/uploads/allimg/100722/1-100H22153240-L.jpg",
			"http://www.circler.cn/uploads/allimg/100419/1-100419215p40-l.jpg",
			"http://www.circler.cn/uploads/allimg/100407/1-10040G319430-L.jpg",
			"http://www.circler.cn/uploads/allimg/100423/1-1004231602500-l.jpg",
			"http://www.circler.cn/uploads/allimg/110219/1-11021Z004060-L.jpg",
			"http://www.circler.cn/uploads/allimg/100909/2-100ZZ434410-L.jpg",
			"http://www.circler.cn/uploads/allimg/100315/2-1003151633250-l.jpg",
			"http://www.circler.cn/uploads/allimg/100627/1-10062H21F40-L.jpg",
			"http://www.circler.cn/uploads/100322/1-100322121J54D.jpg",
			"http://www.circler.cn/uploads/allimg/100722/1-100H22153240-L.jpg",
			"http://www.circler.cn/uploads/allimg/100419/1-100419215p40-l.jpg",
			"http://www.circler.cn/uploads/allimg/100407/1-10040G319430-L.jpg",
			"http://www.circler.cn/uploads/allimg/100423/1-1004231602500-l.jpg",
			"http://www.circler.cn/uploads/allimg/110219/1-11021Z004060-L.jpg",
			"http://www.circler.cn/uploads/allimg/100909/2-100ZZ434410-L.jpg",
			"http://www.circler.cn/uploads/allimg/100315/2-1003151633250-l.jpg",
			"http://www.circler.cn/uploads/allimg/100627/1-10062H21F40-L.jpg",
			"http://www.circler.cn/uploads/100322/1-100322121J54D.jpg",
			"http://www.circler.cn/uploads/allimg/100722/1-100H22153240-L.jpg",
			"http://www.circler.cn/uploads/allimg/100419/1-100419215p40-l.jpg",
			"http://www.circler.cn/uploads/allimg/100407/1-10040G319430-L.jpg"
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
			"看过的影片9",
			"看过的影片10",
			"看过的影片11",
			"看过的影片12",
			"看过的影片13",
			"看过的影片14",
			"看过的影片15",
			"看过的影片16",
			"看过的影片17",
			"看过的影片18",
			"看过的影片19",
			"看过的影片20",
			"看过的影片21",
			"看过的影片22",
			"看过的影片23",
			"看过的影片24",
			"看过的影片25",
			"看过的影片26",
			"看过的影片27"
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
			"收藏的影片9",
			"收藏的影片10",
			"收藏的影片11",
			"收藏的影片12",
			"收藏的影片13",
			"收藏的影片14",
			"收藏的影片15",
			"收藏的影片16",
			"收藏的影片17",
			"收藏的影片18",
			"收藏的影片19",
			"收藏的影片20",
			"收藏的影片21",
			"收藏的影片22",
			"收藏的影片23",
			"收藏的影片24",
			"收藏的影片25",
			"收藏的影片26",
			"收藏的影片27"
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
			"推荐的影片9",
			"推荐的影片10",
			"推荐的影片11",
			"推荐的影片12",
			"推荐的影片13",
			"推荐的影片14",
			"推荐的影片15",
			"推荐的影片16",
			"推荐的影片17",
			"推荐的影片18",
			"推荐的影片19",
			"推荐的影片20",
			"推荐的影片21",
			"推荐的影片22",
			"推荐的影片23",
			"推荐的影片24",
			"推荐的影片25",
			"推荐的影片26",
			"推荐的影片27"
	};
	PullToRefreshView_foot mPullToRefreshView;
	Bitmap BigBitmap;
	AsyncBitmapLoader asyncBitmapLoader=new AsyncBitmapLoader();
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
				getThird_AccessToken.setjujiliebiaoXianshi(1);
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
        scrollView=(ScrollView)findViewById(R.id.act04_sco);
        linearlayoutWidth =  getWindowManager().getDefaultDisplay().getWidth()/3;
        
        images_kanguodeyingpian=SetSaveData("where_4_1", images_kanguodeyingpian);
        name_kanguodeyingpian=SetSaveName("where_4_1", name_kanguodeyingpian);
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
//        	Drawable drawable=new BitmapDrawable(bitmap3);
        	head.setImageBitmap(bitmap3);
        	head.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		}
        addBitmaps(current_page, page_count,images_kanguodeyingpian,name_kanguodeyingpian);
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
		  		  builder.setTitle(getResources().getString(R.string.photostyle)).setItems(bitString, new DialogInterface.OnClickListener() {
					
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
				getThird_AccessToken.setActivitytype("3");
				Intent intent=new Intent();
				intent.setClass(context, Guanzhuderen.class);
				startActivity(intent);
			}
		});
        fensi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getThird_AccessToken.setActivitytype("4");
				Intent intent=new Intent();
				intent.setClass(context, Guanzhuderen.class);
				startActivity(intent);
			}
		});
        btn_kanguodeyingpian.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				selectIndex=1;
				images_kanguodeyingpian=SetSaveData("where_4_1", images_kanguodeyingpian);
				name_kanguodeyingpian=SetSaveName("where_4_1", name_kanguodeyingpian);
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
				images_shoucangdeyingpian=SetSaveData("where_4_2", images_shoucangdeyingpian);
				name_shoucangdeyingpian=SetSaveName("where_4_2", name_shoucangdeyingpian);
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
				images_tuijiandeyingpian=SetSaveData("where_4_3", images_tuijiandeyingpian);
				name_tuijiandeyingpian=SetSaveName("where_4_3", name_tuijiandeyingpian);
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
	//设置按钮
	public void Btn_shezhi(View v){
		Intent intent=new Intent();
		intent.setClass(context, Shezhi.class);
		getThird_AccessToken.setcontext(context);
		startActivity(intent);
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
        	return;
        }
        switch (requestCode) {
        case 1:
        	Bitmap bt=BitmapFactory.decodeResource(getResources(), R.drawable.head);
        	System.out.println(bt.getWidth()+"==============="+bt.getHeight());
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
			in.putExtra("outputX", bt.getWidth());
			in.putExtra("outputY", bt.getHeight());
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
//			    Drawable drawable=new BitmapDrawable(bitmap);
//			    head.setBackgroundDrawable(drawable);
			    head.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			}
			break;
		case 100:
			try {
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
			} catch (Exception e) {
				Toast.makeText(context, getResources().getString(R.string.error_file), Toast.LENGTH_SHORT).show();
			}
			break;
		case 101:
			Bundle extras = data.getExtras();
			if(extras != null ) {
			    Bitmap photo = extras.getParcelable("data");
			    Drawable drawable=new BitmapDrawable(photo);
			    Tools.saveMyBitmap("joy/admin", "bg.png", photo);
			    beijing.setBackgroundDrawable(drawable);
			    beijing.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			}
			break;
		case 200:
			try {
				Bitmap bt1=BitmapFactory.decodeResource(getResources(), R.drawable.head);
	        	System.out.println(bt1.getWidth()+"==============="+bt1.getHeight());
				Intent intent1 = new Intent("com.android.camera.action.CROP");
				intent1.setData(data.getData());     //data是图库选取文件传回的参数
				intent1.putExtra("crop", "true");
				intent1.putExtra("aspectX", 1);
				intent1.putExtra("aspectY", 1);
				intent1.putExtra("outputX", bt1.getWidth());
				intent1.putExtra("outputY", bt1.getHeight());
				intent1.putExtra("noFaceDetection", true);
				intent1.putExtra("return-data", true);
				startActivityForResult(intent1, 201);
			} catch (Exception e) {
				Toast.makeText(context, getResources().getString(R.string.error_file), Toast.LENGTH_SHORT).show();
			}
			break;
		case 201:
			Bundle extras1 = data.getExtras();
			if(extras1 != null ) {
			    Bitmap photo = extras1.getParcelable("data");
			    Bitmap bitmap=Tools.toRoundCorner(photo, 360);
			    Tools.saveMyBitmap("joy/admin", "head.png", bitmap);
//			    Drawable drawable=new BitmapDrawable(bitmap);
			    head.setImageBitmap(bitmap);
			    head.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			}
			break;
		}
        super.onActivityResult(requestCode, resultCode, data);    
    }
	//界面中加载图片
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
    					imageView.setImageBitmap(bitmap);
					}
//    				textView.setText("第"+(index+1)+"张");
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
    				if (index>=img.length) {
    					Toast.makeText(context, getResources().getString(R.string.jiazai), Toast.LENGTH_SHORT).show();
					}
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
					imageView.setImageBitmap(bitmap);
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
