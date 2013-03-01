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
public class SearchListAdapter extends ArrayAdapter {

	// listview的数据
	private Map viewMap;
	private AQuery aq;
	private boolean isAddButton = false;

	// 构造函数
	public SearchListAdapter(Activity activity, List list, boolean isAddButton) {
		super(activity, 0, list);
		this.isAddButton = isAddButton;
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
					R.layout.search_detail_list, null);
			aq = new AQuery(view1);

			// 获取当前数据项的数据
			SearchListData m_SearchListData = (SearchListData) getItem(i);

			aq.id(R.id.txt_video_caption).text(m_SearchListData.Pic_name);
			aq.id(R.id.TextViewScore).text(m_SearchListData.Text_Score + "分");
			aq.id(R.id.TextView03).text(m_SearchListData.Text_Zhuyan);
			aq.id(R.id.TextView04).text(m_SearchListData.Text_Area);

			// 1：电影，2：电视剧，3：综艺，4：视频
			switch (Integer.valueOf(m_SearchListData.prod_type)) {
			case 1:
				aq.id(R.id.TextView05).text("电影");
				break;
			case 2:
				aq.id(R.id.TextView05).text("电视剧");
				break;
			case 3:
				aq.id(R.id.TextView05).text("综艺");
				aq.id(R.id.txt_1).text("主持/嘉宾：");
				aq.id(R.id.TextView01).text("地    区：");
				aq.id(R.id.TextView02).text("分    类：");
				break;
			case 4:
				aq.id(R.id.TextView05).text("视频");
				break;
			}
			if (m_SearchListData.Is_Ready_Have)
				aq.id(R.id.button1).getImageView()
						.setImageResource(R.drawable.search_addon_icon);
			aq.id(R.id.video_preview_img).image(m_SearchListData.Pic_url, true,
					true, 0, R.drawable.default_image_bg);
			if (!isAddButton) {
				aq.id(R.id.button1).gone();
			}
			// else {
			// ImageButton click = (ImageButton)
			// view1.findViewById(R.id.button1);
			// click.setTag(i + "");
			// }

			Integer integer1 = Integer.valueOf(i);
			Object obj = viewMap.put(integer1, view1);
		}

		return view1;
	}
}
