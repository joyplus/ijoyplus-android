package com.joyplus;

import android.os.Environment;

public class Constant {

	// 正式环境
//	public static final String BASE_URL = "http://api.joyplus.tv/joyplus-service/index.php/";
//	public static String DEFAULT_APPKEY = "ijoyplus_android_0001";
//	public static String APPKEY = "ijoyplus_android_0001";

	/*
	 * test:
	 * 新的测试环境：
测试环境：

1：service: apitest.joyplus.tv/joyplus-service/index.php

app_key:

Android:ijoyplusandroid0001bj

IOS: ijoyplusios001bj


2：cms cms-test.yue001.com/manager/index.php

	 */
	public static boolean TestEnv = true;
	public static final String BASE_URL = "http://apitest.yue001.com/joyplus-service/index.php/";
	public static String DEFAULT_APPKEY = "ijoyplus_android_0001bj";
	public static String APPKEY = "ijoyplus_android_0001bj";

	public static final String USER_AGENT_IOS = 
			"Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_0 like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7";
	public static final String USER_AGENT_ANDROID = 
			"Mozilla/5.0 (Linux; U; Android 2.2; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
	public static final String USER_AGENT_FIRFOX = "	Mozilla/5.0 (Windows NT 6.1; rv:19.0) Gecko/20100101 Firefox/19.0";

	public static String PATH = Environment.getExternalStorageDirectory()
			+ "/joy/image_cache/";
	public static String PATH_HEAD = Environment.getExternalStorageDirectory()
			+ "/joy/admin/";
	public static String PATH_XML = Environment.getExternalStorageDirectory()
			+ "/joy/";
	public static String PATH_VIDEO = Environment.getExternalStorageDirectory()
			+ "/joy/video/";
	// 下载视频时支持多线程的数目
	String threadcount = "3";

	// 我们的sina账号
	public static String SINA_CONSUMER_KEY = "1490285522";
	public static String SINA_CONSUMER_SECRET = "f9ebc3ca95991b6dfce2c1608687e92b";
	public static String TECENTAPPID = "100317415";
	public static String SINA_REDIRECTURL = "https://api.weibo.com/oauth2/default.html";

	// Test Env
	public static String Parse_AppId = "5FNbLx7dnRAx3knxV4rOdaLMRJMByqfKjWQRQakT";
	public static String Parse_ClientKey = "RZHrZVn6MK8VGZxfpeshrC2tpxpzzMOZjU0rSS6X";
	
	//Production Env
//	public static String Parse_AppId = "UBgv7IjGR8i6AN0nS4diS48oQTk6YErFi3LrjK4P";
//	public static String Parse_ClientKey = "Y2lKxqco7mN3qBmZ05S8jxSP8nhN92hSN4OHDZR8";
	
	public final static int MSG_DMR_CHANGED = 0;
	public final static int MSG_PUSH_LOCAL_FILE = 1;
	public final static int MSG_PUSH_INTERNET_MEDIA = 2;
	public final static int MSG_MONITOR_DMR = 3;
	public final static int MSG_STATE_UPDATE = 4;
	public final static int MSG_MEDIA_INFO_UPDATE = 5;
	public final static int MSG_POSITION_UPDATE = 6;
	public final static int MSG_VOLUME_UPDATE = 7;
	public final static int MSG_MUTE_UPDATE = 8;
	public final static int MSG_ALLOWED_ACTIONS_UPDATE = 9;
	public final static int MSG_GET_POSITION_TIMER = 10;
	public final static int MSG_ACTION_RESULT = 11;

	public final static int MSG_DMRCHANGED = 12;
	
	public final static int MSG_UPDATEDATA = 30;
	//记录当前的集数
	public static int select_index = -1;
	
	public final static String MSG_KEY_ID_TITLE = "MSG_KEY_ID_TITLE";
	public final static String MSG_KEY_ID_STATE = "MSG_KEY_ID_STATE";
	public final static String MSG_KEY_ID_ALLOWED_ACTION = "MSG_KEY_ID_ALLOWED_ACTION";
	public final static String MSG_KEY_ID_VOLUME = "MSG_KEY_ID_VOLUME";
	public final static String MSG_KEY_ID_MUTE = "MSG_KEY_ID_MUTE";
	public final static String MSG_KEY_ID_POSITION = "MSG_KEY_ID_POSITION";
	public final static String MSG_KEY_ID_DURATION = "MSG_KEY_ID_DURATION";
	public final static String MSG_KEY_ID_MIME_TYPE = "MSG_KEY_ID_MIME_TYPE";
	public final static String MSG_KEY_ID_ACTION_NAME = "MSG_KEY_ID_ACTION_NAME";
	public final static String MSG_KEY_ID_ACTION_RESULT = "MSG_KEY_ID_ACTION_RESULT";
	public static final String[] video_extensions = { ".m3u",".m3u8" ,"3gphd", ".3g2", ".3gp",
			".3gp2", ".3gpp", ".amv", ".asf", ".avi", ".divx", "drc", ".dv",
			".f4v", ".flv", ".gvi", ".gxf", ".iso", ".m1v", ".m2v", ".m2t",
			".m2ts", ".m4v", ".mkv", ".mov", ".mp2", ".mp2v", ".mp4", ".mp4v",
			".mpe", ".mpeg", ".mpeg1", ".mpeg2", ".mpeg4", ".mpg", ".mpv2",
			".mts", ".mtv", ".mxf", ".mxg", ".nsv", ".nuv", ".ogm", ".ogv",
			".ogx", ".ps", ".rec", ".rm", ".rmvb", ".tod", ".ts", ".tts",
			".vob", ".vro", ".webm", ".wm", ".wmv", ".wtv", ".xesc" };
	public static final String[] video_dont_support_extensions = { ".m3u",
			".m3u8" };
	public static final String[] video_index = { "letv",
	"fengxing","qiyi","youku","sinahd","sohu","56","qq","pptv","m1905"};

	/*
	 *  "type": flv,3gp：标清 (普清就是标清) ,"mp4", mp4:高清，hd2：超清
	 */
	public static final String[] quality_index = { "flv","mp4",
		"hd2","3gp"};
	public static final String[] player_quality_index = { "mp4",
		"hd2","3gp","flv"};
	// APP_ID 替换为你的应用从官方网站申请到的合法appId
    public static final String APP_ID = "wxc8ea1cbc355fe2d0";
   
    //测试环境    云端投放 
    public static final String TV_CHANNEL = "/screencast/CHANNEL_TV_";
    public static final String TV_CHANNEL_URL = "http://comettest.joyplus.tv:8080/bindtv";
}
