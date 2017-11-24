package com.xiaotian.framework.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.xiaotian.framework.R;
import com.xiaotian.framework.common.Mylog;
import com.xiaotian.framework.util.BitmapMemoryCache;
import com.xiaotian.framework.view.listener.ViewScrollViewListener;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name ViewHorizontalScrollViewImage
 * @description 水平滚动图片视图
 * @date 2013-11-9
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */

public class ViewHorizontalScrollViewImage extends ViewHorizontalScrollView implements OnClickListener,
		OnLongClickListener {
	protected ViewScrollViewListener scrollListener;
	protected SelectedChangeListener changeListener;
	protected OnClickListener listener;
	protected BitmapMemoryCache memoryCache;
	protected boolean isItemClickAble;
	protected boolean isScrollable;
	protected int selectedItem;
	protected int imageMarginTop;
	protected int imageMarginLeft;
	protected int imageMarginRight;
	protected int imageMarginBottom;

	private DataSource dataSource;
	protected Context context;
	protected float height; // Image Height
	protected float width; // Image Width
	SparseArray<ImageView> cacheImageView;
	// UI
	protected LinearLayout imageContent;
	protected ImageView preHighlightItem;

	public ViewHorizontalScrollViewImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		this.isScrollable = true;
		this.isItemClickAble = false;
		memoryCache = new BitmapMemoryCache();
		memoryCache.setLimit(5 * 1024 * 1024);
		imageMarginTop = getResources().getDimensionPixelOffset(R.dimen.dimen_1);
		imageMarginLeft = getResources().getDimensionPixelOffset(R.dimen.dimen_1);
		imageMarginRight = getResources().getDimensionPixelOffset(R.dimen.dimen_1);
		imageMarginBottom = getResources().getDimensionPixelOffset(R.dimen.dimen_1);
		// UI
		imageContent = new LinearLayout(context);
		imageContent.setOrientation(LinearLayout.HORIZONTAL);
		android.view.ViewGroup.LayoutParams params = new android.view.ViewGroup.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		imageContent.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		addView(imageContent, params);
		//
	}

	@SuppressLint("NewApi")
	public void notifyDatasetChange() {
		// Update Image
		imageContent.removeAllViews();
		List<String> images = dataSource.getFileName();
		if (images == null) return;
		LinearLayout.LayoutParams params = null;
		for (int position = 0; position < images.size(); position++) {
			String imagename = images.get(position);
			final ImageView image = new ImageView(context);
			image.setTag(R.id.id_position, position);
			image.setScaleType(ScaleType.CENTER_CROP);
			if (width > 0f) {
				params = new LinearLayout.LayoutParams((int) width, (int) height);
			} else {
				// 自动匹配高[屏幕比例]
				int screamHeight = getResources().getDisplayMetrics().heightPixels;
				int screamWidth = getResources().getDisplayMetrics().widthPixels;
				width = (screamWidth / (float) screamHeight) * height;
				params = new LinearLayout.LayoutParams((int) width, (int) height);
			}
			// Height Light Image
			if (isItemClickAble) {
				image.setClickable(true);
				image.setOnClickListener(this);
				image.setOnLongClickListener(this);
				image.setTag(R.id.id_0, imagename);
				if (position == selectedItem) {
					preHighlightItem = image;
					int paramWidth = params.width;
					int paramHeight = params.height;
					params = new LinearLayout.LayoutParams(paramWidth + imageMarginLeft + imageMarginRight, paramHeight
							+ imageMarginTop + imageMarginBottom);
					params.setMargins(imageMarginLeft, imageMarginLeft, imageMarginLeft, imageMarginLeft);
					if (changeListener != null) changeListener.onSelectedChange(this, preHighlightItem, position);
				} else {
					params.setMargins(imageMarginLeft, imageMarginTop, imageMarginRight, imageMarginBottom);
				}
			} else {
				params.setMargins(imageMarginLeft, imageMarginTop, imageMarginRight, imageMarginBottom);
			}
			params.gravity = Gravity.CENTER_VERTICAL;
			imageContent.addView(image, params);
			Bitmap bitmap = memoryCache.get(imagename);
			if (bitmap != null) {
				image.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
			} else {
				image.setBackgroundResource(R.color.color_text_black_xiaotian);
				// 异步加载[多线程]
				AsyncTask<String, Integer, Bitmap> task = new AsyncTask<String, Integer, Bitmap>() {
					@Override
					protected Bitmap doInBackground(String... params) {
						File file = new File(params[0]);
						if (!file.exists()) return null;
						// 按比例缩放
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = true;
						FileInputStream inputStream = null;
						try {
							inputStream = new FileInputStream(file);
							BitmapFactory.decodeFileDescriptor(inputStream.getFD(), null, options);
						} catch (OutOfMemoryError e) {
							Runtime.getRuntime().gc();
							System.gc();
						} catch (FileNotFoundException e) {} catch (IOException ie) {} finally {
							if (inputStream != null) try {
								inputStream.close();
							} catch (IOException e) {}
						}
						// 计算长宽比例,设置缩放
						Mylog.info("image: w=" + options.outWidth + ",h=" + options.outHeight);
						int reqHeight = (int) height;
						int reqWidth = (int) (width > 0f ? width : height);
						int inSampleSize = 1;
						if (options.outHeight > reqHeight || options.outWidth > reqHeight) {
							final int halfHeight = options.outHeight / 2;
							final int halfWidth = options.outWidth / 2;
							inSampleSize *= 2;
							while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
								inSampleSize *= 2;
							}
						}
						// 2的倍数
						options.inDither = false;
						options.inScaled = false;
						options.inPurgeable = true;
						options.inInputShareable = true;
						options.inJustDecodeBounds = false;
						options.inSampleSize = inSampleSize;
						options.inTempStorage = new byte[23 * 1024];
						Bitmap b = null;
						try {
							inputStream = new FileInputStream(file);
							b = BitmapFactory.decodeFileDescriptor(inputStream.getFD(), null, options);
						} catch (OutOfMemoryError e) {
							Runtime.getRuntime().gc();
							System.gc();
						} catch (FileNotFoundException e) {} catch (IOException ie) {} finally {
							if (inputStream != null) try {
								inputStream.close();
							} catch (IOException e) {}
						}
						if (b == null) {
							Mylog.info("load Image not fountd");
							return null;
						} else {
							memoryCache.put(params[0], b);
						}
						Mylog.info("container height:" + height + " scate size:" + inSampleSize + " (" + b.getWidth()
								+ "," + b.getHeight() + ")size=" + (b.getRowBytes() * b.getHeight() / 1024)
								+ "Kb name=" + params[0]);
						return b;
					}

					@Override
					protected void onPostExecute(Bitmap result) {
						if (result != null) {
							final TransitionDrawable td = new TransitionDrawable(new Drawable[] {
									new ColorDrawable(android.R.color.transparent),
									new BitmapDrawable(getResources(), result) });
							image.setImageDrawable(td);
							td.startTransition(200);
						}
					}

				};
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imagename);
				} else {
					task.execute(imagename);
				}
			}
		}
	}

	public void setDataSource(DataSource datasource) {
		this.dataSource = datasource;
		post(new Runnable() {
			// TODO 加入到View的消息队列,运行
			@Override
			public void run() {
				// UI Thread
				if (height < 1) height = imageContent.getMeasuredHeight();
				notifyDatasetChange();
			}
		});
	}

	public void setDataSource(DataSource datasource, boolean checked) {
		this.dataSource = datasource;
		post(new Runnable() { // TODO 加入到View的消息队列,运行
			@Override
			public void run() { // UI Thread if (height < 1) height =
				imageContent.getMeasuredHeight();
				notifyDatasetChange();
			}
		});

	}

	public DataSource getDataSource() {
		return dataSource;
	}

	@Override
	public boolean onLongClick(View v) {
		if (listener != null) return listener.onLongClick(this, v);
		return true;
	}

	@Override
	public void onClick(View v) {
		if (listener != null) listener.onClick(this, v);

	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (scrollListener != null) scrollListener.onTouchEvent(ev);
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// if we can scroll pass the event to the superclass
			if (scrollListener != null) scrollListener.onTouchDown(ev);
			if (isScrollable) {
				return super.onTouchEvent(ev);
			} else {
				// only continue to handle the touch event if scrolling
				// enabled
				return isScrollable; // mScrollable is always false at this
										// point
			}
			// 放开手时回调
		case MotionEvent.ACTION_UP:
			if (scrollListener != null) scrollListener.onScrollFinish();
		default:
			return super.onTouchEvent(ev);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// Don't do anything with intercepted touch events if
		// we are not scrollable
		if (!isScrollable) {
			return false;
		} else {
			return super.onInterceptTouchEvent(ev);
		}
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		if (scrollListener != null) scrollListener.onScrollChanged(l, t, oldl, oldt);
	}

	public void setCurrentHighlightItem(int position) {
		if (position < 0 || position == selectedItem || position >= imageContent.getChildCount()) return;
		selectedItem = position;
		if (preHighlightItem != null) {
			preHighlightItem.invalidate();
		}
		preHighlightItem = (ImageView) imageContent.getChildAt(position);
		preHighlightItem.invalidate();
		if (changeListener != null) {
			changeListener.onSelectedChange(this, preHighlightItem, selectedItem);
		}
	}

	public boolean isScrollable() {
		return isScrollable;
	}

	public void setScrollable(boolean isScrollable) {
		this.isScrollable = isScrollable;
	}

	public boolean isItemClickAble() {
		return isItemClickAble;
	}

	public void setItemClickAble(boolean clickable) {
		this.isItemClickAble = clickable;
	}

	public void setItemOnClickListener(OnClickListener listener) {
		if (listener == null) {
			this.isItemClickAble = false;
		} else {
			this.isItemClickAble = true;
			this.listener = listener;
		}
	}

	public void setContentHeight(int height) {
		this.height = height;
	}

	public void setContentWidth(int width) {
		this.width = width;
	}

	public int getSelectedItem() {
		return selectedItem;
	}

	public ImageView getCacheView(int position) {
		return cacheImageView.get(position);
	}

	/**
	 * the index selected item
	 */
	public void setSelectedItem(int selectedItem) {
		this.selectedItem = selectedItem;
	}

	public void setChangeListener(SelectedChangeListener changeListener) {
		this.changeListener = changeListener;
	}

	public void setImageMargin(int left, int top, int right, int bottom) {
		this.imageMarginTop = top;
		this.imageMarginLeft = left;
		this.imageMarginRight = right;
		this.imageMarginBottom = bottom;
	}

	// inner class
	public interface DataSource {
		List<String> getFileName();
	}

	public interface OnClickListener {
		public void onClick(ViewHorizontalScrollViewImage parent, View view);

		public boolean onLongClick(ViewHorizontalScrollViewImage parent, View view);
	}

	public interface SelectedChangeListener {
		public void onSelectedChange(ViewHorizontalScrollViewImage parent, View current, int position);
	}
}
