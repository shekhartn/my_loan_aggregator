/**
 * 
 */
package com.loan.aggregator.util;

import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * 
 */
public class MessageUtil {
	
	public static String getMessage(String key) {
		Locale currentLocale=LocaleContextHolder.getLocale();
		ResourceBundleMessageSource resource=new ResourceBundleMessageSource();
		resource.setBasename("messages/messages");
		String message=resource.getMessage(key,null, currentLocale);
		return message;
	}

}
