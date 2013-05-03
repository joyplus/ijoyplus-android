package com.joyplus.Adapters;

import java.util.ArrayList;

import com.joyplus.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GalleryAdapter extends BaseAdapter {
	private ArrayList<Integer> sourceImage;
	private ArrayList<String> sourceText;
	private Context mContext;
	public GalleryAdapter(Context c) {
		mContext = c;
	}
	
	public GalleryAdapter(Context c,ArrayList<Integer> list,ArrayList<String> text)
	{
		mContext = c;
		this.sourceImage = list;
		this.sourceText = text;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return sourceImage.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.gallery_item, null);
			holder.pic = (ImageView) convertView.findViewById(R.id.image);
			holder.text = (TextView) convertView.findViewById(R.id.text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.pic.setImageResource(sourceImage.get(position));
		holder.text.setText(sourceText.get(position));
		return convertView;
	}

	class ViewHolder {
		private ImageView pic;
		private TextView text;
	}
}
