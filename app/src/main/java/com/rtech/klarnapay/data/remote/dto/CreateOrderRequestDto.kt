package com.rtech.klarnapay.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateOrderRequestDto(
    @SerializedName("authorization_token") val authorizationToken: String,
    @SerializedName("purchase_country") val purchaseCountry: String,
    @SerializedName("purchase_currency") val purchaseCurrency: String,
    @SerializedName("locale") val locale: String,
    @SerializedName("order_amount") val orderAmount: Long,
    @SerializedName("order_lines") val orderLines: List<OrderLineDto>
)

data class OrderLineDto(
    @SerializedName("name") val name: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("unit_price") val unitPrice: Long,
    @SerializedName("total_amount") val totalAmount: Long
)
