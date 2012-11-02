package com.joy;

import java.io.IOException;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MediaActivity extends Activity implements
        OnBufferingUpdateListener, OnCompletionListener,
        MediaPlayer.OnPreparedListener, SurfaceHolder.Callback {

    private MediaPlayer mediaPlayer;

    private SurfaceView surfaceView;

    private SurfaceHolder surfaceHolder;

    private int videoWidth;

    private int videoHeight;
    ProgressDialog progressBar;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surface);
        this.surfaceView = (SurfaceView) this.findViewById(R.id.surface);
        progressBar = ProgressDialog.show(this, getResources().getString(R.string.shaohou), getResources().getString(R.string.pull_to_refresh_footer_refreshing_label));
        this.surfaceHolder = this.surfaceView.getHolder();
        this.surfaceHolder.addCallback(this);
        this.surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        Log.v("mplayer", ">>>create ok.");
    }

    private void playVideo() throws IllegalArgumentException,
            IllegalStateException, IOException {
        this.mediaPlayer = new MediaPlayer();
        this.mediaPlayer
                .setDataSource("http://f3.3g.56.com/15/15/JGfMspPbHtzoqpzseFTPGUsKCEqMXFTW_smooth.3gp"/*getIntent().getStringExtra("url")*/);
        this.mediaPlayer.setDisplay(this.surfaceHolder);
        this.mediaPlayer.prepare();
        this.mediaPlayer.setOnBufferingUpdateListener(this);
        this.mediaPlayer.setOnPreparedListener(this);
        this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        Log.v("mplayer", ">>>play video");
    }

    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        // TODO Auto-generated method stub

    }

    public void onCompletion(MediaPlayer mp) {
        // TODO Auto-generated method stub

    }

    public void onPrepared(MediaPlayer mp) {
        this.videoWidth = this.mediaPlayer.getVideoWidth();
        this.videoHeight = this.mediaPlayer.getVideoHeight();

        if (this.videoHeight != 0 && this.videoWidth != 0) {
            this.surfaceHolder.setFixedSize(this.videoWidth, this.videoHeight);
            progressBar.dismiss();
            this.mediaPlayer.start();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        Log.v("mplayer", ">>>surface changed");
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            this.playVideo();
        } catch (Exception e) {
            Log.e("mplayer", ">>>error", e);
            mediaPlayer.reset();
        }
        Log.v("mplayer", ">>>surface created");
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v("mplayer", ">>>surface destroyed");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mediaPlayer != null) {
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
    }
    public void onResume() { 
		super.onResume();
		MobclickAgent.onResume(this); 
	} 
	public void onPause() { 
		super.onPause(); 
		MobclickAgent.onPause(this); 
	}
}