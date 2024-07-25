package com.jawnpaul.shopapp.feature.product.ui

import com.jawnpaul.shopapp.core.data.model.Product

data class ProductListState(
    val loading: Boolean = false,
    val productList: List<Product> = emptyList(),
    val error: String? = null
) {
    val showError = error != null && productList.isEmpty()
}

data class ProductDetailState(
    val product: Product? = null,
    val count: Int = 0
) {
    val canBeIncreased = product != null && count < product.quantity
    val canBeDecreased = product != null && count > 0
}