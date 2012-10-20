package sqq.ScrollViewPull.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
/**
 * 
 * @author sqq
 *
 */
public class TryPullToRefreshScrollView extends ScrollView {

	public TryPullToRefreshScrollView(Context context) {
		super(context);
		init(context);

	}

	public TryPullToRefreshScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);

	}

	public TryPullToRefreshScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);

	}

	private void init(Context context) {

	}

	
	public static void ScrollToPoint(final View scroll, final View inner,final int i)
	{
		Handler mHandler = new Handler();

		mHandler.post(new Runnable() {
			public void run() {
				if (scroll == null || inner == null) {
					return;
				}

				int offset =inner.getMeasuredHeight() - scroll.getHeight()-i;
				
				if (offset < 0) {
					offset = 0;
				}

				scroll.scrollTo(0, offset);
				
				scroll.invalidate();
			}
		});
	}
	

}