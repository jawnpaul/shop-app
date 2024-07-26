package com.jawnpaul.shopapp.fakes

import com.jawnpaul.shopapp.core.database.ProductDao
import com.jawnpaul.shopapp.core.database.ProductEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeProductDao : ProductDao {

    private val products = mutableListOf(
        ProductEntity(
            productServerId = 1,
            description = "",
            name = "",
            price = 10,
            currencyCode = "USD",
            currencySymbol = "$",
            quantity = 10,
            imageUrl = "",
            status = "Active"
        )
    )

    override suspend fun insert(productEntity: ProductEntity) {
        products.add(productEntity)
    }

    override suspend fun insertAll(productEntityList: List<ProductEntity>) {
        products.addAll(productEntityList)
    }

    override fun getAllProduct(): Flow<List<ProductEntity>> {
        return flowOf(products)
    }

    override suspend fun getProduct(productId: Int): ProductEntity {
        return products.first { it.productServerId == productId }
    }
}
