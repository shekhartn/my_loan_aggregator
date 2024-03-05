package com.loan.aggregator.notification;

import java.io.IOException;
import java.text.ParseException;

import javax.mail.MessagingException;


public interface AmazonSESEmailService {
	public String sendRawEmail(String from, String to, String subject, String body, String appId)
			throws MessagingException, ParseException, IOException;
	public String sendSMS(String name,String phoneNumber)
			throws MessagingException, ParseException, IOException;
}
