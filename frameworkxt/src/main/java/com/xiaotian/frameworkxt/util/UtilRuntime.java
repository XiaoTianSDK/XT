package com.xiaotian.frameworkxt.util;

import java.io.IOException;

import com.xiaotian.frameworkxt.android.common.Mylog;

/**
 * @version 1.0.0
 * @author Administrator
 * @name UtilProcess
 * @description 进程/程序管理
 * @date 2015-6-17
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class UtilRuntime {

	// 执行命令
	public int execute(String command) {
		Runtime runtime = Runtime.getRuntime();
		try {
			Process process = runtime.exec(command);
			return process.waitFor();
		} catch (IOException e) {
			Mylog.printStackTrace(e);
		} catch (InterruptedException e) {
			Mylog.printStackTrace(e);
		}
		return -1;
	}
}
