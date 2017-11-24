/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xiaotian.framework.util.async.loadimage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.xiaotian.framework.BuildConfig;
import com.xiaotian.framework.common.Mylog;
import com.xiaotian.frameworkxt.android.util.UtilUriMatcher;

/**
 * 从URL网络加载图片[Fetcher 缓冲到本地缓冲文件夹]到ImageView A simple subclass of
 * {@link ImageWorkerResizer} that fetches and resizes images fetched from a
 * URL.
 */
public class ImageWorkerResizerFetcher extends ImageWorkerResizer {
	private static final String TAG = "ImageWorkerResizerFetcher";

	private static final int DISK_CACHE_INDEX = 0;
	private static final int IO_BUFFER_SIZE = 8 * 1024; // IO 缓冲大小
	private static final String HTTP_CACHE_DIR = "http"; // Http 网络缓冲目录
	private static final Object mHttpDiskCacheLock = new Object(); // 磁盘缓冲锁
	private static final int HTTP_CACHE_SIZE = 10 * 1024 * 1024; // Http网络缓冲大小10MB
	//
	private int mImageHeight;
	private int mImageWidth;
	private File mHttpCacheDir;
	private DiskLruCache mHttpDiskCache;
	private boolean mHttpDiskCacheStarting = true;

	// 构造器
	public ImageWorkerResizerFetcher(Context context, int imageWidth, int imageHeight) {
		super(context, imageWidth, imageHeight); // 长,宽
		init(context);
	}

	public ImageWorkerResizerFetcher(Context context, int imageSize) {
		super(context, imageSize);// 正方形
		init(context);
	}

	// 初始化
	private void init(Context context) {
		checkConnection(context); // 检测网络状态
		mHttpCacheDir = ImageCache.getDiskCacheDir(context, HTTP_CACHE_DIR);
	}

	// 磁盘内部缓冲实体
	@Override
	protected void initDiskCacheInternal() {
		super.initDiskCacheInternal();
		initHttpDiskCache();
	}

	// 初始化Http网络磁盘缓冲
	private void initHttpDiskCache() {
		if (!mHttpCacheDir.exists()) mHttpCacheDir.mkdirs();
		synchronized (mHttpDiskCacheLock) {
			if (ImageCache.getUsableSpace(mHttpCacheDir) > HTTP_CACHE_SIZE) {
				try {
					mHttpDiskCache = DiskLruCache.open(mHttpCacheDir, 1, 1, HTTP_CACHE_SIZE);
					if (BuildConfig.DEBUG) Log.d(TAG, "HTTP cache initialized");
				} catch (IOException e) {
					mHttpDiskCache = null;
				}
			}
			mHttpDiskCacheStarting = false;
			mHttpDiskCacheLock.notifyAll();
		}
	}

	// 清空内部缓冲
	@Override
	protected void clearCacheInternal() {
		super.clearCacheInternal();
		synchronized (mHttpDiskCacheLock) {
			if (mHttpDiskCache != null && !mHttpDiskCache.isClosed()) {
				try {
					mHttpDiskCache.delete();
					if (BuildConfig.DEBUG) Log.d(TAG, "HTTP cache cleared");
				} catch (IOException e) {
					Log.e(TAG, "clearCacheInternal - " + e);
				}
				mHttpDiskCacheStarting = true;
				mHttpDiskCache = null;
				initHttpDiskCache();
			}
		}
	}

	// 提交刷新内部缓冲
	@Override
	protected void flushCacheInternal() {
		super.flushCacheInternal();
		synchronized (mHttpDiskCacheLock) {
			if (mHttpDiskCache != null) {
				try {
					mHttpDiskCache.flush();
					if (BuildConfig.DEBUG) Log.d(TAG, "HTTP cache flushed");
				} catch (IOException e) {
					Log.e(TAG, "flush - " + e);
				}
			}
		}
	}

	// 关闭内部缓冲
	@Override
	protected void closeCacheInternal() {
		super.closeCacheInternal();
		synchronized (mHttpDiskCacheLock) {
			if (mHttpDiskCache != null) {
				try {
					if (!mHttpDiskCache.isClosed()) {
						mHttpDiskCache.close();
						mHttpDiskCache = null;
						if (BuildConfig.DEBUG) Log.d(TAG, "HTTP cache closed");
					}
				} catch (IOException e) {
					Log.e(TAG, "closeCacheInternal - " + e);
				}
			}
		}
	}

	// 处理图片数据: data:资源Path(本地文件Path,或者网络URL)
	@Override
	protected Bitmap processBitmap(Object data) {
		return processBitmap(String.valueOf(data));
	}

	// 处理String的资源图片
	private Bitmap processBitmap(String data) {
		if (BuildConfig.DEBUG) Log.d(TAG, "processBitmap - " + data);
		String key = ImageCache.hashKeyForDisk(data);
		FileInputStream fileInputStream = null;
		FileDescriptor fileDescriptor = null;
		DiskLruCache.Snapshot snapshot;
		synchronized (mHttpDiskCacheLock) {
			// Wait for disk cache to initialize
			while (mHttpDiskCacheStarting) {
				try {
					mHttpDiskCacheLock.wait();
				} catch (InterruptedException e) {}
			}

			if (mHttpDiskCache != null) {
				try {
					snapshot = mHttpDiskCache.get(key);
					if (snapshot == null) {
						if (BuildConfig.DEBUG) Log.d(TAG, "processBitmap, not found in http cache, downloading...");
						DiskLruCache.Editor editor = mHttpDiskCache.edit(key);
						if (editor != null) {
							// 根据Scheme加载
							switch (UtilUriMatcher.ResourcesScheme.ofUri(data)) {
							case HTTP:
							case HTTPS:
								if (downloadUrlToStream(data, editor.newOutputStream(DISK_CACHE_INDEX))) {
									editor.commit();
								} else {
									editor.abort();
								}
								break;
							case FILE:
								if (copyFileToStream(data, editor.newOutputStream(DISK_CACHE_INDEX))) {
									editor.commit();
								} else {
									editor.abort();
								}
								break;
							default:
								break;

							}

						}
						snapshot = mHttpDiskCache.get(key);
					}
					if (snapshot != null) {
						fileInputStream = (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
						fileDescriptor = fileInputStream.getFD();
					}
				} catch (IOException e) {
					Log.e(TAG, "processBitmap - " + e);
				} catch (IllegalStateException e) {
					Log.e(TAG, "processBitmap - " + e);
				} finally {
					if (fileDescriptor == null && fileInputStream != null) {
						try {
							fileInputStream.close();
						} catch (IOException e) {}
					}
				}
			}
		}

		Bitmap bitmap = null;
		if (fileDescriptor != null) {
			Mylog.info("request image [" + mImageWidth + "," + mImageHeight + "]");
			bitmap = decodeSampledBitmapFromDescriptor(fileDescriptor, mImageWidth, mImageHeight, getImageCache());
			Mylog.info("resource image [" + bitmap.getWidth() + "," + bitmap.getHeight() + "]");
		}
		if (fileInputStream != null) {
			try {
				fileInputStream.close();
			} catch (IOException e) {}
		}
		return bitmap;
	}

	public void setImageSize(int width, int height) {
		super.setImageSize(width, height);
		mImageWidth = width;
		mImageHeight = height;
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
			Log.e(TAG, "Error in downloadBitmap - " + e);
		} finally {
			if (urlConnection != null) urlConnection.disconnect();
			try {
				if (out != null) out.close();
				if (in != null) in.close();
			} catch (final IOException e) {}
		}
		return false;
	}

	public static void disableConnectionReuseIfNecessary() {
		// SDK 小于2.2
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
			System.setProperty("http.keepAlive", "false"); // 当前SDK系统属性
		}
	}

	// 检测网络连接状态
	protected void checkConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
			Log.e(TAG, "checkConnection - no connection found");
		}
	}

	public static boolean copyFileToStream(String filePath, OutputStream outputStream) {
		File file = new File(UtilUriMatcher.ResourcesScheme.FILE.crop(filePath));
		if (file.exists()) {
			byte[] bbuf = new byte[1024];
			BufferedInputStream is = null;
			int hasReaded;
			try {
				is = new BufferedInputStream(new FileInputStream(file));
				while ((hasReaded = is.read(bbuf)) != -1) {
					outputStream.write(bbuf, 0, hasReaded);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} finally {
				if (is != null) try {
					is.close();
				} catch (IOException e) {}
			}
			return true;
		}
		return false;
	}
}
