/*
 * Copyright 2011 woozzu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xiaotian.framework.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

public class IndexableListView extends ListView {
	private boolean isAlwayShowIndexScroller;
	private boolean mIsFastScrollEnabled = false;
	private IndexScroller mScroller = null;
	private GestureDetector mGestureDetector = null;

	public IndexableListView(Context context) {
		super(context);
	}

	public IndexableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public IndexableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setAlwaysShowIndexView(boolean enabled) {
		isAlwayShowIndexScroller = enabled;
		if (mScroller == null) {
			if (enabled) {
				mScroller = new IndexScroller(getContext(), this);
				mScroller.setAlwaysShowIndexView(enabled);
			}
		} else {
			mScroller.setAlwaysShowIndexView(enabled);
		}
	}

	@Override
	public boolean isFastScrollEnabled() {
		return mIsFastScrollEnabled;
	}

	@Override
	public void setFastScrollEnabled(boolean enabled) {
		mIsFastScrollEnabled = enabled;
		if (mIsFastScrollEnabled) {
			if (mScroller == null) {
				mScroller = new IndexScroller(getContext(), this);
				mScroller.setAlwaysShowIndexView(isAlwayShowIndexScroller);
			}
		} else {
			if (mScroller != null) {
				mScroller.hide();
				mScroller = null;
			}
		}
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

		if (mGestureDetector == null) {
			mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
					// If fling happens, index bar shows
					mScroller.show();
					return super.onFling(e1, e2, velocityX, velocityY);
				}

			});
		}
		mGestureDetector.onTouchEvent(ev);

		return super.onTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		if (mScroller != null) mScroller.setAdapter(adapter);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (mScroller != null) mScroller.onSizeChanged(w, h, oldw, oldh);
	}

	public static abstract class LetterBaseAdapter extends BaseAdapter implements SectionIndexer {
		private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		public LetterBaseAdapter(Context context) {}

		@Override
		public int getPositionForSection(int section) {
			// If there is no item for current section, previous section will be selected
			for (int i = section; i >= 0; i--) {
				for (int j = 0; j < getCount(); j++) {
					if (getItem(j) instanceof String) {
						if (i == 0) {
							// For numeric section
							for (int k = 0; k <= 9; k++) {
								if (match(String.valueOf(String.valueOf(getItem(j)).charAt(0)), String.valueOf(k))) return j;
							}
						} else {
							if (match(String.valueOf(String.valueOf(getItem(j)).charAt(0)), String.valueOf(mSections.charAt(i)))) return j;
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

		boolean match(String value, String keyword) {
			if (value == null || keyword == null) return false;
			if (keyword.length() > value.length()) return false;
			int i = 0, j = 0;
			do {
				if (keyword.charAt(j) == value.charAt(i)) {
					i++;
					j++;
				} else if (j > 0) break;
				else
					i++;
			} while (i < value.length() && j < keyword.length());
			return (j == keyword.length()) ? true : false;
		}
	}
}
