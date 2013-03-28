package com.joyplus.playrecord;

import com.joyplus.download.Dao;

import android.content.Context;

public class playRecordManager {
	
	private Context context;
	
	public playRecordManager(Context context){
		
		this.context = context;
	}
	
	public void savePlayRecord(playRecordInfo info)
	{
		if(playRecordDao.getInstance(context).getCount()>200)
		{
			playRecordDao.getInstance(context).delete();
		}
		if(playRecordDao.getInstance(context).getProdIdInfo(info.getProd_id())!=null)
		{
			if(playRecordDao.getInstance(context).isHasInfors(info.getProd_id(),info.getProd_subname()))
			{
				playRecordDao.getInstance(context).updateOneInfo(info);
			}
			else
			{
				if(info.getLast_playtime()!=null&&info.getLast_playtime().length()>0)
				{
					playRecordDao.getInstance(context).updateOneInfo(info);
				}
				
			}
		}
		else
		{
			playRecordDao.getInstance(context).InsertOneInfo(info);
		}
	}
	public playRecordInfo getPlayRecord(String prod_id,String subname)
	{
		return playRecordDao.getInstance(context).getOneInfo(prod_id,subname);
	}
	
	public playRecordInfo getPlayRecord(String prod_id)
	{
		return playRecordDao.getInstance(context).getProdIdInfo(prod_id);
	}
	
	public void deletePlayRecord(String prod_id)
	{
		playRecordDao.getInstance(context).delete(prod_id);
	}
}
