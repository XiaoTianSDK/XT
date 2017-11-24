package com.xiaotian.frameworkxt.common;

/**
 * 
 * @version 1.0.0
 * @author XiaoTian
 * @name SysOut
 * @description System Out
 * @date Oct 29, 2014
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2009-2014 广州隽永贸易科技 Ltd, All Rights Reserved.
 */
public class MyOut {
	public static final String TAG = "XiaoTianXT";
	static boolean isPrintout = true;
	static MyOuter mo = new MyOuter();
	static {
		mo.setIsPrintout(isPrintout);
	}

	public static void info(String message) {
		mo.info(TAG, message);
	}

	public static void info(Object message) {
		mo.info(TAG, message);
	}

	public static void info(Object[] message, Boolean... newLine) {
		mo.info(message, newLine);
	}

	public static void info(String TAG, String message) {
		mo.info(TAG, message);
	}

	public static void info(String TAG, Object message) {
		mo.info(TAG, message);
	}

	public static void info(String key, Object... value) {
		mo.info(key, value);
	}

	public static void info(String key, String... value) {
		mo.info(key, value);
	}

	public static void info(String TAG, String name, String value) {
		mo.info(TAG, name, value);
	}

	public static void info(String TAG, String name, Object value) {
		mo.info(TAG, name, value);
	}

	public static void info(String TAG, Object[] messages, Boolean newLine) {
		mo.info(TAG, messages, newLine);
	}

	public static void infoShortNewLine(String data) {
		mo.infoShortNewLine(data);
	}

	public static void infoClassField(Object data) {
		mo.infoClassField(TAG, data);
	}

	public static void infoTime(String key) {
		mo.infoTime(key);
	}

	public static StringBuffer getStringBuffer() {
		return mo.getStringBuffer();
	}

	public static void printStackTrace(Exception e) {
		mo.printStackTrace(e);
	}
}
