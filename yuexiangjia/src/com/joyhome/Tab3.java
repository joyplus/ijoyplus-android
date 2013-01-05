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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidquery.AQuery;
import com.joyhome.Adapters.Tab3ListData;
import com.joyhome.Adapters.Tab3ListAdapter;
import com.joyhome.Adapters.Tab3ListData;
import com.umeng.analytics.MobclickAgent;

public class Tab3 extends Activity implements
		android.widget.AdapterView.OnItemClickListener {
	private String TAG = "Tab3";
	private App app;
	private AQuery aq;

	private int Fromepage;
	private ArrayList dataStruct;
	private ListView ItemsListView;
	private Tab3ListAdapter Tab3Adapter;

	private static final String EXTERNAL_MEDIA = "external";
	private static final Uri mBaseUri = Files.getContentUri(EXTERNAL_MEDIA);
	private static final Uri mWatchUriAudio = Audio.Media.EXTERNAL_CONTENT_URI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab3);
		app = (App) getApplication();
		aq = new AQuery(this);

		ItemsListView = (ListView) findViewById(R.id.listView1);
		ItemsListView.setOnItemClickListener(this);

		GetMusicData();

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
		Tab3ListData m_Tab3ListData = (Tab3ListData) ItemsListView
				.getItemAtPosition(i);
		if (m_Tab3ListData != null) {

			Intent intent = new Intent(this, MusicPlay.class);
			intent.putExtra("CURRENT", i);
			intent.putParcelableArrayListExtra("IMAGEARRAY", dataStruct);
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException ex) {
				Log.e(TAG, "Call Tab1_Photo failed", ex);
			}
		} else {
			app.MyToast(this, "m_Tab3ListData is empty.");
		}
	}

	private void GetMusicData() {
		dataStruct = new ArrayList();
		// Tab3Adapter = new Tab3ListAdapter(this, dataStruct);
		//
		// ItemsListView.setAdapter(Tab3Adapter);
		// ItemsListView.setOnItemClickListener(this);

		LoadMusicFromSDCard();
		NotifyDataAnalysisFinished();

	}

	public void NotifyDataAnalysisFinished() {
		if (dataStruct != null && ItemsListView != null) {
			Tab3ListAdapter listviewdetailadapter = getAdapter();
			ItemsListView.setAdapter(listviewdetailadapter);
		} else {
			app.MyToast(this, "ItemsListView empty.");
		}
	}

	private Tab3ListAdapter getAdapter() {
		if (Tab3Adapter == null) {
			ArrayList arraylist = dataStruct;
			Tab3ListAdapter listviewdetailadapter = new Tab3ListAdapter(this,
					arraylist);
			Tab3Adapter = listviewdetailadapter;
		} else {
			ArrayList arraylist1 = dataStruct;
			Tab3ListAdapter listviewdetailadapter1 = new Tab3ListAdapter(this,
					arraylist1);
			Tab3Adapter = listviewdetailadapter1;
		}
		return Tab3Adapter;
	}

	/**
	 * Async task for loading the images from the SD card.
	 */
	private void LoadMusicFromSDCard() {

		setProgressBarIndeterminateVisibility(true);

		String[] projection = { "_id", "_data", "_display_name", "_size",
				"mime_type", "date_added", "is_drm", "date_modified", "title",
				"title_key", "duration", "artist_id", "composer", "album_id",
				"track", "year", "is_ringtone", "is_music", "is_alarm",
				"is_notification", "is_podcast", "bookmark", "album_artist" };

		// Create the cursor pointing to the SDCard
		Cursor cursor = managedQuery(mWatchUriAudio, projection, // Which
																	// columns
																	// to
																	// return
				null, // Return all rows
				null, null);

		int size = cursor.getCount();
		// If size is 0, there are no images on the SD Card.
		if (size == 0) {
			// No Images available, post some message to the user
		}
		int imageID = 0;
		for (int i = 0; i < size; i++) {
			cursor.moveToPosition(i);
			if (dataStruct != null) {
				DataAdd(cursor);
			}

		}
		// cursor.close();
	}

	private void DataAdd(Cursor cursor) {
		Tab3ListData m_Tab3ListData = new Tab3ListData();
		m_Tab3ListData._id = cursor.getInt(0);
		m_Tab3ListData._data = cursor.getString(1);
		m_Tab3ListData._display_name = cursor.getString(2);
		m_Tab3ListData._size = cursor.getInt(3);
		m_Tab3ListData.mime_type = cursor.getString(4);
		m_Tab3ListData.date_added = cursor.getString(5);
		m_Tab3ListData.is_drm = cursor.getInt(6);
		m_Tab3ListData.date_modified = cursor.getString(7);
		m_Tab3ListData.title = cursor.getString(8);
		m_Tab3ListData.title_key = cursor.getString(9);
		m_Tab3ListData.duration = cursor.getInt(10);
		m_Tab3ListData.artist_id = cursor.getInt(11);
		m_Tab3ListData.composer = cursor.getString(12);
		m_Tab3ListData.album_id = cursor.getInt(13);
		m_Tab3ListData.track = cursor.getString(14);
		m_Tab3ListData.year = cursor.getString(15);
		m_Tab3ListData.is_ringtone = cursor.getInt(16);
		m_Tab3ListData.is_music = cursor.getInt(17);
		m_Tab3ListData.is_alarm = cursor.getInt(18);
		m_Tab3ListData.is_notification = cursor.getInt(19);
		m_Tab3ListData.is_podcast = cursor.getInt(20);
		m_Tab3ListData.bookmark = cursor.getString(21);
		m_Tab3ListData.album_artist = cursor.getString(22);

		dataStruct.add(m_Tab3ListData);
	}

}
