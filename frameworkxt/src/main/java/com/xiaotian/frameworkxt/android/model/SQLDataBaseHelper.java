package com.xiaotian.frameworkxt.android.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.xiaotian.frameworkxt.android.common.Mylog;
import com.xiaotian.frameworkxt.android.model.SQLTable.DatabaseNameType;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name SQLDataBaseHelper
 * @description SQL Open Helper, DataBase Helper The extends SQLiteOpenHelper
 * @date 2013-11-5
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
public class SQLDataBaseHelper extends SQLiteOpenHelper {
	//
	private static final String DROP_TABLE = " DROP TABLE IF EXISTS %1$s ";
	protected String databaseName;
	protected Context context;
	private Class<?> entity;

	//
	public SQLDataBaseHelper(Context context, String name, Class<?> entityClass) {
		super(context, name, null, getVersion(entityClass));
		this.context = context;
		this.databaseName = name;
		// TODO 初始化数据库
		this.entity = entityClass;
		Mylog.info("Constructor : " + name + " version:" + getVersion(entityClass));
	}

	public SQLDataBaseHelper(Context context, Class<?> entityClass) {
		super(context, getDtabaseName(entityClass), null, getVersion(entityClass));
		// TODO 初始化数据库
		this.context = context;
		this.entity = entityClass;
		this.databaseName = getDtabaseName(entityClass);
		Mylog.info("Constructor : " + databaseName + " version:" + getVersion(entityClass));
	}

	public SQLDataBaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		this.databaseName = name;
		this.context = context;
	}

	public static int getVersion(Class<?> clazz) {
		// TODO 版本号
		SQLTable annotation = getAnnotationTable(clazz);
		if (annotation != null) {
			return ((SQLTable) annotation).version();
		}
		throw new RuntimeException(new SQLException(SQLException.TABLEVERSIONERROR));
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		// TODO 创建数据表
		String createTable = createTableSyntax(entity);
		Mylog.info(createTable);
		database.execSQL(createTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		// TODO 更新数据库
		String tablename = getTableName(entity);
		Mylog.info("update table " + tablename + " : " + oldVersion + " to " + newVersion);
		database.execSQL(String.format(DROP_TABLE, tablename));
		onCreate(database);
	}

	protected String createTableSyntax(Class<?> anotationEntity) {
		Annotation[] annotations = null;
		String columnname = null;
		String syntax = null;
		Field field;
		StringBuilder createTable = new StringBuilder("CREATE TABLE IF NOT EXISTS \"");
		createTable.append(getTableName(anotationEntity));
		createTable.append("\"(");
		// Column
		Field[] fields = getDeclaredFields(anotationEntity);
		for (int i = 0; i < fields.length; i++) {
			field = fields[i];
			annotations = field.getDeclaredAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation instanceof SQLColumn) {
					SQLColumn column = (SQLColumn) annotation;
					if (!column.persistent()) {
						// 1.persistent
						break;
					}
					if (column.value() != SQLColumn.DEFAULTVALUE) {
						// 2.value
						columnname = column.value();
						syntax = createSyntax(field.getType(), true, false);
						break;
					} else if (column.name() != SQLColumn.DEFAULTVALUE) {
						// 3.column name
						columnname = column.name();
						// 4.column type
						syntax = createSyntax(column.type(), column.nullable(), column.unique());
						break;
					}
				} else if (annotation instanceof SQLIdText) {
					SQLIdText ia = (SQLIdText) annotation;
					columnname = ia.value();
					syntax = ia.syntax();
					break;
				} else if (annotation instanceof SQLId) {
					SQLId id = (SQLId) annotation;
					columnname = id.value();
					syntax = id.syntax();
					break;
				}
			}
			// add column
			if (columnname == null) continue;
			if (createTable.lastIndexOf(String.format(",%1$s ", columnname)) == -1) {
				// Only Add A Time
				createTable.append(columnname);
				createTable.append(" ");
				createTable.append(syntax);
				createTable.append(',');
			}
			field = null;
			syntax = null;
			columnname = null;
			annotations = null;
		}
		if (createTable.charAt(createTable.length() - 1) == ',') createTable.deleteCharAt(createTable.length() - 1);
		createTable.append(")");
		return createTable.toString();
	}

	protected String createSyntax(SQLColumnType type, boolean nullable, boolean unique) {
		return type.getType() + (nullable ? " NULL" : " NOT NULL") + (unique ? " UNIQUE" : "");
	}

	protected String createSyntax(Class<?> clazz, boolean nullable, boolean unique) {
		return mappingSQLType(clazz) + (nullable ? " NULL" : " NOT NULL") + (unique ? " UNIQUE" : "");
	}

	public String getSQLDatabaseName() {
		return databaseName;
	}

	public static String getDtabaseName(Class<?> clazz) {
		// TODO 常量配置的databaseName
		Annotation annotation = clazz.getAnnotation(SQLTable.class);
		if (annotation instanceof SQLTable) {
			// 1.数据库名
			SQLTable at = (SQLTable) annotation;
			if (at.databaseType() == DatabaseNameType.CONSTANT && at.databaseName() != SQLTable.DEFAULTVALUE) {
				return at.databaseName();
			} else {
				throw new RuntimeException(new SQLException(SQLException.TABLENAMENULL));
			}
		} else {
			throw new RuntimeException(new SQLException(SQLException.MAPPINGEXCEPTION));
		}
	}

	public String getTableName(Class<?> anotationEntity) {
		// TODO 获取表名
		Annotation annotation = anotationEntity.getAnnotation(SQLTable.class);
		if (annotation instanceof SQLTable) {
			if (((SQLTable) annotation).name() != SQLTable.DEFAULTVALUE) {
				return ((SQLTable) annotation).name();
			} else {
				throw new RuntimeException(new SQLException(SQLException.TABLENAMENULL));
			}
		} else {
			throw new RuntimeException(new SQLException(SQLException.TABLENAMENULL));
		}
	}

	public static String mappingSQLType(Class<?> clazz) {
		if (clazz == String.class) {
			// String
			return "TEXT";
		} else if (clazz == Integer.class) {
			// Integer
			return "INTEGER";
		} else if (clazz == Boolean.class) {
			// Boolean
			return "INTEGER";
		} else if (clazz == Long.class) {
			// Long
			return "INTEGER";
		} else if (clazz == Float.class) {
			// Float
			return "REAL";
		} else if (clazz == Date.class) {
			// Date
			return "INTEGER";
		} else if (clazz == Double.class) {
			// Double
			return "REAL";
		} else if (clazz == Short.class) {
			// Short
			return "INTEGER";
		} else if (clazz == Character.class) {
			// Character
			return "INTEGER";
		} else {
			throw new RuntimeException(new SQLException(SQLException.UNSUPPORTDATATYPE));
		}
	}

	protected Field[] getDeclaredFields(Class<?> clazz) {
		Field[] fields = null;
		List<Field> listFields = new ArrayList<Field>();
		while (clazz != null && clazz.getAnnotation(SQLEntity.class) != null) {
			fields = clazz.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				listFields.add(fields[i]);
			}
			clazz = clazz.getSuperclass();
		}
		fields = new Field[listFields.size()];
		if (listFields.size() > 0) {
			for (int i = 0; i < fields.length; i++) {
				fields[i] = listFields.get(i);
			}
		}
		return fields;
	}

	static SQLEntity getAnnotationEntity(Class<?> clazz) {
		SQLEntity ann = null;
		while (clazz != null && ann == null) {
			ann = clazz.getAnnotation(SQLEntity.class);
			clazz = clazz.getSuperclass();
		}
		return ann;
	}

	static SQLTable getAnnotationTable(Class<?> clazz) {
		SQLTable ann = null;
		while (clazz != null && ann == null) {
			ann = clazz.getAnnotation(SQLTable.class);
			clazz = clazz.getSuperclass();
		}
		return ann;
	}

	public Class<?> getEntityClass() {
		return entity;
	}

	public void setEntityClass(Class<?> entity) {
		this.entity = entity;
	}

}
