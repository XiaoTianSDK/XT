package com.xiaotian.frameworkxt.net;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.URLConnection;
import java.util.List;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @name HttpConnector
 * @description Http接口
 * @date Dec 20, 2014
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2009-2014 广州隽永贸易科技 Ltd, All Rights Reserved.
 */
public interface HttpConnector {
    // Form Request Params
    static final String FORM_PREFIX = "--";
    static final String FORM_LINEND = "\r\n";
    static final String LANGUAGE_EN_US = "en-US";
    static final String LANGUAGE_ZH_CN = "zh-CN";
    // Connection Params
    final int DEFAULT_CONNECT_TIMEOUT = 20 * 1000;
    final int DEFAULT_READ_TIMEOUT = 20 * 1000;
    final int IO_BUFFER_SIZE = 8 * 1024;

    // Http Method
    String sendRequest(String urlPath, String method, String params) throws HttpNetworkException;

    String sendPostFormFileRequestForString(String url, String fileParamName, String fileName, File file, List<? extends HttpParam> requestParams) throws HttpNetworkException;

    <T extends HttpParam> String sendPostFormFileRequestForString(String url, List<? extends String> fileParamName, List<? extends File> file, T... requestParams) throws HttpNetworkException;

    String sendPostFormFileRequestForString(String url, List<? extends String> fileParamName, List<? extends File> file, List<? extends HttpParam> requestParams) throws HttpNetworkException;

    <T extends HttpParam> String sendPostFormFileRequestForString(String url, String fileParamName, String fileName, File file, T... requestParams) throws HttpNetworkException;

    boolean downloadFile(String fileUrl, String filePath) throws HttpNetworkException;

    ByteArrayOutputStream getFile(String fileUrl) throws HttpNetworkException;

    boolean downloadUrlToStream(String urlString, OutputStream outputStream) throws HttpNetworkException;

    // Annotation Http Method
    <T extends HttpParam> String sendAnnotatedRequest(T... postRequestParams) throws HttpNetworkException;

    <T extends HttpParam> String sendAnnotatedRequest(int cacheRequestKey, T... postRequestParams) throws HttpNetworkException;

    <T extends HttpParam> String sendAnnotatedRequest(List<T> postRequestParams) throws HttpNetworkException;

    <T extends HttpParam> String sendAnnotatedRequest(int cacheRequestKey, List<T> postRequestParams) throws HttpNetworkException;

    URLConnection initAnnotatedURLConnection() throws HttpNetworkException;

    <T extends HttpParam> URLConnection initAnnotatedURLConnection(int requestActionKey, List<T> urlParams) throws HttpNetworkException;

    <T extends HttpParam> URLConnection initAnnotatedURLConnection(T... urlParams) throws HttpNetworkException;

    <T extends HttpParam> URLConnection initAnnotatedURLConnection(int requestActionKey, T... urlParams) throws HttpNetworkException;

    <T extends HttpParam> void setRequestHeaders(T... params) throws HttpNetworkException;

    void setRequestContentType(String contentType) throws HttpNetworkException;

    URLConnection getConnection() throws HttpNetworkException;

    // Cookie
    List<HttpCookie> getCookies();

    void addCookie(HttpCookie cookie);
}
