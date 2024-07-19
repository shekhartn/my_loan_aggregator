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
public class LoginRequest {
	
	private String email;
	private String password;
	
	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}

	


	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public String toString() {
		StringBuilder consumerRequestBuilder=new StringBuilder();
		consumerRequestBuilder.append("ConsumerLoginRequest [Email=");
		consumerRequestBuilder.append(email);
		consumerRequestBuilder.append(", Password=");
		consumerRequestBuilder.append(password);
		consumerRequestBuilder.append("]");
		return consumerRequestBuilder.toString();
	}
	

}
