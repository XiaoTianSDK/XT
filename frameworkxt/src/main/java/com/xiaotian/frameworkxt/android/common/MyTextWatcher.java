package com.xiaotian.frameworkxt.android.common;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class MyTextWatcher<T> implements TextWatcher {
	T[] initParams;

	public MyTextWatcher(T... initParams) {
		this.initParams = initParams;
	}

	public T getInitParam(int index) {
		return initParams[index];
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

	@Override
	public void afterTextChanged(Editable s) {}

}
