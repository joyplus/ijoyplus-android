package com.joyhome;

import java.io.IOException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
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
import com.joyhome.Adapters.Tab3ListData;
import com.umeng.analytics.MobclickAgent;


public class MusicPlay extends Activity{
	private String TAG = "Tab1_Photo";
	private App app;
	private AQuery aq;
	private boolean isPlaying;
	
	private MediaPlayer mp = new MediaPlayer();
	
	private ArrayList<Tab3ListData> images_array = null;
	private int current_item = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_play);

		app = (App) getApplication();
		aq = new AQuery(this);
		isPlaying = false;

		Intent intent = getIntent();
		current_item = intent.getIntExtra("CURRENT", 0);
		images_array = intent.getParcelableArrayListExtra("IMAGEARRAY");
		
		playMusic();
		aq.id(R.id.textView1).text(
				images_array.get(current_item).title);
		aq.id(R.id.imageButton2).background(R.drawable.music_play_pause);

	}


	public void OnClickTopLeft(View v) {
		Intent i = new Intent(this, Setting.class);
		startActivity(i);

	}

	public void OnClickTopRight(View v) {
		

	}
	public void OnClickNext(View v) {
		nextMusic();

	}

	public void OnClickResume(View v) {
		if (mp != null) {
			if (!isPlaying) {
				mp.start();
				isPlaying = true;
				aq.id(R.id.imageButton2)
						.background(R.drawable.music_play_pause);
			} else {
				mp.pause();
				isPlaying = false;
				aq.id(R.id.imageButton2).background(R.drawable.music_play_play);
			}
		}

	}
	public void OnClickPre(View v) {
		preMusic();

	}

	private void playMusic() {
		try {
			isPlaying = true;
			aq.id(R.id.textView1).text(
					images_array.get(current_item).title);
			aq.id(R.id.imageButton2).background(R.drawable.music_play_pause);
			mp.reset();

			mp.setDataSource(images_array.get(current_item)._data);

			mp.prepare();

			mp.start();

			// Setup listener so next song starts automatically

			mp.setOnCompletionListener(new OnCompletionListener() {

				public void onCompletion(MediaPlayer arg0) {

					nextMusic();

				}

			});

		} catch (IOException e) {

			Log.v(getString(R.string.app_name), e.getMessage());

		}
	}

	private void nextMusic() {
		if (mp != null && isPlaying) 
			mp.stop();
		if (++current_item >= images_array.size()) {
			// Last song, just reset currentPosition
			current_item = 0;
		}
		playMusic();

	}
	private void preMusic() {
		if (mp != null && isPlaying) 
			mp.stop();
		if (--current_item <= 0) {
			// Last song, just reset currentPosition
			current_item = 0;
		}
		playMusic();
			// Play next song
	}

	@Override
	protected void onDestroy() {
		if (aq != null)
			aq.dismiss();
		if(mp!=null){
			mp.stop();
			mp.release();
		}
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

}
