package com.loan.aggregator.client;


import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.loan.aggregator.client.bo.Customer;

public abstract class CommonService {
	
	@Autowired
	@Qualifier("LoanAggregatorRestTemplate")
	protected RestTemplate restTemplate;

	public static HttpHeaders getHttpHeaders() {
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return requestHeaders;
    }
	

    protected <Q extends Map<String, String>> HttpHeaders applyHttpHeaders(Q headerMap) {
        final HttpHeaders httpHeaders = getHttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }
    protected <P, Q extends Map<String, String>, R> BiFunction<P, Q, R> callWithBody(Function<HttpEntity<P>,R> function){
		return (input,headerMap)->{
			final HttpHeaders headers=applyHttpHeaders(headerMap);
			final HttpEntity<P> httpEntity = new HttpEntity<>(input, headers);
			return call(function, httpEntity);
		};
    }
    
    protected <P extends Map<String, String>, R> Function<P, R> callWithoutBody(Function<HttpEntity<P>,R> function){
    	return (headerMap)->{
    		final HttpHeaders headers=applyHttpHeaders(headerMap);
    		final HttpEntity<P> httpEntity = new HttpEntity<>(headers);
    		return call(function, httpEntity);
    	};
    }
    
	protected <T> T sendTo(final String url, final HttpEntity<?> requestEntity, final Class<T> responseType,
			final HttpMethod httpMethod, Map<String, ?> urlVariables) {
		final ResponseEntity<T> responseEntity = this.getRestTemplate().exchange(cleaned(url), httpMethod,
				requestEntity, responseType, urlVariables);
		Optional<T> toReturn = Optional.ofNullable(responseEntity.getBody());
		Customer customer=(Customer)toReturn.get();
		if(responseType.equals(Void.class) ) {
			return null;
        }
		System.out.println("CUID::"+customer.getCuid());
		return toReturn.get();

	}
    
    protected String cleaned(final String toClean) {
        return StringEscapeUtils.escapeJava(toClean);
    }
    public RestTemplate getRestTemplate() {
		return this.restTemplate;
	}
    protected <P, R> R call(final Function<HttpEntity<P>, R> function, HttpEntity<P> body) {
        R t = null;
        try {
            t = function.apply(body);
        } catch (final HttpClientErrorException | HttpServerErrorException | ResourceAccessException e) {
            System.out.println("Client Exception");
        }
        return t;
    }
}
