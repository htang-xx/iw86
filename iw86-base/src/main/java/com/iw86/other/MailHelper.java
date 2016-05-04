/**
 * 
 */
package com.iw86.other;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.iw86.base.Constant;
import com.iw86.lang.StringUtil;

/**
 * 邮件发送程序
 * @author tanghuang
 */
public class MailHelper {
	private JavaMailSenderImpl sender;
	
	public MailHelper(JavaMailSenderImpl sender){
		this.sender = sender;
	}
	
	/**
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 */
	public MailHelper(String host, int port, String username, String password){
		sender = new JavaMailSenderImpl();
		sender.setHost(host);
		sender.setUsername(username);
		sender.setPassword(password);
		sender.setPort(port);
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		sender.setJavaMailProperties(props);
	}
	
	/**
	 * 发送邮件
	 * @param from 收件人（目前只支持1个）
	 * @param touser
	 * @param cc 抄送人（多个以,隔开）
	 * @param title
	 * @param content
	 * @param isHtml
	 * @param attachments
	 * @param saveName
	 * @throws Exception
	 */
	public void send2(String from, String touser, String cc, String title,
			String content, boolean isHtml, List<String> attachments,String saveName)throws Exception{
		MimeMessage mailMessage = sender.createMimeMessage();
		MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true, "GBK"); //true表示multipart邮件，指定邮件GBK编码(貌似只有GBK不会乱码)
		messageHelper.setTo(touser);// 接受者
		if (!StringUtil.isEmpty(from)) messageHelper.setFrom(from);// 发送者
		else messageHelper.setFrom(sender.getUsername());
		
		messageHelper.setSubject(title);// 主题
		messageHelper.setText(content, isHtml); // 邮件内容，注意加参数true，表示启用html格式
		
		if(attachments!=null){
			for (int i = 0; i < attachments.size(); i++) {
				File file = new File(attachments.get(i));
				messageHelper.addAttachment(MimeUtility.encodeWord(file.getName()),file);// 使用MimeUtility.encodeWord()来解决附件名称的中文问题
			}
		}
		
		if (!StringUtil.isEmpty(cc)){ //抄送
			mailMessage.addRecipients(RecipientType.CC, InternetAddress.parse(cc));
		}
		
		sender.send(mailMessage);
		
		if(!StringUtil.isEmpty(saveName)){
			FileOutputStream os = new FileOutputStream(saveName);
			mailMessage.writeTo(os);
			os.flush();
			os.close();
		}
	}
	
	public void send(String from, String fromname, String to, String cc, String title,
			String content, boolean isHtml, List<String> attachments,String saveName)throws Exception{
		MimeMessage mailMessage = sender.createMimeMessage();
		mailMessage.setHeader("Content-Type","text/html;charset=utf-8");
		mailMessage.setHeader("X-Mailer", "mail");
		if(!StringUtil.isEmpty(fromname)){
			fromname=javax.mail.internet.MimeUtility.encodeText(fromname); 
            mailMessage.setFrom(new InternetAddress(fromname+" <"+from+">"));
		}else{
			mailMessage.setFrom(new InternetAddress(from));
		}
		mailMessage.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
		mailMessage.setSubject(title);

		MimeBodyPart mbp1 = new MimeBodyPart();
	    if(isHtml) mbp1.setContent(content,"text/html; charset=utf-8"); 
	    else mbp1.setText(content);
	    Multipart mp = new MimeMultipart();
	    
	    mp.addBodyPart(mbp1);
	    mailMessage.setContent(mp);
	    mailMessage.saveChanges();

		if(attachments!=null){
			for (int i = 0; i < attachments.size(); i++) {
				MimeBodyPart mbp = new MimeBodyPart(); 
				DataSource source = new FileDataSource(attachments.get(i));
				mbp.setDataHandler(new DataHandler(source));
				mbp.setFileName(MimeUtility.encodeWord(source.getName(), Constant.UTF_8,null));
				mp.addBodyPart(mbp);
			}
		}
		
		if (!StringUtil.isEmpty(cc)){ //抄送
			mailMessage.addRecipients(RecipientType.CC, InternetAddress.parse(cc));
		}
		
		sender.send(mailMessage);
		
		if(!StringUtil.isEmpty(saveName)){
			FileOutputStream os = new FileOutputStream(saveName);
			mailMessage.writeTo(os);
			os.flush();
			os.close();
		}
	}
	
	/**
	 * 转发邮件
	 * @param from
	 * @param to
	 * @param mmsg
	 * @throws Exception
	 */
	public void forward(String from, String to, String cc, MimeMessage mmsg)throws Exception{
		MimeMessage forwardmsg = sender.createMimeMessage();
		forwardmsg.setSubject(getSubject(mmsg));
		forwardmsg.setFrom(new InternetAddress(from));
		forwardmsg.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
		if (!StringUtil.isEmpty(cc)){
			forwardmsg.addRecipients(RecipientType.CC, InternetAddress.parse(cc));
		}
		Multipart multipart = new MimeMultipart();
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setDataHandler(mmsg.getDataHandler());
		multipart.addBodyPart(messageBodyPart);
		forwardmsg.setContent(multipart);
		
		sender.send(forwardmsg);
	}

	/**
	 * 标题的特殊处理
	 * @param mmsg
	 * @return
	 */
	public static String getSubject(MimeMessage mmsg){
        try {
			String header = mmsg.getHeader("SUBJECT")[0];
			String subject = MimeUtility.decodeText(header);
			if (header.toLowerCase().startsWith("=?gb")) {

			} else if (header.toLowerCase().startsWith("=?utf")) {
				subject = new String((subject.getBytes(Constant.UTF_8)), Constant.UTF_8);
			} else {
				subject = new String((subject.getBytes(Constant.ISO_8859_1)), Constant.GBK);
			}
			return subject;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}
