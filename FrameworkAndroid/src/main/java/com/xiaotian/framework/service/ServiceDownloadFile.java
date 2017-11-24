package com.xiaotian.framework.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Locale;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.SparseArray;
import android.widget.RemoteViews;

import com.xiaotian.framework.R;
import com.xiaotian.framework.common.Mylog;
import com.xiaotian.framework.util.UtilExternalStore;
import com.xiaotian.framework.util.UtilToastBroadcast;
import com.xiaotian.frameworkxt.util.UtilFile;

/**
 * @version 1.0.0
 * @author Administrator
 * @name ServiceDownloadFile
 * @description 后台下载文件,同时在notification中通知下载进度
 * @date 2015-6-17
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class ServiceDownloadFile extends Service {
	private static final Object LOCK = new Object();
	public static final String ACTION = "com.xiaotian.framework.service.ServiceDownloadFile";
	public static final String EXTRA_PARAM_URL = "com.xiaotian.frameworkxt.android.service.URL";
	public static final String EXTRA_PARAM_ICON = "com.xiaotian.frameworkxt.android.service.ICON";
	public static final String EXTRA_PARAM_TITLE = "com.xiaotian.frameworkxt.android.service.TITLE";
	public static final String EXTRA_PARAM_AUTO_OPEN = "com.xiaotian.frameworkxt.android.service.AUTO_OPEN";
	public static final String EXTRA_PARAM_CANCELABLE = "com.xiaotian.frameworkxt.android.service.CANCELABLE";
	public static final String EXTRA_PARAM_SAVE_FILE_NAME = "com.xiaotian.frameworkxt.android.service.FILE_NAME";
	public static final String EXTRA_PARAM_EXIST_REDOWNLOAD = "com.xiaotian.frameworkxt.android.service.EXIST_REDOWNLOAD";
	//
	Context mContext;
	UtilFile mUtilFile;
	File downloadFolder;
	UtilExternalStore mUtilExternalStore;
	NotificationManager mNotificationManager;
	SparseArray<DownloadRunnable> arrayDownloader;

	public static Intent getActionIntent(Context context) {
		synchronized (LOCK) {
			Intent intent = new Intent(context, ServiceDownloadFile.class);
			intent.setAction(ACTION);
			return intent;
		}
	}

	public void onCreate() {
		mUtilFile = new UtilFile();
		mContext = getApplicationContext();
		mUtilExternalStore = new UtilExternalStore(mContext);
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		arrayDownloader = new SparseArray<DownloadRunnable>();
		// 注册取消广播接收器
		IntentFilter filter = new IntentFilter();
		filter.addAction(BRCancelDownload.ACTION);
		mContext.registerReceiver(new BRCancelDownload(), filter);
		//
		downloadFolder = new File(mUtilExternalStore.getExternalDirectory(), "Download");
		if (!downloadFolder.exists()) {
			if (!downloadFolder.mkdirs()) return;
		} else if (downloadFolder.isFile()) {
			downloadFolder = mUtilExternalStore.getExternalDirectory();
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	// 启动下载文件服务
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null) return 0;
		// extras bundle
		if (!ACTION.equals(intent.getAction())) return 0;
		if (!intent.hasExtra(EXTRA_PARAM_URL)) return 0;
		int notificationId = getNotificationId();
		if (arrayDownloader.get(notificationId) == null) {
			// 启动下载线程
			intent.putExtra(DownloadRunnable.ID, notificationId);
			DownloadRunnable downloader = new DownloadRunnable(intent);
			Thread downloadThread = new Thread(downloader);
			downloadThread.setDaemon(true); // 后台运行
			downloadThread.start();
			//
			Mylog.info(notificationId + "=" + downloader);
			arrayDownloader.put(notificationId, downloader);
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		if (arrayDownloader.size() > 0) {
			for (int i = 0; i < arrayDownloader.size(); i++) {
				arrayDownloader.get(arrayDownloader.keyAt(i)).cancel();
			}
		}
		super.onDestroy();
	}

	//******************************************* Inner Class *******************************************/
	class DownloadRunnable implements Runnable {
		public static final String ID = "com.xiaotian.framework.service.ServiceDownloadFile.DownloadRunnable.ID";
		//
		File savedFile;
		Notification mNotification; // 重复notify的Notification通知对象,用于刷新作用
		int mIcon, notificationId;
		DownloadRunnable downloader;
		String url, title, fileName;
		long notificationTime;
		boolean canceled, cancelAble, autoOpen, existReDownload;

		public DownloadRunnable(Intent intent) {
			url = intent.getStringExtra(EXTRA_PARAM_URL);
			title = intent.getStringExtra(EXTRA_PARAM_TITLE);
			fileName = intent.getStringExtra(EXTRA_PARAM_SAVE_FILE_NAME);
			autoOpen = intent.getBooleanExtra(EXTRA_PARAM_AUTO_OPEN, false);
			cancelAble = intent.getBooleanExtra(EXTRA_PARAM_CANCELABLE, false);
			existReDownload = intent.getBooleanExtra(EXTRA_PARAM_EXIST_REDOWNLOAD, true);
			mIcon = intent.getIntExtra(EXTRA_PARAM_ICON, R.drawable.xiaotian_ic_launcher);
			notificationId = intent.getIntExtra(ID, 0);
			//
			if (fileName == null) fileName = mUtilFile.getFilename(url);
			mNotification = new Notification();
		}

		@Override
		public void run() {
			disableConnectionReuseIfNecessary();
			HttpURLConnection urlConnection = null;
			BufferedOutputStream out = null;
			BufferedInputStream in = null;
			OutputStream outputStream = null;
			File cacheOutFile = null;
			savedFile = new File(downloadFolder, fileName);
			try {
				if (savedFile.exists() && !existReDownload) {
					sendNotificationDownloadFinish();
					return;
				}
				cacheOutFile = new File(downloadFolder, UtilFile.getInstance().randomFileName("tmp", 20));
				// 创建临时文件,用于缓冲下载过程,如果异常退出则文件无效
				outputStream = new FileOutputStream(cacheOutFile);
				final URL mURL = new URL(url); // URL访问资源
				urlConnection = (HttpURLConnection) mURL.openConnection();
				in = new BufferedInputStream(urlConnection.getInputStream(), 2048);
				out = new BufferedOutputStream(outputStream, 2048);
				int hasReaded = 0;
				long readedByte = 0;
				long totalByte = urlConnection.getContentLength(); // Total Bytes
				byte[] bbuf = new byte[512];
				sendNotificationDownLoadStart();
				while ((hasReaded = in.read(bbuf)) != -1) { // 读取数据
					readedByte += hasReaded;
					out.write(bbuf, 0, hasReaded); // 写入读取数据
					if (canceled) {
						cacheOutFile.delete();
						mNotificationManager.cancel(notificationId);
						return;
					} else {
						if (System.currentTimeMillis() - notificationTime > 300) {
							sendNotificationDownLoading(totalByte, readedByte);
							notificationTime = System.currentTimeMillis();
						}
					}
				}
				if (savedFile.exists()) savedFile.delete();
				cacheOutFile.renameTo(savedFile);
				sendNotificationDownloadFinish();
			} catch (final IOException e) {
				mNotificationManager.cancel(notificationId);
				if (cacheOutFile != null) cacheOutFile.delete();
				UtilToastBroadcast.sendPublicToast(mContext, "下载失败,请重试...");
				Mylog.printStackTrace(e);
			} catch (final Exception e) {
				mNotificationManager.cancel(notificationId);
				if (cacheOutFile != null) cacheOutFile.delete();
				UtilToastBroadcast.sendPublicToast(mContext, "下载错误,请重试...");
				Mylog.printStackTrace(e);
			} finally {
				if (urlConnection != null) urlConnection.disconnect();
				try {
					if (out != null) out.close();
					if (in != null) in.close();
				} catch (final IOException e) {
					Mylog.printStackTrace(e);
					mNotificationManager.cancel(notificationId);
				}
			}
		}

		public void cancel() {
			synchronized (LOCK) {
				this.canceled = true;
			}
		}

		void sendNotificationDownLoadStart() {
			RemoteViews contentView = new RemoteViews(mContext.getPackageName(), R.layout.notification_download_file);
			mNotification.icon = mIcon;
			mNotification.tickerText = fileName != null ? String.format("正在下载%1$s", fileName) : mContext.getString(R.string.string_notification_downloading_start); // 弹出顶部标签文本
			mNotification.contentView = contentView;
			mNotification.contentView.setImageViewResource(R.id.id_1, mIcon);
			mNotification.contentView.setTextViewText(R.id.id_2, title != null ? title : String.format(Locale.CHINA, "正在下载%1$s", fileName));
			mNotification.contentView.setProgressBar(R.id.id_4, 100, 0, false);
			mNotificationManager.notify(notificationId, mNotification);
		}

		void sendNotificationDownLoading(long total, long current) {
			Intent intent = new Intent();
			intent.setAction(BRCancelDownload.ACTION);
			//
			mNotification.contentView = new RemoteViews(mContext.getPackageName(), R.layout.notification_download_file);
			mNotification.contentView.setImageViewResource(R.id.id_1, mIcon);
			mNotification.contentView.setTextViewText(R.id.id_2, title != null ? title : String.format(Locale.CHINA, "正在下载%1$s", fileName));
			if (cancelAble) {
				intent.putExtra(BRCancelDownload.NOTIFICATION_ID, notificationId);
				mNotification.contentView.setTextViewText(R.id.id_3, "下载进度  " + String.format("%1$s/%2$s", mUtilFile.formatMemorySize(current), mUtilFile.formatMemorySize(total)) + " 点击取消下载");
			} else {
				mNotification.contentView.setTextViewText(R.id.id_3, "下载进度  " + String.format("%1$s/%2$s", mUtilFile.formatMemorySize(current), mUtilFile.formatMemorySize(total)));
			}
			mNotification.contentView.setProgressBar(R.id.id_4, (int) total, (int) current, false);
			// Intent的Extras 必须在getBroadcast前设置
			PendingIntent contentIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0); // 挂起接收器,挂起Intent会被系统缓冲,同一个Action只有创建一次实例
			mNotification.contentView.setOnClickPendingIntent(R.id.id_0, contentIntent);
			mNotificationManager.notify(notificationId, mNotification);
		}

		void sendNotificationDownloadFinish() {
			PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, getOpenFileIntent(), 0); // Android会缓冲PaddingIntent的同一个Action维持一个实例挂起
			// 新建,替换Notification
			Notification notification = new Notification();
			notification.icon = mIcon;
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			notification.contentView = new RemoteViews(mContext.getPackageName(), R.layout.notification_download_file);
			notification.contentView.setImageViewResource(R.id.id_1, mIcon);// 设置ICON
			notification.contentView.setProgressBar(R.id.id_4, 100, 100, false);// 设置进度条
			notification.contentView.setTextViewText(R.id.id_2, title != null ? title : fileName); // 设置文本
			notification.contentView.setTextViewText(R.id.id_3, mContext.getText(R.string.string_notification_downloading_finish)); // 设置文本
			notification.defaults = Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND;
			notification.tickerText = mContext.getString(R.string.string_notification_downloading_success);
			notification.contentIntent = contentIntent;
			mNotificationManager.notify(notificationId, notification);
			arrayDownloader.remove(notificationId);
			// 下载完成打开
			if (autoOpen) {
				mContext.startActivity(getOpenFileIntent());
				mNotificationManager.cancel(notificationId);
			}
		}

		void disableConnectionReuseIfNecessary() {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
				System.setProperty("http.keepAlive", "false"); // 当前SDK系统属性
			}
		}

		Intent getOpenFileIntent() {
			Uri fileUri = Uri.fromFile(savedFile);
			String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(fileUri.toString());// URI扩展名
			String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension); // 扩展名的MIME
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(fileUri, mimetype);
			return intent;
		}
	}

	int getNotificationId() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		int m = c.get(Calendar.MINUTE);
		int s = c.get(Calendar.SECOND);
		int ms = c.get(Calendar.MILLISECOND);
		return Integer.parseInt(String.format(Locale.CHINA, "4%1$d%2$d%3$d", m, s, ms));
	}

	// 取消下载侦听器
	private class BRCancelDownload extends BroadcastReceiver {
		public static final String ACTION = "com.xiaotian.framework.service.ServiceDownloadFile.BRCancelDownload";
		public static final String NOTIFICATION_ID = "com.xiaotian.framework.service.NOTIFICATION_ID";

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.hasExtra(NOTIFICATION_ID)) {
				int id = intent.getIntExtra(NOTIFICATION_ID, -1);
				DownloadRunnable downloader = arrayDownloader.get(id);
				Mylog.info(id);
				Mylog.info(downloader);
				if (downloader != null) downloader.cancel();
			}
		}
	}
}
