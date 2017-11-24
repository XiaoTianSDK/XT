package com.xiaotian.framework.util;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;

import com.xiaotian.framework.common.Mylog;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.sql.DriverManager;
import java.util.Iterator;
import java.util.List;

public class UtilEnvironment extends com.xiaotian.frameworkxt.android.util.UtilEnvironment {
    Context mContext;

    ActivityManager mActivityManager;
    PackageManager mPackageManager;
    DriverManager mDriverManager;
    ConnectivityManager mConnectivityManager;
    TelephonyManager mTelephonyManager;
    WifiManager mWifiManager;

    public UtilEnvironment() {
        super();
    }

    public UtilEnvironment(Context context) {
        super(context);
        mContext = context;
        mPackageManager = mContext.getPackageManager();
        mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
    }

    public void demoActivityManager() {
        // 1.检索和操纵设备上的Activity
        int MAX_TASKS = Integer.MAX_VALUE;
        // 检索最近打开的Activity(最近的任务键就是调用这个方法)
        final List<ActivityManager.RecentTaskInfo> recentTasks = mActivityManager.getRecentTasks(MAX_TASKS, ActivityManager.RECENT_IGNORE_UNAVAILABLE);
        for (ActivityManager.RecentTaskInfo recentTaskInfo : recentTasks) {
            //recentTaskInfo.baseIntent; // 跳转到该Activity的Intent
            //recentTaskInfo.id; // 如果是-1，则标识这个程序没启动，其他数字表示启动了
            //recentTaskInfo.persistentId; // 任务的唯一值
        }
        // 启动任务
        //		if (ad.taskId >= 0) {
        //		    // This is an active task; it should just go to the foreground.
        //		    am.moveTaskToFront(ad.taskId, ActivityManager.MOVE_TASK_WITH_HOME,
        //		            opts);
        //		} else {
        //		    Intent intent = ad.intent;
        //		    intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
        //		            | Intent.FLAG_ACTIVITY_TASK_ON_HOME
        //		            | Intent.FLAG_ACTIVITY_NEW_TASK);
        //		    if (DEBUG) Log.v(TAG, "Starting activity " + intent);
        //		    context.startActivityAsUser(intent, opts,
        //		            new UserHandle(UserHandle.USER_CURRENT));
        //		}
        // 滑动删除任务,需系统权限android.permission.REMOVE_TASKS
        // am.removeTask(ad.persistentTaskId, ActivityManager.REMOVE_TASK_KILL_PROCESS);
        // 系统设置中的强行停止应用
        // am.forceStopPackage(pkgName);
        // 检索正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = mActivityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfos) {
            //runningAppProcessInfo.importance; //这个字段的值如果为IMPORTANCE_FOREGROUND （100）时表示为前段运行的进程，为IMPORTANCE_BACKGROUND（400）表示后台运行
            //runningAppProcessInfo.importanceReasonCode; //对进程进行解释的字段，有3个值 REASON_UNKNOWN（0） 、REASON_PROVIDER_IN_USE（1）、REASON_SERVICE_IN_USE（2）。
            //runningAppProcessInfo.importanceReasonPid; // 原因组件ID
            //runningAppProcessInfo.importanceReasonComponent;// 原因组件名
            //runningAppProcessInfo.pid; // 进程PID
            //runningAppProcessInfo.uid; // 用户ID
            //runningAppProcessInfo.processName; // 进程名(同包名)
            //runningAppProcessInfo.pkgList; // 程序主包
        }
        // 检索正在运行的任务(按执行的顺序逆排序)
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = mActivityManager.getRunningTasks(Integer.MAX_VALUE);
        for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTaskInfos) {
            //runningTaskInfo.baseActivity; // 入口,基础Activity
            //runningTaskInfo.numActivities; // 当前任务中的Activity数量
            //runningTaskInfo.numRunning; // 当前任务中正在执行的Activity数量 (not stopped and persisted)
            //runningTaskInfo.topActivity; // 栈顶Activity;
        }
        // 杀死后台进程,权限android.permission.KILL_BACKGROUND_PROCESSES
        //mActivityManager.killBackgroundProcesses(packageName);
        // 检索当前运行的服务
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = mActivityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServiceInfos) {
            //runningServiceInfo.activeSince; //第一次启动这个service到现在所过的时间
            //runningServiceInfo.pid; //进程ID
            //runningServiceInfo.uid; //用户ID
            //runningServiceInfo.lastActivityTime; //最后一次激活Service到现在的时间
            //runningServiceInfo.service; //该服务组件,可通过该组件启动服务
            //runningServiceInfo.restarting; //这个字段的值如果不是0，那么在该时间段过后自动启动这个service,0:已启动
        }
    }

    public void demoPackageManager() {
        // 1.检索和操纵已安装的程序 Application 和相关权限
        // ApplicationInfo对应程序配置<application>标签
        List<ApplicationInfo> applicationInfos = mPackageManager.getInstalledApplications(0);
        Iterator<ApplicationInfo> iterator = applicationInfos.iterator();
        while (iterator.hasNext()) {
            ApplicationInfo applicationInfo = iterator.next();
            Mylog.info("PackageName:" + applicationInfo.packageName);
        }
    }

    public void demoDriverManager() {
        // 1.驱动理器,JDBC驱动
    }

    // 判断是否已经安装程序
    public boolean isInstalledApplication(String appPackageName) {
        PackageManager pm = mContext.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(appPackageName, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    // 获取当前环境签名的MD5
    public String getAPKSignatureMD5() {
        return getAPKSignatureMD5(mContext.getPackageName());
    }

    // 获取签名的MD5
    public String getAPKSignatureMD5(String packageName) {
        byte[] signature = getAPKSignature(packageName);
        return signature == null ? null : UtilSecurity.getMD5(signature);
    }

    // 获取当前环境签名的SHA
    public String getAPKSignatureSHA() {
        return getAPKSignatureSHA(mContext.getPackageName());
    }

    // 获取签名的SHA
    public String getAPKSignatureSHA(String packageName) {
        byte[] signature = getAPKSignature(packageName);
        return signature == null ? null : UtilSecurity.getSHA(signature);
    }

    // 获取当前环境签名的SHA1
    public String getAPKSignatureSHA1() {
        return getAPKSignatureSHA1(mContext.getPackageName());
    }

    // 获取签名的SHA1
    public String getAPKSignatureSHA1(String packageName) {
        StringBuffer sb = new StringBuffer(getAPKSignatureSHA(mContext.getPackageName()).toUpperCase());
        for (int i = sb.length() - 2; i > 0; i -= 2) {
            sb.insert(i, ":");
        }
        return sb.toString();
    }

    // 获取发布公匙
    public BigInteger getAPKPublicKey() {
        String currentPackageName = mContext.getPackageName();
        PackageManager packageManager = mContext.getPackageManager();
        try {
            PackageInfo info = packageManager.getPackageInfo(currentPackageName, PackageManager.GET_SIGNATURES);
            Signature[] signs = info.signatures;
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(signs[0].toByteArray()));
            PublicKey key = cert.getPublicKey();
            return ((RSAPublicKey) key).getModulus();
        } catch (NameNotFoundException e) {
            return new BigInteger("-1");
        } catch (CertificateException e) {
            return new BigInteger("-2");
        }
    }

    // 获取签名
    public byte[] getAPKSignature() {
        return getAPKSignature(mContext.getPackageName());
    }

    // 获取APK签名码
    public byte[] getAPKSignature(String packageName) {
        PackageManager pm = mContext.getPackageManager();
        List<PackageInfo> apps = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
        Iterator<PackageInfo> iter = apps.iterator();
        while (iter.hasNext()) {
            PackageInfo info = iter.next();
            String pn = info.packageName;
            if (packageName.equals(pn)) {
                return info.signatures[0].toByteArray();
            }
        }
        return null;
    }

    // 获取发布公匙
    public String getAPKPublicKey(byte[] signature) {
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature));
            PublicKey key = cert.getPublicKey();
            return ((RSAPublicKey) key).getModulus().toString(16);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 拷贝到剪贴板
    @TargetApi(11)
    public void clipboardSaveText(String label, String text) {
        ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText(label, text));
    }

    @TargetApi(11)
    public void clipboardSaveData(ClipData clipDate) {
        ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(clipDate);
    }

    // 从黏贴板拷贝
    @TargetApi(11)
    public CharSequence clipboardGetText() {
        ClipboardManager clip = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        if (!clip.hasPrimaryClip()) return null;
        if (!clip.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) return null;
        ClipData cd = clip.getPrimaryClip();
        if (cd != null && cd.getItemCount() > 0) {
            ClipData.Item item = cd.getItemAt(0);
            if (item != null) return cd.getItemAt(0).getText();
        }
        return null;
    }

    @TargetApi(11)
    public ClipData clipboardSaveGetData() {
        ClipboardManager clip = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        return clip.getPrimaryClip();
    }

    // LayoutInflater
    public LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(mContext);
    }

    // 是否有前摄像头
    public boolean hasCameraFront() {
        return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
    }

    public int getStatusBarHeight() {
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) return mContext.getResources().getDimensionPixelSize(resourceId);
        return 0;
    }
}
