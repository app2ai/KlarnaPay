package com.rtech.klarnapay.domain
// ─────────────────────────────────────────────
// Product
// ─────────────────────────────────────────────

/**
 * Represents a single product shown on the catalogue screen.
 */
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val priceInCents: Long,           // Klarna uses minor units (cents / pence)
    val currency: String = "USD",
    val imageUrl: String,
    val category: String
) {
    /** Convenience: formatted price string, e.g. "$29.99" */
    val formattedPrice: String
        get() = "$${priceInCents / 100}.${(priceInCents % 100).toString().padStart(2, '0')}"
}

// ─────────────────────────────────────────────
// Klarna Payment Session
// ─────────────────────────────────────────────

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

// ─────────────────────────────────────────────
// Order
// ─────────────────────────────────────────────

/**
 * Confirmed order returned by our backend after a successful Klarna authorization.
 */
data class Order(
    val orderId: String,
    val redirectUrl: String,
    val status: String
)

// ─────────────────────────────────────────────
// Cart / Order request
// ─────────────────────────────────────────────

/**
 * Represents the payload sent to the backend when creating a session or order.
 */
data class OrderLine(
    val name: String,
    val quantity: Int,
    val unitPriceInCents: Long,
    val totalAmountInCents: Long
)

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