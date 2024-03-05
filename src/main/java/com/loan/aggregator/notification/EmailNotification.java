package com.loan.aggregator.notification;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.loan.aggregator.util.CommonConstants;
@Component
public class EmailNotification implements Notification {
	@Autowired
	private Environment env;
	
	@Autowired
	private VelocityEngine velocityEngine;
	
	@Autowired
	private AmazonSESEmailService sesMailSender;
	
	static final ResourceBundle envResourceBundle = ResourceBundle.getBundle(CommonConstants.ENV);
	

	@Override
	public void sendNotification(Map<String, Object> mapData, String appId) {
		String messageId="";
		try {
			//String FROM = env.getProperty(appId.toLowerCase() + CommonConstants.FROM_EMAIL);
			String FROM = envResourceBundle.getString(appId.toLowerCase() + CommonConstants.FROM_EMAIL);
			String REPLY_TO = String.valueOf(mapData.get("toEmail"));
			String SUBJECT = String.valueOf(mapData.get("subject"));
			mapData.put("appId", appId!=null?appId.toLowerCase():null);
			String content = getEmailContent(mapData);
			messageId=sesMailSender.sendRawEmail(FROM, REPLY_TO, SUBJECT, content, appId);
		} catch (Exception e) {
			System.out.println("Exception occured in sending mail:::"+e);
		}
		try {
			String message=sesMailSender.sendSMS(String.valueOf(mapData.get("userName")),String.valueOf(mapData.get("phoneNumber")));
			System.out.println(message);
		} catch (Exception e) {
			System.out.println("Exception occured in sending mail-1:::"+e);
		}
	}

	private String getEmailContent(Map<String, Object> mapData) {
		String callbackUrl = String.valueOf(mapData.get("callbackUrl"));
		String templatePath = String.valueOf(mapData.get(CommonConstants.TEMPLATEPATH));
		String appId = String.valueOf(mapData.get("appId"));
		String token = String.valueOf(mapData.get("token"));
		if (templatePath.contains("welcomeemail")) {
			callbackUrl = callbackUrl + "/consumers/" + String.valueOf(mapData.get("consumerId")) + "/emailverification/"
					+ String.valueOf(token + "?appId=" +appId);
		}
		mapData.put("verifyLink", callbackUrl);
		StringWriter result = new StringWriter();
		velocityEngine.mergeTemplate(String.valueOf(mapData.get(CommonConstants.TEMPLATEPATH)), "UTF-8", this.getVelocityContext(mapData), result);
		return result.toString();
	}
	private Context getVelocityContext(Map<String, Object> model) {
		final VelocityContext velocityContext = new VelocityContext(model);
		velocityContext.put("date", LocalDateTime.class);
		velocityContext.put("zoneId", ZoneId.class);
		velocityContext.put("format", DateTimeFormatter.class);
		return velocityContext;
	}

	/**
	 * @return the velocityEngine
	 */
	public VelocityEngine getVelocityEngine() {
		return velocityEngine;
	}

	/**
	 * @param velocityEngine the velocityEngine to set
	 */
	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}

	/**
	 * @return the sesMailSender
	 */
	public AmazonSESEmailService getSesMailSender() {
		return sesMailSender;
	}

	/**
	 * @param sesMailSender the sesMailSender to set
	 */
	public void setSesMailSender(AmazonSESEmailService sesMailSender) {
		this.sesMailSender = sesMailSender;
	}
	
	

}
