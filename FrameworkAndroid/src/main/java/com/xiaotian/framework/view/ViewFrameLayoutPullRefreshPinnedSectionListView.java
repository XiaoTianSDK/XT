package com.xiaotian.framework.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.xiaotian.framework.R;

import java.util.List;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @description Pinned Section ListView
 * @date 2015/12/2
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class ViewFrameLayoutPullRefreshPinnedSectionListView extends ViewFrameLayoutPullRefresh {

    public ViewFrameLayoutPullRefreshPinnedSectionListView(Context context) {
        super(context);
    }

    public ViewFrameLayoutPullRefreshPinnedSectionListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewFrameLayoutPullRefreshPinnedSectionListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected ViewLinearLayoutPullRefresh createRefreshableView(Context context, AttributeSet attrs) {
        return new ViewPinnedSectionListViewPullRefresh(context, attrs);
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

    public static abstract class PullRefreshAdapterPinnedSection extends PullRefreshAdapter implements ViewListViewPinnedSection.PinnedSectionListAdapter {

        public PullRefreshAdapterPinnedSection(ViewFrameLayoutPullRefreshPinnedSectionListView listView, List listData) {
            super(listView, listData);
        }
    }

    public class ViewPinnedSectionListViewPullRefresh extends ViewLinearLayoutPullRefresh<ViewListViewPinnedSection> {

        public ViewPinnedSectionListViewPullRefresh(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected ViewListViewPinnedSection createRefreshableView(Context context, AttributeSet attrs) {
            ViewListViewPinnedSection listView = new InternalListView(context, attrs);
            listView.setId(R.id.ListView);
            return listView;
        }

        public void setAdapter(BaseAdapter adapter) {
            getRefreshableView().setAdapter(adapter);
        }

        public ListAdapter getAdapter() {
            return getRefreshableView().getAdapter();
        }

        class InternalListView extends ViewListViewPinnedSection implements EmptyViewMethodAccessor {
            public InternalListView(Context context, AttributeSet attrs) {
                super(context, attrs);
            }

            public ContextMenu.ContextMenuInfo getContextMenuInfo() {
                return super.getContextMenuInfo();
            }

            @Override
            public void setEmptyView(View emptyView) {
                ViewPinnedSectionListViewPullRefresh.this.setEmptyView(emptyView);
            }

            @Override
            public void setEmptyViewInternal(View emptyView) {
                super.setEmptyView(emptyView);
            }
        }
    }

}
