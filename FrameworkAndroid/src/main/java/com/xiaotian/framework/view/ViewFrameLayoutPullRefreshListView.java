package com.xiaotian.framework.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.xiaotian.framework.R;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @description Pull Refresh ListView
 * @date 2015/11/27
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class ViewFrameLayoutPullRefreshListView extends ViewFrameLayoutPullRefresh {

    public ViewFrameLayoutPullRefreshListView(Context context) {
        super(context);
    }

    public ViewFrameLayoutPullRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewFrameLayoutPullRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected ViewLinearLayoutPullRefresh createRefreshableView(Context context, AttributeSet attrs) {
        return new ViewListViewPullRefresh(context, attrs);
    }

    public void hasMoreData(boolean hased) {
        switch (getPullRefreshView().getMode()) {
        case ViewLinearLayoutPullRefresh.MODE_PULL_DOWN_TO_REFRESH:
            if (ListView.class.isInstance(getRefreshableView())) {
                if (hased) {
                    // 还有更多
                    ((ListView) getRefreshableView()).removeFooterView(getViewLoadingMore());
                    ((ListView) getRefreshableView()).addFooterView(getViewLoadingMore());
                } else {
                    // 无
                    ((ListView) getRefreshableView()).removeFooterView(getViewLoadingMore());
                }
            }
            getPullRefreshView().setPullBottomToRefreshEnabled(false);
            break;
        case ViewLinearLayoutPullRefresh.MODE_BOTH:
        case ViewLinearLayoutPullRefresh.MODE_PULL_UP_TO_REFRESH:
            if (isPullToRefreshEnabled && isPullBottomToRefreshEnabled) {
                getPullRefreshView().setPullBottomToRefreshEnabled(hased);
            }
            break;
        }
    }

    public class ViewListViewPullRefresh extends ViewLinearLayoutPullRefresh<ListView> {
        RelativeLayout loadFinishView;

        public ViewListViewPullRefresh(Context context) {
            super(context);
        }

        public ViewListViewPullRefresh(Context context, int srollRefreshMode) {
            super(context, srollRefreshMode);
        }

        public ViewListViewPullRefresh(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected ListView createRefreshableView(Context context, AttributeSet attrs) {
            ListView listView = new InternalListView(context, attrs);
            listView.setId(R.id.ListView);
            return listView;
        }

        public void setAdapter(BaseAdapter adapter) {
            getRefreshableView().setAdapter(adapter);
        }

        public ListAdapter getAdapter() {
            return getRefreshableView().getAdapter();
        }

        // ListView
        class InternalListView extends ListView implements EmptyViewMethodAccessor {
            public InternalListView(Context context, AttributeSet attrs) {
                super(context, attrs);
            }

            public ContextMenu.ContextMenuInfo getContextMenuInfo() {
                return super.getContextMenuInfo();
            }

            @Override
            public void setEmptyView(View emptyView) {
                ViewListViewPullRefresh.this.setEmptyView(emptyView);
            }

            @Override
            public void setEmptyViewInternal(View emptyView) {
                super.setEmptyView(emptyView);
            }
        }
    }
}
