package com.joy;

import java.io.File;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.joy.Tools.AsyncBitmapLoader;
import com.joy.Tools.AsyncBitmapLoader.ImageCallBack;
import com.joy.Tools.Tools;
import com.joy.view.PullToRefreshView;
import com.joy.view.PullToRefreshView.OnFooterRefreshListener;
import com.joy.view.PullToRefreshView.OnHeaderRefreshListener;
import com.joy.weibo.net.ShareActivity;
import com.joy.weibo.net.Weibo;
import com.joy.weibo.net.WeiboException;
import com.mobclick.android.MobclickAgent;

public class DetailActivity extends Activity implements OnClickListener,OnHeaderRefreshListener,OnFooterRefreshListener{
	PullToRefreshView mPullToRefreshView;
	RelativeLayout login_goback,recommend,seen,favorite,comment,share;
	LinearLayout myGridView;
	SimpleAdapter adapter;
	List<Map<String, Object>> items;
	TextView nameTextView;
	Map<String, Object> item;
	//int juji = 5;
	int button_wigth = 60;
	int lin_han = 0;
	LinearLayout detail_all_comment;
	ImageView pic;
	String user_name[]={"张3","张4","张5","张6","张7","张8","张9","张0"};
	String user_content[]={"夏日炎炎",
							"马上要光棍节了",
							"星期八小镇，3岁至15岁孩子玩角色扮演",
							"当我还是年轻的时候妈妈告诉我，其实每个人都是super star",
							"上帝之手没有瑕疵",
							"今晚饮得尽兴啊，听日休息，可以训大觉",
							"哥是好爷们，铁血真汉子，不需要备胎",
							"清理QQ好友时发现某个三年没动静的好友。最后一条签名是：自从买了保险，过马路再也不用左右看了..."};
    String user_time[] = {"12:45","12:46","12:47","12:48","12:49","12:50","12:51","12:52"};
    Context context;
    int linearlayoutWidth;
    int index = 0;
    int juji_nub = 5;
    int pinglun_nub = 5;
    int count = 0;
    int page = 0;
    Bitmap bitmap;
//    天生王牌-100611-石小群
//    天生王牌-100521-孙兴
//    天生王牌-100528-孙兴王喜
//    天生王牌-100507-黄品源
//    天生王牌-100618-五强晋级赛

//    String juji_Name[] = {"天生王牌-100611-石小群","天生王牌-100521-孙兴","天生王牌-100528-孙兴王喜",
//    						"天生王牌-100507-黄品源","天生王牌-100618-五强晋级赛"};
    String juji_Name[]={"1","2","3","4","5","6","7","8"};
    AsyncBitmapLoader asyncBitmapLoader=new AsyncBitmapLoader();
    GetThird_AccessToken getThird_AccessToken;
    ProgressDialog progressBar;
    final Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1500:
				into_jiazai(++page);
				break;
			case 300:
				progressBar.dismiss();
				getThird_AccessToken.setActivitytype("");
				Intent intent=new Intent();
				intent.setClass(context, JoyActivity.class);
				startActivity(intent);
				((Welcome) getThird_AccessToken.getcontext()).finish();
				finish();
				break;
			}
		}
	};
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.detailact);
        context = this;
        getThird_AccessToken = (GetThird_AccessToken) getApplicationContext();
        
        //linearlayoutWidth =  getWindowManager().getDefaultDisplay().getWidth()/pinglun_nub;
        into();
        into_juji();
        into_jiazai(page);
    }
    public void into()
    {
    	 findViewById(R.id.shoucang_bt).setOnClickListener(this);
    	 findViewById(R.id.kanguoback).setOnClickListener(this);
    	 findViewById(R.id.jiejian_bt).setOnClickListener(this);
    	findViewById(R.id.recommend).setOnClickListener(this);
    	findViewById(R.id.seen).setOnClickListener(this);
    	findViewById(R.id.favorite).setOnClickListener(this);
    	findViewById(R.id.comment).setOnClickListener(this);
    	findViewById(R.id.share).setOnClickListener(this);
    	findViewById(R.id.login_goback).setOnClickListener(this);
    	nameTextView=(TextView)findViewById(R.id.detail_Move_Name);
    	nameTextView.setText(getThird_AccessToken.getPicName());
    	findViewById(R.id.briefintroduction).setOnClickListener(this);
    	findViewById(R.id.detail_seen_nub).setOnClickListener(this);
    	findViewById(R.id.detail_favorite_nub).setOnClickListener(this);
    	findViewById(R.id.play_moves).setOnClickListener(this);
    	myGridView = (LinearLayout) findViewById(R.id.myGridview);
    	findViewById(R.id.detail_drama);
    	detail_all_comment = (LinearLayout) findViewById(R.id.detail_all_comment);
    	mPullToRefreshView = (PullToRefreshView) findViewById(R.id.detail_main_pull_refresh_view);
    	mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setOnFooterRefreshListener(this);
        pic=(ImageView)findViewById(R.id.detail_beijing);
        pic.setOnClickListener(this);
        bitmap=asyncBitmapLoader.loadBitmap(pic, getThird_AccessToken.getPicURL(), getWindowManager().getDefaultDisplay().getWidth(), new ImageCallBack() {
			
			@Override
			public void imageLoad(ImageView imageView, Bitmap bitmap) {
				if (bitmap==null) {
					imageView.setBackgroundResource(R.drawable.zhuyebg);
				}
				else {
					imageView.setBackgroundDrawable(Tools.BitampTodrawable(bitmap));
				}
			}
		});
        if (bitmap==null) {
			pic.setBackgroundResource(R.drawable.zhuyebg);
		}
        else {
			pic.setBackgroundDrawable(Tools.BitampTodrawable(bitmap));
		}
    }
    //按剧集名字长度来显示每行显示几列
    public int getLie()
    {
    	int  lie= 0;
    	int index = 0;
    	for (int i = 0; i < juji_Name.length;i++) {
    		if (juji_Name[index].length()<juji_Name[i].length()) {
    			index = i;
    		}
    	}
    	System.out.println(juji_Name[index]+"/"+index);
    	linearlayoutWidth = (int)Tools.getWidth(juji_Name[index]);
    	//判断剧集名字长度是否大于屏幕宽度
    	if (getWindowManager().getDefaultDisplay().getWidth()<linearlayoutWidth) {
    		linearlayoutWidth = getWindowManager().getDefaultDisplay().getWidth();
    	}
    	lie = getWindowManager().getDefaultDisplay().getWidth()/linearlayoutWidth;
    	//重新获取每个剧集BUTTON的宽度
    	linearlayoutWidth = getWindowManager().getDefaultDisplay().getWidth()/lie;
    	//判断剧集每行不超过5个
    	if (lie>5) {
    		linearlayoutWidth = getWindowManager().getDefaultDisplay().getWidth()/5;
    		return 5;
    	}
    	return lie;
    }
    public void into_juji()
    {
    	//判断是否是电影或视频
    	if (juji_Name.length == 1 || getThird_AccessToken.getjujiliebiaoXianshi()==0) {
        	findViewById(R.id.detail_drama).setVisibility(View.GONE);
        	myGridView.setVisibility(View.GONE);
        }
        else
        {
        	//判断是否为剧集
        	if (getThird_AccessToken.getjujiliebiaoXianshi()==1) {
        		juji_Name=new String[]{"01","02","03","04","05","06","07","08"};
        	}
        	//判断是否为综艺
        	else if(getThird_AccessToken.getjujiliebiaoXianshi()==2)
        	{
        		juji_Name=new String[]{"天生王牌-100611-石小群","天生王牌-100521-孙兴","天生王牌-100528-孙兴王喜",
						"天生王牌-100507-黄品源","天生王牌-100618-五强晋级赛"};
        	}
        	juji_nub = getLie();
        	lin_han = juji_Name.length/juji_nub;
        	if (lin_han*juji_nub<juji_Name.length) {
        		lin_han++;
        	}
        	for (int i = 0; i < lin_han;i++) {
        		LinearLayout.LayoutParams h_ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        		h_ll.gravity = Gravity.CENTER;
        		LinearLayout h_linearlayout = new LinearLayout(context);
        		h_linearlayout.setOrientation(LinearLayout.HORIZONTAL);
        		
        		h_linearlayout.setLayoutParams(h_ll);
        		for (int j = 0; j < juji_nub; j++) {
        			if (index<juji_Name.length) {
        				
        				LayoutInflater inflater = ( LayoutInflater ) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
        				View lo = ( View ) inflater.inflate( R.layout.grid_item, null );
        				LinearLayout RelativeLayout = (LinearLayout) lo.findViewById(R.id.RelativeLayout01);
        				Button bt = (Button) lo.findViewById(R.id.button_item);
        				bt.setLayoutParams(new LinearLayout.LayoutParams(linearlayoutWidth,LinearLayout.LayoutParams.WRAP_CONTENT));
        				bt.setTextSize(16);
        				bt.setText(juji_Name[index]);
        				bt.setId(index);
        				bt.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								Toast.makeText(context, "这里是第"+(v.getId()+1)+"集", Toast.LENGTH_SHORT).show();
								//webview
								Intent intent = new Intent();
								intent.setClass(context, PlayVideoActivity.class);
								startActivity(intent);
								//打开网页
//								Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse("http://video.sina.cn/?sa=t424d736959v456&pos=23&vt=4"));
//						        //it.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
//						        startActivity(it);

							}
						});
        				h_linearlayout.addView(RelativeLayout);
        				index++;
        			}
        		}
        		myGridView.addView(h_linearlayout);
        	}
        }
    }
    public void into_jiazai(int page)
    {
    	for (int i = count; i < pinglun_nub*(page+1)&&i < user_name.length;i++) {
			LayoutInflater inflater = ( LayoutInflater ) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
			View lo = ( View ) inflater.inflate( R.layout.sigle_recomment, null );
			RelativeLayout sigle_relat = (RelativeLayout) lo.findViewById(R.id.sigle_relat);
			//ImageView user_image = (ImageView) findViewById(R.id.user_image);
			TextView user_name_view = (TextView) lo.findViewById(R.id.user_name);
			user_name_view.setText(user_name[i]);
			TextView user_content_view = (TextView) lo.findViewById(R.id.user_content);
			user_content_view.setText(user_content[i]);
			user_content_view.setId((count+1)*100);
			user_content_view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (getThird_AccessToken.getAccessToken().trim().trim().length()==0) {
						if (getThird_AccessToken.getQQ_Token().trim().trim().length()==0) {
							new AlertDialog.Builder(context).setTitle(getString(R.string.tishi))
							.setMessage(getString(R.string.nologin)).setCancelable(false)
							.setPositiveButton("确认", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = new Intent();
									intent.setClass(context, Welcome.class);
									startActivity(intent);
									//finish();
								}
							}).setNegativeButton(getString(R.string.quxiao), new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
							
								}
							}).show();
						}
						else
						{
							getThird_AccessToken.seteditTextVisable(0);
							Intent intent = new Intent();
							intent.setClass(context, ReplyActivity.class);
							startActivity(intent);
//							finish();
						}
					}
					else
					{
						getThird_AccessToken.seteditTextVisable(0);
						Intent intent = new Intent();
						intent.setClass(context, ReplyActivity.class);
						startActivity(intent);
//						finish();
					}
				}
			});
			TextView user_time_view = (TextView) lo.findViewById(R.id.user_time);
			user_time_view.setText(user_time[i]);
			TextView user_reply_view = (TextView) lo.findViewById(R.id.user_reply);
			user_reply_view.setId((count+1)*1000);
			user_reply_view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (getThird_AccessToken.getAccessToken().trim().trim().length()==0) {
						if (getThird_AccessToken.getQQ_Token().trim().trim().length()==0) {
							new AlertDialog.Builder(context).setTitle(getString(R.string.tishi))
							.setMessage(getString(R.string.nologin)).setCancelable(false)
							.setPositiveButton("确认", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = new Intent();
									intent.setClass(context, Welcome.class);
									startActivity(intent);
									//finish();
								}
							}).setNegativeButton(getString(R.string.quxiao), new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
							
								}
							}).show();
						}
						else
						{
							getThird_AccessToken.seteditTextVisable(1);
							Intent intent = new Intent();
							intent.setClass(context, ReplyActivity.class);
							startActivity(intent);
//							finish();
						}
					}
					else
					{
						getThird_AccessToken.seteditTextVisable(1);
						Intent intent = new Intent();
						intent.setClass(context, ReplyActivity.class);
						startActivity(intent);
//						finish();
					}
				}
			});
			detail_all_comment.addView(sigle_relat);
			count++;
        }
    }
	@Override
	public void onClick(View v) {
		Intent intent;
		File file;
		String sdPath;
		String picPath;
        File picFile;
		switch(v.getId())
		{
		case R.id.recommend:
			Toast.makeText(context, getString(R.string.tuijian), Toast.LENGTH_SHORT).show();
			break;
		case R.id.seen:
			Toast.makeText(context, getString(R.string.kanguo), Toast.LENGTH_SHORT).show();
			break;
		case R.id.favorite:
			Toast.makeText(context, getString(R.string.shoucang), Toast.LENGTH_SHORT).show();
			break;
		case R.id.comment:
			getThird_AccessToken.setButton_Name(getString(R.string.pinglun));
			file = Environment.getExternalStorageDirectory();
            sdPath = file.getAbsolutePath();
            // 请保证SD卡根目录下有这张图片文件
            picPath = sdPath + "/" + "xxxx.ac.jpg";
           // picFile = new File(picPath);
            picPath = null;
            try {
            	if (getThird_AccessToken.getlogin_where().equals(getString(R.string.sinawb))) {
            		share2weibo(getString(R.string.pleaseenter), picPath);
            	}
            	else
            	{
            		intent = new Intent();
            		intent.putExtra(ShareActivity.EXTRA_PIC_URI, picPath);
            		intent.setClass(context, ShareActivity.class);
            		startActivity(intent);
            	}
//                Intent i = new Intent(context, ShareActivity.class);
//                startActivity(i);
            } catch (WeiboException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {

            }
			finish();
			break;
		case R.id.share:
			getThird_AccessToken.setButton_Name(getString(R.string.fenxiang));
			file = Environment.getExternalStorageDirectory();
            sdPath = file.getAbsolutePath();
            String picName=getThird_AccessToken.getPicURL().substring(getThird_AccessToken.getPicURL().lastIndexOf("/") + 1);    
            // 请保证SD卡根目录下有这张图片文件
            picPath = sdPath + "/joy/ijoyplus/"+picName;
            
            picFile = new File(picPath);
            if (!picFile.exists()) {
                Toast.makeText(context, getString(R.string.nopic), Toast.LENGTH_SHORT)
                        .show();
                picPath = null;
            }
            try {
            	if (getThird_AccessToken.getlogin_where().equals(getString(R.string.sinawb))) {
            		share2weibo(getString(R.string.pleaseenter), picPath);
            	}
            	else
            	{
            		intent = new Intent();
            		intent.putExtra(ShareActivity.EXTRA_PIC_URI, picPath);
            		intent.setClass(context, ShareActivity.class);
            		startActivity(intent);
            	}
//                Intent i = new Intent(context, ShareActivity.class);
//                startActivity(i);
            } catch (WeiboException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {

            }
        	finish();
			//Toast.makeText(context, "分享", Toast.LENGTH_SHORT).show();
			break;
		case R.id.login_goback:
			if(getThird_AccessToken.getwhere_gologin()==3) {
				finish();
			}
        	else if(getThird_AccessToken.getwhere_gologin()==1)
        	{
        		getThird_AccessToken.setwhere_gologin(2);
        		finish();
        	}
        	else if (getThird_AccessToken.getwhere_gologin()==2) {
        		progressBar = ProgressDialog.show(context, getResources().getString(R.string.shaohou), getResources().getString(R.string.pull_to_refresh_footer_refreshing_label));
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run(){
						Message msg = new Message(); 
		                msg.what = 300; 
		                handler.sendMessage(msg); 
					}
				}, 1000);
        	}
				
			break;
		case R.id.play_moves:
			intent = new Intent();
			intent.setClass(context, PlayVideoActivity.class);
			startActivity(intent);
			break;
		}
	}
	private void share2weibo(String content, String picPath) throws WeiboException {
        Weibo weibo = Weibo.getInstance();
        System.out.println("weibo.getAccessToken().getSecret():"+weibo.getAccessToken().getSecret());
        System.out.println("weibo.getAccessToken().token():"+weibo.getAccessToken().getToken());
        weibo.share2weibo(this, weibo.getAccessToken().getToken(), weibo.getAccessToken()
                .getSecret(), content, picPath);
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
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
    	switch(keyCode){
        case KeyEvent.KEYCODE_BACK:
        	if(getThird_AccessToken.getwhere_gologin()==3) {
        		finish();
			}
        	else if(getThird_AccessToken.getwhere_gologin()==1)
        	{
        		getThird_AccessToken.setwhere_gologin(2);
        		finish();
        	}
        	else if (getThird_AccessToken.getwhere_gologin()==2) {
        		progressBar = ProgressDialog.show(context, getResources().getString(R.string.shaohou), getResources().getString(R.string.pull_to_refresh_footer_refreshing_label));
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run(){
						Message msg = new Message(); 
		                msg.what = 300; 
		                handler.sendMessage(msg); 
					}
				}, 1000);
        	}
        	break;
    	}
        return true;
    }
	
	@Override
	protected void onDestroy() {
		Tools.ClearBitmap(bitmap);
		super.onDestroy();
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