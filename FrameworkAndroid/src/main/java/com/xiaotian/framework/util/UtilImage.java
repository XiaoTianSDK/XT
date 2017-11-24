package com.xiaotian.framework.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import com.xiaotian.framework.common.Mylog;
import com.xiaotian.frameworkxt.util.UtilFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @name UtilImage
 * @description 图片文件处理类
 * @date 2013-10-13
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2009-2013 广州睿塔科技 Ltd, All Rights Reserved.
 */
public class UtilImage {
    public static final String TAG = "UtilImage";
    private static final int REQ_IMAGE_MAX_WIDTH = 600;
    private static final int REQ_IMAGE_MAX_HEIGHT = 600;
    protected static final char[] arrayChart = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '_', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',};
    static UtilImage util;

    // 临时保存到Map

    public UtilImage() {}

    // 拷贝并压缩图片文件
    public boolean copyCompressFileToFolder(String pathFrom, String pathToFolder) {
        String name = pathFrom.substring(pathFrom.lastIndexOf(File.separator) + 1);
        return copyCompressFileToFile(pathFrom, pathToFolder + File.separator + name);
    }

    public boolean copyCompressFileToFolder(List<String> listPathFrom, String pathToFolder) {
        String name = null;
        for (String pathFrom : listPathFrom) {
            name = pathFrom.substring(pathFrom.lastIndexOf(File.separator) + 1);
            copyCompressFileToFile(pathFrom, pathToFolder + File.separator + name);
        }
        return true;
    }

    public boolean copyCompressFileToFile(String pathFrom, String pathTo) {
        File fileFrom = new File(pathFrom);
        if (!fileFrom.exists()) return false;
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            inputStream = new FileInputStream(fileFrom);
            BitmapFactory.decodeFileDescriptor(inputStream.getFD(), null, options);
        } catch (OutOfMemoryError e) {
            Runtime.getRuntime().gc();
            System.gc();
            return false;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException ie) {
            return false;
        } finally {
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e) {
                return false;
            }
        }
        int inSampleSize = 1;
        if (options.outHeight > REQ_IMAGE_MAX_HEIGHT || options.outWidth > REQ_IMAGE_MAX_WIDTH) {
            final int halfHeight = options.outHeight / 2;
            final int halfWidth = options.outWidth / 2;
            inSampleSize = 2;
            while ((halfHeight / inSampleSize) > REQ_IMAGE_MAX_HEIGHT || (halfWidth / inSampleSize) > REQ_IMAGE_MAX_WIDTH) {
                inSampleSize *= 2;
            }
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inTempStorage = new byte[32 * 1024];
        Bitmap bitmap = null;
        File fileTo = new File(pathTo);
        try {
            inputStream = new FileInputStream(fileFrom);
            bitmap = BitmapFactory.decodeFileDescriptor(inputStream.getFD(), null, options);
            // Re DeCode if rotate the image
            bitmap = reDecodeBitmapIfRotateBitmap(pathFrom, bitmap);
            outputStream = new FileOutputStream(fileTo);
            bitmap.compress(CompressFormat.JPEG, 70, outputStream);
        } catch (OutOfMemoryError e) {
            Runtime.getRuntime().gc();
            System.gc();
            return false;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException ie) {
            return false;
        } finally {
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e) {
                return false;
            }
            if (outputStream != null) try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                return false;
            }
            if (bitmap != null) bitmap.recycle();
        }
        bitmap = null;
        return true;
    }

    public void compressImageFile(String filePath, int requestWidth, int requestHeight, int imageQuantity, CompressFormat imageFormat) {
        File fileFrom = new File(filePath);
        if (!fileFrom.exists()) return;
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            inputStream = new FileInputStream(fileFrom);
            BitmapFactory.decodeFileDescriptor(inputStream.getFD(), null, options);
        } catch (OutOfMemoryError e) {
            Runtime.getRuntime().gc();
            System.gc();
            return;
        } catch (FileNotFoundException e) {
            return;
        } catch (IOException ie) {
            return;
        } finally {
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e) {
                return;
            }
        }
        int inSampleSize = 1;
        if (options.outHeight > requestHeight || options.outWidth > requestWidth) {
            final int halfHeight = options.outHeight / 2;
            final int halfWidth = options.outWidth / 2;
            inSampleSize = 2;
            while ((halfHeight / inSampleSize) > requestHeight || (halfWidth / inSampleSize) > requestHeight) {
                inSampleSize *= 2;
            }
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inTempStorage = new byte[32 * 1024];
        Bitmap bitmap = null;
        try {
            File fileTo = File.createTempFile("image_", "temp");
            inputStream = new FileInputStream(fileFrom);
            bitmap = BitmapFactory.decodeFileDescriptor(inputStream.getFD(), null, options);
            // Re DeCode if rotate the image
            bitmap = reDecodeBitmapIfRotateBitmap(filePath, bitmap);
            outputStream = new FileOutputStream(fileTo);
            bitmap.compress(imageFormat, imageQuantity, outputStream);
            // Cover from file
            UtilFile.getInstance().copyFile(fileTo, fileFrom, true);
            fileTo.deleteOnExit();
        } catch (OutOfMemoryError e) {
            Runtime.getRuntime().gc();
            System.gc();
            return;
        } catch (FileNotFoundException e) {
            return;
        } catch (IOException ie) {
            return;
        } finally {
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e) {
                return;
            }
            if (outputStream != null) try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                return;
            }
            if (bitmap != null) bitmap.recycle();
        }
        bitmap = null;
    }

    public void reDecodeBitmapIfRotateBitmap(String imagePath) {
        File file = new File(imagePath);
        if (!file.exists()) return;
        Bitmap bitmap = null;
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(file);
            bitmap = BitmapFactory.decodeFileDescriptor(inputStream.getFD(), null, new BitmapFactory.Options());
            bitmap = reDecodeBitmapIfRotateBitmap(imagePath, bitmap);
            outputStream = new FileOutputStream(imagePath);
            bitmap.compress(CompressFormat.JPEG, 100, outputStream);
        } catch (OutOfMemoryError e) {
            Runtime.getRuntime().gc();
            System.gc();
            return;
        } catch (FileNotFoundException e) {
            return;
        } catch (IOException ie) {
            return;
        } finally {
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap reDecodeBitmapIfRotateBitmap(String bitmapPath, Bitmap bitmap) throws IOException {
        ExifInterface exif = new ExifInterface(bitmapPath);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        // exif.setAttribute(ExifInterface.ORIENTATION_ROTATE_90, 90);
        Matrix m = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                Mylog.info(TAG, "ExifInterface.ORIENTATION_ROTATE_90");
                m.postRotate(90);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                return bitmap;
            case ExifInterface.ORIENTATION_ROTATE_180:
                Mylog.info("ExifInterface.ORIENTATION_ROTATE_180");
                m.postRotate(180);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                return bitmap;
            case ExifInterface.ORIENTATION_ROTATE_270:
                Mylog.info("ExifInterface.ORIENTATION_ROTATE_270");
                m.postRotate(270);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                return bitmap;
        }
        return bitmap;
    }

    /**
     * 移动图片文件到指定目录,强制进行压缩[600*600]
     */
    public boolean moveCompressFileToFolder(String pathFrom, String pathToFolder) {
        if (copyCompressFileToFolder(pathFrom, pathToFolder)) {
            File file = new File(pathFrom);
            file.deleteOnExit();
            return true;
        }
        return false;
    }

    /**
     * 移动图片文件到指定目录,强制进行压缩[600*600]
     */
    public boolean moveCompressFileToFolder(List<String> listPathFrom, String pathToFolder) {
        if (copyCompressFileToFolder(listPathFrom, pathToFolder)) {
            for (String name : listPathFrom) {
                File file = new File(name);
                file.deleteOnExit();
            }
            return true;
        }
        return false;
    }

    public String buildRandomFileName(String extend) {
        StringBuilder sb = new StringBuilder(32);
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        sb.append(String.format(Locale.getDefault(), "%1$tY%<tm%<td%<tH%<tM%<tS%<tL", calendar));
        // 随机6位数
        for (int i = 0; i < (Math.random() * 1000) % 6; i++) {
            int index = (int) ((Math.random() * 1000) % arrayChart.length);
            sb.append(arrayChart[index]);
        }
        if (extend != null && !extend.equals("")) {
            sb.append(".");
            sb.append(extend);
        }
        return sb.toString();
    }

    public Bitmap decodeByFactoryFile(String filePath, int reqWidth, int reqHeight) {
        // decode image size
        File file = new File(filePath);
        if (!file.exists()) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            BitmapFactory.decodeFileDescriptor(inputStream.getFD(), null, options);
        } catch (OutOfMemoryError e) {
            Runtime.getRuntime().gc();
            System.gc();
        } catch (FileNotFoundException e) {
        } catch (IOException ie) {
        } finally {
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
        if (reqWidth < 100) reqWidth = 70;
        if (reqHeight < 100) reqHeight = 70;
        int inSampleSize = 1;
        if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
            final int halfHeight = options.outHeight / 2;
            final int halfWidth = options.outWidth / 2;
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inTempStorage = new byte[32 * 1024];
        Bitmap bitmap = null;
        try {
            inputStream = new FileInputStream(file);
            bitmap = BitmapFactory.decodeFileDescriptor(inputStream.getFD(), null, options);
        } catch (OutOfMemoryError e) {
            Runtime.getRuntime().gc();
            System.gc();
        } catch (FileNotFoundException e) {
        } catch (IOException ie) {
        } finally {
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
        return bitmap;
    }

    public boolean saveImageToName(Bitmap bitmap, String fileName) {
        // TODO Save Bitmap To File path name
        OutputStream out = null;
        File file = new File(fileName);
        try {
            Mylog.info("save image to path name=" + fileName);
            if (file.exists() || file.createNewFile()) {
                out = new FileOutputStream(file);
                bitmap.compress(CompressFormat.JPEG, 100, out);
                return true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException ignore) {
                ignore.printStackTrace();
            }
        }
        return false;
    }

    public boolean saveImageToName(byte[] data, String fileName) {
        OutputStream out = null;
        File file = new File(fileName);
        try {
            if (file.exists() || file.createNewFile()) {
                out = new FileOutputStream(file);
                out.write(data);
                return true;
            }
        } catch (Exception e) {
            Mylog.info("保存失败");
            return false;
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException ignore) {
            }
        }
        return false;
    }

    // JPEG 类型压缩
    public Bitmap compressBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 100, baos);
        return BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.size());
    }

    // 创建圆角Drawable
    public Drawable createRoundedDrawable(Context context, Bitmap bitmap, int roundRes) {
        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
        drawable.setCornerRadius(context.getResources().getDimensionPixelSize(roundRes));
        return drawable;
    }
}
