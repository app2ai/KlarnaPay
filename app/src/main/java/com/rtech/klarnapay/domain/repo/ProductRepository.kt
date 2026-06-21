package com.rtech.klarnapay.domain.repo

import com.rtech.klarnapay.domain.model.Product

/**
 * Repository interface for the product catalogue.
 * In this POC products are hard-coded locally; in a real app
 * this abstraction allows swapping to a remote source without
 * touching the domain or presentation layers.
 */
interface ProductRepository {
    suspend fun getProducts(): Result<List<Product>>
    suspend fun getProductById(id: String): Result<Product>
}