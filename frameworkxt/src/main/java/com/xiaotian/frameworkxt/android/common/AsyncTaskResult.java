package com.xiaotian.frameworkxt.android.common;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name AsyncTaskResult
 * @description Async Task Result Object
 * @date 2014-6-6
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class AsyncTaskResult {
	public static final int TYPE_EXCEPTION = 0X001;
	public static final int TYPE_SUCCESS = 0X002;
	public static final int TYPE_ERROR = 0X003;
	//
	static final Object syncLock = new Object();
	static AsyncTaskResult staticResult;
	int resultType;
	String message;
	int messageRes;
	Object[] params;

	public static AsyncTaskResult buildResult() {
		return AsyncTaskResult.buildResult(TYPE_SUCCESS, 0);
	}

	public static AsyncTaskResult buildResult(String message) {
		return AsyncTaskResult.buildResult(TYPE_SUCCESS, message);
	}

	public static AsyncTaskResult buildResult(int messageRes) {
		return AsyncTaskResult.buildResult(TYPE_SUCCESS, messageRes);
	}

	public static AsyncTaskResult buildResult(int resultType, String message, Object... params) {
		synchronized (syncLock) {
			if (staticResult == null) staticResult = new AsyncTaskResult();
			staticResult.resultType = resultType;
			staticResult.message = message;
			staticResult.params = params;
			return staticResult;
		}
	}

	public static AsyncTaskResult buildResult(int resultType, int messageRes, Object... params) {
		synchronized (syncLock) {
			if (staticResult == null) staticResult = new AsyncTaskResult();
			staticResult.resultType = resultType;
			staticResult.messageRes = messageRes;
			staticResult.params = params;
			return staticResult;
		}
	}

	public Object getParams(int index) {
		return params[index];
	}

	public boolean isSuccess() {
		return resultType == TYPE_SUCCESS;
	}

	public int getResultType() {
		return resultType;
	}

	public void setResultType(int resultType) {
		this.resultType = resultType;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getMessageRes() {
		return messageRes;
	}

	public void setMessageRes(int messageRes) {
		this.messageRes = messageRes;
	}

	public Object[] getParams() {
		return params;
	}

}
