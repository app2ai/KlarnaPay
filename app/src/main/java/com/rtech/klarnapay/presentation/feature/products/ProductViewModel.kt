package com.rtech.klarnapay.presentation.feature.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rtech.klarnapay.domain.model.Product
import com.rtech.klarnapay.domain.usecase.GetProductsUseCase
import com.rtech.klarnapay.presentation.ProductsIntent
import com.rtech.klarnapay.presentation.ProductsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
