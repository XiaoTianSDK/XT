package com.xiaotian.frameworkxt.android.util;

import android.content.Context;
import android.os.Vibrator;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name UtilVibrator
 * @description 震动 服务 Util
 * @date 2013-10-11
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2009-2013 广州睿塔科技 Ltd, All Rights Reserved.
 */
public class UtilVibrator {
	public static final int REPEAT_TYPE_REPEAT = 1;
	public static final int REPEAT_TYPE_UNREPEAT = -1;

	private static UtilVibrator util;
	private Vibrator mVibrator;

	public UtilVibrator(Context context) {
		mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
	}

	public static UtilVibrator getInstance(Context context) {
		synchronized (context) {
			if (util != null) return util;
			return util = new UtilVibrator(context);
		}
	}

	public void vibrate(long milliseconds) {
		// Vibrate n MS
		if (mVibrator != null) mVibrator.vibrate(milliseconds);
	}

	public void vibrate(long[] pattern, int repeat) {
		// Vibrater ([ wait,vibrate,wait,vibrate...],-1:un repeat/1:repeat)
		if (mVibrator != null) mVibrator.vibrate(pattern, repeat);
	}

	public void cencel() {
		if (mVibrator != null) mVibrator.cancel();
	}

	public Vibrator getVibrator() {
		return mVibrator;
	}
}
