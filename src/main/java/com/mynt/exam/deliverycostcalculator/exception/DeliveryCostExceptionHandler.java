package com.mynt.exam.deliverycostcalculator.exception;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Custom exception response handler for Delivery Cost Calculator
 */
@ControllerAdvice
public class DeliveryCostExceptionHandler extends ResponseEntityExceptionHandler {

	private static final String ERROR_MESSAGE_KEY = "message";

	// Response for handling custom DeliveryCostCalculationException
	@ExceptionHandler(value = DeliveryCostCalculationException.class)
	protected ResponseEntity<Object> handleDeliveryCostValidation(DeliveryCostCalculationException ex,
			WebRequest request) {
		return handleExceptionInternal(ex, mapErrorResponse(ex.getMessage()), new HttpHeaders(), HttpStatus.FORBIDDEN,
				request);
	}

	// Response for handling MethodArgumentNotValidException
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		return handleExceptionInternal(ex,
				mapErrorResponse(ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage()), new HttpHeaders(),
				HttpStatus.BAD_REQUEST, request);
	}

	// Method to map error body returned in response
	private Map<String, String> mapErrorResponse(String message) {
		return Collections.singletonMap(ERROR_MESSAGE_KEY, message);
	}
}
