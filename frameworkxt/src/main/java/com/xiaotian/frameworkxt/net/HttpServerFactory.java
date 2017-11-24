package com.xiaotian.frameworkxt.net;

/**
 * @version 1.0.0
 * @author Administrator
 * @name HttpServerFactory
 * @description HttpServerConnector 工厂
 * @date 2015-5-7
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public abstract class HttpServerFactory {
	// 工厂方法模式(工厂模式, 高内聚，低耦合,工厂方法)
	public HttpServerConnector create() {
		return createHttpServerConnector();
	}

	// 子类去实现(实际方法)
	public abstract HttpServerConnector createHttpServerConnector();

	// 简单工厂模式(静态工厂,耦合性低)
	public static HttpServerConnector createHttpServerConnector(String type) {
		if (type.equals("HttpServerConnector")) {
			return new HttpServerConnector();
		}
		return null;
	}
	// 抽象工厂(定义统一访问Interface)
}
