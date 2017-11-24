package com.xiaotian.frameworkxt.net;

import android.content.Context;

import com.xiaotian.frameworkxt.android.common.Mylog;
import com.xiaotian.frameworkxt.net.HttpProperty.Charset;
import com.xiaotian.frameworkxt.net.HttpProperty.ContentType;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.Certificate;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @name HttpsServerConnector
 * @description
 * @date Jan 12, 2015
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class HttpsServerConnector implements HttpConnector {
    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
    // Form Request Params
    //
    protected String boundary;
    protected HttpServer httpServer; // Cache. Server Class
    protected Method[] declareMethod;
    protected InputStream inputStream;
    protected OutputStream outputStream;
    protected HttpsURLConnection connection;
    private Map<Integer, HttpAction> mArrayCacheAction;
    private List<HttpCookie> cookieParams = new ArrayList<HttpCookie>();

    static {
        trustAllHttpsHosts();
    }

    public HttpsServerConnector() {
        mArrayCacheAction = new HashMap<Integer, HttpAction>();
        boundary = Long.toHexString(System.currentTimeMillis());
    }

    /********************************************** Base Http Request Method **********************************************/
    // Simple Request 基本HTTP请求,[URL:请求URL,Method:请求方法,URLParamsString:请求参数]
    public String sendRequest(String urlPath, String method, String params) throws HttpNetworkException {
        Mylog.info(method + ":" + urlPath);
        Mylog.info("params" + params);
        ByteArrayOutputStream bos = null;
        BufferedInputStream is = null;
        OutputStream os = null;
        try {
            connection = (HttpsURLConnection) new URL(urlPath).openConnection();
            connection.setHostnameVerifier(DO_NOT_VERIFY);
            // Header
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setDefaultUseCaches(false);
            connection.setDoOutput("POST".equalsIgnoreCase(method) ? true : false);
            connection.setRequestMethod((method == null || "".equals(method)) ? "GET" : method);
            connection.setRequestProperty("Content-Type", ContentType.APPLICATION_FORM_URLENCODEED);
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Charsert", Charset.UTF_8);
            connection.setConnectTimeout(20 * 1000);
            connection.setReadTimeout(20 * 1000);
            if (params != null) {
                connection.setChunkedStreamingMode(0);
                connection.setRequestProperty("Content-Length", String.valueOf(params.trim().getBytes().length));
            } else {
                connection.setRequestProperty("Content-Length", "0");
            }
            if (params != null && "POST".equalsIgnoreCase(method)) {
                os = connection.getOutputStream();
                os.write(params.getBytes());
                os.flush();
            }
            int hasReaded = 0;
            byte[] bbuf = new byte[1024];
            switch (connection.getResponseCode()) {
                case HttpsURLConnection.HTTP_OK:
                    is = new BufferedInputStream(connection.getInputStream());
                    bos = new ByteArrayOutputStream();
                    while ((hasReaded = is.read(bbuf)) != -1) {
                        bos.write(bbuf, 0, hasReaded);
                    }
                    break;
                default:
                    is = new BufferedInputStream(connection.getErrorStream());
                    bos = new ByteArrayOutputStream();
                    while ((hasReaded = is.read(bbuf)) != -1) {
                        bos.write(bbuf, 0, hasReaded);
                    }
                    throw new HttpNetworkException("Request failed, response code = " + connection.getResponseCode() + " " + bos);
            }
        } catch (MalformedURLException e) {
            throw new HttpNetworkException(e);
        } catch (IOException e) {
            throw new HttpNetworkException(e);
        } catch (Exception e) {
            throw new HttpNetworkException(e);
        } finally {
            try {
                if (is != null) is.close();
                if (os != null) os.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
            }
            if (connection != null) {
                connection.disconnect();
                connection = null;
            }
        }
        Mylog.info("response:" + bos);
        return bos == null ? null : bos.toString();
    }

    // 构造 POST Form 表单,包含单个文件的请求,fileName:上传文件名,file:本地文件Path,ItemParams 其他参数
    protected String sendPostFormFileRequestForString(String url, String fileName, File file, String requestContentPlanItemParams) throws HttpNetworkException {
        Mylog.info("PostFormFile:" + url);
        Mylog.info("file:" + file.getAbsolutePath());
        Mylog.info("params:" + requestContentPlanItemParams);
        InputStream fileInputStream = null;
        InputStream urlInputStream = null;
        OutputStream urlOutputStream = null;
        StringBuffer stringBuffer = new StringBuffer();
        ByteArrayOutputStream byteArrayOutputStream = null;
        byte[] bbuf = new byte[512];
        int hasReaded = -1;
        try {
            fileInputStream = new FileInputStream(file);
            connection = (HttpsURLConnection) new URL(url).openConnection();
            connection.setHostnameVerifier(DO_NOT_VERIFY);
            // Header Cookie
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setDefaultUseCaches(false);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Charsert", Charset.UTF_8);
            connection.setConnectTimeout(20 * 1000);
            connection.setReadTimeout(20 * 1000);
            // Start Request Content Boundary
            // Meansure The Content Is form urlencode
            connection.setRequestProperty("Content-Type", ContentType.MULTIPART_FORM_DATA + "; boundary=" + boundary);
            urlOutputStream = connection.getOutputStream();
            // Write RequestField Params
            urlOutputStream.write(requestContentPlanItemParams.getBytes());
            // Write Request File Param
            stringBuffer.setLength(0);
            stringBuffer.append(FORM_PREFIX);
            stringBuffer.append(boundary);
            // File Declaration
            stringBuffer.append(FORM_LINEND);
            stringBuffer.append("Content-Disposition: form-data; name=\"picturename\"; filename=\"");
            stringBuffer.append(fileName);
            stringBuffer.append("\"");
            stringBuffer.append(FORM_LINEND);
            stringBuffer.append("Content-Type: application/octet-stream; charset=");
            stringBuffer.append(Charset.UTF_8);
            stringBuffer.append(FORM_LINEND);
            urlOutputStream.write(stringBuffer.toString().getBytes());
            // Start File Byte
            urlOutputStream.write(FORM_LINEND.getBytes());
            while ((hasReaded = fileInputStream.read(bbuf)) != -1) {
                urlOutputStream.write(bbuf, 0, hasReaded);
            }
            // End File Byte
            urlOutputStream.write(FORM_LINEND.getBytes());
            // End Request Boundary
            stringBuffer.setLength(0);
            stringBuffer.append(FORM_PREFIX);
            stringBuffer.append(boundary);
            stringBuffer.append(FORM_PREFIX);
            stringBuffer.append(FORM_LINEND);
            urlOutputStream.write(stringBuffer.toString().getBytes());
            urlOutputStream.flush(); // Flush The Cache , get ResponseAsync
            switch (connection.getResponseCode()) {
                case HttpsURLConnection.HTTP_OK:
                    urlInputStream = new BufferedInputStream(connection.getInputStream());
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    while ((hasReaded = urlInputStream.read(bbuf)) != -1) {
                        byteArrayOutputStream.write(bbuf, 0, hasReaded);
                    }
                    break;
                default:
                    urlInputStream = new BufferedInputStream(connection.getErrorStream());
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    while ((hasReaded = urlInputStream.read(bbuf)) != -1) {
                        byteArrayOutputStream.write(bbuf, 0, hasReaded);
                    }
                    throw new HttpNetworkException("Request failed, response code = " + connection.getResponseCode() + " " + byteArrayOutputStream);
            }
        } catch (FileNotFoundException e) {
            throw new HttpNetworkException(e);
        } catch (IOException e) {
            throw new HttpNetworkException(e);
        } finally {
            try {
                if (fileInputStream != null) fileInputStream.close();
                if (urlOutputStream != null) urlOutputStream.close();
                if (urlInputStream != null) urlInputStream.close();
            } catch (IOException e) {
            }
            if (connection != null) {
                connection.disconnect();
                connection = null;
            }
        }
        Mylog.info("response:" + byteArrayOutputStream.toString());
        return byteArrayOutputStream == null ? null : byteArrayOutputStream.toString();
    }

    // 构造 POST Form 表单,包含单个文件的请求,fileName:上传文件名,file:本地文件Path
    // List<HttpParam> 其他参数
    public String sendPostFormFileRequestForString(String url, String fileName, File file, List<? extends HttpParam> requestParams) throws HttpNetworkException {
        StringBuffer stringBuffer = new StringBuffer();
        for (HttpParam param : requestParams) {
            stringBuffer.append(FORM_PREFIX);
            stringBuffer.append(boundary);
            stringBuffer.append(FORM_LINEND);
            stringBuffer.append("Content-Disposition: form-data; name=\"");
            stringBuffer.append(param.getName());
            stringBuffer.append("\"");
            stringBuffer.append(FORM_LINEND);
            stringBuffer.append("Content-Type: text/plain; charset=");
            stringBuffer.append(Charset.UTF_8);
            stringBuffer.append(FORM_LINEND);
            stringBuffer.append("Content-Transfer-Encoding: 8bit");
            stringBuffer.append(FORM_LINEND);
            stringBuffer.append(FORM_LINEND);
            stringBuffer.append(param.getValue());
            stringBuffer.append(FORM_LINEND);
        }
        return sendPostFormFileRequestForString(url, fileName, file, stringBuffer.toString());
    }

    // 构造 POST Form 表单,包含单个文件的请求,fileName:上传文件名,file:本地文件Path
    // T<HttpParam> 其他参数
    public <T extends HttpParam> String sendPostFormFileRequestForString(String url, String fileName, File file, T... requestParams) throws HttpNetworkException {
        StringBuffer stringBuffer = new StringBuffer();
        for (HttpParam param : requestParams) {
            stringBuffer.append(FORM_PREFIX);
            stringBuffer.append(boundary);
            stringBuffer.append(FORM_LINEND);
            stringBuffer.append("Content-Disposition: form-data; name=\"");
            stringBuffer.append(param.getName());
            stringBuffer.append("\"");
            stringBuffer.append(FORM_LINEND);
            stringBuffer.append("Content-Type: text/plain; charset=");
            stringBuffer.append(Charset.UTF_8);
            stringBuffer.append(FORM_LINEND);
            stringBuffer.append("Content-Transfer-Encoding: 8bit");
            stringBuffer.append(FORM_LINEND);
            stringBuffer.append(FORM_LINEND);
            stringBuffer.append(param.getValue());
            stringBuffer.append(FORM_LINEND);
        }
        return sendPostFormFileRequestForString(url, fileName, file, stringBuffer.toString());
    }

    // GET 获取文件 (传入文件URL,传入文件保存路径Path),返回 Boolean
    public boolean downloadFile(String fileUrl, String filePath) throws HttpNetworkException {
        Mylog.info("download file:" + filePath + " to" + fileUrl);
        if (fileUrl == null || filePath == null) return false;
        File file = new File(filePath);
        if (!file.exists()) {
            File directory = file.getParentFile();
            if (!directory.exists()) directory.mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new HttpNetworkException(e);
            }
        }
        connection = getURLGETConnection(fileUrl);
        FileOutputStream outStream = null;
        InputStream urlInputStream = null;
        byte[] bbuf = new byte[256];
        int hasReaded = -1;
        try {
            switch (connection.getResponseCode()) {
                case HttpsURLConnection.HTTP_OK:
                    urlInputStream = new BufferedInputStream(connection.getInputStream());
                    outStream = new FileOutputStream(file);
                    while ((hasReaded = urlInputStream.read(bbuf)) != -1) {
                        outStream.write(bbuf, 0, hasReaded);
                    }
                    break;
                default:
                    urlInputStream = new BufferedInputStream(connection.getErrorStream());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    while ((hasReaded = urlInputStream.read(bbuf)) != -1) {
                        baos.write(bbuf, 0, hasReaded);
                    }
                    throw new HttpNetworkException("Request failed, response code = " + connection.getResponseCode() + " " + baos);
            }
        } catch (IOException e) {
            throw new HttpNetworkException(e);
        } finally {
            try {
                if (outStream != null) outStream.close();
                if (urlInputStream != null) urlInputStream.close();
            } catch (IOException e) {
            }
            if (connection != null) {
                connection.disconnect();
                connection = null;
            }
        }
        return true;
    }

    // GET 获取文件,传入文件URL,返回 ByteArrayOutputStream
    public ByteArrayOutputStream getFile(String fileUrl) throws HttpNetworkException {
        Mylog.info("get file:" + fileUrl);
        connection = getURLGETConnection(fileUrl);
        ByteArrayOutputStream outStream = null;
        InputStream urlInputStream = null;
        byte[] bbuf = new byte[256];
        int hasReaded = -1;
        try {
            switch (connection.getResponseCode()) {
                case HttpsURLConnection.HTTP_OK:
                    urlInputStream = new BufferedInputStream(connection.getInputStream());
                    outStream = new ByteArrayOutputStream();
                    while ((hasReaded = urlInputStream.read(bbuf)) != -1) {
                        outStream.write(bbuf, 0, hasReaded);
                    }
                    break;
                default:
                    urlInputStream = new BufferedInputStream(connection.getErrorStream());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    while ((hasReaded = urlInputStream.read(bbuf)) != -1) {
                        baos.write(bbuf, 0, hasReaded);
                    }
                    throw new HttpNetworkException("Request failed, response code = " + connection.getResponseCode() + " " + baos);
            }
        } catch (IOException e) {
            throw new HttpNetworkException(e);
        } finally {
            try {
                if (outStream != null) outStream.close();
                if (urlInputStream != null) urlInputStream.close();
            } catch (IOException e) {
            }
            if (connection != null) {
                connection.disconnect();
                connection = null;
            }
        }
        return outStream;
    }

    // GET 获取文件,传入OutputStream
    public boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
        Mylog.info("download url to stream:" + urlString);
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            final URL url = new URL(urlString); // URL访问资源
            connection = (HttpsURLConnection) url.openConnection(); // 打开连接,默认GET
            connection.setHostnameVerifier(DO_NOT_VERIFY);
            in = new BufferedInputStream(connection.getInputStream(), IO_BUFFER_SIZE); // 输入流
            out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);// 缓冲输出流,接入传入的输出流中
            int hasReaded;
            while ((hasReaded = in.read()) != -1) { // 读取数据
                out.write(hasReaded); // 写入读取数据
            }
            return true;
        } catch (final IOException e) {
            System.out.println("Error in downloadUrlToStream - " + e);
        } finally {
            try {
                if (out != null) out.close();
                if (in != null) in.close();
            } catch (final IOException e) {
            }
            if (connection != null) {
                connection.disconnect();
                connection = null;
            }
        }
        return false;
    }

    /********************************************** Annotation Http Request Method **********************************************/
    // Params Request, mush init the request frist,T<HttpParam> Post请求参数[内容]
    public <T extends HttpParam> String sendAnnotatedRequest(T... postRequestParams) throws HttpNetworkException {
        if (connection == null) initAnnotatedURLConnection(postRequestParams);
        ByteArrayOutputStream bos = sendHttpRequestToByteArrayOutputStream(getHttpRequestUrlMethod(), buildURLParam(postRequestParams).toString());
        try {
            return bos == null ? null : bos.toString(Charset.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new HttpNetworkException(e);
        }
    }

    public <T extends HttpParam> String sendAnnotatedRequest(int cacheRequestKey, T... postRequestParams) throws HttpNetworkException {
        if (connection == null) initAnnotatedURLConnection(cacheRequestKey, postRequestParams);
        ByteArrayOutputStream bos = sendHttpRequestToByteArrayOutputStream(getHttpRequestUrlMethod(cacheRequestKey), buildURLParam(postRequestParams).toString());
        try {
            return bos == null ? null : bos.toString(Charset.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new HttpNetworkException(e);
        }
    }

    // Params Request, mush init the request frist,List<HttpParam>
    // Post请求参数[内容]
    public <T extends HttpParam> String sendAnnotatedRequest(List<T> postRequestParams) throws HttpNetworkException {
        if (connection == null) initAnnotatedURLConnection(postRequestParams);
        ByteArrayOutputStream bos = sendHttpRequestToByteArrayOutputStream(getHttpRequestUrlMethod(), buildURLParam(postRequestParams).toString());
        try {
            return bos == null ? null : bos.toString(Charset.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new HttpNetworkException(e);
        }
    }

    public <T extends HttpParam> String sendAnnotatedRequest(int cacheRequestKey, List<T> postRequestParams) throws HttpNetworkException {
        if (connection == null) initAnnotatedURLConnection(cacheRequestKey, postRequestParams);
        ByteArrayOutputStream bos = sendHttpRequestToByteArrayOutputStream(getHttpRequestUrlMethod(cacheRequestKey), buildURLParam(postRequestParams).toString());
        try {
            return bos == null ? null : bos.toString(Charset.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new HttpNetworkException(e);
        }
    }

    // Params Request, mush init the request frist,T<HttpParam>
    // Post Form表单请求参数
    public <T extends HttpParam> String sendAnnotatedRequestFormFormat(T... postRequestParams) throws HttpNetworkException {
        if (connection == null) initAnnotatedURLConnection(postRequestParams);
        ByteArrayOutputStream bos = sendHttpRequestToByteArrayOutputStreamByForm(connection, postRequestParams);
        try {
            return bos == null ? null : bos.toString(Charset.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new HttpNetworkException(e);
        }
    }

    public <T extends HttpParam> String sendAnnotatedRequestFormFormat(int cacheRequestKey, T... postRequestParams) throws HttpNetworkException {
        if (connection == null) initAnnotatedURLConnection(cacheRequestKey, postRequestParams);
        ByteArrayOutputStream bos = sendHttpRequestToByteArrayOutputStreamByForm(connection, postRequestParams);
        try {
            return bos == null ? null : bos.toString(Charset.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new HttpNetworkException(e);
        }
    }

    private ByteArrayOutputStream sendHttpRequestToByteArrayOutputStream(String requestMethod, String requestParams) throws HttpNetworkException {
        ByteArrayOutputStream bos = null;
        BufferedInputStream is = null;
        byte[] bbuf = new byte[1024];
        OutputStream os = null;
        int hasReaded = 0;
        try {
            connection = getConnection();
            if (requestMethod.equalsIgnoreCase(HttpAction.METHOD_POST) && requestParams != null) {
                byte[] paramByte = requestParams.getBytes(Charset.UTF_8);
                connection.setRequestProperty("Content-Length", String.valueOf(paramByte.length));
                os = connection.getOutputStream();
                os.write(paramByte);
                os.flush();
            } else if (requestMethod.equalsIgnoreCase(HttpAction.METHOD_GET)) {
                // GET ignore params
                connection.setRequestProperty("Content-Length", "0");
            } else {
                throw new RuntimeException("Method Un Support Exception!");
            }
            // 捉取Header中的Cookies
            Map<String, List<String>> cookies = connection.getHeaderFields();
            if (cookies != null && !cookies.isEmpty()) {
                Iterator<String> ite = cookies.keySet().iterator();
                while (ite.hasNext()) {
                    String key = ite.next();
                    if (key != null && key.startsWith("Set-Cookie")) {
                        List<String> values = cookies.get(key);
                        for (int i = 0; i < values.size(); i++) {
                            String cookie = values.get(i);
                            HttpCookie hc = parseCookie(cookie);
                            addCookie(hc);
                        }
                    }
                }
            }
            switch (connection.getResponseCode()) {
                case HttpsURLConnection.HTTP_OK:
                    // Connection to network success
                    is = new BufferedInputStream(connection.getInputStream());
                    bos = new ByteArrayOutputStream();
                    while ((hasReaded = is.read(bbuf)) != -1) {
                        bos.write(bbuf, 0, hasReaded);
                    }
                    break;
                default:
                    // Connection to network error
                    is = new BufferedInputStream(connection.getErrorStream());
                    bos = new ByteArrayOutputStream();
                    while ((hasReaded = is.read(bbuf)) != -1) {
                        bos.write(bbuf, 0, hasReaded);
                    }
                    throw new HttpNetworkException("Request failed, response code = " + connection.getResponseCode() + " " + bos.toString(Charset.UTF_8));
            }
        } catch (MalformedURLException e) {
            throw new HttpNetworkException(e);
        } catch (IOException e) {
            throw new HttpNetworkException(e);
        } finally {
            try {
                if (is != null) is.close();
                if (os != null) os.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                throw new HttpNetworkException(e);
            }
            if (connection != null) {
                connection.disconnect();
                connection = null;
            }
        }
        Mylog.info("response:" + bos.toString());
        return bos;
    }

    private ByteArrayOutputStream sendHttpRequestToByteArrayOutputStreamByForm(HttpURLConnection httpConnection, HttpParam... postRequestParams) throws HttpNetworkException {
        if (httpConnection == null) throw new HttpNetworkException("The HttpURLConnection Un Initialized.");
        InputStream fileInputStream = null;
        InputStream urlInputStream = null;
        OutputStream urlOutputStream = null;
        StringBuffer stringBuffer = new StringBuffer();
        ByteArrayOutputStream byteArrayOutputStream = null;
        byte[] bbuf = new byte[512];
        int hasReaded = -1;
        try {
            // Header Cookie
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod(HttpProperty.Method.POST);
            httpConnection.setRequestProperty(HttpProperty.Header.Property.CHARSET, Charset.UTF_8);
            httpConnection.setRequestProperty(HttpProperty.Header.Property.CONNECTION, HttpProperty.Connection.KEEP_ALIVE);
            httpConnection.setRequestProperty(HttpProperty.Header.Property.USER_AGENT, HttpProperty.UserAgent.ANDROID);
            httpConnection.setConnectTimeout(20 * 1000);
            httpConnection.setReadTimeout(20 * 1000);
            httpConnection.setUseCaches(false);
            httpConnection.setInstanceFollowRedirects(false);
            // Start Request Content Boundary
            // Meansure The Content Is form urlencode
            httpConnection.setRequestProperty(HttpProperty.Header.Property.CONTENT_TYPE, ContentType.MULTIPART_FORM_DATA + "; boundary=" + boundary);
            urlOutputStream = connection.getOutputStream();
            // Write RequestField Params
            urlOutputStream.write(buildPostFormFieldParam(postRequestParams).toString().getBytes(Charset.UTF_8));
            for (int i = 0; postRequestParams != null && i < postRequestParams.length; i++) {
                HttpParam httpParam = postRequestParams[i];
                if (!httpParam.isFile()) continue;
                //
                fileInputStream = new FileInputStream((File) httpParam.getValue());
                // Write Request File Param
                stringBuffer.setLength(0);
                stringBuffer.append(FORM_PREFIX);
                stringBuffer.append(boundary);
                // File Declaration
                stringBuffer.append(FORM_LINEND);
                stringBuffer.append("Content-Disposition: form-data; name=\"");
                stringBuffer.append(httpParam.getName());
                stringBuffer.append("\"; filename=\"");
                stringBuffer.append(httpParam.getFileName());
                stringBuffer.append("\"");
                stringBuffer.append(FORM_LINEND);
                stringBuffer.append("Content-Type: application/octet-stream; charset=");
                stringBuffer.append(Charset.UTF_8);
                stringBuffer.append(FORM_LINEND);
                urlOutputStream.write(stringBuffer.toString().getBytes());
                // Start File Byte
                urlOutputStream.write(FORM_LINEND.getBytes());
                while ((hasReaded = fileInputStream.read(bbuf)) != -1) {
                    urlOutputStream.write(bbuf, 0, hasReaded);
                }
                // End File Byte
                urlOutputStream.write(FORM_LINEND.getBytes());
            }
            // End Request Boundary
            stringBuffer.setLength(0);
            stringBuffer.append(FORM_PREFIX);
            stringBuffer.append(boundary);
            stringBuffer.append(FORM_PREFIX);
            stringBuffer.append(FORM_LINEND);
            urlOutputStream.write(stringBuffer.toString().getBytes());
            urlOutputStream.flush(); // Flush The Cache , get ResponseAsync
            switch (connection.getResponseCode()) {
                case HttpURLConnection.HTTP_OK:
                    urlInputStream = new BufferedInputStream(connection.getInputStream());
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    while ((hasReaded = urlInputStream.read(bbuf)) != -1) {
                        byteArrayOutputStream.write(bbuf, 0, hasReaded);
                    }
                    break;
                default:
                    urlInputStream = new BufferedInputStream(connection.getErrorStream());
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    while ((hasReaded = urlInputStream.read(bbuf)) != -1) {
                        byteArrayOutputStream.write(bbuf, 0, hasReaded);
                    }
                    throw new HttpNetworkException("Request failed, response code = " + connection.getResponseCode() + " " + byteArrayOutputStream);
            }
        } catch (FileNotFoundException e) {
            throw new HttpNetworkException(e);
        } catch (IOException e) {
            throw new HttpNetworkException(e);
        } finally {
            try {
                if (fileInputStream != null) fileInputStream.close();
                if (urlOutputStream != null) urlOutputStream.close();
                if (urlInputStream != null) urlInputStream.close();
            } catch (IOException e) {
            }
            if (connection != null) {
                connection.disconnect();
                connection = null;
            }
        }
        return byteArrayOutputStream;
    }

    // URL Connection Init
    // 构造配置[注解]访问连接器,初始化连接器
    public HttpsURLConnection initAnnotatedURLConnection() throws HttpNetworkException {
        try {
            String method = getHttpRequestUrlMethod();
            String url = getAnnotatedHttpRequestUrlString();
            if (method.equalsIgnoreCase(HttpAction.METHOD_GET)) {
                return connection = getURLGETConnection(url);
            } else if (method.equals(HttpAction.METHOD_POST)) {
                return connection = getURLPOSTConnection(url);
            } else {
                throw new HttpNetworkException("Action Method Un Supported Exception!");
            }
        } catch (Exception e) {
            throw new HttpNetworkException(e);
        }
    }

    // 构造配置[注解]访问连接器,初始化连接器[附带URL构造参数 List<HttpParam>]
    public <T extends HttpParam> HttpsURLConnection initAnnotatedURLConnection(List<T> urlParams) throws HttpNetworkException {
        return createHttpsURLConnection(buildAnnotatedURLPath().append('?').append(buildURLParam(urlParams)).toString(), getHttpRequestUrlMethod());
    }

    public <T extends HttpParam> HttpsURLConnection initAnnotatedURLConnection(int requestActionKey, List<T> urlParams) throws HttpNetworkException {
        return createHttpsURLConnection(buildAnnotatedURLPath(requestActionKey).append('?').append(buildURLParam(urlParams)).toString(), getHttpRequestUrlMethod(requestActionKey));
    }

    // 构造配置[注解]访问连接器,初始化连接器[附带URL构造参数 T<HttpParam> ...]
    public <T extends HttpParam> HttpsURLConnection initAnnotatedURLConnection(T... urlParams) throws HttpNetworkException {
        return createHttpsURLConnection(buildAnnotatedURLPath().append('?').append(buildURLParam(urlParams)).toString(), getHttpRequestUrlMethod());
    }

    public <T extends HttpParam> HttpsURLConnection initAnnotatedURLConnection(int requestActionKey, T... urlParams) throws HttpNetworkException {
        return createHttpsURLConnection(buildAnnotatedURLPath(requestActionKey).append('?').append(buildURLParam(urlParams)).toString(), getHttpRequestUrlMethod(requestActionKey));
    }

    // 创建 HttpsURLConnection 实体
    private HttpsURLConnection createHttpsURLConnection(String url, String method) throws HttpNetworkException {
        if (method.equalsIgnoreCase(HttpAction.METHOD_GET)) {
            return connection = getURLGETConnection(url);
        } else if (method.equalsIgnoreCase(HttpAction.METHOD_POST)) {
            return connection = getURLPOSTConnection(url);
        } else {
            throw new HttpNetworkException("Action Method Un Supported Exception!");
        }
    }

    // Annotation Tool
    protected <T extends HttpParam> String getAnnotatedHttpRequestUrlString(T... params) {
        return params.length < 1 ? buildAnnotatedURLPath().toString() : buildAnnotatedURLPath().append("?" + buildURLParam(params)).toString();
    }

    protected String getAnnotatedHttpRequestUrlString(List<? extends HttpParam> params) {
        return params.size() < 1 ? buildAnnotatedURLPath().toString() : buildAnnotatedURLPath().append("?" + buildURLParam(params)).toString();
    }

    private StringBuilder buildAnnotatedURLPath() {
        // 1.Server Annocation
        HttpServer annotationServer = getHttpServer();
        if (annotationServer == null) {
            throw new RuntimeException(new HttpAnnotationException(HttpAnnotationException.ANNOTATION_EXCEPTION_SERVER));
        }
        // 2.Action Annocation
        HttpAction annotationAction = getHttpAction();
        if (annotationAction == null) {
            throw new RuntimeException(new HttpAnnotationException(HttpAnnotationException.ANNOTATION_EXCEPTION_ACTION));
        }
        return createAnnotationUrl(annotationServer, annotationAction);
    }

    private StringBuilder buildAnnotatedURLPath(int requestActionKey) {
        // 1.Server Annocation
        HttpServer annotationServer = getHttpServer();
        if (annotationServer == null) {
            throw new RuntimeException(new HttpAnnotationException(HttpAnnotationException.ANNOTATION_EXCEPTION_SERVER));
        }
        // 2.Action Annocation
        HttpAction annotationAction = getHttpAction(requestActionKey);
        if (annotationAction == null) {
            throw new RuntimeException(new HttpAnnotationException(HttpAnnotationException.ANNOTATION_EXCEPTION_ACTION));
        }
        return createAnnotationUrl(annotationServer, annotationAction);
    }

    private StringBuilder createAnnotationUrl(HttpServer annotationServer, HttpAction annotationAction) {
        StringBuilder sb = new StringBuilder();
        if (annotationAction.value().equals(HttpAction.DEFAULT)) {
            if (annotationAction.serverName().equals(HttpAction.DEFAULT)) {
                // 在类的HttpServer中配置Server参数
                if (annotationServer.serverName() != null) {
                    sb.append(annotationServer.serverName());
                    if (!annotationServer.serverPort().equals(HttpServer.DEFAULT)) {
                        sb.append(":");
                        sb.append(annotationServer.serverPort());
                    }
                } else {
                    sb.append(annotationServer.value());
                }
            } else {
                // 在类HttpAction中配置Server参数
                sb.append(annotationAction.serverName());
                if (!annotationServer.serverPort().equals(HttpAction.DEFAULT)) {
                    sb.append(":");
                    sb.append(annotationAction.serverPort());
                }
            }
        } else {
            // 在类HttpAction中配置Url
            return sb.append(annotationAction.value());
        }
        sb.append("/");
        sb.append(annotationAction.action());
        return sb;
    }

    protected <T extends HttpParam> StringBuilder buildURLParam(T... params) {
        StringBuilder sb = new StringBuilder();
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                if (i > 0) sb.append('&');
                HttpParam pair = params[i];
                sb.append(pair.getName());
                sb.append('=');
                sb.append(pair.getValue());
            }
        }
        return sb;
    }

    protected <T extends HttpParam> StringBuilder buildURLParam(List<T> params) {
        StringBuilder sb = new StringBuilder();
        if (params != null && params.size() > 0) {
            for (int i = 0; i < params.size(); i++) {
                if (i > 0) sb.append('&');
                HttpParam pair = params.get(i);
                sb.append(pair.getName());
                sb.append('=');
                sb.append(pair.getValue());
            }
        }
        return sb;
    }

    protected <T extends HttpParam> StringBuilder buildPostFormFieldParam(T... params) {
        StringBuilder stringBuffer = new StringBuilder();
        for (HttpParam param : params) {
            stringBuffer.append(FORM_PREFIX);
            stringBuffer.append(boundary);
            stringBuffer.append(FORM_LINEND);
            stringBuffer.append("Content-Disposition: form-data; name=\"");
            stringBuffer.append(param.getName());
            stringBuffer.append("\"");
            stringBuffer.append(FORM_LINEND);
            stringBuffer.append("Content-Type: text/plain; charset=");
            stringBuffer.append(Charset.UTF_8);
            stringBuffer.append(FORM_LINEND);
            stringBuffer.append("Content-Transfer-Encoding: 8bit");
            stringBuffer.append(FORM_LINEND);
            stringBuffer.append(FORM_LINEND);
            stringBuffer.append(param.getValue());
            stringBuffer.append(FORM_LINEND);
        }
        return stringBuffer;
    }

    protected <T extends HttpParam> StringBuilder buildPostFormFieldParam(List<T> params) {
        StringBuilder stringBuffer = new StringBuilder();
        for (HttpParam param : params) {
            stringBuffer.append(FORM_PREFIX);
            stringBuffer.append(boundary);
            stringBuffer.append(FORM_LINEND);
            stringBuffer.append("Content-Disposition: form-data; name=\"");
            stringBuffer.append(param.getName());
            stringBuffer.append("\"");
            stringBuffer.append(FORM_LINEND);
            stringBuffer.append("Content-Type: text/plain; charset=");
            stringBuffer.append(Charset.UTF_8);
            stringBuffer.append(FORM_LINEND);
            stringBuffer.append("Content-Transfer-Encoding: 8bit");
            stringBuffer.append(FORM_LINEND);
            stringBuffer.append(FORM_LINEND);
            stringBuffer.append(param.getValue());
            stringBuffer.append(FORM_LINEND);
        }
        return stringBuffer;
    }

    // HTTP Annotation Method
    protected String getHttpRequestUrlMethod() {
        return getHttpAction().method();
    }

    protected HttpServer getHttpServer() {
        if (httpServer != null) return httpServer;
        return httpServer = UtilHttpAnnotation.getHttpServer(getClass());
    }

    protected HttpAction getHttpAction() {
        // Un Cache Action
        return UtilHttpAnnotation.getInvokeHttpAction(getClass(), Thread.currentThread().getStackTrace());
    }

    protected String getHttpRequestUrlMethod(int actionKey) {
        return getHttpAction(actionKey).method();
    }

    protected HttpAction getHttpAction(int actionKey) {
        HttpAction action = mArrayCacheAction.get(actionKey);
        if (action != null) return action;
        action = getHttpAction();
        mArrayCacheAction.put(actionKey, action);
        return action;
    }

    /********************************************** Http Connection Information **********************************************/
    // Connection
    public HttpsURLConnection getConnection() throws HttpNetworkException {
        if (connection == null) {
            throw new RuntimeException("Connect to server exception ,The connecter un initializing.");
        }
        return connection;
    }

    // 构造GET访问连接器,初始化连接器[传入请求的完整 URL]
    public HttpsURLConnection getURLGETConnection(String requestUrl) throws HttpNetworkException {
        try {
            HttpsURLConnection conn = (HttpsURLConnection) new URL(requestUrl).openConnection();
            conn.setHostnameVerifier(DO_NOT_VERIFY);
            conn.setDefaultUseCaches(false);
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(false);
            conn.setReadTimeout(DEFAULT_READ_TIMEOUT);
            conn.setRequestMethod(HttpAction.METHOD_GET);
            conn.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
            conn.setRequestProperty("Charset", Charset.UTF_8);
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Content-Type", ContentType.APPLICATION_FORM_URLENCODEED);
            setCookieProperty(conn);
            return connection = conn;
        } catch (Exception e) {
            throw new HttpNetworkException(e);
        }
    }

    // 构造POST访问连接器,初始化连接器[传入请求的完整 URL]
    public HttpsURLConnection getURLPOSTConnection(String requestUrl) throws HttpNetworkException {
        try {
            HttpsURLConnection conn = (HttpsURLConnection) new URL(requestUrl).openConnection();
            conn.setHostnameVerifier(DO_NOT_VERIFY);
            conn.setDefaultUseCaches(false);
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setReadTimeout(DEFAULT_READ_TIMEOUT);
            conn.setRequestMethod(HttpAction.METHOD_POST);
            conn.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
            conn.setRequestProperty("Charset", Charset.UTF_8);
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Content-Language", LANGUAGE_ZH_CN);
            conn.setRequestProperty("Content-Type", ContentType.APPLICATION_FORM_URLENCODEED);
            setCookieProperty(conn);
            return connection = conn;
        } catch (Exception e) {
            throw new HttpNetworkException(e);
        }
    }

    // 构造GET访问连接器,初始化连接器[传入请求的完整 URL, List<HttpParam>URL请求参数]
    public HttpsURLConnection bulidGETConnection(String requestUrl, List<? extends HttpParam> urlParams) throws HttpNetworkException {
        if (urlParams.size() < 1) return getURLGETConnection(requestUrl);
        return getURLGETConnection(requestUrl + "?" + buildURLParam(urlParams));
    }

    // 构造GET访问连接器,初始化连接器[传入请求的完整 URL, T<HttpParam>URL请求参数]
    public <T extends HttpParam> HttpsURLConnection bulidGETConnection(String requestUrl, T... urlParams) throws HttpNetworkException {
        if (urlParams.length < 1) return getURLGETConnection(requestUrl);
        return getURLGETConnection(requestUrl + "?" + buildURLParam(urlParams));
    }

    // 构造POST访问连接器,初始化连接器[传入请求的完整 URL, List<HttpParam>POST请求参数]
    public HttpsURLConnection buildPOSTConnection(String requestUrl, List<? extends HttpParam> urlParams) throws HttpNetworkException {
        if (urlParams.size() < 1) return getURLPOSTConnection(requestUrl);
        return getURLPOSTConnection(requestUrl + "?" + buildURLParam(urlParams));
    }

    // 构造POST访问连接器,初始化连接器[传入请求的完整 URL, T<HttpParam>POST请求参数]
    public <T extends HttpParam> HttpsURLConnection buildPOSTConnection(String requestUrl, T... urlParams) throws HttpNetworkException {
        if (urlParams.length < 1) return getURLPOSTConnection(requestUrl);
        return getURLPOSTConnection(requestUrl + "?" + buildURLParam(urlParams));
    }

    // Header Parameters, List<HttpParam>
    // [设置Header前必须建立Connection连接,所以必须先初始化]
    public <T extends HttpParam> void setRequestHeaders(T... params) throws HttpNetworkException {
        if (params.length < 1) return;
        HttpsURLConnection huc = getConnection();
        for (HttpParam param : params) {
            huc.setRequestProperty(param.getName(), param.getValue().toString());
        }
    }

    // Header Parameters, T<HttpParam>
    // [设置Header前必须建立Connection连接,所以必须先初始化]
    public void setRequestHeaders(List<? extends HttpParam> params) throws HttpNetworkException {
        HttpsURLConnection huc = getConnection();
        if (params == null) return;
        for (HttpParam param : params) {
            huc.setRequestProperty(param.getName(), param.getValue().toString());
        }
    }

    // Header Request Content Type
    // [设置Header前必须建立Connection连接,所以必须先初始化]
    public void setRequestContentType(String contentType) throws HttpNetworkException {
        HttpsURLConnection huc = getConnection();
        huc.setRequestProperty("Content-Type", ContentType.APPLICATION_FORM_URLENCODEED);
    }

    public void setCookieProperty(URLConnection connection) {
        if (cookieParams.isEmpty()) return;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < cookieParams.size(); i++) {
            HttpCookie cookie = cookieParams.get(i);
            sb.append(cookie.getName());
            sb.append("=");
            sb.append(cookie.getValue());
            if (i + 1 < cookieParams.size()) sb.append(";");
        }
        connection.setRequestProperty("Cookie", sb.toString());
    }

    @Override
    public List<HttpCookie> getCookies() {
        return cookieParams;
    }

    @Override
    public void addCookie(HttpCookie cookie) {
        for (HttpCookie hc : cookieParams) {
            if (hc.getName().equals(cookie.getName())) {
                hc.setValue(cookie.getValue());
                return;
            }
        }
        cookieParams.add(cookie);
    }

    HttpCookie parseCookie(String cookie) {
        HttpCookie hc = null;
        String[] ss = cookie.split(";");
        for (String s : ss) {
            String[] sv = s.split("=");
            if (sv[0].trim().equalsIgnoreCase("PATH") && hc != null) {
                hc.setPath(sv[1].trim());
            } else {
                hc = new HttpCookie(sv[0].trim(), sv[1].trim());
            }
        }
        return hc;
    }

    /**
     * Trust every server - dont check for any certificate
     */
    private static void trustAllHttpsHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static KeyStore buildKeyStore(Context context, int certRawResId) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        // init a default key store
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);

        // read and add certificate authority
        Certificate cert = readCert(context, certRawResId);
        // keyStore.setCertificateEntry("ca", cert);

        return keyStore;
    }

    private static Certificate readCert(Context context, int certResourceId) throws CertificateException, IOException {

        // read certificate resource
        InputStream caInput = context.getResources().openRawResource(certResourceId);

        Certificate ca = null;
        try {
            // generate a certificate
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            // ca = cf.generateCertificate(caInput);
        } finally {
            caInput.close();
        }

        return ca;
    }

    @Override
    public String sendPostFormFileRequestForString(String url, String fileParamName, String fileName, File file, List<? extends HttpParam> requestParams) throws HttpNetworkException {
        return null;
    }

    @Override
    public <T extends HttpParam> String sendPostFormFileRequestForString(String url, List<? extends String> fileParamName, List<? extends File> file, T... requestParams) throws HttpNetworkException {
        return null;
    }

    @Override
    public String sendPostFormFileRequestForString(String url, List<? extends String> fileParamName, List<? extends File> file, List<? extends HttpParam> requestParams) throws HttpNetworkException {
        return null;
    }

    @Override
    public <T extends HttpParam> String sendPostFormFileRequestForString(String url, String fileParamName, String fileName, File file, T... requestParams) throws HttpNetworkException {
        return null;
    }
}
