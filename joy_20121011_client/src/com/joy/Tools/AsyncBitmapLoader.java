package com.joy.Tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class AsyncBitmapLoader {
	/**   
     * 内存图片软引用缓冲   
     */
	private HashMap<String, SoftReference<Bitmap>> imageCache = null;    
    public AsyncBitmapLoader(){
        imageCache = new HashMap<String, SoftReference<Bitmap>>();    
    }    
    public Bitmap loadBitmap(final ImageView imageView, final String imageURL, final ImageCallBack imageCallBack){    
        //在内存缓存中，则返回Bitmap对象    
        if(imageCache.containsKey(imageURL)){
            SoftReference<Bitmap> reference = imageCache.get(imageURL);    
            Bitmap bitmap = reference.get();    
            if(bitmap != null){
                return bitmap;    
            }    
        }
        else{
            /**   
             * 加上一个对本地缓存的查找   
             */    
            String bitmapName = imageURL.substring(imageURL.lastIndexOf("/") + 1);    
            File cacheDir = new File("/mnt/sdcard/joy/ijoyplus/");    
            File[] cacheFiles = cacheDir.listFiles();    
            int i = 0;    
            if(null!=cacheFiles){
            	for(; i<cacheFiles.length; i++){    
                    if(bitmapName.equals(cacheFiles[i].getName())){
                        break;    
                    }
                }
                if(i < cacheFiles.length){
                	BitmapFactory.Options opts1 = new BitmapFactory.Options();  
                    opts1.inSampleSize = 1;
                    Bitmap bitmap = BitmapFactory.decodeFile("/mnt/sdcard/joy/ijoyplus/" + bitmapName, opts1);
                    return bitmap;
                }
            }  
        }    
        final Handler handler = new Handler(){    
            @Override    
            public void handleMessage(Message msg){    
                imageCallBack.imageLoad(imageView, (Bitmap)msg.obj);    
            }    
        };    
        //如果不在内存缓存中，也不在本地（被jvm回收掉），则开启线程下载图片
        new Thread(){
            @Override    
            public void run(){
            	InputStream bitmapIs=null;
            	try {
            		bitmapIs = Tools.getStreamFromURL(imageURL);
				} catch (Exception e) {
				}
                Bitmap bitmap = BitmapFactory.decodeStream(bitmapIs);    
//                imageCache.put(imageURL, new SoftReference<Bitmap>(bitmap));    
//                Message msg = handler.obtainMessage(0, bitmap);    
//                handler.sendMessage(msg);    
                File dir = new File("/mnt/sdcard/joy/ijoyplus/");
                if(!dir.exists()){
                    dir.mkdirs();    
                }
                File bitmapFile = new File("/mnt/sdcard/joy/ijoyplus/" +imageURL.substring(imageURL.lastIndexOf("/") + 1));    
                if(!bitmapFile.exists()){    
                    try{    
                        bitmapFile.createNewFile();
                    }catch (IOException e){    
                        e.printStackTrace();    
                    }    
                }    
                FileOutputStream fos;    
                try{    
                    fos = new FileOutputStream(bitmapFile);    
                    bitmap.compress(Bitmap.CompressFormat.PNG,100, fos);
                    fos.close();    
                }catch (FileNotFoundException e){    
                    e.printStackTrace();    
                }catch (IOException e){
                    e.printStackTrace();    
                }
                imageCache.put(imageURL, new SoftReference<Bitmap>(bitmap));    
                Message msg = handler.obtainMessage(0, bitmap);    
                handler.sendMessage(msg); 
            }    
        }.start();    
        return null;    
    }    
    public interface ImageCallBack    
    {    
        public void imageLoad(ImageView imageView, Bitmap bitmap);    
    }    
}
