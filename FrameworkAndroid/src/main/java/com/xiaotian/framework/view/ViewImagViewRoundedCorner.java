package com.xiaotian.framework.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.xiaotian.framework.R;

/**
 * @version 1.0.0
 * @author Administrator
 * @name ViewImagViewRoundedCorner
 * @description 圆角ImageImage只适用于BitmapDrawable[XML配置,或者设置BitmapDrawable]
 * @date 2015-3-20
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class ViewImagViewRoundedCorner extends ImageView {
	private int cornerRadio;

	public ViewImagViewRoundedCorner(Context context) {
		super(context);
		initAttribute(context, null, -1);
	}

	public ViewImagViewRoundedCorner(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttribute(context, attrs, -1);
	}

	public ViewImagViewRoundedCorner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttribute(context, attrs, defStyle);
	}

	private void initAttribute(Context context, AttributeSet attrs, int defStyle) {
		if (attrs != null) {
			String XIAOTIAN_NS = getResources().getString(R.string.XIAOTIAN_NS);
			cornerRadio = attrs.getAttributeIntValue(XIAOTIAN_NS, "cornerRadio", 10);
		} else {
			cornerRadio = 10;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		//super.onDraw(canvas);
		Drawable drawable = getDrawable();
		if (drawable != null && drawable instanceof BitmapDrawable && ((BitmapDrawable) drawable).getBitmap() != null) {
			Bitmap b = ((BitmapDrawable) drawable).getBitmap();
			Bitmap bitmap = b.copy(Config.ARGB_8888, true);

			int w = getWidth(), h = getHeight();

			Bitmap roundBitmap = getRoundedCornerBitmap(getContext(), bitmap, cornerRadio, w, h, false, false, false, false);
			canvas.drawBitmap(roundBitmap, 0, 0, null);
		} else {
			super.onDraw(canvas);
		}

	}

	public Bitmap getRoundedCornerBitmap(Context context, Bitmap input, int pixels, int w, int h, boolean squareTL, boolean squareTR, boolean squareBL, boolean squareBR) {
		Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final float densityMultiplier = context.getResources().getDisplayMetrics().density;

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, w, h);
		final RectF rectF = new RectF(rect);

		//make sure that our rounded corner is scaled appropriately
		final float roundPx = pixels * densityMultiplier;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		//draw rectangles over the corners we want to be square
		if (squareTL) {
			canvas.drawRect(0, 0, w / 2, h / 2, paint);
		}
		if (squareTR) {
			canvas.drawRect(w / 2, 0, w, h / 2, paint);
		}
		if (squareBL) {
			canvas.drawRect(0, h / 2, w / 2, h, paint);
		}
		if (squareBR) {
			canvas.drawRect(w / 2, h / 2, w, h, paint);
		}

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(input, 0, 0, paint);

		return output;
	}
}
