package com.xiaotian.frameworkxt.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name UtilMD5
 * @description MD5 Util
 * @date 2013-10-18
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
public class UtilMD5 {
	// 十六进制字符组
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static String MD5(String key) {
		// TODO 获取MD5十进制序列数字数组
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		digest.update(key.getBytes());
		byte messageDigest[] = digest.digest();
		return toHexString(messageDigest);
	}

	public static String MD5(byte[] data) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.update(data);
			return toHexString(digest.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static String toHexString(byte[] b) {
		// 二进制转换为16进制
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}
		return sb.toString();
	}
}
