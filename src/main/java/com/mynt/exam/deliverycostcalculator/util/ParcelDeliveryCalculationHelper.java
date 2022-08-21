package com.mynt.exam.deliverycostcalculator.util;

import java.math.BigDecimal;

// Helper class in calculating parcel delivery cost
public class ParcelDeliveryCalculationHelper {

	private ParcelDeliveryCalculationHelper() {
	}

	public static BigDecimal calculateHeavyParcel(BigDecimal weight) {
		return BigDecimal.valueOf(20).multiply(weight);
	}

	public static BigDecimal calculateSmallParcel(BigDecimal volume) {
		return BigDecimal.valueOf(0.03).multiply(volume);
	}

	public static BigDecimal calculateMediumParcel(BigDecimal volume) {
		return BigDecimal.valueOf(0.04).multiply(volume);
	}

	public static BigDecimal calculateLargeParcel(BigDecimal volume) {
		return BigDecimal.valueOf(0.05).multiply(volume);
	}

	public static BigDecimal calculateParcel(BigDecimal weight, BigDecimal volume, RulePriority rule) {
		switch (rule) {
		case SMALL_PARCEL:
			return calculateSmallParcel(volume);
		case MEDIUM_PARCEL:
			return calculateMediumParcel(volume);
		case LARGE_PARCEL:
			return calculateLargeParcel(volume);
		case HEAVY_PARCEL:
			return calculateHeavyParcel(weight);
		default:
			return BigDecimal.ZERO;
		}
	}

}
