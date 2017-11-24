package com.xiaotian.frameworkxt.android.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;

import com.xiaotian.frameworkxt.android.common.Mylog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @name UtilEnvironment
 * @description Environment Util By XiaoTian
 * @date 2014-4-15
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class UtilEnvironment extends Environment {
    private Context mContext;

    public UtilEnvironment() {
        this.mContext = UtilEnvironment.getCurrentApplicationContext();
    }

    public UtilEnvironment(Context context) {
        this.mContext = context;
    }

    /******************************************** Class Package Method ********************************************/
    public List<ResolveInfo> getRunningMainLauncherAPP() {
        PackageManager pm = mContext.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfo = pm.queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY);
        Collections.sort(resolveInfo, new ResolveInfo.DisplayNameComparator(pm));
        return resolveInfo;
    }

    public List<ApplicationInfo> getInstalledAPP() {
        PackageManager pm = mContext.getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(apps, new ApplicationInfo.DisplayNameComparator(pm));
        return apps;
    }

    /******************************************** Class APP Method ********************************************/
    // Action Bar Height
    public int getActionBarHeight() {
        TypedValue tv = new TypedValue();
        if (mContext.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, mContext.getResources().getDisplayMetrics());
        }
        return 0;
    }

    public String getSIMPhoneNumber() {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getLine1Number();
    }

    protected void getAllMountExternalStorage() throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec("mount");
        InputStream is = proc.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        String line;
        String mount = new String();
        BufferedReader br = new BufferedReader(isr);
        while ((line = br.readLine()) != null) {
            Mylog.info(line);
            if (line.contains("secure")) continue;
            if (line.contains("asec")) continue;

            if (line.contains("fat")) {// TF card
                String columns[] = line.split(" ");
                if (columns != null && columns.length > 1) {
                    mount = mount.concat("*" + columns[1] + "\n");
                }
            } else if (line.contains("fuse")) {// internal storage
                String columns[] = line.split(" ");
                if (columns != null && columns.length > 1) {
                    mount = mount.concat(columns[1] + "\n");
                }
            }
        }
        Mylog.info(mount);
    }

    // 是否屏幕锁屏
    public boolean isScreenOn() {
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        return pm.isScreenOn();
    }

    // 唤醒屏幕
    public void wakeLockScreen() {

    }

    // 解锁屏幕
    public void unLockScreen() {

    }

    // 锁定屏幕
    public void lockScreen() {

    }

    // The Application is available/can reachable the application
    public boolean isAPPAvailable(String packageName) {
        PackageManager pm = mContext.getPackageManager();
        try {
            pm.getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    // 移动网络的唯一设备ID
    // for example, the IMEI for GSM and the MEID or ESN for CDMA phones.
    public String getTelephoneDeviceId() {
        //
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    // 移动网络的序列号
    // for example, the IMSI for a GSM phone.
    public String getTelephoneSubscriberId() {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSubscriberId();
    }

    // 设备系统序列号
    // ANDROID_ID
    public String getAndroidId() {
        // Settings.Secure.ANDROID_ID 是一串64位的编码（十六进制的字符串），是随机生成的设备的第一个引导，其记录着一个固定值，通过它可以知道设备的寿命（在设备恢复出厂设置后，该值可能会改变）
        return Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    // 程序信息,Application Info
    public String getApplicationInfo() {
        return String.format("%s\n%s\n%s\n%s\n%s\n%s\n", getCountry(), getBrandInfo(), getModeInfo(), getDeviceInfo(), getVersionInfo(), getLocale());
    }

    // 屏幕亮度,Screen Brightness
    public void changeScreenBrightness(Window window, float brightness) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.screenBrightness = brightness; // 设置屏幕亮度[0.0-1.0]
        window.setAttributes(params);
    }

    // 主板信息
    public String getBrandInfo() {
        return String.format("Brand: %s", Build.BRAND);
    }

    // 国家
    public String getCountry() {
        TelephonyManager mTelephoneMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        return String.format("Country: %s", mTelephoneMgr.getNetworkCountryIso());
    }

    public String getDeviceInfo() {
        return String.format("Device: %s", Build.DEVICE);
    }

    // 型号
    public String getModeInfo() {
        return String.format("Model: %s", Build.MODEL);
    }

    // 版本信息
    public String getVersionInfo() {
        TelephonyManager mTelephoneMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        return String.format("Version: %s", mTelephoneMgr.getDeviceSoftwareVersion());
    }

    // 所在地区
    public String getLocale() {
        return String.format("Locale: %s", mContext.getResources().getConfiguration().locale.getDisplayName());
    }

    // 网络可用
    public boolean networkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED);
    }

    // WiFi网络
    public boolean networkIsWiFi() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED) {
            WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            return wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED;
        }
        return false;
    }

    // 获取当前APK版本名
    public String getCurrentVersionName(String... key) {
        String versionName = null;
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo info = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            versionName = info.versionName;
        } catch (NameNotFoundException e) {
            Mylog.printStackTrace(e);
            versionName = "1.0.0";
        }
        if (key.length > 0) {
            return String.format("%1$s%2$s", key[0], versionName);
        } else {
            return versionName;
        }
    }

    public String getCurrentVersionName(int versionLength, String pattern) {
        String versionName = null;
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo info = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            versionName = info.versionName;
            if (versionName != null && versionName.length() > versionLength) {
                int end = versionLength >= versionName.length() ? versionName.length() - 1 : versionLength;
                versionName = versionName.substring(0, end);
            }
            return String.format(pattern, versionName);
        } catch (NameNotFoundException e) {
            Mylog.printStackTrace(e);
            versionName = "1.0.0";
        } catch (Exception e) {
            return versionName;
        }
        return versionName;
    }

    // 获取当前APK版本号
    public int getCurrentVersionCode() {
        int code = -1;
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo info = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            code = info.versionCode;
        } catch (NameNotFoundException e) {
            Mylog.printStackTrace(e);
        }
        return code;
    }

    public DisplayMetrics getDisplayMetrics() {
        return mContext.getResources().getDisplayMetrics();
    }

    public DisplayMetrics getDisplayMetrics(Activity activity) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics;
    }

    // 反射获取上下文
    public static Context getCurrentApplicationContext() {
        try {
            return (Context) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null, (Object[]) null);
        } catch (final Exception e1) {
            try {
                return (Context) Class.forName("android.app.AppGlobals").getMethod("getInitialApplication").invoke(null, (Object[]) null);
            } catch (final Exception e2) {
                Mylog.printStackTrace(e2);
                return null;
            }
        }
    }

    /******************************************** Class Environment Method ********************************************/
    private static File externalStoragePath = null;
    private static String externalStoragePathString = null;

    // Android 4.0 Ever External Storage - External SDCard
    public static File getExternalStorageDirectory() {
        if (externalStoragePath != null) return externalStoragePath;
        try {
            // 扩展SD卡,从外挂载的系统文件截取
            String externalStoragePath = getExternalStoragePath();
            // 内部SD
            if (externalStoragePath == null) return Environment.getExternalStorageDirectory();
            File externalPath = new File(externalStoragePath);
            File tempExternalPathFile = new File(externalPath, "test.dat"); // 可以写访问,并创建
            if (tempExternalPathFile.createNewFile()) {
                // 截取的SD有效
                tempExternalPathFile.deleteOnExit();
                return externalPath;
            } else {
                // 截取的SD无效
                return Environment.getExternalStorageDirectory();
            }
        } catch (IOException e) {
            // 内部SD
            return Environment.getExternalStorageDirectory();
        }
    }

    // 根据挂载配置文件获取挂载的 SDCard[可能被篡改,不可用]
    private static String getExternalStoragePath() throws IOException {
        if (externalStoragePathString != null) return externalStoragePathString;
        String line;
        String sdCard = null;
        FileReader fr = null;
        BufferedReader br = null;
        String exts = Environment.getExternalStorageDirectory().getPath();
        try {
            // Mounts Config File
            fr = new FileReader(new File("/proc/mounts"));
            br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure") || line.contains("asec")) continue;
                if (line.contains("fat")) { // TF Card
                    String[] pars = line.split("\\s");
                    if (pars.length < 2) continue;
                    if (pars[1].equals(exts)) continue;
                    sdCard = pars[1];
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fr.close();
            br.close();
        }
        return externalStoragePathString = sdCard;
    }

    // 根据进程ID获取APP名称 : android.os.Process.myPid();
    public String getPackageName(int processId) {
        String processName = null;
        ActivityManager am = (ActivityManager) mContext.getSystemService(Activity.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = mContext.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == processId) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }
}
