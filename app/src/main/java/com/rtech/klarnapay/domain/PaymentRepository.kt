package com.rtech.klarnapay.domain

/**
 * Repository interface (port) — the domain layer depends only on this abstraction.
 * The data layer provides the concrete implementation (adapter).
 *
 * Following SOLID:
 *  • Dependency Inversion  — domain depends on abstraction, not concrete class
 *  • Interface Segregation — single, focused contract for payment operations
 */
interface PaymentRepository {

    /**
     * Creates a Klarna payment session on the backend.
     * Returns a [PaymentSession] containing the clientToken for the SDK.
     */
    suspend fun createSession(request: CreateSessionRequest): Result<PaymentSession>

    /**
     * Finalises the payment by creating an order on the backend
     * using the authorization token received from the Klarna SDK.
     */
    suspend fun createOrder(request: CreateOrderRequest): Result<Order>
}