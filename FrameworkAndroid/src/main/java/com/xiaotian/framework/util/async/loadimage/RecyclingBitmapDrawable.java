/*
 * Copyright (C) 2013 The Android Open Source Project
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

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.xiaotian.framework.BuildConfig;

/**
 * 3.0以下使用引用计数 A BitmapDrawable that keeps track of whether it is being
 * displayed or cached. When the drawable is no longer being displayed or
 * cached, {@link Bitmap#recycle() recycle()} will be called on this drawable's
 * bitmap.
 */
public class RecyclingBitmapDrawable extends BitmapDrawable {
	static final String TAG = "RecyclingBitmapDrawable";

	private int mCacheRefCount = 0; // 缓冲计数
	private int mDisplayRefCount = 0; // 显示计数
	private boolean mHasBeenDisplayed; // 已显示[状态]

	public RecyclingBitmapDrawable(Resources res, Bitmap bitmap) {
		super(res, bitmap);
	}

	/**
	 * Notify the drawable that the displayed state has changed. Internally a
	 * count is kept so that the drawable knows when it is no longer being
	 * displayed.
	 * 
	 * @param isDisplayed
	 *            - Whether the drawable is being displayed or not
	 */
	// 设置显示引用计数
	public void setIsDisplayed(boolean isDisplayed) {
		synchronized (this) {
			if (isDisplayed) {
				mDisplayRefCount++;
				mHasBeenDisplayed = true;
			} else {
				mDisplayRefCount--;
			}
		}
		// Check to see if recycle() can be called
		checkState();
	}

	/**
	 * Notify the drawable that the cache state has changed. Internally a count
	 * is kept so that the drawable knows when it is no longer being cached.
	 * 
	 * @param isCached
	 *            - Whether the drawable is being cached or not
	 */
	// 设置缓冲[自增/减:引用计数]
	public void setIsCached(boolean isCached) {
		synchronized (this) {
			if (isCached) {
				mCacheRefCount++;
			} else {
				mCacheRefCount--;
			}
		}

		// Check to see if recycle() can be called
		checkState();
	}

	private synchronized void checkState() {
		// If the drawable cache and display ref counts = 0, and this drawable
		// has been displayed, then recycle
		// 引用计数,显示计数,已经显示过,有效Bitmap[无引用,已显示,图像有效 则进行回收,否则不能回收]
		if (mCacheRefCount <= 0 && mDisplayRefCount <= 0 && mHasBeenDisplayed && hasValidBitmap()) {
			if (BuildConfig.DEBUG) Log.d(TAG, "No longer being used or cached so recycling. " + toString());
			getBitmap().recycle();
		}
	}

	private synchronized boolean hasValidBitmap() {
		// 无效的 Bitmap
		Bitmap bitmap = getBitmap();
		return bitmap != null && !bitmap.isRecycled();
	}

}
