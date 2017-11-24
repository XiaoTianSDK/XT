package com.xiaotian.frameworkxt.android.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.util.SparseArray;

import com.xiaotian.frameworkxt.android.common.Mylog;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name SQLDataSource
 * @description SQL Data Source,数据表的数据源对象,执行增删改查的操作,匹配_id [Integer,String]
 * @date 2013-11-5
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
public class SQLDataSource {

	private DataSourceListener listener;
	private SQLDataBaseHelper helper; // Helper
	private SQLiteDatabase database; // DataBase
	private Class<?> instanceClazz; // Return Instance
	private int[] columnIndex;
	private String tablename;
	private String[] columns;
	private Field[] fields;
	private Class<?> clazz;
	private ExecuteResponse response;
	private SparseArray<CacheEntityClass> arrayEntity;

	public SQLDataSource(Context context, Class<?> clazz) throws SQLException {
		// 在类中配置数据库
		this.clazz = clazz;
		arrayEntity = new SparseArray<CacheEntityClass>();
		CacheEntityClass cec = new CacheEntityClass();
		SQLTable annotation = getAnnotationTable(clazz);
		if (annotation.value() == SQLTable.DEFAULTVALUE) {
			tablename = annotation.name();
		} else {
			tablename = annotation.value();
		}
		fields = getDeclaredFields(clazz);
		cec.tableName = tablename;
		cec.entityClass = clazz;
		cec.fields = fields;
		arrayEntity.append(clazz.hashCode(), cec);
		helper = new SQLDataBaseHelper(context, clazz);
	}

	public SQLDataSource(Context context, String dataBaseName, Class<?> clazz) throws SQLException {
		// 指定数据库
		this.clazz = clazz;
		arrayEntity = new SparseArray<CacheEntityClass>();
		SQLTable annotation = getAnnotationTable(clazz);
		if (annotation.value() == SQLTable.DEFAULTVALUE) {
			tablename = annotation.name();
		} else {
			tablename = annotation.value();
		}
		fields = getDeclaredFields(clazz);
		helper = new SQLDataBaseHelper(context, dataBaseName, clazz);
	}

	public SQLDataSource(Context context, String dataBaseName, CursorFactory factory, int version) throws SQLException {
		// 指定数据库和版本号初始化数据源
		arrayEntity = new SparseArray<CacheEntityClass>();
		helper = new SQLDataBaseHelper(context, dataBaseName, factory, version);
	}

	public void open() throws SQLiteException {
		if (listener != null) listener.onOpen(this);
		database = helper.getWritableDatabase();
		if (listener != null) listener.afterOpen(this); // Invoke open
	}

	public boolean isOpen() {
		if (database == null) return false;
		return database.isOpen();
	}

	public void close() throws SQLiteException {
		if (listener != null) listener.onClose(this);// Invoke close
		if (database != null) database.close();// First Close
		if (helper != null) helper.close(); // Second Close
		if (listener != null) listener.afterClose(this);
	}

	// 增/改,返回行的_id
	public long addOrUpdate(Object data) throws SQLException {
		// 1.判断ID是否存在
		ContentValues contentValues = buildContentValues(data);
		Object id = contentValues.get("_id");
		// 2.插入/更新
		try {
			if (containsID(id)) {
				// Update
				Mylog.info("Update " + tablename + " a row :: " + contentValues);
				if (listener != null) listener.onExecuteUpdate(this, contentValues);
				// 在onExecuteUpdate listener 后设置结果类型
				getResult().setExecuteType(ExecuteResponse.TYPE_UPDATE);
				if (id instanceof Integer) {
					String sid = String.valueOf((Integer) id);
					getResult().effectedRowUpdate = database.update(tablename, contentValues, "_id=?", new String[] { sid });
				} else if (id instanceof String) {
					getResult().effectedRowUpdate = database.update(tablename, contentValues, "_id=?", new String[] { (String) id });
				} else {
					return getResult().effectedRowUpdate = -1;
				}
				if (listener != null) listener.afterExecuteUpdate(this, contentValues);
			} else {
				// Insert
				Mylog.info("Insert " + tablename + " new row :: " + contentValues);
				if (listener != null) listener.onExecuteAdd(this, contentValues);
				getResult().setExecuteType(ExecuteResponse.TYPE_ADD);
				getResult().row = database.insert(tablename, null, contentValues);
				if (listener != null) listener.afterExecuteAdd(this, contentValues);
			}
		} catch (SQLiteException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		}
		return getResult().row;
	}

	// 添加/更新 集合,返回影响行数
	public long addOrUpdate(List<?> data) throws SQLException {
		// 开启事务管理
		int countAdd = 0, countUpdate = 0;
		getResult().setExecuteType(ExecuteResponse.TYPE_ADD_UPDATE);
		if (!database.inTransaction()) database.beginTransaction();
		try {
			for (Object dt : data) {
				// 1.判断ID是否存在
				ContentValues contentValues = buildContentValues(dt);
				Object id = contentValues.get("_id");
				// 2.插入/更新
				if (containsID(id)) {
					// Update
					Mylog.info("Update " + tablename + " a row :: " + contentValues);
					if (listener != null) listener.onExecuteUpdate(this, contentValues);
					if (id instanceof Integer) {
						String sid = String.valueOf((Integer) id);
						countUpdate += database.update(tablename, contentValues, "_id=?", new String[] { sid });
					} else if (id instanceof String) {
						countUpdate += database.update(tablename, contentValues, "_id=?", new String[] { (String) id });
					}
					if (listener != null) listener.afterExecuteUpdate(this, contentValues);
				} else {
					// Insert
					Mylog.info("Insert " + tablename + " new row : " + contentValues);
					if (listener != null) listener.onExecuteAdd(this, contentValues);
					if ((getResult().row = database.insert(tablename, null, contentValues)) > -1) {
						getResult().rowSet.add(getResult().row);
						if (listener != null) listener.afterExecuteAdd(this, contentValues);
						countAdd++;
					}
				}
			}
			getResult().effectedRowAdd = countAdd;
			getResult().effectedRowUpdate = countUpdate;
		} catch (SQLiteException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		}
		if (database.inTransaction()) {
			database.setTransactionSuccessful(); // 提交事务
			database.endTransaction(); // 结束事务
		}
		Mylog.info("Update rows = " + getResult().effectedRowUpdate);
		Mylog.info("Insert rows = " + getResult().effectedRowAdd);
		return getResult().effectedRowUpdate + getResult().effectedRowAdd;
	}

	// 增
	public long add(Object data) throws SQLException {
		ContentValues contentValues = buildContentValues(data);
		Object id = contentValues.get("_id");
		if (containsID(id)) {
			return -1;
		} else {
			Mylog.info("Insert " + tablename + " new row :: " + contentValues);
			if (listener != null) listener.onExecuteAdd(this, contentValues);
			getResult().setExecuteType(ExecuteResponse.TYPE_ADD);
			getResult().row = database.insert(tablename, null, contentValues);
			if (listener != null) listener.afterExecuteAdd(this, contentValues);
			return getResult().row;
		}
	}

	public long add(List<?> data) throws SQLException {
		int countAdd = 0, countSkip = 0;
		long rowId = 0;
		getResult().setExecuteType(ExecuteResponse.TYPE_ADD_UPDATE);
		if (!database.inTransaction()) database.beginTransaction();
		try {
			for (Object dt : data) {
				ContentValues contentValues = buildContentValues(dt);
				Object id = contentValues.get("_id");
				if (containsID(id)) {
					countSkip++;
					continue;
				} else {
					Mylog.info("Insert " + tablename + " new row :: " + contentValues);
					if (listener != null) listener.onExecuteAdd(this, contentValues);
					rowId = database.insert(tablename, null, contentValues);
					if (listener != null) listener.afterExecuteAdd(this, contentValues);
					if (rowId < 0) continue;
					getResult().rowSet.add(rowId);
					countAdd++;
				}
			}
		} catch (SQLiteException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		}
		if (database.inTransaction()) {
			database.setTransactionSuccessful(); // 提交事务
			database.endTransaction(); // 结束事务
		}
		getResult().effectedRowAdd = countAdd;
		Mylog.info("Insert rows = " + countAdd);
		Mylog.info("Skip rows = " + countSkip);
		return getResult().effectedRowUpdate + getResult().effectedRowAdd;
	}

	// 删
	public int delete(Integer id) throws SQLException {
		if (id == null) return 0;
		return delete(String.valueOf(id));
	}

	public int delete(String id) throws SQLException {
		if (listener != null) listener.onExecuteDelete(this, id);
		getResult().setExecuteType(ExecuteResponse.TYPE_DELETE);
		try {
			getResult().effectedRowDelete = database.delete(tablename, "_id=?", new String[] { id });
			if (listener != null) listener.afterExecuteDelete(this, id);
		} catch (SQLiteException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		}
		Mylog.info("delete " + tablename + " _id=" + id + " :affected rows=" + getResult().effectedRowDelete);
		return getResult().effectedRowDelete;
	}

	public int delete(List<?> ids) throws SQLException {
		String id;
		if (!database.inTransaction()) database.beginTransaction();
		getResult().setExecuteType(ExecuteResponse.TYPE_DELETE);
		try {
			for (Object oid : ids) {
				if (oid instanceof Integer) {
					id = String.valueOf((Integer) oid);
				} else if (oid instanceof String) {
					id = (String) oid;
				} else {
					continue;
				}
				if (listener != null) listener.onExecuteDelete(this, id);
				getResult().effectedRowDelete += database.delete(tablename, "_id=?", new String[] { id });
				if (listener != null) listener.afterExecuteDelete(this, id);
			}
		} catch (SQLiteException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		}
		if (database.inTransaction()) {
			database.setTransactionSuccessful();
			database.endTransaction();
		}
		Mylog.info("delete " + tablename + " _id=" + ids + " :affected rows=" + getResult().effectedRowDelete);
		return getResult().effectedRowDelete;
	}

	@Deprecated
	public int delete(String selection, String[] selectionArgs) throws SQLException {
		try {
			if (listener != null) listener.onExecuteDelete(this, selection, selectionArgs);
			else {
				// 表结构
				Cursor cursor;
				int nameIdx, typeIdx;
				String idType = null;
				cursor = database.rawQuery(String.format("PRAGMA table_info(%1$s)", tablename), null);
				if (cursor.moveToFirst()) {
					nameIdx = cursor.getColumnIndexOrThrow("name");
					while (!cursor.isAfterLast()) {
						if (cursor.getString(nameIdx).equalsIgnoreCase("_id")) {
							typeIdx = cursor.getColumnIndexOrThrow("type");
							idType = cursor.getString(typeIdx);
							cursor.close();
							break;
						} else {
							cursor.moveToNext();
						}
					}
					if (idType != null) {
						// hava _id column
						cursor.close();
						cursor = database.query(tablename, new String[] { "_id" }, selection, selectionArgs, null, null, null);
						if (cursor.moveToFirst()) {
							if ("INTEGER".equalsIgnoreCase(idType)) {
								while (!cursor.isAfterLast()) {
									int id = cursor.getInt(0);
									if (listener != null) listener.onExecuteDelete(this, String.valueOf(id));
									cursor.moveToNext();
									if (listener != null) listener.afterExecuteDelete(this, String.valueOf(id));
								}
							} else if ("TEXT".equals(idType)) {
								while (!cursor.isAfterLast()) {
									String id = cursor.getString(0);
									if (listener != null) listener.onExecuteDelete(this, id);
									cursor.moveToNext();
									if (listener != null) listener.afterExecuteDelete(this, id);
								}
							}
						}
						cursor.close();
						getResult().effectedRowDelete = database.delete(tablename, selection, selectionArgs);
					} else {
						// un have _id column
						cursor.close();
						getResult().effectedRowDelete = database.delete(tablename, selection, selectionArgs);
					}
				} else {
					// table info execute exception
					cursor.close();
					getResult().effectedRowDelete = database.delete(tablename, selection, selectionArgs);
				}
			}
			if (listener != null) listener.afterExecuteDelete(this, selection, selectionArgs);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < selectionArgs.length; i++) {
				String s = selectionArgs[i];
				sb.append(s);
				if (i < selectionArgs.length - 1) sb.append(",");
			}
			getResult().setExecuteType(ExecuteResponse.TYPE_DELETE);
			Mylog.info("delete " + tablename + " selection: " + selection + " arg :" + sb.toString() + " :affected rows="
					+ getResult().effectedRowDelete);
		} catch (SQLiteException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		}
		return getResult().effectedRowDelete;
	}

	// 改
	public int update(Object data) throws SQLException {
		ContentValues contentValues = buildContentValues(data);
		String sid;
		Object id = contentValues.get("_id");
		if (listener != null) listener.onExecuteUpdate(this, contentValues);
		getResult().setExecuteType(ExecuteResponse.TYPE_UPDATE);
		if (id instanceof Integer) {
			sid = String.valueOf((Integer) id);
		} else if (id instanceof String) {
			sid = (String) id;
		} else {
			return -1;
		}
		Mylog.info("Update " + tablename + " a row :: " + contentValues);
		getResult().effectedRowUpdate = database.update(tablename, contentValues, "_id=?", new String[] { sid });
		if (listener != null) listener.afterExecuteUpdate(this, contentValues);
		return getResult().effectedRowUpdate;
	}

	public <T extends Object> T get(Integer id) throws SQLException {
		if (id == null) return null;
		return get(id.toString());
	}

	// 查
	@SuppressWarnings("unchecked")
	public <T extends Object> T get(String id) throws SQLException {
		if (id == null) return null;
		T result;
		if (listener != null) listener.onExecuteQuery(this, tablename, getColumns(clazz), "_id=?", new String[] { id }, null, null, null, "1");
		getResult().setExecuteType(ExecuteResponse.TYPE_QUERY);
		try {
			getResult().cursor = database.query(tablename, getColumns(clazz), "_id=?", new String[] { id }, null, null, null, "1");
			if (!getResult().cursor.moveToFirst()) return null;
			columnIndex = getColumnIndex(getResult().cursor, fields);
			result = (T) setInstanceValue(getResult().cursor, getInstanceClazz().getConstructor().newInstance());
			if (listener != null)
				listener.afterExecuteQuery(this, tablename, getColumns(clazz), "_id=?", new String[] { id }, null, null, null, "1");
			return result;
		} catch (SecurityException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} catch (NoSuchMethodException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} catch (IllegalArgumentException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} catch (IllegalAccessException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} catch (InstantiationException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} catch (InvocationTargetException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} finally {
			if (getResult().cursor != null) getResult().cursor.close();
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Object> T get(String selection, String[] selectionArgs) throws SQLException {
		T result;
		if (listener != null) listener.onExecuteQuery(this, tablename, getColumns(clazz), selection, selectionArgs, null, null, null, "1");
		getResult().setExecuteType(ExecuteResponse.TYPE_QUERY);
		try {
			getResult().cursor = database.query(tablename, getColumns(clazz), selection, selectionArgs, null, null, null, "1");
			getResult().cursor = getResult().cursor;
			if (!getResult().cursor.moveToFirst()) return null;
			columnIndex = getColumnIndex(getResult().cursor, fields);
			result = (T) setInstanceValue(getResult().cursor, getInstanceClazz().getConstructor().newInstance());
			if (listener != null) listener.afterExecuteQuery(this, tablename, getColumns(clazz), selection, selectionArgs, null, null, null, "1");
			return result;
		} catch (SecurityException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} catch (NoSuchMethodException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} catch (IllegalArgumentException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} catch (IllegalAccessException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} catch (InstantiationException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} catch (InvocationTargetException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} finally {
			if (getResult().cursor != null) getResult().cursor.close();
		}
	}

	public <T extends Object> T reload(T data) throws SQLException {
		Object oId = null;
		for (Field filed : fields) {
			try {
				if (filed.getAnnotation(SQLId.class) != null) {
					filed.setAccessible(true);
					oId = filed.get(data);
					break;
				} else if (filed.getAnnotation(SQLIdText.class) != null) {
					filed.setAccessible(true);
					oId = filed.get(data);
					break;
				}
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(new SQLException(SQLException.DATABASEEXCEPTION, e));
			} catch (IllegalAccessException e) {
				throw new RuntimeException(new SQLException(SQLException.DATABASEEXCEPTION, e));
			}
		}
		if (oId == null) return data;
		String id = (oId instanceof Integer) ? String.valueOf((Integer) oId) : (oId instanceof String) ? (String) oId : null;
		if (id == null) return data;
		try {
			getResult().cursor = database.query(tablename, getColumns(clazz), "_id=?", new String[] { id }, null, null, null, "1");
			if (!getResult().cursor.moveToFirst()) return data;
			columnIndex = getColumnIndex(getResult().cursor, fields);
			return setInstanceValue(getResult().cursor, data);
		} catch (SecurityException e) {
			throw new RuntimeException(new SQLException(SQLException.DATABASEEXCEPTION, e));
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(new SQLException(SQLException.DATABASEEXCEPTION, e));
		} catch (IllegalAccessException e) {
			throw new RuntimeException(new SQLException(SQLException.DATABASEEXCEPTION, e));
		} finally {
			if (getResult().cursor != null) getResult().cursor.close();
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Object> List<T> getList(String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
			throws SQLException {
		List<T> list = null;
		if (listener != null) listener.onExecuteQuery(this, tablename, getColumns(clazz), selection, selectionArgs, groupBy, having, orderBy, limit);
		getResult().setExecuteType(ExecuteResponse.TYPE_QUERY);
		try {
			getResult().cursor = database.query(tablename, getColumns(clazz), selection, selectionArgs, groupBy, having, orderBy, limit);
			if (!getResult().cursor.moveToFirst()) {
				Mylog.info("query " + tablename + " result has 0 rows in cursor!");
				return null;
			}
			Mylog.info("query " + tablename + " result has " + getResult().cursor.getCount() + " rows in cursor!");
			list = new ArrayList<T>();
			columnIndex = getColumnIndex(getResult().cursor, fields);
			Constructor<?> constructor = getInstanceClazz().getConstructor();
			while (!getResult().cursor.isAfterLast()) {
				list.add((T) setInstanceValue(getResult().cursor, constructor.newInstance()));
				getResult().cursor.moveToNext();
			}
			if (listener != null)
				listener.afterExecuteQuery(this, tablename, getColumns(clazz), selection, selectionArgs, groupBy, having, orderBy, limit);
		} catch (SecurityException e) {
			throw new RuntimeException(new SQLException(SQLException.DATABASEEXCEPTION, e));
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(new SQLException(SQLException.DATABASEEXCEPTION, e));
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(new SQLException(SQLException.DATABASEEXCEPTION, e));
		} catch (InstantiationException e) {
			throw new RuntimeException(new SQLException(SQLException.DATABASEEXCEPTION, e));
		} catch (IllegalAccessException e) {
			throw new RuntimeException(new SQLException(SQLException.DATABASEEXCEPTION, e));
		} catch (InvocationTargetException e) {
			throw new RuntimeException(new SQLException(SQLException.DATABASEEXCEPTION, e));
		} finally {
			if (getResult().cursor != null) getResult().cursor.close();
		}
		return list;
	}

	public <T extends Object> List<T> getList(String selection, String[] selectionArgs, long start, long count) throws SQLException {
		if (start < 0) start = 0;
		if (count < 0) count = Long.MAX_VALUE;
		return getList(selection, selectionArgs, null, null, null, String.format("%1$d , %2$d", start, count));
	}

	public <T extends Object> List<T> getList(long start, long count) throws SQLException {
		return getList(null, null, start, count);
	}

	// 统计查询
	public int count(String selection, String[] selectionArgs) throws SQLException {
		String sql = "SELECT COUNT(*) FROM " + tablename + " WHERE " + selection;
		Cursor cursor = null;
		int count = 0;
		try {
			cursor = database.rawQuery(sql, selectionArgs);
			if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
				cursor.moveToFirst();
				count = cursor.getInt(0);
			}
		} catch (SQLiteException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} finally {
			if (cursor != null) cursor.close();
			cursor = null;
		}
		Mylog.info("count query : " + sql + " args=" + selectionArgs + " result count=" + count);
		return count;
	}

	public int count() throws SQLException {
		String sql = "SELECT COUNT(*) FROM " + tablename;
		int count = 0;
		Cursor cursor = null;
		try {
			cursor = database.rawQuery(sql, null);
			if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
				cursor.moveToFirst();
				count = cursor.getInt(0);
			}
		} catch (SQLiteException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} finally {
			if (cursor != null) cursor.close();
			cursor = null;
		}
		Mylog.info("count query : " + sql + " result count=" + count);
		return count;
	}

	public void executeQuery(String sql, Object... selectionArgs) {
		// 执行SQL语句::NOT a SELECT/INSERT/UPDATE/DELETE.
		// ALTER TABLE
		// CREATE or DROP table / trigger / view / index / virtual table
		// REINDEX
		// RELEASE
		// SAVEPOINT
		// PRAGMA that returns no data
		database.execSQL(sql, selectionArgs);
	}

	public void executeDelete(String table, String whereClause, String[] whereArgs) {
		if (listener == null) database.delete(table, whereClause, whereArgs);
		else {

		}
	}

	public int executeDelete(String whereClause, String[] whereArgs) {
		if (listener == null) database.delete(tablename, whereClause, whereArgs);
		return getResult().effectedRowDelete = database.delete(tablename, whereClause, whereArgs);
	}

	public int executeUpdate(ContentValues values, String whereClause, String[] whereArgs) {
		return getResult().effectedRowUpdate = database.update(tablename, values, whereClause, whereArgs);
	}

	public Cursor executeRawQuery(String sql, String[] selectionArgs) {
		// 执行SQL语句[查询],返回结果 Cursor:returns a Cursor over the result set
		return getResult().cursor = database.rawQuery(sql, selectionArgs);
	}

	public Object buildInstanceFromCursor(Cursor cursor, Class<?> clazz) throws SQLException {
		// 从当前的Cursor游标,构造Annotation注解的实体
		Constructor<?> constructor;
		try {
			constructor = getInstanceClazz().getConstructor();
			return setInstanceValue(cursor, constructor.newInstance());
		} catch (NoSuchMethodException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} catch (IllegalArgumentException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} catch (IllegalAccessException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} catch (InstantiationException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} catch (InvocationTargetException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		}
	}

	public void beginTransaction() {
		// 开始事务
		database.beginTransaction();
	}

	public void setTransactionSuccessful() {
		// 成功完成事务
		database.setTransactionSuccessful();
	}

	public boolean inTransaction() {
		// 是否开启事务
		return database.inTransaction();
	}

	public void endTransaction() {
		// 结束事务
		database.endTransaction();
	}

	// 包含查询
	private boolean containsID(Object id) {
		if (id == null) return false;
		String[] args;
		if (id instanceof String) {
			args = new String[] { (String) id };
		} else if (id instanceof Integer) {
			args = new String[] { String.valueOf((Integer) id) };
		} else {
			return false;
		}
		Cursor c = database.query(tablename, new String[] { "_id" }, "_id=?", args, null, null, null);
		int count = c.getCount();
		c.close();
		c = null;
		return count > 0;
	}

	private ContentValues buildContentValues(Object data) {
		// 内容数据
		Annotation[] annotations;
		String name;
		Object value;
		if (fields == null) {
			// extends the annotation class
			fields = getDeclaredFields(data.getClass());
		}
		ContentValues contentValues = new ContentValues();
		try {
			for (Field field : fields) {
				annotations = field.getAnnotations();
				for (Annotation annotation : annotations) {
					if (annotation instanceof SQLId) {
						SQLId column = (SQLId) annotation;
						field.setAccessible(true);
						Object oid = field.get(data); // Value
						field.setAccessible(false);
						if (oid instanceof Integer) {
							// Id Value
							contentValues.put(column.value(), (Integer) oid);
						} else if (oid instanceof Long) {
							contentValues.put(column.value(), (Long) oid);
						}
						field = null;
						annotation = null;

					} else if (annotation instanceof SQLIdText) {
						SQLIdText column = (SQLIdText) annotation;
						field.setAccessible(true);
						Object oid = field.get(data); // Value
						field.setAccessible(false);
						if (oid == null) {
							// Default Id Value
							value = parseDefaultValue(column.type(), column.defaultValue());
							if (field.getType() == String.class) {
								contentValues.put(column.value(), (String.valueOf(value)));
							}
						} else if (oid instanceof Integer) {
							// Id Value
							contentValues.put(column.value(), (Integer) oid);
						} else if (oid instanceof String) {
							contentValues.put(column.value(), (String) oid);
						} else if (oid instanceof Long) {
							contentValues.put(column.value(), (Long) oid);
						}
						field = null;
						annotation = null;
					} else if (annotation instanceof SQLColumn) {
						SQLColumn column = (SQLColumn) annotation;
						if (!column.persistent()) {
							continue;
						}
						// get column name
						if (column.value() != SQLColumn.DEFAULTVALUE) {
							// 1.value
							name = column.value();
						} else if (column.name() != SQLColumn.DEFAULTVALUE) {
							// 2.name
							name = column.name();
						} else {
							throw new RuntimeException(new SQLException(SQLException.MAPPINGEXCEPTION));
						}
						// get column value
						field.setAccessible(true);
						value = field.get(data);
						field.setAccessible(false);
						if (value == null) {
							if (column.defaultValue() != SQLColumn.DEFAULTVALUE) {
								// default value
								value = parseDefaultValue(column.type(), column.defaultValue());
							} else if (column.nullable()) {
								// null
								contentValues.putNull(name);
								annotation = null;
								value = null;
								name = null;
								continue;
							} else {
								// un nullable and null
								throw new RuntimeException(new SQLException(SQLException.UNNULLABLEERROR));
							}
						}
						// set the column value
						if (field.getType() == String.class) {
							contentValues.put(name, (String) value);
						} else if (field.getType() == Integer.class) {
							contentValues.put(name, (Integer) value);
						} else if (field.getType() == Long.class) {
							contentValues.put(name, (Long) value);
						} else if (field.getType() == Short.class) {
							contentValues.put(name, (Short) value);
						} else if (field.getType() == Double.class) {
							contentValues.put(name, (Double) value);
						} else if (field.getType() == Boolean.class) {
							contentValues.put(name, (Boolean) value);
						} else if (field.getType() == Float.class) {
							contentValues.put(name, (Float) value);
						} else if (field.getType() == Date.class) {
							String date = String.format("%1$tQ", value);
							contentValues.put(name, Integer.parseInt(date));
						}
						name = null;
						value = null;
						field = null;
						annotation = null;
					}
				}
				annotations = null;
			}
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(new SQLException(SQLException.FIELDEXCEPTION, e));
		} catch (IllegalAccessException e) {
			throw new RuntimeException(new SQLException(SQLException.FIELDEXCEPTION, e));
		}
		return contentValues;
	}

	private String[] getColumns(Class<?> clazz) {
		// get the class mapped columns
		if (columns != null) return columns;
		ArrayList<String> arrayColumn = new ArrayList<String>(1);
		arrayColumn.add("_id");
		for (Field field : fields) {
			Annotation annotation = field.getAnnotation(SQLColumn.class);
			if (annotation instanceof SQLColumn) {
				SQLColumn column = (SQLColumn) annotation;
				if (!column.persistent()) continue;
				if (column.value() != SQLColumn.DEFAULTVALUE) {
					arrayColumn.add(column.value());
				} else if (column.name() != SQLColumn.DEFAULTVALUE) {
					arrayColumn.add(column.name());
				} else {
					throw new RuntimeException(new SQLException(SQLException.MAPPINGEXCEPTION));
				}
			}
		}
		columns = new String[arrayColumn.size()];
		for (int i = 0; i < columns.length; i++) {
			columns[i] = arrayColumn.get(i);
		}
		return columns;
	}

	private Object parseDefaultValue(SQLColumnType type, String data) {
		switch (type) {
		case INTEGER:
			return Integer.parseInt(data);
		case TEXT:
			return data;
		case FLOAT:
			return Float.parseFloat(data);
		case LONG:
			return Long.parseLong(data);
		case SHORT:
			return Short.parseShort(data);
		case BLOB:
			return data.getBytes();
		case DOUBLE:
			return Double.parseDouble(data);
		}
		return null;
	}

	public <T extends Object> T setInstanceValue(Cursor cursor, T instance) throws IllegalArgumentException, IllegalAccessException {
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			Annotation[] annotations = field.getAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation instanceof SQLIdText) {
					// ID Text Column
					field.setAccessible(true);
					field.set(instance, cursor.getString(columnIndex[i]));
					field.setAccessible(false);
				} else if (annotation instanceof SQLId) {
					// ID Int Column
					field.setAccessible(true);
					field.set(instance, cursor.getInt(columnIndex[i]));
					field.setAccessible(false);
				} else if (annotation instanceof SQLColumn) {
					// Column
					SQLColumn column = (SQLColumn) annotation;
					if (!column.persistent()) break;
					field.setAccessible(true);
					switch (column.type()) {
					case TEXT:
						field.set(instance, translateMappedFieldValue(field, cursor.getString(columnIndex[i])));
						break;
					case INTEGER:
						field.set(instance, translateMappedFieldValue(field, cursor.getInt(columnIndex[i])));
						break;
					case FLOAT:
						field.set(instance, translateMappedFieldValue(field, cursor.getFloat(columnIndex[i])));
						break;
					case LONG:
						field.set(instance, translateMappedFieldValue(field, cursor.getLong(columnIndex[i])));
						break;
					case SHORT:
						field.set(instance, translateMappedFieldValue(field, cursor.getShort(columnIndex[i])));
						break;
					case DOUBLE:
						field.set(instance, translateMappedFieldValue(field, cursor.getDouble(columnIndex[i])));
						break;
					case BLOB:
						field.set(instance, translateMappedFieldValue(field, cursor.getBlob(columnIndex[i])));
						break;
					}
					field.setAccessible(false);
				}
			}
		}
		return instance;
	}

	private Object translateMappedFieldValue(Field field, Object value) {
		// 数据库->实体类的数据类型转换
		if (field.getType() == Boolean.class) {
			Short s = (Short) value;
			value = Boolean.valueOf(s == 0 ? false : true);
		} else if (field.getType() == Date.class) {
			value = new Date((Long) value);
		}
		return value;
	}

	protected Field[] getDeclaredFields(Class<?> clazz) {
		List<Field> listFields = new ArrayList<Field>();
		while (clazz != null && clazz.getAnnotation(SQLEntity.class) != null) {
			fields = clazz.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				listFields.add(fields[i]);
			}
			clazz = clazz.getSuperclass();
		}
		Field[] fields = new Field[listFields.size()];
		if (listFields.size() > 0) {
			for (int i = 0; i < fields.length; i++) {
				fields[i] = listFields.get(i);
			}
		}
		return fields;
	}

	private int[] getColumnIndex(Cursor cursor, Field[] fields) {
		// get the cursor column index
		int[] index = new int[fields.length];
		for (int i = 0; i < fields.length; i++) {
			Annotation[] annotations = fields[i].getAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation instanceof SQLColumn) {
					SQLColumn column = (SQLColumn) annotation;
					if (!column.persistent()) {
						continue;
					}
					if (column.value() != SQLColumn.DEFAULTVALUE) {
						index[i] = cursor.getColumnIndex(column.value());
					} else if (column.name() != SQLColumn.DEFAULTVALUE) {
						index[i] = cursor.getColumnIndex(column.name());
					} else {
						throw new RuntimeException(new SQLException(SQLException.MAPPINGEXCEPTION));
					}
				}
				if (annotation instanceof SQLIdText) {
					SQLIdText column = (SQLIdText) annotation;
					index[i] = cursor.getColumnIndex(column.value());
				}
			}
		}
		return index;
	}

	public Class<?> getInstanceClazz() {
		return instanceClazz == null ? clazz : instanceClazz;
	}

	public void setInstanceClazz(Class<?> instanceClazz) {
		this.instanceClazz = instanceClazz;
	}

	public DataSourceListener getListener() {
		return listener;
	}

	// 数据源侦听器
	public void setListener(DataSourceListener listener) {
		this.listener = listener;
	}

	public Class<?> getEntityClazz() {
		return clazz;
	}

	public void setEntityClazz(Class<?> clazz) {
		CacheEntityClass cec = arrayEntity.get(clazz.hashCode());
		if (cec == null) {
			cec = new CacheEntityClass();
			cec.entityClass = clazz;
			cec.fields = getDeclaredFields(clazz);
			SQLTable annotation = getAnnotationTable(clazz);
			if (annotation.value() == SQLTable.DEFAULTVALUE) {
				cec.tableName = annotation.name();
			} else {
				cec.tableName = annotation.value();
			}
		}
		helper.setEntityClass(cec.entityClass);
		tablename = cec.tableName;
		fields = cec.fields;
	}

	public String getSQLTableName() {
		return tablename;
	}

	public String getSQLDatabaseName() {
		return helper.getSQLDatabaseName();
	}

	public ExecuteResponse getResult() {
		if (response != null) return response;
		return response = new ExecuteResponse();
	}

	public static SQLEntity getAnnotationEntity(Class<?> clazz) {
		SQLEntity ann = null;
		while (clazz != null && ann == null) {
			ann = clazz.getAnnotation(SQLEntity.class);
			clazz = clazz.getSuperclass();
		}
		return ann;
	}

	public static SQLTable getAnnotationTable(Class<?> clazz) {
		SQLTable ann = null;
		while (clazz != null && ann == null) {
			ann = clazz.getAnnotation(SQLTable.class);
			clazz = clazz.getSuperclass();
		}
		return ann;
	}

	public static String getTablename(Class<?> clazz) {
		SQLEntity se = getAnnotationEntity(clazz);
		if (se == null) throw new RuntimeException(new SQLException(SQLException.MAPPINGEXCEPTION));
		SQLTable st = getAnnotationTable(clazz);
		if (st == null) throw new RuntimeException(new SQLException(SQLException.MAPPINGEXCEPTION));
		return st.databaseName();
	}

	// 数据源侦听
	public static abstract class DataSourceListener {

		public abstract void onOpen(SQLDataSource dataSource);

		public abstract void onClose(SQLDataSource dataSource);

		public void afterOpen(SQLDataSource dataSource) {};

		public void afterClose(SQLDataSource dataSource) {}

		public void onExecuteAdd(SQLDataSource dataSource, ContentValues contentValues) {}

		public void afterExecuteAdd(SQLDataSource dataSource, ContentValues contentValues) {}

		public void onExecuteDelete(SQLDataSource dataSource, String id) {}

		public void afterExecuteDelete(SQLDataSource dataSource, String id) {}

		public void onExecuteDelete(SQLDataSource dataSource, String selection, String[] selectionArgs) {}

		public void afterExecuteDelete(SQLDataSource dataSource, String selection, String[] selectionArgs) {}

		public void onExecuteUpdate(SQLDataSource dataSource, ContentValues contentValues) {}

		public void afterExecuteUpdate(SQLDataSource dataSource, ContentValues contentValues) {}

		public void onExecuteQuery(SQLDataSource dataSource, String table, String[] columns, String selection, String[] selectionArgs,
				String groupBy, String having, String orderBy, String limit) {}

		public void afterExecuteQuery(SQLDataSource dataSource, String table, String[] columns, String selection, String[] selectionArgs,
				String groupBy, String having, String orderBy, String limit) {}

	}

	// 执行结果
	public class ExecuteResponse {
		public static final int TYPE_ADD = 0X001;
		public static final int TYPE_DELETE = 0X002;
		public static final int TYPE_UPDATE = 0X003;
		public static final int TYPE_QUERY = 0X004;
		public static final int TYPE_ADD_UPDATE = 0X005;
		//
		private Long row;
		private Cursor cursor;
		private int executeType;
		private Set<Long> rowSet;
		private int effectedRowAdd;
		private int effectedRowUpdate;
		private int effectedRowDelete;

		public ExecuteResponse() {
			rowSet = new HashSet<Long>();
		}

		public void setExecuteType(int type) {
			row = 0l;
			cursor = null;
			effectedRowAdd = 0;
			executeType = type;
			effectedRowUpdate = 0;
			effectedRowDelete = 0;
			if (rowSet.size() > 0) rowSet.clear();
		}

		public Long getRow() {
			return row;
		}

		public Cursor getCursor() {
			return cursor;
		}

		public Set<Long> getRowSet() {
			return rowSet;
		}

		public int getEffectedRowAdd() {
			return effectedRowAdd;
		}

		public int getEffectedRowUpdate() {
			return effectedRowUpdate;
		}

		public int getEffectedRowDelete() {
			return effectedRowDelete;
		}

		public int getExecuteType() {
			return executeType;
		}

	}

	private class CacheEntityClass {
		public Class<?> entityClass;
		public String tableName;
		public Field[] fields;
	}
}
