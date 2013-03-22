package com.joyplus;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import com.androidquery.AQuery;
import com.joyplus.Video.VideoPlayerActivity;
import com.joyplus.download.Dao;
import com.joyplus.download.DownLoadAdapterDetail;
import com.joyplus.download.DownloadInfo;
import com.joyplus.download.DownloadTask;
import com.joyplus.download.Downloader;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class Video_Cache_Detail extends Activity {

	private static final String TAG = "Video_Cache";
	private App app;
	private AQuery aq;
	private Activity activity = null;
	private Context context = null;
	private String prod_id = null;

	ProgressBar progressBar = null;
	TextView textView;
	private GridView gridView;
	public List<DownloadInfo> data;
	View tempview = null;
	DownLoadAdapterDetail adapter = null;
	private boolean isnotChecked = true;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.video_cache_detail);
		app = (App) getApplication();
		aq = new AQuery(this);
		MobclickAgent.updateOnlineConfig(this);
		// add by yyc
		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		prod_id = bundle.getString("prod_id");
		registerBoradcastReceiver();
		progressBar = (ProgressBar) findViewById(R.id.SDCardprogress);
		gridView = (GridView) findViewById(R.id.gridView);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		data = Dao.getInstance(Video_Cache_Detail.this).getInfosOfProd_id(
				prod_id);
		adapter = new DownLoadAdapterDetail(this, data);
		gridView.setAdapter(adapter);

		activity = this;
		context = Video_Cache_Detail.this;
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				tempview = view;
				DownloadInfo info = data.get(position);
				if (app.isNetworkAvailable()) {
					if (info.getDownload_state()
							.equalsIgnoreCase("downloading")) {
						info.setDownload_state("pause");
						Dao.getInstance(Video_Cache_Detail.this)
								.updataInfoState(info.getDownload_state(),
										info.getProd_id(), info.getMy_index());
						String localfile = Constant.PATH_VIDEO
								+ info.getProd_id() + "_" + info.getMy_index()
								+ ".mp4";
						if (App.downloaders.get(localfile) != null) {
							App.downloaders.get(localfile).pause();
							autoDownloadFile();
						}
					} else if (info.getDownload_state()
							.equalsIgnoreCase("wait")) {
						if (Dao.getInstance(Video_Cache_Detail.this)
								.isHasInforsDownloading("downloading")) {
							info.setDownload_state("downloading");
							Dao.getInstance(Video_Cache_Detail.this)
									.updataInfoState(info.getDownload_state(),
											info.getProd_id(),
											info.getMy_index());// 更新为正在下载中
							String localfile = Constant.PATH_VIDEO
									+ info.getProd_id() + "_"
									+ info.getMy_index() + ".mp4";
							// 点击后就开始下载这个item里面的内容
							DownloadTask downloadtask = new DownloadTask(view,
									activity, context, info.getProd_id(), info
											.getMy_index(), info.getUrl(),
									localfile);
							downloadtask.execute(info.getProd_id(),
									info.getMy_index(), info.getUrl(),
									info.getUrlposter(), info.getMy_name(),
									info.getDownload_state());
						} else {
							info.setDownload_state("pause");
							Dao.getInstance(Video_Cache_Detail.this)
									.updataInfoState(info.getDownload_state(),
											info.getProd_id(),
											info.getMy_index());
						}
					} else if (info.getDownload_state().equalsIgnoreCase(
							"pause")) {
						/*
						 * 判断当前是否有下载,有下载则转为等待,没下载则直接为下载
						 */
						if (Dao.getInstance(Video_Cache_Detail.this)
								.isHasInforsDownloading("downloading")) {
							info.setDownload_state("downloading");
							Dao.getInstance(Video_Cache_Detail.this)
									.updataInfoState(info.getDownload_state(),
											info.getProd_id(),
											info.getMy_index());// 更新为正在下载中
							String localfile = Constant.PATH_VIDEO
									+ info.getProd_id() + "_"
									+ info.getMy_index() + ".mp4";
							// 点击后就开始下载这个item里面的内容
							DownloadTask downloadtask = new DownloadTask(view,
									activity, context, info.getProd_id(), info
											.getMy_index(), info.getUrl(),
									localfile);
							downloadtask.execute(info.getProd_id(),
									info.getMy_index(), info.getUrl(),
									info.getUrlposter(), info.getMy_name(),
									info.getDownload_state());
						} else {
							info.setDownload_state("wait");
							Dao.getInstance(Video_Cache_Detail.this)
									.updataInfoState(info.getDownload_state(),
											info.getProd_id(),
											info.getMy_index());
						}
					}
				} else {
					if (info.getCompeleteSize() < info.getFileSize()) {
						app.MyToast(Video_Cache_Detail.this, getResources()
								.getString(R.string.networknotwork));
					}
				}
				if (info.getCompeleteSize() == info.getFileSize()) {
					// 打开播放界面
					String localfile = Constant.PATH_VIDEO + info.getProd_id()
							+ "_" + info.getMy_index() + ".mp4";
					Intent intent = new Intent(Video_Cache_Detail.this,
							VideoPlayerActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("path", localfile);
					bundle.putString("title", info.getMy_name());
					bundle.putString("prod_id", info.getProd_id());
					bundle.putLong("current_time", 0);

					if (info.getMy_index().contains("_tv")) {
						bundle.putString("prod_type", "3");
						bundle.putString("prod_subname", info.getMy_index());// ???
					} else {
						bundle.putString("prod_type", "2");
						bundle.putString("prod_subname",
								"第" + info.getMy_index() + "集"); // yu
					}
					intent.putExtras(bundle);
					try {
						startActivity(intent);
					} catch (ActivityNotFoundException ex) {
						Log.e(TAG, "mp4 fail", ex);
					}
				}
				showGridView();
			}
		});
		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				OnDeleteGridViewItem(position);
				return false;
			}
		});
		getSize();
		for (int i = 0; i < data.size(); i++) {
			String localfile = Constant.PATH_VIDEO + data.get(i).getProd_id()
					+ "_" + data.get(i).getMy_index() + ".mp4";
			if (App.downloaders.get(localfile) == null) {
				Downloader downloader = new Downloader(data.get(i)
						.getCompeleteSize(), data.get(i).getFileSize(), data
						.get(i).getProd_id(), data.get(i).getMy_index(), data
						.get(i).getUrl(), data.get(i).getUrlposter(), data.get(
						i).getMy_name(), data.get(i).getDownload_state(),
						Video_Cache_Detail.this);
				App.downloaders.put(localfile, downloader);
			}
		}
		showGridView();
	}

	public void OnDeleteGridViewItem(final int item) {
		String program_name = "你确定删除影片:<<" + data.get(item).getMy_name()
				+ ">>吗？";// 最好加上名字
		AlertDialog.Builder builder = new AlertDialog.Builder(
				Video_Cache_Detail.this);
		builder.setTitle("下载记录").setMessage(program_name)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 删除数据库数据,从新显示
						DownloadInfo info = data.get(item);
						if (App.downloaders.get(Constant.PATH_VIDEO
								+ info.getProd_id() + "_" + info.getMy_index()
								+ ".mp4") != null) {
							App.downloaders
									.get(Constant.PATH_VIDEO
											+ info.getProd_id() + "_"
											+ info.getMy_index() + ".mp4")
									.pause();
						}
						Dao.getInstance(Video_Cache_Detail.this).delete(
								info.getProd_id(), info.getMy_index());
						File file = new File(Constant.PATH_VIDEO
								+ info.getProd_id() + "_" + info.getMy_index()
								+ ".mp4");
						if (file.exists()) {
							file.delete();
						}
						showGridView();
					}
				}).setNegativeButton("取消", null).create();
		builder.show();
	}

	private void showGridView() {
		// TODO Auto-generated method stub
		data = Dao.getInstance(Video_Cache_Detail.this).getInfosOfProd_id(
				prod_id);
		if(isnotChecked){
			for (int i = 0; i < data.size(); i++) {
	 			String localfile = Constant.PATH_VIDEO + data.get(i).getProd_id()
	 					+ "_" + data.get(i).getMy_index() + ".mp4";
	 		    File file = new File(localfile);
	 		    if(!file.exists()){
	 		    	data.remove(i);
	 		    }
	 		}
			isnotChecked = false;
			}
		adapter.refresh(data);
		if (data.isEmpty()) {
			aq.id(R.id.none_cache).visible();
		}
	}

	

	void getSize() {
		// viewHolder.myTextView.setText("");
		progressBar.setProgress(0);
		// 判断是否有插入存储卡
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File path = Environment.getExternalStorageDirectory();
		// 取得sdcard文件路径
		StatFs statfs = new StatFs(path.getPath());
		long blocSize = statfs.getBlockSize();
		float totalBlocks = statfs.getBlockCount();
		int sizeInMb = (int) (blocSize * totalBlocks) / 1024 / 1024; // 计算总容量
		long availableBlocks = statfs.getAvailableBlocks(); // 获取可用容量
		float percent = 1 - availableBlocks / totalBlocks; // 获取已用比例
		percent = (int) (percent * 100);
		progressBar.setProgress((int) (percent));
		String Text = "总共：" + sizeInMb + "MB" + "   " + "已用:" + sizeInMb
				* percent / 100 + "MB";
		aq.id(R.id.SDcardTextView).text(Text);
		} else if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_REMOVED)) {
			Toast.makeText(Video_Cache_Detail.this, "未安装sdCard",
					Toast.LENGTH_SHORT).show();
		}
	}

	// 返回数组，下标1代表大小，下标2代表单位 KB/MB
	String[] filesize(long size) {
		String str = "";
		if (size >= 1024) {
			str = "KB";
			size /= 1024;
			if (size >= 1024) {
				str = "MB";
				size /= 1024;
			}
		}
		DecimalFormat formatter = new DecimalFormat();
		formatter.setGroupingSize(3);
		String result[] = new String[2];
		result[0] = formatter.format(size);
		result[1] = str;
		return result;
	}

	public void OnClickTab1TopLeft(View v) {
		finish();
	}

	@Override
	public void onResume() {
		super.onResume();
		 data = Dao.getInstance(Video_Cache_Detail.this).getDownloadInfosGroup();
         for (int i = 0; i < data.size(); i++) {
 			String localfile = Constant.PATH_VIDEO + data.get(i).getProd_id()
 					+ "_" + data.get(i).getMy_index() + ".mp4";
 		    File file = new File(localfile);
 		    if(!file.exists()){
 		    	data.remove(i);
 		    }
 		}
		adapter.refresh(data);
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction("UpdateProgressUI");
		myIntentFilter.addAction("completeDownload");
		registerReceiver(mBroadcastReceiverDetail, myIntentFilter);
	}

	private BroadcastReceiver mBroadcastReceiverDetail = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("UpdateProgressUI")) {
				// 更新界面的效率不高
				showGridView();
			} else if (action.equals("completeDownload")) {
				autoDownloadFile();
			}

		}
	};

	public void autoDownloadFile() {
		if (Dao.getInstance(Video_Cache_Detail.this).isHasInforsDownloading(
				"downloading")) {

			if (Dao.getInstance(Video_Cache_Detail.this)
					.getOneStateInfo("wait") != null) {
				DownloadInfo infos = Dao.getInstance(Video_Cache_Detail.this)
						.getOneStateInfo("wait");
				infos.setDownload_state("downloading");
				Dao.getInstance(Video_Cache_Detail.this).updataInfoState(
						infos.getDownload_state(), infos.getProd_id(),
						infos.getMy_index());// 更新为正在下载中
				String localfile = Constant.PATH_VIDEO + infos.getProd_id()
						+ "_" + infos.getMy_index() + ".mp4";
				// 点击后就开始下载这个item里面的内容
				DownloadTask downloadtask = new DownloadTask(tempview,
						activity, context, infos.getProd_id(),
						infos.getMy_index(), infos.getUrl(), localfile);
				downloadtask.execute(infos.getProd_id(), infos.getMy_index(),
						infos.getUrl(), infos.getUrlposter(),
						infos.getMy_name(), infos.getDownload_state());
			}
		}
	}
}
