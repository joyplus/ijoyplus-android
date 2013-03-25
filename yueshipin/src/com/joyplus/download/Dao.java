package com.joyplus.download;

import java.util.ArrayList;
import java.util.List;

import com.joyplus.Video.PlayHistory;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * 一个业务类
 */
public class Dao {
	private static Dao dao = null;
	private Context context;

	private Dao(Context context) {
		this.context = context;
	}

	public static Dao getInstance(Context context) {
		if (dao == null) {
			dao = new Dao(context);
		}
		return dao;
	}

	public SQLiteDatabase getConnection() {
		SQLiteDatabase sqliteDatabase = null;
		try {
			sqliteDatabase = new DBHelper(context).getReadableDatabase();
		} catch (Exception e) {
		}
		return sqliteDatabase;
	}

	/**
	 * 查看数据库中是否有数据
	 */
	public synchronized boolean isHasInfors(String prod_id, String my_index) {
		SQLiteDatabase database = getConnection();
		int count = -1;
		Cursor cursor = null;
		try {
			String sql = "select count(*)  from download_info where prod_id=? and my_index=?";
			cursor = database.rawQuery(sql, new String[] { prod_id, my_index });
			if (cursor.moveToFirst()) {
				count = cursor.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return count == 0;
	}

	/**
	 * 查看数据库中是否有正在下载的数据
	 */
	public synchronized boolean isHasInforsDownloading(String download_state) {
		SQLiteDatabase database = getConnection();
		int count = -1;
		Cursor cursor = null;
		try {
			String sql = "select count(*)  from download_info where download_state=?";
			cursor = database.rawQuery(sql, new String[] { download_state });
			if (cursor.moveToFirst()) {
				count = cursor.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return count == 0;
	}
	
	/*
	 * 保存缓存记录
	 */
	public synchronized void saveInfos(List<DownloadInfo> infos) {
		SQLiteDatabase database = getConnection();
		try {
			for (DownloadInfo info : infos) {
				String sql = "insert into download_info(compeleteSize,fileSize, prod_id,my_index,url,urlposter,my_name,download_state) values (?,?,?,?,?,?,?,?)";
				Object[] bindArgs = { info.getCompeleteSize(),
						info.getFileSize(), info.getProd_id(), info.getMy_index(),
						info.getUrl(), info.getUrlposter(), info.getMy_name(),
						info.getDownload_state()};
				database.execSQL(sql, bindArgs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
	}
	
	/*
	 * 插入一条记录
	 */
	public synchronized void InsertOneInfo(DownloadInfo info) {
		SQLiteDatabase database = getConnection();
		try {
			String sql = "insert into download_info(compeleteSize,fileSize, prod_id,my_index,url,urlposter,my_name,download_state) values (?,?,?,?,?,?,?,?)";
			Object[] bindArgs = { info.getCompeleteSize(), info.getFileSize(),
					info.getProd_id(), info.getMy_index(), info.getUrl(),
					info.getUrlposter(), info.getMy_name(), info.getDownload_state() };
			database.execSQL(sql, bindArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
	}
	
	/*
	 * 获取某一个记录,返回一个DownloadInfo list类型
	 */
	public synchronized List<DownloadInfo> getInfos(String prod_id,
			String my_index) {
		List<DownloadInfo> list = new ArrayList<DownloadInfo>();
		SQLiteDatabase database = getConnection();
		Cursor cursor = null;
		try {
			String sql = "select compeleteSize,fileSize, prod_id,my_index,url,urlposter,my_name,download_state from download_info where prod_id=? and my_index=?";
			cursor = database.rawQuery(sql, new String[] { prod_id, my_index });
			while (cursor.moveToNext()) {
				DownloadInfo info = new DownloadInfo(cursor.getInt(0),
						cursor.getInt(1), cursor.getString(2),
						cursor.getString(3), cursor.getString(4),
						cursor.getString(5), cursor.getString(6),
						cursor.getString(7));
				list.add(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return list;
	}

	public synchronized DownloadInfo getOneInfo(String prod_id, String my_index) {
		DownloadInfo info = null;
		SQLiteDatabase database = getConnection();
		Cursor cursor = null;
		try {
			String sql = "select compeleteSize,fileSize, prod_id,my_index,url,urlposter,my_name,download_state from download_info where prod_id=? and my_index=?";
			cursor = database.rawQuery(sql, new String[] { prod_id, my_index });
			while (cursor.moveToNext()) {
				info = new DownloadInfo(cursor.getInt(0), cursor.getInt(1),
						cursor.getString(2), cursor.getString(3),
						cursor.getString(4), cursor.getString(5),
						cursor.getString(6), cursor.getString(7));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return info;
	}
	
	/*
	 * 获取某一个状态的某一条记录
	 */
	public synchronized DownloadInfo getOneStateInfo(String download_state) {
		DownloadInfo info = null;
		SQLiteDatabase database = getConnection();
		Cursor cursor = null;
		try {
			String sql = "select compeleteSize,fileSize, prod_id,my_index,url,urlposter,my_name,download_state from download_info where download_state=?";
			cursor = database.rawQuery(sql, new String[] { download_state });
			while (cursor.moveToNext()) {
				info = new DownloadInfo(cursor.getInt(0), cursor.getInt(1),
						cursor.getString(2), cursor.getString(3),
						cursor.getString(4), cursor.getString(5),
						cursor.getString(6), cursor.getString(7));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return info;
	}

	/*
	 * 返回数据库中所有的数据
	 */
	public synchronized List<DownloadInfo> getDownloadInfos() {
		List<DownloadInfo> list = new ArrayList<DownloadInfo>();
		SQLiteDatabase database = getConnection();
		Cursor cursor = null;
		try {
			String sql = "select compeleteSize,fileSize, prod_id,my_index,url,urlposter,my_name,download_state from download_info";
			cursor = database.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				DownloadInfo info = new DownloadInfo(cursor.getInt(0),
						cursor.getInt(1), cursor.getString(2),
						cursor.getString(3), cursor.getString(4),
						cursor.getString(5), cursor.getString(6),
						cursor.getString(7));
				list.add(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return list;
	}

	// 根据prod_id进行分组
	public synchronized List<DownloadInfo> getDownloadInfosGroup() {
		List<DownloadInfo> list = new ArrayList<DownloadInfo>();
		SQLiteDatabase database = getConnection();
		Cursor cursor = null;
		try {
			String sql = "select compeleteSize,fileSize, prod_id,my_index,url,urlposter,my_name,download_state from download_info group by prod_id";
			cursor = database.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				DownloadInfo info = new DownloadInfo(cursor.getInt(0),
						cursor.getInt(1), cursor.getString(2),
						cursor.getString(3), cursor.getString(4),
						cursor.getString(5), cursor.getString(6),
						cursor.getString(7));
				list.add(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return list;
	}

	// 获取某一个prod_id的所有数据,通常用于电视剧和节目
	public synchronized List<DownloadInfo> getInfosOfProd_id(String prod_id) {
		List<DownloadInfo> list = new ArrayList<DownloadInfo>();
		SQLiteDatabase database = getConnection();
		Cursor cursor = null;
		try {
			String sql = "select compeleteSize,fileSize, prod_id,my_index,url,urlposter,my_name,download_state from download_info where prod_id=?";
			cursor = database.rawQuery(sql, new String[] { prod_id });
			while (cursor.moveToNext()) {
				DownloadInfo info = new DownloadInfo(cursor.getInt(0),
						cursor.getInt(1), cursor.getString(2),
						cursor.getString(3), cursor.getString(4),
						cursor.getString(5), cursor.getString(6),
						cursor.getString(7));
				list.add(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return list;
	}

	/*
	 * 更新某一个下载记录下载了多少
	 */
	public synchronized void updataInfos(int compeleteSize, String prod_id,
			String my_index) {
		SQLiteDatabase database = getConnection();
		try {
			String sql = "update download_info set compeleteSize=? where prod_id=? and my_index=?";
			Object[] bindArgs = { compeleteSize, prod_id, my_index };
			database.execSQL(sql, bindArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
	}

	/*
	 * 更新某一条下载记录的状态
	 */
	public synchronized void updataInfoState(String download_state,
			String prod_id, String my_index) {
		SQLiteDatabase database = getConnection();
		try {
			String sql = "update download_info set download_state=? where prod_id=? and my_index=?";
			Object[] bindArgs = { download_state, prod_id, my_index };
			database.execSQL(sql, bindArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
	}

	/*
	 * 删除某一个记录
	 */
	public synchronized void delete(String prod_id, String my_index) {
		SQLiteDatabase database = getConnection();
		try {
			database.delete("download_info", "prod_id=? and my_index=?",
					new String[] { prod_id, my_index });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
	}
	
	/*
	 * 添加本地播放记录
	 */
	public synchronized void addPlayHistory(PlayHistory playhistory)
	{
		SQLiteDatabase database = getConnection();
		try {
			String sql = "insert into play_history(prod_id,my_index,play_time) values (?,?,?)";
			Object[] bindArgs = { playhistory.getProd_id(),playhistory.getMy_index(), playhistory.getPlay_time()};
			database.execSQL(sql, bindArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
	}
	/*
	 * 删除本地播放记录
	 */
	public synchronized void delPlayHistory(PlayHistory playhistory)
	{
		SQLiteDatabase database = getConnection();
		try {
			database.delete("play_history", "prod_id=? and my_index=?",
					new String[] { playhistory.getProd_id(), playhistory.getMy_index()});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
	}
	/*
	 * 更新本地播放记录
	 */
	public synchronized void updatePlayHistory(PlayHistory playhistory)
	{
		SQLiteDatabase database = getConnection();
		try {
			String sql = "update play_history set play_time=? where prod_id=? and my_index=?";
			Object[] bindArgs = { playhistory.getPlay_time(), playhistory.getProd_id(), playhistory.getMy_index() };
			database.execSQL(sql, bindArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
	}
	/*
	 * 查找本地播放记录
	 */
	public synchronized PlayHistory queryPlayHistory(PlayHistory playhistory)
	{
		PlayHistory tempPlayHistory = null;
		SQLiteDatabase database = getConnection();
		Cursor cursor = null;
		try {
			String sql = "select prod_id,my_index,play_time from play_history where prod_id=? and my_index=?";
			cursor = database.rawQuery(sql, new String[] { playhistory.getProd_id(),playhistory.getMy_index() });
			while (cursor.moveToNext()) {
				tempPlayHistory = new PlayHistory(cursor.getString(0), cursor.getString(1),
						cursor.getString(2));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return tempPlayHistory;
	}
}