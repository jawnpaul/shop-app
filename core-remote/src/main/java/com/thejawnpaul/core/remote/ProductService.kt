package com.thejawnpaul.core.remote

import retrofit2.http.GET

interface ProductService {

    @GET("productBundles")
    suspend fun getProducts(): List<ProductRemote>
}
