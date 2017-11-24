package com.xiaotian.frameworkxt.serializer.xml;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name XMLSerializingException
 * @description XML Exceptioin
 * @date 2014-4-16
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class XMLSerializingException extends Exception {
	private static final long serialVersionUID = 1L;
	public static final int UNSUPER_EXCEPTION = 0X001;
	public static final int ELEMENT_EXCEPTION = 0X002;
	public static final int PROPERTY_EXCEPTION = 0X003;
	public static final int RUNTIME_EXCEPTION = 0X004;

	public XMLSerializingException(int key) {
		super(exceptionMessage(key));
	}

	public XMLSerializingException(int key, Throwable e) {
		super(exceptionMessage(key), e);
	}

	public static String exceptionMessage(int key) {
		switch (key) {
		case UNSUPER_EXCEPTION:
			return "Un supper the class XML serializable,be sure the serialized class has mapping by @XMLEntity!";
		case ELEMENT_EXCEPTION:
			return "When pass the class field exception,be sure the class's field has mapping by @XMLElement or the field's type is Object";
		case PROPERTY_EXCEPTION:
			return "When pass the class field exception,be sure the class's field has mapping by @XMLProperty or the field's type is Object!";
		case RUNTIME_EXCEPTION:
			return "Throw other runtime exception.";
		}
		return "Un Deceal Exception!";
	}
}
