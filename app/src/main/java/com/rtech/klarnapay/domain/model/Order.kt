package com.rtech.klarnapay.domain.model

/**
 * Confirmed order returned by our backend after a successful Klarna authorization.
 */
data class Order(
    val orderId: String,
    val redirectUrl: String,
    val status: String
)