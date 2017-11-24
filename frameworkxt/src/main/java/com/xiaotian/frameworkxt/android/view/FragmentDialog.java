package com.xiaotian.frameworkxt.android.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name FragmentDialog
 * @description The Fragment Use As Dialog
 * @date 2014-8-26
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class FragmentDialog extends DialogFragment {
	private FragmentDialog() {}

	public static FragmentDialog newInstance(int resTitle) {
		FragmentDialog dialog = new FragmentDialog();
		Bundle args = new Bundle();
		args.putInt("title", resTitle);
		dialog.setArguments(args);
		return dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, 0);
	}

	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int resTitle = getArguments().getInt("title");
		return new AlertDialog.Builder(getActivity()).setIcon(android.R.drawable.ic_dialog_alert).setTitle(resTitle)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {}
				}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {}
				}).create();
	}

}
