package com.xiaotian.framework.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.media.MediaRecorder;
import android.util.AttributeSet;
import android.view.View;

/**
 * 
 * @version 1.0.0
 * @author mac
 * @name ViewAudioAmplitude
 * @description MediaRecorder Amplitude View
 * @date Nov 8, 2014
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class ViewMediaRecorderAmplitude extends View {
	static final long ANIMATION_INTERVAL = 70; // 刷新周期
	static final float LINE_SPACE = 0f;
	static final float SHADOW_OFFSET = 0.2f;
	//
	Paint mPaint, mShadow;
	float mCurrentAngle;
	MediaRecorder mRecorder;
	int mWidth, mHeight;

	public ViewMediaRecorderAmplitude(Context context) {
		super(context);
		init();
	}

	public ViewMediaRecorderAmplitude(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	void init() {
		mPaint = new Paint(Paint.UNDERLINE_TEXT_FLAG);
		mPaint.setColor(Color.GREEN);
		mPaint.setStyle(Style.STROKE);
		mShadow = new Paint(Paint.UNDERLINE_TEXT_FLAG);
		mShadow.setColor(Color.argb(60, 0, 0, 0));
		mShadow.setStyle(Style.STROKE);
		mCurrentAngle = 0;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mWidth > 0 || mHeight > 0) {
			setMeasuredDimension(mWidth, mHeight);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = getWidth();
		int height = getHeight();
		float pivotX = 0;
		float pivotY = height;
		// 当前值
		int currentAmplitude = 2;
		if (mRecorder != null) currentAmplitude = (int) (mRecorder.getMaxAmplitude() / 22768f * 100f);
		float lineHeight = height / 100f;
		mPaint.setStrokeWidth(lineHeight - LINE_SPACE);
		// 画线
		// Mylog.info(width + "," + height + "," + lineHeight);
		for (int i = 0; i < currentAmplitude; i++) {
			pivotY -= LINE_SPACE + lineHeight;
			canvas.drawLine(pivotX, pivotY, pivotX + SHADOW_OFFSET, pivotY - SHADOW_OFFSET, mShadow);
			canvas.drawLine(pivotX, pivotY, width, pivotY - lineHeight, mPaint);
		}
		//
		if (mRecorder != null) postInvalidateDelayed(ANIMATION_INTERVAL);
	}

	public void setMediaRecorder(MediaRecorder recorder) {
		this.mRecorder = recorder;
		requestLayout();
		invalidate();
	}

	public void setSize(int width, int height) {
		this.mHeight = height;
		this.mWidth = width;
		requestLayout();
		invalidate();
	}
}
