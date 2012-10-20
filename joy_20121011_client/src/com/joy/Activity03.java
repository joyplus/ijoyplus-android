package com.joy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joy.Tools.SearchAdapter;
import com.joy.view.PullToRefreshView;
import com.joy.view.PullToRefreshView.OnFooterRefreshListener;
import com.joy.view.PullToRefreshView.OnHeaderRefreshListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Activity03 extends Activity implements OnHeaderRefreshListener,OnFooterRefreshListener{
	Context context;
	ListView listView;
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
			"删除",
			"回复",
			"删除",
			"删除",
			"回复",
			"删除",
			"删除",
			"回复"
			};
	private int page_count = 6;
	private int current_page = 0;
    private int index =0;
    SearchAdapter adapter;
    PullToRefreshView mPullToRefreshView;
    ViewHolder	holder;
    List<Map<String, Object>> listItems=new ArrayList<Map<String, Object>>();
    final Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 100:
				
				break;
			case 200:
				
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity03);
		context=this;
		
		mPullToRefreshView = (PullToRefreshView)findViewById(R.id.act03_main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setOnFooterRefreshListener(this);
		listView=(ListView)findViewById(R.id.act03_listview);
		listItems=getListItems();
		
		MyAdapter adapter=new MyAdapter(context);
		listView.setAdapter(adapter); 
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
			}
		});
		
		
		
	}
	private List<Map<String, Object>> getListItems() {
        for(int i = 0; i < how.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>(); 
            map.put("head", R.drawable.ic_launcher);
            map.put("who", "谁"+(i+1));
            map.put("what", "干什么"+(i+1));
            map.put("img", R.drawable.ic_launcher);
            map.put("time", "时间"+(i+1));
            map.put("how", how[i]);
            listItems.add(map);
//            index++;
        }    
        return listItems;
    }
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
		public ImageView img;
		public TextView title;
		public TextView info;
		public ImageView img1;
		public TextView time;
		public TextView viewBtn;
		public EditText editText;
	}
	public class MyAdapter extends BaseAdapter{
		
		private LayoutInflater mInflater;
		
		public MyAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			return listItems.size();
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
			if(convertView == null){
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.dongtailistview, null);
				holder.img = (ImageView)convertView.findViewById(R.id.dongtailistview_head);
				holder.title = (TextView)convertView.findViewById(R.id.dongtailistview_who);
				holder.info = (TextView)convertView.findViewById(R.id.dongtailistview_what);
				holder.img1 = (ImageView)convertView.findViewById(R.id.dongtailistview_img);
				holder.time = (TextView)convertView.findViewById(R.id.dongtailistview_time);
				holder.viewBtn = (TextView)convertView.findViewById(R.id.dongtailistview_how);
				holder.editText=(EditText)convertView.findViewById(R.id.dongtailistview_edit);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			
			
				holder.img.setBackgroundResource((Integer)listItems.get(position).get("head"));
				holder.title.setText((String)listItems.get(position).get("who"));
				holder.info.setText((String)listItems.get(position).get("what"));
				
				holder.img1.setBackgroundResource((Integer)listItems.get(position).get("img"));
				holder.time.setText((String)listItems.get(position).get("time"));
				holder.viewBtn.setText((String)listItems.get(position).get("how"));
				
				holder.viewBtn.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						System.out.println("position===>"+position);
						if (holder.viewBtn.getText().equals("删除")) {
							listItems.remove(position);
							System.out.println("listItems====>"+listItems.toString());
							MyAdapter.this.notifyDataSetChanged();
							Toast.makeText(context, "删除", Toast.LENGTH_SHORT).show();
						}
						else if (holder.viewBtn.getText().equals("回复")) {
							Toast.makeText(context, "回复", Toast.LENGTH_SHORT).show();
						}
						
					}
				});
			return convertView;
		}
	}
}
