package com.xiaotian.frameworkxt.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name UtilString
 * @description 字符串操作Util
 * @date 2013-10-31
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
public class UtilString {
	// 十六进制字符组
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public String fromList(List<String> list) {
		if (list == null)
			return null;
		StringBuilder sb = new StringBuilder();
		for (String data : list) {
			sb.append(data.toString());
			if (data != list.get(list.size() - 1))
				sb.append("۩");
		}
		return sb.toString();
	}

	public List<String> parserList(String string) {
		if (string == null)
			return null;
		String[] strings = string.split("۩");
		List<String> list = new ArrayList<String>();
		for (String data : strings) {
			list.add(data);
		}
		return list;
	}

	protected String paserTextViewText(String text, String... def) {
		if (text == null) {
			return def.length > 0 ? def[0] : "";
		}
		return text;
	}

	public String paserTextViewText(Integer integerValue, Integer... def) {
		if (integerValue == null) {
			return def.length > 0 ? String.valueOf(def[0]) : "";
		}
		return String.valueOf(integerValue);
	}

	public String paserTextViewText(Long longValue, Long... def) {
		if (longValue == null) {
			return def.length > 0 ? String.valueOf(def[0]) : "";
		}
		return String.valueOf(longValue);
	}

	public String paserTextViewText(Float floatValue, Float... def) {
		if (floatValue == null) {
			return def.length > 0 ? String.valueOf(def[0]) : "";
		}
		return String.valueOf(floatValue);
	}

	public String paserTextVieweMoney(Float floatValue, Float... def) {
		if (floatValue == null) {
			return def.length > 0 ? String.format(Locale.CHINA, "%1$.2f",
					def[0]) : "";
		}
		return String.format(Locale.CHINA, "%1$.2f", floatValue);
	}

	/**************************************** public static method ****************************************/

	public static String random32HexUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	public static String subColonString(CharSequence text) {
		return subColonString(text.toString());
	}

	public static String subColonString(String text) {
		int colonIndex = text.indexOf(":");
		return text.substring(colonIndex + 1).trim();
	}

	public static String format(String pattern, Object... params) {
		return String.format(pattern, params);
	}

	public static String getNextIncreaseId(String id) {
		// 字符串后数字自增::AD1002->AD1003, AD->AD1
		String[] parts = id.split("(?=\\d+$)", 2);
		if (parts.length < 2)
			return String.format("%1$s1", id);
		final int L = parts[1].length();
		final int num = Integer.parseInt(parts[1]) + 1;
		return parts[0] + String.format("%0" + L + "d", num);
	}

	public static String[] splitTextAndNumber(String textAndNumber) {
		return textAndNumber.split("(?=\\d+$)", 2); // 文本,数据Split成两部分
	}

	public static String toHexString(byte[] array) {
		StringBuilder sb = new StringBuilder(array.length * 2);
		for (int i = 0; i < array.length; i++) {
			sb.append(HEX_DIGITS[(array[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[array[i] & 0x0f]);
		}
		return sb.toString();
	}

	public static String toMoney(double money) {
		// 保留两位小数
		return String.format("%1$.2f", money);
	}

	public static String toRetainZero(int length, int number) {
		// 用0在字符串头填充指定长度
		return String.format("%1$0" + length + "d", number);
	}
}
