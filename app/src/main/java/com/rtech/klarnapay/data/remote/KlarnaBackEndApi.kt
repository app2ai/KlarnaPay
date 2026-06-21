package com.rtech.klarnapay.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit interface for our Spring Boot merchant backend.
 *
 * Base URL is injected at runtime from BuildConfig.BACKEND_BASE_URL
 * (defaults to http://10.0.2.2:8080/ for the Android emulator → localhost).
 */
interface KlarnaBackendApi {

    /**
     * POST /api/session
     * Asks our backend to create a Klarna payment session and
     * return the client_token + available payment method categories.
     */
    @POST("api/session")
    suspend fun createSession(
        @Body request: CreateSessionRequestDto
    ): CreateSessionResponseDto

    /**
     * POST /api/order
     * Sends the Klarna authorization_token to our backend so it can
     * finalise the purchase by calling Klarna's order creation API.
     */
    @POST("api/order")
    suspend fun createOrder(
        @Body request: CreateOrderRequestDto
    ): CreateOrderResponseDto
}