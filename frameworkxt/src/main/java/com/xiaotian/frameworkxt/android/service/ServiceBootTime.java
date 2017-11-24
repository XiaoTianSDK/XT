package com.xiaotian.frameworkxt.android.service;

import android.app.IntentService;

/**
 * @version 1.0.0
 * @author mac
 * @name ServiceBootTime
 * @description Star Service When Boot Time
 * @date 2014-10-17
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public abstract class ServiceBootTime extends IntentService {
	// 开机->开机完成发送开机完成广播->接收广播->启动服务
	public static final String DEFAULT_SERVICE_NAME = "com.xiaotian.framework.service.SERVICE_BOOT_TIME";

	public ServiceBootTime() {
		super(DEFAULT_SERVICE_NAME);
	}

	public ServiceBootTime(String name) {
		super(name);
	}
}
