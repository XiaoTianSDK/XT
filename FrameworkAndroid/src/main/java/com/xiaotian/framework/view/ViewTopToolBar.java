package com.xiaotian.framework.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaotian.framework.R;
import com.xiaotian.framework.util.UtilLayoutAttribute;

/**
 * @version 1.0.0
 * @author Administrator
 * @name ViewTopToolBar
 * @description
 * @date 2015-6-24
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class ViewTopToolBar extends LinearLayout {

	public ViewTopToolBar(Context context) {
		super(context);
	}

	public ViewTopToolBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		constructContent(context, attrs);
	}

	protected void constructContent(Context context, AttributeSet attrs) {
		ImageButton button;
		TextView textView;
		Drawable drawable;
		String stringValue;
		UtilLayoutAttribute ua = new UtilLayoutAttribute(context, attrs);
		View root = LayoutInflater.from(context).inflate(R.layout.model_view_toptoolbar_xiaotian, this);
		stringValue = ua.getStringAttribute(ua.getNSXiaoTian(), "textTitle");
		if (stringValue != null) {
			textView = ((TextView) root.findViewById(R.id.view_model_toptoolbar_title_xiaotian));
			textView.setText(stringValue);
		}
		drawable = ua.getDrawableAttribute("iconLeft");
		if (drawable != null) {
			button = (ImageButton) root.findViewById(R.id.view_model_toptoolbar_button_left_xiaotian);
			button.setImageDrawable(drawable);
			button.setVisibility(VISIBLE);
		}
		drawable = ua.getDrawableAttribute("iconRight");
		if (drawable != null) {
			button = (ImageButton) root.findViewById(R.id.view_model_toptoolbar_button_right_xiaotian);
			button.setImageDrawable(drawable);
			button.setVisibility(VISIBLE);
		}
		drawable = ua.getDrawableAttribute("background");
		if (drawable != null) {
			root.findViewById(R.id.view_model_toptoolbar_root_xiaotian).setBackground(drawable);
		}
	}

	// 侦听接口
	public interface ViewTopToolBarOnclickEvent {
		public void toolbarBackOnclick(View view);

		public void toolbarTitleOnclick(View view);

		public void toolbarForwardOnclick(View view);
	}
}
