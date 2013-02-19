package com.joyplus;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.joyplus.Adapters.Tab3Page1ListAdapter;
import com.joyplus.Adapters.Tab3Page1ListData;
import com.joyplus.Video.BlobCache;
import com.joyplus.Video.CacheManager;
import com.joyplus.Video.MovieActivity;
import com.umeng.analytics.MobclickAgent;

public class Tab3Page1 extends Activity implements OnTabActivityResultListener {
	private String TAG = "Tab3Page1";
	private AQuery aq;
	private App app;
	private String datainfo = null;
	private ArrayList dataStruct;
	private ListView ItemsListView;
	private Tab3Page1ListAdapter Tab3Page1Adapter;

	private static final String BOOKMARK_CACHE_FILE = "bookmark";
	private static final int BOOKMARK_CACHE_MAX_ENTRIES = 100;
	private static final int BOOKMARK_CACHE_MAX_BYTES = 10 * 1024;
	private static final int BOOKMARK_CACHE_VERSION = 1;

	private static final int HALF_MINUTE = 30 * 1000;
	private static final int TWO_MINUTES = 4 * HALF_MINUTE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab3page1);
		// 获取listview对象
		ItemsListView = (ListView) findViewById(R.id.listView1);
        ItemsListView.setOnItemClickListener(new OnItemClickListener() {
 			@Override
 			public void onItemClick(AdapterView<?> parent, View view,
 					int position, long id) {
 					Tab3Page1ListData m_Tab3Page1ListData = (Tab3Page1ListData) ItemsListView
 							.getItemAtPosition(position);
 					/*Intent intent = new Intent();
 					// 1：电影，2：电视剧，3：综艺，4：视频
 					if(m_Tab3Page1ListData.Pro_type.equalsIgnoreCase("1"))
 					{
 						intent.setClass(Tab3Page1.this, Detail_Movie.class);
 						intent.putExtra("prod_id", m_Tab3Page1ListData.Pro_ID);
 						try {
 							startActivity(intent);
 						} catch (ActivityNotFoundException ex) {
 							Log.e(TAG, "Call Detail_Movie failed", ex);
 						}
 					}
 					else if(m_Tab3Page1ListData.Pro_type.equalsIgnoreCase("2"))
 					{
 						intent.setClass(Tab3Page1.this, Detail_TV.class);
 						intent.putExtra("prod_id", m_Tab3Page1ListData.Pro_ID);
 						try {
 							startActivity(intent);
 						} catch (ActivityNotFoundException ex) {
 							Log.e(TAG, "Call Detail_TV failed", ex);
 						}
 					}
 					else if(m_Tab3Page1ListData.Pro_type.equalsIgnoreCase("3"))
 					{
 						intent.setClass(Tab3Page1.this, Detail_Show.class);
 						intent.putExtra("prod_id", m_Tab3Page1ListData.Pro_ID);
 						try {
 							startActivity(intent);
 						} catch (ActivityNotFoundException ex) {
 							Log.e(TAG, "Call Detail_Show failed", ex);
 						}
 					}*/
 					OnClickPlayIndex(position);
 			}
 		});
 		ItemsListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				OnDeleteListItem(arg2);
				return false;
			}
		});
		app = (App) getApplication();
		aq = new AQuery(this);

	}

	public void OnClickTab1TopLeft(View v) {
		Intent i = new Intent(this, Search.class);
		startActivity(i);

	}

	public void OnClickTab1TopRight(View v) {
		Intent i = new Intent(this, Setting.class);
		startActivity(i);

	}

	public void OnClickMore(View v) {
		Intent i = new Intent(this, Tab3Page1_more.class);
		startActivityForResult(i, 1);

	}

	@Override
	protected void onDestroy() {
		if (aq != null)
			aq.dismiss();
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		InitListData();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	public void OnClickImageView(View v) {
		/*
		 * Intent intent = new Intent(this, BuChongGeRenZhiLiao.class);
		 * intent.putExtra("prod_id", m_prod_id); intent.putExtra("prod_type",
		 * m_prod_type); try { startActivity(intent); } catch
		 * (ActivityNotFoundException ex) { Log.e(TAG,
		 * "OnClickImageView failed", ex); }
		 */
	}

	// 初始化list数据函数
	public void InitListData() {
		int m_num = 0;
		dataStruct = new ArrayList();
		String m_Value = null;
		String m_time = null;

		NotifyDataAnalysisFinished();

		String m_order = app.GetPlayData("order");
		if (m_order != null && m_order.trim().length() > 0) {

			String[] m_id = m_order.split("\\|");
			for (m_num = 0; m_num < m_id.length && m_num < 4; m_num++) {
				Tab3Page1ListData m_Tab3Page1ListData = new Tab3Page1ListData();
				m_Value = app.GetPlayData(m_id[m_num].trim());
				if (m_Value != null) {
					// prod_id|PROD_SOURCE | Pro_url|Pro_name|Pro_name1|Pro_time
					// prod_id|PROD_URI | Pro_url|Pro_name|Pro_name1|Pro_time
					String[] aa = m_Value.split("\\|");
					m_Tab3Page1ListData.Pro_ID = aa[0];
					m_Tab3Page1ListData.Pro_urlType = aa[1];
					m_Tab3Page1ListData.Pro_url = URLDecoder.decode(aa[2]);
					m_Tab3Page1ListData.Pro_name = aa[3];
					m_Tab3Page1ListData.Pro_name1 = aa[4];
					m_Tab3Page1ListData.Pro_type = aa[6];
					// m_Tab3Page1ListData.Pro_time = aa[5];
					m_time = getBookmark(Uri.parse(m_Tab3Page1ListData.Pro_url));
					if (m_time != null)
						m_Tab3Page1ListData.Pro_time = m_time;

					dataStruct.add(m_Tab3Page1ListData);
				}
			}
			m_num = m_id.length;
		}
		if (m_num == 0) {
			aq.id(R.id.imageNoitemBG).visible();
			aq.id(R.id.Layout1).gone();
			aq.id(R.id.Layout2).gone();
		} else if (m_num <= 4) {
			aq.id(R.id.imageNoitemBG).gone();
			aq.id(R.id.Layout1).visible();
			aq.id(R.id.Layout2).gone();
		} else if (m_num > 4) {
			aq.id(R.id.imageNoitemBG).gone();
			aq.id(R.id.Layout1).visible();
			aq.id(R.id.Layout2).visible();
		}

	}

	// 数据更新
	public void NotifyDataAnalysisFinished() {
		Toast toast;
		if (dataStruct != null && ItemsListView != null) {
			Tab3Page1ListAdapter listviewdetailadapter = getAdapter();
			ItemsListView.setAdapter(listviewdetailadapter);
		} else {
			app.MyToast(this, "ItemsListView empty.");
		}
	}

	private Tab3Page1ListAdapter getAdapter() {
		if (Tab3Page1Adapter == null) {
			ArrayList arraylist = dataStruct;
			Tab3Page1ListAdapter listviewdetailadapter = new Tab3Page1ListAdapter(
					this, arraylist);
			Tab3Page1Adapter = listviewdetailadapter;
		} else {
			ArrayList arraylist1 = dataStruct;
			Tab3Page1ListAdapter listviewdetailadapter1 = new Tab3Page1ListAdapter(
					this, arraylist1);
			Tab3Page1Adapter = listviewdetailadapter1;
		}
		return Tab3Page1Adapter;
	}

	private void OnDeleteListItem(final int item) {
		final Tab3Page1ListData m_Tab3Page1ListData = (Tab3Page1ListData) ItemsListView
				.getItemAtPosition(item);
		String program_name = "你确定删除  " + m_Tab3Page1ListData.Pro_name + "  吗？";
		AlertDialog.Builder builder = new AlertDialog.Builder(
				Tab3Page1.this.getParent());
		builder.setTitle("播放记录").setMessage(program_name)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 删除数据
						app.DeletePlayData(m_Tab3Page1ListData.Pro_ID);

						InitListData();

					}
				}).setNegativeButton("取消", null).create();
		builder.show();
	}

	// public void OnClickPlay(View v) {
	//
	// int index = Integer.parseInt(v.getTag().toString());
	// OnClickPlayIndex(index);
	//
	// }
	public void OnClickPlayIndex(int index) {
		Tab3Page1ListData m_Tab3Page1ListData = (Tab3Page1ListData) ItemsListView
				.getItemAtPosition(index);
		if (m_Tab3Page1ListData != null) {
			if (m_Tab3Page1ListData.Pro_urlType.equalsIgnoreCase("PROD_SOURCE")) {
				CallVideoPlayActivity(m_Tab3Page1ListData.Pro_ID,
						m_Tab3Page1ListData.Pro_url);
			} else if (m_Tab3Page1ListData.Pro_urlType
					.equalsIgnoreCase("PROD_URI")) {
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse(m_Tab3Page1ListData.Pro_url);
				intent.setData(content_url);
				startActivity(intent);
			}

		} else {
			app.MyToast(this, "m_Tab3Page1ListData is empty.");
		}
	}

	public void CallVideoPlayActivity(String prod_id, String m_uri) {

		Intent intent = new Intent(this, MovieActivity.class);
		intent.putExtra("prod_url", m_uri);
		intent.putExtra("prod_id", prod_id);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			Log.e(TAG, "mp4 fail", ex);
		}
	}

	public String getBookmark(Uri uri) {
		try {
			@SuppressWarnings("deprecation")
			BlobCache cache = CacheManager.getCache(this, BOOKMARK_CACHE_FILE,
					BOOKMARK_CACHE_MAX_ENTRIES, BOOKMARK_CACHE_MAX_BYTES,
					BOOKMARK_CACHE_VERSION);

			byte[] data = cache.lookup(uri.hashCode());
			if (data == null)
				return null;

			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(
					data));

			String uriString = DataInputStream.readUTF(dis);
			int bookmark = dis.readInt();
			int duration = dis.readInt();

			if (!uriString.equals(uri.toString())) {
				return null;
			}

			if ((bookmark < HALF_MINUTE) || (duration < TWO_MINUTES)
					|| (bookmark > (duration - HALF_MINUTE))) {
				return null;
			}
			if ((duration - bookmark) < TWO_MINUTES)
				return null;
			else
				return stringForTime(bookmark);
		} catch (Throwable t) {
			Log.w(TAG, "getBookmark failed", t);
		}
		return null;
	}

	private String stringForTime(long millis) {
		int totalSeconds = (int) millis / 1000;
		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;
		if (hours > 0) {
			return String.format("%d:%02d:%02d", hours, minutes, seconds)
					.toString();
		} else {
			return String.format("%02d:%02d", minutes, seconds).toString();
		}
	}

	@Override
	public void onTabActivityResult(int requestCode, int resultCode, Intent data) {
		InitListData();
	}
}
