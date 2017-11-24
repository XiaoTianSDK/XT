package com.xiaotian.frameworkxt.android.model.provider;

import android.net.Uri;
import android.util.SparseArray;

import com.xiaotian.frameworkxt.android.util.UtilUri;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name UtilSQLContentProviderUri
 * @description Util Content Provider URI
 * @date 2014-7-11
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class UtilContentUriMatcher {
	// @SQLContentProvider content://authority/
	// {database name}/{table name}/{type}/{params}/...
	public static final int TYPE_ADD = 0X001;
	public static final int TYPE_DELETE = 0X002;
	public static final int TYPE_UPDATE = 0X003;
	public static final int TYPE_QUERY = 0X004;
	public static final int TYPE_ADD_UPDATE = 0X005;
	//
	private UtilUri utilUri;
	private SparseArray<Uri> cacheBaseUri;

	public UtilContentUriMatcher() {
		utilUri = new UtilUri();
		cacheBaseUri = new SparseArray<Uri>();
	}

	/**
	 * 匹配返回的 Change 类型
	 */
	public int matchChangeType(Uri uri) {
		String type = getOnChangeType(uri);
		if (type == null || type.length() < 1) return -1;
		return Integer.parseInt(type);
	}

	public String getUriAuthority(Uri uri) {
		return uri.getAuthority();
	}

	public String getDatabaseName(Uri uri) {
		return uri.getPathSegments().get(0);
	}

	public String getTableName(Uri uri) {
		return uri.getPathSegments().get(1);
	}

	public String getOnChangeType(Uri uri) {
		return uri.getPathSegments().get(2);
	}

	public String getOnChangeId(Uri uri) {
		return uri.getPathSegments().get(3);
	}

	// Create Content Uri
	public Uri createAddContentUri(String authority, String databaseName, String tableName, String id) {
		Uri uri = utilUri.withAppendedPath(getContentBaseUri(authority, databaseName, tableName), String.valueOf(TYPE_ADD));
		return id == null ? uri : Uri.withAppendedPath(uri, id);
	}

	public Uri createDeleteContentUri(String authority, String databaseName, String tableName, String id) {
		Uri uri = utilUri.withAppendedPath(getContentBaseUri(authority, databaseName, tableName), String.valueOf(TYPE_DELETE));
		return id == null ? uri : Uri.withAppendedPath(uri, id);
	}

	public Uri createUpdateContentUri(String authority, String databaseName, String tableName, String id) {
		Uri uri = utilUri.withAppendedPath(getContentBaseUri(authority, databaseName, tableName), String.valueOf(TYPE_UPDATE));
		return id == null ? uri : Uri.withAppendedPath(uri, id);
	}

	public Uri createQueryContentUri(String authority, String databaseName, String tableName) {
		return utilUri.withAppendedPath(getContentBaseUri(authority, databaseName, tableName), String.valueOf(TYPE_UPDATE));
	}

	public Uri createAddUpdateContentUri(String authority, String databaseName, String tableName, int addCount, int updateCount) {
		return utilUri.withAppendedPath(getContentBaseUri(authority, databaseName, tableName), String.valueOf(TYPE_ADD_UPDATE),
				String.valueOf(addCount), String.valueOf(updateCount));
	}

	public Uri getContentBaseUri(String authority, String databaseName, String tableName) {
		Uri baseUri = cacheBaseUri.get(getKey(databaseName, tableName));
		if (baseUri == null) {
			baseUri = utilUri.parse("content://" + authority, databaseName, tableName);
			cacheBaseUri.append(getKey(databaseName, tableName), baseUri);
		}
		return baseUri;
	}

	private int getKey(String... key) {
		return key.hashCode();
	}
}
