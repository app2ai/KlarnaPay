package com.rtech.klarnapay.domain.usecase

import com.rtech.klarnapay.domain.model.CreateOrderRequest
import com.rtech.klarnapay.domain.model.Order
import com.rtech.klarnapay.domain.repo.PaymentRepository

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