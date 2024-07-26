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

package com.jawnpaul.shopapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jawnpaul.shopapp.feature.product.ui.ProductDetailScreen
import com.jawnpaul.shopapp.feature.product.ui.ProductScreen
import com.jawnpaul.shopapp.feature.product.ui.ProductViewModel

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "product_list") {
        composable("product_list") {
            val viewModel = hiltViewModel<ProductViewModel>()
            ProductScreen(
                modifier = Modifier.padding(horizontal = 16.dp),
                navController = navController,
                viewModel = viewModel
            )
        }

        composable("product_detail") { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("product_list")
            }
            val parentViewModel = hiltViewModel<ProductViewModel>(parentEntry)
            ProductDetailScreen(navController = navController, viewModel = parentViewModel)
        }
    }
}
