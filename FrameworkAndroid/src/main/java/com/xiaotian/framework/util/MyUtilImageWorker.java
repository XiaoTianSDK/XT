package com.xiaotian.framework.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;

import com.xiaotian.framework.common.Mylog;
import com.xiaotian.framework.util.async.loadimage.ImageCache;
import com.xiaotian.framework.util.async.loadimage.ImageWorkerResizer;
import com.xiaotian.frameworkxt.android.util.UtilUriMatcher;

/**
 * 
 * @version 1.0.0
 * @author XiaoTian
 * @name MyUtilImageWorker
 * @description 网络,本地图片加载器
 * @date Nov 7, 2014
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class MyUtilImageWorker extends ImageWorkerResizer {
	private final int IO_BUFFER_SIZE = 8 * 1024;
	private Resources mResources;
	private int mImageHeight;
	private int mImageWidth;

	public MyUtilImageWorker(Context context) {
		super(context);
		mResources = context.getResources();
	}

	@Override
	protected Bitmap processBitmap(Object data) {
		if (data == null || data.equals("")) return null;
		String uri = String.valueOf(data);
		switch (UtilUriMatcher.ResourcesScheme.ofUri(uri)) {
		case FILE:
			String filePath = UtilUriMatcher.ResourcesScheme.FILE.crop(uri);
			return decodeSampledBitmapFromFile(filePath, mImageWidth, mImageHeight, getImageCache());
		case HTTP:
		case HTTPS:
			String key = ImageCache.hashKeyForDisk(uri);
			FileInputStream fileInputStream = null;
			FileDescriptor fileDescriptor = null;
			OutputStream outputStream = null;
			Bitmap bitmap = null;
			File tempFile = null;
			// URL访问资源
			try {
				tempFile = File.createTempFile("image_", null);
				outputStream = new FileOutputStream(tempFile);
				if (!downloadUrlToStream(uri, outputStream)) return null;
				fileInputStream = new FileInputStream(tempFile);
				fileDescriptor = fileInputStream.getFD();
			} catch (MalformedURLException e) {
				Mylog.printStackTrace(e);
				return null;
			} catch (IOException e1) {
				Mylog.printStackTrace(e1);
				return null;
			} finally {
				if (outputStream != null) try {
					outputStream.close();
				} catch (IOException e) {}
				if (tempFile != null) tempFile.deleteOnExit();
			}
			if (fileDescriptor != null) {
				bitmap = decodeSampledBitmapFromDescriptor(fileDescriptor, mImageWidth, mImageHeight, getImageCache());
			}
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {}
			}
			getImageCache().addBitmapToCache(key, new BitmapDrawable(mResources, bitmap));
			return bitmap;
		case DRAWABLE:
			String resultId = UtilUriMatcher.ResourcesScheme.DRAWABLE.crop(uri);
			return decodeSampledBitmapFromResource(mResources, Integer.valueOf(resultId), mImageWidth, mImageHeight, getImageCache());
		default:
			return null;
		}
	}

	// 设置图片大小[width,height]
	public void setImageSize(int width, int height) {
		mImageWidth = width;
		mImageHeight = height;
	}

	// 设置图片大小[width = height = size]
	public void setImageSize(int size) {
		setImageSize(size, size);
	}

	// 下载URL文件到指定的输出流中
	public boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
		disableConnectionReuseIfNecessary(); // 关闭网络持续连接属性,适用SDK 2.2以下版本
		HttpURLConnection urlConnection = null;
		BufferedOutputStream out = null;
		BufferedInputStream in = null;
		try {
			final URL url = new URL(urlString); // URL访问资源
			urlConnection = (HttpURLConnection) url.openConnection(); // 打开连接,默认GET
			in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE); // 输入流
			out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);// 缓冲输出流,接入传入的输出流中
			int hasReaded;
			while ((hasReaded = in.read()) != -1) { // 读取数据
				out.write(hasReaded); // 写入读取数据
			}
			return true;
		} catch (final IOException e) {
			Mylog.printStackTrace(e);
		} finally {
			if (urlConnection != null) urlConnection.disconnect();
			try {
				if (out != null) out.close();
				if (in != null) in.close();
			} catch (final IOException e) {
				Mylog.printStackTrace(e);
			}
		}
		return false;
	}

	public void disableConnectionReuseIfNecessary() {
		// SDK 小于2.2
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
			System.setProperty("http.keepAlive", "false"); // 当前SDK系统属性
		}
	}
}
