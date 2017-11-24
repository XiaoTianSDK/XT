package com.xiaotian.framework.common;

import android.content.DialogInterface.OnDismissListener;

public abstract class MyDialogOnDismissListener<T> implements OnDismissListener {
	protected T[] initParams;

	public MyDialogOnDismissListener(T... initParams) {
		this.initParams = initParams;
	}

	public T getInitParams(int index) {
		return initParams[index];
	}
}
