package com.jawnpaul.shopapp.core.data

import com.google.common.truth.Truth.assertThat
import com.jawnpaul.shopapp.core.database.ProductDao
import com.jawnpaul.shopapp.fakes.FakeProductDao
import com.jawnpaul.shopapp.utils.PRODUCT_BUNDLE_PATH
import com.jawnpaul.shopapp.utils.ShopAppRequestDispatcher
import com.jawnpaul.shopapp.utils.makeTestApiService
import io.mockk.MockKAnnotations
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test

class ProductRepositoryImplTest {

    private lateinit var repository: ProductRepository
    private lateinit var mockWebServer: MockWebServer
    private lateinit var productDao: ProductDao

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockWebServer = MockWebServer()
        mockWebServer.dispatcher = ShopAppRequestDispatcher().RequestDispatcher()
        mockWebServer.start()
        productDao = FakeProductDao()
        repository = ProductRepositoryImpl(productDao, makeTestApiService(mockWebServer))
    }

    @Test
    fun `getProducts should return a list of products`() = runTest {
        // Given
        val products = productDao.getAllProduct().first()

        // When
        val result = repository.getProducts()

        // Then
        result.collect {
            assertThat(it).isInstanceOf(List::class.java)
            assertThat(it.isNotEmpty()).isTrue()
            assertThat(it.first().productServerId).isEqualTo(products.first().productServerId)
        }
    }

    @Test
    fun `getSingleProduct should return product from local database`() = runTest {
        // Given
        val productId = 1
        val product = productDao.getProduct(productId)

        // When
        val result = repository.getSingleProduct(productId)

        // Then
        assertThat(result.productServerId).isEqualTo(product.productServerId)
        assertThat(result.name).isEqualTo(product.name)
        assertThat(result.description).isEqualTo(product.description)
        assertThat(result.price).isEqualTo(product.price)
    }

    @Test
    fun `check that calling getProducts makes request to correct path`() = runTest {
        // Given
        val path = PRODUCT_BUNDLE_PATH

        // When
        repository.getProducts().toList()

        // Then
        assertThat(path)
            .isEqualTo(mockWebServer.takeRequest().path)
    }

    @Test
    fun `check that calling getProducts makes a GET request`() = runTest {
        // Given
        val request = "GET $PRODUCT_BUNDLE_PATH HTTP/1.1"

        // When
        repository.getProducts().toList()

        // Then
        assertThat(request)
            .isEqualTo(mockWebServer.takeRequest().requestLine)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
}
