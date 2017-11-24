package com.xiaotian.framework.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name ViewListViewScrollableEndless
 * @description Endless Scroll able ListView[Top/Bottom Loading View]
 * @date 2014-7-31
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
@SuppressLint("WrongCall")
public class ViewListViewScrollableEndless extends ListView {
	public static enum LoadingMode {
		SCROLL_TO_TOP, SCROLL_TO_BOTTOM
	};

	// A flag to prevent loading header or footer more than once
	private ScrollListPageListener scrollListPageListener;
	private boolean loadingViewVisible = false;
	protected LoadingMode loadingMode;
	//
	private View loadingView;

	public ViewListViewScrollableEndless(Context context) {
		super(context);
	}

	public ViewListViewScrollableEndless(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ViewListViewScrollableEndless(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private void addLoadingView(ListView listView, View loadingView) {
		if (listView == null || loadingView == null) return;
		// Avoid overlapping the header or footer
		if (!loadingViewVisible) {
			if (loadingMode == LoadingMode.SCROLL_TO_TOP) {
				// Add loading view to list view header when scroll up to load
				listView.addHeaderView(loadingView);
			} else {
				// Add loading view to list view footer when scroll down to load
				listView.addFooterView(loadingView);
			}
			loadingViewVisible = true;
		}
	}

	private void removeLoadingView(ListView listView, View loadingView) {
		if (listView == null || loadingView == null) return;
		// Remove header or footer depending on the loading mode
		if (loadingViewVisible) {
			if (loadingMode == LoadingMode.SCROLL_TO_TOP) {
				listView.removeHeaderView(loadingView);
			} else {
				listView.removeFooterView(loadingView);
			}
			loadingViewVisible = false;
		}
	}

	public void setEndlessAdapter(ScrollLessAdapter adapter) {
		// Pass information to adaptor
		ScrollLessAdapter scrollLessAdapter = (ScrollLessAdapter) adapter;
		scrollLessAdapter.setLoadingMode(loadingMode);
		if (scrollListPageListener == null) {
			scrollListPageListener = new ScrollListPageListener() {
				@Override
				public void endOfList() {
					// Remove loading view when there is no more to load
					removeLoadingView(ViewListViewScrollableEndless.this, loadingView);
				}

				@Override
				public void hasMore() {
					// Display loading view when there might be more to load
					addLoadingView(ViewListViewScrollableEndless.this, loadingView);
				}
			};
		}
		scrollLessAdapter.setListPageListener(scrollListPageListener);
		this.setOnScrollListener(scrollLessAdapter);
		// Workaround to keep spaces for header and footer
		View dummy = new View(getContext());
		addLoadingView(ViewListViewScrollableEndless.this, dummy);
		super.setAdapter(adapter);
		removeLoadingView(ViewListViewScrollableEndless.this, dummy);
	}

	public void setLoadingView(View loadingView) {
		this.loadingView = loadingView;
	}

	public void setLoadingMode(LoadingMode loadingMode) {
		this.loadingMode = loadingMode;
	}

	/******************************************* Inner Class *******************************************/
	public abstract static class ScrollLessAdapter extends BaseAdapter implements OnScrollListener {
		protected LoadingMode loadingMode;
		// A lock to prevent another scrolling event to be triggered if one is
		// already in session
		protected boolean canScroll = false;
		// A flag to enable/disable row clicks
		protected boolean rowEnabled = true;
		protected ScrollListPageListener listPageListener;

		// Abstract Method
		protected abstract void onScrollToNext();

		public abstract View getListItemView(int position, View convertView, ViewGroup parent);

		public void setListPageListener(ScrollListPageListener listPageListener) {
			this.listPageListener = listPageListener;
		}

		@Override
		public boolean isEnabled(int position) {
			return rowEnabled;
		}

		public void setRowEnabled(boolean rowEabled) {
			this.rowEnabled = rowEabled;
		}

		public void lock() {
			canScroll = false;
		}

		public void unlock() {
			canScroll = true;
		}

		public void setLoadingMode(LoadingMode loadingMode) {
			this.loadingMode = loadingMode;
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (view instanceof ViewListViewScrollableEndless) {
				// In scroll-to-top-to-load mode, when the list view scrolls to
				// the first visible position it reaches the top
				if (loadingMode == LoadingMode.SCROLL_TO_TOP && firstVisibleItem == 0 && canScroll) {
					onScrollToNext();
				}
				// In scroll-to-bottom-to-load mode, when the sum of first
				// visible position and visible count equals the total number
				// of items in the adapter it reaches the bottom
				if (loadingMode == LoadingMode.SCROLL_TO_BOTTOM
						&& firstVisibleItem + visibleItemCount - 1 == getCount() && canScroll) {
					onScrollToNext();
				}
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {}

		@Override
		public final View getView(int position, View convertView, ViewGroup parent) {
			return getListItemView(position, convertView, parent);
		}

		public void hideLoadingView() {
			// When there is no more to load use the lock to prevent loading
			// from happening
			lock();
			// More actions when there is no more to load
			if (listPageListener != null) {
				listPageListener.endOfList();
			}
		}

		public void showLoadingView() {
			// Release the lock when there might be more to load
			unlock();
			// More actions when it might have more to load
			if (listPageListener != null) {
				listPageListener.hasMore();
			}
		}
	}

	public interface ScrollListPageListener {
		public abstract void endOfList();

		public abstract void hasMore();
	}

}
