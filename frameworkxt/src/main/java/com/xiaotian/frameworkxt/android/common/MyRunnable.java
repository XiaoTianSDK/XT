package com.xiaotian.frameworkxt.android.common;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name MyRunnable
 * @description My Runnable
 * @date 2014-6-2
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public abstract class MyRunnable<T extends Object> implements Runnable {
	protected T[] initParams;

	public MyRunnable(T... initParams) {
		this.initParams = initParams;
	}

	public T getInitParams(int index) {
		return initParams[index];
	}
}
