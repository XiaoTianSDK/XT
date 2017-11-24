package com.xiaotian.framework.util;

import android.app.Activity;
import android.os.Bundle;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @description Bundle Util
 * @date 2015/11/23
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天, All Rights Reserved.
 */
public class UtilBundle {
    Activity activity;
    Bundle extras;

    public UtilBundle(Activity activity) {
        this.activity = activity;
    }

    public Bundle getExtras() {
        if (extras != null) return extras;
        return extras = activity.getIntent().getExtras();
    }

    public String getString(String key) {
        if (getExtras() == null) return null;
        return getExtras().getString(key);
    }
}
