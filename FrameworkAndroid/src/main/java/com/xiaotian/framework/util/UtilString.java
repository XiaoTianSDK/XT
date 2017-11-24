package com.xiaotian.framework.util;

import android.content.Context;
import android.content.res.Resources;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name UtilString
 * @description 字符串操作Util
 * @date 2013-10-31
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
public class UtilString extends com.xiaotian.frameworkxt.util.UtilString {
	private Context context;
	private Resources resources;

	public UtilString(Context context) {
		this.context = context;
		this.resources = this.context.getResources();
	}

	public String appendString(int resId, String append) {
		return resources.getString(resId) + append;
	}
}
