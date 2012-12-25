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
public class BangDanListAdapter extends ArrayAdapter {

	// listview的数据
	private Map viewMap;
	AQuery aq;

	// 构造函数
	public BangDanListAdapter(Activity activity, List list) {
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
					R.layout.detail_bangdan_list, null);
			aq = new AQuery(view1);

			// 获取当前数据项的数据
			BangDanListData m_BangDanListData = (BangDanListData) getItem(i);

			aq.id(R.id.txt_video_caption).text(m_BangDanListData.Pic_name);
			aq.id(R.id.TextView03).text(m_BangDanListData.Text_Zhuyan);
			aq.id(R.id.TextView04).text(m_BangDanListData.Text_Year);
			aq.id(R.id.TextView05).text(m_BangDanListData.Text_Area);
			aq.id(R.id.button1).text(m_BangDanListData.Text_Ding + "人顶");
			aq.id(R.id.button2).text(m_BangDanListData.Text_Favority + "人收藏");
			aq.id(R.id.TextViewScore).text(m_BangDanListData.Text_Score+ "分");

			if (Integer.valueOf(m_BangDanListData.prod_type) == 3) {
				
				aq.id(R.id.txt_1).text("主持/嘉宾：");
				aq.id(R.id.TextView02).text("地    区：");

			}
			aq.id(R.id.video_preview_img)
					.image(m_BangDanListData.Pic_url, true, true, 0,
							R.drawable.default_image_bg);

			Integer integer1 = Integer.valueOf(i);
			Object obj = viewMap.put(integer1, view1);
		}
		return view1;
	}
}
