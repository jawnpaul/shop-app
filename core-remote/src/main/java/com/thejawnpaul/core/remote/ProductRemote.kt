package com.thejawnpaul.core.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductRemote(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "description") val description: String,
    @field:Json(name = "price") val price: Int,
    @field:Json(name = "currencyCode") val currencyCode: String,
    @field:Json(name = "currencySymbol") val currencySymbol: String,
    @field:Json(name = "quantity") val quantity: Int,
    @field:Json(name = "imageLocation") val imageUrl: String,
    @field:Json(name = "status") val status: String
)