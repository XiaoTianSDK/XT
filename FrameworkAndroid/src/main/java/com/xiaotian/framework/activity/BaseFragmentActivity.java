package com.xiaotian.framework.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.xiaotian.framework.R;
import com.xiaotian.framework.common.Constants;
import com.xiaotian.framework.control.DialogCustom;
import com.xiaotian.framework.control.DialogGenerator;
import com.xiaotian.framework.control.DialogGenerator.DialogListenerAlert;
import com.xiaotian.framework.control.DialogGenerator.DialogListenerInput;
import com.xiaotian.framework.control.DialogGenerator.InputDialogTheme;
import com.xiaotian.framework.control.DialogGeneratorFragment;
import com.xiaotian.framework.service.BRToast;
import com.xiaotian.framework.util.UtilDateTime;
import com.xiaotian.framework.util.UtilExternalStore;
import com.xiaotian.framework.util.UtilImage;
import com.xiaotian.framework.util.UtilPermission;
import com.xiaotian.framework.util.UtilResource;
import com.xiaotian.framework.util.UtilString;
import com.xiaotian.framework.util.UtilTextSpan;
import com.xiaotian.frameworkxt.android.common.MyOnClickListener;
import com.xiaotian.frameworkxt.android.common.Mylog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @name BaseFragmentActivity
 * @description The Base Fragment Activity
 * @date 2014-8-26
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class BaseFragmentActivity extends com.xiaotian.frameworkxt.android.BaseFragmentActivity implements DialogListenerAlert {
    public static final String INITIALIZING_TYPE = "com.xiaotian.framework.activity.BaseFragmentActivity.INITIALIZING_TYPE";
    public static final String RESULT_TYPE = "com.xiaotian.framework.activity.BaseFragmentActivity.RESULT_TYPE";
    private static final Object LOCK = new Object();
    private Map<String, DialogFragment> dialogs = new HashMap<String, DialogFragment>();
    protected int resBtBgSelectImageNegative;
    protected int resBtBgSelectImagePositive;
    protected String videoNameCapture;
    private boolean isActivityActive;
    private String imageNameCapture;
    private DialogFragment dialog;
    private int resumeUpdateTypeCode;
    private long exitConfirmTime;
    private UtilTextSpan mUtilTextSpan;
    private Intent paddingPermissionIntent;
    //
    private UtilImage mUtilImage;
    private UtilString mUtilString;
    private UtilResource mUtilResource;
    private UtilDateTime mUtilDateTime;
    private UtilPermission mUtilPermission;
    private UtilExternalStore mUtilExternalStore;
    // 队列Dialog处理,控制新Dialog弹出同时Dismiss旧Dialog无法执行问题

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isActivityActive = true;
    }

    protected void initializingView() {}

    protected void initializingData() {}

    @Override
    protected void onResume() {
        isActivityActive = true;
        super.onResume();
        if (!dialogs.isEmpty()) getHandler().post(new Runnable() {
            @Override
            public void run() {
                Iterator<String> keys = dialogs.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    Fragment prev = getSupportFragmentManager().findFragmentByTag(key);
                    if (prev != null) ft.remove(prev);
                    ft.addToBackStack(null);
                    dialogs.get(key).show(getSupportFragmentManager(), key);
                }
                dialogs.clear();
            }
        });
    }

    @Override
    protected void onPostResume() {
        isActivityActive = true;
        super.onPostResume();
        if (!dialogs.isEmpty()) getHandler().post(new Runnable() {
            @Override
            public void run() {
                Iterator<String> keys = dialogs.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    Fragment prev = getSupportFragmentManager().findFragmentByTag(key);
                    if (prev != null) ft.remove(prev);
                    ft.addToBackStack(null);
                    dialogs.get(key).show(getSupportFragmentManager(), key);
                }
                dialogs.clear();
            }
        });
    }

    @Override
    protected void onPause() {
        isActivityActive = false;
        super.onPause();
    }

    /********************************* Dialog *********************************/
    public void inputText(int restitle, final DialogListenerInput listener) {
        int width = getResources().getDisplayMetrics().widthPixels * 8 / 10;
        DialogCustom mDialog = DialogGenerator.initializingCenterDialogInput(this, restitle, InputDialogTheme.SINGLEEDIT, listener);
        mDialog.setSize(width, 0, TypedValue.COMPLEX_UNIT_PX);
        mDialog.setDialogId(R.id.id_dialog_input_edit);
        mDialog.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog = DialogGeneratorFragment.newInstance(mDialog);
        showDialogIfActive(Constants.TAG.DIALOG, dialog);
    }

    public void inputText(int restitle, DialogListenerInput listener, int widthPx, int heightPx) {
        DialogCustom mDialog = DialogGenerator.initializingCenterDialogInput(this, restitle, InputDialogTheme.SINGLEEDIT);
        mDialog.setSize(widthPx, heightPx);
        mDialog.setDialogId(R.id.id_dialog_input_edit);
        dialog = DialogGeneratorFragment.newInstance(mDialog);
        showDialogIfActive(Constants.TAG.DIALOG, dialog);
    }

    public void inputDate(int restitle, DialogListenerInput listener) {
        inputDate(getString(restitle), listener);
    }

    public void inputDate(String title, DialogListenerInput listener) {
        int width = getResources().getDisplayMetrics().widthPixels * 9 / 10;
        DialogCustom mDialog = DialogGenerator.initializingCenterDialogInput(this, title, InputDialogTheme.DATE, listener);
        mDialog.setDialogId(R.id.id_dialog_input_date);
        mDialog.setWidth(width);
        mDialog.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog = DialogGeneratorFragment.newInstance(mDialog);
        showDialogIfActive(Constants.TAG.DIALOG, dialog);
    }

    public void inputDate(int restitle, Date date, DialogListenerInput listener) {
        inputDate(getString(restitle), date, listener);
    }

    public void inputDate(String title, Date date, DialogListenerInput listener) {
        int width = getResources().getDisplayMetrics().widthPixels * 9 / 10;
        DialogCustom mDialog = DialogGenerator.initializingCenterDialogInput(this, title, InputDialogTheme.DATE, listener);
        DatePicker dp = (DatePicker) mDialog.findViewById(R.id.id_1);
        Calendar c = Calendar.getInstance(Locale.CHINA);
        c.setTime(date);
        dp.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        mDialog.setDialogId(R.id.id_dialog_input_date);
        mDialog.setWidth(width);
        mDialog.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog = DialogGeneratorFragment.newInstance(mDialog);
        showDialogIfActive(Constants.TAG.DIALOG, dialog);
    }

    public void inputDate(String title, Date date, Date minDate, Date maxDate, DialogListenerInput listener) {
        int width = getResources().getDisplayMetrics().widthPixels * 9 / 10;
        DialogCustom mDialog = DialogGenerator.initializingCenterDialogInput(this, title, InputDialogTheme.DATE, listener);
        DatePicker dp = (DatePicker) mDialog.findViewById(R.id.id_1);
        Calendar c = Calendar.getInstance(Locale.CHINA);
        c.setTime(date);
        dp.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dp.setMinDate(minDate.getTime());
        dp.setMaxDate(maxDate.getTime());
        mDialog.setDialogId(R.id.id_dialog_input_date);
        mDialog.setWidth(width);
        mDialog.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog = DialogGeneratorFragment.newInstance(mDialog);
        showDialogIfActive(Constants.TAG.DIALOG, dialog);
    }

    public void inputTime(int restitle, DialogListenerInput listener) {
        inputTime(getString(restitle), listener);
    }

    public void inputTime(String title, DialogListenerInput listener) {
        int width = getResources().getDisplayMetrics().widthPixels * 9 / 10;
        DialogCustom mDialog = DialogGenerator.initializingCenterDialogInput(this, title, InputDialogTheme.TIME, listener);
        mDialog.setDialogId(R.id.id_dialog_input_date);
        mDialog.setWidth(width);
        mDialog.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog = DialogGeneratorFragment.newInstance(mDialog);
        showDialogIfActive(Constants.TAG.DIALOG, dialog);
    }

    public void inputDateTime(String title, Date date, DialogListenerInput listener) {
        int width = getResources().getDisplayMetrics().widthPixels * 9 / 10;
        DialogCustom mDialog = DialogGenerator.initializingCenterDialogInput(this, title, InputDialogTheme.DATETIME, listener);
        DatePicker dp = (DatePicker) mDialog.findViewById(R.id.id_1);
        TimePicker tp = (TimePicker) mDialog.findViewById(R.id.id_2);
        Calendar c = Calendar.getInstance(Locale.CHINA);
        c.setTime(date);
        dp.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        tp.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
        tp.setCurrentMinute(c.get(Calendar.MINUTE));
        mDialog.setDialogId(R.id.id_dialog_input_date);
        mDialog.setWidth(width);
        mDialog.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog = DialogGeneratorFragment.newInstance(mDialog);
        showDialogIfActive(Constants.TAG.DIALOG, dialog);
    }

    public void alert(int resmsgid, DialogListenerAlert... listener) {
        // 没有Title的Alert,默认大小
        alert(resmsgid, 230f, 135f, listener.length < 1 ? this : listener[0]);
    }

    public void alert(int resmsgid, float width, float height, DialogListenerAlert... listener) {
        DialogCustom mDialog = DialogGenerator.initializingCenterDialogAlert(this, null, resmsgid, listener.length < 1 ? this : listener[0]);
        mDialog.setDialogId(R.id.id_0);
        mDialog.setSize(width, height, TypedValue.COMPLEX_UNIT_DIP);
        dialog = DialogGeneratorFragment.newInstance(mDialog);
        showDialogIfActive(Constants.TAG.DIALOG, dialog);
    }

    public void alert(int restitleid, int resmsgid, DialogListenerAlert... listener) {
        // 默认大小的Alert
        alert(restitleid, resmsgid, 260f, 160f, listener.length < 1 ? this : listener[0]);
    }

    public void alert(int restitleid, int resmsgid, float width, float height, DialogListenerAlert... listener) {
        // 指定大小的Alert
        DialogCustom mDialog = DialogGenerator.initializingCenterDialogAlert(this, restitleid, resmsgid, listener);
        mDialog.setSize(width, height, TypedValue.COMPLEX_UNIT_DIP);
        mDialog.setDialogId(R.id.id_0);
        dialog = DialogGeneratorFragment.newInstance(mDialog);
        showDialogIfActive(Constants.TAG.DIALOG, dialog);
    }

    public void alert(String message, DialogListenerAlert... listener) {
        alert(message, 220f, 130f, listener.length < 1 ? this : listener[0]);
    }

    public void alert(String message, float width, float height, DialogListenerAlert... listener) {
        DialogCustom mDialog = DialogGenerator.initializingCenterDialogAlert(this, null, message, listener.length < 1 ? this : listener[0]);
        mDialog.setDialogId(R.id.id_0);
        mDialog.setSize(width, height, TypedValue.COMPLEX_UNIT_DIP);
        dialog = DialogGeneratorFragment.newInstance(mDialog);
        showDialogIfActive(Constants.TAG.DIALOG, dialog);
    }

    public void alert(int restitleid, String message, DialogListenerAlert... listener) {
        alert(restitleid, message, 260f, 160f, listener.length < 1 ? this : listener[0]);
    }

    public void alert(int restitleid, String message, float width, float height, DialogListenerAlert... listener) {
        DialogCustom mDialog = DialogGenerator.initializingCenterDialogAlert(this, restitleid, message, listener);
        mDialog.setSize(width, height, TypedValue.COMPLEX_UNIT_DIP);
        mDialog.setDialogId(R.id.id_0);
        dialog = DialogGeneratorFragment.newInstance(mDialog);
        showDialogIfActive(Constants.TAG.DIALOG, dialog);
    }

    public void confirmDialog(int resmsgid, DialogGenerator.DialogListenerConfirm listener) {
        confirmDialog(resmsgid, listener, 200f, 160f);
    }

    public void confirmDialog(int resmsgid, int positive, int nagetive, DialogGenerator.DialogListenerConfirm listener) {
        confirmDialog(resmsgid, listener, 200f, 160f);
    }

    public void confirmDialog(int restitleid, int resmsgid, DialogGenerator.DialogListenerConfirm listener) {
        confirmDialog(restitleid, resmsgid, listener, 200f, 160f);
    }

    public void confirmDialog(int resmsgid, DialogGenerator.DialogListenerConfirm listener, float width, float height) {
        DialogCustom mDialog = DialogGenerator.initializingCenterDialogConfirm(this, null, resmsgid, listener);
        mDialog.setDialogId(R.id.id_0);
        mDialog.setSize(width, height, TypedValue.COMPLEX_UNIT_SP);
        dialog = DialogGeneratorFragment.newInstance(mDialog);
        showDialogIfActive(Constants.TAG.DIALOG, dialog);
    }

    public void confirmDialog(int resmsgid, int positive, int nagetive, DialogGenerator.DialogListenerConfirm listener, float width, float height) {
        DialogCustom mDialog = DialogGenerator.initializingCenterDialogConfirm(this, null, resmsgid, positive, nagetive, listener);
        mDialog.setDialogId(R.id.id_0);
        mDialog.setSize(width, height, TypedValue.COMPLEX_UNIT_SP);
        dialog = DialogGeneratorFragment.newInstance(mDialog);
        showDialogIfActive(Constants.TAG.DIALOG, dialog);
    }

    public void confirmDialog(int restitleid, int resmsgid, DialogGenerator.DialogListenerConfirm listener, float width, float height) {
        showDialogIfActive(Constants.TAG.DIALOG, DialogGeneratorFragment.newInstance(DialogGenerator.initializingCenterDialogConfirm(this, restitleid, resmsgid, listener)));
    }

    @Override
    public boolean onClickAlterPositive(View view) {
        return true;
    }

    public void sendBroadcastToast(int content, long... time) {
        sendBroadcastToast(getResources().getString(content), time);
    }

    public void sendBroadcastToast(String content, long... time) {
        // Toast 广播
        Intent intent = new Intent(BRToast.ACTION);
        intent.putExtra(BRToast.PARAM_CONTENT, content);
        if (time.length > 0) intent.putExtra(BRToast.PARAM_SHOW_TIME, time[0]);
        sendBroadcast(intent);
    }

    public void startActivityForImageCapture(String saveFilePath, int requestCode) {
        // 系统拍照
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this, "设备不支持拍照", Toast.LENGTH_SHORT).show();
            return;
        }
        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(saveFilePath)));
        intent.putExtra(MediaStore.EXTRA_MEDIA_ALBUM, "XiaoTian");
        intent.putExtra(MediaStore.EXTRA_MEDIA_ARTIST, "XiaoTian");
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                // Show explanation ?
                paddingPermissionIntent = intent;
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, Constants.PERMISSIONS.CAMERA);
            } else {
                paddingPermissionIntent = intent;
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, Constants.PERMISSIONS.CAMERA);
            }
        } else {
            startActivityForResult(intent, requestCode);
        }
    }

    public void startActivityForVideoCapture(String saveFilePath, int requestCode) {
        // 系统录像
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this, "设备不支持录像", Toast.LENGTH_SHORT).show();
            return;
        }
        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(saveFilePath)));
        intent.putExtra(MediaStore.EXTRA_MEDIA_ALBUM, "XiaoTian");
        intent.putExtra(MediaStore.EXTRA_MEDIA_ARTIST, "XiaoTian");
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                // Show Rationale
                paddingPermissionIntent = intent;
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, Constants.PERMISSIONS.ALL);
            } else {
                paddingPermissionIntent = intent;
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, Constants.PERMISSIONS.ALL);
            }
        } else {
            startActivityForResult(intent, requestCode);
        }
    }

    public void startActivityForImageCapture(String saveImageName) {
        startActivityForImageCapture(saveImageName, Constants.REQUEST_CODE.IMAGE_CAPTURE);
    }

    public void startActivityForVideoCapture(String saveVideoName) {
        startActivityForVideoCapture(saveVideoName, Constants.REQUEST_CODE.VIDEO_CAPTURE);
    }

    public void confirmSelectImage() {
        DialogCustom mDialog = new DialogCustom(this, R.style.style_dialog_theme_xiaotian);
        View root = LayoutInflater.from(this).inflate(R.layout.dialog_select_image, new LinearLayout(this), true);
        mDialog.setContentView(root);
        mDialog.setCancelable(true);
        mDialog.setWidth(getResources().getDisplayMetrics().widthPixels * 9 / 10);
        DialogFragment dialog = DialogGeneratorFragment.newInstance(mDialog);
        Button bt = (Button) root.findViewById(R.id.id_0);
        if (resBtBgSelectImagePositive > 0) bt.setBackgroundResource(resBtBgSelectImagePositive);
        bt.setOnClickListener(new MyOnClickListener<DialogFragment>(dialog) {
            @Override
            public void onClick(View v) {
                getInitParams(0).dismiss();
                startActivityForImageCapture(genImageNameCapturePathImagePool());
            }
        });
        bt = (Button) root.findViewById(R.id.id_1);
        if (resBtBgSelectImagePositive > 0) bt.setBackgroundResource(resBtBgSelectImagePositive);
        bt.setOnClickListener(new MyOnClickListener<DialogFragment>(dialog) {
            @Override
            public void onClick(View v) {
                getInitParams(0).dismiss();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                String title = getString(R.string.string_select_image);
                startActivityForResult(Intent.createChooser(intent, title), Constants.REQUEST_CODE.IMAGE_SELECT);
            }
        });
        bt = (Button) root.findViewById(R.id.id_nagetive);
        if (resBtBgSelectImageNegative > 0) bt.setBackgroundResource(resBtBgSelectImageNegative);
        bt.setOnClickListener(new MyOnClickListener<DialogFragment>(dialog) {
            @Override
            public void onClick(View v) {
                getInitParams(0).dismiss();
            }
        });
        showDialogIfActive(Constants.TAG.CONFIRM_IMAGE, dialog);
    }

    public void confirmSelectVideo() {
        DialogCustom mDialog = new DialogCustom(this, R.style.style_dialog_theme_xiaotian);
        View root = LayoutInflater.from(this).inflate(R.layout.dialog_select_video, new LinearLayout(this), true);
        mDialog.setContentView(root);
        mDialog.setCancelable(true);
        mDialog.setWidth(getResources().getDisplayMetrics().widthPixels * 9 / 10);
        DialogFragment dialog = DialogGeneratorFragment.newInstance(mDialog);
        Button bt = (Button) root.findViewById(R.id.id_0);
        if (resBtBgSelectImagePositive > 0) bt.setBackgroundResource(resBtBgSelectImagePositive);
        bt.setOnClickListener(new MyOnClickListener<DialogFragment>(dialog) {
            @Override
            public void onClick(View v) {
                getInitParams(0).dismiss();
                startActivityForVideoCapture(genVideoNameCapturePathVideoPool());
            }
        });
        bt = (Button) root.findViewById(R.id.id_1);
        if (resBtBgSelectImagePositive > 0) bt.setBackgroundResource(resBtBgSelectImagePositive);
        bt.setOnClickListener(new MyOnClickListener<DialogFragment>(dialog) {
            @Override
            public void onClick(View v) {
                getInitParams(0).dismiss();
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                String title = getString(R.string.string_select_video);
                startActivityForResult(Intent.createChooser(intent, title), Constants.REQUEST_CODE.VIDEO_SELECT);
            }
        });
        bt = (Button) root.findViewById(R.id.id_nagetive);
        if (resBtBgSelectImageNegative > 0) bt.setBackgroundResource(resBtBgSelectImageNegative);
        bt.setOnClickListener(new MyOnClickListener<DialogFragment>(dialog) {
            @Override
            public void onClick(View v) {
                getInitParams(0).dismiss();
            }
        });
        showDialogIfActive(Constants.TAG.CONFIRM_VIDEO, dialog);
    }

    public boolean confirmExitApplication() {
        if (System.currentTimeMillis() - exitConfirmTime > 2000) {
            exitConfirmTime = System.currentTimeMillis();
            toast("请再按一次退出程序");
            return false;
        } else {
            return true;
        }
    }

    public void clearCacheImagePoolFile() {
        synchronized (LOCK) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File file = new File(getUtilExternalStore().getImagePoolPath());
                    if (!file.exists()) return;
                    deleteCachePoolFiles(file);
                }

                void deleteCachePoolFiles(File file) {
                    if (file.isDirectory()) {
                        File[] files = file.listFiles();
                        for (File f : files) {
                            deleteCachePoolFiles(f);
                        }
                    }
                    file.delete();
                }
            }).start();
        }
    }

    public void showDialogIfActive(String TAG, DialogFragment dialog) {
        if (isActivityActive) {
            try {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag(TAG);
                if (prev != null) ft.remove(prev);
                ft.addToBackStack(null);
                dialog.show(ft, TAG);
            } catch (Exception e) {
            }
        } else {
            dialogs.put(TAG, dialog);
        }
    }

    public void dismissDialog(String tag) {
        android.support.v4.app.Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.remove(prev);
            ft.commitAllowingStateLoss();
        }
    }

    // Resume Type Code
    public int resumeUpdateTypeCode() {
        synchronized (LOCK) {
            try {
                return resumeUpdateTypeCode;
            } finally {
                resumeUpdateTypeCode = Integer.MIN_VALUE;
            }
        }
    }

    public int queryResumeUpdateTypeCode() {
        return resumeUpdateTypeCode;
    }

    public void setResumeUpdateTypeCode(int resumeUpdateTypeCode) {
        synchronized (LOCK) {
            this.resumeUpdateTypeCode = resumeUpdateTypeCode;
        }
    }

    // Activity Intent
    public void startActivity(Class<?> tagActivity, Bundle... extras) {
        Intent intent = new Intent(getBaseContext(), tagActivity);
        if (extras.length > 0) intent.putExtras(extras[0]);
        startActivity(intent);
    }

    public void startActivity(Class<?> tagActivity, int requestCode, Bundle... extras) {
        Intent intent = new Intent(getBaseContext(), tagActivity);
        if (extras.length > 0) intent.putExtras(extras[0]);
        startActivityForResult(intent, requestCode);
    }

    public String genImageNameCapturePathImagePool() {
        return this.imageNameCapture = getPathImagePool(getUtilImage().buildRandomFileName("jpg"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // 授权回调
        switch (requestCode) {
            case Constants.PERMISSIONS.CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the contacts-related task you need to do.
                    if (paddingPermissionIntent != null) startActivity(paddingPermissionIntent);
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                }
                return;
            }
        }
    }

    /************************************************** Getter **************************************************/
    public UtilString getUtilString() {
        if (mUtilString != null) return mUtilString;
        return mUtilString = new UtilString(this);
    }

    public UtilImage getUtilImage() {
        if (mUtilImage != null) return mUtilImage;
        return mUtilImage = new UtilImage();
    }

    public UtilExternalStore getUtilExternalStore() {
        if (mUtilExternalStore != null) return mUtilExternalStore;
        return mUtilExternalStore = new UtilExternalStore(this);
    }

    public UtilResource getUtilResource() {
        if (mUtilResource != null) return mUtilResource;
        return mUtilResource = new UtilResource(this);
    }

    public UtilDateTime getUtilDateTime() {
        if (mUtilDateTime != null) return mUtilDateTime;
        return mUtilDateTime = new UtilDateTime();
    }

    public UtilTextSpan getUtilTextSpan() {
        if (mUtilTextSpan != null) return mUtilTextSpan;
        return mUtilTextSpan = new UtilTextSpan();
    }

    public UtilPermission getUtilPermission() {
        if (mUtilPermission != null) return mUtilPermission;
        return mUtilPermission = new UtilPermission(getActivity());
    }

    public float getDimen(int id) {
        return getResources().getDimension(id);
    }

    public int getDimenPixel(int id) {
        return getResources().getDimensionPixelSize(id);
    }

    public BaseFragmentActivity getActivity() {
        return this;
    }

    // 获取系统选择文件回话的选中文件
    public String getConfirmSelectedImageFilePath(Uri selectedImageFileUri) {
        try {
            // this will only work for images selected from gallery
            if (Build.VERSION.SDK_INT < 19) {
                // V4.4以前的版本
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImageFileUri, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String sourceFile = c.getString(columnIndex);
                c.close();
                getUtilFile().saveToFile(genImageNameCapturePathImagePool(), new FileInputStream(new File(sourceFile)));
                return getImageNameCapture();
            } else {
                // V.4.4后[不能获取文件Path]
                try {
                    // 拷贝到缓冲文件
                    getUtilFile().saveToFile(genImageNameCapturePathImagePool(), getContentResolver().openInputStream(selectedImageFileUri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
                return getImageNameCapture();
            }
        } catch (Exception e) {
            return null;
        }
    }

    // 获取系统选择文件回话的选中文件
    public String getConfirmSelectedVideoFilePath(Uri selectedImageFileUri) {
        try {
            // this will only work for images selected from gallery
            if (Build.VERSION.SDK_INT < 19) {
                // V4.4以前的版本
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImageFileUri, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String sourceFile = c.getString(columnIndex);
                c.close();
                getUtilFile().saveToFile(genVideoNameCapturePathVideoPool(), new FileInputStream(new File(sourceFile)));
                return getVideoNameCapture();
            } else {
                // V.4.4后[不能获取文件Path]
                try {
                    // 拷贝到缓冲文件
                    getUtilFile().saveToFile(genVideoNameCapturePathVideoPool(), getContentResolver().openInputStream(selectedImageFileUri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
                return getVideoNameCapture();
            }
        } catch (Exception e) {
            toast(R.string.string_dialog_error_file_video_invalid);
            return null;
        }
    }

    public String getPathImagePool(String... filename) {
        if (filename.length > 0) {
            return getUtilExternalStore().getImagePoolPath(filename[0]);
        } else {
            return getUtilExternalStore().getImagePoolPath();
        }
    }

    public String getPathVideoPool(String... filename) {
        if (filename.length > 0) {
            return getUtilExternalStore().getVideoPoolPath(filename[0]);
        } else {
            return getUtilExternalStore().getVideoPoolPath();
        }
    }

    public String genVideoNameCapturePathVideoPool() {
        return this.videoNameCapture = getPathVideoPool(getUtilImage().buildRandomFileName("mp4"));
    }

    public String getVideoNameCapture() {
        return videoNameCapture;
    }

    public void setVideoNameCapture(String videoNameCapture) {
        this.videoNameCapture = videoNameCapture;
    }

    public String getImageNameCapture() {
        return imageNameCapture;
    }

    public void setImageNameCapture(String imageNameCapture) {
        this.imageNameCapture = imageNameCapture;
    }

    public boolean isActivityActive() {
        return isActivityActive;
    }
}
