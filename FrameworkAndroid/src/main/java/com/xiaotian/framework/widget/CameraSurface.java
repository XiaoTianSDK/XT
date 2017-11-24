package com.xiaotian.framework.widget;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name CameraSurface
 * @description 相机默认预览Surface
 * @date 2013-10-12
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2009-2013 广州睿塔科技 Ltd, All Rights Reserved.
 */
public class CameraSurface extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder holder; // 页面控制器 [控制surface的大小和格式]
	private Camera camera; // 照相机

	// 继承surfaceView,实现Callback
	public CameraSurface(Context context) {
		super(context);
		// try {
		// holder = getHolder(); // SurfaceView 页面视图控制器
		// holder.addCallback(this); // 设置回调
		// if (Build.VERSION.SDK_INT < 11) {
		// // 3.0以下要设置,3.0以上默认设置
		// getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// 页面缓冲
		// }
		// } catch (Exception ex) {}
	}

	public CameraSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
		// try {
		// holder = getHolder(); // SurfaceView 页面视图控制器
		// holder.addCallback(this); // 设置回调
		// if (Build.VERSION.SDK_INT < 11) {
		// getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// 页面缓冲
		// }
		// } catch (Exception ex) {}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			// 先释放摄像头
			if (camera != null) {
				try {
					camera.stopPreview();
				} catch (Exception ignore) {}
				try {
					camera.release();
				} catch (Exception ignore) {}
				camera = null;
			}
			// 启用摄像头
			camera = Camera.open();
			camera.setPreviewDisplay(holder); // 绑定预览视图
		} catch (Exception ex) {
			Log.d("CameraSurface", "open camera error");
			ex.printStackTrace();
			try {
				if (camera != null) {
					try {
						camera.stopPreview();
					} catch (Exception ignore) {}
					try {
						camera.release();
					} catch (Exception ignore) {}
					camera = null;
				}
			} catch (Exception ignore) {}
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// 销毁
		try {
			if (camera != null) {
				try {
					camera.stopPreview();
				} catch (Exception ignore) {}
				try {
					camera.release();
				} catch (Exception ignore) {}
				camera = null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// 预览改变[控制器改变预览]
		try {
			Camera.Parameters parameters = camera.getParameters();
			try {
				List<Camera.Size> supportedSizes = null;
				// On older devices (<1.6) the following will fail
				// the camera will work nevertheless
				supportedSizes = Compatibility.getSupportedPreviewSizes(parameters);

				// preview form factor
				float ff = (float) w / h;
				Log.d("CameraSurface", "Screen res: w:" + w + " h:" + h + " aspect ratio:" + ff);

				// holder for the best form factor and size
				float bff = 0;
				int bestw = 0;
				int besth = 0;
				Iterator<Camera.Size> itr = supportedSizes.iterator();

				// we look for the best preview size, it has to be the closest
				// to the
				// screen form factor, and be less wide than the screen itself
				// the latter requirement is because the HTC Hero with update
				// 2.1 will
				// report camera preview sizes larger than the screen, and it
				// will fail
				// to initialize the camera
				// other devices could work with previews larger than the screen
				// though
				while (itr.hasNext()) {
					Camera.Size element = itr.next();
					// current form factor
					float cff = (float) element.width / element.height;
					// check if the current element is a candidate to replace
					// the best match so far
					// current form factor should be closer to the bff
					// preview width should be less than screen width
					// preview width should be more than current bestw
					// this combination will ensure that the highest resolution
					// will win
					Log.d("CameraSurface", "Candidate camera element: w:" + element.width + " h:" + element.height
							+ " aspect ratio:" + cff);
					if ((ff - cff <= ff - bff) && (element.width <= w) && (element.width >= bestw)) {
						bff = cff;
						bestw = element.width;
						besth = element.height;
					}
				}
				Log.d("CameraSurface", "Chosen camera element: w:" + bestw + " h:" + besth + " aspect ratio:" + bff);
				// Some Samsung phones will end up with bestw and besth = 0
				// because their minimum preview size is bigger then the screen
				// size.
				// In this case, we use the default values: 480x320
				if ((bestw == 0) || (besth == 0)) {
					Log.d("CameraSurface", "Using default camera parameters!");
					bestw = 480;
					besth = 320;
				}
				parameters.setPreviewSize(bestw, besth);
			} catch (Exception ex) {
				parameters.setPreviewSize(480, 320);
			}

			camera.setParameters(parameters);
			camera.startPreview();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Camera getCamera() {
		return camera;
	}

}

/**
 * Ensures compatibility with older and newer versions of the API. See the SDK
 * docs for comments
 * 
 * @author daniele
 */
class Compatibility {
	private static Method mParameters_getSupportedPreviewSizes;
	private static Method mDefaultDisplay_getRotation;

	static {
		initCompatibility();
	};

	/** this will fail on older phones (Android version < 2.0) */
	private static void initCompatibility() {
		try {
			mParameters_getSupportedPreviewSizes = Camera.Parameters.class.getMethod("getSupportedPreviewSizes",
					new Class[] {});
			mDefaultDisplay_getRotation = Display.class.getMethod("getRotation", new Class[] {});

			/* success, this is a newer device */
		} catch (NoSuchMethodException nsme) {
			/* failure, must be older device */
		}
	}

	/**
	 * If it's running on a new phone, let's get the supported preview sizes,
	 * before it was fixed to 480 x 320
	 */
	@SuppressWarnings("unchecked")
	public static List<Camera.Size> getSupportedPreviewSizes(Camera.Parameters params) {
		List<Camera.Size> retList = null;

		try {
			Object retObj = mParameters_getSupportedPreviewSizes.invoke(params);
			if (retObj != null) {
				retList = (List<Camera.Size>) retObj;
			}
		} catch (InvocationTargetException ite) {
			/* unpack original exception when possible */
			Throwable cause = ite.getCause();
			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			} else if (cause instanceof Error) {
				throw (Error) cause;
			} else {
				/* unexpected checked exception; wrap and re-throw */
				throw new RuntimeException(ite);
			}
		} catch (IllegalAccessException ie) {
			// System.err.println("unexpected " + ie);
		}
		return retList;
	}

	static public int getRotation(final Activity activity) {
		int result = 1;
		try {
			Display display = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			Object retObj = mDefaultDisplay_getRotation.invoke(display);
			if (retObj != null) {
				result = (Integer) retObj;
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
		}
		return result;
	}

}