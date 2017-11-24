package com.xiaotian.frameworkxt.android.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import com.xiaotian.frameworkxt.android.model.SQLTable.DatabaseNameType;
import com.xiaotian.frameworkxt.util.UtilClass;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name UtilSQLAnnotation
 * @description SQL Annotation Util
 * @date 2014-4-14
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public abstract class UtilSQLEntityAnnotation<EntityType> {
	private Class<?> entityClass;
	private int[] columnIndexs;
	private Field[] fields;

	public UtilSQLEntityAnnotation() {
		fields = getDeclaredFields(getMappingEntityClass());
	}

	/**
	 * @param entityInstance
	 *            映射的实体对象,序列化实体
	 * @return SQL 的ContentValues集合
	 */
	public ContentValues serialize(EntityType data) {
		String name;
		Object value;
		Annotation[] annotations;
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

	/**
	 * @param cursor
	 *            获取当前游标所指定的记录
	 * @return 序列化为泛型实体
	 */
	@SuppressWarnings("unchecked")
	public EntityType deSerialize(Cursor cursor) throws SQLException {
		if (cursor == null || cursor.getCount() < 1) return null;
		// 从当前的Cursor游标,构造Annotation注解的实体
		Constructor<?> constructor;
		if (columnIndexs == null || columnIndexs.length != cursor.getColumnCount()) {
			columnIndexs = getColumnIndex(cursor, fields);
		}
		try {
			constructor = getMappingEntityClass().getConstructor();
			return setInstanceValue(cursor, (EntityType) constructor.newInstance(), columnIndexs);
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

	public <T extends EntityType> T setInstanceValue(Cursor cursor, T instance, int[] columnIndexs) throws IllegalArgumentException,
			IllegalAccessException {
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			Annotation[] annotations = field.getAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation instanceof SQLIdText) {
					// ID Text Column
					field.setAccessible(true);
					field.set(instance, cursor.getString(columnIndexs[i]));
					field.setAccessible(false);
				} else if (annotation instanceof SQLId) {
					// ID Int Column
					field.setAccessible(true);
					field.set(instance, cursor.getInt(columnIndexs[i]));
					field.setAccessible(false);
				} else if (annotation instanceof SQLColumn) {
					// Column
					SQLColumn column = (SQLColumn) annotation;
					if (!column.persistent()) break;
					field.setAccessible(true);
					switch (column.type()) {
					case TEXT:
						field.set(instance, translateMappedFieldValue(field, cursor.getString(columnIndexs[i])));
						break;
					case INTEGER:
						field.set(instance, translateMappedFieldValue(field, cursor.getInt(columnIndexs[i])));
						break;
					case FLOAT:
						field.set(instance, translateMappedFieldValue(field, cursor.getFloat(columnIndexs[i])));
						break;
					case LONG:
						field.set(instance, translateMappedFieldValue(field, cursor.getLong(columnIndexs[i])));
						break;
					case SHORT:
						field.set(instance, translateMappedFieldValue(field, cursor.getShort(columnIndexs[i])));
						break;
					case DOUBLE:
						field.set(instance, translateMappedFieldValue(field, cursor.getDouble(columnIndexs[i])));
						break;
					case BLOB:
						field.set(instance, translateMappedFieldValue(field, cursor.getBlob(columnIndexs[i])));
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

	public Class<?> getMappingEntityClass() {
		if (entityClass != null) return entityClass;
		Type[] types = UtilClass.getGenericSuperParameteizedTypes(getExtendsedClass());// 获取类泛型参数类型
		return entityClass = (Class<?>) types[0]; // 第一个泛型实际类型参数
	}

	private int[] getColumnIndex(Cursor cursor, Field[] fields) {
		// get the cursor column index
		int[] index = new int[fields.length];
		for (int i = 0; i < fields.length; i++) {
			Annotation[] annotations = fields[i].getAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation instanceof SQLColumn) {
					SQLColumn column = (SQLColumn) annotation;
					if (!column.persistent()) continue;
					if (column.value() != SQLColumn.DEFAULTVALUE) {
						index[i] = cursor.getColumnIndex(column.value());
					} else if (column.name() != SQLColumn.DEFAULTVALUE) {
						index[i] = cursor.getColumnIndex(column.name());
					} else {
						throw new RuntimeException(new SQLException(SQLException.MAPPINGEXCEPTION));
					}
					break;
				} else if (annotation instanceof SQLIdText) {
					SQLIdText column = (SQLIdText) annotation;
					index[i] = cursor.getColumnIndex(column.value());
					break;
				} else if (annotation instanceof SQLId) {
					SQLId column = (SQLId) annotation;
					index[i] = cursor.getColumnIndex(column.value());
					break;
				}
			}
		}
		return index;
	}

	/********************************** Abstract Class Static Method **********************************/
	/**
	 * @return
	 * 
	 *         返回继承的Util 注解实体工具类,返回的类必须继承 UtilSQLEntityAnnotation 类
	 */
	public abstract Class<?> getExtendsedClass();

	/********************************** Class Static Method **********************************/
	/**
	 * @param clazz
	 *            The Class Has Mapping By SQLEntity
	 * @return The Declare Fields Contain Sub/Super Class Declared Fields
	 */
	protected static Field[] getDeclaredFields(Class<?> clazz) {
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

	/**
	 * @param clazz
	 *            The SQLEntity annotationed class
	 * @return The annotationed class field
	 */
	public static String[] getSQLEntityProjects(Class<?> clazz) {
		List<String> arrayColumn = new ArrayList<String>();
		arrayColumn.add("_id");
		for (Field field : getDeclaredFields(clazz)) {
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
		String[] columns = new String[arrayColumn.size()];
		for (int i = 0; i < columns.length; i++) {
			columns[i] = arrayColumn.get(i);
		}
		return columns;
	}

	/**
	 * @param annotationClass
	 *            The annotationed entity class.
	 * @return The Param Entity Annotationed SQLEntity, if the request entity's super class has annotated it will return the sub-est annotaion entity class sub class
	 */
	public static SQLEntity getSQLEntity(Class<?> annotationClass) {
		while (annotationClass != null) {
			SQLEntity se = annotationClass.getAnnotation(SQLEntity.class);
			if (se != null) return se;
			annotationClass = annotationClass.getSuperclass();
		}
		return null;
	}

	/**
	 * @param annotationClass
	 *            The annotationed entity class.
	 * @return The Param Entity Annotationed SQLTable, if the request entity's super class has annotated it will return the sub-est annotaion class table annotation sub class
	 */
	public static SQLTable getSQLTable(Class<?> annotationClass) {
		while (annotationClass != null) {
			SQLTable st = annotationClass.getAnnotation(SQLTable.class);
			if (st != null) return st;
			annotationClass = annotationClass.getSuperclass();
		}
		return null;
	}

	/**
	 * @param annotationClass
	 *            The annotationed entity class.
	 * @return The Param Entity Annotationed SQLTable, if the request entity's super class has annotated it will return the sub-est annotaion class database name
	 */
	public static String getDatabaseName(Class<?> annotationClass) {
		Annotation annotation = getSQLTable(annotationClass);
		if (annotation != null && (annotation instanceof SQLTable)) {
			SQLTable at = (SQLTable) annotation;
			if (at.databaseType() == DatabaseNameType.CONSTANT && at.databaseName() != SQLTable.DEFAULTVALUE) {
				return at.databaseName();
			} else {
				throw new RuntimeException(new SQLException(SQLException.DATABASENAMENULL));
			}
		} else {
			throw new RuntimeException(new SQLException(SQLException.MAPPINGEXCEPTION));
		}
	}
}
