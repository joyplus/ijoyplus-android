package com.joyplus.Adapters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.androidquery.AQuery;
import com.joyplus.R;

/*
 * 分类导航详情的数据适配器
 * */
public class Tab3Page1ListAdapter extends ArrayAdapter {

	// listview的数据
	private Map viewMap;
	AQuery aq;

	// 构造函数
	public Tab3Page1ListAdapter(Activity activity, List list) {
		super(activity, 0, list);

		viewMap = new HashMap();
	}

	// 获取显示当前的view
	@Override
	public View getView(int i, View view, ViewGroup viewgroup) {
		Integer integer = Integer.valueOf(i);
		View view1 = (View) viewMap.get(integer);

		if (view1 == null) {
			// 加载布局文件
			view1 = ((Activity) getContext()).getLayoutInflater().inflate(
					R.layout.tab3_page1_detail_list, null);
			aq = new AQuery(view1);
			// 获取当前数据项的数据
			Tab3Page1ListData m_Tab3Page1ListData = (Tab3Page1ListData) getItem(i);

			aq.id(R.id.txt_video_caption).text(m_Tab3Page1ListData.Pro_name);
			// 1：电影，2：电视剧，3：综艺节目，4：视频
			if (m_Tab3Page1ListData.Pro_type != null) {
				if (m_Tab3Page1ListData.Pro_type.equalsIgnoreCase("1")) {
					aq.id(R.id.TextView03).text(m_Tab3Page1ListData.Pro_time);
				} else if (m_Tab3Page1ListData.Pro_type.equalsIgnoreCase("2")
						|| m_Tab3Page1ListData.Pro_type.equalsIgnoreCase("3")) {
					if (!m_Tab3Page1ListData.Pro_name1.equalsIgnoreCase("null"))
						aq.id(R.id.TextView03).text(
								m_Tab3Page1ListData.Pro_name1);
					else
						aq.id(R.id.TextView03).gone();
				}
			}
			if (m_Tab3Page1ListData.Pro_time != null
					&& m_Tab3Page1ListData.Pro_time.equalsIgnoreCase("播放完成"))
				aq.id(R.id.button1)
						.getImageView()
						.setImageResource(R.drawable.tab3_page1_replay_icon_see);

			Integer integer1 = Integer.valueOf(i);
			Object obj = viewMap.put(integer1, view1);
		}
		return view1;
	}
}
