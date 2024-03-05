/**
 * 
 */
package com.loan.aggregator.response;

/**
 * 
 */
public class ConsumerRegistrationResponse extends Response {
	
	public ConsumerRegistrationResponseData getResponseData() {
		return (ConsumerRegistrationResponseData)responseData;
	}
	
	
	public void setResponseData(ConsumerRegistrationResponseData responseData) {
		this.responseData=responseData;
	}

}
