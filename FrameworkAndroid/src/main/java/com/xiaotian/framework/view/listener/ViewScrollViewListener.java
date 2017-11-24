package com.xiaotian.framework.view.listener;

import android.view.MotionEvent;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name ViewScrollViewListener
 * @description 滚动侦听器
 * @date 2013-10-18
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
public abstract class ViewScrollViewListener {

	public abstract void onScrollChanged(int l, int t, int oldl, int oldt);

	public void onScrollFinish() {}

	public boolean onTouchEvent(MotionEvent ev) {
		return true;
	}

	public boolean onTouchDown(MotionEvent ev) {
		return true;
	}
}