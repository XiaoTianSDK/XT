package com.xiaotian.frameworkxt.android.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class UtilSystemIntent {

    // 选择发送 Email
    public static void sendEmailIntent(Context context, String[] receiveEmail, String intentTitle, String emailSubject, String emailBody) {
        Intent msg = new Intent(Intent.ACTION_SEND);
        msg.putExtra(Intent.EXTRA_EMAIL, receiveEmail);
        msg.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        msg.putExtra(Intent.EXTRA_TEXT, emailBody);
        msg.setType("message/rfc822"); // 消息
        context.startActivity(Intent.createChooser(msg, intentTitle));
    }

    // Android市场 APP
    public static void openAPPDetailFromMarket(Context context, String packageName) {
        try {
            Intent market = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=dummy"));
            PackageManager manager = context.getPackageManager();
            List<ResolveInfo> list = manager.queryIntentActivities(market, 0);
            if (list.size() > 0) {
                // 已安装市场
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("market://details?id=%1$s", packageName))));
            } else {
                // 没安装市场
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Market not installed", Toast.LENGTH_SHORT).show();
        }
    }

    // 剪切照片
    public static void cropImage(Context context, String imagePath, String savePath, int imageWidth, int imageHeight) {
        File fileTo = new File(savePath);
        File fileFrom = new File(imagePath);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setData(Uri.fromFile(fileFrom));
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        // this defines the aspect ration
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // this defines the output bitmap size
        intent.putExtra("outputX", 60);
        intent.putExtra("outputY", 60);
        // true to return a Bitmap, false to directly save the cropped iamge
        intent.putExtra("return-data", false);
        // save output image in uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileTo));
        context.startActivity(intent);
    }

    // 打电话
    public static void dialing(Context context, String phoneNumber) {
        Uri callUri = Uri.parse("tel:" + phoneNumber);
        Intent intent = new Intent(Intent.ACTION_DIAL, callUri);
        context.startActivity(intent);
    }

    // 发短信
    public static void sendSMS(Context context, String phoneNumber, String sms) {
        Uri callUri = Uri.parse("sms:" + phoneNumber);
        Intent intent = new Intent(Intent.ACTION_VIEW, callUri);
        intent.putExtra("sms_body", sms);
        intent.setType("vnd.android-dir/mms-sms");
        context.startActivity(intent);
    }

    // 安装APK
    public static void installingAPK(Context context, File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    // 打开视频
    public static void playVideo(Context context, String file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(file)), "video/*");
        context.startActivity(intent);
    }

    // 打开视频录制
    public static void recordVideo(Context context, String file) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(file)));
        context.startActivity(intent);
    }

    // 打开已知的类型文件
    public static void openKnowedFile(Context context, File file) {
        Uri fileUri = Uri.fromFile(file);
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(fileUri.toString());// URI扩展名
        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension); // 扩展名的MIME
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(fileUri, mimetype);
        context.startActivity(intent);
    }

    // 打开设置
    public static void openSetting(Context context) {
        context.startActivity(new Intent(Settings.ACTION_SETTINGS));
    }

    // 打开网络无线配置
    public static void openSettingWireless(Context context) {
        context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
    }

    // 打开网络WIFI配置
    public static void openSettingWifi(Context context) {
        context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

}
