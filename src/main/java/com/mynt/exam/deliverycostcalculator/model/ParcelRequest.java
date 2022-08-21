package com.mynt.exam.deliverycostcalculator.model;

import java.math.BigDecimal;

import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

// Request class for parcel object
@Data
@Builder
@ApiModel
public class ParcelRequest {

	// Length of the parcel in cm
	@JsonProperty(value = "length")
	@ApiModelProperty(value = "length")
	@Min(value = 1, message = "Length cannot be less than 1cm")
	private BigDecimal length;

	// Width of the parcel in cm
	@JsonProperty(value = "width")
	@ApiModelProperty(value = "width")
	@Min(value = 1, message = "Width cannot be less than 1cm")
	private BigDecimal width;

	// Height of the parcel in cm
	@JsonProperty(value = "height")
	@ApiModelProperty(value = "height")
	@Min(value = 1, message = "Height cannot be less than 1cm")
	private BigDecimal height;

	// Weight of the parcel in kg
	@JsonProperty(value = "weight")
	@ApiModelProperty(value = "weight")
	@Min(value = 1, message = "Weight cannot be less than 1kg")
	private BigDecimal weight;

	// Promo code used during request
	@JsonProperty(value = "promoCode")
	@ApiModelProperty(value = "promoCode")
	private String promoCode;

}
