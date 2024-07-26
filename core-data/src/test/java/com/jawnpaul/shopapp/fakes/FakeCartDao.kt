package com.jawnpaul.shopapp.fakes

import com.jawnpaul.shopapp.core.database.CartDao
import com.jawnpaul.shopapp.core.database.CartEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeCartDao : CartDao {

    private val fakeData = mutableListOf<CartEntity>()

    override fun getCartItems(): Flow<List<CartEntity>> {
        return flowOf(fakeData)
    }

    override suspend fun insert(cartEntity: CartEntity) {
        fakeData.add(0, cartEntity)
    }

    override suspend fun getSingleCartItem(productId: Int): CartEntity? {
        val item = fakeData.filter { it.productId == productId }
        return if (item.isEmpty()) {
            null
        } else {
            item[0]
        }
    }

    override suspend fun deleteItem(cartEntity: CartEntity) {
        fakeData.remove(cartEntity)
    }
}
