package com.ijoyplus.Adapters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.androidquery.AQuery;
import com.ijoyplus.R;

/*
 * 分类导航详情的数据适配器
 * */
public class Tab1ListAdapter extends ArrayAdapter {

	// listview的数据
	private Map viewMap;
	AQuery aq;

	// 构造函数
	public Tab1ListAdapter(Activity activity, List list) {
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
					R.layout.tab1_detail_list, null);
			aq = new AQuery(view1);

			// 获取当前数据项的数据
			Tab1ListData m_Tab1ListData = (Tab1ListData) getItem(i);

			aq.id(R.id.txt_video_caption).text(m_Tab1ListData.Pic_name);
			aq.id(R.id.txt_1).text(m_Tab1ListData.Pic_list1);
			aq.id(R.id.TextView01).text(m_Tab1ListData.Pic_list2);
			aq.id(R.id.TextView02).text(m_Tab1ListData.Pic_list3);
			aq.id(R.id.TextView03).text(m_Tab1ListData.Pic_list4);
			aq.id(R.id.TextView04).text(m_Tab1ListData.Pic_list5);
			// aq.id(R.id.TextView05).text(m_Tab1ListData.Pic_list6);

			// 1：电影，2：电视剧，3：综艺，4：视频
			switch (Integer.valueOf(m_Tab1ListData.right)) {
			case 1:
				aq.id(R.id.thisnext).image(R.drawable.tab1_movieflag);
				break;
			case 2:
				aq.id(R.id.thisnext).image(R.drawable.tab1_seriesflag);
				break;
			case 3:
				aq.id(R.id.thisnext).image(R.drawable.tab1_movieflag);
				break;
			case 4:
				aq.id(R.id.thisnext).image(R.drawable.tab1_movieflag);
				break;
			}

			aq.id(R.id.video_preview_img)
					.image(m_Tab1ListData.Pic_url, true, true, 0,
							R.drawable.default_image_bg);

			Integer integer1 = Integer.valueOf(i);
			Object obj = viewMap.put(integer1, view1);
		}
		return view1;
	}
}
