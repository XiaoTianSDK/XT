package com.xiaotian.frameworkxt.net;

import java.io.File;
import java.io.Serializable;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @name HttpParam
 * @description Http Request Param Package
 * @date 2013-12-14
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
public class HttpParam implements Cloneable, Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private Object value;

    public HttpParam(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public boolean isFile() {
        return value instanceof File;
    }

    public String getFileName() {
        if (isFile()) {
            String filePath = ((File) value).getAbsolutePath();
            return filePath.substring(filePath.lastIndexOf("/") + 1);
        }
        return null;
    }

    @Override
    public String toString() {
        return getName() + "=" + getValue();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public int hashCode() {
        return name.hashCode() & value.hashCode();
    }
}
