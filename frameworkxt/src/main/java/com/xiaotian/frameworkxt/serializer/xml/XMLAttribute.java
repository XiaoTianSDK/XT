package com.xiaotian.frameworkxt.serializer.xml;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name XMLProperty
 * @description XML Property
 * @date 2014-4-16
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XMLAttribute {
	public static final boolean DEFAULT_SERIALIZE = true;
	public static final String DEFAULT_NAME = "PROPERTY_NAME";
	public static final String DEFAULT_VALUE = "PROPERTY_VALUE";

	public abstract String value() default DEFAULT_NAME;

	public abstract String name() default DEFAULT_NAME;

	public String defaultValue() default DEFAULT_VALUE;

	public boolean serialize() default DEFAULT_SERIALIZE;
}
