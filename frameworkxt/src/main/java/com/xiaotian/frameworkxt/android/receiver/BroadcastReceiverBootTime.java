package com.xiaotian.frameworkxt.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name BroadcastReceiverBootTime
 * @description Boot Completed Broadcast Receiver,Binded By User
 * @date 2014-10-17
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public abstract class BroadcastReceiverBootTime extends BroadcastReceiver {
	// intent filter : android.intent.action.BOOT_COMPLETED
	Object[] initPerometers;
	boolean isRegisted;
	Context context;

	public BroadcastReceiverBootTime() {}

	public BroadcastReceiverBootTime(Context context, Object... initPerometers) {
		this.context = context;
		this.initPerometers = initPerometers;

	}

	public void registerReceiver() {
		if (isRegisted) return;
		context.registerReceiver(this, new IntentFilter("android.intent.action.BOOT_COMPLETED"));
		this.isRegisted = true;
	}

	public void unregisterReceiver() {
		if (isRegisted) {
			context.unregisterReceiver(this);
			this.isRegisted = false;
		}
	}

}
