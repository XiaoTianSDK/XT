package com.xiaotian.framework.util;

import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaRecorder;

import com.xiaotian.framework.common.Mylog;

/**
 * @version 1.0.0
 * @author mac
 * @name UtilRecorderRadio
 * @description MIC录音
 * @date Nov 7, 2014
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class UtilRecorder {
	public static final int SAMPLING_RATE = 44100;
	protected MediaRecorder mediaRecorder;
	protected MediaPlayer mediaPlayer;

	public UtilRecorder() {}

	// 3gp 格式File
	public void startRecording(String saveFile) {
		mediaRecorder = new MediaRecorder();
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mediaRecorder.setOutputFile(saveFile);
		try {
			mediaRecorder.prepare();
		} catch (IOException e) {
			e.printStackTrace();
			Mylog.info("UtilRecorderRadio", "mediaRecorder prepare.");
		}
		mediaRecorder.start();
	}

	public void stopRecording() {
		if (mediaRecorder == null) return;
		mediaRecorder.stop();
		mediaRecorder.release();
		mediaRecorder = null;
	}

	public void startPlaying(String mediaFile) {
		try {
			mediaPlayer.setDataSource(mediaFile);
			mediaPlayer.setLooping(false);
			mediaPlayer.prepare();
			mediaPlayer.start();
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					Mylog.info("UtilRecorderRadio", "RadioPlayer Completion.");
				}
			});
			mediaPlayer.setOnErrorListener(new OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					Mylog.info("UtilRecorderRadio", "RadioPlayer Error.");
					return false;
				}
			});
			mediaPlayer.setOnInfoListener(new OnInfoListener() {
				@Override
				public boolean onInfo(MediaPlayer mp, int what, int extra) {
					Mylog.info("UtilRecorderRadio", "RadioPlayer Info.");
					return false;
				}
			});
		} catch (IOException e) {
			Mylog.info("UtilRecorderRadio", "RadioPlayer IOException.");
		}
	}

	public void stopPlaying() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}
		mediaPlayer = null;
	}

	public MediaRecorder getMediaRecorder() {
		return mediaRecorder;
	}

	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

}
