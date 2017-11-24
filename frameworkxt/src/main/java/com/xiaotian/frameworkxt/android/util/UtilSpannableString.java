package com.xiaotian.frameworkxt.android.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;

/**
 * 
 * @version 1.0.0
 * @author XiaoTian
 * @name UtilTextFace
 * @description Text 格式化
 * @date Nov 12, 2014
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2009-2014 广州隽永贸易科技 Ltd, All Rights Reserved.
 */
public class UtilSpannableString {
	// 对Text应用Span
	// 直接子类
	// BackgroundColorSpan, ClickableSpan, ForegroundColorSpan, MaskFilterSpan,
	// MetricAffectingSpan, RasterizerSpan, StrikethroughSpan, SuggestionSpan,
	// UnderlineSpan
	// 简介子类
	// AbsoluteSizeSpan, DynamicDrawableSpan, ImageSpan, LocaleSpan,
	// RelativeSizeSpan, ReplacementSpan, ScaleXSpan, StyleSpan, SubscriptSpan,
	// SuperscriptSpan, TextAppearanceSpan, TypefaceSpan, URLSpan

	// 颜色Span
	public SpannableString getColorSpanText(String text) {
		ForegroundColorSpan colorSpan;
		SpannableString spannable = new SpannableString(text);
		int start = 2;
		int length = 3;
		colorSpan = new ForegroundColorSpan(UtilColor.randomColor());
		spannable.setSpan(colorSpan, start, start + length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 设置Span
		return spannable;
	}

	// 颜色Span对params进行设置颜色
	public SpannableString getSpannableStringColor(String pattern, int color, Object... params) {
		int startMatcher = 0;
		ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
		String text = String.format(Locale.CHINA, pattern, params);
		SpannableString spannable = new SpannableString(text);
		for (Object op : params) {
			Pattern p = Pattern.compile(String.valueOf(op));
			Matcher matcher = p.matcher(spannable);
			if (matcher.find(startMatcher)) {
				startMatcher = matcher.start();
				spannable.setSpan(colorSpan, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		return spannable;
	}

	// 图片Span
	
	// 字体Span
	public static String getText(String nullAbleText) {
		return nullAbleText == null ? "" : nullAbleText;
	}

	// ClickAble TextView
	public static abstract class MyClickableSpan<T> extends ClickableSpan {
		// 设置CharSequence时必须设置TextView开启接受活动事件方法:
		// TextView.setMovementMethod(LinkMovementMethod.getInstance());
		T[] params;

		public MyClickableSpan(T... params) {
			this.params = params;
		}

		T getInitParam(int index) {
			return params[index];
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setColor(ds.linkColor); // 设置Text颜色
			ds.setUnderlineText(true); // 设置Text下划线
		}
	}
}
