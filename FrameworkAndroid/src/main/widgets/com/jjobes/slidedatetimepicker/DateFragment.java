package com.jjobes.slidedatetimepicker;

import java.lang.reflect.Field;
import java.util.Date;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.NumberPicker;

import com.jjobes.slidedatetimepicker.SlideDateTimeDialogFragment.CustomViewPager;
import com.xiaotian.framework.R;

/**
 * The fragment for the first page in the ViewPager that holds the
 * {@link CustomDatePicker}.
 * 
 * @author jjobes
 * 
 */
public class DateFragment extends Fragment {
	/**
	 * Used to communicate back to the parent fragment as the user is changing
	 * the date spinners so we can dynamically update the tab text.
	 */
	public interface DateChangedListener {
		void onDateChanged(int year, int month, int day);
	}

	private DateChangedListener mCallback;
	private CustomDatePicker mDatePicker;

	public DateFragment() {}

	/**
	 * Cast the reference to {@link SlideDateTimeDialogFragment} to a
	 * {@link DateChangedListener}.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			mCallback = (DateChangedListener) getTargetFragment();
		} catch (ClassCastException e) {
			throw new ClassCastException("Calling fragment must implement " + "DateFragment.DateChangedListener interface");
		}
	}

	/**
	 * Return an instance of DateFragment with its bundle filled with the
	 * constructor arguments. The values in the bundle are retrieved in
	 * {@link #onCreateView()} below to properly initialize the DatePicker.
	 * 
	 * @param theme
	 * @param year
	 * @param month
	 * @param day
	 * @param minDate
	 * @param maxDate
	 * @return an instance of DateFragment
	 */
	public static final DateFragment newInstance(int theme, int year, int month, int day, Date minDate, Date maxDate) {
		DateFragment f = new DateFragment();
		Bundle b = new Bundle();
		b.putInt("theme", theme);
		b.putInt("year", year);
		b.putInt("month", month);
		b.putInt("day", day);
		b.putSerializable("minDate", minDate);
		b.putSerializable("maxDate", maxDate);
		f.setArguments(b);
		return f;
	}

	/**
	 * Create and return the user interface view for this fragment.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		int theme = getArguments().getInt("theme");
		int initialYear = getArguments().getInt("year");
		int initialMonth = getArguments().getInt("month");
		int initialDay = getArguments().getInt("day");
		Date minDate = (Date) getArguments().getSerializable("minDate");
		Date maxDate = (Date) getArguments().getSerializable("maxDate");
		// Unless we inflate using a cloned inflater with a Holo theme,
		// on Lollipop devices the DatePicker will be the new-style
		// DatePicker, which is not what we want. So we will
		// clone the inflater that we're given but with our specified
		// theme, then inflate the layout with this new inflater.
		Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), theme == SlideDateTimePicker.HOLO_DARK ? android.R.style.Theme_Holo : android.R.style.Theme_Holo_Light);
		mDatePicker = new CustomDatePicker(contextThemeWrapper, null);
		mDatePicker.setId(CustomViewPager.PICKER_ID_DATE);
		mDatePicker.setCalendarViewShown(false);
		// block keyboard popping up on touch
		mDatePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
		mDatePicker.init(initialYear, initialMonth, initialDay, new OnDateChangedListener() {
			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				mCallback.onDateChanged(year, monthOfYear, dayOfMonth);
			}
		});
		if (minDate != null) mDatePicker.setMinDate(minDate.getTime());
		if (maxDate != null) mDatePicker.setMaxDate(maxDate.getTime());
		return mDatePicker;
	}

	// 重写日期选择,实现改选择器间隔线颜色,字体,颜色
	public static class CustomDatePicker extends DatePicker {
		private static final String TAG = "CustomDatePicker";
		int dividerColor = getResources().getColor(R.color.color_holo_blue);

		public CustomDatePicker(Context context, AttributeSet attrs) {
			super(context, attrs);

			Class<?> idClass = null;
			Class<?> numberPickerClass = null;
			Field selectionDividerField = null;
			Field monthField = null;
			Field dayField = null;
			Field yearField = null;
			NumberPicker monthNumberPicker = null;
			NumberPicker dayNumberPicker = null;
			NumberPicker yearNumberPicker = null;
			try {
				// Create an instance of the id class
				idClass = Class.forName("com.android.internal.R$id");
				// Get the fields that store the resource IDs for the month, day and year NumberPickers
				monthField = idClass.getField("month");
				dayField = idClass.getField("day");
				yearField = idClass.getField("year");
				// Use the resource IDs to get references to the month, day and year NumberPickers
				monthNumberPicker = (NumberPicker) findViewById(monthField.getInt(null));
				dayNumberPicker = (NumberPicker) findViewById(dayField.getInt(null));
				yearNumberPicker = (NumberPicker) findViewById(yearField.getInt(null));
				numberPickerClass = Class.forName("android.widget.NumberPicker");
				// Set the value of the mSelectionDivider field in the month, day and year NumberPickers to refer to our custom drawables
				selectionDividerField = numberPickerClass.getDeclaredField("mSelectionDivider");
				selectionDividerField.setAccessible(true);
				selectionDividerField.set(monthNumberPicker, new ColorDrawable(dividerColor));
				selectionDividerField.set(dayNumberPicker, new ColorDrawable(dividerColor));
				selectionDividerField.set(yearNumberPicker, new ColorDrawable(dividerColor));
			} catch (ClassNotFoundException e) {
				Log.e(TAG, "ClassNotFoundException in CustomDatePicker", e);
			} catch (NoSuchFieldException e) {
				Log.e(TAG, "NoSuchFieldException in CustomDatePicker", e);
			} catch (IllegalAccessException e) {
				Log.e(TAG, "IllegalAccessException in CustomDatePicker", e);
			} catch (IllegalArgumentException e) {
				Log.e(TAG, "IllegalArgumentException in CustomDatePicker", e);
			}
		}
	}
}
