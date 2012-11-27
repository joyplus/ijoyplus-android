package com.joy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

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

import com.joy.Service.DownLoadService;
import com.joy.Service.Return.ReturnUserPrestiges;
import com.joy.Service.Return.ReturnValue;
import com.joy.Tools.AsyncBitmapLoader;
import com.joy.Tools.AsyncBitmapLoader.ImageCallBack;
import com.joy.Tools.AsyncImageLoader;
import com.joy.Tools.Tools;
import com.joy.view.PullToRefreshView_foot;
import com.joy.view.PullToRefreshView_foot.OnFooterRefreshListener;
import com.mobclick.android.MobclickAgent;

public class Darentuijian extends Activity implements OnFooterRefreshListener {
	Context context;
	Button btn_allguanzhu, btn_xiayibu;
	RelativeLayout btn_back;;
	ProgressDialog progressBar;
	private LinearLayout linearLayout1 = null;
	private LinearLayout linearLayout2 = null;
	private LinearLayout linearLayout3 = null;
	private int USE_LINEAR_INTERVAL = 0;
	private int linearlayoutWidth = 0;// 根据屏幕的大小来计算每一张图片的宽度
	private int page_count = 6;// 每次加载x张图片
	private int current_page = 0;// 当前页数
	private int index = 0;// 加载的张数
	Bitmap bitmap2;
	long overPlus = 100;// 判断剩余SD卡剩余MB
	App app;
	private DownLoadService DOWNLOADSERVICE;
	Intent intent;
	List<String> list;
	private String images[] = {
			"http://www.qqtai.com/qqhead/UploadFiles_3178/200901/2009011503573742.jpg",
			"http://www.qqtai.com/qqhead/uploadfiles_3178/200901/2009011503573886.jpg",
			"http://www.qqtai.com/qqhead/UploadFiles_3178/200901/2009011503573759.jpg",
			"http://www.2qqtouxiang.cn/uploads/allimg/110903/1_110903203627_14.jpg",
			"http://www.2qqtouxiang.cn/uploads/allimg/110903/1_110903203627_6.jpg",
			"http://www.2qqtouxiang.cn/uploads/allimg/110903/1_110903203627_7.jpg",
			"http://www.2qqtouxiang.cn/uploads/allimg/110903/1_110903203627_2.jpg",
			"http://www.2qqtouxiang.cn/uploads/allimg/110903/1_110903203627_4.jpg",
			"http://www.2qqtouxiang.cn/uploads/allimg/110903/1_110903203627_1.jpg" };
	private String names[] = { "名字1", "名字2", "名字3", "名字4", "名字5", "名字6", "名字7",
			"名字8", "名字9" };
	private String user_id[] = new String[9];
	PullToRefreshView_foot mPullToRefreshView;
	AsyncBitmapLoader asyncBitmapLoader = new AsyncBitmapLoader();
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1500:
				addBitmaps(++current_page, page_count, images);
				break;
			case 300:
				progressBar.dismiss();
				app.setActivitytype("");
				/*
				 * Intent intent=new Intent(); intent.setClass(context,
				 * JoyActivity.class); startActivity(intent);
				 */
				finish();
				break;
			case 999:
				Intent intent1 = new Intent();
				intent1.setClass(context, OtherPersonActivity.class);
				startActivity(intent1);
				progressBar.dismiss();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.darentuijian);
		app = (App) getApplicationContext();
		context = this;
		app.setexit(getString(R.string.exit_false));
		app.SaveExit();
		list = new ArrayList<String>();

		mPullToRefreshView = (PullToRefreshView_foot) findViewById(R.id.darentuijian_main_pull_refresh_view);
		mPullToRefreshView.setOnFooterRefreshListener(this);

		linearLayout1 = (LinearLayout) findViewById(R.id.darentuijian_linearlayout1);
		linearLayout2 = (LinearLayout) findViewById(R.id.darentuijian_linearlayout2);
		linearLayout3 = (LinearLayout) findViewById(R.id.darentuijian_linearlayout3);
		linearlayoutWidth = getWindowManager().getDefaultDisplay().getWidth() / 3;

		btn_back = (RelativeLayout) findViewById(R.id.darentuijian_back);
		btn_allguanzhu = (Button) findViewById(R.id.darentuijian_guanzhu);
		btn_xiayibu = (Button) findViewById(R.id.darentuijian_xiayibu);
		if (app.getActivitytype().equals("1")) {
			btn_back.setVisibility(View.GONE);
		} else if (app.getActivitytype().equals("2")) {
			btn_xiayibu.setVisibility(View.GONE);
		}

		// veteranyu add
		DOWNLOADSERVICE = app.getService();
		intent = getIntent();
		boolean ifLogin = intent.getBooleanExtra("Login", false);
		if (ifLogin) {
			SaveInfo();
			app.IsLogin = true;
		}
		// *************************************************************************
		images = SetSaveData("where_daren", images);
		addBitmaps(current_page, page_count, images);
		btn_xiayibu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
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
		});
		btn_allguanzhu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// veteranyu add
				for (int i = 0; i < user_id.length; i++) {
					DOWNLOADSERVICE.Friendfollow(user_id[i]);
				}
			}
		});
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				app.setActivitytype("");
				finish();
			}
		});
	}

	@Override
	protected void onDestroy() {
		Tools.ClearBitmap(bitmap2);
		super.onDestroy();
	}

	// 界面中加载图片
	private void addBitmaps(int pageindex, int pagecount, String img[]) {
		GetServiceData();
		list = Arrays.asList(img);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		try {
			for (int i = index; i < pagecount * (pageindex + 1)
					&& i < img.length; i++) {
				try {
					final View view = inflater
							.inflate(R.layout.darenview, null);
					LinearLayout rll = (LinearLayout) view
							.findViewById(R.id.RelativeLayout03);
					final ImageView imageView = (ImageView) view
							.findViewById(R.id.darenview_image);
					TextView textView = (TextView) view
							.findViewById(R.id.darenview_text);
					Button button = (Button) view
							.findViewById(R.id.darenview_button);
					// 没有SD卡或者SD卡容量小于100MB直接显示网络图片
					if (Tools.hasSdcard() == false
							|| (Tools.getAvailableStore("/mnt/sdcard/joy/") >> 20) < overPlus) {
						new AsyncImageLoader().loadDrawable(list.get(index),
								new AsyncImageLoader.ImageCallback() {
									public void imageLoaded(
											Drawable imageDrawable) {
										if (imageDrawable != null) {
											Bitmap myBitmap = BitmapFactory
													.decodeResource(
															getResources(),
															R.drawable.daren);
											bitmap2 = Tools.toRoundCorner(
													Tools.drawableToBitamp(imageDrawable),
													360);
											imageView.setImageBitmap(bitmap2);
											LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
													myBitmap.getWidth() - 2,
													(myBitmap.getHeight() - 2) + 40);
											params.setMargins(5, 1, 5, 1);
											imageView.setLayoutParams(params);
										} else {
											imageView
													.setImageResource(R.drawable.daren);
										}
									}
								});
					} else {
						Bitmap bitmap = asyncBitmapLoader.loadBitmap(imageView,
								list.get(index), linearlayoutWidth,
								new ImageCallBack() {

									@Override
									public void imageLoad(ImageView imageView,
											Bitmap bitmap) {
										Bitmap myBitmap = BitmapFactory
												.decodeResource(getResources(),
														R.drawable.daren);
										bitmap2 = Tools.toRoundCorner(bitmap,
												360);
										imageView.setImageBitmap(bitmap2);
										LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
												myBitmap.getWidth() - 2,
												(myBitmap.getHeight() - 2) + 40);
										params.setMargins(5, 1, 5, 1);
										imageView.setLayoutParams(params);
									}
								});

						if (bitmap == null) {
							imageView.setImageResource(R.drawable.daren);
						} else {
							Bitmap myBitmap = BitmapFactory.decodeResource(
									getResources(), R.drawable.daren);
							bitmap2 = Tools.toRoundCorner(bitmap, 360);
							imageView.setImageBitmap(bitmap2);
							LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
									myBitmap.getWidth() - 2,
									(myBitmap.getHeight() - 2) + 40);
							params.setMargins(5, 1, 5, 1);
							imageView.setLayoutParams(params);
						}
					}
					textView.setText(names[index]);
					button.setId(index * 100);
					// 点击了头像，用到ontouch方法是为了有点击效果
					imageView.setOnTouchListener(new OnTouchListener() {

						@Override
						public boolean onTouch(View v, MotionEvent event) {
							switch (event.getAction()) {
							case MotionEvent.ACTION_UP:
								int index = (Integer) v.getTag();
								Toast.makeText(context, "" + (index + 1),
										Toast.LENGTH_SHORT).show();
								Tools.changeLight(imageView, 0);
								progressBar = ProgressDialog
										.show(context,
												getResources().getString(
														R.string.shaohou),
												getResources()
														.getString(
																R.string.pull_to_refresh_footer_refreshing_label));
								new Handler().postDelayed(new Runnable() {
									@Override
									public void run() {
										Message msg = new Message();
										msg.what = 999;
										handler.sendMessage(msg);
									}
								}, 1000);
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
					// 关注按钮
					button.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							int id = v.getId() / 100;
							ReturnValue m_ReturnValue = DOWNLOADSERVICE
									.Friendfollow(user_id[id]);
							Toast.makeText(context, m_ReturnValue.res_desc,
									Toast.LENGTH_SHORT).show();
							// Toast.makeText(context, ""+(id/100),
							// Toast.LENGTH_SHORT).show();
						}
					});
					imageView.setTag(new Integer(index));
					switch (USE_LINEAR_INTERVAL) {
					case 0:
						linearLayout1.addView(rll);
						break;
					case 1:
						linearLayout2.addView(rll);
						break;
					case 2:
						linearLayout3.addView(rll);
						break;
					default:
						break;
					}
					index++;
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

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (app.getActivitytype().equals("2")) {
				app.setActivitytype("");
				finish();
			}
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

	public void GetServiceData() {
		String str_current_page = Integer.toString(current_page + 1);
		String str_page_count = Integer.toString(page_count);
		ReturnUserPrestiges m_ReturnUserPrestiges = DOWNLOADSERVICE
				.UserPrestiges(str_current_page, str_page_count);
		if (m_ReturnUserPrestiges.prestiges != null)
			for (int i = 0; i < m_ReturnUserPrestiges.prestiges.length
					&& i < page_count; i++) {

				user_id[i] = m_ReturnUserPrestiges.prestiges[i].id;
				names[i] = m_ReturnUserPrestiges.prestiges[i].nickname;
				images[i] = m_ReturnUserPrestiges.prestiges[i].user_pic_url;
			}
	}

	public void SaveInfo() {
		String name = null;
		String email = null;
		String password = null;
		// 新浪token
		if (app.getlogin_where().equals(
				getString(R.string.sinawb))) {
			app.setAccessToken(getIntent().getStringExtra(
					"token"));
			app.setExpires_in(getIntent().getStringExtra(
					"expires_in"));
			app.SaveAccessToken();
			app.SaveExpires_in();
			System.out.println("getIntent().getStringExtra(\"token\")====>"
					+ getIntent().getStringExtra("token"));
			System.out
					.println("getIntent().getStringExtra(\"expires_in\")====>"
							+ getIntent().getStringExtra("expires_in"));
			if (intent == null)
				return;
			name = getIntent().getStringExtra("uid");
			email = name + "@sina.com";
			password = name;
			// 1. save to local
			app.SaveIjoyID(email, password);
			// 2.Register
			ReturnValue m_ReturnValue = DOWNLOADSERVICE.AccountRegister(email,
					password, name);
			if (m_ReturnValue.res_code.trim().equalsIgnoreCase("00000")) {
				// 3. 获取头像并上传
				UploadSinaHeadUrl(getIntent().getStringExtra("token"),
						getIntent().getStringExtra("uid"));
				// 4.绑定第三方账号 account/bindAccount
				DOWNLOADSERVICE.AccountBindAccount(name, "1");
				Toast.makeText(context, m_ReturnValue.res_code,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, m_ReturnValue.res_desc,
						Toast.LENGTH_SHORT).show();
			}
		}
		// QqTOKEN
		else if (app.getlogin_where().equals(
				getString(R.string.tencent))) {
			name = app.GetOpenID();
			email = name + "@qq.com";
			password = name;
			app.setQQ_Token(getIntent()
					.getStringExtra("token"));
			app.SaveQQAccessToken();
			// app.GetQQAccessToken();
			// System.out.println("token:"+app.getQQ_Token());
			// 1. save to local
			app.SaveIjoyID(email, password);
			// 2.Register
			ReturnValue m_ReturnValue = DOWNLOADSERVICE.AccountRegister(email,
					password, name);
			if (m_ReturnValue.res_code.trim().equalsIgnoreCase("00000")) {
				// 2. 获取头像并上传
				UploadQQHeadUrl(getIntent().getStringExtra("UserHeadUrl"));
				// 3.绑定第三方账号 account/bindAccount
				DOWNLOADSERVICE.AccountBindAccount(name, "2");
				Toast.makeText(context, m_ReturnValue.res_code,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, m_ReturnValue.res_desc,
						Toast.LENGTH_SHORT).show();
			}
		}
		// 3.LOGIN
		DOWNLOADSERVICE.AccountLogin(email, password);
	}

	private boolean UploadSinaHeadUrl(String access_token, String uid) {
		String m_GetURL = "https://api.weibo.com/2/users/show.json?access_token="
				+ access_token + "&uid=" + uid;
		String rltString = DOWNLOADSERVICE.OnHttpGet(m_GetURL);
		String head_url = null;
		try {
			JSONObject json = new JSONObject(rltString);
			head_url = json.optString("avatar_large");
			if (head_url.length() > 0) {
				if (DOWNLOADSERVICE.UserUpdatePicUrl(head_url))
					return true;
			}
		} catch (JSONException e) {
			// 不处理
		}
		return false;
	}

	private boolean UploadQQHeadUrl(String head_url) {
		if (head_url.length() > 0) {
			if (DOWNLOADSERVICE.UserUpdatePicUrl(head_url))
				return true;
		}
		return false;
	}
}
