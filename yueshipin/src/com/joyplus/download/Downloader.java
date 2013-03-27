package com.joyplus.download;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//import com.joyplus.App;
import com.joyplus.Constant;

import android.content.Context;
import android.content.Intent;
//import android.media.audiofx.BassBoost.Settings;
import android.util.Log;

public class Downloader {
	private String urlstr;// 下载的地址
	private String localfile;// 保存路径
	private String prod_id;// 视频id
	private String my_index;// 视频二级id
	private int compeleteSize;// 下载了多少
	private int fileSize;// 所要下载的文件的大小
	private String urlposter;// 下载文件的图片
	private String my_name;// 下载文件的名字
	private String download_state;// 下载文件的大小
	private Context context;
	private List<DownloadInfo> infos;// 存放下载信息类的集合
	private static final int INIT = 1;// 定义三种下载的状态：初始化状态，正在下载状态，暂停状态
	private static final int DOWNLOADING = 2;
	private static final int PAUSE = 3;
	private static final int STOP = 4;
	private int state = INIT;

	public Downloader(int compeleteSize, int fileSize, String prod_id,
			String my_index, String urlstr, String urlposter, String my_name,
			String download_state, Context context) {
		this.compeleteSize = compeleteSize;
		this.fileSize = fileSize;
		this.prod_id = prod_id;
		this.my_index = my_index;
		this.urlstr = urlstr;
		this.urlposter = urlposter;
		this.my_name = my_name;
		this.download_state = download_state;
		this.context = context;
	}

	/**
	 * 判断是否正在下载
	 */
	public boolean isdownloading() {
		return state == DOWNLOADING;
	}

	public DownloadInfo getDownloaderInfors() {
		if (isFirst(prod_id)) {
			Log.v("TAG", "isFirst");
			init();
			infos = new ArrayList<DownloadInfo>();
			DownloadInfo info = new DownloadInfo(compeleteSize, fileSize,
					prod_id, my_index, urlstr, urlposter, my_name,
					download_state);
			infos.add(info);
			// 保存infos中的数据到数据库
			Dao.getInstance(context).saveInfos(infos);
			return info;
		} else {
			// 得到数据库中已有的urlstr的下载器的具体信息
			infos = Dao.getInstance(context).getInfos(prod_id, my_index);
			Log.v("TAG", "not isFirst size=" + infos.size());
			int compeleteSize = 0;
			for (DownloadInfo info : infos) {
				compeleteSize += info.getCompeleteSize();
				fileSize = info.getFileSize();
			}
			return new DownloadInfo(compeleteSize, fileSize, prod_id, my_index,
					urlstr, urlposter, my_name, download_state);
		}
	}

	/**
	 * 初始化
	 */
	private void init() {
		localfile = Constant.PATH_VIDEO + prod_id + "_" + my_index + ".mp4";
		try {
			URL url = new URL(urlstr);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setConnectTimeout(5000);
			connection.setRequestMethod("GET");
			fileSize = connection.getContentLength();
			connection.disconnect();
			// 本地访问文件
			RandomAccessFile accessFile = new RandomAccessFile(localfile, "rwd");
			accessFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断是否是第一次 下载
	 */
	private boolean isFirst(String prod_id) {
		return Dao.getInstance(context).isHasInfors(prod_id, my_index);
	}

	/**
	 * 利用线程开始下载数据
	 */
	public void download() {
		if (infos != null) {
			if (state == DOWNLOADING)// 设置flag变量,设置线程数量为1
				return;
			for (DownloadInfo info : infos) {
				if (Dao.getInstance(context).isHasInforsDownloading(
						"downloading")) {
					state = DOWNLOADING;
					info.setDownload_state("downloading");
					Dao.getInstance(context).updataInfoState(
							info.getDownload_state(), info.getProd_id(),
							info.getMy_index());
					new MyThread(info.getCompeleteSize(), info.getFileSize(),
							info.getProd_id(), info.getMy_index(),
							info.getUrl(), context).start();
				} else {
					DownloadInfo tempInfo = Dao.getInstance(context)
							.getOneStateInfo("downloading");
					if (tempInfo.getProd_id().equalsIgnoreCase(
							info.getProd_id())
							&& tempInfo.getMy_index().equalsIgnoreCase(
									info.getMy_index())) {
						state = DOWNLOADING;
						info.setDownload_state("downloading");
						Dao.getInstance(context).updataInfoState(
								info.getDownload_state(), info.getProd_id(),
								info.getMy_index());
						new MyThread(info.getCompeleteSize(),
								info.getFileSize(), info.getProd_id(),
								info.getMy_index(), info.getUrl(), context)
								.start();
					}
				}
			}
		}
	}

	public class MyThread extends Thread {
		private int compeleteSize;
		private int fileSize;
		private String prod_id;
		private String my_index;
		private String urlstr;
		private Context context;
		long percent = 0;

		public MyThread(int compeleteSize, int fileSize, String prod_id,
				String my_index, String urlstr, Context context) {
			this.compeleteSize = compeleteSize;
			this.fileSize = fileSize;
			this.prod_id = prod_id;
			this.my_index = my_index;
			this.urlstr = urlstr;
			this.context = context;
		}

		// localfile的值是什么呢
		@Override
		public void run() {
			// 标记此线程为true
			localfile = Constant.PATH_VIDEO + prod_id + "_" + my_index + ".mp4";
			HttpURLConnection connection = null;
			RandomAccessFile randomAccessFile = null;
			InputStream inputstream = null;
			try {
				URL url = new URL(urlstr);
				connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(10000);
				connection.setRequestMethod("GET");
				// 设置范围，格式为Range：bytes x-y;
				connection.setRequestProperty("Range", "bytes=" + compeleteSize
						+ "-" + (fileSize - 1));// 后面的
				randomAccessFile = new RandomAccessFile(localfile, "rwd");
				randomAccessFile.seek(compeleteSize);
				inputstream = connection.getInputStream();
				// 将要下载的文件写到保存在保存路径下的文件
				byte[] buffer = new byte[1024 * 50];
				int length = -1;
				while ((length = inputstream.read(buffer)) != -1) {
					randomAccessFile.write(buffer, 0, length);
					compeleteSize += length;
					if (compeleteSize < fileSize / 1000) {
						Intent intent = new Intent();
						intent.setAction("UpdateProgressUI");
						context.sendBroadcast(intent);
					}
					if (((long) compeleteSize * 100 / fileSize - percent) > 0.5) {
						percent = (long) compeleteSize * 100 / fileSize;
						// 更新数据库中的下载信息
						Dao.getInstance(context).updataInfos(compeleteSize,
								prod_id, my_index);
						// 用广播将下载信息传给进度条，对进度条进行更新
						Intent intent = new Intent();
						intent.setAction("UpdateProgressUI");
						context.sendBroadcast(intent);
					}

					if (compeleteSize == fileSize) {
						Dao.getInstance(context).updataInfoState("pause",
								prod_id, my_index);
						state = STOP;
						randomAccessFile.close();
						Intent i = new Intent();
						i.setAction("completeDownload");
						context.sendBroadcast(i);
					}
					if (state == PAUSE) {
						Dao.getInstance(context).updataInfoState("pause",
								prod_id, my_index);
						return;
					}
					Log.i("Downloader:compeleteSize",
							Integer.toString(compeleteSize));
					Log.i("Downloader:fileSize", Integer.toString(fileSize));
				}
			} catch (Exception e) {
				state = STOP;
				Dao.getInstance(context).updataInfoState("pause", prod_id,
						my_index);
				e.printStackTrace();
			} finally {
				state = STOP;
				Dao.getInstance(context).updataInfoState("pause", prod_id,
						my_index);
			}
		}
	}

	// 删除数据库中urlstr对应的下载器信息
	public void delete(String urlstr) {
		Dao.getInstance(context).delete(prod_id, my_index);
	}

	// 设置暂停
	public void pause() {
		state = PAUSE;
	}

	// 重置下载状态
	public void reset() {
		state = INIT;
	}

	public int getstate() {
		return state;
	}
}