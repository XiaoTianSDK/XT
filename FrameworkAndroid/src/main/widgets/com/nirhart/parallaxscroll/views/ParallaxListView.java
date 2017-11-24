package com.nirhart.parallaxscroll.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

/**
 * @version 1.0.0
 * @author Administrator
 * @name ParallaxListView
 * @description ListView 视差
 * @date 2015-6-26
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class ParallaxListView extends ListView {
	private ParallaxListViewHelper helper;

	public ParallaxListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public ParallaxListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	protected void init(Context context, AttributeSet attrs) {
		helper = new ParallaxListViewHelper(context, attrs, this);
		super.setOnScrollListener(helper);
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		helper.setOnScrollListener(l);
	}

	public void addParallaxedHeaderView(View v) {
		super.addHeaderView(v);
		helper.addParallaxedHeaderView(v);
	}

	public void addParallaxedHeaderView(View v, Object data, boolean isSelectable) {
		super.addHeaderView(v, data, isSelectable);
		helper.addParallaxedHeaderView(v, data, isSelectable);
	}
}
