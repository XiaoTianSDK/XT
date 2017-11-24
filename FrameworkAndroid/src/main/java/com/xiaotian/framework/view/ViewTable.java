package com.xiaotian.framework.view;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;

import com.xiaotian.framework.R;
import com.xiaotian.framework.common.Mylog;
import com.xiaotian.framework.util.UtilLayoutAttribute;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name ViewTable
 * @description 画带border边界的表格Table
 * @date 2013-10-21
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
public class ViewTable extends View {
	private static final int TEXT_SIZE_TITLE = 18;
	private static final int TEXT_SIZE_DATA = 16;

	private List<String> titleRow;
	private List<String> titleColumn;
	private SparseArray<String> tableData;
	private int[] titleRowHeight;
	private int[] titleColumnWidth;

	private Rect rect;
	private Paint paint;
	private int borderSize;
	private int colorBorder;
	private int colorContent;
	private int textColorData;
	private int colorTitleRow;
	private int textColorTitle;
	private float textSizeData;
	private float textSizeTitle;
	private int colorTitleColumn;
	private int titleColumnPaddingV;
	private int titleColumnPaddingH;
	private ViewTableDataSource ds;
	private int titleRowPaddingV;
	private DisplayMetrics dm;

	public ViewTable(Context context) {
		super(context);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		rect = new Rect();
		colorTitleColumn = Color.CYAN;
		colorTitleRow = Color.WHITE;
		colorBorder = Color.BLACK;
		colorContent = getResources().getColor(android.R.color.transparent);
		colorContent = Color.YELLOW;
		textColorTitle = Color.BLACK;
		textColorData = Color.BLACK;
		dm = getResources().getDisplayMetrics();
		textSizeTitle = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_TITLE, dm);
		textSizeData = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_DATA, dm);
		titleColumnPaddingV = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, dm);
		titleColumnPaddingH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, dm);
		titleRowPaddingV = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, dm);
		borderSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, dm);
	}

	public ViewTable(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		dm = getResources().getDisplayMetrics();
		rect = new Rect();
		//
		UtilLayoutAttribute ua = new UtilLayoutAttribute(context, attrs);
		colorTitleColumn = ua.getColorAttribute(ua.getNSXiaoTian(), "backgroundColorTitleColumn", Color.WHITE);
		colorTitleRow = ua.getColorAttribute(ua.getNSXiaoTian(), "backgroundColorTitleRow", Color.WHITE);
		colorBorder = ua.getColorAttribute(ua.getNSXiaoTian(), "borderColor", Color.BLACK);
		colorContent = ua.getColorAttribute(ua.getNSXiaoTian(), "backgroundColorTable", Color.WHITE);
		textColorTitle = ua.getColorAttribute(ua.getNSXiaoTian(), "textColorTitleColumn", Color.BLACK);
		textColorData = ua.getColorAttribute(ua.getNSXiaoTian(), "textColorTable", Color.BLACK);
		textSizeTitle = (int) ua.getDimension(ua.getNSXiaoTian(), "textSizeTitle", (float) textSizeTitle);
		textSizeData = (int) ua.getDimension(ua.getNSXiaoTian(), "textSizeTable", (float) textSizeData);
		titleColumnPaddingV = (int) ua.getDimension(ua.getNSXiaoTian(), "textPaddingTopColumn", (float) titleColumnPaddingV);
		titleColumnPaddingH = (int) ua.getDimension(ua.getNSXiaoTian(), "textPaddingLeftColumn", (float) titleColumnPaddingH);
		titleRowPaddingV = (int) ua.getDimension(ua.getNSXiaoTian(), "textPaddingTopRow", (float) titleRowPaddingV);
		borderSize = (int) ua.getDimension(ua.getNSXiaoTian(), "borderWidth", (float) borderSize);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (ds == null) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			return;
		}
		int width = borderSize;
		int height = borderSize;
		this.titleRow = ds.getViewTableTitleRow();
		this.titleColumn = ds.getViewTableTitleColumn();
		this.tableData = ds.getViewTableTableData();
		titleColumnWidth = new int[titleColumn.size()];
		titleRowHeight = new int[titleRow.size() + 1];
		String string;
		// Column Width
		paint.setTextSize(textSizeTitle);
		paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		for (int i = 0; i < titleColumn.size(); i++) {
			string = titleColumn.get(i);
			paint.getTextBounds(string, 0, string.length(), rect);
			titleColumnWidth[i] = rect.width() + titleColumnPaddingH * 2;
			width += titleColumnWidth[i] + borderSize;
			if (i == 0 && titleRowHeight[0] == 0) {
				titleRowHeight[0] = rect.height() + titleColumnPaddingV * 2;
				height += titleRowHeight[0] + borderSize;
			}
			string = null;
		}
		// Row Height
		for (int i = 0; i < titleRow.size(); i++) {
			string = titleRow.get(i);
			paint.getTextBounds(string, 0, 1, rect);
			titleRowHeight[i + 1] = rect.height() + titleRowPaddingV * 2;
			height += titleRowHeight[i + 1] + borderSize;
			string = null;
		}
		// Set Content size;
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int baseLeft = borderSize;
		int baseTop = borderSize;
		float borderM = borderSize / 2f;
		float left, top, width, height;
		float baseLineDistance;
		String string;
		// title Column
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(textSizeTitle);
		paint.setStrokeWidth(borderSize);
		paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		baseLineDistance = (paint.descent() + paint.ascent()) / 2; // 字体基线距离
		for (int i = 0; i < titleColumn.size();) {
			string = titleColumn.get(i);
			paint.setStyle(Paint.Style.FILL);
			paint.getTextBounds(string, 0, string.length(), rect);
			// background
			paint.setColor(colorTitleColumn);
			width = baseLeft + titleColumnWidth[i];
			height = baseTop + titleRowHeight[0];
			canvas.drawRect(baseLeft, baseTop, width, height, paint);
			// Text
			paint.setColor(textColorTitle);
			left = baseLeft + titleColumnWidth[i] / 2f; // 文字横向中点
			top = baseTop + titleRowHeight[0] / 2f - baseLineDistance; // 文字纵向中点-基线
			canvas.drawText(string, left, top, paint); // 画Text:x对齐点,y字体基线对齐点
			baseLeft += titleColumnWidth[i];
			// border
			if (++i < titleColumn.size()) {
				paint.setColor(colorBorder);
				paint.setStyle(Paint.Style.STROKE);
				canvas.drawLine(baseLeft + borderM, baseTop, baseLeft + borderM, baseTop + titleRowHeight[0], paint);
				baseLeft += borderSize;
			}
		}
		baseLeft = borderSize;
		baseTop = borderSize + titleRowHeight[0];
		// title Data
		for (int i = 0; i < titleRow.size(); i++) {
			// top line
			paint.setColor(colorBorder);
			paint.setStrokeWidth(borderSize);
			paint.setStyle(Paint.Style.STROKE);
			width = getWidth() - borderSize;
			canvas.drawLine(baseLeft, baseTop + borderM, width, baseTop + borderM, paint);
			left = baseLeft;
			baseTop += borderSize;
			//
			string = titleRow.get(i);
			paint.setStyle(Paint.Style.FILL);
			paint.setTextSize(textSizeTitle);
			paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
			baseLineDistance = (paint.descent() + paint.ascent()) / 2;
			paint.getTextBounds(string, 0, string.length(), rect);
			// row title background
			paint.setColor(colorTitleRow);
			width = left + titleColumnWidth[0];
			height = baseTop + titleRowHeight[i + 1];
			canvas.drawRect(left, baseTop, width, height, paint);
			// row title
			left = baseLeft + titleColumnWidth[0] / 2;
			top = baseTop + titleRowHeight[i + 1] / 2 - baseLineDistance;
			paint.setColor(textColorTitle);
			canvas.drawText(string, left, top, paint);
			baseLeft += titleColumnWidth[0];
			paint.setTextSize(textSizeData);
			paint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
			baseLineDistance = (paint.descent() + paint.ascent()) / 2;
			for (int j = 0; j < titleColumn.size(); j++) {
				// border
				paint.setColor(colorBorder);
				paint.setStrokeWidth(borderSize);
				paint.setStyle(Paint.Style.STROKE);
				canvas.drawLine(baseLeft + borderM, baseTop, baseLeft + borderM, baseTop + titleRowHeight[i + 1], paint);
				baseLeft += borderSize;
				// background
				paint.setColor(colorContent);
				paint.setStyle(Paint.Style.FILL);
				canvas.drawRect(baseLeft, baseTop, baseLeft + titleColumnWidth[j + 1], baseTop + titleRowHeight[i + 1], paint);
				// data
				string = tableData.get(getKey(j, i));
				paint.setColor(textColorData);
				paint.getTextBounds(string, 0, string.length(), rect);
				left = baseLeft + (titleColumnWidth[j + 1]) / 2;
				top = baseTop + titleRowHeight[i + 1] / 2 - baseLineDistance;
				canvas.drawText(string, left, top, paint);
				baseLeft += titleColumnWidth[j + 1];
			}
			baseLeft = borderSize;
			baseTop += titleRowHeight[i + 1];
		}
		// 外边框
		paint.setColor(colorBorder);
		paint.setStrokeWidth(borderSize);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(borderM, borderM, getWidth() - borderM, getHeight() - borderM, paint);
	}

	public int getKey(int column, int row) {
		return Integer.parseInt(String.format(Locale.getDefault(), "%1$d%2$d", column, row));
	}

	public void setDataSource(ViewTableDataSource ds) {
		this.ds = ds;
	}

	// 数据源接口
	public interface ViewTableDataSource {
		public List<String> getViewTableTitleRow();

		public List<String> getViewTableTitleColumn();

		public SparseArray<String> getViewTableTableData();
	}
}
