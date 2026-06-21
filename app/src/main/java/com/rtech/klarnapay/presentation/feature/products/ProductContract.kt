package com.rtech.klarnapay.presentation.feature.products

import com.rtech.klarnapay.domain.model.Product


sealed interface ProductsIntent {
    data object LoadProducts : ProductsIntent
    data class SelectProduct(val product: Product) : ProductsIntent
    data object DismissError : ProductsIntent
}

/** Immutable UI state snapshot. */
data class ProductsState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val error: String? = null,
    val navigateToCheckout: String? = null   // product ID to navigate to
)
