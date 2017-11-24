package com.xiaotian.frameworkxt.android.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.DrawableRes;
import android.view.View;

import java.io.File;

/**
 * @author Administrator
 * @version 1.0.0
 * @name UtilBitmap
 * @description
 * @date 2015-3-20
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class UtilBitmap {
    private Context mContext;

    public UtilBitmap(Context context) {
        this.mContext = context;
    }

    // 重绘圆角Bitmap
    public static Bitmap getRoundedCornerBitmap(Context context, Bitmap input, int radioPixels, int w, int h, boolean squareTL, boolean squareTR, boolean squareBL, boolean squareBR) {
        Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);

        //make sure that our rounded corner is scaled appropriately
        final float roundPx = radioPixels * densityMultiplier;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        //draw rectangles over the corners we want to be square
        if (squareTL) {
            canvas.drawRect(0, 0, w / 2, h / 2, paint);
        }
        if (squareTR) {
            canvas.drawRect(w / 2, 0, w, h / 2, paint);
        }
        if (squareBL) {
            canvas.drawRect(0, h / 2, w / 2, h, paint);
        }
        if (squareBR) {
            canvas.drawRect(w / 2, h / 2, w, h, paint);
        }

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(input, 0, 0, paint);

        return output;
    }

    // 获取视频预览照片
    public Bitmap getVideoThumbnail(String video) {
        File videoFile = new File(video);
        if (videoFile.exists()) {
            return ThumbnailUtils.createVideoThumbnail(video, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
        }
        return null;
    }

    // 构造View的Blur的Bitmap
    // view:源视图,targetWidth:目标宽,targetHeight:目标高,translateX:转换X,translateY:转换Y,downSampling:view缩小倍数,radius:模糊高斯半径
    public Bitmap genViewBlurBitmap(View view, int targetWidth, int targetHeight, float translateX, float translateY, float downSampling, int radius) {
        Bitmap bitmap = drawViewToBitmap(view, targetWidth, targetHeight, translateX, translateY, downSampling);
        return bitmap == null ? null : blurApply(mContext, bitmap, radius);
    }

    // View 内容视图转换成Bitmap
    public static Bitmap drawViewToBitmap(View view, int width, int height, float translateX, float translateY, float downSampling) {
        if (width < 1 || height < 1) return null;
        float scale = 1f / downSampling;
        int bmpWidth = (int) (width * scale - translateX / downSampling);
        int bmpHeight = (int) (height * scale - translateY / downSampling);
        Bitmap dest = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(dest);
        c.translate(-translateX / downSampling, -translateY / downSampling);
        if (downSampling > 1) {
            c.scale(scale, scale);
        }
        view.draw(c);
        return dest;
    }

    // 模糊处理
    public Bitmap blurApply(Context context, Bitmap sentBitmap, int radius) {
        if (sentBitmap == null) return null;
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        final RenderScript rs = RenderScript.create(context);
        final Allocation input = Allocation.createFromBitmap(rs, sentBitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(radius);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap);
        // clean up renderscript resources
        rs.destroy();
        input.destroy();
        output.destroy();
        script.destroy();
        return bitmap;
    }

    // 原色调渲染
    public Bitmap genRenderingIcon(@DrawableRes int origin, int primaryColor) {
        if (origin == -1) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        Bitmap bitmapOrigin = BitmapFactory.decodeResource(mContext.getResources(), origin, options);
        Bitmap iconBitmap = bitmapOrigin.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(iconBitmap);
        canvas.drawColor(primaryColor & 0x00ffffff | (isLight(primaryColor) ? 0xff000000 : 0x8a000000), PorterDuff.Mode.SRC_IN);
        return iconBitmap;
    }

    private Bitmap scaleIcon(Bitmap origin, int iconSize) {
        int width = origin.getWidth();
        int height = origin.getHeight();
        int size = Math.max(width, height);
        if (size == iconSize) {
            return origin;
        } else if (size > iconSize) {
            int scaledWidth;
            int scaledHeight;
            if (width > iconSize) {
                scaledWidth = iconSize;
                scaledHeight = (int) (iconSize * ((float) height / width));
            } else {
                scaledHeight = iconSize;
                scaledWidth = (int) (iconSize * ((float) width / height));
            }
            return Bitmap.createScaledBitmap(origin, scaledWidth, scaledHeight, false);
        } else {
            return origin;
        }
    }

    public boolean isLight(int color) {
        return Math.sqrt(Color.red(color) * Color.red(color) * .241 +
                Color.green(color) * Color.green(color) * .691 +
                Color.blue(color) * Color.blue(color) * .068) > 130;
    }
}
