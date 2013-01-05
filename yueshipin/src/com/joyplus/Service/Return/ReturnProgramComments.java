package com.joyplus.Service.Return;


/*
 * {
comments: [
    {
        owner_id: int 鍙戣〃璇勮鐨勭敤鎴穒d
        owner_name: string 鍙戣〃璇勮鐢ㄦ埛鍚�        owner_pic_url: string 鍙戣〃璇勮鐢ㄦ埛鐨勫ご鍍�        id: int 璇勮id
        content: string 璇勮鐨勫唴瀹�        create_date: date 璇勮鏃堕棿
    }
    ......
  ]
}
 */
public class ReturnProgramComments {
	 
		public Comments[] comments;

	    public static class Comments{
	       
	        public String owner_id;
	        public String owner_name;
	        public String content;	        
	    }
	    
}
