package com.rtech.klarnapay.domain.model

/**
 * Represents the payload sent to the backend when creating a session or order.
 */

data class CreateSessionRequest(
    val purchaseCountry: String = "GB",
    val purchaseCurrency: String = "GBP",
    val locale: String = "en-GB",
    val orderAmountInCents: Long,
    val orderLines: List<OrderLine>
)

data class CreateOrderRequest(
    val authorizationToken: String,
    val purchaseCountry: String = "GB",
    val purchaseCurrency: String = "GBP",
    val locale: String = "en-GB",
    val orderAmountInCents: Long,
    val orderLines: List<OrderLine>
)

data class OrderLine(
    val name: String,
    val quantity: Int,
    val unitPriceInCents: Long,
    val totalAmountInCents: Long
)