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

package com.jawnpaul.shopapp.feature.product.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jawnpaul.shopapp.core.data.CartRepository
import com.jawnpaul.shopapp.core.data.ProductRepository
import com.jawnpaul.shopapp.core.data.model.Cart
import com.jawnpaul.shopapp.core.data.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _productList = MutableStateFlow(ProductListState())
    val productList get() = _productList

    private val _cartSize = MutableStateFlow(0)
    val cartSize get() = _cartSize

    private val _selectedProduct = MutableStateFlow(ProductDetailState())
    val selectedProduct get() = _selectedProduct

    private val selectedProductId: Int?
        get() = savedStateHandle.get<Int>("product_id")

    init {
        getProducts()
        getCartSize()
    }

    fun getProducts() {
        _productList.update { it.copy(loading = true, error = null) }
        productRepository.getProducts().onEach(::updateProducts).catch { throwable ->
            _productList.update { it.copy(loading = false, error = "Something went wrong.") }
        }.launchIn(viewModelScope)
    }

    private fun updateProducts(result: List<Product>) {
        _productList.update { it.copy(loading = false, productList = result) }
    }

    fun selectProduct(productId: Int) {
        savedStateHandle["product_id"] = productId
        getSingleProduct()
    }

    private fun getSingleProduct() {
        viewModelScope.launch {
            selectedProductId?.let { id ->
                val product = productRepository.getSingleProduct(id)
                _selectedProduct.update { it.copy(product = product) }
            }
        }
        updateProductState()
    }

    private fun getCartSize() {
        cartRepository.getCartItems().onEach(::updateCart)
            .launchIn(viewModelScope)
    }

    private fun updateCart(result: List<Cart>) {
        updateProductState()
        _cartSize.update { result.sumOf { it.count } }
    }

    fun addToCart() {
        viewModelScope.launch {
            selectedProductId?.let {
                cartRepository.insertItem(it)
            }
        }
        getCartSize()
    }

    fun removeFromCart() {
        viewModelScope.launch {
            selectedProductId?.let {
                cartRepository.removeItem(it)
            }
        }
        getCartSize()
    }

    private fun updateProductState() {
        viewModelScope.launch {
            selectedProductId?.let { id ->
                val count = cartRepository.getProductCount(id)
                _selectedProduct.update { it.copy(count = count) }
            }
        }
    }
}
