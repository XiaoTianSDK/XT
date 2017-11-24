package com.xiaotian.frameworkxt.android.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name Id
 * @description 声明为Table的_Id列[String 类型的ID]
 * @date 2013-11-5
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SQLIdText {

	public static final String DEFAULTSYNTAXID = " TEXT PRIMARY KEY NOT NULL ";

	public String value() default "_id";

	public String syntax() default DEFAULTSYNTAXID;

	public String defaultValue() default SQLColumn.DEFAULTVALUE;

	public abstract SQLColumnType type() default SQLColumnType.TEXT;
}
