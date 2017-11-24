package com.xiaotian.framework.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import com.xiaotian.framework.R;
import com.xiaotian.framework.control.DialogCustom;
import com.xiaotian.framework.control.DialogGenerator;
import com.xiaotian.framework.control.DialogGenerator.DialogListenerAlert;
import com.xiaotian.framework.control.DialogGenerator.DialogListenerInput;
import com.xiaotian.framework.control.DialogGenerator.InputDialogTheme;
import com.xiaotian.framework.service.BRToast;
import com.xiaotian.framework.util.UtilString;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name ActivityBase
 * @description Base Level Activity{Toast,Alert,UncatchException Handler}
 * @date 2013-10-17
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
public class BaseActivity extends com.xiaotian.frameworkxt.android.BaseActivity implements DialogListenerAlert {
	public static final String EXTRA_PARAM_RESPONSE_CODE = "com.xiaotian.framework.activity.RESPONSE_CODE";
	private UtilString utilString;
	protected DialogCustom dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected UtilString getUtilString() {
		if (utilString == null) {
			utilString = new UtilString(this);
		}
		return utilString;
	}

	protected Handler getHandler() {
		if (handler == null) {
			handler = new Handler();
		}
		return handler;
	}

	protected void initializingData() {}

	protected void initializingView() {}

	protected void inputText(int restitle, final DialogListenerInput listener) {
		int width = getResources().getDisplayMetrics().widthPixels * 8 / 10;
		dialog = DialogGenerator.initializingCenterDialogInput(this, restitle, InputDialogTheme.SINGLEEDIT, listener);
		dialog.setSize(width, 0, TypedValue.COMPLEX_UNIT_PX);
		dialog.setDialogId(R.id.id_dialog_input_edit);
		dialog.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
		dialog.show();
	}

	public void inputText(int restitle, DialogListenerInput listener, int widthPx, int heightPx) {
		dialog = DialogGenerator.initializingCenterDialogInput(this, restitle, InputDialogTheme.SINGLEEDIT);
		dialog.setSize(widthPx, heightPx);
		dialog.setDialogId(R.id.id_dialog_input_edit);
		dialog.show();
	}

	protected void inputDate(int restitle, DialogListenerInput listener) {
		inputDate(getString(restitle), listener);
	}

	protected void inputDate(String title, DialogListenerInput listener) {
		int width = getResources().getDisplayMetrics().widthPixels * 9 / 10;
		dialog = DialogGenerator.initializingCenterDialogInput(this, title, InputDialogTheme.DATE, listener);
		dialog.setDialogId(R.id.id_dialog_input_date);
		dialog.setWidth(width);
		dialog.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
		dialog.show();
	}

	protected void alert(int resmsgid, DialogListenerAlert... listener) {
		// 没有Title的Alert,默认大小
		alert(resmsgid, 230f, 135f, listener.length < 1 ? this : listener[0]);
	}

	protected void alert(int resmsgid, float width, float height, DialogListenerAlert... listener) {
		dialog = DialogGenerator.initializingCenterDialogAlert(this, null, resmsgid, listener.length < 1 ? this : listener[0]);
		dialog.setDialogId(R.id.id_0);
		dialog.setSize(width, height, TypedValue.COMPLEX_UNIT_DIP);
		dialog.show();
	}

	protected void alert(int restitleid, int resmsgid, DialogListenerAlert... listener) {
		// 默认大小的Alert
		alert(restitleid, resmsgid, 260f, 160f, listener.length < 1 ? this : listener[0]);
	}

	protected void alert(int restitleid, int resmsgid, float width, float height, DialogListenerAlert... listener) {
		// 指定大小的Alert
		dialog = DialogGenerator.initializingCenterDialogAlert(this, restitleid, resmsgid, listener);
		dialog.setSize(width, height, TypedValue.COMPLEX_UNIT_DIP);
		dialog.setDialogId(R.id.id_0);
		dialog.show();
	}

	protected void alert(String message, DialogListenerAlert... listener) {
		alert(message, 220f, 130f, listener.length < 1 ? this : listener[0]);
	}

	protected void alert(String message, float width, float height, DialogListenerAlert... listener) {
		dialog = DialogGenerator.initializingCenterDialogAlert(this, null, message, listener.length < 1 ? this : listener[0]);
		dialog.setDialogId(R.id.id_0);
		dialog.setSize(width, height, TypedValue.COMPLEX_UNIT_DIP);
		dialog.show();
	}

	protected void alert(int restitleid, String message, DialogListenerAlert... listener) {
		alert(restitleid, message, 260f, 160f, listener.length < 1 ? this : listener[0]);
	}

	protected void alert(int restitleid, String message, float width, float height, DialogListenerAlert... listener) {
		dialog = DialogGenerator.initializingCenterDialogAlert(this, restitleid, message, listener);
		dialog.setSize(width, height, TypedValue.COMPLEX_UNIT_DIP);
		dialog.setDialogId(R.id.id_0);
		dialog.show();
	}

	protected void confirmDialog(int resmsgid, DialogGenerator.DialogListenerConfirm listener) {
		confirmDialog(resmsgid, listener, 200f, 160f);
	}

	protected void confirmDialog(int resmsgid, int positive, int nagetive, DialogGenerator.DialogListenerConfirm listener) {
		confirmDialog(resmsgid, listener, 200f, 160f);
	}

	protected void confirmDialog(int restitleid, int resmsgid, DialogGenerator.DialogListenerConfirm listener) {
		confirmDialog(restitleid, resmsgid, listener, 200f, 160f);
	}

	protected void confirmDialog(int resmsgid, DialogGenerator.DialogListenerConfirm listener, float width, float height) {
		dialog = DialogGenerator.initializingCenterDialogConfirm(this, null, resmsgid, listener);
		dialog.setDialogId(R.id.id_0);
		dialog.setSize(width, height, TypedValue.COMPLEX_UNIT_SP);
		dialog.show();
	}

	protected void confirmDialog(int resmsgid, int positive, int nagetive, DialogGenerator.DialogListenerConfirm listener, float width, float height) {
		dialog = DialogGenerator.initializingCenterDialogConfirm(this, null, resmsgid, positive, nagetive, listener);
		dialog.setDialogId(R.id.id_0);
		dialog.setSize(width, height, TypedValue.COMPLEX_UNIT_SP);
		dialog.show();
	}

	protected void confirmDialog(int restitleid, int resmsgid, DialogGenerator.DialogListenerConfirm listener, float width, float height) {
		dialog = DialogGenerator.initializingCenterDialogConfirm(this, restitleid, resmsgid, listener);
		dialog.setDialogId(R.id.id_0);
		dialog.setSize(width, height, TypedValue.COMPLEX_UNIT_SP);
		dialog.show();
	}

	@Override
	public boolean onClickAlterPositive(View view) {
		return true;
	}

	protected void sendBroadcastToast(String content, long... time) {
		Intent intent = new Intent(BRToast.ACTION);
		intent.putExtra(BRToast.PARAM_CONTENT, content);
		if (time.length > 0) intent.putExtra(BRToast.PARAM_SHOW_TIME, time[0]);
		sendBroadcast(intent);
	}

	// Activity Intent
	protected void startActivity(Class<?> tagActivity, Bundle... extras) {
		Intent intent = new Intent(getBaseContext(), tagActivity);
		if (extras.length > 0) {
			intent.putExtras(extras[0]);
		}
		startActivity(intent);
	}

	protected void startActivity(Class<?> tagActivity, int requestCode, Bundle... extras) {
		Intent intent = new Intent(getBaseContext(), tagActivity);
		if (extras.length > 0) {
			intent.putExtras(extras[0]);
		}
		startActivityForResult(intent, requestCode);
	}
}
