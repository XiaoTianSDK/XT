package com.xiaotian.frameworkxt.android.model.provider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name SQLContentProvider
 * @description SQL Content Provider 配置 ContentProvider
 * @date 2014-7-8
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
// Policy : the annotation retained time
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SQLContentProvider {
	/*
	 * Content providers are one of the primary building blocks of Android applications, providing content to applications. They encapsulate data and provide it to applications
	 * through the single ContentResolver interface. A content provider is only required if you need to share data between multiple applications. For example, the contacts data is
	 * used by multiple applications and must be stored in a content provider. If you don't need to share data amongst multiple applications you can use a database directly via
	 * SQLiteDatabase.
	 */
	public static final String PATTERN_CONTENT_URI = "content://%1$s/%2$s";// content://{authority}/{content_path}
	public static final String DEFAULT_DATABASE = "defaultDB";
	public static final String DEFAULT_PATH = "";

	/**
	 * AUTHORITY 访问域目录
	 */
	public abstract String authorities();

	/**
	 * CONTENT_Path 统一访问资源目录内容
	 */
	public String contentPath() default DEFAULT_PATH;

	// XML provider :
	// name :供应者类
	// authorities :注册URI资源域
	// exported :对外app开放
}
// RetentionPolicy:
// CLASS
// Annotations are to be recorded in the class file by the compiler but need not
// be retained by the VM at run time.
// RUNTIME
// Annotations are to be recorded in the class file by the compiler and retained
// by the VM at run time, so they may be read reflectively.
// SOURCE
// Annotations are to be discarded by the compiler.