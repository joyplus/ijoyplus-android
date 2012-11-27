package com.joy;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.Transformer;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.joy.Service.DownLoadService;
import com.joy.Service.Return.ReturnVideoMovies;
import com.joy.Service.Return.ReturnVideoShows;
import com.joy.Service.Return.ReturnVideoTVs;
import com.joy.Service.Return.ReturnVideoVideo;
import com.joy.Tools.JoyFileExplorer;
import com.joy.Tools.Tools;
import com.mobclick.android.MobclickAgent;

public class Tab1 extends Activity {
	protected AQuery aq;
	Button btn_sousuo, btn_dlna;
	private LinearLayout linearLayout1 = null;
	private LinearLayout linearLayout2 = null;
	private LinearLayout linearLayout3 = null;
	private ScrollView scrollView;
	private int USE_LINEAR_INTERVAL = 0;// 控制图片添加到那一个LinearLayout中
	private int linearlayoutWidth = 0;// 根据屏幕的大小来计算每一张图片的宽度
	private int linearlayoutHeight = 0;// 根据屏幕的大小来计算每一张图片的宽度
	private int page_count = Constant.MAXITEM;// 每次加载x张图片
	private int current_page = 0;// 当前页数
	private int index = 0;// 加载的张数
	// List<String> IMG_list;
	Context context;
	int selectIndex = 1;// 记录在哪一种类型的电影中
	// Bitmap BigBitmap;
	App app;
	private DownLoadService DOWNLOADSERVICE;
	long overPlus = 100;// 判断剩余SD卡剩余MB
	String where = "where_1_1";
	private ArrayList<String> images_dianying = null;
	private ArrayList<String> images_juji = null;
	private ArrayList<String> images_zongyi = null;
	private ArrayList<String> images_shipin = null;
	private ArrayList<String> name_dianying = null;
	private ArrayList<String> name_juji = null;
	private ArrayList<String> name_zongyi = null;
	private ArrayList<String> name_shipin = null;
	private ArrayList<String> dianying_pro_id = null;
	private ArrayList<String> juji_pro_id = null;
	private ArrayList<String> shipin_pro_id = null;
	private ArrayList<String> zongyi_pro_id = null;
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				addBitmaps(++current_page, page_count, images_dianying,
						name_dianying);
				break;
			case 999:
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
		setContentView(R.layout.tab1);
		context = this;
		app = (App) getApplicationContext();

		btn_dlna = (Button) findViewById(R.id.act01_dlna);
		btn_sousuo = (Button) findViewById(R.id.act01_sousuo);
		linearLayout1 = (LinearLayout) findViewById(R.id.act01_linearlayout1);
		linearLayout2 = (LinearLayout) findViewById(R.id.act01_linearlayout2);
		linearLayout3 = (LinearLayout) findViewById(R.id.act01_linearlayout3);
		scrollView = (ScrollView) findViewById(R.id.act01_sco);
		linearlayoutWidth = getWindowManager().getDefaultDisplay().getWidth() / 3;
		linearlayoutHeight = getWindowManager().getDefaultDisplay().getHeight() / 3 - 20;

		// veteranyu add

		images_dianying = new ArrayList<String>();
		images_juji = new ArrayList<String>();
		images_zongyi = new ArrayList<String>();
		images_shipin = new ArrayList<String>();
		name_dianying = new ArrayList<String>();
		name_juji = new ArrayList<String>();
		name_zongyi = new ArrayList<String>();
		name_shipin = new ArrayList<String>();
		dianying_pro_id = new ArrayList<String>();
		juji_pro_id = new ArrayList<String>();
		shipin_pro_id = new ArrayList<String>();
		zongyi_pro_id = new ArrayList<String>();
		DOWNLOADSERVICE = app.getService();
		GetServiceData();
		// *************************************************************************
		addBitmaps(current_page, page_count, images_dianying, name_dianying);

		btn_sousuo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, Sousuo.class);
				startActivity(intent);
			}
		});
		btn_dlna.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, JoyFileExplorer.class);
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
								switch (selectIndex) {
								case 1:
									app
											.setProdID(dianying_pro_id.get(
													index).toString());
									app
											.setPicURL(images_dianying.get(
													index).toString());
									app
											.setPicName(name_dianying
													.get(index).toString());
									break;
								case 2:
									app.setProdID(juji_pro_id
											.get(index).toString());
									app.setPicURL(images_juji
											.get(index).toString());
									app.setPicName(name_juji
											.get(index).toString());
									break;
								case 3:
									app
											.setProdID(zongyi_pro_id.get(index)
													.toString());
									app
											.setPicURL(images_zongyi.get(index)
													.toString());
									app.setPicName(name_zongyi
											.get(index).toString());
									break;
								case 4:
									app
											.setProdID(shipin_pro_id.get(index)
													.toString());
									app
											.setPicURL(images_shipin.get(index)
													.toString());
									app.setPicName(name_shipin
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

	// 保存URL地址，没网络的情况从内存拿之前保存过的地址来显示图片
	public ArrayList<String> SetSaveData(String where, ArrayList<String> URL) {
		if (URL == null)
			return null;
		if (Tools.isNetworkAvailable(context) == false) {
			app.GetImageName(where);
			String Img_Name = app.getIMG_Name();
			URL = Tools.SplitA(Img_Name, "$URL$");
		} else {
			int a = 0;
			String iMG_Name = "";
			for (int i = 0; i < URL.size(); i++) {
				if (a == 0) {
					iMG_Name += URL.get(i).toString();
					a = 1;
				} else {
					iMG_Name += "$URL$" + URL.get(i).toString();
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

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	protected void onStart() {

		super.onStart();

		if (app.getexit() == "true") {
			finish();
		}

	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	public void GetServiceData() {
		async_transformer();
		String str_current_page = Integer.toString(current_page + 1);
		String str_page_count = Integer.toString(page_count);
		int m_length = 0;
		ReturnVideoMovies m_VideoMovies = DOWNLOADSERVICE.VideoMovies(
				str_current_page, str_page_count);
		if (m_VideoMovies.movie != null) {
			m_length = m_VideoMovies.movie.length;
			for (int i = 0; i < m_length; i++) {
				images_dianying.add(current_page * page_count + i,
						m_VideoMovies.movie[i].prod_pic_url);
				dianying_pro_id.add(current_page * page_count + i,
						m_VideoMovies.movie[i].prod_id);

				name_dianying.add(current_page * page_count + i,
						m_VideoMovies.movie[i].prod_name);
				// PRODID = m_VideoMovies.movie[3].prod_id;
			}
		}
		ReturnVideoTVs m_VideoTVs = DOWNLOADSERVICE.VideoTVs(str_current_page,
				str_page_count);
		if (m_VideoTVs.tv != null) {
			m_length = m_VideoTVs.tv.length;
			for (int i = 0; i < m_VideoTVs.tv.length; i++) {
				images_juji.add(current_page * page_count + i,
						m_VideoTVs.tv[i].prod_pic_url);
				juji_pro_id.add(current_page * page_count + i,
						m_VideoTVs.tv[i].prod_id);
				name_juji.add(current_page * page_count + i,
						m_VideoTVs.tv[i].prod_name);
			}
		}

		ReturnVideoShows m_VideoShows = DOWNLOADSERVICE.VideoShows(
				str_current_page, str_page_count);
		if (m_VideoShows.show != null) {
			m_length = m_VideoShows.show.length;
			for (int i = 0; i < m_length; i++) {
				images_zongyi.add(current_page * page_count + i,
						m_VideoShows.show[i].prod_pic_url);
				zongyi_pro_id.add(current_page * page_count + i,
						m_VideoShows.show[i].prod_id);
				name_zongyi.add(current_page * page_count + i,
						m_VideoShows.show[i].prod_name);
			}
		}
		ReturnVideoVideo m_VideoVideo = DOWNLOADSERVICE.VideoVideo(
				str_current_page, str_page_count);
		if (m_VideoVideo.video != null) {
			m_length = m_VideoVideo.video.length;
			for (int i = 0; i < m_length; i++) {
				images_shipin.add(current_page * page_count + i,
						m_VideoVideo.video[i].prod_pic_url);
				shipin_pro_id.add(current_page * page_count + i,
						m_VideoVideo.video[i].prod_id);
				name_shipin.add(current_page * page_count + i,
						m_VideoVideo.video[i].prod_name);
			}
		}

	}

	private static class GsonTransformer implements Transformer {

		public <T> T transform(String url, Class<T> type, String encoding,
				byte[] data, AjaxStatus status) {
			Gson g = new Gson();
			return g.fromJson(new String(data), type);
		}
	}

	public void async_transformer() {

		String url = Constant.BASE_URL + "video_shows" + "?app_key="
				+ Constant.APPKEY + "&page_num=1&page_size=30";

		aq = new AQuery(this);
		aq.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {

			@Override
			public void callback(String url, JSONObject json, AjaxStatus status) {
				ObjectMapper mapper = new ObjectMapper();
				ReturnVideoMovies m_r;
				try {
					m_r = mapper.readValue(json.toString(),
							ReturnVideoMovies.class);
				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int i = 0;
				// showResult(json);

			}
		});

	}
}
