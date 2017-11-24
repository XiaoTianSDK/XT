package com.xiaotian.framework.widget.pattern;

import java.util.ArrayList;

import com.xiaotian.framework.widget.pattern.Point;

public class UtilPatternPoint {
	public static String toString(ArrayList<Point> pattern) {
		if (pattern == null) return null;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < pattern.size(); i++) {
			Point point = pattern.get(i);
			sb.append(point.x);
			sb.append(",");
			sb.append(point.y);
			if (point != pattern.get(pattern.size() - 1)) {
				sb.append("-");
			}
		}
		return sb.toString();
	}

	public static ArrayList<Point> pastPatternPoint(String patternString) {
		if (patternString == null) return null;
		ArrayList<Point> pattern = new ArrayList<Point>();
		String[] pss = patternString.split("-");
		for (String s : pss) {
			String[] ss = s.split(",");
			pattern.add(new Point(Integer.parseInt(ss[0]), Integer.parseInt(ss[1])));
		}
		return pattern;
	}
}
