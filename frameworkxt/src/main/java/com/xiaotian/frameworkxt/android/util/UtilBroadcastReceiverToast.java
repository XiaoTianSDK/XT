package com.xiaotian.frameworkxt.android.util;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.xiaotian.frameworkxt.android.receiver.BroadcastReceiverToast;

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
public class UtilBroadcastReceiverToast {

	public static void sendPublicToast(Context context, String content, int... duration) {
		Intent intent = new Intent(BroadcastReceiverToast.ACTION);
		intent.putExtra(BroadcastReceiverToast.PARAM_CONTENT, content);
		intent.putExtra(BroadcastReceiverToast.PARAM_SHOW_TIME, duration.length > 0 ? duration[0] : Toast.LENGTH_SHORT);
		context.sendBroadcast(intent);
	}

	public static void sendPublicToast(Context context, int content, int... duration) {
		Intent intent = new Intent(BroadcastReceiverToast.ACTION);
		intent.putExtra(BroadcastReceiverToast.PARAM_CONTENT, context.getString(content));
		intent.putExtra(BroadcastReceiverToast.PARAM_SHOW_TIME, duration.length > 0 ? duration[0] : Toast.LENGTH_SHORT);
		context.sendBroadcast(intent);
	}
}
