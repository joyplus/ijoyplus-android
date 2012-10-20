package com.joy.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.joy.R;


public class PullToRefreshView extends LinearLayout{
	private static final String TAG = "PullToRefreshView";
	private static final int PULL_TO_REFRESH = 2;
	private static final int RELEASE_TO_REFRESH = 3;
	private static final int REFRESHING = 4;
	private static final int PULL_UP_STATE = 0;
	private static final int PULL_DOWN_STATE = 1;
	
	private int mLastMotionY;
	
	private boolean mLock;
	
	private View mHeaderView;
	
	private View mFooterView;
	
	private AdapterView<?> mAdapterView;
	
	private ScrollView mScrollView;
	
	private int mHeaderViewHeight;
	
	private int mFooterViewHeight;
	
	private ImageView mHeaderImageView;
	
	private ImageView mFooterImageView;
	
	private TextView mHeaderTextView;
	
	private TextView mFooterTextView;
	
//	private TextView mHeaderUpdateTextView;
	
	// private TextView mFooterUpdateTextView;
	
	private ProgressBar mHeaderProgressBar;
	
	private ProgressBar mFooterProgressBar;
	
	private LayoutInflater mInflater;
	
	private int mHeaderState;
	
	private int mFooterState;
	
	private int mPullState;
	
	private RotateAnimation mFlipAnimation;
	
	private RotateAnimation mReverseFlipAnimation;
	
	private OnFooterRefreshListener mOnFooterRefreshListener;
	
	private OnHeaderRefreshListener mOnHeaderRefreshListener;
	
	private String mLastUpdateTime;

	public PullToRefreshView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PullToRefreshView(Context context) {
		super(context);
		init();
	}

	/**
	 * init
	 * 
	 * @description
	 * @param context
	 * 
	 */
	private void init() {
		// Load all of the animations we need in code rather than through XML
		mFlipAnimation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mFlipAnimation.setInterpolator(new LinearInterpolator());
		mFlipAnimation.setDuration(250);
		mFlipAnimation.setFillAfter(true);
		mReverseFlipAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
		mReverseFlipAnimation.setDuration(250);
		mReverseFlipAnimation.setFillAfter(true);

		mInflater = LayoutInflater.from(getContext());
		addHeaderView();
	}

	private void addHeaderView() {
		// header view
		mHeaderView = mInflater.inflate(R.layout.refresh_header, this, false);

		mHeaderImageView = (ImageView) mHeaderView.findViewById(R.id.pull_to_refresh_image);
		mHeaderTextView = (TextView) mHeaderView.findViewById(R.id.pull_to_refresh_text);
//		mHeaderUpdateTextView = (TextView) mHeaderView.findViewById(R.id.pull_to_refresh_updated_at);
		mHeaderProgressBar = (ProgressBar) mHeaderView.findViewById(R.id.pull_to_refresh_progress);
		// header layout
		measureView(mHeaderView);
		mHeaderViewHeight = mHeaderView.getMeasuredHeight();
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				mHeaderViewHeight);
		
		params.topMargin = -(mHeaderViewHeight);
		// mHeaderView.setLayoutParams(params1);
		addView(mHeaderView, params);

	}

	private void addFooterView() {
		// footer view
		mFooterView = mInflater.inflate(R.layout.refresh_footer, this, false);
		mFooterImageView = (ImageView) mFooterView
				.findViewById(R.id.pull_to_load_image);
		mFooterTextView = (TextView) mFooterView
				.findViewById(R.id.pull_to_load_text);
		mFooterProgressBar = (ProgressBar) mFooterView
				.findViewById(R.id.pull_to_load_progress);
		// footer layout
		measureView(mFooterView);
		mFooterViewHeight = mFooterView.getMeasuredHeight();
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				mFooterViewHeight);
		// int top = getHeight();
		// params.topMargin
		// =getHeight();//鍦ㄨ繖閲実etHeight()==0,浣嗗湪onInterceptTouchEvent()鏂规硶閲実etHeight()宸茬粡鏈夊€间簡,涓嶅啀鏄�0;
		// getHeight()浠€涔堟椂鍊欎細璧嬪€�,绋嶅€欏啀鐮旂┒涓€涓�
		// 鐢变簬鏄嚎鎬у竷灞€鍙互鐩存帴娣诲姞,鍙AdapterView鐨勯珮搴︽槸MATCH_PARENT,閭ｄ箞footer view灏变細琚坊鍔犲埌鏈€鍚�,骞堕殣钘�
		addView(mFooterView, params);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		// footer view 鍦ㄦ娣诲姞淇濊瘉娣诲姞鍒發inearlayout涓殑鏈€鍚�
		addFooterView();
		initContentAdapterView();
	}

	/**
	 * init AdapterView like ListView,GridView and so on;or init ScrollView
	 * 
	 * @description hylin 2012-7-30涓嬪崍8:48:12
	 */
	private void initContentAdapterView() {
		int count = getChildCount();
		if (count < 3) {
			throw new IllegalArgumentException(
					"this layout must contain 3 child views,and AdapterView or ScrollView must in the second position!");
		}
		View view = null;
		for (int i = 0; i < count - 1; ++i) {
			view = getChildAt(i);
			if (view instanceof AdapterView<?>) {
				mAdapterView = (AdapterView<?>) view;
			}
			if (view instanceof ScrollView) {
				// finish later
				mScrollView = (ScrollView) view;
			}
		}
		if (mAdapterView == null && mScrollView == null) {
			throw new IllegalArgumentException(
					"must contain a AdapterView or ScrollView in this layout!");
		}
	}

	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		int y = (int) e.getRawY();
		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 棣栧厛鎷︽埅down浜嬩欢,璁板綍y鍧愭爣
			mLastMotionY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			// deltaY > 0 鏄悜涓嬭繍鍔�,< 0鏄悜涓婅繍鍔�
			int deltaY = y - mLastMotionY;
			if (isRefreshViewScroll(deltaY)) {
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			break;
		}
		return false;
	}

	/*
	 * 濡傛灉鍦╫nInterceptTouchEvent()鏂规硶涓病鏈夋嫤鎴�(鍗硂nInterceptTouchEvent()鏂规硶涓� return
	 * false)鍒欑敱PullToRefreshView 鐨勫瓙View鏉ュ鐞�;鍚﹀垯鐢变笅闈㈢殑鏂规硶鏉ュ鐞�(鍗崇敱PullToRefreshView鑷繁鏉ュ鐞�)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mLock) {
			return true;
		}
		int y = (int) event.getRawY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// onInterceptTouchEvent宸茬粡璁板綍
			// mLastMotionY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			int deltaY = y - mLastMotionY;
			if (mPullState == PULL_DOWN_STATE) {
				// PullToRefreshView鎵ц涓嬫媺
				Log.i(TAG, " pull down!parent view move!");
				headerPrepareToRefresh(deltaY);
				// setHeaderPadding(-mHeaderViewHeight);
			} else if (mPullState == PULL_UP_STATE) {
				// PullToRefreshView鎵ц涓婃媺
				Log.i(TAG, "pull up!parent view move!");
				footerPrepareToRefresh(deltaY);
			}
			mLastMotionY = y;
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			int topMargin = getHeaderTopMargin();
			if (mPullState == PULL_DOWN_STATE) {
				if (topMargin >= 0) {
					// 寮€濮嬪埛鏂�
					headerRefreshing();
				} else {
					// 杩樻病鏈夋墽琛屽埛鏂帮紝閲嶆柊闅愯棌
					setHeaderTopMargin(-mHeaderViewHeight);
				}
			} else if (mPullState == PULL_UP_STATE) {
				if (Math.abs(topMargin) >= mHeaderViewHeight
						+ mFooterViewHeight) {
					// 寮€濮嬫墽琛宖ooter 鍒锋柊
					footerRefreshing();
				} else {
					// 杩樻病鏈夋墽琛屽埛鏂帮紝閲嶆柊闅愯棌
					setHeaderTopMargin(-mHeaderViewHeight);
				}
			}
			break;
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 鏄惁搴旇鍒颁簡鐖禫iew,鍗砅ullToRefreshView婊戝姩
	 * 
	 * @param deltaY
	 *            , deltaY > 0 鏄悜涓嬭繍鍔�,< 0鏄悜涓婅繍鍔�
	 * @return
	 */
	private boolean isRefreshViewScroll(int deltaY) {
		if (mHeaderState == REFRESHING || mFooterState == REFRESHING) {
			return false;
		}
		//瀵逛簬ListView鍜孏ridView
		if (mAdapterView != null) {
			// 瀛恦iew(ListView or GridView)婊戝姩鍒版渶椤剁
			if (deltaY > 0) {

				View child = mAdapterView.getChildAt(0);
				if (child == null) {
					// 濡傛灉mAdapterView涓病鏈夋暟鎹�,涓嶆嫤鎴�
					return false;
				}
				if (mAdapterView.getFirstVisiblePosition() == 0
						&& child.getTop() == 0) {
					mPullState = PULL_DOWN_STATE;
					return true;
				}
				int top = child.getTop();
				int padding = mAdapterView.getPaddingTop();
				if (mAdapterView.getFirstVisiblePosition() == 0
						&& Math.abs(top - padding) <= 8) {//杩欓噷涔嬪墠鐢�3鍙互鍒ゆ柇,浣嗙幇鍦ㄤ笉琛�,杩樻病鎵惧埌鍘熷洜
					mPullState = PULL_DOWN_STATE;
					return true;
				}

			} else if (deltaY < 0) {
				View lastChild = mAdapterView.getChildAt(mAdapterView
						.getChildCount() - 1);
				if (lastChild == null) {
					// 濡傛灉mAdapterView涓病鏈夋暟鎹�,涓嶆嫤鎴�
					return false;
				}
				// 鏈€鍚庝竴涓瓙view鐨凚ottom灏忎簬鐖禫iew鐨勯珮搴﹁鏄巑AdapterView鐨勬暟鎹病鏈夊～婊＄埗view,
				// 绛変簬鐖禫iew鐨勯珮搴﹁鏄巑AdapterView宸茬粡婊戝姩鍒版渶鍚�
				if (lastChild.getBottom() <= getHeight()
						&& mAdapterView.getLastVisiblePosition() == mAdapterView
								.getCount() - 1) {
					mPullState = PULL_UP_STATE;
					return true;
				}
			}
		}
		// 瀵逛簬ScrollView
		if (mScrollView != null) {
			// 瀛恠croll view婊戝姩鍒版渶椤剁
			View child = mScrollView.getChildAt(0);
			if (deltaY > 0 && mScrollView.getScrollY() == 0) {
				mPullState = PULL_DOWN_STATE;
				return true;
			} else if (deltaY < 0
					&& child.getMeasuredHeight() <= getHeight()
							+ mScrollView.getScrollY()) {
				mPullState = PULL_UP_STATE;
				return true;
			}
		}
		return false;
	}

	/**
	 * header 鍑嗗鍒锋柊,鎵嬫寚绉诲姩杩囩▼,杩樻病鏈夐噴鏀�
	 * 
	 * @param deltaY
	 *            ,鎵嬫寚婊戝姩鐨勮窛绂�
	 */
	private void headerPrepareToRefresh(int deltaY) {
		int newTopMargin = changingHeaderViewTopMargin(deltaY);
		// 褰揾eader view鐨則opMargin>=0鏃讹紝璇存槑宸茬粡瀹屽叏鏄剧ず鍑烘潵浜�,淇敼header view 鐨勬彁绀虹姸鎬�
		if (newTopMargin >= 0 && mHeaderState != RELEASE_TO_REFRESH) {
			mHeaderTextView.setText(R.string.pull_to_refresh_release_label);
//			mHeaderUpdateTextView.setVisibility(View.VISIBLE);
			mHeaderImageView.clearAnimation();
			mHeaderImageView.startAnimation(mFlipAnimation);
			mHeaderState = RELEASE_TO_REFRESH;
		} else if (newTopMargin < 0 && newTopMargin > -mHeaderViewHeight) {// 鎷栧姩鏃舵病鏈夐噴鏀�
			mHeaderImageView.clearAnimation();
			mHeaderImageView.startAnimation(mFlipAnimation);
			// mHeaderImageView.
			mHeaderTextView.setText(R.string.pull_to_refresh_pull_label);
			mHeaderState = PULL_TO_REFRESH;
		}
	}

	/**
	 * footer 鍑嗗鍒锋柊,鎵嬫寚绉诲姩杩囩▼,杩樻病鏈夐噴鏀� 绉诲姩footer view楂樺害鍚屾牱鍜岀Щ鍔╤eader view
	 * 楂樺害鏄竴鏍凤紝閮芥槸閫氳繃淇敼header view鐨則opmargin鐨勫€兼潵杈惧埌
	 * 
	 * @param deltaY
	 *            ,鎵嬫寚婊戝姩鐨勮窛绂�
	 */
	private void footerPrepareToRefresh(int deltaY) {
		int newTopMargin = changingHeaderViewTopMargin(deltaY);
		// 濡傛灉header view topMargin 鐨勭粷瀵瑰€煎ぇ浜庢垨绛変簬header + footer 鐨勯珮搴�
		// 璇存槑footer view 瀹屽叏鏄剧ず鍑烘潵浜嗭紝淇敼footer view 鐨勬彁绀虹姸鎬�
		if (Math.abs(newTopMargin) >= (mHeaderViewHeight + mFooterViewHeight)
				&& mFooterState != RELEASE_TO_REFRESH) {
			mFooterTextView
					.setText(R.string.pull_to_refresh_footer_release_label);
			mFooterImageView.clearAnimation();
			mFooterImageView.startAnimation(mFlipAnimation);
			mFooterState = RELEASE_TO_REFRESH;
		} else if (Math.abs(newTopMargin) < (mHeaderViewHeight + mFooterViewHeight)) {
			mFooterImageView.clearAnimation();
			mFooterImageView.startAnimation(mFlipAnimation);
			mFooterTextView.setText(R.string.pull_to_refresh_footer_pull_label);
			mFooterState = PULL_TO_REFRESH;
		}
	}

	/**
	 * 淇敼Header view top margin鐨勫€�
	 * 
	 * @description
	 * @param deltaY
	 * @return hylin 2012-7-31涓嬪崍1:14:31
	 */
	private int changingHeaderViewTopMargin(int deltaY) {
		LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
		float newTopMargin = params.topMargin + deltaY * 0.3f;
		//杩欓噷瀵逛笂鎷夊仛涓€涓嬮檺鍒�,鍥犱负褰撳墠涓婃媺鍚庣劧鍚庝笉閲婃斁鎵嬫寚鐩存帴涓嬫媺,浼氭妸涓嬫媺鍒锋柊缁欒Е鍙戜簡,鎰熻阿缃戝弸yufengzungzhe鐨勬寚鍑�
		//琛ㄧず濡傛灉鏄湪涓婃媺鍚庝竴娈佃窛绂�,鐒跺悗鐩存帴涓嬫媺
		if(deltaY>0&&mPullState == PULL_UP_STATE&&Math.abs(params.topMargin) <= mHeaderViewHeight){
			return params.topMargin;
		}
		//鍚屾牱鍦�,瀵逛笅鎷夊仛涓€涓嬮檺鍒�,閬垮厤鍑虹幇璺熶笂鎷夋搷浣滄椂涓€鏍风殑bug
		if(deltaY<0&&mPullState == PULL_DOWN_STATE&&Math.abs(params.topMargin)>=mHeaderViewHeight){
			return params.topMargin;
		}
		params.topMargin = (int) newTopMargin;
		mHeaderView.setLayoutParams(params);
		invalidate();
		return params.topMargin;
	}

	/**
	 * header refreshing
	 * 
	 * @description hylin 2012-7-31涓婂崍9:10:12
	 */
	private void headerRefreshing() {
		mHeaderState = REFRESHING;
		setHeaderTopMargin(0);
		mHeaderImageView.setVisibility(View.GONE);
		mHeaderImageView.clearAnimation();
		mHeaderImageView.setImageDrawable(null);
		mHeaderProgressBar.setVisibility(View.VISIBLE);
		mHeaderTextView.setText(R.string.pull_to_refresh_refreshing_label);
		if (mOnHeaderRefreshListener != null) {
			mOnHeaderRefreshListener.onHeaderRefresh(this);
		}
	}

	/**
	 * footer refreshing
	 * 
	 * @description hylin 2012-7-31涓婂崍9:09:59
	 */
	private void footerRefreshing() {
		mFooterState = REFRESHING;
		int top = mHeaderViewHeight + mFooterViewHeight;
		setHeaderTopMargin(-top);
		mFooterImageView.setVisibility(View.GONE);
		mFooterImageView.clearAnimation();
		mFooterImageView.setImageDrawable(null);
		mFooterProgressBar.setVisibility(View.VISIBLE);
		mFooterTextView
				.setText(R.string.pull_to_refresh_footer_refreshing_label);
		if (mOnFooterRefreshListener != null) {
			mOnFooterRefreshListener.onFooterRefresh(this);
		}
	}

	/**
	 * 璁剧疆header view 鐨則opMargin鐨勫€�
	 * 
	 * @description
	 * @param topMargin
	 *            锛屼负0鏃讹紝璇存槑header view 鍒氬ソ瀹屽叏鏄剧ず鍑烘潵锛� 涓�-mHeaderViewHeight鏃讹紝璇存槑瀹屽叏闅愯棌浜�
	 *            hylin 2012-7-31涓婂崍11:24:06
	 */
	private void setHeaderTopMargin(int topMargin) {
		LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
		params.topMargin = topMargin;
		mHeaderView.setLayoutParams(params);
		invalidate();
	}

	/**
	 * header view 瀹屾垚鏇存柊鍚庢仮澶嶅垵濮嬬姸鎬�
	 * 
	 * @description hylin 2012-7-31涓婂崍11:54:23
	 */
	public void onHeaderRefreshComplete() {
		setHeaderTopMargin(-mHeaderViewHeight);
		mHeaderImageView.setVisibility(View.VISIBLE);
		mHeaderImageView.setImageResource(R.drawable.ic_pulltorefresh_arrow);
		mHeaderTextView.setText(R.string.pull_to_refresh_pull_label);
		mHeaderProgressBar.setVisibility(View.GONE);
		// mHeaderUpdateTextView.setText("");
		mHeaderState = PULL_TO_REFRESH;
	}

	/**
	 * Resets the list to a normal state after a refresh.
	 * 
	 * @param lastUpdated
	 *            Last updated at.
	 */
	public void onHeaderRefreshComplete(CharSequence lastUpdated) {
		setLastUpdated(lastUpdated);
		onHeaderRefreshComplete();
	}

	/**
	 * footer view 瀹屾垚鏇存柊鍚庢仮澶嶅垵濮嬬姸鎬�
	 */
	public void onFooterRefreshComplete() {
		setHeaderTopMargin(-mHeaderViewHeight);
		mFooterImageView.setVisibility(View.VISIBLE);
		mFooterImageView.setImageResource(R.drawable.ic_pulltorefresh_arrow_up);
		mFooterTextView.setText(R.string.pull_to_refresh_footer_pull_label);
		mFooterProgressBar.setVisibility(View.GONE);
		// mHeaderUpdateTextView.setText("");
		mFooterState = PULL_TO_REFRESH;
	}

	/**
	 * Set a text to represent when the list was last updated.
	 * 
	 * @param lastUpdated
	 *            Last updated at.
	 */
	public void setLastUpdated(CharSequence lastUpdated) {
		if (lastUpdated != null) {
//			mHeaderUpdateTextView.setVisibility(View.VISIBLE);
//			mHeaderUpdateTextView.setText(lastUpdated);
		} else {
//			mHeaderUpdateTextView.setVisibility(View.GONE);
		}
	}

	/**
	 * 鑾峰彇褰撳墠header view 鐨則opMargin
	 * 
	 * @description
	 * @return hylin 2012-7-31涓婂崍11:22:50
	 */
	private int getHeaderTopMargin() {
		LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
		return params.topMargin;
	}

	/**
	 * lock
	 * 
	 * @description hylin 2012-7-27涓嬪崍6:52:25
	 */
	private void lock() {
		mLock = true;
	}

	/**
	 * unlock
	 * 
	 * @description hylin 2012-7-27涓嬪崍6:53:18
	 */
	private void unlock() {
		mLock = false;
	}

	/**
	 * set headerRefreshListener
	 * 
	 * @description
	 * @param headerRefreshListener
	 *            hylin 2012-7-31涓婂崍11:43:58
	 */
	public void setOnHeaderRefreshListener(
			OnHeaderRefreshListener headerRefreshListener) {
		mOnHeaderRefreshListener = headerRefreshListener;
	}

	public void setOnFooterRefreshListener(
			OnFooterRefreshListener footerRefreshListener) {
		mOnFooterRefreshListener = footerRefreshListener;
	}

	/**
	 * Interface definition for a callback to be invoked when list/grid footer
	 * view should be refreshed.
	 */
	public interface OnFooterRefreshListener {
		public void onFooterRefresh(PullToRefreshView view);
	}

	/**
	 * Interface definition for a callback to be invoked when list/grid header
	 * view should be refreshed.
	 */
	public interface OnHeaderRefreshListener {
		public void onHeaderRefresh(PullToRefreshView view);
	}
}
