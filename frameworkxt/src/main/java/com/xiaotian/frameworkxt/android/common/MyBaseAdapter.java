package com.xiaotian.frameworkxt.android.common;

import java.util.List;

import android.widget.BaseAdapter;

/**
 * 
 * @version 1.0.0
 * @author XiaoTian
 * @name MyBaseAdapter
 * @description
 * @date Nov 5, 2014
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2009-2014 广州隽永贸易科技 Ltd, All Rights Reserved.
 */
public abstract class MyBaseAdapter<DataItem, T> extends BaseAdapter {
	protected T[] initParams;
	protected List<DataItem> listData;

	public MyBaseAdapter(List<DataItem> listData, T... initParams) {
		this.listData = listData;
		this.initParams = initParams;
	}

	public T getInitParams(int index) {
		return initParams[index];
	}

	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}
