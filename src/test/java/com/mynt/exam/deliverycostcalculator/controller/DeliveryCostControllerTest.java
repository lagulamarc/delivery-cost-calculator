package com.mynt.exam.deliverycostcalculator.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mynt.exam.deliverycostcalculator.model.CalculationResponse;
import com.mynt.exam.deliverycostcalculator.model.ParcelRequest;
import com.mynt.exam.deliverycostcalculator.service.intf.DeliveryCostCalculatorService;

@WebMvcTest(DeliveryCostController.class)
class DeliveryCostControllerTest {

	private static final String ENDPOINT_URI = "/calculate";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@MockBean
	private DeliveryCostCalculatorService deliveryCostCalculatorService;

	/**
	 * Test calculate delivery cost endpoint
	 * 
	 * @throws Exception
	 */
	@Test
	void testCalculateDeliveryCost() throws Exception {
		when(deliveryCostCalculatorService.calculateCost(any())).thenReturn(CalculationResponse.builder()
				.deliveryCost(BigDecimal.TEN).totalVolume(BigDecimal.valueOf(100)).totalWeight(BigDecimal.TEN).build());

		ParcelRequest request = ParcelRequest.builder().length(BigDecimal.TEN).width(BigDecimal.TEN)
				.height(BigDecimal.TEN).weight(BigDecimal.ONE).build();
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(ENDPOINT_URI)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.content(this.mapper.writeValueAsString(request));

		mockMvc.perform(mockRequest)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.deliveryCost", is(10)))
		.andExpect(jsonPath("$.totalVolume", is(100)))
		.andExpect(jsonPath("$.totalWeight", is(10)));
	}
	
	/**
	 * Test calculate delivery cost - Invalid length (0cm)
	 * 
	 * @throws Exception
	 */
	@Test
	void testCalculateDeliveryCostInvalidLength() throws Exception {
		ParcelRequest request = ParcelRequest.builder().length(BigDecimal.ZERO).width(BigDecimal.TEN)
				.height(BigDecimal.TEN).weight(BigDecimal.ONE).build();
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(ENDPOINT_URI)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.content(this.mapper.writeValueAsString(request));

		mockMvc.perform(mockRequest)
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message", is("Length cannot be less than 1cm")));
	}
	
	/**
	 * Test calculate delivery cost - Invalid width (0cm)
	 * 
	 * @throws Exception
	 */
	@Test
	void testCalculateDeliveryCostInvalidWidth() throws Exception {
		ParcelRequest request = ParcelRequest.builder().length(BigDecimal.TEN).width(BigDecimal.ZERO)
				.height(BigDecimal.TEN).weight(BigDecimal.ONE).build();
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(ENDPOINT_URI)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.content(this.mapper.writeValueAsString(request));

		mockMvc.perform(mockRequest)
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message", is("Width cannot be less than 1cm")));
	}

	
	/**
	 * Test calculate delivery cost - Invalid height (0cm)
	 * 
	 * @throws Exception
	 */
	@Test
	void testCalculateDeliveryCostInvalidHeight() throws Exception {
		ParcelRequest request = ParcelRequest.builder().length(BigDecimal.TEN).width(BigDecimal.TEN)
				.height(BigDecimal.ZERO).weight(BigDecimal.ONE).build();
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(ENDPOINT_URI)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.content(this.mapper.writeValueAsString(request));

		mockMvc.perform(mockRequest)
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message", is("Height cannot be less than 1cm")));
	}

	
	/**
	 * Test calculate delivery cost - Invalid weight (0kg)
	 * 
	 * @throws Exception
	 */
	@Test
	void testCalculateDeliveryCostInvalidWeight() throws Exception {
		ParcelRequest request = ParcelRequest.builder().length(BigDecimal.TEN).width(BigDecimal.TEN)
				.height(BigDecimal.TEN).weight(BigDecimal.ZERO).build();
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(ENDPOINT_URI)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.content(this.mapper.writeValueAsString(request));

		mockMvc.perform(mockRequest)
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message", is("Weight cannot be less than 1kg")));
	}

}
