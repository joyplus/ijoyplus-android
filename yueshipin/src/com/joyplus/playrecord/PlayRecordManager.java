package com.joyplus.playrecord;

import com.joyplus.download.Dao;

import android.content.Context;

public class PlayRecordManager {
	
	private Context context;
	
	public PlayRecordManager(Context context){
		
		this.context = context;
	}
	
	public void savePlayRecord(PlayRecordInfo info)
	{
		if(PlayRecordDao.getInstance(context).getCount()>200)
		{
			PlayRecordDao.getInstance(context).delete();
		}
		if(PlayRecordDao.getInstance(context).getProdIdInfo(info.getProd_id())!=null)
		{
			if(PlayRecordDao.getInstance(context).isHasInfors(info.getProd_id(),info.getProd_subname()))
			{
				PlayRecordDao.getInstance(context).updateOneInfo(info);
			}
			else
			{
				if(info.getLast_playtime()!=null&&info.getLast_playtime().length()>0)
				{
					PlayRecordDao.getInstance(context).updateOneInfo(info);
				}
			}
		}
		else
		{
			PlayRecordDao.getInstance(context).InsertOneInfo(info);
		}
	}
	public PlayRecordInfo getPlayRecord(String prod_id,String subname)
	{
		return PlayRecordDao.getInstance(context).getOneInfo(prod_id,subname);
	}
	
	public PlayRecordInfo getPlayRecord(String prod_id)
	{
		return PlayRecordDao.getInstance(context).getProdIdInfo(prod_id);
	}
	
	public void deletePlayRecord(String prod_id)
	{
		PlayRecordDao.getInstance(context).delete(prod_id);
	}
}
