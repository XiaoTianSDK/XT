package com.xiaotian.framework.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.xiaotian.framework.R;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name BRToastNetworkInvalid
 * @description 网络不可用提示
 * @date Jan 12, 2015
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class BRToastNetworkInvalid extends BroadcastReceiver {
	public static final String ACTION = "com.xiaotian.framework.service.BRToastNetworkInvalid";

	public static Intent intent() {
		Intent intent = new Intent(ACTION);
		return intent;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, context.getString(R.string.string_network_invalid), Toast.LENGTH_SHORT).show();
	}
}
