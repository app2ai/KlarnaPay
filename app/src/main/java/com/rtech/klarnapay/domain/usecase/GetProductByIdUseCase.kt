package com.rtech.klarnapay.domain.usecase

import com.rtech.klarnapay.domain.model.Product
import com.rtech.klarnapay.domain.repo.ProductRepository

/**
 * Fetches a single product by its ID.
 */
class GetProductByIdUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(id: String): Result<Product> = repository.getProductById(id)
}
