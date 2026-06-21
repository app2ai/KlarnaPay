package com.rtech.klarnapay.domain

// ─────────────────────────────────────────────
// Product Use Cases
// ─────────────────────────────────────────────

/**
 * Fetches the product catalogue.
 * Single Responsibility: only retrieves the product list.
 */
class GetProductsUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(): Result<List<Product>> = repository.getProducts()
}

/**
 * Fetches a single product by its ID.
 */
class GetProductByIdUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(id: String): Result<Product> = repository.getProductById(id)
}

// ─────────────────────────────────────────────
// Payment Use Cases
// ─────────────────────────────────────────────

/**
 * Creates a Klarna payment session via the backend.
 * Validates business rules before delegating to the repository.
 */
class CreatePaymentSessionUseCase(private val repository: PaymentRepository) {

    suspend operator fun invoke(request: CreateSessionRequest): Result<PaymentSession> {
        // Business rule: order amount must be positive
        if (request.orderAmountInCents <= 0) {
            return Result.failure(IllegalArgumentException("Order amount must be greater than zero"))
        }
        if (request.orderLines.isEmpty()) {
            return Result.failure(IllegalArgumentException("Order must contain at least one line item"))
        }
        return repository.createSession(request)
    }
}

/**
 * Creates the final order after the user has authorized via the Klarna SDK.
 */
class CreateOrderUseCase(private val repository: PaymentRepository) {

    suspend operator fun invoke(request: CreateOrderRequest): Result<Order> {
        if (request.authorizationToken.isBlank()) {
            return Result.failure(IllegalArgumentException("Authorization token cannot be empty"))
        }
        return repository.createOrder(request)
    }
}