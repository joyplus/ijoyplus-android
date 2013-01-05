package com.joyhome;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Video;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.joyhome.Adapters.Tab2GridData;
import com.joyhome.Adapters.Tab2GridData;
import com.joyhome.Adapters.Tab2_VideoAdapter;
import com.joyhome.Video.MovieActivity;
import com.umeng.analytics.MobclickAgent;

public class Tab2_Video extends Activity implements
		AdapterView.OnItemClickListener {
	private String TAG = "Tab2_Video";
	private App app;
	private AQuery aq;
	private int BUCKET_ID = 0;

	private int Fromepage;
	private ArrayList dataStruct;
	private GridView gridView;
	private Tab2_VideoAdapter Tab2Adapter;

	private static final String EXTERNAL_MEDIA = "external";
	private static final Uri mBaseUri = Files.getContentUri(EXTERNAL_MEDIA);
	private static final Uri mWatchUriVideo = Video.Media.EXTERNAL_CONTENT_URI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video);
		app = (App) getApplication();
		aq = new AQuery(this);
		Intent intent = getIntent();

		gridView = (GridView) findViewById(R.id.gridView1);

		BUCKET_ID = intent.getIntExtra("BUCKET_ID", 0);
		if (BUCKET_ID != 0)
			GetVideoData();

	}

	public void OnClickTopLeft(View v) {
		Intent i = new Intent(this, Setting.class);
		startActivity(i);

	}

	public void OnClickTopRight(View v) {
		

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

	// listview的点击事件接口函数
	public void onItemClick(AdapterView adapterview, View view, int i, long l) {
		Tab2GridData m_Tab2GridData = (Tab2GridData) gridView
				.getItemAtPosition(i);
		if (m_Tab2GridData != null) {
			CallVideoPlayActivity(m_Tab2GridData._data,m_Tab2GridData.title);
		} else {
			app.MyToast(this, "m_Tab2GridData is empty.");
		}
	}

	private void GetVideoData() {
		dataStruct = new ArrayList();

		LoadVideoFromSDCard();
		Tab2Adapter = new Tab2_VideoAdapter(this, dataStruct);

		gridView.setAdapter(Tab2Adapter);
		gridView.setOnItemClickListener(this);
	}

	/**
	 * Async task for loading the images from the SD card.
	 */
	private void LoadVideoFromSDCard() {
		String[] mediaColumns = new String[] { "_id", "_data", "_display_name",
				"_size", "mime_type", "date_added", "date_modified", "title",
				"duration", "artist", "album", "bucket_id",
				"bucket_display_name" };

		// 首先检索SDcard上所有的video
		Cursor cursor = this
				.managedQuery(mWatchUriVideo, mediaColumns, "bucket_id=?",
						new String[] { String.valueOf(BUCKET_ID) }, null);

		int size = cursor.getCount();
		// If size is 0, there are no images on the SD Card.
		if (size == 0) {
			// No Images available, post some message to the user
		}
		int imageID = 0;
		for (int i = 0; i < size; i++) {
			cursor.moveToPosition(i);
			DataAdd(cursor);
		}
		// cursor.close();
	}

	private void DataAdd(Cursor cursor) {
		Tab2GridData m_Tab2GridData = new Tab2GridData();
		m_Tab2GridData._id = cursor.getInt(0);
		m_Tab2GridData._data = cursor.getString(1);
		m_Tab2GridData._display_name = cursor.getString(2);
		m_Tab2GridData._size = cursor.getString(3);
		m_Tab2GridData.mime_type = cursor.getString(4);
		m_Tab2GridData.date_added = cursor.getString(5);
		m_Tab2GridData.date_modified = cursor.getString(6);
		m_Tab2GridData.title = cursor.getString(7);
		m_Tab2GridData.duration = cursor.getString(8);
		m_Tab2GridData.artist = cursor.getString(9);
		m_Tab2GridData.album = cursor.getString(10);
		m_Tab2GridData.bucket_id = cursor.getInt(11);
		m_Tab2GridData.bucket_display_name = cursor.getString(12);

		dataStruct.add(m_Tab2GridData);
	}

	public void CallVideoPlayActivity(String m_uri,String title) {

		Intent intent = new Intent(this, MovieActivity.class);
		intent.putExtra("prod_url", m_uri);
		intent.putExtra("title", title);

		try {
			startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			Log.e(TAG, "CallVideoPlayActivity failed", ex);
		}

	}

}
