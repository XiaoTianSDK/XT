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

import java.lang.ref.WeakReference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import com.xiaotian.framework.BuildConfig;
import com.xiaotian.framework.common.Mylog;
import com.xiaotian.frameworkxt.android.util.UtilSDKVersion;

/**
 * Image Worker 图片加载器 This class wraps up completing some arbitrary long running
 * work when loading a bitmap to an ImageView. It handles things like using a
 * memory and disk cache, running the work in a background thread and setting a
 * placeholder image.
 */
public abstract class ImageWorker {
	private static final String TAG = "ImageWorker";
	private final Object mPauseWorkLock = new Object();
	private static final int FADE_IN_TIME = 200;
	// Disk Async Message
	private static final int MESSAGE_CLEAR = 0;
	private static final int MESSAGE_FLUSH = 1;
	private static final int MESSAGE_CLOSE = 2;
	private static final int MESSAGE_INIT_DISK_CACHE = 3;
	//
	private ImageCache mImageCache;
	private ImageCache.ImageCacheParams mImageCacheParams;// 图片缓冲配置
	//
	private Bitmap mLoadingBitmap;
	private boolean mPauseWork = false; // 暂停标识
	private boolean mFadeInBitmap = true;
	private DisplayMetrics displayMetrics;
	private boolean mExitTasksEarly = false; // 退出任务标识,不编码图片[已经进入run,不执行decode]
	//
	private Context context;
	private Resources mResources;

	// 构造器
	protected ImageWorker(Context context) {
		this.context = context;
		mResources = context.getResources();
	}

	/********************************** Abstract Method ********************************/
	protected abstract Bitmap processBitmap(Object data);

	// 异步加载图片到ImageView
	public void loadImage(Object data, ImageView imageView) {
		if (data == null) return;
		BitmapDrawable value = null;
		if (mImageCache != null) value = mImageCache.getBitmapFromMemCache(String.valueOf(data));
		if (value != null) {
			// Bitmap found in memory cache
			Mylog.info("find image drawable from cache colection.");
			imageView.setImageDrawable(value);
		} else if (cancelPotentialWork(data, imageView)) {
			// Un found Bitmap in memory cache
			Mylog.info("un find image drawable .");
			final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
			final AsyncDrawable asyncDrawable = new AsyncDrawable(mResources, mLoadingBitmap, task);
			imageView.setImageDrawable(asyncDrawable);
			task.executeOnExecutor(AsyncTask.DUAL_THREAD_EXECUTOR, data);
		}
	}

	// 设置图片缓冲配置::FragmentManager:缓冲图片配置到FragmentManager中
	public void addImageCache(FragmentManager fragmentManager, ImageCache.ImageCacheParams cacheParams) {
		mImageCacheParams = cacheParams;
		// 获取缓冲在Fragment中的图片缓冲实体
		mImageCache = ImageCache.getInstance(fragmentManager, mImageCacheParams);
		// Init Disk
		new CacheAsyncTask().execute(MESSAGE_INIT_DISK_CACHE);
	}

	// ::缓冲图片配置到FragmentActivity的FragmentManager中
	public void addImageCache(FragmentActivity activity, String diskCacheDirectoryName) {
		mImageCacheParams = new ImageCache.ImageCacheParams(activity, diskCacheDirectoryName);
		// 获取缓冲在Fragment中的图片缓冲实体
		mImageCache = ImageCache.getInstance(activity.getSupportFragmentManager(), mImageCacheParams);
		// Init Disk
		new CacheAsyncTask().execute(MESSAGE_INIT_DISK_CACHE);
	}

	// 设置ImageView 图片内容
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void setImageDrawable(ImageView imageView, Drawable drawable) {
		mFadeInBitmap = true;
		if (mFadeInBitmap) {
			// 渐变显示照片
			final TransitionDrawable td = new TransitionDrawable(new Drawable[] {
					new ColorDrawable(context.getResources().getColor(android.R.color.transparent)), drawable });
			// 设置默认TransitionDrawable背景图片,Transition时加载
			if (UtilSDKVersion.hasJellyBean()) {
				imageView.setBackground(new BitmapDrawable(mResources, mLoadingBitmap));
			} else {
				imageView.setBackgroundDrawable(new BitmapDrawable(mResources, mLoadingBitmap));
			}
			imageView.setImageDrawable(td);
			td.startTransition(FADE_IN_TIME);
		} else {
			// 直接显示,不渐变效果
			imageView.setImageDrawable(drawable);
		}
	}

	// 屏幕信息
	public DisplayMetrics getDisplayMetrics() {
		if (displayMetrics != null) return displayMetrics;
		return displayMetrics = context.getResources().getDisplayMetrics();
	}

	// 设置图片渐变显示
	public void setImageFadeIn(boolean fadeIn) {
		mFadeInBitmap = fadeIn;
	}

	// 设置线程可以预退出[取消不加载内容]
	public void setExitTasksEarly(boolean exitTasksEarly) {
		mExitTasksEarly = exitTasksEarly;
		setPauseWork(false);
	}

	protected ImageCache getImageCache() {
		return mImageCache;
	}

	public void setPauseWork(boolean pauseWork) {
		synchronized (mPauseWorkLock) {
			mPauseWork = pauseWork;
			if (!mPauseWork) mPauseWorkLock.notifyAll();
		}
	}

	public void setLoadingImage(Bitmap bitmap) {
		mLoadingBitmap = bitmap;
	}

	public void setLoadingImage(int resId) {
		mLoadingBitmap = BitmapFactory.decodeResource(mResources, resId);
	}

	/******************************************************** Static Class Method ********************************************************/
	// 取消图片加载任务
	public static void cancelWork(ImageView imageView) {
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
		if (bitmapWorkerTask != null) {
			bitmapWorkerTask.cancel(true);
			if (BuildConfig.DEBUG) {
				final Object bitmapData = bitmapWorkerTask.data;
				Log.d(TAG, "cancelWork - cancelled work for " + bitmapData);
			}
		}
	}

	/**
	 * 取消可能存在的正在执行的操作线程 Returns true if the current work has been canceled or if
	 * there was no work in progress on this image view. Returns false if the
	 * work in progress deals with the same data. The work is not stopped in
	 * that case.
	 */
	public static boolean cancelPotentialWork(Object data, ImageView imageView) {
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView); // 获取加载Bitmap的线程

		if (bitmapWorkerTask != null) {
			final Object bitmapData = bitmapWorkerTask.data;
			if (bitmapData == null || !bitmapData.equals(data)) {
				bitmapWorkerTask.cancel(true);
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "cancelPotentialWork - cancelled work for " + data);
				}
			} else {
				// The same work is already in progress.
				return false;
			}
		}
		return true;
	}

	// 根据ImageView 获取加载图片线程
	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}

	/******************************************************** Disk Cache Method ********************************************************/
	protected void initDiskCacheInternal() {
		if (mImageCache != null) mImageCache.initDiskCache();
	}

	// 清缓冲[内存+磁盘]
	protected void clearCacheInternal() {
		if (mImageCache != null) mImageCache.clearCache();
	}

	// 刷新缓冲
	protected void flushCacheInternal() {
		if (mImageCache != null) mImageCache.flush();
	}

	// 关闭缓冲
	protected void closeCacheInternal() {
		if (mImageCache != null) mImageCache.close();
		mImageCache = null;

	}

	// 异步清缓冲
	public void clearCache() {
		new CacheAsyncTask().execute(MESSAGE_CLEAR);
	}

	// 异步刷新缓冲
	public void flushCache() {
		new CacheAsyncTask().execute(MESSAGE_FLUSH);
	}

	// 异步关闭缓冲
	public void closeCache() {
		new CacheAsyncTask().execute(MESSAGE_CLOSE);
	}

	/******************************************************** Inner Class ********************************************************/
	// 可异步 Drawable 图片绘画[传入异步任务Task,用于在线程池中获取并执行]
	private static class AsyncDrawable extends BitmapDrawable {
		private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

		public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
			super(res, bitmap);
			bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
		}

		public BitmapWorkerTask getBitmapWorkerTask() {
			return bitmapWorkerTaskReference.get();
		}
	}

	// 异步编码图片任务::继承 AsyncTask
	private class BitmapWorkerTask extends AsyncTask<Object, Void, BitmapDrawable> {
		private Object data;
		private final WeakReference<ImageView> imageViewReference;

		public BitmapWorkerTask(ImageView imageView) {
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		@Override
		protected BitmapDrawable doInBackground(Object... params) {
			if (BuildConfig.DEBUG) Log.d(TAG, "doInBackground - starting work");
			data = params[0];
			Bitmap bitmap = null;
			BitmapDrawable drawable = null;
			// 图片加载
			synchronized (mPauseWorkLock) {
				while (mPauseWork && !isCancelled()) {
					try {
						mPauseWorkLock.wait();
					} catch (InterruptedException e) {}
				}
			}
			// 从内存中加载图片[内存缓冲不为null,线程没被取消,线程加载的图片有效性,是否提前退出View]
			if (mImageCache != null && !isCancelled() && getAttachedImageView() != null && !mExitTasksEarly) {
				bitmap = mImageCache.getBitmapFromDiskCache(String.valueOf(data)); // 缓冲Bitmap
			}
			// 调用processBitmap 获取新图片
			if (bitmap == null && !isCancelled() && getAttachedImageView() != null && !mExitTasksEarly) {
				bitmap = processBitmap(params[0]);
			}
			if (bitmap != null) {
				// SDK 3.0
				if (UtilSDKVersion.hasHoneycomb()) {
					drawable = new BitmapDrawable(mResources, bitmap);
				} else {
					drawable = new RecyclingBitmapDrawable(mResources, bitmap);
				}
				// 加入引用
				if (mImageCache != null) mImageCache.addBitmapToCache(String.valueOf(data), drawable);
			}
			if (BuildConfig.DEBUG) Log.d(TAG, "doInBackground - finished work");
			return drawable;
		}

		@Override
		protected void onPostExecute(BitmapDrawable value) {
			if (isCancelled() || mExitTasksEarly) value = null;
			final ImageView imageView = getAttachedImageView();
			if (value != null && imageView != null) {
				if (BuildConfig.DEBUG) Log.d(TAG, "onPostExecute - setting bitmap");
				setImageDrawable(imageView, value);
			}
		}

		@Override
		protected void onCancelled(BitmapDrawable value) {
			super.onCancelled(value);
			synchronized (mPauseWorkLock) {
				mPauseWorkLock.notifyAll();
			}
		}

		private ImageView getAttachedImageView() {
			// 获取当前线程加载的目的ImageView是不是当前ImageView绑定的图片加载线程[多个任务同时触发,以前的任务取消]
			final ImageView imageView = imageViewReference.get(); // 取得当前引用ImageView
			final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView); // 取得加载线程
			if (this == bitmapWorkerTask) return imageView; // 当前线程加载线程恒等于ImageView中绑定的线程[线程没被其他线程覆盖]
			return null; // 当前线程加载的不是ImageView绑定的线程[当前加载任务已过期]
		}
	}

	// 缓冲线程任务::继承自定义AsyncTask线程操作
	protected class CacheAsyncTask extends AsyncTask<Object, Void, Void> {
		@Override
		protected Void doInBackground(Object... params) {
			switch ((Integer) params[0]) {
			case MESSAGE_CLEAR:
				clearCacheInternal();
				break;
			case MESSAGE_INIT_DISK_CACHE:
				initDiskCacheInternal();
				break;
			case MESSAGE_FLUSH:
				flushCacheInternal();
				break;
			case MESSAGE_CLOSE:
				closeCacheInternal();
				break;
			}
			return null;
		}
	}
}
