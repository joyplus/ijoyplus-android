package com.joy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.joy.Tools.BrayAdapter;
import com.joy.view.PullToRefreshView_foot;
import com.joy.view.PullToRefreshView_foot.OnFooterRefreshListener;
import com.mobclick.android.MobclickAgent;

public class TongxunluList extends Activity implements OnFooterRefreshListener{
	Context context;
	ListView listView;
	EditText sousuo,phone;
	AutoCompleteTextView autoCompleteTextView;
	List<Map<String, String>> list= new ArrayList<Map<String,String>>();
	List<Map<String, String>> Alllist= new ArrayList<Map<String,String>>();
//	SimpleAdapter adapter;
    DragAdapters adapters;
    Vector<String>vName=new Vector<String>();
    Vector<String>vNum=new Vector<String>();
	PullToRefreshView_foot mPullToRefreshView;
	App App;
	public static List<String> groupKey= new ArrayList<String>();
    private List<String> haoyoulList = new ArrayList<String>();
    private List<String> yaoqingList = new ArrayList<String>();
    private static List<String> lists = null;
    public static String friends[]={ "好友1", "好友2", "好友3", "好友4", "好友5"};
    public static String yaoqing[];
    public static String yaoqingNum[];
	final Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1500:
				/*getPhoneNum(++current_page, page_count);
				adapter=new SimpleAdapter(context,list,R.layout.mylistview,new String[] {"name"},new int[ ] {R.id.item_text});
				listView.setAdapter(adapter);
				listView.setSelection(listView.getCount()-1);*/
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tongxunlunlist);
		App=(App)getApplicationContext();
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		context=this;
		mPullToRefreshView = (PullToRefreshView_foot)findViewById(R.id.tongxunlu_main_pull_refresh_view);
        mPullToRefreshView.setOnFooterRefreshListener(this);
		listView=(ListView)findViewById(R.id.tongxunlu_list);
//		sousuo=(EditText)findViewById(R.id.tongxunlu_sousuo);
		autoCompleteTextView=(AutoCompleteTextView)findViewById(R.id.tongxunlu_sousuo);
		phone=(EditText)findViewById(R.id.tongxunlu_phone);
//		getPhoneNum(current_page, page_count);
		getAllPhontNum();
		yaoqing=new String[vName.size()];
		yaoqingNum=new String[vNum.size()];
		vName.copyInto(yaoqing);
		vNum.copyInto(yaoqingNum);
//		adapter=new SimpleAdapter(context,list,R.layout.mylistview,new String[] {"name"},new int[ ] {R.id.item_text});
		initData();
		adapters=new DragAdapters(context, lists);
		
		BrayAdapter<String>adapter=new BrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,yaoqing);
		/*ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.list_item, yaoqing);*/
		autoCompleteTextView.setAdapter(adapter);
		listView.setAdapter(adapters);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
//				App.setphoneNum(yaoqingNum[arg2+friends.length+2]);
//				App.setphoneName(yaoqing[arg2+friends.length+2]);
				if (arg2>=friends.length+2) {
					App.setphoneNum(yaoqingNum[(arg2-(friends.length+2))]);
					App.setphoneName(yaoqing[(arg2-(friends.length+2))]);
				}
				else {
					App.setphoneNum("");
					App.setphoneName("");
				}
				Intent intent=new Intent();
				intent.setClass(context, Yaoqing.class);
				startActivity(intent);
			}
		});
		
	}
	public void initData(){
        lists = new ArrayList<String>();
        
        groupKey.add(getResources().getString(R.string.haoyouliebiao));
        groupKey.add(getResources().getString(R.string.yaoqinghaoyou));
        
        for (int i = 0; i < friends.length; i++) {
        	haoyoulList.add(friends[i]);
		}
        lists.add(getResources().getString(R.string.haoyouliebiao));
        lists.addAll(haoyoulList);
        
        for(int i = 0; i < yaoqing.length; i++){
            yaoqingList.add(yaoqing[i]);
        }
        lists.add(getResources().getString(R.string.yaoqinghaoyou));
        lists.addAll(yaoqingList);
    }
	public static class DragAdapters extends ArrayAdapter<String>{

        public DragAdapters(Context context, List<String> objects) {
            super(context, 0, objects);
        }
        
        public List<String> getList(){
            return lists;
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
                else {
                	textView2.setText(view.getResources().getString(R.string.yaoqing));
                    textView2.setTextColor(Color.GREEN);
				}
            }
            
            TextView textView = (TextView)view.findViewById(R.id.drag_list_item_text);
            textView.setText(getItem(position));
            return view;
        }
    }
	//返回按钮
	public void Btnback(View v){
		finish();
	}
	//搜索按钮
	public void Btnsousuo(View v){
		int ok=0;
		if (autoCompleteTextView.getText().toString().trim().length()!=0) {
			for (int i = 0; i < yaoqing.length; i++) {
				if (autoCompleteTextView.getText().toString().trim().equals(yaoqing[i])
						||autoCompleteTextView.getText().toString().trim().equals(yaoqingNum[i])) {
					App.setphoneNum(yaoqingNum[i]);
					App.setphoneName(yaoqing[i]);
					ok=1;
					break;
				}
			}
		}
		if (ok==1) {
			Intent intent=new Intent();
			intent.setClass(context, Yaoqing.class);
			startActivity(intent);
		}else {
			Toast.makeText(context, getResources().getString(R.string.weizhaodao), Toast.LENGTH_SHORT).show();
		}
		
	}
	//提交按钮
	public void BtnSend(View v){
		if (phone.getText().toString().trim().length()!=0) {
			Intent intent=new Intent();
			intent.setClass(context, Friend.class);
			startActivity(intent);
		}
	}
	//获取手机通讯录
	public void getAllPhontNum(){
		ContentResolver resolver = context.getContentResolver();
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,null, null, null, null);  //传入正确的uri
		if(phoneCursor!=null){
			for (int i = 0; i < phoneCursor.getCount(); i++) {
				phoneCursor.moveToPosition(i);
				int nameIndex = phoneCursor.getColumnIndex(Phone.DISPLAY_NAME);   //获取联系人name
				String name = phoneCursor.getString(nameIndex);
				String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.NUMBER)); //获取联系人number
				if(TextUtils.isEmpty(phoneNumber)){
					 continue;
				}
				vName.addElement(name);
				vNum.addElement(phoneNumber);
			}
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
	@Override
	public void onResume() { 
		super.onResume();
		MobclickAgent.onResume(this); 
	} 
	@Override
	public void onPause() { 
		super.onPause(); 
		MobclickAgent.onPause(this); 
	}
}
