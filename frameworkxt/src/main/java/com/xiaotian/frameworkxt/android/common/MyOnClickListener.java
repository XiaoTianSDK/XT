package com.xiaotian.frameworkxt.android.common;

import android.view.View.OnClickListener;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name MyRunnable
 * @description My View.OnClickListener
 * @date 2014-6-2
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public abstract class MyOnClickListener<T extends Object> implements OnClickListener {
	protected T[] initParams;

	public MyOnClickListener(T... initParams) {
		this.initParams = initParams;
	}

	public T getInitParams(int index) {
		return initParams[index];
	}
}
