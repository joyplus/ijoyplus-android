package com.joy.Tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.URLEncoder;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.joy.Constant;

public class AsyncBitmapLoader {
	/**
	 * 内存图片软引用缓冲
	 */
	private String PATH = Constant.PATH;
	private HashMap<String, SoftReference<Bitmap>> imageCache = null;

	public AsyncBitmapLoader() {
		imageCache = new HashMap<String, SoftReference<Bitmap>>();
	}

	public Bitmap loadBitmap(final ImageView imageView, final String imageURL,
			final int w, final ImageCallBack imageCallBack) {
		// 在内存缓存中，则返回Bitmap对象
		if (imageCache.containsKey(imageURL)) {
			SoftReference<Bitmap> reference = imageCache.get(imageURL);
			Bitmap bitmap = reference.get();
			if (bitmap != null) {
				return bitmap;
			}
		} else {
			/**
			 * 加上一个对本地缓存的查找
			 */
			// String bitmapName = imageURL.substring(imageURL.lastIndexOf("/")
			// + 1);

			String bitmapName = null;
			try {
				bitmapName = URLEncoder.encode(imageURL, "UTF-8");
				File cacheDir = new File(PATH);
				File[] cacheFiles = cacheDir.listFiles();
				int i = 0;
				if (null != cacheFiles) {
					for (; i < cacheFiles.length; i++) {
						if (bitmapName.equals(cacheFiles[i].getName())) {
							break;
						}
					}
					if (i < cacheFiles.length) {
						BitmapFactory.Options opts1 = new BitmapFactory.Options();
						opts1.inSampleSize = 1;
						/*
						 * if (cacheDir.length()<20480) { opts1.inSampleSize =
						 * 1; System.out.println(1); } else if
						 * (cacheDir.length()<51200) { opts1.inSampleSize = 2;
						 * System.out.println(2); } else if
						 * (cacheDir.length()<307200) { opts1.inSampleSize = 4;
						 * System.out.println(4); } else if
						 * (cacheDir.length()<819200) { opts1.inSampleSize = 6;
						 * System.out.println(6); } else if
						 * (cacheDir.length()<1048576) { opts1.inSampleSize = 8;
						 * System.out.println(8); }else { opts1.inSampleSize =
						 * 10; System.out.println(10); }
						 */
						Bitmap bitmap = BitmapFactory.decodeFile(PATH
								+ bitmapName, opts1);
						return bitmap;
					}
				}
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				imageCallBack.imageLoad(imageView, (Bitmap) msg.obj);
			}
		};
		// 如果不在内存缓存中，也不在本地（被jvm回收掉），则开启线程下载图片
		new Thread() {
			@Override
			public void run() {
				try {
					InputStream bitmapIs = null;
					bitmapIs = Tools.getStreamFromURL(imageURL);
					BitmapFactory.Options opts1 = new BitmapFactory.Options();
					opts1.inSampleSize = 1;
					Bitmap bitmap = BitmapZoom.bitmapZoomByWidth(
							BitmapFactory.decodeStream(bitmapIs, null, opts1),
							w);
					// imageCache.put(imageURL, new
					// SoftReference<Bitmap>(bitmap));
					// Message msg = handler.obtainMessage(0, bitmap);
					// handler.sendMessage(msg);
					File dir = new File(PATH);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					// File bitmapFile = new File(PATH
					// +imageURL.substring(imageURL.lastIndexOf("/") + 1));
					File bitmapFile = new File(PATH
							+ URLEncoder.encode(imageURL, "UTF-8"));
					if (!bitmapFile.exists()) {
						bitmapFile.createNewFile();
					}
					FileOutputStream fos;
					fos = new FileOutputStream(bitmapFile);
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
					fos.close();
					imageCache.put(imageURL, new SoftReference<Bitmap>(bitmap));
					Message msg = handler.obtainMessage(0, bitmap);
					handler.sendMessage(msg);
				} catch (Exception e) {
				}
			}
		}.start();
		return null;
	}

	public interface ImageCallBack {
		public void imageLoad(ImageView imageView, Bitmap bitmap);
	}
}
