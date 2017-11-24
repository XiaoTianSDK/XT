package com.xiaotian.framework.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import com.xiaotian.framework.view.listener.ViewScrollViewListener;

/**
 * @version 1.0.0
 * @author Administrator
 * @name ViewHorizontalScrollView
 * @description
 * @date 2015-6-24
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class ViewHorizontalScrollView extends HorizontalScrollView {
	private ViewScrollViewListener scrollListener;
	private boolean isScrollable;

	public ViewHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		isScrollable = true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (scrollListener != null) scrollListener.onTouchEvent(ev);
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// if we can scroll pass the event to the superclass
			if (isScrollable) {
				return super.onTouchEvent(ev);
			} else {
				// only continue to handle the touch event if scrolling
				// enabled
				return isScrollable; // mScrollable is always false at this
										// point
			}
			// 放开手时回调
		case MotionEvent.ACTION_UP:
			if (scrollListener != null) scrollListener.onScrollFinish();
		default:
			return super.onTouchEvent(ev);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// Don't do anything with intercepted touch events if
		// we are not scrollable
		if (!isScrollable) {
			return false;
		} else {
			return super.onInterceptTouchEvent(ev);
		}
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		if (scrollListener != null) {
			scrollListener.onScrollChanged(l, t, oldl, oldt);
		}
	}

	public boolean isScrollable() {
		return isScrollable;
	}

	public void setScrollable(boolean isScrollable) {
		this.isScrollable = isScrollable;
	}

	public void setScrollListener(ViewScrollViewListener scrollListener) {
		this.scrollListener = scrollListener;
	}

}
