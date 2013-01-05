package com.joyhome.Adapters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.androidquery.AQuery;
import com.joyhome.BitmapUtils;
import com.joyhome.R;

/*
 * 分类导航详情的数据适配器
 * */
public class Tab2GridAdapter extends ArrayAdapter {

	// listview的数据
	private Map viewMap;
	AQuery aq;

	// 构造函数
	public Tab2GridAdapter(Activity activity, List list) {
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
					R.layout.tab2_detail_grid, null);
			aq = new AQuery(view1);

			// 获取当前数据项的数据
			Tab2GridData m_Tab2GridData = (Tab2GridData) getItem(i);

			aq.id(R.id.txt_video_caption).text(
					m_Tab2GridData.bucket_display_name);

			Bitmap bm = onDecodeOriginal(m_Tab2GridData._data);
			if (bm != null) {
				aq.id(R.id.video_preview_img).image(bm);
			}

			Integer integer1 = Integer.valueOf(i);
			Object obj = viewMap.put(integer1, view1);
		}
		return view1;
	}

	public Bitmap onDecodeOriginal(String mLocalFilePath) {
		Bitmap bitmap = BitmapUtils.createVideoThumbnail(mLocalFilePath);
		if (bitmap == null)
			return null;
		return bitmap;
	}
}
