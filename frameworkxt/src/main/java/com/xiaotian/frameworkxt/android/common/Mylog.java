package com.xiaotian.frameworkxt.android.common;

public class Mylog {
    public static boolean DEBUG = false;
    static MyLogger ml = new MyLogger();

    static {
        ml.setTAG("FrameworkXT");
        ml.setDebug(DEBUG);
        ml.setError(DEBUG);
        ml.setInfo(DEBUG);
    }

    public static void info(String message) {
        ml.info(message);
    }

    public static void info(Object message) {
        ml.info(message);
    }

    public static void info(Object[] message, Boolean... newLine) {
        ml.info(message, newLine);
    }

    public static void info(String TAG, String message) {
        ml.info(TAG, message);
    }

    public static void info(String TAG, Object message) {
        ml.info(TAG, message);
    }

    public static void info(String key, Object... value) {
        ml.info(key, value);
    }

    public static void info(String key, String... value) {
        ml.info(key, value);
    }

    public static void info(String TAG, String name, String value) {
        ml.info(TAG, name, value);
    }

    public static void info(String TAG, String name, Object value) {
        ml.info(TAG, name, value);
    }

    public static void info(String TAG, Object[] messages, Boolean newLine) {
        ml.info(TAG, messages, newLine);
    }

    public static void infoShortNewLine(String data) {
        ml.infoShortNewLine(data);
    }

    public static void infoClassField(Object data) {
        ml.infoClassField(data);
    }

    public static void infoTime(String key) {
        ml.infoTime(key);
    }

    public static void d(String TAG, Object message) {
        ml.d(message);
    }

    public static void d(Object message) {
        ml.d(message);
    }

    public static void dClassField(Object instance) {
        ml.dClassField(instance);
    }

    public static void e(String TAG, Object message) {
        ml.e(message);
    }

    public static void e(Object message) {
        ml.e(message);
    }

    public static void cleanBuffer() {
        ml.cleanBuffer();
    }

    public static void append(Object data) {
        ml.append(data);
    }

    public static void infoBuffer() {
        ml.infoBuffer();
    }

    public static StringBuffer getStringBuffer() {
        return ml.getStringBuffer();
    }

    public static void printStackTrace(Exception e) {
        ml.printStackTrace(e);
    }
}
