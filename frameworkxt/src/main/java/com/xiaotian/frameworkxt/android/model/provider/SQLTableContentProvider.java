package com.xiaotian.frameworkxt.android.model.provider;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.xiaotian.frameworkxt.android.common.Mylog;
import com.xiaotian.frameworkxt.android.model.SQLDataBaseHelper;
import com.xiaotian.frameworkxt.android.model.SQLTable;
import com.xiaotian.frameworkxt.android.model.UtilSQLEntityAnnotation;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name SQLTableContentProvider
 * @description SQL 注解的Content Provider
 * @date 2014-7-8
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public abstract class SQLTableContentProvider<EntityClass> extends ContentProvider {
	// 一个页面只能使用一种类型的 ContentProvider
	public static final String VALUES_DATABASE_NAME = "com.xiaotian.framework.model.provider.DATABASE_NAME";
	// SQLite ContentProvider 系统封装的ContentProvider不会产生数据库并发访问的问题,由系统调度
	// URI 规范 : {}
	protected static final int URIMATCHED_CODE_TABLE = 0X001;
	protected static final int URIMATCHED_CODE_ID = 0X002;
	// 资源访问域[使用范围权] {packge name}.provider.{provider name}
	protected SQLDataBaseHelper dataBaseHelper;
	protected SQLContentProvider contentProvider;
	// Provider private
	private UriMatcher mUriMatcher;
	// 资源访问目录[URI 资源定位]{AUTHORITY}/{database name}[/content path]
	protected Uri CONTENT_URI;
	protected String CONTENT_PATH;
	// Entity private
	private SQLTable sqlTable;
	private Class<?> entityClass;
	private String[] entityClassProjects;

	// Constructor get the
	// 无参构造器,运行时程序时初始化[只运行一次]
	public SQLTableContentProvider() {}

	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	// 增
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLDataBaseHelper dbHelper = getSQLDataBaseHelper(uri);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Mylog.info("Insert " + TABLE() + " " + values);
		long rowId = db.insert(TABLE(), null, values);
		uri = values.containsKey("_id") ? Uri.withAppendedPath(uri, String.valueOf(values.get("_id"))) : Uri.withAppendedPath(uri, String.valueOf(rowId));
		getContext().getContentResolver().notifyChange(uri, null);
		return uri;
	}

	// 删
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int rows = -1;
		SQLDataBaseHelper dbHelper = getSQLDataBaseHelper(uri);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (getUriMatcher(uri).match(uri)) {
		case URIMATCHED_CODE_TABLE:
			Mylog.info("Delete " + TABLE() + " " + selection + " " + selectionArgs);
			rows = db.delete(TABLE(), selection, selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			return rows;
		case URIMATCHED_CODE_ID:
			Mylog.info("Delete " + TABLE() + " _id=" + uri.getPathSegments().get(2));
			rows = db.delete(TABLE(), "_id=?", new String[] { uri.getPathSegments().get(2) });
			getContext().getContentResolver().notifyChange(uri, null);
			return rows;
		default:
			return -1;
		}
	}

	// 改
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLDataBaseHelper dbHelper = getSQLDataBaseHelper(uri);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int rows = -1;
		switch (getUriMatcher(uri).match(uri)) {
		case URIMATCHED_CODE_TABLE:
			Mylog.info("Update " + TABLE() + " " + selection + " " + selectionArgs + " " + values);
			rows = db.update(TABLE(), values, selection, selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			break;
		case URIMATCHED_CODE_ID:
			Mylog.info("Update " + TABLE() + " _id=" + uri.getPathSegments().get(2) + " " + values);
			rows = db.update(TABLE(), values, "_id=?", new String[] { uri.getPathSegments().get(2) });
			getContext().getContentResolver().notifyChange(uri, null);
			break;
		default:
			return -1;
		}
		return rows;
	}

	// 查询
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		// 查询一条数据
		SQLDataBaseHelper dbHelper = getSQLDataBaseHelper(uri);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(TABLE());
		Cursor cursor = null;
		switch (getUriMatcher(uri).match(uri)) {
		case URIMATCHED_CODE_TABLE:
			Mylog.info("Query " + TABLE() + " " + (projection == null ? getEntityProjects() : projection) + " " + selectionArgs + " " + selectionArgs + " " + sortOrder);
			cursor = queryBuilder.query(db, projection == null ? getEntityProjects() : projection, selection, selectionArgs, null, null, sortOrder);
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			return cursor;
		case URIMATCHED_CODE_ID:
			Mylog.info("Query " + TABLE() + " " + (projection == null ? getEntityProjects() : projection) + " _id=" + uri.getPathSegments().get(2));
			cursor = queryBuilder.query(db, projection == null ? getEntityProjects() : projection, "_id=?", new String[] { uri.getPathSegments().get(2) }, null, null, "_id ASC");
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			return cursor;
		default:
			return null;
		}
	}

	// 执行批量操作
	@Override
	public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
		return super.applyBatch(operations);
	}

	// 插入批量记录
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		SQLDataBaseHelper dbHelper = getSQLDataBaseHelper(uri);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int rows = 0;
		switch (getUriMatcher(uri).match(uri)) {
		case URIMATCHED_CODE_TABLE:
			try {
				db.beginTransaction();
				for (ContentValues value : values) {
					if (db.insert(TABLE(), null, value) > 0) rows++;
				}
				db.setTransactionSuccessful(); // Success
			} catch (Exception e) {} finally {
				db.endTransaction(); // FailedEnd
			}
			break;
		case URIMATCHED_CODE_ID:
			return -1;
		}
		return rows;
	}

	protected SQLDataBaseHelper getSQLDataBaseHelper(Uri uri) {
		if (dataBaseHelper != null) return dataBaseHelper;
		switch (getSQLTable().databaseType()) {
		case CONSTANT:
			// 1.静态配置,在类中配置
			return dataBaseHelper = new SQLDataBaseHelper(getContext(), getMappingEntityClass());
		case DYNAMIC:
			// 2.动态配置
			return dataBaseHelper = new SQLDataBaseHelper(getContext(), DATABASE_NAME(uri), getMappingEntityClass());
		default:
			throw new RuntimeException("Database Name Error.Un konw the entity class mapping database type.");
		}
	}

	public Class<?> getMappingEntityClass() {
		if (entityClass != null) return entityClass;
		ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass(); // 单层父类
		return entityClass = (Class<?>) type.getActualTypeArguments()[0]; // 第一个泛型实际类型参数
	}

	// Getter Method
	/**
	 * @param URI
	 *            If database is set for dynamic the database name is from
	 *            uri,other ignore.
	 * @return The UriMatcher will return which add URI matchered pattern
	 */
	protected UriMatcher getUriMatcher(Uri uri) {
		if (mUriMatcher != null) return mUriMatcher;
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI(AUTHORITIES(), CONTENT_PATH(uri), URIMATCHED_CODE_TABLE);// 匹配表模式
		mUriMatcher.addURI(AUTHORITIES(), CONTENT_PATH(uri, "/#"), URIMATCHED_CODE_ID);// 匹配表id模式
		return mUriMatcher;
	}

	public String DATABASE_NAME(Uri uri) {
		switch (getSQLTable().databaseType()) {
		case CONSTANT:
			// 1.静态配置,在类中配置
			return getSQLTable().databaseName();
		case DYNAMIC:
			// 2.动态配置,在Uri中第一个Path
			List<String> uriPathSegments = uri.getPathSegments();
			if (uriPathSegments.size() < 1) throw new RuntimeException("Database Name Error.The Input Uri Hasn't Database Name Path.");
			return UtilSQLContentProviderAnnotation.wrapDBToDataBaseName(uriPathSegments.get(0));
		default:
			throw new RuntimeException("Database Name Error.Un konw the entity class mapping database type.");
		}
	}

	public Uri CONTENT_URI(Uri uri) {
		// content://{authorities}/{databasename}[/contentPath]
		if (CONTENT_URI != null) return CONTENT_URI;
		switch (getSQLTable().databaseType()) {
		case CONSTANT:
			return CONTENT_URI = UtilSQLContentProviderAnnotation.getContentURI(getMappingEntityClass());
		case DYNAMIC:
			return CONTENT_URI = UtilSQLContentProviderAnnotation.getContentURI(getMappingEntityClass(), DATABASE_NAME(uri));
		default:
			throw new RuntimeException("The SQLTable's DataBase Type Un Support, You Must Only Set Type As CONSTANT OR DYNAMIC.");
		}
	}

	public String TABLE() {
		return getSQLTable().name();
	}

	public Integer VERSION() {
		return getSQLTable().version();
	}

	public String AUTHORITIES() {
		return getSQLContentProvider().authorities();
	}

	public String CONTENT_PATH(Uri uri, String... appendPath) {
		// {databasename}/{contentPath}
		if (CONTENT_PATH != null) return CONTENT_PATH;
		StringBuffer sb = new StringBuffer();
		SQLTable sqlTable = getSQLTable();
		switch (sqlTable.databaseType()) {
		case CONSTANT:
			sb.append(UtilSQLContentProviderAnnotation.cropDataBaseName(sqlTable.databaseName()));
			break;
		case DYNAMIC:
			List<String> uriPathSegments = uri.getPathSegments();
			if (uriPathSegments.size() < 1) throw new RuntimeException("Database Name Error.The Input Uri Hasn't Database Name Path.");
			sb.append(uriPathSegments.get(0));
			break;
		default:
			throw new RuntimeException("The SQLTable's DataBase Type Un Support, You Must Only Set Type As CONSTANT OR DYNAMIC.");
		}
		if (!getSQLContentProvider().contentPath().equals(SQLContentProvider.DEFAULT_PATH)) {
			sb.append("/");
			sb.append(getSQLContentProvider().contentPath());
		}
		for (String path : appendPath) {
			sb.append(path);
		}
		return sb.toString();
	}

	// The Selector Projects From The Mapping Entity
	public String[] getEntityProjects() {
		if (entityClassProjects != null) return entityClassProjects;
		entityClassProjects = UtilSQLEntityAnnotation.getSQLEntityProjects(getMappingEntityClass());
		if (sqlTable == null) {
			throw new RuntimeException("The SQLContrentProvider Entity Mapping Exception, You Must Mapping The Entity By @SQLContentProvider Tag.");
		}
		return entityClassProjects;
	}

	/****************************** 注解体 ******************************/
	protected SQLContentProvider getSQLContentProvider() {
		if (contentProvider != null) return contentProvider;
		return contentProvider = UtilSQLContentProviderAnnotation.getSQLContentProvider(getMappingEntityClass());
	}

	protected SQLTable getSQLTable() {
		if (sqlTable != null) return sqlTable;
		sqlTable = UtilSQLEntityAnnotation.getSQLTable(getMappingEntityClass());
		if (sqlTable == null) {
			throw new RuntimeException("The SQL Entity Mapping Exception, You Must Mapping The Entity By @SQLEntity Tag.");
		}
		return sqlTable;
	}

}
