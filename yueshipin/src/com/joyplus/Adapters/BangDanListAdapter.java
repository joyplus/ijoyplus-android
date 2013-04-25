package com.joyplus.Adapters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.joyplus.R;

/*
 * 分类导航详情的数据适配器
 * */
public class BangDanListAdapter extends ArrayAdapter {

	// listview的数据
	private Map viewMap;
	AQuery aq;
	private LayoutInflater mInflater;

	// 构造函数
	public BangDanListAdapter(Context context, List list) {
		super(context, 0, list);
		mInflater = LayoutInflater.from(context);
		viewMap = new HashMap();
	}

	// 获取显示当前的view
	@Override
	public View getView(int i, View view, ViewGroup viewgroup) {
		// Integer integer = Integer.valueOf(i);
		// View view1 = (View) viewMap.get(integer);
		ViewHolder holder = null;
		if (view == null) {
			// 加载布局文件
			view = mInflater.inflate(R.layout.detail_bangdan_list, null);
			holder = new ViewHolder();
			holder.name = (TextView) view.findViewById(R.id.txt_video_caption);
			holder.textview3 = (TextView) view.findViewById(R.id.TextView03);
			holder.TextView5 = (TextView) view.findViewById(R.id.TextView05);
			holder.button1 = (Button) view.findViewById(R.id.button1);
			holder.button2 = (Button) view.findViewById(R.id.button2);
			holder.TextViewScore = (TextView) view
					.findViewById(R.id.TextViewScore);
			holder.txt_1 = (TextView) view.findViewById(R.id.txt_1);
			holder.TextView02 = (TextView) view.findViewById(R.id.TextView02);
			holder.video_preview_img = (ImageView) view
					.findViewById(R.id.video_preview_img);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		aq = new AQuery(view);
		BangDanListData m_BangDanListData = (BangDanListData) getItem(i);
		holder.name.setText(m_BangDanListData.Pic_name);
		holder.textview3.setText(m_BangDanListData.Text_Zhuyan);
		// holder.textview2.setText(m_BangDanListData.Text_Year);
		holder.TextView5.setText(m_BangDanListData.Text_Area);
		holder.button1.setText(m_BangDanListData.Text_Ding + "人顶");
		holder.button2.setText(m_BangDanListData.Text_Favority + "人收藏");
		holder.TextViewScore.setText(m_BangDanListData.Text_Score + "分");
		if (Integer.valueOf(m_BangDanListData.prod_type) == 3) {
			holder.txt_1.setText("主持/嘉宾：");
			holder.TextView02.setText("地    区：");
		}
		aq.id(R.id.video_preview_img).image(m_BangDanListData.Pic_url, true,
				true, 0, R.drawable.default_image_bg);
		Integer integer1 = Integer.valueOf(i);
		Object obj = viewMap.put(integer1, view);

		return view;
	}

	class ViewHolder {
		public TextView name = null;
		public TextView textview3 = null;
		public TextView TextView5 = null;
		public TextView TextViewScore = null;
		public Button button1;
		public Button button2;
		public TextView txt_1 = null;
		public TextView TextView02 = null;
		public ImageView video_preview_img = null;

	}
}
