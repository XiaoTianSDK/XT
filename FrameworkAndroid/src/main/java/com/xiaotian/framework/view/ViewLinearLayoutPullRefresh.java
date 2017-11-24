package com.xiaotian.framework.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xiaotian.framework.R;
import com.xiaotian.framework.util.UtilLayoutAttribute;

import java.sql.Date;
import java.util.Locale;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @name ViewListViewPullRefresh
 * @description Pull Up And Down Refresh , The Content AbsListView
 * @date 2014-9-28
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public abstract class ViewLinearLayoutPullRefresh<T extends AbsListView> extends LinearLayout {
    public static final int MODE_PULL_DOWN_TO_REFRESH = 0x1;
    public static final int MODE_PULL_UP_TO_REFRESH = 0x2;
    public static final int MODE_BOTH = 0x3;
    //
    static final int PULL_TO_REFRESH = 0x0;
    static final int RELEASE_TO_REFRESH = 0x1;
    static final int REFRESHING = 0x2;
    static final int MANUAL_REFRESHING = 0x3;
    static final float FRICTION = 2.0f;
    //
    private float initialMotionY;
    private float lastMotionX;
    private float lastMotionY;
    private int currentMode;
    private int touchSlop;
    private int headerHeight, footerHeight;
    private int state = PULL_TO_REFRESH;
    private int lastSavedFirstVisibleItem = -1;
    private int mode = MODE_PULL_DOWN_TO_REFRESH;
    //
    private boolean isBeingDragged = false;
    private boolean isPullToRefreshEnabled = true;
    private boolean isPullTopRefreshEnabled = true;
    private boolean isPullBottomRefreshEnabled = true;
    private boolean disableScrollingWhileRefreshing = true;
    //
    private OnScrollListener onScrollListener;
    private OnRefreshListener onRefreshListener;
    private SmoothScrollRunnable currentSmoothScrollRunnable;
    private OnLastItemVisibleListener onLastItemVisibleListener;
    private final Handler handler = new Handler();
    // UI
    private View emptyView;
    private LoadingLayoutHeader headerLayout;
    private LoadingLayoutFooter footerLayout;
    private AbsListView refreshableView;
    private FrameLayout refreshableViewHolder;

    public ViewLinearLayoutPullRefresh(Context context) {
        this(context, null);
    }

    public ViewLinearLayoutPullRefresh(Context context, int srollRefreshMode) {
        this(context);
        this.mode = srollRefreshMode;
    }

    public ViewLinearLayoutPullRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.VERTICAL);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        touchSlop = viewConfiguration.getScaledTouchSlop(); // move touch distance
        UtilLayoutAttribute ua = new UtilLayoutAttribute(context, attrs);
        //
        mode = ua.getIntegerAttribute(ua.getNSXiaoTian(), "mode", 3);
        // Refreshable View By passing the attrs, we can add ListView/GridView parameter via XML
        refreshableView = createRefreshableView(context, attrs);
        addRefreshableView(context, refreshableView);
        // Loading View Strings Add Loading Views
        if (mode == MODE_PULL_DOWN_TO_REFRESH || mode == MODE_BOTH) {
            headerLayout = new LoadingLayoutHeader(context);
            headerLayout.setPullLabel(ua.getStringAttribute("pullStringHeader"));
            headerLayout.setRefreshingLabel(ua.getStringAttribute("refreshingStringHeader"));
            headerLayout.setReleasedLabel(ua.getStringAttribute("releasedStringHeader"));
            headerLayout.setRefreshedDatePattern(ua.getStringAttribute("formatDateHeader"));
            headerLayout.setTextLabelSize(ua.getDimension(ua.getNSXiaoTian(), "headerTextSize", -1));
            headerLayout.setTextDateSize(ua.getDimension(ua.getNSXiaoTian(), "headerDateTextSize", -1));
            headerLayout.setTextLabelColor(ua.getColorAttribute(ua.getNSXiaoTian(), "headerTextColor", -1));
            headerLayout.setTextDateColor(ua.getColorAttribute(ua.getNSXiaoTian(), "headerDateTextColor", -1));
            headerLayout.setPullImage(ua.getAttributeResourceValue(ua.getNSXiaoTian(), "pullHeaderImage", -1));
            addView(headerLayout, 0, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            measureView(headerLayout);
            headerHeight = headerLayout.getMeasuredHeight();
        }
        if (mode == MODE_PULL_UP_TO_REFRESH || mode == MODE_BOTH) {
            footerLayout = new LoadingLayoutFooter(context);
            footerLayout.setPullLabel(ua.getStringAttribute("pullStringFooter"));
            footerLayout.setRefreshingLabel(ua.getStringAttribute("refreshingStringFooter"));
            footerLayout.setReleasedLabel(ua.getStringAttribute("releasedStringFooter"));
            footerLayout.setTextLabelSize(ua.getDimension(ua.getNSXiaoTian(), "footerTextSize", -1));
            footerLayout.setTextLabelColor(ua.getColorAttribute(ua.getNSXiaoTian(), "footerTextColor", -1));
            footerLayout.setPullImage(ua.getAttributeResourceValue(ua.getNSXiaoTian(), "pullFooterImage", -1));
            addView(footerLayout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            measureView(footerLayout);
            footerHeight = footerLayout.getMeasuredHeight();
        }
        // Hide Loading Views
        switch (mode) {
        case MODE_BOTH:
            setPadding(0, -headerHeight, 0, -footerHeight);
            break;
        case MODE_PULL_UP_TO_REFRESH:
            setPadding(0, 0, 0, -footerHeight);
            break;
        case MODE_PULL_DOWN_TO_REFRESH:
        default:
            setPadding(0, -headerHeight, 0, 0);
            break;
        }
        // If we're not using MODE_BOTH, then just set currentMode to current mode
        if (mode != MODE_BOTH) currentMode = mode;
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public final boolean onTouchEvent(MotionEvent event) {
        if (!isPullToRefreshEnabled) return false; // un handle the event
        if (isRefreshing() && disableScrollingWhileRefreshing) return true; // has handle the event
        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) return false;
        //
        switch (event.getAction()) {
        case MotionEvent.ACTION_MOVE:
            if (isBeingDragged) {
                lastMotionY = event.getY();
                pullEvent();
                return true;
            }
            break;
        case MotionEvent.ACTION_DOWN:
            if (isReadyForPull()) {
                lastMotionY = initialMotionY = event.getY();
                return true;
            }
            break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
            if (isBeingDragged) {
                isBeingDragged = false;
                if (state == RELEASE_TO_REFRESH && null != onRefreshListener) {
                    switch (currentMode) {
                    case MODE_PULL_UP_TO_REFRESH:
                        if (isPullTopRefreshEnabled) {
                            setRefreshingInternal(true);
                            onRefreshListener.onRefresh();
                        } else {
                            smoothScrollTo(0);
                        }
                        break;
                    case MODE_PULL_DOWN_TO_REFRESH:
                        if (isPullBottomRefreshEnabled) {
                            setRefreshingInternal(true);
                            onRefreshListener.onRefresh();
                        } else {
                            smoothScrollTo(0);
                        }
                        break;
                    default:
                        setRefreshingInternal(true);
                        onRefreshListener.onRefresh();
                    }
                } else {
                    smoothScrollTo(0);
                }
                return true;
            }
            break;
        }
        return false;
    }

    @Override
    public final boolean onInterceptTouchEvent(MotionEvent event) {
        if (!isPullToRefreshEnabled) return false; // un handle the event
        if (isRefreshing() && disableScrollingWhileRefreshing) return true; // has handle the event
        final int action = event.getAction();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            isBeingDragged = false;
            return false;
        }
        if (action != MotionEvent.ACTION_DOWN && isBeingDragged) return true;
        switch (action) {
        case MotionEvent.ACTION_MOVE: {
            if (isReadyForPull()) {
                final float y = event.getY();
                final float dy = y - lastMotionY;
                final float yDiff = Math.abs(dy);
                final float xDiff = Math.abs(event.getX() - lastMotionX);
                if (yDiff > touchSlop && yDiff > xDiff) {
                    if ((mode == MODE_PULL_DOWN_TO_REFRESH || mode == MODE_BOTH) && dy >= 0.0001f && isReadyForPullDown()) {
                        lastMotionY = y;
                        isBeingDragged = true;
                        if (mode == MODE_BOTH) currentMode = MODE_PULL_DOWN_TO_REFRESH;
                    } else if ((mode == MODE_PULL_UP_TO_REFRESH || mode == MODE_BOTH) && dy <= 0.0001f && isReadyForPullUp()) {
                        lastMotionY = y;
                        isBeingDragged = true;
                        if (mode == MODE_BOTH) currentMode = MODE_PULL_UP_TO_REFRESH;
                    }
                }
            }
            break;
        }
        case MotionEvent.ACTION_DOWN: {
            if (isReadyForPull()) {
                lastMotionY = initialMotionY = event.getY();
                lastMotionX = event.getX();
                isBeingDragged = false;
            }
            break;
        }
        }
        return isBeingDragged;
    }

    protected void resetHeader() {
        state = PULL_TO_REFRESH;
        isBeingDragged = false;
        if (null != headerLayout) headerLayout.reset();
        if (null != footerLayout) footerLayout.reset();
        smoothScrollTo(0);
    }

    protected void setRefreshingInternal(boolean doScroll) {
        state = REFRESHING;
        if (null != headerLayout) headerLayout.refreshing();
        if (null != footerLayout) footerLayout.refreshing();
        if (doScroll) smoothScrollTo(currentMode == MODE_PULL_DOWN_TO_REFRESH ? -headerHeight : footerHeight);
    }

    protected final void setHeaderScroll(int y) {
        scrollTo(0, y);
    }

    protected final void smoothScrollTo(int y) {
        if (null != currentSmoothScrollRunnable) currentSmoothScrollRunnable.stop();
        if (this.getScrollY() != y) {
            this.currentSmoothScrollRunnable = new SmoothScrollRunnable(handler, getScrollY(), y);
            handler.post(currentSmoothScrollRunnable);
        }
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    private boolean pullEvent() {
        final int newHeight;
        final int oldHeight = this.getScrollY();
        switch (currentMode) {
        case MODE_PULL_UP_TO_REFRESH:
            newHeight = Math.round(Math.max(initialMotionY - lastMotionY, 0) / FRICTION);
            break;
        case MODE_PULL_DOWN_TO_REFRESH:
        default:
            newHeight = Math.round(Math.min(initialMotionY - lastMotionY, 0) / FRICTION);
            break;
        }
        setHeaderScroll(newHeight);
        if (newHeight != 0) {
            switch (currentMode) {
            case MODE_PULL_DOWN_TO_REFRESH:
                if (state == PULL_TO_REFRESH && headerHeight < Math.abs(newHeight)) {
                    state = RELEASE_TO_REFRESH;
                    headerLayout.releaseToRefresh();
                    return true;
                } else if (state == RELEASE_TO_REFRESH && headerHeight >= Math.abs(newHeight)) {
                    state = PULL_TO_REFRESH;
                    headerLayout.pullToRefresh();
                    return true;
                }
                break;
            case MODE_PULL_UP_TO_REFRESH:
                if (state == PULL_TO_REFRESH && footerHeight < Math.abs(newHeight)) {
                    state = RELEASE_TO_REFRESH;
                    footerLayout.releaseToRefresh();
                    return true;
                } else if (state == RELEASE_TO_REFRESH && footerHeight >= Math.abs(newHeight)) {
                    state = PULL_TO_REFRESH;
                    footerLayout.pullToRefresh();
                    return true;
                }
                break;
            }
        }
        return oldHeight != newHeight;
    }

    private boolean isReadyForPull() {
        switch (mode) {
        case MODE_PULL_DOWN_TO_REFRESH:
            return isReadyForPullDown();
        case MODE_PULL_UP_TO_REFRESH:
            return isReadyForPullUp();
        case MODE_BOTH:
            return isReadyForPullUp() || isReadyForPullDown();
        }
        return false;
    }

    @Override
    public void setLongClickable(boolean longClickable) {
        getRefreshableView().setLongClickable(longClickable);
    }

    protected void addRefreshableView(Context context, View refreshableView) {
        addView(refreshableView, new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1.0f));
    }

    public final void onScroll(final T view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
        if (null != onLastItemVisibleListener) {
            // detect if last item is visible
            if (visibleItemCount > 0 && (firstVisibleItem + visibleItemCount == totalItemCount)) {
                // only process first event
                if (firstVisibleItem != lastSavedFirstVisibleItem) {
                    lastSavedFirstVisibleItem = firstVisibleItem;
                    onLastItemVisibleListener.onLastItemVisible();
                }
            }
        }
        if (null != onScrollListener) onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
    }

    public final void onScrollStateChanged(final AbsListView view, final int scrollState) {
        if (null != onScrollListener) onScrollListener.onScrollStateChanged(view, scrollState);
    }

    public final void setEmptyView(View newEmptyView) {
        // If we already have an Empty View, remove it
        if (null != emptyView) refreshableViewHolder.removeView(emptyView);
        if (null != newEmptyView) {
            ViewParent newEmptyViewParent = newEmptyView.getParent();
            if (null != newEmptyViewParent && newEmptyViewParent instanceof ViewGroup) ((ViewGroup) newEmptyViewParent).removeView(newEmptyView);
            this.refreshableViewHolder.addView(newEmptyView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        if (refreshableView instanceof EmptyViewMethodAccessor) {
            ((EmptyViewMethodAccessor) refreshableView).setEmptyViewInternal(newEmptyView);
        } else {
            refreshableView.setEmptyView(newEmptyView);
        }
    }

    public final void setOnLastItemVisibleListener(OnLastItemVisibleListener listener) {
        onLastItemVisibleListener = listener;
    }

    public final void setOnScrollListener(OnScrollListener listener) {
        onScrollListener = listener;
    }

    // Add Refreshable ListView/GridView or other ABSListView
    protected void addRefreshableView(Context context, T refreshableView) {
        refreshableViewHolder = new FrameLayout(context);
        refreshableViewHolder.addView(refreshableView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(refreshableViewHolder, new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1.0f));
    }

    protected boolean isReadyForPullDown() {
        return isFirstItemVisible();
    }

    protected boolean isReadyForPullUp() {
        return isLastItemVisible();
    }

    private boolean isFirstItemVisible() {
        if (this.refreshableView.getCount() == 0) return true;
        if (refreshableView.getFirstVisiblePosition() == 0) {
            final View firstVisibleChild = refreshableView.getChildAt(0);
            if (firstVisibleChild != null) return firstVisibleChild.getTop() >= refreshableView.getTop();
        }
        return false;
    }

    private boolean isLastItemVisible() {
        final int count = this.refreshableView.getCount();
        final int lastVisiblePosition = refreshableView.getLastVisiblePosition();
        if (count == 0) return true;
        if (lastVisiblePosition == count - 1) {
            final int childIndex = lastVisiblePosition - refreshableView.getFirstVisiblePosition();
            final View lastVisibleChild = refreshableView.getChildAt(childIndex);
            if (lastVisibleChild != null) return lastVisibleChild.getBottom() <= refreshableView.getBottom();
        }
        return false;
    }

    public final void onRefreshComplete() {
        if (state != PULL_TO_REFRESH) resetHeader();
    }

    public final boolean isRefreshing() {
        return state == REFRESHING || state == MANUAL_REFRESHING;
    }

    public final void setDisableScrollingWhileRefreshing(boolean disableScrollingWhileRefreshing) {
        this.disableScrollingWhileRefreshing = disableScrollingWhileRefreshing;
    }

    public final AbsListView getRefreshableView() {
        return refreshableView;
    }

    public final boolean isPullToRefreshEnabled() {
        return isPullToRefreshEnabled;
    }

    public final boolean isPullTopToRefreshEnabled() {
        return null == footerLayout ? false : footerLayout.isLoadingContentVisiable;
    }

    public final boolean isPullButtomToRefreshEnabled() {
        return null == headerLayout ? false : headerLayout.isLoadingContentVisiable;
    }

    public final boolean isDisableScrollingWhileRefreshing() {
        return disableScrollingWhileRefreshing;
    }

    public final void setOnRefreshListener(OnRefreshListener listener) {
        onRefreshListener = listener;
    }

    public final void setPullToRefreshEnabled(boolean enable) {
        this.isPullToRefreshEnabled = enable;
    }

    public final void setPullTopToRefreshEnabled(boolean enable) {
        if (null != headerLayout) headerLayout.setLoadingLayoutVisiable(isPullBottomRefreshEnabled = enable);
    }

    public final void setPullBottomToRefreshEnabled(boolean enable) {
        if (null != footerLayout) footerLayout.setLoadingLayoutVisiable(isPullTopRefreshEnabled = enable);
    }

    public final void setRefreshing() {
        this.setRefreshing(true);
    }

    public final void setRefreshing(boolean doScroll) {
        if (!isRefreshing()) {
            setRefreshingInternal(doScroll);
            state = MANUAL_REFRESHING;
        }
    }

    public final boolean hasPullFromTop() {
        return currentMode != MODE_PULL_UP_TO_REFRESH;
    }

    public int getRefreshType() {
        return getCurrentMode();
    }

    protected final int getCurrentMode() {
        return currentMode;
    }

    protected final LoadingLayoutFooter getFooterLayout() {
        return footerLayout;
    }

    protected final LoadingLayoutHeader getHeaderLayout() {
        return headerLayout;
    }

    protected final int getMode() {
        return mode;
    }

    public void setPullDateColor(int color) {
        if (null != headerLayout) headerLayout.setTextDateColor(color);
    }

    public void resetPullLabel() {
        if (null != headerLayout) headerLayout.reset();
        if (null != footerLayout) footerLayout.reset();
    }

    public LoadingLayoutHeader getPullHeader() {
        return headerLayout;
    }

    public LoadingLayoutFooter getPullFooter() {
        return footerLayout;
    }

    /******************************************* Abstract Class *******************************************/
    protected abstract T createRefreshableView(Context context, AttributeSet attrs);

    /******************************************* Inner Interface *******************************************/
    public static interface OnRefreshListener {
        public void onRefresh();
    }

    public static interface OnLastItemVisibleListener {
        public void onLastItemVisible();
    }

    // Scroll View Interface,setting the empty view
    public interface EmptyViewMethodAccessor {
        public void setEmptyView(View emptyView);

        public void setEmptyViewInternal(View emptyView);
    }

    /******************************************* Inner Class *******************************************/
    final class SmoothScrollRunnable implements Runnable {
        static final int ANIMATION_DURATION_MS = 190;
        static final int ANIMATION_FPS = 1000 / 60;
        private final Interpolator interpolator;
        private boolean continueRunning = true;
        private final int scrollFromY;
        private final Handler handler;
        private final int scrollToY;
        private long startTime = -1;
        private int currentY = -1;

        public SmoothScrollRunnable(Handler handler, int fromY, int toY) {
            this.interpolator = new AccelerateDecelerateInterpolator();
            this.scrollFromY = fromY;
            this.handler = handler;
            this.scrollToY = toY;
        }

        @Override
        public void run() {
            /**
             * Only set startTime if this is the first time we're starting, else
             * actually calculate the Y delta
             */
            if (startTime == -1) {
                startTime = System.currentTimeMillis();
            } else {
                /**
                 * We do do all calculations in long to reduce software float
                 * calculations. We use 1000 as it gives us good accuracy and
                 * small rounding errors
                 */
                long normalizedTime = (1000 * (System.currentTimeMillis() - startTime)) / ANIMATION_DURATION_MS;
                normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);

                final int deltaY = Math.round((scrollFromY - scrollToY) * interpolator.getInterpolation(normalizedTime / 1000f));
                this.currentY = scrollFromY - deltaY;
                setHeaderScroll(currentY);
            }
            // If we're not at the target Y, keep going...
            if (continueRunning && scrollToY != currentY) handler.postDelayed(this, ANIMATION_FPS);
        }

        public void stop() {
            this.continueRunning = false;
            this.handler.removeCallbacks(this);
        }
    }

    public class LoadingLayoutHeader extends FrameLayout {
        private boolean isLoadingContentVisiable = true;
        private long currentRefreshDate = System.currentTimeMillis();
        private String refreshingLabel = "Loading...", releaseLabel = "Release To Loading", pullLabel = "Pull To Refreshing", refreshedDatePattern = "%1$Ty-%<Tm-%<Td %<TH:%<TM";
        // UI
        private final TextView headerText, headerDate;
        private final ImageView headerImage;
        private final View headerProgress;
        private final Animation rotateAnimation, resetRotateAnimation;

        public LoadingLayoutHeader(Context context) {
            super(context);
            // UI
            ViewGroup header = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header, this);
            headerText = (TextView) header.findViewById(R.id.pull_to_refresh_text);
            headerDate = (TextView) header.findViewById(R.id.pull_to_refresh_date);
            headerImage = (ImageView) header.findViewById(R.id.pull_to_refresh_image);
            headerProgress = header.findViewById(R.id.pull_to_refresh_progress);
            // Turn Image 180 degree
            final Interpolator interpolator = new LinearInterpolator();
            rotateAnimation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setInterpolator(interpolator);
            rotateAnimation.setDuration(250);
            rotateAnimation.setFillAfter(true);
            // Turn Back Rotate Animation
            resetRotateAnimation = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            resetRotateAnimation.setInterpolator(interpolator);
            resetRotateAnimation.setDuration(250);
            resetRotateAnimation.setFillAfter(true);
            headerImage.setImageResource(R.drawable.pulltorefresh_down_arrow);
        }

        public void reset() {
            if (!isLoadingContentVisiable) return;
            headerText.setText(pullLabel);
            headerImage.setVisibility(View.VISIBLE);
            headerProgress.setVisibility(View.GONE);
            headerDate.setText(formatDateString());
        }

        public void releaseToRefresh() {
            if (!isLoadingContentVisiable) return;
            headerText.setText(releaseLabel);
            headerImage.clearAnimation();
            headerImage.startAnimation(rotateAnimation);
        }

        public void pullToRefresh() {
            if (!isLoadingContentVisiable) return;
            headerText.setText(pullLabel);
            headerImage.clearAnimation();
            headerImage.startAnimation(resetRotateAnimation);
        }

        public void refreshing() {
            if (!isLoadingContentVisiable) return;
            currentRefreshDate = System.currentTimeMillis();
            headerText.setText(refreshingLabel);
            headerImage.clearAnimation();
            headerImage.setVisibility(View.INVISIBLE);
            headerProgress.setVisibility(View.VISIBLE);
        }

        private String formatDateString() {
            return String.format(Locale.CHINA, refreshedDatePattern == null ? "%1$Ty.%<Tm.%<Td %<TH:%<TM" : refreshedDatePattern, new Date(currentRefreshDate));
        }

        public void setPullLabel(String pullLabel) {
            if (pullLabel == null) return;
            headerText.setText(this.pullLabel = pullLabel);
        }


        public void setRefreshingLabel(String refreshingLabel) {
            if (refreshingLabel == null) return;
            this.refreshingLabel = refreshingLabel;
        }

        public void setReleasedLabel(String releaseLabel) {
            if (releaseLabel == null) return;
            this.releaseLabel = releaseLabel;
        }

        public void setRefreshedDatePattern(String pattern) {
            if (pattern == null) return;
            this.refreshedDatePattern = pattern;
        }

        public void setTextLabelColor(int color) {
            if (color == -1) return;
            headerText.setTextColor(color);
        }

        public void setTextLabelSize(float size) {
            if (size < 0) return;
            headerText.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
            measureView(this);
            headerHeight = getMeasuredHeight();
            ViewLinearLayoutPullRefresh.this.setPadding(0, -headerHeight, 0, -footerHeight);
        }

        public void setTextDateSize(float size) {
            if (size < 0) return;
            headerDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
            measureView(this);
            headerHeight = getMeasuredHeight();
            ViewLinearLayoutPullRefresh.this.setPadding(0, -headerHeight, 0, -footerHeight);
        }

        public void setTextDateColor(int color) {
            if (color == -1) return;
            headerDate.setTextColor(color);
        }

        public void setPullImage(int pullImage) {
            if (pullImage == -1) return;
            headerImage.setImageResource(pullImage);
            measureView(this);
            headerHeight = getMeasuredHeight();
            ViewLinearLayoutPullRefresh.this.setPadding(0, -headerHeight, 0, -footerHeight);
        }

        public void setLoadingLayoutVisiable(boolean enable) {
            this.isLoadingContentVisiable = enable;
            if (isLoadingContentVisiable) {
                if (headerDate.getVisibility() != View.VISIBLE) headerDate.setVisibility(View.VISIBLE);
                if (headerText.getVisibility() != View.VISIBLE) headerText.setVisibility(View.VISIBLE);
                if (headerImage.getVisibility() != View.VISIBLE) headerImage.setVisibility(View.VISIBLE);
                if (headerProgress.getVisibility() != View.VISIBLE) headerProgress.setVisibility(View.VISIBLE);
            } else {
                if (headerDate.getVisibility() != View.INVISIBLE) headerDate.setVisibility(View.INVISIBLE);
                if (headerText.getVisibility() != View.INVISIBLE) headerText.setVisibility(View.INVISIBLE);
                if (headerImage.getVisibility() != View.INVISIBLE) headerImage.setVisibility(View.INVISIBLE);
                if (headerProgress.getVisibility() != View.INVISIBLE) headerProgress.setVisibility(View.INVISIBLE);
            }
        }
    }

    public class LoadingLayoutFooter extends FrameLayout {
        private boolean isLoadingContentVisiable = true;
        private String refreshingLabel = "Loading...", releaseLabel = "Release To Loading", pullLabel = "Pull To Refreshing";
        // UI
        private final TextView footerText;
        private final ImageView footerImage;
        private final ProgressBar footerProgress;
        private final Animation rotateAnimation, resetRotateAnimation;

        public LoadingLayoutFooter(Context context) {
            super(context);
            // UI
            ViewGroup header = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_footer, this);
            footerText = (TextView) header.findViewById(R.id.pull_to_refresh_text);
            footerImage = (ImageView) header.findViewById(R.id.pull_to_refresh_image);
            footerProgress = (ProgressBar) header.findViewById(R.id.pull_to_refresh_progress);
            // Turn Image 180 degree
            final Interpolator interpolator = new LinearInterpolator();
            rotateAnimation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setInterpolator(interpolator);
            rotateAnimation.setDuration(250);
            rotateAnimation.setFillAfter(true);
            // Turn Back Rotate Animation
            resetRotateAnimation = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            resetRotateAnimation.setInterpolator(interpolator);
            resetRotateAnimation.setDuration(250);
            resetRotateAnimation.setFillAfter(true);
            footerImage.setImageResource(R.drawable.pulltorefresh_up_arrow);
        }

        public void reset() {
            if (!isLoadingContentVisiable) return;
            footerText.setText(pullLabel);
            footerImage.setVisibility(View.VISIBLE);
            footerProgress.setVisibility(View.GONE);
        }

        public void releaseToRefresh() {
            if (!isLoadingContentVisiable) return;
            footerText.setText(releaseLabel);
            footerImage.clearAnimation();
            footerImage.startAnimation(rotateAnimation);
        }

        public void pullToRefresh() {
            if (!isLoadingContentVisiable) return;
            footerText.setText(pullLabel);
            footerImage.clearAnimation();
            footerImage.startAnimation(resetRotateAnimation);
        }

        public void refreshing() {
            if (!isLoadingContentVisiable) return;
            footerText.setText(refreshingLabel);
            footerImage.clearAnimation();
            footerImage.setVisibility(View.INVISIBLE);
            footerProgress.setVisibility(View.VISIBLE);
        }

        public void setPullLabel(String pullLabel) {
            if (pullLabel == null) return;
            footerText.setText(this.pullLabel = pullLabel);
        }


        public void setRefreshingLabel(String refreshingLabel) {
            if (refreshingLabel == null) return;
            this.refreshingLabel = refreshingLabel;
        }

        public void setReleasedLabel(String releaseLabel) {
            if (releaseLabel == null) return;
            this.releaseLabel = releaseLabel;
        }

        public void setTextLabelColor(int color) {
            if (color == -1) return;
            footerText.setTextColor(color);
        }

        public void setPullImage(int pullImage) {
            if (pullImage == -1) return;
            footerImage.setImageResource(pullImage);
            measureView(this);
            footerHeight = getMeasuredHeight();
            ViewLinearLayoutPullRefresh.this.setPadding(0, -headerHeight, 0, -footerHeight);
        }

        public void setTextLabelSize(float size) {
            if (size < 0) return;
            footerText.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
            measureView(this);
            footerHeight = getMeasuredHeight();
            ViewLinearLayoutPullRefresh.this.setPadding(0, -headerHeight, 0, -footerHeight);
        }


        public void setLoadingLayoutVisiable(boolean enable) {
            this.isLoadingContentVisiable = enable;
            if (isLoadingContentVisiable) {
                if (footerText.getVisibility() != View.VISIBLE) footerText.setVisibility(View.VISIBLE);
                if (footerImage.getVisibility() != View.VISIBLE) footerImage.setVisibility(View.VISIBLE);
                if (footerProgress.getVisibility() != View.VISIBLE) footerProgress.setVisibility(View.VISIBLE);
            } else {
                if (footerText.getVisibility() != View.INVISIBLE) footerText.setVisibility(View.INVISIBLE);
                if (footerImage.getVisibility() != View.INVISIBLE) footerImage.setVisibility(View.INVISIBLE);
                if (footerProgress.getVisibility() != View.INVISIBLE) footerProgress.setVisibility(View.INVISIBLE);
            }
        }
    }
}
