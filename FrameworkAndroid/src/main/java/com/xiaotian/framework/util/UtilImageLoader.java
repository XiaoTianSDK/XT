package com.xiaotian.framework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.widget.ImageView;

import com.xiaotian.framework.common.Mylog;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name UtilImageLoader
 * @description ImageView 的图片加载器,必须有主线程创建本实体,Handle加载到创建的线程中
 * @date 2014-1-24
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class UtilImageLoader {
	// filePath(hash) - url 缓存
	protected FileCache fileCache;// 已harsh码保存临时文件
	// Bitmap - filePath 缓存
	protected MemoryCache memoryCache = new MemoryCache(); // 内存缓存
	// ImageView - filePath 缓存
	protected Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	protected ExecutorService executorService; // 线程池
	protected Handler handler = new Handler();// handler to display images in UI
	protected Context context; // thread

	public UtilImageLoader(Context context) {
		this.context = context;
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5); // 固定线程池
	}

	public void asyncLoadFileImage(final String filePath, final ImageView imageView, final Drawable defaultDrawable) {
		// 1.内存缓存
		// 2.本地文件
		imageViews.put(imageView, filePath);
		Bitmap bitmap = memoryCache.get(filePath);
		if (bitmap != null) imageView.setImageBitmap(bitmap);
		else {
			PhotoToLoad p = new PhotoToLoad(filePath, imageView);
			executorService.submit(new PhotosLocalLoader(p));
			imageView.setImageDrawable(defaultDrawable);
		}
	}

	public void asyncLoadWebImage(final String filepath, final ImageView imageView, final Drawable defaultDrawable) {
		// 1.内存缓存
		// 2.本地文件缓存
		// 3.联网下载
		imageViews.put(imageView, filepath); // 加入缓冲集
		Bitmap bitmap = memoryCache.get(filepath); // 在内存缓存中加载
		if (bitmap != null) imageView.setImageBitmap(bitmap);
		else {
			queuePhoto(filepath, imageView);
			imageView.setImageDrawable(defaultDrawable);
		}
	}

	private void queuePhoto(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url) {
		File f = fileCache.getFile(url); // 已经下载
		// from SD cache
		Bitmap b = decodeFile(f);
		if (b != null) return b;
		// from web
		try {
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			Utils.copyStream(is, os);
			os.close();
			bitmap = decodeFile(f); // 下载完成
			return bitmap;
		} catch (Throwable ex) {
			ex.printStackTrace();
			if (ex instanceof OutOfMemoryError) memoryCache.clear();
			return null;
		}
	}

	// decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			FileInputStream stream1 = new FileInputStream(f);
			BitmapFactory.decodeStream(stream1, null, o);
			stream1.close();

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 70;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			if (scale >= 2) {
				scale /= 2;
			}
			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			FileInputStream stream2 = new FileInputStream(f);
			Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
			stream2.close();
			return bitmap;
		} catch (FileNotFoundException e) {} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// inner class
	class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		// 联网缓存图片加载
		protected PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			try {
				if (imageViewReused(photoToLoad)) return;
				Bitmap bmp = getBitmap(photoToLoad.url);
				memoryCache.put(photoToLoad.url, bmp);
				if (imageViewReused(photoToLoad)) return;
				BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
				handler.post(bd);
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}
	}

	class PhotosLocalLoader extends PhotosLoader {

		PhotosLocalLoader(PhotoToLoad photoToLoad) {
			super(photoToLoad);
		}

		@Override
		public void run() {
			try {
				// Crop
				if (photoToLoad.url == null) return;
				File file = new File(photoToLoad.url);
				if (!file.exists()) return;
				Mylog.info("load image=" + photoToLoad.url);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(photoToLoad.url, options);
				int reqWidth = photoToLoad.imageView.getMeasuredWidth();
				int reqHeight = photoToLoad.imageView.getMeasuredHeight();
				int inSampleSize = 1;
				if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
					final int halfHeight = options.outHeight / 2;
					final int halfWidth = options.outWidth / 2;
					while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
						inSampleSize *= 2;
					}
				}
				options.inSampleSize = inSampleSize;
				options.inJustDecodeBounds = false;
				final Bitmap bmp = BitmapFactory.decodeFile(photoToLoad.url, options);
				memoryCache.put(photoToLoad.url, bmp);
				handler.post(new Runnable() {
					@Override
					public void run() {
						photoToLoad.imageView.setImageBitmap(bmp);
					}
				});
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url)) return true;
		return false;
	}

	// Used to display bitmap in the UI thread
	public class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad)) return;
			if (bitmap != null) photoToLoad.imageView.setImageBitmap(bitmap);
			else
				photoToLoad.imageView.setImageDrawable(null);
		}
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

	public class MemoryCache {
		// 本地文件内存缓存[用顺序Map移除首先添加的Bitmap]
		// Last argument true for LRU ordering
		private Map<String, Bitmap> cache = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 1.5f,
				true));
		private long size = 0;// current allocated size
		private long limit = 1000000;// max memory in bytes

		public MemoryCache() {
			// use 25% of available heap size
			setLimit(Runtime.getRuntime().maxMemory() / 4);
		}

		public void setLimit(long new_limit) {
			limit = new_limit;
			Mylog.info("MemoryCache will use up to " + limit / 1024. / 1024. + "MB");
		}

		public Bitmap get(String id) {
			try {
				if (!cache.containsKey(id)) return null;
				// NullPointerException sometimes happen here
				// http://code.google.com/p/osmdroid/issues/detail?id=78
				return cache.get(id);
			} catch (NullPointerException ex) {
				ex.printStackTrace();
				return null;
			}
		}

		public void put(String id, Bitmap bitmap) {
			try {
				if (cache.containsKey(id)) size -= getSizeInBytes(cache.get(id));
				cache.put(id, bitmap);
				size += getSizeInBytes(bitmap);
				checkSize();
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}

		private void checkSize() {
			Mylog.info("cache size=" + size + " length=" + cache.size());
			if (size > limit) {
				// least recently accessed item will be the first one iterated
				Iterator<Entry<String, Bitmap>> iter = cache.entrySet().iterator();
				while (iter.hasNext()) {
					Entry<String, Bitmap> entry = iter.next();
					size -= getSizeInBytes(entry.getValue());
					iter.remove();
					if (size <= limit) break;
				}
				Mylog.info("Clean cache. New size " + cache.size());
			}
		}

		public void clear() {
			try {
				// NullPointerException sometimes happen here
				// http://code.google.com/p/osmdroid/issues/detail?id=78
				cache.clear();
				size = 0;
			} catch (NullPointerException ex) {
				ex.printStackTrace();
			}
		}

		long getSizeInBytes(Bitmap bitmap) {
			if (bitmap == null) return 0;
			return bitmap.getRowBytes() * bitmap.getHeight();
		}
	}

	public class FileCache {
		// 网络文件缓存
		private File cacheDir;

		public FileCache(Context context) {
			// Find the dir to save cached images
			if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) cacheDir = new File(
					android.os.Environment.getExternalStorageDirectory(), "LazyList");
			else
				cacheDir = context.getCacheDir();
			if (!cacheDir.exists()) cacheDir.mkdirs();
		}

		public File getFile(String url) {
			// I identify images by hashcode. Not a perfect solution, good for
			// the demo.
			String filename = String.valueOf(url.hashCode());
			// Another possible solution (thanks to grantland)
			// String filename = URLEncoder.encode(url);
			File f = new File(cacheDir, filename);
			return f;

		}

		public void clear() {
			File[] files = cacheDir.listFiles();
			if (files == null) return;
			for (File f : files)
				f.delete();
		}
	}
}
