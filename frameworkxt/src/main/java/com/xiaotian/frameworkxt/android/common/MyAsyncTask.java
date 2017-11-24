package com.xiaotian.frameworkxt.android.common;

import android.os.AsyncTask;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name AsyncTaskNetworkRequest
 * @description Network Request
 * @date Nov 13, 2014
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2009-2014 广州隽永贸易科技 Ltd, All Rights Reserved.
 */
public abstract class MyAsyncTask<T, Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
	private T[] params;

	public MyAsyncTask(T... pagems) {
		this.params = pagems;
	}

	public T getInitParams(int index) {
		return params[index];
	}
}
