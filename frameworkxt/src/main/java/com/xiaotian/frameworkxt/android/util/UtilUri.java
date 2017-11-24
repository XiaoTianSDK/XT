package com.xiaotian.frameworkxt.android.util;

import android.net.Uri;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name UtilUri
 * @description Uri Util
 * @date 2014-7-8
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class UtilUri {
	final String ASSET_BASE = "file:///android_asset/";
	// to refer to bar.png under your package's res/drawable/ directory, use
	// "file:///android_res/drawable/bar.png". Use "drawable" to refer to
	// "drawable-hdpi" directory as well.
	final String PROXY_BASE = "file:///cookieless_proxy/";
	final String RESOURCE_BASE = "file:///android_res/";
	final String CONTENT_BASE = "content:";
	final String FILE_BASE = "file://";

	public boolean isAssetUrl(String url) {
		return (null != url) && url.startsWith(ASSET_BASE);
	}

	public boolean isResourceUrl(String url) {
		return (null != url) && url.startsWith(RESOURCE_BASE);
	}

	public boolean isFileUrl(String url) {
		return (null != url) && (url.startsWith(FILE_BASE) && !url.startsWith(ASSET_BASE) && !url.startsWith(PROXY_BASE));
	}

	public boolean isAboutUrl(String url) {
		return (null != url) && url.startsWith("about:");
	}

	public boolean isDataUrl(String url) {
		return (null != url) && url.startsWith("data:");
	}

	public boolean isJavaScriptUrl(String url) {
		return (null != url) && url.startsWith("javascript:");
	}

	public boolean isHttpUrl(String url) {
		return (null != url) && (url.length() > 6) && url.substring(0, 7).equalsIgnoreCase("http://");
	}

	public boolean isHttpsUrl(String url) {
		return (null != url) && (url.length() > 7) && url.substring(0, 8).equalsIgnoreCase("https://");
	}

	public boolean isNetworkUrl(String url) {
		if (url == null || url.length() == 0) return false;
		return isHttpUrl(url) || isHttpsUrl(url);
	}

	public boolean isContentUrl(String url) {
		return (null != url) && url.startsWith(CONTENT_BASE);
	}

	public boolean isValidUrl(String url) {
		if (url == null || url.length() == 0) {
			return false;
		}

		return (isAssetUrl(url) || isResourceUrl(url) || isFileUrl(url) || isAboutUrl(url) || isHttpUrl(url) || isHttpsUrl(url) || isJavaScriptUrl(url) || isContentUrl(url));
	}

	public Uri parse(String uriString, String... path) {
		Uri uri = Uri.parse(uriString);
		// 添加Path参数在URI的末尾,构造新的URI
		for (String p : path) {
			uri = Uri.withAppendedPath(uri, p);
		}
		return uri;
	}

	public Uri withAppendedPath(Uri uri, String... path) {
		for (String p : path) {
			uri = Uri.withAppendedPath(uri, p);
		}
		return uri;
	}
}
