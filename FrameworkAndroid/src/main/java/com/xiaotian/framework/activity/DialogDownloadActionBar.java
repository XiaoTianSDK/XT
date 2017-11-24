package com.xiaotian.framework.activity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xiaotian.framework.R;
import com.xiaotian.framework.common.Mylog;
import com.xiaotian.frameworkxt.android.util.UtilAsyncTask;
import com.xiaotian.frameworkxt.android.util.UtilEnvironment;
import com.xiaotian.frameworkxt.util.UtilFile;

/**
 * @version 1.0.0
 * @author Administrator
 * @name DialogDownloadActionBar
 * @description 下载对话框
 * @date 2015-3-23
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class DialogDownloadActionBar extends BaseActivity {
	public static final String EXTRA_PARAM_URL = "com.xiaotian.framework.activity.URL";
	public static final String EXTRA_PARAM_SAVE_PATH = "com.xiaotian.framework.activity.SAVE_PATH";
	public static final String EXTRA_PARAM_CANCELABLE = "com.xiaotian.framework.activity.CANCELABLE";
	String url, savePath;
	Boolean cancelAble;
	File saveFile;
	ProgressBar mProgressBar;
	TextView textTitl, textLeft, textRight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		url = getIntent().getStringExtra(EXTRA_PARAM_URL); // 下载URL
		savePath = getIntent().getStringExtra(EXTRA_PARAM_SAVE_PATH); // 保存文件Path
		cancelAble = getIntent().getBooleanExtra(EXTRA_PARAM_CANCELABLE, false); // 是否可返回隐藏对话框
		if (savePath == null) {
			saveFile = new File(UtilEnvironment.getExternalStorageDirectory(), UtilFile.getInstance().getFileNameExtend(url));
		} else {
			saveFile = new File(savePath);
		}
		setFinishOnTouchOutside(cancelAble);
		setContentView(R.layout.dialog_download_actionbar);
		textTitl = (TextView) findViewById(R.id.id_0);
		mProgressBar = (ProgressBar) findViewById(R.id.id_1);
		textLeft = (TextView) findViewById(R.id.id_2);
		textRight = (TextView) findViewById(R.id.id_3);
		UtilAsyncTask.executeAsyncTask(new AsyncTask<Object, Integer, File>() {
			int totalBytes, currentBytes;
			File tempFile;

			@Override
			protected File doInBackground(Object... params) {
				String fileUrl = (String) params[0];
				File filePath = (File) params[1];
				HttpURLConnection urlConnection = null;
				BufferedOutputStream out = null;
				BufferedInputStream in = null;
				try {
					tempFile = File.createTempFile("nvp", null);
					final URL url = new URL(fileUrl);
					urlConnection = (HttpURLConnection) url.openConnection();
					in = new BufferedInputStream(urlConnection.getInputStream(), 1024);
					out = new BufferedOutputStream(new FileOutputStream(tempFile));
					int hasReaded;
					byte[] bbuf = new byte[512];
					totalBytes = urlConnection.getContentLength();
					while ((hasReaded = in.read(bbuf)) != -1) {// 读取数据
						publishProgress(totalBytes, currentBytes += hasReaded);
						out.write(bbuf, 0, hasReaded); // 写入读取数据
					}
					publishProgress(totalBytes, totalBytes);
					return filePath;
				} catch (final IOException e) {
					Mylog.printStackTrace(e);
					return null;
				} finally {
					if (urlConnection != null) urlConnection.disconnect();
					try {
						if (out != null) out.close();
						if (in != null) in.close();
					} catch (final IOException e) {}
				}
			}

			@Override
			protected void onPostExecute(File result) {
				if (result != null) {
					// 拷贝临时文件
					byte[] bbuf = new byte[1024];
					int hasRead;
					BufferedOutputStream out = null;
					BufferedInputStream in = null;
					try {
						in = new BufferedInputStream(new FileInputStream(tempFile));
						out = new BufferedOutputStream(new FileOutputStream(saveFile));
						while ((hasRead = in.read(bbuf)) != -1) {
							out.write(bbuf, 0, hasRead);
						}
					} catch (FileNotFoundException e) {
						Mylog.printStackTrace(e);
						saveFile.deleteOnExit();
					} catch (IOException e) {
						Mylog.printStackTrace(e);
						saveFile.deleteOnExit();
					} finally {
						try {
							tempFile.deleteOnExit();
							if (in != null) in.close();
							if (out != null) out.close();
						} catch (IOException e) {}
					}
					if (!saveFile.exists()) return;
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setDataAndType(Uri.fromFile(saveFile), "application/vnd.android.package-archive");
					startActivity(intent);
				}
				finish();
			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				float cb = currentBytes;
				float tb = totalBytes;
				if (mProgressBar.getMax() != totalBytes) mProgressBar.setMax(totalBytes);
				mProgressBar.setProgress(currentBytes);
				textLeft.setText(String.format("%1$.0f%%", cb / tb * 100));
				textRight.setText(String.format("%1$.0f/%2$.0f Kb", cb / 1000, tb / 1000));
			}
		}, url, saveFile);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (cancelAble) {
				return super.onKeyDown(keyCode, event);
			} else {
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
