package com.xiaotian.frameworkxt.util;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name UtilNumber
 * @description 数字操作类
 * @date 2013-11-12
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
public class UtilNumber {

	public Float paserFloat(Character text) {
		return paserFloat(text.toString());
	}

	public Float paserFloat(String text) {
		try {
			return Float.parseFloat(text);
		} catch (Exception e) {
			return null;
		}
	}

	// 中文 转换
	// 一~ 九十九
	public int parserChineseNumber(String cnumber) {
		// 1位[一,二,三...九,十]
		cnumber = cnumber.trim();
		if (cnumber.length() == 1) {
			return parserChinessSingleNumber(cnumber.charAt(0));
		}
		// 2位[十一,十二...二十..三十,四十,五十,六十,七十,八十,九十]
		if (cnumber.length() == 2) {
			char t = cnumber.charAt(0);
			if (t == '十') {
				return parserChinessSingleNumber(t) + parserChinessSingleNumber(cnumber.charAt(1));
			} else {
				return parserChinessSingleNumber(t) * 10;
			}
		}
		// 3位[二十一,二十二...三十一,..九十九]
		if (cnumber.length() == 3) {
			return parserChinessSingleNumber(cnumber.charAt(0)) * 10 + parserChinessSingleNumber(cnumber.charAt(2));
		}
		return -1;
	}

	public String formatChineseNumber(int number) {
		if (number > 99) return null;
		// 1位
		if (number <= 10) {
			return String.valueOf(parserChinessSingleNumber(number));
		}
		// 2位
		if (number < 20) {
			return String.valueOf(new char[] { parserChinessSingleNumber(10), parserChinessSingleNumber(number % 10) });
		}
		switch (number) {
		case 20:
		case 30:
		case 40:
		case 50:
		case 60:
		case 70:
		case 80:
		case 90:
			return String.valueOf(new char[] { parserChinessSingleNumber(number / 10), parserChinessSingleNumber(10) });
		default:
			return String.valueOf(new char[] { parserChinessSingleNumber(number / 10), parserChinessSingleNumber(10),
					parserChinessSingleNumber(number % 10) });
		}

	}

	public int parserChinessSingleNumber(char number) {
		switch (number) {
		case '零':
			return 0;
		case '一':
			return 1;
		case '二':
			return 2;
		case '三':
			return 3;
		case '四':
			return 4;
		case '五':
			return 5;
		case '六':
			return 6;
		case '七':
			return 7;
		case '八':
			return 8;
		case '九':
			return 9;
		case '十':
			return 10;
		default:
			return -1;
		}
	}

	public char parserChinessSingleNumber(int number) {
		switch (number) {
		case 0:
			return '零';
		case 1:
			return '一';
		case 2:
			return '二';
		case 3:
			return '三';
		case 4:
			return '四';
		case 5:
			return '五';
		case 6:
			return '六';
		case 7:
			return '七';
		case 8:
			return '八';
		case 9:
			return '九';
		case 10:
			return '十';
		case 100:
			return '百';
		default:
			return 0;
		}
	}
}
