package com.xiaotian.framework.view;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaotian.framework.R;
import com.xiaotian.framework.util.UtilLayoutAttribute;

/**
 * 
 * @version 1.0.0
 * @author Administrator
 * @name ViewBlock
 * @description
 * @date 2015-6-24
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class ViewBlock extends LinearLayout {

	protected int backGroundView;
	protected int sizeHeight, sizeWidth;
	protected String header;
	protected String secondHeader;
	protected String footer;
	protected int badgeNumber;
	protected int blockSizeType;
	protected int baseHeightAlignment;
	protected float weight;

	protected ImageButton imageButtonIcon;
	protected TextView textFooter;
	protected ImageView iv;
	protected TextView tv;
	protected View root;
	protected View v;
	protected ViewGroup.LayoutParams params;

	public ViewBlock(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 实例化对象
		setBackgroundColor(Color.TRANSPARENT);
		constructContent(context, attrs);
	}

	protected void constructContent(Context context, AttributeSet attrs) {
		// TODO 获取配置的属性[XML传入配置列表形式创建实例]
		DisplayMetrics dm = getResources().getDisplayMetrics();
		UtilLayoutAttribute ua = new UtilLayoutAttribute(context, attrs);
		backGroundView = ua.getAttributeResourceValue("backgroundView");
		LayoutParams lparams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		// 加到父窗口时添加margin边隔[有margin的必须是match_parent]
		if (backGroundView != -1) {
			// 加载布局页面资源
			root = LayoutInflater.from(context).inflate(backGroundView, null);
		} else {
			// 构造布局
			root = LayoutInflater.from(getContext()).inflate(R.layout.model_view_blockview_xiaotian, null); // this-解析到this
			textFooter = (TextView) root.findViewById(R.id.view_model_blockview_footer_xiaotian);
			// 设置布局配置和资源
			int intValue;;
			float floatValue;
			int attrResource;
			String stringValue;
			attrResource = ua.getAttributeResourceValue(ua.getNSXiaoTian(), "backgroundImage", 0);
			if (attrResource != 0) root.setBackgroundResource(attrResource);
			attrResource = ua.getAttributeResourceValue(ua.getNSXiaoTian(), "backgroundIcon", 0);
			if (attrResource != 0) {
				imageButtonIcon = ((ImageButton) root.findViewById(R.id.view_model_blockview_icon_xiaotian));
				imageButtonIcon.setImageResource(attrResource);
				imageButtonIcon.setVisibility(View.VISIBLE);
			}
			attrResource = ua.getAttributeResourceValue(ua.getNSXiaoTian(), "footerImage", 0);
			if (attrResource != 0) {
				iv = ((ImageView) root.findViewById(R.id.view_model_blockview_footimage_xiaotian));
				iv.setImageResource(attrResource);
				iv.setVisibility(View.VISIBLE);
			}
			root.setBackgroundColor(ua.getColorAttribute(ua.getNSXiaoTian(), "backgroundColor", Color.WHITE));
			stringValue = ua.getStringAttribute("header");
			if (stringValue != null) setTextViewText(R.id.view_model_blockview_header_xiaotian, header = stringValue);
			stringValue = ua.getStringAttribute("hesecondHeaderader");
			if (stringValue != null) setTextViewText(R.id.view_model_blockview_secondheader_xiaotian, secondHeader = stringValue);
			stringValue = ua.getStringAttribute("footer");
			if (stringValue != null) setTextViewText(R.id.view_model_blockview_footer_xiaotian, footer = stringValue);
			intValue = ua.getIntegerAttribute(ua.getNSXiaoTian(), "badgeNumber", -1);
			if (intValue != -1) {
				v = root.findViewById(R.id.view_model_blockview_badge);
				tv = (TextView) v.findViewById(R.id.view_model_blockview_badge_number);
				tv.setText(String.valueOf(badgeNumber = intValue));
				v.setVisibility(View.VISIBLE);
			}
			lparams.leftMargin = (int) ua.getDimension(ua.getNSXiaoTian(), "marginLeft", 0f);
			lparams.topMargin = (int) ua.getDimension(ua.getNSXiaoTian(), "marginTop", 0f);
			lparams.rightMargin = (int) ua.getDimension(ua.getNSXiaoTian(), "marginRight", 0f);
			lparams.bottomMargin = (int) ua.getDimension(ua.getNSXiaoTian(), "marginBottom", 0f);
			setTextViewTextColor(R.id.view_model_blockview_header_xiaotian, ua.getColorAttribute(ua.getNSXiaoTian(), "headerColor", Color.BLACK));
			setTextViewTextColor(R.id.view_model_blockview_secondheader_xiaotian, ua.getColorAttribute(ua.getNSXiaoTian(), "secondHeaderColor", Color.BLACK));
			setTextViewTextColor(R.id.view_model_blockview_footer_xiaotian, ua.getColorAttribute(ua.getNSXiaoTian(), "footerColor", Color.BLACK));
			setTextViewTextSize(R.id.view_model_blockview_header_xiaotian, ua.getDimension(ua.getNSXiaoTian(), "headerSize", TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, dm)));
			setTextViewTextSize(R.id.view_model_blockview_secondheader_xiaotian, ua.getDimension(ua.getNSXiaoTian(), "secondHeaderSize", TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, dm)));
			setTextViewTextSize(R.id.view_model_blockview_footer_xiaotian, ua.getDimension(ua.getNSXiaoTian(), "footerSize", TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, dm)));
			alignmentToParentRule(R.id.view_model_blockview_icon_xiaotian, ua.getIntegerAttribute(ua.getNSXiaoTian(), "iconAlignment", 0));
			floatValue = ua.getDimension("iconMargin");
			if (floatValue != UtilLayoutAttribute.DEFAULT_DIMENSION) marginToViewReference(R.id.view_model_blockview_icon_xiaotian, 0, (int) floatValue);
			floatValue = ua.getDimension("iconMarginLeft");
			if (floatValue != UtilLayoutAttribute.DEFAULT_DIMENSION) marginToViewReference(R.id.view_model_blockview_icon_xiaotian, 1, (int) floatValue);
			floatValue = ua.getDimension("iconMarginTop");
			if (floatValue != UtilLayoutAttribute.DEFAULT_DIMENSION) marginToViewReference(R.id.view_model_blockview_icon_xiaotian, 2, (int) floatValue);
			floatValue = ua.getDimension("iconMarginRight");
			if (floatValue != UtilLayoutAttribute.DEFAULT_DIMENSION) marginToViewReference(R.id.view_model_blockview_icon_xiaotian, 3, (int) floatValue);
			floatValue = ua.getDimension("iconMarginBottom");
			if (floatValue != UtilLayoutAttribute.DEFAULT_DIMENSION) marginToViewReference(R.id.view_model_blockview_icon_xiaotian, 4, (int) floatValue);
			intValue = ua.getIntegerAttribute("headerAlignment");
			if (intValue != UtilLayoutAttribute.DEFAULT_INTEGER) alignmentToGravity(R.id.view_model_blockview_header_xiaotian, intValue);
			floatValue = ua.getDimension("headerMargin");
			if (floatValue != UtilLayoutAttribute.DEFAULT_DIMENSION) marginToViewReference(R.id.view_model_blockview_header_xiaotian, 0, (int) floatValue);
			floatValue = ua.getDimension("headerMarginLeft");
			if (floatValue != UtilLayoutAttribute.DEFAULT_DIMENSION) marginToViewReference(R.id.view_model_blockview_header_xiaotian, 1, (int) floatValue);
			floatValue = ua.getDimension("headerMarginTop");
			if (floatValue != UtilLayoutAttribute.DEFAULT_DIMENSION) marginToViewReference(R.id.view_model_blockview_header_xiaotian, 2, (int) floatValue);
			floatValue = ua.getDimension("headerMarginRight");
			if (floatValue != UtilLayoutAttribute.DEFAULT_DIMENSION) marginToViewReference(R.id.view_model_blockview_header_xiaotian, 3, (int) floatValue);
			floatValue = ua.getDimension("headerMarginBottom");
			if (floatValue != UtilLayoutAttribute.DEFAULT_DIMENSION) marginToViewReference(R.id.view_model_blockview_header_xiaotian, 4, (int) floatValue);
			intValue = ua.getIntegerAttribute("secondHeaderAlignment");
			if (intValue != UtilLayoutAttribute.DEFAULT_INTEGER) alignmentToGravity(R.id.view_model_blockview_secondheader_xiaotian, intValue);
			floatValue = ua.getDimension("secondHeaderMargin");
			if (floatValue != UtilLayoutAttribute.DEFAULT_DIMENSION) marginToViewReference(R.id.view_model_blockview_secondheader_xiaotian, 0, (int) floatValue);
			floatValue = ua.getDimension("secondHeaderMarginLeft");
			if (floatValue != UtilLayoutAttribute.DEFAULT_DIMENSION) marginToViewReference(R.id.view_model_blockview_secondheader_xiaotian, 1, (int) floatValue);
			floatValue = ua.getDimension("secondHeaderMarginTop");
			if (floatValue != UtilLayoutAttribute.DEFAULT_DIMENSION) marginToViewReference(R.id.view_model_blockview_secondheader_xiaotian, 2, (int) floatValue);
			floatValue = ua.getDimension("secondHeaderMarginRight");
			if (floatValue != UtilLayoutAttribute.DEFAULT_DIMENSION) marginToViewReference(R.id.view_model_blockview_secondheader_xiaotian, 3, (int) floatValue);
			floatValue = ua.getDimension("secondHeaderMarginBottom");
			if (floatValue != UtilLayoutAttribute.DEFAULT_DIMENSION) marginToViewReference(R.id.view_model_blockview_secondheader_xiaotian, 4, (int) floatValue);
			intValue = ua.getIntegerAttribute("footerAlignment");
			if (intValue != UtilLayoutAttribute.DEFAULT_INTEGER) alignmentToGravity(R.id.view_model_blockview_footer_xiaotian, intValue);
			floatValue = ua.getDimension("footerMargin");
			if (floatValue != UtilLayoutAttribute.DEFAULT_DIMENSION) marginToViewReference(R.id.view_model_blockview_footer_xiaotian, 0, (int) floatValue);
			floatValue = ua.getDimension("footerMarginLeft");
			if (floatValue != UtilLayoutAttribute.DEFAULT_DIMENSION) marginToViewReference(R.id.view_model_blockview_footer_xiaotian, 1, (int) floatValue);
			floatValue = ua.getDimension("footerMarginTop");
			if (floatValue != UtilLayoutAttribute.DEFAULT_DIMENSION) marginToViewReference(R.id.view_model_blockview_footer_xiaotian, 2, (int) floatValue);
			floatValue = ua.getDimension("footerMarginRight");
			if (floatValue != UtilLayoutAttribute.DEFAULT_DIMENSION) marginToViewReference(R.id.view_model_blockview_footer_xiaotian, 3, (int) floatValue);
			floatValue = ua.getDimension("footerrMarginBottom");
			if (floatValue != UtilLayoutAttribute.DEFAULT_DIMENSION) marginToViewReference(R.id.view_model_blockview_secondheader_xiaotian, 4, (int) floatValue);
			blockSizeType = ua.getIntegerAttribute(ua.getNSXiaoTian(), "blockSizeType", 0);
			baseHeightAlignment = ua.getIntegerAttribute(ua.getNSXiaoTian(), "baseHeightAligned", 0);
			floatValue = ua.getDimension("badgeMarginTop");
			if (floatValue != UtilLayoutAttribute.DEFAULT_DIMENSION) marginToViewReference(R.id.view_model_blockview_badge, 2, (int) floatValue);
			floatValue = ua.getDimension("badgeMarginRight");
			if (floatValue != UtilLayoutAttribute.DEFAULT_DIMENSION) marginToViewReference(R.id.view_model_blockview_badge, 3, (int) floatValue);
			stringValue = ua.getStringAttribute("onClick");
			if (stringValue != null) {
				View image = root.findViewById(R.id.view_model_blockview_icon_xiaotian);
				Method method;
				try {
					method = context.getClass().getMethod(stringValue, View.class);
					image.setTag(R.id.id_0, method);
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
				image.setOnClickListener(new OnClickListener() {
					Method method;

					@Override
					public void onClick(View paramView) {
						if (method == null) method = (Method) paramView.getTag(R.id.id_0);
						try {
							method.invoke(paramView.getContext(), paramView.getParent());
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				});
			}
			weight = attrs.getAttributeFloatValue("http://schemas.android.com/apk/res/android", "layout_weight", 0.0f);
		}
		addView(root, lparams);
		// 设置点击Icon的事件委托大小
		root.post(new Runnable() {
			@Override
			public void run() {
				Rect delegateArea = new Rect();
				root.getHitRect(delegateArea);
				View image = root.findViewById(R.id.view_model_blockview_icon_xiaotian);
				TouchDelegate touchDelegate = new TouchDelegate(delegateArea, image);
				root.setTouchDelegate(touchDelegate);
			}
		});
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// widthMeasureSpec:包含 模式/大小 [mode/size]的组合体
		int spaceHeightMode = MeasureSpec.getMode(widthMeasureSpec);
		sizeWidth = MeasureSpec.getSize(widthMeasureSpec); // 系统计算的大小
		sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
		switch (blockSizeType) {
		case 0:
			// square
			sizeHeight = sizeWidth;
			break;
		case 1:
			// rectangle
			switch (baseHeightAlignment) {
			// 长方形的高的对齐方式
			case 0:
				// square [以宽为高对齐]
				sizeHeight = (int) Math.ceil(sizeWidth / (weight > 0.0f ? weight : 1));
				break;
			case 1:
				// rectangle [按4:3对齐]
				sizeHeight = (int) Math.ceil(sizeWidth / (weight > 0.0f ? weight : 1) * 3 / 4);
				break;
			}
			break;
		}
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(sizeHeight, spaceHeightMode);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec); // 调用父类的确认方法--系统添加组件[不调用系统添加inflater组件]
	}

	private void alignmentToGravity(int viewReference, int viewAlignment) {
		tv = (TextView) root.findViewById(viewReference);

		switch (viewAlignment) {
		case 1:
			// left
			tv.setGravity(Gravity.LEFT);
			break;
		case 2:
			// Top
			tv.setGravity(Gravity.TOP);
			break;
		case 4:
			// Right
			tv.setGravity(Gravity.RIGHT);
			break;
		case 8:
			// Button
			tv.setGravity(Gravity.BOTTOM);
			break;
		case 3:
			// left|top
			tv.setGravity(Gravity.LEFT | Gravity.TOP);
			break;
		case 6:
			// right|top
			tv.setGravity(Gravity.RIGHT | Gravity.TOP);
			break;
		case 9:
			// left|bottom
			tv.setGravity(Gravity.LEFT | Gravity.BOTTOM);
			break;
		case 12:
			// right|bottom
			tv.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
			break;
		case 16:
			// Center Default
			break;
		}
		tv = null;
	}

	private void alignmentToParentRule(int viewReference, int viewAlignment) {
		params = (RelativeLayout.LayoutParams) root.findViewById(viewReference).getLayoutParams();
		switch (viewAlignment) {
		case 1:
			// left
			((RelativeLayout.LayoutParams) params).addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			break;
		case 2:
			// Top
			((RelativeLayout.LayoutParams) params).addRule(RelativeLayout.ALIGN_PARENT_TOP);
			break;
		case 4:
			// Right
			((RelativeLayout.LayoutParams) params).addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			break;
		case 8:
			// Bottom
			((RelativeLayout.LayoutParams) params).addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			break;
		case 3:
			// left|top
			((RelativeLayout.LayoutParams) params).addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			((RelativeLayout.LayoutParams) params).addRule(RelativeLayout.ALIGN_PARENT_TOP);
			break;
		case 6:
			// right|top
			((RelativeLayout.LayoutParams) params).addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			((RelativeLayout.LayoutParams) params).addRule(RelativeLayout.ALIGN_PARENT_TOP);
			break;
		case 9:
			// left|bottom
			((RelativeLayout.LayoutParams) params).addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			((RelativeLayout.LayoutParams) params).addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			break;
		case 12:
			// right|bottom
			((RelativeLayout.LayoutParams) params).addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			((RelativeLayout.LayoutParams) params).addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			break;
		case 16:
			// center Default
			break;
		}
		params = null;
	}

	private void marginToViewReference(int viewReference, int side, int value) {
		params = (RelativeLayout.LayoutParams) root.findViewById(viewReference).getLayoutParams();
		switch (side) {
		case 0:
			((RelativeLayout.LayoutParams) params).setMargins(value, value, value, value);
			break;
		case 1:
			// left
			((RelativeLayout.LayoutParams) params).setMargins(value, ((RelativeLayout.LayoutParams) params).topMargin, ((RelativeLayout.LayoutParams) params).rightMargin,
					((RelativeLayout.LayoutParams) params).bottomMargin);
			break;
		case 2:
			// top
			((RelativeLayout.LayoutParams) params).setMargins(((RelativeLayout.LayoutParams) params).leftMargin, value, ((RelativeLayout.LayoutParams) params).rightMargin,
					((RelativeLayout.LayoutParams) params).bottomMargin);
			break;
		case 3:
			// right
			((RelativeLayout.LayoutParams) params).setMargins(((RelativeLayout.LayoutParams) params).leftMargin, ((RelativeLayout.LayoutParams) params).topMargin, value,
					((RelativeLayout.LayoutParams) params).bottomMargin);
			break;
		case 4:
			// buttom
			((RelativeLayout.LayoutParams) params).setMargins(((RelativeLayout.LayoutParams) params).leftMargin, ((RelativeLayout.LayoutParams) params).topMargin,
					((RelativeLayout.LayoutParams) params).rightMargin, value);
			break;
		}
		params = null;
	}

	private void setTextViewText(int viewReference, String text) {
		tv = (TextView) root.findViewById(viewReference);
		tv.setText(text);
		tv.setVisibility(View.VISIBLE);
		tv = null;
	}

	private void setTextViewTextColor(int viewReference, int color) {
		tv = (TextView) root.findViewById(viewReference);
		tv.setTextColor(color);
		tv = null;
	}

	private void setTextViewTextSize(int viewReference, float size) {
		tv = (TextView) root.findViewById(viewReference);
		tv.setTextSize(size);
		tv = null;
	}

	public void setBadgeNumber(int number) {
		if (number == 0) {
			this.findViewById(R.id.view_model_blockview_badge).setVisibility(View.GONE);
		} else {
			this.findViewById(R.id.view_model_blockview_badge).setVisibility(View.VISIBLE);
			tv = (TextView) this.findViewById(R.id.view_model_blockview_badge_number);
			tv.setText(String.valueOf(number));
		}
	}

	public void setFooterText(String text) {
		if (textFooter.getVisibility() != View.VISIBLE) textFooter.setVisibility(View.VISIBLE);
		textFooter.setText(text);
	}
}
