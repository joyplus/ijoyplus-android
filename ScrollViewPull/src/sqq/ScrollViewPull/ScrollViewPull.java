package sqq.ScrollViewPull;

import sqq.ScrollViewPull.widget.TryRefreshableView;
import sqq.ScrollViewPull.widget.TryRefreshableView.RefreshListener;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
/**
 * 
 * @author sqq
 *
 */
public class ScrollViewPull extends Activity {
	private ScrollView sv;
	private String s;
	private TryRefreshableView rv;
	private String[] mStrings = { "aaaaaaaaaaaaaaaaaa", "bb" };
	private TextView msvTextView;
	String msg = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pull_to_refresh_tryscroll);
		sv = (ScrollView) findViewById(R.id.trymySv);
		rv = (TryRefreshableView) findViewById(R.id.trymyRV);
		rv.mfooterView = (View) findViewById(R.id.tryrefresh_footer);
		rv.sv = sv;
		
		//隐藏mfooterView
		rv.mfooterViewText = (TextView) findViewById(R.id.tryrefresh_footer_text);
		
		
		
		s = "Android是一种以Linux为基础的开放源码操作系统，主要使用于便携设备。目前尚未有统一中文名称，中国大陆地区较多人使用安卓（非官方）或安致（官方）。";

		for (int t = 0; t < 20; t++) {
			msg += s;
		}
		msvTextView = (TextView) findViewById(R.id.sv_text);
		msvTextView.setText(msg);
		Log.i("other","msvTextView.getHeight()"+msvTextView.getHeight());
		
		msvTextView.setTextSize(23);


		
		//监听是否加载刷新
		rv.setRefreshListener(new RefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				if (rv.mRefreshState == 4) {
					new GetHeaderDataTask().execute();
				} else if (rv.mfooterRefreshState == 4) {
					new GetFooterDataTask().execute();
				}

			}
		});
		

	}

	private class GetHeaderDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {

			}
			return mStrings;
		}

		@Override
		protected void onPostExecute(String[] result) {

			msg = mStrings[0] + msg;
			msvTextView.setText(msg);
			msvTextView.setTextSize(23);
			rv.finishRefresh();

			super.onPostExecute(result);
		}

	}

	private class GetFooterDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {

			}
			return mStrings;
		}

		@Override
		protected void onPostExecute(String[] result) {

			msg = msg + mStrings[0];
			msvTextView.setText(msg);
			msvTextView.setTextSize(23);
			rv.finishRefresh();

			super.onPostExecute(result);
		}

	}

}