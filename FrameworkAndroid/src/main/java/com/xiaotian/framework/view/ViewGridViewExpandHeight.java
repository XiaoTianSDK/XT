package com.xiaotian.framework.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @description GridView
 * @date 2015/11/21
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class ViewGridViewExpandHeight extends GridView {

    public ViewGridViewExpandHeight(Context context) {
        super(context);
    }

    public ViewGridViewExpandHeight(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST));
    }
}
