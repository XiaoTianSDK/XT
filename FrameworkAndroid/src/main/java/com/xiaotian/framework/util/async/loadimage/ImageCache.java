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

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.xiaotian.framework.BuildConfig;
import com.xiaotian.frameworkxt.android.util.UtilSDKVersion;

/**
 * This class handles disk and memory caching of bitmaps in conjunction with the {@link ImageWorker} class and its subclasses. Use
 * {@link ImageCache#getInstance(FragmentManager, ImageCacheParams)} to get an instance of this class, although usually a cache should be added directly to an {@link ImageWorker}
 * by calling {@link ImageWorker#addImageCache(FragmentManager, ImageCacheParams)}.
 */
public class ImageCache {
	private static final String TAG = "ImageCache";
	//
	private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 5; // 5MB(KB)
	private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB(B)
	//
	private static final int DISK_CACHE_INDEX = 0; // 缓冲索引
	private static final int DEFAULT_COMPRESS_QUALITY = 70; // 压缩质量
	private static final boolean DEFAULT_MEM_CACHE_ENABLED = true; // 默认开启内存缓冲
	private static final boolean DEFAULT_DISK_CACHE_ENABLED = true; // 默认开启磁盘缓冲
	private static final boolean DEFAULT_INIT_DISK_CACHE_ON_CREATE = false; // 初始化磁盘缓冲目录
	private static final CompressFormat DEFAULT_COMPRESS_FORMAT = CompressFormat.JPEG; // 压缩类型

	private DiskLruCache mDiskLruCache;
	private ImageCacheParams mCacheParams;
	private boolean mDiskCacheStarting = true;
	private final Object mDiskCacheLock = new Object(); // 磁盘锁对象
	private final Object mResuableCacheLock = new Object(); // 重用锁对象
	private LruCache<String, BitmapDrawable> mMemoryCache; // supportV4封装的[队列KV集合]
	// SoftReference should be cleared and enqueued as late as possible, that
	// is, in case the VM is in danger of running out of memory
	// WeakReference may be cleared and enqueued as soon as is known to be
	// weakly-referenced
	private Set<SoftReference<Bitmap>> mReusableBitmaps; // 弱引用的Bitmap的Set集合,用于Bitmap的inBitmap缓冲

	/******************************************************** Static Instance ********************************************************/
	// 创建/获取 ,缓冲图片配置到FragmentManager中
	public static ImageCache getInstance(FragmentManager fragmentManager, ImageCacheParams cacheParams) {
		if (BuildConfig.DEBUG) Log.i("ImageCache", "ImageCache Get Instance.");
		// Search for, or create an instance of the non-UI RetainFragment
		final RetainFragment mRetainFragment = findOrCreateRetainFragment(fragmentManager);
		if (mRetainFragment == null) return new ImageCache(cacheParams);;
		// See if we already have an ImageCache stored in RetainFragment
		ImageCache imageCache = (ImageCache) mRetainFragment.getObject();
		// No existing ImageCache, create one and store it in RetainFragment
		if (imageCache == null) {
			imageCache = new ImageCache(cacheParams);
			mRetainFragment.setObject(imageCache);
		}
		return imageCache;
	}

	// 构造器
	private ImageCache(ImageCacheParams cacheParams) {
		init(cacheParams);
	}

	// 初始化
	private void init(ImageCacheParams cacheParams) {
		mCacheParams = cacheParams;
		// 开启内存缓冲
		if (mCacheParams.memoryCacheEnabled) {
			if (BuildConfig.DEBUG) Log.d(TAG, "Memory cache created (size = " + mCacheParams.memCacheSize + ")");
			// 3.0 以上,Bitmap的弱引用Set,作为inBitmap参数decode时重用
			if (UtilSDKVersion.hasHoneycomb()) mReusableBitmaps = Collections.synchronizedSet(new HashSet<SoftReference<Bitmap>>());
			// BitmapDrawable 队列集合
			mMemoryCache = new LruCache<String, BitmapDrawable>(mCacheParams.memCacheSize) {
				// LruCache
				// 队列对象,evicted:是否队满释放,oldValue:队满释放对象,newValue:put/remove时的对象
				@Override
				protected void entryRemoved(boolean evicted, String key, BitmapDrawable oldValue, BitmapDrawable newValue) {
					if (RecyclingBitmapDrawable.class.isInstance(oldValue)) {
						// 3.0以下自定引用处理图片重用
						((RecyclingBitmapDrawable) oldValue).setIsCached(false);
					} else {
						// 3.0以上,使用弱引用集合方式处理图片重加载
						if (UtilSDKVersion.hasHoneycomb()) {
							synchronized (mResuableCacheLock) {
								mReusableBitmaps.add(new SoftReference<Bitmap>(oldValue.getBitmap()));
							}
						}
					}
				}

				@Override
				protected int sizeOf(String key, BitmapDrawable value) {
					// 获取 Item 的大小, 添加时调用
					final int bitmapSize = getBitmapSize(value) / 1024; // 计算大小KB
					return bitmapSize == 0 ? 1 : bitmapSize;
				}
			};
		}
		// 初始化磁盘目录
		if (cacheParams.initDiskCacheOnCreate) {
			// Set up disk cache
			initDiskCache();
		}
	}

	// 初始化磁盘缓冲
	public void initDiskCache() {
		synchronized (mDiskCacheLock) {
			// 磁盘缓冲锁
			if (mDiskLruCache == null || mDiskLruCache.isClosed()) {
				File diskCacheDir = mCacheParams.diskCacheDir;
				// 开启磁盘缓冲
				if (mCacheParams.diskCacheEnabled && diskCacheDir != null) {
					if (!diskCacheDir.exists()) diskCacheDir.mkdirs(); // 创建磁盘目录
					if (getUsableSpace(diskCacheDir) > mCacheParams.diskCacheSize) {
						try {
							// 初始化磁盘缓冲处理器
							mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, mCacheParams.diskCacheSize);
							if (BuildConfig.DEBUG) Log.d(TAG, "Disk cache initialized");
						} catch (final IOException e) {
							mCacheParams.diskCacheDir = null;
							Log.e(TAG, "initDiskCache - " + e);
						}
					}
				}
			}
			mDiskCacheStarting = false;
			mDiskCacheLock.notifyAll();
		}
	}

	public void addBitmapToCache(String data, BitmapDrawable value) {
		if (data == null || value == null) return;
		// 内存缓冲
		if (mMemoryCache != null) {
			// 3.0以下自定引用的Bitmap,设置有效性
			if (RecyclingBitmapDrawable.class.isInstance(value)) ((RecyclingBitmapDrawable) value).setIsCached(true);
			// 加入内存
			mMemoryCache.put(data, value);
		}
		synchronized (mDiskCacheLock) {
			// 磁盘缓冲
			if (mDiskLruCache != null) {
				final String key = hashKeyForDisk(data);
				OutputStream out = null;
				try {
					DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
					if (snapshot == null) {
						final DiskLruCache.Editor editor = mDiskLruCache.edit(key);
						if (editor != null) {
							out = editor.newOutputStream(DISK_CACHE_INDEX);
							value.getBitmap().compress(mCacheParams.compressFormat, mCacheParams.compressQuality, out);
							editor.commit();
							out.close();
						}
					} else {
						snapshot.getInputStream(DISK_CACHE_INDEX).close();
					}
				} catch (final IOException e) {
					Log.e(TAG, "addBitmapToCache - " + e);
				} catch (Exception e) {
					Log.e(TAG, "addBitmapToCache - " + e);
				} finally {
					try {
						if (out != null) out.close();
					} catch (IOException e) {}
				}
			}
		}
	}

	public BitmapDrawable getBitmapFromMemCache(String data) {
		// 缓冲取
		BitmapDrawable memValue = null;
		if (mMemoryCache != null) memValue = mMemoryCache.get(data); // V4队列集合
		if (BuildConfig.DEBUG && memValue != null) Log.d(TAG, "Memory cache hit");
		return memValue;
	}

	public Bitmap getBitmapFromDiskCache(String imageFilePath) {
		// 磁盘缓冲取
		Bitmap bitmap = null;
		final String key = hashKeyForDisk(imageFilePath);
		synchronized (mDiskCacheLock) { // 锁磁盘目录
			while (mDiskCacheStarting) { // 其他线程在读取,正在执行,锁加入等待
				try {
					mDiskCacheLock.wait();
				} catch (InterruptedException e) {}
			}
			if (mDiskLruCache != null) {
				InputStream inputStream = null;
				try {
					final DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
					if (snapshot != null) {
						if (BuildConfig.DEBUG) Log.d(TAG, "Disk cache hit");
						inputStream = snapshot.getInputStream(DISK_CACHE_INDEX);
						if (inputStream != null) {
							FileDescriptor fd = ((FileInputStream) inputStream).getFD(); // 文件描述器
							// Decode bitmap, but we don't want to sample so
							// give MAX_VALUE as the target dimensions
							// 编码文件全图,不伸缩
							bitmap = ImageWorkerResizer.decodeSampledBitmapFromDescriptor(fd, Integer.MAX_VALUE, Integer.MAX_VALUE, this);
						}
					}
				} catch (final IOException e) {
					Log.e(TAG, "getBitmapFromDiskCache - " + e);
				} finally {
					try {
						if (inputStream != null) inputStream.close();
					} catch (IOException e) {}
				}
			}
			return bitmap;
		}
	}

	// 多线程Iterator集合状态异常,put/next导致
	// 从弱引用集合中获取,加载到inBitmap Params 中
	protected Bitmap getBitmapFromReusableSet(BitmapFactory.Options options) {
		// 从重用计划中加载
		Bitmap bitmap = null;
		if (mReusableBitmaps != null && !mReusableBitmaps.isEmpty()) {
			Bitmap item;
			synchronized (mResuableCacheLock) {
				final Iterator<SoftReference<Bitmap>> iterator = mReusableBitmaps.iterator();
				try {
					while (iterator.hasNext()) {
						item = iterator.next().get();
						if (null != item && item.isMutable()) {
							// Check to see it the item can be used for inBitmap
							if (canUseForInBitmap(item, options)) {
								bitmap = item;
								// Remove from reusable set so it can't be used
								// again
								iterator.remove();
								break;
							}
						} else {
							// Remove from the set if the reference has been
							// cleared.
							iterator.remove();
						}
					}
				} catch (ConcurrentModificationException e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		bitmap = null; // un re-use
		return bitmap;
	}

	// 清空V4队列集合
	public void clearCache() {
		if (mMemoryCache != null) {
			mMemoryCache.evictAll(); // pop所有
			if (BuildConfig.DEBUG) Log.d(TAG, "Memory cache cleared");
		}
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

	// 刷新,提交磁盘缓冲文件
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

	// 关闭磁盘文件缓冲
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

	/********************************************** Static Method **********************************************/
	// 重引用Fragment
	private static RetainFragment findOrCreateRetainFragment(FragmentManager fm) {
		// 传入null 不缓冲ImageCache图片缓冲
		if (fm == null) return null;
		// 创建或重引用Fragment碎片
		RetainFragment mRetainFragment = (RetainFragment) fm.findFragmentByTag(TAG); // FM根据TAG获取碎片
		if (mRetainFragment == null) {
			mRetainFragment = new RetainFragment(); // 新建Fragment
			// 添加,提交[commitAllowingStateLoss:可在Activity状态被销毁提交,commit:Activity状态被销毁时报异常]
			fm.beginTransaction().add(mRetainFragment, TAG).commitAllowingStateLoss();
		}
		return mRetainFragment;
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	private static boolean canUseForInBitmap(Bitmap candidate, BitmapFactory.Options targetOptions) {
		// 长宽是否直接符合缩放比例
		// BEGIN_INCLUDE(can_use_for_inbitmap)
		if (!UtilSDKVersion.hasKitKat()) {
			// On earlier versions, the dimensions must match exactly and the
			// inSampleSize must be 1
			return candidate.getWidth() == targetOptions.outWidth && candidate.getHeight() == targetOptions.outHeight
					&& targetOptions.inSampleSize == 1;
		}
		// From Android 4.4 (KitKat) onward we can re-use if the byte size of
		// the new bitmap is smaller than the reusable bitmap candidate
		// allocation byte count.
		int width = targetOptions.outWidth / targetOptions.inSampleSize;
		int height = targetOptions.outHeight / targetOptions.inSampleSize;
		int byteCount = width * height * getBytesPerPixel(candidate.getConfig());
		return byteCount <= candidate.getAllocationByteCount();
		// END_INCLUDE(can_use_for_inbitmap)
	}

	private static int getBytesPerPixel(Bitmap.Config config) {
		if (config == Bitmap.Config.ARGB_8888) {
			return 4;
		} else if (config == Bitmap.Config.RGB_565) {
			return 2;
		} else if (config == Bitmap.Config.ARGB_4444) {
			return 2;
		} else if (config == Bitmap.Config.ALPHA_8) {
			return 1;
		}
		return 1;
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

	@TargetApi(19)
	public static int getBitmapSize(BitmapDrawable value) {
		Bitmap bitmap = value.getBitmap();
		if (bitmap == null) return 1;
		// 4.4以上
		if (UtilSDKVersion.hasKitKat()) return bitmap.getAllocationByteCount();
		// 3.1 以上直接获取图片的大小
		if (UtilSDKVersion.hasHoneycombMR1()) return bitmap.getByteCount();
		// 其他版本的计算吧 , Pre HC-MR1
		return bitmap.getRowBytes() * bitmap.getHeight();
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

	/********************************************** Inner Class **********************************************/
	public static class ImageCacheParams {
		// 缓冲配置对象
		public File diskCacheDir;
		public int memCacheSize = DEFAULT_MEM_CACHE_SIZE;
		public int diskCacheSize = DEFAULT_DISK_CACHE_SIZE;
		public int compressQuality = DEFAULT_COMPRESS_QUALITY;
		public CompressFormat compressFormat = DEFAULT_COMPRESS_FORMAT;
		//
		public boolean diskCacheEnabled = DEFAULT_DISK_CACHE_ENABLED;
		public boolean memoryCacheEnabled = DEFAULT_MEM_CACHE_ENABLED;
		public boolean initDiskCacheOnCreate = DEFAULT_INIT_DISK_CACHE_ON_CREATE;

		// 无参构造
		public ImageCacheParams() {}

		// context:上下文,diskCacheDirectoryName:文件缓冲文件夹名[路径根据SD卡自动选择(外设缓冲,内部上下文缓冲)]
		public ImageCacheParams(Context context, String diskCacheDirectoryName) {
			if (BuildConfig.DEBUG) Log.i(TAG, "ImageCacheParams Initializing.");
			diskCacheDir = getDiskCacheDir(context, diskCacheDirectoryName);
		}

		public void setMemCacheSizePercent(float percent) {
			// 内存缓冲大小[0.05% - 0.8% 之间]
			if (percent < 0.05f || percent > 0.8f) {
				throw new IllegalArgumentException("setMemCacheSizePercent - percent must be " + "between 0.05 and 0.8 (inclusive)");
			}
			memCacheSize = Math.round(percent * Runtime.getRuntime().maxMemory() / 1024); // 计算大小KB
		}
	}

	public static class RetainFragment extends Fragment {
		// 可重用的Fragment碎片[缓冲配置对象到Fragment中]
		private Object mObject; // ImageCache 缓冲配置

		public RetainFragment() {}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			// Make sure this Fragment is retained over a configuration change
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
