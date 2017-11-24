package com.xiaotian.framework.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;

import com.xiaotian.framework.R;

import java.util.List;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @description Pull Refresh ListView
 * @date 2015/11/27
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class ViewFrameLayoutPullRefreshIndexableListView extends ViewFrameLayoutPullRefresh {
    private ListView mListView;
    private IndexScroller mScroller = null;
    private boolean mIsFastScrollEnabled = false;
    private GestureDetector mGestureDetector = null;
    private boolean isAlwayShowIndexScroller = false;

    public ViewFrameLayoutPullRefreshIndexableListView(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public ViewFrameLayoutPullRefreshIndexableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }

    public ViewFrameLayoutPullRefreshIndexableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
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


    public void setAlwaysShowIndexView(boolean enabled) {
        isAlwayShowIndexScroller = enabled;
        if (mScroller == null) {
            if (enabled) {
                mScroller = new IndexScroller(getContext(), getListView());
                mScroller.setAlwaysShowIndexView(enabled);
            }
        } else {
            mScroller.setAlwaysShowIndexView(enabled);
        }
    }


    public void setFastScrollEnabled(boolean enabled) {
        mIsFastScrollEnabled = enabled;
        if (mIsFastScrollEnabled) {
            if (mScroller == null) {
                mScroller = new IndexScroller(getContext(), getListView());
                mScroller.setAlwaysShowIndexView(isAlwayShowIndexScroller);
            }
        } else {
            if (mScroller != null) {
                mScroller.hide();
                mScroller = null;
            }
        }
    }

    public void hideIndexScroller() {
        if (mScroller != null) mScroller.hide();
    }

    public void showIndexScroller() {
        if (mScroller != null) mScroller.show();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        // Overlay index bar
        if (mScroller != null) mScroller.draw(canvas);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent ev) {
        // Intercept ListView's touch event
        if (mScroller != null && mScroller.onTouchEvent(ev)) return true;
        if (mScroller != null && mGestureDetector == null) {
            mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    // If fling happens, index bar shows
                    mScroller.show();
                    return super.onFling(e1, e2, velocityX, velocityY);
                }
            });
        }
        if (mGestureDetector != null) mGestureDetector.onTouchEvent(ev);
        if (mScroller != null && ev.getAction() == MotionEvent.ACTION_UP) postInvalidate();
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mScroller != null && mScroller.contains(ev.getX(), ev.getY())) return true;
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void setPullRefreshAdapter(PullRefreshAdapter adapter) {
        super.setPullRefreshAdapter(adapter);
        if (mScroller != null) mScroller.setAdapter(adapter);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mScroller != null) mScroller.onSizeChanged(w, h, oldw, oldh);
    }

    public ListView getListView() {
        if (mListView != null) return mListView;
        return mListView = (ListView) getRefreshableView();
    }

    public static abstract class PullRefreshAdapterIndexable<T> extends PullRefreshAdapter<T> implements SectionIndexer {
        private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        public PullRefreshAdapterIndexable(ViewFrameLayoutPullRefreshIndexableListView listView, List listData) {
            super(listView, listData);
        }

        @Override
        public int getPositionForSection(int section) {
            // If there is no item for current section, previous section will be selected
            for (int i = section; i >= 0; i--) {
                for (int j = 0; j < getCount(); j++) {
                    // Matcher Method
                    if (getItem(j) instanceof String) { // Item is String
                        if (i == 0) {
                            // For numeric section
                            for (int k = 0; k <= 9; k++) {
                                if (match(String.valueOf(String.valueOf(getItem(j)).charAt(0)), String.valueOf(k))) {
                                    return j;
                                }
                            }
                        } else {
                            // match section
                            if (match(String.valueOf(String.valueOf(getItem(j)).charAt(0)), String.valueOf(mSections.charAt(i)))) {
                                return j;
                            }
                        }
                    }
                    return 0;
                }
            }
            return 0;
        }

        @Override
        public int getSectionForPosition(int position) {
            return 0;
        }

        @Override
        public Object[] getSections() {
            String[] sections = new String[mSections.length()];
            for (int i = 0; i < mSections.length(); i++)
                sections[i] = String.valueOf(mSections.charAt(i));
            return sections;
        }

        public String getStringCharacterAt(int index) {
            return String.valueOf(mSections.charAt(index));
        }

        public boolean match(String value, String keyword) {
            if (value == null || keyword == null) return false;
            if (keyword.length() > value.length()) return false;
            int i = 0, j = 0;
            do {
                if (keyword.charAt(j) == value.charAt(i)) {
                    i++;
                    j++;
                } else if (j > 0) break;
                else i++;
            } while (i < value.length() && j < keyword.length());
            return (j == keyword.length()) ? true : false;
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
            ListView listView = new ListView(context, attrs);
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
        class InternalListView extends IndexableListView implements EmptyViewMethodAccessor {
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
