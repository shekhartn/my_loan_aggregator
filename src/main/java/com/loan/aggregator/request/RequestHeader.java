/**
 * 
 */
package com.loan.aggregator.request;

import com.loan.aggregator.model.AppInfoLkp;
import com.loan.aggregator.model.ConsumerSessionInfo;

/**
 * 
 */
public class RequestHeader {

	private String appId;
	private String authToken;
	
	private AppInfoLkp appInfoLkp;
	
	private ConsumerSessionInfo consumerSessionInfo;
	/**
	 * @return the appId
	 */
	public String getAppId() {
		return appId;
	}
	/**
	 * @param appId the appId to set
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}
	/**
	 * @return the authToken
	 */
	public String getAuthToken() {
		return authToken;
	}
	/**
	 * @param authToken the authToken to set
	 */
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	/**
	 * @return the appInfoLkp
	 */
	public AppInfoLkp getAppInfoLkp() {
		return appInfoLkp;
	}
	/**
	 * @param appInfoLkp the appInfoLkp to set
	 */
	public void setAppInfoLkp(AppInfoLkp appInfoLkp) {
		this.appInfoLkp = appInfoLkp;
	}
	/**
	 * @return the consumerSessionInfo
	 */
	public ConsumerSessionInfo getConsumerSessionInfo() {
		return consumerSessionInfo;
	}
	/**
	 * @param consumerSessionInfo the consumerSessionInfo to set
	 */
	public void setConsumerSessionInfo(ConsumerSessionInfo consumerSessionInfo) {
		this.consumerSessionInfo = consumerSessionInfo;
	}
	
	
	
}
