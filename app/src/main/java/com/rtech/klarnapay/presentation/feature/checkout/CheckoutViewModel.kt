package com.rtech.klarnapay.presentation.feature.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rtech.klarnapay.domain.model.CreateOrderRequest
import com.rtech.klarnapay.domain.model.CreateSessionRequest
import com.rtech.klarnapay.domain.model.OrderLine
import com.rtech.klarnapay.domain.usecase.CreateOrderUseCase
import com.rtech.klarnapay.domain.usecase.CreatePaymentSessionUseCase
import com.rtech.klarnapay.domain.usecase.GetProductByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val productId: String,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val createPaymentSessionUseCase: CreatePaymentSessionUseCase,
    private val createOrderUseCase: CreateOrderUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CheckoutState())
    val state: StateFlow<CheckoutState> = _state.asStateFlow()

    init {
        handleIntent(CheckoutIntent.LoadProduct)
    }

    fun handleIntent(intent: CheckoutIntent) {
        when (intent) {
            is CheckoutIntent.LoadProduct             -> loadProduct()
            is CheckoutIntent.SelectPaymentCategory   -> selectCategory(intent.id)
            is CheckoutIntent.InitiatePayment         -> initiatePayment()
            is CheckoutIntent.OnKlarnaAuthorized      -> finalizeOrder(intent.authToken)
            is CheckoutIntent.OnKlarnaError           -> onKlarnaError(intent.message)
            is CheckoutIntent.DismissError            -> dismissError()
            is CheckoutIntent.NavigateBack            -> _state.update { it.copy(navigateBack = true) }
        }
    }

    // ── Private handlers ─────────────────────

    private fun loadProduct() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingProduct = true) }

            getProductByIdUseCase(productId)
                .onSuccess { product ->
                    _state.update { it.copy(isLoadingProduct = false, product = product) }
                }
                .onFailure { err ->
                    _state.update { it.copy(isLoadingProduct = false, error = err.message) }
                }
        }
    }

    private fun selectCategory(categoryId: String) {
        _state.update { it.copy(selectedCategoryId = categoryId) }
    }

    /**
     * Step 1 of the Klarna flow:
     * Ask backend to create a Klarna session → receive clientToken.
     */
    private fun initiatePayment() {
        val product = _state.value.product ?: return

        viewModelScope.launch {
            _state.update { it.copy(isCreatingSession = true, error = null) }

            val request = CreateSessionRequest(
                orderAmountInCents = product.priceInCents,
                orderLines = listOf(
                    OrderLine(
                        name = product.name,
                        quantity = 1,
                        unitPriceInCents = product.priceInCents,
                        totalAmountInCents = product.priceInCents
                    )
                )
            )

            createPaymentSessionUseCase(request)
                .onSuccess { session ->
                    val defaultCategory = session.paymentMethodCategories.firstOrNull()?.identifier
                    _state.update {
                        it.copy(
                            isCreatingSession       = false,
                            clientToken             = session.clientToken,
                            paymentMethodCategories = session.paymentMethodCategories,
                            selectedCategoryId      = defaultCategory
                        )
                    }
                }
                .onFailure { err ->
                    _state.update { it.copy(isCreatingSession = false, error = err.message) }
                }
        }
    }

    /**
     * Step 3 of the Klarna flow (Step 2 = SDK authorization handled by UI):
     * Send authToken to backend to create the final order.
     */
    private fun finalizeOrder(authToken: String) {
        val product = _state.value.product ?: return

        viewModelScope.launch {
            _state.update { it.copy(isCreatingOrder = true, error = null) }

            val request = CreateOrderRequest(
                authorizationToken = authToken,
                orderAmountInCents = product.priceInCents,
                orderLines = listOf(
                    OrderLine(
                        name = product.name,
                        quantity = 1,
                        unitPriceInCents = product.priceInCents,
                        totalAmountInCents = product.priceInCents
                    )
                )
            )

            createOrderUseCase(request)
                .onSuccess { order ->
                    _state.update { it.copy(isCreatingOrder = false, order = order) }
                }
                .onFailure { err ->
                    _state.update { it.copy(isCreatingOrder = false, error = err.message) }
                }
        }
    }

    private fun onKlarnaError(message: String) {
        _state.update { it.copy(error = "Klarna error: $message", isCreatingOrder = false) }
    }

    private fun dismissError() {
        _state.update { it.copy(error = null) }
    }

    fun onNavigatedToConfirmation() {
        _state.update { it.copy(order = null) }
    }

    fun onNavigatedBack() {
        _state.update { it.copy(navigateBack = false) }
    }
}
