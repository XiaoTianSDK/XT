package com.xiaotian.frameworkxt.android.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name SQLIdInt
 * @description SQL Table Integer Id AutoInstance
 * @date 2014-3-17
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SQLId {

	public static final String DEFAULTSYNTAXID = " INTEGER PRIMARY KEY AUTOINCREMENT ";

	public String value() default "_id";

	public String syntax() default DEFAULTSYNTAXID;

	public abstract SQLColumnType type() default SQLColumnType.INTEGER;
}
