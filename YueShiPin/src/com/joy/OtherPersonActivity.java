package com.joy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.joy.Service.DownLoadService;
import com.joy.Service.Return.ReturnUserFavorities;
import com.joy.Service.Return.ReturnUserRecommends;
import com.joy.Service.Return.ReturnUserView;
import com.joy.Service.Return.ReturnUserWatchs;
import com.joy.Tools.AsyncBitmapLoader;
import com.joy.Tools.AsyncBitmapLoader.ImageCallBack;
import com.joy.Tools.AsyncImageLoader;
import com.joy.Tools.BitmapZoom;
import com.joy.Tools.Tools;
import com.joy.view.PullToRefreshView_foot;
import com.joy.view.PullToRefreshView_foot.OnFooterRefreshListener;
import com.mobclick.android.MobclickAgent;

public class OtherPersonActivity extends Activity implements
		OnFooterRefreshListener {
	protected AQuery aq;
	private LinearLayout linearLayout1 = null;
	private LinearLayout linearLayout2 = null;
	private LinearLayout linearLayout3 = null;
	private ScrollView scrollView;
	private Button btn_kanguodeyingpian, btn_shoucangdeyingpian,
			btn_tuijiandeyingpian;
	private TextView text_guanzhunumber, text_funsumber, text_myname;
	LinearLayout guanzhu, fensi;
	private int USE_LINEAR_INTERVAL = 0;
	private int linearlayoutWidth = 0;
	private int linearlayoutHeight = 0;// 根据屏幕的大小来计算每一张图片的宽度
	private int page_count = 6;// 每次加载x张图片
	private int current_page = 0;// 当前页数
	private int index = 0;
	List<String> list;
	public Context context;
	ImageView beijing, head;
	int selectIndex = 1;
	String bitString[] = { "拍照", "相册" };
	private File mCurrentPhotoFile;
	App app;
	private DownLoadService DOWNLOADSERVICE;
	private String UserID;
	ProgressDialog progressBar;
	int bitmaph, bitmapw;
	private ArrayList<String> images_kanguodeyingpian = null;
	private ArrayList<String> images_shoucangdeyingpian = null;
	private ArrayList<String> images_tuijiandeyingpian = null;
	private ArrayList<String> name_kanguodeyingpian = null;
	private ArrayList<String> name_shoucangdeyingpian = null;
	private ArrayList<String> name_tuijiandeyingpian = null;
	private ArrayList<String> images_pro_id_kanguodeyingpian = null;
	private ArrayList<String> images_pro_id_shoucangdeyingpian = null;
	private ArrayList<String> images_pro_id_tuijiandeyingpian = null;
	PullToRefreshView_foot mPullToRefreshView;
	long overPlus = 100;// 判断剩余SD卡剩余MB
	Bitmap BigBitmap;
	AsyncBitmapLoader asyncBitmapLoader = new AsyncBitmapLoader();
	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				addBitmaps(++current_page, page_count, images_kanguodeyingpian,
						name_kanguodeyingpian);
				break;
			case 2:
				addBitmaps(++current_page, page_count,
						images_shoucangdeyingpian, name_shoucangdeyingpian);
				break;
			case 3:
				addBitmaps(++current_page, page_count,
						images_tuijiandeyingpian, name_tuijiandeyingpian);
				break;
			case 11:
				linearLayout1.removeAllViews();
				linearLayout2.removeAllViews();
				linearLayout3.removeAllViews();
				current_page = 0;
				index = 0;
				scrollView.fullScroll(View.FOCUS_UP);
				addBitmaps(current_page, page_count, images_kanguodeyingpian,
						name_kanguodeyingpian);
				break;
			case 12:
				linearLayout1.removeAllViews();
				linearLayout2.removeAllViews();
				linearLayout3.removeAllViews();
				current_page = 0;
				index = 0;
				scrollView.fullScroll(View.FOCUS_UP);
				addBitmaps(current_page, page_count, images_shoucangdeyingpian,
						name_shoucangdeyingpian);
				break;
			case 13:
				linearLayout1.removeAllViews();
				linearLayout2.removeAllViews();
				linearLayout3.removeAllViews();
				current_page = 0;
				index = 0;
				scrollView.fullScroll(View.FOCUS_UP);
				addBitmaps(current_page, page_count, images_tuijiandeyingpian,
						name_tuijiandeyingpian);
				break;
			case 999:
				app.setjujiliebiaoXianshi(1);
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
		setContentView(R.layout.tab4);
		context = this;
		app = (App) getApplicationContext();
		Tools.creat("joy/admin");
		mPullToRefreshView = (PullToRefreshView_foot) findViewById(R.id.act04_main_pull_refresh_view);
		// mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		Bitmap bt = BitmapFactory.decodeResource(getResources(),
				R.drawable.head);
		bitmaph = bt.getHeight();
		bitmapw = bt.getWidth();
		list = new ArrayList<String>();

		beijing = (ImageView) findViewById(R.id.act04_beijing);
		head = (ImageView) findViewById(R.id.act04_hand);
		guanzhu = (LinearLayout) findViewById(R.id.act04_guanzhu);
		fensi = (LinearLayout) findViewById(R.id.act04_fensi);
		btn_kanguodeyingpian = (Button) findViewById(R.id.act04_kanguodeyingpian);
		btn_kanguodeyingpian.setEnabled(false);
		btn_kanguodeyingpian.setBackgroundResource(R.drawable.topleft1);
		btn_shoucangdeyingpian = (Button) findViewById(R.id.act04_shoucangdeyingpian);
		btn_tuijiandeyingpian = (Button) findViewById(R.id.act04_tuijiandeyingpian);
		linearLayout1 = (LinearLayout) findViewById(R.id.act04_linearlayout1);
		linearLayout2 = (LinearLayout) findViewById(R.id.act04_linearlayout2);
		linearLayout3 = (LinearLayout) findViewById(R.id.act04_linearlayout3);
		scrollView = (ScrollView) findViewById(R.id.act04_sco);
		linearlayoutWidth = getWindowManager().getDefaultDisplay().getWidth() / 3;
		linearlayoutHeight = getWindowManager().getDefaultDisplay().getHeight() / 3 - 20;

		text_guanzhunumber = (TextView) findViewById(R.id.act04_guanzhunumber);
		text_funsumber = (TextView) findViewById(R.id.act04_funsumber);
		text_myname = (TextView) findViewById(R.id.act04_myname);
		// veteranyu add

		images_kanguodeyingpian = new ArrayList<String>();
		images_shoucangdeyingpian = new ArrayList<String>();
		images_tuijiandeyingpian = new ArrayList<String>();
		name_kanguodeyingpian = new ArrayList<String>();
		name_shoucangdeyingpian = new ArrayList<String>();
		name_tuijiandeyingpian = new ArrayList<String>();
		images_pro_id_kanguodeyingpian = new ArrayList<String>();
		images_pro_id_shoucangdeyingpian = new ArrayList<String>();
		images_pro_id_tuijiandeyingpian = new ArrayList<String>();

		DOWNLOADSERVICE = app.getService();

		ReturnUserView m_ReturnUserView = DOWNLOADSERVICE.UserView();
		if (m_ReturnUserView != null) {
			UserID = m_ReturnUserView.id;
			text_guanzhunumber.setText(m_ReturnUserView.follow_num);
			text_funsumber.setText(m_ReturnUserView.fan_num);
			text_myname.setText(m_ReturnUserView.nickname);
		}
		GetServiceData();
		// *************************************************************************
		// String
		// path_bg=Environment.getExternalStorageDirectory()+"/joy/admin/bg.png";
		// String
		// path_head=Environment.getExternalStorageDirectory()+"/joy/admin/head.png";
		String path_bg = Constant.PATH_HEAD + "bg.png";
		String path_head = Constant.PATH_HEAD + "head.png";

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 1;
		Bitmap bitmap = BitmapFactory.decodeFile(path_bg, opts);

		BitmapFactory.Options opts1 = new BitmapFactory.Options();
		opts1.inSampleSize = 1;
		Bitmap bitmap2 = BitmapFactory.decodeFile(path_head, opts1);
		if (bitmap != null) {
			Drawable drawable = new BitmapDrawable(bitmap);
			beijing.setBackgroundDrawable(drawable);
		}
		if (bitmap2 != null) {
			Bitmap bitmap3 = Tools.toRoundCorner(bitmap2, 360);
			Bitmap bitmap4 = BitmapZoom.bitmapZoomByWidth(bitmap3, bitmapw);
			head.setImageBitmap(bitmap4);
		}
		addBitmaps(current_page, page_count, images_kanguodeyingpian,
				name_kanguodeyingpian);
		beijing.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent1 = new Intent();
				intent1.setType("image/*");
				intent1.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent1, 100);
			}
		});
		guanzhu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				app.setActivitytype("3");
				Intent intent = new Intent();
				intent.setClass(context, Guanzhuderen.class);
				startActivity(intent);
			}
		});
		fensi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				app.setActivitytype("4");
				Intent intent = new Intent();
				intent.setClass(context, Guanzhuderen.class);
				startActivity(intent);
			}
		});
		btn_kanguodeyingpian.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectIndex = 1;
				btn_kanguodeyingpian.setEnabled(false);
				btn_shoucangdeyingpian.setEnabled(true);
				btn_tuijiandeyingpian.setEnabled(true);
				btn_kanguodeyingpian.setBackgroundResource(R.drawable.topleft1);
				btn_shoucangdeyingpian
						.setBackgroundResource(R.drawable.topbarmid);
				btn_tuijiandeyingpian
						.setBackgroundResource(R.drawable.topbarright);
				Message msg = new Message();
				msg.what = 11;
				handler.sendMessage(msg);

			}
		});
		btn_shoucangdeyingpian.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectIndex = 2;
				btn_kanguodeyingpian.setEnabled(true);
				btn_shoucangdeyingpian.setEnabled(false);
				btn_tuijiandeyingpian.setEnabled(true);
				btn_kanguodeyingpian.setBackgroundResource(R.drawable.topleft);
				btn_shoucangdeyingpian
						.setBackgroundResource(R.drawable.topbarmid1);
				btn_tuijiandeyingpian
						.setBackgroundResource(R.drawable.topbarright);
				Message msg = new Message();
				msg.what = 12;
				handler.sendMessage(msg);

			}
		});
		btn_tuijiandeyingpian.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectIndex = 3;
				btn_kanguodeyingpian.setEnabled(true);
				btn_shoucangdeyingpian.setEnabled(true);
				btn_tuijiandeyingpian.setEnabled(false);
				btn_kanguodeyingpian.setBackgroundResource(R.drawable.topleft);
				btn_shoucangdeyingpian
						.setBackgroundResource(R.drawable.topbarmid);
				btn_tuijiandeyingpian
						.setBackgroundResource(R.drawable.topbarright1);
				Message msg = new Message();
				msg.what = 13;
				handler.sendMessage(msg);

			}
		});
		head.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(getResources().getString(R.string.photostyle))
						.setItems(bitString,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										if (which == 0) {
											Intent intent = new Intent(
													MediaStore.ACTION_IMAGE_CAPTURE);
											if (Tools.hasSdcard()) {
												mCurrentPhotoFile = new File(
														"mnt/sdcard/joy/admin/",
														"head.png");
												intent.putExtra(
														MediaStore.EXTRA_OUTPUT,
														Uri.fromFile(mCurrentPhotoFile));
											}
											startActivityForResult(intent, 1);

										} else {
											Intent intent1 = new Intent();
											intent1.setType("image/*");
											intent1.setAction(Intent.ACTION_GET_CONTENT);
											startActivityForResult(intent1, 200);
										}
									}
								});
				AlertDialog ad = builder.create();
				ad.show();
			}
		});
	}

	@Override
	protected void onDestroy() {
		aq.dismiss();
		Tools.ClearBitmap(BigBitmap);
		super.onDestroy();
	}

	// 设置按钮
	public void Btn_shezhi(View v) {
		Intent intent = new Intent();
		intent.setClass(context, Shezhi.class);
		app.setcontext(context);
		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case 1:
			if (Tools.hasSdcard()) {
				Intent intent = new Intent("com.android.camera.action.CROP");
				intent.setDataAndType(Uri.fromFile(mCurrentPhotoFile),
						"image/*");
				// 设置裁剪
				intent.putExtra("crop", "true");
				// aspectX aspectY 是宽高的比例
				intent.putExtra("aspectX", 1);
				intent.putExtra("aspectY", 1);
				// outputX outputY 是裁剪图片宽高
				intent.putExtra("outputX", bitmapw);
				intent.putExtra("outputY", bitmaph);
				intent.putExtra("return-data", true);
				startActivityForResult(intent, 3);
			} else {
				Toast.makeText(context,
						getResources().getString(R.string.sdcard),
						Toast.LENGTH_SHORT).show();
			}
			break;
		case 3:
			Bundle ex = data.getExtras();
			if (ex != null) {
				Bitmap photo = ex.getParcelable("data");
				Bitmap bitmap = Tools.toRoundCorner(photo, 360);
				Bitmap bitmap2 = BitmapZoom.bitmapZoomByWidth(bitmap, bitmapw);
				Tools.saveMyBitmap("joy/admin", "head.png", bitmap2);
				head.setImageBitmap(bitmap2);
				// Drawable drawable=new BitmapDrawable(bitmap);
				// head.setBackgroundDrawable(drawable);
				// head.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			}
			break;
		case 100:
			try {
				Intent intent = new Intent("com.android.camera.action.CROP");
				intent.setData(data.getData()); // data是图库选取文件传回的参数
				intent.putExtra("crop", "true");
				intent.putExtra("aspectX", 2);
				intent.putExtra("aspectY", 1);
				intent.putExtra("outputX", getWindowManager()
						.getDefaultDisplay().getWidth());
				intent.putExtra("outputY", getWindowManager()
						.getDefaultDisplay().getWidth() / 2);
				intent.putExtra("noFaceDetection", true);
				intent.putExtra("return-data", true);
				startActivityForResult(intent, 101);
			} catch (Exception e) {
				Toast.makeText(context,
						getResources().getString(R.string.error_file),
						Toast.LENGTH_SHORT).show();
			}
			break;
		case 101:
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				Drawable drawable = new BitmapDrawable(photo);
				Tools.saveMyBitmap("joy/admin", "bg.png", photo);
				beijing.setBackgroundDrawable(drawable);
				// beijing.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			}
			break;
		case 200:
			try {
				Intent intent1 = new Intent("com.android.camera.action.CROP");
				intent1.setDataAndType(data.getData(), "image/*"); // data是图库选取文件传回的参数
				intent1.putExtra("crop", "true");
				intent1.putExtra("aspectX", 1);
				intent1.putExtra("aspectY", 1);
				intent1.putExtra("outputX", bitmapw);
				intent1.putExtra("outputY", bitmaph);
				intent1.putExtra("return-data", true);
				startActivityForResult(intent1, 3);
			} catch (Exception e) {
				Toast.makeText(context,
						getResources().getString(R.string.error_file),
						Toast.LENGTH_SHORT).show();
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
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
								switch (selectIndex) {
								case 1:
									app
											.setProdID(images_pro_id_kanguodeyingpian
													.get(index).toString());
									app
											.setPicURL(images_kanguodeyingpian
													.get(index).toString());
									app
											.setPicName(name_kanguodeyingpian
													.get(index).toString());
									break;
								case 2:
									app
											.setProdID(images_pro_id_shoucangdeyingpian
													.get(index).toString());
									app
											.setPicURL(images_shoucangdeyingpian
													.get(index).toString());
									app
											.setPicName(name_shoucangdeyingpian
													.get(index).toString());
									break;
								case 3:
									app
											.setProdID(images_pro_id_tuijiandeyingpian
													.get(index).toString());
									app
											.setPicURL(images_tuijiandeyingpian
													.get(index).toString());
									app
											.setPicName(name_tuijiandeyingpian
													.get(index).toString());
									break;
								}

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

	// 异步加载图片
	public Bitmap setImage(ImageView imageView, String URL) {
		return asyncBitmapLoader.loadBitmap(imageView, URL, linearlayoutWidth,
				new ImageCallBack() {

					@Override
					public void imageLoad(ImageView imageView, Bitmap bitmap) {
						if (bitmap != null) {
							BigBitmap = BitmapZoom.bitmapZoomByWidthA(bitmap,
									linearlayoutWidth);
							imageView.setImageBitmap(BigBitmap);
							RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
									BigBitmap.getWidth(), BigBitmap.getHeight());
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
							BigBitmap = BitmapZoom.bitmapZoomByWidthA(
									Tools.drawableToBitamp(imageDrawable),
									linearlayoutWidth);
							v.setImageBitmap(BigBitmap);
							RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
									BigBitmap.getWidth(), BigBitmap.getHeight());
							layoutParams.setMargins(4, 1, 4, 1);
							v.setLayoutParams(layoutParams);
						} else {
							v.setImageResource(R.drawable.pic_bg);
						}
					}
				});
	}

	@Override
	public void onFooterRefresh(PullToRefreshView_foot view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				Message msg = new Message();
				msg.what = selectIndex;
				handler.sendMessage(msg);
				mPullToRefreshView.onFooterRefreshComplete();
			}
		}, 1000);
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
								public void onClick(DialogInterface dialog,
										int which) {
									DOWNLOADSERVICE.AccountLogout();
									DOWNLOADSERVICE.CloseService();
									app
											.setexit(getString(R.string.exit_true));
									app.SaveExit();
									finish();
									// android.os.Process.killProcess(android.os.Process.myPid());
									// System.exit(0);
								}
							})
					.setNegativeButton(
							getResources().getString(R.string.quxiao),
							new DialogInterface.OnClickListener() {
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
		super.onPause();
		MobclickAgent.onPause(this);
	}

	public void GetServiceData() {
		String str_current_page = Integer.toString(current_page + 1);
		String str_page_count = Integer.toString(page_count);

		ReturnUserWatchs m_ReturnUserWatchs;
		ReturnUserFavorities m_ReturnUserFavorities;
		ReturnUserRecommends m_ReturnUserRecommends;
		if (UserID != null) {
			m_ReturnUserWatchs = DOWNLOADSERVICE.UserWatchs(UserID,
					str_current_page, str_page_count);
			m_ReturnUserFavorities = DOWNLOADSERVICE.UserFavorities(UserID,
					str_current_page, str_page_count);
			m_ReturnUserRecommends = DOWNLOADSERVICE.UserRecommends(UserID,
					str_current_page, str_page_count);
		} else {
			m_ReturnUserWatchs = DOWNLOADSERVICE.UserWatchs(str_current_page,
					str_page_count);
			m_ReturnUserFavorities = DOWNLOADSERVICE.UserFavorities(
					str_current_page, str_page_count);
			m_ReturnUserRecommends = DOWNLOADSERVICE.UserRecommends(
					str_current_page, str_page_count);

		}

		if (m_ReturnUserWatchs.watchs != null)
			for (int i = 0; i < m_ReturnUserWatchs.watchs.length; i++) {
				images_pro_id_kanguodeyingpian.add(i,
						m_ReturnUserWatchs.watchs[i].content_id);
				images_kanguodeyingpian.add(i,
						m_ReturnUserWatchs.watchs[i].content_pic_url);
				name_kanguodeyingpian.add(i,
						m_ReturnUserWatchs.watchs[i].content_name);
			}

		if (m_ReturnUserFavorities.watchs != null)
			for (int i = 0; i < m_ReturnUserFavorities.watchs.length; i++) {

				images_pro_id_shoucangdeyingpian.add(i,
						m_ReturnUserFavorities.watchs[i].content_id);
				images_shoucangdeyingpian.add(i,
						m_ReturnUserFavorities.watchs[i].content_pic_url);
				name_shoucangdeyingpian.add(i,
						m_ReturnUserFavorities.watchs[i].content_name);
			}

		if (m_ReturnUserRecommends.recommends != null)
			for (int i = 0; i < m_ReturnUserRecommends.recommends.length; i++) {

				images_pro_id_tuijiandeyingpian.add(i,
						m_ReturnUserRecommends.recommends[i].content_id);
				images_tuijiandeyingpian.add(i,
						m_ReturnUserRecommends.recommends[i].content_pic_url);
				name_tuijiandeyingpian.add(i,
						m_ReturnUserRecommends.recommends[i].content_name);
			}

	}
}
