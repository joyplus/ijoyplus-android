package com.joyplus;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;
import com.joyplus.download.DownloadTask;
import com.joyplus.download.Downloader;
import com.joyplus.weibo.net.Weibo;
import com.joyplus.weibo.net.WeiboDialogListener;
import com.parse.Parse;

@SuppressLint("DefaultLocale")
public class App extends Application {
	private final String TAG = "App";

	private static App instance;
	public String UserID;
	private Weibo Weibo; // 用于weibodiallog2中
	private String url = ""; // 用于weibodiallog2中
	private WeiboDialogListener WeiboDialogListener;// weibo监听器，用于weibodiallog2中
	public static int percentDown = 0;
	public static String urlDown = null;
	public List prodIdList = new ArrayList();
	public static Map<String, DownloadTask> downloadtasks = new HashMap<String, DownloadTask>();
	public static Map<String, Downloader> downloaders = new HashMap<String, Downloader>();
	// 固定存放下载的音乐的路径：SD卡目录下
	public boolean ThreadStartFlag = false;

	@Override
	public void onCreate() {
		super.onCreate();

		File cacheDir = new File(Constant.PATH);
		if (cacheDir.exists())
			AQUtility.setCacheDir(cacheDir);
		// 创建一个目录
		File destDir = new File(Constant.PATH_VIDEO);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}

		Parse.initialize(this, Constant.Parse_AppId, Constant.Parse_ClientKey);

		instance = this;
	}

	/**
	 * Called when the overall system is running low on memory
	 */
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		BitmapAjaxCallback.clearCache();
		Log.w(TAG, "System is running low on memory");
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

	public void setpercentDown(int percentDown) {
		App.percentDown = percentDown;
	}

	public int getpercentDown() {
		return percentDown;
	}

	public void seturlDown(String urlDown) {
		App.urlDown = urlDown;
	}

	public String geturlDown() {
		return urlDown;
	}

	public WeiboDialogListener getWeiboDialogListener() {
		return WeiboDialogListener;
	}

	public void setWeiboDialogListener(WeiboDialogListener WeiboDialogListener) {
		this.WeiboDialogListener = WeiboDialogListener;
	}

	public boolean IfSupportFormat(String Url) {
		boolean isValid = false;

		AsyncTask<String, Void, Boolean> as = new AsyncTask<String, Void, Boolean>() {

			private boolean isValid = false;

			@Override
			protected Boolean doInBackground(String... params) {
				// TODO Auto-generated method stub

				isValid = isUrlValid(params[0]);
				return Boolean.valueOf(isValid);
			}

		}.execute(Url);

		try {
			isValid = as.get().booleanValue();

			return isValid;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	public boolean isNetworkAvailable() {
		Context context = getApplicationContext();
		ConnectivityManager connect = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connect == null) {
			return false;
		} else// get all network info
		{
			NetworkInfo[] info = connect.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean IfIncludeM3U(String Url) {
		for (int i = 0; i < Constant.video_dont_support_extensions.length; i++) {

			if (Url.trim().toLowerCase()
					.contains(Constant.video_dont_support_extensions[i])) {
				return true;
			}
		}
		return false;

	}

	public void SaveServiceData(String where, String Data) {
		SharedPreferences.Editor sharedatab = getSharedPreferences(
				"ServiceData", 0).edit();
		sharedatab.putString(where, Data);
		sharedatab.commit();
	}

	public void DeleteServiceData(String where) {
		SharedPreferences.Editor sharedatab = getSharedPreferences(
				"ServiceData", 0).edit();
		sharedatab.remove(where);
		sharedatab.commit();
	}

	public String GetServiceData(String where) {
		SharedPreferences sharedata = getSharedPreferences("ServiceData", 0);
		return sharedata.getString(where, null);
	}

	public void SavePlayData(String where, String Data) {
		String m_data = GetPlayData("order");
		String m_rep = where + "|";
		// 重复了就不允许添加，只更新
		if (m_data != null) {
			if (m_data.indexOf(m_rep) != -1)// 重复了,只更新
				m_data = m_data.replace(m_rep, "");
			m_data = m_rep + m_data.trim();// 更新到最前面
		} else
			m_data = m_rep;
		SharedPreferences.Editor sharedatab = getSharedPreferences("PlayData",
				0).edit();
		sharedatab.putString("order", m_data);
		sharedatab.putString(where, Data);
		sharedatab.commit();

	}

	public void DeletePlayData(String where) {
		String m_data = GetPlayData("order");
		String m_rep = where + "|";
		if (m_data != null) {
			m_data = m_data.replace(m_rep, "");
		}
		SharedPreferences.Editor sharedatab = getSharedPreferences("PlayData",
				0).edit();
		sharedatab.putString("order", m_data.trim());
		sharedatab.remove(where);
		sharedatab.commit();
	}

	public String GetPlayData(String where) {
		SharedPreferences sharedata = getSharedPreferences("PlayData", 0);
		return sharedata.getString(where, null);
	}

	public void MyToast(Context context, CharSequence text) {
		Toast m_toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		m_toast.setGravity(Gravity.CENTER, m_toast.getXOffset() / 2,
				m_toast.getYOffset() / 2);
		m_toast.show();
	}

	public synchronized boolean isUrlValid(String urlStr) {
		HttpURLConnection connection = null;

		if (urlStr == null || urlStr.length() <= 0) {
			return false;
		}

		int counts = 0;
		while (counts < 5) {
			try {
				URL url = new URL(urlStr);
				connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(1000 * 1);
				
				int state = connection.getResponseCode();
				if (state == 200) {
					connection.disconnect();
					return true;
				}
				break;
			} catch (Exception e) {
				e.printStackTrace();
				counts++;
				continue;
			}
		}
		if (connection != null) {
			connection.disconnect();
		}
		return false;
	}

}
