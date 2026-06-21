package com.rtech.klarnapay.presentation.feature.checkout

import com.rtech.klarnapay.domain.model.Order
import com.rtech.klarnapay.domain.model.PaymentMethodCategory
import com.rtech.klarnapay.domain.model.Product

/**
 * All user actions on the checkout screen.
 */
sealed interface CheckoutIntent {
    data object LoadProduct                          : CheckoutIntent
    data class SelectPaymentCategory(val id: String): CheckoutIntent
    data object InitiatePayment                      : CheckoutIntent
    // Called by the UI after the Klarna SDK returns an authorization token
    data class OnKlarnaAuthorized(val authToken: String) : CheckoutIntent
    // Called by the UI when Klarna SDK reports an error
    data class OnKlarnaError(val message: String)        : CheckoutIntent
    data object DismissError                         : CheckoutIntent
    data object NavigateBack                         : CheckoutIntent
}

/**
 * Represents the complete UI state for the checkout screen.
 */
data class CheckoutState(
    val isLoadingProduct: Boolean = true,
    val isCreatingSession: Boolean = false,
    val isCreatingOrder: Boolean = false,

    val product: Product? = null,

    // Klarna session data
    val clientToken: String? = null,
    val paymentMethodCategories: List<PaymentMethodCategory> = emptyList(),
    val selectedCategoryId: String? = null,

    // Flow signals
    val error: String? = null,
    val order: Order? = null,            // non-null = navigate to confirmation
    val navigateBack: Boolean = false
) {
    /** True once we have a client token ready to pass to the Klarna SDK. */
    val sessionReady: Boolean get() = clientToken != null

    /** The currently selected category object (null if none selected). */
    val selectedCategory: PaymentMethodCategory?
        get() = paymentMethodCategories.find { it.identifier == selectedCategoryId }
}
