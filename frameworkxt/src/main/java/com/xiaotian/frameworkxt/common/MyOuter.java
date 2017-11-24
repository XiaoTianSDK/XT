package com.xiaotian.frameworkxt.common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name MyOuter
 * @description System out print Util
 * @date Nov 29, 2014
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2009-2014 广州隽永贸易科技 Ltd, All Rights Reserved.
 */
public class MyOuter {
	public static final String TAG = "XiaoTianXT";
	public boolean isPrintout = true;
	private StringBuffer stringBuffer;

	public void setIsPrintout(boolean isPrintout) {
		this.isPrintout = isPrintout;
	}

	public void info(String message) {
		if (isPrintout) System.out.println(message);
	}

	public void info(String TAG, String message) {
		if (isPrintout) System.out.println(String.format("%1$s　　%2$s", TAG, message));
	}

	public void info(String TAG, Object message) {
		if (isPrintout) info(TAG, message.toString());
	}

	public void info(Object message) {
		if (isPrintout) {
			if (message instanceof Object[]) {
				Object[] msg = (Object[]) message;
				info(TAG, "length = " + msg.length + "{");
				for (Object obj : msg)
					info(TAG, obj);
				info(TAG, "}");
			} else if (message instanceof String[]) {
				String[] msg = (String[]) message;
				info(TAG, "length = " + msg.length + "{");
				for (String obj : msg)
					info(TAG, obj);
				info(TAG, "}");
			} else if (message instanceof int[]) {
				int[] msg = (int[]) message;
				info(TAG, "length = " + msg.length + "{");
				for (int obj : msg)
					info(TAG, obj);
				info(TAG, "}");
			} else if (message instanceof Collection<?>) {
				Collection<?> collection = (Collection<?>) message;
				info(TAG, "size = " + collection.size() + " : {");
				for (Object obj : collection)
					info(TAG, obj);
				info(TAG, "}");
			} else {
				info(TAG, message);
			}
		}
	}

	public void info(String key, String... value) {
		StringBuilder sb = new StringBuilder(key);
		sb.append(" : [");
		for (String v : value) {
			sb.append(v);
			sb.append(",");
		}
		sb.append("]");
		info(sb.toString());
	}

	public void info(String key, Object... value) {
		StringBuilder sb = new StringBuilder(key);
		sb.append(" : [");
		for (Object v : value) {
			sb.append(v.toString());
			if (v == value[value.length - 1]) break;
			sb.append(",");
		}
		sb.append("]");
		info(sb.toString());
	}

	public void info(String TAG, Object[] messages, Boolean newLine) {
		if (isPrintout) {
			StringBuilder sb = null;
			if (messages == null) info(TAG, "null");
			else {
				if (newLine) {
					info(TAG, "{");
					for (Object message : messages) {
						info(TAG, "\t" + message instanceof String ? (String) message : message.toString());
					}
					info(TAG, "}");
				} else {
					sb = messages.length < 1 ? null : ((sb = new StringBuilder()) != null ? sb.append("[") : null);
					for (Object message : messages) {
						sb.append(message.toString());
						sb.append(messages[messages.length - 1].equals(message) ? "" : ",");
					}
					sb = sb != null ? sb.append("]") : null;
					info(TAG, sb == null ? "null" : sb.toString());
				}
			}
		}
	}

	public void infoShortNewLine(String data) {
		if (isPrintout) {
			int start = 0;
			int length = 30;
			int end = data.length() > length ? length : data.length();
			info("{");
			info("Length: " + data.length());
			while (start < data.length() - 1) {
				info(start + " : " + data.substring(start, end));
				start = end;
				end = (end + length) > data.length() ? data.length() : (end + length);
			}
			info("}");
		}
	}

	public void info(Object[] message, Boolean... newLine) {
		if (isPrintout) if (newLine.length > 0 && newLine[0]) info(TAG, message, true);
		else info(TAG, message, false);
	}

	public void infoClassField(String TAG, Object data) {
		if (isPrintout) {
			if (data == null) {
				info(TAG, "null");
				return;
			}
			String name = data.getClass().getName();
			if (data instanceof List) {
				List<?> list = (List<?>) data;
				info(TAG, name + " size = " + list.size() + " [");
				Field[] fields = null;
				Class<?> clazz = null;
				if (list.size() > 0) {
					clazz = list.get(0).getClass();
					List<Field> listField = new ArrayList<Field>();
					while (clazz != null) {
						fields = clazz.getDeclaredFields();
						for (int i = 0; i < fields.length; i++) {
							listField.add(fields[i]);
						}
						clazz = clazz.getSuperclass();
					}
					fields = new Field[listField.size()];
					for (int i = 0; i < listField.size(); i++) {
						fields[i] = listField.get(i);
					}
				}
				for (Object dt : list) {
					StringBuilder sb = new StringBuilder("{");
					for (Field field : fields) {
						field.setAccessible(true);
						sb.append(field.getName());
						sb.append("=");
						try {
							sb.append(field.get(dt));
						} catch (IllegalArgumentException ignore) {} catch (IllegalAccessException ignore) {}
						if (field != fields[fields.length - 1]) sb.append(",");
					}
					sb.append("}");
					info(TAG, sb.toString());
				}
				info(TAG, "]");
			} else if (data instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<Object, Object> map = (Map<Object, Object>) data;
				info(TAG, name + " size = " + map.size() + " [");
				List<Field> listField = null;
				Field[] fields = null;
				Class<?> clazz = null;
				Iterator<Object> ite = map.keySet().iterator();
				while (ite.hasNext()) {
					Object key = ite.next();
					Object entry = map.get(key);
					clazz = entry.getClass();
					listField = new ArrayList<Field>();
					while (clazz != null) {
						fields = clazz.getDeclaredFields();
						for (int i = 0; i < fields.length; i++) {
							listField.add(fields[i]);
						}
						clazz = clazz.getSuperclass();
					}
					fields = new Field[listField.size()];
					for (int i = 0; i < listField.size(); i++) {
						fields[i] = listField.get(i);
					}
					StringBuilder sb = new StringBuilder(key + ":{");
					for (Field field : fields) {
						field.setAccessible(true);
						sb.append(field.getName());
						sb.append("=");
						try {
							sb.append(field.get(entry));
						} catch (IllegalArgumentException ignore) {} catch (IllegalAccessException ignore) {}
						if (field != fields[fields.length - 1]) sb.append(",");
					}
					sb.append("}");
					info(TAG, sb.toString());
				}
				info(TAG, "]");
			} else {
				info(TAG, getClassExtendTree(data) + getInstanceInformaion(data));
			}
		}
	}

	// Class Information
	public String getInstanceInformaion(Object instance) {
		// Instance All Field
		if (instance == null) return "null";
		Field[] fields = null;
		Class<?> clazz = instance.getClass();
		StringBuffer string = new StringBuffer("{");
		List<Field> listField = new ArrayList<Field>();
		while (clazz != null) {
			fields = clazz.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				listField.add(fields[i]);
			}
			clazz = clazz.getSuperclass();
		}
		for (int i = 0; i < listField.size(); i++) {
			Field field = listField.get(i);
			field.setAccessible(true);
			string.append(field.getName());
			string.append("=");
			try {
				string.append(field.get(instance));
			} catch (IllegalArgumentException ignore) {} catch (IllegalAccessException ignore) {}
			if (i <= listField.size()) string.append(",");
		}
		string.append("}");
		return string.toString();
	}

	public String getClassExtendTree(Object instance) {
		// instance->supe->......
		StringBuffer string = new StringBuffer();
		Class<?> clazz = instance.getClass();
		while (clazz != null) {
			string.append(clazz.getName());
			clazz = clazz.getSuperclass();
			if (clazz != null) string.append("->");
		}
		return string.toString();
	}

	private Long time;

	public void resetTime() {
		time = System.currentTimeMillis();
	}

	public void infoTime() {
		if (isPrintout) infoTime("Wast time");

	}

	public void infoTime(String key) {
		if (isPrintout) {
			info(key + ":" + formatTime(System.currentTimeMillis() - time));
			time = System.currentTimeMillis();
		}
	}

	private String formatTime(long time) {
		return String.format(Locale.CHINA, "%1$d MS", time);
	}

	public void cleanBuffer() {
		getStringBuffer().setLength(0);
	}

	public void append(Object data) {
		getStringBuffer().append(data);
	}

	public void infoBuffer() {
		info(getStringBuffer().toString());
	}

	public StringBuffer getStringBuffer() {
		if (stringBuffer != null) return stringBuffer;
		return stringBuffer = new StringBuffer();
	}

	public void printStackTrace(Exception e) {
		if (isPrintout) e.printStackTrace();
	}
}
