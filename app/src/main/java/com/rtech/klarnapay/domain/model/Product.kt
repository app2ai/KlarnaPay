package com.rtech.klarnapay.domain.model

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