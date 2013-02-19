/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.joyplus.Video;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyplus.App;
import com.joyplus.Constant;
import com.joyplus.R;
import com.joyplus.Service.Return.ReturnProgramView;

/**
 * This activity plays a video from a specified URI.
 */
public class MovieActivity extends Activity {
	@SuppressWarnings("unused")
	private static final String TAG = "MovieActivity";

	private MoviePlayer mPlayer;
	private boolean mFinishOnCompletion;
	private Uri mUri;
	private App app;
	private String prod_url = null;
	private String prod_id = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (App) getApplication();
		// requestWindowFeature(Window.FEATURE_ACTION_BAR);
		// requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

		setContentView(R.layout.movie_view);
		View rootView = findViewById(R.id.root);
		Intent intent = getIntent();
		prod_id = intent.getStringExtra("prod_id");
		prod_url = intent.getStringExtra("prod_url");
		// initializeActionBar(intent);
		mFinishOnCompletion = intent.getBooleanExtra(
				MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
		mPlayer = new MoviePlayer(rootView, this, Uri.parse(prod_url),
				savedInstanceState, !mFinishOnCompletion) {
			@Override
			public void onCompletion() {
				if (mFinishOnCompletion) {
					File cachefile = new File(Constant.PATH_XML + "TV_data"
							+ prod_id + ".json");
					ObjectMapper mapper = new ObjectMapper();
					try {
						ReturnProgramView m_ReturnProgramView = mapper
								.readValue(cachefile, ReturnProgramView.class);
						int index = ++m_ReturnProgramView.tv.current_play;

						if (m_ReturnProgramView.tv.episodes[index].down_urls != null
								&& m_ReturnProgramView.tv.episodes[index].down_urls[0].urls.length > 0
								&& m_ReturnProgramView.tv.episodes[index].down_urls[0].urls[0].url != null
								&& app.IfSupportFormat(m_ReturnProgramView.tv.episodes[index].down_urls[0].urls[0].url)) {
							//
							String datainfo = m_ReturnProgramView.tv.id
									+ "|"
									+ "PROD_SOURCE"
									+ "|"
									+ URLEncoder
											.encode(m_ReturnProgramView.tv.episodes[index].down_urls[0].urls[0].url)
									+ "|" + m_ReturnProgramView.tv.name + "|"
									+ "��" + Integer.toString(index + 1) + "��"
									+ "|" + "null" + "|2";
							app.SavePlayData(m_ReturnProgramView.tv.id,
									datainfo);
							mapper.writeValue(cachefile, m_ReturnProgramView);

							String m_uri = m_ReturnProgramView.tv.episodes[index].down_urls[0].urls[0].url;
							CallVideoPlay(m_uri);

						}

					} catch (JsonParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonMappingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					finish();
				}
			}
		};
		if (intent.hasExtra(MediaStore.EXTRA_SCREEN_ORIENTATION)) {
			int orientation = intent.getIntExtra(
					MediaStore.EXTRA_SCREEN_ORIENTATION,
					ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			if (orientation != getRequestedOrientation()) {
				setRequestedOrientation(orientation);
			}
		}
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		winParams.buttonBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF;
		winParams.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		win.setAttributes(winParams);
	}

	private void initializeActionBar(Intent intent) {
		// ActionBar actionBar = getActionBar();
		// actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP,
		// ActionBar.DISPLAY_HOME_AS_UP);
		String title = intent.getStringExtra(Intent.EXTRA_TITLE);
		mUri = intent.getData();
		if (title == null) {
			Cursor cursor = null;
			try {
				cursor = getContentResolver().query(mUri,
						new String[] { MediaColumns.TITLE }, null, null, null);
				if (cursor != null && cursor.moveToNext()) {
					title = cursor.getString(0);
				}
			} catch (Throwable t) {
				Log.w(TAG, "cannot get title from: " + intent.getDataString(),
						t);
			} finally {
				if (cursor != null)
					cursor.close();
			}
		}
		// if (title != null)
		// actionBar.setTitle(title);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		/*
		 * getMenuInflater().inflate(R.menu.movie, menu); ShareActionProvider
		 * provider = GalleryActionBar .initializeShareActionProvider(menu);
		 * 
		 * if (provider != null) { Intent intent = new
		 * Intent(Intent.ACTION_SEND); intent.setType("video/*");
		 * intent.putExtra(Intent.EXTRA_STREAM, mUri);
		 * provider.setShareIntent(intent); }
		 */
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return false;
	}

	@Override
	public void onStart() {
		((AudioManager) getSystemService(AUDIO_SERVICE)).requestAudioFocus(
				null, AudioManager.STREAM_MUSIC,
				AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
		super.onStart();
	}

	@Override
	protected void onStop() {
		((AudioManager) getSystemService(AUDIO_SERVICE))
				.abandonAudioFocus(null);
		super.onStop();
	}

	@Override
	public void onPause() {
		mPlayer.onPause();
		super.onPause();
	}

	@Override
	public void onResume() {
		mPlayer.onResume();
		super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mPlayer.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroy() {
		mPlayer.onDestroy();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return mPlayer.onKeyDown(keyCode, event)
				|| super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return mPlayer.onKeyUp(keyCode, event) || super.onKeyUp(keyCode, event);
	}

	public void CallVideoPlay(String m_uri) {

		Intent intent = new Intent(this, MovieActivity.class);
		intent.putExtra("prod_url", m_uri);
		intent.putExtra("prod_id", prod_id);

		try {
			startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			Log.e(TAG, "video failed", ex);
		}

	}
}
