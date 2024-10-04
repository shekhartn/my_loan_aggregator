package com.loan.aggregator.mapper;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import com.loan.aggregator.model.Consumer;
import com.loan.aggregator.model.ConsumerSessionInfo;
import com.loan.aggregator.request.ConsumerRequest;
import com.loan.aggregator.util.CommonConstants;
import com.loan.aggregator.util.DateUtility;

public class ConsumerMapper {
	
	public static Consumer mapToConsumer(ConsumerRequest consumerRequest) {
		Consumer consumer=new Consumer();
		if(consumerRequest== null) {
			return null;
		}
		consumer.setEmail(consumerRequest.getEmail());
		consumer.setEmailVerified(CommonConstants.BYTE_ZERO);
		consumer.setIsActive(CommonConstants.BYTE_ONE);
		consumer.setIsLoggedIn(CommonConstants.BYTE_ZERO);
		consumer.setLoginCount(new BigInteger(String.valueOf(0)));
		consumer.setCreatedDate(DateUtility.getCalendarInUTCZone().getTime());
		consumer.setModifiedDate(DateUtility.getCalendarInUTCZone().getTime());
		return consumer;
	}

}
