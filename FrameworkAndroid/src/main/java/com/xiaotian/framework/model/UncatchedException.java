package com.xiaotian.framework.model;

import com.xiaotian.frameworkxt.android.model.SQLColumn;
import com.xiaotian.frameworkxt.android.model.SQLColumnType;
import com.xiaotian.frameworkxt.android.model.SQLEntity;
import com.xiaotian.frameworkxt.android.model.SQLId;
import com.xiaotian.frameworkxt.android.model.SQLTable;
import com.xiaotian.frameworkxt.android.model.SQLTable.DatabaseNameType;
import com.xiaotian.frameworkxt.android.model.provider.SQLContentProvider;

/**
 * @version 1.0.0
 * @author Administrator
 * @name UncatchedException
 * @description 未扑捉异常
 * @date 2015-6-12
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
@SQLEntity
@SQLContentProvider(authorities = "com.xiaotian.framework.model.ContentProvider.UncatchedException", contentPath = "UncatchedException")
@SQLTable(databaseName = "com_xiaotian_framework_model_ContentProvider_UncatchedException.db", databaseType = DatabaseNameType.CONSTANT, name = "UncatchedException", version = 2)
public class UncatchedException {
	public final int STATUS_UNKNOW = 0X000;
	public final int STATUS_COMMITED = 0X001;
	public final int STATUS_SKIP = 0X002;
	@SQLId
	private Integer id;
	@SQLColumn(name = "status", type = SQLColumnType.INTEGER, defaultValue = "0")
	private Integer status;
	// 创建时间
	@SQLColumn(name = "date", type = SQLColumnType.LONG)
	private Long date;
	// 邮件标题
	@SQLColumn(name = "email_subject")
	private String emailSubject;
	// 邮件内容
	@SQLColumn(name = "email_content")
	private String emailContent;

	public UncatchedException() {
		date = System.currentTimeMillis();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getEmailContent() {
		return emailContent;
	}

	public void setEmailContent(String emailContent) {
		this.emailContent = emailContent;
	}

}
