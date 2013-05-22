package com.joyplus.Service.Return;

import com.joyplus.Service.Return.ReturnUserFavorities.Favorities;
/*
 * /*
 * {
   histories: [
      {
          "prod_type": 视频类别 1：电影，2：电视剧，3：综艺，4：视频
          "prod_name": 视频名字,
          "prod_subname": 视频的集数,
          "prod_id": 视频id,
          "create_date": 播放时间
          "play_type": 播放的类别  1: 视频地址播放 2:webview播放
          "playback_time": 上次播放时间，单位：秒,
          "video_url": 视频地址,
          "duration": 视频时长， 单位：秒
          "prod_pic_url": 视频的图片
      },
     .......
   ]
 }
 */
public class ReturnUserPlayHistories {
	public Histories[] histories;

	public static class Histories {

		public int prod_type;
		public String prod_name;
		public String prod_subname;
		public String prod_id;
		public String create_date;
		public String play_type;
		public String prod_pic_url;
		public int playback_time;
		public String video_url;
		public int duration;

	}
}
