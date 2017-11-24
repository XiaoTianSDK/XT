package com.xiaotian.frameworkxt.net;

import com.xiaotian.frameworkxt.android.common.Mylog;
import com.xiaotian.frameworkxt.net.HttpProperty.Charset;
import com.xiaotian.frameworkxt.net.HttpProperty.Connection;
import com.xiaotian.frameworkxt.net.HttpProperty.ContentType;
import com.xiaotian.frameworkxt.net.HttpProperty.Header.Property;
import com.xiaotian.frameworkxt.net.HttpProperty.UserAgent;
import com.xiaotian.frameworkxt.util.UtilFile;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @name HttpConnector
 * @description Http Server Connector Control
 * @date 2013-12-14
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
public class HttpServerConnector implements HttpConnector {
    //
    protected String boundary;
    protected HttpServer httpServer; // Cache. Server Class
    protected Method[] declareMethod;
    protected InputStream inputStream;
    protected OutputStream outputStream;
    protected HttpURLConnection connection;
    private Map<Integer, HttpAction> mArrayCacheAction;
    private List<HttpCookie> cookieParams = new ArrayList<HttpCookie>();

    public HttpServerConnector() {
        mArrayCacheAction = new HashMap<Integer, HttpAction>();
        boundary = Long.toHexString(System.currentTimeMillis());
    }

    /********************************************** Base Http Request Method **********************************************/
    // Simple Request 基本HTTP请求,[URL:请求URL,Method:请求方法,URLParamsString:请求参数]
    public String sendRequest(String urlPath, String method, String params) throws HttpNetworkException {
        ByteArrayOutputStream bos = null;
        BufferedInputStream is = null;
        OutputStream os = null;
        try {
            connection = (HttpURLConnection) new URL(urlPath).openConnection();
            // Header Cookie
            connection.setDoInput(true);
            connection.setDoOutput(HttpProperty.Method.POST.equalsIgnoreCase(method) ? true : false);
            connection.setRequestMethod((method == null || "".equals(method)) ? HttpProperty.Method.GET : method);
            connection.setRequestProperty(Property.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODEED);
            connection.setRequestProperty(Property.CONNECTION, Connection.KEEP_ALIVE);
            connection.setRequestProperty(Property.USER_AGENT, UserAgent.ANDROID);
            connection.setRequestProperty(Property.CHARSET, Charset.UTF_8);
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
            case HttpURLConnection.HTTP_OK:
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
        return bos == null ? null : bos.toString();
    }

    // 构造 POST Form 表单,包含单个文件的请求,fileParamName:上传文件的字段名,fileName:上传文件名,file:本地文件Path,ItemParams 其他参数
    protected String sendPostFormFileRequestForString(String url, String fileParamName, String fileName, File file, String requestContentPlanItemParams) throws HttpNetworkException {
        InputStream fileInputStream = null;
        InputStream urlInputStream = null;
        OutputStream urlOutputStream = null;
        StringBuffer stringBuffer = new StringBuffer();
        ByteArrayOutputStream byteArrayOutputStream = null;
        byte[] bbuf = new byte[512];
        int hasReaded = -1;
        try {
            fileInputStream = new FileInputStream(file);
            connection = (HttpURLConnection) new URL(url).openConnection();
            // Header Cookie
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod(HttpProperty.Method.POST);
            connection.setRequestProperty(Property.CONNECTION, Connection.KEEP_ALIVE);
            connection.setRequestProperty(Property.USER_AGENT, UserAgent.ANDROID);
            connection.setRequestProperty(Property.CHARSET, Charset.UTF_8);
            connection.setConnectTimeout(20 * 1000);
            connection.setReadTimeout(20 * 1000);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(false);
            // Start Request Content Boundary
            // Meansure The Content Is form urlencode
            connection.setRequestProperty(Property.CONTENT_TYPE, ContentType.MULTIPART_FORM_DATA + "; boundary=" + boundary);
            urlOutputStream = connection.getOutputStream();
            // Write RequestField Params
            urlOutputStream.write(requestContentPlanItemParams.getBytes());
            // Write Request File Param
            stringBuffer.setLength(0);
            stringBuffer.append(FORM_PREFIX);
            stringBuffer.append(boundary);
            // File Declaration
            stringBuffer.append(FORM_LINEND);
            stringBuffer.append("Content-Disposition: form-data; name=\"");
            stringBuffer.append(fileParamName);
            stringBuffer.append("\"; filename=\"");
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
            urlOutputStream.flush(); // Flush The Cache , get Response
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
        return byteArrayOutputStream == null ? null : byteArrayOutputStream.toString();
    }

    // 构造 POST Form 表单,包含多个文件的请求,fileParamName:上传文件字段名,file:本地文件Path,ItemParams 其他参数
    protected String sendPostFormFileRequestForString(String url, List<? extends String> fileParamNames, List<? extends File> files, String requestContentPlanItemParams) throws HttpNetworkException {
        InputStream fileInputStream = null;
        InputStream urlInputStream = null;
        OutputStream urlOutputStream = null;
        StringBuffer stringBuffer = new StringBuffer();
        ByteArrayOutputStream byteArrayOutputStream = null;
        byte[] bbuf = new byte[512];
        int hasReaded = -1;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            // Header Cookie
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod(HttpProperty.Method.POST);
            connection.setRequestProperty(Property.CHARSET, Charset.UTF_8);
            connection.setRequestProperty(Property.CONNECTION, Connection.KEEP_ALIVE);
            connection.setRequestProperty(Property.USER_AGENT, UserAgent.ANDROID);
            connection.setConnectTimeout(20 * 1000);
            connection.setReadTimeout(20 * 1000);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(false);
            // Start Request Content Boundary
            // Meansure The Content Is form urlencode
            connection.setRequestProperty(Property.CONTENT_TYPE, ContentType.MULTIPART_FORM_DATA + "; boundary=" + boundary);
            urlOutputStream = connection.getOutputStream();
            // Write RequestField Params
            urlOutputStream.write(requestContentPlanItemParams.getBytes());
            for (int i = 0; files != null && i < files.size(); i++) {
                File file = files.get(i);
                String fileParamName = fileParamNames.get(i);
                //
                fileInputStream = new FileInputStream(file);
                // Write Request File Param
                stringBuffer.setLength(0);
                stringBuffer.append(FORM_PREFIX);
                stringBuffer.append(boundary);
                // File Declaration
                stringBuffer.append(FORM_LINEND);
                stringBuffer.append("Content-Disposition: form-data; name=\"");
                stringBuffer.append(fileParamName);
                stringBuffer.append("\"; filename=\"");
                stringBuffer.append(UtilFile.getInstance().getFilename(file.getAbsolutePath()));
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
            urlOutputStream.flush(); // Flush The Cache , get Response
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
        return byteArrayOutputStream == null ? null : byteArrayOutputStream.toString();
    }

    // 构造 POST Form 表单,包含单个文件的请求,fileParamName:上传变量字段名,fileName:上传文件名,file:本地文件Path
    // List<HttpParam> 其他参数
    public String sendPostFormFileRequestForString(String url, String fileParamName, String fileName, File file, List<? extends HttpParam> requestParams) throws HttpNetworkException {
        return sendPostFormFileRequestForString(url, fileParamName, fileName, file, getPostFormFileRequestParams(requestParams));
    }

    // 构造 POST Form 表单,包含单个文件的请求,fileParamName:上传变量字段名,fileName:上传文件名,file:本地文件Path
    // T<HttpParam> 其他参数
    public <T extends HttpParam> String sendPostFormFileRequestForString(String url, String fileParamName, String fileName, File file, T... requestParams) throws HttpNetworkException {
        return sendPostFormFileRequestForString(url, fileParamName, fileName, file, getPostFormFileRequestParams(requestParams));
    }

    // 构造 POST Form 表单,包含多个文件的请求,fileParamName:上传变量字段名,file:本地文件Path集合
    // List<HttpParam> 其他参数
    public String sendPostFormFileRequestForString(String url, List<? extends String> fileParamName, List<? extends File> files, List<? extends HttpParam> requestParams) throws HttpNetworkException {
        return sendPostFormFileRequestForString(url, fileParamName, files, getPostFormFileRequestParams(requestParams));
    }

    // 构造 POST Form 表单,包含多个文件的请求,fileParamName:上传变量字段名,file:本地文件Path集合
    // T<HttpParam> 其他参数
    public <T extends HttpParam> String sendPostFormFileRequestForString(String url, List<? extends String> fileParamName, List<? extends File> files, T... requestParams) throws HttpNetworkException {
        return sendPostFormFileRequestForString(url, fileParamName, files, getPostFormFileRequestParams(requestParams));
    }

    // 构造其他请求参数串
    private String getPostFormFileRequestParams(List<? extends HttpParam> requestParams) {
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
        return stringBuffer.toString();
    }

    // 构造其他请求参数串
    private <T extends HttpParam> String getPostFormFileRequestParams(T... requestParams) {
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
        return stringBuffer.toString();
    }

    // GET 获取文件 (传入文件URL,传入文件保存路径Path),返回 Boolean
    public boolean downloadFile(String fileUrl, String filePath) throws HttpNetworkException {
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
            case HttpURLConnection.HTTP_OK:
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
        connection = getURLGETConnection(fileUrl);
        ByteArrayOutputStream outStream = null;
        InputStream urlInputStream = null;
        byte[] bbuf = new byte[256];
        int hasReaded = -1;
        try {
            switch (connection.getResponseCode()) {
            case HttpURLConnection.HTTP_OK:
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
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            final URL url = new URL(urlString); // URL访问资源
            connection = (HttpURLConnection) url.openConnection(); // 打开连接,默认GET
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
        ByteArrayOutputStream bos = sendHttpRequestToByteArrayOutputStream(connection, getHttpRequestUrlMethod(), buildURLParam(postRequestParams).toString());
        try {
            return bos == null ? null : bos.toString(Charset.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new HttpNetworkException(e);
        }
    }

    public <T extends HttpParam> String sendAnnotatedRequest(int cacheRequestKey, T... postRequestParams) throws HttpNetworkException {
        if (connection == null) initAnnotatedURLConnection(cacheRequestKey, postRequestParams);
        ByteArrayOutputStream bos = sendHttpRequestToByteArrayOutputStream(connection, getHttpRequestUrlMethod(cacheRequestKey), buildURLParam(postRequestParams).toString());
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
        ByteArrayOutputStream bos = sendHttpRequestToByteArrayOutputStream(connection, getHttpRequestUrlMethod(), buildURLParam(postRequestParams).toString());
        try {
            return bos == null ? null : bos.toString(Charset.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new HttpNetworkException(e);
        }
    }

    public <T extends HttpParam> String sendAnnotatedRequest(int cacheRequestKey, List<T> postRequestParams) throws HttpNetworkException {
        if (connection == null) initAnnotatedURLConnection(cacheRequestKey, postRequestParams);
        ByteArrayOutputStream bos = sendHttpRequestToByteArrayOutputStream(connection, getHttpRequestUrlMethod(cacheRequestKey), buildURLParam(postRequestParams).toString());
        try {
            return bos == null ? null : bos.toString(Charset.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new HttpNetworkException(e);
        }
    }

    private ByteArrayOutputStream sendHttpRequestToByteArrayOutputStream(HttpURLConnection httpConnection, String requestMethod, String requestParams) throws HttpNetworkException {
        if (httpConnection == null) throw new HttpNetworkException("The HttpURLConnection Un Initialized.");
        ByteArrayOutputStream bos = null;
        BufferedInputStream is = null;
        byte[] bbuf = new byte[1024];
        OutputStream os = null;
        int hasReaded = 0;
        try {
            if (requestMethod.equalsIgnoreCase(HttpAction.METHOD_POST) && requestParams != null) {
                connection = getConnection();
                byte[] paramByte = requestParams.getBytes(Charset.UTF_8);
                connection.setRequestProperty("Content-Length", String.valueOf(paramByte.length));
                os = connection.getOutputStream();
                os.write(paramByte);
                os.flush();
            } else if (requestMethod.equalsIgnoreCase(HttpAction.METHOD_GET)) {
                // GET ignore params
            } else {
                throw new RuntimeException("Method Un Support Exception!");
            }
            switch (connection.getResponseCode()) {
            case HttpURLConnection.HTTP_OK:
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
                throw new HttpNetworkException("Request failed, response code = " + connection.getResponseCode() + " " + bos);
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
            }
            if (connection != null) {
                connection.disconnect();
                connection = null;
            }
        }
        return bos;
    }

    // URL Connection Init
    // 构造配置[注解]访问连接器,初始化连接器
    public HttpURLConnection initAnnotatedURLConnection() throws HttpNetworkException {
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
    public <T extends HttpParam> HttpURLConnection initAnnotatedURLConnection(List<T> urlParams) throws HttpNetworkException {
        String method = getHttpRequestUrlMethod();
        if (method.equalsIgnoreCase(HttpAction.METHOD_GET)) {
            return createHttpURLConnection(buildAnnotatedURLPath().append('?').append(buildURLParam(urlParams)).toString(), method);
        } else if (method.equals(HttpAction.METHOD_POST)) {
            return createHttpURLConnection(buildAnnotatedURLPath().toString(), method);
        } else {
            throw new HttpNetworkException("Action Method Un Supported Exception!");
        }

    }

    public <T extends HttpParam> HttpURLConnection initAnnotatedURLConnection(int requestActionKey, List<T> urlParams) throws HttpNetworkException {
        String method = getHttpRequestUrlMethod(requestActionKey);
        if (method.equalsIgnoreCase(HttpAction.METHOD_GET)) {
            return createHttpURLConnection(buildAnnotatedURLPath(requestActionKey).append('?').append(buildURLParam(urlParams)).toString(), method);
        } else if (method.equals(HttpAction.METHOD_POST)) {
            return createHttpURLConnection(buildAnnotatedURLPath(requestActionKey).toString(), method);
        } else {
            throw new HttpNetworkException("Action Method Un Supported Exception!");
        }
    }

    // 构造配置[注解]访问连接器,初始化连接器[附带URL构造参数 T<HttpParam> ...]
    public <T extends HttpParam> HttpURLConnection initAnnotatedURLConnection(T... urlParams) throws HttpNetworkException {
        String method = getHttpRequestUrlMethod();
        if (method.equalsIgnoreCase(HttpAction.METHOD_GET)) {
            return createHttpURLConnection(buildAnnotatedURLPath().append('?').append(buildURLParam(urlParams)).toString(), method);
        } else if (method.equals(HttpAction.METHOD_POST)) {
            return createHttpURLConnection(buildAnnotatedURLPath().toString(), method);
        } else {
            throw new HttpNetworkException("Action Method Un Supported Exception!");
        }
    }

    public <T extends HttpParam> HttpURLConnection initAnnotatedURLConnection(int requestActionKey, T... urlParams) throws HttpNetworkException {
        String method = getHttpRequestUrlMethod(requestActionKey);
        if (method.equalsIgnoreCase(HttpAction.METHOD_GET)) {
            return createHttpURLConnection(buildAnnotatedURLPath(requestActionKey).toString(), method);
        } else if (method.equals(HttpAction.METHOD_POST)) {
            return createHttpURLConnection(buildAnnotatedURLPath(requestActionKey).append('?').append(buildURLParam(urlParams)).toString(), method);
        } else {
            throw new HttpNetworkException("Action Method Un Supported Exception!");
        }
    }

    // 创建 HttpURLConnection 实体
    private HttpURLConnection createHttpURLConnection(String url, String method) throws HttpNetworkException {
        if (method.equalsIgnoreCase(HttpAction.METHOD_GET)) {
            return connection = getURLGETConnection(url);
        } else if (method.equals(HttpAction.METHOD_POST)) {
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
    public HttpURLConnection getConnection() throws HttpNetworkException {
        if (connection == null) {
            throw new RuntimeException("When connect to server exception ,The connecter un initializing.");
        }
        return connection;
    }

    // 构造GET访问连接器,初始化连接器[传入请求的完整 URL]
    public HttpURLConnection getURLGETConnection(String requestUrl) throws HttpNetworkException {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(requestUrl).openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(false);
            conn.setRequestMethod("GET");
            conn.setReadTimeout(DEFAULT_READ_TIMEOUT);
            conn.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
            conn.setRequestProperty(Property.CHARSET, Charset.UTF_8);
            conn.setRequestProperty(Property.USER_AGENT, UserAgent.ANDROID);
            conn.setRequestProperty(Property.CONNECTION, Connection.KEEP_ALIVE);
            conn.setRequestProperty(Property.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODEED);
            return connection = conn;
        } catch (Exception e) {
            throw new HttpNetworkException(e);
        }
    }

    // 构造POST访问连接器,初始化连接器[传入请求的完整 URL]
    public HttpURLConnection getURLPOSTConnection(String requestUrl) throws HttpNetworkException {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(requestUrl).openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setReadTimeout(DEFAULT_READ_TIMEOUT);
            conn.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
            conn.setRequestProperty(Property.CHARSET, Charset.UTF_8);
            conn.setRequestProperty(Property.USER_AGENT, UserAgent.ANDROID);
            conn.setRequestProperty(Property.CONNECTION, Connection.KEEP_ALIVE);
            conn.setRequestProperty(Property.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODEED);
            return connection = conn;
        } catch (Exception e) {
            throw new HttpNetworkException(e);
        }
    }

    // 构造GET访问连接器,初始化连接器[传入请求的完整 URL, List<HttpParam>URL请求参数]
    public HttpURLConnection bulidGETConnection(String requestUrl, List<? extends HttpParam> urlParams) throws HttpNetworkException {
        if (urlParams.size() < 1) return getURLGETConnection(requestUrl);
        return getURLGETConnection(requestUrl + "?" + buildURLParam(urlParams));
    }

    // 构造GET访问连接器,初始化连接器[传入请求的完整 URL, T<HttpParam>URL请求参数]
    public <T extends HttpParam> HttpURLConnection bulidGETConnection(String requestUrl, T... urlParams) throws HttpNetworkException {
        if (urlParams.length < 1) return getURLGETConnection(requestUrl);
        return getURLGETConnection(requestUrl + "?" + buildURLParam(urlParams));
    }

    // 构造POST访问连接器,初始化连接器[传入请求的完整 URL, List<HttpParam>POST请求参数]
    public HttpURLConnection buildPOSTConnection(String requestUrl, List<? extends HttpParam> urlParams) throws HttpNetworkException {
        if (urlParams.size() < 1) return getURLPOSTConnection(requestUrl);
        return getURLPOSTConnection(requestUrl + "?" + buildURLParam(urlParams));
    }

    // 构造POST访问连接器,初始化连接器[传入请求的完整 URL, T<HttpParam>POST请求参数]
    public <T extends HttpParam> HttpURLConnection buildPOSTConnection(String requestUrl, T... urlParams) throws HttpNetworkException {
        if (urlParams.length < 1) return getURLPOSTConnection(requestUrl);
        return getURLPOSTConnection(requestUrl + "?" + buildURLParam(urlParams));
    }

    // Header Parameters, List<HttpParam>
    // [设置Header前必须建立Connection连接,所以必须先初始化]
    public <T extends HttpParam> void setRequestHeaders(T... params) throws HttpNetworkException {
        if (params.length < 1) return;
        HttpURLConnection huc = getConnection();
        for (HttpParam param : params) {
            huc.setRequestProperty(param.getName(), param.getValue().toString());
        }
    }

    // Header Parameters, T<HttpParam>
    // [设置Header前必须建立Connection连接,所以必须先初始化]
    public void setRequestHeaders(List<? extends HttpParam> params) throws HttpNetworkException {
        HttpURLConnection huc = getConnection();
        if (params == null) return;
        for (HttpParam param : params) {
            huc.setRequestProperty(param.getName(), param.getValue().toString());
        }
    }

    // Header Request Content Type
    // [设置Header前必须建立Connection连接,所以必须先初始化]
    public void setRequestContentType(String contentType) throws HttpNetworkException {
        HttpURLConnection huc = getConnection();
        huc.setRequestProperty("Content-Type", ContentType.APPLICATION_FORM_URLENCODEED);
    }

    @Override
    public List<HttpCookie> getCookies() {
        return cookieParams;
    }

    @Override
    public void addCookie(HttpCookie cookie) {
        this.cookieParams.add(cookie);
    }
}
