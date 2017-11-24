/*
Copyright 2010-2013 Michael Shick

This file is part of 'Lock Pattern Generator'.

'Lock Pattern Generator' is free software: you can redistribute it and/or
modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or (at your option)
any later version.

'Lock Pattern Generator' is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details.

You should have received a copy of the GNU General Public License along with
'Lock Pattern Generator'.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.xiaotian.framework.widget.pattern;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

/** 绘制Node [extends Drawable] **/
public class NodeDrawable extends Drawable {
	// Node 状态
	public static final int STATE_UNSELECTED = 0; // 未选中
	public static final int STATE_SELECTED = 1; // 选中
	public static final int STATE_HIGHLIGHTED = 2; // 高亮
	public static final int STATE_CORRECT = 3; // 正确
	public static final int STATE_INCORRECT = 4; // 错误
	public static final int STATE_CUSTOM = 5; // 自定义
	// 默认Node颜色
	public static final int[] DEFAULT_STATE_COLORS = { 0xff999999, 0xff00cc00, 0xff00cccc, 0xff1111ff, 0xffdd1111, 0xff999999 };
	// 绘画的员参数
	public static final int CIRCLE_COUNT = 3;
	public static final int CIRCLE_OUTER = 0;
	public static final int CIRCLE_MIDDLE = 1;
	public static final int CIRCLE_INNER = 2;
	public static final float[] CIRCLE_RATIOS = { 1.0f, 0.9f, 0.33f };
	public static final int[] DEFAULT_CIRCLE_COLORS = { DEFAULT_STATE_COLORS[STATE_UNSELECTED], 0xff000000, 0xffffffff };
	public static final int[] CIRCLE_ORDER = { CIRCLE_OUTER, CIRCLE_MIDDLE, CIRCLE_INNER };

	// For drawing an arrow exit indicator
	float mArrowTipRad, mArrowBaseRad, mArrowHalfBase;

	ShapeDrawable mCircles[];
	Paint mExitPaint;
	Path mExitIndicator;
	float mExitAngle;
	Point mCenter;
	float mDiameter;
	int mState;
	int mCustomColor;

	/**
	 * @param diameter
	 *            直径
	 * @param center
	 *            中心点
	 */
	public NodeDrawable(float diameter, Point center) {
		mCircles = new ShapeDrawable[CIRCLE_COUNT];
		mCenter = center;
		mDiameter = diameter;
		mState = STATE_UNSELECTED;
		mExitAngle = Float.NaN;
		setCustomColor(DEFAULT_STATE_COLORS[STATE_CUSTOM]);

		mExitPaint = new Paint();
		mExitPaint.setStyle(Paint.Style.FILL);// 填充
		mExitPaint.setFlags(Paint.ANTI_ALIAS_FLAG); // 反锯齿边缘

		buildShapes(diameter, center);
	}

	@Override
	public void draw(Canvas canvas) {
		for (int ii = 0; ii < CIRCLE_COUNT; ii++) {
			mCircles[CIRCLE_ORDER[ii]].draw(canvas);
		}
		if (!Float.isNaN(mExitAngle)) {
			canvas.drawPath(mExitIndicator, mExitPaint);
		}
	}

	/** 构造图像[外,中,内] **/
	private void buildShapes(float outerDiameter, Point center) {
		for (int ii = 0; ii < CIRCLE_COUNT; ii++) {
			mCircles[ii] = new ShapeDrawable(new OvalShape());
			Paint circlePaint = mCircles[ii].getPaint(); // 设置画笔
			circlePaint.setColor(DEFAULT_CIRCLE_COLORS[ii]);
			circlePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

			float diameter = outerDiameter * CIRCLE_RATIOS[ii]; // 计算直径
			int offset = (int) (diameter / 2.0f); // 半径

			// 设置Bounds边界[startX,startY,entX,endY]
			mCircles[ii].setBounds(center.x - offset, center.y - offset, center.x + offset, center.y + offset);
		}

		// crunch variables for exit arrows independent of angle
		float middleDiameter = outerDiameter * CIRCLE_RATIOS[CIRCLE_MIDDLE];

		mArrowTipRad = middleDiameter / 2.0f * 0.9f;
		mArrowBaseRad = middleDiameter / 2.0f * 0.6f;
		mArrowHalfBase = middleDiameter / 2.0f * 0.3f;
	}

	//
	// Accessors / mutators
	//

	public void setNodeState(int state) {
		int color = mCustomColor;
		if (state != STATE_CUSTOM) {
			color = DEFAULT_STATE_COLORS[state];
		}
		mCircles[CIRCLE_OUTER].getPaint().setColor(color);
		mExitPaint.setColor(color);
		if (state == STATE_UNSELECTED) {
			setExitAngle(Float.NaN);
		}
		mState = state;
	}

	public int getNodeState() {
		return mState;
	}

	/** 设置指示器角度 **/
	public void setExitAngle(float angle) {
		// construct exit indicator arrow
		if (!Float.isNaN(angle)) {
			float tipX = mCenter.x - ((float) Math.cos(angle)) * mArrowTipRad;
			float tipY = mCenter.y - ((float) Math.sin(angle)) * mArrowTipRad;

			float baseCenterX = mCenter.x - ((float) Math.cos(angle)) * mArrowBaseRad;
			float baseCenterY = mCenter.y - ((float) Math.sin(angle)) * mArrowBaseRad;

			// first base vertex of arrow
			float baseVertAX = baseCenterX - mArrowHalfBase * ((float) Math.cos(angle + Math.PI / 2));
			float baseVertAY = baseCenterY - mArrowHalfBase * ((float) Math.sin(angle + Math.PI / 2));
			// second base vertex of arrow
			float baseVertBX = baseCenterX - mArrowHalfBase * ((float) Math.cos(angle - Math.PI / 2));
			float baseVertBY = baseCenterY - mArrowHalfBase * ((float) Math.sin(angle - Math.PI / 2));

			Path arrow = new Path();
			arrow.moveTo(tipX, tipY);
			arrow.lineTo(baseVertAX, baseVertAY);
			arrow.lineTo(baseVertBX, baseVertBY);
			arrow.lineTo(tipX, tipY);

			mExitIndicator = arrow;
		}
		mExitAngle = angle;
	}

	public float getExitAngle() {
		return mExitAngle;
	}

	public Point getCenter() {
		return mCenter;
	}

	public void setCustomColor(int color) {
		mCustomColor = color;
	}

	public int getCustomColor() {
		return mCustomColor;
	}

	//
	// Required methods for a Drawable, generally just phoning it in to the
	// child drawables
	//

	@Override
	public int getOpacity() {
		return mCircles[CIRCLE_OUTER].getOpacity();
	}

	@Override
	public void setAlpha(int alpha) {
		for (int ii = 0; ii < CIRCLE_COUNT; ii++) {
			mCircles[ii].setAlpha(alpha);
		}
	}

	@Override
	public void setColorFilter(android.graphics.ColorFilter cf) {
		for (int ii = 0; ii < CIRCLE_COUNT; ii++) {
			mCircles[ii].setColorFilter(cf);
		}
	}
}
