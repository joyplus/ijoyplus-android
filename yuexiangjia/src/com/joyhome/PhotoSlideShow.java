package com.joyhome;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import com.androidquery.AQuery;
import com.joyhome.Adapters.Tab1GridData;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ViewSwitcher.ViewFactory;

public class PhotoSlideShow extends Activity implements
		AdapterView.OnItemSelectedListener, ViewFactory,OnTouchListener{

	private AQuery aq;
//	private ImageSwitcher mSwitcher;
	private Gallery m_gallery;
	private final int THUMBNAIL_SIZE = 64;
	

	private ArrayList<Tab1GridData> images_array = null;
	private ArrayList<Drawable>images_Thumb_array = null;
	private int current_item = 0;
    // 左右滑动时手指按下的X坐标  
    private float touchDownX;  
    // 左右滑动时手指松开的X坐标  
    private float touchUpX;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.photo_slideshow);
		aq = new AQuery(this);

		Intent intent = getIntent();
		current_item = intent.getIntExtra("CURRENT", 0);
		images_array = intent.getParcelableArrayListExtra("IMAGEARRAY");
		CreateThumb_array();
		
		// mSwitcher.postDelayed(new Runnable() {
		// int i = 0;
		// public void run() {
		// mSwitcher.setImageResource(
		// i++ % 2 == 0 ?
		// R.drawable.image1 :
		// R.drawable.mage2);
		// mSwitcher.postDelayed(this, 1000);
		// }
		// }, 1000);
		m_gallery = (Gallery) findViewById(R.id.gallery1);
		m_gallery.setAdapter(new ImageAdapter(this));
		m_gallery.setOnItemSelectedListener(this);
		m_gallery.setSelection(current_item);
	}
	private void CreateThumb_array(){
		if(images_Thumb_array == null)
			images_Thumb_array = new ArrayList<Drawable>();
		for(int i = 0; i<images_array.size();i++){

			try     
	        {
	            FileInputStream fis = new FileInputStream(images_array.get(i)._data);
	            Bitmap imageBitmap = BitmapFactory.decodeStream(fis);

	            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
	            images_Thumb_array.add(new BitmapDrawable(imageBitmap));
//	            imageBitmap.recycle();
	            fis.close();

	        }
	        catch(Exception ex) {

	        }
		}
		//public static Bitmap resizeDownToPixels( 
	}
	@Override
	protected void onDestroy() {
		if (aq != null)
			aq.dismiss();
		super.onDestroy();
	}
	public void onItemSelected(AdapterView parent, View v, int position, long id) {
//			mSwitcher.setImageURI(Uri.parse(images_array.get(position)._data));
		File file = new File(images_array.get(position)._data);
		if (file.exists()) {
			aq.id(R.id.imageView1).image(file, 500);
		}
			String strName = images_array.get(position).title+"    ("+Integer.toString(position+1) + "/" +Integer.toString(images_array.size()) + ")";
			aq.id(R.id.textView1).text(strName);
	}

	public void onNothingSelected(AdapterView parent) {
	}

	 @Override
	public View makeView() {
		ImageView i = new ImageView(this);
		i.setBackgroundColor(0xFFF3F3F3);
		i.setScaleType(ImageView.ScaleType.FIT_CENTER);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		return i;
	}

	public class ImageAdapter extends BaseAdapter {
		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return images_Thumb_array.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);

			i.setImageDrawable(images_Thumb_array.get(position));
			i.setAdjustViewBounds(true);
			i.setLayoutParams(new Gallery.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			i.setBackgroundResource(R.drawable.tab1_moviecard);
			return i;
		}

		private Context mContext;

	}
	 @Override
	 public boolean onTouch(View v, MotionEvent event) {  
	        if (event.getAction() == MotionEvent.ACTION_DOWN) {  
	            // 取得左右滑动时手指按下的X坐标  
	            touchDownX = event.getX();  
	            return true;  
	        } else if (event.getAction() == MotionEvent.ACTION_UP) {  
	            // 取得左右滑动时手指松开的X坐标  
	            touchUpX = event.getX();  
	            // 从左往右，看前一张  
	            if (touchUpX - touchDownX > 100) {  
	                // 取得当前要看的图片的index  
	                current_item = current_item == 0 ? images_array.size() - 1  
	                        : current_item - 1;  
	                // 设置图片切换的动画  
//	                mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,  
//	                        android.R.anim.slide_in_left));  
//	                mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,  
//	                        android.R.anim.slide_out_right));  
//	                // 设置当前要看的图片  
//	                mSwitcher.setImageDrawable(images_Thumb_array.get(current_item));
	                // 从右往左，看下一张  
	            } else if (touchDownX - touchUpX > 100) {  
	                // 取得当前要看的图片的index  
	                current_item = current_item == images_array.size() - 1 ? 0  
	                        : current_item + 1;  
	                // 设置图片切换的动画  
	                // 由于Android没有提供slide_out_left和slide_in_right，所以仿照slide_in_left和slide_out_right编写了slide_out_left和slide_in_right  
//	                mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,  
//	                        R.anim.slide_out_left));  
//	                mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,  
//	                        R.anim.slide_in_right));  
//	                // 设置当前要看的图片  
//	                mSwitcher.setImageDrawable(images_Thumb_array.get(current_item));
	            }  
	            return true;  
	        }  
	        return false;  
	    }  
}