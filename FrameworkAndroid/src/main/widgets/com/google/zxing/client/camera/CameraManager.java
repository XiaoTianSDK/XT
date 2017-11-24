/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.camera;

import java.io.IOException;
import java.lang.reflect.Method;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import com.google.zxing.client.PlanarYUVLuminanceSource;

/**
 * This object wraps the Camera service object and expects to be the only one
 * talking to it. The implementation encapsulates the steps needed to take
 * preview-sized images, which are used for both preview and decoding.
 * @author dswitkin@google.com (Daniel Switkin)
 */
// public final class CameraManager {
//
// private static final String TAG = CameraManager.class.getSimpleName();
//
// private static final int MIN_FRAME_WIDTH = 240;
// private static final int MIN_FRAME_HEIGHT = 240;
// private static final int MAX_FRAME_WIDTH = 480;
// private static final int MAX_FRAME_HEIGHT = 360;
//
// private static CameraManager cameraManager;
//
// static final int SDK_INT; // Later we can use Build.VERSION.SDK_INT
// static {
// int sdkInt;
// try {
// sdkInt = Integer.parseInt(Build.VERSION.SDK);
// } catch (NumberFormatException nfe) {
// // Just to be safe
// sdkInt = 10000;
// }
// SDK_INT = sdkInt;
// }
//
// public void setLight(boolean bOn) {
// try {
// //when autofocus, the setparam may failed;must cancel autofocus first
// configManager.setLight(camera, bOn);
// } catch (Exception e) {
// e.printStackTrace();
// }
// }
//
// private final Context context;
// private final CameraConfigurationManager configManager;
// private Camera camera;
// private Rect framingRect;
// private Rect framingRectInPreview;
// private boolean initialized;
// private boolean previewing;
// private final boolean useOneShotPreviewCallback;
// /**
// * Preview frames are delivered here, which we pass on to the registered
// handler. Make sure to
// * clear the handler so it will only receive one message.
// */
// private final PreviewCallback previewCallback;
// /** Autofocus callbacks arrive here, and are dispatched to the Handler which
// requested them. */
// private final AutoFocusCallback autoFocusCallback;
//
// /**
// * Initializes this static object with the Context of the calling Activity.
// *
// * @param context The Activity which wants to use the camera.
// */
// public static void init(Context context) {
// if (cameraManager == null) {
// cameraManager = new CameraManager(context);
// }
// }
//
// public Camera getCamera(){
// return camera;
// }
//
// /**
// * Gets the CameraManager singleton instance.
// *
// * @return A reference to the CameraManager singleton.
// */
// public static CameraManager get() {
// return cameraManager;
// }
//
// private CameraManager(Context context) {
//
// this.context = context;
// this.configManager = new CameraConfigurationManager(context);
//
// // Camera.setOneShotPreviewCallback() has a race condition in Cupcake, so we
// use the older
// // Camera.setPreviewCallback() on 1.5 and earlier. For Donut and later, we
// need to use
// // the more efficient one shot callback, as the older one can swamp the
// system and cause it
// // to run out of memory. We can't use SDK_INT because it was introduced in
// the Donut SDK.
// //useOneShotPreviewCallback = Integer.parseInt(Build.VERSION.SDK) >
// Build.VERSION_CODES.CUPCAKE;
// useOneShotPreviewCallback = Integer.parseInt(Build.VERSION.SDK) > 3; // 3 =
// Cupcake
//
// previewCallback = new PreviewCallback(configManager,
// useOneShotPreviewCallback);
// autoFocusCallback = new AutoFocusCallback();
// }
//
// /**
// * Opens the camera driver and initializes the hardware parameters.
// *
// * @param holder The surface object which the camera will draw preview frames
// into.
// * @throws IOException Indicates the camera driver failed to open.
// */
// public void openDriver(SurfaceHolder holder) throws IOException {
// if (camera == null) {
// camera = Camera.open();
// if (camera == null) {
// throw new IOException();
// }
// camera.setPreviewDisplay(holder);
//
// if (!initialized) {
// initialized = true;
// configManager.initFromCameraParameters(camera);
// }
// configManager.setDesiredCameraParameters(camera);
//
// SharedPreferences prefs =
// PreferenceManager.getDefaultSharedPreferences(context);
// if (prefs.getBoolean(PreferencesActivity.KEY_FRONT_LIGHT, false)) {
// FlashlightManager.enableFlashlight();
// }
// }
// }
//
// /**
// * Closes the camera driver if still in use.
// */
// public void closeDriver() {
// if (camera != null) {
// FlashlightManager.disableFlashlight();
// camera.release();
// camera = null;
// }
// }
//
// /**
// * Asks the camera hardware to begin drawing preview frames to the screen.
// */
// public void startPreview() {
// if (camera != null && !previewing) {
// camera.startPreview();
// previewing = true;
// }
// }
//
// /**
// * Tells the camera to stop drawing preview frames.
// */
// public void stopPreview() {
// if (camera != null && previewing) {
// if (!useOneShotPreviewCallback) {
// camera.setPreviewCallback(null);
// }
// camera.stopPreview();
// previewCallback.setHandler(null, 0);
// autoFocusCallback.setHandler(null, 0);
// previewing = false;
// }
// }
//
// public void stopAutoFocus() {
// if (camera != null) {
// autoFocusCallback.setHandler(null, 0);
// }
// // camera.a
// }
//
// /**
// * A single preview frame will be returned to the handler supplied. The data
// will arrive as byte[]
// * in the message.obj field, with width and height encoded as message.arg1 and
// message.arg2,
// * respectively.
// *
// * @param handler The handler to send the message to.
// * @param message The what field of the message to be sent.
// */
// public void requestPreviewFrame(Handler handler, int message) {
// if (camera != null && previewing) {
// previewCallback.setHandler(handler, message);
// if (useOneShotPreviewCallback) {
// camera.setOneShotPreviewCallback(previewCallback);
// } else {
// camera.setPreviewCallback(previewCallback);
// }
// }
// }
//
// /**
// * Asks the camera hardware to perform an autofocus.
// *
// * @param handler The Handler to notify when the autofocus completes.
// * @param message The message to deliver.
// */
// public void requestAutoFocus(Handler handler, int message) {
// if (camera != null && previewing) {
// autoFocusCallback.setHandler(handler, message);
// //Log.d(TAG, "Requesting auto-focus callback");
// String focusMode = camera.getParameters().getFocusMode();
// if (focusMode != null &&
// !focusMode.equals(Camera.Parameters.FOCUS_MODE_FIXED)
// && !focusMode.equals(Camera.Parameters.FOCUS_MODE_INFINITY))
// {
// camera.autoFocus(autoFocusCallback);
// }
//
// // camera.autoFocus(autoFocusCallback);
// }
// }
//
// /**
// * Calculates the framing rect which the UI should draw to show the user where
// to place the
// * barcode. This target helps with alignment as well as forces the user to
// hold the device
// * far enough away to ensure the image will be in focus.
// *
// * @return The rectangle to draw on screen in window coordinates.
// */
// public Rect getFramingRect() {
// Point screenResolution = configManager.getScreenResolution();
// if (framingRect == null) {
// if (camera == null) {
// return null;
// }
// // int width = screenResolution.x * 3 / 4;
// // if (width < MIN_FRAME_WIDTH) {
// // width = MIN_FRAME_WIDTH;
// // } else if (width > MAX_FRAME_WIDTH) {
// // width = MAX_FRAME_WIDTH;
// // }
// // int height = screenResolution.y * 3 / 4;
// // if (height < MIN_FRAME_HEIGHT) {
// // height = MIN_FRAME_HEIGHT;
// // } else if (height > MAX_FRAME_HEIGHT) {
// // height = MAX_FRAME_HEIGHT;
// // }
//
// WindowManager manager = (WindowManager)
// context.getSystemService(Context.WINDOW_SERVICE);
// Display display = manager.getDefaultDisplay();
// // Point screenResolution = new Point(display.getWidth(),
// display.getHeight());
//
// int minWidth =
// display.getWidth()>=display.getHeight()?display.getHeight():display.getWidth();
//
// int height = minWidth*5/7;
// int width = minWidth*5/7;
//
// int leftOffset = (screenResolution.x - width) / 2;
// int topOffset = (screenResolution.y - height) / 2;
// framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset +
// height);
// }
// return framingRect;
// }
//
// /**
// * Like {@link #getFramingRect} but coordinates are in terms of the preview
// frame,
// * not UI / screen.
// */
// public Rect getFramingRectInPreview() {
// if (framingRectInPreview == null) {
// Rect rect = new Rect(getFramingRect());
// Point cameraResolution = configManager.getCameraResolution();
// Point screenResolution = configManager.getScreenResolution();
// rect.left = rect.left * cameraResolution.x / screenResolution.x;
// rect.right = rect.right * cameraResolution.x / screenResolution.x;
// rect.top = rect.top * cameraResolution.y / screenResolution.y;
// rect.bottom = rect.bottom * cameraResolution.y / screenResolution.y;
// framingRectInPreview = rect;
// }
// return framingRectInPreview;
// }
//
// public void setDisplayOrientation(int degrees) {
//
// Method downPolymorphic;
// try {
// downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new
// Class[] { int.class });
// if (downPolymorphic != null)
// downPolymorphic.invoke(camera, new Object[] { degrees });
// } catch (Exception e1) {
// }
//
// }
//
// /**
// * Converts the result points from still resolution coordinates to screen
// coordinates.
// *
// * @param points The points returned by the Reader subclass through
// Result.getResultPoints().
// * @return An array of Points scaled to the size of the framing rect and
// offset appropriately
// * so they can be drawn in screen coordinates.
// */
// /*
// public Point[] convertResultPoints(ResultPoint[] points) {
// Rect frame = getFramingRectInPreview();
// int count = points.length;
// Point[] output = new Point[count];
// for (int x = 0; x < count; x++) {
// output[x] = new Point();
// output[x].x = frame.left + (int) (points[x].getX() + 0.5f);
// output[x].y = frame.top + (int) (points[x].getY() + 0.5f);
// }
// return output;
// }
// */
//
// /**
// * A factory method to build the appropriate LuminanceSource object based on
// the format
// * of the preview buffers, as described by Camera.Parameters.
// *
// * @param data A preview frame.
// * @param width The width of the image.
// * @param height The height of the image.
// * @return A PlanarYUVLuminanceSource instance.
// */
// public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width,
// int height) {
// Rect rect = getFramingRectInPreview();
// int previewFormat = configManager.getPreviewFormat();
// String previewFormatString = configManager.getPreviewFormatString();
// switch (previewFormat) {
// // This is the standard Android format which all devices are REQUIRED to
// support.
// // In theory, it's the only one we should ever care about.
// case PixelFormat.YCbCr_420_SP:
// // This format has never been seen in the wild, but is compatible as we only
// care
// // about the Y channel, so allow it.
// case PixelFormat.YCbCr_422_SP:
// return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top,
// rect.width(), rect.height());
// default:
// // The Samsung Moment incorrectly uses this variant instead of the 'sp'
// version.
// // Fortunately, it too has all the Y data up front, so we can read it.
// if ("yuv420p".equals(previewFormatString)) {
// return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top,
// rect.width(), rect.height());
// }
// }
// throw new IllegalArgumentException("Unsupported picture format: " +
// previewFormat + '/' + previewFormatString);
// }
//
// }

public final class CameraManager {

    private static final String TAG = CameraManager.class.getSimpleName();
    private final Context context;
    private final CameraConfigurationManager configManager;
    private Camera camera;
    private boolean initialized;
    private boolean previewing;
    /**
     * Preview frames are delivered here, which we pass on to the registered
     * handler. Make sure to clear the handler so it will only receive one
     * message.
     */
    private final PreviewCallback previewCallback;
    /**
     * Autofocus callbacks arrive here, and are dispatched to the Handler which
     * requested them.
     */
    private final AutoFocusCallback autoFocusCallback;

    private static CameraManager cameraManager;

    /**
     * Initializes this static object with the Context of the calling Activity.
     * @param context The Activity which wants to use the camera.
     */
    public static void init(Context context) {
        if (cameraManager == null) {
            cameraManager = new CameraManager(context);
        }
    }

    public Camera getCamera() {
        return camera;
    }

    /**
     * Gets the CameraManager singleton instance.
     * @return A reference to the CameraManager singleton.
     */
    public static CameraManager get() {
        return cameraManager;
    }

    private CameraManager(Context context) {
        this.context = context;
        this.configManager = new CameraConfigurationManager(context);
        previewCallback = new PreviewCallback(configManager, false);
        autoFocusCallback = new AutoFocusCallback();
    }

    public void setDisplayOrientaiton() {

    }

    public void setDisplayOrientation(int degrees) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[]{int.class});
            if (downPolymorphic != null) downPolymorphic.invoke(camera, new Object[]{degrees});
        } catch (Exception e1) {
        }

    }

    /**
     * Opens the camera driver and initializes the hardware parameters.
     * @param holder The surface object which the camera will draw preview frames
     * into.
     * @exception IOException Indicates the camera driver failed to open.
     */
    public void openDriver(SurfaceHolder holder) throws IOException {
        Camera theCamera = camera;
        if (theCamera == null) {
            theCamera = Camera.open();
            if (theCamera == null) {
                throw new IOException();
            }
            camera = theCamera;
        }
        theCamera.setPreviewDisplay(holder);
        if (!initialized) {
            initialized = true;
            configManager.initFromCameraParameters(theCamera);
        }
        configManager.setDesiredCameraParameters(theCamera);
    }

    /**
     * Closes the camera driver if still in use.
     */
    public void closeDriver() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    public void setLight(boolean bOn) {
        try {
            // when autofocus, the setparam may failed;must cancel autofocus
            // first
            configManager.setLight(camera, bOn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Asks the camera hardware to begin drawing preview frames to the screen.
     */
    public void startPreview() {
        Camera theCamera = camera;
        if (theCamera != null && !previewing) {
            theCamera.startPreview();
            previewing = true;
        }
    }

    /**
     * Tells the camera to stop drawing preview frames.
     */
    public void stopPreview() {
        if (camera != null && previewing) {
            camera.stopPreview();
            previewCallback.setHandler(null, 0);
            previewing = false;
        }
    }

    /**
     * A single preview frame will be returned to the handler supplied. The data
     * will arrive as byte[] in the message.obj field, with width and height
     * encoded as message.arg1 and message.arg2, respectively.
     * @param handler The handler to send the message to.
     * @param message The what field of the message to be sent.
     */

    public void requestPreviewFrame(Handler handler, int message) {
        Camera theCamera = camera;
        if (theCamera != null && previewing) {
            previewCallback.setHandler(handler, message);
            theCamera.setOneShotPreviewCallback(previewCallback);
        }
    }

    /**
     * Asks the camera hardware to perform an autofocus.
     * @param handler The Handler to notify when the autofocus completes.
     * @param message The message to deliver.
     */
    public void requestAutoFocus(Handler handler, int message) {
        if (camera != null && previewing) {

            autoFocusCallback.setHandler(handler, message);
            try {
                camera.autoFocus(autoFocusCallback);
            } catch (RuntimeException re) {
                // Have heard RuntimeException reported in Android 4.0.x+;
                // continue?
                Log.w(TAG, "Unexpected exception while focusing", re);
            }
        }
    }

    public void stopAutoFocus() {
        if (camera != null) {
            autoFocusCallback.setHandler(null, 0);
        }
        // camera.a
    }

    /**
     * Calculates the framing rect which the UI should draw to show the user
     * where to place the barcode. This target helps with alignment as well as
     * forces the user to hold the device far enough away to ensure the image
     * will be in focus.
     *
     * @return The rectangle to draw on screen in window coordinates.
     */
    // public Rect getFramingRect() {
    // if (framingRect == null) {
    // if (camera == null) {
    // return null;
    // }
    // Point screenResolution = configManager.getScreenResolution();
    // int width = screenResolution.x * 3 / 4;
    // if (width < MIN_FRAME_WIDTH) {
    // width = MIN_FRAME_WIDTH;
    // } else if (width > MAX_FRAME_WIDTH) {
    // width = MAX_FRAME_WIDTH;
    // }
    // int height = screenResolution.y * 3 / 4;
    // if (height < MIN_FRAME_HEIGHT) {
    // height = MIN_FRAME_HEIGHT;
    // } else if (height > MAX_FRAME_HEIGHT) {
    // height = MAX_FRAME_HEIGHT;
    // }
    // int leftOffset = (screenResolution.x - width) / 2;
    // int topOffset = (screenResolution.y - height) / 2;
    // framingRect = new Rect(leftOffset, topOffset, leftOffset + width,
    // topOffset + height);
    // Log.d(TAG, "Calculated framing rect: " + framingRect);
    // }
    // return framingRect;
    // }

    /**
     * Like {@link #getFramingRect} but coordinates are in terms of the preview
     * frame, not UI / screen.
     */
    // public Rect getFramingRectInPreview() {
    // if (framingRectInPreview == null) {
    // Rect framingRect = getFramingRect();
    // if (framingRect == null) {
    // return null;
    // }
    // Rect rect = new Rect(framingRect);
    // Point cameraResolution = configManager.getCameraResolution();
    // Point screenResolution = configManager.getScreenResolution();
    // rect.left = rect.left * cameraResolution.x / screenResolution.x;
    // rect.right = rect.right * cameraResolution.x / screenResolution.x;
    // rect.top = rect.top * cameraResolution.y / screenResolution.y;
    // rect.bottom = rect.bottom * cameraResolution.y / screenResolution.y;
    // framingRectInPreview = rect;
    // }
    // return framingRectInPreview;
    // }
    /**
     * Calculates the framing rect which the UI should draw to show the user
     * where to place the barcode. This target helps with alignment as well as
     * forces the user to hold the device far enough away to ensure the image
     * will be in focus.
     * @return The rectangle to draw on screen in window coordinates.
     */
    private static final int MIN_FRAME_WIDTH = 240;
    private static final int MIN_FRAME_HEIGHT = 240;
    private static final int MAX_FRAME_WIDTH = 1200; // = 5/8 * 1920
    private static final int MAX_FRAME_HEIGHT = 675; // = 5/8 * 1080
    private Rect framingRect;
    private Rect framingRectInPreview;
    private int requestedFramingRectWidth;
    private int requestedFramingRectHeight;

    public Rect getFramingRect() {
        if (framingRect == null) {
            if (camera == null) {
                return null;
            }
            Point screenResolution = configManager.getScreenResolution();
            int width = screenResolution.x * 3 / 4;
            if (width < MIN_FRAME_WIDTH) {
                width = MIN_FRAME_WIDTH;
            } else if (width > MAX_FRAME_WIDTH) {
                width = MAX_FRAME_WIDTH;
            }
            int height = screenResolution.y * 3 / 4;
            if (height < MIN_FRAME_HEIGHT) {
                height = MIN_FRAME_HEIGHT;
            } else if (height > MAX_FRAME_HEIGHT) {
                height = MAX_FRAME_HEIGHT;
            }
            int leftOffset = (screenResolution.x - width) / 2;
            int topOffset = (screenResolution.y - height) / 2;
            framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
            Log.d(TAG, "Calculated framing rect: " + framingRect);
        }
        return framingRect;
    }

    /**
     * Like {@link #getFramingRect} but coordinates are in terms of the preview
     * frame, not UI / screen.
     */
    public Rect getFramingRectInPreview() {
        if (framingRectInPreview == null) {
            Rect framingRect = getFramingRect();
            if (framingRect == null) {
                return null;
            }
            Rect rect = new Rect(framingRect);
            Point cameraResolution = configManager.getCameraResolution();
            Point screenResolution = configManager.getScreenResolution();
            rect.left = rect.left * cameraResolution.x / screenResolution.x;
            rect.right = rect.right * cameraResolution.x / screenResolution.x;
            rect.top = rect.top * cameraResolution.y / screenResolution.y;
            rect.bottom = rect.bottom * cameraResolution.y / screenResolution.y;
            framingRectInPreview = rect;
        }
        return framingRectInPreview;
    }

    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
        Rect rect = getFramingRectInPreview();
        if (rect == null) {
            return null;
        }
        // Go ahead and assume it's YUV rather than die.
        return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top, rect.width(), rect.height());
    }
}