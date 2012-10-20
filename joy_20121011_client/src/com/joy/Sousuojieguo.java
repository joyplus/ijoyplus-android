package com.joy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joy.Tools.SearchAdapter;
import com.joy.view.PullToRefreshView;
import com.joy.view.PullToRefreshView_foot;
import com.joy.view.PullToRefreshView.OnFooterRefreshListener;
import com.joy.view.PullToRefreshView.OnHeaderRefreshListener;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Sousuojieguo extends Activity implements OnHeaderRefreshListener,OnFooterRefreshListener{
	Context context;
	ListView listView;
	GetThird_AccessToken getThird_AccessToken;
	TextView youguandeyingpin,youguandeshipin,titleName,doubanpinfen,shichang,text;
	Button btn_sousuo;
	EditText editText;
	List<Map<String, Object>> listItems=new ArrayList<Map<String, Object>>();;
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
	private int page_count = 6;
	private int current_page = 0;
    private int index =0;
    SearchAdapter adapter;
    PullToRefreshView mPullToRefreshView;
    final Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1000:
				listItems = getListItems(++current_page, page_count);
				adapter.notifyDataSetChanged();
				listView.setSelection(listView.getCount()-1);
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sousuojieguo);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		context=this;
		getThird_AccessToken=(GetThird_AccessToken)getApplicationContext();
		mPullToRefreshView = (PullToRefreshView)findViewById(R.id.sousuojieguo_main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setOnFooterRefreshListener(this);
        
        youguandeyingpin=(TextView)findViewById(R.id.sousuojieguo_youguandeyingpian);
        youguandeshipin=(TextView)findViewById(R.id.sousuojieguo_youguandeshipin);
        titleName=(TextView)findViewById(R.id.sousuojieguo_title);
        doubanpinfen=(TextView)findViewById(R.id.sousuojieguo_pingfeng);
        text=(TextView)findViewById(R.id.sousuojieguo_txt);
        shichang=(TextView)findViewById(R.id.sousuojieguo_time);
        
        youguandeyingpin.setText("和"+"《"+getThird_AccessToken.getdinayingName()+"》"+getResources().getString(R.string.youguandeyingpian));
        youguandeshipin.setText("和"+"《"+getThird_AccessToken.getdinayingName()+"》"+getResources().getString(R.string.youguandeshipin));
        titleName.setText("《"+getThird_AccessToken.getdinayingName()+"》");
        doubanpinfen.setText(getResources().getString(R.string.doubanpingfen));
        text.setText("内容");
        shichang.setText(getResources().getString(R.string.shichang));
        
        btn_sousuo=(Button)findViewById(R.id.sousuojieguo_sousuo);
		listView=(ListView)findViewById(R.id.sousuojieguo_listview);
		listItems = getListItems(current_page, page_count);
		adapter = new SearchAdapter(
				context,listItems,
				R.layout.sousuolist,
				new String[] {"image", "title", "info" ,"time"},
				new int[] {R.id.sousuolist_img, R.id.sousuolist_title,R.id.sousuolist_pingfeng,R.id.sousuolist_time});
		listView.setAdapter(adapter);    
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				arg0.getItemAtPosition(arg2);
				Toast.makeText(context, (arg2+1)+"", Toast.LENGTH_SHORT).show();
			}
		});
	}
	private List<Map<String, Object>> getListItems(int pageindex, int pagecount) {
        for(int i = index; i < pagecount * (pageindex + 1)&&i<images.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>(); 
            map.put("image", images[i]);
            map.put("title", "影片名"+(i+1));
            map.put("info", "评分："+(i+1));
            map.put("time", "时长："+(i+1));
            listItems.add(map);
            index++;
        }    
        return listItems;
    }
	public void Btn_sousuojieguo_back(View v){
		finish();
	}
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Message msg = new Message(); 
                msg.what = 1000; 
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
		}, 1000);
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	public void Btn_dianyingjieguo(View v){
		Toast.makeText(context, "点击的影片", Toast.LENGTH_SHORT).show();
	}
}
