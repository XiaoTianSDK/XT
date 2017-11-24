package com.xiaotian.framework.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class ViewListViewExpandHeight extends ListView {

	public ViewListViewExpandHeight(Context context) {
		super(context);
	}

	public ViewListViewExpandHeight(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST));
	}
}
