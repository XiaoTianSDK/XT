package com.xiaotian.frameworkxt.android.util;

import java.io.File;

import android.os.StatFs;

public class UtilFile extends com.xiaotian.frameworkxt.util.UtilFile {

	@Override
	@SuppressWarnings("deprecation")
	protected long captureDirectorySize(long size, String directory) {
		if (directory == null) return 0;
		File file = new File(directory);
		if (!file.exists()) return 0;
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				return size + captureDirectorySize(size, f.getAbsolutePath());
			}
		}
		if (UtilSDKVersion.hasGingerbread()) {
			// 2.3以上
			return size + file.getTotalSpace();
		} else {
			// 2.3以前
			final StatFs stats = new StatFs(directory);
			return size + (long) stats.getBlockSize() * (stats.getBlockCount() - stats.getAvailableBlocks());
		}
	}
}
