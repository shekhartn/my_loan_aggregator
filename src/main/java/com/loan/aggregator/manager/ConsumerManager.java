/**
 * 
 */
package com.loan.aggregator.manager;


import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.loan.aggregator.exception.MyLoanAggregatorException;
import com.loan.aggregator.model.AppInfoLkp;
import com.loan.aggregator.model.Consumer;
import com.loan.aggregator.model.ConsumerSessionInfo;
import com.loan.aggregator.model.ConsumerSessionLink;
import com.loan.aggregator.notification.EmailNotification;
import com.loan.aggregator.repository.AppInfoLkpRepository;
import com.loan.aggregator.repository.ConsumerRepository;
import com.loan.aggregator.repository.ConsumerSessionInfoRepository;
import com.loan.aggregator.repository.ConsumerSessionLinkRepository;
import com.loan.aggregator.request.ConsumerRequest;
import com.loan.aggregator.request.RequestHeader;
import com.loan.aggregator.response.ConsumerRegistrationResponse;
import com.loan.aggregator.response.ConsumerRegistrationResponseData;
import com.loan.aggregator.response.Response;
import com.loan.aggregator.task.SendEmailTaskExecutor;
import com.loan.aggregator.util.CommonConstants;
import com.loan.aggregator.util.DateUtility;
import com.loan.aggregator.util.MessageUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 
 */
@Service
public class ConsumerManager extends BaseManager {
	
	@Autowired
	private ConsumerRepository consumerRepository;
	
	@Autowired
	private AppInfoLkpRepository appInfoLkpRepository;
	
	@Autowired
	private ConsumerSessionInfoRepository consumerSessionInfoRepository;
	
	@Autowired
	private ConsumerSessionLinkRepository consumerSessionlnkRepository;
	
	@Autowired
	protected TaskExecutor taskExecutor;
	
	@Autowired
	private EmailNotification emailNotification;

	public Response registerConsumer(ConsumerRequest consumerRequest, RequestHeader requestHeaders)throws MyLoanAggregatorException {
		
		ConsumerRegistrationResponse response=new ConsumerRegistrationResponse();
		AppInfoLkp appInfoLkp=requestHeaders.getAppInfoLkp();
		String appId=requestHeaders.getAppId();
		boolean isRegistered;
		Consumer consumer;
		synchronized (this) {
			System.out.println("registerConsumer ThreadName Start :" + Thread.currentThread().getName());
			if(appInfoLkp.isRegistrations()!=CommonConstants.BYTE_ONE && CommonConstants.ESSO_UK.equals(appId)) {
				response.setStatusCode(String.valueOf(HttpStatus.CONFLICT.value()));
				response.setMessage(MessageUtil.getMessage(appId.toLowerCase()+"."+CommonConstants.CONSUMER_REGISTRATION_NOTALLOWED));
				return response;
				
			}
			isRegistered=isEmailAlreadyRegisted(consumerRequest.getEmail(),appInfoLkp);
			if(!isRegistered) {
				consumer=createConsumer(consumerRequest,appInfoLkp);
				if(consumer.getConsumerId()!=null) {
					ConsumerRegistrationResponseData consumerResponse=new ConsumerRegistrationResponseData();
					consumerResponse.setConsumerId(consumer.getConsumerId()!=null?consumer.getConsumerId().toString():null);
					consumerResponse.setSessionToken(consumer.getConsumerSessionInfo().stream().findFirst().get().getSessionToken());
					consumerResponse.setFullName(consumerRequest.getFirstName() + " "+consumerRequest.getLastName());
					response.setStatusCode(CommonConstants.SUCCESS_CODE);
					response.setMessage(getMessage(appId.toLowerCase() + ".consumer.reg.message"));
					sendWelcomeEmail(consumer,appInfoLkp);
					ConsumerSessionLink consumerSessionLink=consumerSessionlnkRepository.findByConsumerAndTypeAndDeleteFlag(consumer, "Email Verification", (byte)0);
					consumerResponse.setToken(consumerSessionLink!=null?consumerSessionLink.getSecretToken():null);
					response.setResponseData(consumerResponse);
					
					return response;
				}
			}else {
				response.setStatusCode(String.valueOf(HttpStatus.CONFLICT.value()));
				response.setMessage(MessageUtil.getMessage(appId.toLowerCase()+"."+CommonConstants.CONSUMER_ALREADY_REGISTERED));
				return response;
			}
			System.out.println("registerConsumer ThreadName End :" + Thread.currentThread().getName());
		}
		throw new MyLoanAggregatorException(CommonConstants.CUSTOM_ERROR_CODE,
				MessageUtil.getMessage(CommonConstants.TECHNICAL_ERROR));
	}

	private void sendWelcomeEmail(Consumer consumer, AppInfoLkp appInfoLkp)throws MyLoanAggregatorException{
		String token=getToken();
		createConsumerSessionLink(consumer,CommonConstants.EMAIL_VERIFICATION,token);
		consumerRepository.save(consumer);
		Map<String,Object> dataMap=createDataMap(consumer,appInfoLkp,token);
		System.out.println("Email Sent Successfully.....");
		//taskExecutor.execute(new SendEmailTaskExecutor(dataMap,emailNotification,appInfoLkp.getAppId()));
	}
	private Map<String, Object> createDataMap(Consumer consumer, AppInfoLkp appInfoLkp,String token) {
		Map<String, Object> dataMap=new HashMap<String, Object>();
		String app_id=appInfoLkp.getAppId().toLowerCase();
		dataMap.put("userName", consumer.getFirstName()+" "+consumer.getLastName());
		dataMap.put("phoneNumber", consumer.getPhoneNumer());
		dataMap.put("callbackUrl",getCallbackUrl());
		dataMap.put("consumerId", consumer.getConsumerId());
		dataMap.put("toEmail", consumer.getEmail());
		dataMap.put("token",token);
		dataMap.put("subject", MessageUtil.getMessage(app_id+"."+CommonConstants.WELCOME_EMAIL_SUBJECT));
		dataMap.put("operation", CommonConstants.VERIFY_EMAIL);
		dataMap.put(CommonConstants.TEMPLATEPATH, CommonConstants.TEMPLATES + app_id + "/welcomeemail.vm");
		dataMap.put(CommonConstants.TEMPLATENAME, "welcomeemail");
		dataMap.put(CommonConstants.APP_ID, app_id);
		return dataMap;
	}

	private String getCallbackUrl() {
		return CommonConstants.BASE_URL;
	}

	private ConsumerSessionLink createConsumerSessionLink(Consumer consumer, String type,String token) {
		ConsumerSessionLink consumersessionlnk = consumerSessionlnkRepository.findByConsumerAndTypeAndDeleteFlag(consumer,
				type, CommonConstants.BYTE_ZERO);
		if(CommonConstants.EMAIL_VERIFICATION.equals("Email Verification") && consumersessionlnk==null) {
			ConsumerSessionLink sessionlnk=new ConsumerSessionLink();
			sessionlnk.setConsumer(consumer);
			sessionlnk.setType(type);
			sessionlnk.setSecretToken(token);
			sessionlnk.setCreatedDate(DateUtility.getCalendarInUTCZone().getTime());
			sessionlnk.setModifiedDate(DateUtility.getCalendarInUTCZone().getTime());
			sessionlnk.setDeleteFlag(CommonConstants.BYTE_ZERO);
			sessionlnk=consumerSessionlnkRepository.save(sessionlnk);
			return sessionlnk;
		}
		return null;
	}

	public String getToken() {
		return UUID.randomUUID().toString();
	}

	private Consumer createConsumer(ConsumerRequest consumerRequest, AppInfoLkp appInfoLkp) {
		Consumer consumer=new Consumer();
		ConsumerSessionInfo consumerSessionInfo=new ConsumerSessionInfo();
		Set<ConsumerSessionInfo> sessions=new HashSet<ConsumerSessionInfo>();
		if(consumerRequest== null || appInfoLkp==null) {
			return null;
		}
		consumer.setEmail(consumerRequest.getEmail());
		consumer.setFirstName(consumerRequest.getFirstName());
		consumer.setLastName(consumerRequest.getLastName());
		consumer.setEmailVerified(CommonConstants.BYTE_ZERO);
		consumer.setIsActive(CommonConstants.BYTE_ONE);
		consumer.setIsLoggedIn(CommonConstants.BYTE_ZERO);
		consumer.setAppinfolkp(appInfoLkp);
		consumer.setPhoneNumer(consumerRequest.getPhoneNumer());
		consumer.setCreatedDate(DateUtility.getCalendarInUTCZone().getTime());
		consumer.setModifiedDate(DateUtility.getCalendarInUTCZone().getTime());
		consumer=consumerRepository.save(consumer);
		
		consumerSessionInfo.setSessionToken(getSessionToken(consumerRequest.getEmail()));
		consumerSessionInfo.setCreatedDate(DateUtility.getCalendarInUTCZone().getTime());
		consumerSessionInfo.setModifiedDate(DateUtility.getCalendarInUTCZone().getTime());
		consumerSessionInfo.setConsumer(consumer);
		sessions.add(consumerSessionInfo);
		consumerSessionInfoRepository.save(consumerSessionInfo);
		consumer.setConsumerSessionInfo(sessions);
		
		return consumer;
	}



	private boolean isEmailAlreadyRegisted(String email, AppInfoLkp appInfoLkp) {
		Consumer consumer=consumerRepository.findByEmailAndIsActiveAndAppinfolkp(email,(byte)1,appInfoLkp);
		if(consumer!=null) {
			return true;
		}
		return false;
	}

	public String emailVerified(Consumer consumer, BigInteger consumerId, String token, String appId,
			HttpServletRequest request) {
		ConsumerSessionLink consumerSession = consumerSessionlnkRepository.findBySecretTokenAndTypeAndConsumer(token,
				"Email Verification", consumer);
		if (consumerSession == null || consumerSession.getSecretToken() == null) {
			System.out.println("INVALID_VERIFICAION_TOKEN");
			MessageUtil.getMessage("");
			return MessageUtil.getMessage(appId + ".consumer.invalid.request");

		} /* if email already verified */
		else if (consumer.getEmailVerified() == CommonConstants.BYTE_ONE) {
			return MessageUtil.getMessage("consumer.email.already.verified");
		} /* if tapped on old link */
		else if (consumerSession.getDeleteFlag() == CommonConstants.BYTE_ONE) {
			return MessageUtil.getMessage("consumer.email.verification.new.link.triggered");
		}

		Date date = consumerSession.getModifiedDate();
		long noOfDays = DateUtility.getDaysDifference(date, new java.util.Date());
		/* notify if link expired:validity 30 days */
		if (noOfDays > 30) {
			consumerSession.setDeleteFlag(CommonConstants.BYTE_ONE);
			consumerSessionlnkRepository.save(consumerSession);
			System.out.println("Email Verification link is expired");
			System.out.println("verification token expired for consumer[" + consumer.getEmail() + "]");
			return MessageUtil.getMessage("consumer.email.verification.link.expired");
		}
		consumer.setEmailVerified(CommonConstants.BYTE_ONE);
		consumer.setModifiedDate(DateUtility.getCalendarInUTCZone().getTime());
		consumerSession.setDeleteFlag(CommonConstants.BYTE_ONE);
		consumerSessionlnkRepository.save(consumerSession);
		consumerRepository.save(consumer);
		System.out.println("email verification success[" + consumer.getConsumerId() + "]");
		return MessageUtil.getMessage("consumer.email.verification.success");
	}

	private AppInfoLkp getAppInfoLkp(String appId) {
		
		if(appId!=null) {
			return appInfoLkpRepository.findByAppId(appId);
		}
		return null;
	}

}
