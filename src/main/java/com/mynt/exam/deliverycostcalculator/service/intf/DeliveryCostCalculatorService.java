package com.mynt.exam.deliverycostcalculator.service.intf;

import com.mynt.exam.deliverycostcalculator.model.CalculationResponse;
import com.mynt.exam.deliverycostcalculator.model.ParcelRequest;

// Interface class for delivery cost service
public interface DeliveryCostCalculatorService {

	/**
	 * Calculate cost of delivery
	 * 
	 * @param request request object containing calculation basis
	 * @return total cost
	 */
	CalculationResponse calculateCost(ParcelRequest request);
}
