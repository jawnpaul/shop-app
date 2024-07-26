package com.jawnpaul.shopapp.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "cart_table")
data class CartEntity(
    @PrimaryKey
    val productId: Int,
    val count: Int
)

@Dao
interface CartDao {

    @Query("SELECT * FROM cart_table")
    fun getCartItems(): Flow<List<CartEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cartEntity: CartEntity)

    @Query("SELECT * FROM cart_table WHERE productId = :productId LIMIT 1")
    suspend fun getSingleCartItem(productId: Int): CartEntity?

    @Delete
    suspend fun deleteItem(cartEntity: CartEntity)

    @Transaction
    suspend fun removeItemAndUpdateInTransaction(oldItem: CartEntity, newItem: CartEntity) {
        deleteItem(oldItem)
        insert(newItem)
    }
}
