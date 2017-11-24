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

import java.io.FileDescriptor;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import com.xiaotian.framework.BuildConfig;
import com.xiaotian.frameworkxt.android.util.UtilSDKVersion;

/**
 * 加载图片,继承ImageWorker,加入图片编码的静态方法::根据设置图片大小加载图片 A simple subclass of
 * {@link ImageWorker} that resizes images from resources given a target width
 * and height. Useful for when the input images might be too large to simply
 * load directly into memory.
 */
public class ImageWorkerResizer extends ImageWorker {
	private static final String TAG = "ImageWorkerResizer";
	private Resources mResources;
	private int mImageHeight;
	private int mImageWidth;

	// 构造器[固定大小加载]
	public ImageWorkerResizer(Context context, int imageWidth, int imageHeight) {
		super(context);
		mResources = context.getResources();
		setImageSize(imageWidth, imageHeight);
	}

	public ImageWorkerResizer(Context context, int imageSize) {
		super(context);
		mResources = context.getResources();
		setImageSize(imageSize);
	}

	public ImageWorkerResizer(Context context) {
		super(context);
		mResources = context.getResources();
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

	// 处理资源ID图片
	private Bitmap processBitmap(int resId) {
		if (BuildConfig.DEBUG) Log.d(TAG, "processBitmap - " + resId);
		return decodeSampledBitmapFromResource(mResources, resId, mImageWidth, mImageHeight, getImageCache());
	}

	// 处理资源路径图片
	@Override
	protected Bitmap processBitmap(Object data) {
		return processBitmap(Integer.parseInt(String.valueOf(data)));
	}

	// 从资源ID编码图片Bitmap
	// res:资源上下文,resId:资源Id,reqWidth:请求图片宽,reqHeight:请求图片高,cache:缓冲器
	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight,
			ImageCache cache) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		// If we're running on Honeycomb or newer, try to use inBitmap option
		if (UtilSDKVersion.hasHoneycomb()) addInBitmapOptions(options, cache);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	// 从文件Path编码图片Bitmap
	// filename:文件Path 路径名,reqWidth:请求图片宽,reqHeight:请求图片高,cache:缓冲器
	public static Bitmap decodeSampledBitmapFromFile(String filename, int reqWidth, int reqHeight, ImageCache cache) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filename, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		// If we're running on Honeycomb or newer, try to use inBitmap option
		if (UtilSDKVersion.hasHoneycomb()) addInBitmapOptions(options, cache);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filename, options);
	}

	// 从文件的描述器编码图片Bitmap,fileDescriptor:文件描述,reqWidth:请求图片宽,reqHeight:请求图片高,cache:缓冲器
	public static Bitmap decodeSampledBitmapFromDescriptor(FileDescriptor fileDescriptor, int reqWidth, int reqHeight,
			ImageCache cache) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 编码边界
		BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		options.inJustDecodeBounds = false; // 编码内容
		// If we're running on Honeycomb or newer, try to use inBitmap
		if (UtilSDKVersion.hasHoneycomb()) addInBitmapOptions(options, cache);// 3.0以上(缓冲到缓冲器)
		return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
	}

	// 3.0 以上SDK
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void addInBitmapOptions(BitmapFactory.Options options, ImageCache cache) {
		// inBitmap only works with mutable bitmaps so force the decoder to
		// return mutable bitmaps.
		options.inMutable = true;
		if (cache != null) {
			// Try and find a bitmap to use for inBitmap
			Bitmap inBitmap = cache.getBitmapFromReusableSet(options);
			if (inBitmap != null) {
				if (BuildConfig.DEBUG) Log.d(TAG, "Found bitmap to use for inBitmap");
				options.inBitmap = inBitmap;
			}
		}
	}

	// 计算图片缩小比例大小
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
			final float totalPixels = width * height;
			final float totalReqPixelsCap = reqWidth * reqHeight * 2;
			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
				inSampleSize++;
			}
		}
		return inSampleSize;
	}
}
