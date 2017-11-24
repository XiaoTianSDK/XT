package com.xiaotian.frameworkxt.util;

import com.xiaotian.frameworkxt.android.common.Mylog;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.Provider;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @name EmailUtil
 * @description 邮件发送工具Util, 用于发送程序反馈/BUG信息到邮箱<b>{sendMail(subject,content);</b>发送邮件}
 * @date 2013-10-17
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
public class UtilEmail extends javax.mail.Authenticator {
    // Java Mail : https://java.net/projects/javamail/pages/Home
    // 添加供应器Provider到安全机制中
    static {
        Security.addProvider(new JSSEProvider());
    }
    // ::xiaotian911@yahoo.com,xt891111,smtp.mail.yahoo.com
    // ::xiaotian8911@126.com,T89bmw154d1Lm2,smtp.126.com
    // ::xiaotian151222@sina.com,GK23mpZBoo90QQ,smtp.sina.com
    // ::xiaotiantian89@qq.com,Lpo09bnm4RtJam2,
    // ::
    // 发送邮件的信箱:
    private String fromAuthor = "xiaotian8911@126.com";
    // 发送邮件的信箱的密:T89bmw154d1Lm2
    private String fromPassword = "T89bmw154d1Lm2";
    // 发送邮件的信息服务器host主机
    private String host = "smtp.126.com";
    // 接收邮件的信箱
    private String toAuthor = "xiaotiantian8911@sina.com";
    // 邮件会话Session
    private Session session;

    public UtilEmail() {
        // 配置邮件服务器
        Properties props = new Properties();
        props.put("mail.smtp.user", fromAuthor);
        props.setProperty("mail.transport.protocol", "smtp"); // 协议
        props.setProperty("mail.host", host); // 主机
        props.put("mail.smtp.auth", "true"); // 密码验证
        props.put("mail.smtp.port", "465"); // 主机端口
        props.put("mail.smtp.socketFactory.port", "465"); // Socket Factory 端口
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false"); // 可撤回
        props.setProperty("mail.smtp.quitwait", "false"); // 稍后
        props.setProperty("mail.smtp.timeout", "30000"); // Socket 超时
        session = Session.getDefaultInstance(props, this);
    }

    public synchronized void sendMailSMS(String subject, String content) throws MessagingException {
        //
        Mylog.info("Send Email .....");
        MimeMessage message = new MimeMessage(session);

        message.setSender(new InternetAddress(fromAuthor)); // 设置发件人::与登录服务器相同
        // 设置标题
        message.setSubject(subject, "utf-8");

        // 构造内容handler处理器
        DataHandler handler = new DataHandler(new ByteArrayDataSource(content.toString().getBytes(), "text/html; charset=utf-8"));

        // 设置内容handler
        message.setDataHandler(handler);
        // 必须最后设置
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(toAuthor)); // 设置目的邮箱
        Transport.send(message);
        Mylog.info("Send Email success!");
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        // TODO Authentication 验证接口::用户登录服务器验证
        return new PasswordAuthentication(fromAuthor, fromPassword);
    }

    // inner class byte 数据源转换器
    public class ByteArrayDataSource implements DataSource {
        private byte[] data;
        private String type;

        public ByteArrayDataSource(byte[] data, String type) {
            super();
            this.data = data;
            this.type = type;
        }

        public ByteArrayDataSource(byte[] data) {
            super();
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentType() {
            if (type == null) return "application/octet-stream";
            else return type;
        }

        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }

    public static class EmailMessagePair {
        private String subject;
        private String content;

        public EmailMessagePair() {}

        public EmailMessagePair(String subject, String content) {
            this.subject = subject;
            this.content = content;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

    }

    /**
     * @author XiaoTian
     * @version 1.0.0
     * @name JSSEProvider
     * @description 邮件协议供应器
     * @date 2013-10-17
     * @link gtrstudio@qq.com
     * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
     */
    final static class JSSEProvider extends Provider {

        private static final long serialVersionUID = 1L;

        public JSSEProvider() {
            super("HarmonyJSSE", 1.0, "Harmony JSSE Provider");
            AccessController.doPrivileged(new java.security.PrivilegedAction<Void>() {
                public Void run() {
                    put("SSLContext.TLS", "org.apache.harmony.xnet.provider.jsse.SSLContextImpl");
                    put("Alg.Alias.SSLContext.TLSv1", "TLS");
                    put("KeyManagerFactory.X509", "org.apache.harmony.xnet.provider.jsse.KeyManagerFactoryImpl");
                    put("TrustManagerFactory.X509", "org.apache.harmony.xnet.provider.jsse.TrustManagerFactoryImpl");
                    return null;
                }
            });
        }
    }
}
