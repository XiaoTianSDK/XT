package com.xiaotian.framework.util;

import android.content.Context;
import android.content.Intent;

import com.xiaotian.framework.service.BRToast;

/**
 * 
 * @version 1.0.0
 * @author mac
 * @name UtilToastBroadcast
 * @description
 * @date Nov 4, 2014
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class UtilToastBroadcast {
	//
	public static void sendPublicToast(Context context, String content, int... duration) {
		Intent intent = new Intent(BRToast.ACTION);
		intent.putExtra(BRToast.PARAM_CONTENT, content);
		if (duration.length > 0) intent.putExtra(BRToast.PARAM_SHOW_TIME, duration[0]);
		context.sendBroadcast(intent);
	}

	public static void sendPublicToast(Context context, int content, int... duration) {
		Intent intent = new Intent(BRToast.ACTION);
		intent.putExtra(BRToast.PARAM_CONTENT, context.getString(content));
		if (duration.length > 0) intent.putExtra(BRToast.PARAM_SHOW_TIME, duration[0]);
		context.sendBroadcast(intent);
	}
}
