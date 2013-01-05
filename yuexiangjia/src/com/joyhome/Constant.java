package com.joyhome;

import android.os.Environment;

public class Constant {

	public static String PATH = Environment.getExternalStorageDirectory()
			+ "/joy/image_cache/";
	public static String PATH_HEAD = Environment.getExternalStorageDirectory()
			+ "/joy/admin/";
	public static String PATH_XML = Environment.getExternalStorageDirectory()
			+ "/joy/";


	// 我们的sina账号
	public static String SINA_CONSUMER_KEY = "1490285522";
	public static String SINA_CONSUMER_SECRET = "f9ebc3ca95991b6dfce2c1608687e92b";
	public static String TECENTAPPID = "100317415";
	public static String SINA_REDIRECTURL = "https://api.weibo.com/oauth2/default.html";


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
