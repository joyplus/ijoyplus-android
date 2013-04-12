package com.joyplus;

import java.util.ArrayList;

import org.json.JSONObject;

import com.zxing.activity.CaptureActivity;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class Tab2 extends ActivityGroup {
	private static final int Sao_Yi_Sao = 11;
	private ViewPager viewPager;
	private ArrayList<View> pageViews;

	private PageIndicator mIndicator;

	/***
	 * init view
	 */
	void InItView() {
		pageViews = new ArrayList<View>();
		View view01 = getLocalActivityManager().startActivity("Tab2Page1",
				new Intent(this, Tab2Page2.class)).getDecorView();
		View view02 = getLocalActivityManager().startActivity("Tab2Page2",
				new Intent(this, Tab2Page1.class)).getDecorView();
		View view03 = getLocalActivityManager().startActivity("Tab2Page3",
				new Intent(this, Tab2Page3.class)).getDecorView();

		pageViews.add(view01);
		pageViews.add(view02);
		pageViews.add(view03);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab2);
		viewPager = (ViewPager) findViewById(R.id.pager);

		InItView();
		viewPager.setAdapter(new myPagerView());
		viewPager.clearAnimation();

		mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(viewPager);

	}

	class myPagerView extends PagerAdapter {
		// ��ʾ��Ŀ
		@Override
		public int getCount() {
			return pageViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			// TODO Auto-generated method stub
			((ViewPager) arg0).removeView(pageViews.get(arg1));
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(pageViews.get(arg1));
			return pageViews.get(arg1);
		}

	}

	public void OnClickTab1TopLeft(View v) {
		Intent i = new Intent(this, Search.class);
		startActivity(i);
	}

	public void OnClickSaoMiaoTopRight(View v) {
		Intent openCameraIntent = new Intent(Tab2.this,CaptureActivity.class);
		startActivityForResult(openCameraIntent, Sao_Yi_Sao);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//处理扫描结果（在界面上显示）
		if (resultCode == Sao_Yi_Sao) {
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("result"); //扫描结果

			Intent intent = new Intent(this, Before_Binding.class);
			startActivity(intent);
		}
	}

}