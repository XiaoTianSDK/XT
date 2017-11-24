package com.xiaotian.framework.util.async.loadimage;

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
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Handler;
import android.widget.ImageView;

import com.xiaotian.frameworkxt.util.UtilFile;

/**
 * Image Loader 图片加载器 Using LazyList via
 * https://github.com/thest1/LazyList/tree/master/src/com/fedorvlasov/lazylist
 * for the example since its super lightweight I barely modified this file
 */
public class ImageLoader {
	static final String TAG = "ImageLoader";

	static final int FADE_IN_TIME = 200;
	boolean mFadeInBitmap = true;
	FileCache fileCache;
	Bitmap mLoadingBitmap;
	Handler handler = new Handler();// handler to display images in UI thread
	ExecutorService executorService;
	MemoryCache memoryCache = new MemoryCache();
	Map<ImageView, Object> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, Object>());
	Resources mResources;
	Context mContext;

	public ImageLoader(Context context) {
		this.mContext = context;
		this.mResources = context.getResources();
		fileCache = new FileCache(context);
		memoryCache.setLimit(10 * 1024 * 1024);
		executorService = Executors.newFixedThreadPool(5);
	}

	// final int stub_id= android.R.drawable.alert_dark_frame;
	public void loadImage(Object data, ImageView imageView) {
		imageViews.put(imageView, data);
		Bitmap bitmap = memoryCache.get(String.valueOf(data.hashCode()));
		if (bitmap != null) imageView.setImageBitmap(bitmap);
		else {
			queuePhoto(data, imageView);
			imageView.setImageBitmap(mLoadingBitmap);
		}
	}

	private void queuePhoto(Object data, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(data, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	protected Bitmap processBitmap(Object bitmapResource) {
		File f = fileCache.getFile(String.valueOf(bitmapResource));
		// from SD cache
		Bitmap b = decodeFile(f);
		if (b != null) return b;
		// from web
		try {
			Bitmap bitmap = null;
			URL imageUrl = new URL(String.valueOf(bitmapResource));
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			new UtilFile().copyStream(is, os);
			os.close();
			bitmap = decodeFile(f);
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
			final int REQUIRED_SIZE = 1024;
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

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		Object tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.data)) return true;
		return false;
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

	public void clearMemoryCache() {
		memoryCache.clear();
	}

	public boolean isFadeInBitmap() {
		return mFadeInBitmap;
	}

	public void setFadeInBitmap(boolean mFadeInBitmap) {
		this.mFadeInBitmap = mFadeInBitmap;
	}

	// Task for the queue
	private class PhotoToLoad {
		public Object data;
		public ImageView imageView;

		public PhotoToLoad(Object data, ImageView imageView) {
			this.imageView = imageView;
			this.data = data;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			try {
				if (imageViewReused(photoToLoad)) return;
				Bitmap bmp = processBitmap(photoToLoad.data);
				memoryCache.put(String.valueOf(photoToLoad.data.hashCode()), bmp);
				if (imageViewReused(photoToLoad)) return;
				BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
				handler.post(bd);
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad)) return;
			if (bitmap != null) {
				setImageDrawable(photoToLoad.imageView, new BitmapDrawable(mResources, bitmap));
			} else
				photoToLoad.imageView.setImageDrawable(null);
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	void setImageDrawable(ImageView imageView, Drawable drawable) {
		mFadeInBitmap = true;
		if (mFadeInBitmap) {
			final TransitionDrawable td = new TransitionDrawable(new Drawable[] { new ColorDrawable(mContext.getResources().getColor(android.R.color.transparent)), drawable });
			imageView.setBackground(new BitmapDrawable(mResources, mLoadingBitmap));
			imageView.setImageDrawable(td);
			td.startTransition(FADE_IN_TIME);
		} else {
			imageView.setImageDrawable(drawable);
		}
	}
}
