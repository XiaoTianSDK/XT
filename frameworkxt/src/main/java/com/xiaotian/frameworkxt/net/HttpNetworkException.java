package com.xiaotian.frameworkxt.net;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name HttpNetworkException
 * @description Http Request Network Exception
 * @date 2014-4-15
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class HttpNetworkException extends Exception {
	public static final int EXCEPTION_CODE_SERVER = 0x001;
	public int exceptionCode;

	private static final long serialVersionUID = 1L;

	public HttpNetworkException(String exception) {
		super(exception);
	}

	public HttpNetworkException(Throwable exception) {
		super(exception);
	}

	public HttpNetworkException(String message, Throwable exception) {
		super(message, exception);
	}
}
