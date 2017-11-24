package com.xiaotian.framework.util.async.loadimage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.xiaotian.framework.BuildConfig;
import com.xiaotian.framework.common.Mylog;
import com.xiaotian.frameworkxt.android.util.UtilSDKVersion;

/**
 * 
 * @version 1.0.0
 * @author XiaoTian
 * @name NetworkFileCache
 * @description Network File Cache
 * @date Oct 28, 2014
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class NetworkFileCache {
	private static final String TAG = "NetworkFileCache";
	private static final String DEFAULT_DISK_CACHE_FOLDER = "NetworkCacheFile";
	//
	private static final int IO_BUFFER_SIZE = 8 * 1024; // IO 缓冲大小
	private static final int DISK_CACHE_INDEX = 0; // 缓冲索引
	private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB(B)
	private static final boolean DEFAULT_INIT_DISK_CACHE_ON_CREATE = false; // 初始化磁盘缓冲目录
	private DiskLruCache mDiskLruCache;
	private NetworkCacheParams mCacheParams;
	private boolean mDiskCacheStarting = true;
	private final Object mDiskCacheLock = new Object(); // 磁盘锁对象

	// 创建/获取 ,缓冲图片配置到FragmentManager中
	public static NetworkFileCache getInstance(FragmentManager fragmentManager, NetworkCacheParams cacheParams) {
		Mylog.info(TAG, "NetworkFileCache Get Instance.");
		// Search for, or create an instance of the non-UI RetainFragment
		final RetainFragment mRetainFragment = findOrCreateRetainFragment(fragmentManager);
		if (mRetainFragment == null) {
			return new NetworkFileCache(cacheParams);
		}
		// See if we already have an ImageCache stored in RetainFragment
		NetworkFileCache fileCache = (NetworkFileCache) mRetainFragment.getObject();
		// No existing ImageCache, create one and store it in RetainFragment
		if (fileCache == null) {
			fileCache = new NetworkFileCache(cacheParams);
			mRetainFragment.setObject(fileCache);
		}
		return fileCache;
	}

	// 构造器
	private NetworkFileCache(NetworkCacheParams cacheParams) {
		init(cacheParams);
	}

	// 初始化
	private void init(NetworkCacheParams cacheParams) {
		mCacheParams = cacheParams;
		initDiskCache();
	}

	// 初始化磁盘缓冲
	public void initDiskCache() {
		synchronized (mDiskCacheLock) {
			// 磁盘缓冲锁
			if (mDiskLruCache == null || mDiskLruCache.isClosed()) {
				File diskCacheDir = mCacheParams.diskCacheDir;
				// 开启磁盘缓冲
				if (diskCacheDir != null) {
					if (!diskCacheDir.exists()) diskCacheDir.mkdirs(); // 创建磁盘目录
					if (getUsableSpace(diskCacheDir) > mCacheParams.diskCacheSize) {
						try {
							// 初始化磁盘缓冲处理器
							mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, mCacheParams.diskCacheSize);
							Mylog.info(TAG, "Disk cache initialized");
						} catch (final IOException e) {
							mCacheParams.diskCacheDir = null;
							Mylog.printStackTrace(e);
						} catch (Exception e) {
							mCacheParams.diskCacheDir = null;
							Mylog.printStackTrace(e);
						}
					}
				}
			}
			mDiskCacheStarting = false;
			mDiskCacheLock.notifyAll();
		}
	}

	public void addInputStreamToCache(String url, InputStream is) {
		if (url == null || is == null) return;
		synchronized (mDiskCacheLock) {
			while (mDiskCacheStarting) { // 其他线程在读取,正在执行,锁加入等待
				try {
					mDiskCacheLock.wait();
				} catch (InterruptedException e) {}
			}
			// 磁盘缓冲
			if (mDiskLruCache != null) {
				final String key = hashKeyForDisk(url);
				OutputStream out = null;
				try {
					DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
					if (snapshot == null) {
						final DiskLruCache.Editor editor = mDiskLruCache.edit(key);
						if (editor != null) {
							out = editor.newOutputStream(DISK_CACHE_INDEX);
							byte[] bbuf = new byte[512];
							int hasRead = -1;
							while ((hasRead = is.read(bbuf)) != -1) {
								out.write(bbuf, 0, hasRead);
							}
							editor.commit();
							out.close();
							is.close();
						}
					} else {
						snapshot.getInputStream(DISK_CACHE_INDEX).close();
					}
				} catch (final IOException e) {
					Mylog.info(TAG, "addFileToCache - " + e);
				} catch (Exception e) {
					Mylog.info(TAG, "addFileToCache - " + e);
				} finally {
					try {
						if (is != null) is.close();
						if (out != null) out.close();
					} catch (IOException e) {}
				}
			}
		}
	}

	public FileInputStream getInputStreamFromCache(String url) throws IOException {
		// 磁盘缓冲取
		final String key = hashKeyForDisk(url);
		synchronized (mDiskCacheLock) { // 锁磁盘目录
			while (mDiskCacheStarting) { // 其他线程在读取,正在执行,锁加入等待
				try {
					mDiskCacheLock.wait();
				} catch (InterruptedException e) {}
			}
			if (mDiskLruCache != null) {
				DiskLruCache.Snapshot snapshot = null;
				snapshot = mDiskLruCache.get(key);
				if (snapshot != null) {
					// 已缓冲
					Mylog.info(TAG, "Disk cache hit");
					return (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
				} else {
					// 无缓冲
					Mylog.info(TAG, "not found file in http cache, downloading...");
					DiskLruCache.Editor editor = mDiskLruCache.edit(key);
					if (editor != null) {
						if (downloadUrlToStream(url, editor.newOutputStream(DISK_CACHE_INDEX))) {
							editor.commit();
						} else {
							editor.abort();
						}
					}
					snapshot = mDiskLruCache.get(key);
					if (snapshot != null) {
						return (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
					}
				}
			}
			return null;
		}
	}

	// 获取String 的唯一Key,用于缓冲的Key
	public static String hashKeyForDisk(String key) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5"); // MD5编码器
			mDigest.update(key.getBytes());// 计算指定byte的MD5
			cacheKey = bytesToHexString(mDigest.digest());// 获取MD5值,并转换为Hex16进制
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());// 算了,编码错误,MD5神经病,还是直接拿哈希码吧
		}
		return cacheKey;
	}

	// Byte -> Hex16进制码String
	private static String bytesToHexString(byte[] bytes) {
		// http://stackoverflow.com/questions/332079
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	@TargetApi(9)
	@SuppressWarnings("deprecation")
	public static long getUsableSpace(File path) {
		// 2.3以上File可以直接获取Path的剩余空间
		if (UtilSDKVersion.hasGingerbread()) return path.getUsableSpace();
		// 其他计算剩余空间
		final StatFs stats = new StatFs(path.getPath());// StatFs文件系统信息处理器[对Path信息收集]
		return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks(); // 块的大小*可用块数
	}

	public void clearCache() {
		// 清空磁盘缓冲
		synchronized (mDiskCacheLock) { // 磁盘文件锁
			mDiskCacheStarting = true; // 初始化
			if (mDiskLruCache != null && !mDiskLruCache.isClosed()) {
				try {
					mDiskLruCache.delete(); // 删除磁盘缓冲下的所有文件,目录
					if (BuildConfig.DEBUG) Log.d(TAG, "Disk cache cleared");
				} catch (IOException e) {
					Log.e(TAG, "clearCache - " + e);
				}
				mDiskLruCache = null;
				initDiskCache(); // 初始化磁盘目录
			}
			mDiskCacheStarting = false; // 初始化完成
		}
	}

	public void flush() {
		synchronized (mDiskCacheLock) {
			if (mDiskLruCache != null) {
				try {
					mDiskLruCache.flush();
					if (BuildConfig.DEBUG) Log.d(TAG, "Disk cache flushed");
				} catch (IOException e) {
					Log.e(TAG, "flush - " + e);
				}
			}
		}
	}

	public void close() {
		synchronized (mDiskCacheLock) {
			if (mDiskLruCache != null) {
				try {
					if (!mDiskLruCache.isClosed()) {
						mDiskLruCache.close();
						mDiskLruCache = null;
						if (BuildConfig.DEBUG) Log.d(TAG, "Disk cache closed");
					}
				} catch (IOException e) {
					Log.e(TAG, "close - " + e);
				}
			}
		}
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
			Log.e(TAG, "Error in download the file to cache Stream - " + e);
		} finally {
			if (urlConnection != null) urlConnection.disconnect();
			try {
				if (out != null) out.close();
				if (in != null) in.close();
			} catch (final IOException e) {}
		}
		return false;
	}

	public void disableConnectionReuseIfNecessary() {
		// SDK 小于2.2
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
			System.setProperty("http.keepAlive", "false"); // 当前SDK系统属性
		}
	}

	// 配置
	public static class NetworkCacheParams {
		public File diskCacheDir;
		public int diskCacheSize = DEFAULT_DISK_CACHE_SIZE;

		public NetworkCacheParams(Context context) {
			if (BuildConfig.DEBUG) Log.i(TAG, "NetworkCacheParams Initializing.");
			diskCacheDir = getDiskCacheDir(context, DEFAULT_DISK_CACHE_FOLDER);
		}

		public NetworkCacheParams(Context context, String diskCacheDirectoryName) {
			if (BuildConfig.DEBUG) Log.i(TAG, "NetworkCacheParams Initializing.");
			diskCacheDir = getDiskCacheDir(context, diskCacheDirectoryName);
		}

		public static File getDiskCacheDir(Context context, String uniqueName) {
			// 获取磁盘缓冲Path
			final String cachePath = (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !isExternalStorageRemovable()) // 外部可卸载SD
			? getExternalCacheDir(context).getPath() // 获取外部可卸载SD缓冲目录
					: context.getCacheDir().getPath();// 上下文缓冲[通常在内部存蓄上]
			return new File(cachePath + File.separator + uniqueName);
		}

		@TargetApi(8)
		public static File getExternalCacheDir(Context context) {
			// 2.2以上,直接获取外设缓冲DIR路径(系统自动分配了缓冲文件夹)
			if (UtilSDKVersion.hasFroyo()) return context.getExternalCacheDir();
			// 其他没有缓冲DIR,构造一个
			final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
			return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
		}

		@TargetApi(9)
		public static boolean isExternalStorageRemovable() {
			// 2.3以上检测SD卡是外插SD卡还是机身SD卡[2.3以上有机身SD卡]
			if (UtilSDKVersion.hasGingerbread()) return Environment.isExternalStorageRemovable(); // 可移除SD卡[非机身SD]
			// 其他没有内置SD卡[可以移除啦]
			return true;
		}
	}

	private static RetainFragment findOrCreateRetainFragment(FragmentManager fm) {
		if (fm == null) return null;
		// 创建或重引用Fragment碎片
		RetainFragment mRetainFragment = (RetainFragment) fm.findFragmentByTag(TAG); // FM根据TAG获取碎片
		if (mRetainFragment == null) {
			mRetainFragment = new RetainFragment();
			fm.beginTransaction().add(mRetainFragment, TAG).commitAllowingStateLoss();
		}
		return mRetainFragment;
	}

	public static class RetainFragment extends Fragment {
		private Object mObject;

		public RetainFragment() {}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setRetainInstance(true); // 可重用
		}

		public void setObject(Object object) {
			mObject = object;
		}

		public Object getObject() {
			return mObject;
		}
	}
}
