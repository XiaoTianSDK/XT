package com.xiaotian.framework.control;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.xiaotian.framework.R;
import com.xiaotian.framework.common.MyViewOnClickListener;
import com.xiaotian.frameworkxt.util.UtilDateTime;

import java.util.Calendar;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @name DialogGenerator
 * @description Dialog 生成处理器
 * @date 2013-10-10
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2009-2013 广州睿塔科技 Ltd, All Rights Reserved.
 */
public class DialogGenerator {
    public static final String TAG = "DialogGenerator";

    public enum InputDialogTheme {
        SINGLEEDIT, SELECTOR, RADIO, DATE, TIME, DATETIME, CUSTOMER
    }

    // 无内容的 Top Dialog
    @SuppressWarnings("deprecation")
    public static DialogCustom initializingTopDialog(Activity activity, int dialogTheme) {
        DialogCustom dialog = new DialogCustom(activity, dialogTheme);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.TOP;
        layoutParams.width = activity.getWindow().getWindowManager().getDefaultDisplay().getWidth();
        dialog.getWindow().setAttributes(layoutParams);
        return dialog;
    }

    // 确认Top dialog
    public static DialogCustom initializingTopDialogConfirm(Activity activity, String title, String message) {
        DialogCustom dialog = initializingTopDialog(activity, R.style.style_dialog_theme_base_xiaotian);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_confirm_xiaotian, null);
        TextView tv = null;
        if (title != null) {
            tv = (TextView) view.findViewById(R.id.id_0);
            tv.setText(title);
            tv.setVisibility(View.VISIBLE);
        }
        tv = (TextView) view.findViewById(R.id.id_1);
        tv.setText(message);
        dialog.setContentView(view);
        return dialog;
    }

    public static DialogCustom initializingTopDialogConfirm(Activity activity, String title, int messageResource) {
        return initializingTopDialogConfirm(activity, title, activity.getResources().getString(messageResource));
    }

    public static DialogCustom initializingTopDialogConfirm(Activity activity, int titleResource, String message) {
        return initializingTopDialogConfirm(activity, activity.getResources().getString(titleResource), message);
    }

    public static DialogCustom initializingTopDialogConfirm(Activity activity, int titleResource, int messageResource) {
        return initializingTopDialogConfirm(activity, activity.getResources().getString(titleResource), activity.getResources().getString(messageResource));
    }

    // 无内容 Center Dialog
    public static DialogCustom initializingCenterDialog(Activity activity, int dialogTheme) {
        DialogCustom dialog = new DialogCustom(activity, dialogTheme);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(layoutParams);
        return dialog;
    }

    // 确认 Confirm Center Dialog
    public static DialogCustom initializingCenterDialogConfirm(Activity activity, String title, String message, String positive, String nagetive, DialogListenerConfirm... listener) {
        DialogCustom dialog = null;
        if (title == null) {
            dialog = initializingCenterDialog(activity, R.style.style_dialog_theme_notitle_xiaotian);
        } else {
            dialog = initializingCenterDialog(activity, R.style.style_dialog_theme_xiaotian);
        }
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_confirm_xiaotian, null);
        TextView tv = null;
        if (title != null) {
            tv = (TextView) view.findViewById(R.id.id_0);
            tv.setText(title);
            tv.setVisibility(View.VISIBLE);
        } else {
            ((TextView) view.findViewById(R.id.id_1)).setGravity(Gravity.CENTER);
        }
        tv = (TextView) view.findViewById(R.id.id_1);
        tv.setText(message);
        // On Click Listener
        if (listener.length > 0) {
            final DialogListenerConfirm dcl = listener[0];
            view.findViewById(R.id.id_nagetive_xiaotian).setOnClickListener(new MyViewOnClickListener<DialogCustom>(dialog) {
                public void onClick(View v) {
                    if (dcl.onClickConfirmNegative(v)) {
                        DialogCustom dialog = getInitParams(0);
                        dialog.dismiss();
                    }
                }
            });
            view.findViewById(R.id.id_positive_xiaotian).setOnClickListener(new MyViewOnClickListener<DialogCustom>(dialog) {
                public void onClick(View v) {
                    if (dcl.onClickConfirmPositive(v)) {
                        DialogCustom dialog = getInitParams(0);
                        dialog.dismiss();
                    }
                }
            });
        }
        if (nagetive != null) {
            Button bt = (Button) view.findViewById(R.id.id_nagetive_xiaotian);
            bt.setText(nagetive);
        }
        if (positive != null) {
            Button bt = (Button) view.findViewById(R.id.id_positive_xiaotian);
            bt.setText(positive);
        }
        dialog.setContentView(view);
        return dialog;
    }

    public static DialogCustom initializingCenterDialogConfirm(Activity activity, String title, int messageResource, DialogListenerConfirm... listener) {
        return initializingCenterDialogConfirm(activity, title, activity.getResources().getString(messageResource), null, null, listener);
    }

    public static DialogCustom initializingCenterDialogConfirm(Activity activity, String title, int messageResource, int positiveResource, int nagetiveResource, DialogListenerConfirm... listener) {
        return initializingCenterDialogConfirm(activity, title, activity.getResources().getString(messageResource), activity.getResources().getString(positiveResource), activity.getResources()
                .getString(nagetiveResource), listener);
    }

    public static DialogCustom initializingCenterDialogConfirm(Activity activity, int titleResource, String message, DialogListenerConfirm... listener) {
        return initializingCenterDialogConfirm(activity, activity.getResources().getString(titleResource), message, null, null, listener);
    }

    public static DialogCustom initializingCenterDialogConfirm(Activity activity, int titleResource, int messageResource, DialogListenerConfirm... listener) {
        return initializingCenterDialogConfirm(activity, activity.getResources().getString(titleResource), activity.getResources().getString(messageResource), null, null, listener);
    }

    public static DialogCustom initializingCenterDialogConfirm(Activity activity, int titleResource, int messageResource, int positiveResult, int negativeResult, DialogListenerConfirm... listener) {
        return initializingCenterDialogConfirm(activity, activity.getResources().getString(titleResource), activity.getResources().getString(messageResource), activity.getResources().getString
                (positiveResult), activity.getResources().getString(negativeResult), listener);
    }

    // 提示Alert 只有确定按钮
    public static DialogCustom initializingCenterDialogAlert(Activity activity, String title, String message, final DialogListenerAlert... listener) {
        DialogCustom dialog = null;
        View root = null;
        TextView tv;
        if (title == null) {
            dialog = initializingCenterDialog(activity, R.style.style_dialog_theme_notitle_xiaotian);
            root = LayoutInflater.from(activity).inflate(R.layout.dialog_alert_notitle_xiaotian, null);
            tv = (TextView) root.findViewById(R.id.id_0);
            tv.setText(message);
        } else {
            dialog = initializingCenterDialog(activity, R.style.style_dialog_theme_xiaotian);
            root = LayoutInflater.from(activity).inflate(R.layout.dialog_alert_title_xiaotian, null);
            tv = (TextView) root.findViewById(R.id.id_0);
            tv.setText(title);
            tv = (TextView) root.findViewById(R.id.id_1);
            tv.setText(message);
        }

        root.findViewById(R.id.id_positive_xiaotian).setOnClickListener(new MyViewOnClickListener<DialogCustom>(dialog) {
            @Override
            public void onClick(View v) {
                if (listener.length > 0 && listener[0].onClickAlterPositive(v)) {
                    DialogCustom dialog = getInitParams(0);
                    dialog.dismiss();
                } else if (listener.length < 1) {
                    DialogCustom dialog = getInitParams(0);
                    dialog.dismiss();
                }
            }

        });
        dialog.setContentView(root);
        return dialog;
    }

    public static DialogCustom initializingCenterDialogAlert(Activity activity, String title, int messageResource, DialogListenerAlert... listener) {
        return initializingCenterDialogAlert(activity, title, activity.getResources().getString(messageResource), listener);
    }

    public static DialogCustom initializingCenterDialogAlert(Activity activity, int titleResource, String message, DialogListenerAlert... listener) {
        return initializingCenterDialogAlert(activity, activity.getResources().getString(titleResource), message, listener);
    }

    public static DialogCustom initializingCenterDialogAlert(Activity activity, int titleResource, int messageResource, DialogListenerAlert... listener) {
        return initializingCenterDialogAlert(activity, activity.getResources().getString(titleResource), activity.getResources().getString(messageResource), listener);
    }

    // Update Dialog Text
    public static void setTextCenterDialogAlert(DialogCustom dialog, String title, String message) {
        TextView tv;
        if (title != null) {
            tv = (TextView) dialog.findViewById(R.id.id_0);
            tv.setText(title);
        }
        if (message != null) {
            tv = (TextView) dialog.findViewById(R.id.id_1);
            tv.setText(message);
        }
    }

    public static void setTextCenterDialogAlert(DialogCustom dialog, int message) {
        setTextCenterDialogAlert(dialog, null, dialog.getContext().getResources().getString(message));
    }

    public static void setTextCenterDialogAlert(DialogCustom dialog, int title, int message) {
        setTextCenterDialogAlert(dialog, dialog.getContext().getResources().getString(title), dialog.getContext().getResources().getString(message));
    }

    // input 输入主题的Center Dialog
    public static DialogCustom initializingCenterDialogInput(Activity activity, int title, InputDialogTheme theme, DialogListenerInput... listener) {
        return initializingCenterDialogInput(activity, activity.getResources().getString(title), theme, listener);
    }

    public static DialogCustom initializingCenterDialogInput(final Activity activity, String title, InputDialogTheme theme, final DialogListenerInput... listener) {
        DialogCustom dialog = new DialogCustom(activity, R.style.style_dialog_theme_xiaotian);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(layoutParams);
        TextView textView;
        switch (theme) {
        case SINGLEEDIT:
            // TODO 单行输入框
            dialog.setContentView(R.layout.dialog_input_single_edit_xiaotian);
            dialog.findViewById(R.id.id_positive_xiaotian).setOnClickListener(new MyViewOnClickListener<DialogCustom>(dialog) {
                @Override
                public void onClick(View v) {
                    // TODO Positive
                    DialogCustom dialog = getInitParams(0);
                    EditText et = (EditText) dialog.findViewById(R.id.id_1);
                    if (listener.length > 0 && listener[0] != null) {
                        String text = et.getText().toString().trim();
                        if (listener[0].onClickInputPositive(dialog, text)) {
                            dialog.dismiss();
                        }
                    }
                }
            });
            break;
        case DATE:
            // TODO 日期选择
            dialog.setContentView(R.layout.dialog_input_date_xiaotian);
            dialog.findViewById(R.id.id_positive_xiaotian).setOnClickListener(new MyViewOnClickListener<DialogCustom>(dialog) {
                @Override
                public void onClick(View v) {
                    // TODO Positive
                    DialogCustom dialog = getInitParams(0);
                    DatePicker dp = (DatePicker) dialog.findViewById(R.id.id_1);
                    Calendar c = new UtilDateTime().getCalendar();
                    c.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
                    c.set(Calendar.MILLISECOND, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.HOUR_OF_DAY, 0);
                    if (listener.length > 0 && listener[0] != null) {
                        if (listener[0].onClickInputPositive(dialog, c.getTimeInMillis())) {
                            dialog.dismiss();
                        }
                    }
                }
            });
            break;
        case CUSTOMER:
            // TODO 自定义内部组件
            break;
        case SELECTOR:
            // TODO 多选框
            break;
        case RADIO:
            // TODO 单选框
            break;
        case TIME:
            // TODO 时间
            dialog.setContentView(R.layout.dialog_input_time_xiaotian);
            dialog.findViewById(R.id.id_positive_xiaotian).setOnClickListener(new MyViewOnClickListener<DialogCustom>(dialog) {
                @Override
                public void onClick(View v) {
                    // TODO Positive
                    DialogCustom dialog = getInitParams(0);
                    TimePicker tp = (TimePicker) dialog.findViewById(R.id.id_1);
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(System.currentTimeMillis());
                    c.set(Calendar.HOUR_OF_DAY, tp.getCurrentHour());
                    c.set(Calendar.MINUTE, tp.getCurrentMinute());
                    if (listener.length > 0 && listener[0] != null) {
                        if (listener[0].onClickInputPositive(dialog, tp.getCurrentHour(), tp.getCurrentMinute())) {
                            dialog.dismiss();
                        }
                    }
                }
            });
            break;
        case DATETIME:
            // TODO 日期时间
            dialog.setContentView(R.layout.dialog_input_datetime_xiaotian);
            dialog.findViewById(R.id.id_positive_xiaotian).setOnClickListener(new MyViewOnClickListener<DialogCustom>(dialog) {
                DatePicker dp;
                TimePicker tp;

                @Override
                public void onClick(View v) {
                    // TODO Positive
                    DialogCustom dialog = getInitParams(0);
                    dp = (DatePicker) dialog.findViewById(R.id.id_1);
                    tp = (TimePicker) dialog.findViewById(R.id.id_2);
                    Calendar c = new UtilDateTime().getCalendar();
                    c.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
                    c.set(Calendar.HOUR_OF_DAY, tp.getCurrentHour());
                    c.set(Calendar.MINUTE, tp.getCurrentMinute());
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    if (listener.length > 0 && listener[0] != null) {
                        if (listener[0].onClickInputPositive(dialog, c.getTimeInMillis())) {
                            dialog.dismiss();
                        }
                    }
                }
            });
            TimePicker tp = (TimePicker) dialog.findViewById(R.id.id_2);
            tp.setIs24HourView(true);
            break;
        default:
            break;
        }
        // Common View
        textView = (TextView) dialog.findViewById(R.id.id_0);
        textView.setText(title);
        dialog.findViewById(R.id.id_nagetive_xiaotian).setOnClickListener(new MyViewOnClickListener<DialogCustom>(dialog) {
            @Override
            public void onClick(View v) {
                // TODO Nagetive
                DialogCustom dialog = getInitParams(0);
                if (listener.length > 0 && listener[0] != null) {
                    listener[0].onClickInputNegative(dialog);
                }
                dialog.dismiss();
            }
        });
        return dialog;
    }

    // innner class interface
    public interface DialogListenerConfirm {
        public boolean onClickConfirmNegative(View view);

        public boolean onClickConfirmPositive(View view);
    }

    public interface DialogListenerAlert {
        public boolean onClickAlterPositive(View view);
    }

    public interface DialogListenerInput {
        public boolean onClickInputNegative(DialogCustom dialog);

        public boolean onClickInputPositive(DialogCustom dialog, Object... data);
    }
}
