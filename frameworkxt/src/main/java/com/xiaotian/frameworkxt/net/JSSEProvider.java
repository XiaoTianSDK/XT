package com.xiaotian.frameworkxt.net;

import java.security.AccessController;
import java.security.Provider;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name JSSEProvider
 * @description Provider JSSE For Email
 * @date 2014-4-15
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public final class JSSEProvider extends Provider {

	private static final long serialVersionUID = 1L;

	public JSSEProvider() {
		super("HarmonyJSSE", 1.0, "Harmony JSSE Provider");
		AccessController.doPrivileged(new java.security.PrivilegedAction<Void>() {
			public Void run() {
				put("SSLContext.TLS", "org.apache.harmony.xnet.provider.jsse.SSLContextImpl");
				put("Alg.Alias.SSLContext.TLSv1", "TLS");
				put("KeyManagerFactory.X509", "org.apache.harmony.xnet.provider.jsse.KeyManagerFactoryImpl");
				put("TrustManagerFactory.X509", "org.apache.harmony.xnet.provider.jsse.TrustManagerFactoryImpl");
				return null;
			}
		});
	}
}
