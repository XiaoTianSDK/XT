package com.xiaotian.framework.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ViewListViewStretch extends ListView implements OnScrollListener, View.OnTouchListener, AdapterView.OnItemSelectedListener {
	protected static float BREAKSPEED = 4f, ELASTICITY = 0.67f, SCROLL_RATIO = 0.4f;
	public int nHeaders = 1, nFooters = 1, delay = 10, mMaxYOverscrollDistance;
	private int firstVis, visibleCnt, lastVis, totalItems, scrollstate;
	private boolean rebound = false, recalcV = false, trackballEvent = false;
	private long flingTimestamp;
	private float velocity;
	private View measure;
	private GestureDetector gesture;
	private Handler mHandler = new Handler();
	//
	public CheckListviewTopAndBottom checkListviewTopAndBottom = new CheckListviewTopAndBottom();

	// 构造器
	public ViewListViewStretch(Context context) {
		super(context);
		initialize(context);
	}

	public ViewListViewStretch(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	public ViewListViewStretch(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context);
	}

	private void initialize(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		final View v = new View(context);
		v.setMinimumHeight(Math.max(dm.widthPixels, dm.heightPixels));
		// 添加 Header/Footer,用于拉伸
		addHeaderView(v, null, false);
		addFooterView(v, null, false);
		//
		setHeaderDividersEnabled(false);
		setFooterDividersEnabled(false);
		setOnTouchListener(this);
		setOnScrollListener(this);
		setOnItemSelectedListener(this);
		//
		gesture = new GestureDetector(new gestureListener());
		gesture.setIsLongpressEnabled(false);
		flingTimestamp = System.currentTimeMillis();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// 缓冲视图可视状态
		firstVis = firstVisibleItem;
		visibleCnt = visibleItemCount;
		totalItems = totalItemCount;
		lastVis = firstVisibleItem + visibleItemCount;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		scrollstate = scrollState;
		if (scrollState != OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
			rebound = true;
			mHandler.postDelayed(checkListviewTopAndBottom, delay);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> av, View v, int position, long id) {
		rebound = true;
		mHandler.postDelayed(checkListviewTopAndBottom, delay);
	}

	@Override
	public void onNothingSelected(AdapterView<?> av) {
		rebound = true;
		mHandler.postDelayed(checkListviewTopAndBottom, delay);
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		trackballEvent = true;
		rebound = true;
		mHandler.postDelayed(checkListviewTopAndBottom, delay);
		return super.onTrackballEvent(event);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		gesture.onTouchEvent(event);
		return false;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		this.initializeValues();
	}

	/**
	 * This should be called after you finish populating the listview ! This
	 * includes any calls to {@link Adapter#notifyDataSetChanged()} and
	 * obviously every time you re-populate the listview.
	 */
	public void initializeValues() {
		nHeaders = 1; // hide first view
		nFooters = 1;
		//
		firstVis = 0;
		visibleCnt = 0;
		lastVis = 0;
		totalItems = 0;
		scrollstate = 0;
		rebound = true;
		//设置当前为第一个View,位置为0
		setSelectionFromTop(nHeaders, 0);
		smoothScrollBy(0, 0);
		mHandler.postDelayed(checkListviewTopAndBottom, delay);
	}

	public void setBreakspeed(final float breakspeed) {
		if (Math.abs(breakspeed) >= 1.05f) {
			BREAKSPEED = Math.abs(breakspeed);
		}
	}

	public void setElasticity(final float elasticity) {
		if (Math.abs(elasticity) <= 0.75f) {
			ELASTICITY = Math.abs(elasticity);
		}
	}

	private class gestureListener implements OnGestureListener {
		@Override
		public boolean onDown(MotionEvent e) {
			rebound = false;
			recalcV = false;
			velocity = 0f;
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			rebound = true;
			recalcV = true;
			velocity = velocityY / 25f;
			flingTimestamp = System.currentTimeMillis();
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			rebound = true;
			recalcV = false;
			velocity = 0f;
			return false;
		}
	};

	private class CheckListviewTopAndBottom implements Runnable {
		@Override
		public void run() {
			// remove un worked callbacks
			mHandler.removeCallbacks(checkListviewTopAndBottom);
			if (trackballEvent && firstVis < nHeaders && lastVis >= totalItems) {
				trackballEvent = false;
				rebound = false;
				return;
			}
			// is Re bound
			if (rebound) {
				if (firstVis < nHeaders) {
					// hack to avoid strange behaviour when there aren't enough items to fill the entire listview
					if (lastVis >= totalItems) {
						smoothScrollBy(0, 0);
						rebound = false;
						recalcV = false;
						velocity = 0f;
					}
					if (recalcV) {
						recalcV = false;
						velocity /= (1f + ((System.currentTimeMillis() - flingTimestamp) / 1000f));
					}
					if (firstVis == nHeaders) {
						recalcV = false;
					}
					// Header
					if (visibleCnt > nHeaders) {
						measure = getChildAt(nHeaders);
						if (measure.getTop() + velocity < 0) {
							velocity *= -ELASTICITY;
							setSelectionFromTop(nHeaders, 0);
						}
					} else {
						if (velocity > 0f) velocity = -velocity;
					}
					if (rebound) {
						smoothScrollBy((int) -velocity, 0);
						if (velocity > BREAKSPEED) {
							velocity *= ELASTICITY;
							if (velocity < BREAKSPEED) {
								rebound = false;
								recalcV = false;
								velocity = 0f;
							}
						} else
							velocity -= BREAKSPEED;
					}
				} else if (lastVis >= totalItems) {
					//
					if (recalcV) {
						recalcV = false;
						velocity /= (1f + ((System.currentTimeMillis() - flingTimestamp) / 1000f));
					}
					if (lastVis == totalItems - nHeaders - nFooters) {
						rebound = false;
						recalcV = false;
						velocity = 0f;
					} else {
						if (visibleCnt > (nHeaders + nFooters)) {
							measure = getChildAt(visibleCnt - nHeaders - nFooters);
							if (measure.getBottom() + velocity > getHeight()) {
								velocity *= -ELASTICITY;
								setSelectionFromTop(lastVis - nHeaders - nFooters, getHeight() - measure.getHeight() - 1);
							}
						} else {
							if (velocity < 0f) velocity = -velocity;
						}
					}
					if (rebound) {
						smoothScrollBy((int) -velocity, 0);
						if (velocity < -BREAKSPEED) {
							velocity *= ELASTICITY;
							if (velocity > -BREAKSPEED / ELASTICITY) {
								rebound = false;
								recalcV = false;
								velocity = 0f;
							}
						} else
							velocity += BREAKSPEED;
					}
				} else if (scrollstate == OnScrollListener.SCROLL_STATE_IDLE) {
					rebound = false;
					recalcV = false;
					velocity = 0f;
				}
				mHandler.postDelayed(checkListviewTopAndBottom, delay);
				return;
			}
			if (scrollstate != OnScrollListener.SCROLL_STATE_IDLE) return;
			if (totalItems == (nHeaders + nFooters) || firstVis < nHeaders) {
				setSelectionFromTop(nHeaders, 0);
				smoothScrollBy(0, 0);
			} else if (lastVis == totalItems) {
				int offset = getHeight();
				measure = getChildAt(visibleCnt - nHeaders - nFooters);
				if (measure != null) offset -= measure.getHeight();
				setSelectionFromTop(lastVis - nHeaders - nFooters, offset);
				smoothScrollBy(0, 0);
			}
		}
	}

	public static abstract class StretchAdapter extends BaseAdapter {
		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}
	}
}
