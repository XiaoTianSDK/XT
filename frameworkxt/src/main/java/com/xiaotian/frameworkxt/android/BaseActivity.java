package com.xiaotian.frameworkxt.android;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
 * @name ActivityBase
 * @description Base Level Activity{Toast,Alert,UncatchException Handler}
 * @date 2013-10-17
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
public class BaseActivity extends Activity {
	public static final String EXTRA_PARAM_RESPONSE_CODE = "com.xiaotian.framework.activity.RESPONSE_CODE";

	protected boolean isExecuteOnClickEvent;
	protected int resBackgroundDialogButton;
	protected ProgressDialog dialogLoading;
	private UtilDecimal utilDecimal;
	private UtilNumber utilNumber;
	private UtilDateTime utilDate;
	protected Handler handler;
	private UtilFile utilFile;
	private UtilUri utilUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected UtilNumber getUtilNumber() {
		if (utilNumber == null) {
			utilNumber = new UtilNumber();
		}
		return utilNumber;
	}

	protected Handler getHandler() {
		if (handler == null) {
			handler = new Handler();
		}
		return handler;
	}

	protected UtilFile getUtilFile() {
		if (utilFile == null) {
			utilFile = UtilFile.getInstance();
		}
		return utilFile;
	}

	public UtilDateTime getUtilDate() {
		if (utilDate == null) utilDate = new UtilDateTime();
		return utilDate;
	}

	public UtilDecimal getUtilDecimal() {
		if (utilDecimal == null) utilDecimal = new UtilDecimal();
		return utilDecimal;
	}

	public UtilUri getUtilUri() {
		if (utilUri == null) utilUri = new UtilUri();
		return utilUri;
	}

	protected void initializingData() {}

	protected void initializingView() {}

	protected void toast(int msgresid) {
		toast(msgresid, Toast.LENGTH_SHORT);
	}

	protected void toast(int msgresid, int timeLong) {
		Toast.makeText(this, msgresid, timeLong).show();
	}

	protected void toast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	protected String format(String pattern, Object... params) {
		return String.format(pattern, params);
	}

	// TODO loading
	protected void showLoadingDialog(int msgresid) {
		if (dialogLoading != null && dialogLoading.isShowing()) return;
		dialogLoading = ProgressDialog.show(this, null, getString(msgresid));
	}

	protected void showLoadingDialog(int msgresid, boolean cancelAble) {
		if (dialogLoading != null && dialogLoading.isShowing()) return;
		dialogLoading = ProgressDialog.show(this, null, getString(msgresid), false, cancelAble);
	}

	protected void showLoadingDialog(int titleres, int msgresid) {
		if (dialogLoading != null && dialogLoading.isShowing()) return;
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

	// 清空当前Focus,不兼容三星,会导致当前Focus View 失去状态
	@Deprecated
	protected void cleanCurrentFocus() {
		View view = getWindow().getCurrentFocus();
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
	protected void onResume() {
		isExecuteOnClickEvent = true;
		super.onResume();
	}

	@Override
	protected void onPause() {
		isExecuteOnClickEvent = false;
		super.onPause();
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
	// inner class

}
