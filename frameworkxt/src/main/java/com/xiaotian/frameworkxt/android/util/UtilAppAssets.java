package com.xiaotian.frameworkxt.android.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @description
 * @date 2015/11/16
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class UtilAppAssets {
    //
    private Context mContext;
    private AssetManager mAssetManager;

    public UtilAppAssets(Context context) {
        mContext = context;
        mAssetManager = mContext.getAssets();
    }

    /****************************** Static Method ******************************/
    public Typeface getTypeface(String fontFilename) {
        return Typeface.createFromAsset(mContext.getAssets(), fontFilename);
    }
}
