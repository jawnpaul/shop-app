package com.jawnpaul.shopapp.core.data

import com.jawnpaul.shopapp.core.data.model.Cart
import com.jawnpaul.shopapp.core.database.CartDao
import com.jawnpaul.shopapp.core.database.CartEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

interface CartRepository {

    fun getCartItems(): Flow<List<Cart>>

    suspend fun insertItem(productId: Int)

    suspend fun removeItem(productId: Int)

    suspend fun getProductCount(productId: Int): Int
}

class CartRepositoryImpl @Inject constructor(private val cartDao: CartDao) : CartRepository {

    override fun getCartItems(): Flow<List<Cart>> = channelFlow {
        launch {
            cartDao.getCartItems().map { entityList ->
                entityList.map { entity ->
                    with(entity) {
                        Cart(productId = productId, count = count)
                    }
                }
            }.collect(::send)
        }
    }

    override suspend fun insertItem(productId: Int) {
        val entity = cartDao.getSingleCartItem(productId)

        if (entity != null) {
            val newEntity = CartEntity(productId = productId, count = entity.count + 1)
            cartDao.insert(newEntity)
        } else {
            cartDao.insert(CartEntity(productId = productId, count = 1))
        }
    }

    override suspend fun removeItem(productId: Int) {
        val entity = cartDao.getSingleCartItem(productId)
        if (entity != null) {
            val newCount = entity.count - 1
            if (newCount < 1) {
                // We can't have an item with 0 count
                //delete item
                cartDao.deleteItem(entity)
            } else {
                val newEntity = CartEntity(productId = productId, count = newCount)
                cartDao.removeItemAndUpdateInTransaction(oldItem = entity, newItem = newEntity)
            }
        }
    }

    override suspend fun getProductCount(productId: Int): Int {
        val entity = cartDao.getSingleCartItem(productId)
        return entity?.count ?: 0
    }

}