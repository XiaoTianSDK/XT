package com.xiaotian.frameworkxt.android.model.provider;

import android.net.Uri;

import com.xiaotian.frameworkxt.android.model.SQLTable;
import com.xiaotian.frameworkxt.android.model.UtilSQLEntityAnnotation;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name UtilSQLContentProviderAnnotation
 * @description SQL Content Provider Annotation Util
 * @date 2014-7-8
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class UtilSQLContentProviderAnnotation {
	public UtilSQLContentProviderAnnotation() {}

	/**
	 * @param contentProviderEntityClass
	 *            The annotationed @SQLContentProvider class.
	 * @return The Param Class Annotationed SQLContentProvider, if the request class's or super class has annotated it will return the sub-est annotaion SQLContentProvider class
	 */
	public static SQLContentProvider getSQLContentProvider(Class<?> contentProviderEntityClass) {
		SQLContentProvider contentProvider = contentProviderEntityClass.getAnnotation(SQLContentProvider.class);
		while (contentProvider == null && contentProviderEntityClass != null) {
			contentProviderEntityClass = contentProviderEntityClass.getSuperclass();
			contentProvider = contentProviderEntityClass == null ? null : contentProviderEntityClass.getAnnotation(SQLContentProvider.class);
		}
		if (contentProvider == null)
			throw new RuntimeException("SQLContentProvider Error. Un found the @SQLContentProvider annotation by the class.");
		return contentProvider;
	}

	/**
	 * @param contentProviderEntityClass
	 *            The annotationed @SQLContentProvider class.
	 * @param dataBaseName
	 *            The Dynamic DataBase Name
	 * @return Uri of the @SQLContentProvider class,the format: content://{authorities}/{datbase name}[{/content Path}]
	 */
	public static Uri getContentURI(Class<?> contentProviderEntityClass, String... dataBaseName) {
		SQLContentProvider scp = getSQLContentProvider(contentProviderEntityClass);
		SQLTable stb = UtilSQLEntityAnnotation.getSQLTable(contentProviderEntityClass);
		if (scp == null) throw new RuntimeException("Annotation Exception, The @SQLContentProvider Un Found In The Entity Class.");
		if (stb == null) throw new RuntimeException("Annotation Exception, The @SQLTable Un Found In The Entity Class.");
		switch (stb.databaseType()) {
		case CONSTANT:
			if (scp.contentPath().equals(SQLContentProvider.DEFAULT_PATH)) {
				return Uri.parse(String.format("content://%1$s/%2$s", scp.authorities(), cropDataBaseName(stb.databaseName())));
			}
			return Uri.parse(String.format("content://%1$s/%2$s/%3$s", scp.authorities(), cropDataBaseName(stb.databaseName()), scp.contentPath()));
		case DYNAMIC:
			if (dataBaseName.length < 1) throw new RuntimeException("The @SQLTable DataBase Type Is DYNAMIC , But Un Input The DataBase Name.");
			if (scp.contentPath().equals(SQLContentProvider.DEFAULT_PATH)) {
				return Uri.parse(String.format("content://%1$s/%2$s", scp.authorities(), cropDataBaseName(dataBaseName[0])));
			}
			return Uri.parse(String.format("content://%1$s/%2$s/%3$s", scp.authorities(), cropDataBaseName(dataBaseName[0]), scp.contentPath()));
		default:
			throw new RuntimeException("Annotation Exception, The @SQLTable DataBase Type Un Support.");
		}
	}

	public static String cropDataBaseName(String dataBaseName) {
		return dataBaseName.endsWith(".db") ? dataBaseName.replace(".db", "") : dataBaseName;
	}

	public static String wrapDBToDataBaseName(String dataBaseName) {
		return dataBaseName.endsWith(".db") ? dataBaseName : dataBaseName + ".db";
	}
}
