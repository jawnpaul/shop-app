package com.jawnpaul.shopapp.feature.product.ui.fakes

import com.jawnpaul.shopapp.core.data.CartRepository
import com.jawnpaul.shopapp.core.data.model.Cart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeCartRepository : CartRepository {

    private val cartItems = mutableListOf(Cart(productId = 1, count = 3))

    override fun getCartItems(): Flow<List<Cart>> {
        return flowOf(cartItems)
    }

    override suspend fun insertItem(productId: Int) {
        if (cartItems.isEmpty()) {
            cartItems.add(Cart(productId = productId, count = 1))
        } else {
            val match = cartItems.filter { it.productId == productId }
            if (match.isEmpty()) {
                cartItems.add(Cart(productId = productId, count = 1))
            } else {
                // increase count
                val oldCount = match[0].count
                cartItems.remove(Cart(productId = productId, count = oldCount))
                cartItems.add(Cart(productId = productId, count = oldCount + 1))
            }
        }
    }

    override suspend fun removeItem(productId: Int) {
        val oldCount = cartItems.first { it.productId == productId }.count
        cartItems.remove(Cart(productId = productId, count = oldCount))
        cartItems.add(Cart(productId = productId, count = oldCount - 1))
    }

    override suspend fun getProductCount(productId: Int): Int {
        val item = cartItems.first { it.productId == productId }
        return item.count
    }
}
