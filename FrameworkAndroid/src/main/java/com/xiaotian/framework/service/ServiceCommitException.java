package com.xiaotian.framework.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import com.xiaotian.framework.model.UncatchedException;
import com.xiaotian.frameworkxt.android.common.Mylog;
import com.xiaotian.frameworkxt.android.model.SQLException;
import com.xiaotian.frameworkxt.android.model.UtilSQLEntityAnnotation;
import com.xiaotian.frameworkxt.android.model.provider.UtilSQLContentProviderAnnotation;
import com.xiaotian.frameworkxt.util.UtilEmail;

import javax.mail.MessagingException;

/**
 * @author Administrator
 * @version 1.0.0
 * @name ServiceCommitException
 * @description 异常提交
 * @date 2015-6-12
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class ServiceCommitException extends Service {
    UtilSQLEntityAnnotation<UncatchedException> utilEntity = new UtilSQLEntityAnnotation<UncatchedException>() {
        @Override
        public Class<?> getExtendsedClass() {
            return getClass();
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new MyRunnable()).start();
        return START_STICKY;
    }

    class MyRunnable implements Runnable {

        @Override
        public void run() {
            Uri contentUri = UtilSQLContentProviderAnnotation.getContentURI(UncatchedException.class);
            String[] projects = UtilSQLEntityAnnotation.getSQLEntityProjects(UncatchedException.class);
            Cursor cursor = getContentResolver().query(contentUri, projects, null, null, "_id");
            UtilEmail utilEmail = new UtilEmail();
            UncatchedException ue = null;
            if (cursor.moveToFirst()) {
                do {
                    try {
                        ue = utilEntity.deSerialize(cursor);
                        try {
                            utilEmail.sendMailSMS(ue.getEmailSubject(), ue.getEmailContent());
                            getContentResolver().delete(contentUri, "_id=?", new String[]{String.valueOf(ue.getId())});
                        } catch (MessagingException e) {
                            Mylog.printStackTrace(e);
                        }
                    } catch (SQLException e) {
                        Mylog.printStackTrace(e);
                    } catch (Exception ex) {
                        Mylog.printStackTrace(ex);
                    }
                } while (cursor.moveToNext());
            }
        }
    }
}
