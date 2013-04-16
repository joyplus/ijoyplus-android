package com.joyplus;

import java.util.ArrayList;
import java.util.List;

import com.androidquery.AQuery;
import com.zxing.activity.CaptureActivity;

import android.app.ActivityGroup;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class Tab2 extends ActivityGroup {
	private static final int Sao_Yi_Sao = 11;

	private ViewPager mPager;//页卡内容
	private List<View> listViews; // Tab页面列表
	private ImageView cursor;// 动画图片
	private TextView t1, t2, t3 ,t4;// 页卡头标
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
    AQuery aq;
    private App app;
    ImageButton Relieve_Binding;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab2);
		app = (App) getApplication();
		aq = new AQuery(this);
		InitImageView();
		InitTextView();
		InitViewPager();
		
		Relieve_Binding = (ImageButton)findViewById(R.id.Binding_Click);
		Relieve_Binding.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it = new Intent(Tab2.this, Relieve_Binding.class);
				startActivity(it);
			}
		});
	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}



	@Override
	protected void onResume() {
		if(app.GetServiceData("Binding_TV_Channal") != null){
			aq.id(R.id.Binding_Click).visible();
		}else{
			aq.id(R.id.Binding_Click).gone();
		}
		super.onResume();
	}



	/**
	 * 初始化动画
	 */
	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.tab2_10_s)
				.getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 4 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// 设置动画初始位置
	}
	
	/**
	 * 初始化头标
	 */
	private void InitTextView() {
		t1 = (TextView) findViewById(R.id.text1);
		t2 = (TextView) findViewById(R.id.text2);
		t3 = (TextView) findViewById(R.id.text3);
		t4 = (TextView) findViewById(R.id.text4);

		t1.setOnClickListener(new MyOnClickListener(0));
		t2.setOnClickListener(new MyOnClickListener(1));
		t3.setOnClickListener(new MyOnClickListener(2));
		t4.setOnClickListener(new MyOnClickListener(3));
	}
	/**
	 * 初始化ViewPager
	 */
	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();

		View view01 = getLocalActivityManager().startActivity("Tab2Page2",
				new Intent(this, Tab2Page2.class)).getDecorView();
		View view02 = getLocalActivityManager().startActivity("Tab2Page1",
				new Intent(this, Tab2Page1.class)).getDecorView();
		View view03 = getLocalActivityManager().startActivity("Tab2Page4",
				new Intent(this, Tab2Page4.class)).getDecorView();
		View view04 = getLocalActivityManager().startActivity("Tab2Page3",
				new Intent(this, Tab2Page3.class)).getDecorView();
		listViews.add(view01);
		listViews.add(view02);
		listViews.add(view03);
		listViews.add(view04);
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		aq.id(R.id.movie_1).visible();
		aq.id(R.id.text1).textColor(0xffEB9924);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}
	/**
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}
	/**
	 * 头标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};
	/**
	 * 页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW +5;// 页卡1 -> 页卡2 偏移量
		int two = one * 2 + 5;// 页卡1 -> 页卡3 偏移量
		int three = one*3;

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				}else if(currIndex == 3){
					animation = new TranslateAnimation(three, 0, 0, 0);
				}
				aq.id(R.id.movie_1).visible();
				aq.id(R.id.tv_1).invisible();
				aq.id(R.id.animation_1).invisible();
				aq.id(R.id.show_1).invisible();
				aq.id(R.id.text1).textColor(0xffEB9924);
				aq.id(R.id.text2).textColor(R.color.grey);
				aq.id(R.id.text3).textColor(R.color.grey);
				aq.id(R.id.text4).textColor(R.color.grey);
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, one, 0, 0);
				}
				
				aq.id(R.id.movie_1).invisible();
				aq.id(R.id.tv_1).visible();
				aq.id(R.id.animation_1).invisible();
				aq.id(R.id.show_1).invisible();
				aq.id(R.id.text1).textColor(R.color.grey);
				aq.id(R.id.text2).textColor(0xffEB9924);
				aq.id(R.id.text3).textColor(R.color.grey);
				aq.id(R.id.text4).textColor(R.color.grey);
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				}else if (currIndex == 3) {
					animation = new TranslateAnimation(three, two, 0, 0);
				}
				aq.id(R.id.movie_1).invisible();
				aq.id(R.id.tv_1).invisible();
				aq.id(R.id.animation_1).visible();
				aq.id(R.id.show_1).invisible();
				aq.id(R.id.text1).textColor(R.color.grey);
				aq.id(R.id.text2).textColor(R.color.grey);
				aq.id(R.id.text3).textColor(0xffEB9924);
				aq.id(R.id.text4).textColor(R.color.grey);
				break;
			case 3:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, three, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, three, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, three, 0, 0);
				}
				aq.id(R.id.movie_1).invisible();
				aq.id(R.id.tv_1).invisible();
				aq.id(R.id.animation_1).invisible();
				aq.id(R.id.show_1).visible();
				aq.id(R.id.text1).textColor(R.color.grey);
				aq.id(R.id.text2).textColor(R.color.grey);
				aq.id(R.id.text3).textColor(R.color.grey);
				aq.id(R.id.text4).textColor(0xffEB9924);
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
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
			intent.putExtra("SaoMiao_result", scanResult);
			startActivity(intent);
		}
	}

}