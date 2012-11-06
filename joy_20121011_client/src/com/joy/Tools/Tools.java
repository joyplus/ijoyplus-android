package com.joy.Tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ImageView;

public class Tools {
	public Tools(){
		
	}
	public static void changeLight(ImageView imageView, int brightness) {
        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[] { 1, 0, 0, 0, brightness, 0, 1, 0, 0,
                        brightness,// 改变亮度
                        0, 0, 1, 0, brightness, 0, 0, 0, 1, 0 });
        imageView.setColorFilter(new ColorMatrixColorFilter(cMatrix));
}
	public static Bitmap drawableToBitamp(Drawable drawable){
		BitmapDrawable bd = (BitmapDrawable) drawable;
		return bd.getBitmap();
	}
	public static Drawable BitampTodrawable(Bitmap bitmap){
		BitmapDrawable bd=new BitmapDrawable(bitmap);
		return bd;
	}
	public static boolean isNetworkAvailable(Context context) {   
        ConnectivityManager cm = (ConnectivityManager) context   
                .getSystemService(Context.CONNECTIVITY_SERVICE);   
        if (cm == null) {   
        } else {
        	//如果仅仅是用来判断网络连接 ,则可以使用 cm.getActiveNetworkInfo().isAvailable();
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {   
                for (int i = 0; i < info.length; i++) {   
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {   
                        return true;   
                    }   
                }   
            }   
        }   
        return false;   
    }
	
	public static InputStream getStreamFromURL(String imageURL) {  
        InputStream in=null;  
        try {  
            URL url=new URL(imageURL);
            HttpURLConnection connection=(HttpURLConnection) url.openConnection();  
            in=connection.getInputStream();  
              
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        return in;  
          
    }  
	public static boolean isSimExist(Context context){	//是否插卡
		TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if(mTelephonyManager.getSimState()==TelephonyManager.SIM_STATE_READY){
			return true;
		}
		return false;
	}
	public static List<String> readFileList(String imgPath,String endwith){  
		List<String> fileList = new ArrayList<String>();  
		File fileDir = new File(imgPath);  
		File[] files = fileDir.listFiles();
        if(files!=null){
            for(File file:files){  
            	//fileList.add(file.getPath());  
                String fileName = file.getName();  
                if (fileName.endsWith(endwith)){  
                	fileList.add(file.getPath()); 
                }  
            }  
        }
        return fileList;  
    }
	public static int isspecial(String str){
		int t=0;
		for(int i=0;i<str.length();i++){
			char ch = str.charAt(i);
			if(ch=='|'||ch=='~'||ch=='^'||ch=='#'||ch==';'||ch=='*'||ch=='%'||ch=='"'){
				t=1;
				break;
			}
		}
		return t;
	}
	public static boolean isChineseCharacter_f2(String s) 
    { 
            int len=s.length(); 
            for(int i=0;i<len;i++) 
            { 
                 char ch=s.charAt(i); 
                    if(!(((ch>='a')&&(ch <='z'))||((ch>='A')&&(ch<='Z'))||((ch>='0')&&(ch<='9'))||(ch=='/')||
                    		ch=='|'||ch=='~'||ch=='^'||ch=='#'||ch==';'||ch=='*'||ch=='%'||ch=='"'||ch=='_')) 
                    { 
                            return true; 
                    }  
            } 
            return false; 
    }
	public static int isOk(String str) {
  		int ok=0;
  		if(str.charAt(0)==47)//接受的STRING为NULL或�??�长度为0
  		{
  			ok=0;
  		}
  		else if((str.charAt(0)>=65&&str.charAt(0)<=90)||(str.charAt(0)>=97&&str.charAt(0)<=122))//接受的STRING以字母开�??
		{
			ok=1;
		}
		else//其他
		{
			ok=2;
		}
		return ok;
       } 
	public static byte[] readStream(InputStream inStream) throws Exception{  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024];  
        int len = 0;  
        while( (len=inStream.read(buffer)) != -1){  
            outStream.write(buffer, 0, len);  
        }  
        outStream.close();  
        inStream.close();  
        return outStream.toByteArray();  
    }
	 public static byte[] getFileToByte(File file) { 
	    	byte[] by = new byte[(int) file.length()]; 
	    	try { 
	    	InputStream is = new FileInputStream(file); 
	    	ByteArrayOutputStream bytestream = new ByteArrayOutputStream(); 
	    	byte[] bb = new byte[2048]; 
	    	int ch; 
	    	ch = is.read(bb); 
	    	while (ch != -1) { 
	    	bytestream.write(bb, 0, ch); 
	    	ch = is.read(bb); 
	    	} 
	    	by = bytestream.toByteArray(); 
	    	} catch (Exception ex) { 
	    	ex.printStackTrace(); 
	    	} 

	    	return by; 
	    	}
	public static String replace(String from, String to, String source){   
        if(source == null || from == null || to == null){   
            return null;   
        }   
        StringBuffer bf = new StringBuffer();   
        int index = -1;   
        while((index = source.indexOf(from)) != -1){   
            bf.append(source.substring(0, index) + to);   
            source = source.substring(index + from.length());   
            index = -1;   
        }   
        bf.append(source);   
        return bf.toString();   
    }
	public static String savecurrentDate() {
    	TimeZone   t   =   TimeZone.getTimeZone("GMT+08:00");
		Calendar calendar = Calendar.getInstance(t);
        int year = calendar.get( Calendar.YEAR );
        int month = calendar.get( Calendar.MONTH ) + 1;
        int day = calendar.get( Calendar.DATE );
        int h=calendar.get(Calendar.HOUR_OF_DAY);
        int m=calendar.get(Calendar.MINUTE);
        int mm=calendar.get(Calendar.SECOND);
        String sb = "";
        sb=year+"-"+month+"-"+day+" "+h+":"+m+":"+mm;
        return sb;
    }
	public static String currentDate() {
    	TimeZone   t   =   TimeZone.getTimeZone("GMT+08:00");
		Calendar calendar = Calendar.getInstance(t);
        int year = calendar.get( Calendar.YEAR );
        int month = calendar.get( Calendar.MONTH ) + 1;
        int day = calendar.get( Calendar.DATE );
        String sb = "";
        sb=year+"年"+month+"月"+day+"日";
        return sb;
    }
	public static String McurrentDate() {
    	TimeZone   t   =   TimeZone.getTimeZone("GMT+08:00");
		Calendar calendar = Calendar.getInstance(t);
        int year = calendar.get( Calendar.YEAR );
        int month = calendar.get( Calendar.MONTH ) + 1;
        String sb = "";
        sb=year+"-"+month;
        return sb;
    }
	
	public static void creat(String sdname)
	{
		File sd=Environment.getExternalStorageDirectory(); 
        String path=sd.getPath()+"/"+sdname;  
        File file=new File(path); 
        if(!file.exists()){
        	file.mkdir(); 
        }

	}
	public static void SaveFile(String s,String filename,String sdname)
	 {
	  try 
	  {
	   FileOutputStream outStream = new FileOutputStream(Environment.getExternalStorageDirectory()+sdname+"/"+filename,true);
	   OutputStreamWriter writer = new OutputStreamWriter(outStream,"UTF-8");
	   writer.write(s);
	   writer.write("\n");
	   writer.flush();
	   writer.close();//记得关闭

	   outStream.close();
	  } 
	  catch (Exception e)
	  {
	   Log.e("m", "file write error");
	  } 
	 }
	
	public static void saveMyBitmap(String sdname,String bitName,Bitmap mBitmap){
		  File f = new File(Environment.getExternalStorageDirectory()+"/"+sdname+"/" + bitName );
		  try {
		   f.createNewFile();
		  } catch (IOException e) {
		   // TODO Auto-generated catch block
		 //  DebugMessage.put("在保存图片时出错�??"+e.toString());
		  }
		  FileOutputStream fOut = null;
		  try {
		   fOut = new FileOutputStream(f);
		  } catch (FileNotFoundException e) {
		   e.printStackTrace();
		  }
		  mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
		  try {
		   fOut.flush();
		  } catch (IOException e) {
		   e.printStackTrace();
		  }
		  try {
		   fOut.close();
		  } catch (IOException e) {
		   e.printStackTrace();
		  }
		 }
	public static String[] Bigfo(String t,char big)
	{
		int len = t.length();
		int pos = -1;
//		int widths = 0;
		int lineStart = 0;
		int lineEnd = -1;
		Vector<String> v = new Vector<String>();
		while(++pos<len)
		{
			char c = t.charAt(pos);
			if(c == big)
			{
				lineEnd = pos + 1;
			}
			if(lineEnd > 0)
			{
				int subEnd = lineEnd;
				if(c == big)
					subEnd--;
				if(lineStart < subEnd)
					v.addElement(t.substring(lineStart,subEnd));
				lineStart = lineEnd;
				lineEnd = -1;
//				widths = 0 ;
			}
		}
		if(lineStart < len)
		{
			v.addElement(t.substring(lineStart,len));
		}
		String []ret = new String[v.size()];
		v.copyInto(ret);
		return ret;
	}
	public static boolean notPass(String base,String[] str)
	{
		boolean notpass=false;
		for(int i=0;i<str.length;i++)
		{
			if(isIncludeSubString(base,str[i])==true)
			{
				notpass=true;
				break;
			}
		}
		return notpass;
	}
	public static boolean isIncludeSubString(String str, String subString)
    {
        if( str.indexOf( subString.trim() ) != -1 )
        {
            //System.out.println( "str���subString�ַ�" );
            return true;
        }
        else
        {
           // System.out.println( "str�ﲻ��subString�ַ�" );
            return false;
        }
    }
	public   static   String[]   Split(String   Source,   String   Delimiter)   {   
	     int   iCount,   iPos,   iLength;       
	     boolean   bEnd;   //判断结束的符号是不是分割符号   
	     String   sTemp;   //   
	     String[]   aSplit   =   null,   t   =   null;   //aSplit结果返回     t临时的 

	     sTemp   =   Source;   
	     iCount   =   0;   
	     iLength   =   Delimiter.length();   
	     bEnd=sTemp.endsWith(Delimiter);  

	     for   (;   ;   )   {   
	         iPos   =   sTemp.indexOf(Delimiter);   
	         if   (iPos   <   0)   //直到没有分割的字符串，就退出   
	             break;   
	         else   {  

	             if   (iCount   >   0)   t   =   aSplit;     //第一次，不用拷贝数组  

	             iCount++;   
	             aSplit   =   new   String[iCount];   //新的数组，  

	             if   (iCount   >   1)   {                       //不是第一次，拷贝数组   
	                 for   (int   i   =   0;   i   <   t.length;   i++)   aSplit[i]   =   t[i];   
	             }  

	             aSplit[iCount   -   1]   =   sTemp.substring(0,   iPos);     
	             sTemp   =   sTemp.substring(iPos   +   iLength);             //   取余下的字符串   
	         }   
	     }  

	     if(   (sTemp.length()   >=   0)   ||   bEnd)   {     //   判断最后剩余的   String，如果最后的字符是分割符号   
	         if   (iCount   >   0)   t   =   aSplit;   
	         iCount++;   
	         aSplit   =   new   String[iCount];   
	         if   (iCount   >   1)   {   
	             for   (int   i   =   0;   i   <   t.length;   i++)   aSplit[i]   =   t[i];   
	         }  

	         aSplit[iCount   -   1]   =   sTemp;   
	     }  

	     return   aSplit;   
	}
	 public Bitmap getImage(String path,Handler mHandler){ 
		 try{
			 	URL url = new URL(path);       
		        HttpURLConnection conn = (HttpURLConnection) url.openConnection();       
		        conn.setConnectTimeout(5 * 1000);       
		        conn.setRequestMethod("GET");       
		        InputStream inStream = conn.getInputStream();       
		        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
		        	if (readStream(inStream) != null) {
		        		return BitmapFactory.decodeByteArray(readStream(inStream), 0, readStream(inStream).length);// 
		        	}
		        	else
		        	{
		        		return null;
		        	}
		        }
		        else
		        {
		        	return null;
		        }
		 }catch(Exception e)
		 {
			 return null;
		 }
	              
	    }
	 public static void ClearBitmap(Bitmap bitmap){
		 if (bitmap!=null&&!bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap=null;
		}
		 System.gc();
	 }
	 public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {  
         
	        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);  
	        Canvas canvas = new Canvas(output);  
	  
	        final int color = 0xff424242;  
	        final Paint paint = new Paint();  
	        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());  
	        final RectF rectF = new RectF(rect);  
	        final float roundPx = pixels;  
	  
	        paint.setAntiAlias(true);  
	        canvas.drawARGB(0, 0, 0, 0);  
	        paint.setColor(color);  
	        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);  
	  
	        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
	        canvas.drawBitmap(bitmap, rect, rect, paint);  
	  
	        return output;  
	    }
	 public static float getWidth(String text)
	 {
		 	Paint mPaint = new Paint();
	        mPaint.setTextSize(16);   
	        float FontSpace = mPaint.getFontSpacing(); 
	        return text.length()*FontSpace;
	 }
}
