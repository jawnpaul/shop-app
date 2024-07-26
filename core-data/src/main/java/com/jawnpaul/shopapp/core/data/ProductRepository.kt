/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jawnpaul.shopapp.core.data

import com.jawnpaul.shopapp.core.data.model.Product
import com.jawnpaul.shopapp.core.database.ProductDao
import com.jawnpaul.shopapp.core.database.ProductEntity
import com.thejawnpaul.core.remote.ProductService
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

interface ProductRepository {

    fun getProducts(): Flow<List<Product>>

    suspend fun getSingleProduct(productId: Int): Product
}

class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val productService: ProductService
) : ProductRepository {
    override fun getProducts(): Flow<List<Product>> = channelFlow {
        launch {
            productDao.getAllProduct().map { entityList ->
                entityList.map { entity ->
                    with(entity) {
                        Product(
                            productServerId = productServerId,
                            imageUrl = imageUrl,
                            name = name,
                            description = description,
                            price = price,
                            currencySymbol = currencySymbol,
                            quantity = quantity
                        )
                    }
                }
            }.collect(::send)
        }

        val cacheModel = productService.getProducts().map { model ->
            with(model) {
                ProductEntity(
                    productServerId = id,
                    description = description,
                    name = name,
                    price = price,
                    currencySymbol = currencySymbol,
                    currencyCode = currencyCode,
                    quantity = quantity,
                    imageUrl = imageUrl,
                    status = status
                )
            }
        }

        insertProducts(cacheModel)
    }.filter(List<Product>::isNotEmpty)

    override suspend fun getSingleProduct(productId: Int): Product = with(productDao.getProduct(productId)) {
        Product(
            productServerId = productServerId,
            imageUrl = imageUrl,
            name = name,
            description = description,
            price = price,
            currencySymbol = currencySymbol,
            quantity = quantity
        )
    }

    private suspend fun insertProducts(items: List<ProductEntity>) {
        productDao.insertAll(items)
    }
}
