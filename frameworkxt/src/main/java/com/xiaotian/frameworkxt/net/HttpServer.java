package com.xiaotian.frameworkxt.net;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @name HttpServer
 * @description Http Server Annotaion
 * @date 2013-12-24
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HttpServer {
    public static final String DEFAULT = "DEFAULT";

    public String value() default DEFAULT; // http://servername:port

    public abstract String serverName();

    public String serverPort() default DEFAULT; // http://servername

}
