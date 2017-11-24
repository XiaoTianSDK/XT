package com.xiaotian.framework.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.xiaotian.framework.R;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @description 下拉更新, 下拉自动加载下一页GridView
 * @date 2015/11/27
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class ViewFrameLayoutPullRefreshGridView extends ViewFrameLayoutPullRefresh {

    public ViewFrameLayoutPullRefreshGridView(Context context) {
        super(context);
    }

    public ViewFrameLayoutPullRefreshGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewFrameLayoutPullRefreshGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected ViewLinearLayoutPullRefresh createRefreshableView(Context context, AttributeSet attrs) {
        return new ViewGridViewPullRefresh(context, attrs);
    }

    public void hasMoreData(boolean hased) {
    }

    public class ViewGridViewPullRefresh extends ViewLinearLayoutPullRefresh<GridView> {

        public ViewGridViewPullRefresh(Context context) {
            super(context);
        }

        public ViewGridViewPullRefresh(Context context, int srollRefreshMode) {
            super(context, srollRefreshMode);
        }

        public ViewGridViewPullRefresh(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected GridView createRefreshableView(Context context, AttributeSet attrs) {
            GridView gridView = new InternalListView(context, attrs);
            gridView.setId(R.id.GridView);
            return gridView;
        }

        public void setAdapter(BaseAdapter adapter) {
            getRefreshableView().setAdapter(adapter);
        }

        public ListAdapter getAdapter() {
            return getRefreshableView().getAdapter();
        }

        // GridView
        class InternalListView extends GridView implements EmptyViewMethodAccessor {
            public InternalListView(Context context, AttributeSet attrs) {
                super(context, attrs);
            }

            public ContextMenu.ContextMenuInfo getContextMenuInfo() {
                return super.getContextMenuInfo();
            }

            @Override
            public void setEmptyView(View emptyView) {
                ViewGridViewPullRefresh.this.setEmptyView(emptyView);
            }

            @Override
            public void setEmptyViewInternal(View emptyView) {
                super.setEmptyView(emptyView);
            }
        }
    }
}
