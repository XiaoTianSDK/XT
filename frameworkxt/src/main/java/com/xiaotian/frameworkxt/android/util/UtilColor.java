package com.xiaotian.frameworkxt.android.util;

import android.graphics.Color;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @name UtilColor
 * @description Color Util
 * @date 2014-6-7
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class UtilColor {

    public static int randomColor() {
        return Color.rgb((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
    }

    public static int rgb(int red, int green, int blue) {
        return Color.rgb(red, green, blue);
    }

    public static int rgb(int alpha, int red, int green, int blue) {
        return Color.argb(alpha, red, green, blue);
    }

    public static int parse(String colorString) {
        return Color.parseColor(colorString);
    }

    // 根据比率进行颜色的转换,比率从: 0-1
    public static int convertColor(int colorFrom, int colorTo, float rate) {
        int a0 = Color.alpha(colorFrom);
        int r0 = Color.red(colorFrom);
        int g0 = Color.green(colorFrom);
        int b0 = Color.blue(colorFrom);
        //
        int a1 = Color.alpha(colorTo);
        int r1 = Color.red(colorTo);
        int g1 = Color.green(colorTo);
        int b1 = Color.blue(colorTo);
        //
        int a = (int) ((a0 - a1) * rate);
        int r = (int) ((r0 - r1) * rate);
        int g = (int) ((g0 - g1) * rate);
        int b = (int) ((b0 - b1) * rate);
        return Color.argb(a0 - a, r0 - r, g0 - g, b0 - b);
    }

    /************************************** HSV 360度彩虹色环 **************************************/
    public static int rainbowColor(float current, float count) {
        // current 当前索引
        // count 最大索引
        float wheelPosition = current / count * 360.0f;
        return Color.HSVToColor(new float[]{wheelPosition, 1.0f, 1.0f});
    }


}
