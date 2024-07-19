package com.loan.aggregator.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.loan.aggregator.client.bo.Customer;
import com.loan.aggregator.client.bo.CustomerRegistrationDetailsExposed;
import com.loan.aggregator.config.LoanAggregatorClientProperties;
import com.loan.aggregator.model.AppInfoLkp;

@Component
public class LoanAggregatorClientServiceImpl extends CommonService implements LoanAggregatorClientService{
	

	@Autowired
	private LoanAggregatorClientProperties properties;

	@Override
	public Customer registerCustomer(CustomerRegistrationDetailsExposed request, AppInfoLkp appInfo) {
		final String endpoint = this.buildUrl(this.properties.getRegisterCustomer(), appInfo.getEndPointUrl(),
				Optional.empty());
		BiFunction<Customer, Map<String,String>, Customer> callwithBody=this.callWithBody((body)->this.sendTo(endpoint,body,Customer.class,HttpMethod.POST,Collections.emptyMap()));
		return callwithBody.apply(request.getCustomer(), getCustomHttpHeaders(appInfo, Optional.of(request), Optional.empty(),
				Optional.empty(), endpoint, HttpMethod.POST));
	}
	
	@Override
	public Customer getCustomer(Map<String, String> urlVariables, AppInfoLkp appInfo) {
		System.out.println("Get Consumer details from client::");
		final String endPoint=this.buildUrl(this.properties.getFetchCustomer(), appInfo.getEndPointUrl(), Optional.of(urlVariables));
		Function<Map<String, String>, Customer> callwithBody=this.callWithoutBody((body)->this.sendTo(endPoint,body,Customer.class,HttpMethod.GET,Collections.emptyMap()));
		Customer customer= callwithBody.apply(getCustomHttpHeaders(appInfo, Optional.empty(), Optional.empty(),
				Optional.empty(), endPoint, HttpMethod.GET));
		System.out.println(customer);
		return customer;
	}
	
	private <T> Map<String, String> getCustomHttpHeaders(final AppInfoLkp appInfo, final Optional<T> request,
			final Optional<String> authorizationType, final Optional<String> clientToken, final String endpoint,
			final HttpMethod method) {
		final Map<String, String> headers = new HashMap<>();
		headers.put(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		return headers;
	}
	
	
	
	private String buildUrl(String relativePath, String baseUrl, Optional<Map<String, String>> urlVariables) {
		String endpoint = !Objects.isNull(baseUrl) ? baseUrl.concat(relativePath) : "";
		if (urlVariables.isPresent()) {
			StringSubstitutor strSubstitutor = new StringSubstitutor(urlVariables.get(), "{", "}");
			endpoint = strSubstitutor.replace(endpoint);
		}
		return endpoint;
	}

	
	 
}
