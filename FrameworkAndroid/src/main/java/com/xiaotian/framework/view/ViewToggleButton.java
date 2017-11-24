package com.xiaotian.framework.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.xiaotian.framework.util.UtilLayoutAttribute;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @name ViewToggleButton
 * @description 开关按钮, 继承ImageView[系统默认的ToggleButton的高度被拉伸]
 * @date 2013-11-2
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
public class ViewToggleButton extends ImageView {
    private boolean checked;
    private Method methodOnClick;
    private OnClickListener listener;
    private int resChecked, resUnCheck;
    private OnCheckedChangeListener changeListenger;

    public ViewToggleButton(Context context) {
        super(context);
    }

    public ViewToggleButton(final Context context, AttributeSet attrs) {
        super(context, attrs);
        UtilLayoutAttribute ua = new UtilLayoutAttribute(context, attrs);
        String onClick = ua.getStringAttribute(ua.getNSAndroid(), "onClick");
        try {
            if (onClick != null) methodOnClick = context.getClass().getMethod(onClick, View.class);
        } catch (SecurityException e2) {
        } catch (NoSuchMethodException e2) {
        }
        resChecked = ua.getAttributeResourceValue(ua.getNSXiaoTian(), "checkedImage", -1);
        resUnCheck = ua.getAttributeResourceValue(ua.getNSXiaoTian(), "uncheckImage", -1);
        if (ua.getBooleanAttribute("checkable")) {
            setClickable(true);
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewToggleButton b = (ViewToggleButton) v;
                    b.setChecked(!b.isChecked());
                    if (listener != null) listener.onClick(v);
                    if (methodOnClick != null) try {
                        methodOnClick.invoke(context, v);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        setChecked(ua.getBooleanAttribute("checked"));
    }

    /**
     * @return the checked
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * @param checked the checked to set
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
        setImageResource(checked ? resChecked : resUnCheck);
        if (changeListenger != null) {
            changeListenger.onCheckChange(this, this.checked);
        }
    }

    /**
     * @return the resChecked
     */
    public int getResChecked() {
        return resChecked;
    }

    /**
     * @return the resUnCheck
     */
    public int getResUnCheck() {
        return resUnCheck;
    }

    /**
     * @return the listener
     */
    public OnClickListener getListener() {
        return listener;
    }

    /**
     * @param resChecked the resChecked to set
     */
    public void setResChecked(int resChecked) {
        this.resChecked = resChecked;
    }

    /**
     * @param resUnCheck the resUnCheck to set
     */
    public void setResUnCheck(int resUnCheck) {
        this.resUnCheck = resUnCheck;
    }

    /**
     * @param listener the listener to set
     */
    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    public void setOnChangeListener(OnCheckedChangeListener listener) {
        this.changeListenger = listener;
    }

    public static interface OnCheckedChangeListener {
        public void onCheckChange(ViewToggleButton view, boolean checked);
    }
}
