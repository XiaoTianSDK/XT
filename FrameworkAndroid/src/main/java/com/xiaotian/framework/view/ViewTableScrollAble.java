package com.xiaotian.framework.view;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.xiaotian.framework.R;
import com.xiaotian.framework.util.UtilLayoutAttribute;
import com.xiaotian.framework.view.ViewTable.ViewTableDataSource;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name ViewTableScrollAble
 * @description 除左右表头,可滚动表格::实现ViewTableDataSource接口
 * @date 2013-10-21
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
public class ViewTableScrollAble extends LinearLayout implements ViewTableDataSource {
	private static final int TEXT_SIZE_TITLE = 18;
	private static final int TEXT_SIZE_DATA = 16;
	private ScrollViewListener scrollListener;
	private ViewTableDataSource dataSource;
	private boolean isScrollable;
	private ScrollTableData data;
	private DisplayMetrics dm;
	// UI
	private ViewTable table;
	private ScrollView scrollViewV;
	private ViewTopLeft titleBingeTopLeft;
	private TitleViewRow titleRowView;
	private TitleViewColumn titleColumnView;
	private HorizontalScrollView scrollViewH;

	public ViewTableScrollAble(Context context) {
		super(context);
		isScrollable = true;
		dm = getResources().getDisplayMetrics();
		data = new ScrollTableData();
		// default config
		data.colorTitleColumn = Color.CYAN;
		data.colorTitleBinge = Color.YELLOW;
		data.textColorTitleBinge = Color.BLACK;
		data.colorTitleRow = Color.WHITE;
		data.colorBorder = Color.BLACK;
		data.colorContent = getResources().getColor(android.R.color.transparent);
		data.textColorTitle = Color.BLACK;
		data.textColorData = Color.BLACK;
		data.textSizeTitle = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_TITLE, dm);
		data.textSizeData = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_DATA, dm);
		data.titleColumnPaddingV = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, dm);
		data.titleColumnPaddingH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, dm);
		data.titleRowPaddingV = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, dm);
		data.setBorderSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, dm));
	}

	public ViewTableScrollAble(Context context, AttributeSet attrs) {
		super(context, attrs);

		// 读取配置
		isScrollable = true;
		dm = getResources().getDisplayMetrics();
		data = new ScrollTableData();
		UtilLayoutAttribute ua = new UtilLayoutAttribute(context, attrs);
		data.colorTitleColumn = ua.getColorAttribute(ua.getNSXiaoTian(), "backgroundColorTitleColumn", Color.WHITE);
		data.colorTitleBinge = ua.getColorAttribute(ua.getNSXiaoTian(), "backgroundColorTitleBinge", Color.WHITE);
		data.textColorTitleBinge = ua.getColorAttribute(ua.getNSXiaoTian(), "textColorBinge", Color.BLACK);
		data.colorTitleRow = ua.getColorAttribute(ua.getNSXiaoTian(), "backgroundColorTitleRow", Color.WHITE);
		data.colorBorder = ua.getColorAttribute(ua.getNSXiaoTian(), "borderColor", Color.BLACK);
		data.colorContent = ua.getColorAttribute(ua.getNSXiaoTian(), "backgroundColorTable", Color.WHITE);
		data.textColorTitle = ua.getColorAttribute(ua.getNSXiaoTian(), "textColorTitleColumn", Color.BLACK);
		data.textColorData = ua.getColorAttribute(ua.getNSXiaoTian(), "textColorTable", Color.BLACK);
		data.textSizeTitle = ua.getDimension(ua.getNSXiaoTian(), "textSizeTitle", TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_TITLE, dm));
		data.textSizeData = ua.getDimension(ua.getNSXiaoTian(), "textSizeTable", TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_DATA, dm));
		data.titleColumnPaddingV = (int) ua.getDimension(ua.getNSXiaoTian(), "textPaddingTopColumn", TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, dm));
		data.titleColumnPaddingH = (int) ua.getDimension(ua.getNSXiaoTian(), "textPaddingLeftColumn", TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, dm));
		data.titleRowPaddingV = (int) ua.getDimension(ua.getNSXiaoTian(), "textPaddingTopRow", TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, dm));
		data.setBorderSize(ua.getDimension(ua.getNSXiaoTian(), "borderWidth", TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, dm)));
		// UI
		View root = LayoutInflater.from(context).inflate(R.layout.model_view_table_scrollable_xiaotian, this);
		table = (ViewTable) root.findViewById(R.id.id_5);
		scrollViewV = (ScrollView) root.findViewById(R.id.id_3);
		titleBingeTopLeft = (ViewTopLeft) root.findViewById(R.id.id_0);
		titleRowView = (TitleViewRow) root.findViewById(R.id.id_2);
		titleColumnView = (TitleViewColumn) root.findViewById(R.id.id_1);
		scrollViewH = (HorizontalScrollView) root.findViewById(R.id.id_4);
		titleBingeTopLeft.setParent(this);
		titleColumnView.setParent(this);
		titleRowView.setParent(this);
		table.setParent(this);
		scrollViewV.setScrollable(isScrollable);
		scrollViewH.setScrollable(isScrollable);
		// Scroll View Listener
		scrollViewV.setScrollListener(new ScrollViewListener() {
			@Override
			public void onScrollChanged(int x, int y, int oldx, int oldy) {
				// 判断水平滑动
				if (scrollViewH.isScrollable && Math.abs(oldy - y) > 5) {
					scrollViewH.setScrollable(false);
				}
				titleRowView.scrollTo(x, y);
				if (scrollListener != null) {
					scrollListener.onScrollChanged(x, y, oldx, oldy);
				}
			}

			@Override
			public void onScrollFinish() {
				scrollViewH.setScrollable(true);
				scrollViewV.setScrollable(true);
				if (scrollListener != null) scrollListener.onScrollFinish();
			}
		});
		scrollViewH.setScrollListener(new ScrollViewListener() {
			@Override
			public void onScrollChanged(int x, int y, int oldx, int oldy) {
				// 判断水平滑动
				if (scrollViewV.isScrollable && Math.abs(oldx - x) > 5) {
					scrollViewV.setScrollable(false);
				}
				titleColumnView.scrollTo(x, y);
				if (scrollListener != null) {
					scrollListener.onScrollChanged(x, y, oldx, oldy);
				}
			}

			@Override
			public void onScrollFinish() {
				scrollViewV.setScrollable(true);
				scrollViewH.setScrollable(true);
				if (scrollListener != null) scrollListener.onScrollFinish();
			}
		});
		// Apply Config
		invalidate(); // update view call ondraw
	}

	public void notifyDatasetChange() {
		// Config
		data.titleRow = getViewTableTitleRow();
		data.titleColumn = getViewTableTitleColumn();
		data.tableData = getViewTableTableData();
		data.titleRowHeight = new int[data.titleRow.size() + 1];
		data.titleColumnWidth = new int[data.titleColumn.size() + 1];
		//
		removeAllViewsInLayout();
		View root = LayoutInflater.from(getContext()).inflate(R.layout.model_view_table_scrollable_xiaotian, this);
		table = (ViewTable) root.findViewById(R.id.id_5);
		scrollViewV = (ScrollView) root.findViewById(R.id.id_3);
		titleBingeTopLeft = (ViewTopLeft) root.findViewById(R.id.id_0);
		titleRowView = (TitleViewRow) root.findViewById(R.id.id_2);
		titleColumnView = (TitleViewColumn) root.findViewById(R.id.id_1);
		scrollViewH = (HorizontalScrollView) root.findViewById(R.id.id_4);
		titleBingeTopLeft.setParent(this);
		titleColumnView.setParent(this);
		titleRowView.setParent(this);
		table.setParent(this);
		scrollViewV.setScrollable(isScrollable);
		scrollViewH.setScrollable(isScrollable);
		// Scroll View Listener
		scrollViewV.setScrollListener(new ScrollViewListener() {
			@Override
			public void onScrollChanged(int x, int y, int oldx, int oldy) {
				// 判断水平滑动
				if (scrollViewH.isScrollable && Math.abs(oldy - y) > 5) {
					scrollViewH.setScrollable(false);
				}
				titleRowView.scrollTo(x, y);
				if (scrollListener != null) {
					scrollListener.onScrollChanged(x, y, oldx, oldy);
				}
			}

			@Override
			public void onScrollFinish() {
				scrollViewH.setScrollable(true);
				scrollViewV.setScrollable(true);
				if (scrollListener != null) scrollListener.onScrollFinish();
			}
		});
		scrollViewH.setScrollListener(new ScrollViewListener() {
			@Override
			public void onScrollChanged(int x, int y, int oldx, int oldy) {
				// 判断水平滑动
				if (scrollViewV.isScrollable && Math.abs(oldx - x) > 5) {
					scrollViewV.setScrollable(false);
				}
				titleColumnView.scrollTo(x, y);
				if (scrollListener != null) {
					scrollListener.onScrollChanged(x, y, oldx, oldy);
				}
			}

			@Override
			public void onScrollFinish() {
				scrollViewV.setScrollable(true);
				scrollViewH.setScrollable(true);
				if (scrollListener != null) scrollListener.onScrollFinish();
			}
		});
		// Apply Config
		invalidate(); // update view call ondraw
	}

	public void setDataSource(ViewTableDataSource ds) {
		this.dataSource = ds;
	}

	@Override
	public List<String> getViewTableTitleRow() {
		return dataSource.getViewTableTitleRow();
	}

	@Override
	public List<String> getViewTableTitleColumn() {
		return dataSource.getViewTableTitleColumn();
	}

	@Override
	public SparseArray<String> getViewTableTableData() {
		return dataSource.getViewTableTableData();
	}

	public ScrollTableData getConfigData() {
		// 配置不变,更新DS绑定的数据源数据
		if (data.titleColumn == null || data.titleRow == null) {
			data.titleRow = getViewTableTitleRow();
			data.tableData = getViewTableTableData();
			data.titleColumn = getViewTableTitleColumn();
			data.titleRowHeight = new int[data.titleRow.size() + 1];
			data.titleColumnWidth = new int[data.titleColumn.size() + 1];
		}
		return data;
	}

	public boolean isScrollable() {
		return isScrollable;
	}

	public void setScrollable(boolean isScrollable) {
		this.isScrollable = isScrollable;
	}

	public void setScrollListener(ScrollViewListener scrollListener) {
		this.scrollListener = scrollListener;
	}

	public static int getKey(int column, int row) {
		return Integer.parseInt(String.format(Locale.getDefault(), "%1$d%2$d", column, row));
	}

	public void setTopLeftText(String text) {
		this.data.topleftTitle = text;
		titleBingeTopLeft.setText(text);
	}

	// inner class table ( simple no title [H.V])
	// 内部类,只有用static修饰才能实例化,不然要通过父类实例来实例化内部类
	// [public static class 内部静态类:可直接实例化,静态实例化非通过实例对象实例化]
	public static class ViewTable extends View {
		private ViewTableScrollAble tableParent;
		private ScrollTableData data;
		float baseLineDistance;
		private String text;
		private Paint paint;

		public ViewTable(Context context, AttributeSet attrs) {
			super(context, attrs);
			paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		}

		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			data = tableParent.getConfigData();
			int width = 0;
			int height = 0;
			for (int i = 1; i < data.titleColumnWidth.length; i++) {
				width += data.titleColumnWidth[i] + data.borderSize;
			}
			for (int i = 1; i < data.titleRowHeight.length; i++) {
				height += data.titleRowHeight[i] + data.borderSize;
			}
			paint.setStyle(Style.FILL);
			paint.setTextAlign(Align.CENTER);
			paint.setTextSize(data.textSizeData);
			paint.setStrokeWidth(data.borderSize);
			paint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
			baseLineDistance = (paint.ascent() + paint.descent()) / 2;
			setMeasuredDimension(width, height);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			int baseLeft = 0;
			int baseTop = 0;
			for (int row = 0; row < data.titleRow.size(); row++) {
				// a row
				for (int column = 0; column < data.titleColumn.size(); column++) {
					// background
					paint.setColor(data.colorContent);
					paint.setStyle(Style.FILL);
					canvas.drawRect(baseLeft, baseTop, baseLeft + data.titleColumnWidth[column + 1], baseTop + data.titleRowHeight[row + 1], paint);
					// text
					text = data.tableData.get(getKey(column, row));
					paint.setColor(data.textColorData);
					canvas.drawText(text == null ? "" : text, baseLeft + data.titleColumnWidth[column + 1] / 2, baseTop + data.titleRowHeight[row + 1] / 2 - baseLineDistance, paint);
					baseLeft += data.titleColumnWidth[column + 1];

					// border right / bottom
					paint.setColor(data.colorBorder);
					paint.setStyle(Style.STROKE);
					canvas.drawLines(new float[] { baseLeft + data.borderM, baseTop, baseLeft + data.borderM, baseTop + data.titleRowHeight[row + 1], baseLeft + data.borderSize,
							baseTop + data.titleRowHeight[row + 1] + data.borderM, baseLeft - data.titleColumnWidth[column + 1], baseTop + data.titleRowHeight[row + 1] + data.borderM }, paint);
					baseLeft += data.borderSize;
					text = null;
				}
				baseLeft = 0;
				baseTop += data.titleRowHeight[row + 1] + data.borderSize;
			}
		}

		public void setParent(ViewTableScrollAble tableParent) {
			this.tableParent = tableParent;
		}
	}

	// 行表头
	public static class TitleViewColumn extends View {
		private ViewTableScrollAble tableParent;
		private ScrollTableData data;
		float baseLineDistance;
		private String text;
		private Paint paint;
		private Rect rect;

		public TitleViewColumn(Context context, AttributeSet attrs) {
			super(context, attrs);
			rect = new Rect();
			paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			data = tableParent.getConfigData();
			int width = 0;
			paint.setStyle(Style.FILL);
			paint.setTextAlign(Align.CENTER);
			paint.setTextSize(data.textSizeTitle);
			paint.setStrokeWidth(data.borderSize);
			paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
			baseLineDistance = (paint.ascent() + paint.descent()) / 2;
			// 计算画布大小
			for (int i = 0; i < data.titleColumn.size(); i++) {
				paint.getTextBounds(data.titleColumn.get(i), 0, data.titleColumn.get(i).length(), rect);
				data.titleColumnWidth[i + 1] = rect.width() + data.titleColumnPaddingH * 2;
				width += data.titleColumnWidth[i + 1] + data.borderSize;
			}

			// 确认画布大小
			setMeasuredDimension(data.borderSize + width, data.borderSize * 2 + data.titleRowHeight[0]);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			// border
			int baseLeft = 0;
			int baseTop = data.borderSize;
			for (int i = 0; i < data.titleColumn.size(); i++) {
				// background
				paint.setColor(data.colorTitleColumn);
				paint.setStyle(Style.FILL);
				canvas.drawRect(baseLeft, baseTop, baseLeft + data.titleColumnWidth[i + 1], baseTop + data.titleRowHeight[0], paint);
				// text
				text = data.titleColumn.get(i);
				paint.setColor(data.textColorTitle);
				canvas.drawText(text, baseLeft + data.titleColumnWidth[i + 1] / 2, baseTop + data.titleRowHeight[0] / 2 - baseLineDistance, paint);
				baseLeft += data.titleColumnWidth[i + 1];
				// border
				paint.setColor(data.colorBorder);
				paint.setStyle(Style.STROKE);
				canvas.drawLines(new float[] { baseLeft - data.titleColumnWidth[i], data.borderM, baseLeft, data.borderM, baseLeft + data.borderM, 0, baseLeft + data.borderM,
						baseTop + data.titleRowHeight[0], baseLeft - data.titleColumnWidth[i + 1], baseTop + data.titleRowHeight[0] + data.borderM, baseLeft + data.borderSize,
						baseTop + data.titleRowHeight[0] + data.borderM }, paint);
				baseLeft += data.borderSize;
				text = null;
			}
		}

		public void setParent(ViewTableScrollAble tableParent) {
			this.tableParent = tableParent;
		}
	}

	// 列表头
	public static class TitleViewRow extends View {
		private ViewTableScrollAble tableParent;
		private ScrollTableData data;
		private float baseLineDistance;
		private String text;
		private Paint paint;
		private Rect rect;

		public TitleViewRow(Context context, AttributeSet attrs) {
			super(context, attrs);
			paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			rect = new Rect();
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			data = tableParent.getConfigData();
			int height = 0;
			paint.setStyle(Style.FILL);
			paint.setTextAlign(Align.CENTER);
			paint.setTextSize(data.textSizeTitle);
			paint.setStrokeWidth(data.borderSize);
			paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
			baseLineDistance = (paint.ascent() + paint.descent()) / 2;
			for (int i = 0; i < data.titleRow.size(); i++) {
				paint.getTextBounds(data.titleRow.get(i), 0, 1, rect);
				data.titleRowHeight[i + 1] = rect.height() + data.titleRowPaddingV * 2;
				height += data.titleRowHeight[i + 1] + data.borderSize;
			}
			setMeasuredDimension(data.borderSize * 2 + data.titleColumnWidth[0], height);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			int baseLeft = data.borderSize; // 内容Content Base
			int baseTop = 0;
			for (int i = 0; i < data.titleRow.size(); i++) {
				// background
				paint.setColor(data.colorTitleRow);
				paint.setStyle(Style.FILL);
				canvas.drawRect(baseLeft, baseTop, baseLeft + data.titleColumnWidth[0], baseTop + data.titleRowHeight[i + 1], paint);
				// text
				text = data.titleRow.get(i);
				paint.setColor(data.textColorTitle);
				canvas.drawText(text, baseLeft + data.titleColumnWidth[0] / 2, baseTop + data.titleRowHeight[i + 1] / 2 - baseLineDistance, paint);
				baseTop += data.titleRowHeight[i + 1];
				// bottom border
				paint.setColor(data.colorBorder);
				paint.setStyle(Style.STROKE);
				canvas.drawLine(data.borderSize, baseTop + data.borderM, data.borderSize + data.titleColumnWidth[0], baseTop + data.borderM, paint);
				baseTop += data.borderSize;
				text = null;
			}
			// left/bottom/right border
			paint.setColor(data.colorBorder);
			paint.setStyle(Style.STROKE);
			int h = getHeight();
			canvas.drawLines(new float[] { data.borderM, 0, data.borderM, h, data.borderSize + data.titleColumnWidth[0] + data.borderM, 0, data.borderSize + data.titleColumnWidth[0] + data.borderM,
					h, }, paint);

		}

		public void setParent(ViewTableScrollAble tableParent) {
			this.tableParent = tableParent;
		}
	}

	public static class ViewTopLeft extends View {
		private ViewTableScrollAble tableParent;
		private ScrollTableData data;
		float baseLineDistance;
		private String text;
		private Paint paint;
		private Rect rect;

		public ViewTopLeft(Context context, AttributeSet attrs) {
			super(context, attrs);
			paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			rect = new Rect();
			text = "";
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			float x = data.borderSize + data.titleColumnWidth[0] / 2;
			float y = data.borderSize + data.titleRowHeight[0] / 2 - baseLineDistance;
			paint.setColor(data.textColorTitleBinge);
			paint.setStyle(Style.FILL);
			canvas.drawText(text, x, y, paint);
			// border
			paint.setColor(data.colorBorder);
			paint.setStyle(Style.STROKE);
			canvas.drawLines(new float[] { 0, data.borderM, data.borderSize + data.titleColumnWidth[0], data.borderM, data.borderM, data.borderSize, data.borderM,
					data.borderSize + data.titleRowHeight[0], data.borderSize + data.titleColumnWidth[0] + data.borderM, 0, data.borderSize + data.titleColumnWidth[0] + data.borderM,
					data.borderSize + data.titleRowHeight[0], 0, data.borderSize + data.titleRowHeight[0] + data.borderM, data.borderSize + data.borderSize + data.titleColumnWidth[0],
					data.borderSize + data.titleRowHeight[0] + data.borderM }, paint);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			// Measure View Size
			data = tableParent.getConfigData();
			setBackgroundColor(data.colorTitleBinge);
			int height = data.borderSize;
			int width = data.borderSize;
			paint.setStyle(Style.FILL);
			paint.setTextAlign(Align.CENTER);
			paint.setTextSize(data.textSizeTitle);
			paint.setStrokeWidth(data.borderSize);
			paint.getTextBounds(text, 0, text.length(), rect);
			paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
			baseLineDistance = (paint.ascent() + paint.descent()) / 2;
			// [0,0]
			data.titleColumnWidth[0] = rect.width() + data.titleColumnPaddingH * 2;
			data.titleRowHeight[0] = rect.height() + data.titleColumnPaddingV * 2;
			width += data.titleColumnWidth[0];
			height += data.titleRowHeight[0];
			setMeasuredDimension(data.borderSize + width, data.borderSize + height);
		}

		public void setText(String text) {
			this.text = text;
			invalidate();
		}

		public void setParent(ViewTableScrollAble tableParent) {
			this.tableParent = tableParent;
		}
	}

	public static class HorizontalScrollView extends android.widget.HorizontalScrollView {
		private boolean isScrollable;
		private ScrollViewListener listener;

		public HorizontalScrollView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// if we can scroll pass the event to the superclass
				if (isScrollable) {
					return super.onTouchEvent(ev);
				} else {
					// only continue to handle the touch event if scrolling
					// enabled
					return isScrollable; // mScrollable is always false at this
											// point
				}
				// 放开手时回调
			case MotionEvent.ACTION_UP:
				if (listener != null) listener.onScrollFinish();
			default:
				return super.onTouchEvent(ev);
			}
		}

		@Override
		public boolean onInterceptTouchEvent(MotionEvent ev) {
			// Don't do anything with intercepted touch events if
			// we are not scrollable
			if (!isScrollable) {
				return false;
			} else {
				return super.onInterceptTouchEvent(ev);
			}
		}

		@Override
		protected void onScrollChanged(int l, int t, int oldl, int oldt) {
			if (listener != null) listener.onScrollChanged(l, t, oldl, oldt);
		}

		public boolean isScrollable() {
			return isScrollable;
		}

		public void setScrollable(boolean isScrollable) {
			this.isScrollable = isScrollable;
		}

		public void setScrollListener(ScrollViewListener listener) {
			this.listener = listener;
		}
	}

	public static class ScrollView extends android.widget.ScrollView {
		private boolean isScrollable;
		private ScrollViewListener listener;

		public ScrollView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (isScrollable) {
					return super.onTouchEvent(ev);
				} else {
					return isScrollable;
				}
				// 放开手时回调
			case MotionEvent.ACTION_UP:
				if (listener != null) listener.onScrollFinish();
			default:
				return super.onTouchEvent(ev);
			}
		}

		@Override
		public boolean onInterceptTouchEvent(MotionEvent ev) {
			if (!isScrollable) {
				return false;
			} else {
				return super.onInterceptTouchEvent(ev);
			}
		}

		@Override
		protected void onScrollChanged(int l, int t, int oldl, int oldt) {
			if (listener != null) listener.onScrollChanged(l, t, oldl, oldt);
		}

		public boolean isScrollable() {
			return isScrollable;
		}

		public void setScrollable(boolean isScrollable) {
			this.isScrollable = isScrollable;
		}

		public void setScrollListener(ScrollViewListener listener) {
			this.listener = listener;
		}

	}

	// table 配置信息
	public static class ScrollTableData {
		public List<String> titleRow;
		public List<String> titleColumn;
		public SparseArray<String> tableData;
		public int[] titleColumnWidth;
		public int[] titleRowHeight;
		public String topleftTitle;
		public int borderSize;
		public int colorBorder;
		public int colorContent;
		public int textColorData;
		public int colorTitleRow;
		public int textColorTitle;
		public float textSizeData;
		public int colorTitleBinge;
		public float textSizeTitle;
		public int colorTitleColumn;
		public int textColorTitleBinge;
		public int titleColumnPaddingV;
		public int titleColumnPaddingH;
		public int titleRowPaddingV;
		public float borderM;
		public float contentWidth;
		public float contentHeight;

		public void setBorderSize(float borderSize) {
			this.borderM = borderSize / 2f;
			this.borderSize = (int) borderSize;
		}
	}

	public static interface ScrollViewListener {
		public void onScrollChanged(int l, int t, int oldl, int oldt);

		public void onScrollFinish();
	}
}
