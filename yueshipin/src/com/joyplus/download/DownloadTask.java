package com.joyplus.download;

import com.joyplus.App;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;


public class DownloadTask extends AsyncTask<String, Integer, DownloadInfo> {
	Downloader downloader = null;
	View v = null;
	int compeleteSize;
	int fileSize;
	String prod_id;
	String my_index;
	String urlstr;
	App app ;
	Context context;
	String localfile;
	String urlposter;
	String my_name;
	String download_state;
	boolean flag = false;

	public DownloadTask(final View v,Activity activity,Context context,String prod_id,String my_index,String urlstr,String localfile) {
		this.v = v; // v为null
		this.app = (App) activity.getApplication();
		this.context = context;
		this.prod_id = prod_id;
		this.my_index = my_index;
		this.urlstr = urlstr;
		this.localfile = localfile;
	}

	@Override
	protected DownloadInfo doInBackground(String... params) {
		String prod_id = params[0];
		String my_index = params[1];
		urlstr = params[2];
		urlposter = params[3];
		my_name = params[4];
		download_state = params[5];
		// 初始化一个downloader下载器
		//downloader = App.downloaders.get(prod_id);
		downloader = App.downloaders.get(localfile);
		if (downloader == null) {
			downloader = new Downloader(compeleteSize,fileSize,prod_id,my_index,urlstr,urlposter,
					my_name,download_state,context);
			//App.downloaders.put(prod_id, downloader);
			App.downloaders.put(localfile, downloader);
			app.prodIdList.add(prod_id);//将新申请的下载视频的prod_id放置prodIdList里面
		}
		if (downloader.isdownloading())
			return null;
		// 得到下载信息类的个数组成集合
		return downloader.getDownloaderInfors();
	}

	@Override
	protected void onPostExecute(DownloadInfo downloadInfo) {
		if (downloadInfo != null) {
			// 显示进度条
			// 调用方法开始下载
			downloader.download();
		}
	}
	
	public String getlocalfile()
	{
		return localfile;
	}
	public String geturl()
	{
		return urlstr;
	}
};