package com.jawnpaul.shopapp.feature.product.ui

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.jawnpaul.shopapp.core.data.CartRepository
import com.jawnpaul.shopapp.core.data.ProductRepository
import com.jawnpaul.shopapp.feature.product.ui.fakes.FakeCartRepository
import com.jawnpaul.shopapp.feature.product.ui.fakes.FakeProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
        // Given
        val products = productRepository.getProducts().first()

        // When
        viewModel.getProducts()

        val listState = viewModel.productList.first()

        // Then
        assertThat(listState.loading).isFalse()
        assertThat(listState.productList).isInstanceOf(List::class.java)
        assertThat(listState.productList).isEqualTo(products)
    }

    @Test
    fun `getProducts handle error correctly`() = runTest {
        // Given
        val productRepository = FakeProductRepository(true)
        viewModel = ProductViewModel(savedStateHandle, productRepository, cartRepository)

        // When
        viewModel.getProducts()

        val listState = viewModel.productList.first()
        // Then
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

        // When
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
