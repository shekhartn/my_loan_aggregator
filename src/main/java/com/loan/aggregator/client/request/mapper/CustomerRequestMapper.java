package com.loan.aggregator.client.request.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.loan.aggregator.client.bo.Customer;
import com.loan.aggregator.client.bo.CustomerRegistrationDetailsExposed;
import com.loan.aggregator.request.ConsumerRequest;

@Mapper
public interface CustomerRequestMapper {

	CustomerRequestMapper INSTANCE=Mappers.getMapper(CustomerRequestMapper.class);
	
	@Mapping(target="customer.email",expression = "java(getEmail(consumerRequest))")
	@Mapping(target="customer.phoneNumber",expression = "java(getPhoneNumber(consumerRequest))")
	public CustomerRegistrationDetailsExposed toCustomerRegistrationDetailsExposed(ConsumerRequest consumerRequest);
	
	@AfterMapping
	default CustomerRegistrationDetailsExposed getRegisterCustomerDetails(ConsumerRequest consumerRequest,@MappingTarget CustomerRegistrationDetailsExposed target) {
		Customer customer=target.getCustomer();
		customer.setAppId(consumerRequest.getAppId());
		customer.setFirstName(consumerRequest.getFirstName());
		customer.setLastName(consumerRequest.getLastName());
		customer.setPassword(consumerRequest.getPassword());
		target.setCustomer(customer);
		return target;
	};
	
	default <T> String getEmail(final T request){
		return (request instanceof ConsumerRequest)? ((ConsumerRequest)request).getEmail():null;
	}
	default <T> String getPhoneNumber(final T request){
		return (request instanceof ConsumerRequest)? ((ConsumerRequest)request).getPhoneNumber():null;
	}
}
