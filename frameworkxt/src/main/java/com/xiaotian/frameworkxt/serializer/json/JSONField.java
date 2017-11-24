package com.xiaotian.frameworkxt.serializer.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name JSONField
 * @description 注解序列化域, JSON Field
 * @date 2013-10-27
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JSONField {

	public static final int TYPE_NULL = 0X001;
	public static final int TYPE_LONG = 0X002;
	public static final int TYPE_STRING = 0X003;
	public static final int TYPE_DOUBLE = 0X004;
	public static final int TYPE_INTEGER = 0X005;
	public static final int TYPE_BOOLEAN = 0X006;

	public static final String VALUE_TRUE = "true";
	public static final String VALUE_FALSE = "false";
	public static final String DEFAULTNAME = "DEFAULT_NAME";
	// default value
	public static final String DEFAULTVALUE = "";
	public static final int DEFAULTINTEGER = 0X001;
	public static final boolean DEFAULTBOOLEAN = true;

	// set the default could unset value in mapping tager
	public abstract String name() default DEFAULTNAME;

	public abstract String defaultValue() default DEFAULTVALUE;

	public abstract boolean serialize() default DEFAULTBOOLEAN;

	// set the simple value mapping tager
	public abstract String value() default DEFAULTNAME;

	public abstract int type() default TYPE_STRING;
}