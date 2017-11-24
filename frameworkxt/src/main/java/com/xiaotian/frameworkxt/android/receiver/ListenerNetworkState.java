package com.xiaotian.frameworkxt.android.receiver;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.xiaotian.frameworkxt.android.common.Mylog;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name ServiceNetworkState
 * @description Net Work State Change Service
 * @date 2014-4-25
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class ListenerNetworkState extends PhoneStateListener {
	Object[] initParameters;
	Context context;

	public ListenerNetworkState(Context context, Object... initParameters) {
		this.initParameters = initParameters;
		this.context = context;
	}

	@Override
	public void onDataConnectionStateChanged(int state, int networkType) {
		super.onDataConnectionStateChanged(state);
		switch (state) {
		case TelephonyManager.DATA_DISCONNECTED:
			Mylog.info("Network State Change To Disconnected Network Type :" + networkType);
			break;
		case TelephonyManager.DATA_CONNECTED:
			Mylog.info("Network State Change To Connection Network Type :" + networkType);
			break;
		}
	}

	public void requestStartListener() {
		// One listener and remove
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(this, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
	}

}
