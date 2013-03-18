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
public class Tab2Page3ListAdapter extends ArrayAdapter {

	// listview的数据
	private Map viewMap;
	AQuery aq;

	// 构造函数
	public Tab2Page3ListAdapter(Activity activity, List list) {
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
					R.layout.tab2_page3_detail_list, null);
			aq = new AQuery(view1);

			// 获取当前数据项的数据
			Tab2Page3ListData Tab2Page3ListData = (Tab2Page3ListData) getItem(i);

			aq.id(R.id.txt_video_caption).text(Tab2Page3ListData.Pic_name);
			aq.id(R.id.txt_1).text(Tab2Page3ListData.Pic_list1);

			aq.id(R.id.video_preview_img).image(Tab2Page3ListData.Pic_url,
					true, true, 0, R.drawable.tab2_page3_show_pic);

			Integer integer1 = Integer.valueOf(i);
			Object obj = viewMap.put(integer1, view1);
		}
		return view1;
	}

}
