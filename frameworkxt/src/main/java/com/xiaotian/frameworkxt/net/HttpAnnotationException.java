package com.xiaotian.frameworkxt.net;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name HttpAnnotationException
 * @description Http Annotation Exception
 * @date 2014-4-15
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class HttpAnnotationException extends RuntimeException {
	public static final int ANNOTATION_EXCEPTION_SERVER = 0X001;
	public static final int ANNOTATION_EXCEPTION_ACTION = 0X002;
	private static final long serialVersionUID = 1L;

	public HttpAnnotationException(int type) {
		super(getExceptionMessage(type));
	}

	public static String getExceptionMessage(int type) {
		switch (type) {
		case ANNOTATION_EXCEPTION_SERVER:
			return "Http Request Annotation Error , You Mush Use @HttpServer To Annotated The Class!";
		case ANNOTATION_EXCEPTION_ACTION:
			return "Http Request Annotation Error , You Mush Use @HttpAction To Annotated The Method And The Method Param Must List Colection!";
		}
		return "Unknow Annotation Error";
	}
}
