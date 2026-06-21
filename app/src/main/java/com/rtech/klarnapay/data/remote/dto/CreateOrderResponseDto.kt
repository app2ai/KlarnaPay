package com.rtech.klarnapay.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateOrderResponseDto(
    @SerializedName("order_id") val orderId: String,
    @SerializedName("redirect_url") val redirectUrl: String,
    @SerializedName("status") val status: String
)