/**
 * 
 */
package com.loan.aggregator.manager;


import java.util.UUID;

import org.apache.tomcat.util.codec.binary.Base64;

import com.loan.aggregator.util.MessageUtil;



/**
 * 
 */
public class BaseManager {
	
	protected String getSessionToken(String email) {
		if(email!=null) {
			String sessionString=email+":"+UUID.randomUUID();
			return new String(Base64.encodeBase64(sessionString.getBytes(),true));
		}
		return null;
	}
	
	protected String getMessage(String key) {
		try {
			return MessageUtil.getMessage(key);
		} catch (Exception e) {
			System.out.println("Exception in get app message:"+e.getStackTrace());
		}
		return null;
	}

}
