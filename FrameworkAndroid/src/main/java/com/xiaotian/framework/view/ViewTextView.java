package com.xiaotian.framework.view;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import com.xiaotian.frameworkxt.android.util.UtilColor;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name ViewTextView
 * @description TextView
 * @date 2014-6-10
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class ViewTextView extends TextView {

	public ViewTextView(Context context) {
		super(context);
	}

	public ViewTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// 对Text应用Span
	// 直接子类
	// BackgroundColorSpan, ClickableSpan, ForegroundColorSpan, MaskFilterSpan,
	// MetricAffectingSpan, RasterizerSpan, StrikethroughSpan, SuggestionSpan,
	// UnderlineSpan
	// 间接子类
	// AbsoluteSizeSpan, DynamicDrawableSpan, ImageSpan, LocaleSpan,
	// RelativeSizeSpan, ReplacementSpan, ScaleXSpan, StyleSpan, SubscriptSpan,
	// SuperscriptSpan, TextAppearanceSpan, TypefaceSpan, URLSpan

	// 颜色Span
	protected CharSequence getColorSpanText(String text) {
		ForegroundColorSpan colorSpan;
		SpannableString spannable = new SpannableString(text);
		int start = 2;
		int length = 3;
		colorSpan = new ForegroundColorSpan(UtilColor.randomColor());
		//		Spanned.SPAN_EXCLUSIVE_EXCLUSIVE --- 不包含两端start和end所在的端点              (a,b)
		//		Spanned.SPAN_EXCLUSIVE_INCLUSIVE --- 不包含端start，但包含end所在的端点       (a,b]
		//		Spanned.SPAN_INCLUSIVE_EXCLUSIVE --- 包含两端start，但不包含end所在的端点   [a,b)
		//		Spanned.SPAN_INCLUSIVE_INCLUSIVE--- 包含两端start和end所在的端点                     [a,b]
		spannable.setSpan(colorSpan, start, start + length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 设置Span
		return spannable;
	}
	// 图片Span

	// 字体Span

}
