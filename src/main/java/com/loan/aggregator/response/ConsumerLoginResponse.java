/**
 * 
 */
package com.loan.aggregator.response;

/**
 * 
 */
public class ConsumerLoginResponse extends Response {
	
	public ConsumerLoginResponseData getResponseData() {
		return (ConsumerLoginResponseData)responseData;
	}
	
	
	public void setResponseData(ConsumerLoginResponseData responseData) {
		this.responseData=responseData;
	}

}
