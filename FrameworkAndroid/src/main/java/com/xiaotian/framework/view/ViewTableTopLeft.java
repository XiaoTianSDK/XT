package com.xiaotian.framework.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.xiaotian.framework.util.UtilLayoutAttribute;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name ViewTableTopLeft
 * @description 标签表格的TOP/LEFT 视图View
 * @date 2013-10-24
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
public class ViewTableTopLeft extends View {
	// TODO 本类用于绘画表格表头的分割线的标题View,用本类必须上表头和下表头的文字[中文]个数相同,然后按3:4比例画表
	private MeaSureCallback measure;
	private int borderColor;
	private float borderSize;
	private float borderSizeM;
	// 文字 Padding
	private int leftPaddingBottom;
	private int leftPaddingLeft;
	private int rightPaddingRight;
	private int rightPaddingTop;
	// 画边界的 padding
	private int borderPaddingLeft;
	private int borderPaddingTop;
	private int borderPaddingRight;
	private int borderPaddingBottom;
	private int textSize;
	private int textColor;
	private String textLeft;
	private String textRight;
	// Private
	private Paint paint;
	private Rect rectLeft;
	private Rect rectRight;
	private float[] borders;
	private float hm, wm;
	private float baseLineDemension;

	public ViewTableTopLeft(Context context) {
		super(context);
	}

	public ViewTableTopLeft(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		rectLeft = new Rect();
		rectRight = new Rect();
		// 读取配置
		DisplayMetrics dm = getResources().getDisplayMetrics();
		UtilLayoutAttribute ua = new UtilLayoutAttribute(context, attrs);
		borderColor = ua.getColorAttribute(ua.getNSXiaoTian(), "borderColor", Color.WHITE);
		setBorderSize(ua.getDimension(ua.getNSXiaoTian(), "borderSize", TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, dm)));
		leftPaddingBottom = (int) ua.getDimension(ua.getNSXiaoTian(), "textLeftPaddingBottom", 0);
		leftPaddingLeft = (int) ua.getDimension(ua.getNSXiaoTian(), "textLeftPaddingLeft", 0);
		rightPaddingRight = (int) ua.getDimension(ua.getNSXiaoTian(), "textRightPaddingRight", 0);
		rightPaddingTop = (int) ua.getDimension(ua.getNSXiaoTian(), "textRightPaddingTop", 0);
		textLeft = ua.getStringAttribute("textLeft");
		textRight = ua.getStringAttribute("textRight");
		textSize = (int) ua.getDimension(ua.getNSXiaoTian(), "textSize", TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, dm));
		borderPaddingLeft = (int) ua.getDimension(ua.getNSXiaoTian(), "borderPaddingLeft", 0);
		borderPaddingTop = (int) ua.getDimension(ua.getNSXiaoTian(), "borderPaddingTop", 0);
		borderPaddingRight = (int) ua.getDimension(ua.getNSXiaoTian(), "borderPaddingRight", 0);
		borderPaddingBottom = (int) ua.getDimension(ua.getNSXiaoTian(), "borderPaddingBottom", 0);
		textColor = ua.getColorAttribute(ua.getNSXiaoTian(), "textColor", Color.BLACK);
		// set value
		paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(textSize);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO 确定布局大小
		// 固定长宽比例为4:3
		float width = leftPaddingLeft + rightPaddingRight + borderPaddingLeft + borderPaddingRight;
		float height = rightPaddingTop + leftPaddingBottom + borderPaddingTop + borderPaddingBottom;
		paint.getTextBounds(textLeft, 0, textLeft.length(), rectLeft);
		paint.getTextBounds(textRight, 0, textRight.length(), rectRight);
		baseLineDemension = (paint.ascent() + paint.descent()) / 2;
		if (rectLeft.width() > rectRight.width()) {
			rectRight.set(rectLeft);
		} else {
			rectLeft.set(rectRight);
		}
		hm = (3 * (rectLeft.width() + rectRight.width()) - 4 * (rectLeft.height() + rectRight.height())) / 12;
		wm = 4 * hm / 3;
		height += rectLeft.height() + rectRight.height() + hm;
		width += rectLeft.width() + rectRight.width() - wm;
		setMeasuredDimension((int) width, (int) height);
		if (measure != null) {
			measure.tableTopLeftOnMeaSure((int) width, (int) height);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// Text
		paint.setStyle(Style.FILL);
		paint.setColor(textColor);
		float x = borderPaddingLeft + rectLeft.width() / 2 + leftPaddingLeft;
		float y = (getHeight() - leftPaddingBottom - borderPaddingBottom - rectLeft.height() / 2) - baseLineDemension;
		canvas.drawText(textLeft, x, y, paint);
		x = getWidth() - rightPaddingRight - borderPaddingRight - rectRight.width() / 2;
		y = borderPaddingTop + rightPaddingTop + rectRight.height() / 2 - baseLineDemension;
		canvas.drawText(textRight, x, y, paint);
		// border
		borderSizeM = borderSize / 2.0f;
		if (borders == null) {
			borders = new float[20];
			// Top
			borders[0] = 0;
			borders[1] = borderSizeM;
			borders[2] = getWidth();
			borders[3] = borderSizeM;
			// Right
			borders[4] = getWidth() - borderSizeM;
			borders[5] = 0;
			borders[6] = getWidth() - borderSizeM;
			borders[7] = getHeight();
			// Bottom
			borders[8] = getWidth();
			borders[9] = getHeight() - borderSizeM;
			borders[10] = 0;
			borders[11] = getHeight() - borderSizeM;
			// Left
			borders[12] = borderSizeM;
			borders[13] = getHeight();
			borders[14] = borderSizeM;
			borders[15] = 0;
			// 对角线
			borders[16] = borderSizeM + leftPaddingLeft;
			borders[17] = borderSizeM;
			borders[18] = getWidth() - borderSizeM - rightPaddingRight;
			borders[19] = getHeight() - borderSizeM;
		}
		paint.setColor(borderColor);
		paint.setStrokeWidth(borderSize);
		paint.setStyle(Style.STROKE);
		canvas.drawLines(borders, paint);
	}

	public void setBorderSize(float size) {
		this.borderSize = size;
	}

	public void setText(String left, String right) {
		this.textLeft = left;
		this.textRight = right;
		invalidate();
	}

	public void setOnMeaSureCallback(MeaSureCallback callback) {
		this.measure = callback;
	}

	public interface MeaSureCallback {
		public void tableTopLeftOnMeaSure(int width, int height);
	}
}
