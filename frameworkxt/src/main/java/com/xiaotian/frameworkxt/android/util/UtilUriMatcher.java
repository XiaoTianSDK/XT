package com.xiaotian.frameworkxt.android.util;

import java.util.Locale;

import android.content.UriMatcher;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name UtilUriMatcher
 * @description UriMatcher Util
 * @date 2014-7-8
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class UtilUriMatcher extends UriMatcher {
	public enum ResourcesScheme {
		HTTP("http"), HTTPS("https"), FILE("file"), CONTENT("content"), ASSETS("assets"), DRAWABLE("drawable"), UNKNOWN("");
		//
		private String scheme;
		private String uriPrefix;

		ResourcesScheme(String scheme) {
			this.scheme = scheme;
			this.uriPrefix = scheme + "://";
		}

		public static ResourcesScheme ofUri(String uri) {
			if (uri != null) {
				for (ResourcesScheme s : values()) {
					if (s.belongsTo(uri)) {
						return s;
					}
				}
			}
			return UNKNOWN;
		}

		/** The uri belong to the given scheme **/
		private boolean belongsTo(String uri) {
			return uri.toLowerCase(Locale.getDefault()).startsWith(uriPrefix);
		}

		/** Appends scheme to incoming path */
		public String wrap(String path) {
			return uriPrefix + path;
		}

		/** Appends scheme to incoming path */
		public String wrap(Integer path) {
			return uriPrefix + path;
		}

		/** Removed scheme part ("scheme://") from incoming URI */
		public String crop(String uri) {
			if (!belongsTo(uri)) {
				throw new IllegalArgumentException(String.format("URI [%1$s] doesn't have expected scheme [%2$s]", uri, scheme));
			}
			return uri.substring(uriPrefix.length());
		}
	}

	public UtilUriMatcher(int code) {
		super(code);
	}

	// https?://([-\w\.]+)+(:\d+)?(/([\w/_\.]*(\?\S+)?)?)?
	public String createFileUri(String path) {
		return null;
	}
}
