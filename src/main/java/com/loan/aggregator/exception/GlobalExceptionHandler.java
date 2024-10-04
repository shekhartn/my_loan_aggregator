package com.loan.aggregator.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.loan.aggregator.request.ErrorResponseDto;
import com.loan.aggregator.util.CommonConstants;
import com.loan.aggregator.util.MessageUtil;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(MyLoanAggregatorException.class)
	public ResponseEntity<ErrorResponseDto> loanExceptionHandler(MyLoanAggregatorException aggregatorException,
			WebRequest request) {
		ErrorResponseDto errorResponse = ErrorResponseDto.builder().apiPath(request.getDescription(false))
				.errorCode(aggregatorException.getStatusCode())
				.errorMessage(aggregatorException.getMessage())
				.errorTime(LocalDateTime.now())
				.build();
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

}
