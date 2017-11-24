package com.xiaotian.framework.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.xiaotian.framework.R;
import com.xiaotian.framework.util.UtilAnimation;
import com.xiaotian.framework.util.UtilLayoutAttribute;
import com.xiaotian.framework.util.UtilTextSpan;
import com.xiaotian.framework.util.UtilView;
import com.xiaotian.frameworkxt.android.util.UtilDevice;
import com.xiaotian.frameworkxt.android.util.UtilSystemIntent;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @description
 * @date 2015/12/4
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 广州易约信息科技有限公司.LTD, All Rights Reserved.
 */
public abstract class ViewFrameLayoutPullRefresh extends FrameLayout implements View.OnClickListener {
    private int PAGE_SIZE = 10;
    private int currentPage = 0;
    private UtilDevice mUtilDevice;
    private PullRefreshAdapter mAdapter;
    private boolean showNetworkDisconnectHint;
    boolean isPullToRefreshEnabled = true;
    boolean isPullTopToRefreshEnabled = true;
    boolean isPullBottomToRefreshEnabled = true;
    // UI
    private View viewLoadingMore;
    private FrameLayout rootView;
    private AbsListView mRefreshableView;
    private UtilAnimation mUtilAnimation;
    private LinearLayout linearErrorConnect;
    private TextView textLoading, textLoadingMore, textEmpty, textErrorMessage, textErrorConnect;
    private FrameLayout linearLoading, linearError, linearEmpty;
    private ViewLinearLayoutPullRefresh mViewListViewPullRefresh;

    // 构造器
    public ViewFrameLayoutPullRefresh(Context context) {
        super(context);
        initializingView(context, null, 0);
    }

    public ViewFrameLayoutPullRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializingView(context, attrs, 0);
    }

    public ViewFrameLayoutPullRefresh(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializingView(context, attrs, defStyleAttr);
    }

    // 初始化
    protected void initializingView(Context context, AttributeSet attrs, int defStyleAttr) {
        mUtilDevice = new UtilDevice(context);
        mUtilAnimation = new UtilAnimation(context);
        showNetworkDisconnectHint = true;
        LayoutInflater inflater = LayoutInflater.from(context);
        viewLoadingMore = inflater.inflate(com.xiaotian.framework.R.layout.pull_to_refresh_loading, new LinearLayout(context), true);
        textLoadingMore = (TextView) viewLoadingMore.findViewById(R.id.pull_to_refresh_text);
        inflater.inflate(com.xiaotian.framework.R.layout.view_framelayout_pull_refresh_ablistview, this, true);
        rootView = (FrameLayout) getChildAt(0);
        linearLoading = (FrameLayout) rootView.getChildAt(0);
        linearError = (FrameLayout) rootView.getChildAt(1);
        linearEmpty = (FrameLayout) rootView.getChildAt(2);
        FrameLayout pullRefreshContainer = (FrameLayout) rootView.getChildAt(3);
        linearErrorConnect = (LinearLayout) rootView.getChildAt(4);
        //
        textErrorMessage = (TextView) ((ViewGroup) linearError.getChildAt(0)).getChildAt(1);
        textLoading = (TextView) ((ViewGroup) linearLoading.getChildAt(0)).getChildAt(1);
        textEmpty = (TextView) ((ViewGroup) linearEmpty.getChildAt(0)).getChildAt(1);
        textErrorConnect = (TextView) linearErrorConnect.getChildAt(0);
        // Instance View
        mViewListViewPullRefresh = createRefreshableView(context, attrs);
        pullRefreshContainer.addView(mViewListViewPullRefresh, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mViewListViewPullRefresh.setDisableScrollingWhileRefreshing(false);
        mRefreshableView = (AbsListView) mViewListViewPullRefresh.getRefreshableView();
        //
        UtilView utilView = new UtilView();
        UtilTextSpan utilTextSpan = new UtilTextSpan();
        UtilLayoutAttribute ua = new UtilLayoutAttribute(context, attrs);
        // Loading
        utilTextSpan.setText(textLoading, ua.getStringAttribute(ua.getNSXiaoTian(), "textLoading"));
        utilTextSpan.setTextSize(textLoading, ua.getDimension(ua.getNSXiaoTian(), "textSizeLoading", -1));
        utilTextSpan.setTextColor(textLoading, ua.getColorAttribute(ua.getNSXiaoTian(), "textColorLoading", -1));
        // Footer loading more
        utilTextSpan.setText(textLoadingMore, ua.getStringAttribute(ua.getNSXiaoTian(), "textLoadingMore"));
        utilTextSpan.setTextSize(textLoadingMore, ua.getDimension(ua.getNSXiaoTian(), "textSizeLoadingMore", -1));
        utilTextSpan.setTextColor(textLoadingMore, ua.getColorAttribute(ua.getNSXiaoTian(), "textColorLoadingMore", -1));
        // Empty
        utilTextSpan.setText(textEmpty, ua.getStringAttribute(ua.getNSXiaoTian(), "textEmpty"));
        utilTextSpan.setTextSize(textEmpty, ua.getDimension(ua.getNSXiaoTian(), "textSizeEmpty", -1));
        utilTextSpan.setTextColor(textEmpty, ua.getColorAttribute(ua.getNSXiaoTian(), "textColorEmpty", -1));
        // Error
        utilTextSpan.setText(textErrorMessage, ua.getStringAttribute(ua.getNSXiaoTian(), "textErrorMessage"));
        utilTextSpan.setTextSize(textErrorMessage, ua.getDimension(ua.getNSXiaoTian(), "textSizeErrorMessage", -1));
        utilTextSpan.setTextColor(textErrorMessage, ua.getColorAttribute(ua.getNSXiaoTian(), "textColorErrorMessage", -1));
        // Top Error Message
        utilTextSpan.setText(textErrorConnect, ua.getStringAttribute(ua.getNSXiaoTian(), "textErrorMessageConnect"));
        utilTextSpan.setTextSize(textErrorConnect, ua.getDimension(ua.getNSXiaoTian(), "textSizeErrorMessageConnect", -1));
        utilTextSpan.setTextColor(textErrorConnect, ua.getColorAttribute(ua.getNSXiaoTian(), "textColorErrorMessageConnect", -1));
        utilView.setBackgroundColor(linearErrorConnect, ua.getColorAttribute(ua.getNSXiaoTian(), "backgroundErrorMessageConnect", -1));
        mViewListViewPullRefresh.setOnRefreshListener(new ViewLinearLayoutPullRefresh.OnRefreshListener() {
            @Override
            public void onRefresh() {
                switch (mViewListViewPullRefresh.getRefreshType()) {
                case 1:
                    // 上拉
                    if (mAdapter != null) mAdapter.loadingFirstPageData();
                    break;
                case 2:
                    // 下拉
                    if (mAdapter != null) mAdapter.loadingNextPageData();
                    break;
                default:
                    if (mAdapter != null) mAdapter.reLoadingPageData();
                    break;
                }
            }
        });
        //
        linearEmpty.setOnClickListener(this);
        linearError.setOnClickListener(this);
        linearErrorConnect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilSystemIntent.openSetting(getContext());
            }
        });
        linearErrorConnect.setVisibility(View.VISIBLE);
        linearErrorConnect.setVisibility(View.GONE);
    }

    protected abstract ViewLinearLayoutPullRefresh createRefreshableView(Context context, AttributeSet attrs);

    public AbsListView getRefreshableView() {
        return mRefreshableView;
    }

    // 显示加载主页内容
    public void showLoadingContent() {
        if (linearLoading.getVisibility() != View.VISIBLE) linearLoading.setVisibility(View.VISIBLE);
        if (mViewListViewPullRefresh.getVisibility() != View.GONE) mViewListViewPullRefresh.setVisibility(View.GONE);
        if (linearErrorConnect.getVisibility() != View.GONE) linearErrorConnect.setVisibility(View.GONE);
        if (linearError.getVisibility() != View.GONE) linearError.setVisibility(View.GONE);
        if (linearEmpty.getVisibility() != View.GONE) linearEmpty.setVisibility(View.GONE);
    }

    // 显示数据视图
    public void showDataView() {
        if (mViewListViewPullRefresh.isRefreshing()) {
            mViewListViewPullRefresh.onRefreshComplete();
        } else {
            mViewListViewPullRefresh.resetPullLabel();
        }
        if (linearLoading.getVisibility() != View.GONE) linearLoading.setVisibility(View.GONE);
        if (mViewListViewPullRefresh.getVisibility() != View.VISIBLE) mViewListViewPullRefresh.setVisibility(View.VISIBLE);
        if (linearErrorConnect.getVisibility() != View.GONE) linearErrorConnect.setVisibility(View.GONE);
        if (linearError.getVisibility() != View.GONE) linearError.setVisibility(View.GONE);
        if (linearEmpty.getVisibility() != View.GONE) linearEmpty.setVisibility(View.GONE);
        if (mAdapter != null) if (getRefreshableView().getAdapter() == null) {
            getRefreshableView().setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
            if (SwipeListView.class.isInstance(getRefreshableView())) {
                ((SwipeListView) getRefreshableView()).closeOpenedItems();
            }
        }
    }

    // 显示无数据视图
    public void showEmptyView() {
        mViewListViewPullRefresh.onRefreshComplete();
        if (linearLoading.getVisibility() != View.GONE) linearLoading.setVisibility(View.GONE);
        if (mViewListViewPullRefresh.getVisibility() != View.GONE) mViewListViewPullRefresh.setVisibility(View.GONE);
        if (linearErrorConnect.getVisibility() != View.GONE) linearErrorConnect.setVisibility(View.GONE);
        if (linearError.getVisibility() != View.GONE) linearError.setVisibility(View.GONE);
        if (linearEmpty.getVisibility() != View.VISIBLE) linearEmpty.setVisibility(View.VISIBLE);
    }

    // 显示错误视图
    public void showErrorView() {
        showErrorView(null);
    }

    // 显示错误视图
    public void showErrorView(String message) {
        mViewListViewPullRefresh.onRefreshComplete();
        if (linearLoading.getVisibility() != View.GONE) linearLoading.setVisibility(View.GONE);
        if (mViewListViewPullRefresh.getVisibility() != View.GONE) mViewListViewPullRefresh.setVisibility(View.GONE);
        if (linearErrorConnect.getVisibility() != View.GONE) linearErrorConnect.setVisibility(View.GONE);
        if (linearError.getVisibility() != View.VISIBLE) linearError.setVisibility(View.VISIBLE);
        if (message != null) textErrorMessage.setText(message);
        if (linearEmpty.getVisibility() != View.GONE) linearEmpty.setVisibility(View.GONE);
        if (showNetworkDisconnectHint && !mUtilDevice.netWorkIsConnected()) pushErrorConnection();
    }

    // 淡出顶部错误视图
    public void toastException(Exception exception) {
        mViewListViewPullRefresh.onRefreshComplete();
        hasMoreData(false);
        if (linearErrorConnect.getVisibility() == View.VISIBLE) return;
        if (showNetworkDisconnectHint && !mUtilDevice.netWorkIsConnected()) pushErrorConnection();
    }

    private void pushErrorConnection() {
        linearErrorConnect.setVisibility(View.VISIBLE);
        mUtilAnimation.startPushDownShow(linearErrorConnect, new UtilAnimation.SimpleAnimationListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (linearErrorConnect.getVisibility() != View.VISIBLE) return;
                        getHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mUtilAnimation.startPushUpHide(linearErrorConnect, new UtilAnimation.SimpleAnimationListener() {
                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        linearErrorConnect.setVisibility(View.GONE);
                                        linearErrorConnect.clearAnimation(); // Clean animation frame of view
                                    }
                                });
                            }
                        }, 2500);
                    }
                }

        );
    }

    public void hasMoreData(boolean hased) {
    }

    @Override
    public void onClick(View v) {
        // 无数据,加载异常
        if (mAdapter != null) mAdapter.initializingData();
    }

    public void setPullRefreshAdapter(PullRefreshAdapter adapter) {
        this.mAdapter = adapter;
    }

    public PullRefreshAdapter getPullRefreshAdapter() {
        return mAdapter;
    }

    //
    public ViewLinearLayoutPullRefresh getPullRefreshView() {
        return mViewListViewPullRefresh;
    }

    public void setPullToRefreshEnabled(boolean enabled) {
        getPullRefreshView().setPullToRefreshEnabled(this.isPullToRefreshEnabled = enabled);
    }

    public void setPullTopToRefreshEnabled(boolean enabled) {
        getPullRefreshView().setPullTopToRefreshEnabled(this.isPullTopToRefreshEnabled = enabled);
    }

    public void setPullBottomToRefreshEnabled(boolean enabled) {
        getPullRefreshView().setPullBottomToRefreshEnabled(this.isPullBottomToRefreshEnabled = enabled);
    }

    public void showNetworkSettingHint(boolean show) {
        this.showNetworkDisconnectHint = show;
    }

    public TextView getTextLoading() {
        return textLoading;
    }

    public TextView getTextLoadingMore() {
        return textLoadingMore;
    }

    public TextView getTextEmpty() {
        return textEmpty;
    }

    public TextView getTextErrorMessage() {
        return textErrorMessage;
    }

    public TextView getTextErrorConnect() {
        return textErrorConnect;
    }

    public FrameLayout getLinearLoading() {
        return linearLoading;
    }

    public FrameLayout getLinearError() {
        return linearError;
    }

    public FrameLayout getLinearEmpty() {
        return linearEmpty;
    }

    public LinearLayout getLinearErrorConnect() {
        return linearErrorConnect;
    }

    public View getViewLoadingMore() {
        return viewLoadingMore;
    }

}
