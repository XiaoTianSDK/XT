package com.xiaotian.frameworkxt.android.common;

import android.view.ViewTreeObserver.OnGlobalLayoutListener;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name MyOnGlobalLayoutListener
 * @description
 * @date Nov 4, 2014
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2009-2014 广州隽永贸易科技 Ltd, All Rights Reserved.
 */
public abstract class MyOnGlobalLayoutListener<T extends Object> implements OnGlobalLayoutListener {
	protected T[] initParams;

	public MyOnGlobalLayoutListener(T... initParams) {
		this.initParams = initParams;
	}

	public T getInitParams(int index) {
		return initParams[index];
	}
}
