/**
 * 
 */
package com.loan.aggregator.manager;



import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.loan.aggregator.client.LoanAggregatorClientService;
import com.loan.aggregator.client.bo.Customer;
import com.loan.aggregator.client.request.mapper.CustomerRequestMapper;
import com.loan.aggregator.exception.MyLoanAggregatorException;
import com.loan.aggregator.mapper.ConsumerMapper;
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
import com.loan.aggregator.request.LoginRequest;
import com.loan.aggregator.request.RequestHeader;
import com.loan.aggregator.response.ConsumerLoginResponse;
import com.loan.aggregator.response.ConsumerLoginResponseData;
import com.loan.aggregator.response.ConsumerRegistrationResponse;
import com.loan.aggregator.response.ConsumerRegistrationResponseData;
import com.loan.aggregator.response.Response;
import com.loan.aggregator.util.CommonConstants;
import com.loan.aggregator.util.DateUtility;
import com.loan.aggregator.util.MessageUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 
 */
@Service
public class ConsumerManager extends BaseManager {
	
	private static final Logger log = LoggerFactory.getLogger(ConsumerManager.class);
	
	@Autowired
	private ConsumerRepository consumerRepository;
	
	@Autowired
	private AppInfoLkpRepository appInfoLkpRepository;
	
	@Autowired
	private ConsumerSessionInfoRepository sessionDao;
	
	@Autowired
	private ConsumerSessionLinkRepository sessionlnkDao;
	
	@Autowired
	private LoanAggregatorClientService loanAggregatorClientService;
	
	@Autowired
	private CustomerRequestMapper customerRequestMapper;
	
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
			log.info("registerConsumer ThreadName Start :",Thread.currentThread().getName());
			if(appInfoLkp.isRegistrations()!=CommonConstants.BYTE_ONE && CommonConstants.ESSO_UK.equals(appId)) {
				response.setStatusCode(String.valueOf(HttpStatus.CONFLICT.value()));
				response.setMessage(MessageUtil.getMessage(appId.toLowerCase()+"."+CommonConstants.CONSUMER_REGISTRATION_NOTALLOWED));
				return response;
				
			}
			isRegistered=isEmailAlreadyRegisted(consumerRequest.getEmail(),appInfoLkp);
			if(!isRegistered) {
				consumer=createConsumer(consumerRequest,appInfoLkp);
				String token=getToken();
				createConsumerSessionLink(consumer,CommonConstants.EMAIL_VERIFICATION,token);
				consumerRepository.save(consumer);
				if(consumer.getConsumerId()!=null) {
					ConsumerRegistrationResponseData consumerResponse=new ConsumerRegistrationResponseData();
					consumerResponse.setConsumerId(consumer.getConsumerId()!=null?consumer.getConsumerId().toString():null);
					response.setStatusCode(CommonConstants.SUCCESS_CODE);
					response.setMessage(getMessage(appId.toLowerCase() + ".consumer.reg.message"));
					sendWelcomeEmail(consumer,appInfoLkp,token);
					consumerResponse.setToken(token);
					consumerRequest.setPassword(getSecurePassword(consumer, consumerRequest.getPwd()));
					//Client call
					try {
						Customer customer=loanAggregatorClientService.registerCustomer(customerRequestMapper.toCustomerRegistrationDetailsExposed(consumerRequest), appInfoLkp);
						if(!Objects.isNull(customer)) {
							consumerResponse.setFullName(customer.getFirstName() + " "+customer.getLastName());
							consumerResponse.setPhoneNumber(customer.getPhoneNumber());
							consumerResponse.setEmail(customer.getEmail());
							consumerResponse.setPassword(customer.getPassword());
							consumer.setCuid(customer.getCuid());
							consumerRepository.save(consumer);
						}
						response.setResponseData(consumerResponse);
						log.info("registerConsumer ThreadName End :" + Thread.currentThread().getName());
						return response;
					} catch (MyLoanAggregatorException e) {
						throw new MyLoanAggregatorException(CommonConstants.CUSTOM_ERROR_CODE,
								MessageUtil.getMessage(CommonConstants.TECHNICAL_ERROR));
					}
				}
			}else {
				throw new MyLoanAggregatorException(String.valueOf(HttpStatus.CONFLICT.value()),
						MessageUtil.getMessage(MessageUtil.getMessage(appId.toLowerCase()+"."+CommonConstants.CONSUMER_ALREADY_REGISTERED)));
//				response.setStatusCode(String.valueOf(HttpStatus.CONFLICT.value()));
//				response.setMessage(MessageUtil.getMessage(appId.toLowerCase()+"."+CommonConstants.CONSUMER_ALREADY_REGISTERED));
//				return response;
			}
		}
		throw new MyLoanAggregatorException(CommonConstants.CUSTOM_ERROR_CODE,
				MessageUtil.getMessage(CommonConstants.TECHNICAL_ERROR));
	}

	private void sendWelcomeEmail(Consumer consumer, AppInfoLkp appInfoLkp,String token)throws MyLoanAggregatorException{
		Map<String,Object> dataMap=createDataMap(consumer,appInfoLkp,token);
		System.out.println("Email Sent Successfully.....");
		//taskExecutor.execute(new SendEmailTaskExecutor(dataMap,emailNotification,appInfoLkp.getAppId()));
	}
	private Map<String, Object> createDataMap(Consumer consumer, AppInfoLkp appInfoLkp,String token) {
		Map<String, Object> dataMap=new HashMap<String, Object>();
		String app_id=appInfoLkp.getAppId().toLowerCase();
		//dataMap.put("userName", consumer.getFirstName()+" "+consumer.getLastName());
		//dataMap.put("phoneNumber", consumer.getPhoneNumber());
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
		ConsumerSessionLink consumersessionlnk = sessionlnkDao.findByConsumerAndTypeAndDeleteFlag(consumer,
				type, CommonConstants.BYTE_ZERO);
		if(CommonConstants.EMAIL_VERIFICATION.equals(type) && consumersessionlnk==null) {
			ConsumerSessionLink sessionlnk=new ConsumerSessionLink();
			sessionlnk.setConsumer(consumer);
			sessionlnk.setType(type);
			sessionlnk.setSecretToken(token);
			sessionlnk.setCreatedDate(DateUtility.getCalendarInUTCZone().getTime());
			sessionlnk.setModifiedDate(DateUtility.getCalendarInUTCZone().getTime());
			sessionlnk.setDeleteFlag(CommonConstants.BYTE_ZERO);
			sessionlnk=sessionlnkDao.save(sessionlnk);
			return sessionlnk;
		}
		return null;
	}

	public String getToken() {
		return UUID.randomUUID().toString();
	}

	private Consumer createConsumer(ConsumerRequest consumerRequest, AppInfoLkp appInfoLkp) {
		if(consumerRequest== null || appInfoLkp==null) {
			return null;
		}
		Consumer consumer=ConsumerMapper.mapToConsumer(consumerRequest);
		consumer.setAppinfolkp(appInfoLkp);
		consumerRequest.setAppId(appInfoLkp.getAppId());
		
		consumer=consumerRepository.save(consumer);
		
		ConsumerSessionInfo sessionDetails=sessionDao.save(getConsumerSession(consumer));
		Set<ConsumerSessionInfo> sessions=new HashSet<ConsumerSessionInfo>();
		sessions.add(sessionDetails);
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
		ConsumerSessionLink consumerSession = sessionlnkDao.findBySecretTokenAndTypeAndConsumer(token,
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
			sessionlnkDao.save(consumerSession);
			System.out.println("Email Verification link is expired");
			System.out.println("verification token expired for consumer[" + consumer.getEmail() + "]");
			return MessageUtil.getMessage("consumer.email.verification.link.expired");
		}
		consumer.setEmailVerified(CommonConstants.BYTE_ONE);
		consumer.setModifiedDate(DateUtility.getCalendarInUTCZone().getTime());
		consumerSession.setDeleteFlag(CommonConstants.BYTE_ONE);
		sessionlnkDao.save(consumerSession);
		consumerRepository.save(consumer);
		System.out.println("email verification success[" + consumer.getConsumerId() + "]");
		return MessageUtil.getMessage("consumer.email.verification.success");
	}


	public Response login(LoginRequest loginRequest, RequestHeader requestHeaders) {
		System.out.println("LoginRequest:::"+loginRequest);
		ConsumerLoginResponse response=null;
		try {
			if(loginRequest!=null) {
				response=new ConsumerLoginResponse();
				AppInfoLkp appInfoLkp=requestHeaders.getAppInfoLkp();
				Consumer consumer=consumerRepository.findByEmailAndIsActiveAndAppinfolkp(loginRequest.getEmail(), (byte)1, appInfoLkp);
				
				//Email registered or not
				if(isEmailNotRegistered(consumer)) {
					response.setStatusCode(String.valueOf(HttpStatus.NOT_FOUND.value()));
					response.setMessage(getMessage(requestHeaders.getAppId().toLowerCase() + ".login.consumer.email.not.registered"));
					System.out.println("Email is not registered");
					return response;
					
				}
				
				//get session or create if already deleted or not available
				ConsumerSessionInfo consumerSession=sessionDao.findFirstByConsumerAndDeleteFlagOrderByConsumerSessionIdDesc(consumer,(byte)0);
				ConsumerSessionInfo sessionInfo=consumerSession!=null?consumerSession:sessionDao.save(getConsumerSession(consumer));
				System.out.println("Consumer Session Details in Login::"+sessionInfo);
				
				//get time difference
				Long lastLoginAttemptInMinutes=TimeUnit.MILLISECONDS.toMinutes(DateUtility.getTimeDiffInMilliSecs(sessionInfo.getModifiedDate(),DateUtility.getCalendarInUTCZone().getTime()));
				
				//reset login count
				int loginCount=resetLoginCountIfUnblocked(consumer,lastLoginAttemptInMinutes);
				
				//Get consumer details from client
				Map<String, String>  profileMap=invokeRestApiToFetchConsumerDetails(consumer,appInfoLkp);
				String clientPassword = profileMap.get("password");
				String loginPwd = loginRequest.getPassword();
				
				if (loginPwd ==null || clientPassword ==null) {
					throw new MyLoanAggregatorException(CommonConstants.CUSTOM_ERROR_CODE,
							MessageUtil.getMessage(CommonConstants.TECHNICAL_ERROR));
				}
				
				//Authorize user
				if(isNotAuthorized(consumer,loginPwd,clientPassword)) {
					return loginAttemptCounter(consumer,loginCount, response, appInfoLkp);
				}
				
				//already login check
				boolean isAlreadyLoggedIn=consumer.getIsLoggedIn()!=CommonConstants.BYTE_ONE?false:true;
				if(isAlreadyLoggedIn) {
					sessionInfo.setModifiedDate(DateUtility.getCalendarInUTCZone().getTime());
					sessionDao.save(sessionInfo);
					response.setStatus(CommonConstants.STATUS_FAILURE);
					response.setMessage(MessageUtil.getMessage(appInfoLkp.getAppId().toLowerCase()+ ".consumer.already.login"));
					return response;
				}
				//login success response
				ConsumerLoginResponseData loginResponseData=new ConsumerLoginResponseData();
				loginResponseData.setEmail(loginRequest.getEmail());
				loginResponseData.setFirstName(profileMap.get("firstName"));
				loginResponseData.setLastName(profileMap.get("lastName"));
				loginResponseData.setPhoneNumber(profileMap.get("phoneNumber"));
				loginResponseData.setAuthToken(sessionInfo.getSessionToken());
				response.setStatusCode(CommonConstants.SUCCESS_CODE);
				response.setMessage(getMessage(requestHeaders.getAppId().toLowerCase() + ".consumer.login.message"));
				response.setResponseData(loginResponseData);
				return response;
			}
			
		} catch (Exception e) {
			System.out.println("Login exception");
		}
		return null;
	}


	private Response loginAttemptCounter(Consumer consumer, int loginAttemptCount,Response response,AppInfoLkp appInfo) {
		if(loginAttemptCount>=3) {
			forceLogoutUser(consumer,appInfo);
			response.setStatus(CommonConstants.STATUS_FAILURE);
			response.setMessage(MessageUtil.getMessage(appInfo.getAppId().toLowerCase()+ ".login.consumer.account.blocked"));
			return response;
		}
		//update login attempt count
		loginAttemptCount+=1;
		consumer.setLoginCount(new BigInteger(String.valueOf(loginAttemptCount)));
		consumer.setModifiedDate(DateUtility.getCalendarInUTCZone().getTime());
		consumerRepository.save(consumer);
		
		response.setStatus(CommonConstants.STATUS_FAILURE);
		response.setMessage(MessageUtil.getMessage(appInfo.getAppId().toLowerCase()+ ".login.consumer.credential.incorrect"));
		return response;
	}

	private void forceLogoutUser(Consumer consumer, AppInfoLkp appInfo) {
		//delete consumer details
		consumer.setIsLoggedIn(CommonConstants.BYTE_ONE);
		consumer.setModifiedDate(DateUtility.getCalendarInUTCZone().getTime());
		consumerRepository.save(consumer);
		
		
		//delete session details
		deleteAuthSessionToken(consumer);
		
		//delete sessionlnk details
		deleteAuthorizationToken(consumer);
	}

	private void deleteAuthorizationToken(Consumer consumer) {
		ConsumerSessionLink sessionlnk=sessionlnkDao.findByConsumerAndTypeAndDeleteFlag(consumer, "Authorization", (byte)0);
		if(Objects.nonNull(sessionlnk)) {
			sessionlnk.setDeleteFlag(CommonConstants.BYTE_ONE);
			sessionlnk.setModifiedDate(DateUtility.getCalendarInUTCZone().getTime());
			sessionlnkDao.save(sessionlnk);
		}
	}

	private void deleteAuthSessionToken(Consumer consumer) {
		ConsumerSessionInfo sessionInfo=sessionDao.findFirstByConsumerAndDeleteFlagOrderByConsumerSessionIdDesc(consumer, (byte)0);
		if(Objects.nonNull(sessionInfo)) {
			sessionInfo.setDeleteFlag(CommonConstants.BYTE_ONE);
			sessionInfo.setModifiedDate(DateUtility.getCalendarInUTCZone().getTime());
			sessionDao.save(sessionInfo);
			
		}
	}

	private boolean isNotAuthorized(Consumer consumer, String loginPwd, String clientPassword) {
		if(!clientPassword.equals(loginPwd)) {
			System.out.println("CONSUMER_NOT_AUTHORIZED");
			return true;
		}
		return false;
	}

	private int resetLoginCountIfUnblocked(Consumer consumerData,Long lastLoginAttemptInMinutes) {
		int loginCount=consumerData.getLoginCount().intValue();
		if(loginCount>=3 && lastLoginAttemptInMinutes>=2) {
			consumerData.setLoginCount(new BigInteger(String.valueOf(0)));
			consumerRepository.save(consumerData);
			System.out.println("Account is un-blocked now");
			return 0;
		}
		return loginCount;
	}
}
