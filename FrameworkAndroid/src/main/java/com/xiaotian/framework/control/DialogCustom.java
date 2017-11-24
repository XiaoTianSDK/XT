package com.xiaotian.framework.control;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

/**
 * 
 * @version 1.0.0
 * @author XiaoTian
 * @name DialogCustom
 * @description Dialog Custom
 * @date 2014-10-17
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class DialogCustom extends Dialog {
	// 页面配置有UI控制,后台不执行任何UI控制
	private int dialogId;

	public DialogCustom(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public DialogCustom(Context context) {
		super(context);
	}

	public int getDialogId() {
		return dialogId;
	}

	public void setDialogId(int dialogId) {
		this.dialogId = dialogId;
	}

	public void setSize(float width, float height, int... units) {
		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
		int unit = TypedValue.COMPLEX_UNIT_DIP;
		if (units.length > 0) {
			unit = units[0];
		}
		DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
		layoutParams.width = (int) TypedValue.applyDimension(unit, width, dm);
		layoutParams.height = (int) TypedValue.applyDimension(unit, height, dm);
		getWindow().setAttributes(layoutParams);
	}

	public void setSize(int width, int height) {
		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
		layoutParams.width = width;
		layoutParams.height = height;
		getWindow().setAttributes(layoutParams);
	}

	public void setWidth(int width) {
		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
		layoutParams.width = width;
		getWindow().setAttributes(layoutParams);
	}

	public void setHeight(int height) {
		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
		layoutParams.height = height;
		getWindow().setAttributes(layoutParams);
	}

	public void setWrapContentHeight() {
		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
		layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		getWindow().setAttributes(layoutParams);
		ViewGroup container = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
		LayoutParams params = container.getLayoutParams();
		params.height = LayoutParams.WRAP_CONTENT;
		container.setLayoutParams(params);
		for (int i = 0; i < container.getChildCount(); i++) {
			View child = container.getChildAt(i);
			params = child.getLayoutParams();
			params.height = LayoutParams.WRAP_CONTENT;
			child.setLayoutParams(params);
		}
	}

}
