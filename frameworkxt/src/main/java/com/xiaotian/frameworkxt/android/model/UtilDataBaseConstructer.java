package com.xiaotian.frameworkxt.android.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.xiaotian.frameworkxt.android.common.Mylog;
import com.xiaotian.frameworkxt.android.util.UtilEnvironment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name UtilDataBaseConstructer
 * @description DataBase Constructer 数据库初始化控制
 * @date 2014-7-4
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class UtilDataBaseConstructer extends SQLDataBaseHelper {

	public UtilDataBaseConstructer(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	public boolean backupDataBase() {
		File file = UtilEnvironment.getExternalStorageDirectory();
		if (!file.exists()) file.mkdirs();
		file = new File(file, "database_backup.bat.db");
		if (!file.exists()) try {
			file.createNewFile();
		} catch (IOException e) {}
		return backupDataBase(file.getAbsolutePath());
	}

	public boolean backupDataBase(String backupPath) {
		Mylog.info("back up database path=" + backupPath);
		SQLiteDatabase db = getWritableDatabase();
		try {
			copyFile(new File(db.getPath()), new File(backupPath));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@SuppressWarnings("resource")
	private void copyFile(File src, File dst) throws IOException {
		FileChannel inChannel = new FileInputStream(src).getChannel();
		FileChannel outChannel = new FileOutputStream(dst).getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} finally {
			if (inChannel != null) inChannel.close();
			if (outChannel != null) outChannel.close();
		}
	}
}
