package com.joy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.joy.Tools.*;
import com.joy.Tools.AsyncBitmapLoader.ImageCallBack;
import com.mobclick.android.MobclickAgent;

public class ReplyActivity extends Activity{
	Context context;
	EditText reply_editText;//输入框
	Button reply_button;//回复按键
	RelativeLayout login_goback;//返回键
	ScrollView reply_listview;
	SimpleAdapter adapter;
	LinearLayout reply_linearlayout;
	//评论用户的id
	String user_name[]={"张3","张4","张5","张6","张7","张8","张9","张0"};
	//评论内容
	String user_content[]={"夏日炎炎",
			"马上要光棍节了",
			"星期八小镇，3岁至15岁孩子玩角色扮演",
			"当我还是年轻的时候妈妈告诉我，其实每个人都是super star",
			"上帝之手没有瑕疵",
			"今晚饮得尽兴啊，听日休息，可以训大觉",
			"哥是好爷们，铁血真汉子，不需要备胎",
			"清理QQ好友时发现某个三年没动静的好友。最后一条签名是：自从买了保险，过马路再也不用左右看了..."};  
	//评论时间
	String user_time[] = {"12:45","12:46","12:47","12:48","12:49","12:50","12:51","12:52"};
    GetThird_AccessToken getThird_AccessToken;
    Bitmap bitmap_user;
    AsyncBitmapLoader asyncBitmapLoader2=new AsyncBitmapLoader();
    private String images[] = {
			"http://www.qqtai.com/qqhead/UploadFiles_3178/200901/2009011503573742.jpg",
			"http://www.qqtai.com/qqhead/uploadfiles_3178/200901/2009011503573886.jpg",
			"http://www.qqtai.com/qqhead/UploadFiles_3178/200901/2009011503573759.jpg",
			"http://www.2qqtouxiang.cn/uploads/allimg/110903/1_110903203627_14.jpg",
			"http://www.2qqtouxiang.cn/uploads/allimg/110903/1_110903203627_6.jpg",
			"http://www.2qqtouxiang.cn/uploads/allimg/110903/1_110903203627_7.jpg",
			"http://www.2qqtouxiang.cn/uploads/allimg/110903/1_110903203627_2.jpg",
			"http://www.2qqtouxiang.cn/uploads/allimg/110903/1_110903203627_4.jpg",
			};
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getThird_AccessToken = (GetThird_AccessToken) getApplicationContext();
        getThird_AccessToken.setuser_image_head(images);
//        if (getThird_AccessToken.geteditTextVisable()==0) {
//        	getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//        }
        setContentView(R.layout.replyact);
        context = this;
        reply_editText = (EditText) findViewById(R.id.reply_edit);
        reply_listview = (ScrollView) findViewById(R.id.reply_listview);
        reply_linearlayout = (LinearLayout) findViewById(R.id.reply_linearlayout);
        into_listContent();
        //判断用户是点击评论内容进入此activity的还是点击回复之后进入的
        if (getThird_AccessToken.geteditTextVisable()==1) {
//        	reply_editText.setFocusable(true);
//			reply_editText.setFocusableInTouchMode(true); 
    		new Handler().postDelayed(new Runnable(){
			@Override
			public void run(){
				reply_listview.scrollTo(0, 99999999);  
			}
		}, 400);
//        	reply_listview.post(new Runnable() {   
//			    public void run() {  
//			    	reply_listview.scrollTo(0, 99999999);  
//			    }   
//			});
        }
        else
        {
        	new Handler().postDelayed(new Runnable(){
    			@Override
    			public void run(){
    				//reply_listview.scrollTo(0, 99999999);  
    				InputMethodManager m = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    	            m .hideSoftInputFromWindow(reply_editText.getWindowToken(), 0);
    			}
    		}, 100);
        	


        	new Handler().postDelayed(new Runnable(){
    			@Override
    			public void run(){
    				reply_listview.scrollTo(0, 1);  
    			}
    		}, 400);
        }
        login_goback = (RelativeLayout) findViewById(R.id.login_goback);
        login_goback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent();
//				intent.setClass(context, DetailActivity.class);
//				startActivity(intent);
				finish();
			}
		});
        reply_button = (Button) findViewById(R.id.reply_button);
        reply_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (reply_editText.getText().toString().trim().length()>0) {
					Toast.makeText(context, getString(R.string.replysuess), Toast.LENGTH_SHORT).show();
					reply_editText.setText("");
				}
				else
				{
					Toast.makeText(context, getString(R.string.pleaseenterwords), Toast.LENGTH_SHORT).show();
				}
			}
		});
        reply_editText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				reply_editText.setFocusable(true);
				reply_editText.setFocusableInTouchMode(false); 
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run(){
						reply_listview.scrollTo(0, 99999999);  
					}
				}, 400);
			}
		});
        
//        reply_editText.setFocusable(true);
//		reply_editText.setFocusableInTouchMode(true); 
//		reply_editText.requestFocus();  
//		 
//		new Handler().postDelayed(new Runnable(){
//			@Override
//			public void run(){
//				
//			}
//		}, 1000);
		
	}
	//加载用户评论内容
	public void into_listContent()
	{
		for (int i = 0; i < user_name.length;i++) {
			LayoutInflater inflater = ( LayoutInflater ) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
			View lo = ( View ) inflater.inflate( R.layout.sigle_recomment, null );
			RelativeLayout sigle_relat = (RelativeLayout) lo.findViewById(R.id.sigle_relat);
			TextView user_name_view = (TextView) lo.findViewById(R.id.user_name);
			user_name_view.setText(user_name[i]);
			TextView user_content_view = (TextView) lo.findViewById(R.id.user_content);
			user_content_view.setText(user_content[i]);
			TextView user_time_view = (TextView) lo.findViewById(R.id.user_time);
			user_time_view.setText(user_time[i]);
			ImageView user_image_view = (ImageView) lo.findViewById(R.id.user_image);
			//动态加载头像
			bitmap_user=asyncBitmapLoader2.loadBitmap(user_image_view, getThird_AccessToken.getuser_image_head()[i], getWindowManager().getDefaultDisplay().getWidth(), new ImageCallBack() {
				
				@Override
				public void imageLoad(ImageView imageView, Bitmap bitmap) {
					if (bitmap==null) {
						imageView.setImageResource(R.drawable.head);
					}
					else {
						imageView.setImageBitmap(BitmapZoom.bitmapZoomByWidth(Tools.toRoundCorner(bitmap, 360), BitmapFactory.decodeResource(getResources(), R.drawable.head).getWidth()));
					}
				}
			});
	        if (bitmap_user==null) {
	        	user_image_view.setImageResource(R.drawable.head);
			}
	        else {
	        	user_image_view.setImageBitmap(BitmapZoom.bitmapZoomByWidth(Tools.toRoundCorner(bitmap_user, 360), BitmapFactory.decodeResource(getResources(), R.drawable.head).getWidth()));
			}
			user_image_view.setId((i+1));
			user_image_view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(context, OtherPersonActivity.class);
					startActivity(intent);
				}
			});
//			LinearLayout reply_layout = (LinearLayout) lo.findViewById(R.id.sigle_linear);
//			reply_layout.setId((i+1)*100);
//			reply_layout.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					reply_listview.post(new Runnable() {   
//					    public void run() {  
//					    	reply_listview.scrollTo(0, 99999999);  
//					    }   
//					});
//					//reply_listview.scrollTo(0, reply_listview.FOCUS_DOWN);
//					//reply_listview.gets
//					reply_editText.setFocusable(true);  
//					reply_editText.setFocusableInTouchMode(true);  
//					reply_editText.requestFocus();  
//					InputMethodManager inputManager = (InputMethodManager)reply_editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
//					inputManager.showSoftInput(reply_editText, 0); 
//				}
//			});
			reply_linearlayout.addView(sigle_relat);
		}
	}
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
    	switch(keyCode){
        case KeyEvent.KEYCODE_BACK:
//        	if (reply_editText.hasFocus()) {
//        		reply_editText.setFocusable(false);
//        		reply_editText.setFocusableInTouchMode(false);  
//        		reply_editText.requestFocus(); 
//        	}
//        	else
//        	{
//        		Intent intent = new Intent();
//				intent.setClass(context, DetailActivity.class);
//				startActivity(intent);
				finish();
 //       	}
        	break;
    	}
        return true;
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
