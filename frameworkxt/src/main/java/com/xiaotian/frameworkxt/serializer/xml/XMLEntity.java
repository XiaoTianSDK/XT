package com.xiaotian.frameworkxt.serializer.xml;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name XMLSerial
 * @description XML Serialize Able
 * @date 2014-4-16
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface XMLEntity {

	public static final String DEFAULT_NAME = "TAG_NAME";

	public abstract String value() default DEFAULT_NAME;

	public abstract String name() default DEFAULT_NAME;

	public String[] attribute() default {};
}
