package com.xiaotian.framework.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @version 1.0.0
 * @author Administrator
 * @name UtilUniversalImageLoader
 * @description Universal Imageloader Util
 * @date 2015-6-16
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
//当使用UniversalImageloader时可选性导出jar库 Framework 
public class UtilUniversalImageLoader {
	Context mContext;

	public void initImageLoader(Context context) {
		mContext = context;
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		// 显示照片选项
		DisplayImageOptions.Builder diBuilder = new DisplayImageOptions.Builder();
		diBuilder.imageScaleType(ImageScaleType.EXACTLY);
		diBuilder.bitmapConfig(Bitmap.Config.RGB_565);
		diBuilder.cacheInMemory(true);
		diBuilder.cacheOnDisk(true);
		// 加载照片选项
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context);
		builder.threadPriority(Thread.NORM_PRIORITY - 2);
		builder.threadPoolSize(10);
		builder.denyCacheImageMultipleSizesInMemory();
		builder.defaultDisplayImageOptions(diBuilder.build());
		builder.tasksProcessingOrder(QueueProcessingType.LIFO);
		builder.diskCacheExtraOptions(dm.widthPixels, dm.heightPixels, null);
		builder.memoryCache(new WeakMemoryCache());
		builder.memoryCacheExtraOptions(dm.widthPixels, dm.heightPixels);
		if (com.xiaotian.frameworkxt.android.common.Mylog.DEBUG) builder.writeDebugLogs();
		// 测试图片加载
		ImageLoader.getInstance().init(builder.build());
	}

	// 普通压缩编码加载显示
	public DisplayImageOptions getDisplayImageOptions() {
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		builder.imageScaleType(ImageScaleType.EXACTLY);
		builder.bitmapConfig(Bitmap.Config.RGB_565);
		builder.cacheInMemory(true);
		builder.cacheOnDisk(true);
		return builder.build();
	}

	// 圆角加载显示
	public DisplayImageOptions getDisplayImageOptionsRound(int round) {
		DisplayImageOptions.Builder diBuilder = new DisplayImageOptions.Builder();
		diBuilder.imageScaleType(ImageScaleType.EXACTLY);
		diBuilder.bitmapConfig(Bitmap.Config.RGB_565);
		diBuilder.cacheInMemory(true);
		diBuilder.cacheOnDisk(true);
		diBuilder.displayer(new RoundedBitmapDisplayer(round));
		return diBuilder.build();
	}

	//	加载渐变显示图片加载侦听器
	public DisplayImageOptions getDisplayImageOptionsFadeIn(int animateTime) {
		DisplayImageOptions.Builder diBuilder = new DisplayImageOptions.Builder();
		diBuilder.imageScaleType(ImageScaleType.EXACTLY);
		diBuilder.bitmapConfig(Bitmap.Config.RGB_565);
		diBuilder.cacheInMemory(true);
		diBuilder.cacheOnDisk(true);
		diBuilder.displayer(new FadeInBitmapDisplayer(animateTime));
		return diBuilder.build();
	}

	// 第一次加载渐变显示图片加载侦听器
	public ImageLoadingListener getImageLoadingListenerAnimateFirstDisplay() {
		return new AnimateFirstDisplayListener();
	}

	static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 800);
					displayedImages.add(imageUri);
				}
			}
		}
	}
}
