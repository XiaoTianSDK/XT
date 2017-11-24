package com.xiaotian.frameworkxt.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name ServiceNetworkState
 * @description Service Network State 网络连接状态广播接收,Binded By User
 * @date 2014-4-25
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public abstract class BroadcastReceiverNetworkState extends BroadcastReceiver {
	// intent filter : android.net.conn.CONNECTIVITY_CHANGE
	Object[] initPerometers;
	boolean isRegisted;
	Context context;

	public BroadcastReceiverNetworkState(Context context, Object... initPerometers) {
		this.context = context;
		this.initPerometers = initPerometers;

	}

	public void registerReceiver() {
		if (isRegisted) return;
		context.registerReceiver(this, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
		this.isRegisted = true;
	}

	public void unregisterReceiver() {
		if (isRegisted) {
			context.unregisterReceiver(this);
			this.isRegisted = false;
		}
	}
}
