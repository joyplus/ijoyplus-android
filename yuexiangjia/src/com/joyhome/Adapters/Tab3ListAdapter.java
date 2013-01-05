package com.joyhome.Adapters;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.androidquery.AQuery;
import com.joyhome.R;

/*
 * 分类导航详情的数据适配器
 * */
public class Tab3ListAdapter extends ArrayAdapter {

	// listview的数据
	private Map viewMap;
	private AQuery aq;
	private static final long MEGA_BYTES = 1024L * 1024;
	private static final long GIGA_BYTES = 1024L * 1024 * 1024;

	private static final long[] SIZE_LEVELS = { 0, 1 * MEGA_BYTES,
			10 * MEGA_BYTES, 100 * MEGA_BYTES, 1 * GIGA_BYTES, 2 * GIGA_BYTES,
			4 * GIGA_BYTES, };

	// 构造函数
	public Tab3ListAdapter(Activity activity, List list) {
		super(activity, 0, list);

		viewMap = new HashMap();
	}

	// 获取显示当前的view
	public View getView(int i, View view, ViewGroup viewgroup) {
		Integer integer = Integer.valueOf(i);
		View view1 = (View) viewMap.get(integer);

		if (view1 == null) {
			// 加载布局文件
			view1 = ((Activity) getContext()).getLayoutInflater().inflate(
					R.layout.tab3_detail_list, null);
			aq = new AQuery(view1);

			// 获取当前数据项的数据
			Tab3ListData m_Tab3ListData = (Tab3ListData) getItem(i);

			aq.id(R.id.txt_video_caption).text(m_Tab3ListData.title);
			aq.id(R.id.textView1).text(
					formatDuration(m_Tab3ListData.duration / 1000));
			aq.id(R.id.textView2).text(getSizeString(m_Tab3ListData._size));

			Integer integer1 = Integer.valueOf(i);
			Object obj = viewMap.put(integer1, view1);
		}
		return view1;
	}

	private String getSizeString(int size) {
		float G = (float) size / (1024 * 1024 * 1024);
		float M = (float) size / (1024 * 1024);
		float K = (float) size / 1024;
		DecimalFormat df = new DecimalFormat("00.00");
		String sizeValue;
		if (G < 1) {
			if (M < 1)
				sizeValue = df.format(K) + "M";
			else
				sizeValue = df.format(M) + "K";
		} else
			sizeValue = df.format(G) + "G";

		return sizeValue;

	}

	// Returns a (localized) string for the given duration (in seconds).
	public static String formatDuration(int duration) {
		int h = duration / 3600;
		int m = (duration - h * 3600) / 60;
		int s = duration - (h * 3600 + m * 60);
		String durationValue;
		if (h == 0) {
			durationValue = String.format("%1$02d:%2$02d", m, s);
		} else {
			durationValue = String.format("%1$d:%2$02d:%3$02d", h, m, s);
		}
		return durationValue;
	}
}
