package com.xiaotian.framework.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TableLayout;
import android.widget.ToggleButton;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name ViewToggleButtonGround
 * @description 继承于Table布局的多选按钮组::实现侦听子ToggleButton[系统开关按钮]
 * @date 2013-10-19
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
public class ViewToggleButtonGroundTable extends TableLayout implements OnCheckedChangeListener {
	protected int resTextSelector;
	// UI

	protected ArrayList<Integer> chosedIds;

	public ViewToggleButtonGroundTable(Context context) {
		super(context);
		chosedIds = new ArrayList<Integer>();
	}

	public ViewToggleButtonGroundTable(Context context, AttributeSet attrs) {
		super(context, attrs);
		chosedIds = new ArrayList<Integer>();
	}

	@Override
	public void addView(View child) {
		super.addView(child);
		addToggleButtonEvent(child);
	}

	@Override
	public void addView(View child, ViewGroup.LayoutParams params) {
		super.addView(child, params);
		addToggleButtonEvent(child);
	}

	protected void addToggleButtonEvent(View child) {
		if (child instanceof ToggleButton) {
			((ToggleButton) child).setOnCheckedChangeListener(this);
		} else {
			setOnCheckedChangeListenerIteralorViewGround(child);
		}
	}

	protected void setOnCheckedChangeListenerIteralorViewGround(View ground) {
		if (ground instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) ground;
			for (int i = 0; i < vg.getChildCount(); i++) {
				setOnCheckedChangeListenerIteralorViewGround(vg.getChildAt(i));
			}
		} else if (ground instanceof ToggleButton) {
			ground.setClickable(true);
			((ToggleButton) ground).setOnCheckedChangeListener(this);
		}
	}

	/**
	 * @return the chosedIds
	 */
	public ArrayList<Integer> getChosedIds() {
		return chosedIds;
	}

	public void addChosedId(Integer id) {
		chosedIds.add(id);
	}

	public void removeChosedId(Integer id) {
		chosedIds.remove(id);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			chosedIds.add(Integer.valueOf(buttonView.getId()));
		} else {
			chosedIds.remove(Integer.valueOf(buttonView.getId()));
		}
	}
}
