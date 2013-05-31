package com.joyplus.Video;

import io.vov.vitamio.Vitamio;

import java.lang.ref.WeakReference;

import com.joyplus.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;

public class InitActivity extends Activity {
	public static final String FROM_ME = "fromVitamioInitActivity";
	public static final String EXTRA_MSG = "EXTRA_MSG";
	public static final String EXTRA_FILE = "EXTRA_FILE";
	private ProgressDialog mPD;
	private UIHandler uiHandler;
	private long WAIT_TIMES = 3000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		uiHandler = new UIHandler(this);

		new AsyncTask<Object, Object, Boolean>() {
			@Override
			protected void onPreExecute() {
				mPD = new ProgressDialog(InitActivity.this);
				mPD.setCancelable(false);
				mPD.setMessage(getString(R.string.init_decoders));
				mPD.show();
			}

			@Override
			protected Boolean doInBackground(Object... params) {
				return Vitamio.initialize(InitActivity.this);
			}

			@Override
			protected void onPostExecute(Boolean inited) {
				if (inited) {
					uiHandler.sendEmptyMessage(0);
				}
			}

		}.execute();
	}

	private static class UIHandler extends Handler {
		private WeakReference<Context> mContext;

		public UIHandler(Context c) {
			mContext = new WeakReference<Context>(c);
		}

		public void handleMessage(Message msg) {
			InitActivity ctx = (InitActivity) mContext.get();
			switch (msg.what) {
			case 0:
				Intent src = ctx.getIntent();
				Intent i = new Intent();
				i.setClassName(src.getStringExtra("package"),
						src.getStringExtra("className"));
				i.setData(src.getData());
				i.putExtras(src);
				i.putExtra(FROM_ME, true);
				ctx.startActivity(i);
				ctx.finish();
				break;
			}
		}
	};
}
