package com.xiaotian.frameworkxt.android.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * @version 1.0.0
 * @author Administrator
 * @name UtilSystem
 * @description System 工具
 * @date 2015-9-1
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class UtilSystem {
	private Context mContext;

	public UtilSystem(Context context) {
		this.mContext = context;
	}

	public void hideSoftKeyboard(Activity activity) {
		View focus = activity.getCurrentFocus();
		if (focus == null) return;
		InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(focus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public void hideSoftKeyboard(View focusedView) {
		if (focusedView == null) return;
		InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
}
