package com.xiaotian.framework.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.xiaotian.framework.R;
import com.xiaotian.framework.common.Mylog;

/**
 * @author Administrator
 * @version 1.0.0
 * @name UtilLayoutAttribute
 * @description 布局配置属性
 * @date 2015-9-1
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class UtilLayoutAttribute {
    public static final int DEFAULT_INTEGER = Integer.MIN_VALUE;
    public static final int DEFAULT_RESOURCE = Integer.MIN_VALUE;
    public static final double DEFAULT_DOUBLE = Double.MIN_VALUE;
    public static final float DEFAULT_DIMENSION = Float.MIN_VALUE;
    public static final float DEFAULT_FLOAT = Float.MIN_VALUE;
    public static final int DEFAULT_COLOR = Color.WHITE;
    public static final boolean DEFAULT_BOOLEAN = false;
    private final String XIAOTIAN_NS; // XiaoTian 命名空间
    private final String ANDROID_NS; // Android 命名空间
    private AttributeSet mAttributeSet;
    private DisplayMetrics mDisplayMetrics;
    private Context mContext;

    public UtilLayoutAttribute(Context context, AttributeSet attrs) {
        this.mContext = context;
        this.mAttributeSet = attrs;
        ANDROID_NS = context.getString(R.string.GLOBAL_NS);
        XIAOTIAN_NS = context.getString(R.string.XIAOTIAN_NS);
    }

    // 获取属性值
    public String getAttributeValue(String attribute) {
        return getAttributeValue(XIAOTIAN_NS, attribute);
    }

    public String getAttributeValueAndroid(String attribute) {
        return getAttributeValue(ANDROID_NS, attribute);
    }

    public String getAttributeValue(String namespace, String attribute) {
        if (mAttributeSet == null) return null;
        return mAttributeSet.getAttributeValue(namespace, attribute);
    }

    // 获取属性资源值
    public int getAttributeResourceValue(String attribute) {
        return getAttributeResourceValue(XIAOTIAN_NS, attribute);
    }

    public int getAttributeResourceValueAndroid(String attribute) {
        return getAttributeResourceValue(ANDROID_NS, attribute);
    }

    public int getAttributeResourceValue(String namespace, String attribute) {
        return getAttributeResourceValue(namespace, attribute, DEFAULT_RESOURCE);
    }

    public int getAttributeResourceValue(String namespace, String attribute, int defaultResource) {
        if (mAttributeSet == null) return DEFAULT_INTEGER;
        return mAttributeSet.getAttributeResourceValue(namespace, attribute, defaultResource);
    }

    // 获取字符串属性
    public String getStringAttribute(String attribute) {
        return getStringAttribute(XIAOTIAN_NS, attribute);
    }

    public String getStringAttributeAndroid(String attribute) {
        return getStringAttribute(ANDROID_NS, attribute);
    }

    public String getStringAttribute(String namespace, String attribute) {
        return getStringAttribute(namespace, attribute, null);
    }

    public String getStringAttribute(String namespace, String attribute, String defaultValue) {
        if (mAttributeSet == null) return defaultValue;
        int attributeRes = mAttributeSet.getAttributeResourceValue(namespace, attribute, DEFAULT_INTEGER);
        String stringAttribute = null;
        if (attributeRes == DEFAULT_INTEGER) {
            stringAttribute = mAttributeSet.getAttributeValue(namespace, attribute);
            return stringAttribute == null ? defaultValue : stringAttribute;
        } else {
            stringAttribute = mContext.getString(attributeRes);
            return stringAttribute == null ? defaultValue : stringAttribute;
        }
    }

    // 获取颜色属性
    public int getColorAttribute(String attribute) {
        return getColorAttribute(XIAOTIAN_NS, attribute, DEFAULT_COLOR);
    }

    public int getColorAttributeAndroid(String attribute) {
        return getColorAttribute(ANDROID_NS, attribute, DEFAULT_COLOR);
    }

    public int getColorAttribute(String namespace, String attribute, int defaultValue) {
        if (mAttributeSet == null) return DEFAULT_COLOR;
        int attributeRes = mAttributeSet.getAttributeResourceValue(namespace, attribute, DEFAULT_INTEGER);
        if (attributeRes == DEFAULT_INTEGER) {
            try {
                String attributeValue = mAttributeSet.getAttributeValue(namespace, attribute);
                return attributeValue == null ? defaultValue : Color.parseColor(attributeValue);
            } catch (Exception e) {
                Mylog.printStackTrace(e);
                return defaultValue;
            }
        } else {
            return mContext.getResources().getColor(attributeRes);
        }
    }

    // 获取距离属性
    public float getDimension(String attribute) {
        return getDimension(XIAOTIAN_NS, attribute, DEFAULT_DIMENSION);
    }

    public float getDimensionAndroid(String attribute) {
        return getDimension(ANDROID_NS, attribute, DEFAULT_DIMENSION);
    }

    public float getDimension(String namespace, String attribute, float defaultValue) {
        if (mAttributeSet == null) return DEFAULT_DIMENSION;
        int attributeRes = mAttributeSet.getAttributeResourceValue(namespace, attribute, DEFAULT_INTEGER);
        if (attributeRes == DEFAULT_INTEGER) {
            try {
                String dimension = mAttributeSet.getAttributeValue(namespace, attribute);
                if (dimension == null) return defaultValue;
                if (dimension.toUpperCase().endsWith("DP")) {
                    float dimen = Float.parseFloat(dimension.substring(0, dimension.length() - 2));
                    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dimen, getDisplayMetrics());
                } else if (dimension.toUpperCase().endsWith("DIP")) {
                    float dimen = Float.parseFloat(dimension.substring(0, dimension.length() - 3));
                    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dimen, getDisplayMetrics());
                } else if (dimension.toUpperCase().endsWith("SP")) {
                    float dimen = Float.parseFloat(dimension.substring(0, dimension.length() - 2));
                    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dimen, getDisplayMetrics());
                } else if (dimension.toUpperCase().endsWith("PX")) {
                    float dimen = Float.parseFloat(dimension.substring(0, dimension.length() - 2));
                    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, dimen, getDisplayMetrics());
                }
                return defaultValue;
            } catch (Exception e) {
                Mylog.printStackTrace(e);
                return defaultValue;
            }
        } else {
            return mContext.getResources().getDimension(attributeRes);
        }
    }

    // 获取绘画属性
    public Drawable getDrawableAttribute(String attribute) {
        return getDrawableAttribute(XIAOTIAN_NS, attribute);
    }

    public Drawable getDrawableAttributeAndroid(String attribute) {
        return getDrawableAttribute(ANDROID_NS, attribute);
    }

    public Drawable getDrawableAttribute(String namespace, String attribute) {
        return getDrawableAttribute(namespace, attribute, null);
    }

    public Drawable getDrawableAttribute(String namespace, String attribute, Drawable defaultDrawable) {
        if (mAttributeSet == null) return null;
        int attributeRes = mAttributeSet.getAttributeResourceValue(namespace, attribute, DEFAULT_INTEGER);
        if (attributeRes == DEFAULT_INTEGER) {
            try {
                String attributeValue = mAttributeSet.getAttributeValue(namespace, attribute);
                return attributeValue == null ? defaultDrawable : new ColorDrawable(Color.parseColor(attributeValue));
            } catch (Exception e) {
                Mylog.printStackTrace(e);
                return defaultDrawable;
            }
        } else {
            return mContext.getResources().getDrawable(attributeRes);
        }
    }

    // 获取布尔值
    public boolean getBooleanAttribute(String attribute) {
        return getBooleanAttribute(XIAOTIAN_NS, attribute);
    }

    public boolean getBooleanAttributeAndroid(String attribute) {
        return getBooleanAttribute(ANDROID_NS, attribute);
    }

    public boolean getBooleanAttribute(String namespace, String attribute) {
        return getBooleanAttribute(namespace, attribute, false);
    }

    public boolean getBooleanAttribute(String namespace, String attribute, boolean defaultValue) {
        if (mAttributeSet == null) return false;
        int attributeRes = mAttributeSet.getAttributeResourceValue(namespace, attribute, DEFAULT_INTEGER);
        if (attributeRes == DEFAULT_INTEGER) {
            try {
                String attributeValue = mAttributeSet.getAttributeValue(namespace, attribute);
                return attributeValue == null ? defaultValue : Boolean.parseBoolean(attributeValue);
            } catch (Exception e) {
                Mylog.printStackTrace(e);
                return defaultValue;
            }
        } else {
            return mContext.getResources().getBoolean(attributeRes);
        }
    }

    // 获取整形值
    public int getIntegerAttribute(String attribute) {
        return getIntegerAttribute(XIAOTIAN_NS, attribute);
    }

    public int getIntegerAttributeAndroid(String attribute) {
        return getIntegerAttribute(ANDROID_NS, attribute);
    }

    public int getIntegerAttribute(String namespace, String attribute) {
        return getIntegerAttribute(namespace, attribute, DEFAULT_INTEGER);
    }

    public int getIntegerAttribute(String namespace, String attribute, int defaultValue) {
        if (mAttributeSet == null) return DEFAULT_INTEGER;
        int attributeRes = mAttributeSet.getAttributeResourceValue(namespace, attribute, DEFAULT_INTEGER);
        if (attributeRes == DEFAULT_INTEGER) {
            try {
                String attributeValue = mAttributeSet.getAttributeValue(namespace, attribute);
                return attributeValue == null ? defaultValue : Integer.parseInt(attributeValue);
            } catch (Exception e) {
                Mylog.printStackTrace(e);
                return defaultValue;
            }
        } else {
            return mContext.getResources().getInteger(attributeRes);
        }
    }

    // 获取浮点值
    public double getDoubleAttribute(String attribute) {
        return getDoubleAttribute(XIAOTIAN_NS, attribute);
    }

    public double getDoubleAttributeAndroid(String attribute) {
        return getDoubleAttribute(ANDROID_NS, attribute);
    }

    public double getDoubleAttribute(String namespace, String attribute) {
        return getDoubleAttribute(namespace, attribute, DEFAULT_DOUBLE);
    }

    public double getDoubleAttribute(String namespace, String attribute, double defaultValue) {
        if (mAttributeSet == null) return DEFAULT_DOUBLE;
        int attributeRes = mAttributeSet.getAttributeResourceValue(namespace, attribute, DEFAULT_INTEGER);
        if (attributeRes == DEFAULT_INTEGER) {
            try {
                String attributeValue = mAttributeSet.getAttributeValue(namespace, attribute);
                return attributeValue == null ? defaultValue : Double.parseDouble(attributeValue);
            } catch (Exception e) {
                Mylog.printStackTrace(e);
                return defaultValue;
            }
        } else {
            return mContext.getResources().getInteger(attributeRes);
        }
    }

    // 自定义命名空间
    public String getNSXiaoTian() {
        return XIAOTIAN_NS;
    }

    // Android命名空间
    public String getNSAndroid() {
        return ANDROID_NS;
    }

    // 屏幕度量
    private DisplayMetrics getDisplayMetrics() {
        if (mDisplayMetrics != null) return mDisplayMetrics;
        return mDisplayMetrics = mContext.getResources().getDisplayMetrics();
    }
}
