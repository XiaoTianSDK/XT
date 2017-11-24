package com.xiaotian.frameworkxt.serializer.xml;

import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name XMLAtom
 * @description Atom Type
 * @date 2014-9-27
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class XMLAtom {
	Map<String, String> mAttrs;
	Object mValue;
	String mName;

	public XMLAtom() {}

	public XMLAtom(String name, Object value) {
		this.mName = name;
		this.mValue = value;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public String getName() {
		return this.mName;
	}

	public void setValue(Object value) {
		this.mValue = value;
	}

	public String getValue() {
		if (mValue == null) return null;
		return String.valueOf(mValue);
	}

	public void addAttribute(String name, String value) {
		if (mAttrs == null) mAttrs = new HashMap<String, String>();
		mAttrs.put(name, value);
	}

	public Map<String, String> getAttribute() {
		return mAttrs;
	}
}
