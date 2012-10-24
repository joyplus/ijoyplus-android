package com.joy.Tools;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;

public class BitmapZoom 
{
	
	/**
	 * 按指定比例压缩
	 * @param srcBitmap
	 * @param percent
	 * @return
	 */
	public static Bitmap bitmapZoomByPercent(Bitmap srcBitmap ,double percent)
	{
		int srcWidth = srcBitmap.getWidth();   
        int srcHeight = srcBitmap.getHeight();    
  
        float scaleWidth = (float) percent;   
        float scaleHeight = (float) percent;   
  
        return bitmapZoomByScale(srcBitmap, scaleWidth, scaleHeight);
	}
	
	/**
	 * 按照指定长宽压缩
	 * @param srcBitmap
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public static Bitmap bitmapZoomBySize(Bitmap srcBitmap,int newWidth,int newHeight)
	{
		int srcWidth = srcBitmap.getWidth();   
        int srcHeight = srcBitmap.getHeight();    
  
        float scaleWidth = ((float) newWidth) / srcWidth;   
        float scaleHeight = ((float) newHeight) / srcHeight;   
  
        return bitmapZoomByScale(srcBitmap, scaleWidth, scaleHeight);
	}
	
	/**
	 * 按照高度的百分比压缩
	 * @param srcBitmap
	 * @param newHeight
	 * @return
	 */
	public static Bitmap bitmapZoomByHeight(Bitmap srcBitmap,int newHeight)
	{
		int srcWidth = srcBitmap.getWidth();   
        int srcHeight = srcBitmap.getHeight();    
     
        float scaleHeight = ((float) newHeight) / srcHeight;   
        float scaleWidth = scaleHeight;
        
        return bitmapZoomByScale(srcBitmap, scaleWidth, scaleHeight);	
	}
	
	/**
	 * 按照宽度的百分比压缩
	 * @param srcBitmap
	 * @param newHeight
	 * @return
	 */
	public static Bitmap bitmapZoomByWidth(Bitmap srcBitmap,int newWidth)
	{
		int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();    
     
        float scaleWidth = ((float) newWidth) / srcWidth;   
        float scaleHeight = scaleWidth;
        
        return bitmapZoomByScale(srcBitmap, scaleWidth, scaleHeight);
	}
	
	/**
	 * 使用长宽缩放比缩放
	 * @param srcBitmap
	 * @param scaleWidth
	 * @param scaleHeight
	 * @return
	 */
	public static Bitmap bitmapZoomByScale(Bitmap srcBitmap,float scaleWidth,float scaleHeight)
	{
		int srcWidth = srcBitmap.getWidth();   
        int srcHeight = srcBitmap.getHeight();  
		Matrix matrix = new Matrix();   
        matrix.postScale(scaleWidth, scaleHeight);     
        Bitmap resizedBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcWidth,   
        		srcHeight, matrix, true);
        
        if(resizedBitmap != null)
        {
        	return resizedBitmap;
        }
        else
        {
        	
        	return srcBitmap;
        }
	}
	
	
}
