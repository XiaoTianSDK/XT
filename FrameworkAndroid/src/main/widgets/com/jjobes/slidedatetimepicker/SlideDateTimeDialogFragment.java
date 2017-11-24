package com.jjobes.slidedatetimepicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.jjobes.slidedatetimepicker.SlideDateTimePicker.SlideDateTimeListener;
import com.xiaotian.framework.R;

/**
 * <p>
 * The {@code DialogFragment} that contains the {@link SlidingTabLayout} and
 * {@link CustomViewPager}.
 * </p>
 * <p>
 * The {@code CustomViewPager} contains the {@link DateFragment} and
 * {@link TimeFragment}.
 * </p>
 * <p>
 * This {@code DialogFragment} is managed by {@link SlideDateTimePicker}.
 * </p>
 * 
 * @author jjobes
 * 
 */
public class SlideDateTimeDialogFragment extends DialogFragment implements DateFragment.DateChangedListener, TimeFragment.TimeChangedListener {
	public static final String TAG_SLIDE_DATE_TIME_DIALOG_FRAGMENT = "tagSlideDateTimeDialogFragment";
	private static SlideDateTimeListener mListener;
	private Context mContext;
	private CustomViewPager mViewPager;
	private ViewPagerAdapter mViewPagerAdapter;
	private SlidingTabLayout mSlidingTabLayout;
	private View mButtonHorizontalDivider;
	private View mButtonVerticalDivider;
	private Button mOkButton;
	private Button mCancelButton;
	private Date mInitialDate;
	private int mTheme;
	private int mIndicatorColor;
	private Date mMinDate;
	private Date mMaxDate;
	private boolean mIsClientSpecified24HourTime;
	private boolean mIs24HourTime;
	private Calendar mCalendar;
	private int mDateFlags = DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL;
	private int grayHoloDark = 0xff464646, grayHoloLight = 0xffd0d0d0;

	public SlideDateTimeDialogFragment() {}

	public static SlideDateTimeDialogFragment newInstance(SlideDateTimeListener listener, Date initialDate, Date minDate, Date maxDate, boolean isClientSpecified24HourTime, boolean is24HourTime,
			int theme, int indicatorColor) {
		mListener = listener;
		SlideDateTimeDialogFragment dialogFragment = new SlideDateTimeDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable("initialDate", initialDate);
		bundle.putSerializable("minDate", minDate);
		bundle.putSerializable("maxDate", maxDate);
		bundle.putBoolean("isClientSpecified24HourTime", isClientSpecified24HourTime);
		bundle.putBoolean("is24HourTime", is24HourTime);
		bundle.putInt("theme", theme);
		bundle.putInt("indicatorColor", indicatorColor);
		dialogFragment.setArguments(bundle);
		return dialogFragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		unpackBundle();
		mCalendar = Calendar.getInstance();
		mCalendar.setTime(mInitialDate);
		// 设置Dialog Style
		switch (mTheme) {
		case SlideDateTimePicker.HOLO_DARK:
			setStyle(DialogFragment.STYLE_NO_TITLE, R.style.StyleXiaoTian_Dialog_NoActionBar);
			break;
		case SlideDateTimePicker.HOLO_LIGHT:
			setStyle(DialogFragment.STYLE_NO_TITLE, R.style.StyleXiaoTian_Dialog_NoActionBar_Light);
			break;
		default: // if no theme was specified, default to holo light
			setStyle(DialogFragment.STYLE_NO_TITLE, R.style.StyleXiaoTian_Dialog_NoActionBar_Light);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.slide_date_time_picker, container);
		setupViews(view);
		customizeViews();
		initViewPager();
		initTabs();
		initButtons();
		return view;
	}

	@Override
	public void onDestroyView() {
		// Workaround for a bug in the compatibility library where calling setRetainInstance(true) does not retain the instance across orientation changes.
		if (getDialog() != null && getRetainInstance()) getDialog().setDismissMessage(null);
		super.onDestroyView();
	}

	private void setupViews(View v) {
		mViewPager = (CustomViewPager) v.findViewById(R.id.id_1);
		mSlidingTabLayout = (SlidingTabLayout) v.findViewById(R.id.id_0);
		mButtonHorizontalDivider = v.findViewById(R.id.id_2);
		mButtonVerticalDivider = v.findViewById(R.id.id_4);
		mOkButton = (Button) v.findViewById(R.id.id_5);
		mCancelButton = (Button) v.findViewById(R.id.id_3);
	}

	private void customizeViews() {
		int lineColor = mTheme == SlideDateTimePicker.HOLO_DARK ? grayHoloDark : grayHoloLight;
		// Set the colors of the horizontal and vertical lines for the
		// bottom buttons depending on the theme.
		switch (mTheme) {
		case SlideDateTimePicker.HOLO_LIGHT:
		case SlideDateTimePicker.HOLO_DARK:
			mButtonHorizontalDivider.setBackgroundColor(lineColor);
			mButtonVerticalDivider.setBackgroundColor(lineColor);
			break;
		default: // if no theme was specified, default to holo light
			mButtonHorizontalDivider.setBackgroundColor(grayHoloLight);
			mButtonVerticalDivider.setBackgroundColor(grayHoloLight);
		}
		// Set the color of the selected tab underline if one was specified.
		if (mIndicatorColor != 0) mSlidingTabLayout.setSelectedIndicatorColors(mIndicatorColor);
	}

	private void initViewPager() {
		mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
		mViewPager.setAdapter(mViewPagerAdapter);
		// Setting this custom layout for each tab ensures that the tabs will
		// fill all available horizontal space.
		//mSlidingTabLayout.setCustomTabView(R.layout.slide_date_time_picker_tab, R.id.id_1); // Tab Scrip UI 
		mSlidingTabLayout.setViewPager(mViewPager);
	}

	private void initTabs() {
		// Set intial date on date tab
		updateDateTab();
		// Set initial time on time tab
		updateTimeTab();
	}

	private void initButtons() {
		mOkButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) mListener.onDateTimeSet(new Date(mCalendar.getTimeInMillis()));
				dismiss();
			}
		});
		mCancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) mListener.onDateTimeCancel();
				dismiss();
			}
		});
	}

	private void unpackBundle() {
		// 解析传入Bundle包含的参数
		Bundle args = getArguments();
		mInitialDate = (Date) args.getSerializable("initialDate");
		mMinDate = (Date) args.getSerializable("minDate");
		mMaxDate = (Date) args.getSerializable("maxDate");
		mIsClientSpecified24HourTime = args.getBoolean("isClientSpecified24HourTime");
		mIs24HourTime = args.getBoolean("is24HourTime");
		mTheme = args.getInt("theme");
		mIndicatorColor = args.getInt("indicatorColor");
	}

	@Override
	public void onDateChanged(int year, int month, int day) {
		mCalendar.set(year, month, day);
		updateDateTab();
	}

	@Override
	public void onTimeChanged(int hour, int minute) {
		mCalendar.set(Calendar.HOUR_OF_DAY, hour);
		mCalendar.set(Calendar.MINUTE, minute);
		updateTimeTab();
	}

	private void updateDateTab() {
		mSlidingTabLayout.setTabText(0, DateUtils.formatDateTime(mContext, mCalendar.getTimeInMillis(), mDateFlags));
	}

	@SuppressLint("SimpleDateFormat")
	private void updateTimeTab() {
		if (mIsClientSpecified24HourTime) {
			SimpleDateFormat formatter;
			if (mIs24HourTime) {
				formatter = new SimpleDateFormat("HH:mm");
				mSlidingTabLayout.setTabText(1, formatter.format(mCalendar.getTime()));
			} else {
				formatter = new SimpleDateFormat("h:mm aa");
				mSlidingTabLayout.setTabText(1, formatter.format(mCalendar.getTime()));
			}
		} else // display time using the device's default 12/24 hour format preference
		{
			mSlidingTabLayout.setTabText(1, DateFormat.getTimeFormat(mContext).format(mCalendar.getTimeInMillis()));
		}
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		if (mListener != null) mListener.onDateTimeCancel();
	}

	/********************************************* inner class *********************************************/

	public static class CustomViewPager extends ViewPager {
		public static final int PICKER_ID_DATE = 0X03241232;
		public static final int PICKER_ID_TIME = 0X03241233;
		private DatePicker mDatePicker;
		private TimePicker mTimePicker;
		private float x1, y1, x2, y2;
		private float mTouchSlop;

		public CustomViewPager(Context context) {
			super(context);
			init(context);
		}

		public CustomViewPager(Context context, AttributeSet attrs) {
			super(context, attrs);
			init(context);
		}

		private void init(Context context) {
			mTouchSlop = ViewConfiguration.get(context).getScaledPagingTouchSlop();
		}

		/**
		 * Setting wrap_content on a ViewPager's layout_height in XML doesn't
		 * seem to be recognized and the ViewPager will fill the height of the
		 * screen regardless. We'll force the ViewPager to have the same height
		 * as its immediate child.
		 */
		@Override
		public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			if (getChildCount() > 0) {
				View childView = getChildAt(0);
				if (childView != null) {
					childView.measure(widthMeasureSpec, heightMeasureSpec);
					int h = childView.getMeasuredHeight();
					setMeasuredDimension(getMeasuredWidth(), h);
					getLayoutParams().height = h;
				}
			}
			mDatePicker = (DatePicker) findViewById(PICKER_ID_DATE);
			mTimePicker = (TimePicker) findViewById(PICKER_ID_TIME);
		}

		/**
		 * When the user swipes their finger horizontally, dispatch those touch
		 * events to the ViewPager. When they swipe vertically, dispatch those
		 * touch events to the date or time picker (depending on which page
		 * we're currently on).
		 * 
		 * @param event
		 */
		@Override
		public boolean dispatchTouchEvent(MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				x1 = event.getX();
				y1 = event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				x2 = event.getX();
				y2 = event.getY();
				if (isScrollingHorizontal(x1, y1, x2, y2)) {
					// When the user is scrolling the ViewPager horizontally,
					// block the pickers from scrolling vertically.
					return super.dispatchTouchEvent(event);
				}
				break;
			}
			// As long as the ViewPager isn't scrolling horizontally,
			// dispatch the event to the DatePicker or TimePicker,
			// depending on which page the ViewPager is currently on.
			switch (getCurrentItem()) {
			case 0:
				if (mDatePicker != null) mDatePicker.dispatchTouchEvent(event);
				break;
			case 1:
				if (mTimePicker != null) mTimePicker.dispatchTouchEvent(event);
				break;
			}
			// need this for the ViewPager to scroll horizontally at all
			return super.onTouchEvent(event);
		}

		/**
		 * Determine whether the distance between the user's ACTION_DOWN event
		 * (x1, y1) and the current ACTION_MOVE event (x2, y2) should be
		 * interpreted as a horizontal swipe.
		 * 
		 * @param x1
		 * @param y1
		 * @param x2
		 * @param y2
		 * @return
		 */
		private boolean isScrollingHorizontal(float x1, float y1, float x2, float y2) {
			float deltaX = x2 - x1;
			float deltaY = y2 - y1;
			if (Math.abs(deltaX) > mTouchSlop && Math.abs(deltaX) > Math.abs(deltaY)) return true;
			return false;
		}
	}

	private class ViewPagerAdapter extends FragmentPagerAdapter {
		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				int year = mCalendar.get(Calendar.YEAR);
				int month = mCalendar.get(Calendar.MONTH);
				int day = mCalendar.get(Calendar.DAY_OF_MONTH);
				DateFragment dateFragment = DateFragment.newInstance(mTheme, year, month, day, mMinDate, mMaxDate);
				dateFragment.setTargetFragment(SlideDateTimeDialogFragment.this, 100);
				return dateFragment;
			case 1:
				int h = mCalendar.get(Calendar.HOUR_OF_DAY);
				int m = mCalendar.get(Calendar.MINUTE);
				TimeFragment timeFragment = TimeFragment.newInstance(mTheme, h, m, mIsClientSpecified24HourTime, mIs24HourTime);
				timeFragment.setTargetFragment(SlideDateTimeDialogFragment.this, 200);
				return timeFragment;
			default:
				return null;
			}
		}

		@Override
		public int getCount() {
			return 2;
		}
	}
}
