package com.loan.aggregator.filters;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.loan.aggregator.util.CommonConstants;
import com.loan.aggregator.util.LogUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class LoggingFilter extends OncePerRequestFilter {

	  final static String AMAZON_TRACE_ID = "X-Amzn-Trace-Id";
		private static final String APP_ID_HEADER = "X-App-Id";
		public static final String APP_ID = "appId";


	    @Override
	    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
	        //final Optional<String> traceId = LogUtil.getTraceId(httpServletRequest.getHeader(AMAZON_TRACE_ID));
	        final Optional<String> consumerId =LogUtil.getConsumerId(httpServletRequest);
	        final Optional<String> appId =LogUtil.getAppId(httpServletRequest.getHeader(APP_ID_HEADER));
	        appId.ifPresent(value ->  MDC.put(APP_ID, value));
	        consumerId.ifPresent(value ->  MDC.put(CommonConstants.CONSUMER_ID, value));
	       // traceId.ifPresent(value ->  MDC.put(TRACE_ID, value));
	        filterChain.doFilter(httpServletRequest, httpServletResponse);
	       // traceId.ifPresent(value -> MDC.remove(TRACE_ID));
	        appId.ifPresent(value ->  MDC.remove(APP_ID));
	        consumerId.ifPresent(value ->  MDC.remove(CommonConstants.CONSUMER_ID));
}
}
