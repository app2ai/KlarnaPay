package com.rtech.klarnapay.domain.usecase

import com.rtech.klarnapay.domain.model.CreateSessionRequest
import com.rtech.klarnapay.domain.model.PaymentSession
import com.rtech.klarnapay.domain.repo.PaymentRepository

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
