package com.joyplus;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
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
	private static final String NOT_VALID_LINK = "NULL";
	private static final String FENGXING = "1";

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
	public boolean use2G3G = false;
	private String mURLPath;
	private Map<String, String> headers;


	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

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
		
		Parse.initialize(this, Constant.Parse_AppId,
				Constant.Parse_ClientKey);
		
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
	public String getURLPath() {
		return mURLPath;
	}
	
	/**
	 * 只是简单文本判断
	 * @param Url
	 * @return
	 */
	public boolean IfSupportFormat(String Url) {
		/*
		 * URLUtil里面可以检测网址是否有效
		 */
//		return URLUtil.isNetworkUrl(Url);
		if(CheckUrl(Url)) {
			
			if(CheckUrl(mURLPath)) {
				
				return true;
			} 
		}	
		return false;

	}
	
	/*
	 * checkUserSelect
	 * 检测当前用户用的网络,如果为wifi返回true
	 * 如果不是wifi,用户选择了确定使用2G3G返回true
	 */
	public void checkUserSelect(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context  
	            .getSystemService(Context.CONNECTIVITY_SERVICE);  
	    NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();  
	    if (activeNetInfo != null  
	            && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {  
	    	use2G3G = true;
	    }
	    else
	    {
	    	AlertDialog.Builder builder = new AlertDialog.Builder(
					context);
			builder.setTitle("温馨提醒")
					.setMessage("您目前在3G/2G网络环境下，确定继续?")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									use2G3G = true;
								}
							}).setNegativeButton("取消", null).create();
			builder.show();
	    }
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
	
	
	/**
	 * 检查urlLink文本是否正常
	 * @param urlLink
	 * @return
	 */
	private boolean CheckUrl(String urlLink) {
		
		//url本身不正常 直接返回
		   if (urlLink == null || urlLink.length() <= 0) {     
			   
			    return false;                   
			  }   else {
				  
				  if(!URLUtil.isValidUrl(urlLink)) {
					  
					  return false;
				  }
			  }
		   
		   return true;
	}
	
	/**
	 * id 对应播放源 letv 0、fengxing 1、qiyi 2、youku 3、sinahd 4、
	 *                       sohu 5、56 6、qq 7、pptv 8、m1905 9.
	 * 启动一个异步任务，把网络相关放在此任务中
	 * 重定向新的链接，直到拿到资源URL
	 * 
	 * 注意：因为网络或者服务器原因，重定向时间有可能比较长
	 * 因此需要较长时间等待
	 * @param url
	 * @param id
	 * @return 字符串
	 */
	private String newATask(String url, int sourceId) {
		
		AsyncTask<String,Void,String> aynAsyncTask = new AsyncTask<String, Void, String>(){

			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				
				List<String> list = new ArrayList<String>();
				String dstUrl = params[0];
				if(BuildConfig.DEBUG) Log.i(TAG, "newATask--->>params : " + params[0] + params[1]);
				try {
					simulateFirfoxRequest(Constant.USER_AGENT_IOS,params ,list);//使用递归，并把得到的链接放在集合中，取最后一次得到的链接即可
					
					dstUrl = list.get(list.size() - 1);
					if(BuildConfig.DEBUG) Log.i(TAG, "AsyncTask----->>URL : " + dstUrl);
					list.clear();
					
					if(! dstUrl.equals(NOT_VALID_LINK)) {
						return dstUrl;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					if(BuildConfig.DEBUG) Log.i(TAG, "TimeOut!!!!!! : " + e);
					e.printStackTrace();
				}
				
				return params[0];//如果TimeOut或者不能够拿到真正地址，那就把最原始链接返回
			}
			
		}.execute(new String[]{url , ""+ sourceId});
		
		try {
			String redirectUrl = aynAsyncTask.get();//从异步任务中获取结果
			
			return redirectUrl;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return url;
	}
	
	/**
	 * 模拟火狐浏览器给服务器发送不同请求，有火狐本身请求，IOS请求，Android请求
	 * @param userAgent firfox ios android
	 * @param params包括srcUrl 原始地址【可能可以播放，可能需要跳转】和 sourceID 例："1"
	 * @param list 存储播放地址
	 */
	private void simulateFirfoxRequest(String userAgent,String[] params , List<String> list) {
		if(params == null || params.length < 2) {
			
			if(BuildConfig.DEBUG) Log.i(TAG, "Params Wrong");
			list.add(NOT_VALID_LINK);
			return ;
		}
		
		String srcUrl = params[0];//源地址
		String sourceId = params[1];//资源来源id
		
		//模拟火狐ios发用请求  使用userAgent
		AndroidHttpClient mAndroidHttpClient = AndroidHttpClient.newInstance(userAgent);
		
		HttpParams httpParams =  mAndroidHttpClient.getParams();
		//连接时间最长5秒，可以更改
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000 * 1);
				
		try {
			URL url = new URL(srcUrl);
			HttpGet mHttpGet = new HttpGet(url.toURI());
			HttpResponse response = mAndroidHttpClient.execute(mHttpGet);
			
			//限定连接时间
			
			StatusLine statusLine = response.getStatusLine();
			int status = statusLine.getStatusCode();
			
			if(BuildConfig.DEBUG) Log.i(TAG, "HTTP STATUS : " + status);
			
			//如果资源来源为风行，那就对url进行重定向 如果不是就只是简单判断
			//风行资源id 为 1
			//如果拿到资源直接返回url  如果没有拿到资源，并且要进行跳转,那就使用递归跳转
			if(status != HttpStatus.SC_OK) {
				if(BuildConfig.DEBUG) Log.i(TAG, "NOT OK   start");
				
				if(sourceId != null && sourceId.equals(FENGXING)) {
					
					if(BuildConfig.DEBUG) Log.i(TAG, "NOT OK start");
						if(status == HttpStatus.SC_MOVED_PERMANENTLY ||//网址被永久移除
								status == HttpStatus.SC_MOVED_TEMPORARILY ||//网址暂时性移除
								status ==HttpStatus.SC_SEE_OTHER ||//重新定位资源
								status == HttpStatus.SC_TEMPORARY_REDIRECT) {//暂时定向
						
							Header header = response.getFirstHeader("Location");//拿到重新定位后的header
							String location = header.getValue();//从header重新取出信息
							list.add(location);
						
							mAndroidHttpClient.close();//关闭此次连接
						
							if(BuildConfig.DEBUG) Log.i(TAG, "Location: " + location);
							//进行下一次递归
							simulateFirfoxRequest(userAgent,new String[]{location , FENGXING} , list);
						} else {
							
							//如果地址真的不存在，那就往里面加NULL字符串
							mAndroidHttpClient.close();
							list.add(NOT_VALID_LINK);
						}
				} else {
					
					//如果地址真的不存在，那就往里面加NULL字符串
					mAndroidHttpClient.close();
					list.add(NOT_VALID_LINK);
				}
			} else {
				//正确的话直接返回，不进行下面的步骤
				mAndroidHttpClient.close();
				list.add(srcUrl);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if(BuildConfig.DEBUG) Log.i(TAG, "NOT OK" + e);
			
			//如果地址真的不存在，那就往里面加NULL字符串
			mAndroidHttpClient.close();
			list.add(NOT_VALID_LINK);
			e.printStackTrace();
		}
		

		
	}


}
