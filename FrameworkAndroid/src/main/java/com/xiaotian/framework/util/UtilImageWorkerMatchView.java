package com.xiaotian.framework.util;

import java.io.File;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.xiaotian.framework.common.Mylog;
import com.xiaotian.framework.util.async.loadimage.ImageCache;
import com.xiaotian.framework.util.async.loadimage.ImageCache.ImageCacheParams;
import com.xiaotian.framework.util.async.loadimage.ImageWorkerResizer;
import com.xiaotian.frameworkxt.android.util.UtilSDKVersion;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name UtilImageWorkerMatchView
 * @description ImageWorker
 * @date 2014-9-29
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class UtilImageWorkerMatchView extends ImageWorkerResizer {
	ImageView imageView;

	public UtilImageWorkerMatchView(Context context) {
		super(context);
		setImageFadeIn(true);// 渐变
		ImageCacheParams cacheParams = new ImageCacheParams();
		cacheParams.memoryCacheEnabled = true; // 开启内存缓冲
		cacheParams.diskCacheEnabled = false; // 关闭文件缓冲
		cacheParams.setMemCacheSizePercent(.2f); // 内存缓冲占总内存20%
		addImageCache(null, cacheParams); // 设置缓冲配置::fragmentManager:缓冲图片缓冲配置到FM
	}

	@Override
	public void loadImage(Object imageURI, ImageView imageView) {
		super.loadImage(imageURI, imageView);
		this.imageView = imageView;
	}

	// 处理加载Bitmap的过程[自动加入缓冲,自动多线程加载,参考ImageWorkerFetcher网络URL资源异步加载器]
	@Override
	protected Bitmap processBitmap(Object data) {
		int width = imageView.getMeasuredWidth();
		int height = imageView.getMeasuredHeight();
		if (data instanceof String) {
			String imagePath = String.valueOf(data);
			File file = new File(imagePath);
			if (!file.exists()) {
				Mylog.info("process async load bitmap is not exists.");
				return null;
			}
			// 按需求加载Bitmap,根据ImageView加载
			Bitmap bitmap = decodeSampledBitmapFromFile(imagePath, width, height, getImageCache());
			if (bitmap == null) return null;
			Mylog.info("process async load bitmap container (" + width + "," + height + ") loaded bitmap ("
					+ bitmap.getWidth() + "," + bitmap.getHeight() + ") size (" + getBitmapSize(bitmap) + ")");
			return bitmap;
		} else if (data instanceof Integer) {
			Bitmap bitmap = decodeSampledBitmapFromResource(imageView.getResources(), (Integer) data, width, height,
					getImageCache());
			Mylog.info("process async load bitmap container (" + width + "," + height + ") loaded bitmap ("
					+ bitmap.getWidth() + "," + bitmap.getHeight() + ") size (" + getBitmapSize(bitmap) + ")");
			return bitmap;
		}
		return null;
	}

	public static Bitmap decodeSampledBitmapFromFile(String filename, int reqWidth, int reqHeight, ImageCache cache) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filename, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		if (UtilSDKVersion.hasHoneycomb()) addInBitmapOptions(options, cache); // 3.0
		// 引用 mutable Bitmap
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filename, options);
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res, Integer resourceImage, int reqWidth,
			int reqHeight, ImageCache cache) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resourceImage, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		if (UtilSDKVersion.hasHoneycomb()) addInBitmapOptions(options, cache); // 3.0
		// 引用 mutable Bitmap
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resourceImage, options);
	}

	@TargetApi(12)
	public static String getBitmapSize(Bitmap bitmap) {
		if (UtilSDKVersion.hasHoneycombMR1()) return bitmap.getByteCount() / 1024 + "KB";
		return bitmap.getRowBytes() * bitmap.getHeight() / 1024 + "KB";
	}
}
