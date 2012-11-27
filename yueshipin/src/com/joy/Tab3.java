package com.joy;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joy.Service.DownLoadService;
import com.joy.Service.Return.ReturnUserFriendDynamics;
import com.joy.Service.Return.ReturnUserView;
import com.joy.Tools.AsyncBitmapLoader;
import com.joy.Tools.AsyncBitmapLoader.ImageCallBack;
import com.joy.Tools.AsyncImageLoader;
import com.joy.Tools.BitmapZoom;
import com.joy.Tools.Tools;
import com.joy.msg.ChatMsgEntity;
import com.joy.view.PullToRefreshView;
import com.joy.view.PullToRefreshView.OnFooterRefreshListener;
import com.joy.view.PullToRefreshView.OnHeaderRefreshListener;
import com.mobclick.android.MobclickAgent;

public class Tab3 extends Activity implements OnHeaderRefreshListener,
		OnFooterRefreshListener {
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
			"http://www.circler.cn/uploads/allimg/100407/1-10040G319430-L.jpg" };

	private int page_count = 3;// 每次加载x张图片
	private int current_page = 0;// 当前页数
	private int index = 0;// 加载的张数
	Bitmap BigBitmap;
	PullToRefreshView mPullToRefreshView;
	AsyncBitmapLoader asyncBitmapLoader = new AsyncBitmapLoader();
	ViewHolder holder;
	MyAdapter adapter;
	int select;// 判断listview属于那里行的零时变量
	String selectURL = "";
	long overPlus = 100;// 判断剩余SD卡剩余MB
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
	App app;
	private DownLoadService DOWNLOADSERVICE;
	private Thread mThread;
	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 100:
				initData(++current_page, page_count);
				adapter.notifyDataSetChanged();
				listView.setSelection(listView.getCount() - 1);
				break;
			case 200:
				index = 0;
				current_page = 0;
				mDataArrays.clear();
				initData(current_page, page_count);
				adapter.notifyDataSetChanged();
				listView.setSelection(0);
				break;
			case 1001:
				Intent intent = new Intent();
				intent.setClass(context, OtherPersonActivity.class);
				startActivity(intent);
				break;
			case 1002:
				/*
				 * String a=selectURL.substring(0, selectURL.indexOf("|"));
				 * String b=selectURL.substring(selectURL.indexOf("|")+1,
				 * selectURL.length()); app.setPicURL(a);
				 * app.setPicName(b);
				 */
				Intent intent2 = new Intent();
				intent2.setClass(context, DetailActivity.class);
				startActivity(intent2);
				break;
			case 444:
				app.seteditTextVisable(1);
				Intent intent1 = new Intent();
				intent1.setClass(context, ReplyActivity.class);
				startActivity(intent1);
				break;
			case 333:
				mDataArrays.remove(select);
				adapter.notifyDataSetChanged();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.tab3);
		context = this;
		app = (App) getApplicationContext();
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.act03_main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		listView = (ListView) findViewById(R.id.act03_listview);
		// veteranyu add
		DOWNLOADSERVICE = app.getService();
		// *************************************************************************
		adapter = new MyAdapter(context, mDataArrays);
		listView.setAdapter(adapter);
		initData(current_page, page_count);

	}

	@Override
	protected void onDestroy() {
		Tools.ClearBitmap(BigBitmap);
		super.onDestroy();
	}

	/*
	 * 
	 * public void initData(int pageindex, int pagecount){ String
	 * str_current_page = Integer.toString(pageindex + 1); String str_page_count
	 * = Integer.toString(pagecount);
	 * 
	 * int j = 0; ReturnUserView m_ReturnUserView= DOWNLOADSERVICE.UserView();
	 * ReturnUserFriendDynamics m_ReturnUserFriendDynamics = DOWNLOADSERVICE
	 * .UserFriendDynamics(m_ReturnUserView.id,str_current_page,
	 * str_page_count); if (m_ReturnUserFriendDynamics.dynamics != null &&
	 * m_ReturnUserFriendDynamics.dynamics.length >= pagecount) { for (int i =
	 * index; i < pagecount * (pageindex + 1) && i < images.length; i++) {
	 * ChatMsgEntity entity = new ChatMsgEntity();
	 * 
	 * entity.hm_ReturnUserFriendDynamics.dynamics[j].user_pic_url);
	 * //type":"recommend
	 * if(m_ReturnUserFriendDynamics.dynamics[j].type.trim().toLowerCase
	 * ().indexOf("recommend") != -1){ entity.setWho("推荐了");
	 * entity.setName(m_ReturnUserFriendDynamics.dynamics[j].user_name);
	 * entity.setDate(m_ReturnUserFriendDynamics.dynamics[j].prod_name);
	 * entity.setURL(m_ReturnUserFriendDynamics.dynamics[j].prod_poster);
	 * entity.settime(m_ReturnUserFriendDynamics.dynamics[j].create_date);
	 * //entity.setWhat(m_ReturnUserFriendDynamics.dynamics[j].reason);
	 * mDataArrays.add(entity); }
	 * 
	 * else
	 * if(m_ReturnUserFriendDynamics.dynamics[j].type.trim().toLowerCase().indexOf
	 * ("reply") != -1){
	 * 
	 * entity.setName(m_ReturnUserFriendDynamics.dynamics[j].friend_name);
	 * entity.setDate(m_ReturnUserFriendDynamics.dynamics[j].content);
	 * entity.setURL(m_ReturnUserFriendDynamics.dynamics[j].friend_pic_url);
	 * entity.settime(m_ReturnUserFriendDynamics.dynamics[j].create_date);
	 * //entity.sethow(m_ReturnUserFriendDynamics.dynamics[j].reason);
	 * mDataArrays.add(entity); } else
	 * if(m_ReturnUserFriendDynamics.dynamics[j].
	 * type.trim().toLowerCase().indexOf("watch") != -1){
	 * 
	 * entity.setName(m_ReturnUserFriendDynamics.dynamics[j].friend_name);
	 * entity.setDate(m_ReturnUserFriendDynamics.dynamics[j].content);
	 * entity.setURL(m_ReturnUserFriendDynamics.dynamics[j].friend_pic_url);
	 * entity.settime(m_ReturnUserFriendDynamics.dynamics[j].create_date);
	 * //entity.sethow(m_ReturnUserFriendDynamics.dynamics[j].reason);
	 * mDataArrays.add(entity); } else
	 * if(m_ReturnUserFriendDynamics.dynamics[j].
	 * type.trim().toLowerCase().indexOf("follow") != -1){
	 * 
	 * entity.setName(m_ReturnUserFriendDynamics.dynamics[j].friend_name);
	 * entity.setDate(m_ReturnUserFriendDynamics.dynamics[j].content);
	 * entity.setURL(m_ReturnUserFriendDynamics.dynamics[j].friend_pic_url);
	 * entity.settime(m_ReturnUserFriendDynamics.dynamics[j].create_date);
	 * //entity.sethow(m_ReturnUserFriendDynamics.dynamics[j].reason);
	 * mDataArrays.add(entity); } //like else
	 * if(m_ReturnUserFriendDynamics.dynamics
	 * [j].type.trim().toLowerCase().indexOf("like") != -1){
	 * 
	 * entity.setName(m_ReturnUserFriendDynamics.dynamics[j].friend_name);
	 * entity.setDate(m_ReturnUserFriendDynamics.dynamics[j].content);
	 * entity.setURL(m_ReturnUserFriendDynamics.dynamics[j].friend_pic_url);
	 * entity.settime(m_ReturnUserFriendDynamics.dynamics[j].create_date);
	 * //entity.sethow(m_ReturnUserFriendDynamics.dynamics[j].reason);
	 * mDataArrays.add(entity); }
	 * 
	 * index++; j++; } }
	 * 
	 * }
	 */
	public void initData(int pageindex, int pagecount) {
		String str_current_page = Integer.toString(current_page + 1);
		String str_page_count = Integer.toString(pagecount);

		int j = 0;
		ReturnUserView m_ReturnUserView = DOWNLOADSERVICE.UserView();
		ReturnUserFriendDynamics m_ReturnUserFriendDynamics = DOWNLOADSERVICE
				.UserFriendDynamics(m_ReturnUserView.id, str_current_page,
						str_page_count);
		if (m_ReturnUserFriendDynamics.dynamics != null
				&& m_ReturnUserFriendDynamics.dynamics.length >= pagecount) {
			for (int i = index; i < pagecount * (pageindex + 1)
					&& i < images.length; i++) {
				ChatMsgEntity entity = new ChatMsgEntity();
				// type":"recommend
				if (m_ReturnUserFriendDynamics.dynamics[j].type.trim()
						.toLowerCase().indexOf("recommend") != -1) {
					entity.setWho("推荐了");
					/*
					 * //thread process
					 * if(m_ReturnUserFriendDynamics.dynamics[j]
					 * .user_pic_url.length() >1){ Runnable r = new
					 * MyThread(m_ReturnUserFriendDynamics
					 * .dynamics[j].user_pic_url); new Thread(r).start();//线程启动
					 * }
					 */
					entity.setProd_id(m_ReturnUserFriendDynamics.dynamics[j].prod_id);
					entity.setHead_url(m_ReturnUserFriendDynamics.dynamics[j].user_pic_url);
					entity.setName(m_ReturnUserFriendDynamics.dynamics[j].user_name);
					entity.setWhat(m_ReturnUserFriendDynamics.dynamics[j].prod_name);
					entity.setImg_url(m_ReturnUserFriendDynamics.dynamics[j].prod_poster);
					entity.setTime(m_ReturnUserFriendDynamics.dynamics[j].create_date);

				}
				mDataArrays.add(entity);
				index++;
				j++;
			}
		}

	}

	// 消息的按钮
	public void Btn_goxiaoxi(View view) {
		Intent intent = new Intent();
		intent.setClass(context, Xiaoxi.class);
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(getResources().getString(R.string.tishi));
			builder.setMessage(getResources().getString(R.string.shifoutuichu))
					.setPositiveButton(
							getResources().getString(R.string.queding),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									DOWNLOADSERVICE.AccountLogout();
									DOWNLOADSERVICE.CloseService();
									app
											.setexit(getString(R.string.exit_true));
									app.SaveExit();
									finish();
									android.os.Process
											.killProcess(android.os.Process
													.myPid());
									System.exit(0);
								}
							})
					.setNegativeButton(
							getResources().getString(R.string.quxiao),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

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

	public final class ViewHolder {

		public ImageView head_url;
		public TextView name;
		public TextView who;
		public TextView what;
		public ImageView img_url;
		public TextView time;

		public RelativeLayout relativeLayout;
	}

	// listview的adapter
	public class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private List<ChatMsgEntity> coll;

		public MyAdapter(Context context, List<ChatMsgEntity> coll) {
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ChatMsgEntity entity = coll.get(position);
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.dongtailistview, null);
				holder.head_url = (ImageView) convertView
						.findViewById(R.id.dongtailistview_head);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.who = (TextView) convertView
						.findViewById(R.id.dongtailistview_who);
				holder.img_url = (ImageView) convertView
						.findViewById(R.id.dongtailistview_img);
				holder.time = (TextView) convertView
						.findViewById(R.id.dongtailistview_time);
				holder.what = (TextView) convertView
						.findViewById(R.id.dongtailistview_what);
				holder.relativeLayout = (RelativeLayout) convertView
						.findViewById(R.id.dongtailistview_rel);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.name.setText(entity.getName());
			holder.who.setText(entity.getWho());
			holder.what.setText(entity.getWhat());
			// 没有SD卡或者SD卡容量小于100MB直接显示网络图片
			if (Tools.hasSdcard() == false
					|| (Tools.getAvailableStore("/mnt/sdcard/joy/") >> 20) < overPlus) {
				new AsyncImageLoader().loadDrawable(entity.getHead_url(),
						new AsyncImageLoader.ImageCallback() {

							@Override
							public void imageLoaded(Drawable imageDrawable) {
								if (imageDrawable != null) {
									Bitmap bitmap1 = Tools.toRoundCorner(Tools
											.drawableToBitamp(imageDrawable),
											360);
									Bitmap bitmap2 = BitmapZoom
											.bitmapZoomByWidth(
													bitmap1,
													BitmapFactory
															.decodeResource(
																	getResources(),
																	R.drawable.head)
															.getWidth());
									holder.head_url.setImageBitmap(bitmap2);
								} else {
									holder.head_url
											.setImageResource(R.drawable.head);
								}
							}
						});
			} else {
				Bitmap headBitmap = asyncBitmapLoader.loadBitmap(
						holder.head_url, entity.getHead_url(),
						getWindowManager().getDefaultDisplay().getWidth() / 2,
						new ImageCallBack() {

							@Override
							public void imageLoad(ImageView imageView,
									Bitmap bitmap) {
								if (bitmap != null) {
									Bitmap bitmap1 = Tools.toRoundCorner(
											bitmap, 360);
									Bitmap bitmap2 = BitmapZoom
											.bitmapZoomByWidth(
													bitmap1,
													BitmapFactory
															.decodeResource(
																	getResources(),
																	R.drawable.head)
															.getWidth());
									imageView.setImageBitmap(bitmap2);
								} else {
									imageView.setImageResource(R.drawable.head);
								}
							}
						});
				if (headBitmap != null) {
					Bitmap bitmap1 = Tools.toRoundCorner(headBitmap, 360);
					Bitmap bitmap2 = BitmapZoom.bitmapZoomByWidth(
							bitmap1,
							BitmapFactory.decodeResource(getResources(),
									R.drawable.head).getWidth());
					holder.head_url.setImageBitmap(bitmap2);
				} else {
					holder.head_url.setImageResource(R.drawable.head);
				}
			}
			// holder.head.setImageResource(entity.gethead());
			// holder.title.setText(entity.getName());
			// holder.info.setText(entity.getDate());
			// 没有SD卡或者SD卡容量小于100MB直接显示网络图片
			if (Tools.hasSdcard() == false
					|| (Tools.getAvailableStore("/mnt/sdcard/joy/") >> 20) < overPlus) {
				new AsyncImageLoader().loadDrawable(entity.getImg_url(),
						new AsyncImageLoader.ImageCallback() {

							@Override
							public void imageLoaded(Drawable imageDrawable) {
								if (imageDrawable != null) {
									BigBitmap = BitmapZoom.bitmapZoomByWidth(
											Tools.drawableToBitamp(imageDrawable),
											getWindowManager()
													.getDefaultDisplay()
													.getWidth() / 3);
									holder.img_url.setImageBitmap(BigBitmap);
								} else {
									holder.img_url
											.setImageResource(R.drawable.pic_bg);
								}
							}
						});
			} else {
				Bitmap bitmap = asyncBitmapLoader.loadBitmap(holder.img_url,
						entity.getImg_url(), getWindowManager()
								.getDefaultDisplay().getWidth() / 3,
						new ImageCallBack() {

							@Override
							public void imageLoad(ImageView imageView,
									Bitmap bitmap) {
								if (bitmap != null) {
									BigBitmap = BitmapZoom.bitmapZoomByWidth(
											bitmap, getWindowManager()
													.getDefaultDisplay()
													.getWidth() / 3);
									imageView.setImageBitmap(BigBitmap);
								} else {
									imageView
											.setImageResource(R.drawable.pic_bg);
								}
							}
						});
				if (bitmap != null) {
					BigBitmap = BitmapZoom
							.bitmapZoomByWidth(bitmap, getWindowManager()
									.getDefaultDisplay().getWidth() / 3);
					holder.img_url.setImageBitmap(BigBitmap);
				} else {
					holder.img_url.setImageResource(R.drawable.pic_bg);
				}
			}
			holder.img_url.setTag(entity.getImg_url() + "|" + entity.getName());
			holder.img_url.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					app.setProdID(entity.getProd_id());

					Message msg = new Message();
					msg.what = 1002;
					handler.sendMessage(msg);
				}
			});
			holder.time.setText(entity.getTime());
			// holder.viewBtn.setText(entity.gethow());

			holder.head_url.setImageResource(R.drawable.head);
			/*
			 * String bitmapName = "/mnt/sdcard/joy/admin/"; try { bitmapName =
			 * bitmapName + URLEncoder.encode(entity.getHead_url(),"UTF-8");
			 * holder.head_url.setImageURI(Uri.parse(bitmapName)); } catch
			 * (UnsupportedEncodingException e) { // TODO Auto-generated catch
			 * block e.printStackTrace(); }
			 */
			holder.head_url.setTag((position + 1));
			// holder.head_url.setImageURI(Uri.parse(entity.getHead_url()));
			/*
			 * bitmap=asyncBitmapLoader.loadBitmap(holder.head_url,
			 * entity.getHead_url(),
			 * getWindowManager().getDefaultDisplay().getWidth()/3,new
			 * ImageCallBack() {
			 * 
			 * @Override public void imageLoad(ImageView imageView, Bitmap
			 * bitmap) { BigBitmap = BitmapZoom.bitmapZoomByWidth(bitmap,
			 * getWindowManager().getDefaultDisplay().getWidth()/3);
			 * imageView.setImageBitmap(BigBitmap); } }); if (bitmap!=null) {
			 * BigBitmap = BitmapZoom.bitmapZoomByWidth(bitmap,
			 * getWindowManager().getDefaultDisplay().getWidth()/3);
			 * holder.img_url.setImageBitmap(BigBitmap); } else {
			 * holder.img_url.setImageResource(R.drawable.daren); }
			 */
			holder.head_url.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					selectURL = (String) v.getTag();
					Message msg = new Message();
					msg.what = 1001;
					handler.sendMessage(msg);
				}
			});
			holder.relativeLayout.setTag(entity.getWhat());

			// 点击listview中的删除还是回复
			holder.relativeLayout
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							if (v.getTag().equals(
									getResources().getString(R.string.delete))) {
								select = position;
								Message msg = new Message();
								msg.what = 333;
								handler.sendMessage(msg);
							} else if (v.getTag().equals(
									getResources().getString(R.string.huifu))) {
								Message msg = new Message();
								msg.what = 444;
								handler.sendMessage(msg);
							}
						}
					});
			return convertView;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onStart() {

		super.onStart();

		if (app.getexit() == "true") {
			finish();
		}

	}

	@Override
	public void onPause() {
		index = 0;
		current_page = 0;
		if (app.IsLogin) {
			mDataArrays.clear();
			initData(current_page, page_count);
			adapter.notifyDataSetChanged();
		}
		listView.setSelection(0);
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
