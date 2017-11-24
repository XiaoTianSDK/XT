package com.xiaotian.framework.activity;

import java.lang.reflect.InvocationTargetException;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;

/**
 * @version 1.0.0
 * @author Administrator
 * @name ActivityWebView
 * @description 网络浏览页面
 * @date 2015-4-1
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class ActivityWebView extends BaseActivity {
	public static final String INVOKE_TAG = "InvokeTag"; // Js 调用本实例[This]接口Tag
	WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initializingView();
		initializingData();
	}

	@Override
	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	protected void initializingView() {
		mWebView = new WebView(this);
		setContentView(mWebView);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.addJavascriptInterface(this, INVOKE_TAG);
		WebSettings setting = mWebView.getSettings();
		setting.setPluginState(PluginState.ON);
		setting.setLoadWithOverviewMode(true);
		setting.setUseWideViewPort(true);
		mWebView.setWebChromeClient(new WebChromeClient());
	}

	@Override
	protected void initializingData() {
		mWebView.loadUrl("http://www.uyeo.com.cn/");
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			mWebView.getClass().getMethod("onPause").invoke(mWebView, (Object[]) null);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			mWebView.getClass().getMethod("onResume").invoke(mWebView, (Object[]) null);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
