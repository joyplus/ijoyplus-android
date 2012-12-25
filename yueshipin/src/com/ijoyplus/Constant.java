package com.ijoyplus;

import android.os.Environment;

public class Constant {
	public static final String BASE_URL = "http://api.joyplus.tv/joyplus-service/index.php/";
	// "http://112.64.18.12/joyplus-service/index.php/";

	// "http://test.joyplus.tv/index.php/"; // 正式数据库
	/*
	 * 测试数据库：http://112.64.18.12/phpmyadmin/ joyplus/ilovetv001
	 * 测试api接口：http://112.64.18.12/joyplus-service
	 * 
	 * http://test.joyplus.tv/index.php/tops
	 * http://test.joyplus.tv/index.php/XXXXXX
	 */
	// "http://112.64.18.12/joyplus-service/index.php/"; //测试数据库
	// "http://112.65.239.202/joyplus-service/index.php/"; //测试数据库2
	// "http://115.239.196.123/joyplus-service/index.php/"; // 正式数据库

	public static String PATH = Environment.getExternalStorageDirectory()
			+ "/joy/image_cache/";
	public static String PATH_HEAD = Environment.getExternalStorageDirectory()
			+ "/joy/admin/";
	public static String PATH_XML = Environment.getExternalStorageDirectory()
			+ "/joy/";

	public static final String APPKEY = "ijoyplus_android_0001";

	// 我们的sina账号
	public static String SINA_CONSUMER_KEY = "1490285522";
	public static String SINA_CONSUMER_SECRET = "f9ebc3ca95991b6dfce2c1608687e92b";
	public static String TECENTAPPID = "100317415";
	public static String SINA_REDIRECTURL = "https://api.weibo.com/oauth2/default.html";
	/*
	 * <string name="SINA_CONSUMER_KEY">3069972161</string> <string
	 * name="SINA_CONSUMER_SECRET">eea5ede316c6a283c6bae57e52c9a877</string>
	 * <string name="mAppid">222222</string>
	 */
	// public static String SINA_CONSUMER_KEY = "3069972161";
	// public static String SINA_CONSUMER_SECRET =
	// "eea5ede316c6a283c6bae57e52c9a877";
	// public static String TECENTAPPID = "222222";

	public static final String[] video_extensions = {"3gphd", ".3g2", ".3gp", ".3gp2",
			".3gpp", ".amv", ".asf", ".avi", ".divx", "drc", ".dv", ".f4v",
			".flv", ".gvi", ".gxf", ".iso", ".m1v", ".m2v", ".m2t", ".m2ts",
			".m4v", ".mkv", ".mov", ".mp2", ".mp2v", ".mp4", ".mp4v", ".mpe",
			".mpeg", ".mpeg1", ".mpeg2", ".mpeg4", ".mpg", ".mpv2", ".mts",
			".mtv", ".mxf", ".mxg", ".nsv", ".nuv", ".ogm", ".ogv", ".ogx",
			".ps", ".rec", ".rm", ".rmvb", ".tod", ".ts", ".tts", ".vob",
			".vro", ".webm", ".wm", ".wmv", ".wtv", ".xesc" };
	public static final String[] video_dont_support_extensions = { ".m3u",
			".m3u8" };

}
