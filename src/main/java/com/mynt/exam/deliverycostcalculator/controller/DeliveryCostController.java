package com.mynt.exam.deliverycostcalculator.controller;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mynt.exam.deliverycostcalculator.model.CalculationResponse;
import com.mynt.exam.deliverycostcalculator.model.ParcelRequest;
import com.mynt.exam.deliverycostcalculator.service.intf.DeliveryCostCalculatorService;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;

/**
 * Rest controller class for Delivery Cost calculator
 */
@Api
@RestController
@RequiredArgsConstructor
public class DeliveryCostController {

	private final DeliveryCostCalculatorService costCalculatorService;

	/**
	 * Calculate delivery cost
	 * 
	 * @param request parcel dimensions and promo code (if any)
	 * @return total calculation for delivery of parcel
	 */
	@PostMapping(value = "/calculate", consumes = MediaType.APPLICATION_JSON)
	public @ResponseBody ResponseEntity<CalculationResponse> calculateDeliveryCost(
			@Valid @RequestBody ParcelRequest request) {
		return ResponseEntity.ok(costCalculatorService.calculateCost(request));
	}
}
