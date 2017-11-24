package com.xiaotian.framework.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaotian.framework.R;
import com.xiaotian.framework.common.Constants;
import com.xiaotian.framework.view.HackyViewPager;
import com.xiaotian.framework.view.JazzyViewPager;
import com.xiaotian.framework.view.JazzyViewPager.TransitionEffect;
import com.xiaotian.frameworkxt.android.util.UtilUriMatcher;
import com.xiaotian.frameworkxt.util.UtilFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @version 1.0.0
 * @name ActivityFullScreenImageViewer
 * @description 查看照片
 * @date 2015-4-3
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class ActivityFullScreenImageViewer extends BaseFragmentActivity {
    String showId;
    UtilFile utilFile;
    int currentPosition;
    ArrayList<String> listImage;
    ArrayList<String> listImageIds;
    ProgressDialog dialogLoading;
    ImagePagerAdapter imageAdapter;
    ImageLoader mImageLoader;
    // UI
    TextView textTitle;
    JazzyViewPager viewPager;

    public ActivityFullScreenImageViewer() {
        listImage = new ArrayList<String>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ArrayList<String> images = extras.getStringArrayList(Constants.EXTRA_PARAM.ARRAYLIST);
            if (images != null) listImage.addAll(images);
            currentPosition = extras.getInt(Constants.EXTRA_PARAM.CURRENT_POSITION, 0);
        }
        //
        imageAdapter = new ImagePagerAdapter(getSupportFragmentManager());
        initializingView();
        initializingData();
        if (savedInstanceState != null) {
            boolean isLocked = savedInstanceState.getBoolean("isLocked", false);
            ((HackyViewPager) viewPager).setLocked(isLocked);
        }
    }

    @Override
    protected void initializingView() {
        setContentView(R.layout.activity_fullscreen_imageviewer);
        textTitle = (TextView) findViewById(R.id.view_model_toptoolbar_title_xiaotian);
        textTitle.setText(String.format("%1$d/%2$d", currentPosition + 1, listImage.size()));
        viewPager = (JazzyViewPager) findViewById(R.id.ViewPager);
        viewPager.setTransitionEffect(TransitionEffect.Standard);
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                textTitle.setText(String.format("%1$d / %2$d", position + 1, listImage.size()));
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {}

            @Override
            public void onPageScrollStateChanged(int arg0) {}
        });
        viewPager.setAdapter(imageAdapter);
        viewPager.setPageMargin(30);
        if (currentPosition > -1) viewPager.setCurrentItem(currentPosition);
    }

    protected void initializingData() {}

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (viewPager != null && viewPager instanceof HackyViewPager) {
            outState.putBoolean("isLocked", ((HackyViewPager) viewPager).isLocked());
        }
        super.onSaveInstanceState(outState);
    }

    // Inner Class
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
            Fragment fragment = ImageDetailFragment.newInstance(listImage, position);
            viewPager.setObjectForPosition(fragment, position);
            return fragment;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    public static class ImageDetailFragment extends Fragment {
        static final String EXTRA_IMAGE_POSITION = "position";
        static final String EXTRA_IMAGE_NAME = "name";
        ImageView imageView;
        String imagePath;
        int position;
        View progressBar;
        FrameLayout rootView;

        public static ImageDetailFragment newInstance(List<String> images, int imagePosition) {
            final ImageDetailFragment f = new ImageDetailFragment();
            final String image = images.get(imagePosition);
            final Bundle args = new Bundle();
            args.putString(EXTRA_IMAGE_NAME, image);
            args.putInt(EXTRA_IMAGE_POSITION, imagePosition);
            f.setArguments(args);
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
            rootView = (FrameLayout) inflater.inflate(R.layout.page_gesture_fullscreen_imageview, container, false);
            imageView = (ImageView) rootView.findViewById(R.id.id_1);
            progressBar = rootView.findViewById(R.id.ProgressBar);
            rootView.setTag(R.id.id_position, position);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            if (ActivityFullScreenImageViewer.class.isInstance(getActivity())) {
                ImageLoader utilImageLoader = ((ActivityFullScreenImageViewer) getActivity()).getUniversalImageloader();
                switch (UtilUriMatcher.ResourcesScheme.ofUri(imagePath)) {
                case UNKNOWN:
                    utilImageLoader.displayImage(UtilUriMatcher.ResourcesScheme.FILE.wrap(imagePath), imageView);
                    break;
                default:
                    utilImageLoader.displayImage(imagePath, imageView);
                    break;
                }
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (imageView != null) {
                // Cancel any pending image work
                if (ActivityFullScreenImageViewer.class.isInstance(getActivity())) {
                    // 取消加载的线程
                    ((ActivityFullScreenImageViewer) getActivity()).getUniversalImageloader().cancelDisplayTask(imageView);
                }
                // 清空Bitmap引用,系统将会自动回收内存
                imageView.setImageDrawable(null);
                imageView = null;
            }
        }
    }

    public ImageLoader getUniversalImageloader() {
        if (mImageLoader != null) return mImageLoader;
        return mImageLoader = ImageLoader.getInstance();
    }
}
