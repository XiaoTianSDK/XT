package com.xiaotian.frameworkxt.android;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.xiaotian.frameworkxt.android.util.UtilUri;
import com.xiaotian.frameworkxt.util.UtilDateTime;
import com.xiaotian.frameworkxt.util.UtilDecimal;
import com.xiaotian.frameworkxt.util.UtilFile;
import com.xiaotian.frameworkxt.util.UtilNumber;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name BaseFragmentActivity
 * @description The Base Fragment Activity
 * @date 2014-8-26
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class BaseFragmentActivity extends FragmentActivity {
	protected boolean isExecuteOnClickEvent;
	protected int resBackgroundDialogButton;
	protected ProgressDialog dialogLoading;
	protected DialogFragment dialog;
	protected boolean mPendingShowDialog = false;
	protected boolean mIsStateAlreadySaved = false;
	protected Map<String, DialogFragment> paddingDialogFragment;
	//
	private UtilDecimal mUtilDecimal;
	private UtilNumber mUtilNumber;
	private UtilDateTime mUtilDate;
	private UtilFile mUtilFile;
	private Handler mHandler;
	private UtilUri mUtilUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected void initializingData() {}

	protected void initializingView() {}

	/********************************* Utils *********************************/
	protected UtilNumber getUtilNumber() {
		if (mUtilNumber != null) return mUtilNumber;
		return mUtilNumber = new UtilNumber();
	}

	protected Handler getHandler() {
		if (mHandler != null) return mHandler;
		return mHandler = new Handler();
	}

	protected UtilFile getUtilFile() {
		if (mUtilFile != null) return mUtilFile;
		return mUtilFile = UtilFile.getInstance();
	}

	public UtilDateTime getUtilDate() {
		if (mUtilDate != null) return mUtilDate;
		return mUtilDate = new UtilDateTime();
	}

	public UtilDecimal getUtilDecimal() {
		if (mUtilDecimal != null) return mUtilDecimal;
		return mUtilDecimal = new UtilDecimal();
	}

	public UtilUri getUtilUri() {
		if (mUtilUri != null) return mUtilUri;
		return mUtilUri = new UtilUri();
	}

	protected void toast(int msgresid) {
		toast(msgresid, Toast.LENGTH_SHORT);
	}

	protected void toast(int msgresid, int timeLong) {
		Toast.makeText(this, msgresid, timeLong).show();
	}

	protected void toast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	protected void toast(String msg, int duration, int gravity, int xOffset, int yOffset) {
		Toast toast = Toast.makeText(this, msg, duration);
		toast.setGravity(gravity, xOffset, yOffset);
		toast.show();
	}

	protected String format(String pattern, Object... params) {
		return String.format(pattern, params);
	}

	protected void startActivity(Class<?> activityClazz, int requestCode, Bundle... extras) {
		Intent intent = new Intent(this, activityClazz);
		if (extras.length > 0) intent.putExtras(extras[0]);
		startActivityForResult(intent, requestCode);
	}

	// TODO loading
	protected void showLoadingDialog(int msgresid) {
		if (dialogLoading != null && dialogLoading.isShowing()) dialogLoading.dismiss();
		dialogLoading = ProgressDialog.show(this, null, getString(msgresid));
	}

	protected void showLoadingDialog(int msgresid, boolean cancelAble) {
		if (dialogLoading != null && dialogLoading.isShowing()) dialogLoading.dismiss();
		dialogLoading = ProgressDialog.show(this, null, getString(msgresid), false, cancelAble);
	}

	protected void showLoadingDialog(int titleres, int msgresid) {
		if (dialogLoading != null && dialogLoading.isShowing()) dialogLoading.dismiss();
		dialogLoading = ProgressDialog.show(this, getString(titleres), getString(msgresid));
	}

	protected void dismissLoadingDialog() {
		if (dialogLoading != null && dialogLoading.isShowing()) dialogLoading.dismiss();
	}

	// View Click able
	protected void setExecuteOnClickable(boolean clickable) {
		this.isExecuteOnClickEvent = clickable;
	}

	protected boolean requestExecuteOnClick() {
		if (isExecuteOnClickEvent) {
			isExecuteOnClickEvent = false;
			return true;
		}
		return false;
	}

	protected void enableExecuteOnClick() {
		this.isExecuteOnClickEvent = true;
	}

	protected void disableExecuteOnClick() {
		this.isExecuteOnClickEvent = false;
	}

	// TextView text
	protected String getText(Object text) {
		return text == null ? "" : text.toString();
	}

	protected String getText(String text) {
		return text == null ? "" : text;
	}

	protected String getText(int resPre, String last) {
		return getString(resPre) + (last == null ? "" : last);
	}

	protected String getText(int resPre, String current, String last) {
		return getString(resPre) + (current == null ? "" : current) + (last == null ? "" : last);
	}

	protected String getText(int resPre, String current, int resLast) {
		return getString(resPre) + (current == null ? "" : current) + getString(resLast);
	}

	protected String getText(int resPre, Integer last) {
		return getString(resPre) + (last == null ? "" : last);
	}

	protected String getText(int resPre, Integer last, int resLast) {
		return getString(resPre) + (last == null ? "" : last) + getString(resLast);
	}

	protected String getText(int resPre, Long last) {
		return getString(resPre) + (last == null ? "" : last);
	}

	protected String getText(int resPre, Long last, int resLast) {
		return getString(resPre) + (last == null ? "" : last) + getString(resLast);
	}

	protected String getMoneyText(int resPre, Float money, float... def) {
		if (money == null) {
			return getString(resPre) + (def.length > 0 ? String.format("%1$#.2f", def[0]) : "");
		}
		return getString(resPre) + String.format("%1$#.2f", money);
	}

	protected String getMoneyText(int resPre, Float money, int resLast) {
		return getMoneyText(resPre, money) + getString(resLast);
	}

	protected CharSequence getTextCharSequence(int resPre, String last, int colorPre, int colorLast) {
		String pre = getString(resPre);
		ForegroundColorSpan colorSpan;
		int lengthPre = pre.length();
		SpannableString spannable = new SpannableString(pre + last);
		int lengthSpannable = spannable.length();
		if (colorPre != 0 && pre.length() > 0) {
			colorSpan = new ForegroundColorSpan(colorPre);
			spannable.setSpan(colorSpan, 0, lengthPre, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		if (colorLast != 0 && last.length() > 0) {
			colorSpan = new ForegroundColorSpan(colorLast);
			spannable.setSpan(colorSpan, lengthPre, lengthSpannable, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return spannable;
	}

	// 校验
	protected boolean verifyInteger(Character characterValue) {
		return verifyInteger(characterValue.toString());
	}

	protected boolean verifyInteger(String siValue) {
		try {
			Integer.parseInt(siValue.trim());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	protected boolean verifyLong(Character characterValue) {
		return verifyLong(characterValue.toString());
	}

	protected boolean verifyLong(String slValue) {
		try {
			Long.parseLong(slValue.trim());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	protected boolean verifyFloat(Character characterValue) {
		return verifyFloat(characterValue.toString());
	}

	protected boolean verifyFloat(String sfValue) {
		try {
			Float.parseFloat(sfValue.trim());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	// Edit text to value
	protected int getEditIntValue(Character characterValue) {
		String sv = characterValue.toString().trim();
		if (sv.equals("")) return 0;
		try {
			return Integer.parseInt(sv);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	protected long getEditLongValue(Character characterValue) {
		String sv = characterValue.toString().trim();
		if (sv.equals("")) return 0;
		try {
			return Long.parseLong(sv);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	protected float getEditFloatValue(Character characterValue) {
		String sv = characterValue.toString().trim();
		if (sv.equals("")) return 0;
		try {
			return Float.parseFloat(sv);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	protected String getUnNullText(String string) {
		if (string == null) return "";
		return string;
	}

	protected int getUnnullLisSize(List<?> list) {
		if (list == null) return 0;
		else return list.size();
	}

	public int getIntValue(Integer value) {
		return getIntValue(value, 0);
	}

	public int getIntValue(Integer value, int nullValue) {
		return value == null ? nullValue : value;
	}

	public long getLongValue(Long value) {
		return getLongValue(value, 0l);
	}

	public long getLongValue(Long value, long nullValue) {
		return value == null ? nullValue : value;
	}

	public float getFloatValue(Float value) {
		return getFloatValue(value, 0f);
	}

	public float getFloatValue(Float value, float nullValue) {
		return value == null ? nullValue : value;
	}

	public String getStringValue(String value) {
		return getStringValue(value, "");
	}

	public String getStringValue(String value, String nullValue) {
		return value == null ? nullValue : value;
	}

	public Double getDoubleValue(Double value) {
		return getDoubleValue(value, 0d);
	}

	public Double getDoubleValue(Double value, double nullValue) {
		return value == null ? nullValue : value;
	}

	// TODO 3.0多线程安全操作集对象
	@SuppressLint("NewApi")
	public <Params, Progress, Result> void executeAsyncTask(AsyncTask<Params, Progress, Result> task, Params... params) {
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			// android.os.Build.VERSION_CODES.HONEYCOMB
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
		} else {
			task.execute(params);
		}
	}

	// 软键盘
	protected void hideKeyboardAway() {
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	protected void hideKeyboard() {
		View focus = getCurrentFocus();
		if (focus == null) return;
		InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(focus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	protected void hideKeyboard(View focusView) {
		if (focusView == null) return;
		InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(focusView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	// 清空当前Focus
	protected void cleanCurrentFocus() {
		View view = getCurrentFocus();
		if (view != null) view.clearFocus();
	}

	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	@Override
	protected void onPause() {
		isExecuteOnClickEvent = false;
		super.onPause();
		mIsStateAlreadySaved = true;
	}

	@Override
	protected void onStart() {
		isExecuteOnClickEvent = true;
		super.onStart();
	}

	@Override
	protected void onStop() {
		isExecuteOnClickEvent = false;
		super.onStop();
	}

	@Override
	protected void onResume() {
		isExecuteOnClickEvent = true;
		super.onResume();
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		mIsStateAlreadySaved = false;
		if (mPendingShowDialog) {
			mPendingShowDialog = false;
			if (paddingDialogFragment != null) {
				Iterator<Entry<String, DialogFragment>> iter = paddingDialogFragment.entrySet().iterator();
				while (iter.hasNext()) {
					Entry<String, DialogFragment> entry = iter.next();
					entry.getValue().show(getSupportFragmentManager(), entry.getKey());
					iter.remove();
				}
			}
		}
	}

	public void show(DialogFragment dialog, String tag) {
		if (mIsStateAlreadySaved) {
			mPendingShowDialog = true;
			if (paddingDialogFragment == null) {
				paddingDialogFragment = new LinkedHashMap<String, DialogFragment>();
			}
			paddingDialogFragment.put(tag, dialog);
		} else {
			dialog.show(getSupportFragmentManager(), tag);
		}
	}

	public void dismiss(String tag) {
		android.support.v4.app.Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
		if (prev != null) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.remove(prev);
			ft.commit();
		}
	}
}
