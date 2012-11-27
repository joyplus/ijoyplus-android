package com.joy;

import java.util.ArrayList;
import java.util.List;

import com.joy.view.PullToRefreshView_foot;
import com.joy.view.PullToRefreshView_foot.OnFooterRefreshListener;
import com.mobclick.android.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class Friend extends Activity implements OnFooterRefreshListener{
	Context context;
	GetThird_AccessToken getThird_AccessToken;
	PullToRefreshView_foot mPullToRefreshView;
	ListView listView;
	EditText sousuoEditText;
	Button btn_sousuo;
	public static List<String> groupKey= new ArrayList<String>();
    private List<String> haoyoulList = new ArrayList<String>();
    private List<String> yaoqingList = new ArrayList<String>();
    private List<String> guanzhuList = new ArrayList<String>();
    private static List<String> list = null;
    DragAdapter adapter;
    public static String friends[]={ "好友1", "好友2", "好友3", "好友4", "好友5"};
    public static String yaoqing[]={ "邀请好友1", "邀请好友2", "邀请好友3", "邀请好友4", "邀请好友5" };
    public static String guanzhu[]={ "关注好友1", "关注好友2", "关注好友3", "关注好友4", "关注好友5" };
	final Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 200:
				
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		context=this;
		getThird_AccessToken=(GetThird_AccessToken)getApplicationContext();
		mPullToRefreshView = (PullToRefreshView_foot)findViewById(R.id.friend_main_pull_refresh_view);
        mPullToRefreshView.setOnFooterRefreshListener(this);
		listView=(ListView)findViewById(R.id.friend_listview);
		btn_sousuo=(Button)findViewById(R.id.friend_sousuo);
		sousuoEditText=(EditText)findViewById(R.id.friend_edit);
		initData();
		adapter=new DragAdapter(context, list);
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
					getThird_AccessToken.setphoneName(sousuoEditText.getText().toString().trim());
					getThird_AccessToken.setActivitytype("5");
					Intent intent=new Intent();
					intent.setClass(context, Yaoqing.class);
					startActivity(intent);
				}
			}
		});
	}
	public void initData(){
        list = new ArrayList<String>();
        
        groupKey.add(getResources().getString(R.string.haoyouliebiao));
        groupKey.add(getResources().getString(R.string.yaoqinghaoyou));
        groupKey.add(getResources().getString(R.string.yiguanzhu));
        
        for (int i = 0; i < friends.length; i++) {
        	haoyoulList.add(friends[i]);
		}
        list.add(getResources().getString(R.string.haoyouliebiao));
        list.addAll(haoyoulList);
        
        for(int i = 0; i < yaoqing.length; i++){
            yaoqingList.add(yaoqing[i]);
        }
        list.add(getResources().getString(R.string.yaoqinghaoyou));
        list.addAll(yaoqingList);
        for (int i = 0; i < guanzhu.length; i++) {
			guanzhuList.add(guanzhu[i]);
		}
        list.add(getResources().getString(R.string.yiguanzhu));
        list.addAll(guanzhuList);
    }
	public void Btn_friend_back(View v){
		finish();
	}
	public static class DragAdapter extends ArrayAdapter<String>{

        public DragAdapter(Context context, List<String> objects) {
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
                view = LayoutInflater.from(getContext()).inflate(R.layout.drag_list_item, null);
                TextView textView2=(TextView)view.findViewById(R.id.drag_list_item_what);
                if (position<=friends.length) {
                	textView2.setText(view.getResources().getString(R.string.jiaguanzhu));
                    textView2.setTextColor(Color.BLUE);
				}
                else if (position<=(friends.length+yaoqing.length+1)&&position>friends.length) {
                	textView2.setText(view.getResources().getString(R.string.yaoqing));
                    textView2.setTextColor(Color.GREEN);
				}
                else {
                	textView2.setText(view.getResources().getString(R.string.yiguanzhu1));
                    textView2.setTextColor(Color.GRAY);
				}
            }
            
            TextView textView = (TextView)view.findViewById(R.id.drag_list_item_text);
            textView.setText(getItem(position));
            return view;
        }
    }
	@Override
	public void onFooterRefresh(PullToRefreshView_foot view) {
		mPullToRefreshView.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Message msg = new Message(); 
                msg.what = 200; 
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
