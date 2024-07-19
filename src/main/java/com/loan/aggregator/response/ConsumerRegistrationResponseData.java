/**
 * 
 */
package com.loan.aggregator.response;

/**
 * 
 */
public class ConsumerRegistrationResponseData {
	private String consumerId;

	private Boolean existingConsumer = Boolean.FALSE;

	private String fullName;
	
	private String phoneNumber;
	
	private String token;
	
	private String email;
	
	private String password;

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


	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
	

}
