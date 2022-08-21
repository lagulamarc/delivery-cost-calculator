package com.mynt.exam.deliverycostcalculator.exception;

// Custom exception for delivery cost calculation
public class DeliveryCostCalculationException extends RuntimeException {

	private static final long serialVersionUID = -2103985884956495190L;

	public DeliveryCostCalculationException() {
		super();
	}

	public DeliveryCostCalculationException(String message, Throwable cause) {
		super(message, cause);
	}

	public DeliveryCostCalculationException(String message) {
		super(message);
	}

	public DeliveryCostCalculationException(Throwable cause) {
		super(cause);
	}
}
