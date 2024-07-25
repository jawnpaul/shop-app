package com.jawnpaul.shopapp.core.data

import com.google.common.truth.Truth.assertThat
import com.jawnpaul.shopapp.core.database.ProductDao
import com.jawnpaul.shopapp.core.database.ProductEntity
import com.jawnpaul.shopapp.utils.PRODUCT_BUNDLE_PATH
import com.jawnpaul.shopapp.utils.ShopAppRequestDispatcher
import com.jawnpaul.shopapp.utils.makeTestApiService
import com.thejawnpaul.core.remote.ProductRemote
import com.thejawnpaul.core.remote.ProductService
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test

class ProductRepositoryImplTest {

    private lateinit var repository: ProductRepository
    private lateinit var mockWebServer: MockWebServer

    @MockK
    lateinit var productDao: ProductDao

    @MockK
    lateinit var productService: ProductService

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockWebServer = MockWebServer()
        mockWebServer.dispatcher = ShopAppRequestDispatcher().RequestDispatcher()
        mockWebServer.start()
        repository = ProductRepositoryImpl(productDao, makeTestApiService(mockWebServer))
    }


    @Test
    fun `getProducts should return a list of products`() = runTest {
        //Given
        val entityList = listOf(
            ProductEntity(
                productServerId = 1,
                description = "",
                name = "",
                price = 10,
                currencyCode = "USD",
                currencySymbol = "$",
                quantity = 10,
                imageUrl = "",
                status = "Active"
            )
        )

        val remoteList = listOf(
            ProductRemote(
                id = 1,
                name = "",
                description = "",
                price = 10,
                currencyCode = "USD",
                currencySymbol = "$",
                quantity = 1,
                imageUrl = "",
                status = "Active"
            )
        )

        coEvery { productDao.getAllProduct() } returns flowOf(entityList)
        coEvery { productService.getProducts() } returns remoteList
        coEvery { productDao.insertAll(any()) } just Runs


        //When
        val result = repository.getProducts()

        //Then
        result.collect {
            assertThat(it).isInstanceOf(List::class.java)
            assertThat(it.isNotEmpty()).isTrue()
            assertThat(it.first().productServerId).isEqualTo(entityList.first().productServerId)
        }

    }

    @Test
    fun `getSingleProduct should return product from local database`() = runTest {
        // Given
        val productId = 1
        val productEntity = ProductEntity(
            productServerId = productId,
            name = "Banana",
            description = "Description",
            price = 10,
            currencyCode = "USD",
            currencySymbol = "$",
            quantity = 1,
            imageUrl = "",
            status = "Active"
        )
        coEvery { productDao.getProduct(productId) } returns productEntity

        // When
        val result = repository.getSingleProduct(productId)

        // Then
        assertThat(result.productServerId).isEqualTo(productId)
        assertThat(result.name).isEqualTo("Banana")
        assertThat(result.description).isEqualTo("Description")
        assertThat(result.price).isEqualTo(10)

        coVerify { productDao.getProduct(productId) }
    }

    @Test
    fun `insertProducts should insert products into local database`() = runTest {
        // Given
        val products = listOf(
            ProductEntity(
                productServerId = 1,
                description = "",
                name = "",
                price = 10,
                currencyCode = "USD",
                currencySymbol = "$",
                quantity = 10,
                imageUrl = "",
                status = "Active"
            ),
            ProductEntity(
                productServerId = 2,
                description = "",
                name = "",
                price = 10,
                currencyCode = "USD",
                currencySymbol = "$",
                quantity = 10,
                imageUrl = "",
                status = "Active"
            )
        )
        coEvery { productDao.insertAll(products) } just Runs

        // When
        repository.insertProducts(products)

        // Then
        coVerify { productDao.insertAll(products) }
    }

    @Test
    fun `check that calling getProducts makes request to correct path`() = runTest {
        //Given
        val path = PRODUCT_BUNDLE_PATH

        coEvery { productDao.getAllProduct() } returns flowOf(emptyList())
        coEvery { productService.getProducts() } returns emptyList()
        coEvery { productDao.insertAll(any()) } just Runs

        //When
        repository.getProducts().toList()


        //Then
        assertThat(path)
            .isEqualTo(mockWebServer.takeRequest().path)
    }

    @Test
    fun `check that calling getProducts makes a GET request`() = runTest {

        //Given
        val request = "GET $PRODUCT_BUNDLE_PATH HTTP/1.1"

        coEvery { productDao.getAllProduct() } returns flowOf(emptyList())
        coEvery { productService.getProducts() } returns emptyList()
        coEvery { productDao.insertAll(any()) } just Runs

        //When
        repository.getProducts().toList()


        //Then
        assertThat(request)
            .isEqualTo(mockWebServer.takeRequest().requestLine)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
}