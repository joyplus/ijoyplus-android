package com.joyplus.widget;

import com.joyplus.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class PullToRefreshListView extends ListView implements OnScrollListener {
	// 状态
    private static final int TAP_TO_REFRESH = 1;//点击刷新
    private static final int PULL_TO_REFRESH = 2;  //拉动刷新 
    private static final int RELEASE_TO_REFRESH = 3; //释放刷新
    private static final int REFRESHING = 4;  //正在刷新
    // 当前滑动状态
    private int mCurrentScrollState;
    // 当前刷新状态 
    private int mRefreshState;
    //头视图的高度
    private int mRefreshViewHeight;
    //头视图 原始的top padding 属性值
    private int mRefreshOriginalTopPadding;
    private int mLastMotionY;
    // 监听对listview的滑动动作
    private OnRefreshListener mOnRefreshListener;
    //箭头图片
    private static  int REFRESHICON = R.drawable.goicon;    
    //listview 滚动监听器
    private OnScrollListener mOnScrollListener;
    private LayoutInflater mInflater;
    private RelativeLayout mRefreshView;
    //顶部刷新时出现的控件
    private TextView mRefreshViewText;
    private ImageView mRefreshViewImage;
    private ProgressBar mRefreshViewProgress;
    private TextView mRefreshViewLastUpdated;
    // 箭头动画效果
    //变为向下的箭头
    private RotateAnimation mFlipAnimation;
    //变为逆向的箭头
    private RotateAnimation mReverseFlipAnimation;
    //是否反弹
    private boolean mBounceHack;

    public PullToRefreshListView(Context context) {
        super(context);
        init(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    /** 
     * 初始化控件和箭头动画（这里直接在代码中初始化动画而不是通过xml） 
     */ 

    private void init(Context context) {
        // Load all of the animations we need in code rather than through XML
    	
    	//第一个参数fromDegrees为动画起始时的旋转角度    
    	//第二个参数toDegrees为动画旋转到的角度   
    	//第三个参数pivotXType为动画在X轴相对于物件位置类型  
    	//第四个参数pivotXValue为动画相对于物件的X坐标的开始位置
    	//第五个参数pivotXType为动画在Y轴相对于物件位置类型   
    	//第六个参数pivotYValue为动画相对于物件的Y坐标的开始位置   	
        mFlipAnimation = new RotateAnimation(0, -180,RotateAnimation.RELATIVE_TO_SELF, 
        		0.5f,RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(250);
        mFlipAnimation.setFillAfter(true);
                     
        mReverseFlipAnimation = new RotateAnimation(-180, 0,RotateAnimation.RELATIVE_TO_SELF, 0.5f,RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(250);
        mReverseFlipAnimation.setFillAfter(true);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRefreshView = (RelativeLayout) mInflater.inflate(R.layout.pull_to_refresh_header, this, false);
		mRefreshViewText =(TextView) mRefreshView.findViewById(R.id.pull_to_refresh_text);
        mRefreshViewImage =(ImageView) mRefreshView.findViewById(R.id.pull_to_refresh_image);
        mRefreshViewProgress =(ProgressBar) mRefreshView.findViewById(R.id.pull_to_refresh_progress);
        mRefreshViewLastUpdated =(TextView) mRefreshView.findViewById(R.id.pull_to_refresh_updated_at);

        mRefreshViewImage.setMinimumHeight(50);
        mRefreshView.setOnClickListener(new OnClickRefreshListener());
        mRefreshOriginalTopPadding = mRefreshView.getPaddingTop();
        mRefreshState = TAP_TO_REFRESH;
      //为listview头部增加一个view  
        addHeaderView(mRefreshView);
        super.setOnScrollListener(this);
        measureView(mRefreshView);
        mRefreshViewHeight = mRefreshView.getMeasuredHeight();  
    }

    @Override
    protected void onAttachedToWindow() {
        setSelection(1);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        setSelection(1);
    }

    /**
     * Set the listener that will receive notifications every time the list scrolls.
     * @param l The scroll listener. 
     */
    @Override
    public void setOnScrollListener(AbsListView.OnScrollListener l) {
        mOnScrollListener = l;
    }

    /**
     * Register a callback to be invoked when this list should be refreshed.
     * @param onRefreshListener The callback to run.
     */
    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    /**
     * Set a text to represent when the list was last updated. 
     * @param lastUpdated Last updated at.
     */
    public void setLastUpdated(CharSequence lastUpdated) {
        if (lastUpdated != null) {
            mRefreshViewLastUpdated.setVisibility(View.VISIBLE);
            mRefreshViewLastUpdated.setText(lastUpdated);
        } else {
            mRefreshViewLastUpdated.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	//当前手指的Y值
        final int y = (int) event.getY();
        mBounceHack = false;   
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            	//将垂直滚动条设置为可用状态
                if (!isVerticalScrollBarEnabled()) {
                    setVerticalScrollBarEnabled(true);
                }
                if (getFirstVisiblePosition() == 0 && mRefreshState != REFRESHING) {
                	// 拖动距离达到刷新需要
                    if ((mRefreshView.getBottom() >= mRefreshViewHeight
                            || mRefreshView.getTop() >= 0)
                            && mRefreshState == RELEASE_TO_REFRESH) {  
                    	// 把状态设置为正在刷新
                        // Initiate the refresh
                        mRefreshState = REFRESHING; //将标量设置为，正在刷新
                        // 准备刷新
                        prepareForRefresh();  
                        // 刷新  
                        onRefresh();  
                    } else if (mRefreshView.getBottom() < mRefreshViewHeight
                            || mRefreshView.getTop() <= 0) {
                        // Abort refresh and scroll down below the refresh view
                    	//停止刷新，并且滚动到头部刷新视图的下一个视图
                        resetHeader();
                        setSelection(1); //定位在第二个列表项
                    }
                }
                break;
            case MotionEvent.ACTION_DOWN:
            	// 获得按下y轴位置 
                mLastMotionY = y;  
                break;            
            case MotionEvent.ACTION_MOVE:
            	//更行头视图的toppadding 属性
                applyHeaderPadding(event);
                break;
        }
        return super.onTouchEvent(event);
    }
    // 获得header距离 
    private void applyHeaderPadding(MotionEvent ev) {
    	//获取累积的动作数
        int pointerCount = ev.getHistorySize();
        for (int p = 0; p < pointerCount; p++) {
        	//如果是释放将要刷新状态
            if (mRefreshState == RELEASE_TO_REFRESH) {   
                if (isVerticalFadingEdgeEnabled()) {   
                    setVerticalScrollBarEnabled(false);
                }
                //历史累积的高度
                int historicalY = (int) ev.getHistoricalY(p);
                // Calculate the padding to apply, we divide by 1.7 to
                // simulate a more resistant effect during pull.
                // 计算申请的边距，除以1.7使得拉动效果更好
                int topPadding = (int) (((historicalY - mLastMotionY)- mRefreshViewHeight) / 1.7);
                mRefreshView.setPadding(
                        mRefreshView.getPaddingLeft(),
                        topPadding,
                        mRefreshView.getPaddingRight(),
                        mRefreshView.getPaddingBottom());
            }
        }
    }

    /** 
     * Sets the header padding back to original size.
     * 将head的边距重置为初始的数值 
     */ 
    private void resetHeaderPadding() {
        mRefreshView.setPadding(
                mRefreshView.getPaddingLeft(),
                mRefreshOriginalTopPadding,
                mRefreshView.getPaddingRight(),
                mRefreshView.getPaddingBottom());
    }    
    /** 
     * Resets the header to the original state.
     * 重置header为之前的状态 
     */ 
    private void resetHeader() {
        if (mRefreshState != TAP_TO_REFRESH) {
            mRefreshState = TAP_TO_REFRESH; 
            resetHeaderPadding();
            // 将刷新图标换成箭头 
            // Set refresh view text to the pull label
            mRefreshViewText.setText(R.string.pull_to_refresh_tap_label);
            // Replace refresh drawable with arrow drawable
            // 清除动画 
            mRefreshViewImage.setImageResource(REFRESHICON);
            // Clear the full rotation animation
            mRefreshViewImage.clearAnimation();
            // Hide progress bar and arrow.
            // 隐藏图标和进度条 
            mRefreshViewImage.setVisibility(View.GONE);
            mRefreshViewProgress.setVisibility(View.GONE);
        }
    }
    //测量视图的高度
    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0,0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {  
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);   
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);     
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
        // When the refresh view is completely visible, change the text to say
        // "Release to refresh..." and flip the arrow drawable.
    	// 在refreshview完全可见时，设置文字为松开刷新，同时翻转箭头 
    	//如果是接触滚动状态,并且不是正在刷新的状态
        if (mCurrentScrollState == SCROLL_STATE_TOUCH_SCROLL&& mRefreshState != REFRESHING) {
            if (firstVisibleItem == 0) {  
            	//如果显示出来了第一个列表项，显示刷新图片
                mRefreshViewImage.setVisibility(View.VISIBLE);
                //如果下拉了listiview,则显示上拉刷新动画
                if ((mRefreshView.getBottom() >= mRefreshViewHeight + 20|| mRefreshView.getTop() >= 0)
                        && mRefreshState != RELEASE_TO_REFRESH) { 
                    mRefreshViewText.setText(R.string.pull_to_refresh_release_label);
                    mRefreshViewImage.clearAnimation();
                    mRefreshViewImage.startAnimation(mFlipAnimation);
                    mRefreshState = RELEASE_TO_REFRESH;   
                  //如果下拉距离不够，则回归原来的状态
                } else if (mRefreshView.getBottom() < mRefreshViewHeight + 20
                        && mRefreshState != PULL_TO_REFRESH) {    
                    mRefreshViewText.setText(R.string.pull_to_refresh_pull_label);
                    if (mRefreshState != TAP_TO_REFRESH) {
                        mRefreshViewImage.clearAnimation();
                        mRefreshViewImage.startAnimation(mReverseFlipAnimation);
                    }
                    mRefreshState = PULL_TO_REFRESH;
                }
            } else {   
                mRefreshViewImage.setVisibility(View.GONE);  
                resetHeader();   
            }
          //如果是滚动状态+ 第一个视图已经显示+ 不是刷新状态
        } else if (mCurrentScrollState == SCROLL_STATE_FLING  && firstVisibleItem == 0
                && mRefreshState != REFRESHING) {
            setSelection(1);
            mBounceHack = true;   
        } else if (mBounceHack && mCurrentScrollState == SCROLL_STATE_FLING) {
            setSelection(1);       
        }
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem,visibleItemCount, totalItemCount);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mCurrentScrollState = scrollState;
        if (mCurrentScrollState == SCROLL_STATE_IDLE) {  
            mBounceHack = false;
        }
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }
    
    public void prepareForRefresh() {
        resetHeaderPadding();   // 恢复header的边距 
        mRefreshViewImage.setVisibility(View.GONE);
        // We need this hack, otherwise it will keep the previous drawable.
     // 注意加上，否则仍然显示之前的图片  
        mRefreshViewImage.setImageDrawable(null);
        mRefreshViewProgress.setVisibility(View.VISIBLE);
        // Set refresh view text to the refreshing label
       mRefreshViewText.setText(R.string.pull_to_refresh_refreshing_label);
        mRefreshState = REFRESHING;
    }
    public void onRefresh() {
        if (mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh();
        }
    }
    /**
     * 
     * @param lastUpdated Last updated at.
     */
    /** 
     * Resets the list to a normal state after a refresh.
     * 重置listview为普通的listview
     * @param lastUpdated 
     * Last updated at. 
     */  
    
    public void onRefreshComplete(CharSequence lastUpdated) {
        setLastUpdated(lastUpdated);
        onRefreshComplete(); 
    }
    /** 
     * Resets the list to a normal state after a refresh.
     * 重置listview为普通的listview，
     */
    public void onRefreshComplete() {        
        resetHeader();
        // If refresh view is visible when loading completes, scroll down to
        // the next item.
        if (mRefreshView.getBottom() > 0) {
            invalidateViews();  //重绘视图
            setSelection(1);
        }
    }
    /**
     * Invoked when the refresh view is clicked on. This is mainly used when
     * there's only a few items in the list and it's not possible to drag the
     * list.
     */
    private class OnClickRefreshListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (mRefreshState != REFRESHING) {
                prepareForRefresh();  
                onRefresh(); 
            }
        }
    }
    public interface OnRefreshListener {       
        public void onRefresh();
    }
}