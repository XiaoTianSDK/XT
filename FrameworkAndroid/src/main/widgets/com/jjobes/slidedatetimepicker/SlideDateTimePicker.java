package com.jjobes.slidedatetimepicker;

import java.util.Date;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * <p>
 * This class contains methods for the library client to create a new
 * {@code SlideDateTimePicker}.
 * </p>
 * 
 * <p>
 * It also implements a Builder API that offers more convenient object creation.
 * </p>
 * 
 * @author jjobes
 * 
 */
public class SlideDateTimePicker {
	// 对话框构造器 Builder
	public static final int HOLO_DARK = 1; // 黑色主题
	public static final int HOLO_LIGHT = 2; // 白色主题
	private FragmentManager mFragmentManager;
	private SlideDateTimeListener mListener;
	private Date mInitialDate;
	private Date mMinDate;
	private Date mMaxDate;
	private boolean mIsClientSpecified24HourTime;
	private boolean mIs24HourTime;
	private int mTheme;
	private int mIndicatorColor;

	public SlideDateTimePicker(FragmentManager fm) {
		// See if there are any DialogFragments from the FragmentManager
		FragmentTransaction ft = fm.beginTransaction();
		Fragment prev = fm.findFragmentByTag(SlideDateTimeDialogFragment.TAG_SLIDE_DATE_TIME_DIALOG_FRAGMENT);
		// Remove if found
		if (prev != null) {
			ft.remove(prev);
			ft.commit();
		}
		mFragmentManager = fm;
	}

	// 构造完成对话框基本参数,显示对话框[创建对话框+设置+显示]
	public void show() {
		if (mInitialDate == null) mInitialDate = new Date();
		// 构造对话框Fragment
		SlideDateTimeDialogFragment dialogFragment = SlideDateTimeDialogFragment.newInstance(mListener, mInitialDate, mMinDate, mMaxDate, mIsClientSpecified24HourTime, mIs24HourTime, mTheme,
				mIndicatorColor);
		// 显示Fragment
		dialogFragment.show(mFragmentManager, SlideDateTimeDialogFragment.TAG_SLIDE_DATE_TIME_DIALOG_FRAGMENT);
	}

	public void setListener(SlideDateTimeListener listener) {
		mListener = listener;
	}

	public void setInitialDate(Date initialDate) {
		mInitialDate = initialDate;
	}

	public void setMinDate(Date minDate) {
		mMinDate = minDate;
	}

	public void setMaxDate(Date maxDate) {
		mMaxDate = maxDate;
	}

	private void setIsClientSpecified24HourTime(boolean isClientSpecified24HourTime) {
		mIsClientSpecified24HourTime = isClientSpecified24HourTime;
	}

	public void setIs24HourTime(boolean is24HourTime) {
		setIsClientSpecified24HourTime(true);
		mIs24HourTime = is24HourTime;
	}

	public void setTheme(int theme) {
		mTheme = theme;
	}

	public void setIndicatorColor(int indicatorColor) {
		mIndicatorColor = indicatorColor;
	}

	/********************************************* inner class *********************************************/
	public static class Builder {
		// 构造对话框的构造器静态实体类Builder

		// Required
		private FragmentManager fm;
		private SlideDateTimeListener listener;
		// Optional
		private Date initialDate;
		private Date minDate;
		private Date maxDate;
		private boolean isClientSpecified24HourTime;
		private boolean is24HourTime;
		private int theme;
		private int indicatorColor;

		public Builder(FragmentManager fm) {
			this.fm = fm;
		}

		public Builder setListener(SlideDateTimeListener listener) {
			this.listener = listener;
			return this;
		}

		public Builder setInitialDate(Date initialDate) {
			this.initialDate = initialDate;
			return this;
		}

		public Builder setMinDate(Date minDate) {
			this.minDate = minDate;
			return this;
		}

		public Builder setMaxDate(Date maxDate) {
			this.maxDate = maxDate;
			return this;
		}

		public Builder setIs24HourTime(boolean is24HourTime) {
			this.isClientSpecified24HourTime = true;
			this.is24HourTime = is24HourTime;
			return this;
		}

		public Builder setTheme(int theme) {
			this.theme = theme;
			return this;
		}

		public Builder setIndicatorColor(int indicatorColor) {
			this.indicatorColor = indicatorColor;
			return this;
		}

		public SlideDateTimePicker build() {
			SlideDateTimePicker picker = new SlideDateTimePicker(fm);
			picker.setListener(listener);
			picker.setInitialDate(initialDate);
			picker.setMinDate(minDate);
			picker.setMaxDate(maxDate);
			picker.setIsClientSpecified24HourTime(isClientSpecified24HourTime);
			picker.setIs24HourTime(is24HourTime);
			picker.setTheme(theme);
			picker.setIndicatorColor(indicatorColor);
			return picker;
		}
	}

	public static abstract class SlideDateTimeListener {
		// 时间日期选择侦听器
		public abstract void onDateTimeSet(Date date);

		public void onDateTimeCancel() {}
	}
}
