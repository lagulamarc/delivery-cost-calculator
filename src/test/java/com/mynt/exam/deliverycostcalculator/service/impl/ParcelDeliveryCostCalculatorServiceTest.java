package com.mynt.exam.deliverycostcalculator.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.mynt.exam.deliverycostcalculator.exception.DeliveryCostCalculationException;
import com.mynt.exam.deliverycostcalculator.model.CalculationResponse;
import com.mynt.exam.deliverycostcalculator.model.ParcelRequest;
import com.mynt.exam.deliverycostcalculator.remoteservice.VoucherService;

@SpringBootTest
@RunWith(SpringRunner.class)
class ParcelDeliveryCostCalculatorServiceTest {

	@InjectMocks
	private ParcelDeliveryCostCalculatorService parcelDeliveryCostCalculatorService;

	@Mock
	private VoucherService voucherService;

	private final Map<String, Object> noPromoResponse = Collections.emptyMap();
	private final Map<String, Object> withPromoResponse = new HashMap<>();

	private static final String PROMO_CODE = "test";

	@BeforeEach
	void init() {
		// Initialize promo response
		withPromoResponse.put("code", "TEST");
		withPromoResponse.put("discount", 11.22);
		withPromoResponse.put("expiry", "2050-08-18");
	}

	/**
	 * Test cost calculation for Heavy Parcel (no promo)
	 */
	@Test
	void testCalculateCostHeavyParcel() {
		ParcelRequest request = ParcelRequest.builder().length(BigDecimal.ONE).width(BigDecimal.ONE)
				.height(BigDecimal.ONE).weight(BigDecimal.valueOf(15)).build();
		Mockito.when(voucherService.getVoucherDiscountFromPromoCode("abc")).thenReturn(noPromoResponse);

		CalculationResponse response = parcelDeliveryCostCalculatorService.calculateCost(request);
		assertNotNull(response);
		assertEquals(BigDecimal.ONE.setScale(2), response.getTotalVolume());
		assertEquals(BigDecimal.valueOf(15).setScale(2), response.getTotalWeight());
		// HEAVY calculation = weight * 20 = 15 * 20
		assertEquals(BigDecimal.valueOf(300).setScale(2), response.getDeliveryCost());
	}

	/**
	 * Test cost calculation for Small Parcel (no promo)
	 */
	@Test
	void testCalculateCostSmallParcel() {
		ParcelRequest request = ParcelRequest.builder().length(BigDecimal.valueOf(30)).width(BigDecimal.valueOf(20))
				.height(BigDecimal.valueOf(2)).weight(BigDecimal.valueOf(3)).build();
		Mockito.when(voucherService.getVoucherDiscountFromPromoCode("abc")).thenReturn(noPromoResponse);

		CalculationResponse response = parcelDeliveryCostCalculatorService.calculateCost(request);
		assertNotNull(response);
		// 30 * 20 * 2 = 1200cm3
		assertEquals(BigDecimal.valueOf(1200).setScale(2), response.getTotalVolume());
		assertEquals(BigDecimal.valueOf(3).setScale(2), response.getTotalWeight());
		// SMALL calculation = volume * 0.03 = 1200 * 0.03
		assertEquals(BigDecimal.valueOf(36).setScale(2), response.getDeliveryCost());
	}

	/**
	 * Test cost calculation for Medium Parcel (no promo)
	 */
	@Test
	void testCalculateCostMediumParcel() {
		ParcelRequest request = ParcelRequest.builder().length(BigDecimal.TEN).width(BigDecimal.valueOf(20))
				.height(BigDecimal.TEN).weight(BigDecimal.valueOf(8)).build();
		Mockito.when(voucherService.getVoucherDiscountFromPromoCode("abc")).thenReturn(noPromoResponse);

		CalculationResponse response = parcelDeliveryCostCalculatorService.calculateCost(request);
		assertNotNull(response);
		// 10 * 20 * 10 = 2000cm3
		assertEquals(BigDecimal.valueOf(2000).setScale(2), response.getTotalVolume());
		assertEquals(BigDecimal.valueOf(8).setScale(2), response.getTotalWeight());
		// MEDIUM calculation = volume * 0.04 = 2000 * 0.04
		assertEquals(BigDecimal.valueOf(80).setScale(2), response.getDeliveryCost());
	}

	/**
	 * Test cost calculation for Large Parcel (no promo)
	 */
	@Test
	void testCalculateCostLargeParcel() {
		ParcelRequest request = ParcelRequest.builder().length(BigDecimal.TEN).width(BigDecimal.valueOf(20))
				.height(BigDecimal.valueOf(30)).weight(BigDecimal.valueOf(10)).build();
		Mockito.when(voucherService.getVoucherDiscountFromPromoCode("abc")).thenReturn(noPromoResponse);

		CalculationResponse response = parcelDeliveryCostCalculatorService.calculateCost(request);
		assertNotNull(response);
		// 10 * 20 * 30 = 6000cm3
		assertEquals(BigDecimal.valueOf(6000).setScale(2), response.getTotalVolume());
		assertEquals(BigDecimal.valueOf(10).setScale(2), response.getTotalWeight());
		// LARGE calculation = volume * 0.05 = 6000 * 0.05
		assertEquals(BigDecimal.valueOf(300).setScale(2), response.getDeliveryCost());
	}

	/**
	 * Test cost calculation for Heavy Parcel (with promo)
	 */
	@Test
	void testCalculateCostHeavyParcelWithPromo() {
		ParcelRequest request = ParcelRequest.builder().length(BigDecimal.ONE).width(BigDecimal.ONE)
				.height(BigDecimal.ONE).weight(BigDecimal.valueOf(15)).promoCode(PROMO_CODE).build();
		Mockito.when(voucherService.getVoucherDiscountFromPromoCode(PROMO_CODE)).thenReturn(withPromoResponse);

		CalculationResponse response = parcelDeliveryCostCalculatorService.calculateCost(request);
		assertNotNull(response);
		assertEquals(BigDecimal.ONE.setScale(2), response.getTotalVolume());
		assertEquals(BigDecimal.valueOf(15).setScale(2), response.getTotalWeight());
		// HEAVY calculation = weight * 20 = 15 * 20
		// With promo: 300 - (300 * 0.1122)
		assertEquals(BigDecimal.valueOf(266.34), response.getDeliveryCost());
	}

	/**
	 * Test cost calculation for Small Parcel (with promo)
	 */
	@Test
	void testCalculateCostSmallParcelWithPromo() {
		ParcelRequest request = ParcelRequest.builder().length(BigDecimal.valueOf(30)).width(BigDecimal.valueOf(20))
				.height(BigDecimal.valueOf(2)).weight(BigDecimal.valueOf(3)).promoCode(PROMO_CODE).build();
		Mockito.when(voucherService.getVoucherDiscountFromPromoCode(PROMO_CODE)).thenReturn(withPromoResponse);

		CalculationResponse response = parcelDeliveryCostCalculatorService.calculateCost(request);
		assertNotNull(response);
		// 30 * 20 * 2 = 1200cm3
		assertEquals(BigDecimal.valueOf(1200).setScale(2), response.getTotalVolume());
		assertEquals(BigDecimal.valueOf(3).setScale(2), response.getTotalWeight());
		// SMALL calculation = volume * 0.03 = 1200 * 0.03
		// With promo: 36 - (36 * 0.1122)
		assertEquals(BigDecimal.valueOf(31.96).setScale(2), response.getDeliveryCost());
	}

	/**
	 * Test cost calculation for Medium Parcel (with promo)
	 */
	@Test
	void testCalculateCostMediumParcelWithPromo() {
		ParcelRequest request = ParcelRequest.builder().length(BigDecimal.TEN).width(BigDecimal.valueOf(20))
				.height(BigDecimal.TEN).weight(BigDecimal.valueOf(8)).promoCode(PROMO_CODE).build();
		Mockito.when(voucherService.getVoucherDiscountFromPromoCode(PROMO_CODE)).thenReturn(withPromoResponse);

		CalculationResponse response = parcelDeliveryCostCalculatorService.calculateCost(request);
		assertNotNull(response);
		// 10 * 20 * 10 = 2000cm3
		assertEquals(BigDecimal.valueOf(2000).setScale(2), response.getTotalVolume());
		assertEquals(BigDecimal.valueOf(8).setScale(2), response.getTotalWeight());
		// MEDIUM calculation = volume * 0.04 = 2000 * 0.04
		// With promo: 80 - (80 * 0.1122)
		assertEquals(BigDecimal.valueOf(71.02).setScale(2), response.getDeliveryCost());
	}

	/**
	 * Test cost calculation for Large Parcel (with promo)
	 */
	@Test
	void testCalculateCostLargeParcelWithPromo() {
		ParcelRequest request = ParcelRequest.builder().length(BigDecimal.TEN).width(BigDecimal.valueOf(20))
				.height(BigDecimal.valueOf(30)).weight(BigDecimal.valueOf(10)).promoCode(PROMO_CODE).build();
		Mockito.when(voucherService.getVoucherDiscountFromPromoCode(PROMO_CODE)).thenReturn(withPromoResponse);

		CalculationResponse response = parcelDeliveryCostCalculatorService.calculateCost(request);
		assertNotNull(response);
		// 10 * 20 * 30 = 6000cm3
		assertEquals(BigDecimal.valueOf(6000).setScale(2), response.getTotalVolume());
		assertEquals(BigDecimal.valueOf(10).setScale(2), response.getTotalWeight());
		// LARGE calculation = volume * 0.05 = 6000 * 0.05
		// With promo: 300 - (300 * 0.1122)
		assertEquals(BigDecimal.valueOf(266.34).setScale(2), response.getDeliveryCost());
	}

	/**
	 * Test cost calculation with weight exceeding 50kg. Should
	 * throw @DeliveryCostCalculationException
	 */
	@Test
	void testCalculateCostWeightExceedLimit() {
		DeliveryCostCalculationException expected = assertThrows(DeliveryCostCalculationException.class, () -> {
			ParcelRequest request = ParcelRequest.builder().length(BigDecimal.TEN).width(BigDecimal.TEN)
					.height(BigDecimal.TEN).weight(BigDecimal.valueOf(100)).build();
			Mockito.when(voucherService.getVoucherDiscountFromPromoCode("abc")).thenReturn(noPromoResponse);

			parcelDeliveryCostCalculatorService.calculateCost(request);
		});
		assertEquals("Weight exceeds 50kg. Reject cost calculation for parcel", expected.getMessage());
	}

	/**
	 * Test cost calculation with invalid length. Should
	 * throw @DeliveryCostCalculationException
	 */
	@Test
	void testCalculateCostInvalidLength() {
		DeliveryCostCalculationException expected = assertThrows(DeliveryCostCalculationException.class, () -> {
			ParcelRequest request = ParcelRequest.builder().length(BigDecimal.ZERO).width(BigDecimal.TEN)
					.height(BigDecimal.TEN).weight(BigDecimal.valueOf(10)).build();
			Mockito.when(voucherService.getVoucherDiscountFromPromoCode("abc")).thenReturn(noPromoResponse);

			parcelDeliveryCostCalculatorService.calculateCost(request);
		});
		assertEquals("Invalid dimension. Reject cost calculation for parcel", expected.getMessage());
	}

	/**
	 * Test cost calculation with invalid width. Should
	 * throw @DeliveryCostCalculationException
	 */
	@Test
	void testCalculateCostInvalidWidth() {
		DeliveryCostCalculationException expected = assertThrows(DeliveryCostCalculationException.class, () -> {
			ParcelRequest request = ParcelRequest.builder().length(BigDecimal.TEN).width(BigDecimal.ZERO)
					.height(BigDecimal.TEN).weight(BigDecimal.valueOf(10)).build();
			Mockito.when(voucherService.getVoucherDiscountFromPromoCode("abc")).thenReturn(noPromoResponse);

			parcelDeliveryCostCalculatorService.calculateCost(request);
		});
		assertEquals("Invalid dimension. Reject cost calculation for parcel", expected.getMessage());
	}

	/**
	 * Test cost calculation with invalid height. Should
	 * throw @DeliveryCostCalculationException
	 */
	@Test
	void testCalculateCostInvalidHeight() {
		DeliveryCostCalculationException expected = assertThrows(DeliveryCostCalculationException.class, () -> {
			ParcelRequest request = ParcelRequest.builder().length(BigDecimal.TEN).width(BigDecimal.TEN)
					.height(BigDecimal.ZERO).weight(BigDecimal.valueOf(10)).build();
			Mockito.when(voucherService.getVoucherDiscountFromPromoCode("abc")).thenReturn(noPromoResponse);

			parcelDeliveryCostCalculatorService.calculateCost(request);
		});
		assertEquals("Invalid dimension. Reject cost calculation for parcel", expected.getMessage());
	}

	/**
	 * Test cost calculation with invalid weight. Should
	 * throw @DeliveryCostCalculationException
	 */
	@Test
	void testCalculateCostInvalidWeight() {
		DeliveryCostCalculationException expected = assertThrows(DeliveryCostCalculationException.class, () -> {
			ParcelRequest request = ParcelRequest.builder().length(BigDecimal.TEN).width(BigDecimal.TEN)
					.height(BigDecimal.TEN).weight(BigDecimal.ZERO).build();
			Mockito.when(voucherService.getVoucherDiscountFromPromoCode("abc")).thenReturn(noPromoResponse);

			parcelDeliveryCostCalculatorService.calculateCost(request);
		});
		assertEquals("Invalid dimension. Reject cost calculation for parcel", expected.getMessage());
	}
}
