package com.xiaotian.frameworkxt.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @name UtilRawResource
 * @description Raw Bittery Resource
 * @date 2014-7-10
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class UtilAppRaw {
    // Read the files in : /res/xml/xxx.xml [R.raw.xxx]
    public static final float BEEP_VOLUME = 0.1f;
    Context mContext;

    public UtilAppRaw(Context context) {
        this.mContext = context;
    }

    String getStringFromRawFile(int rawResource) throws IOException {
        Resources r = mContext.getResources();
        InputStream is = r.openRawResource(rawResource);
        String myText = convertStreamToString(is);
        is.close();
        return myText;
    }

    String convertStreamToString(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = is.read();
        while (i != -1) {
            baos.write(i);
            i = is.read();
        }
        return baos.toString();
    }

    void playRawSound(Activity activity, int soundRes) {
        // The volume on STREAM_SYSTEM is not adjustable, and users found it too loud, so we now play on the music stream.
        activity.getWindow().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.seekTo(0);
            }
        });
        AssetFileDescriptor file = activity.getResources().openRawResourceFd(soundRes);
        try {
            mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            file.close();
            mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
            mediaPlayer.prepare();
            // 播放
            mediaPlayer.start();
        } catch (IOException e) {
            mediaPlayer = null;
        }
    }

    public Context getContext() {
        return mContext;
    }
}
