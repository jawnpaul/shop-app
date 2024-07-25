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

package com.jawnpaul.shopapp.core.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "product_table", indices = [Index(value = ["productServerId"], unique = true)])
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0,
    val productServerId: Int,
    val description: String,
    val name: String,
    val price: Int,
    val currencyCode: String,
    val currencySymbol: String,
    val quantity: Int,
    val imageUrl: String,
    val status: String
)

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(productEntity: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(productEntityList: List<ProductEntity>)

    @Query("SELECT * FROM product_table")
    fun getAllProduct(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM product_table WHERE productServerId =:productId")
    suspend fun getProduct(productId: Int): ProductEntity
}
