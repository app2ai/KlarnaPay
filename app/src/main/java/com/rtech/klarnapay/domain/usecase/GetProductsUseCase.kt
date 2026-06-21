package com.rtech.klarnapay.domain.usecase

import com.rtech.klarnapay.domain.model.Product
import com.rtech.klarnapay.domain.repo.ProductRepository

/**
 * Fetches the product catalogue.
 * Single Responsibility: only retrieves the product list.
 */
class GetProductsUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(): Result<List<Product>> = repository.getProducts()
}
