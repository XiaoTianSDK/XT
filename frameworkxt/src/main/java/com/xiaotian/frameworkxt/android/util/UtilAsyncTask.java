package com.xiaotian.frameworkxt.android.util;

import android.annotation.TargetApi;
import android.os.AsyncTask;

/**
 * @version 1.0.0
 * @author Administrator
 * @name UtilAsyncTask
 * @description
 * @date 2015-3-20
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class UtilAsyncTask {

	@TargetApi(android.os.Build.VERSION_CODES.HONEYCOMB)
	public static <Params, Progress, Result> void executeAsyncTask(AsyncTask<Params, Progress, Result> task, Params... params) {
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			// android.os.Build.VERSION_CODES.HONEYCOMB
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
		} else {
			task.execute(params);
		}
	}
}
