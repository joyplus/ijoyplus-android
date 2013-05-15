package com.joyplus.widget;

import com.joyplus.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class MyGallery extends HorizontalScrollView {

	private OnItemClickListener itmeClickListener;
	private BaseAdapter adapter;
	private int selectedIndex;
	private LinearLayout layout;
	private Drawable drawable;
	private Drawable drawable_t;
	private View selectView;

	public MyGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		layout = new LinearLayout(context);
		drawable = this.getResources().getDrawable(R.drawable.logo_bg);
		drawable_t = this.getResources().getDrawable(R.drawable.logo_bg_s);
		this.setVerticalScrollBarEnabled(false); //禁用垂直滚动
		this.setHorizontalScrollBarEnabled(false); //禁用水平滚动
		// TODO Auto-generated constructor stub
	}

	public MyGallery(Context context) {
		super(context);
		layout = new LinearLayout(context);
		drawable = this.getResources().getDrawable(R.drawable.logo_bg);
		drawable_t = this.getResources().getDrawable(R.drawable.logo_bg_s);
		this.setVerticalScrollBarEnabled(false); //禁用垂直滚动
		this.setHorizontalScrollBarEnabled(false); //禁用水平滚动
		// TODO Auto-generated constructor stub
	}
	
	public void setAdapter(BaseAdapter adapter){
		removeAllViews();
		layout.removeAllViews();
		this.adapter = adapter;
		if(this.adapter == null){
			return;
		}
		for(int i=0;i<adapter.getCount();i++){  
            View view =adapter.getView(i, null, this);
            final int index = i;
            if(i == 0)
            {
            	selectView = view.findViewById(R.id.galllery_item);
            	selectView.setBackgroundDrawable(drawable);
            }else {
//            	view.setBackgroundColor(Color.TRANSPARENT);
            	view.setBackgroundDrawable(drawable_t);
			}
            view.setOnClickListener(new OnClickListener() {  
                @Override  
                public void onClick(View v) {    
                	if(itmeClickListener!=null){ 
//                		selectView.setBackgroundColor(Color.TRANSPARENT);
                		selectView.setBackgroundDrawable(drawable_t);
                		itmeClickListener.onItemClick(null, v, index, 0);
                		v.findViewById(R.id.galllery_item).setBackgroundDrawable(drawable);
                		selectView = v.findViewById(R.id.galllery_item);
                	}
                }  
            });  
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.addView(view,view.getLayoutParams());  
		}
		this.addView(layout, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		
	}
	
	public void setOnItemClickListener(OnItemClickListener listener){
		this.itmeClickListener = listener;
	}
	
	public BaseAdapter getAdapter(){
		return this.adapter;
	}
	
	public void setSelect(int index)
	{
		selectView.setBackgroundDrawable(drawable_t);
		View v = null;
		v = layout.getChildAt(index);
		selectView = v.findViewById(R.id.galllery_item);
		selectView.setBackgroundDrawable(drawable);
	}
	
//	public void setSelection(int index){
//		this.selectedIndex = index-1; 
//		layout.setVisibility(View.INVISIBLE);
//		if(layout.getChildAt(0)!=null){
//			handler.postDelayed(new Runnable() {
//				
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					layout.scrollTo(layout.getChildAt(0).getWidth()*(selectedIndex+1),0);  
//					layout.setVisibility(View.VISIBLE);
//				}
//			}, 100);
//			if(itmeSelectedListener!=null){
//				itmeSelectedListener.onItemSelected(null, layout.getChildAt((selectedIndex+1)), index, 0);
//        	}
//		}else{
//			selectedIndex = 0;
//		}
//		
//	}
	
	public int getSelectedItemPosition(){
		return (selectedIndex+1);
	}

}
