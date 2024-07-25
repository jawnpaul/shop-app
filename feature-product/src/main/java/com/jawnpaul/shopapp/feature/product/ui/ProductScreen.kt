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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.jawnpaul.shopapp.core.data.model.Product
import com.jawnpaul.shopapp.feature.product.R

@Composable
fun ProductScreen(modifier: Modifier = Modifier, navController: NavController, viewModel: ProductViewModel) {

    val cartSize = viewModel.cartSize.collectAsStateWithLifecycle()
    val products = viewModel.productList.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        if (cartSize.value > 0) {
            //Show cart
            item {
                CartUI(cartSize = cartSize.value)
            }
        }

        if (products.value.loading) {
            //Show Loading
            item {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }

        if (products.value.productList.isNotEmpty()) {
            items(
                items = products.value.productList,
                key = { product -> product.productServerId }) { product ->
                SingleProductItem(modifier = Modifier, product){
                    //navigate to detail page
                    viewModel.selectProduct(it)
                    navController.navigate("product_detail")
                }
            }
        }

    }
}

@Composable
fun SingleProductItem(
    modifier: Modifier = Modifier,
    product: Product,
    onClick: (productId: Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        onClick = {
            onClick(product.productServerId)
        }
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(corner = CornerSize(8.dp))
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = stringResource(R.string.product_image),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Column(modifier = Modifier.padding(start = 8.dp)) {
                //Name
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium
                )
                //Price
                Text(
                    text = product.priceText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

        }
    }
}
