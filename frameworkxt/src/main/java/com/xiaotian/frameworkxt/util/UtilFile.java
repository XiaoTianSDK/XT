package com.xiaotian.frameworkxt.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name UtilFile
 * @description 文件操作Util
 * @date 2013-11-13
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
public class UtilFile {
	private static final Object LOCK = new Object();
	private static UtilFile util;
	protected static final char[] arrayChart = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
			'u', 'v', 'w', 'x', 'y', 'z', '_', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', };

	public static synchronized UtilFile getInstance() {
		synchronized (LOCK) {
			if (util != null) return util;
			return util = new UtilFile();
		}
	}

	public String buildRandomFileNameDateTimeRM6(String extend) {
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

	public String buildRandomFileNameDateTime(String extend, int length) {
		StringBuilder sb = new StringBuilder(32);
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		sb.append(String.format(Locale.getDefault(), "%1$tY%<tm%<td%<tH%<tM%<tS%<tL", calendar));
		for (int i = 0; i < length - sb.length(); i++) {
			int index = (int) ((Math.random() * 1000) % arrayChart.length);
			sb.append(arrayChart[index]);
		}
		if (extend != null && !extend.equals("")) {
			sb.append(".");
			sb.append(extend);
		}
		return sb.toString();
	}

	public String randomFileName(String extend, int length, String... part) {
		StringBuilder sb = new StringBuilder();
		if (part.length > 0) {
			sb.append(part[0]);
			length -= part[0].length();
		}
		// 0-9,a-Z
		while (length-- > 0) {
			int type = (int) (Math.random() * 1000 % 3);
			// 数字 48-57
			// A-Z 65-90
			// a-z 97-122
			switch (type) {
			case 0:
				sb.append((char) (48 + Math.random() * 9));
				break;
			case 1:
				sb.append((char) (65 + Math.random() * 25));
				break;
			case 2:
				sb.append((char) (97 + Math.random() * 25));
				break;
			}
		}
		if (part.length > 1) {
			sb.append(part[1]);
		}
		sb.append(".");
		sb.append(extend);
		return sb.toString();
	}

	public String getFileNameExtend(String name) {
		return name.substring(name.lastIndexOf(".") + 1);
	}

	public boolean moveFileToFolder(String file, String folder, Boolean... override) {
		// TODO 拷贝图片到指定目录
		if (file == null || folder == null) return false;
		boolean result = false;
		File fileFrom = new File(file);
		if (fileFrom.exists()) {
			File folderTo = new File(folder);
			if (!folderTo.exists()) folderTo.mkdirs();
			File fileTo = new File(folderTo, getFilename(file));
			if (fileTo.exists() && override.length > 0 && override[0] == false) return false;
			int hasReaded = 0;
			InputStream in = null;
			OutputStream out = null;
			byte[] bbuf = new byte[512];
			try {
				fileTo.createNewFile();
				in = new FileInputStream(fileFrom);
				out = new FileOutputStream(fileTo);
				while ((hasReaded = in.read(bbuf)) != -1) {
					out.write(bbuf, 0, hasReaded);
				}
				result = true;
			} catch (IOException e) {
				e.printStackTrace();
				result = false;
			} finally {
				try {
					if (in != null) in.close();
					if (out != null) {
						out.flush();
						out.close();
					}
				} catch (IOException ignore) {}
			}
		}
		// 移动完成,删除原文件
		if (result) fileFrom.deleteOnExit();
		return result;
	}

	public int moveFileToFolder(List<String> files, String folder, Boolean... override) {
		// TODO 拷贝图片到指定目录
		if (files == null || folder == null) return 0;
		int count = 0;
		File fileFrom;
		int hasReaded = 0;
		InputStream in = null;
		OutputStream out = null;
		byte[] bbuf = new byte[512];
		File folderTo = new File(folder);
		if (!folderTo.exists()) folderTo.mkdirs();
		for (String filepath : files) {
			fileFrom = new File(filepath);
			if (fileFrom.exists()) {
				File fileTo = new File(folderTo, getFilename(filepath));
				if (fileTo.equals(fileFrom)) continue;
				if (fileTo.exists() && override.length > 0 && !override[0]) {
					continue;
				}
				try {
					fileTo.createNewFile();
					in = new FileInputStream(fileFrom);
					out = new FileOutputStream(fileTo);
					while ((hasReaded = in.read(bbuf)) != -1) {
						out.write(bbuf, 0, hasReaded);
					}
					count++;
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (in != null) in.close();
						if (out != null) {
							out.flush();
							out.close();
						}
					} catch (IOException ignore) {}
				}
			}
		}
		return count;
	}

	public String getFilename(String filePath) {
		if (filePath == null) return null;
		return filePath.substring(filePath.lastIndexOf("/") + 1);
	}

	public String getCacheFileName(String data) {
		// MD5 Cache Name
		if (data == null) return null;
		return String.format("%1$s.tmp", UtilMD5.MD5(data));
	}

	public boolean copyFile(String from, String to, boolean... cover) {
		if (from == null || to == null) return false;
		File fromFile = new File(from);
		if (!fromFile.exists()) return false;
		File toFile = new File(to);
		if (cover.length > 0 && !cover[0] && fromFile.equals(toFile)) return false;
		return copyFile(fromFile, toFile, cover);
	}

	public boolean copyFile(File from, File to, boolean... cover) {
		if (from == null || to == null || !from.exists()) return false;
		if (cover.length > 0 && !cover[0] && from.getAbsolutePath().equals(to.getAbsolutePath())) return false;
		int hasReaded = 0;
		InputStream in = null;
		OutputStream out = null;
		byte[] bbuf = new byte[512];
		try {
			to.deleteOnExit();
			in = new FileInputStream(from);
			out = new FileOutputStream(to);
			while ((hasReaded = in.read(bbuf)) != -1) {
				out.write(bbuf, 0, hasReaded);
			}
			return true;
		} catch (IOException ie) {
			ie.printStackTrace();
		} finally {
			try {
				if (in != null) in.close();
				if (out != null) {
					out.flush();
					out.close();
				}
			} catch (IOException e) {}
		}
		return false;
	}

	public boolean saveToFile(String savePath, InputStream inputStream) {
		if (savePath == null || inputStream == null) return false;
		OutputStream out = null;
		File saveFile = new File(savePath);
		byte[] bbuf = new byte[256];
		int hasReaded = -1;
		try {
			if (!saveFile.exists() && !saveFile.createNewFile()) return false;
			out = new FileOutputStream(saveFile);
			while ((hasReaded = inputStream.read(bbuf)) != -1) {
				out.write(bbuf, 0, hasReaded);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (out != null) out.close();
				if (inputStream != null) inputStream.close();
			} catch (IOException e) {}
		}
		return true;
	}

	public boolean deleteField(String name) {
		File file = new File(name);
		return file.delete();
	}

	public int deleteField(List<String> names) {
		int count = 0;
		for (String name : names) {
			File file = new File(name);
			count += file.delete() ? 1 : 0;
		}
		return count;
	}

	// Copy Stream To Stream
	public void copyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1) break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {}
	}

	// 获取File的大小
	public long captureFileSize(String filePath) {
		if (filePath == null) return -1;
		File file = new File(filePath);
		if (!file.exists()) return -1;
		if (file.isDirectory()) return captureDirectorySize(0, filePath);
		return file.getTotalSpace();
	}

	// 获取文件夹大小
	protected long captureDirectorySize(long size, String directory) {
		if (directory == null) return 0;
		File file = new File(directory);
		if (!file.exists()) return 0;
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				return size + captureDirectorySize(size, f.getAbsolutePath());
			}
		}
		return size + file.getTotalSpace();
	}

	// 格式化容量大小
	public String formatMemorySize(long size) {
		// TB
		if (size >= 1099511627776l) {
			return new DecimalFormat("#.#TB").format((double) size / (double) 1099511627776l);
		}
		// GB
		if (size >= 1073741824l) {
			return new DecimalFormat("#.#GB").format((double) size / (double) 1073741824l);
		}
		// MB
		if (size >= 1048576l) {
			return new DecimalFormat("#.#MB").format((double) size / (double) 1048576l);
		}
		// KB
		if (size >= 1024l) {
			return new DecimalFormat("#.#KB").format((double) size / (double) 1024l);
		}
		// B
		return String.format("%1$dB", size);
	}
}
