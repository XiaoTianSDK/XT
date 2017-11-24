package com.xiaotian.frameworkxt.serializer.xml;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name UtilXMLAnnotation
 * @description XML Util
 * @date 2014-4-16
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class UtilXMLAnnotation {

	public static XMLEntity getXMLEntityAnnotation(Class<? extends Object> annotatedEntity) throws XMLSerializingException {
		XMLEntity xmlEntity = null;
		while (xmlEntity == null) {
			if (annotatedEntity == null) throw new XMLSerializingException(XMLSerializingException.UNSUPER_EXCEPTION);
			xmlEntity = annotatedEntity.getAnnotation(XMLEntity.class);
			annotatedEntity = annotatedEntity.getSuperclass();
		};
		return xmlEntity;
	}

}
