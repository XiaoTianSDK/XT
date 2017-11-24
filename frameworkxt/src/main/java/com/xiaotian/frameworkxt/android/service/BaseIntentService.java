package com.xiaotian.frameworkxt.android.service;

import android.app.IntentService;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name BaseIntentService
 * @description Intent Service
 * @date 2014-8-20
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public abstract class BaseIntentService extends IntentService {
	public static final String DEFAULT_SERVICE_NAME = "com.xiaotian.framework.service.BaseIntentService.DEFAULT";

	public BaseIntentService() {
		super(DEFAULT_SERVICE_NAME);
	}

	public BaseIntentService(String name) {
		super(name);
	}
}
