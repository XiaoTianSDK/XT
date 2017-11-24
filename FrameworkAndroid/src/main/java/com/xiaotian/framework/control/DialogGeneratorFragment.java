package com.xiaotian.framework.control;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name DialogGeneratorFragment
 * @description Dialog Generator Fragment
 * @date 2014-10-17
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class DialogGeneratorFragment extends DialogFragment {
	private static final Object LOCK = new Object();
	private Dialog mDialog;

	// Create Showing Dialog in onCreateDialog Method
	public static DialogGeneratorFragment newInstance() {
		synchronized (LOCK) {
			return new DialogGeneratorFragment();
		}
	}

	// Input The Showing Dialog
	public static DialogGeneratorFragment newInstance(Dialog dialog) {
		synchronized (LOCK) {
			DialogGeneratorFragment dialogGeneratorFragment = new DialogGeneratorFragment();
			dialogGeneratorFragment.mDialog = dialog;
			return dialogGeneratorFragment;
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if (mDialog != null) return mDialog;
		return super.onCreateDialog(savedInstanceState);
	}
}
