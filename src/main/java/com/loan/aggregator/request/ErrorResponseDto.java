package com.loan.aggregator.request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class ErrorResponseDto {
	private String apiPath;
	private String errorCode;
	private String errorMessage;
	private LocalDateTime errorTime;

}
