package com.xiaotian.frameworkxt.android.model;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name SQLException
 * @description SQL Exception
 * @date 2013-11-5
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
public class SQLException extends Exception {
	private static final long serialVersionUID = 1L;
	public static final int UNSUPPORTEXCEPTION = 0X001;
	public static final int MAPPINGEXCEPTION = 0X002;
	public static final int DATABASEEXCEPTION = 0X003;
	public static final int DATABASENAMENULL = 0X004;
	public static final int TABLENAMENULL = 0X005;
	public static final int UNSUPPORTDATATYPE = 0X006;
	public static final int FIELDEXCEPTION = 0X007;
	public static final int UNNULLABLEERROR = 0X008;
	public static final int TABLEVERSIONERROR = 0X009;

	public SQLException(int type) {
		super(getMessaget(type));
	}

	public SQLException(int type, Throwable e) {
		super(getMessaget(type), e);
	}

	public static String getMessaget(int type) {
		switch (type) {
		case UNSUPPORTEXCEPTION:
			return "When get the mapping class cause exception,make sure class has @SQLEntity mapped!";
		case MAPPINGEXCEPTION:
			return "When get the mapping field cause exception,make sure the persistent class has mapped!";
		case DATABASEEXCEPTION:
			return "When execute sql from database cause exception!";
		case DATABASENAMENULL:
			return "Data base name un definition!";
		case TABLENAMENULL:
			return "Table name un definition!";
		case UNSUPPORTDATATYPE:
			return "Un support the data type !";
		case FIELDEXCEPTION:
			return "When initialize the class field cause exception !";
		case UNNULLABLEERROR:
			return "The column can't null able!";
		case TABLEVERSIONERROR:
			return "When get the @Table version cause exception,un find the versition value";
		}
		return "Un define exception!";
	}
}
