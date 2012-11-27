package com.joy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joy.Service.DownLoadService;
import com.joy.Service.Return.ReturnSearch;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.joy.Tools.AsyncBitmapLoader;
import com.joy.Tools.AsyncBitmapLoader.ImageCallBack;
import com.joy.Tools.SearchAdapter;
import com.joy.view.PullToRefreshView;
import com.joy.view.PullToRefreshView.OnFooterRefreshListener;
import com.joy.view.PullToRefreshView.OnHeaderRefreshListener;
import com.mobclick.android.MobclickAgent;

public class Sousuojieguo extends Activity implements OnHeaderRefreshListener,OnFooterRefreshListener{
	Context context;
	ListView listView;
	App app;
	TextView youguandeyingpin,youguandeshipin,titleName,doubanpinfen,shichang,text;
	Button btn_sousuo;
	EditText editText;
	ImageView imageView;
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
	private String name[] = {
			"影片名1",
			"影片名2",
			"影片名3",
			"影片名4",
			"影片名5",
			"影片名6",
			"影片名7",
			"影片名8",
			"影片名9"
	};
	private String pingfen[] = {
			"9.2",
			"9.2",
			"9.2",
			"9.2",
			"9.2",
			"9.2",
			"9.2",
			"9.2",
			"9.2"
	};
	private String time[] = {
			"8:10分钟",
			"8:10分钟",
			"8:10分钟",
			"8:10分钟",
			"8:10分钟",
			"8:10分钟",
			"8:10分钟",
			"8:10分钟",
			"8:10分钟"
	};
	String type[]={"电影","电视剧","综艺节目"};
	ProgressDialog progressBar;
	private int page_count = 6;
	private int current_page = 0;
    private int index =0;
    SearchAdapter adapter;
    PullToRefreshView mPullToRefreshView;
	private DownLoadService DOWNLOADSERVICE;
	private String KEYWORD = null;
    AsyncBitmapLoader asyncBitmapLoader=new AsyncBitmapLoader();
    final Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1000:
				listItems = getListItems(++current_page, page_count);
				adapter.notifyDataSetChanged();
				listView.setSelection(listView.getCount()-1);
				break;
			case 999:
				Intent intent = new Intent();
		    	intent.setClass(context, DetailActivity.class);
		    	startActivity(intent);
		    	progressBar.dismiss();
				break;
			case 111:
				youguandeyingpin.setText("和"+"《"+editText.getText().toString().trim()+"》"+getResources().getString(R.string.youguandeyingpian)+type[app.getmovType()]);
		        youguandeshipin.setText("和"+"《"+editText.getText().toString().trim()+"》"+getResources().getString(R.string.youguandeyingpian)+type[app.getmovType1()]);
		        //影片名
		        titleName.setText("《"+editText.getText().toString().trim()+"》");
		        //豆瓣评分
		        doubanpinfen.setText(getResources().getString(R.string.doubanpingfen));
		        //简介
		        text.setText("内容");
		        //时长
		        shichang.setText(getResources().getString(R.string.shichang));
		        editText.setText("");
		        listItems.clear();
		        current_page=0;
		        index=0;
		        listItems = getListItems(current_page, page_count);
		        adapter.notifyDataSetChanged();
		        listView.setSelection(0);
		        progressBar.dismiss();
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
		app=(App)getApplicationContext();
		mPullToRefreshView = (PullToRefreshView)findViewById(R.id.sousuojieguo_main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setOnFooterRefreshListener(this);
        
        youguandeyingpin=(TextView)findViewById(R.id.sousuojieguo_youguandeyingpian);
        youguandeshipin=(TextView)findViewById(R.id.sousuojieguo_youguandeshipin);
        titleName=(TextView)findViewById(R.id.sousuojieguo_title);
        doubanpinfen=(TextView)findViewById(R.id.sousuojieguo_pingfeng);
        text=(TextView)findViewById(R.id.sousuojieguo_txt);
        shichang=(TextView)findViewById(R.id.sousuojieguo_time);
        editText=(EditText)findViewById(R.id.sousuojieguo_edit);
        imageView=(ImageView)findViewById(R.id.sousuojieguo_img);
        
        
        Bitmap bitmap=asyncBitmapLoader.loadBitmap(imageView, app.getseachURL(), 0, new ImageCallBack() {
			
			@Override
			public void imageLoad(ImageView imageView, Bitmap bitmap) {
				if (bitmap!=null) {
					imageView.setImageBitmap(bitmap);
				}
			}
		});
        if (bitmap!=null) {
        	imageView.setImageBitmap(bitmap);
		}else {
			imageView.setImageResource(R.drawable.pic_bg);
		}
        
        youguandeyingpin.setText("和"+"《"+app.getdinayingName()+"》"+getResources().getString(R.string.youguandeyingpian)+type[app.getmovType()]);
        youguandeshipin.setText("和"+"《"+app.getdinayingName()+"》"+getResources().getString(R.string.youguandeyingpian)+type[app.getmovType1()]);
        //影片名
        titleName.setText("《"+app.getdinayingName()+"》");
        //豆瓣评分
        doubanpinfen.setText(getResources().getString(R.string.doubanpingfen));
        //简介
        text.setText("内容");
        //时长
        shichang.setText(getResources().getString(R.string.shichang));
        
        btn_sousuo=(Button)findViewById(R.id.sousuojieguo_sousuo);
		listView=(ListView)findViewById(R.id.sousuojieguo_listview);
		// veteranyu add
		Intent intent = getIntent();
		KEYWORD = intent.getStringExtra("KEYWORD");
		DOWNLOADSERVICE = app.getService();
		GetServiceData();
		// *************************************************************************
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
				app.setPicURL(images[arg2]);
				app.setPicName(name[arg2]);
				progressBar = ProgressDialog.show(context, getResources().getString(R.string.shaohou), getResources().getString(R.string.pull_to_refresh_footer_refreshing_label));
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run(){
						Message msg = new Message(); 
		                msg.what = 999; 
		                handler.sendMessage(msg); 
					}
				}, 1000);
			}
		});
		btn_sousuo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (editText.getText().toString().trim().length()!=0) {
					progressBar = ProgressDialog.show(context, getResources().getString(R.string.shaohou), getResources().getString(R.string.pull_to_refresh_footer_refreshing_label));
					new Handler().postDelayed(new Runnable(){
						@Override
						public void run(){
							Message msg = new Message(); 
			                msg.what = 111; 
			                handler.sendMessage(msg); 
						}
					}, 1000);
				}
			}
		});
	}
	//添加list
	private List<Map<String, Object>> getListItems(int pageindex, int pagecount) {
        for(int i = index; i < pagecount * (pageindex + 1)&&i<images.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>(); 
            map.put("image", images[i]);
            map.put("title", name[i]);
            map.put("info", pingfen[i]);
            map.put("time", time[i]);
            listItems.add(map);
            index++;
        }    
        return listItems;
    }
	//返回按钮
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
	public void onResume() { 
		super.onResume();
		MobclickAgent.onResume(this); 
	} 
	public void onPause() { 
		super.onPause(); 
		MobclickAgent.onPause(this); 
	}
	public void GetServiceData() {
		ReturnSearch m_ReturnSearch = DOWNLOADSERVICE.Search(app.getdinayingName());
		if (m_ReturnSearch.results != null)
			for (int i = 0; i < m_ReturnSearch.results.length; i++) {
				images[i] = m_ReturnSearch.results[i].prod_pic_url;
				name[i] = m_ReturnSearch.results[i].prod_name;
				//pingfen[i] =  m_ReturnSearch.results[i].;
				//pingfen
			}
	}
}
