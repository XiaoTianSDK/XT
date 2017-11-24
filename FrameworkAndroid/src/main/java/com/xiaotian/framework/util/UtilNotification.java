package com.xiaotian.framework.util;

import android.app.NotificationManager;
import android.content.Context;

/**
 * @version 1.0.0
 * @author Administrator
 * @name UtilNotification
 * @description 后台通知
 * @date 2015-6-17
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class UtilNotification extends com.xiaotian.frameworkxt.android.util.UtilNotification {
	private Context mContext;
	private NotificationManager mNotificationManager;;

	public UtilNotification(Context context) {
		super(context);
		mContext = context;
		mNotificationManager = getNotificationManager();
	}

}
