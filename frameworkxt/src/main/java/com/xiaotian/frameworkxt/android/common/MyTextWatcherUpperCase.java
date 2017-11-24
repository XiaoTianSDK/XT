package com.xiaotian.frameworkxt.android.common;

import java.util.Locale;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name MyTextWatcherUpperCase
 * @description 自动转换为大写的TextWatcher
 * @date Nov 13, 2014
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2009-2014 广州隽永贸易科技 Ltd, All Rights Reserved.
 */
public class MyTextWatcherUpperCase implements TextWatcher {
	EditText mEditText;

	public MyTextWatcherUpperCase(EditText editText) {
		this.mEditText = editText;
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void afterTextChanged(Editable editable) {
		String s = editable.toString();
		// 如果不是大写,则转换为大写
		if (!s.equals(s.toUpperCase(Locale.CHINA))) {
			s = s.toUpperCase(Locale.CHINA);
			mEditText.setText(s);
			mEditText.setSelection(mEditText.getText().length());
		}
	}
}
