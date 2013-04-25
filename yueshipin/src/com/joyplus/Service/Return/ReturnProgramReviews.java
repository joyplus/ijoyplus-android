package com.joyplus.Service.Return;

import com.joyplus.Service.Return.ReturnProgramComments.Comments;

/*
 *参数：

app_key required string 申请应用时分配的AppKey。
prod_id required string 节目id
page_num = 需要请求的页码（可选），默认为1
page_size = 每一页包含的记录数（可选），默认为10
返回值：

{
reviews: [
    {
     	review_id: int 影评id
        title: string 影评标题
        comments: string 影评
        douban_review_id: int 豆瓣影评id
        create_date: date 评论时间
    }
    ......
  ]
}
 */
public class ReturnProgramReviews {

	public Reviews[] reviews;

	public static class Reviews {
		public String review_id;		//影评id
		public String title;			//影评标题
		public String comments;			//影评
		public String douban_review_id;	//豆瓣影评id
		public String create_date;		//评论时间
	}

}