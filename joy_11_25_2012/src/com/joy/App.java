package com.joy;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;
import com.joy.Service.DownLoadService;
import com.joy.weibo.net.AccessToken;
import com.joy.weibo.net.Oauth2AccessTokenHeader;
import com.joy.weibo.net.Token;
import com.joy.weibo.net.Utility;
import com.joy.weibo.net.Weibo;
import com.joy.weibo.net.WeiboDialogListener;

public class App extends Application {
	public final static String TAG = "App";
	private static App instance;

	public static final String MOBILE_AGENT = "Mozilla/5.0 (Linux; U; Android 2.2) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533";

	private DownLoadService DOWNLOADSERVICE;
	String AccessToken = ""; // 新浪token值
	String VerificationCode = "";
	String phoneName = "";// 传递电话名字
	String phoneNum = "";// 传递电话号码
	String Activitytype = "";// 用于判断darentuij.class是从那进去的
	String dinayingName = "";// 传递搜索中的电影名字
	String Expires_in = "";// 新浪Expires_in值
	String Button_Name = ""; // 切换分享和评论button的名字
	String OpenID = ""; // 腾讯的open_id
	Token SinaToken = null; // 新浪Token值(临时)
	String Sina_Expires_in = ""; // 新浪Expires_in值(临时)
	String QQ_Token = ""; // 腾讯token值
	String QQ_OpenID = "";
	int movType = 0;// 传递搜索中的电影类型
	int movType1 = 0;// 传递搜索中的电影类型
	String seachURL = ""; // 传递搜索url地址
	int bitmapW = 0;
	WeiboDialogListener WeiboDialogListener;// weibo监听器，用于weibodiallog2中
	Context context; // 保存零时context，用于关闭activity
	String go_detail = "";

	int mTab_Where = 0;
	int mTab_ID = R.id.channel1;
	String url = ""; // 用于weibodiallog2中

	Weibo Weibo; // 用于weibodiallog2中

	String exit = "false"; // 用于判断用户是否为正常退出
	String PicName = "";// 传递搜索结果中的电影名字
	String PicURL = "";// 传递搜索结果中的电影URL
	String prod_id = "";

	int jujiliebiaoXianshi = 0;
	String user_image_head[]; // 用于临时储存用户头像的url地址

	public String[] getuser_image_head() {
		return user_image_head;
	}

	public void setuser_image_head(String[] user_image_head) {
		this.user_image_head = user_image_head;
	}

	public void setuser_image_headA(ArrayList<String> user_image_head) {
		String[] sa = new String[user_image_head.size()];
		user_image_head.toArray(sa);
		this.user_image_head = sa;
	}

	int editTextVisable = 0;
	public boolean IsLogin;

	@Override
	public void onCreate() {
		super.onCreate();
		IsLogin = false;

		File cacheDir = new File(Constant.PATH);
		AQUtility.setCacheDir(cacheDir);
		// Are we using advanced debugging - locale?
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		String p = pref.getString("set_locale", "");
		if (p != null && !p.equals("")) {
			Locale locale;
			// workaround due to region code
			if (p.startsWith("zh")) {
				locale = Locale.CHINA;
			} else {
				locale = new Locale(p);
			}
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			getBaseContext().getResources().updateConfiguration(config,
					getBaseContext().getResources().getDisplayMetrics());
		}

		instance = this;
	}

	public String getseachURL() {
		return seachURL;
	}

	public void setseachURL(String seachURL) {
		this.seachURL = seachURL;
	}

	public int getmovType1() {
		return movType1;
	}

	public void setmovType1(int movType1) {
		this.movType1 = movType1;
	}

	public int getmovType() {
		return movType;
	}

	public void setmovType(int movType) {
		this.movType = movType;
	}

	/**
	 * Called when the overall system is running low on memory
	 */
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		BitmapAjaxCallback.clearCache();
		Log.w(TAG, "System is running low on memory");

		// BitmapCache.getInstance().clear();
	}

	/**
	 * @return the main context of the App
	 */
	public static Context getAppContext() {
		return instance;
	}

	/**
	 * @return the main resources from the App
	 */
	public static Resources getAppResources() {
		return instance.getResources();
	}

	public int getjujiliebiaoXianshi() {
		return jujiliebiaoXianshi;
	}

	public void setjujiliebiaoXianshi(int jujiliebiaoXianshi) {
		this.jujiliebiaoXianshi = jujiliebiaoXianshi;
	}

	public int geteditTextVisable() {
		return editTextVisable;
	}

	public void seteditTextVisable(int editTextVisable) {
		this.editTextVisable = editTextVisable;
	}

	public String getPicURL() {
		return PicURL;
	}

	public void setPicURL(String p) {
		PicURL = p;
	}

	public String getPicName() {
		return PicName;
	}

	public void setPicName(String p) {
		PicName = p;
	}

	public String getProdID() {
		return prod_id;
	}

	public void setProdID(String p) {
		prod_id = p;
	}

	public void setexit(String exit) {
		this.exit = exit;
	}

	public String getexit() {
		return exit;
	}

	public void setWeibo(Weibo Weibo) {
		this.Weibo = Weibo;
	}

	public Weibo getWeibo() {
		return Weibo;
	}

	public void seturl(String url) {
		this.url = url;
	}

	public String geturl() {
		return url;
	}

	public WeiboDialogListener getWeiboDialogListener() {
		return WeiboDialogListener;
	}

	public void setWeiboDialogListener(WeiboDialogListener WeiboDialogListener) {
		this.WeiboDialogListener = WeiboDialogListener;
	}

	public int getbitmapW() {
		return bitmapW;
	}

	public void setbitmapW(int n) {
		bitmapW = n;
	}

	int where_gologin = 2;

	public void setwhere_gologin(int where_gologin) {
		this.where_gologin = where_gologin;
	}

	public int getwhere_gologin() {
		return where_gologin;
	}

	public void setcontext(Context context) {
		this.context = context;
	}

	public Context getcontext() {
		return context;
	}

	public void setmTab_ID(int mTab_ID) {
		this.mTab_ID = mTab_ID;
	}

	public int getmTab_ID() {
		return mTab_ID;
	}

	public void setgo_detail(String go_detail) {
		this.go_detail = go_detail;
	}

	public String getgo_detail() {
		return go_detail;
	}

	public void setmTab_Where(int mTab_Where) {
		this.mTab_Where = mTab_Where;
	}

	public int getmTab_Where() {
		return mTab_Where;
	}

	public void setQQ_OpenID(String QQ_OpenID) {
		this.QQ_OpenID = QQ_OpenID;
	}

	public String getQQ_OpenID() {
		return QQ_OpenID;
	}

	public void setQQ_Token(String QQ_Token) {
		this.QQ_Token = QQ_Token;
	}

	public String getQQ_Token() {
		return QQ_Token;
	}

	public void setSina_Expires_in(String Sina_Expires_in) {
		this.Sina_Expires_in = Sina_Expires_in;
	}

	public String getSina_Expires_in() {
		return Sina_Expires_in;
	}

	public void setSinaToken(AccessToken SinaToken) {
		this.SinaToken = SinaToken;
	}

	public Token getSinaToken() {
		return SinaToken;
	}

	String IMG_Name = "";// 保存当前Activity的图片名字
	String Name_URL = "";// 保存当前Activity的图片URL

	public String getName_URL() {
		return Name_URL;
	}

	public void setName_URL(String n) {
		Name_URL = n;
	}

	public String getIMG_Name() {
		return IMG_Name;
	}

	public void setIMG_Name(String n) {
		IMG_Name = n;
	}

	public void setOpenID(String OpenID) {
		this.OpenID = OpenID;
	}

	public String getOpenID() {
		return OpenID;
	}

	public void setButton_Name(String Button_Name) {
		this.Button_Name = Button_Name;
	}

	public String getButton_Name() {
		return Button_Name;
	}

	public void setdinayingName(String a) {
		dinayingName = a;

	}

	public String getdinayingName() {
		return dinayingName;
	}

	public void setExpires_in(String Expires_in) {
		this.Expires_in = Expires_in;
	}

	public String getExpires_in() {
		return Expires_in;
	}

	public void setActivitytype(String a) {
		Activitytype = a;
	}

	public String getActivitytype() {
		return Activitytype;
	}

	public void setphoneNum(String n) {
		phoneNum = n;
	}

	public String getphoneNum() {
		return phoneNum;
	}

	public void setphoneName(String n) {
		phoneName = n;
	}

	public String getphoneName() {
		return phoneName;
	}

	String login_where = "";

	// �ж��ĸ������
	public void setlogin_where(String login_where) {
		this.login_where = login_where;

	}

	public String getlogin_where() {
		return login_where;
	}

	// 唯一值
	public void setAccessToken(String AccessToken) {
		this.AccessToken = AccessToken;

	}

	public String getAccessToken() {
		return AccessToken;
	}

	// 验证�?
	public void setVerificationCode(String VerificationCode) {
		this.VerificationCode = VerificationCode;

	}

	public String getVerificationCode() {
		return VerificationCode;
	}

	public void SaveName(String where) {
		SharedPreferences.Editor sharedatab = getSharedPreferences("Name", 0)
				.edit();
		sharedatab.putString("Name_URL" + where, Name_URL);
		sharedatab.commit();
	}

	public void GetName(String where) {
		SharedPreferences sharedata = getSharedPreferences("Name", 0);
		Name_URL = sharedata.getString("Name_URL" + where, "");
	}

	public void SaveImageName(String where) {
		SharedPreferences.Editor sharedatab = getSharedPreferences("IMG", 0)
				.edit();
		sharedatab.putString("IMG_Name" + where, IMG_Name);
		sharedatab.commit();
	}

	public void GetImageName(String where) {
		SharedPreferences sharedata = getSharedPreferences("IMG", 0);
		IMG_Name = sharedata.getString("IMG_Name" + where, "");
	}

	public void SaveAccessToken() {
		SharedPreferences.Editor sharedatab = getSharedPreferences(
				"AccessToken", 0).edit();
		sharedatab.putString("AccessToken", AccessToken);
		sharedatab.commit();
	}

	public void GetAccessToken() {
		SharedPreferences sharedata = getSharedPreferences("AccessToken", 0);
		AccessToken = sharedata.getString("AccessToken", "");
	}

	public void SaveQQAccessToken() {
		SharedPreferences.Editor sharedatab = getSharedPreferences("QQ_Token",
				0).edit();
		sharedatab.putString("QQ_Token", QQ_Token);
		sharedatab.commit();
	}

	public void GetQQAccessToken() {
		SharedPreferences sharedata = getSharedPreferences("QQ_Token", 0);
		QQ_Token = sharedata.getString("QQ_Token", "");
	}

	// 保存新浪Expires_in值
	public void SaveExpires_in() {
		SharedPreferences.Editor sharedatab = getSharedPreferences(
				"Expires_in", 0).edit();
		sharedatab.putString("Expires_in", Expires_in);
		sharedatab.commit();
	}

	public void GetExpires_in() {
		SharedPreferences sharedata = getSharedPreferences("Expires_in", 0);
		Expires_in = sharedata.getString("Expires_in", "");
	}

	// 保存QQ的OpenID
	public void SaveOpenID() {
		SharedPreferences.Editor sharedatab = getSharedPreferences("OpenID", 0)
				.edit();
		sharedatab.putString("OpenID", OpenID);
		sharedatab.commit();
	}

	public String GetOpenID() {
		SharedPreferences sharedata = getSharedPreferences("OpenID", 0);
		OpenID = sharedata.getString("OpenID", "");
		return OpenID;
	}

	// veteranyu add 保存自己的账�?
	public void SaveIjoyID(String email, String password) {
		SharedPreferences.Editor sharedatab = getSharedPreferences("IjoyID", 0)
				.edit();
		sharedatab.putString("email", email);
		sharedatab.putString("password", password);
		sharedatab.commit();
	}

	// 取得新浪微博,QQ 信息
	public void GetInfo() {
		setlogin_where(getString(R.string.sinawb));
		GetAccessToken();
		if (getAccessToken().trim().length() == 0) {
			setlogin_where(getString(R.string.tencent));
			GetQQAccessToken();
			if (getQQ_Token().trim().length() != 0) {
				setQQ_Token(getQQ_Token().trim());
				GetOpenID();
				setOpenID(getOpenID());
				// System.out.println(getwhere_gologin());
			}
		} else {
			Weibo.getInstance().setupConsumerConfig(Constant.SINA_CONSUMER_KEY,
					Constant.SINA_CONSUMER_SECRET);
			setAccessToken(getAccessToken().trim());
			GetExpires_in();
			Utility.setAuthorization(new Oauth2AccessTokenHeader());
			AccessToken accessToken = new AccessToken(getAccessToken().trim(),
					Constant.SINA_CONSUMER_SECRET);
			accessToken.setExpiresIn(getExpires_in());
			Weibo.getInstance().setAccessToken(accessToken);
			// System.out.println(getwhere_gologin());
		}
	}

	// �Ƿ����˳�
	public void SaveExit() {
		SharedPreferences.Editor sharedatab = getSharedPreferences("Exit", 0)
				.edit();
		sharedatab.putString("Exit", exit);
		sharedatab.commit();
	}

	public void GetExit() {
		SharedPreferences sharedata = getSharedPreferences("Exit", 0);
		exit = sharedata.getString("Exit", "");
	}

	// veteranyu add
	public DownLoadService getService() {
		return DOWNLOADSERVICE;
	}

	public boolean InitService() {
		DOWNLOADSERVICE = new DownLoadService(); // 初始化全局变量
		DOWNLOADSERVICE.InitService();

		return true;
	}

	public boolean CloseService() {
		if (DOWNLOADSERVICE.AccountLogout())
			return true;
		else
			return false;
	}

	public void SaveServiceData(String where, String Data) {
		SharedPreferences.Editor sharedatab = getSharedPreferences(
				"ServiceData", 0).edit();
		sharedatab.putString(where, Data);
		sharedatab.commit();
	}

	public String GetServiceData(String where) {
		SharedPreferences sharedata = getSharedPreferences("ServiceData", 0);
		return sharedata.getString(where, null);
	}

}
