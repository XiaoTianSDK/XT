package com.xiaotian.framework.util.view.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @description
 * @date 2015/12/31
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InjectId {
    public static final String DEFAULT_ONCLICK = "DEFAULT_ONCLICK";

    public abstract int id() default Integer.MIN_VALUE;

    public abstract String onClick() default DEFAULT_ONCLICK;
}
