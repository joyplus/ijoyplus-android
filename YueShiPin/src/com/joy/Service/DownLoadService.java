package com.joy.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joy.App;
import com.joy.Constant;
import com.joy.Service.Return.ReturnCommentReplies;
import com.joy.Service.Return.ReturnCommentView;
import com.joy.Service.Return.ReturnComments;
import com.joy.Service.Return.ReturnFriendAndMeDynamics;
import com.joy.Service.Return.ReturnFriendRecommends;
import com.joy.Service.Return.ReturnProgramView;
import com.joy.Service.Return.ReturnProgramViewRecommend;
import com.joy.Service.Return.ReturnSearch;
import com.joy.Service.Return.ReturnSearchTopKeywords;
import com.joy.Service.Return.ReturnUserFans;
import com.joy.Service.Return.ReturnUserFavorities;
import com.joy.Service.Return.ReturnUserFriendDynamics;
import com.joy.Service.Return.ReturnUserFriends;
import com.joy.Service.Return.ReturnUserMsgs;
import com.joy.Service.Return.ReturnUserOwnDynamics;
import com.joy.Service.Return.ReturnUserPrestiges;
import com.joy.Service.Return.ReturnUserRecommends;
import com.joy.Service.Return.ReturnUserThirdPartyUsers;
import com.joy.Service.Return.ReturnUserView;
import com.joy.Service.Return.ReturnUserWatchs;
import com.joy.Service.Return.ReturnValue;
import com.joy.Service.Return.ReturnVideoMovies;
import com.joy.Service.Return.ReturnVideoShows;
import com.joy.Service.Return.ReturnVideoTVs;
import com.joy.Service.Return.ReturnVideoVideo;

public class DownLoadService {
	private static final String TAG = "DownLoadService";
	private HttpClient httpClient;

	public void InitService() {

		this.httpClient = new DefaultHttpClient();
		Log.i(TAG, "InitService()...");
	}

	public void CloseService() {
		this.httpClient.getConnectionManager().shutdown();
	}

	public String OnHttpPost(String PostURL, List<NameValuePair> nameValuePairs) {
		JSONObject jsonObject = null;
		String result = null;
		if (PostURL.trim().length() == 0)
			return null;

		Log.i("cat", ">>>>>>" + PostURL.toString());
		HttpPost httpPost = new HttpPost(PostURL);
		httpPost.setHeader("Connection", "keep-alive");
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse httpResponse = this.httpClient.execute(httpPost);
			int res = httpResponse.getStatusLine().getStatusCode();
			if (res == 200) {

				StringBuilder ReturnBuilder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(
						new InputStreamReader(httpResponse.getEntity()
								.getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2
						.readLine()) {
					ReturnBuilder.append(s);
				}
				Log.i("cat", ">>>>>>" + ReturnBuilder.toString());

				// 返回的文件头信息
				Header[] hs = httpResponse.getAllHeaders();
				for (Header h : hs) {
					Log.i("Header", ">>>>>>" + h.getValue());
				}
				Log.i("cat", ">>>>>>" + ReturnBuilder.toString());

				result = ReturnBuilder.toString().trim();
				if (result != null && result.length() != 0) {
					try {
						jsonObject = new JSONObject(result);
						return jsonObject.toString();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public String OnHttpGet(String GetURL) {
		StringBuilder ReturnBuilder = new StringBuilder();
		String result = null;
		JSONObject jsonObject = null;
		if (GetURL.trim().length() == 0)
			return null;

		Log.i("cat", ">>>>>>" + GetURL.toString());
		HttpGet get = new HttpGet(GetURL);
		get.setHeader("Connection", "keep-alive");
		HttpResponse response;
		try {
			response = this.httpClient.execute(get);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			// builder.append("\nRETURN:\n");
			for (String s = reader.readLine(); s != null; s = reader.readLine()) {
				// builder.append(s);
				ReturnBuilder.append(s);
			}
			// 返回的文件头信息
			Header[] hs = response.getAllHeaders();
			for (Header h : hs) {
				Log.i("Header", ">>>>>>" + h.getValue());
			}
			Log.i("cat", ">>>>>>" + ReturnBuilder.toString());
			result = ReturnBuilder.toString().trim();
			if (result != null && result.length() != 0) {
				// save to local
				SaveServiceData(URLEncoder.encode(GetURL, "UTF-8"), result);
			} else
				result = GetServiceData(URLEncoder.encode(GetURL, "UTF-8"));
			try {
				jsonObject = new JSONObject(result);
				return jsonObject.toString();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			result = GetServiceData(URLEncoder.encode(GetURL, "UTF-8"));
			if (result == null)
				return null;
			try {
				jsonObject = new JSONObject(result);
				return jsonObject.toString();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	/*
	 * 功能说明：
	 * 
	 * 用户登出。 接口形式：
	 * 
	 * account/logout 请求方式：
	 * 
	 * GET 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 返回值：
	 * 
	 * { res_code: [String], // res code res_desc: [String], // res desc
	 * 
	 * }
	 */
	public boolean AccountLogout() {
		String m_GetURL = null;
		m_GetURL = Constant.BASE_URL + "account/logout" + "?app_key="
				+ Constant.APPKEY;

		return ReturnTorF(OnHttpGet(m_GetURL));
	}

	// { "kPathProgramView ", BASE_URL
	// +"program/view","GET","2","app_key","prod_id","RETURN","XML"},
	public ReturnProgramView ProgramView(String prod_id) {
		String m_GetURL = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);

		m_GetURL = Constant.BASE_URL + "program/view" + "?app_key="
				+ Constant.APPKEY + "&prod_id=" + prod_id;

		try {
			return mapper.readValue(OnHttpGet(m_GetURL),
					ReturnProgramView.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 功能说明：
	 * 
	 * 获得最热搜索 接口形式：
	 * 
	 * /search/topKeywords 请求方式：
	 * 
	 * GET 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 num option 条数，可选，默认为10条 返回值：
	 */

	public ReturnSearchTopKeywords SearchTopKeywords() {

		return SearchTopKeywords("10");
	}

	public ReturnSearchTopKeywords SearchTopKeywords(String num) {
		String m_GetURL = null;
		ObjectMapper mapper = new ObjectMapper();

		m_GetURL = Constant.BASE_URL + "search/topKeywords" + "?app_key="
				+ Constant.APPKEY + "&num=" + num;

		try {
			return mapper.readValue(OnHttpGet(m_GetURL),
					ReturnSearchTopKeywords.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 功能说明：
	 * 
	 * 获得某节目的评论 接口形式：
	 * 
	 * program/comments 请求方式：
	 * 
	 * GET 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 prod_id required string 节目id
	 * page_num = 需要请求的页码（可选），默认为1 page_size = 每一页包含的记录数（可选），默认为10 返回值：
	 */
	public ReturnComments ProgramComments(String prod_id) {

		return ProgramComments(prod_id, "1", "10");
	}

	public ReturnComments ProgramComments(String prod_id, String page_num) {

		return ProgramComments(prod_id, page_num, "10");
	}

	public ReturnComments ProgramComments(String prod_id, String page_num,
			String page_size) {
		String m_GetURL = null;
		ObjectMapper mapper = new ObjectMapper();

		m_GetURL = Constant.BASE_URL + "program/comments" + "?app_key="
				+ Constant.APPKEY + "&prod_id=" + prod_id + "&page_num="
				+ page_num + "&page_size=" + page_size;

		try {
			return mapper.readValue(OnHttpGet(m_GetURL), ReturnComments.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 功能说明：
	 * 
	 * 获得好友推荐的节目内容 接口形式：
	 * 
	 * program/viewRecommend 请求方式：
	 * 
	 * GET 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 prod_id required string 节目id
	 * user_id required string 用户id 返回值：
	 */
	public ReturnProgramViewRecommend ProgramViewRecommend(String prod_id,
			String user_id) {
		String m_GetURL = null;
		ObjectMapper mapper = new ObjectMapper();

		m_GetURL = Constant.BASE_URL + "program/viewRecommend" + "?app_key="
				+ Constant.APPKEY + "&prod_id=" + prod_id + "&user_id="
				+ user_id;

		try {
			return mapper.readValue(OnHttpGet(m_GetURL),
					ReturnProgramViewRecommend.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 功能说明：
	 * 
	 * 获得用户信息 接口形式：
	 * 
	 * user/view 请求方式：
	 * 
	 * GET 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 userid required string 用户id，
	 * 可选，默认为当前用户 返回值： ReturnUserView
	 */
	public ReturnUserView UserView() {
		String m_GetURL = null;
		ObjectMapper mapper = new ObjectMapper();

		m_GetURL = Constant.BASE_URL + "user/view" + "?app_key="
				+ Constant.APPKEY;
		try {
			return mapper.readValue(OnHttpGet(m_GetURL), ReturnUserView.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public ReturnUserView UserView(String userid) {
		String m_GetURL = null;
		if (userid.trim().length() == 0)
			return null;
		ObjectMapper mapper = new ObjectMapper();

		m_GetURL = Constant.BASE_URL + "user/view" + "?app_key="
				+ Constant.APPKEY + "&userid=" + userid;
		try {
			return mapper.readValue(OnHttpGet(m_GetURL), ReturnUserView.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 功能说明：
	 * 
	 * 获得用户第三方账号好友列表 接口形式：
	 * 
	 * user/thirdPartyUsers 请求方式：
	 * 
	 * GET 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 source_type required string
	 * 第三方账号类别。 1:新浪，2：腾讯，3：人人网，4：豆瓣,5:本地通讯录 返回值： ReturnUserThirdPartyUsers
	 */
	public ReturnUserThirdPartyUsers UserThirdPartyUsers(String source_type) {
		String m_GetURL = null;
		if (source_type.trim().length() == 0)
			return null;
		ObjectMapper mapper = new ObjectMapper();

		m_GetURL = Constant.BASE_URL + "user/thirdPartyUsers" + "?app_key="
				+ Constant.APPKEY + "&source_type=" + source_type;
		try {
			return mapper.readValue(OnHttpGet(m_GetURL),
					ReturnUserThirdPartyUsers.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 获得关注用户New Page Edit Page Page History 功能说明：
	 * 
	 * 获得用户的关注用户 接口形式：
	 * 
	 * user/friends 请求方式：
	 * 
	 * GET 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 userid required string
	 * 用户id，可选，默认为当前用户 page_num = 需要请求的页码（可选），默认为1 page_size =
	 * 每一页包含的记录数（可选），默认为10 返回值： ReturnUserFriends
	 */
	public ReturnUserFriends UserFriends(String userid) {

		return UserFriends(userid, "1", "10");
	}

	public ReturnUserFriends UserFriends(String userid, String page_num) {

		return UserFriends(userid, page_num, "10");
	}

	public ReturnUserFriends UserFriends(String userid, String page_num,
			String page_size) {
		String m_GetURL = null;
		ObjectMapper mapper = new ObjectMapper();

		m_GetURL = Constant.BASE_URL + "user/friends" + "?app_key="
				+ Constant.APPKEY + "&userid=" + userid + "&page_num="
				+ page_num + "&page_size=" + page_size;
		try {
			return mapper.readValue(OnHttpGet(m_GetURL),
					ReturnUserFriends.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 功能说明：
	 * 
	 * 获得用户的粉丝 接口形式：
	 * 
	 * user/fans 请求方式：
	 * 
	 * GET 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 userid required string
	 * 用户id，可选，默认为当前用户 page_num = 需要请求的页码（可选），默认为1 page_size =
	 * 每一页包含的记录数（可选），默认为10 返回值： ReturnUserFans
	 */
	public ReturnUserFans UserFans(String userid) {

		return UserFans(userid, "1", "10");
	}

	public ReturnUserFans UserFans(String userid, String page_num) {

		return UserFans(userid, page_num, "10");
	}

	public ReturnUserFans UserFans(String userid, String page_num,
			String page_size) {
		String m_GetURL = null;
		ObjectMapper mapper = new ObjectMapper();

		m_GetURL = Constant.BASE_URL + "user/fans" + "?app_key="
				+ Constant.APPKEY + "&userid=" + userid + "&page_num="
				+ page_num + "&page_size=" + page_size;
		try {
			return mapper.readValue(OnHttpGet(m_GetURL), ReturnUserFans.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 功能说明：
	 * 
	 * 获得好友推荐 接口形式：
	 * 
	 * friend/recommends 请求方式：
	 * 
	 * GET 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 page_num = 需要请求的页码（可选），默认为1
	 * page_size = 每一页包含的记录数（可选），默认为10 返回值： ReturnFriendRecommends
	 */
	public ReturnFriendRecommends FriendRecommends() {

		return FriendRecommends("1", "10");
	}

	public ReturnFriendRecommends FriendRecommends(String page_num) {

		return FriendRecommends(page_num, "10");
	}

	public ReturnFriendRecommends FriendRecommends(String page_num,
			String page_size) {
		String m_GetURL = null;
		ObjectMapper mapper = new ObjectMapper();

		m_GetURL = Constant.BASE_URL + "friend/recommends" + "?app_key="
				+ Constant.APPKEY + "&page_num=" + page_num + "&page_size="
				+ page_size;
		try {
			return mapper.readValue(OnHttpGet(m_GetURL),
					ReturnFriendRecommends.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 功能说明：
	 * 
	 * 获得用户好友的动态 接口形式：
	 * 
	 * user/friendDynamics 请求方式：
	 * 
	 * GET 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 page_num = 需要请求的页码（可选），默认为1
	 * page_size = 每一页包含的记录数（可选），默认为10 user_id 用户id，可选，默认为当前用户 返回值：
	 * ReturnUserFriendDynamics
	 */
	public ReturnUserFriendDynamics UserFriendDynamics(String user_id) {

		return UserFriendDynamics(user_id, "1", "10");
	}

	public ReturnUserFriendDynamics UserFriendDynamics(String user_id,
			String page_num) {

		return UserFriendDynamics(user_id, page_num, "10");
	}

	public ReturnUserFriendDynamics UserFriendDynamics(String user_id,
			String page_num, String page_size) {
		String m_GetURL = null;
		ObjectMapper mapper = new ObjectMapper();

		m_GetURL = Constant.BASE_URL + "user/friendDynamics" + "?app_key="
				+ Constant.APPKEY + "&page_num=" + page_num + "&page_size="
				+ page_size + "&user_id=" + user_id;
		try {
			return mapper.readValue(OnHttpGet(m_GetURL),
					ReturnUserFriendDynamics.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 功能说明：
	 * 
	 * 获得用户和他的好友的动态,目前只有观看节目，收藏节目，推荐节目，关注某人。 接口形式：
	 * 
	 * user/friendAndMeDynamics 请求方式：
	 * 
	 * GET 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 page_num = 需要请求的页码（可选），默认为1
	 * page_size = 每一页包含的记录数（可选），默认为10 user_id 用户id，可选，默认为当前用户 返回值：,
	 * ReturnFriendAndMeDynamics
	 */
	public ReturnFriendAndMeDynamics FriendAndMeDynamics(String user_id) {

		return FriendAndMeDynamics("1", "10");
	}

	public ReturnFriendAndMeDynamics FriendAndMeDynamics(String user_id,
			String page_num) {

		return FriendAndMeDynamics(page_num, "10");
	}

	public ReturnFriendAndMeDynamics FriendAndMeDynamics(String page_num,
			String page_size, String user_id) {
		String m_GetURL = null;
		ObjectMapper mapper = new ObjectMapper();

		m_GetURL = Constant.BASE_URL + "user/friendAndMeDynamics" + "?app_key="
				+ Constant.APPKEY + "&page_num=" + page_num + "&page_size="
				+ page_size + "&user_id=" + user_id;
		try {
			return mapper.readValue(OnHttpGet(m_GetURL),
					ReturnFriendAndMeDynamics.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 功能说明：
	 * 
	 * 获得用户的通知信息 //目前只通知评论，回复评论，关注 接口形式：
	 * 
	 * user/msgs 请求方式：
	 * 
	 * GET 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 page_num = 需要请求的页码（可选），默认为1
	 * page_size = 每一页包含的记录数（可选），默认为10 返回值： ReturnUserMsgs
	 */
	public ReturnUserMsgs UserMsgs() {

		return UserMsgs("1", "10");
	}

	public ReturnUserMsgs UserMsgs(String page_num) {

		return UserMsgs(page_num, "10");
	}

	public ReturnUserMsgs UserMsgs(String page_num, String page_size) {
		String m_GetURL = null;
		ObjectMapper mapper = new ObjectMapper();

		m_GetURL = Constant.BASE_URL + "user/msgs" + "?app_key="
				+ Constant.APPKEY + "&page_num=" + page_num + "&page_size="
				+ page_size;
		try {
			return mapper.readValue(OnHttpGet(m_GetURL), ReturnUserMsgs.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 功能说明：
	 * 
	 * 获得用户看过的节目清单 接口形式：
	 * 
	 * user/watchs 请求方式：
	 * 
	 * GET 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 userid required string
	 * 用户id，可选，默认为当前用户 page_num = 需要请求的页码（可选），默认为1 page_size =
	 * 每一页包含的记录数（可选），默认为10 返回值：
	 */
	public ReturnUserWatchs UserWatchs(String userid) {

		return UserWatchs(userid, "1", "10");
	}

	public ReturnUserWatchs UserWatchs(String userid, String page_num) {

		return UserWatchs(userid, page_num, "10");
	}

	public ReturnUserWatchs UserWatchs(String userid, String page_num,
			String page_size) {
		String m_GetURL = null;
		if (userid.trim().length() == 0)
			return null;

		ObjectMapper mapper = new ObjectMapper();

		m_GetURL = Constant.BASE_URL + "user/watchs" + "?app_key="
				+ Constant.APPKEY + "&userid=" + userid + "&page_num="
				+ page_num + "&page_size=" + page_size;
		try {
			return mapper
					.readValue(OnHttpGet(m_GetURL), ReturnUserWatchs.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 功能说明：
	 * 
	 * 获得用户推荐过的节目清单 接口形式：
	 * 
	 * /user/recommends 请求方式：
	 * 
	 * GET 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 userid required string
	 * 用户id，可选，默认为当前用户 page_num = 需要请求的页码（可选），默认为1 page_size =
	 * 每一页包含的记录数（可选），默认为10 返回值：
	 */
	public ReturnUserRecommends UserRecommends(String userid) {

		return UserRecommends(userid, "1", "10");
	}

	public ReturnUserRecommends UserRecommends(String userid, String page_num) {

		return UserRecommends(userid, page_num, "10");
	}

	public ReturnUserRecommends UserRecommends(String userid, String page_num,
			String page_size) {
		String m_GetURL = null;
		ObjectMapper mapper = new ObjectMapper();

		m_GetURL = Constant.BASE_URL + "user/recommends" + "?app_key="
				+ Constant.APPKEY + "&userid=" + userid + "&page_num="
				+ page_num + "&page_size=" + page_size;
		try {
			return mapper.readValue(OnHttpGet(m_GetURL),
					ReturnUserRecommends.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 功能说明：
	 * 
	 * 获得用户收藏过的节目清单 接口形式：
	 * 
	 * /user/favorities 请求方式：
	 * 
	 * GET 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 userid required string
	 * 用户id，可选，默认为当前用户 page_num = 需要请求的页码（可选），默认为1 page_size =
	 * 每一页包含的记录数（可选），默认为10 返回值：
	 */
	public ReturnUserFavorities UserFavorities(String userid) {

		return UserFavorities(userid, "1", "10");
	}

	public ReturnUserFavorities UserFavorities(String userid, String page_num) {

		return UserFavorities(userid, page_num, "10");
	}

	public ReturnUserFavorities UserFavorities(String userid, String page_num,
			String page_size) {
		String m_GetURL = null;
		ObjectMapper mapper = new ObjectMapper();

		m_GetURL = Constant.BASE_URL + "user/favorities" + "?app_key="
				+ Constant.APPKEY + "&userid=" + userid + "&page_num="
				+ page_num + "&page_size=" + page_size;
		try {
			return mapper.readValue(OnHttpGet(m_GetURL),
					ReturnUserFavorities.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 获取评论内容New Page Edit Page Page History 功能说明：
	 * 
	 * 获取评论的内容，并且包含10条最新的评论回复 接口形式：
	 * 
	 * comment/view 请求方式：
	 * 
	 * GET 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 thread_id required string 评论的id。
	 * 返回值：
	 */
	public ReturnCommentView CommentView(String thread_id) {
		String m_GetURL = null;
		ObjectMapper mapper = new ObjectMapper();

		m_GetURL = Constant.BASE_URL + "comment/view" + "?app_key="
				+ Constant.APPKEY + "&thread_id=" + thread_id;
		try {
			return mapper.readValue(OnHttpGet(m_GetURL),
					ReturnCommentView.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 搜索New Page Edit Page Page History 功能说明：
	 * 
	 * 根据关键字获得节目内容 接口形式：
	 * 
	 * search 请求方式：
	 * 
	 * GET 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 keyword required string 搜索关键字
	 * page_num = 需要请求的页码（可选），默认为1 page_size = 每一页包含的记录数（可选），默认为10 返回值：
	 */
	public ReturnSearch Search(String keyword) {

		return Search(keyword, "1", "10");
	}

	public ReturnSearch Search(String keyword, String page_num) {

		return Search(keyword, page_num, "10");
	}

	public ReturnSearch Search(String keyword, String page_num, String page_size) {

		String m_GetURL = null;
		String m_Result = null;
		ObjectMapper mapper = new ObjectMapper();

		m_GetURL = Constant.BASE_URL + "search" + "?app_key=" + Constant.APPKEY
				+ "&keyword=" + keyword + "&page_num=" + page_num
				+ "&page_size=" + page_size;

		try {
			return mapper.readValue(OnHttpGet(m_GetURL), ReturnSearch.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/*
	 * 功能说明：
	 * 
	 * 返回按照热门程度逆序排序的节目列表（节目可以被手动置顶）。 接口形式：
	 * 
	 * video_movies 请求方式：
	 * 
	 * GET 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 page_num = 需要请求的页码（可选），默认为1
	 * page_size = 每一页包含的记录数（可选），默认为10 返回值：
	 */
	public ReturnVideoMovies VideoMovies() {

		return VideoMovies("1", "10");
	}

	public ReturnVideoMovies VideoMovies(String page_num) {

		return VideoMovies(page_num, "10");
	}

	public ReturnVideoMovies VideoMovies(String page_num, String page_size) {

		String m_GetURL = null;
		ObjectMapper mapper = new ObjectMapper();

		m_GetURL = Constant.BASE_URL + "video_movies" + "?app_key="
				+ Constant.APPKEY + "&page_num=" + page_num + "&page_size="
				+ page_size;
		String str = OnHttpGet(m_GetURL);
		try {
			return mapper.readValue(str, ReturnVideoMovies.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 功能说明：
	 * 
	 * 返回按照热门程度逆序排序的电视剧，包含TV剧和动画片，节目列表（节目可以被手动置顶）。 接口形式：
	 * 
	 * video_tvs 请求方式：
	 * 
	 * GET 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 page_num = 需要请求的页码（可选），默认为1
	 * page_size = 每一页包含的记录数（可选），默认为10 返回值：
	 */
	public ReturnVideoTVs VideoTVs() {

		return VideoTVs("1", "10");
	}

	public ReturnVideoTVs VideoTVs(String page_num) {

		return VideoTVs(page_num, "10");
	}

	public ReturnVideoTVs VideoTVs(String page_num, String page_size) {

		String m_GetURL = null;
		ObjectMapper mapper = new ObjectMapper();

		m_GetURL = Constant.BASE_URL + "video_tvs" + "?app_key="
				+ Constant.APPKEY + "&page_num=" + page_num + "&page_size="
				+ page_size;
		try {
			return mapper.readValue(OnHttpGet(m_GetURL), ReturnVideoTVs.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 功能说明：
	 * 
	 * 返回按照热门程度逆序排序的综艺节目列表（节目可以被手动置顶）。 接口形式：
	 * 
	 * video_shows 请求方式：
	 * 
	 * GET 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 page_num = 需要请求的页码（可选），默认为1
	 * page_size = 每一页包含的记录数（可选），默认为10 返回值：
	 */
	public ReturnVideoShows VideoShows() {

		return VideoShows("1", "10");
	}

	public ReturnVideoShows VideoShows(String page_num) {

		return VideoShows(page_num, "10");
	}

	public ReturnVideoShows VideoShows(String page_num, String page_size) {

		String m_GetURL = null;
		ObjectMapper mapper = new ObjectMapper();

		m_GetURL = Constant.BASE_URL + "video_shows" + "?app_key="
				+ Constant.APPKEY + "&page_num=" + page_num + "&page_size="
				+ page_size;

		try {
			return mapper
					.readValue(OnHttpGet(m_GetURL), ReturnVideoShows.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 功能说明：
	 * 
	 * 返回按照热门程度逆序排序的个人上传的搞笑视频，节目列表（节目可以被手动置顶）。 接口形式：
	 * 
	 * video_video 请求方式：
	 * 
	 * GET 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 page_num = 需要请求的页码（可选），默认为1
	 * page_size = 每一页包含的记录数（可选），默认为10 返回值：
	 */
	public ReturnVideoVideo VideoVideo() {

		return VideoVideo("1", "10");
	}

	public ReturnVideoVideo VideoVideo(String page_num) {

		return VideoVideo(page_num, "10");
	}

	public ReturnVideoVideo VideoVideo(String page_num, String page_size) {

		String m_GetURL = null;
		ObjectMapper mapper = new ObjectMapper();

		m_GetURL = Constant.BASE_URL + "video_video" + "?app_key="
				+ Constant.APPKEY + "&page_num=" + page_num + "&page_size="
				+ page_size;
		try {
			return mapper
					.readValue(OnHttpGet(m_GetURL), ReturnVideoVideo.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 推荐达人New Page Edit Page Page History 功能说明：
	 * 
	 * 获得推荐达人列表 接口形式：
	 * 
	 * user/prestiges 请求方式：
	 * 
	 * GET 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 page_num = 需要请求的页码（可选），默认为1
	 * page_size = 每一页包含的记录数（可选），默认为10 返回值：
	 */
	public ReturnUserPrestiges UserPrestiges() {

		return UserPrestiges("1", "10");
	}

	public ReturnUserPrestiges UserPrestiges(String page_num) {

		return UserPrestiges(page_num, "10");
	}

	public ReturnUserPrestiges UserPrestiges(String page_num, String page_size) {

		String m_GetURL = null;
		ObjectMapper mapper = new ObjectMapper();

		m_GetURL = Constant.BASE_URL + "user/prestiges" + "?app_key="
				+ Constant.APPKEY + "&page_num=" + page_num + "&page_size="
				+ page_size;
		try {
			return mapper.readValue(OnHttpGet(m_GetURL),
					ReturnUserPrestiges.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 功能说明：
	 * 
	 * 获得用户动态 接口形式：
	 * 
	 * user/ownDynamics 请求方式：
	 * 
	 * GET 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 page_num = 需要请求的页码（可选），默认为1
	 * page_size = 每一页包含的记录数（可选），默认为10 user_id 用户id，可选，默认为当前用户 返回值：
	 * ReturnUserOwnDynamics
	 */
	public ReturnUserOwnDynamics UserOwnDynamics() {

		return UserOwnDynamics("1", "10");
	}

	public ReturnUserOwnDynamics UserOwnDynamics(String page_num) {

		return UserOwnDynamics(page_num, "10");
	}

	public ReturnUserOwnDynamics UserOwnDynamics(String page_num,
			String page_size) {

		String m_GetURL = null;
		ObjectMapper mapper = new ObjectMapper();

		m_GetURL = Constant.BASE_URL + "user/ownDynamics" + "?app_key="
				+ Constant.APPKEY + "&page_num=" + page_num + "&page_size="
				+ page_size;
		try {
			return mapper.readValue(OnHttpGet(m_GetURL),
					ReturnUserOwnDynamics.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 功能说明：
	 * 
	 * 获取某评论的回复 接口形式：
	 * 
	 * comment/replies 请求方式：
	 * 
	 * GET 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 thread_id required string 评论的id。
	 * page_num = 需要请求的页码（可选），默认为1 page_size = 每一页包含的记录数（可选），默认为10 返回值：
	 */
	public ReturnCommentReplies CommentReplies() {

		return CommentReplies("1", "10");
	}

	public ReturnCommentReplies CommentReplies(String page_num) {

		return CommentReplies(page_num, "10");
	}

	public ReturnCommentReplies CommentReplies(String page_num, String page_size) {

		String m_GetURL = null;
		ObjectMapper mapper = new ObjectMapper();

		m_GetURL = Constant.BASE_URL + "comment/replies" + "?app_key="
				+ Constant.APPKEY + "&page_num=" + page_num + "&page_size="
				+ page_size;

		try {
			return mapper.readValue(OnHttpGet(m_GetURL),
					ReturnCommentReplies.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 功能说明：
	 * 
	 * 用户登陆。 接口形式：
	 * 
	 * account/login 请求方式：
	 * 
	 * POST 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 username required string
	 * 授权用户的用户名。 password required string 授权用户的密码。 返回值：
	 * 
	 * { res_code: [String], // res code res_desc: [String], // res desc
	 * 
	 * }
	 */

	public boolean AccountLogin(String username, String password) {
		String m_PostURL = Constant.BASE_URL + "account/login";
		String getLoginString = null;

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);

		nameValuePairs.add(new BasicNameValuePair("app_key", Constant.APPKEY));
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("password", password));
		getLoginString = OnHttpPost(m_PostURL, nameValuePairs);
		return ReturnTorF(getLoginString);

	}

	/*
	 * 功能说明：
	 * 
	 * 收藏节目，收藏你感兴趣的节目，方便以后观看和推荐给好友 接口形式：
	 * 
	 * program/favority 请求方式：
	 * 
	 * POST 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 prod_id required string 节目的id
	 * 返回值：
	 */
	public boolean ProgramFavority(String prod_id) {
		String m_PostURL = Constant.BASE_URL + "program/favority";
		String geString = null;

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

		nameValuePairs.add(new BasicNameValuePair("app_key", Constant.APPKEY));
		nameValuePairs.add(new BasicNameValuePair("prod_id", prod_id));
		geString = OnHttpPost(m_PostURL, nameValuePairs);
		return ReturnTorF(geString);
	}

	/*
	 * 取消收藏节目New Page Edit Page Page History 功能说明：
	 * 
	 * 取消你收藏的节目 接口形式：
	 * 
	 * program/unfavority 请求方式：
	 * 
	 * POST 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 prod_id required string 节目的id
	 * 返回值：
	 */
	public ReturnValue ProgramUnfavority(String prod_id) {
		String m_PostURL = Constant.BASE_URL + "program/unfavority";
		String geString = null;

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("app_key", Constant.APPKEY));
		nameValuePairs.add(new BasicNameValuePair("prod_id", prod_id));
		geString = OnHttpPost(m_PostURL, nameValuePairs);
		return ReturnClassValue(geString);
	}

	/*
	 * 功能说明：
	 * 
	 * 对喜欢或不喜欢的节目发表自己的感受，评论 接口形式：
	 * 
	 * program/comment 请求方式：
	 * 
	 * POST 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 prod_id required string 被评论的节目id
	 * content required string 评论的内容 返回值：
	 */
	public boolean ProgramComment(String prod_id, String content) {

		String m_PostURL = Constant.BASE_URL + "program/comment";
		String geString = null;

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("app_key", Constant.APPKEY));
		nameValuePairs.add(new BasicNameValuePair("prod_id", prod_id));
		geString = OnHttpPost(m_PostURL, nameValuePairs);
		return ReturnTorF(geString);
	}

	/*
	 * 观看节目New Page Edit Page Page History 功能说明：
	 * 
	 * 如果你观看了节目，需要调用这个接口 接口形式：
	 * 
	 * program/watch 请求方式：
	 * 
	 * POST 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 prod_id required string 节目id 返回值：
	 */
	public boolean ProgramWatch(String prod_id) {

		String m_PostURL = Constant.BASE_URL + "program/watch";
		String geString = null;

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("app_key", Constant.APPKEY));
		nameValuePairs.add(new BasicNameValuePair("prod_id", prod_id));
		geString = OnHttpPost(m_PostURL, nameValuePairs);
		return ReturnTorF(geString);
	}

	/*
	 * 分享节目New Page Edit Page Page History 功能说明：
	 * 
	 * 分享节目，如果你分享接到到第三方系统，你需要调用这个接口，目前有新浪，腾讯，人人网，豆瓣 接口形式：
	 * 
	 * program/share 请求方式：
	 * 
	 * POST 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 prod_id required string 被分享的节目id
	 * where required string 分享哪儿， 1:新浪，2：腾讯，3：人人网，4：豆瓣 返回值：
	 */
	public ReturnValue ProgramShare(String prod_id, String where) {

		String m_PostURL = Constant.BASE_URL + "program/share";
		String geString = null;

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("app_key", Constant.APPKEY));
		nameValuePairs.add(new BasicNameValuePair("prod_id", prod_id));
		nameValuePairs.add(new BasicNameValuePair("where", where));
		geString = OnHttpPost(m_PostURL, nameValuePairs);
		return ReturnClassValue(geString);
	}

	/*
	 * 功能说明：
	 * 
	 * 推荐节目，推荐过的节目可能会在好友推荐里出现 接口形式：
	 * 
	 * program/recommend 请求方式：
	 * 
	 * POST 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 prod_id required string 节目id
	 * reason option string 推荐理由 返回值：
	 */
	public boolean ProgramRecommend(String thread_id, String reason) {

		String m_PostURL = Constant.BASE_URL + "program/recommend";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("app_key", Constant.APPKEY));
		nameValuePairs.add(new BasicNameValuePair("prod_id", thread_id));
		nameValuePairs.add(new BasicNameValuePair("reason", reason));
		return ReturnTorF(OnHttpPost(m_PostURL, nameValuePairs));
	}

	/*
	 * 功能说明：
	 * 
	 * 隐藏推荐节目，此节目不会在好友推荐和好友的推荐列表中出现 接口形式：
	 * 
	 * program/hiddenRecommend 请求方式：
	 * 
	 * POST 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 prod_id required string 节目id 返回值：
	 */
	public ReturnValue ProgramHiddenRecommend(String prod_id) {
		String m_PostURL = Constant.BASE_URL + "program/hiddenRecommend";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("app_key", Constant.APPKEY));
		nameValuePairs.add(new BasicNameValuePair("prod_id", prod_id));
		return ReturnClassValue(OnHttpPost(m_PostURL, nameValuePairs));
	}

	/*
	 * 功能说明：
	 * 
	 * 隐藏观看过的节目，此节目不会在好友的观看列表中出现 接口形式：
	 * 
	 * program/hiddenWatch 请求方式：
	 * 
	 * POST 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 prod_id required string 节目id 返回值：
	 */
	public ReturnValue ProgramHiddenWatch(String prod_id) {

		String m_PostURL = Constant.BASE_URL + "program/hiddenWatch";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("app_key", Constant.APPKEY));
		nameValuePairs.add(new BasicNameValuePair("prod_id", prod_id));
		return ReturnClassValue(OnHttpPost(m_PostURL, nameValuePairs));
	}

	/*
	 * 功能说明：
	 * 
	 * 用第三方账号登陆后，需要完善个人资料，其中包括昵称，电子邮件，密码等。 接口形式：
	 * 
	 * account/updateProfile 请求方式：
	 * 
	 * POST 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 username required string
	 * 授权用户的用户名。必须是电子邮件 password required string 授权用户的密码。 nickname required
	 * string 授权用户昵称 source_id required string 第三方账号用户id。 source_type required
	 * string 第三方账号类别。 1:新浪，2：腾讯，3：人人网，4：豆瓣 返回值：
	 * 
	 * { res_code: [String], // res 编码 res_desc: [String], // res 描述
	 * 
	 * }
	 */
	public ReturnValue AccountUpdateProfile(String username, String password,
			String nickname, String source_id, String source_type) {
		String m_PostURL = Constant.BASE_URL + "account/updateProfile";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
		nameValuePairs.add(new BasicNameValuePair("app_key", Constant.APPKEY));
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("password", password));
		nameValuePairs.add(new BasicNameValuePair("nickname", nickname));
		nameValuePairs.add(new BasicNameValuePair("source_id", source_id));
		nameValuePairs.add(new BasicNameValuePair("source_type", source_type));
		return ReturnClassValue(OnHttpPost(m_PostURL, nameValuePairs));
	}

	/*
	 * 功能说明：
	 * 
	 * 绑定手机号码，用于其它用户通过本地通讯录寻找好友 接口形式：
	 * 
	 * account/bindPhone 请求方式：
	 * 
	 * POST 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 phone required string 手机号码。 返回值：
	 * 
	 * { res_code: [String], // res code res_desc: [String], // res desc
	 * 
	 * }
	 */
	public ReturnValue AccountBindPhone(String phone) {

		String m_PostURL = Constant.BASE_URL + "account/bindPhone";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("app_key", Constant.APPKEY));
		nameValuePairs.add(new BasicNameValuePair("phone", phone));
		return ReturnClassValue(OnHttpPost(m_PostURL, nameValuePairs));
	}

	/*
	 * 功能说明：
	 * 
	 * 取消绑定第三方账号，目前可以绑定豆瓣，腾讯，新浪，人人网的微博账号 接口形式：
	 * 
	 * account/unbindAccount 请求方式：
	 * 
	 * POST 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 source_type required string
	 * 第三方账号类别。 1:新浪，2：腾讯，3：人人网，4：豆瓣 返回值：
	 * 
	 * { res_code: [String], // res code res_desc: [String], // res desc
	 * 
	 * }
	 */
	public ReturnValue AccountUnbindAccount(String source_type) {

		String m_PostURL = Constant.BASE_URL + "account/unbindAccount";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("app_key", Constant.APPKEY));
		nameValuePairs.add(new BasicNameValuePair("source_type", source_type));
		return ReturnClassValue(OnHttpPost(m_PostURL, nameValuePairs));
	}

	/*
	 * 功能说明：
	 * 
	 * 忘记密码，通过发送电子邮件重置用户密码 接口形式：
	 * 
	 * account/forgotPwd 请求方式：
	 * 
	 * POST 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 loginname required string
	 * 用户名/用户电子邮件 返回值：
	 * 
	 * { res_code: [String], // res code res_desc: [String], // res desc
	 * 
	 * }
	 */
	public ReturnValue AccountForgotPwd(String loginname) {

		String m_PostURL = Constant.BASE_URL + "account/forgotPwd";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("app_key", Constant.APPKEY));
		nameValuePairs.add(new BasicNameValuePair("loginname", loginname));
		return ReturnClassValue(OnHttpPost(m_PostURL, nameValuePairs));
	}

	/*
	 * 功能说明：
	 * 
	 * 关注用户，你可以批量关注多个/一个用户 接口形式：
	 * 
	 * friend/follow 请求方式：
	 * 
	 * POST 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 friend_ids required string
	 * 你希望关注用户的id，每个用户的id以,分开。比如11,12,14 返回值： boolean
	 */
	public ReturnValue Friendfollow(String friend_ids) {

		String m_PostURL = Constant.BASE_URL + "friend/follow";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("app_key", Constant.APPKEY));
		nameValuePairs.add(new BasicNameValuePair("friend_ids", friend_ids));
		return ReturnClassValue(OnHttpPost(m_PostURL, nameValuePairs));
	}

	/*
	 * 功能说明：
	 * 
	 * 喜欢用户，你可以批量喜欢多个/一个用户 接口形式：
	 * 
	 * friend/like 请求方式：
	 * 
	 * POST 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 user_id required string
	 * 你希望喜欢用户的id 返回值：
	 * 
	 * boolean
	 */
	public ReturnValue Friendlike(String user_id) {

		String m_PostURL = Constant.BASE_URL + "friend/like";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("app_key", Constant.APPKEY));
		nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
		return ReturnClassValue(OnHttpPost(m_PostURL, nameValuePairs));
	}

	/*
	 * 功能说明：
	 * 
	 * 取消关注用户，你可以批量取消关注多个/一个用户 接口形式：
	 * 
	 * friend/destory 请求方式：
	 * 
	 * POST 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 friend_ids required string
	 * 你希望取消关注用户的id，每个用户的id以,分开。比如11,12,14 返回值：
	 * 
	 * boolean
	 */
	public ReturnValue FriendDestory(String friend_ids) {

		String m_PostURL = Constant.BASE_URL + "friend/destory";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("app_key", Constant.APPKEY));
		nameValuePairs.add(new BasicNameValuePair("friend_ids", friend_ids));
		return ReturnClassValue(OnHttpPost(m_PostURL, nameValuePairs));
	}

	/*
	 * 功能说明：
	 * 
	 * 更新用户的头像地址 接口形式：
	 * 
	 * user/updatePicUrl 请求方式：
	 * 
	 * POST 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 url required URL 用户头像地址(http)
	 * 返回值：
	 */
	public boolean UserUpdatePicUrl(String url) {

		String m_PostURL = Constant.BASE_URL + "user/updatePicUrl";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("app_key", Constant.APPKEY));
		nameValuePairs.add(new BasicNameValuePair("url", url));
		return ReturnTorF(OnHttpPost(m_PostURL, nameValuePairs));
	}

	/*
	 * 更新用户背景图片New Page Edit Page Page History 功能说明：
	 * 
	 * 更新用户的头像地址 接口形式：
	 * 
	 * user/updateBGPicUrl 请求方式：
	 * 
	 * POST 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 url required URL 用户头像地址(http)
	 * 返回值：
	 * 
	 * { res_code: [String], // res code res_desc: [String], // res desc
	 * 
	 * }
	 */
	public ReturnValue UserUpdateBGPUrl(String url) {

		String m_PostURL = Constant.BASE_URL + "user/updateBGPicUrl";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("app_key", Constant.APPKEY));
		nameValuePairs.add(new BasicNameValuePair("url", url));
		return ReturnClassValue(OnHttpPost(m_PostURL, nameValuePairs));
	}

	/*
	 * 
	 * 功能说明： 预生成用户第三方账号用户列表 接口形式：
	 * 
	 * user/preGenThirdpartUsers 请求方式：
	 * 
	 * POST 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 source_ids required string
	 * 第三方账号的好友id，以,分隔 比如123,123 source_type required string 第三方账号类别。
	 * 1:新浪，2：腾讯，3：人人网，4：豆瓣,5:本地通讯录 返回值： true false
	 */
	public ReturnValue UserPreGenThirdPartyUser(String source_ids,
			String source_type) {

		String m_PostURL = Constant.BASE_URL + "user/preGenThirdPartyUsers";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("app_key", Constant.APPKEY));
		nameValuePairs.add(new BasicNameValuePair("source_ids", source_ids));
		nameValuePairs.add(new BasicNameValuePair("source_type", source_type));
		return ReturnClassValue(OnHttpPost(m_PostURL, nameValuePairs));
	}

	/*
	 * * 回复评论New Page Edit Page Page History 功能说明：
	 * 
	 * 回复评论 接口形式：
	 * 
	 * comment/reply 请求方式：
	 * 
	 * POST 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 thread_id required string 评论的id
	 * content required string 回复的内容，最长不能超过140字 返回值：
	 */
	public ReturnValue CommentReply(String thread_id, String content) {

		String m_PostURL = Constant.BASE_URL + "comment/reply";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("app_key", Constant.APPKEY));
		nameValuePairs.add(new BasicNameValuePair("thread_id", thread_id));
		nameValuePairs.add(new BasicNameValuePair("content", content));
		return ReturnClassValue(OnHttpPost(m_PostURL, nameValuePairs));
	}

	/*
	 * 功能说明：
	 * 
	 * 验证第三方账号是否已经绑定到系统，如果绑定，自动登陆系统，目前可以绑定豆瓣，腾讯，新浪，人人网的微博账号 接口形式：
	 * 
	 * account/validateThirdParty 请求方式：
	 * 
	 * POST 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 source_id required string
	 * 第三方账号用户id。 source_type required string 第三方账号类别。 1:新浪，2：腾讯，3：人人网，4：豆瓣 返回值：
	 * 
	 * { res_code: [String], // res code res_desc: [String], // res desc
	 * 
	 * }
	 */
	public ReturnValue AccountValidateThirdParty(String source_id,
			String source_type) {

		String m_PostURL = Constant.BASE_URL + "account/validateThirdParty";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("app_key", Constant.APPKEY));
		nameValuePairs.add(new BasicNameValuePair("source_id", source_id));
		nameValuePairs.add(new BasicNameValuePair("source_type", source_type));
		return ReturnClassValue(OnHttpPost(m_PostURL, nameValuePairs));
	}

	/*
	 * 功能说明：
	 * 
	 * 用户注册。 接口形式：
	 * 
	 * account/register 请求方式：
	 * 
	 * POST 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 username required string
	 * 授权用户的用户名。必须是电子邮件格式 password required string 授权用户的密码。 nickname required
	 * string 授权用户昵称 返回值：
	 * 
	 * { res_code: [String], // res 编码 res_desc: [String], // res 描述
	 * 
	 * }
	 */
	public ReturnValue AccountRegister(String username, String password,
			String nickname) {

		String m_PostURL = Constant.BASE_URL + "account/register";
		ObjectMapper mapper = new ObjectMapper();

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
		nameValuePairs.add(new BasicNameValuePair("app_key", Constant.APPKEY));
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("password", password));
		nameValuePairs.add(new BasicNameValuePair("nickname", nickname));

		try {
			return mapper.readValue(OnHttpPost(m_PostURL, nameValuePairs),
					ReturnValue.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	/*
	 * 发布节目New Page Edit Page Page History 功能说明：
	 * 
	 * 发布节目。没有被发布的节目，你才能发布 接口形式：
	 * 
	 * program/publish 请求方式：
	 * 
	 * POST 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 prod_id required string 节目id 返回值：
	 */
	public ReturnValue ProgramPublish(String prod_id) {

		String m_PostURL = Constant.BASE_URL + "program/publish";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("app_key", Constant.APPKEY));
		nameValuePairs.add(new BasicNameValuePair("prod_id", prod_id));
		return ReturnClassValue(OnHttpPost(m_PostURL, nameValuePairs));
	}

	/*
	 * 功能说明：
	 * 
	 * 喜欢节目 接口形式：
	 * 
	 * program/like 请求方式：
	 * 
	 * POST 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 prod_id required string 节目id 返回值：
	 */
	public ReturnValue ProgramLike(String prod_id) {

		String m_PostURL = Constant.BASE_URL + "program/like";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("app_key", Constant.APPKEY));
		nameValuePairs.add(new BasicNameValuePair("prod_id", prod_id));
		return ReturnClassValue(OnHttpPost(m_PostURL, nameValuePairs));
	}

	/*
	 * 功能说明：
	 * 
	 * 绑定第三方账号，目前可以绑定豆瓣，腾讯，新浪，人人网的微博账号 接口形式：
	 * 
	 * account/bindAccount 请求方式：
	 * 
	 * POST 参数：
	 * 
	 * app_key required string 申请应用时分配的AppKey。 source_id required string
	 * 第三方账号用户id。 source_type required string 第三方账号类别。 1:新浪，2：腾讯，3：人人网，4：豆瓣 返回值：
	 * 
	 * { res_code: [String], // res code res_desc: [String], // res desc
	 * 
	 * }
	 */
	public ReturnValue AccountBindAccount(String source_id, String source_type) {

		String m_PostURL = Constant.BASE_URL + "account/bindAccount";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("app_key", Constant.APPKEY));
		nameValuePairs.add(new BasicNameValuePair("source_id", source_id));
		nameValuePairs.add(new BasicNameValuePair("source_type", source_type));
		return ReturnClassValue(OnHttpPost(m_PostURL, nameValuePairs));
	}

	public boolean ReturnTorF(String mReturn) {
		if (mReturn.length() == 0)
			return false;
		try {
			JSONObject jsonObject = new JSONObject(mReturn);
			if (jsonObject.getString("res_code").trim()
					.equalsIgnoreCase("00000"))
				return true;
			else
				return false;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public ReturnValue ReturnClassValue(String mReturn) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(mReturn, ReturnValue.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void SaveServiceData(String where, String Data) {
		Date now = new Date();
		String m_result = null;
		String m_current_date = new SimpleDateFormat("yyyy-MM-dd").format(now);

		Context m_Context = App.getAppContext();

		m_result = GetServiceData(where);
		if (m_result == null || m_result.trim().indexOf(m_current_date) != 0) {
			SharedPreferences.Editor sharedatab = m_Context
					.getSharedPreferences("ServiceData", 0).edit();
			try {
				sharedatab.putString(m_current_date + "-" + where,
						URLEncoder.encode(Data, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sharedatab.commit();
		}
	}

	public String GetServiceData(String where) {
		Date now = new Date();
		String m_result = null;
		String m_current_date = new SimpleDateFormat("yyyy-MM-dd").format(now);

		Context m_Context = App.getAppContext();
		SharedPreferences sharedata = m_Context.getSharedPreferences(
				"ServiceData", 0);
		m_result = sharedata.getString(m_current_date + "-" + where, null);
		if (m_result != null && m_result.length() > 0)
			m_result = URLDecoder.decode(m_result);
		return m_result;
	}

}
