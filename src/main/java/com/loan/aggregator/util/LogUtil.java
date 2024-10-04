package com.loan.aggregator.util;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;

public class LogUtil {

	public static Optional<String> getConsumerId(final HttpServletRequest httpServletRequest) {
		String consumerId = httpServletRequest.getParameter(CommonConstants.CONSUMER_ID);
		if (consumerId != null) {
			return Optional.of(consumerId);
		} else {
			consumerId = getConsumerIdFromURL(httpServletRequest.getRequestURL());
			if (consumerId != null) {
				return Optional.of(consumerId);
			}
			return Optional.empty();
		}

	}

	public static Optional<String> getAppId(final String input) {
		System.out.println("App ID:::"+input);
		return Optional.ofNullable(input);
	}

	private static String getConsumerIdFromURL(StringBuffer stringBuffer) {
		Matcher m = Pattern.compile("/consumers/(\\d+)").matcher(stringBuffer);
		if (m.find()) {
			return m.group(1);
		} else {
			return null;
		}

	}
}
