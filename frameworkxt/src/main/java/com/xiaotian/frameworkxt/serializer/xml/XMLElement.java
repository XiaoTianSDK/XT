package com.xiaotian.frameworkxt.serializer.xml;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name XMLElement
 * @description
 * @date 2014-4-16
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XMLElement {
	public static final String DEFAULT_VALUE = "TAG_VALUE";
	public static final String DEFAULT_NAME = "TAG_NAME";
	public static final boolean DEFAULT_SERIALIZE = true;

	public abstract String name() default DEFAULT_NAME;

	public abstract String value() default DEFAULT_NAME;

	public abstract String defaultValue() default DEFAULT_VALUE;

	public abstract boolean serialize() default DEFAULT_SERIALIZE;

}
