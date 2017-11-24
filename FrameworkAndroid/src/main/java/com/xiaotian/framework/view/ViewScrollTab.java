package com.xiaotian.framework.view;

import java.util.Map;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaotian.framework.R;
import com.xiaotian.framework.util.UtilLayoutAttribute;
import com.xiaotian.framework.view.ViewPagerTouchable.ViewScrollViewListener;

/**
 * @version 1.0.0
 * @author Administrator
 * @name ViewScrollTab
 * @description
 * @date 2015-6-24
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class ViewScrollTab extends LinearLayout implements OnPageChangeListener {
	// 基本属性
	protected String titles[]; // 选项卡文本
	protected int colorScrollTabNormal; // 默认状态选项卡颜色
	protected int colorScrollTabHighlight; // 高亮状态选项卡颜色
	protected int drawableScrollTabBottom; // 底部高亮图片资源
	protected int pageHeight; // Pager高度
	protected ViewScrollTabPageDataSource dataSource; // 绑定数据源
	protected boolean isScrollable; // 可滑动页面切换
	protected ViewScrollTabListener scrollListener;
	// UI
	protected View root;
	protected ViewPagerTouchable viewPager;
	protected TextView preTextView;
	protected LinearLayout pageTabStrip;
	protected View pageTabStripIndicator;
	protected LayoutParams params;
	protected TextView tv;

	// 初始化可以传入数据源
	public ViewScrollTab(Context context) {
		super(context);
		isScrollable = true;
	}

	public ViewScrollTab(Context context, AttributeSet attrs) {
		super(context, attrs);
		isScrollable = true;
		constructContent(context, attrs);
	}

	public void constructContent(Context context, AttributeSet attrs) {
		// TODO 构造View内容
		// TODO XML 配置初始化
		// 初始化顶部工具栏Top Bar
		addScriptTitle(context, attrs);
		// View Pager 组件::
		addPagerView(context);
	}

	public void constructContent(Context context, Map<String, Integer> attrs) {
		setOrientation(LinearLayout.VERTICAL);
		addScriptTitle(context, attrs);
		addPagerView(context);
	}

	protected void addScriptTitle(Context context, AttributeSet attrs) {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		UtilLayoutAttribute ua = new UtilLayoutAttribute(context, attrs);
		int resid;
		try {
			// Script Title 组件 ::
			int titleCount = 0;
			if (dataSource == null && context instanceof ViewScrollTabPageDataSource) {
				dataSource = (ViewScrollTabPageDataSource) context;
			}
			if (dataSource == null) throw new RuntimeException("The Activity Context Must implements ViewScrollTabPageDataSource, Or Create Instance Must Set DataSource For The ViewPager");
			titleCount = dataSource.getTitleCount();
			if (titleCount > 0) {
				titles = dataSource.getTitleString();
				pageTabStrip = new LinearLayout(context);
				pageTabStrip.setId(R.id.PageTitleStrip);
				resid = ua.getAttributeResourceValue(ua.getNSXiaoTian(), "toolbarBackground", R.drawable.background_navigator_grean_xiaotian);
				float paddingTop = ua.getDimension(ua.getNSXiaoTian(), "paddingTop", TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, dm));
				float paddingSide = ua.getDimension(ua.getNSXiaoTian(), "paddingSide", TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, dm));
				pageTabStrip.setPadding((int) paddingSide, (int) paddingTop, (int) paddingSide, 0);
				pageTabStrip.setOrientation(LinearLayout.HORIZONTAL);
				pageTabStrip.setBackgroundResource(resid);
				// 配置的颜色
				colorScrollTabNormal = ua.getColorAttribute(ua.getNSXiaoTian(), "textColorNormal", getResources().getColor(R.color.color_view_scroll_tab_normal_xiaotian));
				colorScrollTabHighlight = ua.getColorAttribute(ua.getNSXiaoTian(), "textColorHighlight", getResources().getColor(R.color.color_view_scroll_tab_highlight_xiaotian));
				drawableScrollTabBottom = ua.getAttributeResourceValue(ua.getNSXiaoTian(), "textDrawableBottom", R.drawable.shape_scrolltab_bottom_normal);
				// 配置的属性
				float drawablePadding = ua.getDimension(ua.getNSXiaoTian(), "textDrawablePadding", TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, dm));
				float marginTop = ua.getDimension(ua.getNSXiaoTian(), "textMarginTop", TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, dm));
				float marginBottom = ua.getDimension(ua.getNSXiaoTian(), "textMarginBottom", TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, dm));
				float textSize = ua.getDimension(ua.getNSXiaoTian(), "textSize", TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, dm));
				TextView tv = null;
				for (int i = 0; i < titleCount; i++) {
					tv = new TextView(context);
					tv.setClickable(true);
					tv.setId(i + 4409);
					tv.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							onClickScrollTabTitle(v);
						}
					});
					tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
					tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
					tv.setCompoundDrawablePadding((int) drawablePadding);
					tv.setText(titles[i]);
					tv.setGravity(Gravity.CENTER);
					params = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
					params.setMargins(0, (int) marginTop, 0, (int) marginBottom);
					params.gravity = Gravity.CENTER_VERTICAL;
					if (i == 0) {
						// 默认选中
						preTextView = tv;
						tv.setTextColor(colorScrollTabHighlight);
						tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, drawableScrollTabBottom);
					} else {
						tv.setTextColor(colorScrollTabNormal);
					}
					pageTabStrip.addView(tv, params);
				}
				params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				float paddingLeft = ua.getDimension(ua.getNSXiaoTian(), "titlePaddingLeft", 0);
				float paddingRight = ua.getDimension(ua.getNSXiaoTian(), "titlePaddingRight", 0);
				pageTabStrip.setPadding((int) paddingLeft, 0, (int) paddingRight, 0);
				addView(pageTabStrip, params);
			}
		} catch (NullPointerException ignore) {}
	}

	protected void addScriptTitle(Context context, Map<String, Integer> attrs) {
		int titleCount = 0;
		titleCount = dataSource.getTitleCount();
		if (titleCount > 0) {
			DisplayMetrics dm = getResources().getDisplayMetrics();
			Integer integerValue;
			titles = dataSource.getTitleString();
			pageTabStrip = new LinearLayout(context);
			pageTabStrip.setId(R.id.PageTitleStrip);
			int paddingTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, dm);
			int paddingSide = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, dm);
			pageTabStrip.setPadding(paddingSide, paddingTop, paddingSide, 0);
			pageTabStrip.setOrientation(LinearLayout.HORIZONTAL);
			Integer intValue = attrs.get("toolbarBackground");
			pageTabStrip.setBackgroundResource(intValue == null ? R.drawable.background_navigator_grean_xiaotian : intValue);
			// 配置的颜色
			integerValue = attrs.get("textColorNormal");
			colorScrollTabNormal = getResources().getColor(integerValue == null ? R.color.color_view_scroll_tab_normal_xiaotian : integerValue);
			integerValue = attrs.get("textColorHighlight");
			colorScrollTabHighlight = getResources().getColor(integerValue == null ? R.color.color_view_scroll_tab_highlight_xiaotian : integerValue);
			integerValue = attrs.get("textDrawableBottom");
			drawableScrollTabBottom = integerValue == null ? R.drawable.shape_scrolltab_bottom_normal : integerValue;
			// 配置的属性
			integerValue = attrs.get("textDrawablePadding");
			int drawablePadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, integerValue == null ? 3 : integerValue, dm);
			integerValue = attrs.get("textMarginTop");
			int marginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, integerValue == null ? 5 : integerValue, dm);
			integerValue = attrs.get("textMarginBottom");
			int marginBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, integerValue == null ? 3 : integerValue, dm);
			integerValue = attrs.get("textSize");
			int textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, integerValue == null ? 18 : integerValue, dm);
			TextView tv = null;
			for (int i = 0; i < titleCount; i++) {
				tv = new TextView(context);
				tv.setClickable(true);
				tv.setId(i + 4409);
				tv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						onClickScrollTabTitle(v);
					}
				});
				tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
				tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
				tv.setCompoundDrawablePadding(drawablePadding);
				tv.setText(titles[i]);
				tv.setGravity(Gravity.CENTER);
				params = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
				params.setMargins(0, marginTop, 0, marginBottom);
				params.gravity = Gravity.CENTER_VERTICAL;
				if (i == 0) {
					// 默认选中
					preTextView = tv;
					tv.setTextColor(colorScrollTabHighlight);
					tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, drawableScrollTabBottom);
				} else {
					tv.setTextColor(colorScrollTabNormal);
				}
				pageTabStrip.addView(tv, params);
			}
			params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			integerValue = attrs.get("titlePaddingLeft");
			int paddingLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, integerValue == null ? 0 : integerValue, dm);
			integerValue = attrs.get("titlePaddingRight");
			int paddingRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, integerValue == null ? 0 : integerValue, dm);
			pageTabStrip.setPadding(paddingLeft, 0, paddingRight, 0);
			addView(pageTabStrip, params);
			// Height
			integerValue = attrs.get("height");
			pageHeight = integerValue == null ? 0 : integerValue;
		}
	}

	protected void addPagerView(final Context context) {
		viewPager = new ViewPagerTouchable(context);
		viewPager.setId(R.id.ViewPager);
		params = new LayoutParams(LayoutParams.MATCH_PARENT, pageHeight, 1);
		// 当前上下文的对象必须实现接口数据源方法,用于加载数据的委托转移
		viewPager.setAdapter(new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				if (dataSource == null) {
					if (context instanceof ViewScrollTabPageDataSource) dataSource = (ViewScrollTabPageDataSource) context;
				}
				if (dataSource == null) throw new RuntimeException("The Activity Context Must implements ViewScrollTabPageDataSource, Or Create Instance Must Set DataSource For The ViewPager");
				return dataSource.isViewFromObject(arg0, arg1);
			}

			@Override
			public int getCount() {
				if (dataSource == null) {
					if (context instanceof ViewScrollTabPageDataSource) dataSource = (ViewScrollTabPageDataSource) context;
				}
				if (dataSource == null) throw new RuntimeException("The Activity Context Must implements ViewScrollTabPageDataSource, Or Create Instance Must Set DataSource For The ViewPager");
				return dataSource.getCount();
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				if (dataSource == null) {
					if (context instanceof ViewScrollTabPageDataSource) dataSource = (ViewScrollTabPageDataSource) context;
				}
				if (dataSource == null) throw new RuntimeException("The Activity Context Must implements ViewScrollTabPageDataSource, Or Create Instance Must Set DataSource For The ViewPager");
				return dataSource.instantiateItem(container, position);
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				if (dataSource == null) {
					if (context instanceof ViewScrollTabPageDataSource) dataSource = (ViewScrollTabPageDataSource) context;
				}
				if (dataSource == null) throw new RuntimeException("The Activity Context Must implements ViewScrollTabPageDataSource, Or Create Instance Must Set DataSource For The ViewPager");
				dataSource.destroyItem(container, position, object);
			}

		});
		viewPager.setOnPageChangeListener(this);
		viewPager.setBackgroundColor(getResources().getColor(R.color.color_text_gray_xiaotian));
		viewPager.setListener(new ViewScrollViewListener() {
			@Override
			public void onScrollFinish() {
				if (scrollListener != null) scrollListener.onScrollFinish();
			}

			@Override
			public void onScrollChanged(int l, int t, int oldl, int oldt) {
				if (scrollListener != null) scrollListener.onScrollChanged(l, t, oldl, oldt);
			}
		});
		addView(viewPager, params);
	}

	@Override
	public void onPageSelected(int position) {
		int id = 4409 + position;
		onClickScrollTabTitle(pageTabStrip.findViewById(id));
		if (scrollListener != null) scrollListener.onPageSelected(position);
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {}

	@Override
	public void onPageScrollStateChanged(int arg0) {}

	// 页面Title Scrip点击事件
	public void onClickScrollTabTitle(View v) {
		if (preTextView != null) {
			preTextView.setTextColor(colorScrollTabNormal);
			preTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		}
		preTextView = (TextView) v;
		viewPager.setCurrentItem(v.getId() - 4409, true);
		preTextView.setTextColor(colorScrollTabHighlight);
		preTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, drawableScrollTabBottom);
	}

	public ViewPager getViewPager() {
		return viewPager;
	}

	public void setDataSource(ViewScrollTabPageDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setAttribute(Map<String, Integer> attr) {
		constructContent(getContext(), attr);
	}

	public boolean isScrollable() {
		return isScrollable;
	}

	public void setScrollable(boolean isScrollable) {
		this.isScrollable = isScrollable;
		viewPager.setPageScrollable(this.isScrollable);
	}

	public int getCurrentItem() {
		return viewPager.getCurrentItem();
	}

	public void setCurrentItem(int position) {
		int id = 4409 + position;
		View view = pageTabStrip.findViewById(id);
		if (view == null) return;
		onClickScrollTabTitle(view);
	}

	public void setScrollListener(ViewScrollTabListener scrollListener) {
		this.scrollListener = scrollListener;
	}

	// inner class
	/**
	 * @version 1.0.0
	 * @author XiaoTian
	 * @name ViewScrollTabPageListener
	 * @description Scroll Tab 左右滚动PagerView适配器实现PageAdapter基本方法
	 * @date 2013-8-28
	 * @link gtrstudio@qq.com
	 * @copyright Copyright © 2009-2013 广州睿塔科技 Ltd, All Rights Reserved.
	 */
	public static interface ViewScrollTabPageDataSource {

		// Title String 标题数
		public int getTitleCount();

		// 标题文本
		public String[] getTitleString();

		// PagerView Adapter 基本实现方法
		public boolean isViewFromObject(View view, Object object);

		/**
		 * View Page 的页面数
		 * 
		 * @return
		 */
		public int getCount();

		/**
		 * 销毁一个Page 对象
		 * 
		 * @param container ViewGroup视图组
		 * @param position 位置
		 * @param object Page页视图对象
		 */
		public void destroyItem(ViewGroup container, int position, Object object);

		/**
		 * 实例化一个Page 对象
		 * 
		 * @param container
		 * @param position
		 * @return Page 页视图
		 */
		public Object instantiateItem(ViewGroup container, int position);

	}

	public static abstract class ViewScrollTabListener {
		public void onPageSelected(int position) {}

		public void onScrollChanged(int x, int y, int oldx, int oldy) {}

		public void onScrollFinish() {}
	}

}
