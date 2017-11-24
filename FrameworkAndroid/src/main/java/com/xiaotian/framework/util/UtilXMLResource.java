package com.xiaotian.framework.util;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name UtilXMLResource
 * @description XML Resoutce Parser
 * @date 2014-7-10
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class UtilXMLResource {
	// Read the files in : /res/xml/xxx.xml [R.xml.xxx]
	
	Context context;

	public UtilXMLResource(Context context) {
		this.context = context;
	}

	// XML Resource File To String
	public String parseXMLResource(int xmlResource) throws XmlPullParserException, IOException {
		StringBuffer sb = new StringBuffer();
		Resources res = context.getResources();
		XmlResourceParser xpp = res.getXml(xmlResource);
		xpp.next();
		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_DOCUMENT) {
				sb.append("******Start document");
			} else if (eventType == XmlPullParser.START_TAG) {
				sb.append("\nStart tag " + xpp.getName());
			} else if (eventType == XmlPullParser.END_TAG) {
				sb.append("\nEnd tag " + xpp.getName());
			} else if (eventType == XmlPullParser.TEXT) {
				sb.append("\nText " + xpp.getText());
			}
			eventType = xpp.next();
		}
		sb.append("\n******End document");
		return sb.toString();
	}
}
