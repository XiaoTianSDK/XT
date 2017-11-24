package com.xiaotian.framework.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @version 1.0.0
 * @author Administrator
 * @name UtilParcelable
 * @description
 * @date 2015-8-6
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class UtilParcelable implements Parcelable {
	public UtilParcelable(Parcel dest) {

	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

	}

	@SuppressWarnings("rawtypes")
	public static final Creator CREATOR = new Creator() {
		public UtilParcelable createFromParcel(Parcel in) {
			return new UtilParcelable(in);
		}

		public UtilParcelable[] newArray(int size) {
			return new UtilParcelable[size];
		}
	};
}
