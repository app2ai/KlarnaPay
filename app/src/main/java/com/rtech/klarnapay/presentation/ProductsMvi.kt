package com.rtech.klarnapay.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rtech.klarnapay.domain.GetProductsUseCase
import com.rtech.klarnapay.domain.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────
// MVI Contract
// ─────────────────────────────────────────────

/** All actions the user can trigger on the Products screen. */
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

// ─────────────────────────────────────────────
// ViewModel
// ─────────────────────────────────────────────

class ProductsViewModel(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProductsState())
    val state: StateFlow<ProductsState> = _state.asStateFlow()

    init {
        handleIntent(ProductsIntent.LoadProducts)
    }

    fun handleIntent(intent: ProductsIntent) {
        when (intent) {
            is ProductsIntent.LoadProducts    -> loadProducts()
            is ProductsIntent.SelectProduct   -> selectProduct(intent.product)
            is ProductsIntent.DismissError    -> dismissError()
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getProductsUseCase()
                .onSuccess  { products -> _state.update { it.copy(isLoading = false, products = products) } }
                .onFailure  { error    -> _state.update { it.copy(isLoading = false, error = error.message) } }
        }
    }

    private fun selectProduct(product: Product) {
        _state.update { it.copy(navigateToCheckout = product.id) }
    }

    fun onNavigatedToCheckout() {
        _state.update { it.copy(navigateToCheckout = null) }
    }

    private fun dismissError() {
        _state.update { it.copy(error = null) }
    }
}