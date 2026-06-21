package com.rtech.klarnapay.data.repo

import com.rtech.klarnapay.domain.Product
import com.rtech.klarnapay.domain.repo.ProductRepository


/**
 * Local (hardcoded) product catalogue for POC purposes.
 * In a production app this would call a remote API instead.
 *
 * Using Picsum Photos for placeholder images (no API key needed).
 */
class ProductRepositoryImpl : ProductRepository {

    private val catalogue: List<Product> = listOf(
        Product(
            id           = "1",
            name         = "Wireless Headphones",
            description  = "Premium noise-cancelling headphones with 30h battery life.",
            priceInCents = 14999,
            imageUrl     = "https://picsum.photos/seed/headphones/400/300",
            category     = "Electronics"
        ),
        Product(
            id           = "2",
            name         = "Mechanical Keyboard",
            description  = "Compact TKL layout with RGB backlighting & tactile switches.",
            priceInCents = 8999,
            imageUrl     = "https://picsum.photos/seed/keyboard/400/300",
            category     = "Electronics"
        ),
        Product(
            id           = "3",
            name         = "Running Shoes",
            description  = "Lightweight trainers with responsive foam cushioning.",
            priceInCents = 11999,
            imageUrl     = "https://picsum.photos/seed/shoes/400/300",
            category     = "Footwear"
        ),
        Product(
            id           = "4",
            name         = "Smart Watch",
            description  = "Health tracking, GPS, and 7-day battery in a slim design.",
            priceInCents = 24999,
            imageUrl     = "https://picsum.photos/seed/smartwatch/400/300",
            category     = "Electronics"
        ),
        Product(
            id           = "5",
            name         = "Yoga Mat",
            description  = "Extra-thick non-slip mat with alignment markings.",
            priceInCents = 3999,
            imageUrl     = "https://picsum.photos/seed/yogamat/400/300",
            category     = "Sports"
        ),
        Product(
            id           = "6",
            name         = "Coffee Maker",
            description  = "Programmable 12-cup drip coffee maker with thermal carafe.",
            priceInCents = 6999,
            imageUrl     = "https://picsum.photos/seed/coffee/400/300",
            category     = "Kitchen"
        ),
        Product(
            id           = "7",
            name         = "Leather Backpack",
            description  = "Full-grain leather with padded laptop compartment (up to 15\").",
            priceInCents = 18999,
            imageUrl     = "https://picsum.photos/seed/backpack/400/300",
            category     = "Bags"
        ),
        Product(
            id           = "8",
            name         = "Desk Lamp",
            description  = "LED desk lamp with wireless charging base and 5 brightness levels.",
            priceInCents = 4999,
            imageUrl     = "https://picsum.photos/seed/lamp/400/300",
            category     = "Home"
        )
    )

    override suspend fun getProducts(): Result<List<Product>> =
        Result.success(catalogue)

    override suspend fun getProductById(id: String): Result<Product> {
        val product = catalogue.find { it.id == id }
        return if (product != null) Result.success(product)
        else Result.failure(NoSuchElementException("Product $id not found"))
    }
}
