/*
 * Copyright 2011 Sina.
 *
 * Licensed under the Apache License and Weibo License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.open.weibo.com
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.joy.weibo.net;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joy.App;
import com.joy.Constant;
import com.joy.DetailActivity;
import com.joy.R;
import com.joy.Service.DownLoadService;
import com.joy.Tools.AsyncBitmapLoader;
import com.joy.Tools.BitmapZoom;
import com.joy.Tools.MyEditText;
import com.joy.Tools.Tools;
import com.joy.weibo.net.AsyncWeiboRunner.RequestListener;
import com.mobclick.android.MobclickAgent;
import com.tencent.tauth.TAuthView;
import com.tencent.tauth.TencentOpenAPI;
import com.tencent.tauth.bean.OpenId;
import com.tencent.tauth.http.Callback;
import com.tencent.tauth.http.TDebug;

/**
 * A dialog activity for sharing any text or image message to weibo. Three
 * parameters , accessToken, tokenSecret, consumer_key, are needed, otherwise a
 * WeiboException will be throwed.
 * 
 * ShareActivity should implement an interface, RequestListener which will
 * return the request result.
 * 
 * @author ZhangJie (zhangjie2@staff.sina.com.cn)
 */

public class ShareActivity extends Activity implements OnClickListener,
		RequestListener {
	private TextView mTextNum, title_text;
	private Button share_pinglun, xinlang_button, qq_button;
	private MyEditText fx_pl_edit;
	private String mPicPath = "";
	private String mContent = "";
	private String mAccessToken = "";
	private String mTokenSecret = "";
	Dialog dialog;
	Context context;
	int sina_choice, qq_choice;
	AsyncBitmapLoader asyncBitmapLoader = new AsyncBitmapLoader();
	public String mOpenId;// 申请时分配的appid
	public static final String EXTRA_WEIBO_CONTENT = "com.weibo.android.content";
	public static final String EXTRA_PIC_URI = "com.weibo.android.pic.uri";
	public static final String EXTRA_ACCESS_TOKEN = "com.weibo.android.accesstoken";
	public static final String EXTRA_TOKEN_SECRET = "com.weibo.android.token.secret";

	public static final int WEIBO_MAX_LENGTH = 120;
	App application;
	private DownLoadService DOWNLOADSERVICE;
	String PROD_ID = null;
	int linearlayout_width = 0;
	int QQ_SS_Count = 0;
	private AuthReceiver receiver;
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.getData().getInt("msg")) {
			case 1:
				share_pinglun.setEnabled(true);
				break;
			}
		}
	};
	Bitmap newBT = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.setContentView(R.layout.share_mblog_view);
		context = this;
		linearlayout_width = getWindowManager().getDefaultDisplay().getWidth() / 3;
		application = (App) getApplicationContext();
		Intent in = this.getIntent();
		xinlang_button = (Button) findViewById(R.id.xinlang_button);
		xinlang_button.setOnClickListener(this);
		qq_button = (Button) findViewById(R.id.qq_button);
		qq_button.setOnClickListener(this);
		// 获取新浪上传的一切值
		if (Weibo.getInstance().getAccessToken() != null) {
			System.out.println(getString(R.string.sinawb));
			mPicPath = in.getStringExtra(EXTRA_PIC_URI);
			mContent = "";
			mAccessToken = in.getStringExtra(EXTRA_ACCESS_TOKEN);
			mTokenSecret = in.getStringExtra(EXTRA_TOKEN_SECRET);
			xinlang_button
					.setBackgroundResource(R.drawable.synchronous_sina_true);
			sina_choice = 1;
			AccessToken accessToken = new AccessToken(mAccessToken,
					mTokenSecret);
			Weibo weibo = Weibo.getInstance();
			weibo.setAccessToken(accessToken);
		} else if (application.getQQ_Token().trim().length() != 0) {
			qq_button.setBackgroundResource(R.drawable.synchronous_qq_true);
			qq_choice = 1;
			mContent = "";
			mPicPath = in.getStringExtra(EXTRA_PIC_URI);
		} else {
			sina_choice = 0;
			qq_choice = 0;
			mContent = "";
			mPicPath = in.getStringExtra(EXTRA_PIC_URI);
		}
		// 获取新浪上传的一切值
		RelativeLayout login_goback = (RelativeLayout) this
				.findViewById(R.id.login_goback);
		login_goback.setOnClickListener(this);
		share_pinglun = (Button) this.findViewById(R.id.share_pinglun);
		share_pinglun.setText(application.getButton_Name());
		share_pinglun.setOnClickListener(this);

		mTextNum = (TextView) this.findViewById(R.id.tv_text_limit);
		title_text = (TextView) findViewById(R.id.title_text);
		title_text.setText(application.getButton_Name());
		fx_pl_edit = (MyEditText) this.findViewById(R.id.fx_pl_edit);
		ImageView imageView = (ImageView) findViewById(R.id.image_moves);
		imageView.setVisibility(View.GONE);
		System.out.println("mPicPath====>" + mPicPath);
		// if (TextUtils.isEmpty(this.mPicPath)) {
		// f = null;
		// }
		// else
		// {
		// f = new File(this.mPicPath);
		// }
		if (application.getButton_Name().equals(getString(R.string.fenxiang))
				&& mPicPath != null) {
			imageView.setVisibility(View.VISIBLE);
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = 1;
			Bitmap bitmap = BitmapFactory.decodeFile(mPicPath, opts);
			/*
			 * Bitmap bitmap = asyncBitmapLoader.loadBitmap(imageView, mPicPath,
			 * linearlayout_width, new ImageCallBack() {
			 * 
			 * @Override public void imageLoad(ImageView imageView, Bitmap
			 * bitmap) { if (bitmap != null) {
			 * newBT=BitmapZoom.bitmapZoomByWidth(bitmap, linearlayout_width);
			 * imageView.setImageBitmap(newBT); } }
			 * 
			 * });
			 */
			if (bitmap != null) {
				newBT = BitmapZoom
						.bitmapZoomByWidth(bitmap, linearlayout_width);
				imageView.setImageBitmap(newBT);
			}
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					fx_pl_edit.setFocusable(true);
					fx_pl_edit.setFocusableInTouchMode(true);
					fx_pl_edit.requestFocus();
					InputMethodManager inputManager = (InputMethodManager) fx_pl_edit
							.getContext().getSystemService(
									Context.INPUT_METHOD_SERVICE);
					inputManager.showSoftInput(fx_pl_edit, 0);
				}
			});
		}
		fx_pl_edit.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String mText = fx_pl_edit.getText().toString();
				int len = mText.length();
				if (len <= WEIBO_MAX_LENGTH) {
					len = WEIBO_MAX_LENGTH - len;
					if (!share_pinglun.isEnabled())
						share_pinglun.setEnabled(true);
				} else {
					len = len - WEIBO_MAX_LENGTH;
					if (share_pinglun.isEnabled())
						share_pinglun.setEnabled(false);
				}
				mTextNum.setText(getString(R.string.canenter) + len
						+ getString(R.string.word));
			}
		});
		fx_pl_edit.setText(mContent);

		// veteranyu add
		DOWNLOADSERVICE = application.getService();
		PROD_ID = in.getStringExtra("PROD_ID");
		// *************************************************************************
	}

	// 界面按键响应做的事情
	@Override
	public void onClick(View v) {
		int viewId = v.getId();

		if (viewId == R.id.login_goback) {
			Intent intent = new Intent();
			intent.setClass(this, DetailActivity.class);
			startActivity(intent);
			finish();
		} else if (viewId == R.id.share_pinglun) {
			String str = fx_pl_edit.getText().toString();
			// veteranyu add
			if (DOWNLOADSERVICE.ProgramComment(PROD_ID, fx_pl_edit.getText()
					.toString()))
				Toast.makeText(context, "评论成功", Toast.LENGTH_SHORT).show();
			// *************************************************************************
			share_pinglun.setEnabled(false);
			if (qq_choice == 0 && sina_choice == 0) {
				share_pinglun.setEnabled(true);
				Toast.makeText(context, getString(R.string.send_sucess),
						Toast.LENGTH_SHORT).show();
				return;
			}
			dialog = ProgressDialog.show(context, getString(R.string.tishi),
					getString(R.string.wait));
			new Thread(new uploadThread()).start();

		} else if (viewId == R.id.xinlang_button) {
			if (sina_choice == 0) {
				Token tk = Weibo.getInstance().getAccessToken();
				// if (tk == null) {
				// tk = application.getSinaToken();
				// }
				if (tk == null) {
					Weibo weibo = Weibo.getInstance();
					weibo.setupConsumerConfig(Constant.SINA_CONSUMER_KEY,
							Constant.SINA_CONSUMER_SECRET);
					// Oauth2.0
					// 隐式授权认证方式
					weibo.setRedirectUrl("http://www.sina.com");// 此处回调页内容应该替换为与appkey对应的应用回调页
					// 对应的应用回调页可在开发者登陆新浪微博开发平台之后，
					// 进入我的应用--应用详情--应用信息--高级信息--授权设置--应用回调页进行设置和查看，
					// 应用回调页不可为空
					weibo.authorize(ShareActivity.this,
							new AuthDialogListener());
				} else {
					sina_choice = 1;
					xinlang_button
							.setBackgroundResource(R.drawable.synchronous_sina_true);
				}
			} else {
				sina_choice = 0;
				xinlang_button
						.setBackgroundResource(R.drawable.synchronous_sina_false);
			}
		} else if (viewId == R.id.qq_button) {
			if (qq_choice == 0) {
				String str_token = application.getQQ_Token().trim();
				// if (str_token.length()==0) {
				// str_token = application.getQQ_Token().trim();
				// }
				if (str_token.length() == 0) {
					registerIntentReceivers();
					auth(Constant.TECENTAPPID, "_self");
				} else {
					qq_choice = 1;
					qq_button
							.setBackgroundResource(R.drawable.synchronous_qq_true);
				}
			} else {
				qq_choice = 0;
				qq_button
						.setBackgroundResource(R.drawable.synchronous_qq_false);
			}

		}
	}

	// 新浪分享包含图片
	private String upload(Weibo weibo, String source, String file,
			String status, String lon, String lat) throws WeiboException {
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("source", source);
		bundle.add("pic", file);
		bundle.add("status", status);
		if (!TextUtils.isEmpty(lon)) {
			bundle.add("lon", lon);
		}
		if (!TextUtils.isEmpty(lat)) {
			bundle.add("lat", lat);
		}
		String rlt = "";
		String url = Weibo.SERVER + "statuses/upload.json";
		AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(weibo);
		weiboRunner.request(this, url, bundle, Utility.HTTPMETHOD_POST, this);

		return rlt;
	}

	// 新浪分享不包含图片
	private String update(Weibo weibo, String source, String status,
			String lon, String lat) throws MalformedURLException, IOException,
			WeiboException {
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("source", source);
		bundle.add("status", status);
		if (!TextUtils.isEmpty(lon)) {
			bundle.add("lon", lon);
		}
		if (!TextUtils.isEmpty(lat)) {
			bundle.add("lat", lat);
		}
		String rlt = "";
		String url = Weibo.SERVER + "statuses/update.json";
		AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(weibo);
		weiboRunner.request(this, url, bundle, Utility.HTTPMETHOD_POST, this);
		return rlt;
	}

	// QQ分享包含图片
	public void uploadPic(String path) {
		// application.setlogin_where(getString(R.string.tencent));
		// application.GetAccessToken();
		// if (application.getAccessToken().length()==0) {
		// Toast.makeText(context, getString(R.string.please_login),
		// Toast.LENGTH_SHORT).show();
		// return;
		// }
		Bundle bundle = null;
		bundle = new Bundle();
		File f = new File(path);
		byte[] buff = null;
		try {

			InputStream is = new FileInputStream(f);
			ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				outSteam.write(buffer, 0, len);
			}
			outSteam.close();
			is.close();
			buff = outSteam.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 发布照片所需要的
		bundle.putByteArray("picture", buff);// 必须.上传照片的文件名以及图片的内容（在发送请求时，图片内容以二进制数据流的形式发送，见下面的请求示例），注意照片名称不能超过30个字符。
		bundle.putString("photodesc", mContent);// 照片描述，注意照片描述不能超过200个字符。
		// bundle.putString("title", "QQ登陆SDK：UploadPic测试" +
		// System.currentTimeMillis() + ".png");//照片的命名，必须以.jpg, .gif, .png,
		// .jpeg, .bmp此类后缀结尾。
		// bundle.putString("albumid",
		// "564546-asdfs-feawfe5545-45454");//相册id，不填则传到默认相册
		// bundle.putString("comment", ("QQ登陆SDK：测试吸血鬼日记"));
		bundle.putString("x", "0-360");// 照片拍摄时的地理位置的经度。请使用原始数据（纯经纬度，0-360）。
		bundle.putString("y", "0-360");// 照片拍摄时的地理位置的纬度。请使用原始数据（纯经纬度，0-360）。
		// 取得QQ的AccessToken值和openid值
		// application.GetOpenID();
		TencentOpenAPI.uploadPic(application.getQQ_Token(),
				Constant.TECENTAPPID, application.getOpenID(), bundle,
				new Callback() {

					@Override
					public void onSuccess(final Object obj) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								dialog.dismiss();
								qq_choice = 0;
								qq_button
										.setBackgroundResource(R.drawable.synchronous_qq_false);
								Toast.makeText(context,
										getString(R.string.send_sucess),
										Toast.LENGTH_LONG).show();
							}
						});
					}

					@Override
					public void onFail(final int ret, final String msg) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								dialog.dismiss();
								Toast.makeText(context,
										getString(R.string.send_failed),
										Toast.LENGTH_LONG).show();
							}
						});
					}
				});
	}

	// QQ分享不包含图片
	public void uploadQQ() {
		// 判断token值是否存在
		// application.setlogin_where(getString(R.string.tencent));
		// application.GetAccessToken();
		// if (application.getAccessToken().length()==0) {
		// Toast.makeText(context, getString(R.string.please_login),
		// Toast.LENGTH_SHORT).show();
		// return;
		// }
		// 发布评论所需要的
		Bundle bundle = null;
		bundle = new Bundle();
		// bundle.putString("richtype", "2");//发布心情时引用的信息的类型。1表示图片； 2表示网页；
		// 3表示视频。
		// bundle.putString("richval", ("http://www.qq.com" + "#" +
		// System.currentTimeMillis()));//发布心情时引用的信息的值。有richtype时必须有richval
		bundle.putString("con", mContent);// 发布的心情的内容。
		// bundle.putString("lbs_nm","广东省深圳市南山区高新科技园腾讯大厦");//地址文
		// bundle.putString("lbs_x","0-360");//经度。请使用原始数据（纯经纬度，0-360）。
		// bundle.putString("lbs_y","0-360");//纬度。请使用原始数据（纯经纬度，0-360）。
		// bundle.putString("lbs_id","360");//地点ID。lbs_id与lbs_idnm通常一起使用，来明确标识一个地址。
		// bundle.putString("lbs_idnm","腾讯");//地点名称。lbs_id与lbs_idnm通常一起使用，来明确标识一个地址。

		// 取得QQ的AccessToken值和openid值
		// application.GetOpenID();
		TencentOpenAPI.addTopic(application.getQQ_Token(),
				Constant.TECENTAPPID, application.getOpenID(), bundle,
				new Callback() {
					// 因腾讯发表文字会连续调用2次返回，故我们只捕捉第2次的
					@Override
					public void onSuccess(final Object obj) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								QQ_SS_Count++;
								if (QQ_SS_Count == 2) {
									System.out.println("sucess");
									dialog.dismiss();
									qq_choice = 0;
									qq_button
											.setBackgroundResource(R.drawable.synchronous_qq_false);
									Toast.makeText(ShareActivity.this,
											getString(R.string.send_sucess),
											Toast.LENGTH_LONG).show();
								}
								// mActivity.showMessage("发表说说返回数据",
								// obj.toString());
							}
						});
					}

					@Override
					public void onFail(final int ret, final String msg) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								if (ret == 0) {
									QQ_SS_Count++;
								} else {
									dialog.dismiss();
									Toast.makeText(ShareActivity.this,
											getString(R.string.send_failed),
											Toast.LENGTH_LONG).show();
								}
								System.out.println("false" + "/ret:" + ret);
								if (QQ_SS_Count == 2) {
									dialog.dismiss();
									Toast.makeText(ShareActivity.this,
											getString(R.string.send_failed),
											Toast.LENGTH_LONG).show();
								}
								// TDebug.msg(ret + ": " + msg, mActivity);
							}
						});
					}
				});
	}

	// 新浪成功或失败返回
	@Override
	public void onComplete(String response) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (qq_choice == 1) {
					sina_choice = 0;
					xinlang_button
							.setBackgroundResource(R.drawable.synchronous_sina_false);
					new Thread(new uploadThread()).start();
				} else {
					dialog.dismiss();
					sina_choice = 0;
					xinlang_button
							.setBackgroundResource(R.drawable.synchronous_sina_false);
					System.out.println("sucess2");
					Toast.makeText(ShareActivity.this,
							getString(R.string.send_sucess), Toast.LENGTH_LONG)
							.show();
				}
			}
		});
		// share_pinglun.setEnabled(true);
		// this.finish();
	}

	@Override
	public void onIOException(IOException e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(final WeiboException e) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				dialog.dismiss();
				System.out.println("false2");
				System.out.println(e.getMessage().toString());
				Toast.makeText(
						ShareActivity.this,
						String.format(
								ShareActivity.this
										.getString(R.string.send_failed)
										+ ":%s", e.getMessage()),
						Toast.LENGTH_LONG).show();
			}
		});
		// share_pinglun.setEnabled(true);

	}

	// 因有的机器点击评论或发送后有卡机的情况，故写一个线程控制
	class uploadThread implements Runnable {

		@Override
		public void run() {
			try {
				// Thread.sleep(1000*10);
				doShare();
				Message msg = handler.obtainMessage();
				Bundle b = new Bundle();
				b.putInt("msg", 1);
				msg.setData(b);
				handler.sendMessage(msg);
			} catch (Exception e) {
				System.out.println(1231231);
				dialog.dismiss();
				share_pinglun.setEnabled(true);
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // sleep 1000ms
		}
	}

	// 单击分享或评论后所做的事情（目前只有新浪）
	void doShare() {
		if (sina_choice == 1) {
			System.out.println("SINA");
			Weibo weibo = Weibo.getInstance();
			try {
				System.out.println("weibo.getAccessToken().getToken()===>"
						+ weibo.getAccessToken().getToken());
				System.out.println(!TextUtils.isEmpty((String) (weibo
						.getAccessToken().getToken())));
				if (!TextUtils.isEmpty((String) (weibo.getAccessToken()
						.getToken()))) {
					this.mContent = fx_pl_edit.getText().toString();
					if (!TextUtils.isEmpty(mPicPath)) {
						// uploadPic(mPicPath);
						System.out.println("pic");
						// veteranyu modified must upload local file.
						upload(weibo, Weibo.getAppKey(), this.mPicPath,
								this.mContent, "", "");

					} else {
						// Just update a text weibo!
						// System.out.println("Weibo.getAppKey()===>"+Weibo.getAppKey());
						// System.out.println("mContent====>"+mContent);
						// uploadQQ();
						System.out.println("no pic");
						update(weibo, Weibo.getAppKey(), mContent, "", "");
					}
				} else {
					dialog.dismiss();
					// share_pinglun.setEnabled(true);
					Toast.makeText(this, this.getString(R.string.please_login),
							Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				// dialog.dismiss();
				e.printStackTrace();
			}
		} else if (qq_choice == 1) {
			System.out.println("QQ");
			QQ_SS_Count = 0;
			this.mContent = fx_pl_edit.getText().toString();
			if (!TextUtils.isEmpty(mPicPath)) {
				System.out.println(1);
				uploadPic(mPicPath);
				// upload(weibo, Weibo.getAppKey(), this.mPicPath,
				// this.mContent, "", "");

			} else {
				// Just update a text weibo!
				// System.out.println("Weibo.getAppKey()===>"+Weibo.getAppKey());
				// System.out.println("mContent====>"+mContent);
				System.out.println(2);
				uploadQQ();
				// update(weibo, Weibo.getAppKey(), mContent, "", "");
			}
		}
		// if
		// (application.getlogin_where().equals(getString(R.string.sinawb)))
		// {
		//
		// }
		// else if
		// (application.getlogin_where().equals(getString(R.string.tencent)))
		// {
		//
		// }
		// else
		// {
		//
		// }
	}

	// 第三方新浪登录
	class AuthDialogListener implements WeiboDialogListener {

		@Override
		public void onComplete(Bundle values) {
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			System.out.println("expires_in=====>" + expires_in);
			AccessToken accessToken = new AccessToken(token,
					Constant.SINA_CONSUMER_SECRET);
			accessToken.setExpiresIn(expires_in);
			Weibo.getInstance().setAccessToken(accessToken);
			application.setSinaToken(accessToken);
			application.setSina_Expires_in(expires_in);
			sina_choice = 1;
			xinlang_button
					.setBackgroundResource(R.drawable.synchronous_sina_true);
			// Intent intent = new Intent();
			// intent.setClass(context, SupplementaryInformation.class);
			// startActivity(intent);
		}

		@Override
		public void onError(DialogError e) {
			Toast.makeText(getApplicationContext(),
					"Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
		}

		@Override
		public void onCancel() {
			Toast.makeText(getApplicationContext(), "Auth cancel",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(getApplicationContext(),
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}

	}

	// 以第三方QQ账户登录
	private void auth(String clientId, String target) {
		Intent intent = new Intent(context, com.tencent.tauth.TAuthView.class);

		intent.putExtra(TAuthView.CLIENT_ID, clientId);
		intent.putExtra(TAuthView.SCOPE, getString(R.string.scope));
		intent.putExtra(TAuthView.TARGET, target);
		intent.putExtra(TAuthView.CALLBACK, getString(R.string.CALLBACK));
		startActivity(intent);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (receiver != null) {
			unregisterIntentReceivers();
		}
		Tools.ClearBitmap(newBT);
	}

	// 初始化广播
	private void registerIntentReceivers() {
		receiver = new AuthReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(TAuthView.AUTH_BROADCAST);
		registerReceiver(receiver, filter);
	}

	private void unregisterIntentReceivers() {
		unregisterReceiver(receiver);
	}

	// 调用一个广播以通知PC用户我利用第三方登陆
	public class AuthReceiver extends BroadcastReceiver {

		private static final String TAG = "AuthReceiver";

		@Override
		public void onReceive(Context context, Intent intent) {
			final Context mContext = context;
			Bundle exts = intent.getExtras();
			String raw = exts.getString("raw");
			String access_token = exts.getString(TAuthView.ACCESS_TOKEN);
			String expires_in = exts.getString(TAuthView.EXPIRES_IN);
			String error_ret = exts.getString(TAuthView.ERROR_RET);
			String error_des = exts.getString(TAuthView.ERROR_DES);
			Log.i(TAG, String.format("raw: %s, access_token:%s, expires_in:%s",
					raw, access_token, expires_in));

			if (access_token != null) {
				if (!isFinishing()) {
					System.out.println("do this");
					showDialog(PROGRESS);
				}
				application.setQQ_Token(access_token);
				// Intent intent2 = new Intent();
				// intent2.putExtra("token", access_token);
				// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// intent2.setClass(mContext, SupplementaryInformation.class);
				// /*application.setVerificationCode(url);*/
				// //intent.putExtra(ConfigUtil.OAUTH_VERIFIER_URL, url);
				// startActivity(intent2);
				// finish();
				// 用access token 来获取open id
				TencentOpenAPI.openid(access_token, new Callback() {
					@Override
					public void onSuccess(final Object obj) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								dismissDialog(PROGRESS);
								Toast.makeText(mContext,
										getString(R.string.shouquansuess),
										Toast.LENGTH_SHORT).show();
								application.setOpenID(((OpenId) obj)
										.getOpenId());
								qq_choice = 1;
								qq_button
										.setBackgroundResource(R.drawable.synchronous_qq_true);
								// application.setOpenID(((OpenId)obj).getOpenId());
								// application.SaveOpenID();
							}
						});
					}

					@Override
					public void onFail(int ret, final String msg) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								dismissDialog(PROGRESS);
								TDebug.msg(msg, getApplicationContext());
							}
						});
					}
				});
			}
			if (error_ret != null) {
				Toast.makeText(context, getString(R.string.shouquanfalse),
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	public boolean satisfyConditions() {
		return mAccessToken != null && Constant.TECENTAPPID != null
				&& mOpenId != null && !mAccessToken.equals("")
				&& !Constant.TECENTAPPID.equals("") && !mOpenId.equals("");
	}

	public static final int PROGRESS = 0;

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case PROGRESS:
			dialog = new ProgressDialog(this);
			((ProgressDialog) dialog).setMessage(getString(R.string.qingqiu));
			break;
		}

		return dialog;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			Intent intent = new Intent();
			intent.setClass(this, DetailActivity.class);
			startActivity(intent);
			finish();
			break;
		}
		return true;
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
