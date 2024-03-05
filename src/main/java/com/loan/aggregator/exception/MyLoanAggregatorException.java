/**
 * 
 */
package com.loan.aggregator.exception;

import org.springframework.validation.Errors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MyLoanAggregatorException extends RuntimeException {

	private String statusCode;
	private Errors errors;

	public MyLoanAggregatorException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	private MyLoanAggregatorException(String message, Errors errors) {
		super(message);
		this.errors = errors;
	}

	public MyLoanAggregatorException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public MyLoanAggregatorException(String message) {
		super(message);
	}

	public MyLoanAggregatorException() {
		super();
	}

	public MyLoanAggregatorException(String statusCode,String message) {
		super(message);
		this.statusCode = statusCode;
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public MyLoanAggregatorException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the statusCode
	 */
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @return the errors
	 */
	public Errors getErrors() {
		return errors;
	}

	/**
	 * @param errors the errors to set
	 */
	public void setErrors(Errors errors) {
		this.errors = errors;
	}

}
