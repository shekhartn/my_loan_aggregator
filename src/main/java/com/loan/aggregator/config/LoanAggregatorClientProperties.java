package com.loan.aggregator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="loan.aggregator.client")
public class LoanAggregatorClientProperties {

	private String registerCustomer;

	private String updateCustomer;

	private String fetchCustomer;

	/**
	 * @return the registerCustomer
	 */
	public String getRegisterCustomer() {
		return registerCustomer;
	}

	/**
	 * @param registerCustomer the registerCustomer to set
	 */
	public void setRegisterCustomer(String registerCustomer) {
		this.registerCustomer = registerCustomer;
	}

	/**
	 * @return the updateCustomer
	 */
	public String getUpdateCustomer() {
		return updateCustomer;
	}

	/**
	 * @param updateCustomer the updateCustomer to set
	 */
	public void setUpdateCustomer(String updateCustomer) {
		this.updateCustomer = updateCustomer;
	}

	/**
	 * @return the fetchCustomer
	 */
	public String getFetchCustomer() {
		return fetchCustomer;
	}

	/**
	 * @param fetchCustomer the fetchCustomer to set
	 */
	public void setFetchCustomer(String fetchCustomer) {
		this.fetchCustomer = fetchCustomer;
	}
	
	
	
}
