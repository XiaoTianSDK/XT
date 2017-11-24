package com.xiaotian.framework.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name ViewListViewScrollable
 * @description 侦听器的ListView,enable/disable Scroll
 * @date 2013-10-30
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
public class ViewListViewScrollable extends ListView {
	protected ViewScrollViewListener listener;
	protected boolean isScrollable;

	public ViewListViewScrollable(Context context) {
		super(context);
		isScrollable = true;
	}

	public ViewListViewScrollable(Context context, AttributeSet attrs) {
		super(context, attrs);
		isScrollable = true;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (listener != null) {
			listener.onScrollChanged(l, t, oldl, oldt);
		}
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
		if (!isScrollable) {
			return false;
		} else {
			return super.onInterceptTouchEvent(ev);
		}
	}

	public void setScrollListener(ViewScrollViewListener listener) {
		this.listener = listener;
	}

	public boolean isScrollable() {
		return isScrollable;
	}

	public void setScrollable(boolean isScrollable) {
		this.isScrollable = isScrollable;
	}

	public void setListener(ViewScrollViewListener listener) {
		this.listener = listener;
	}

	// Inner class
	public static interface ViewScrollViewListener {
		public void onScrollChanged(int l, int t, int oldl, int oldt);

		public void onScrollFinish();
	}
}
