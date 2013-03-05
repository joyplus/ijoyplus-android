package com.joyplus.download;

import java.util.List;

import com.androidquery.AQuery;
import com.joyplus.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownLoadAdapterDetail extends BaseAdapter {
private static final int MAX = 100;
	
	private LayoutInflater mInflater;
	
	private List<DownloadInfo> data;
	private Context context;
	private OnClickListener click;
	private AQuery aqtemp;
	
	public DownLoadAdapterDetail(Context context,List<DownloadInfo> data) {
		this.context=context;
		mInflater = LayoutInflater.from(context);
		this.data=data;
	}
	public void refresh(List<DownloadInfo> data) {
		this.data=data;
		this.notifyDataSetChanged();
	}
	public void setOnclick(OnClickListener click) {
		 this.click=click;
	}
	
	@Override
	public int getCount() {
		return data.size();//只是为了测试随便设置的一个数字，后面要删除掉
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			
			convertView = mInflater.inflate(R.layout.item_download_detail, null);
			holder = new ViewHolder(); 
			holder.resourceDownloadName = (TextView)convertView.findViewById(R.id.downloadfilename);
			holder.resouceDownloadState = (TextView)convertView.findViewById(R.id.download_state);
			holder.resourceImage = (ImageView)convertView.findViewById(R.id.movieImageview);
			holder.resourceDownProgress =(ProgressBar)convertView.findViewById(R.id.downloadprogress);
			holder.resourcePercentDown = (TextView)convertView.findViewById(R.id.precentDownload);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		aqtemp = new AQuery(convertView);
		if(position<data.size())
		{
			DownloadInfo info = data.get(position);
			long completesize = info.getCompeleteSize();
			long filesize = info.getFileSize();
			long percent =completesize*MAX/filesize;
			String posterurl = info.getPoster();
			if(info.getIndex().contains("_show"))
			{
				
				holder.resourceDownloadName.setText(info.getName());
				
			}
			else
			{
				holder.resourceDownloadName.setText("第"+info.getIndex()+"集");
			}
			if(info.getState().equalsIgnoreCase("wait"))
			{
				holder.resouceDownloadState.setText("等待下载");
			}
			else if(info.getState().equalsIgnoreCase("downloading"))
			{
				holder.resouceDownloadState.setText("正在下载");
			}
			else if(info.getState().equalsIgnoreCase("stop"))
			{
				holder.resouceDownloadState.setText("暂停下载");
			}
			holder.resourceDownProgress.setMax(MAX);
			holder.resourceDownProgress.setSecondaryProgress((int) (completesize*MAX/filesize));
			holder.resourcePercentDown.setText((percent)+"%");
			if(info.getCompeleteSize()==info.getFileSize())
			{
				holder.resouceDownloadState.setText("");
				holder.resourcePercentDown.setText("");
				aqtemp.id(R.id.downloadprogress).gone();
				aqtemp.id(R.id.state_layer).gone();
			}
			aqtemp.id(R.id.movieImageview).image(posterurl,true,true);
		}
		return convertView;
	}
	public OnClickListener getClick() {
		return click;
	}
	public void setClick(OnClickListener click) {
		this.click = click;
	}
	private class ViewHolder { 
		public ImageView resourceImage;
		public ProgressBar resourceDownProgress;
		public TextView resourcePercentDown;
		public TextView resouceDownloadState;
		public TextView resourceDownloadName;
	}	
}