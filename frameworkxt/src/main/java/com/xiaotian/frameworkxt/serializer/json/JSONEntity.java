package com.xiaotian.frameworkxt.serializer.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name JSONSerial
 * @description 序列化类声明
 * @date 2013-10-27
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
// accessed via reflection at runtime
@Retention(RetentionPolicy.RUNTIME)
// classes and interfaces typically
// you can leave the target out alltogether so the annotation can be used for
// both classes, methods and fields
@Target(ElementType.TYPE)
public @interface JSONEntity {}
