package com.xiaotian.frameworkxt.android.common;

import android.widget.AdapterView.OnItemClickListener;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name MyOnItemClickListener
 * @description
 * @date Nov 5, 2014
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2009-2014 广州隽永贸易科技 Ltd, All Rights Reserved.
 */
public abstract class MyOnItemClickListener<T> implements OnItemClickListener {
	protected T[] initParams;

	public MyOnItemClickListener(T... initParams) {
		this.initParams = initParams;
	}

	public T getInitParams(int index) {
		return initParams[index];
	}
}
