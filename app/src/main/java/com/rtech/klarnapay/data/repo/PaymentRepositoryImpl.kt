package com.rtech.klarnapay.data.repo

import com.rtech.klarnapay.data.remote.dto.CreateOrderRequestDto
import com.rtech.klarnapay.data.remote.dto.CreateSessionRequestDto
import com.rtech.klarnapay.data.remote.dto.OrderLineDto
import com.rtech.klarnapay.data.remote.service.KlarnaBackendApi
import com.rtech.klarnapay.domain.model.CreateOrderRequest
import com.rtech.klarnapay.domain.model.CreateSessionRequest
import com.rtech.klarnapay.domain.model.Order
import com.rtech.klarnapay.domain.model.PaymentMethodCategory
import com.rtech.klarnapay.domain.model.PaymentSession
import com.rtech.klarnapay.domain.repo.PaymentRepository

/**
 * Concrete implementation of [PaymentRepository].
 *
 * Responsibilities:
 *  1. Map domain models → DTOs before sending to API
 *  2. Map response DTOs → domain models
 *  3. Wrap network errors in [Result.failure]
 *
 * SOLID highlights:
 *  • Single Responsibility — only handles payment API calls
 *  • Open/Closed — new fields can be added via DTOs without changing domain
 *  • Liskov Substitution — safely substitutes PaymentRepository anywhere
 */
class PaymentRepositoryImpl(
    private val api: KlarnaBackendApi
) : PaymentRepository {

    override suspend fun createSession(request: CreateSessionRequest): Result<PaymentSession> =
        runCatching {
            val dto = api.createSession(request.toDto())
            PaymentSession(
                sessionId    = dto.sessionId,
                clientToken  = dto.clientToken,
                paymentMethodCategories = dto.paymentMethodCategories.map { cat ->
                    PaymentMethodCategory(
                        identifier = cat.identifier,
                        name = cat.name,
                        assetUrl = cat.assetUrl
                    )
                }
            )
        }

    override suspend fun createOrder(request: CreateOrderRequest): Result<Order> =
        runCatching {
            val dto = api.createOrder(request.toDto())
            Order(
                orderId     = dto.orderId,
                redirectUrl = dto.redirectUrl,
                status      = dto.status
            )
        }

    // ── Private mappers ───────────────────────

    private fun CreateSessionRequest.toDto() = CreateSessionRequestDto(
        purchaseCountry = purchaseCountry,
        purchaseCurrency = purchaseCurrency,
        locale = locale,
        orderAmount = orderAmountInCents,
        orderLines = orderLines.map { line ->
            OrderLineDto(
                name = line.name,
                quantity = line.quantity,
                unitPrice = line.unitPriceInCents,
                totalAmount = line.totalAmountInCents
            )
        }
    )

    private fun CreateOrderRequest.toDto() = CreateOrderRequestDto(
        authorizationToken = authorizationToken,
        purchaseCountry = purchaseCountry,
        purchaseCurrency = purchaseCurrency,
        locale = locale,
        orderAmount = orderAmountInCents,
        orderLines = orderLines.map { line ->
            OrderLineDto(
                name = line.name,
                quantity = line.quantity,
                unitPrice = line.unitPriceInCents,
                totalAmount = line.totalAmountInCents
            )
        }
    )
}
