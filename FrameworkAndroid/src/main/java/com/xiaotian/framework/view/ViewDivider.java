package com.xiaotian.framework.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.xiaotian.framework.util.UtilLayoutAttribute;

/**
 * @version 1.0.0
 * @author Administrator
 * @name ViewDivider
 * @description 分隔View(横向/竖向,实线,破折线)
 * @date 2015-4-10
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class ViewDivider extends View {
	static public int ORIENTATION_HORIZONTAL = 0;
	static public int ORIENTATION_VERTICAL = 1;
	private Paint mPaint;
	private int orientation;
	private Path path = new Path();

	public ViewDivider(Context context) {
		this(context, null);
	}

	public ViewDivider(Context context, AttributeSet attrs) {
		super(context, attrs);
		int dashGap, dashLength, dashThickness;
		int color;
		UtilLayoutAttribute ua = new UtilLayoutAttribute(context, attrs);
		dashGap = (int) ua.getDimension(ua.getNSXiaoTian(), "dashGap", 0);// 分隔间隔
		dashLength = (int) ua.getDimension(ua.getNSXiaoTian(), "dashLength", 0);// 线长
		dashThickness = (int) ua.getDimension(ua.getNSXiaoTian(), "dashThickness", 0);// 线厚度
		color = ua.getColorAttribute(ua.getNSXiaoTian(), "color", 0xff000000);// 线颜色
		orientation = ua.getIntegerAttribute(ua.getNSXiaoTian(), "orientation", ORIENTATION_HORIZONTAL);// 方向
		//TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ViewDivider, 0, 0);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(color);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(dashThickness);
		mPaint.setPathEffect(new DashPathEffect(new float[] { dashLength, dashGap }, 0));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (orientation == ORIENTATION_HORIZONTAL) {
			float center = getMeasuredHeight() * .5f;
			path.moveTo(0, center);
			path.lineTo(getMeasuredWidth(), center);
			canvas.drawPath(path, mPaint);
		} else {
			float center = getMeasuredWidth() * .5f;
			path.moveTo(center, 0);
			path.lineTo(center, getMeasuredHeight());
			canvas.drawPath(path, mPaint);
		}
	}
}
