package com.xiaotian.framework.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.sun.mail.imap.ACL;
import com.xiaotian.framework.common.Constants;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @description 权限
 * @date 2016/5/20
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2016 小天天 Studio, All Rights Reserved.
 */
public class UtilPermission {
    Activity mActivity;

    public UtilPermission(Activity activity) {
        this.mActivity = activity;
    }

    // 请求网络权限
    public boolean requestInternet() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.INTERNET)) {
            // Show Rationale
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.INTERNET}, Constants.PERMISSIONS.ALL);
        } else {
            //
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.INTERNET}, Constants.PERMISSIONS.ALL);
        }
        return false;
    }

    // 请求摄像权限
    public boolean requestCamera() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.CAMERA)) {
            // Show Rationale
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA}, Constants.PERMISSIONS.ALL);
        } else {
            //
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA}, Constants.PERMISSIONS.ALL);
        }
        return false;
    }

    // 存储权限
    public boolean requestStore() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Show Rationale
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.PERMISSIONS.ALL);
        } else {
            //
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.PERMISSIONS.ALL);
        }
        return false;
    }

    // 录音权限
    public boolean requestRecorder() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.RECORD_AUDIO)) {
            // Show Rationale
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.RECORD_AUDIO}, Constants.PERMISSIONS.ALL);
        } else {
            //
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.RECORD_AUDIO}, Constants.PERMISSIONS.ALL);
        }
        return false;
    }

    // 定位权限
    public boolean requestLocation() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Show Rationale
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.PERMISSIONS.ALL);
        } else {
            //
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.PERMISSIONS.ALL);
        }
        return false;
    }

    // 读取联系人
    public boolean requestContact() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.READ_CONTACTS)) {
            // Show Rationale
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_CONTACTS}, Constants.PERMISSIONS.ALL);
        } else {
            //
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_CONTACTS}, Constants.PERMISSIONS.ALL);
        }
        return false;
    }
}
