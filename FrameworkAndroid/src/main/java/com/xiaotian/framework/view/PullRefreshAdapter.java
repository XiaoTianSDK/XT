package com.xiaotian.framework.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @description 分页加载ListView, Mode=1:上拉更新,下拉自动加载下一页,Mode=3:上拉更新,下拉加载下一页[同时取消分页,可拉动ListView]
 * @date 2015/12/2
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public abstract class PullRefreshAdapter<T> extends BaseAdapter {
    private List<T> listData;
    private boolean isLoadingData, hasMoreData;
    private int currentPage = 1, pageSize = 10, refreshType;
    private ViewFrameLayoutPullRefresh pullRefreshView;

    public PullRefreshAdapter(ViewFrameLayoutPullRefresh listView, List<T> listData) {
        if (!ViewFrameLayoutPullRefresh.class.isInstance(listView)) {
            throw new RuntimeException("The Init ListView is not ViewFrameLayoutPullRefreshListView.");
        }
        this.pullRefreshView = listView;
        this.listData = listData;
    }

    // 初始化
    public void initializingData() {
        if (isLoadingData) return;
        // 第一次加载页面显示
        refreshType = 0;
        currentPage = 1;
        isLoadingData = true;
        if (listData.isEmpty()) {
            pullRefreshView.showLoadingContent();
        } else {
            pullRefreshView.getRefreshableView().smoothScrollToPosition(0);
            pullRefreshView.getPullRefreshView().setRefreshing(true);
        }
        loadingPageData(currentPage, pageSize);
    }


    // 加载下一页
    public void loadingNextPageData() {
        if (isLoadingData) return;
        if (!hasMoreData) {
            pullRefreshView.getPullRefreshView().onRefreshComplete();
            return;
        }
        refreshType = 2;
        isLoadingData = true;
        loadingPageData(currentPage + 1, pageSize);
    }

    // 加载第一页
    public void loadingFirstPageData() {
        if (isLoadingData) return;
        refreshType = 1;
        isLoadingData = true;
        loadingPageData(currentPage = 1, pageSize);
    }

    // 重新加载当前页
    public void reLoadingPageData() {
        if (isLoadingData) return;
        refreshType = 0;
        isLoadingData = true;
        loadingPageData(currentPage, pageSize);
    }

    // 重载所有数据
    public void reLoadingData() {
        if (isLoadingData) return;
        refreshType = 0;
        currentPage = 1;
        isLoadingData = true;
        pullRefreshView.getRefreshableView().smoothScrollToPosition(0);
        pullRefreshView.getPullRefreshView().setRefreshing(true);
        loadingPageData(currentPage, pageSize);
    }

    // 加载一页
    public abstract void loadingPageData(int currentPage, int pageSize);


    // 加载完成
    public void onLoadingComplete(List<T> loadedData, Exception e) {
        isLoadingData = false;
        if (e == null) {
            onLoadingSuccess(loadedData);
        } else {
            onLoadingFail(e);
        }
    }

    // 加载成功
    public void onLoadingSuccess(List<T> loadedData) {
        isLoadingData = false;
        switch (refreshType) {
        case 0:
            // Initialize data
            if (!listData.isEmpty()) listData.clear();
            if (loadedData != null) listData.addAll(loadedData);
            break;
        case 1:
            // Clean list data and add new data to list data
            if (!listData.isEmpty()) listData.clear();
            if (loadedData != null) listData.addAll(loadedData);
            break;
        case 2:
            // Direct add new data to list data
            if (loadedData != null) {
                if (loadedData.size() >= pageSize) {
                    currentPage++;
                }
                listData.addAll(loadedData);
            }
            break;
        default:
            // Direct add new data to list data
            if (loadedData != null) listData.addAll(loadedData);
            break;
        }
        hasMoreData = loadedData == null ? false : loadedData.size() >= pageSize;
        pullRefreshView.hasMoreData(hasMoreData);
        if (listData.isEmpty()) {
            pullRefreshView.showEmptyView();
        } else {
            pullRefreshView.showDataView();
        }
    }

    // 加载失败
    public void onLoadingFail(Exception exception) {
        isLoadingData = false;
        if (listData.isEmpty()) {
            // 第一次加载失败
            pullRefreshView.showErrorView();
        } else {
            // 其他加载失败
            pullRefreshView.toastException(exception);
        }
    }

    public boolean isLoadingData() {
        return isLoadingData;
    }

    public void setPullToRefreshEnabled(boolean enable) {
        pullRefreshView.setPullToRefreshEnabled(enable);
    }

    public void setPullTopToRefreshEnabled(boolean enable) {
        pullRefreshView.setPullTopToRefreshEnabled(enable);
    }

    public void setPullBottomToRefreshEnabled(boolean enable) {
        pullRefreshView.setPullBottomToRefreshEnabled(enable);
    }

    public void showNetworkSettingHint(boolean show) {
        pullRefreshView.showNetworkSettingHint(show);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position > getCount() - 5) loadingNextPageData();
        return null;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setHasMoreData(boolean hasMoreData) {
        pullRefreshView.hasMoreData(this.hasMoreData = hasMoreData);
    }
}
