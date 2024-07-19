package com.loan.aggregator.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
@Configuration
public class LoanAggregatorClientConfig {

    @Bean(name = "LoanAggregatorRestTemplate")
    RestTemplate getRestTemplate(final RestTemplateBuilder builder) {
        System.out.println("Initializing LoanAggregatorRestTemplate ...");
        final RestTemplate restTemplate = builder.build();
        return restTemplate;
    }

}
