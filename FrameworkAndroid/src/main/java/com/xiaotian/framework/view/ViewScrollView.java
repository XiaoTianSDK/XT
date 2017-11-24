package com.xiaotian.framework.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * @version 1.0.0
 * @author Administrator
 * @name ViewScrollView
 * @description
 * @date 2015-6-24
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class ViewScrollView extends ScrollView {
	private ViewScrollViewListener scrollListener;

	public ViewScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (scrollListener != null) {
			scrollListener.scrollCallBack(l, t, oldl, oldt);
		}
	}

	public void setScrollListener(ViewScrollViewListener scrollListener) {
		this.scrollListener = scrollListener;
	}

	/**
	 * @version 1.0.0
	 * @author XiaoTian
	 * @name ViewScrollViewListener
	 * @description 滚动侦听器
	 * @date 2013-10-18
	 * @link gtrstudio@qq.com
	 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
	 */
	public static interface ViewScrollViewListener {
		public void scrollCallBack(int l, int t, int oldl, int oldt);
	}
}
