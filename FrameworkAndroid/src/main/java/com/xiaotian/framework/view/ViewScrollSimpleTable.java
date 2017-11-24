package com.xiaotian.framework.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.xiaotian.framework.R;
import com.xiaotian.framework.common.Mylog;
import com.xiaotian.framework.view.listener.ViewScrollViewListener;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name ViewScrollSimpleTable
 * @description 表格控件,可滚动,添加,表格模式[行颜色布局]
 * @date 2014-1-25
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2009-2014 广州睿塔科技 Ltd, All Rights Reserved.
 */
public class ViewScrollSimpleTable extends LinearLayout {
	public static final int LISTENER_TYPE_CODE = 0x001;
	public static final int LISTENER_TYPE_COLOR = 0x002;
	public static final int LISTENER_TYPE_DATA = 0x003;
	//
	protected int background;
	protected int dimension_3;
	protected int colorEditText;
	protected int colorTableFirst;
	protected int colorTableSecond;
	protected List<String> listRow;
	protected int colorEditTextHint;
	protected BaseAdapter adapterCode;
	protected List<String> listColumn;
	protected SparseArray<String> arrayData;
	protected List<String> listCodeSelector;
	protected SparseArray<Object> layoutParamBorderRow;
	protected SparseArray<Object> layoutParamBorderData;
	protected SparseArray<Object> layoutParamBorderColumn;
	protected ViewHorizontalScrollView scrollViewH;
	protected OnDataChangeListener changeListenerQuantity;
	protected OnDataChangeListener changeListenerCode;
	protected ViewScrollViewListener scrollListener;
	protected boolean updateTableData;
	protected Runnable notifyMessage;
	protected DataSource dataSource;
	protected String hintQuantity;
	protected int totalQuantity;
	protected String hintColor;
	protected String hintCode;
	protected int borderColor;
	protected Context context;
	// UI
	protected LinearLayout linearTitleV;
	protected TableRow tableTitleH;
	protected TableLayout table;

	public ViewScrollSimpleTable(Context context) {
		super(context);
		this.context = context;
		initializdingTable(context);
	}

	public ViewScrollSimpleTable(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initializdingTable(context);
	}

	protected void initializdingTable(Context context) {
		hintColor = "color";
		hintCode = "code";
		// Data
		arrayData = new SparseArray<String>();
		listColumn = new ArrayList<String>();
		listRow = new ArrayList<String>();
		hintQuantity = "hint";
		dimension_3 = getResources().getDimensionPixelSize(R.dimen.dimen_3);
		colorTableFirst = getResources().getColor(android.R.color.transparent);
		colorTableSecond = getResources().getColor(android.R.color.transparent);
		colorEditTextHint = getResources().getColor(android.R.color.transparent);
		colorEditText = getResources().getColor(android.R.color.transparent);
		background = android.R.color.transparent;
		// UI
		View root = LayoutInflater.from(context).inflate(R.layout.view_scroll_table_simple_template, null);
		linearTitleV = (LinearLayout) root.findViewById(R.id.id_e);
		tableTitleH = (TableRow) root.findViewById(R.id.id_h);
		scrollViewH = (ViewHorizontalScrollView) root.findViewById(R.id.id_f);
		root.findViewById(R.id.id_i0).setTag(R.id.id_parent, this);
		root.findViewById(R.id.id_i1).setTag(R.id.id_parent, this);
		table = (TableLayout) root.findViewById(R.id.id_g);
		addView(root, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		tableTitleH.post(new Runnable() {
			@Override
			public void run() {
				int height = tableTitleH.getMeasuredHeight();
				View v = linearTitleV.getChildAt(0);
				LayoutParams params = (LayoutParams) v.getLayoutParams();
				params.height = height;
				v.setLayoutParams(params);
			}
		});
		linearTitleV.post(new Runnable() {
			@Override
			public void run() {
				int width = linearTitleV.getMeasuredWidth();
				tableTitleH.setMinimumWidth(getResources().getDisplayMetrics().widthPixels - width);
				ImageButton add = (ImageButton) linearTitleV.getChildAt(linearTitleV.getChildCount() - 1);
				add.setBackgroundColor(colorTableSecond);
				TableRow row = getTableRow(0);
				row.setBackgroundColor(colorTableSecond);
				add.measure(0, 0);
				row.setMinimumHeight(add.getMeasuredHeight());
			}
		});
		scrollViewH.setScrollListener(new ViewScrollViewListener() {
			@Override
			public void onScrollChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
				if (scrollListener != null) scrollListener.onScrollChanged(paramInt1, paramInt2, paramInt3, paramInt4);
			}

			@Override
			public void onScrollFinish() {
				if (scrollListener != null) scrollListener.onScrollFinish();
			}

			@Override
			public boolean onTouchEvent(MotionEvent ev) {
				if (scrollListener != null) scrollListener.onTouchEvent(ev);
				switch (ev.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (scrollListener != null) scrollListener.onTouchDown(ev);
					break;
				}
				return super.onTouchEvent(ev);
			}

		});
	}

	public void addNewRow(String code) {
		// TODO 添加一行::行表头
		LinearLayout container;
		int rowIndex = listRow.size();
		listRow.add(code);
		int bgColor = rowIndex % 2 == 0 ? colorTableSecond : colorTableFirst;
		int width = linearTitleV.getMeasuredWidth();
		int height = tableTitleH.getMeasuredHeight();
		container = createEditText(code, hintCode, width, height, Gravity.CENTER, LISTENER_TYPE_CODE);
		container.setTag(R.id.id_position, rowIndex);
		container.setBackgroundColor(bgColor);
		linearTitleV.addView(container, linearTitleV.getChildCount() - 1);
		TableRow row = getTableRow(rowIndex);
		row.setBackgroundColor(bgColor);
		row.setMinimumHeight(height);
		width = getResources().getDimensionPixelSize(R.dimen.dimen_20) * 4;
		for (int i = 0; i < listColumn.size(); i++) {
			row.addView(createEditText("", hintQuantity, width, height, Gravity.LEFT, LISTENER_TYPE_DATA, i, rowIndex));
		}
		bgColor = ++rowIndex % 2 == 0 ? colorTableSecond : colorTableFirst;
		ImageButton add = (ImageButton) linearTitleV.getChildAt(linearTitleV.getChildCount() - 1);
		add.setBackgroundColor(bgColor);
		row = getTableRow(rowIndex);
		row.setMinimumHeight(add.getHeight());
		row.setBackgroundColor(bgColor);
	}

	public void addNewColumn(String color) {
		LinearLayout container;
		int columnIndex = listColumn.size();
		listColumn.add(color);
		int width = getResources().getDimensionPixelSize(R.dimen.dimen_20) * 4;
		int height = tableTitleH.getMeasuredHeight();
		container = createEditText(color, hintColor, width, height, Gravity.CENTER, LISTENER_TYPE_COLOR);
		container.setTag(R.id.id_position, columnIndex);
		TableRow.LayoutParams params = new TableRow.LayoutParams(width, height);
		tableTitleH.addView(container, tableTitleH.getChildCount() - 1, params);
		for (int rowIndex = 0; rowIndex < listRow.size(); rowIndex++) {
			container = createEditText("", hintQuantity, width, height, Gravity.LEFT, LISTENER_TYPE_DATA, columnIndex,
					rowIndex);
			getTableRow(rowIndex).addView(container);
		}
	}

	protected TableRow getTableRow(int index) {
		TableRow row = null;
		if (table.getChildCount() < index + 2) {
			row = new TableRow(context);
			TableLayout.LayoutParams tbParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
					TableLayout.LayoutParams.WRAP_CONTENT);
			row.setMinimumWidth(tableTitleH.getMeasuredWidth());
			table.addView(row, tbParams);
		} else {
			row = (TableRow) table.getChildAt(index + 1);
		}
		return row;
	}

	protected LinearLayout createEditText(String text, String hint, int width, int height, int gravity, int type) {
		LinearLayout parent = new LinearLayout(context);
		LayoutParams params = new LayoutParams(width, height);
		params.gravity = Gravity.CENTER;
		parent.setLayoutParams(params);
		EditText editText = new EditText(context);
		editText.setPadding(dimension_3 + dimension_3, dimension_3, dimension_3, dimension_3);
		editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		editText.setInputType(InputType.TYPE_CLASS_TEXT);
		editText.setHintTextColor(colorEditTextHint);
		editText.setBackgroundResource(background);
		editText.setEllipsize(TruncateAt.END);
		editText.setTextColor(colorEditText);
		editText.setGravity(gravity);
		editText.setSingleLine(true);
		editText.setHint(hint);
		editText.setText(text);
		// 侦听[Code/Color]
		switch (type) {
		case LISTENER_TYPE_CODE:
			editText.addTextChangedListener(new MyTextWatcher(editText) {
				@Override
				public void afterTextChanged(Editable s) {
					int index = (Integer) ((View) editText.getParent()).getTag(R.id.id_position);
					listRow.remove(index);
					listRow.add(index, s.toString().trim());
				}
			});
			break;
		case LISTENER_TYPE_COLOR:
			editText.addTextChangedListener(new MyTextWatcher(editText) {
				@Override
				public void afterTextChanged(Editable s) {
					int index = (Integer) ((View) editText.getParent()).getTag(R.id.id_position);
					listColumn.remove(index);
					listColumn.add(index, s.toString().trim());
				}
			});
			break;
		}
		LayoutParams paramsEdit = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		parent.setPadding(dimension_3, 0, dimension_3, 0);
		paramsEdit.gravity = Gravity.CENTER;
		parent.addView(editText, paramsEdit);
		return parent;
	}

	protected LinearLayout createEditText(String text, String hint, int width, int height, int gravity, int type,
			int column, int row) {
		LinearLayout parent = new LinearLayout(context);
		TableRow.LayoutParams params = new TableRow.LayoutParams(width, height);
		parent.setLayoutParams(params);
		EditText editText = new EditText(context);
		editText.setPadding(dimension_3 + dimension_3, dimension_3, dimension_3, dimension_3);
		editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		editText.setBackgroundResource(background);
		editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		editText.setHintTextColor(colorEditTextHint);
		editText.setEllipsize(TruncateAt.END);
		editText.setTextColor(colorEditText);
		editText.setGravity(Gravity.LEFT);
		editText.setSingleLine(true);
		editText.setHint(hint);
		editText.setText(text);
		editText.setFocusable(true);
		editText.setSaveEnabled(true);
		editText.setFocusableInTouchMode(true);
		editText.setTag(R.id.id_0, column);
		editText.setTag(R.id.id_1, row);
		editText.addTextChangedListener(new MyTextWatcher(editText) {
			@Override
			public void afterTextChanged(Editable s) {
				if (updateTableData) {
					int column = (Integer) editText.getTag(R.id.id_0);
					int row = (Integer) editText.getTag(R.id.id_1);
					setToTableData(column, row, s.toString().trim());
				}
			}
		});
		LayoutParams paramsEdit = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		parent.setPadding(dimension_3, 0, dimension_3, 0);
		paramsEdit.gravity = Gravity.CENTER;
		parent.addView(editText, paramsEdit);
		return parent;
	}

	public void setHorizontalScrollViewListener(ViewScrollViewListener scrollListener) {
		this.scrollListener = scrollListener;
	}

	public List<String> getTableRow() {
		return listRow;
	}

	public List<String> getTableColumn() {
		return listColumn;
	}

	public SparseArray<String> getTableData() {
		return arrayData;
	}

	public void setToTableData(int column, int row, String text) {
		int key = formatKey(column, row);
		arrayData.append(key, text);
		Mylog.info("appdend to table data :column=" + column + ",row=" + row + " key=" + key + " value=" + text);
		// 计算数量
		postDelayed(new Runnable() {
			@Override
			public void run() {
				totalQuantity = 0;
				try {
					for (int i = 0; i < arrayData.size(); i++) {
						String text = arrayData.get(arrayData.keyAt(i));
						if (text == null || text.equals("")) continue;
						totalQuantity += Integer.parseInt(text);
					}
				} catch (NumberFormatException e) {
					totalQuantity = Integer.MAX_VALUE;
				}
				if (changeListenerQuantity != null) changeListenerQuantity.onChange(totalQuantity);
			}
		}, 0);
	}

	public void setOnQuantityChangeListener(OnDataChangeListener changeListener) {
		this.changeListenerQuantity = changeListener;
	}

	public void setOnCodeChangeListener(OnDataChangeListener changeListener) {
		this.changeListenerCode = changeListener;
	}

	public OnDataChangeListener getOnQuantityChangeListener() {
		return this.changeListenerQuantity;
	}

	public OnDataChangeListener getOnCodeChangeListener() {
		return this.changeListenerCode;
	}

	public String getTableData(int column, int row) {
		return arrayData.get(formatKey(column, row));
	}

	protected void setTableData(int column, int row, String text) {
		TableRow tableRow = (TableRow) table.getChildAt(row + 1);
		EditText editText = (EditText) ((LinearLayout) tableRow.getChildAt(column)).getChildAt(0);
		editText.setText(text);
	}

	public int getTotalQuantity() {
		return totalQuantity;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		if (notifyMessage == null) {
			notifyMessage = new Runnable() {
				@Override
				public void run() {
					notifyDataSetChange();
				}
			};
		} else {
			removeCallbacks(notifyMessage);
		}
		post(notifyMessage);
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void notifyDataSetChange() {
		if (dataSource == null) return;
		Mylog.info("notify data set changeed");
		// 清空表格
		TableRow row;
		tableTitleH.removeViews(0, tableTitleH.getChildCount() - 1);
		linearTitleV.removeViews(1, linearTitleV.getChildCount() - 2);
		table.removeViews(1, table.getChildCount() - 1);
		LinearLayout container;
		// column
		listColumn = dataSource.getColor();
		if (listColumn.size() > 0) {
			// other
			int width = getResources().getDimensionPixelSize(R.dimen.dimen_20) * 4;
			int height = tableTitleH.getHeight();
			for (int i = 0; i < listColumn.size(); i++) {
				String color = listColumn.get(i);
				container = createEditText(color, hintColor, width, height, Gravity.CENTER, LISTENER_TYPE_COLOR);
				container.setTag(R.id.id_position, i);
				TableRow.LayoutParams params = new TableRow.LayoutParams(width, height);
				tableTitleH.addView(container, tableTitleH.getChildCount() - 1, params);
			}
		}
		// row
		listRow = dataSource.getCode();
		if (listRow.size() > 0) {
			int columnWidth = getResources().getDimensionPixelSize(R.dimen.dimen_20) * 4;
			int rowWidth = linearTitleV.getMeasuredWidth();
			int height = tableTitleH.getMeasuredHeight();
			Mylog.info("column = " + listColumn.size());
			int i = 0;
			int bgColor;
			for (; i < listRow.size(); i++) {
				bgColor = i % 2 == 0 ? colorTableSecond : colorTableFirst;
				String code = listRow.get(i);
				container = createEditText(code, hintCode, rowWidth, height, Gravity.CENTER, LISTENER_TYPE_CODE);
				container.setBackgroundColor(bgColor);
				container.setTag(R.id.id_position, i);
				linearTitleV.addView(container, linearTitleV.getChildCount() - 1);
				row = getTableRow(i);
				row.setBackgroundColor(bgColor);
				for (int j = 0; j < listColumn.size(); j++) {
					container = createEditText("", hintQuantity, columnWidth, height, Gravity.LEFT, LISTENER_TYPE_DATA,
							j, i);
					row.addView(container);
				}
			}
			// + 号
			bgColor = i % 2 == 0 ? colorTableSecond : colorTableFirst;
			ImageButton add = (ImageButton) linearTitleV.getChildAt(linearTitleV.getChildCount() - 1);
			add.setBackgroundColor(bgColor);
			row = getTableRow(i);
			row.setMinimumHeight(add.getHeight());
			row.setBackgroundColor(bgColor);
		}
		// data
		updateTableData = false; // cancel data change listener
		arrayData = dataSource.getData();
		for (int columnIndex = 0; columnIndex < listColumn.size(); columnIndex++) {
			for (int rowIndex = 0; rowIndex < listRow.size(); rowIndex++) {
				int key = formatKey(columnIndex, rowIndex);
				String text = arrayData.get(key);
				if (text != null && text.length() > 0) {
					setTableData(columnIndex, rowIndex, text);
				}
			}
		}
		updateTableData = true;
	}

	public static int formatKey(int column, int row) {
		return Integer.parseInt(String.format(Locale.CHINA, "%1$d%2$d", column, row));
	}

	public void setScrollListener(ViewScrollViewListener scrollListener) {
		this.scrollListener = scrollListener;
	}

	// inner class
	protected abstract class MyTextWatcher implements TextWatcher {
		EditText editText;

		public MyTextWatcher(EditText editText) {
			this.editText = editText;
		}

		@Override
		public void afterTextChanged(Editable s) {}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {}

	}

	public interface OnViewClickListener {
		public void onClickAddRow(View view);

		public void onClickAddColumn(View view);

		public void onClickPositive(View view);
	}

	public static abstract class OnDataChangeListener {
		protected Object[] params;

		public OnDataChangeListener(Object... params) {
			this.params = params;
		}

		public void onChange(int current) {}
	}

	public interface DataSource {
		public List<String> getColor();

		public List<String> getCode();

		public SparseArray<String> getData();
	}

}
