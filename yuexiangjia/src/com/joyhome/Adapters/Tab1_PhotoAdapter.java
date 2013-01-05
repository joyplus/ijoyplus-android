package com.joyhome.Adapters;

import java.io.File;
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
public class Tab1_PhotoAdapter extends ArrayAdapter {

	// listview的数据
	private Map viewMap;
	AQuery aq;

	// 构造函数
	public Tab1_PhotoAdapter(Activity activity, List list) {
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
					R.layout.photo_detail_grid, null);
			aq = new AQuery(view1);

			// 获取当前数据项的数据
			Tab1GridData m_Tab1GridData = (Tab1GridData) getItem(i);

			aq.id(R.id.txt_video_caption).text(m_Tab1GridData.title);

			File file = new File(m_Tab1GridData._data);
			if (file.exists()) {
				aq.id(R.id.video_preview_img).image(file, 148);
			}

			Integer integer1 = Integer.valueOf(i);
			Object obj = viewMap.put(integer1, view1);
		}
		return view1;
	}
}
