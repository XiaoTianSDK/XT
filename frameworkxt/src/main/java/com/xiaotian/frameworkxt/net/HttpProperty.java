package com.xiaotian.frameworkxt.net;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import com.xiaotian.frameworkxt.android.common.Mylog;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name HttpMIME
 * @description Http Request Content Property Type And Value
 * @date 2014-4-2
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class HttpProperty {
	public static class Header {
		public static class Property {
			// header fields
			public static final String ACCEPT = "Accept";
			public static final String ACCEPT_CHARSET = "Accept-Charset";
			public static final String ACCEPT_ENCODING = "Accept-Encoding";
			public static final String ACCEPT_LANGUAGE = "Accept-Language";
			public static final String AUTHORIZATION = "Authorization";
			public static final String FROM = "From";
			public static final String HOST = "Host";
			public static final String RANGE = "Range";
			public static final String EXPECT = "Expect";
			public static final String REFERER = "Referer";
			public static final String USER_AGENT = "User-Agent";
			public static final String MAX_FORWARDS = "Max-Forwards";
			public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";
			public static final String TE = "TE";
			public static final String IF_RANGE = "If-Range";
			public static final String IF_MATCH = "If-Match";
			public static final String IF_NONE_MATCH = "If-None-Match";
			public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
			public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
			// header fields for Entity
			public static final String DATE = "Date";
			public static final String ALLOW = "Allow";
			public static final String EXPIRES = "Expires";
			public static final String CHARSET = "Charset";
			public static final String CONNECTION = "Connection";
			public static final String CONTENT_MD5 = "Content-MD5";
			public static final String CONTENT_TYPE = "Content-Type";
			public static final String LAST_MODIFIED = "Last-Modified";
			public static final String CONTENT_RANGE = "Content-Range";
			public static final String CONTENT_LENGTH = "Content-Length";
			public static final String CONTENT_ENCODING = "Content-Encoding";
			public static final String CONTENT_LANGUAGE = "Content-Language";
			public static final String CONTENT_LOCATION = "Content-Location";
			public static final String CONTENT_DISPOSITION = "Content-Disposition";
			public static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
			// Media
			public static final String BOUNDARY = "boundary";
		}
	}

	// PROPERTY_CONTENT_TYPE
	public static class ContentType {
		// Content Type MIME
		// mime 1.0 (Media Type Name / Media Subtype Name)
		public static final String TEXT_PLAN = "text/plain";
		public static final String TEXT_HTML = "text/html";
		public static final String IMAGE_GIF = "image/gif";
		public static final String IMAGE_PNG = "image/png";
		public static final String IMAGE_JPG = "image/jpeg";
		public static final String VIDEO_MPEG = "video/mpeg";
		public static final String MESSAGE_HTTP = "message/http";
		public static final String MESSAGE_RFC822 = "message/rfc822";
		public static final String APPLICATION_PDF = "application/pdf";
		public static final String APPLICATION_JSON = "application/json";
		public static final String APPLICATION_WORD = "application/msword";
		public static final String APPLICATION_XHTML = "application/xhtml+xml";
		public static final String APPLICATION_WAP_HTML_10 = "application/vnd.wap.xhtml+xml";
		public static final String APPLICATION_WAP_HTML_20 = "application/xhtml+xml";
		public static final String APPLICATION_STREAM = "application/octet-stream";
		public static final String MULTIPART_ALTERNATIVE = "multipart/alternative";
		public static final String MULTIPART_FORM_DATA = "multipart/form-data";
		public static final String MULTIPART_BYTERANGES = "multipart/byteranges";
		public static final String APPLICATION_FORM_URLENCODEED = "application/x-www-form-urlencoded;charset=utf-8";
		// 必要参数:
		// **1.message/http**
		// Media Type name: message
		// Media subtype name: http
		// Required parameters: none
		// Optional parameters: version, msgtype
		// version: The HTTP-Version number of the enclosed message
		// (e.g., "1.1"). If not present, the version can be
		// determined from the first line of the body.
		// msgtype: The message type -- "request" or "response". If not
		// present, the type can be determined from the first
		// line of the body.
		// Encoding considerations: only "7bit", "8bit", or "binary" are
		// permitted
		// Security considerations: none
		// **2.application/http**
		// Media Type name: application
		// Media subtype name: http
		// Required parameters: none
		// Optional parameters: version, msgtype
		// version: The HTTP-Version number of the enclosed messages
		// (e.g., "1.1"). If not present, the version can be
		// determined from the first line of the body.
		// msgtype: The message type -- "request" or "response". If not
		// present, the type can be determined from the first
		// line of the body.
		// Encoding considerations: HTTP messages enclosed by this type
		// are in "binary" format; use of an appropriate
		// Content-Transfer-Encoding is required when
		// transmitted via E-mail.
		// Security considerations: none
		// 3.**multipart/byteranges**
		// Media Type name: multipart
		// Media subtype name: byteranges
		// Required parameters: boundary
		// Optional parameters: none
		// Encoding considerations: only "7bit", "8bit", or "binary" are
		// permitted
		// Security considerations: none
		// ######范例:#####
		// Date: Wed, 15 Nov 1995 06:25:24 GMT
		// Last-Modified: Wed, 15 Nov 1995 04:58:08 GMT
		// Content-type: multipart/byteranges; boundary=THIS_STRING_SEPARATES
		//
		// --THIS_STRING_SEPARATES
		// Content-type: application/pdf
		// Content-range: bytes 500-999/8000
		//
		// ...the first range...
		// --THIS_STRING_SEPARATES
		// Content-type: application/pdf
		// Content-range: bytes 7000-7999/8000
		//
		// ...the second range
		// --THIS_STRING_SEPARATES--

	}

	//
	public static class TransferEncoding {
		public static final String BIT_7 = "7bit"; // 7 BIT ASCII
		public static final String BIT_8 = "8bit"; // 8 BIT ASCII
		public static final String BINARY = "binary";
		public static final String BASE64 = "base64";
		public static final String QUOTED_PRINTABLE = "quoted-printable";
	}

	//
	public static class Charset {
		public static final String GBK = "GBK";
		public static final String UTF_8 = "UTF-8";
	}

	//
	public static class Method {
		public static final String GET = "GET";
		public static final String PUT = "PUT";
		public static final String POST = "POST";
		public static final String HEAD = "HEAD";
		public static final String TRACE = "TRACE";
		public static final String DELETE = "DELETE";
		public static final String CONNECT = "CONNECT";
		public static final String OPTIONS = "OPTIONS";
	}

	//
	public static class Accept {
		public static final String ALL = "*/*";
		public static final String TEXT = "text/*";
		public static final String TEXT_HTML = "text/html";
		public static final String TEXT_HTML_level_1 = "text/html;level=1";
		public static final String IMAGE = "image/*";
		public static final String IMAGE_jpeg = "image/jpeg";
	}

	public static class AcceptEncoding {
		public static String ALL = "*";
		public static String COMPRESS_GZIP = "compress, gzip";
	}

	public static class ConetentEncoding {
		public static String GZIP = "gzip";
	}

	public static class Date {
		public static String Current() {
			DateFormat dt = DateFormat.getDateTimeInstance();
			dt.setTimeZone(TimeZone.getTimeZone("GMT"));
			Calendar c = Calendar.getInstance(Locale.CHINA);
			c.setTimeInMillis(System.currentTimeMillis());
			return dt.format(c.getTime());
		}
	}

	public static class ETag {
		public static String XYZZY = "\"xyzzy\"";
		public static String WXYZZY = "W/\"xyzzy\"";
	}

	public static class From {
		public static String ME = "gtrstudio@qq.com";
	}

	public static class Host {
		public static String ME = "www.xiaotiangd.com";
	}

	public static class UserAgent {
		public static String IE = "IntentExplorer";
		public static String Chrome = "Chrome";
		public static String ANDROID = "Android";
		public static String CERN = "CERN-LineMode/2.15 libwww/2.17b3";
	}

	public static class ContentTransferEncoding {
		public static String QUOTED_PRINTABLE = "quoted-printable";
		public static String BASE64 = "base64";
	}

	public static class ContentDisposition {
		public static String ATTACHMENT_FILENAME = "attachment; filename=\"fname.ext\"";
	}

	public static class Language {
		public static String EN = "en";
		public static String EN_US = "en-US";
		public static String CN = "cn";
		public static String ZH_CN = "zh-CN";
	}

	public static class Connection {
		public static String KEEP_ALIVE = "keep-alive";
	}

	// 编码
	//	apply to application/x-www-form-urlencoded MIME content type
	public static String encodeURLString(String data) {
		// All characters except letters ('a'..'z', 'A'..'Z') and numbers ('0'..'9') and characters '.', '-', '*', '_'
		// are converted into their hexadecimal value prepended by '%, eg: # -> %23
		try {
			return URLEncoder.encode(data, Charset.UTF_8);
		} catch (UnsupportedEncodingException e) {
			Mylog.printStackTrace(e);
		}
		return null;
	}

	// 解码
	public static String decodeURLString(String data) {
		try {
			return URLDecoder.decode(data, Charset.UTF_8);
		} catch (UnsupportedEncodingException e) {
			Mylog.printStackTrace(e);
		}
		return null;
	}

	// **General Header Fields**
	// general-header = Cache-Control ; Section 14.9
	// | Connection ; Section 14.10
	// | Date ; Section 14.18
	// | Pragma ; Section 14.32
	// | Trailer ; Section 14.40
	// | Transfer-Encoding ; Section 14.41
	// | Upgrade ; Section 14.42
	// | Via ; Section 14.45
	// | Warning ; Section 14.46
	// **Request Header Fields**
	// request-header = Accept ; Section 14.1
	// | Accept-Charset ; Section 14.2
	// | Accept-Encoding ; Section 14.3
	// | Accept-Language ; Section 14.4
	// | Authorization ; Section 14.8
	// | Expect ; Section 14.20
	// | From ; Section 14.22
	// | Host ; Section 14.23
	// | If-Match ; Section 14.24
	// | If-Modified-Since ; Section 14.25
	// | If-None-Match ; Section 14.26
	// | If-Range ; Section 14.27
	// | If-Unmodified-Since ; Section 14.28
	// | Max-Forwards ; Section 14.31
	// | Proxy-Authorization ; Section 14.34
	// | Range ; Section 14.35
	// | Referer ; Section 14.36
	// | TE ; Section 14.39
	// | User-Agent ; Section 14.43
	// **Status Code and Reason Phrase**
	// - 1xx: Informational - Request received, continuing process
	//
	// - 2xx: Success - The action was successfully received,
	// understood, and accepted
	//
	// - 3xx: Redirection - Further action must be taken in order to
	// complete the request
	//
	// - 4xx: Client Error - The request contains bad syntax or cannot
	// be fulfilled
	//
	// - 5xx: Server Error - The server failed to fulfill an apparently
	// valid request Status-Code = "100" ; Section 10.1.1: Continue
	// | "101" ; Section 10.1.2: Switching Protocols
	// | "200" ; Section 10.2.1: OK
	// | "201" ; Section 10.2.2: Created
	// | "202" ; Section 10.2.3: Accepted
	// | "203" ; Section 10.2.4: Non-Authoritative Information
	// | "204" ; Section 10.2.5: No Content
	// | "205" ; Section 10.2.6: Reset Content
	// | "206" ; Section 10.2.7: Partial Content
	// | "300" ; Section 10.3.1: Multiple Choices
	// | "301" ; Section 10.3.2: Moved Permanently
	// | "302" ; Section 10.3.3: Found
	// | "303" ; Section 10.3.4: See Other
	// | "304" ; Section 10.3.5: Not Modified
	// | "305" ; Section 10.3.6: Use Proxy
	// | "307" ; Section 10.3.8: Temporary Redirect
	// | "400" ; Section 10.4.1: Bad Request
	// | "401" ; Section 10.4.2: Unauthorized
	// | "402" ; Section 10.4.3: Payment Required
	// | "403" ; Section 10.4.4: Forbidden
	// | "404" ; Section 10.4.5: Not Found
	// | "405" ; Section 10.4.6: Method Not Allowed
	// | "406" ; Section 10.4.7: Not Acceptable
	// | "407" ; Section 10.4.8: Proxy Authentication Required
	// | "408" ; Section 10.4.9: Request Time-out
	// | "409" ; Section 10.4.10: Conflict
	// | "410" ; Section 10.4.11: Gone
	// | "411" ; Section 10.4.12: Length Required
	// | "412" ; Section 10.4.13: Precondition Failed
	// | "413" ; Section 10.4.14: Request Entity Too Large
	// | "414" ; Section 10.4.15: Request-URI Too Large
	// | "415" ; Section 10.4.16: Unsupported Media Type
	// | "416" ; Section 10.4.17: Requested range not satisfiable
	// | "417" ; Section 10.4.18: Expectation Failed
	// | "500" ; Section 10.5.1: Internal Server Error
	// | "501" ; Section 10.5.2: Not Implemented
	// | "502" ; Section 10.5.3: Bad Gateway
	// | "503" ; Section 10.5.4: Service Unavailable
	// | "504" ; Section 10.5.5: Gateway Time-out
	// | "505" ; Section 10.5.6: HTTP Version not supported
	// **Response Header Fields**
	// response-header = Accept-Ranges ; Section 14.5
	// | Age ; Section 14.6
	// | ETag ; Section 14.19
	// | Location ; Section 14.30
	// | Proxy-Authenticate ; Section 14.33
	// **Entity Header Fields**
	// entity-header = Allow ; Section 14.7
	// | Content-Encoding ; Section 14.11
	// | Content-Language ; Section 14.12
	// | Content-Length ; Section 14.13
	// | Content-Location ; Section 14.14
	// | Content-MD5 ; Section 14.15
	// | Content-Range ; Section 14.16
	// | Content-Type ; Section 14.17
	// | Expires ; Section 14.21
	// | Last-Modified ; Section 14.29
	// | extension-header
}
