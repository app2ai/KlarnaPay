package com.rtech.klarnapay.domain.model

/**
 * A Klarna payment session returned by our backend.
 * The clientToken is passed directly to the Klarna Mobile SDK.
 */
data class PaymentSession(
    val sessionId: String,
    val clientToken: String,
    val paymentMethodCategories: List<PaymentMethodCategory>
)

/**
 * One Klarna payment category (pay_now / pay_later / pay_over_time).
 */
data class PaymentMethodCategory(
    val identifier: String,   // e.g. "pay_later"
    val name: String,         // e.g. "Pay in 30 days"
    val assetUrl: String      // Klarna-provided badge image URL
)