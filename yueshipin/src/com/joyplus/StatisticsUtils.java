package com.joyplus;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;

public class StatisticsUtils {
	
	private static final String TAG = "StatisticsUtils";
	
	/**
	 * 用来统计用户点击播放视屏后正常跳转的次数 有可能跳转到播放器，也有可能跳转到浏览器
	 * 
	 * 数据从服务器上获取
	 * @param aq
	 * @param prod_id
	 * @param prod_name
	 * @param prod_subname
	 * @param pro_type
	 */
		public static void StatisticsClicksShow(AQuery aq , App app,String prod_id , String prod_name , String prod_subname , int pro_type) {

			String url = Constant.BASE_URL + "program/recordPlay";

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("app_key", Constant.APPKEY);// required string //
													// 申请应用时分配的AppKey。

			params.put("prod_id", prod_id);// required string // 视频id

			params.put("prod_name", prod_name);// required // string 视频名字

			params.put("prod_subname", "");// required // string 视频的集数 电影的subname为空

			params.put("prod_type", 1);// required int 视频类别 1：电影，2：电视剧，3：综艺，4：视频

			AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
			 cb.SetHeader(app.getHeaders());
			cb.params(params).url(url).type(JSONObject.class);
			
			Log.i(TAG, "JSON:1111111111");
			
			aq.ajax(cb);
		}

}
