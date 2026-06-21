package com.rtech.klarnapay.di

import com.rtech.klarnapay.BuildConfig
import com.rtech.klarnapay.data.remote.service.KlarnaBackendApi
import com.rtech.klarnapay.data.repo.PaymentRepositoryImpl
import com.rtech.klarnapay.data.repo.ProductRepositoryImpl
import com.rtech.klarnapay.domain.repo.PaymentRepository
import com.rtech.klarnapay.domain.repo.ProductRepository
import com.rtech.klarnapay.domain.usecase.CreateOrderUseCase
import com.rtech.klarnapay.domain.usecase.CreatePaymentSessionUseCase
import com.rtech.klarnapay.domain.usecase.GetProductByIdUseCase
import com.rtech.klarnapay.domain.usecase.GetProductsUseCase
import com.rtech.klarnapay.presentation.feature.checkout.CheckoutViewModel
import com.rtech.klarnapay.presentation.feature.products.ProductsViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Single Koin module for the entire app.
 *
 * Layered structure:
 *   Network → Data → Domain (UseCases) → Presentation (ViewModels)
 *
 * Each dependency is declared once — Koin handles the graph resolution.
 */
val appModule = module {

    // ── Network ───────────────────────────────

    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BACKEND_BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<KlarnaBackendApi> {
        get<Retrofit>().create(KlarnaBackendApi::class.java)
    }

    // ── Repositories ──────────────────────────

    single<ProductRepository> { ProductRepositoryImpl() }

    single<PaymentRepository> { PaymentRepositoryImpl(api = get()) }

    // ── Use Cases ─────────────────────────────

    factory { GetProductsUseCase(repository = get()) }
    factory { GetProductByIdUseCase(repository = get()) }
    factory { CreatePaymentSessionUseCase(repository = get()) }
    factory { CreateOrderUseCase(repository = get()) }

    // ── ViewModels ────────────────────────────

    viewModel {
        ProductsViewModel(
            getProductsUseCase = get()
        )
    }

    viewModel { (productId: String) ->
        CheckoutViewModel(
            productId = productId,
            getProductByIdUseCase = get(),
            createPaymentSessionUseCase = get(),
            createOrderUseCase = get()
        )
    }
}
