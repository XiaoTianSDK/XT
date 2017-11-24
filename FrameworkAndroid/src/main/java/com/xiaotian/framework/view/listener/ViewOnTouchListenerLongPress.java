package com.xiaotian.framework.view.listener;

import com.xiaotian.frameworkxt.android.util.UtilVibrator;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * @version 1.0.0
 * @author mac
 * @name ViewLongPressListener
 * @description LongPress Listener
 * @date Nov 29, 2014
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public abstract class ViewOnTouchListenerLongPress implements View.OnTouchListener {
	boolean mHasPerformedLongPress;
	PerformClick mPerformClick;
	UnsetPressedState mUnsetPressedState;
	CheckForLongPress mPendingCheckForLongPress;
	View cv;

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		cv = view;
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			boolean focusTaken = false;
			if (cv.isFocusable() && cv.isFocusableInTouchMode() && !cv.isFocused()) {
				focusTaken = cv.requestFocus();
			}
			if (!mHasPerformedLongPress) {
				// This is a tap, so remove the longpress check
				removeLongPressCallback();
				if (!focusTaken) {
					// Use a Runnable and post this rather than calling
					// performClick directly. This lets other visual state
					// of the view update before click actions start.
					if (mPerformClick == null) {
						mPerformClick = new PerformClick(cv);
					}
					if (!cv.post(mPerformClick)) {
						onClick(cv);
					}
				}
			} else {
				finishLongPress(cv);
			}
			if (mUnsetPressedState == null) {
				mUnsetPressedState = new UnsetPressedState();
			}
			if (!cv.post(mUnsetPressedState)) {
				// If the post failed, unpress right now
				mUnsetPressedState.run();
			}
			break;
		case MotionEvent.ACTION_DOWN:
			mHasPerformedLongPress = false;
			cv.setPressed(true);
			checkForLongClick(0);
			break;
		}
		return true;
	}

	private void checkForLongClick(int delayOffset) {
		mHasPerformedLongPress = false;
		if (mPendingCheckForLongPress == null) {
			mPendingCheckForLongPress = new CheckForLongPress();
		}
		cv.postDelayed(mPendingCheckForLongPress, ViewConfiguration.getLongPressTimeout() - delayOffset);
	}

	private void removeLongPressCallback() {
		if (mPendingCheckForLongPress != null) {
			cv.removeCallbacks(mPendingCheckForLongPress);
		}
	}

	protected boolean isVibrator() {
		return true;
	}

	final class PerformClick implements Runnable {
		View view;

		public PerformClick(View view) {
			this.view = view;
		}

		public void run() {
			onClick(view);
		}
	}

	class CheckForLongPress implements Runnable {
		public void run() {
			if (cv.isPressed()) {
				if (startLongPress(cv)) {
					if (isVibrator()) UtilVibrator.getInstance(cv.getContext()).vibrate(100);
					mHasPerformedLongPress = true;
				}
			}
		}
	}

	final class UnsetPressedState implements Runnable {
		public void run() {
			cv.setPressed(false);
		}
	}

	// Abstract Method
	public abstract void onClick(View view);

	public abstract boolean startLongPress(View view);

	public abstract void finishLongPress(View view);
}
