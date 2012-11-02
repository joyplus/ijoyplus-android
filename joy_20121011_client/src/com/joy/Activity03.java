package com.joy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.joy.Tools.AsyncBitmapLoader;
import com.joy.Tools.BitmapZoom;
import com.joy.Tools.Tools;
import com.joy.Tools.AsyncBitmapLoader.ImageCallBack;
import com.joy.Tools.SearchAdapter;
import com.joy.msg.ChatMsgEntity;
import com.joy.view.PullToRefreshView;
import com.joy.view.PullToRefreshView.OnFooterRefreshListener;
import com.joy.view.PullToRefreshView.OnHeaderRefreshListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.xp.view.o;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity03 extends Activity implements OnHeaderRefreshListener,OnFooterRefreshListener{
	Context context;
	ListView listView;
	private String images[] = {
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
	private String images_head[] = {
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
	private String how[] = {
			"删除",
			"回复",
			"删除",
			"删除",
			"回复",
			"删除",
			"回复",
			"删除",
			"删除"
			};
	private String time[] = {
			"12:34",
			"11:34",
			"07:14",
			"02:14",
			"22:00",
			"21:04",
			"20:50",
			"14:06",
			"10:11"
			};
	private String who[] = {
			"JoyPlus",
			"Tina",
			"Steven",
			"JoyPlus",
			"Tina",
			"Steven",
			"JoyPlus",
			"Tina",
			"Steven"
			};
	private String what[] = {
			"是夏日，葱绿的森林，四散的流光都会感染上秀绿。你戴着奇怪的面具，明明看不到眉毛，却一眼就觉得是个可爱的人",
			"是夏日，葱绿的森林，四散的流光都会感染上秀绿。你戴着奇怪的面具，明明看不到眉毛，却一眼就觉得是个可爱的人",
			"是夏日，葱绿的森林，四散的流光都会感染上秀绿。你戴着奇怪的面具，明明看不到眉毛，却一眼就觉得是个可爱的人",
			"是夏日，葱绿的森林，四散的流光都会感染上秀绿。你戴着奇怪的面具，明明看不到眉毛，却一眼就觉得是个可爱的人",
			"是夏日，葱绿的森林，四散的流光都会感染上秀绿。你戴着奇怪的面具，明明看不到眉毛，却一眼就觉得是个可爱的人",
			"是夏日，葱绿的森林，四散的流光都会感染上秀绿。你戴着奇怪的面具，明明看不到眉毛，却一眼就觉得是个可爱的人",
			"是夏日，葱绿的森林，四散的流光都会感染上秀绿。你戴着奇怪的面具，明明看不到眉毛，却一眼就觉得是个可爱的人",
			"是夏日，葱绿的森林，四散的流光都会感染上秀绿。你戴着奇怪的面具，明明看不到眉毛，却一眼就觉得是个可爱的人",
			"是夏日，葱绿的森林，四散的流光都会感染上秀绿。你戴着奇怪的面具，明明看不到眉毛，却一眼就觉得是个可爱的人"
			};
	private int page_count = 3;
	private int current_page = 0;
    private int index =0;
    Bitmap BigBitmap;
    ProgressDialog progressBar;
    PullToRefreshView mPullToRefreshView;
    AsyncBitmapLoader asyncBitmapLoader=new AsyncBitmapLoader();
    ViewHolder	holder;
    MyAdapter adapter;
    int select;
    private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
    GetThird_AccessToken getThird_AccessToken;
    final Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 100:
				initData(++current_page, page_count);
				adapter.notifyDataSetChanged();
				listView.setSelection(listView.getCount()-1);
				break;
			case 200:
				index=0;
				current_page=0;
				mDataArrays.clear();
				initData(current_page, page_count);
				adapter.notifyDataSetChanged();
				listView.setSelection(0);
				break;
			case 1001:
				Intent intent=new Intent();
				intent.setClass(context, OtherPersonActivity.class);
				startActivity(intent);
				progressBar.dismiss();
				break;
			case 444:
				Intent intent1 = new Intent();
				intent1.setClass(context, ReplyActivity.class);
				startActivity(intent1);
				progressBar.dismiss();
				break;
			case 333:
				mDataArrays.remove(select);
				adapter.notifyDataSetChanged();
				progressBar.dismiss();
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity03);
		context=this;
		getThird_AccessToken = (GetThird_AccessToken) getApplicationContext();
		mPullToRefreshView = (PullToRefreshView)findViewById(R.id.act03_main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setOnFooterRefreshListener(this);
		listView=(ListView)findViewById(R.id.act03_listview);
		adapter=new MyAdapter(context,mDataArrays);
    	listView.setAdapter(adapter); 
		initData(current_page, page_count);
		
		
	}
	
	@Override
	protected void onDestroy() {
		Tools.ClearBitmap(BigBitmap);
		super.onDestroy();
	}

	public void initData(int pageindex, int pagecount){
    	for(int i = index; i < pagecount * (pageindex + 1)&&i<images.length; i++){
    		ChatMsgEntity entity = new ChatMsgEntity();
    		entity.sethead(R.drawable.head);
    		entity.setName(who[i]);
    		entity.setDate(what[i]);
    		entity.setURL(images[i]);
    		entity.settime(time[i]);
    		entity.sethow(how[i]);
    		mDataArrays.add(entity);
    		index++;
    	}
		
    }
	//消息的按钮
	public void Btn_goxiaoxi(View view){
		Intent intent=new Intent();
		intent.setClass(context, Xiaoxi.class);
		startActivity(intent);
	}
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
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Message msg = new Message(); 
                msg.what = 100; 
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
                msg.what = 200; 
                handler.sendMessage(msg); 
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 1000);
	}
	public final class ViewHolder{
		public ImageView head;
		public TextView title;
		public TextView info;
		public ImageView img1;
		public TextView time;
		public TextView viewBtn;
		public EditText editText;
		public RelativeLayout relativeLayout;
	}
	//listview的adapter
	public class MyAdapter extends BaseAdapter{
		
		private LayoutInflater mInflater;
		private List<ChatMsgEntity> coll;
		
		public MyAdapter(Context context,List<ChatMsgEntity> coll){
			this.mInflater = LayoutInflater.from(context);
			this.coll = coll;
		}
		@Override
		public int getCount() {
			return coll.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg1) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ChatMsgEntity entity = coll.get(position);
			if(convertView == null){
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.dongtailistview, null);
				holder.head = (ImageView)convertView.findViewById(R.id.dongtailistview_head);
				holder.title = (TextView)convertView.findViewById(R.id.dongtailistview_who);
				holder.info = (TextView)convertView.findViewById(R.id.dongtailistview_what);
				holder.img1 = (ImageView)convertView.findViewById(R.id.dongtailistview_img);
				holder.time = (TextView)convertView.findViewById(R.id.dongtailistview_time);
				holder.viewBtn = (TextView)convertView.findViewById(R.id.dongtailistview_how);
				holder.relativeLayout=(RelativeLayout)convertView.findViewById(R.id.dongtailistview_rel);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			
			holder.head.setImageResource(entity.gethead());
			holder.title.setText(entity.getName());
			holder.info.setText(entity.getDate());
			Bitmap bitmap=asyncBitmapLoader.loadBitmap(holder.img1, entity.getURL(), getWindowManager().getDefaultDisplay().getWidth()/3,new ImageCallBack() {
				
				public void imageLoad(ImageView imageView, Bitmap bitmap) {
					BigBitmap = BitmapZoom.bitmapZoomByWidth(bitmap, getWindowManager().getDefaultDisplay().getWidth()/3);
					imageView.setImageBitmap(BigBitmap);
				}
			});
			if (bitmap!=null) {
				BigBitmap = BitmapZoom.bitmapZoomByWidth(bitmap, getWindowManager().getDefaultDisplay().getWidth()/3);
				holder.img1.setImageBitmap(BigBitmap);
			}
			else {
				holder.img1.setImageResource(R.drawable.pic_bg);
			}
			holder.time.setText(entity.gettime());
			holder.viewBtn.setText(entity.gethow());
			holder.head.setTag((position+1));
			holder.head.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					progressBar = ProgressDialog.show(context, getResources().getString(R.string.shaohou), getResources().getString(R.string.pull_to_refresh_footer_refreshing_label));
					Toast.makeText(context, v.getTag()+"", Toast.LENGTH_SHORT).show();
					new Handler().postDelayed(new Runnable(){
						@Override
						public void run(){
							Message msg = new Message(); 
			                msg.what = 1001;
			                handler.sendMessage(msg);
						}
					}, 1000);
					
				}
			});
			holder.relativeLayout.setTag(entity.gethow());
			
			//点击listview中的删除还是回复
			holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (v.getTag().equals(getResources().getString(R.string.delete))) {
						select=position;
						progressBar = ProgressDialog.show(context, getResources().getString(R.string.shaohou), getResources().getString(R.string.pull_to_refresh_footer_refreshing_label));
						new Handler().postDelayed(new Runnable(){
							@Override
							public void run(){
								Message msg = new Message(); 
				                msg.what = 333;
				                handler.sendMessage(msg);
							}
						}, 1000);
					}
					else if (v.getTag().equals(getResources().getString(R.string.huifu))) {
						progressBar = ProgressDialog.show(context, getResources().getString(R.string.shaohou), getResources().getString(R.string.pull_to_refresh_footer_refreshing_label));
						new Handler().postDelayed(new Runnable(){
							@Override
							public void run(){
								Message msg = new Message(); 
				                msg.what = 444;
				                handler.sendMessage(msg);
							}
						}, 1000);
					}
				}
			});
			return convertView;
		}
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
