package com.mynt.exam.deliverycostcalculator.remoteservice;

import static com.mynt.exam.deliverycostcalculator.util.ApplicationConstants.VOUCHER_RESPONSE_CODE;
import static com.mynt.exam.deliverycostcalculator.util.ApplicationConstants.VOUCHER_RESPONSE_DISCOUNT;
import static com.mynt.exam.deliverycostcalculator.util.ApplicationConstants.VOUCHER_SERVICE_URL;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@RunWith(SpringRunner.class)
class VoucherServiceTest {

	@InjectMocks
	private VoucherService voucherService;

	@Mock
	private RestTemplate restTemplate;

	/**
	 * Test remote voucher service call with promo code: MYNT
	 */
	@Test
	void testGetVoucherDiscountFromPromoCodeMynt() {
		Map<String, Object> promoResponse = voucherService.getVoucherDiscountFromPromoCode("mynt");
		assertNotNull(promoResponse);
		assertEquals("MYNT", promoResponse.get(VOUCHER_RESPONSE_CODE));
		assertEquals(12.25d, promoResponse.get(VOUCHER_RESPONSE_DISCOUNT));
	}

	/**
	 * Test remote voucher service call with promo code: GFI
	 */
	@Test
	void testGetVoucherDiscountFromPromoCodeGfi() {
		Map<String, Object> promoResponse = voucherService.getVoucherDiscountFromPromoCode("gfi");
		assertNotNull(promoResponse);
		assertEquals("GFI", promoResponse.get(VOUCHER_RESPONSE_CODE));
		assertEquals(7.5d, promoResponse.get(VOUCHER_RESPONSE_DISCOUNT));
	}

	/**
	 * Test remote voucher service call with an invalid promo code
	 */
	@Test
	void testGetVoucherDiscountFromPromoCodeInvalidCode() {
		Map<String, Object> promoResponse = voucherService.getVoucherDiscountFromPromoCode("abcdef");
		assertEquals(0, promoResponse.size());
	}

	/**
	 * Test remote voucher service call returns a server side error (500)
	 */
	@Test
	void testGetVoucherDiscountFromPromoCodeServerError() {
		ResponseEntity<Map<String, Object>> response = new ResponseEntity<Map<String, Object>>(
				HttpStatus.INTERNAL_SERVER_ERROR);
		Mockito.when(restTemplate.exchange(eq(VOUCHER_SERVICE_URL), eq(HttpMethod.POST),
				ArgumentMatchers.<HttpEntity<Map<String, Object>>>any(),
				ArgumentMatchers.<ParameterizedTypeReference<Map<String, Object>>>any())).thenReturn(response);
		assertNull(response.getBody());
	}
}
