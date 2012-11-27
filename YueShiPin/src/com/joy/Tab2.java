package com.joy;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.joy.Service.DownLoadService;
import com.joy.Service.Return.ReturnFriendRecommends;
import com.joy.Tools.AsyncBitmapLoader;
import com.joy.Tools.AsyncBitmapLoader.ImageCallBack;
import com.joy.Tools.AsyncImageLoader;
import com.joy.Tools.BitmapZoom;
import com.joy.Tools.Tools;
import com.joy.view.PullToRefreshView;
import com.joy.view.PullToRefreshView.OnFooterRefreshListener;
import com.joy.view.PullToRefreshView.OnHeaderRefreshListener;
import com.mobclick.android.MobclickAgent;

public class Tab2 extends Activity implements OnHeaderRefreshListener,
		OnFooterRefreshListener {
	protected AQuery aq;
	Button btn_xunzhaohaoyou;
	private LinearLayout linearLayout1 = null;
	private LinearLayout linearLayout2 = null;
	private LinearLayout linearLayout3 = null;
	PullToRefreshView mPullToRefreshView;
	private int USE_LINEAR_INTERVAL = 0;// 控制图片添加到那一个LinearLayout中
	private int linearlayoutWidth = 0;// 根据屏幕的大小来计算每一张图片的宽度
	private int linearlayoutHeight = 0;// 根据屏幕的大小来计算每一张图片的宽度
	private int page_count = Constant.MAXITEM;// 每次加载x张图片
	private int current_page = 0;// 当前页数
	private int index = 0;// 加载的张数
	Context context;
	Bitmap bitmap2;
	App app;
	long overPlus = 100;// 判断剩余SD卡剩余MB
	private DownLoadService DOWNLOADSERVICE;
	private ArrayList<String> images = null;
	private ArrayList<String> name_dianying = null;
	private ArrayList<String> images_pro_id = null;
	AsyncBitmapLoader asyncBitmapLoader = new AsyncBitmapLoader();
	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1500:
				addBitmaps(++current_page, page_count, images, name_dianying);
				break;
			case 1501:
				linearLayout1.removeAllViews();
				linearLayout2.removeAllViews();
				linearLayout3.removeAllViews();
				Tools.ClearBitmap(bitmap2);
				current_page = 0;
				index = 0;
				addBitmaps(current_page, page_count, images, name_dianying);
				Toast.makeText(context,
						getResources().getString(R.string.shuaxin),
						Toast.LENGTH_SHORT).show();
				break;
			case 999:
				// app.setjujiliebiaoXianshi(1);
				Intent intent = new Intent();
				intent.setClass(context, DetailActivity.class);
				startActivity(intent);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.tab2);
		context = this;
		app = (App) getApplicationContext();
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.act02_main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);

		btn_xunzhaohaoyou = (Button) findViewById(R.id.act02_xunzhaohaoyou);
		linearLayout1 = (LinearLayout) findViewById(R.id.act02_linearlayout1);
		linearLayout2 = (LinearLayout) findViewById(R.id.act02_linearlayout2);
		linearLayout3 = (LinearLayout) findViewById(R.id.act02_linearlayout3);
		linearlayoutWidth = getWindowManager().getDefaultDisplay().getWidth() / 3;
		linearlayoutHeight = getWindowManager().getDefaultDisplay().getHeight() / 3 - 20;

		// veteranyu add]
		images = new ArrayList<String>();
		name_dianying = new ArrayList<String>();
		images_pro_id = new ArrayList<String>();
		DOWNLOADSERVICE = app.getService();
		GetServiceData();
		// *************************************************************************

		addBitmaps(current_page, page_count, images, name_dianying);

		btn_xunzhaohaoyou.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, Xunzhaohaoyou.class);
				startActivity(intent);

			}
		});
	}

	@Override
	protected void onDestroy() {
		aq.dismiss();
		super.onDestroy();
	}

	// 界面中加载图片
	private void addBitmaps(int pageindex, int pagecount,
			ArrayList<String> img, ArrayList<String> name) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		try {
			for (int i = index; i < pagecount * (pageindex + 1)
					&& i < img.size(); i++) {
				if (img.get(i) == null || name.get(i) == null)
					break;
				try {
					View view = inflater.inflate(R.layout.wall, null);
					View view_text = inflater.inflate(R.layout.wall_text, null);
					aq = new AQuery(this, view);
					RelativeLayout rll = (RelativeLayout) view
							.findViewById(R.id.RelativeLayout02);
					RelativeLayout rll_text = (RelativeLayout) view_text
							.findViewById(R.id.RelativeLayout_text);

					final ImageView imageView = (ImageView) view
							.findViewById(R.id.wall_image);
					TextView textView = (TextView) view_text
							.findViewById(R.id.wall_text);
					aq.id(R.id.wall_image)
							.progress(R.id.progress)
							.image(img.get(index), true, true, 0,
									R.drawable.pic_bg);

					textView.setText(name.get(index).toString().trim());

					if (USE_LINEAR_INTERVAL == 0) {
						Bitmap bm = aq.getCachedImage(img.get(index));
						if (bm != null)
							linearlayoutHeight = bm.getHeight();
						else
							linearlayoutHeight = getWindowManager()
									.getDefaultDisplay().getHeight() / 3 - 20;
					}

					// 点击了影片，用到ontouch方法是为了有点击效果
					imageView.setOnTouchListener(new OnTouchListener() {

						@Override
						public boolean onTouch(View v, MotionEvent event) {
							switch (event.getAction()) {
							case MotionEvent.ACTION_UP:
								int index = (Integer) v.getTag();
								app.setProdID(images_pro_id
										.get(index).toString());
								app.setPicURL(images
										.get(index).toString());
								app.setPicName(name_dianying
										.get(index).toString());

								// Toast.makeText(context, ""+(index+1),
								// Toast.LENGTH_SHORT).show();
								Tools.changeLight(imageView, 0);
								Message msg = new Message();
								msg.what = 999;
								handler.sendMessage(msg);
								break;
							case MotionEvent.ACTION_DOWN:
								Tools.changeLight(imageView, -50);
								break;
							case MotionEvent.ACTION_CANCEL:
								Tools.changeLight(imageView, 0);
								break;
							}
							return true;
						}
					});
					imageView.setTag(new Integer(index));
					switch (USE_LINEAR_INTERVAL) {
					case 0:
						linearLayout1.addView(rll, linearlayoutWidth,
								linearlayoutHeight);
						linearLayout1.addView(rll_text, linearlayoutWidth, 60);
						break;
					case 1:
						linearLayout2.addView(rll, linearlayoutWidth,
								linearlayoutHeight);
						linearLayout2.addView(rll_text, linearlayoutWidth, 60);
						break;
					case 2:
						linearLayout3.addView(rll, linearlayoutWidth,
								linearlayoutHeight);
						linearLayout3.addView(rll_text, linearlayoutWidth, 60);
						break;
					default:
						break;
					}
					index++;
					if (index >= img.size()) {
						Toast.makeText(context,
								getResources().getString(R.string.jiazai),
								Toast.LENGTH_SHORT).show();
					}
					System.out.println("index====>" + index);
					USE_LINEAR_INTERVAL++;
					USE_LINEAR_INTERVAL = USE_LINEAR_INTERVAL % 3;

				} catch (Exception e) {
					System.out.println(e.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.toString());
		}
	}

	// 保存URL地址，没网络的情况从内存拿之前保存过的地址来显示图片
	public String[] SetSaveData(String where, String URL[]) {
		if (Tools.isNetworkAvailable(context) == false) {
			app.GetImageName(where);
			String Img_Name = app.getIMG_Name();
			URL = Tools.Split(Img_Name, "$URL$");
			for (int i = 0; i < URL.length; i++) {
				System.out.println("URL==>" + URL[i]);
			}
		} else {
			int a = 0;
			String iMG_Name = "";
			for (int i = 0; i < URL.length; i++) {
				if (a == 0) {
					iMG_Name += URL[i];
					a = 1;
				} else {
					iMG_Name += "$URL$" + URL[i];
				}
			}
			app.setIMG_Name(iMG_Name);
			app.SaveImageName(where);
		}
		return URL;
	}

	public ArrayList<String> SetSaveName(String where, ArrayList<String> Name) {
		if (Tools.isNetworkAvailable(context) == false) {
			app.GetName(where);
			String Name_URL = app.getName_URL();
			Name = Tools.SplitA(Name_URL, "$URL$");
		} else {
			int a = 0;
			String Name_URL = "";
			for (int i = 0; i < Name.size(); i++) {
				if (a == 0) {
					Name_URL += Name.get(i).toString();
					a = 1;
				} else {
					Name_URL += "$URL$" + Name.get(i).toString();
				}
			}
			app.setName_URL(Name_URL);
			app.SaveName(where);
		}
		return Name;
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
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
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				Message msg = new Message();
				msg.what = 1501;
				handler.sendMessage(msg);
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 1000);
	}

	// 异步加载图片
	public Bitmap setImage(ImageView imageView, String URL) {
		return asyncBitmapLoader.loadBitmap(imageView, URL, linearlayoutWidth,
				new ImageCallBack() {

					@Override
					public void imageLoad(ImageView imageView, Bitmap bitmap) {
						if (bitmap != null) {
							bitmap2 = BitmapZoom.bitmapZoomByWidthA(bitmap,
									linearlayoutWidth);
							imageView.setImageBitmap(bitmap2);
							RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
									bitmap2.getWidth(), bitmap2.getHeight());
							layoutParams.setMargins(4, 1, 4, 1);
							imageView.setLayoutParams(layoutParams);
							imageView.setImageBitmap(bitmap);
						} else {
							imageView.setImageResource(R.drawable.pic_bg);
						}
					}
				});
	}

	public void setViewImage(final ImageView v, String url) {
		new AsyncImageLoader().loadDrawable(url,
				new AsyncImageLoader.ImageCallback() {
					@Override
					public void imageLoaded(Drawable imageDrawable) {
						if (imageDrawable != null) {
							bitmap2 = BitmapZoom.bitmapZoomByWidthA(
									Tools.drawableToBitamp(imageDrawable),
									linearlayoutWidth);
							v.setImageBitmap(bitmap2);
							RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
									bitmap2.getWidth(), bitmap2.getHeight());
							layoutParams.setMargins(4, 1, 4, 1);
							v.setLayoutParams(layoutParams);
						} else {
							v.setImageResource(R.drawable.pic_bg);
						}
					}
				});
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
	protected void onStart() {

		super.onStart();

		if (app.getexit() == "true") {
			finish();
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		// addBitmaps(current_page, page_count,images,name_dianying);
		super.onPause();
		MobclickAgent.onPause(this);
	}

	public void GetServiceData() {

		String str_current_page = Integer.toString(current_page + 1);
		String str_page_count = Integer.toString(page_count);
		ReturnFriendRecommends m_FriendRecommends = DOWNLOADSERVICE
				.FriendRecommends(str_current_page, str_page_count);
		if (m_FriendRecommends.recommends != null) {
			// images = new String[m_FriendRecommends.recommends.length];
			// name_dianying= new String[m_FriendRecommends.recommends.length];
			// images_pro_id = new String[m_FriendRecommends.recommends.length];
			for (int i = 0; i < m_FriendRecommends.recommends.length; i++) {
				images.add(i, m_FriendRecommends.recommends[i].content_pic_url);
				name_dianying.add(i,
						m_FriendRecommends.recommends[i].content_name);
				images_pro_id.add(i,
						m_FriendRecommends.recommends[i].content_id);

			}
		}
	}
}
