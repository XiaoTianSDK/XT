package com.xiaotian.frameworkxt.serializer.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name JSONParameter
 * @description 序列化变量参数
 * @date 2013-10-27
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@interface JSONParameter {
	public String name();

	public String defaultValue();
}
