package com.xiaotian.frameworkxt.serializer.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name XMLDocumentation
 * @description XML Documentation Container
 * @date 2014-9-22
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class XMLDocumentation {
	// Entity Mush Be Mapped By @XMLEntity
	/**
	 * 序列化的时候Map不限制嵌套层数
	 */
	private String rootTag = "document";
	private String encoding = "UTF-8";
	private String version = "1.0";

	// Root -> Attribute;
	private Map<String, String> mMapAttribute = new HashMap<String, String>();
	// List -> Entry 对象类型
	private List<Object> mListData = new ArrayList<Object>();
	// Map Key -> value 基本类型
	private Map<String, Object> mMapData = new HashMap<String, Object>();

	public XMLDocumentation() {}

	public XMLDocumentation(String version, String encoding, String rootTag) {
		this.encoding = encoding;
		this.version = version;
		this.rootTag = rootTag;
	}

	// Put An Object
	public void put(Object object) {
		if (object == null) return;
		mListData.add(object);
	}

	// Put An Object
	public void put(String tagName, XMLElementContainer entity) {
		mMapData.put(tagName, entity);
	}

	// Put An String
	public void put(String tagName, String value) {
		mMapData.put(tagName, value);
	}

	public void put(String tagName, Integer value) {
		mMapData.put(tagName, value);
	}

	public void put(String tagName, Boolean value) {
		mMapData.put(tagName, value);
	}

	public void put(String tagName, Float value) {
		mMapData.put(tagName, value);
	}

	public void put(String tagName, Double value) {
		mMapData.put(tagName, value);
	}

	public void put(String tagName, Short value) {
		mMapData.put(tagName, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String tagName) {
		return (T) mMapData.get(tagName);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> get() {
		return mListData == null ? null : (List<T>) mListData;
	}

	public void addAttribute(String tagName, Object value) {
		mMapAttribute.put(tagName, String.valueOf(value));
	}

	// Get Declaration
	public String getDeclaration() {
		return "<?xml version=\"" + version + "\" encoding=\"" + encoding + "\"?>";
	}

	// GET/SET
	public String getRootTag() {
		return rootTag;
	}

	public void setRootTag(String rootTag) {
		this.rootTag = rootTag;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public List<Object> getListData() {
		return mListData;
	}

	public Map<String, Object> getMapData() {
		return mMapData;
	}

	public Map<String, String> getMapAttribute() {
		return mMapAttribute;
	}

	// Override toString
	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		Iterator<String> iteAttrsKey = mMapAttribute.keySet().iterator();
		while (iteAttrsKey.hasNext()) {
			String key = iteAttrsKey.next();
			String value = mMapAttribute.get(key);
			stringBuffer.append(key);
			stringBuffer.append("=");
			stringBuffer.append(value);
			if (iteAttrsKey.hasNext()) stringBuffer.append(",");
		}
		Iterator<Object> iteList = mListData.iterator();
		while (iteList.hasNext()) {
			Object entry = iteList.next();
			stringBuffer.append(entry.toString());
			if (iteAttrsKey.hasNext()) stringBuffer.append(",");
		}
		Iterator<String> iteDataKey = mMapData.keySet().iterator();
		while (iteDataKey.hasNext()) {
			String key = iteDataKey.next();
			Object value = mMapData.get(key);
			stringBuffer.append(key);
			stringBuffer.append("=");
			stringBuffer.append(value == null ? "null" : value.toString());
			if (iteAttrsKey.hasNext()) stringBuffer.append(",");
		}
		return stringBuffer.toString();
	}

	// Contain Element
	public static class XMLElementContainer extends XMLDocumentation {
		// XML Element Container
		// 1.基本数据类型放到Map中
		// 2.对象类型放到List中
	}
}
