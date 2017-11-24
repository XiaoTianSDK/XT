package com.xiaotian.framework.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name ViewPagerTouchable
 * @description enable/disable ViewPager
 * @date 2013-10-30
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
public class ViewPagerTouchable extends ViewPager {
	boolean isScrollable;
	protected ViewScrollViewListener listener;

	public ViewPagerTouchable(Context context) {
		super(context);
		this.isScrollable = true;
	}

	public ViewPagerTouchable(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.isScrollable = true;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		if (listener != null) listener.onScrollChanged(l, t, oldl, oldt);
		super.onScrollChanged(l, t, oldl, oldt);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (isScrollable) {
				return super.onTouchEvent(ev);
			} else {
				return false;
			}
		case MotionEvent.ACTION_UP:
			if (isScrollable && listener != null) {
				listener.onScrollFinish();
			}
		default:
			return super.onTouchEvent(ev);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			return isScrollable ? super.onInterceptTouchEvent(ev) : false;
		} catch (Exception e) {
			return false;
		}
	}

	// Inner Class
	public static interface ViewScrollViewListener {
		public void onScrollChanged(int l, int t, int oldl, int oldt);

		public void onScrollFinish();
	}

	// getter/Setter

	public ViewScrollViewListener getListener() {
		return listener;
	}

	public void setListener(ViewScrollViewListener listener) {
		this.listener = listener;
	}

	public boolean getPageScrollable() {
		return isScrollable;
	}

	public void setPageScrollable(boolean isScrollable) {
		this.isScrollable = isScrollable;
	}
}
