package com.xiaotian.framework.view;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Handler;
import android.support.annotation.WorkerThread;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;

import java.util.List;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @description
 * @date 2016/1/18
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2015 广州易约信息科技有限公司.LTD, All Rights Reserved.
 */
public abstract class PullRefreshCursorAdapter extends PullRefreshAdapter implements Filterable {
    public static final int FLAG_AUTO_REQUERY = 0x01;
    public static final int FLAG_REGISTER_CONTENT_OBSERVER = 0x02;
    protected boolean mDataValid;
    protected boolean mAutoRequery;
    protected Cursor mCursor;
    protected Context mContext;
    protected Context mDropDownContext;
    protected int mRowIDColumn;
    protected ChangeObserver mChangeObserver;
    protected DataSetObserver mDataSetObserver;
    protected CursorFilter mCursorFilter;
    protected FilterQueryProvider mFilterQueryProvider;
    protected ViewFrameLayoutPullRefresh pullRefreshView;
    Handler mHandler = new Handler();

    public PullRefreshCursorAdapter(ViewFrameLayoutPullRefresh pullRefreshView) {
        super(pullRefreshView, null);
        this.pullRefreshView = pullRefreshView;
    }

    public PullRefreshCursorAdapter(ViewFrameLayoutPullRefresh pullRefreshView, List listData) {
        super(pullRefreshView, listData);
    }


    // 初始化
    @Override
    public void initializingData() {
        if (mCursor == null || mCursor.getCount() < 1) {
            pullRefreshView.showLoadingContent();
        } else {
            pullRefreshView.getRefreshableView().smoothScrollToPosition(0);
            pullRefreshView.getPullRefreshView().setRefreshing(true);
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = getCursor();
                if (cursor != null) {
                    onLoadingSuccess(cursor);
                    init(pullRefreshView.getContext(), cursor, FLAG_AUTO_REQUERY | FLAG_AUTO_REQUERY);
                } else {
                    onLoadingFail(new RuntimeException(""));
                }
            }
        }, 1000);
    }


    void init(Context context, Cursor c, int flags) {
        if ((flags & FLAG_AUTO_REQUERY) == FLAG_AUTO_REQUERY) {
            flags |= FLAG_REGISTER_CONTENT_OBSERVER;
            mAutoRequery = true;
        } else {
            mAutoRequery = false;
        }
        boolean cursorPresent = c != null;
        mCursor = c;
        mDataValid = cursorPresent;
        mContext = context;
        mRowIDColumn = cursorPresent ? c.getColumnIndexOrThrow("_id") : -1;
        if ((flags & FLAG_REGISTER_CONTENT_OBSERVER) == FLAG_REGISTER_CONTENT_OBSERVER) {
            mChangeObserver = new ChangeObserver();
            mDataSetObserver = new MyDataSetObserver();
        } else {
            mChangeObserver = null;
            mDataSetObserver = null;
        }
        //
        if (cursorPresent) {
            if (mChangeObserver != null) c.registerContentObserver(mChangeObserver);
            if (mDataSetObserver != null) c.registerDataSetObserver(mDataSetObserver);
        }
    }

    @Override
    public void loadingNextPageData() {}

    @Override
    public void loadingFirstPageData() {
        loadingPageDate();
    }

    @Override
    public void reLoadingPageData() {}

    @Override
    public void reLoadingData() {}

    @Override
    public void loadingPageData(int currentPage, int pageSize) {

    }

    public void loadingPageDate() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = getCursor();
                if (cursor != null) {
                    onLoadingSuccess(cursor);
                    swapCursor(cursor);
                } else {
                    onLoadingFail(new RuntimeException(""));
                }
            }
        }, 1000);
    }

    @Override
    public void onLoadingComplete(List loadedData, Exception e) {}

    @Override
    public void onLoadingSuccess(List loadedData) {

    }

    public void onLoadingSuccess(Cursor cursor) {
        pullRefreshView.hasMoreData(false);
        if (cursor.getCount() < 1) {
            pullRefreshView.showEmptyView();
        } else {
            pullRefreshView.showDataView();
        }
    }

    @Override
    public void onLoadingFail(Exception exception) {
        if (getCount() < 1) {
            // 第一次加载失败
            pullRefreshView.showErrorView();
        } else {
            // 其他加载失败
            pullRefreshView.toastException(exception);
        }
    }

    public abstract Cursor getCursor();

    public abstract View newView(Context context, Cursor cursor, ViewGroup parent);

    public abstract void bindView(View view, Context context, Cursor cursor);

    public int getCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    public Object getItem(int position) {
        if (mDataValid && mCursor != null) {
            mCursor.moveToPosition(position);
            return mCursor;
        } else {
            return null;
        }
    }

    public long getItemId(int position) {
        if (mDataValid && mCursor != null) {
            if (mCursor.moveToPosition(position)) {
                return mCursor.getLong(mRowIDColumn);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        View v;
        if (convertView == null) {
            v = newView(mContext, mCursor, parent);
        } else {
            v = convertView;
        }
        bindView(v, mContext, mCursor);
        return v;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (mDataValid) {
            final Context context = mDropDownContext == null ? mContext : mDropDownContext;
            mCursor.moveToPosition(position);
            final View v;
            if (convertView == null) {
                v = newDropDownView(context, mCursor, parent);
            } else {
                v = convertView;
            }
            bindView(v, context, mCursor);
            return v;
        } else {
            return null;
        }
    }


    public View newDropDownView(Context context, Cursor cursor, ViewGroup parent) {
        return newView(context, cursor, parent);
    }

    // Swap New Cursor
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        if (oldCursor != null) {
            if (mChangeObserver != null) oldCursor.unregisterContentObserver(mChangeObserver);
            if (mDataSetObserver != null) oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (newCursor != null) {
            if (mChangeObserver != null) newCursor.registerContentObserver(mChangeObserver);
            if (mDataSetObserver != null) newCursor.registerDataSetObserver(mDataSetObserver);
            mRowIDColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            mRowIDColumn = -1;
            mDataValid = false;
            // notify the observers about the lack of a data set
            notifyDataSetInvalidated();
        }
        return oldCursor;
    }


    public Filter getFilter() {
        if (mCursorFilter == null) {
            mCursorFilter = new CursorFilter(new CursorFilter.CursorFilterClient() {
                @Override
                public CharSequence convertToString(Cursor cursor) {
                    return cursor == null ? "" : cursor.toString();
                }

                @Override
                @WorkerThread
                public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
                    if (mFilterQueryProvider != null) {
                        return mFilterQueryProvider.runQuery(constraint);
                    }

                    return mCursor;
                }

                @Override
                public Cursor getCursor() {
                    return mCursor;
                }

                @Override
                public void changeCursor(Cursor cursor) {
                    Cursor old = swapCursor(cursor);
                    if (old != null) {
                        old.close();
                    }
                }
            });
        }
        return mCursorFilter;
    }

    public FilterQueryProvider getFilterQueryProvider() {
        return mFilterQueryProvider;
    }

    public void setFilterQueryProvider(FilterQueryProvider filterQueryProvider) {
        mFilterQueryProvider = filterQueryProvider;
    }

    protected void onContentChanged() {
        if (mAutoRequery && mCursor != null && !mCursor.isClosed()) {
            if (false) Log.v("Cursor", "Auto requerying " + mCursor + " due to update");
            mDataValid = mCursor.requery();
        }
    }

    private class ChangeObserver extends ContentObserver {
        public ChangeObserver() {
            super(new Handler());
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            onContentChanged();
        }
    }

    private class MyDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            mDataValid = false;
            notifyDataSetInvalidated();
        }
    }


    public static class CursorFilter extends Filter {
        CursorFilterClient mClient;

        interface CursorFilterClient {
            CharSequence convertToString(Cursor cursor);

            Cursor runQueryOnBackgroundThread(CharSequence constraint);

            Cursor getCursor();

            void changeCursor(Cursor cursor);
        }

        CursorFilter(CursorFilterClient client) {
            mClient = client;
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return mClient.convertToString((Cursor) resultValue);
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Cursor cursor = mClient.runQueryOnBackgroundThread(constraint);

            FilterResults results = new FilterResults();
            if (cursor != null) {
                results.count = cursor.getCount();
                results.values = cursor;
            } else {
                results.count = 0;
                results.values = null;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            Cursor oldCursor = mClient.getCursor();

            if (results.values != null && results.values != oldCursor) {
                mClient.changeCursor((Cursor) results.values);
            }
        }
    }
}
