package com.loan.aggregator.cron;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class LoanAggregatorJob extends QuartzJobBean{
	
	private LoanAggregatorJobManager loanAggregatorTask;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		loanAggregatorTask.loanAggregatorRunner();
	}

	public void setLoanAggregatorTask(LoanAggregatorJobManager loanAggregatorTask) {
		this.loanAggregatorTask = loanAggregatorTask;
	}
	
	

}
