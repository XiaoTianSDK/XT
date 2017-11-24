package com.xiaotian.framework.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.daimajia.easing.linear.Linear;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @description
 * @date 2016/8/8
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2016 小天天, All Rights Reserved.
 */
public class ViewLinearLayoutMatchParentHeight extends LinearLayout implements ViewTreeObserver.OnGlobalLayoutListener {

    public ViewLinearLayoutMatchParentHeight(Context context) {
        super(context);
        bindParent();
    }

    public ViewLinearLayoutMatchParentHeight(Context context, AttributeSet attrs) {
        super(context, attrs);
        bindParent();
    }

    @TargetApi(11)
    public ViewLinearLayoutMatchParentHeight(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bindParent();
    }

    @TargetApi(21)
    public ViewLinearLayoutMatchParentHeight(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        bindParent();
    }

    void bindParent() {
        ViewParent viewParent = getParent();
        if (viewParent instanceof View) {
            View parent = (View) viewParent;
            parent.getViewTreeObserver().addOnGlobalLayoutListener(this);
        }
    }

    @Override
    public void onGlobalLayout() {
        ViewParent viewParent = getParent();
        if (viewParent instanceof View) {
            View parent = (View) viewParent;
            int ht = getHeight();
            int h = parent.getHeight();
            if (h != ht) {
                ViewGroup.LayoutParams params = getLayoutParams();
                if (params != null) {
                    params.height = h;
                    setLayoutParams(params);
                }
            }
        }
    }
}
