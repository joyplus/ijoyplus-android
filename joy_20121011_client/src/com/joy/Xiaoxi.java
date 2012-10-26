package com.joy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.joy.Tools.AsyncBitmapLoader;
import com.joy.view.PullToRefreshView;
import com.joy.view.PullToRefreshView.OnFooterRefreshListener;
import com.joy.view.PullToRefreshView.OnHeaderRefreshListener;



public class Xiaoxi extends Activity implements OnHeaderRefreshListener,OnFooterRefreshListener {
	Context context;
	ListView listView;
	PullToRefreshView mPullToRefreshView;
	MyAdapter adapter;
    AsyncBitmapLoader asyncBitmapLoader=new AsyncBitmapLoader();
    List<Map<String, Object>> listItems=new ArrayList<Map<String, Object>>();
    ViewHolder holder;
    private int page_count = 3;
	private int current_page = 0;
    private int index =0;
    private String title[]={
    		"Joy小编 关注了您",
			"您收藏的剧集《天使之城》更新了第12集。",
			"Joy小编回复了您在《萤火之森》中的评论。",
			"内容title",
			"内容title",
			"内容title",
			"内容title",
			"内容title",
			"内容title"	
    };
    private String data[] = {
			"",
			"",
			"你戴着奇怪的面具，明明看不到眉毛，却一眼就觉得是个可爱的人",
			"",
			"",
			"",
			"",
			"",
			""
			};
    private String who[]={
    		"",
			"",
			"joy+",
			"",
			"",
			"",
			"",
			"",
			""	
    };
    private String huifudata[] = {
			"",
			"",
			"是夏日，葱绿的森林，四散的流光都会感染上秀绿。你戴着奇怪的面具，明明看不到眉毛，却一眼就觉得是个可爱的人",
			"",
			"",
			"",
			"",
			"",
			""
			};
    private String huifutime[]={
    		"",
			"",
			"一周前",
			"",
			"",
			"",
			"",
			"",
			""	
    };
    private String time[] = {
			"三天前",
			"三天前",
			"一周前",
			"二周前",
			"三周前",
			"四周前",
			"五周前",
			"六周前",
			"七周前"
			};
    final Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 100:
				listItems=getListItems(++current_page, page_count);
				adapter.notifyDataSetChanged();
				listView.setSelection(listView.getCount()-1);
				break;
			case 200:
				
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xiaoxi);
		context=this;
		
		mPullToRefreshView = (PullToRefreshView)findViewById(R.id.xiaoxi_main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setOnFooterRefreshListener(this);
		listView=(ListView)findViewById(R.id.xiaoxi_listview);
		listItems=getListItems(current_page, page_count);
		adapter=new MyAdapter(context);
		listView.setAdapter(adapter); 
	}
	
	
	
	private List<Map<String, Object>> getListItems(int pageindex, int pagecount) {
        for(int i = index; i < pagecount * (pageindex + 1)&&i<time.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>(); 
            map.put("head", R.drawable.head);
            map.put("title", title[i]);
            map.put("data", data[i]);
            map.put("who", who[i]);
            map.put("huifudata", huifudata[i]);
            map.put("huifutime", huifutime[i]);
            map.put("how", "回复");
            map.put("time", time[i]);
            listItems.add(map);
            index++;
        }    
        return listItems;
    }
	public void Btn_xiaoback(View v){
		finish();
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
		public TextView data;
		public TextView who;
		public TextView huifudata;
		public TextView huifutime;
		public TextView how;
		public TextView time;
		public LinearLayout layout;
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
				convertView = mInflater.inflate(R.layout.xiaoxilist, null);
				
				holder.head=(ImageView)convertView.findViewById(R.id.xiaoxilist_head);
				holder.title=(TextView)convertView.findViewById(R.id.xiaoxilist_title);
				holder.data=(TextView)convertView.findViewById(R.id.xiaoxilist_data);
				holder.who=(TextView)convertView.findViewById(R.id.xiaoxilist_who);
				holder.huifudata=(TextView)convertView.findViewById(R.id.xiaoxilist_huifudata);
				holder.huifutime=(TextView)convertView.findViewById(R.id.xiaoxilist_huifutime);
				holder.how=(TextView)convertView.findViewById(R.id.xiaoxilist_how);
				holder.time=(TextView)convertView.findViewById(R.id.xiaoxilist_time);
				holder.layout=(LinearLayout)convertView.findViewById(R.id.xiaoxilist_lin);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			
			holder.head.setBackgroundResource((Integer)listItems.get(position).get("head"));
			holder.head.setTag((position+1));
			holder.head.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Toast.makeText(context, v.getTag()+"", Toast.LENGTH_SHORT).show();
					Intent intent=new Intent();
					intent.setClass(context, OtherPersonActivity.class);
					startActivity(intent);
				}
			});
			holder.title.setText((String)listItems.get(position).get("title"));
			holder.data.setText((String)listItems.get(position).get("data"));
			if (listItems.get(position).get("who").equals("")) {
				holder.layout.setVisibility(View.GONE);
			}
			else {
				holder.layout.setVisibility(View.VISIBLE);
				holder.who.setText((String)listItems.get(position).get("who"));
				holder.huifudata.setText((String)listItems.get(position).get("huifudata"));
				holder.huifutime.setText((String)listItems.get(position).get("huifutime"));
				holder.how.setText((String)listItems.get(position).get("how"));
			}
			holder.time.setText((String)listItems.get(position).get("time"));
			
			return convertView;
		}
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
