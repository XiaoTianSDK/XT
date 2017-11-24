package com.xiaotian.frameworkxt.util;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @name UtilPatternMarcher
 * @description
 * @date Nov 1, 2014
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2009-2014 广州隽永贸易科技 Ltd, All Rights Reserved.
 */
public class UtilPatternMatcher {
    public boolean match(String pattern, String text) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    public Matcher matchResult(String pattern, String text) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        return m;
    }

    public MatchResult matchResultFirst(String pattern, String text) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        if (m.find()) {
            return m.toMatchResult();
        }
        return null;
    }

    public MatchResult matchResultEnd(String pattern, String text) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        MatchResult result = null;
        while (m.find()) {
            result = m.toMatchResult();
        }
        return result;
    }

    // 10年目前国家号码段分配如下：
    // 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    // 联通：130、131、132、152、155、156、185、186
    // 电信：133、153、180、189、181（1349卫通）
    public boolean matchMobileCN(String mobileNumber) {
        // 创建模式匹配器,匹配前缀+8位数字
        // ^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$
        // ^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,1,3,4,5-9]))\\d{8}$");
        // 创建匹配器
        Matcher m = p.matcher(mobileNumber);
        // 匹配
        return m.matches();
    }

    //1+10位,
    public boolean matchPhoneNumber(String mobileNumber) {
        Pattern p = Pattern.compile("1([\\d]{10})|((\\+[0-9]{2,4})?\\(?[0-9]+\\)?-?)?[0-9]{7,8}");
        Matcher m = p.matcher(mobileNumber);
        return m.matches();
    }

    public boolean matchEmail(String email) {
        Pattern p = Pattern.compile("^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public boolean matchURL(String url) {
        Pattern p = Pattern.compile("^(http|https|www|ftp|)?(://)?(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*((:\\d+)?)(/(\\w+(-\\w+)*))*(\\.?(\\w)*)(\\?)?(((\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*" +
                "(\\w*&)*(\\w*-)*(\\w*=)*(\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*)*(\\w*)*)$");
        Matcher m = p.matcher(url);
        return m.matches();
    }

    public boolean matchNumberOrCharacter(String text) {
        Pattern p = Pattern.compile("^[0-9A-Za-z]+$");
        Matcher m = p.matcher(text);
        return m.matches();
    }

    public boolean matchNumberOrCharacter(String text, int minLength, int maxLength) {
        Pattern p = Pattern.compile("^[0-9A-Za-z]{" + minLength + "," + maxLength + "}$");
        Matcher m = p.matcher(text);
        return m.matches();
    }

    // 01、"^\\d+$"　　 //非负整数（正整数 + 0）
    // 02、"^[0-9]*[1-9][0-9]*$"　　 //正整数
    // 03、"^((-\\d+)|(0+))$"　　 //非正整数（负整数 + 0）
    // 04、"^-[0-9]*[1-9][0-9]*$"　　//负整数
    // 05、"^-?\\d+$"　　　　 //整数
    // 06、"^\\d+(\\.\\d+)?$"　　 //非负浮点数（正浮点数 + 0）
    // 07、"^(([0-9]+\\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\\.[0-9]+)|([0-9]*[1-9][0-9]*))$"　　//正浮点数
    // 08、"^((-\\d+(\\.\\d+)?)|(0+(\\.0+)?))$"　　//非正浮点数（负浮点数 + 0）
    // 09、"^(-(([0-9]+\\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\\.[0-9]+)|([0-9]*[1-9][0-9]*)))$"　　//负浮点数
    // 10、"^(-?\\d+)(\\.\\d+)?$"　　//浮点数
    // 11、"^[A-Za-z]+$"　　 //由26个英文字母组成的字符串
    // 12、"^[A-Z]+$"　　 //由26个英文字母的大写组成的字符串
    // 13、"^[a-z]+$"　　 //由26个英文字母的小写组成的字符串
    // 14、"^[A-Za-z0-9]+$"　　 //由数字和26个英文字母组成的字符串
    // 15、"^\\w+$"　　 //由数字、26个英文字母或者下划线组成的字符串
    // 16、"^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$"　　　　//email地址
    //
    // 1.^ : 匹配字符串开始,如果设置匹配多行,则匹配\n或\r之后的位置
    // 2.$ : 匹配字符串结束位置,如果设置匹配多行,则匹配\n或\r之前的位置
    // 3.\ : 将下一个字符标记为一个特殊字符、或一个原义字符、或一个向后引用、或一个八进制转义符 例如:\\n匹配\n,\n匹配换行符,\\匹配\,\(匹配(
    // 4.* : 匹配前面的子表达式零次或多次(大于等于0次)。例如，zo*能匹配“z”，“zo”以及“zoo”。*等价于{0,}
    // 5.+ : 匹配前面的子表达式一次或多次(大于等于1次）。例如，“zo+”能匹配“zo”以及“zoo”，但不能匹配“z”。+等价于{1,}
    // 6.? : 匹配前面的子表达式零次或一次。例如，“do(es)?”可以匹配“do”或“does”中的“do”。?等价于{0,1}
    // 7.. : 匹配除“\r\n”之外的任何单个字符。要匹配包括“\r\n”在内的任何字符，请使用像“[\s\S]”的模式
    // 8.{n} : n是一个非负整数。匹配确定的n次。例如，“o{2}”不能匹配“Bob”中的“o”，但是能匹配“food”中的两个o
    // 9.{n,} : n是一个非负整数。至少匹配n次。例如，“o{2,}”不能匹配“Bob”中的“o”，但能匹配“foooood”中的所有o。“o{1,}”等价于“o+”。“o{0,}”则等价于“o*”
    // 10.{n,m} : m和n均为非负整数，其中n<=m。最少匹配n次且最多匹配m次。例如，“o{1,3}”将匹配“fooooood”中的前三个o。“o{0,1}”等价于“o?”。请注意在逗号和两个数之间不能有空格
    // 11.? : 当该字符紧跟在任何一个其他限制符（*,+,?，{n}，{n,}，{n,m}）后面时，匹配模式是非贪婪的。非贪婪模式尽可能少的匹配所搜索的字符串，而默认的贪婪模式则尽可能多的匹配所搜索的字符串。例如，对于字符串“oooo”，“o+?”将匹配单个“o”，而“o+”将匹配所有“o”
    // 12.(pattern) : 匹配pattern并获取这一匹配。所获取的匹配可以从产生的Matches集合得到，在VBScript中使用SubMatches集合，在JScript中则使用$0…$9属性。要匹配圆括号字符，请使用“\(”或“\)”
    // 13.(?:pattern) : 匹配pattern但不获取匹配结果，也就是说这是一个非获取匹配，不进行存储供以后使用。这在使用或字符“(|)”来组合一个模式的各个部分是很有用。例如“industr(?:y|ies)”就是一个比“industry|industries”更简略的表达式
    // 14.(?=pattern) :
    // 正向肯定预查，在任何匹配pattern的字符串开始处匹配查找字符串。这是一个非获取匹配，也就是说，该匹配不需要获取供以后使用。例如，“Windows(?=95|98|NT|2000)”能匹配“Windows2000”中的“Windows”，但不能匹配“Windows3
    // .1”中的“Windows”。预查不消耗字符，也就是说，在一个匹配发生后，在最后一次匹配之后立即开始下一次匹配的搜索，而不是从包含预查的字符之后开始
    // 15.(?!pattern) : 正向否定预查，在任何不匹配pattern的字符串开始处匹配查找字符串。这是一个非获取匹配，也就是说，该匹配不需要获取供以后使用。例如“Windows(?!95|98|NT|2000)”能匹配“Windows3.1”中的“Windows”，但不能匹配“Windows2000”中的“Windows”
    // 16.(?<=pattern) : 反向肯定预查，与正向肯定预查类似，只是方向相反。例如，“(?<=95|98|NT|2000)Windows”能匹配“2000Windows”中的“Windows”，但不能匹配“3.1Windows”中的“Windows”。
    // 17.(?<!pattern) : 反向否定预查，与正向否定预查类似，只是方向相反。例如“(?<!95|98|NT|2000)Windows”能匹配“3.1Windows”中的“Windows”，但不能匹配“2000Windows”中的“Windows”
    // 18.x|y : 匹配x或y。例如，“z|food”能匹配“z”或“food”。“(z|f)ood”则匹配“zood”或“food”
    // 19.[xyz] : 字符集合。匹配所包含的任意一个字符。例如，“[abc]”可以匹配“plain”中的“a”
    // 20.[^xyz] : 负值字符集合。匹配未包含的任意字符。例如，“[^abc]”可以匹配“plain”中的“plin”
    // 21.[a-z] : 字符范围。匹配指定范围内的任意字符。例如，“[a-z]”可以匹配“a”到“z”范围内的任意小写字母字符。 注意:只有连字符在字符组内部时,并且出现在两个字符之间时,才能表示字符的范围; 如果出字符组的开头,则只能表示连字符本身
    // 22.[^a-z] : 负值字符范围。匹配任何不在指定范围内的任意字符。例如，“[^a-z]”可以匹配任何不在“a”到“z”范围内的任意字符
    // 23.\b : 匹配一个单词边界，也就是指单词和空格间的位置。例如，“er\b”可以匹配“never”中的“er”，但不能匹配“verb”中的“er”
    // 24.\B : 匹配非单词边界。“er\B”能匹配“verb”中的“er”，但不能匹配“never”中的“er”
    // 25.\cx : 匹配由x指明的控制字符。例如，\cM匹配一个Control-M或回车符。x的值必须为A-Z或a-z之一。否则，将c视为一个原义的“c”字符
    // 26.\d : 匹配一个数字字符。等价于[0-9]
    // 27.\D : 匹配一个非数字字符。等价于[^0-9]
    // 28.\f : 匹配一个换页符。等价于\x0c和\cL
    // 29.\n : 匹配一个换行符。等价于\x0a和\cJ
    // 30.\r : 匹配一个回车符。等价于\x0d和\cM
    // 31.\s : 匹配任何空白字符，包括空格、制表符、换页符等等。等价于[ \f\n\r\t\v]
    // 32.\S : 匹配任何非空白字符。等价于[^ \f\n\r\t\v]
    // 33.\t : 匹配一个制表符。等价于\x09和\cI
    // 34.\v : 匹配一个垂直制表符。等价于\x0b和\cK
    // 35.\w : 匹配包括下划线的任何单词字符。等价于“[A-Za-z0-9_]”
    // 36.\W : 匹配任何非单词字符。等价于“[^A-Za-z0-9_]”
    // 37.\xn : 匹配n，其中n为十六进制转义值。十六进制转义值必须为确定的两个数字长。例如，“\x41”匹配“A”。“\x041”则等价于“\x04&1”。正则表达式中可以使用ASCII编码
    // 38.\num : 匹配num，其中num是一个正整数。对所获取的匹配的引用。例如，“(.)\1”匹配两个连续的相同字符
    // 39.\n : 标识一个八进制转义值或一个向后引用。如果\n之前至少n个获取的子表达式，则n为向后引用。否则，如果n为八进制数字（0-7），则n为一个八进制转义值
    // 40.\nm : 标识一个八进制转义值或一个向后引用。如果\nm之前至少有nm个获得子表达式，则nm为向后引用。如果\nm之前至少有n个获取，则n为一个后跟文字m的向后引用。如果前面的条件都不满足，若n和m均为八进制数字（0-7），则\nm将匹配八进制转义值nm
    // 41.\nml : 如果n为八进制数字（0-7），且m和l均为八进制数字（0-7），则匹配八进制转义值nml
    // 42.\u0000 : 匹配n，其中n是一个用四个十六进制数字表示的Unicode字符。例如，\u00A9匹配版权符号（&copy;）
    // 43.\< \> : 匹配词（word）的开始（\<）和结束（\>）。例如正则表达式\<the\>能够匹配字符串"for the wise"中的"the"，但是不能匹配字符串"otherwise"中的"the"。注意：这个元字符不是所有的软件都支持的
    // 44.\( \) : 将 \( 和 \) 之间的表达式定义为“组”（group），并且将匹配这个表达式的字符保存到一个临时区域（一个正则表达式中最多可以保存9个），它们可以用 \1 到\9 的符号来引用
    // 45.| : 将两个匹配条件进行逻辑“或”（Or）运算。例如正则表达式(him|her) 匹配"it belongs to him"和"it belongs to her"，但是不能匹配"it belongs to them."。注意：这个元字符不是所有的软件都支持的
    // 46.+ : 匹配1或多个正好在它之前的那个字符。例如正则表达式9+匹配9、99、999等。注意：这个元字符不是所有的软件都支持的
    // 47.? : 匹配0或1个正好在它之前的那个字符。注意：这个元字符不是所有的软件都支持的
    // 48.{i} {i,j} : 匹配指定数目的字符，这些字符是在它之前的表达式定义的。例如正则表达式A[0-9]{3} 能够匹配字符"A"后面跟着正好3个数字字符的串，例如A123、A348等，但是不匹配A1234。而正则表达式[0-9]{4,6} 匹配连续的任意4个、5个或者6个数字
    // 正在表达式专用字符:
    // there are 12 characters with special meanings:
    // the backslash \
    // the caret ^
    // the dollar sign $
    // the period or dot .
    // the vertical bar or pipe symbol |
    // the question mark ?
    // the asterisk or star *
    // the plus sign +
    // the opening parenthesis (
    // the closing parenthesis )
    // and the opening square bracket [
    // the opening curly brace {
    // These special characters are often called "metacharacters".
    // 要用这些字符时可以用正则表达式转义: \\,例如: \\\,\\^,\\$,\\.或者用Pattern里面的转义方法:Pattern.quote("|")
}
