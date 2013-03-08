package com.joyplus;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/*
 * 自己复写ListView的onMeasure&onSizeChanged方法
 * 进行应用推荐的刷新问题
 */
public class appRecommendListView extends ListView {

	public appRecommendListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public appRecommendListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public appRecommendListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		this.setFocusable(false);
	}

}
