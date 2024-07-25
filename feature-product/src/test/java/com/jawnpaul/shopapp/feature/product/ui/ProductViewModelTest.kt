package com.jawnpaul.shopapp.feature.product.ui

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.jawnpaul.shopapp.core.data.CartRepository
import com.jawnpaul.shopapp.core.data.ProductRepository
import com.jawnpaul.shopapp.core.data.model.Cart
import com.jawnpaul.shopapp.core.data.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductViewModelTest {

    private lateinit var cartRepository: CartRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var viewModel: ProductViewModel
    private lateinit var savedStateHandle: SavedStateHandle
    private val testDispatcher = UnconfinedTestDispatcher()


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        cartRepository = FakeCartRepository()
        productRepository = FakeProductRepository()
        savedStateHandle = SavedStateHandle()
        viewModel = ProductViewModel(savedStateHandle, productRepository, cartRepository)
    }


    @Test
    fun `getProducts updates productList state correctly`() = runTest {
        //Given
        val products = productRepository.getProducts().first()

        //When
        viewModel.getProducts()

        val listState = viewModel.productList.first()

        //Then
        assertThat(listState.loading).isFalse()
        assertThat(listState.productList).isInstanceOf(List::class.java)
        assertThat(listState.productList).isEqualTo(products)

    }

    @Test
    fun `getProducts handle error correctly`() = runTest {
        //Given
        val productRepository = FakeProductRepository(true)
        viewModel = ProductViewModel(savedStateHandle, productRepository, cartRepository)

        //When
        viewModel.getProducts()

        val listState = viewModel.productList.first()
        //Then
        assertThat(listState.loading).isFalse()
        assertThat(listState.productList).isEmpty()
        assertThat(listState.error).isNotNull()
        assertThat(listState.error).isEqualTo("Something went wrong.")
    }

    @Test
    fun `selectProduct updates savedStateHandle and triggers getSingleProduct`() = runTest {
        // Given
        val productId = 1
        val product = productRepository.getSingleProduct(productId)

        // When
        viewModel.selectProduct(productId)

        // Then
        assertThat(savedStateHandle.get<Int>("product_id")).isEqualTo(productId)
        assertThat(viewModel.selectedProduct.value.product).isEqualTo(product)

    }

    @Test
    fun `addToCart increases cart size`() = runTest {
        // Given
        val productId = 1
        savedStateHandle["product_id"] = productId

        // When
        viewModel.addToCart()

        // Then
        assertThat(viewModel.cartSize.value).isEqualTo(4)
        assertThat(viewModel.selectedProduct.first().count).isEqualTo(
            cartRepository.getProductCount(
                productId
            )
        )
    }

    @Test
    fun `removeFromCart decreases cart size`() = runTest {
        // Given
        val productId = 1
        savedStateHandle["product_id"] = productId

        //When
        viewModel.removeFromCart()

        // Then
        assertThat(viewModel.cartSize.value).isEqualTo(2)
        assertThat(viewModel.selectedProduct.first().count).isEqualTo(
            cartRepository.getProductCount(
                productId
            )
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

}


class FakeProductRepository(private val shouldThrowException: Boolean = false) : ProductRepository {
    private val products =
        listOf(
            Product(
                productServerId = 1,
                price = 1,
                description = "Description",
                imageUrl = "",
                currencySymbol = "",
                quantity = 2,
                name = ""
            )
        )

    override fun getProducts(): Flow<List<Product>> = flow {
        if (shouldThrowException) {
            throw Exception("Something went wrong.")
        } else {
            emit(products)
        }
    }

    override suspend fun getSingleProduct(productId: Int): Product {
        return products.first { it.productServerId == productId }
    }

}

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
                //increase count
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