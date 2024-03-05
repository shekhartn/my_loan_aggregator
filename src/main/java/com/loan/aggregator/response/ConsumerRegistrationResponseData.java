/**
 * 
 */
package com.loan.aggregator.response;

/**
 * 
 */
public class ConsumerRegistrationResponseData {
	private String consumerId;

	private String sessionToken;

	private Boolean existingConsumer = Boolean.FALSE;

	private String fullName;
	
	private String token;

	/**
	 * @return the consumerId
	 */
	public String getConsumerId() {
		return consumerId;
	}

	/**
	 * @param consumerId the consumerId to set
	 */
	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}

	/**
	 * @return the sessionToken
	 */
	public String getSessionToken() {
		return sessionToken;
	}

	/**
	 * @param sessionToken the sessionToken to set
	 */
	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	/**
	 * @return the existingConsumer
	 */
	public Boolean getExistingConsumer() {
		return existingConsumer;
	}

	/**
	 * @param existingConsumer the existingConsumer to set
	 */
	public void setExistingConsumer(Boolean existingConsumer) {
		this.existingConsumer = existingConsumer;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}
	

}
