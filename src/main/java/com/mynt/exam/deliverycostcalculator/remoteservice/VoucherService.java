package com.mynt.exam.deliverycostcalculator.remoteservice;

import static com.mynt.exam.deliverycostcalculator.util.ApplicationConstants.API_KEY_REQUEST_PARAM;
import static com.mynt.exam.deliverycostcalculator.util.ApplicationConstants.PROMO_CODE_REQUEST_PARAM;
import static com.mynt.exam.deliverycostcalculator.util.ApplicationConstants.VOUCHER_SERVICE_URL;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * Remote service class that calls on Voucher Service
 *
 */
@Slf4j
@Service
public class VoucherService {

	/*
	 * Get voucher discount percentage based on promo code
	 * 
	 * @param promoCode promo code from request
	 * 
	 * @return response body containing discount percentage
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String, Object> getVoucherDiscountFromPromoCode(String promoCode) {
		if (StringUtils.hasText(promoCode)) {
			log.info("Remote service call to voucher service initiated. Promo code: [{}]", promoCode);

			RestTemplate restTemplate = new RestTemplate();

			HttpHeaders headers = new HttpHeaders();
			HttpEntity<HttpHeaders> httpEntity = new HttpEntity<>(headers);

			Map<String, String> uriVariables = setUriVariables(promoCode);

			try {
				ResponseEntity<Map> response = restTemplate.exchange(VOUCHER_SERVICE_URL, HttpMethod.GET, httpEntity,
						Map.class, uriVariables);

				if (response.getStatusCode() == HttpStatus.OK) {
					log.info("Voucher promo code [{}] is valid!", promoCode);
					return response.getBody();
				}
			} catch (HttpClientErrorException e) {
				logErrorScenario(promoCode, e);
			}
		}
		return Collections.emptyMap();
	}

	/**
	 * Set uri parameters for voucher service call
	 * 
	 * @param promoCode promo code from request
	 * @return pre-set request parameters
	 */
	private Map<String, String> setUriVariables(String promoCode) {
		Map<String, String> uriVariables = new HashMap<>();

		uriVariables.put(PROMO_CODE_REQUEST_PARAM, promoCode.toUpperCase());
		// Hardcoded apikey
		uriVariables.put(API_KEY_REQUEST_PARAM, "apikey");
		return uriVariables;
	}

	/**
	 * In the case VoucherService returns a server side error or a BAD_REQUEST
	 * (400), it should not stop customer from getting a delivery calculation
	 * 
	 * @param e HttpClientErrorException
	 */
	private void logErrorScenario(String promoCode, HttpClientErrorException e) {
		if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
			log.info("Voucher code [{}] invalid!", promoCode);
		} else {
			log.error("Exception encountered during voucher service call: {}", e.getMessage());
		}
	}

}
