package com.loan.aggregator.client;

import java.util.Map;

import com.loan.aggregator.client.bo.Customer;
import com.loan.aggregator.client.bo.CustomerRegistrationDetailsExposed;
import com.loan.aggregator.model.AppInfoLkp;

public interface LoanAggregatorClientService {

	public Customer registerCustomer(final CustomerRegistrationDetailsExposed request,final AppInfoLkp appInfo);

	public Customer getCustomer(Map<String, String> createUrlAndPathVariableMapWithCustomerId, AppInfoLkp appInfoLkp);
}


