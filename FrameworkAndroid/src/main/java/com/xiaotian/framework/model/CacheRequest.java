package com.xiaotian.framework.model;

import com.xiaotian.frameworkxt.android.model.SQLColumn;
import com.xiaotian.frameworkxt.android.model.SQLColumnType;
import com.xiaotian.frameworkxt.android.model.SQLEntity;
import com.xiaotian.frameworkxt.android.model.SQLId;
import com.xiaotian.frameworkxt.android.model.SQLTable;
import com.xiaotian.frameworkxt.android.model.provider.SQLContentProvider;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @description
 * @date 2016/2/18
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
@SQLEntity
@SQLContentProvider(authorities = "com.xiaotian.framework.model.ContentProvider.CacheRequest", contentPath = "CacheRequest")
@SQLTable(databaseName = "com_xiaotian_framework_model_ContentProvider_CacheRequest.db", databaseType = SQLTable.DatabaseNameType.CONSTANT, name = "CacheRequest", version = 1)
public class CacheRequest {
    @SQLId
    private Integer id;
    @SQLColumn(name = "identification", type = SQLColumnType.INTEGER)
    private Integer identification; // 标志符
    @SQLColumn(name = "markup", type = SQLColumnType.TEXT)
    private String markup; // 备注标志
    @SQLColumn(name = "url", type = SQLColumnType.TEXT)
    private String url; // 请求URL
    @SQLColumn(name = "method", type = SQLColumnType.TEXT)
    private String method; // 请求方法
    @SQLColumn(name = "params", type = SQLColumnType.TEXT)
    private String params; // 请求参数
    @SQLColumn(name = "cookie", type = SQLColumnType.TEXT)
    private String cookie; // 请求cookie
    @SQLColumn(name = "params_optional", type = SQLColumnType.TEXT)
    private String paramsOptional; // 可选请求参数
    @SQLColumn(name = "date", type = SQLColumnType.LONG)
    private Long date; // 请求时间
    @SQLColumn(name = "response_bytes", type = SQLColumnType.BLOB)
    private Byte[] responseBytes; // 返回的字节数据
    @SQLColumn(name = "response_string", type = SQLColumnType.TEXT)
    private String responseString; // 返回的字符串数据
    @SQLColumn(name = "extras_int", type = SQLColumnType.INTEGER)
    private Integer extrasInt; // 额外整数
    @SQLColumn(name = "extras_string", type = SQLColumnType.TEXT)
    private String extrasString; // 额外字符串
    @SQLColumn(name = "invalid_time", type = SQLColumnType.LONG)
    private Long invalidTime; // 失效时长
    //
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdentification() {
        return identification;
    }

    public void setIdentification(Integer identification) {
        this.identification = identification;
    }

    public String getMarkup() {
        return markup;
    }

    public void setMarkup(String markup) {
        this.markup = markup;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getParamsOptional() {
        return paramsOptional;
    }

    public void setParamsOptional(String paramsOptional) {
        this.paramsOptional = paramsOptional;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Byte[] getResponseBytes() {
        return responseBytes;
    }

    public void setResponseBytes(Byte[] responseBytes) {
        this.responseBytes = responseBytes;
    }

    public String getResponseString() {
        return responseString;
    }

    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }

    public Integer getExtrasInt() {
        return extrasInt;
    }

    public void setExtrasInt(Integer extrasInt) {
        this.extrasInt = extrasInt;
    }

    public String getExtrasString() {
        return extrasString;
    }

    public void setExtrasString(String extrasString) {
        this.extrasString = extrasString;
    }

    public Long getInvalidTime() {
        return invalidTime;
    }

    public void setInvalidTime(Long invalidTime) {
        this.invalidTime = invalidTime;
    }

    /**************************** Class Method ****************************/
    public boolean isInvalid() {
        if (invalidTime < 1) return false;
        return System.currentTimeMillis() - date < invalidTime;
    }
}
