package com.mynt.exam.deliverycostcalculator.service.impl;

import static com.mynt.exam.deliverycostcalculator.util.ApplicationConstants.VOUCHER_RESPONSE_DISCOUNT;
import static com.mynt.exam.deliverycostcalculator.util.ApplicationConstants.VOUCHER_RESPONSE_EXPIRY;
import static com.mynt.exam.deliverycostcalculator.util.ParcelDeliveryCalculationHelper.calculateHeavyParcel;
import static com.mynt.exam.deliverycostcalculator.util.ParcelDeliveryCalculationHelper.calculateLargeParcel;
import static com.mynt.exam.deliverycostcalculator.util.ParcelDeliveryCalculationHelper.calculateMediumParcel;
import static com.mynt.exam.deliverycostcalculator.util.ParcelDeliveryCalculationHelper.calculateParcel;
import static com.mynt.exam.deliverycostcalculator.util.ParcelDeliveryCalculationHelper.calculateSmallParcel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.mynt.exam.deliverycostcalculator.exception.DeliveryCostCalculationException;
import com.mynt.exam.deliverycostcalculator.model.CalculationResponse;
import com.mynt.exam.deliverycostcalculator.model.ParcelRequest;
import com.mynt.exam.deliverycostcalculator.remoteservice.VoucherService;
import com.mynt.exam.deliverycostcalculator.service.intf.DeliveryCostCalculatorService;
import com.mynt.exam.deliverycostcalculator.util.RulePriority;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Service implementation of delivery cost calculator for parcels
@Slf4j
@Service
@RequiredArgsConstructor
public class ParcelDeliveryCostCalculatorService implements DeliveryCostCalculatorService {

	private final VoucherService voucherService;

	private static final String CALCULATION_LOG = "Calculating using {} calculation";
	private static final String CALCULATE_ACTION = "CALCULATE";

	@Override
	public CalculationResponse calculateCost(ParcelRequest request) {
		log.info("Calculating parcel cost");

		// Step 1: pre-check rejected scenarios
		handleRejectedScenarios(request);

		// Step 2.a: check promo code validity
		Map<String, Object> voucherResponse = voucherService.getVoucherDiscountFromPromoCode(request.getPromoCode());

		// Step 2.b: assign voucher promo details(if valid)
		BigDecimal discountPct = getDiscountPercentage(voucherResponse);
		
		// Expiry date consideration commented out to consider discount in calculation
		// See Considerations/Assumptions #3
		// LocalDate expiryDate = getExpiryDate(voucherResponse);
		// Confirm if promo is still applicable
		// discountPct = checkPromoExpiry(discountPct, expiryDate);

		// Step 3: Calculate response properties
		BigDecimal totalVolume = getParcelTotalVolume(request);
		BigDecimal deliveryCost = getParcelDeliveryCost(request, totalVolume, discountPct);

		return CalculationResponse.builder().deliveryCost(deliveryCost)
				.totalWeight(request.getWeight().setScale(2, RoundingMode.HALF_UP))
				.totalVolume(totalVolume.setScale(2, RoundingMode.HALF_UP)).build();
	}

	/**
	 * Method handler for rejected scenarios. Expected to throw
	 * DeliveryCostCalculationException
	 * 
	 * @param request parcel request object
	 */
	private void handleRejectedScenarios(ParcelRequest request) {
		log.debug("Dimensions: L [{}] - W [{}] - H [{}] - Weight [{}]", request.getLength(), request.getWidth(),
				request.getHeight(), request.getWeight());
		// If any parcel dimensions (weight/length/width/height) is 0
		if (BigDecimal.ZERO.compareTo(request.getWeight()) >= 0 || BigDecimal.ZERO.compareTo(request.getLength()) >= 0
				|| BigDecimal.ZERO.compareTo(request.getWidth()) >= 0
				|| BigDecimal.ZERO.compareTo(request.getHeight()) >= 0) {
			throw new DeliveryCostCalculationException("Invalid dimension. Reject cost calculation for parcel");
		}
	}

	/**
	 * Get discount percentage from voucher promo
	 * 
	 * @param voucherResponse response from voucher service
	 * @return discount percentage multiplier
	 */
	private BigDecimal getDiscountPercentage(Map<String, Object> voucherResponse) {
		if (!ObjectUtils.isEmpty(voucherResponse)) {
			return BigDecimal.valueOf((Double) voucherResponse.get(VOUCHER_RESPONSE_DISCOUNT))
					.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
		}
		// Return 0 if no promo
		return BigDecimal.ZERO;
	}

	/**
	 * Get expiry date from voucher promo
	 * 
	 * @param voucherResponse response from voucher service
	 * @return expiry date of promo
	 */
	private LocalDate getExpiryDate(Map<String, Object> voucherResponse) {
		if (!ObjectUtils.isEmpty(voucherResponse)) {
			return LocalDate.parse((String) voucherResponse.get(VOUCHER_RESPONSE_EXPIRY));
		}
		// Return null if no promo
		return null;
	}

	/**
	 * Check if discount percentage will still apply based on expiry date
	 * 
	 * @param discountPct discount multiplier
	 * @param expiryDate voucher expiry date
	 * @return if expired, return 0; else, return discountPct
	 */
	private BigDecimal checkPromoExpiry(BigDecimal discountPct, LocalDate expiryDate) {
		if (BigDecimal.ZERO.compareTo(discountPct) <= 0) {
			LocalDate today = LocalDate.now();
			if (today.isBefore(expiryDate)) {
				log.info("Promo discount applied");
				return discountPct;
			}
		} 
		log.info("Promo discount already expired: Expiry date: {}", expiryDate.toString());
		return BigDecimal.ZERO;
	}

	/**
	 * Calculate total volume of parcel
	 * 
	 * @param request contains length, width, and height dimension
	 * @return total volume of parcel
	 */
	private BigDecimal getParcelTotalVolume(ParcelRequest request) {
		return request.getLength().multiply(request.getWidth()).multiply(request.getHeight()).setScale(4,
				RoundingMode.HALF_UP);
	}

	/**
	 * Calculate delivery cost of parcel
	 * 
	 * @param request        contains properties to be considered in cost
	 *                       calculation
	 * @param totalVolume    calculated total volume of the parcel
	 * @param discMultiplier discount to be multiplied if available
	 * @return total delivery cost
	 */
	private BigDecimal getParcelDeliveryCost(ParcelRequest request, BigDecimal totalVolume, BigDecimal discMultiplier) {
		BigDecimal deliveryCost = BigDecimal.ZERO;
		for (Map.Entry<Integer, RulePriority> ruleEntry : RulePriority.getRules().entrySet()) {
			deliveryCost = calculateDeliveryCostBasedOnPriority(request.getWeight(), totalVolume, ruleEntry.getValue());
			// Break out of loop once a delivery cost has been calculated
			if (BigDecimal.ZERO.compareTo(deliveryCost) < 0)
				break;
		}

		// If delivery cost is still uncalculated at this point, use highest rule
		// priority calculation
		if (BigDecimal.ZERO.compareTo(deliveryCost) == 0)
			deliveryCost = calculateDeliveryCostUsingHighestRulePriorityCalculation(request.getWeight(), totalVolume);

		return deliveryCost.subtract(deliveryCost.multiply(discMultiplier)).setScale(2, RoundingMode.HALF_UP);
	}

	/**
	 * Calculate delivery cost based on rule priority
	 * 
	 * @param weight parcel weight
	 * @param volume parcel volume
	 * @param rule   calculation rule to be checked
	 * @return total delivery cost
	 */
	private BigDecimal calculateDeliveryCostBasedOnPriority(BigDecimal weight, BigDecimal volume, RulePriority rule) {
		if (RulePriority.REJECT == rule && BigDecimal.valueOf(50).compareTo(weight) < 0) {
			// If request.weight > 50kg, reject to deliver/calculate
			log.info("Weight exceeds 50kg. Rejecting calculation");
			throw new DeliveryCostCalculationException("Weight exceeds 50kg. Reject cost calculation for parcel");
		} else {
			if (RulePriority.HEAVY_PARCEL == rule && BigDecimal.TEN.compareTo(weight) < 0) {
				log.info(CALCULATION_LOG, rule.getRemark());
				return calculateHeavyParcel(weight);
			} else if (RulePriority.SMALL_PARCEL == rule && BigDecimal.valueOf(1500).compareTo(volume) > 0) {
				log.info(CALCULATION_LOG, rule.getRemark());
				return calculateSmallParcel(volume);
			} else if (RulePriority.MEDIUM_PARCEL == rule && BigDecimal.valueOf(2500).compareTo(volume) > 0) {
				log.info(CALCULATION_LOG, rule.getRemark());
				return calculateMediumParcel(volume);
			} else if (RulePriority.LARGE_PARCEL == rule && BigDecimal.valueOf(2500).compareTo(volume) <= 0) {
				log.info(CALCULATION_LOG, rule.getRemark());
				return calculateLargeParcel(volume);
			}
		}
		return BigDecimal.ZERO;
	}

	/**
	 * Calculate delivery cost based using highest rule priority calculation
	 * 
	 * @param weight
	 * @param volume
	 * @return total delivery cost
	 */
	private BigDecimal calculateDeliveryCostUsingHighestRulePriorityCalculation(BigDecimal weight, BigDecimal volume) {
		for (Map.Entry<Integer, RulePriority> ruleEntry : RulePriority.getRules().entrySet()) {
			if (ruleEntry.getValue().getAction().equalsIgnoreCase(CALCULATE_ACTION)) {
				log.info(CALCULATION_LOG, ruleEntry.getValue().getRemark());
				return calculateParcel(weight, volume, ruleEntry.getValue());
			}
		}
		// If no calculation available, throw RuntimeException
		// But it's going to be a really edge case it reaches this point
		throw new DeliveryCostCalculationException("No calculation available for parcel.");
	}

}
