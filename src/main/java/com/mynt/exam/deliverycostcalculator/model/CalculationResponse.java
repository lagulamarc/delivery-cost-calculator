package com.mynt.exam.deliverycostcalculator.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

// Response class for parcel calculation result
@Data
@Builder
@ApiModel
public class CalculationResponse {

	// Total delivery cost calculated
	@JsonProperty(value = "deliveryCost")
	@ApiModelProperty(value = "deliveryCost")
	private BigDecimal deliveryCost;

	// Total weight 
	@JsonProperty(value = "totalWeight")
	@ApiModelProperty(value = "totalWeight")
	private BigDecimal totalWeight;

	// Total volume
	@JsonProperty(value = "totalVolume")
	@ApiModelProperty(value = "totalVolume")
	private BigDecimal totalVolume;

}
