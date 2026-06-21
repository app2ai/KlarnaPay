package com.rtech.klarnapay.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateSessionRequestDto(
    @SerializedName("purchase_country") val purchaseCountry: String,
    @SerializedName("purchase_currency") val purchaseCurrency: String,
    @SerializedName("locale") val locale: String,
    @SerializedName("order_amount") val orderAmount: Long,
    @SerializedName("order_lines") val orderLines: List<OrderLineDto>
)
