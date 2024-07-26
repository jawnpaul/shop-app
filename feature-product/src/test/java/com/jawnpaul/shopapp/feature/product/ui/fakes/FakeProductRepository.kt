package com.jawnpaul.shopapp.feature.product.ui.fakes

import com.jawnpaul.shopapp.core.data.ProductRepository
import com.jawnpaul.shopapp.core.data.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeProductRepository(private val shouldThrowException: Boolean = false) : ProductRepository {
    private val products =
        listOf(
            Product(
                productServerId = 1,
                price = 1,
                description = "Description",
                imageUrl = "",
                currencySymbol = "",
                quantity = 2,
                name = ""
            )
        )

    override fun getProducts(): Flow<List<Product>> = flow {
        if (shouldThrowException) {
            throw Exception("Something went wrong.")
        } else {
            emit(products)
        }
    }

    override suspend fun getSingleProduct(productId: Int): Product {
        return products.first { it.productServerId == productId }
    }
}
