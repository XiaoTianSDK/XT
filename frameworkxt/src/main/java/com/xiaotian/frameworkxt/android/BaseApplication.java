package com.xiaotian.frameworkxt.android;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.xiaotian.frameworkxt.android.common.Mylog;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name ApplicationBase
 * @description 崩溃异常捕捉 Application
 * @date 2013-10-17
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
public class BaseApplication extends Application {
	static {
		initializingUncaughtException();
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	public String getDeviceId() {
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

	public String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return capitalize(model);
		} else {
			return capitalize(manufacturer) + " " + model;
		}
	}

	private String capitalize(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	}

	public static void initializingUncaughtException() {
		final Thread.UncaughtExceptionHandler exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable throwable) {
				if (throwable instanceof OutOfMemoryError) {
					Mylog.info("内存溢出!");
				}
				Mylog.info("程序异常退出!");
				exceptionHandler.uncaughtException(thread, throwable);
			}
		});
	}
}
