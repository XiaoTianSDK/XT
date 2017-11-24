package com.xiaotian.frameworkxt.android.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @name UtilTelephonyManager
 * @description Telephony Manager
 * @date 2014-9-28
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class UtilDevice {
    TelephonyManager mTelephonyManager;
    Context context;

    public UtilDevice(Context context) {
        this.context = context;
        this.mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    // IMEI
    public String getSerialNumber() {
        String deviceId;
        if (mTelephonyManager.getDeviceId() != null) {
            deviceId = mTelephonyManager.getDeviceId();
        } else {
            deviceId = Secure.getString(context.getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        }
        return deviceId;
    }

    // MISE
    public String getSIMSerialNumber() {
        return mTelephonyManager.getSimSerialNumber();
    }

    // GSM Phone Number
    public String getSIMNumber() {
        return mTelephonyManager.getLine1Number();
    }

    // Network Operator Name
    public String getSIMNetworkOperatorName() {
        return mTelephonyManager.getNetworkOperatorName();
    }

    public TelephonyManager getTelephonyManager() {
        return mTelephonyManager;
    }

    public boolean netWorkIsConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED;
    }
}
