package com.jawnpaul.shopapp.core.data

import com.google.common.truth.Truth.assertThat
import com.jawnpaul.shopapp.core.database.CartDao
import com.jawnpaul.shopapp.core.database.CartEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CartRepositoryImplTest {

    private lateinit var repository: CartRepository
    private lateinit var cartDao: CartDao

    @Before
    fun setUp() {
        cartDao = FakeCartDao()
        repository = CartRepositoryImpl(cartDao)
    }

    @Test
    fun `getCartItems should return a list of cartItems`() = runTest {
        //Given
        cartDao.insert(CartEntity(productId = 1, count = 2))
        cartDao.getCartItems()

        //When
        val result = repository.getCartItems()

        //Then
        result.collect{
            assertThat(it).isInstanceOf(List::class.java)
            assertThat(it.isNotEmpty()).isTrue()
        }

    }

    @Test
    fun `getCartItems should return a emptyList when empty`() = runTest {
        //Given
        cartDao.getCartItems()

        //When
        val result = repository.getCartItems()

        //Then
        result.collect{
            assertThat(it).isInstanceOf(List::class.java)
            assertThat(it.isEmpty()).isTrue()
        }

    }

    @Test
    fun `insertItem should save cart item`() = runTest {
        //Given
        val productId = 1

        //When
        repository.insertItem(productId)

        //Then
        assertThat(cartDao.getSingleCartItem(productId)?.productId).isEqualTo(productId)

    }

    @Test
    fun `insertItem should increase item count when item exists`() = runTest {
        //Given
        val productId = 1
        cartDao.insert(CartEntity(productId = productId, count = 1))

        //When
        repository.insertItem(productId)

        //Then
        assertThat(cartDao.getSingleCartItem(productId)?.count).isEqualTo(2)

    }

    @Test
    fun `removeItem should remove item`() = runTest {
        //Given
        val productId = 1
        cartDao.insert(CartEntity(productId = productId, count = 1))

        //When
        repository.removeItem(productId)

        //Then
        assertThat(cartDao.getSingleCartItem(productId)).isEqualTo(null)
    }

    @Test
    fun `removeItem should decrease item count when item exits`() = runTest {
        //Given
        val productId = 1
        cartDao.insert(CartEntity(productId = productId, count = 2))

        //When
        repository.removeItem(productId)

        //Then
        assertThat(cartDao.getSingleCartItem(productId)?.count).isEqualTo(1)
    }

    @Test
    fun `getProductCount should return correct count`() = runTest {
        //Given
        val productId = 1
        cartDao.insert(CartEntity(productId = productId, count = 10))


        //When
        val result = repository.getProductCount(productId)

        //Then
        assertThat(result).isEqualTo(10)
    }
}

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