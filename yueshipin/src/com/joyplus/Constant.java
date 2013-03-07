package com.joyplus;

import android.os.Environment;

public class Constant {
	public static boolean Debug = true;
	// 正式环境
	public static final String BASE_URL = "http://api.joyplus.tv/joyplus-service/index.php/";

//	 public static final String BASE_URL =
//	 "http://112.64.18.12/joyplus-service/index.php/";

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

	public static String DEFAULT_APPKEY = "ijoyplus_android_0001";

	public static String APPKEY = "ijoyplus_android_0001";

	// 我们的sina账号
	public static String SINA_CONSUMER_KEY = "1490285522";
	public static String SINA_CONSUMER_SECRET = "f9ebc3ca95991b6dfce2c1608687e92b";
	public static String TECENTAPPID = "100317415";
	public static String SINA_REDIRECTURL = "https://api.weibo.com/oauth2/default.html";

	// Test Env
//	public static String Parse_AppId = "FtAzML5ln4zKkcL28zc9XR6kSlSGwXLdnsQ2WESB";
//	public static String Parse_ClientKey = "YzMYsyKNV7ibjZMfIDSGoV5zxsylV4evtO8x64tl";
	
	//Production Env
	public static String Parse_AppId = "UBgv7IjGR8i6AN0nS4diS48oQTk6YErFi3LrjK4P";
	public static String Parse_ClientKey = "Y2lKxqco7mN3qBmZ05S8jxSP8nhN92hSN4OHDZR8";
	
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
}
