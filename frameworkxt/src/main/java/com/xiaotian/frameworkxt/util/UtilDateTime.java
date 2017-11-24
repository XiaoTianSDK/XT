package com.xiaotian.frameworkxt.util;

import com.xiaotian.frameworkxt.android.common.Mylog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @name UtilDateTime
 * @description 日期时间操作Util
 * @date 2013-10-31
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
public class UtilDateTime {
    public static final String PATTERN_DATE = "yyyy-MM-DD";
    private static long clickFastTimeCache = System.currentTimeMillis();
    static UtilDateTime util;

    public static UtilDateTime getInstance(long milliseconds) {
        if (util == null) util = new UtilDateTime();
        return util;

    }

    public Calendar getCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
    }

    public Calendar getCalendar(TimeZone zone) {
        return Calendar.getInstance(zone);
    }

    public Calendar getCalendar(Locale locale) {
        return Calendar.getInstance(locale);
    }

    // 日期:不包含时间
    public Calendar getCurrentDate() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.set(Calendar.HOUR_OF_DAY, 0); // H
        calendar.set(Calendar.MINUTE, 0);// M
        calendar.set(Calendar.SECOND, 0);// S
        calendar.set(Calendar.MILLISECOND, 0);// MS
        return calendar;
    }

    // 日期+时间
    public Calendar getCurrentCurrentDateTime() {
        return Calendar.getInstance(Locale.CHINA);
    }

    public Long getCurrentMillis() {
        return System.currentTimeMillis();
    }

    // 获取今天[日期不包含时间]的毫秒数
    public Long getTodayExactDay() {
        return getCurrentDate().getTimeInMillis();
    }

    public Long getDayExactDay(int day) {
        Calendar c = getCurrentDate();
        c.add(Calendar.DAY_OF_MONTH, day);
        return c.getTimeInMillis();
    }

    public String getCurrentMillisString() {
        return String.valueOf(getCurrentMillis());
    }

    public String getCurrentDateString() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        return String.format(Locale.getDefault(), "%1$tY-%1tm-%1td", calendar);
    }

    public String getCurrentTimeString() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        return String.format(Locale.getDefault(), "%1$tT", calendar);
    }

    // Simple 全数字的简单形式
    public String getCurrentSimpleDateTimeString() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        return String.format(Locale.getDefault(), "%1$tY%<tm%<td%<tH%<tM%<tS%<tL", calendar);
    }

    public String getCurrentSimpleDateString() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        return String.format(Locale.getDefault(), "%1$tY%<tm%<td", calendar);
    }

    public String getCurrentSimpleTimeString() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        return String.format(Locale.getDefault(), "%1$tH%<tM%<tS%<tL", calendar);
    }

    // String 语法格式化日期时间[String格式化器]
    public String formatDate(String pattern, Long milliseconds) {
        if (milliseconds == null || pattern == null) throw new RuntimeException("Input Pattern or Milliseconds can't null.");
        Calendar c = Calendar.getInstance(Locale.CHINA);
        c.setTimeInMillis(milliseconds);
        return formatDate(pattern, c);
    }

    public String formatDate(String pattern, Calendar c) {
        return String.format(Locale.CHINA, pattern, c);
    }

    public String formatDate(String pattern, Date date) {
        Calendar c = Calendar.getInstance(Locale.CHINA);
        c.setTime(date);
        return String.format(Locale.CHINA, pattern, c);
    }

    public String formatDateFunny(long date) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date);
        return formatDateFunny(c);
    }

    public String formatDateFunny(Date date) {
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return formatDateFunny(c);
    }

    public String formatDateFunny(Calendar date) {
        if (date == null) {
            return null;
        }
        Calendar cs = Calendar.getInstance();
        cs.setTimeInMillis(System.currentTimeMillis());
        cs.add(Calendar.MINUTE, -10);
        // 10分钟之内,显示刚刚
        if (cs.before(date)) {
            return "刚刚";
        }
        // 一个小时内,显示多少分钟前
        for (int i = 10; i < 60; i += 10) {
            cs.add(Calendar.MINUTE, -10);
            if (cs.before(date)) {
                return String.format(Locale.CHINA, "%1$d分钟前", i);
            }
        }
        // 一天内,显示几点几分
        cs.set(Calendar.HOUR_OF_DAY, 0);
        cs.set(Calendar.MINUTE, 0);
        cs.set(Calendar.SECOND, 0);
        if (cs.before(date)) {
            return String.format(Locale.CHINA, "%1$tH:%1$tM", date.getTime());
        }
        // 昨天显示昨天
        cs.add(Calendar.DAY_OF_MONTH, -1);
        if (cs.before(date)) {
            return "昨天";
        }
        // 其他显示日期
        return String.format(Locale.CHINA, "%1$tm月%1$td日", date.getTime());
    }

    // Date 语法格式日期时间
    public Date parseDate(String pattern, String date) {
        SimpleDateFormat sf = new SimpleDateFormat(pattern);
        try {
            return sf.parse(date);
        } catch (ParseException e) {
            Mylog.printStackTrace(e);
        }
        return null;
    }

    public Date parseDate(long milliseconds) {
        Calendar c = Calendar.getInstance(Locale.CHINA);
        c.setTimeInMillis(milliseconds);
        return c.getTime();
    }

    public String parseString(String pattern, Date date) {
        SimpleDateFormat sf = new SimpleDateFormat(pattern);
        return sf.format(date);
    }

    public String parseString(String pattern, long milliseconds) {
        SimpleDateFormat sf = new SimpleDateFormat(pattern);
        Calendar c = Calendar.getInstance(Locale.CHINA);
        c.setTimeInMillis(milliseconds);
        return sf.format(c.getTime());
    }

    public static boolean isClickFast() {
        long current = System.currentTimeMillis();
        if (current - clickFastTimeCache < 500) {
            return true;
        }
        clickFastTimeCache = current;
        return false;
    }
    // String 字符串匹配表达式
    // 语法格式:
    // %[argument_index$][flags][width][.precision]conversion
    // argument_index: 引用参数的索引start 1[第一个参数: 1$]
    // flags: 声明输出的格式[由conversion进行类型转换]
    // width: 声明添加字符串的长度
    // precision: 声明浮点数的精度
    // conversion: [必须]声明参数的转换类型
    // conversion
    // 转换器类型b/B:boolean[String.value(arg)],
    // h/H:十六进制[Integer.toHexString(arg)],
    // s/S:字符串[arg.toString()]
    // c/C: unicode character
    // d: dicimal integer 十进制整数
    // o: octal integer 八进制整数
    // x/X :十六进制整数
    // e/E :科学计数法整数
    // f: 十进制浮点数
    // g/G :科学计数法或小数
    // a/A :指数类型的科学计数法
    // t/T : 日期时间
    // %: %百分号计数
    // n: 新行
    // Mylog.info(String.format("%1$t", c));
    // t/T[..]: 时间加二成转换器->[tH,tI...]
    // H :时,24时制[01-24]
    // I :时,12时制[01-12]
    // k :时,24时制[0-23](小写k)
    // l :时,12时制[0-12](小写L)
    // M :分
    // S :秒
    // L :毫秒 Millisecond[000-999],当前时间毫秒
    // N :微秒 Nanosecond [00000000-99999999]
    // p :上下午[同时使用大写T,则转换为大写AM/PM]
    // z :时区
    // Q :毫秒[统计从1970 00:00:00开始的毫秒数]
    // B :月份[全称]
    // b :月份[简称]
    // h :与b相同
    // A :周中的天[全称,sunday]
    // a :周中的天[简称]
    // Y :年[1970]
    // y :年[70]
    // j :年中的天[001-366]
    // m :月[01-13]
    // d :月中的天[01-31]
    // e :月中的天[1-31]
    // 系统组合转换器
    // R :24时制 H:M -> %tH:%tM
    // T :24时制 H:M:S -> %tH:%tM:%tS
    // r :12时制 H:M:S am/pm -> "%tI:%tM:%tS %Tp"
    // D :日期 y:m:d -> %tm%td%ty
    // F :日期 Y:m:d -> "%tY-%tm-%td"
    // c :日期和时间 -> %ta %tb %td %tT %tZ %tY
    // Mylog.info(String.format("%1$tT", c));

    // Flag
    // - :左对齐 lift-justified[所有类型转换都支持],默认靠右
    // + :输出+号[支持: o,x,X,F]
    // ' ': 前缀加空格[支持: d,o,x,X,F]
    // 0 : 用0填充[d,f] 001,0001
    // , : 用,号隔开分位 [d,f]
    // ( : 如果是负数则用括号括起来[d,o,x,X,e,E,f,g,G]
    // # : 使用转换器的交互格式[默认格式][o,f,x,X]
    // Mylog.info(String.format("Now is %1$-20tT !", c));
    // Mylog.info(String.format("Now is %1$#10f !", 120.32));
    // 精度 .precision [支持: e,E,f,g,G]
    // Mylog.info(String.format("the number is %1$#10.2f", 120.3236))

    // argument index [参数索引]
    // Mylog.info(String.format("the number is %1$#10.2f, 名字:%2$s",120.3236,"Xiaotian"));
    // < '\u003c' 索引[重复引用上一次参数::代替 1$]
    // Mylog.info(String.format(String.format("Duke's Brithday: %1$tm %<te,%<tY",
    // c)));
    // [省略]系统自动选择index[同引用 -> 同索引参数,如果参数不够,抛异常]
    // Mylog.info(String.format(String.format("params : %-5s,%-5s,%-5s,%-5s","S1","S2","S3","S4")));
    // < 重复引用,同时系统选择(系统从1开始计数,与引用没影响::两者不影响)
    // Mylog.info(String.format(String.format("params : %1$-5s,%<-5s,%s,%s,%s,%<s",
    // "S1","S2","S3","S4")));
    // %,n : 无参数转换器
    // Mylog.info(String.format(String.format("This is %n a")));

    // SimpleDateTime 语法匹配格式
    // yyyy: 四位年
    // yy : 两位年
    // MM : 两位月
    // dd : 两位日
    // hh : 两位时
    // mm : 两位分
    // ss : 两位秒
}
