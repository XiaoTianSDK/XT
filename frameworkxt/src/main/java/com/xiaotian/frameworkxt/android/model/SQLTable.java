package com.xiaotian.frameworkxt.android.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name Table
 * @description 声明映射的Table配置参数
 * @date 2013-11-5
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SQLTable {
	public static final String DEFAULTVALUE = "";
	public static final int VERSIONCODE = 1;

	public enum DatabaseNameType {
		// TODO 数据库名称规范[常量,变量]
		CONSTANT, DYNAMIC;
	}

	public abstract String databaseName() default DEFAULTVALUE;

	public abstract String name() default DEFAULTVALUE;

	public abstract int version() default VERSIONCODE;

	public abstract String value() default DEFAULTVALUE;

	public abstract DatabaseNameType databaseType() default DatabaseNameType.CONSTANT;
}
