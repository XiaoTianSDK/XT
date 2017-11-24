package com.xiaotian.frameworkxt.android.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name SQLColumn
 * @description Table Column Row Property
 * @date 2013-11-5
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SQLColumn {
	public static final String DEFAULTVALUE = "NULL";

	public abstract String defaultValue() default DEFAULTVALUE;

	public abstract String value() default DEFAULTVALUE;

	public abstract String name() default DEFAULTVALUE;

	public abstract SQLColumnType type() default SQLColumnType.TEXT;

	public abstract boolean persistent() default true;

	public abstract boolean nullable() default true;

	public abstract boolean unique() default false;
}
