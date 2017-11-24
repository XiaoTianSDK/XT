package com.xiaotian.framework.common;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name AsyncOperationMessage
 * @description 异步操作结果消息
 * @date 2014-5-30
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class AsyncOperationMessage {
	String message;
	Object[] results;
	boolean status;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object[] getResults() {
		return results;
	}

	public void setResults(Object[] results) {
		this.results = results;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

}
