/**
 * 
 */
package com.loan.aggregator.manager;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.collections4.MapUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;

import com.loan.aggregator.client.LoanAggregatorClientService;
import com.loan.aggregator.client.bo.Customer;
import com.loan.aggregator.exception.MyLoanAggregatorException;
import com.loan.aggregator.model.AppInfoLkp;
import com.loan.aggregator.model.Consumer;
import com.loan.aggregator.model.ConsumerSessionInfo;
import com.loan.aggregator.util.CommonConstants;
import com.loan.aggregator.util.DateUtility;
import com.loan.aggregator.util.HashingUtil;
import com.loan.aggregator.util.MessageUtil;



/**
 * 
 */
public class BaseManager {
	
	@Autowired
	private LoanAggregatorClientService loanAggregatorClientService;
	
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
	
	protected String getSecurePassword(Consumer consumer,String passwordToHash) {
		return HashingUtil.encrypt(consumer, passwordToHash);
	}
	
	protected ConsumerSessionInfo getConsumerSession(Consumer consumer) {
		ConsumerSessionInfo consumerSessionInfo=new ConsumerSessionInfo();
		consumerSessionInfo.setSessionToken(getSessionToken(consumer.getEmail()));
		consumerSessionInfo.setCreatedDate(DateUtility.getCalendarInUTCZone().getTime());
		consumerSessionInfo.setModifiedDate(DateUtility.getCalendarInUTCZone().getTime());
		consumerSessionInfo.setConsumer(consumer);
		return consumerSessionInfo;
	}

	protected boolean isEmailNotRegistered(Consumer consumer) {
		if(consumer!=null&&consumer.getEmail()!=null) {
			return false;
		}
		return true;
	}
	
	protected Map<String, String> invokeRestApiToFetchConsumerDetails(Consumer consumer, AppInfoLkp appInfoLkp) {
		Map<String, String> profileMap=null;
		try {
			profileMap=buildProfileMap(loanAggregatorClientService.getCustomer(createUrlAndPathVariableMapWithCustomerId(consumer),appInfoLkp));
			if (profileMap ==null || profileMap.isEmpty() || profileMap.get("password") ==null) {
				throw new MyLoanAggregatorException(CommonConstants.CUSTOM_ERROR_CODE,
						MessageUtil.getMessage(CommonConstants.TECHNICAL_ERROR));
			}
		}  catch (MyLoanAggregatorException e) {
			throw new MyLoanAggregatorException(CommonConstants.CUSTOM_ERROR_CODE,
					MessageUtil.getMessage(CommonConstants.TECHNICAL_ERROR));
		}
		return profileMap;
		
	}
	
	protected Map<String, String> buildProfileMap(Customer customer) {
		final Map<String, String> profileMap = new HashMap<>();
		if (customer == null) {
			return null;
		}
		profileMap.put("password", customer.getPassword());
		if (customer.getEmail() != null) {
			profileMap.put("email", customer.getEmail());
		}
		if (Objects.nonNull(customer.getFirstName()) && Objects.nonNull(customer.getLastName())){
			profileMap.put("firstName", customer.getFirstName());
			profileMap.put("lastName", customer.getLastName());
		}
		if (Objects.nonNull(customer.getPhoneNumber()) ){
			profileMap.put("phoneNumber", customer.getPhoneNumber());
		}
		return profileMap;
	}
	
	private <T> Map<String, String> createUrlAndPathVariableMapWithCustomerId(final T t) {
		final Map<String, String> urlAndPathVariables= new HashMap<>();
		if(t instanceof Consumer) {
			urlAndPathVariables.put("customerId", ((Consumer) t).getCuid());
		}else {
			urlAndPathVariables.put("customerId", String.valueOf(t));
		}
		return urlAndPathVariables;
	}

}
