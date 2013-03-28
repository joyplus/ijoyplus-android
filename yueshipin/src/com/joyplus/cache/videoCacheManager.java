package com.joyplus.cache;

import com.joyplus.download.Dao;

import android.content.Context;

public class videoCacheManager {
	private Context context;
	public videoCacheManager(Context context)
	{
		this.context = context;
	}
	/*
	 * 将缓存数据保存在数据库里
	 */
	public void saveVideoCache(videoCacheInfo info) {
		//保存之前先进行判断是不是要删除里面的数据
 		if(Dao_Cache.getInstance(context).getCount()>200)
		{
			Dao_Cache.getInstance(context).delete();
		}
		if(Dao_Cache.getInstance(context).isHasInfors(info.getProd_id()))
		{
			Dao_Cache.getInstance(context).InsertOneInfo(info);
		}
		else
		{
			Dao_Cache.getInstance(context).updateOneInfo(info);
		}
	}
	/*
	 * 从数据库中取出缓存数据
	 */
	public videoCacheInfo getVideoCache(String prod_id) {
		return Dao_Cache.getInstance(context).getOneInfo(prod_id);
	}

}
