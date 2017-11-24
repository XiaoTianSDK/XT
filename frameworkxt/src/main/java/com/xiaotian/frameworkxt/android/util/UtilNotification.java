package com.xiaotian.frameworkxt.android.util;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.SparseIntArray;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name UtilNotifycation
 * @description System NotifyCation
 * @date 2014-7-10
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class UtilNotification {
	private Uri mSoundUri;
	private Context mContext;
	private long mTimePreSound;
	private boolean mIsVibrate;
	private long[] mVibratorTime;
	private SparseIntArray mMessageNumber;
	private long mRemainInterspace = 2 * 60 * 1000;
	private NotificationManager mNotificationManager;

	public UtilNotification(Context context) {
		mContext = context;
		mMessageNumber = new SparseIntArray();
		mSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@TargetApi(16)
	@SuppressWarnings("deprecation")
	public void sendNotification(int id, int icon, String title, String text, Class<?> resultActivity, Intent paramIntent) {
		int number = mMessageNumber.get(id);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext).setSmallIcon(icon).setContentTitle(title).setContentText(text).setAutoCancel(true);
		if (number > 0) {
			mBuilder.setNumber(number + 1);
			mBuilder.setContentInfo(String.valueOf(number + 1));
		}
		// set the alarm sound and vibrator
		if ((System.currentTimeMillis() - mTimePreSound) > mRemainInterspace) {
			mTimePreSound = System.currentTimeMillis();
			if (mSoundUri != null) mBuilder.setSound(mSoundUri);
			if (mIsVibrate) mBuilder.setVibrate(mVibratorTime);
		}
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(mContext, resultActivity);
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		if (paramIntent != null) {
			resultIntent.setAction(paramIntent.getAction());
			resultIntent.putExtras(paramIntent);
		}
		// The stack builder object will contain an artificial back stack for
		// the started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(resultActivity);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		// mId allows you to update the notification later on.
		if (UtilSDKVersion.hasJellyBean()) {
			mNotificationManager.notify(id, mBuilder.build());
		} else {
			mNotificationManager.notify(id, mBuilder.getNotification());
		}
		mMessageNumber.put(id, number + 1);
	}

	// Getter/Setter
	public void sendNotification(int id, Notification notification) {
		mNotificationManager.notify(id, notification);
	}

	public void cancel(int id) {
		mNotificationManager.cancel(id);
	}

	public NotificationManager getNotificationManager() {
		return mNotificationManager;
	}

	public void setVibrator(long[] data) {
		mIsVibrate = true;
		mVibratorTime = data == null ? new long[] { 200, 100, 200 } : data;
	}

	public void setRemindSoundUri(Uri soundUri) {
		this.mSoundUri = soundUri;
	}

	public void setRemindInterspace(long remainInterspace) {
		this.mRemainInterspace = remainInterspace;
	}

	public void cleanNotificationNumber() {
		mMessageNumber.clear();
	}
}
