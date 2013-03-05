package com.joyplus;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import com.androidquery.AQuery;
import com.joyplus.Video.VideoPlayerActivity;
import com.joyplus.download.Dao;
import com.joyplus.download.DownLoadAdapter;
import com.joyplus.download.DownloadInfo;
import com.joyplus.download.DownloadTask;
import com.joyplus.download.Downloader;
//import com.joyplus.download.LoadInfo;
import com.umeng.analytics.MobclickAgent;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Video_Cache extends Activity {
	private static final String TAG = "Video_Cache";
	private App app;
	private AQuery aq;
	private Activity activity = null;
	private Context context = null;

	ProgressBar progressBar = null;
	TextView textView;
	private GridView gridView;
	public List<DownloadInfo> data;
	View tempview = null;
	DownLoadAdapter adapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.video_cache);
		app = (App) getApplication();
		aq = new AQuery(this);
		MobclickAgent.updateOnlineConfig(this);
		// add by yyc
		registerBoradcastReceiver();
		progressBar = (ProgressBar) findViewById(R.id.SDCardprogress);
		gridView = (GridView) findViewById(R.id.gridView);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		data = Dao.getInstance(Video_Cache.this).getDownloadInfosGroup();
		adapter = new DownLoadAdapter(this, data);
		gridView.setAdapter(adapter);

		activity = this;
		context = Video_Cache.this;
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				tempview = view;
				DownloadInfo info = data.get(position);
				if (info.getIndex().equalsIgnoreCase("movie")) {
					if (app.isNetworkAvailable()) {
						if (info.getState().equalsIgnoreCase("downloading")) {
							info.setState("stop");
							Dao.getInstance(Video_Cache.this).updataInfoState(
									info.getState(), info.getProdId(),
									info.getIndex());
							String localfile = Constant.PATH_VIDEO
									+ info.getProdId() + "_" + info.getIndex()
									+ ".mp4";
							if (App.downloaders.get(localfile) != null) {
								App.downloaders.get(localfile).pause();
								autoDownloadFile();
							}
						} else if (info.getState().equalsIgnoreCase("wait")) {
							if (Dao.getInstance(Video_Cache.this)
									.isHasInforsDownloading("downloading")) {
								String localfile = Constant.PATH_VIDEO
										+ info.getProdId() + "_"
										+ info.getIndex() + ".mp4";
								// 点击后就开始下载这个item里面的内容
								DownloadTask downloadtask = new DownloadTask(
										view, activity, context, info
												.getProdId(), info.getIndex(),
										info.getUrl(), localfile);
								downloadtask.execute(info.getProdId(),
										info.getIndex(), info.getUrl(),
										info.getPoster(), info.getName(),
										info.getState());
								if (App.downloaders.get(localfile) != null) {
									App.downloaders.get(localfile).reset();
									App.downloaders.get(localfile).download();
								}
							} else {
								info.setState("stop");
								Dao.getInstance(Video_Cache.this)
										.updataInfoState(info.getState(),
												info.getProdId(),
												info.getIndex());
							}
						} else if (info.getState().equalsIgnoreCase("stop")) {
							/*
							 * 判断当前是否有下载,有下载则转为等待,没下载则直接为下载
							 */
							if (Dao.getInstance(Video_Cache.this)
									.isHasInforsDownloading("downloading")) {
								String localfile = Constant.PATH_VIDEO
										+ info.getProdId() + "_"
										+ info.getIndex() + ".mp4";
								// 点击后就开始下载这个item里面的内容
								DownloadTask downloadtask = new DownloadTask(
										view, activity, context, info
												.getProdId(), info.getIndex(),
										info.getUrl(), localfile);
								downloadtask.execute(info.getProdId(),
										info.getIndex(), info.getUrl(),
										info.getPoster(), info.getName(),
										info.getState());
							} else {
								info.setState("wait");
								Dao.getInstance(Video_Cache.this)
										.updataInfoState(info.getState(),
												info.getProdId(),
												info.getIndex());
							}
						}
						showGridView();
					} else {
						if (info.getCompeleteSize() < info.getFileSize()) {
							app.MyToast(Video_Cache.this, getResources()
									.getString(R.string.networknotwork));
						}

					}

					if (info.getCompeleteSize() == info.getFileSize()) {
						// 打开播放界面
						String localfile = Constant.PATH_VIDEO
								+ info.getProdId() + "_" + info.getIndex()
								+ ".mp4";
						Intent intent = new Intent(Video_Cache.this,
								VideoPlayerActivity.class);
						intent.putExtra("path", localfile);
						intent.putExtra("title", info.getProdId());

						try {
							startActivity(intent);
						} catch (ActivityNotFoundException ex) {
							Log.e(TAG, "mp4 fail", ex);
						}
					}
					showGridView();
				} else {
					/*
					 * 启动activity时将prod_id传过去
					 */
					Bundle bundle = new Bundle();
					bundle.putString("prod_id", info.getProdId());
					Intent intent = new Intent();
					intent.putExtras(bundle);
					intent.setClass(Video_Cache.this, Video_Cache_Detail.class);
					startActivity(intent);
				}
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
			String localfile = Constant.PATH_VIDEO + data.get(i).getProdId()
					+ "_" + data.get(i).getIndex() + ".mp4";
			if (App.downloaders.get(localfile) == null) {
				Downloader downloader = new Downloader(data.get(i)
						.getCompeleteSize(), data.get(i).getFileSize(), data
						.get(i).getProdId(), data.get(i).getIndex(), data
						.get(i).getUrl(), data.get(i).getPoster(), data.get(i)
						.getName(), data.get(i).getState(), Video_Cache.this);
				App.downloaders.put(localfile, downloader);
			}
		}
		showGridView();
	}

	public void OnDeleteGridViewItem(final int item) {
		if (data.get(item).getIndex().equalsIgnoreCase("movie")) {
			String program_name = "你确定删除影片:<<" + data.get(item).getName()
					+ ">>吗？";// 最好加上名字
			AlertDialog.Builder builder = new AlertDialog.Builder(
					Video_Cache.this);
			builder.setTitle("下载记录")
					.setMessage(program_name)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// 删除数据库数据,从新显示
									DownloadInfo info = data.get(item);
									if (App.downloaders.get(Constant.PATH_VIDEO
											+ info.getProdId() + "_"
											+ info.getIndex() + ".mp4") != null) {
										App.downloaders.get(
												Constant.PATH_VIDEO
														+ info.getProdId()
														+ "_" + info.getIndex()
														+ ".mp4").pause();
									}
									Dao.getInstance(Video_Cache.this).delete(
											info.getProdId(), info.getIndex());
									data = Dao.getInstance(Video_Cache.this)
											.getDownloadInfosGroup();
									DownLoadAdapter adapter = new DownLoadAdapter(
											Video_Cache.this, data);
									gridView.setAdapter(adapter);
									adapter.notifyDataSetChanged();
									File file = new File(Constant.PATH_VIDEO
											+ info.getProdId() + "_"
											+ info.getIndex() + ".mp4");
									if (file.exists()) {
										file.delete();
									}
								}
							}).setNegativeButton("取消", null).create();
			builder.show();
		} else {
			// 添加相应的处理
		}
	}

	private void showGridView() {
		// TODO Auto-generated method stub
		data = Dao.getInstance(Video_Cache.this).getDownloadInfosGroup();
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
			Toast.makeText(Video_Cache.this, "没有sdCard", Toast.LENGTH_SHORT)
					.show();
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
		showGridView();
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
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
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
		if (Dao.getInstance(Video_Cache.this).isHasInforsDownloading(
				"downloading")) {
			for (int i = 0; i < data.size(); i++) {
				if (data.get(i).getState().equalsIgnoreCase("wait")) {
					DownloadInfo infos = data.get(i);
					String localfile = Constant.PATH_VIDEO + infos.getProdId()
							+ "_" + infos.getIndex() + ".mp4";
					// 点击后就开始下载这个item里面的内容
					DownloadTask downloadtask = new DownloadTask(tempview,
							activity, context, infos.getProdId(),
							infos.getIndex(), infos.getUrl(), localfile);
					downloadtask.execute(infos.getProdId(), infos.getIndex(),
							infos.getUrl(), infos.getPoster(), infos.getName(),
							infos.getState());
					break;
				}
			}
		}
	}
}