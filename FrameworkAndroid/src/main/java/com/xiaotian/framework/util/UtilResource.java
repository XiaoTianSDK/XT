package com.xiaotian.framework.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.TypedValue;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @name UtilResource
 * @description
 * @date 2015-3-13
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class UtilResource {
    private static final HashMap<String, Typeface> sCachedFonts = new HashMap<String, Typeface>();
    private static final String PREFIX_ASSET = "asset:";
    private AssetManager mAssetManager;
    private static TypedValue value;
    private Context mContext;

    public UtilResource(Context context) {
        this.mContext = context;
    }

    public String rawSchemePath(String packageName, int rawResourceId) {
        return String.format(Locale.CHINA, "android.resource://%1$s/%2$d", packageName, rawResourceId);
    }

    public int getDimenPixel(int resDimen) {
        return mContext.getResources().getDimensionPixelSize(resDimen);
    }

    @TargetApi(21)
    public int getColor(int id) {
        if (value == null) {
            value = new TypedValue();
        }
        try {
            Resources.Theme theme = mContext.getTheme();
            if (theme != null && theme.resolveAttribute(id, value, true)) {
                if (value.type >= TypedValue.TYPE_FIRST_INT && value.type <= TypedValue.TYPE_LAST_INT) {
                    return value.data;
                } else if (value.type == TypedValue.TYPE_STRING) {
                    return mContext.getResources().getColor(value.resourceId);
                }
            }
        } catch (Exception ex) {
        }
        return 0;
    }

    /**
     * @param familyName if start with 'asset:' prefix, then load font from asset folder.
     * @return
     */
    public static Typeface load(Context context, String familyName, int style) {
        if (familyName != null && familyName.startsWith(PREFIX_ASSET)) synchronized (sCachedFonts) {
            try {
                if (!sCachedFonts.containsKey(familyName)) {
                    final Typeface typeface = Typeface.createFromAsset(context.getAssets(), familyName.substring(PREFIX_ASSET.length()));
                    sCachedFonts.put(familyName, typeface);
                    return typeface;
                }
            } catch (Exception e) {
                return Typeface.DEFAULT;
            }

            return sCachedFonts.get(familyName);
        }

        return Typeface.create(familyName, style);
    }

    //
    public AssetManager getAssetManager() {
        if (mAssetManager != null) return mAssetManager;
        return mAssetManager = mContext.getAssets();
    }

    public InputStream openAssetFile(String fileName) throws IOException {
        return getAssetManager().open(fileName);
    }

    public BufferedInputStream openAssetFileToBufferedInputStream(String fileName) throws IOException {
        return new BufferedInputStream(openAssetFile(fileName));
    }

    public BufferedReader openAssetFileToBufferedReader(String fileName) throws IOException {
        return new BufferedReader(new InputStreamReader(openAssetFile(fileName)));
    }
}
