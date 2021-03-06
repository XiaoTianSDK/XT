/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xiaotian.framework.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;

/**
 * Class containing some static utility methods.
 */
public class Utils {
	public static void copyStream(InputStream is, OutputStream os) {
		byte[] bbuf = new byte[256];
		int hasReaded;
		try {
			while ((hasReaded = is.read(bbuf)) != -1) {
				os.write(bbuf, 0, hasReaded);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isTopActivity(Context context) {
		ActivityManager manager = (ActivityManager) context.getApplicationContext().getSystemService(
				Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTasks = manager.getRunningTasks(1);
		if (runningTasks != null && runningTasks.size() > 0) {
			ComponentName topActivity = runningTasks.get(0).topActivity;
			// Here you can get the TopActivity
			if (topActivity.getPackageName().equals(context.getPackageName())) {
				return true;
			}
		}
		return false;
	}
}
