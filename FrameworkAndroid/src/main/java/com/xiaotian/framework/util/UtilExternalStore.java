package com.xiaotian.framework.util;

import java.io.File;

import com.xiaotian.framework.common.Mylog;
import com.xiaotian.frameworkxt.android.util.UtilEnvironment;

import android.content.Context;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name UtilExternalStore
 * @description SD 卡外部存蓄
 * @date 2013-10-12
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2009-2013 广州睿塔科技 Ltd, All Rights Reserved.
 */
public class UtilExternalStore {
	public static final String CACHE_FOLDER_STUB = "CacheFolderXT";
	public static final String CACHE_FOLDER_IMAGE_POOL = "CachePoolImage";
	public static final String CACHE_FOLDER_VIDEO_POOL = "CachePoolVideo";
	File externalDirectory;

	public UtilExternalStore(Context context) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				File pathImage = new File(getImagePoolPath());
				File pathVideo = new File(getVideoPoolPath());
				if (!pathImage.exists() && !pathImage.mkdirs()) {
					Mylog.info("Crate " + pathImage.getAbsolutePath() + " Failure.");
				}
				if (!pathVideo.exists() && !pathVideo.mkdirs()) {
					Mylog.info("Crate " + pathVideo.getAbsolutePath() + " Failure.");
				}
			}
		}).start();
	}

	public String getVideoPoolPath(String fileName) {
		return getVideoPoolPath() + File.separator + fileName;
	}

	public String getVideoPoolPath() {
		return getExternalDirectory().getAbsolutePath() + File.separator + CACHE_FOLDER_STUB + File.separator + CACHE_FOLDER_VIDEO_POOL;
	}

	public String getImagePoolPath(String fileName) {
		return getImagePoolPath() + File.separator + fileName;
	}

	public String getImagePoolPath() {
		return getExternalDirectory().getAbsolutePath() + File.separator + CACHE_FOLDER_STUB + File.separator + CACHE_FOLDER_IMAGE_POOL;
	}

	public File getExternalDirectory() {
		if (externalDirectory == null) externalDirectory = UtilEnvironment.getExternalStorageDirectory();
		return externalDirectory;
	}
}
