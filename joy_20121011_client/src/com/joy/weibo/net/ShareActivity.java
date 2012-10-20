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

import com.joy.Tools.*;

import com.joy.DetailActivity;
import com.joy.GetThird_AccessToken;
import com.joy.R;
import com.joy.Tools.BitmapZoom;
import com.joy.Tools.MyEditText;
import com.joy.weibo.net.AsyncWeiboRunner.RequestListener;
import com.tencent.tauth.TencentOpenAPI;
import com.tencent.tauth.http.Callback;
import com.tencent.tauth.http.TDebug;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;


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

public class ShareActivity extends Activity implements OnClickListener, RequestListener {
    private TextView mTextNum,title_text;
    private Button share_pinglun,xinlang_button,qq_button;
    private MyEditText fx_pl_edit;
    private FrameLayout mPiclayout;

    private String mPicPath = "";
    private String mContent = "";
    private String mAccessToken = "";
    private String mTokenSecret = "";
    Dialog dialog;
    Context context;
    int sina_choice,qq_choice;
    public String mAppid = "222222";//申请时分配的appid
    public static final String EXTRA_WEIBO_CONTENT = "com.weibo.android.content";
    public static final String EXTRA_PIC_URI = "com.weibo.android.pic.uri";
    public static final String EXTRA_ACCESS_TOKEN = "com.weibo.android.accesstoken";
    public static final String EXTRA_TOKEN_SECRET = "com.weibo.android.token.secret";

    public static final int WEIBO_MAX_LENGTH = 120;
    GetThird_AccessToken getThird_AccessToken;
    int linearlayout_width = 0;
    final Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.getData().getInt("msg")) {
			case 1:
				dialog.dismiss();
				break;
			}
		}
    };
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        this.setContentView(R.layout.share_mblog_view);
        context = this;
        linearlayout_width =  getWindowManager().getDefaultDisplay().getWidth()/3;
        getThird_AccessToken = (GetThird_AccessToken) getApplicationContext();
        Intent in = this.getIntent();
        mPicPath = in.getStringExtra(EXTRA_PIC_URI);
        mContent = in.getStringExtra(EXTRA_WEIBO_CONTENT);
        mAccessToken = in.getStringExtra(EXTRA_ACCESS_TOKEN);
        System.out.println("mAccessToken====>"+mAccessToken);
        mTokenSecret = in.getStringExtra(EXTRA_TOKEN_SECRET);
        System.out.println("mTokenSecret====>"+mTokenSecret);
        AccessToken accessToken = new AccessToken(mAccessToken, mTokenSecret);
        Weibo weibo = Weibo.getInstance();
        weibo.setAccessToken(accessToken);

        Button login_goback = (Button) this.findViewById(R.id.login_goback);
        login_goback.setOnClickListener(this);
        share_pinglun = (Button) this.findViewById(R.id.share_pinglun);
        share_pinglun.setText(getThird_AccessToken.getButton_Name());
        share_pinglun.setOnClickListener(this);
        xinlang_button = (Button) findViewById(R.id.xinlang_button);
        xinlang_button.setOnClickListener(this);
        qq_button = (Button) findViewById(R.id.qq_button);
        qq_button.setOnClickListener(this);
        mTextNum = (TextView) this.findViewById(R.id.tv_text_limit);
        title_text = (TextView) findViewById(R.id.title_text);
        title_text.setText(getThird_AccessToken.getButton_Name());
        fx_pl_edit = (MyEditText) this.findViewById(R.id.fx_pl_edit);
        ImageView imageView = (ImageView) findViewById(R.id.image_moves);
        imageView.setVisibility(View.GONE);
        if (getThird_AccessToken.getButton_Name().equals(getString(R.string.fenxiang))) {
        	Bitmap bt = BitmapFactory.decodeFile(mPicPath);
        	imageView.setVisibility(View.VISIBLE);
        	Bitmap newBit = BitmapZoom.bitmapZoomByWidth(bt, linearlayout_width);
        	imageView.setImageBitmap(newBit);
        	imageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					fx_pl_edit.setFocusable(true);  
					fx_pl_edit.setFocusableInTouchMode(true);  
					fx_pl_edit.requestFocus();  
					InputMethodManager inputManager = (InputMethodManager)fx_pl_edit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
					inputManager.showSoftInput(fx_pl_edit, 0); 
				}
			});
        }
        fx_pl_edit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String mText = fx_pl_edit.getText().toString();
                String mStr;
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
                mTextNum.setText("还可以输入"+len+"字");
            }
        });
        fx_pl_edit.setText(mContent);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == R.id.login_goback) {
        	Intent intent = new Intent();
        	intent.setClass(this, DetailActivity.class);
        	startActivity(intent);
            finish();
        } else if (viewId == R.id.share_pinglun) {
        	share_pinglun.setEnabled(false);
        	dialog = ProgressDialog.show(context,getString(R.string.tishi),getString(R.string.wait));
        	new Thread(new uploadThread()).start();
        } else if (viewId == R.id.xinlang_button) {
        	if (sina_choice == 0) {
        		xinlang_button.setTextColor(R.color.red);
        		sina_choice = 1;
        	}
        	else
        	{
        		sina_choice = 0;
        		xinlang_button.setTextColor(R.color.black);
        	}
        } else if (viewId == R.id.qq_button) {
        	if (qq_choice == 0) {
        		qq_choice = 1;
        		qq_button.setTextColor(R.color.red);
        	}
        	else
        	{
        		qq_choice = 0;
        		qq_button.setTextColor(R.color.black);
        	}
        }
    }
//新浪分享包含图片
    private String upload(Weibo weibo, String source, String file, String status, String lon,
            String lat) throws WeiboException {
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
//新浪分享不包含图片
    private String update(Weibo weibo, String source, String status, String lon, String lat)
            throws MalformedURLException, IOException, WeiboException {
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
    //QQ分享包含图片
    public  void uploadPic(String path) {
    	getThird_AccessToken.setlogin_where(getString(R.string.tencent));
    	getThird_AccessToken.GetAccessToken();
    	if (getThird_AccessToken.getAccessToken().length()==0) {
    		Toast.makeText(context, getString(R.string.please_login), Toast.LENGTH_SHORT).show();
			return;
    	}
		Bundle bundle = null;
		bundle = new Bundle();
		File f = new File(path);
		byte[] buff = null;
		try {
			
			InputStream is = new FileInputStream(f);
			ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while( (len = is.read(buffer)) !=-1 ){
				outSteam.write(buffer, 0, len);
			}
			outSteam.close();
			is.close();
			buff = outSteam.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//发布照片所需要的
		bundle.putByteArray("picture", buff);//必须.上传照片的文件名以及图片的内容（在发送请求时，图片内容以二进制数据流的形式发送，见下面的请求示例），注意照片名称不能超过30个字符。
		bundle.putString("photodesc", "测试吸血鬼日记");//照片描述，注意照片描述不能超过200个字符。 
		//bundle.putString("title", "QQ登陆SDK：UploadPic测试" + System.currentTimeMillis() + ".png");//照片的命名，必须以.jpg, .gif, .png, .jpeg, .bmp此类后缀结尾。
//		bundle.putString("albumid", "564546-asdfs-feawfe5545-45454");//相册id，不填则传到默认相册
		//bundle.putString("comment", ("QQ登陆SDK：测试吸血鬼日记"));
		bundle.putString("x", "0-360");//照片拍摄时的地理位置的经度。请使用原始数据（纯经纬度，0-360）。
		bundle.putString("y", "0-360");//照片拍摄时的地理位置的纬度。请使用原始数据（纯经纬度，0-360）。
		//取得QQ的AccessToken值和openid值
		getThird_AccessToken.GetOpenID();
		TencentOpenAPI.uploadPic(getThird_AccessToken.getAccessToken(), mAppid, getThird_AccessToken.getOpenID(), bundle, new Callback() {
			
			@Override
			public void onSuccess(final Object obj) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						dialog.dismiss();
						Toast.makeText(ShareActivity.this, R.string.send_sucess, Toast.LENGTH_LONG).show();
					}
				});
			}
			
			@Override
			public void onFail(final int ret, final String msg) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						dialog.dismiss();
						Toast.makeText(ShareActivity.this, R.string.send_failed, Toast.LENGTH_LONG).show();
					}
				});
			}
		});
	}
    //分享不包含图片
    public void uploadQQ()
    {
    	//判断token值是否存在
    	getThird_AccessToken.setlogin_where(getString(R.string.tencent));
    	getThird_AccessToken.GetAccessToken();
    	if (getThird_AccessToken.getAccessToken().length()==0) {
    		Toast.makeText(context, getString(R.string.please_login), Toast.LENGTH_SHORT).show();
			return;
    	}
		//发布评论所需要的
		Bundle bundle = null;
		bundle = new Bundle();
		bundle.putString("richtype", "2");//发布心情时引用的信息的类型。1表示图片； 2表示网页； 3表示视频。 
	//	bundle.putString("richval", ("http://www.qq.com" + "#" + System.currentTimeMillis()));//发布心情时引用的信息的值。有richtype时必须有richval 
		bundle.putString("con","测试！");//发布的心情的内容。
		//bundle.putString("lbs_nm","广东省深圳市南山区高新科技园腾讯大厦");//地址文
	//	bundle.putString("lbs_x","0-360");//经度。请使用原始数据（纯经纬度，0-360）。
	//	bundle.putString("lbs_y","0-360");//纬度。请使用原始数据（纯经纬度，0-360）。
	//	bundle.putString("lbs_id","360");//地点ID。lbs_id与lbs_idnm通常一起使用，来明确标识一个地址。 
		//bundle.putString("lbs_idnm","腾讯");//地点名称。lbs_id与lbs_idnm通常一起使用，来明确标识一个地址。
		
		//取得QQ的AccessToken值和openid值
		getThird_AccessToken.GetOpenID();
		TencentOpenAPI.addTopic(getThird_AccessToken.getAccessToken(), mAppid, getThird_AccessToken.getOpenID(), bundle, new Callback() {
			
			@Override
			public void onSuccess(final Object obj) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						dialog.dismiss();
						Toast.makeText(ShareActivity.this, R.string.send_sucess, Toast.LENGTH_LONG).show();
						//mActivity.showMessage("发表说说返回数据", obj.toString());
					}
				});
			}
			
			@Override
			public void onFail(final int ret, final String msg) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						dialog.dismiss();
						Toast.makeText(ShareActivity.this, R.string.send_failed, Toast.LENGTH_LONG).show();
						//TDebug.msg(ret + ": " + msg, mActivity);
					}
				});
			}
		});
    }
    @Override
    public void onComplete(String response) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
            	dialog.dismiss();
                Toast.makeText(ShareActivity.this, R.string.send_sucess, Toast.LENGTH_LONG).show();
            }
        });
        share_pinglun.setEnabled(true);
        //this.finish();
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
                Toast.makeText(
                        ShareActivity.this,
                        String.format(ShareActivity.this.getString(R.string.send_failed) + ":%s",
                                e.getMessage()), Toast.LENGTH_LONG).show();
            }
        });
        share_pinglun.setEnabled(true);

    }
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
    	switch(keyCode){
        case KeyEvent.KEYCODE_BACK:
        	break;
    	}
        return true;
    }
    //因有的机器点击评论或发送后有卡机的情况，故写一个线程控制
    class uploadThread implements Runnable
    {

		@Override
		public void run() {
			try {
				//Thread.sleep(1000*10);
				doShare();
				Message msg = handler.obtainMessage();
				Bundle b = new Bundle();
				b.putInt("msg", 1);
				msg.setData(b);
				handler.sendMessage(msg);
			} catch (Exception e) {
				dialog.dismiss();
				// TODO Auto-generated catch block
				e.printStackTrace();
			}     // sleep 1000ms   
		}
    }
    //单击分享或评论后所做的事情
    void doShare()
    {
    	Weibo weibo = Weibo.getInstance();
        try {
        	
            if (!TextUtils.isEmpty((String) (weibo.getAccessToken().getToken()))) {
                this.mContent = fx_pl_edit.getText().toString();
                if (!TextUtils.isEmpty(mPicPath)) {
                    upload(weibo, Weibo.getAppKey(), this.mPicPath, this.mContent, "", "");

                } else {
                    // Just update a text weibo!
                	System.out.println("Weibo.getAppKey()===>"+Weibo.getAppKey());
                	System.out.println("mContent====>"+mContent);
                    update(weibo, Weibo.getAppKey(), mContent, "", "");
                }
            } else {
            	dialog.dismiss();
            	share_pinglun.setEnabled(true);
                Toast.makeText(this, this.getString(R.string.please_login), Toast.LENGTH_LONG).show();
            }
        } catch (MalformedURLException e) {
        	//dialog.dismiss();
            e.printStackTrace();
        } catch (IOException e) {
        	//dialog.dismiss();
            e.printStackTrace();
        } catch (WeiboException e) {
        	//dialog.dismiss();
            e.printStackTrace();
        }
    }
}
