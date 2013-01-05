package com.joyhome;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.androidquery.AQuery;
import com.joyhome.Adapters.Tab2GridAdapter;
import com.joyhome.Adapters.Tab2GridData;
import com.umeng.analytics.MobclickAgent;

public class Tab2 extends Activity implements AdapterView.OnItemClickListener {
	private String TAG = "Tab2";
	private App app;
	private AQuery aq;

	private int Fromepage;
	private ArrayList dataStruct;
	private GridView gridView;
	private Tab2GridAdapter Tab2Adapter;

	private static final String EXTERNAL_MEDIA = "external";
	private static final Uri mBaseUri = Files.getContentUri(EXTERNAL_MEDIA);
	private static final Uri mWatchUriImage = Images.Media.EXTERNAL_CONTENT_URI;
	private static final Uri mWatchUriVideo = Video.Media.EXTERNAL_CONTENT_URI;
	private static final Uri mWatchUriAudio = Audio.Media.EXTERNAL_CONTENT_URI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab2);
		app = (App) getApplication();
		aq = new AQuery(this);

		gridView = (GridView) findViewById(R.id.gridView1);

		GetVideoData();

	}

	public void OnClickTab1TopLeft(View v) {

	}

	public void OnClickTab1TopRight(View v) {

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
			// app.MyToast(this, m_Tab2GridData.Pic_name, Toast.LENGTH_LONG)
			// .show();
			Intent intent = new Intent(this, Tab2_Video.class);
			intent.putExtra("BUCKET_ID", m_Tab2GridData.bucket_id);
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException ex) {
				Log.e(TAG, "Call Tab2_Video failed", ex);
			}
		} else {
			app.MyToast(this, "m_Tab2GridData is empty.");
		}
	}

	private void GetVideoData() {
		dataStruct = new ArrayList();

		LoadVideoFromSDCard();
		Tab2Adapter = new Tab2GridAdapter(this, dataStruct);

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
		Cursor cursor = this.managedQuery(mWatchUriVideo, mediaColumns, null,
				null, null);

		int size = cursor.getCount();
		// If size is 0, there are no images on the SD Card.
		if (size == 0) {
			// No Images available, post some message to the user
		}
		int imageID = 0;
		for (int i = 0; i < size; i++) {
			cursor.moveToPosition(i);
			if (dataStruct == null || dataStruct.size() == 0) {
				DataAdd(cursor);
			} else {
				int B_ID = cursor.getInt(11);
				boolean NewFolder = true;
				for (int j = 0; j < dataStruct.size(); j++) {
					Tab2GridData m_Tab2GridData_old = (Tab2GridData) dataStruct
							.get(j);
					int B_ID_OLD = m_Tab2GridData_old.bucket_id;
					if (B_ID_OLD == B_ID) {
						NewFolder = false;
					}

				}
				if (NewFolder) {
					DataAdd(cursor);
				}
			}

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

}
