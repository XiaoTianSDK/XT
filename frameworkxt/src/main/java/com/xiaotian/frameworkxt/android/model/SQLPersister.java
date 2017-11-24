package com.xiaotian.frameworkxt.android.model;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.util.SparseArray;

import com.xiaotian.frameworkxt.android.common.Mylog;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name SQLPersister
 * @description 数据库持久化工厂
 * @date 2013-11-5
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
public class SQLPersister {

	private Context context;
	private boolean isTransaction;
	private SQLDataSource dataSource;
	private SparseArray<SQLDataSource> dataSources;
	private PersisterTransactionListener transactionListerner;

	// Database Name Mapping
	public SQLPersister(Context context) {
		this.context = context;
		this.dataSources = new SparseArray<SQLDataSource>();
	}

	// Add Entity
	public synchronized long add(Object data) throws SQLException {
		return add(data, UtilSQLEntityAnnotation.getDatabaseName(data.getClass()));
	}

	public synchronized long add(List<Object> data) throws SQLException {
		if (data == null || data.size() < 1) return -1;
		return add(data, UtilSQLEntityAnnotation.getDatabaseName(data.getClass()));
	}

	public synchronized long add(Object data, String databaseName) throws SQLException {
		verifyAnnotation(data.getClass());
		if (databaseName == null) databaseName = UtilSQLEntityAnnotation.getDatabaseName(data.getClass());
		try {
			dataSource = getDataSource(getEntityClass(data.getClass()), databaseName);
			if (!dataSource.isOpen()) dataSource.open();
			return dataSource.add(data);
		} catch (SQLException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} finally {
			if (dataSource != null) dataSource.close();
		}
	}

	public synchronized long add(List<? extends Object> data, String databaseName) throws SQLException {
		if (data == null || data.size() < 1) return 0;
		Class<?> clazz = data.get(0).getClass();
		verifyAnnotation(clazz);
		if (databaseName == null) databaseName = UtilSQLEntityAnnotation.getDatabaseName(clazz);
		try {
			dataSource = getDataSource(getEntityClass(clazz), databaseName);
			if (!dataSource.isOpen()) dataSource.open();
			return dataSource.add(data);
		} catch (SQLException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} finally {
			if (dataSource != null) dataSource.close();
		}
	}

	// Persistering Entitiy
	public synchronized long persister(Object data) throws SQLException {
		return persister(data, UtilSQLEntityAnnotation.getDatabaseName(data.getClass()));
	}

	public synchronized long persister(List<? extends Object> data) throws SQLException {
		if (data == null || data.size() < 1) return -1;
		return persister(data, UtilSQLEntityAnnotation.getDatabaseName(data.get(0).getClass()));
	}

	public synchronized long persister(Object data, String databaseName) throws SQLException {
		// 持久化对象到指定数据库
		verifyAnnotation(data.getClass());
		if (databaseName == null) databaseName = UtilSQLEntityAnnotation.getDatabaseName(data.getClass());
		try {
			dataSource = getDataSource(getEntityClass(data.getClass()), databaseName);
			if (!dataSource.isOpen()) dataSource.open();
			return dataSource.addOrUpdate(data);
		} catch (SQLException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} finally {
			if (dataSource != null) dataSource.close();
		}
	}

	public synchronized long persister(List<? extends Object> data, String databaseName) throws SQLException {
		// 持久化一个集合对象到指定数据库
		if (data == null || data.size() < 1) return 0;
		Class<?> clazz = data.get(0).getClass();
		verifyAnnotation(clazz);
		if (databaseName == null) databaseName = UtilSQLEntityAnnotation.getDatabaseName(clazz);
		try {
			dataSource = getDataSource(getEntityClass(clazz), databaseName);
			if (!dataSource.isOpen()) dataSource.open();
			return dataSource.addOrUpdate(data);
		} catch (SQLException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} finally {
			if (dataSource != null) dataSource.close();
		}
	}

	// Delete
	public synchronized int delete(Integer id, Class<?> clazz) throws SQLException {
		return delete(String.valueOf(id), clazz, UtilSQLEntityAnnotation.getDatabaseName(clazz));
	}

	public synchronized int delete(String id, Class<?> clazz) throws SQLException {
		return delete(id, clazz, UtilSQLEntityAnnotation.getDatabaseName(clazz));
	}

	public synchronized int delete(Integer id, Class<?> clazz, String databaseName) throws SQLException {
		if (id == null || id < 0) return 0;
		return delete(String.valueOf(id), clazz, databaseName);
	}

	public synchronized int delete(String id, Class<?> clazz, String databaseName) throws SQLException {
		if (id == null) return 0;
		verifyAnnotation(clazz);
		if (databaseName == null) databaseName = UtilSQLEntityAnnotation.getDatabaseName(clazz);
		try {
			dataSource = getDataSource(getEntityClass(clazz), databaseName);
			if (!dataSource.isOpen()) dataSource.open();
			return dataSource.delete(id);
		} catch (SQLException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} finally {
			if (dataSource != null) dataSource.close();
		}
	}

	public synchronized int delete(Class<?> clazz, String selection, String[] selectionArg) throws SQLException {
		return delete(clazz, UtilSQLEntityAnnotation.getDatabaseName(clazz), selection, selectionArg);
	}

	@SuppressWarnings("deprecation")
	public synchronized int delete(Class<?> clazz, String databaseName, String selection, String[] selectionArg) throws SQLException {
		verifyAnnotation(clazz);
		if (databaseName == null) databaseName = UtilSQLEntityAnnotation.getDatabaseName(clazz);
		try {
			dataSource = getDataSource(getEntityClass(clazz), databaseName);
			if (!dataSource.isOpen()) dataSource.open();
			return dataSource.delete(selection, selectionArg);
		} catch (SQLException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} finally {
			if (dataSource != null) dataSource.close();
		}
	}

	public synchronized int delete(List<? extends Object> ids, Class<?> clazz) throws SQLException {
		return delete(ids, clazz, UtilSQLEntityAnnotation.getDatabaseName(clazz));
	}

	public synchronized int delete(List<? extends Object> ids, Class<?> clazz, String databaseName) throws SQLException {
		if (ids == null || ids.size() < 0) return 0;
		verifyAnnotation(clazz);
		if (databaseName == null) databaseName = UtilSQLEntityAnnotation.getDatabaseName(clazz);
		try {
			dataSource = getDataSource(getEntityClass(clazz), databaseName);
			if (!dataSource.isOpen()) dataSource.open();
			return dataSource.delete(ids);
		} catch (SQLException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} finally {
			if (dataSource != null) dataSource.close();
		}
	}

	// Get
	public synchronized <T extends Object> List<T> get(Class<?> clazz, long... limit) throws SQLException {
		return get(clazz, UtilSQLEntityAnnotation.getDatabaseName(clazz), limit);
	}

	public synchronized <T extends Object> List<T> get(Class<?> clazz, String selection, String[] selectionArgs, long... limit) throws SQLException {
		return get(clazz, UtilSQLEntityAnnotation.getDatabaseName(clazz), selection, selectionArgs, limit);
	}

	public synchronized <T extends Object> List<T> get(Class<?> clazz, String selection, String[] selectionArgs, String groupBy, String having,
			String orderBy, String limit) throws SQLException {
		return get(clazz, UtilSQLEntityAnnotation.getDatabaseName(clazz), selection, selectionArgs, groupBy, having, orderBy, limit);
	}

	// get a Entity througth the id key
	public synchronized <T extends Object> T get(Integer id, Class<?> clazz) throws SQLException {
		if (id == null) return null;
		return get(String.valueOf(id), clazz, UtilSQLEntityAnnotation.getDatabaseName(clazz));
	}

	public synchronized <T extends Object> T get(String id, Class<?> clazz) throws SQLException {
		if (id == null) return null;
		return get(id, clazz, UtilSQLEntityAnnotation.getDatabaseName(clazz));
	}

	public synchronized <T extends Object> T get(Integer id, Class<?> clazz, String databaseName) throws SQLException {
		if (id == null || id < 0) return null;
		return get(String.valueOf(id), clazz, databaseName);
	}

	@SuppressWarnings("unchecked")
	public synchronized <T extends Object> T get(String id, Class<?> clazz, String databaseName) throws SQLException {
		// 根据_Id获取一个指定对象
		if (id == null) return null;
		verifyAnnotation(clazz);
		if (databaseName == null) databaseName = UtilSQLEntityAnnotation.getDatabaseName(clazz);
		try {
			dataSource = getDataSource(getEntityClass(clazz), databaseName);
			dataSource.open();
			return (T) dataSource.get(id);
		} catch (SQLException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} finally {
			if (dataSource != null) dataSource.close();
		}
	}

	public synchronized <T extends Object> List<T> get(Class<?> clazz, String databaseName, long... limit) throws SQLException {
		// 获取数据库对象集合 limit[0]=count or limit[0]=start limit[1]=count
		return get(clazz, databaseName, null, null, limit);
	}

	public synchronized <T extends Object> List<T> get(Class<?> clazz, String databaseName, String selection, String[] selectionArgs, long... limit)
			throws SQLException {
		// 获取数据库对象集合[包含where条件] limit[0]=count or limit[0]=start
		// limit[1]=count
		List<T> list = null;
		verifyAnnotation(clazz);
		if (databaseName == null) databaseName = UtilSQLEntityAnnotation.getDatabaseName(clazz);
		try {
			dataSource = getDataSource(getEntityClass(clazz), databaseName);
			dataSource.open();
			if (limit.length == 0) {
				list = dataSource.getList(selection, selectionArgs, 0, -1);
			} else if (limit.length == 1) {
				list = dataSource.getList(selection, selectionArgs, 0, limit[0]);
			} else if (limit.length > 2) {
				list = dataSource.getList(selection, selectionArgs, limit[0], limit[1]);
			}
		} catch (SQLException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} finally {
			if (dataSource != null) dataSource.close();
		}
		return list;
	}

	public synchronized <T extends Object> List<T> get(Class<?> clazz, String databaseName, String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy, String limit) throws SQLException {
		List<T> list = null;
		verifyAnnotation(clazz);
		if (databaseName == null) databaseName = UtilSQLEntityAnnotation.getDatabaseName(clazz);
		try {
			Class<?> entityClass = getEntityClass(clazz);
			dataSource = getDataSource(entityClass, databaseName);
			if (entityClass != clazz) dataSource.setInstanceClazz(clazz);
			dataSource.open();
			list = dataSource.getList(selection, selectionArgs, groupBy, having, orderBy, limit);
		} catch (SQLException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} finally {
			if (dataSource != null) dataSource.close();
		}
		return list;
	}

	// Reflesh Instance
	public synchronized <T extends Object> T reflesh(T data) throws SQLException {
		return reflesh(data, UtilSQLEntityAnnotation.getDatabaseName(data.getClass()));
	}

	public synchronized <T extends Object> T reflesh(T obj, String databaseName) throws SQLException {
		// 根据传入对象(id)刷新数据库新数据返回
		if (obj == null) return null;
		// obj对象必须包含@SQLId注解
		Class<?> clazz = obj.getClass();
		verifyAnnotation(clazz);
		if (databaseName == null) databaseName = UtilSQLEntityAnnotation.getDatabaseName(clazz);
		try {
			dataSource = getDataSource(getEntityClass(clazz), databaseName);
			dataSource.open();
			return dataSource.reload(obj);
		} catch (SQLException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} finally {
			if (dataSource != null) dataSource.close();
		}
	}

	// Count Record
	public synchronized int count(Class<?> clazz) throws SQLException {
		return count(clazz, UtilSQLEntityAnnotation.getDatabaseName(clazz));
	}

	public synchronized int count(Class<?> clazz, String selection, String[] selectionArg) throws SQLException {
		return count(clazz, UtilSQLEntityAnnotation.getDatabaseName(clazz), selection, selectionArg);
	}

	public synchronized int count(Class<?> clazz, String databaseName) throws SQLException {
		verifyAnnotation(clazz);
		if (databaseName == null) databaseName = UtilSQLEntityAnnotation.getDatabaseName(clazz);
		try {
			dataSource = getDataSource(getEntityClass(clazz), databaseName);
			dataSource.open();
			return dataSource.count();
		} catch (SQLException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} finally {
			if (dataSource != null) dataSource.close();
		}
	}

	public synchronized int count(Class<?> clazz, String databaseName, String selection, String[] selectionArg) throws SQLException {
		verifyAnnotation(clazz);
		if (databaseName == null) databaseName = UtilSQLEntityAnnotation.getDatabaseName(clazz);
		try {
			dataSource = getDataSource(getEntityClass(clazz), databaseName);
			dataSource.open();
			return dataSource.count(selection, selectionArg);
		} catch (SQLException e) {
			throw new SQLException(SQLException.DATABASEEXCEPTION, e);
		} finally {
			if (dataSource != null) dataSource.close();
		}
	}

	public synchronized SQLDataSource getDataSource(Class<?> clazz, String databaseName) throws SQLException {
		// reuse the Data Source
		if (databaseName == null) databaseName = UtilSQLEntityAnnotation.getDatabaseName(clazz);
		if (inTransaction()) {
			SQLTable sqlTable = SQLDataSource.getAnnotationTable(clazz);
			int version = sqlTable.version();
			String keyNameVersion = String.format(Locale.getDefault(), "%1$s%2$d", databaseName, version);
			dataSource = dataSources.get(keyNameVersion.hashCode(), null);
			if (dataSource == null) {
				Mylog.info("create a new transaction datasource: " + databaseName);
				dataSource = new SQLDataSource(context, databaseName, null, version);
				dataSources.put(keyNameVersion.hashCode(), dataSource);
			}
			dataSource.setEntityClazz(getEntityClass(clazz));
			if (!dataSource.isOpen()) dataSource.open();
			return dataSource;
		}
		dataSource = dataSources.get(clazz.hashCode(), null);
		if (dataSource == null) {
			Mylog.info("create a new datasource: " + clazz.getName());
			dataSource = new SQLDataSource(context, databaseName, clazz);
			dataSources.put(clazz.hashCode(), dataSource);
		}
		return dataSource;
	}

	private synchronized void verifyAnnotation(Class<?> clazz) throws SQLException {
		// Verify @Entity Annotation
		if (!(getAnnotationEntity(clazz) instanceof SQLEntity)) {
			throw new SQLException(SQLException.UNSUPPORTEXCEPTION);
		}
	}

	public synchronized void beginTransaction() {
		// 开始事务
		isTransaction = true;
		Mylog.info("beginTransaction");
		if (transactionListerner != null) transactionListerner.onBeginTransaction(this);
		for (int i = 0; i < dataSources.size(); i++) {
			dataSource = dataSources.get(dataSources.keyAt(i));
			if (!dataSource.isOpen()) dataSource.open();
			if (!dataSource.inTransaction()) dataSource.beginTransaction();
		}
	}

	public boolean inTransaction() {
		return isTransaction;
	}

	public synchronized void setTransactionSuccessful() {
		// 提交事务
		Mylog.info("setTransactionSuccessful");
		if (transactionListerner != null) transactionListerner.onSetTransactionSuccessful(this);
		for (int i = 0; i < dataSources.size(); i++) {
			dataSource = dataSources.get(dataSources.keyAt(i));
			if (dataSource.isOpen() && !dataSource.inTransaction()) dataSource.setTransactionSuccessful();
		}
	}

	public synchronized void endTransaction() {
		// 结束事务
		isTransaction = false;
		Mylog.info("endTransaction");
		for (int i = 0; i < dataSources.size(); i++) {
			dataSource = dataSources.get(dataSources.keyAt(i));
			if (dataSource.isOpen()) {
				if (dataSource.inTransaction()) dataSource.endTransaction();
				dataSource.close();
			}
		}
		if (transactionListerner != null) transactionListerner.onEndTransaction(this);
	}

	public void setTransactionListener(PersisterTransactionListener listener) {
		this.transactionListerner = listener;
	}

	SQLEntity getAnnotationEntity(Class<?> clazz) {
		SQLEntity ann = null;
		while (clazz != null && ann == null) {
			ann = clazz.getAnnotation(SQLEntity.class);
			clazz = clazz.getSuperclass();
		}
		return ann;
	}

	Class<?> getEntityClass(Class<?> clazz) {
		while (clazz != null) {
			if (clazz.getAnnotation(SQLEntity.class) != null) return clazz;
			clazz = clazz.getSuperclass();
		}
		throw new RuntimeException(new SQLException(SQLException.MAPPINGEXCEPTION));
	}

	public static interface PersisterTransactionListener {
		public abstract void onBeginTransaction(SQLPersister persister);

		public abstract void onSetTransactionSuccessful(SQLPersister persister);

		public abstract void onEndTransaction(SQLPersister persister);

	}
}
