package com.joy.Tools;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.joy.R;
import com.joy.dlna.dlna_main;

public class JoyFileExplorer extends ListActivity {

	public static final String[] EXTENSIONS = new String[] { ".mp4", ".jpg",
			".3gp" };
	private static final String TAG = "FFMpegFileExplorer";

	private String mRoot = "/sdcard";
	// private String mRoot = "/dev/sample";
	private TextView mTextViewLocation;
	private File[] mFiles;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.joy_file_explorer);
		mTextViewLocation = (TextView) findViewById(R.id.textview_path);
		getDirectory(mRoot);
	}

	protected static boolean checkExtension(File file) {
		for (int i = 0; i < EXTENSIONS.length; i++) {
			if (file.getName().indexOf(EXTENSIONS[i]) > 0) {
				return true;
			}
		}
		return false;
	}

	private void sortFilesByDirectory(File[] files) {
		Arrays.sort(files, new Comparator<File>() {

			public int compare(File f1, File f2) {
				return Long.valueOf(f1.length()).compareTo(f2.length());
			}

		});
	}

	private void getDirectory(String dirPath) {
		try {
			mTextViewLocation.setText("Location: " + dirPath);

			File f = new File(dirPath);
			File[] temp = f.listFiles();

			sortFilesByDirectory(temp);

			File[] files = null;
			if (!dirPath.equals(mRoot)) {
				files = new File[temp.length + 1];
				System.arraycopy(temp, 0, files, 1, temp.length);
				files[0] = new File(f.getParent());
			} else {
				files = temp;
			}

			mFiles = files;
			setListAdapter(new FileExplorerAdapter(this, files,
					temp.length == files.length));
		} catch (Exception ex) {
			JoyMessageBox.show(this, "Error", ex.getMessage());
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		File file = mFiles[position];

		if (file.isDirectory()) {
			if (file.canRead())
				getDirectory(file.getAbsolutePath());
			else {
				JoyMessageBox.show(this, "Error", "[" + file.getName()
						+ "] folder can't be read!");
			}
		} else {
			if (!checkExtension(file)) {
				StringBuilder strBuilder = new StringBuilder();
				for (int i = 0; i < EXTENSIONS.length; i++)
					strBuilder.append(EXTENSIONS[i] + " ");
				JoyMessageBox.show(
						this,
						"Error",
						"File must have this extensions: "
								+ strBuilder.toString());
				return;
			}

			startPlayer(file.getAbsolutePath());
		}
	}

	private void startPlayer(String filePath) {
		/*
		 * Intent i = new Intent(this, FFMpegPlayerActivity.class);
		 * i.putExtra(getResources().getString(R.string.input_file), filePath);
		 * startActivity(i);
		 */
		if (filePath.length() <= 1) {
			filePath = "/sdcard/test.jpg";
		}
		Intent intent = new Intent(this, dlna_main.class);
		intent.putExtra("IsLocal", true);
		intent.putExtra("meta", filePath);
		intent.putExtra("url", filePath);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			Log.e(TAG, "OnDLNA fail", ex);
		}
	}

}
