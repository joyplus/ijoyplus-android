package com.joy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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

import com.joy.Service.DownLoadService;
import com.joy.Service.Return.ReturnUserMsgs;
import com.joy.Tools.AsyncBitmapLoader;
import com.joy.Tools.AsyncBitmapLoader.ImageCallBack;
import com.joy.Tools.AsyncImageLoader;
import com.joy.Tools.BitmapZoom;
import com.joy.Tools.Tools;
import com.joy.view.PullToRefreshView;
import com.joy.view.PullToRefreshView.OnFooterRefreshListener;
import com.joy.view.PullToRefreshView.OnHeaderRefreshListener;
import com.mobclick.android.MobclickAgent;

public class Xiaoxi extends Activity implements OnHeaderRefreshListener,
		OnFooterRefreshListener {
	Context context;
	ListView listView;
	PullToRefreshView mPullToRefreshView;
	MyAdapter adapter;
	AsyncBitmapLoader asyncBitmapLoader = new AsyncBitmapLoader();
	List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
	ViewHolder holder;
	private int page_count = 3;// 每次加载x张图片
	private int current_page = 0;// 当前页数
	private int index = 0;// 加载的张数
	long overPlus = 100;// 判断剩余SD卡剩余MB
	private ArrayList<String> title = null;
	private ArrayList<String> images_head = null;
	private ArrayList<String> data = null;
	private ArrayList<String> who = null;
	private ArrayList<String> huifudata = null;
	private ArrayList<String> huifutime = null;
	private ArrayList<String> time = null;
	ProgressDialog progressBar;
	private App app;
	private DownLoadService DOWNLOADSERVICE;
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 100:
				listItems = getListItems(++current_page, page_count);
				adapter.notifyDataSetChanged();
				listView.setSelection(listView.getCount() - 1);
				break;
			case 200:
				listItems.clear();
				current_page = 0;
				index = 0;
				listItems = getListItems(current_page, page_count);
				adapter.notifyDataSetChanged();
				break;
			case 1001:
				Intent intent = new Intent();
				intent.setClass(context, OtherPersonActivity.class);
				startActivity(intent);
				progressBar.dismiss();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xiaoxi);
		context = this;

		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.xiaoxi_main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		listView = (ListView) findViewById(R.id.xiaoxi_listview);
		listItems = getListItems(current_page, page_count);
		// veteranyu add
		title = new ArrayList<String>();
		images_head = new ArrayList<String>();
		data = new ArrayList<String>();
		who = new ArrayList<String>();
		huifudata = new ArrayList<String>();
		huifutime = new ArrayList<String>();
		time = new ArrayList<String>();
		app = (App) getApplication();
		DOWNLOADSERVICE = app.getService();
		GetServiceData();
		// *************************************************************************
		adapter = new MyAdapter(context);
		listView.setAdapter(adapter);
	}

	// 添加list
	private List<Map<String, Object>> getListItems(int pageindex, int pagecount) {
		for (int i = index; i < pagecount * (pageindex + 1) && i < time.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("head", images_head.get(i).toString());
			map.put("title", title.get(i).toString());
			map.put("data", data.get(i).toString());
			map.put("who", who.get(i).toString());
			map.put("huifudata", huifudata.get(i).toString());
			map.put("huifutime", huifutime.get(i).toString());
			map.put("how", getResources().getString(R.string.huifu));
			map.put("time", time.get(i).toString());
			listItems.add(map);
			index++;
		}
		return listItems;
	}

	public void Btn_xiaoback(View v) {
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

	public final class ViewHolder {
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

	// listview的adapter
	public class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.xiaoxilist, null);

				holder.head = (ImageView) convertView
						.findViewById(R.id.xiaoxilist_head);
				holder.title = (TextView) convertView
						.findViewById(R.id.xiaoxilist_title);
				holder.data = (TextView) convertView
						.findViewById(R.id.xiaoxilist_data);
				holder.who = (TextView) convertView
						.findViewById(R.id.xiaoxilist_who);
				holder.huifudata = (TextView) convertView
						.findViewById(R.id.xiaoxilist_huifudata);
				holder.huifutime = (TextView) convertView
						.findViewById(R.id.xiaoxilist_huifutime);
				holder.how = (TextView) convertView
						.findViewById(R.id.xiaoxilist_how);
				holder.time = (TextView) convertView
						.findViewById(R.id.xiaoxilist_time);
				holder.layout = (LinearLayout) convertView
						.findViewById(R.id.xiaoxilist_lin);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 没有SD卡或者SD卡容量小于100MB直接显示网络图片
			if (Tools.hasSdcard() == false
					|| (Tools.getAvailableStore("/mnt/sdcard/joy/") >> 20) < overPlus) {
				new AsyncImageLoader().loadDrawable(
						(String) listItems.get(position).get("head"),
						new AsyncImageLoader.ImageCallback() {
							public void imageLoaded(Drawable imageDrawable) {
								if (imageDrawable != null) {
									holder.head.setImageBitmap(Tools.toRoundCorner(
											BitmapZoom.bitmapZoomByWidth(
													Tools.drawableToBitamp(imageDrawable),
													BitmapFactory
															.decodeResource(
																	getResources(),
																	R.drawable.head)
															.getWidth()), 360));
								} else {
									holder.head
											.setImageResource(R.drawable.head);
								}
							}
						});
			} else {
				Bitmap bitmap = asyncBitmapLoader.loadBitmap(holder.head,
						(String) listItems.get(position).get("head"), 0,
						new ImageCallBack() {

							public void imageLoad(ImageView imageView,
									Bitmap bitmap) {
								if (bitmap != null) {
									holder.head.setImageBitmap(Tools.toRoundCorner(
											BitmapZoom
													.bitmapZoomByWidth(
															bitmap,
															BitmapFactory
																	.decodeResource(
																			getResources(),
																			R.drawable.head)
																	.getWidth()),
											360));
								} else {
									holder.head
											.setImageResource(R.drawable.head);
								}
							}
						});
				if (bitmap != null) {
					holder.head.setImageBitmap(Tools.toRoundCorner(BitmapZoom
							.bitmapZoomByWidth(
									bitmap,
									BitmapFactory.decodeResource(
											getResources(), R.drawable.head)
											.getWidth()), 360));
				} else {
					holder.head.setImageResource(R.drawable.head);
				}
			}
			// holder.head.setBackgroundResource((Integer)listItems.get(position).get("head"));
			holder.head.setTag((position + 1));
			holder.head.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					progressBar = ProgressDialog
							.show(context,
									getResources().getString(R.string.shaohou),
									getResources()
											.getString(
													R.string.pull_to_refresh_footer_refreshing_label));
					Toast.makeText(context, v.getTag() + "", Toast.LENGTH_SHORT)
							.show();
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							Message msg = new Message();
							msg.what = 1001;
							handler.sendMessage(msg);
						}
					}, 1000);

				}
			});
			holder.title.setText((String) listItems.get(position).get("title"));
			holder.data.setText((String) listItems.get(position).get("data"));
			if (listItems.get(position).get("who").equals("")) {
				holder.layout.setVisibility(View.GONE);
			} else {
				holder.layout.setVisibility(View.VISIBLE);
				holder.who.setText((String) listItems.get(position).get("who"));
				holder.huifudata.setText((String) listItems.get(position).get(
						"huifudata"));
				holder.huifutime.setText((String) listItems.get(position).get(
						"huifutime"));
				holder.how.setText((String) listItems.get(position).get("how"));
			}
			holder.time.setText((String) listItems.get(position).get("time"));

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

	public void GetServiceData() {
		String str_current_page = Integer.toString(current_page + 1);
		String str_page_count = Integer.toString(page_count);
		ReturnUserMsgs m_ReturnUserMsgs = DOWNLOADSERVICE.UserMsgs(
				str_current_page, str_page_count);
		if (m_ReturnUserMsgs.msgs != null)
			for (int i = 0; i < m_ReturnUserMsgs.msgs.length; i++) {
				title.add(i, m_ReturnUserMsgs.msgs[i].user_name);
				data.add(i, m_ReturnUserMsgs.msgs[i].content);
				// who[i]= m_ReturnUserMsgs.msgs[i].content_pic_url;
				// huifudata[i]= m_ReturnUserMsgs.msgs[i].content_pic_url;
				// huifutime[i]= m_ReturnUserMsgs.msgs[i].content_pic_url;
				time.add(i, m_ReturnUserMsgs.msgs[i].create_date);
			}
	}
}
