package com.xiaotian.framework.activity;

import java.util.List;

import uk.co.senab.photoview.PhotoView;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

import com.xiaotian.framework.R;
import com.xiaotian.framework.control.DialogCustom;
import com.xiaotian.framework.util.MyUtilImageWorker;
import com.xiaotian.framework.util.async.loadimage.ImageCache.ImageCacheParams;
import com.xiaotian.framework.view.ViewPagerTouchable;
import com.xiaotian.frameworkxt.util.UtilFile;

/**
 * @version 1.0.0
 * @author Administrator
 * @name ActivityGestureViewPage
 * @description 全屏查看图片
 * @date 2015-4-3
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class ActivityGestureViewPage extends BaseFragmentActivity {
	// 代码设置动画
	// dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation
	public static final String EXTRA_PARAM_IMAGES = "com.xiaotian.framework.activity.images";
	public static final String EXTRA_PARAM_IMAGE_SELCTED_POSITION = "com.xiaotian.framework.activity.position";
	//
	String showId;
	UtilFile utilFile;
	List<String> listImage;
	List<String> listImageIds;
	ProgressDialog dialogLoading;
	protected DialogCustom dialog;
	ImagePagerAdapter imageAdapter;
	MyUtilImageWorker imageWorker;
	// UI
	ViewPagerTouchable viewPager;

	public ActivityGestureViewPage() {}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN);
		//
		listImage = getIntent().getStringArrayListExtra(EXTRA_PARAM_IMAGES);
		//
		imageWorker = ImageWorkerFragment.getInstance(this, getSupportFragmentManager()).getImageWorker();
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		imageWorker.setImageSize(metrics.widthPixels, metrics.heightPixels);
		imageAdapter = new ImagePagerAdapter(getSupportFragmentManager());
		//
		setContentView(R.layout.activity_gesture_viewpage);
		viewPager = (ViewPagerTouchable) findViewById(R.id.ViewPagerTouchable);
		//
		viewPager.setOffscreenPageLimit(2); // 缓冲页面
		viewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.dimen_1));
		viewPager.setAdapter(imageAdapter);
		int selected = getIntent().getIntExtra(EXTRA_PARAM_IMAGE_SELCTED_POSITION, -1);
		if (selected > -1) viewPager.setCurrentItem(selected);
		initializingData();
	}

	protected void initializingData() {}

	@Override
	protected void onPause() {
		super.onPause();
		imageWorker.flushCache();
		imageWorker.closeCache();
	}

	public MyUtilImageWorker getImageWorker() {
		return imageWorker;
	}

	// 1.FragmentStatePagerAdapter会调用onDestroy销毁Fragment,适用于Page多的情况
	// 2.FragmentPagerAdapter不会调用onDestroy销毁Fragment,而是有FragmentManager管理,适用于Page少的情况
	class ImagePagerAdapter extends FragmentStatePagerAdapter {

		public ImagePagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public int getCount() {
			return listImage.size();
		}

		@Override
		public Fragment getItem(int position) {
			// 新建Fragment,不缓冲UI
			return ImageDetailFragment.newInstance(listImage, position);
		}

		@Override
		public int getItemPosition(Object object) {
			// notify item view
			return POSITION_NONE;
		}
	}

	public static class ImageDetailFragment extends Fragment {
		private static final String EXTRA_IMAGE_POSITION = "position";
		private static final String EXTRA_IMAGE_NAME = "name";
		private ImageView imageView;
		private String imagePath;
		private String imageId;
		private int position;

		public static ImageDetailFragment newInstance(List<String> images, int imagePosition) {
			final ImageDetailFragment f = new ImageDetailFragment();
			final String image = images.get(imagePosition);
			final Bundle args = new Bundle();
			args.putString(EXTRA_IMAGE_NAME, image);
			args.putInt(EXTRA_IMAGE_POSITION, imagePosition);
			f.setArguments(args); // 初始化传入文件Path Name
			return f;
		}

		public ImageDetailFragment() {}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			// 取出文件Path Name
			imagePath = getArguments() != null ? getArguments().getString(EXTRA_IMAGE_NAME) : null;
			position = getArguments() != null ? getArguments().getInt(EXTRA_IMAGE_POSITION) : 0;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// Inflate and locate the main ImageView
			View root = inflater.inflate(R.layout.page_gesture_image_view, container, false);
			imageView = (ImageView) root.findViewById(R.id.id_1);
			root.setTag(R.id.id_0, imageId);
			root.setTag(R.id.id_position, position);
			return root;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			if (ActivityGestureViewPage.class.isInstance(getActivity())) {
				((ActivityGestureViewPage) getActivity()).getImageWorker().loadImage(imagePath, imageView);
			}
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			if (imageView != null) {
				// Cancel any pending image work
				if (ActivityGestureViewPage.class.isInstance(getActivity())) {
					MyUtilImageWorker.cancelWork(imageView);
				}
				imageView.setImageDrawable(null);
			}
		}
	}

	public static class ImageWorkerFragment extends Fragment {
		public static final String TAG = "ImageWorkerFragment";
		private MyUtilImageWorker imageWorker;
		private Context mContext;

		public ImageWorkerFragment(Context context) {
			this.mContext = context;
		}

		public static ImageWorkerFragment getInstance(Context context, FragmentManager fm) {
			Fragment fragment = fm.findFragmentByTag(TAG);
			if (fragment != null) return (ImageWorkerFragment) fragment;
			fragment = new ImageWorkerFragment(context);
			fm.beginTransaction().add(fragment, TAG).commit();
			return (ImageWorkerFragment) fragment;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setRetainInstance(true);
		}

		public MyUtilImageWorker getImageWorker() {
			if (imageWorker != null) return imageWorker;
			imageWorker = new MyUtilImageWorker(mContext);
			imageWorker.setImageFadeIn(false);
			imageWorker.setLoadingImage(null);
			ImageCacheParams cacheParams = new ImageCacheParams(mContext, "GestureImageCache");
			cacheParams.memoryCacheEnabled = true;
			cacheParams.diskCacheEnabled = true;
			cacheParams.memCacheSize = 10 * 1024; // KB
			cacheParams.diskCacheSize = 60 * 1024 * 1024; // B
			imageWorker.addImageCache(getFragmentManager(), cacheParams);
			return imageWorker;
		}
	}
}
