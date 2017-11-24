package com.xiaotian.frameworkxt.android.common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.util.Log;

public class MyLogger {
	// Config Log Information
	protected String TAG = "FrameworkXT";
	protected boolean DEBUG = true;
	protected boolean INFOR = true;
	protected boolean ERROR = true;
	private StringBuffer stringBuffer;

	// Info Message
	public void info(String message) {
		if (INFOR) info(TAG, message);
	}

	public void info(Object message) {
		if (INFOR) {
			if (message instanceof Object[]) {
				Object[] msg = (Object[]) message;
				Log.i(TAG, "length = " + msg.length + "{");
				for (Object obj : msg)
					info(TAG, obj);
				Log.i(TAG, "}");
			} else if (message instanceof String[]) {
				String[] msg = (String[]) message;
				Log.i(TAG, "length = " + msg.length + "{");
				for (String obj : msg)
					info(TAG, obj);
				Log.i(TAG, "}");
			} else if (message instanceof int[]) {
				int[] msg = (int[]) message;
				Log.i(TAG, "length = " + msg.length + "{");
				for (int obj : msg)
					info(TAG, obj);
				Log.i(TAG, "}");
			} else if (message instanceof Collection<?>) {
				Collection<?> collection = (Collection<?>) message;
				Log.i(TAG, "size = " + collection.size() + " : {");
				for (Object obj : collection)
					info(TAG, obj);
				Log.i(TAG, "}");
			} else {
				info(TAG, message);
			}
		}
	}

	public void info(Object[] message, Boolean... newLine) {
		if (INFOR) if (newLine.length > 0 && newLine[0]) info(TAG, message, true);
		else info(TAG, message, false);
	}

	// Process
	public void info(String TAG, String message) {
		if (INFOR) Log.i(TAG, message == null ? "null" : message);
	}

	public void info(String TAG, Object message) {
		if (INFOR) Log.i(TAG, message == null ? "null" : message.toString());
	}

	public void info(String key, Object... value) {
		StringBuilder sb = new StringBuilder(key);
		sb.append(" : [");
		for (Object v : value) {
			sb.append(v.toString());
			if (v == value[value.length - 1]) {
				break;
			}
			sb.append(",");
		}
		sb.append("]");
		info(sb.toString());
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

	public void info(String TAG, String name, String value) {
		if (INFOR) Log.i(TAG, name + " : " + value);
	}

	public void info(String TAG, String name, Object value) {
		if (INFOR) Log.i(TAG, name + " : " + (value instanceof String ? (String) value : value.toString()));
	}

	public void info(String TAG, Object[] messages, Boolean newLine) {
		if (INFOR) {
			StringBuilder sb = null;
			if (messages == null) Log.i(TAG, "null");
			else {
				if (newLine) {
					Log.i(TAG, "{");
					for (Object message : messages) {
						Log.i(TAG, "\t" + message instanceof String ? (String) message : message.toString());
					}
					Log.i(TAG, "}");
				} else {
					sb = messages.length < 1 ? null : ((sb = new StringBuilder()) != null ? sb.append("[") : null);
					for (Object message : messages) {
						sb.append(message.toString());
						sb.append(messages[messages.length - 1].equals(message) ? "" : ",");
					}
					sb = sb != null ? sb.append("]") : null;
					Log.i(TAG, sb == null ? "null" : sb.toString());
				}
			}
		}
	}

	public void infoShortNewLine(String data) {
		if (INFOR) {
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

	private Long time;

	public void resetTime() {
		time = System.currentTimeMillis();
	}

	public void infoTime() {
		infoTime("Wast time");

	}

	public void infoClassField(Object data) {
		if (INFOR) {
			if (data == null) {
				info("null");
				return;
			}
			String name = data.getClass().getName();
			if (data instanceof List) {
				List<?> list = (List<?>) data;
				info(name + " size = " + list.size() + " [");
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
						if (field != fields[fields.length - 1]) {
							sb.append(",");
						}
					}
					sb.append("}");
					info(sb.toString());
				}
				info("]");
			} else if (data instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<Object, Object> map = (Map<Object, Object>) data;
				info(name + " size = " + map.size() + " [");
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
						if (field != fields[fields.length - 1]) {
							sb.append(",");
						}
					}
					sb.append("}");
					info(sb.toString());
				}
				info("]");
			} else {
				info(getClassExtendTree(data) + getInstanceInformaion(data));
			}
		}
	}

	public void infoTime(String key) {
		if (INFOR) {
			info(key + ":" + formatTime(System.currentTimeMillis() - time));
		}
		time = System.currentTimeMillis();
	}

	private String formatTime(long time) {
		return String.format(Locale.CHINA, "%1$d MS", time);
	}

	// Debug Message
	public void d(String TAG, Object message) {
		if (DEBUG) Log.i(TAG, message == null ? "null" : message.toString());
	}

	public void d(Object message) {
		if (DEBUG) {
			if (message instanceof Object[]) {
				Object[] msg = (Object[]) message;
				Log.d(TAG, "length = " + msg.length + "{");
				for (Object obj : msg)
					d(TAG, obj);
				Log.d(TAG, "}");
			} else if (message instanceof String[]) {
				String[] msg = (String[]) message;
				Log.d(TAG, "length = " + msg.length + "{");
				for (String obj : msg)
					d(TAG, obj);
				Log.d(TAG, "}");
			} else if (message instanceof int[]) {
				int[] msg = (int[]) message;
				Log.d(TAG, "length = " + msg.length + "{");
				for (int obj : msg)
					d(TAG, obj);
				Log.d(TAG, "}");
			} else if (message instanceof Collection<?>) {
				Collection<?> collection = (Collection<?>) message;
				Log.d(TAG, "size = " + collection.size() + " : {");
				for (Object obj : collection)
					d(TAG, obj);
				Log.d(TAG, "}");
			} else {
				d(TAG, message);
			}
		}
	}

	public void dClassField(Object instance) {

	}

	// Error Message
	public void e(String TAG, Object message) {
		if (ERROR) Log.e(TAG, message == null ? "null" : message.toString());
	}

	public void e(Object message) {
		if (ERROR) {
			if (message instanceof Object[]) {
				Object[] msg = (Object[]) message;
				Log.e(TAG, "length = " + msg.length + "{");
				for (Object obj : msg)
					e(TAG, obj);
				Log.e(TAG, "}");
			} else if (message instanceof String[]) {
				String[] msg = (String[]) message;
				Log.e(TAG, "length = " + msg.length + "{");
				for (String obj : msg)
					e(TAG, obj);
				Log.e(TAG, "}");
			} else if (message instanceof int[]) {
				int[] msg = (int[]) message;
				Log.e(TAG, "length = " + msg.length + "{");
				for (int obj : msg)
					e(TAG, obj);
				Log.e(TAG, "}");
			} else if (message instanceof Collection<?>) {
				Collection<?> collection = (Collection<?>) message;
				Log.e(TAG, "size = " + collection.size() + " : {");
				for (Object obj : collection)
					e(TAG, obj);
				Log.e(TAG, "}");
			} else {
				e(TAG, message);
			}
		}
	}

	public void setTAG(String TAG) {
		if (TAG == null) return;
		this.TAG = TAG;
	}

	public void setTAG(Class<?> TAGClass) {
		if (TAGClass == null) return;
		TAG = TAGClass.getName();
	}

	public void setInfo(boolean info) {
		this.INFOR = info;
	}

	public void setDebug(boolean debug) {
		this.DEBUG = debug;
	}

	public void setError(boolean error) {
		this.ERROR = error;
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
		if (ERROR) e.printStackTrace();
	}
}
