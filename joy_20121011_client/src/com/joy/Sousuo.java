package com.joy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joy.view.PullToRefreshView;
import com.joy.view.PullToRefreshView_foot;
import com.joy.view.PullToRefreshView_foot.OnFooterRefreshListener;
import com.umeng.analytics.MobclickAgent;

import android.R.integer;
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
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class Sousuo extends Activity implements OnFooterRefreshListener{
	Context context;
	ListView listView;
	EditText sousuoEditText;
	Button btn_sousuo;
	PullToRefreshView_foot mPullToRefreshView;
	GetThird_AccessToken getThird_AccessToken;
	public static List<String> groupKey= new ArrayList<String>();
    private List<String> navList = new ArrayList<String>();
    private List<String> moreList = new ArrayList<String>();
    private static List<String> list = null;
    private DragListAdapter adapter = null;
    private int page_count = 5;
	private int current_page = 0;
    private int index =0;
	private String remenStr[]={ "开心魔法", "奋斗", "导火索", "碟中谍4", "碟中碟3",
				"美人心计", "倒霉熊", "火影忍者", "喜洋洋", "北京爱情故事" 
	};
	private String jiluStr[]={ "记录1", "记录2", "记录3", "记录4", "记录5" 
	};
	final Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1500:
		        addremen(++current_page, page_count);
		        adapter.notifyDataSetChanged();
		        listView.setSelection(listView.getCount()-1);
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sousuo);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		context=this;
		getThird_AccessToken=(GetThird_AccessToken)getApplicationContext();
		mPullToRefreshView = (PullToRefreshView_foot)findViewById(R.id.sousuo_main_pull_refresh_view);
        mPullToRefreshView.setOnFooterRefreshListener(this);
		listView=(ListView)findViewById(R.id.sousuo_listview);
		btn_sousuo=(Button)findViewById(R.id.sousuo_sousuo);
		sousuoEditText=(EditText)findViewById(R.id.sousuo_edit);
		initData();
        adapter = new DragListAdapter(this, list);
        listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				sousuoEditText.setText((String)arg0.getItemAtPosition(arg2));
			}
		});
		btn_sousuo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (sousuoEditText.getText().toString().trim().length()!=0) {
					getThird_AccessToken.setdinayingName(sousuoEditText.getText().toString().trim());
					Intent intent=new Intent();
					intent.setClass(context, Sousuojieguo.class);
					startActivity(intent);
					sousuoEditText.setText("");
				}
				else {
					
				}
			}
		});
		
	}
	public void Btn_sousuo_back(View v){
		finish();
	}
	//添加list
	public void initData(){
        list = new ArrayList<String>();
        
        groupKey.add(getResources().getString(R.string.sousuojilu));
        groupKey.add(getResources().getString(R.string.remensousuo));
        
        for(int i=jiluStr.length-1; i>=0; i--){
            navList.add(jiluStr[i]);
        }
        list.add(getResources().getString(R.string.sousuojilu));
        list.addAll(navList);
        
        for(int i = 0; i < page_count; i++){
            moreList.add(remenStr[i]);
            index++;
        }
        list.add(getResources().getString(R.string.remensousuo));
        list.addAll(moreList);
    }
	//下拉加载更多时调用的添加list
	public void addremen(int pageindex, int pagecount){
		navList.clear();
		moreList.clear();
		for(int i = index; i < pagecount * (pageindex + 1) && i < remenStr.length; i++){
            moreList.add(remenStr[i]);
            index++;
        }
        list.addAll(moreList);
	}
	//listview的adapter
	public static class DragListAdapter extends ArrayAdapter<String>{

        public DragListAdapter(Context context, List<String> objects) {
            super(context, 0, objects);
        }
        
        public List<String> getList(){
            return list;
        }
        
        @Override
        public boolean isEnabled(int position) {
            if(groupKey.contains(getItem(position))){
                return false;
            }
            return super.isEnabled(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            
            View view = convertView;
            if(groupKey.contains(getItem(position))){
                view = LayoutInflater.from(getContext()).inflate(R.layout.drag_list_item_tag, null);
            }else{
                view = LayoutInflater.from(getContext()).inflate(R.layout.drag_list_item_sousuo, null);
            }
            
            TextView textView = (TextView)view.findViewById(R.id.drag_list_item_text);
            textView.setText(getItem(position));
            return view;
        }
    }
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
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
	public void onResume() { 
		super.onResume();
		MobclickAgent.onResume(this); 
	} 
	public void onPause() { 
		super.onPause(); 
		MobclickAgent.onPause(this); 
	}
}
