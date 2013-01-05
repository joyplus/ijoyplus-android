package com.joyhome;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.androidquery.AQuery;
import com.joyhome.Adapters.Tab1GridData;
import com.joyhome.Adapters.Tab1_PhotoAdapter;
import com.umeng.analytics.MobclickAgent;


public class Tab1_Photo extends Activity implements
		AdapterView.OnItemClickListener {
	private String TAG = "Tab1_Photo";
	private App app;
	private AQuery aq;

	private int Fromepage;
	private ArrayList<Tab1GridData> dataStruct = null;
	private GridView gridView;
	private Tab1_PhotoAdapter PhotoAdapter;
	private int BUCKET_ID = 0;
	private static final String EXTERNAL_MEDIA = "external";

	private  Uri mBaseUri = Files.getContentUri(EXTERNAL_MEDIA);
	private  Uri mWatchUriImage = Images.Media.EXTERNAL_CONTENT_URI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo);

		app = (App) getApplication();
		aq = new AQuery(this);
		Intent intent = getIntent();

		gridView = (GridView) findViewById(R.id.gridView1);
		BUCKET_ID = intent.getIntExtra("BUCKET_ID", 0);
		if (BUCKET_ID != 0)
			GetPhotoData();

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
		Tab1GridData m_Tab1GridData = (Tab1GridData) gridView
				.getItemAtPosition(i);
		if (m_Tab1GridData != null) {

		
			Intent intent = new Intent(this, PhotoSlideShow.class);
//			Bundle bundle = new Bundle();
//			bundle.putParcelable("IMAGEARRAY", dataStruct);
//			
			intent.putExtra("CURRENT", i);
			intent.putParcelableArrayListExtra("IMAGEARRAY", dataStruct);
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
		dataStruct = new ArrayList<Tab1GridData>();

		LoadImagesFromSDCard();
		PhotoAdapter = new Tab1_PhotoAdapter(this, dataStruct);

		gridView.setAdapter(PhotoAdapter);
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
				"bucket_id=?", // Return all rows
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

		if(m_Tab1GridData._size != null && Integer.parseInt(m_Tab1GridData._size) >0 ){
			dataStruct.add(m_Tab1GridData);
		}
		
	}

}
