package com.xiaotian.framework.util;

public class UtilDateTime extends com.xiaotian.frameworkxt.util.UtilDateTime {
    public String formatFellingDateAddress(Long data, String address) {
        if (data == null) return "";
        String d = formatDate("%1$tm-%<td %<tH:%<tM", data);
        return address == null ? d : String.format("%1$s %2$s", d, address);
    }
}
