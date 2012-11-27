package com.joy;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.videolan.vlc.VideoPlayerActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.joy.Service.DownLoadService;
import com.joy.Service.Return.ReturnProgramView;
import com.joy.Tools.AsyncBitmapLoader;
import com.joy.Tools.AsyncBitmapLoader.ImageCallBack;
import com.joy.Tools.BitmapZoom;
import com.joy.Tools.Tools;
import com.joy.weibo.net.ShareActivity;
import com.joy.weibo.net.Weibo;
import com.joy.weibo.net.WeiboException;
import com.mobclick.android.MobclickAgent;

public class DetailActivity extends Activity implements OnClickListener {
	// PullToRefreshView mPullToRefreshView;
	RelativeLayout login_goback, recommend, seen, favorite, comment, share;
	private TextView text_dou_nub, text_detail_seen_nub,
			text_detail_favorite_nub;
	LinearLayout myGridView;
	SimpleAdapter adapter;
	List<Map<String, Object>> items;
	TextView nameTextView;
	Map<String, Object> item;
	// int juji = 5;
	int button_wigth = 60;
	int lin_han = 0;
	LinearLayout detail_all_comment;
	ImageView pic;
	private int page_count = 8;
	private ArrayList<String> user_name = null;
	private ArrayList<String> user_content = null;
	private ArrayList<String> user_time = null;
	private ArrayList<String> headURL = null;
	String[] video_extensions = { ".3g2", ".3gp", ".3gp2", ".3gpp", ".amv",
			".asf", ".avi", ".divx", "drc", ".dv", ".f4v", ".flv", ".gvi",
			".gxf", ".iso", ".m1v", ".m2v", ".m2t", ".m2ts", ".m4v", ".mkv",
			".mov", ".mp2", ".mp2v", ".mp4", ".mp4v", ".mpe", ".mpeg",
			".mpeg1", ".mpeg2", ".mpeg4", ".mpg", ".mpv2", ".mts", ".mtv",
			".mxf", ".mxg", ".nsv", ".nuv", ".ogm", ".ogv", ".ogx", ".ps",
			".rec", ".rm", ".rmvb", ".tod", ".ts", ".tts", ".vob", ".vro",
			".webm", ".wm", ".wmv", ".wtv", ".xesc" };
	String[] video_dont_support_extensions = { ".m3u", ".m3u8" };

	Context context;
	int linearlayoutWidth;
	int index = 0;
	int juji_nub = 0;
	int pinglun_nub = 0;
	int count = 0;
	int page = 0;
	String POSTER;
	String PROD_ID = null;
	String PROD_URI = null;
	String PROD_SUMMARY = null;
	String PROD_NAME = null;
	String PROD_SOURCE = null;
	Bitmap bitmap;
	String s_dou_nub, s_detail_seen_nub, s_detail_favorite_nub;

	private ArrayList<String> juji_Name = null;
	private ArrayList<String> juji_url = null;
	private ArrayList<String> juji_Source = null;
	AsyncBitmapLoader asyncBitmapLoader = new AsyncBitmapLoader();
	AsyncBitmapLoader asyncBitmapLoader2 = new AsyncBitmapLoader();
	App app;
	private DownLoadService DOWNLOADSERVICE;
	ProgressDialog progressBar;
	Bitmap bitmap_user;
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1500:
				// into_jiazai(++page);
				into_jiazai(page);
				break;
			case 300:
				progressBar.dismiss();
				app.setActivitytype("");
				/*
				 * Intent intent=new Intent(); intent.setClass(context,
				 * JoyActivity.class); startActivity(intent); ((Welcome)
				 * app.getcontext()).finish();
				 */
				finish();
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.detail_activity);
		context = this;
		app = (App) getApplicationContext();

		text_dou_nub = (TextView) findViewById(R.id.dou_nub);
		text_detail_seen_nub = (TextView) findViewById(R.id.detail_seen_nub);
		text_detail_favorite_nub = (TextView) findViewById(R.id.detail_favorite_nub);
		// veteranyu add
		// Intent intent = getIntent();
		user_name = new ArrayList<String>();
		user_content = new ArrayList<String>();
		user_time = new ArrayList<String>();
		headURL = new ArrayList<String>();
		juji_Name = new ArrayList<String>();
		juji_url = new ArrayList<String>();
		juji_Source = new ArrayList<String>();
		PROD_ID = app.getProdID();// intent.getStringExtra("PROD_ID");
		DOWNLOADSERVICE = app.getService();
		GetServiceData();
		app.setuser_image_headA(headURL);
		// *************************************************************************
		into();
		into_juji();
		into_jiazai(page);
	}

	public void into() {
		findViewById(R.id.shoucang_bt).setOnClickListener(this);
		findViewById(R.id.kanguoback).setOnClickListener(this);
		findViewById(R.id.jiejian_bt).setOnClickListener(this);
		findViewById(R.id.recommend).setOnClickListener(this);
		findViewById(R.id.seen).setOnClickListener(this);
		findViewById(R.id.favorite).setOnClickListener(this);
		findViewById(R.id.comment).setOnClickListener(this);
		findViewById(R.id.share).setOnClickListener(this);
		findViewById(R.id.login_goback).setOnClickListener(this);
		nameTextView = (TextView) findViewById(R.id.detail_Move_Name);
		nameTextView.setText(PROD_NAME);
		findViewById(R.id.briefintroduction).setOnClickListener(this);
		findViewById(R.id.detail_seen_nub).setOnClickListener(this);
		findViewById(R.id.detail_favorite_nub).setOnClickListener(this);
		findViewById(R.id.play_moves).setOnClickListener(this);
		myGridView = (LinearLayout) findViewById(R.id.myGridview);
		findViewById(R.id.detail_drama);
		detail_all_comment = (LinearLayout) findViewById(R.id.detail_all_comment);
		// mPullToRefreshView = (PullToRefreshView)
		// findViewById(R.id.detail_main_pull_refresh_view);
		// mPullToRefreshView.setOnHeaderRefreshListener(this);
		// mPullToRefreshView.setOnFooterRefreshListener(this);
		pic = (ImageView) findViewById(R.id.detail_beijing);
		pic.setOnClickListener(this);
		// bitmap=asyncBitmapLoader.loadBitmap(pic,
		// app.getPicURL(),
		// getWindowManager().getDefaultDisplay().getWidth(), new
		// ImageCallBack() {
		if (POSTER != null) {
			bitmap = asyncBitmapLoader.loadBitmap(pic, POSTER,
					getWindowManager().getDefaultDisplay().getWidth(),
					new ImageCallBack() {
						@Override
						public void imageLoad(ImageView imageView, Bitmap bitmap) {
							if (bitmap == null) {
								imageView
										.setBackgroundResource(R.drawable.zhuyebg);
							} else {
								Bitmap BigBitmap = BitmapZoom
										.bitmapZoomByHeight(bitmap,
												getWindowManager()
														.getDefaultDisplay()
														.getHeight() / 2);
								imageView.setImageBitmap(BigBitmap);

								// imageView.setBackgroundDrawable(Tools.BitampTodrawable(bitmap));
							}
						}
					});
		}

		if (bitmap == null) {
			pic.setBackgroundResource(R.drawable.zhuyebg);
		} else {
			Bitmap BigBitmap = BitmapZoom.bitmapZoomByHeight(bitmap,
					getWindowManager().getDefaultDisplay().getWidth() / 2);
			pic.setImageBitmap(BigBitmap);
			// pic.setBackgroundDrawable(Tools.BitampTodrawable(bitmap));
		}

	}

	// 按剧集名字长度来显示每行显示几列
	public int getLie() {
		int lie = 0;
		int index = 0;
		for (int i = 0; i < juji_Name.size(); i++) {
			if (juji_Name.get(index).toString().length() < juji_Name.get(i)
					.toString().length()) {
				index = i;
			}
		}
		System.out.println(juji_Name.get(index).toString() + "/" + index);
		linearlayoutWidth = (int) Tools.getWidth(juji_Name.get(index));
		// 判断剧集名字长度是否大于屏幕宽度
		if (getWindowManager().getDefaultDisplay().getWidth() < linearlayoutWidth) {
			linearlayoutWidth = getWindowManager().getDefaultDisplay()
					.getWidth();
		}
		lie = getWindowManager().getDefaultDisplay().getWidth()
				/ linearlayoutWidth;
		// 重新获取每个剧集BUTTON的宽�? linearlayoutWidth =
		// getWindowManager().getDefaultDisplay().getWidth()/ lie;
		// 判断剧集每行不超�?�?
		if (lie > 5) {
			linearlayoutWidth = getWindowManager().getDefaultDisplay()
					.getWidth() / 5;
			return 5;
		}
		return lie;
	}

	public void into_juji() {
		// 判断是否是电影或视频
		if (juji_Name.size() == 1
				|| app.getjujiliebiaoXianshi() == 0) {
			findViewById(R.id.detail_drama).setVisibility(View.GONE);
			myGridView.setVisibility(View.GONE);
		} else {
			/*
			 * //判断是否为剧�?if (app.getjujiliebiaoXianshi()==1) {
			 * juji_Name=new String[]{"01","02","03","04","05","06","07","08"};
			 * } //判断是否为综�?else
			 * if(app.getjujiliebiaoXianshi()==2) {
			 * juji_Name=new
			 * String[]{"天生王牌-100611-石小�?,"天生王牌-100521-孙兴","天生王牌-100528-孙兴王喜",
			 * "天生王牌-100507-黄品�?,"天生王牌-100618-五强晋级�?}; }
			 */
			juji_nub = getLie();
			lin_han = juji_Name.size() / juji_nub;
			if (lin_han * juji_nub < juji_Name.size()) {
				lin_han++;
			}
			for (int i = 0; i < lin_han; i++) {
				LinearLayout.LayoutParams h_ll = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				h_ll.gravity = Gravity.CENTER;
				LinearLayout h_linearlayout = new LinearLayout(context);
				h_linearlayout.setOrientation(LinearLayout.HORIZONTAL);

				h_linearlayout.setLayoutParams(h_ll);
				for (int j = 0; j < juji_nub; j++) {
					if (index < juji_Name.size()) {

						LayoutInflater inflater = (LayoutInflater) context
								.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						View lo = (View) inflater.inflate(R.layout.grid_item,
								null);
						LinearLayout RelativeLayout = (LinearLayout) lo
								.findViewById(R.id.RelativeLayout01);
						Button bt = (Button) lo.findViewById(R.id.button_item);
						bt.setLayoutParams(new LinearLayout.LayoutParams(
								linearlayoutWidth,
								LinearLayout.LayoutParams.WRAP_CONTENT));
						bt.setTextSize(16);
						bt.setText(juji_Name.get(index).toString());
						bt.setId(index);
						bt.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								int click_num = v.getId();// +1;
								// Toast.makeText(context,
								// "这里是第"+(v.getId()+1)+"�?,
								// Toast.LENGTH_SHORT).show();
								if (juji_Source.get(click_num) != null) {
									CallVideoPlayActivity(juji_Source.get(
											click_num).toString());
									// if (PROD_SOURCE != null &&
									// PROD_SOURCE.trim().length() > 0) {
									// CallVideoPlayActivity(PROD_SOURCE);
									// }
								} else {
									Intent intent = new Intent();
									intent.setAction("android.intent.action.VIEW");
									Uri content_url = Uri.parse(juji_url.get(
											click_num).toString());
									intent.setData(content_url);
									startActivity(intent);
								}
								/*
								 * //webview Intent intent = new Intent();
								 * intent.setClass(context,
								 * PlayVideoActivity.class);
								 * startActivity(intent);
								 */
								// 打开网页
								// Intent it = new Intent(Intent.ACTION_VIEW,
								// Uri.parse("http://video.sina.cn/?sa=t424d736959v456&pos=23&vt=4"));
								// //it.setClassName("com.android.browser",
								// "com.android.browser.BrowserActivity");
								// startActivity(it);

							}
						});
						h_linearlayout.addView(RelativeLayout);
						index++;
					}
				}
				myGridView.addView(h_linearlayout);
			}
		}
	}

	public void into_jiazai(int page) {

		// && user_content[i] != null
		for (int i = count; i < pinglun_nub * (page + 1)
				&& i < user_name.size(); i++) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View lo = (View) inflater.inflate(R.layout.sigle_recomment, null);
			RelativeLayout sigle_relat = (RelativeLayout) lo
					.findViewById(R.id.sigle_relat);
			ImageView user_image = (ImageView) lo.findViewById(R.id.user_image);

			text_dou_nub.setText(s_dou_nub);
			text_detail_seen_nub.setText(s_detail_seen_nub);
			text_detail_favorite_nub.setText(s_detail_favorite_nub);
			// 动态加载头�?
			bitmap_user = asyncBitmapLoader2.loadBitmap(user_image,
					app.getuser_image_head()[i],
					getWindowManager().getDefaultDisplay().getWidth(),
					new ImageCallBack() {

						@Override
						public void imageLoad(ImageView imageView, Bitmap bitmap) {
							if (bitmap == null) {
								imageView.setImageResource(R.drawable.head);
							} else {
								imageView.setImageBitmap(BitmapZoom.bitmapZoomByWidth(
										Tools.toRoundCorner(bitmap, 360),
										BitmapFactory
												.decodeResource(getResources(),
														R.drawable.head)
												.getWidth()));
							}
						}
					});
			if (bitmap_user == null) {
				user_image.setImageResource(R.drawable.head);
			} else {
				user_image.setImageBitmap(BitmapZoom.bitmapZoomByWidth(Tools
						.toRoundCorner(bitmap_user, 360), BitmapFactory
						.decodeResource(getResources(), R.drawable.head)
						.getWidth()));
			}
			user_image.setId((count + 1));
			user_image.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(context, OtherPersonActivity.class);
					startActivity(intent);
				}
			});
			TextView user_name_view = (TextView) lo
					.findViewById(R.id.user_name);
			user_name_view.setText(user_name.get(i).toString());
			TextView user_content_view = (TextView) lo
					.findViewById(R.id.user_content);
			user_content_view.setText(user_content.get(i).toString());
			user_content_view.setId((count + 1) * 10000);
			user_content_view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (app.getAccessToken().trim().trim()
							.length() == 0) {
						if (app.getQQ_Token().trim().trim()
								.length() == 0) {
							new AlertDialog.Builder(context)
									.setTitle(getString(R.string.tishi))
									.setMessage(getString(R.string.nologin))
									.setCancelable(false)
									.setPositiveButton(
											"确认",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int which) {

													Intent intent = new Intent();
													intent.setClass(
															context,
															Login_Activity.class);
													startActivity(intent);
												}
											})
									.setNegativeButton(
											getString(R.string.quxiao),
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int which) {

												}
											}).show();
						} else {
							app.seteditTextVisable(0);
							Intent intent = new Intent();
							intent.setClass(context, ReplyActivity.class);
							startActivity(intent);
							// finish();
						}
					} else {
						app.seteditTextVisable(0);
						Intent intent = new Intent();
						intent.setClass(context, ReplyActivity.class);
						startActivity(intent);
						// finish();
					}
				}
			});
			TextView user_time_view = (TextView) lo
					.findViewById(R.id.user_time);
			user_time_view.setText(user_time.get(i).toString());
			TextView user_reply_view = (TextView) lo
					.findViewById(R.id.user_reply);
			user_reply_view.setId((count + 1) * 100000);
			user_reply_view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (app.getAccessToken().trim().trim()
							.length() == 0) {
						if (app.getQQ_Token().trim().trim()
								.length() == 0) {
							new AlertDialog.Builder(context)
									.setTitle(getString(R.string.tishi))
									.setMessage(getString(R.string.nologin))
									.setCancelable(false)
									.setPositiveButton(
											"确认",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int which) {

													Intent intent = new Intent();
													intent.setClass(
															context,
															Login_Activity.class);
													startActivity(intent);
												}
											})
									.setNegativeButton(
											getString(R.string.quxiao),
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int which) {

												}
											}).show();
						} else {
							app.seteditTextVisable(1);
							Intent intent = new Intent();
							intent.setClass(context, ReplyActivity.class);
							startActivity(intent);
							// finish();
						}
					} else {
						app.seteditTextVisable(1);
						Intent intent = new Intent();
						intent.setClass(context, ReplyActivity.class);
						startActivity(intent);
						// finish();
					}
				}
			});
			detail_all_comment.addView(sigle_relat);
			count++;
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		File file;
		String sdPath;
		String picPath;
		File picFile;
		switch (v.getId()) {
		case R.id.recommend: // 推荐
			if (DOWNLOADSERVICE.ProgramRecommend(PROD_ID, "OK"))
				Toast.makeText(context, "推荐成功", Toast.LENGTH_SHORT).show();
			break;
		case R.id.seen:
			if (DOWNLOADSERVICE.ProgramWatch(PROD_ID))
				Toast.makeText(context, getString(R.string.kanguo),
						Toast.LENGTH_SHORT).show();
			break;
		case R.id.favorite:
			if (DOWNLOADSERVICE.ProgramFavority(PROD_ID))
				Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show();
			break;
		case R.id.jiejian_bt:
			AlertDialog alertDialog = new AlertDialog.Builder(this).setMessage(
					PROD_SUMMARY).create();
			Window window = alertDialog.getWindow();
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.alpha = 0.6f;
			window.setAttributes(lp);
			alertDialog.show();
			break;
		case R.id.comment:
			app.setButton_Name(getString(R.string.pinglun));
			file = Environment.getExternalStorageDirectory();
			sdPath = file.getAbsolutePath();
			// 请保证SD卡根目录下有这张图片文件
			picPath = sdPath + "/" + "xxxx.ac.jpg";
			// picFile = new File(picPath);
			picPath = null;
			try {
				if (app.getlogin_where().equals(
						getString(R.string.sinawb))) {
					share2weibo(getString(R.string.pleaseenter), picPath);
				} else {
					intent = new Intent();
					intent.putExtra("PROD_ID", PROD_ID);
					intent.putExtra(ShareActivity.EXTRA_PIC_URI, picPath);
					intent.setClass(context, ShareActivity.class);
					startActivity(intent);
				}
				// Intent i = new Intent(context, ShareActivity.class);
				// startActivity(i);
			} catch (WeiboException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {

			}
			finish();
			break;
		case R.id.share:
			app.setButton_Name(getString(R.string.fenxiang));

			// String
			// picName=app.getPicURL().substring(app.getPicURL().lastIndexOf("/")
			// + 1);
			String picName;
			try {
				picName = URLEncoder.encode(app.getPicURL(),
						"UTF-8");
				// picName = URLEncoder.encode(app.getPicURL(),
				// "UTF-8");
				// 请保证SD卡根目录下有这张图片文件
				picPath = Constant.PATH + picName;

				picFile = new File(picPath);
				if (!picFile.exists()) {
					Toast.makeText(context, getString(R.string.nopic),
							Toast.LENGTH_SHORT).show();
					picPath = null;
				}
				try {
					if (app.getlogin_where().equals(
							getString(R.string.sinawb))) {
						share2weibo(getString(R.string.pleaseenter), picPath);
					} else {
						intent = new Intent();
						intent.putExtra("PROD_ID", PROD_ID);
						intent.putExtra(ShareActivity.EXTRA_PIC_URI, picPath);
						intent.setClass(context, ShareActivity.class);
						startActivity(intent);
					}
					// Intent i = new Intent(context, ShareActivity.class);
					// startActivity(i);
				} catch (WeiboException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {

				}
				finish();
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// Toast.makeText(context, "分享", Toast.LENGTH_SHORT).show();
			break;
		case R.id.login_goback:
			if (app.getwhere_gologin() == 3) {
				finish();
			} else if (app.getwhere_gologin() == 1) {
				app.setwhere_gologin(2);
				finish();
			} else if (app.getwhere_gologin() == 2) {
				progressBar = ProgressDialog
						.show(context,
								getResources().getString(R.string.shaohou),
								getResources()
										.getString(
												R.string.pull_to_refresh_footer_refreshing_label));
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						Message msg = new Message();
						msg.what = 300;
						handler.sendMessage(msg);
					}
				}, 1000);
			}

			break;
		case R.id.play_moves:
			/*
			 * intent = new Intent(); intent.putExtra("URI", PROD_URI);
			 * intent.setClass(context, PlayVideoActivity.class);
			 * startActivity(intent);
			 */
			if (PROD_SOURCE != null && PROD_SOURCE.trim().length() > 0) {
				CallVideoPlayActivity(PROD_SOURCE);
				/*
				 * intent = new Intent(); intent.putExtra("SOURCE",
				 * PROD_SOURCE); //intent.setClass(context,
				 * MediaActivity.class); intent.setClass(context,
				 * VideoPlayerActivity.class); startActivity(intent);
				 */
			} else {
				intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse(PROD_URI);
				intent.setData(content_url);
				startActivity(intent);
			}
			break;
		}
	}

	private void share2weibo(String content, String picPath)
			throws WeiboException {
		Weibo weibo = Weibo.getInstance();
		System.out.println("weibo.getAccessToken().getSecret():"
				+ weibo.getAccessToken().getSecret());
		System.out.println("weibo.getAccessToken().token():"
				+ weibo.getAccessToken().getToken());
		weibo.share2weibo(this, weibo.getAccessToken().getToken(), weibo
				.getAccessToken().getSecret(), content, picPath);
	}

	/*
	 * @Override public void onFooterRefresh(PullToRefreshView view) {
	 * mPullToRefreshView.postDelayed(new Runnable() {
	 * 
	 * @Override public void run() { Message msg = new Message(); msg.what =
	 * 1500; handler.sendMessage(msg);
	 * mPullToRefreshView.onFooterRefreshComplete(); } }, 1000); }
	 * 
	 * @Override public void onHeaderRefresh(PullToRefreshView view) {
	 * mPullToRefreshView.postDelayed(new Runnable() {
	 * 
	 * @Override public void run() {
	 * mPullToRefreshView.onHeaderRefreshComplete(); } }, 1000); }
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (app.getwhere_gologin() == 3) {
				finish();
			} else if (app.getwhere_gologin() == 1) {
				app.setwhere_gologin(2);
				finish();
			} else if (app.getwhere_gologin() == 2) {
				progressBar = ProgressDialog
						.show(context,
								getResources().getString(R.string.shaohou),
								getResources()
										.getString(
												R.string.pull_to_refresh_footer_refreshing_label));
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						Message msg = new Message();
						msg.what = 300;
						handler.sendMessage(msg);
					}
				}, 1000);
			}
			break;
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		Tools.ClearBitmap(bitmap);
		super.onDestroy();
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/*
	 * 播放视频到时候需要如下规则： 1：播放格式优先级：mp4,m3u8,网页 2：清晰度优先级：高清，标清， 流畅，超�?"type": "mp4",
	 * mp4:高清，flv：标清，hd2：超�?
	 */
	public void GetServiceData() {

		ReturnProgramView m_ReturnProgramView = DOWNLOADSERVICE
				.ProgramView(PROD_ID);
		if (m_ReturnProgramView.tv != null) {
			POSTER = m_ReturnProgramView.tv.poster;
			PROD_NAME = m_ReturnProgramView.tv.name;
			PROD_SUMMARY = m_ReturnProgramView.tv.summary;
			PROD_URI = m_ReturnProgramView.tv.episodes[0].video_urls[0].url;

			s_dou_nub = m_ReturnProgramView.tv.score;
			s_detail_seen_nub = m_ReturnProgramView.tv.watch_num;
			s_detail_favorite_nub = m_ReturnProgramView.tv.favority_num;

			// if (m_ReturnProgramView.tv.episodes[0].down_urls != null)
			// PROD_SOURCE =
			// m_ReturnProgramView.tv.episodes[0].down_urls[0].urls[0].url;
			if (m_ReturnProgramView.tv.episodes != null) {

				if (m_ReturnProgramView.tv.episodes[0].down_urls != null
						&& m_ReturnProgramView.tv.episodes[0].down_urls.length > 0
						&& m_ReturnProgramView.tv.episodes[0].down_urls[0].urls[0].url != null
						&& IfSupportFormat(m_ReturnProgramView.tv.episodes[0].down_urls[0].urls[0].url))
					PROD_SOURCE = m_ReturnProgramView.tv.episodes[0].down_urls[0].urls[0].url;
				juji_nub = m_ReturnProgramView.tv.episodes.length;
				for (int i = 0; i < juji_nub; i++) {
					juji_Name.add(i, m_ReturnProgramView.tv.episodes[i].name);
					if (m_ReturnProgramView.tv.episodes[i].video_urls != null
							&& m_ReturnProgramView.tv.episodes[i].video_urls.length > 0)
						juji_url.add(
								i,
								m_ReturnProgramView.tv.episodes[i].video_urls[0].url);
					if (m_ReturnProgramView.tv.episodes[i].down_urls != null
							&& m_ReturnProgramView.tv.episodes[i].down_urls[0].urls.length > 0
							&& m_ReturnProgramView.tv.episodes[i].down_urls[0].urls[0].url != null
							&& IfSupportFormat(m_ReturnProgramView.tv.episodes[i].down_urls[0].urls[0].url)) {

						juji_Source
								.add(i,
										m_ReturnProgramView.tv.episodes[i].down_urls[0].urls[0].url);

					}
				}

			}
		} else if (m_ReturnProgramView.show != null) {
			POSTER = m_ReturnProgramView.show.poster;
			PROD_NAME = m_ReturnProgramView.show.name;
			PROD_SUMMARY = m_ReturnProgramView.show.summary;
			PROD_URI = m_ReturnProgramView.show.episodes[0].video_urls[0].url;
			s_dou_nub = m_ReturnProgramView.show.score;
			s_detail_seen_nub = m_ReturnProgramView.show.watch_num;
			s_detail_favorite_nub = m_ReturnProgramView.show.favority_num;
			// if (m_ReturnProgramView.show.episodes[0].down_urls != null)
			// PROD_SOURCE =
			// m_ReturnProgramView.show.episodes[0].down_urls[0].urls[0].url;
			if (m_ReturnProgramView.show.episodes != null) {

				if (m_ReturnProgramView.show.episodes[0].down_urls != null
						&& m_ReturnProgramView.show.episodes[0].down_urls.length > 0
						&& m_ReturnProgramView.show.episodes[0].down_urls[0].urls != null
						&& m_ReturnProgramView.show.episodes[0].down_urls[0].urls.length > 0
						&& m_ReturnProgramView.show.episodes[0].down_urls[0].urls[0].url != null
						&& IfSupportFormat(m_ReturnProgramView.show.episodes[0].down_urls[0].urls[0].url))
					PROD_SOURCE = m_ReturnProgramView.show.episodes[0].down_urls[0].urls[0].url;

				juji_nub = m_ReturnProgramView.show.episodes.length;
				for (int i = 0; i < juji_nub; i++) {
					juji_Name.add(i, m_ReturnProgramView.show.episodes[i].name);

					if (m_ReturnProgramView.show.episodes[i].video_urls != null
							&& m_ReturnProgramView.show.episodes[i].video_urls.length > 0)
						juji_url.add(
								i,
								m_ReturnProgramView.show.episodes[i].video_urls[0].url);

					if (m_ReturnProgramView.show.episodes[i].down_urls != null
							&& m_ReturnProgramView.show.episodes[i].down_urls[0].urls.length > 0
							&& m_ReturnProgramView.show.episodes[i].down_urls[0].urls[0].url != null
							&& IfSupportFormat(m_ReturnProgramView.show.episodes[i].down_urls[0].urls[0].url)) {

						juji_Source
								.add(i,
										m_ReturnProgramView.show.episodes[i].down_urls[0].urls[0].url);

					}
				}

			}
		} else if (m_ReturnProgramView.movie != null) {
			POSTER = m_ReturnProgramView.movie.poster;
			PROD_NAME = m_ReturnProgramView.movie.name;
			PROD_SUMMARY = m_ReturnProgramView.movie.summary;
			PROD_URI = m_ReturnProgramView.movie.episodes[0].video_urls[0].url;
			s_dou_nub = m_ReturnProgramView.movie.score;
			s_detail_seen_nub = m_ReturnProgramView.movie.watch_num;
			s_detail_favorite_nub = m_ReturnProgramView.movie.favority_num;

			// add to juji_Name 防止数据显示不全
			String juji_Name1[] = { "01", "02", "03", "04", "05", "06", "07",
					"08" };
			juji_Name = Tools.trans(juji_Name1);
			// ///////////////////////
			if (m_ReturnProgramView.movie.episodes != null) {

				if (m_ReturnProgramView.movie.episodes[0].down_urls != null
						&& m_ReturnProgramView.movie.episodes[0].down_urls.length > 0
						&& m_ReturnProgramView.movie.episodes[0].down_urls[0].urls != null
						&& m_ReturnProgramView.movie.episodes[0].down_urls[0].urls.length > 0
						&& m_ReturnProgramView.movie.episodes[0].down_urls[0].urls[0].url != null
						&& IfSupportFormat(m_ReturnProgramView.movie.episodes[0].down_urls[0].urls[0].url))
					PROD_SOURCE = m_ReturnProgramView.movie.episodes[0].down_urls[0].urls[0].url;

				juji_nub = m_ReturnProgramView.movie.episodes.length;
				for (int i = 0; i < juji_nub; i++) {
					juji_Name
							.add(i, m_ReturnProgramView.movie.episodes[i].name);

					if (m_ReturnProgramView.movie.episodes[i].video_urls != null
							&& m_ReturnProgramView.movie.episodes[i].video_urls.length > 0)
						juji_url.add(
								i,
								m_ReturnProgramView.movie.episodes[i].video_urls[0].url);

					if (m_ReturnProgramView.movie.episodes[i].down_urls != null
							&& m_ReturnProgramView.movie.episodes[i].down_urls[0].urls.length > 0
							&& m_ReturnProgramView.movie.episodes[i].down_urls[0].urls[0].url != null
							&& IfSupportFormat(m_ReturnProgramView.movie.episodes[i].down_urls[0].urls[0].url)) {

						juji_Source
								.add(i,
										m_ReturnProgramView.movie.episodes[i].down_urls[0].urls[0].url);

					}
				}

			}
			/*
			 * if (m_ReturnProgramView.movie != null) { // for (int i = 0; i
			 * <m_ReturnProgramView.movie.episodes.length; // i++) { // if
			 * (m_ReturnProgramView.movie != null) POSTER =
			 * m_ReturnProgramView.movie.poster; PROD_NAME =
			 * m_ReturnProgramView.movie.name; PROD_SUMMARY =
			 * m_ReturnProgramView.movie.summary; PROD_URI =
			 * m_ReturnProgramView.movie.video_urls[0].url; s_dou_nub =
			 * m_ReturnProgramView.movie.score; s_detail_seen_nub =
			 * m_ReturnProgramView.movie.watch_num; s_detail_favorite_nub =
			 * m_ReturnProgramView.movie.favority_num;
			 * 
			 * String juji_Name1[] = { "01", "02", "03", "04", "05", "06", "07",
			 * "08" }; juji_Name = Tools.trans(juji_Name1); // if
			 * (m_ReturnProgramView.movie.down_urls != null) // PROD_SOURCE =
			 * m_ReturnProgramView.movie.down_urls[0].urls[0].url; if
			 * (m_ReturnProgramView.movie.down_urls != null) { for (int i = 0; i
			 * < m_ReturnProgramView.movie.down_urls.length; i++) { if
			 * (m_ReturnProgramView.movie.down_urls[i].urls != null &&
			 * IfSupportFormat
			 * (m_ReturnProgramView.movie.down_urls[i].urls[0].url)) {
			 * PROD_SOURCE = m_ReturnProgramView.movie.down_urls[i].urls[0].url;
			 * break; }
			 * 
			 * } }
			 */
			// }
		} else if (m_ReturnProgramView.video != null) {
			POSTER = m_ReturnProgramView.video.poster;
			PROD_NAME = m_ReturnProgramView.video.name;
			PROD_SUMMARY = m_ReturnProgramView.video.summary;
			PROD_URI = m_ReturnProgramView.video.video_urls[0].url;
			s_dou_nub = m_ReturnProgramView.video.score;
			s_detail_seen_nub = m_ReturnProgramView.video.watch_num;
			s_detail_favorite_nub = m_ReturnProgramView.video.favority_num;

			String juji_Name1[] = { "01", "02", "03", "04", "05", "06", "07",
					"08" };
			juji_Name = Tools.trans(juji_Name1);
			if (m_ReturnProgramView.video.down_urls != null) {
				for (int i = 0; i < m_ReturnProgramView.video.down_urls.length; i++) {
					if (m_ReturnProgramView.video.down_urls[i].urls != null
							&& IfSupportFormat(m_ReturnProgramView.video.down_urls[i].urls[0].url)) {
						PROD_SOURCE = m_ReturnProgramView.video.down_urls[i].urls[0].url;
						break;
					}

				}
			}

		}
		if (m_ReturnProgramView.comments != null) {
			int m_length = m_ReturnProgramView.comments.length;
			pinglun_nub = m_length;
			if (m_length > 0) {
				for (int i = 0; i < m_length; i++) {
					headURL.add(i,
							m_ReturnProgramView.comments[i].owner_pic_url);
					user_name
							.add(i, m_ReturnProgramView.comments[i].owner_name);
					user_content
							.add(i, m_ReturnProgramView.comments[i].content);
					user_time.add(i,
							m_ReturnProgramView.comments[i].create_date);
				}
			}
		}
		// else
		// pinglun_nub = 0;

	}

	public boolean IfSupportFormat(String Url) {
		for (int i = 0; i < video_dont_support_extensions.length; i++) {

			if (Url.trim().toLowerCase()
					.contains(video_dont_support_extensions[i])) {
				return false;
			}
		}

		for (int i = 0; i < video_extensions.length; i++) {

			if (Url.trim().toLowerCase().contains(video_extensions[i])) {
				return true;
			}
		}
		return false;
	}

	public void CallVideoPlayActivity(String m_uri) {

		Uri uri = Uri.parse(m_uri);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);

		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClass(this, VideoPlayerActivity.class);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			Log.e("!!!!!!!!!!!!", "CallVideoPlayActivity fail", ex);
		}

	}
}