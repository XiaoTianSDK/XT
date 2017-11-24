package com.xiaotian.frameworkxt.android.common;

import android.support.v4.view.ViewPager.OnPageChangeListener;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name MyOnPageChangeListener
 * @description
 * @date Nov 29, 2014
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2009-2014 广州隽永贸易科技 Ltd, All Rights Reserved.
 */
public abstract class MyOnPageChangeListener<T> implements OnPageChangeListener {
	private T[] initParams;

	public MyOnPageChangeListener(T... initParams) {
		this.initParams = initParams;
	}

	public T getInitParams(int index) {
		return initParams[index];
	}
}
