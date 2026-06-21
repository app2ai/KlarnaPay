package com.rtech.klarnapay.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PaymentMethodCategoryDto(
    @SerializedName("identifier") val identifier: String,
    @SerializedName("name") val name: String,
    @SerializedName("asset_url") val assetUrl: String = ""
)

data class CreateSessionResponseDto(
    @SerializedName("session_id") val sessionId: String,
    @SerializedName("client_token") val clientToken: String,
    @SerializedName("payment_method_categories") val paymentMethodCategories: List<PaymentMethodCategoryDto>
)