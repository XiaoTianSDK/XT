package com.xiaotian.frameworkxt.serializer.json;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name UtilJSON
 * @description JSON 工具
 * @date 2014-4-14
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class UtilJSONAnnotation {
	public static JSONField getJSONField(Class<?> annotationClass) {
		while (annotationClass != null) {
			JSONField jf = annotationClass.getAnnotation(JSONField.class);
			if (jf != null) return jf;
			annotationClass = annotationClass.getSuperclass();
		}
		return null;
	}

	public static JSONEntity getJSONEntity(Class<?> annotationClass) {
		while (annotationClass != null) {
			JSONEntity js = annotationClass.getAnnotation(JSONEntity.class);
			if (js != null) return js;
			annotationClass = annotationClass.getSuperclass();
		}
		return null;
	}
}
