package com.loan.aggregator.cron;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class LoanAggregatorJobManager {
	
	public void loanAggregatorRunner() {
		System.out.println(LocalDateTime.now()+"  Loan Aggregator Runner Executed::");
	}

}
