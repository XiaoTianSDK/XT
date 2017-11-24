package com.xiaotian.framework.view;

import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

/**
 * @version 1.0.0
 * @author Administrator
 * @name ViewNumberPicker
 * @description 数字选择器
 * @date 2015-5-22
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class ViewNumberPicker extends NumberPicker {
	public ViewNumberPicker(Context context) {
		super(context);
	}

	public ViewNumberPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ViewNumberPicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setTextColor(int textColor) {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			if (child instanceof EditText) {
				try {
					Field selectorWheelPaintField = NumberPicker.class
							.getDeclaredField("mSelectorWheelPaint");
					selectorWheelPaintField.setAccessible(true);
					((Paint) selectorWheelPaintField.get(this))
							.setColor(textColor);
					((EditText) child).setTextColor(textColor);
					invalidate();
				} catch (NoSuchFieldException e) {
					Log.w("setNumberPickerTextColor", e);
				} catch (IllegalAccessException e) {
					Log.w("setNumberPickerTextColor", e);
				} catch (IllegalArgumentException e) {
					Log.w("setNumberPickerTextColor", e);
				}
			}
		}
	}

	public void setDividerColor(int dividerColor) {
		Field selectionDividerField = null;
		try {
			selectionDividerField = NumberPicker.class
					.getDeclaredField("mSelectionDivider");
			selectionDividerField.setAccessible(true);
			selectionDividerField.set(this, new ColorDrawable(dividerColor));
		} catch (NoSuchFieldException e) {
			Log.w("setNumberPickerDividerColor", e);
		} catch (IllegalAccessException e) {
			Log.w("setNumberPickerDividerColor", e);
		} catch (IllegalArgumentException e) {
			Log.w("setNumberPickerDividerColor", e);
		}
	}
}
