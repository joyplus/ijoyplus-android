package com.joyhome;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.androidquery.AQuery;
import com.joyhome.Adapters.Tab1GridAdapter;
import com.joyhome.Adapters.Tab1GridData;
import com.umeng.analytics.MobclickAgent;

public class Tab1 extends Activity implements AdapterView.OnItemClickListener {
	private String TAG = "Tab1";
	private App app;
	private AQuery aq;

	private int Fromepage;
	private ArrayList dataStruct;
	private GridView gridView;
	private Tab1GridAdapter Tab1Adapter;

	private static final String EXTERNAL_MEDIA = "external";
	private static final Uri mBaseUri = Files.getContentUri(EXTERNAL_MEDIA);
	private static final Uri mWatchUriImage = Images.Media.EXTERNAL_CONTENT_URI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab1);
		app = (App) getApplication();
		aq = new AQuery(this);

		gridView = (GridView) findViewById(R.id.gridView1);

		GetPhotoData();

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

	// 点击事件接口函数
	public void onItemClick(AdapterView adapterview, View view, int i, long l) {
		Tab1GridData m_Tab1GridData = (Tab1GridData) gridView
				.getItemAtPosition(i);
		if (m_Tab1GridData != null) {
			// app.MyToast(this, m_Tab1GridData.Pic_name, Toast.LENGTH_LONG)
			// .show();
			Intent intent = new Intent(this, Tab1_Photo.class);
			intent.putExtra("BUCKET_ID", m_Tab1GridData.bucket_id);
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException ex) {
				Log.e(TAG, "Call Tab1_Photo failed", ex);
			}
		} else {
			app.MyToast(this, "m_Tab1GridData is empty.");
		}
	}

	private void GetPhotoData() {
		dataStruct = new ArrayList();

		LoadImagesFromSDCard();
		Tab1Adapter = new Tab1GridAdapter(this, dataStruct);

		gridView.setAdapter(Tab1Adapter);
		gridView.setOnItemClickListener(this);
	}

	/**
	 * Async task for loading the images from the SD card.
	 */
	private void LoadImagesFromSDCard() {

		setProgressBarIndeterminateVisibility(true);

		String[] projection = { "_id", "_data", "_size", "_display_name",
				"mime_type", "title", "date_added", "date_modified",
				"bucket_id", "bucket_display_name", "width", "height" };
		// Create the cursor pointing to the SDCard
		Cursor cursor = managedQuery(mWatchUriImage, projection, // Which
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
			if (dataStruct == null || dataStruct.size() == 0) {
				DataAdd(cursor);
			} else {
				int B_ID = cursor.getInt(8);
				boolean NewFolder = true;
				for (int j = 0; j < dataStruct.size(); j++) {
					Tab1GridData m_Tab1GridData_old = (Tab1GridData) dataStruct
							.get(j);
					int B_ID_OLD = m_Tab1GridData_old.bucket_id;
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
		Tab1GridData m_Tab1GridData = new Tab1GridData();
		m_Tab1GridData._id = cursor.getInt(0);
		m_Tab1GridData._data = cursor.getString(1);
		m_Tab1GridData._size = cursor.getString(2);
		m_Tab1GridData._display_name = cursor.getString(3);
		m_Tab1GridData.mime_type = cursor.getString(4);
		m_Tab1GridData.title = cursor.getString(5);
		m_Tab1GridData.date_added = cursor.getString(6);
		m_Tab1GridData.date_modified = cursor.getString(7);
		m_Tab1GridData.bucket_id = cursor.getInt(8);
		m_Tab1GridData.bucket_display_name = cursor.getString(9);
		m_Tab1GridData.width = cursor.getString(10);
		m_Tab1GridData.height = cursor.getString(11);

		dataStruct.add(m_Tab1GridData);
	}

	private String GetFirstDir(String path) {
		if (path != null && path.length() > 0) {
			String PATH = Environment.getExternalStorageDirectory() + "/";
			String PTAH1 = path.trim();
			PTAH1 = PTAH1.replace(PATH, "");

			// String[] PTAH2 = PTAH1.split("/");
			if (PTAH1 != null && PTAH1.length() > 0) {
				String[] PTAH2 = PTAH1.split("\\/");
				if (PTAH2.length > 1)// sd root
					return PTAH2[0];
				else
					return "sdcard";
			}
		}
		return null;
	}

}
