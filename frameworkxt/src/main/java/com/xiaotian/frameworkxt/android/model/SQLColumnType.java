package com.xiaotian.frameworkxt.android.model;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name SQLColumnType
 * @description Table Column Type In SQLLite
 * @date 2013-12-30
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
public enum SQLColumnType {
	INTEGER {
		// 整数
		@Override
		public String getType() {
			/**
			 * 对应数据库类型: INT INTEGER TINYINT SMALLINT MEDIUMINT BIGINT UNSIGNED BIG INT INT2 INT8
			 */
			return "INTEGER";
		}
	},
	TEXT {
		// 文本
		@Override
		public String getType() {
			/**
			 * 对应数据库类型: CHARACTER(20) VARCHAR(255) VARYING CHARACTER(255) NCHAR(55) NATIVE CHARACTER(70) NVARCHAR(100) TEXT CLOB
			 */
			return "TEXT";
		}
	},
	FLOAT {
		// 数字[浮点]
		@Override
		public String getType() {
			/**
			 * REAL DOUBLE DOUBLEPRECISION FLOA
			 */
			return "REAL";
		}

	},
	LONG {
		// 长整
		@Override
		public String getType() {
			return "INTEGER";
		}
	},
	SHORT {
		// 短整
		@Override
		public String getType() {
			return "INTEGER";
		}
	},
	DOUBLE {
		// 浮点
		@Override
		public String getType() {
			/**
			 * REAL DOUBLE DOUBLEPRECISION FLOA
			 */
			return "REAL";
		}
	},
	BLOB {
		// 数据块
		@Override
		public String getType() {
			/**
			 * BLOB , no datatype specified
			 */
			return "BLOB";
		}

	};
	public abstract String getType();
}
