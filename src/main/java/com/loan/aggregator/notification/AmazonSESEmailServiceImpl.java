package com.loan.aggregator.notification;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.amazonaws.services.simpleemail.model.SendRawEmailResult;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.loan.aggregator.util.CommonConstants;
import com.loan.aggregator.util.MessageUtil;

@Component
public class AmazonSESEmailServiceImpl implements AmazonSESEmailService {
	
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;


	@Override
	public String sendRawEmail(String from, String to, String subject, String body, String appId)
			throws MessagingException, ParseException, IOException {
		final String defaultCharSet = MimeUtility.getDefaultJavaCharset();
		final Session session = Session.getDefaultInstance(getProps());
		MimeMessage message = new MimeMessage(session);
		try {
			message.setSubject(subject, "UTF-8");
			message.setFrom(new InternetAddress(from));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			this.createMessageBody(body, defaultCharSet, message,appId);
			SendRawEmailResult result=this.sendEmail(message);
			return result.getMessageId();
		} catch (Exception e) {
			System.out.println("The email was not sent.");
		}
		return null;
	}
	
	private SendRawEmailResult sendEmail(MimeMessage message) throws IOException, MessagingException {
		System.out.println("Attempting to send an email through Amazon SES using the AWS SDK for Java... ");
		final var client = getSESClient();
		final var rawEmailRequest = this.createRawEmailMessage(message);
		SendRawEmailResult result=null;
		try {
			
			result = client.sendRawEmail(rawEmailRequest);
			System.out.println("Email sent - " + result.getMessageId());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.out.println(e.getStackTrace());
		}
		return result;
	}
	
	private SendRawEmailRequest createRawEmailMessage(final MimeMessage message) throws IOException, MessagingException {
		// Send the email.
		final var outputStream = new ByteArrayOutputStream();
		message.writeTo(outputStream);
		final var rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
		final var rawEmailRequest = new SendRawEmailRequest(rawMessage);
		return rawEmailRequest;
	}

	
	private AmazonSimpleEmailService getSESClient() {
        return AmazonSimpleEmailServiceClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(getCredentials()))
                .withRegion(region)
                .build();
	}
	
	private AmazonSNS getSNSClient() {
		AWSStaticCredentialsProvider cred=new AWSStaticCredentialsProvider(getCredentials());
		return  AmazonSNSClientBuilder.standard()
				.withCredentials(cred)
				.withRegion(region)
				.build();
	}
	private BasicAWSCredentials getCredentials() {
		return new BasicAWSCredentials(accessKey, secretKey);
	}
	private MimeMultipart createMessageBody(final String BODY_HTML, final String DefaultCharSet, final MimeMessage message,final String appId)
			throws MessagingException, UnsupportedEncodingException {
		// Create a multipart/alternative child container.
		final var msg_body = new MimeMultipart("alternative");
		// Create a wrapper for the HTML and text parts.
		final var wrap = new MimeBodyPart();
		// Define the HTML part.
		final var htmlPart = new MimeBodyPart();
		// Encode the HTML content and set the character encoding.
		htmlPart.setContent(MimeUtility.encodeText(BODY_HTML, DefaultCharSet, "B"), "text/html; charset=UTF-8");
		htmlPart.setHeader("Content-Transfer-Encoding", "base64");
		// Add the text and HTML parts to the child container.
		msg_body.addBodyPart(htmlPart);
		// Add the child container to the wrapper object.
		wrap.setContent(msg_body);
		// Create a multipart/mixed parent container.
		final var msg = new MimeMultipart("mixed");
		// Add the multipart/alternative part to the message.
		msg.addBodyPart(wrap);
		// Add the parent container to the message.
		message.setContent(msg);
		return msg;
	}
	
	private Properties getProps() {
		final var props=new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.port", "587"); 
        props.setProperty("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        return props;
	}

	@Override
	public String sendSMS(String name, String phoneNumber) throws MessagingException, ParseException, IOException {
		try {
			AmazonSNS snsClient=getSNSClient();
			final PublishRequest publishRequest = new PublishRequest();
			publishRequest.setPhoneNumber(phoneNumber);
			String smsMessage="Hey "+name+MessageUtil.getMessage(CommonConstants.SMS_SUCCESS_MESSAGE);
			publishRequest.setMessage(smsMessage);
			PublishResult publishResponse=snsClient.publish(publishRequest);
			System.out.println("SMS::"+smsMessage+"\nResult::"+publishResponse);
			System.out.println("SDK Http response status code:::"+publishResponse.getSdkHttpMetadata().getHttpStatusCode());
		    if (publishResponse.getSdkHttpMetadata().getHttpStatusCode()==200) {
		        System.out.println("Message publishing to phone successful");
		    } else {
		        throw new ResponseStatusException(
		            HttpStatus.INTERNAL_SERVER_ERROR, publishResponse.getSdkResponseMetadata().getRequestId()
		        );
		    }
		    snsClient.shutdown();
		    return "SMS sent to " + phoneNumber + ". Message-ID: " + publishResponse.getMessageId();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception in sending SMS::"+e);
		}
		return null;
	}

}
