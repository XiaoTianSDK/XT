package com.jjobes.slidedatetimepicker;

import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TimePicker;

import com.jjobes.slidedatetimepicker.SlideDateTimeDialogFragment.CustomViewPager;
import com.xiaotian.framework.R;

/**
 * The fragment for the second page in the ViewPager that holds the TimePicker.
 * 
 * @author jjobes
 * 
 */
public class TimeFragment extends Fragment {
	/**
	 * Used to communicate back to the parent fragment as the user is changing
	 * the time spinners so we can dynamically update the tab text.
	 */
	public interface TimeChangedListener {
		void onTimeChanged(int hour, int minute);
	}

	private TimeChangedListener mCallback;
	private CustomTimePicker mTimePicker;

	public TimeFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			mCallback = (TimeChangedListener) getTargetFragment();
		} catch (ClassCastException e) {
			throw new ClassCastException("Calling fragment must implement " + "TimeFragment.TimeChangedListener interface");
		}
	}

	/**
	 * Return an instance of TimeFragment with its bundle filled with the
	 * constructor arguments. The values in the bundle are retrieved in
	 * {@link #onCreateView()} below to properly initialize the TimePicker.
	 * 
	 * @param theme
	 * @param hour
	 * @param minute
	 * @param isClientSpecified24HourTime
	 * @param is24HourTime
	 * @return
	 */
	public static final TimeFragment newInstance(int theme, int hour, int minute, boolean isClientSpecified24HourTime, boolean is24HourTime) {
		TimeFragment f = new TimeFragment();
		Bundle b = new Bundle();
		b.putInt("theme", theme);
		b.putInt("hour", hour);
		b.putInt("minute", minute);
		b.putBoolean("isClientSpecified24HourTime", isClientSpecified24HourTime);
		b.putBoolean("is24HourTime", is24HourTime);
		f.setArguments(b);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		int theme = getArguments().getInt("theme");
		int initialHour = getArguments().getInt("hour");
		int initialMinute = getArguments().getInt("minute");
		boolean isClientSpecified24HourTime = getArguments().getBoolean("isClientSpecified24HourTime");
		boolean is24HourTime = getArguments().getBoolean("is24HourTime");
		// Unless we inflate using a cloned inflater with a Holo theme,
		// on Lollipop devices the TimePicker will be the new-style
		// radial TimePicker, which is not what we want. So we will
		// clone the inflater that we're given but with our specified
		// theme, then inflate the layout with this new inflater.
		Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), theme == SlideDateTimePicker.HOLO_DARK ? android.R.style.Theme_Holo : android.R.style.Theme_Holo_Light);
		mTimePicker = new CustomTimePicker(contextThemeWrapper, null);
		mTimePicker.setId(CustomViewPager.PICKER_ID_TIME);
		// block keyboard popping up on touch
		mTimePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
		mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				mCallback.onTimeChanged(hourOfDay, minute);
			}
		});
		// If the client specifies a 24-hour time format, set it on
		// the TimePicker.
		if (isClientSpecified24HourTime) {
			mTimePicker.setIs24HourView(is24HourTime);
		} else {
			// If the client does not specify a 24-hour time format, use the
			// device default.
			mTimePicker.setIs24HourView(DateFormat.is24HourFormat(getTargetFragment().getActivity()));
		}
		mTimePicker.setCurrentHour(initialHour);
		mTimePicker.setCurrentMinute(initialMinute);

		// Fix for the bug where a TimePicker's onTimeChanged() is not called when
		// the user toggles the AM/PM button. Only applies to 4.0.0 and 4.0.3.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
			fixTimePickerBug18982();
		}
		return mTimePicker;
	}

	/**
	 * Workaround for bug in Android TimePicker where the onTimeChanged()
	 * callback is not invoked when the user toggles between AM/PM. But we need
	 * to be able to detect this in order to dynamically update the tab title
	 * properly when the user toggles between AM/PM.
	 * 
	 * Registered as Issue 18982:
	 * 
	 * https://code.google.com/p/android/issues/detail?id=18982
	 */
	private void fixTimePickerBug18982() {
		View amPmView = ((ViewGroup) mTimePicker.getChildAt(0)).getChildAt(3);
		if (amPmView instanceof NumberPicker) {
			((NumberPicker) amPmView).setOnValueChangedListener(new OnValueChangeListener() {

				@Override
				public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
					if (picker.getValue() == 1) // PM
					{
						if (mTimePicker.getCurrentHour() < 12) mTimePicker.setCurrentHour(mTimePicker.getCurrentHour() + 12);
					} else // AM
					{
						if (mTimePicker.getCurrentHour() >= 12) mTimePicker.setCurrentHour(mTimePicker.getCurrentHour() - 12);
					}

					mCallback.onTimeChanged(mTimePicker.getCurrentHour(), mTimePicker.getCurrentMinute());
				}
			});
		}
	}

	// 重写时间选择器,改变时间间隔线,字体,颜色
	public static class CustomTimePicker extends TimePicker {
		private static final String TAG = "CustomTimePicker";
		int dividerColor = getResources().getColor(R.color.color_holo_blue);

		public CustomTimePicker(Context context, AttributeSet attrs) {
			super(context, attrs);
			Class<?> idClass = null;
			Class<?> numberPickerClass = null;
			Field selectionDividerField = null;
			Field hourField = null;
			Field minuteField = null;
			Field amPmField = null;
			NumberPicker hourNumberPicker = null;
			NumberPicker minuteNumberPicker = null;
			NumberPicker amPmNumberPicker = null;
			try {
				// Create an instance of the id class
				idClass = Class.forName("com.android.internal.R$id");
				// Get the fields that store the resource IDs for the hour, minute and amPm NumberPickers
				hourField = idClass.getField("hour");
				minuteField = idClass.getField("minute");
				amPmField = idClass.getField("amPm");
				// Use the resource IDs to get references to the hour, minute and amPm NumberPickers
				hourNumberPicker = (NumberPicker) findViewById(hourField.getInt(null));
				minuteNumberPicker = (NumberPicker) findViewById(minuteField.getInt(null));
				amPmNumberPicker = (NumberPicker) findViewById(amPmField.getInt(null));
				numberPickerClass = Class.forName("android.widget.NumberPicker");
				// Set the value of the mSelectionDivider field in the hour, minute and amPm NumberPickers
				// to refer to our custom drawables
				selectionDividerField = numberPickerClass.getDeclaredField("mSelectionDivider");
				selectionDividerField.setAccessible(true);
				selectionDividerField.set(hourNumberPicker, new ColorDrawable(dividerColor));
				selectionDividerField.set(minuteNumberPicker, new ColorDrawable(dividerColor));
				selectionDividerField.set(amPmNumberPicker, new ColorDrawable(dividerColor));
			} catch (ClassNotFoundException e) {
				Log.e(TAG, "ClassNotFoundException in CustomTimePicker", e);
			} catch (NoSuchFieldException e) {
				Log.e(TAG, "NoSuchFieldException in CustomTimePicker", e);
			} catch (IllegalAccessException e) {
				Log.e(TAG, "IllegalAccessException in CustomTimePicker", e);
			} catch (IllegalArgumentException e) {
				Log.e(TAG, "IllegalArgumentException in CustomTimePicker", e);
			}
		}
	}
}
