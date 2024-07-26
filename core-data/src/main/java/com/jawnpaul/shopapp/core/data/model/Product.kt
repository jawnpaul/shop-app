package com.jawnpaul.shopapp.core.data.model

data class Product(
    val productServerId: Int,
    val imageUrl: String,
    val name: String,
    val description: String,
    val price: Int,
    val currencySymbol: String,
    val quantity: Int
) {
    val priceText = "${currencySymbol}$price"
}
