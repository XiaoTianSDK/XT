package com.xiaotian.framework.activity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xiaotian.framework.R;
import com.xiaotian.framework.common.Mylog;

/**
 * @version 1.0.0
 * @author Administrator
 * @name ActivityMediaRecorder
 * @description
 * @date 2015-3-25
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class ActivityMediaRecorder extends BaseFragmentActivity {
	static final String TAG = "ActivityMediaRecorder";
	public static final String EXTRA_OUTPUT = "com.xiaotian.framework.activity.FILE";
	public static final String EXTRA_PARAMS_VIDEO_WIDTH = "com.xiaotian.framework.activity.VIDEO_WIDTH";
	public static final String EXTRA_PARAMS_VIDEO_HEIGHT = "com.xiaotian.framework.activity.VIDEO_HEIGHT";

	int cameraType = Camera.CameraInfo.CAMERA_FACING_BACK;
	int CAMERA_ORIENTATION = 90;
	boolean isRecording = false, isPreviewing = true;
	int recordedTime = 0, videoWidth, videoHeight;
	String file;
	// UI
	View rootView;
	Camera mCamera;
	Button captureButton;
	ImageButton playButton;
	TextureView mPreview;
	MediaRecorder mMediaRecorder;
	TextView textTime, textREC;
	Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			int s = recordedTime % 60;
			int m = recordedTime / 60 % 60;
			textTime.setText(String.format(Locale.getDefault(), "%1$02d:%2$02d", m, s));
			if (isRecording) {
				textREC.setVisibility(textREC.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						recordedTime++;
						mHandler.sendMessage(mHandler.obtainMessage());
					}
				}, 1000);
			} else {
				textREC.setVisibility(View.VISIBLE);
			}
			return true;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mediarecorder);
		rootView = findViewById(R.id.id_0);
		mPreview = (TextureView) findViewById(R.id.id_1);
		textTime = (TextView) findViewById(R.id.id_7);
		textREC = (TextView) findViewById(R.id.id_8);
		captureButton = (Button) findViewById(R.id.id_9);
		playButton = (ImageButton) findViewById(R.id.id_10);
		if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
			findViewById(R.id.id_5).setVisibility(View.VISIBLE);
		}
		//if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
		//findViewById(R.id.id_4).setVisibility(View.VISIBLE);
		//}
	}

	public void onCaptureClick(View view) {
		view.setEnabled(false);
		if (isRecording) {
			// stop recording and release camera
			mMediaRecorder.stop(); // stop the recording
			releaseMediaRecorder(); // release the MediaRecorder object
			mCamera.lock(); // take camera access back from MediaRecorder
			// inform the user that recording has stopped
			captureButton.setText(R.string.capture_start);
			isRecording = false;
			isPreviewing = false;
			releaseCamera();
			view.setEnabled(true);
			if (file != null && playButton.getVisibility() != View.VISIBLE) playButton.setVisibility(View.VISIBLE);
		} else {
			releaseCamera();
			new MediaPrepareTask().execute(view);
		}
	}

	@Override
	public void onBackPressed() {
		onBackClick(null);
	}

	public void onBackClick(View view) {
		if (isRecording) {
			toast(R.string.waiting_capturing);
		} else {
			releaseCamera();
			finish();
		}
	}

	public void onSwitchClick(View view) {
		if (isRecording) {
			toast(R.string.waiting_capturing);
		} else {
			view.setEnabled(false);
			releaseCamera();
			if (playButton.getVisibility() != View.INVISIBLE) playButton.setVisibility(View.INVISIBLE);
			cameraType = cameraType == Camera.CameraInfo.CAMERA_FACING_BACK ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK;
			new MediaPreviewTask().execute(view);
		}
	}

	public void onClickFlash(View view) {

	}

	public void onPlayClick(View view) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(new File(file)), "video/*");
		startActivity(intent);
	}

	@Override
	protected void onPause() {
		Mylog.info(TAG + ".onPause() called");
		super.onPause();
		if (isRecording) {
			mMediaRecorder.stop();
			releaseMediaRecorder();
			mCamera.lock();
			captureButton.setText(R.string.capture_start);
			isRecording = false;
			isPreviewing = false;
			releaseCamera();
		} else {
			releaseCamera();
		}
	}

	@Override
	protected void onResume() {
		Mylog.info(TAG + ".onResume() called");
		super.onResume();
		if (isPreviewing) {
			releaseCamera();
			new MediaPreviewTask().execute();
		} else {
			if (playButton.getVisibility() != View.VISIBLE) playButton.setVisibility(View.VISIBLE);
			Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(file, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
			rootView.setBackgroundDrawable(new BitmapDrawable(getResources(), thumbnail));
		}
	}

	private void releaseMediaRecorder() {
		if (mMediaRecorder != null) {
			// clear recorder configuration
			mMediaRecorder.reset();
			// release the recorder object
			mMediaRecorder.release();
			mMediaRecorder = null;
			// Lock camera for later use i.e taking it back from MediaRecorder.
			// MediaRecorder doesn't need it anymore and we will release it if the activity pauses.
			mCamera.lock();
		}
	}

	private void releaseCamera() {
		if (mCamera != null) {
			// release the camera for other applications
			mCamera.cancelAutoFocus();
			mCamera.release();
			mCamera = null;
		}
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private boolean prepareVideoRecorder(int cameraType) {
		// BEGIN_INCLUDE (configure_preview)
		try {
			switch (cameraType) {
			case Camera.CameraInfo.CAMERA_FACING_FRONT:
				mCamera = CameraHelper.getDefaultFrontFacingCameraInstance();
				break;
			case Camera.CameraInfo.CAMERA_FACING_BACK:
				mCamera = CameraHelper.getDefaultBackFacingCameraInstance();
				break;
			default:
				mCamera = CameraHelper.getDefaultCameraInstance();
			}
		} catch (Exception e) {
			return false;
		}
		// We need to make sure that our preview and recording video size are supported by the
		// camera. Query camera to find all the sizes and choose the optimal size given the
		// dimensions of our preview surface.
		Camera.Parameters parameters = mCamera.getParameters();
		List<Camera.Size> mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
		Camera.Size optimalSize = CameraHelper.getOptimalPreviewSize(mSupportedPreviewSizes, mPreview.getHeight(), mPreview.getWidth());
		Mylog.info(optimalSize.width + "," + optimalSize.height);
		// Use the same size for recording profile.
		CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
		//audioBitRate=128000,audioChannels=2,audioCodec=3,audioSampleRate=48000,duration=30,fileFormat=2,quality=4,videoBitRate=3449000,videoCodec=2,videoFrameHeight=480,videoFrameRate=30,videoFrameWidth=720,
		profile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
		profile.videoCodec = MediaRecorder.VideoEncoder.MPEG_4_SP;
		profile.videoFrameWidth = optimalSize.width;
		profile.videoFrameHeight = optimalSize.height;
		//		profile.audioBitRate = 68000;
		//		profile.audioChannels = 1;
		//		profile.audioCodec = MediaRecorder.AudioEncoder.AAC;
		//		profile.videoBitRate = 900000;
		// likewise for the camera object itself.
		parameters.setPreviewSize(profile.videoFrameWidth, profile.videoFrameHeight);
		mCamera.setParameters(parameters);
		try {
			// Requires API level 11+, For backward compatibility use {@link setPreviewDisplay}  with {@link SurfaceView}
			mCamera.setDisplayOrientation(CAMERA_ORIENTATION);
			mCamera.setPreviewTexture(mPreview.getSurfaceTexture());
		} catch (IOException e) {
			Log.e(TAG, "Surface texture is unavailable or unsuitable" + e.getMessage());
			return false;
		}
		// END_INCLUDE (configure_preview)

		// BEGIN_INCLUDE (configure_media_recorder)
		mMediaRecorder = new MediaRecorder();
		mMediaRecorder.setOrientationHint(cameraType == Camera.CameraInfo.CAMERA_FACING_BACK ? 90 : 270);

		// Step 1: Unlock and set camera to MediaRecorder
		mCamera.unlock();
		mMediaRecorder.setCamera(mCamera);

		// Step 2: Set sources
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

		// Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
		mMediaRecorder.setProfile(profile);

		// Step 4: Set output file
		if (file == null) file = CameraHelper.getOutputMediaFile(CameraHelper.MEDIA_TYPE_VIDEO).toString();
		mMediaRecorder.setOutputFile(file);
		// END_INCLUDE (configure_media_recorder)

		// Step 5: Prepare configured MediaRecorder
		try {
			mMediaRecorder.prepare();
		} catch (IllegalStateException e) {
			Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
			releaseMediaRecorder();
			return false;
		} catch (IOException e) {
			Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
			releaseMediaRecorder();
			return false;
		}
		return true;
	}

	private boolean prepareVideoPreview(int cameraType) {
		try {
			switch (cameraType) {
			case Camera.CameraInfo.CAMERA_FACING_FRONT:
				mCamera = CameraHelper.getDefaultFrontFacingCameraInstance();
				break;
			case Camera.CameraInfo.CAMERA_FACING_BACK:
				mCamera = CameraHelper.getDefaultBackFacingCameraInstance();
				break;
			default:
				mCamera = CameraHelper.getDefaultCameraInstance();
			}
		} catch (Exception e) {
			return false;
		}
		Camera.Parameters parameters = mCamera.getParameters();
		List<Camera.Size> mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
		Camera.Size optimalSize = CameraHelper.getOptimalPreviewSize(mSupportedPreviewSizes, mPreview.getWidth(), mPreview.getHeight());
		CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
		profile.videoFrameWidth = optimalSize.width;
		profile.videoFrameHeight = optimalSize.height;
		parameters.setPreviewSize(profile.videoFrameWidth, profile.videoFrameHeight);
		mCamera.setParameters(parameters);
		try {
			mCamera.setDisplayOrientation(CAMERA_ORIENTATION);
			mCamera.setPreviewTexture(mPreview.getSurfaceTexture());
		} catch (IOException e) {
			Log.e(TAG, "Surface texture is unavailable or unsuitable" + e.getMessage());
			return false;
		}
		if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		}
		return true;
	}

	class MediaPreviewTask extends AsyncTask<View, Void, Boolean> {
		View[] tagView;

		@Override
		protected Boolean doInBackground(View... tagView) {
			this.tagView = tagView;
			// initialize video camera
			if (prepareVideoPreview(cameraType)) {
				mCamera.startPreview();
				isPreviewing = true;
			} else {
				releaseCamera();
				isPreviewing = false;
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			for (View tv : tagView) {
				tv.setEnabled(true);
			}
			if (result) {
				//ActivityMediaRecorder.this.finish();
				mCamera.autoFocus(new MyAutoFocusCallback());
			} else {

			}
		}

		class MyAutoFocusCallback implements AutoFocusCallback {
			final long AUTOFOCUS_INTERVAL_MS = 1500L;
			Handler autoFocusHandler = new MyHandle();
			int autoFocusMessage = 0x001;
			int autoFocusTime = 3;
			Camera mCamera;

			public void onAutoFocus(boolean success, Camera camera) {
				this.mCamera = camera;
				if (autoFocusTime-- > 0) {
					Message message = autoFocusHandler.obtainMessage(autoFocusMessage, success);
					autoFocusHandler.sendMessageDelayed(message, AUTOFOCUS_INTERVAL_MS);
				}
			}

			@SuppressLint("HandlerLeak")
			class MyHandle extends Handler {
				@Override
				public void handleMessage(Message msg) {
					if (mCamera != null) mCamera.autoFocus(MyAutoFocusCallback.this);
				}
			}
		}
	}

	/**
	 * Asynchronous task for preparing the {@link MediaRecorder}
	 * since it's a long blocking operation.
	 */
	class MediaPrepareTask extends AsyncTask<View, Void, Boolean> {
		View[] tagView;

		@Override
		protected Boolean doInBackground(View... tagView) {
			this.tagView = tagView;
			// initialize video camera
			if (prepareVideoRecorder(cameraType)) {
				// Camera is available and unlocked, MediaRecorder is prepared, now you can start recording
				mMediaRecorder.start();

				isRecording = true;
			} else {
				// prepare didn't work, release the camera
				releaseMediaRecorder();
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			for (View tv : tagView) {
				tv.setEnabled(true);
			}
			if (!result) {
				//ActivityMediaRecorder.this.finish();
			} else {
				recordedTime = 0;
				mHandler.sendMessage(mHandler.obtainMessage());
				// inform the user that recording has started
				captureButton.setText(R.string.capture_stop);
				if (playButton.getVisibility() != View.INVISIBLE) playButton.setVisibility(View.INVISIBLE);
			}
		}
	}

	public static class CameraHelper {
		public static final String TAG = "CameraHelper";
		public static final int MEDIA_TYPE_IMAGE = 1;
		public static final int MEDIA_TYPE_VIDEO = 2;

		/**
		 * Iterate over supported camera preview sizes to see which one best
		 * fits the dimensions of the given view while maintaining the aspect
		 * ratio. If none can, be lenient with the aspect ratio.
		 * 
		 * @param sizes Supported camera preview sizes.
		 * @param w The width of the view.
		 * @param h The height of the view.
		 * @return Best match camera preview size to fit in the view.
		 */
		public static Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
			// Use a very small tolerance because we want an exact match.
			final double ASPECT_TOLERANCE = 0.1;
			double targetRatio = (double) w / h;
			if (sizes == null) return null;

			Camera.Size optimalSize = null;

			// Start with max value and refine as we iterate over available preview sizes. This is the
			// minimum difference between view and camera height.
			double minDiff = Double.MAX_VALUE;

			// Target view height
			int targetHeight = h;

			// Try to find a preview size that matches aspect ratio and the target view size.
			// Iterate over all available sizes and pick the largest size that can fit in the view and
			// still maintain the aspect ratio.
			for (Camera.Size size : sizes) {
				double ratio = (double) size.width / size.height;
				if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
			// Cannot find preview size that matches the aspect ratio, ignore the requirement
			if (optimalSize == null) {
				minDiff = Double.MAX_VALUE;
				for (Camera.Size size : sizes) {
					if (Math.abs(size.height - targetHeight) < minDiff) {
						optimalSize = size;
						minDiff = Math.abs(size.height - targetHeight);
					}
				}
			}
			return optimalSize;
		}

		/**
		 * @return the default camera on the device. Return null if there is no
		 *         camera on the device.
		 */
		public static Camera getDefaultCameraInstance() {
			return Camera.open();
		}

		/**
		 * @return the default rear/back facing camera on the device. Returns
		 *         null if camera is not available.
		 */
		public static Camera getDefaultBackFacingCameraInstance() {
			return getDefaultCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
		}

		/**
		 * @return the default front facing camera on the device. Returns null
		 *         if camera is not available.
		 */
		public static Camera getDefaultFrontFacingCameraInstance() {
			return getDefaultCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
		}

		/**
		 * 
		 * @param position Physical position of the camera i.e
		 *        Camera.CameraInfo.CAMERA_FACING_FRONT or
		 *        Camera.CameraInfo.CAMERA_FACING_BACK.
		 * @return the default camera on the device. Returns null if camera is
		 *         not available.
		 */
		@TargetApi(Build.VERSION_CODES.GINGERBREAD)
		private static Camera getDefaultCamera(int position) {
			// Find the total number of cameras available
			int mNumberOfCameras = Camera.getNumberOfCameras();

			// Find the ID of the back-facing ("default") camera
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			for (int i = 0; i < mNumberOfCameras; i++) {
				Camera.getCameraInfo(i, cameraInfo);
				if (cameraInfo.facing == position) {
					return Camera.open(i);

				}
			}
			return null;
		}

		/**
		 * Creates a media file in the {@code Environment.DIRECTORY_PICTURES}
		 * directory. The directory is persistent and available to other
		 * applications like gallery.
		 * 
		 * @param type Media type. Can be video or image.
		 * @return A file object pointing to the newly created file.
		 */
		public static File getOutputMediaFile(int type) {
			// To be safe, you should check that the SDCard is mounted
			// using Environment.getExternalStorageState() before doing this.
			if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
				return null;
			}
			File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CameraSample");
			// This location works best if you want the created images to be shared between applications and persist after your app has been uninstalled.
			// Create the storage directory if it does not exist
			if (!mediaStorageDir.exists()) {
				if (!mediaStorageDir.mkdirs()) {
					Log.d(TAG, "failed to create directory");
					return null;
				}
			}
			// Create a media file name
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
			File mediaFile;
			if (type == MEDIA_TYPE_IMAGE) {
				mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
			} else if (type == MEDIA_TYPE_VIDEO) {
				mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
			} else {
				return null;
			}
			return mediaFile;
		}

		public static Bitmap getVideoThumbnail(String file) {
			return ThumbnailUtils.createVideoThumbnail(file, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
		}
	}
}
