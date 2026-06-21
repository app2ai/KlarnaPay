package com.rtech.klarnapay.data.remote

import com.google.gson.annotations.SerializedName

// ── Request DTOs ──────────────────────────────

data class OrderLineDto(
    @SerializedName("name")              val name: String,
    @SerializedName("quantity")          val quantity: Int,
    @SerializedName("unit_price")        val unitPrice: Long,
    @SerializedName("total_amount")      val totalAmount: Long
)

data class CreateSessionRequestDto(
    @SerializedName("purchase_country")  val purchaseCountry: String,
    @SerializedName("purchase_currency") val purchaseCurrency: String,
    @SerializedName("locale")            val locale: String,
    @SerializedName("order_amount")      val orderAmount: Long,
    @SerializedName("order_lines")       val orderLines: List<OrderLineDto>
)

data class CreateOrderRequestDto(
    @SerializedName("authorization_token") val authorizationToken: String,
    @SerializedName("purchase_country")    val purchaseCountry: String,
    @SerializedName("purchase_currency")   val purchaseCurrency: String,
    @SerializedName("locale")             val locale: String,
    @SerializedName("order_amount")       val orderAmount: Long,
    @SerializedName("order_lines")        val orderLines: List<OrderLineDto>
)

// ── Response DTOs ─────────────────────────────

data class PaymentMethodCategoryDto(
    @SerializedName("identifier") val identifier: String,
    @SerializedName("name")       val name: String,
    @SerializedName("asset_url")  val assetUrl: String = ""
)

data class CreateSessionResponseDto(
    @SerializedName("session_id")               val sessionId: String,
    @SerializedName("client_token")             val clientToken: String,
    @SerializedName("payment_method_categories") val paymentMethodCategories: List<PaymentMethodCategoryDto>
)

data class CreateOrderResponseDto(
    @SerializedName("order_id")      val orderId: String,
    @SerializedName("redirect_url")  val redirectUrl: String,
    @SerializedName("status")        val status: String
)