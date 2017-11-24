package com.xiaotian.frameworkxt.util;

import java.text.DecimalFormat;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name UtilDecimal
 * @description 浮点数操作类
 * @date 2014-7-7
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class UtilDecimal {

	// 格式化浮点数不要0拖尾
	public String getTrailingZeroe(double value) {
		return new DecimalFormat("0.##").format(value);
	}

	// 指定匹配式格式化浮点数
	public String format(String pattern, double value) {
		return new DecimalFormat(pattern).format(value);
	}
	// Symbol Description
	// 0 : a digit
	// # : a digit, zero shows as absent
	// . : placeholder for decimal separator
	// , : placeholder for grouping separator
	// E : separates mantissa and exponent for exponential formats
	// ; : separates formats
	// - : default negative prefix
	// % : multiply by 100 and show as percentage
	// ? : multiply by 1000 and show as per mille
	// ¤ : currency sign; replaced by currency symbol; if doubled, replaced by
	// international currency symbol; if present in a pattern, the monetary
	// decimal separator is used instead of the decimal separator
	//
	// X : any other characters can be used in the prefix or suffix
	// ' : used to quote special characters in a prefix or suffix
}
