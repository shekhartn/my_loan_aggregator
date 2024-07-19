/**
 * 
 */
package com.loan.aggregator.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsumerRequest {
	
	private String firstName;
	private String lastName;
	private String email;
	private String appId;
	private String phoneNumber;
	private String pwd;
	private String password;
	
	
	
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}



	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}



	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}



	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}



	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}



	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}



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






	public String getPhoneNumber() {
		return phoneNumber;
	}



	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	



	public String getPwd() {
		return pwd;
	}



	public void setPwd(String pwd) {
		this.pwd = pwd;
	}



	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}



	public String toString() {
		StringBuilder consumerRequestBuilder=new StringBuilder();
		consumerRequestBuilder.append("ConsumerRegistrationRequest [firstName=");
		consumerRequestBuilder.append(firstName);
		consumerRequestBuilder.append(", lastName=");
		consumerRequestBuilder.append(lastName);
		consumerRequestBuilder.append(", Email=");
		consumerRequestBuilder.append(email);
		consumerRequestBuilder.append(", AppId=");
		consumerRequestBuilder.append(appId);
		consumerRequestBuilder.append(", PhoneNumer=");
		consumerRequestBuilder.append(phoneNumber);
		consumerRequestBuilder.append("]");
		return consumerRequestBuilder.toString();
	}
	

}
