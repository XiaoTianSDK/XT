package com.xiaotian.frameworkxt.android.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name FragmentMenu
 * @description The Fragment Use As menu
 * @date 2014-8-26
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class FragmentMenu extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// indicate that the fragment would like to add items to the Options
		// Menu (otherwise, the fragment will not receive a call to
		// onCreateOptionsMenu()).
		setHasOptionsMenu(true);
	}

	// has no UI
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return null;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// MenuItemCompat
		// SHOW_AS_ACTION_IF_ROOM : If Has Root Then Show In The Action Bar
		// SHOW_AS_ACTION_ALWAYS : Show In The Action Bar
		// SHOW_AS_ACTION_NEVER : Never Show In The Action Bar
		MenuItem item;
		item = menu.add("Menu Test 1");
		MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
		item = menu.add("Menu Test 2");
		MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

}
