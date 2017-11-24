package com.xiaotian.frameworkxt.android.util;

import java.util.Locale;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

import com.xiaotian.frameworkxt.android.util.UtilColor;

/**
 * @version 1.0.0
 * @author Administrator
 * @name UtilRandomVerificationCodeImage
 * @description
 * @date 2015-6-26
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class UtilRandomVerificationCodeImage {
	public static Bitmap createVerificationCode(String code, int width, int height, int backgroundColor) {
		Bitmap save = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(save);
		Paint paint = new Paint();
		paint.setAntiAlias(false);
		paint.setColor(backgroundColor);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setAlpha(255);
		canvas.drawRect(new Rect(0, 0, width, height), paint);
		// 画文本
		paint.setStyle(Style.FILL);
		Random random = new Random();
		code = code.toUpperCase(Locale.getDefault());
		paint.setAlpha(255);
		paint.setTextSkewX(-0.25f);
		paint.setColor(UtilColor.randomColor());
		// 根据高度自动设置Text大小
		adjustTextSize(paint, code, width, height);
		// 设置ScaleX
		int baseLine = adjustTextScale(paint, code, width, height);
		// 居中
		canvas.drawText(code, width / 2 - 10, height - baseLine, paint);
		// 画线
		paint.setStyle(Style.STROKE);
		for (int i = 0; i < 50; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(40) + 10;
			int yl = random.nextInt(40) + 10;
			int color = UtilColor.randomColor();
			paint.setColor(color);
			paint.setStrokeWidth(random.nextInt(8) + 3);
			paint.setAlpha(random.nextInt(155));
			paint.setShadowLayer((float) (random.nextInt(360) / Math.PI), 5 + random.nextInt(5), 5 + random.nextInt(5), UtilColor.randomColor());
			canvas.drawLine(x, y, x + xl, y + yl, paint);
		}
		for (int i = 0; i < 10; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(width);
			int yl = random.nextInt(width);
			paint.setStrokeMiter(23);
			paint.setColor(UtilColor.randomColor());
			paint.setStrokeWidth(random.nextInt(8) + 3);
			paint.setAlpha(random.nextInt(200));
			canvas.drawLine(x, y, x + xl, y + yl, paint);
		}
		return save;
	}

	// 根据容器高度获取匹配字体大小[字体占高度0.7]
	private static void adjustTextSize(Paint textPaint, String text, int viewWidth, int viewHeight) {
		textPaint.setTextSize(100);
		textPaint.setTextScaleX(1.0f);
		Rect bounds = new Rect();
		// ask the paint for the bounding rect if it were to draw this text
		textPaint.getTextBounds(text, 0, text.length(), bounds);
		// get the height that would have been produced
		int h = bounds.bottom - bounds.top;
		// make the text text up 70% of the height
		float target = (float) viewHeight * .7f;
		// figure out what textSize setting would create that height of text
		float size = ((target / h) * 100f);
		// and set it into the paint
		textPaint.setTextSize(size);
	}

	// 根据容器设置X拉伸,并且计算字体基线位置
	private static int adjustTextScale(Paint textPaint, String text, int viewWidth, int viewHeight) {
		// do calculation with scale of 1.0 (no scale)
		textPaint.setTextScaleX(1.0f);
		Rect bounds = new Rect();
		// ask the paint for the bounding rect if it were to draw this text.
		textPaint.getTextBounds(text, 0, text.length(), bounds);
		// determine the width
		int w = bounds.right - bounds.left;
		// calculate the baseline to use so that the entire text is visible including the descenders
		int text_h = bounds.bottom - bounds.top;
		int textBaseLine = bounds.bottom + ((viewHeight - text_h) / 2);
		// determine how much to scale the width to fit the view
		float xscale = (float) (viewWidth / w);
		// set the scale for the text paint
		textPaint.setTextScaleX(xscale);
		return textBaseLine;
	}
}
