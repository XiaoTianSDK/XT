package com.xiaotian.framework.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import com.xiaotian.framework.model.UncatchedException;
import com.xiaotian.frameworkxt.android.model.UtilSQLEntityAnnotation;
import com.xiaotian.frameworkxt.android.model.provider.UtilSQLContentProviderAnnotation;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Locale;

/**
 * @author Administrator
 * @version 1.0.0
 * @name BRUncatchedException
 * @description 未扑捉异常
 * @date 2015-6-12
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class UtilUncatchedException {
    Context mContext;
    UtilSQLEntityAnnotation<UncatchedException> utilEntity = new UtilSQLEntityAnnotation<UncatchedException>() {
        @Override
        public Class<?> getExtendsedClass() {
            return getClass();
        }
    };

    public UtilUncatchedException(Context context) {
        this.mContext = context;
    }

    public void catchUncaughtException(Thread thread, Throwable throwable) {
        UncatchedException exp = new UncatchedException();
        StringBuffer exception = new StringBuffer();
        exception.append(getErrorInformation(thread, (Exception) throwable));
        exception.append(getSystemInformation());
        exp.setEmailSubject(buildSubject((Exception) throwable));
        exp.setEmailContent(exception.toString());
        ContentValues values = utilEntity.serialize(exp);
        Uri contentUri = UtilSQLContentProviderAnnotation.getContentURI(UncatchedException.class);
        mContext.getContentResolver().insert(contentUri, values);
    }

    public String buildSubject(Exception exception) {
        StringBuilder sb = new StringBuilder();
        // 20121024 12:23
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        sb.append(String.format("FrameworkAndroid-%1$tY%<tm%<1td%<tH:%<tM:%<tS", calendar));
        sb.append("(SDK.");
        sb.append(Build.VERSION.SDK_INT);
        sb.append(")");
        sb.append(getDeviceName());
        try {
            int versionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_CONFIGURATIONS).versionCode;
            sb.append(String.format("(V.%1$d)", versionCode));
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        sb.append(exception.getClass().getName());
        return sb.toString();
    }

    // 设备信息
    public String getSystemInformation() {
        // 当前系统信息{系统版本,手机类型,手机系统名,SDK版本}
        StringBuilder sb = new StringBuilder();
        int versionCode = -1;
        String versionName = "error";
        try {
            PackageInfo pi = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            versionCode = pi.versionCode;
            versionName = pi.versionName;
        } catch (Exception ignore) {
        }
        sb.append("<b style=\"font-size:16px\">System Information:</b><br/>");
        sb.append("<table border='1' margin-right:5px>");
        sb.append(createTableHeader(new String[]{" System Property Configuration Name ", " Property Configuration Value "}));
        // System
        sb.append(createTableRow("System SDK Version", Build.VERSION.SDK_INT));
        sb.append(createTableRow("Volunteer System Version Code", versionCode));
        sb.append(createTableRow("Volunteer System Version Name", versionName));
        sb.append(createTableRow("System SDK Release", Build.VERSION.RELEASE));
        sb.append(createTableRow("System SDK Name", Build.VERSION.CODENAME));
        sb.append(createTableRow("Connected NetWork", getNetwork()));
        sb.append(createTableRow("Hardware Driver System", getDeviceName()));
        boolean booleanValue = mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
        sb.append(createTableRow("Hardware Main Camera", booleanValue ? "Support" : "UnSupport"));
        // Scream
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        float size = (float) (Math.sqrt(Math.pow(dm.heightPixels, 2) + Math.pow(dm.widthPixels, 2)) / (160 * dm.density));
        sb.append(createTableRow("Hardware Driver Scream", String.format("%1$dx%2$d Pixel, %3$.2f xdip, %4$.1f inch ", dm.widthPixels, dm.heightPixels, dm.density, size)));
        // Phone
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        sb.append(createTableRow("IMEI", String.valueOf(tm.getDeviceId())));
        sb.append(createTableRow("Number", String.valueOf(tm.getLine1Number())));
        sb.append(createTableRow("Model", Build.MODEL));
        sb.append(createTableRow("Product", Build.PRODUCT));
        sb.append(createTableRow("Manufacturer", Build.MANUFACTURER));
        sb.append(createTableRow("Board", Build.BOARD));
        sb.append(createTableRow("Hardware", Build.HARDWARE));
        sb.append(createTableRow("Bootloader", Build.BOOTLOADER));
        sb.append(createTableRow("ID", Build.ID));
        // Sim Card
        sb.append(createTableRow("Telephone Network Type", tm.getNetworkType()));
        sb.append(createTableRow("Telephone Network Operator", tm.getNetworkOperator()));
        sb.append(createTableRow("Telephone Network Country Iso", tm.getNetworkCountryIso()));
        sb.append(createTableRow("Telephone Network Operator Name", tm.getNetworkOperatorName()));
        sb.append(createTableRow("Telephone Device Software Version", tm.getDeviceSoftwareVersion()));
        sb.append(createTableRow("Telephone Sim Country Iso", tm.getSimCountryIso()));
        sb.append(createTableRow("Telephone Sim Operator", tm.getSimOperator()));
        sb.append(createTableRow("Telephone Sim Operator Name", tm.getSimOperatorName()));
        sb.append(createTableRow("Telephone Sim Serial Number", tm.getSimSerialNumber()));
        sb.append(createTableRow("Telephone Sim State", tm.getSimState()));
        sb.append(createTableRow("Telephone Network Roaming", tm.isNetworkRoaming() ? "Roaming" : "UnRoaming"));
        sb.append("</table><br/>");
        return sb.toString();
    }

    // 异常信息
    public String getErrorInformation(Thread thread, Throwable e) {
        // {程序版本信息,崩溃的包名,方法名,原因}
        StringBuilder sb = new StringBuilder();
        sb.append("<b style=\"font-size:16px\">Exception Message:</b><br/>");
        sb.append("<table border='1' width=\"100%\" style=\"margin-right:5px\">");
        sb.append(createTableHeader(new String[]{" Thread ID ", " Thread Name ", " Thread State "}));
        sb.append(createTableRowSimple(String.valueOf(thread.getId()), thread.getName(), thread.getState().toString()));
        //
        sb.append("<tr><td colspan=\"4\"> Thread Ground : ");
        sb.append(thread.getThreadGroup());
        sb.append("</td></tr><tr><th colspan=\"4\">::Exception Stack Trace::</th></tr>");
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        sb.append("<tr><td colspan=\"4\">");
        sb.append(errors.toString());
        sb.append("</td></tr></table><br/>");
        return sb.toString();
    }

    public String getDeviceId() {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    public String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public String createTableRow(String string, int intData) {
        return createTableRow(string, String.valueOf(intData));
    }

    public String createTableRowSimple(String... rowdata) {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr>");
        for (int i = 0; i < rowdata.length; i++) {
            sb.append("<td>");
            sb.append(rowdata[i]);
            sb.append("</td>");
        }
        sb.append("</tr>");
        return sb.toString();
    }

    public String createTableRow(String... rowdata) {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr>");
        for (int i = 0; i < rowdata.length; i++) {
            sb.append("<td>");
            if (i == 0) {
                sb.append("<b>");
                sb.append(rowdata[i]);
                sb.append("</b>");
            } else {
                sb.append(rowdata[i]);
            }
            sb.append("</td>");
        }
        sb.append("</tr>");
        return sb.toString();
    }

    public String createTableHeader(String[] headers) {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr>");
        for (String hd : headers) {
            sb.append("<th >");
            sb.append(hd);
            sb.append("</th>");
        }
        sb.append("</tr>");
        return sb.toString();
    }

    public String getNetwork() {
        String network_type = "UNKNOWN";// maybe usb reverse tethering
        NetworkInfo active_network = ((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (active_network != null && active_network.isConnectedOrConnecting()) {
            if (active_network.getType() == ConnectivityManager.TYPE_WIFI) {
                network_type = "WIFI";
            } else if (active_network.getType() == ConnectivityManager.TYPE_MOBILE) {
                network_type = ((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo().getSubtypeName();
            }
        }
        return network_type;
    }
}