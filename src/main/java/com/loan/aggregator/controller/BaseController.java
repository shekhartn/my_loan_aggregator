/**
 * 
 */
package com.loan.aggregator.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.loan.aggregator.exception.MyLoanAggregatorException;
import com.loan.aggregator.repository.AppInfoLkpRepository;
import com.loan.aggregator.repository.ConsumerSessionInfoRepository;
import com.loan.aggregator.request.RequestHeader;
import com.loan.aggregator.response.Response;
import com.loan.aggregator.util.CommonConstants;
import com.loan.aggregator.util.MessageUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 
 */
@Component
public class BaseController {

	protected static final Set<String> HTTP_METHODS = Collections.unmodifiableSet(
			new HashSet<>(Arrays.asList(HttpMethod.GET.name(), HttpMethod.PUT.name(), HttpMethod.PATCH.name())));

	@Autowired
	protected AppInfoLkpRepository appInfoLkpRepository;

	@Autowired
	protected ConsumerSessionInfoRepository consumerSessionInfoRepository;

	public RequestHeader createRequestHeaders(Object object) {
		String appId = null;
		String userAuthToken = null;
		if (object instanceof HttpServletRequest) {
			final HttpServletRequest headers = (HttpServletRequest) object;
			appId = headers.getHeader("X-App-Id");
			userAuthToken = headers.getHeader("X-User-Auth-Token");
			final RequestHeader requestHeader = new RequestHeader();
			requestHeader.setAppId(appId);
			requestHeader.setAuthToken(userAuthToken);

			if (requestHeader.getAppId() != null) {
				requestHeader.setAppInfoLkp(appInfoLkpRepository.findByAppId(appId));
			}

			if (requestHeader.getAuthToken() != null) {
				requestHeader.setConsumerSessionInfo(
						consumerSessionInfoRepository.findBySessionTokenAndDeleteFlag(userAuthToken, (byte) 0));
				;
			}

			return requestHeader;
		} else {
			new Exception("Excetion thrown in parsing request headers");
			/*
			 * throw new FirstDataMobileAppException(CUSTOM_ERROR_CODE,
			 * MessageUtil.getMessage("fd.user.request.format.error"), "");
			 */
		}

		return null;
	}

	protected ResponseEntity<? extends Response> restResponse(Response responseData, String method) {
		if (CommonConstants.SUCCESS_CODE.equals(responseData.getStatusCode())) {
			return setStatusAndStatusCode(responseData, method);
		}else {
			setStatusAsFailure(responseData);
			int httpStatus= !StringUtils.hasText(responseData.getStatusCode())?HttpStatus.CONFLICT.value():
				Integer.valueOf(responseData.getStatusCode());
			responseData.setStatusCode(String.valueOf(httpStatus));
			return new ResponseEntity<>(responseData,HttpStatus.resolve(httpStatus));
		}
	}

	private void setStatusAsFailure(Response responseData) {
		String status= responseData.getStatus();
		if(status == null || status.isEmpty()){
			responseData.setStatus(CommonConstants.STATUS_FAILURE);
		}
		
	}

	protected ResponseEntity<? extends Response> setStatusAndStatusCode(Response responseData, String method) {
		if (method == null || HTTP_METHODS.contains(method)) {
			responseData.setStatus(CommonConstants.STATUS_SUCCESS);
			responseData.setStatusCode(String.valueOf(HttpStatus.OK.value()));
			return new ResponseEntity<>(responseData, HttpStatus.OK);
		} else if (CommonConstants.POST.equals(method)) {
			responseData.setStatus(CommonConstants.STATUS_SUCCESS);
			responseData.setStatusCode(String.valueOf(HttpStatus.CREATED.value()));
			return new ResponseEntity<>(responseData, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(responseData, HttpStatus.NOT_FOUND);
	}
	
	protected void throwMyLoanAggregatorException(RequestHeader requestHeaders)throws MyLoanAggregatorException {
		if(requestHeaders!=null && requestHeaders.getAppId() != null) {
			throw new MyLoanAggregatorException(CommonConstants.CUSTOM_ERROR_CODE, getCustomeFailureResponse(requestHeaders.getAppId()));
		}else {
			throw new MyLoanAggregatorException(CommonConstants.CUSTOM_ERROR_CODE, CommonConstants.NULL);
		}
	}

	private String getCustomeFailureResponse(String appId) {
		return appId!=null?MessageUtil.getMessage(appId.toLowerCase()+"."+CommonConstants.SERVICE_TECHNICAL_ERROR_UNABLE_TO_PROCESS)
				:CommonConstants.SERVICE_TECHNICAL_ERROR_UNABLE_TO_PROCESS;
	}

}
