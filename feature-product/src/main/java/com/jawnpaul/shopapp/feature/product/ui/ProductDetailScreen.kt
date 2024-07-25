package com.jawnpaul.shopapp.feature.product.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.jawnpaul.shopapp.feature.product.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ProductViewModel
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var showBottomSheet by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    val cartSize = viewModel.cartSize.collectAsStateWithLifecycle()
    val productState = viewModel.selectedProduct.collectAsStateWithLifecycle()


    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        text = stringResource(R.string.product_detail),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {

            if (cartSize.value > 0) {
                Spacer(modifier = Modifier.size(4.dp))
                CartUI(cartSize = cartSize.value)
            }


            productState.value.product?.let { product ->
                //Image
                Surface(
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 24.dp)
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(corner = CornerSize(8.dp))
                ) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = stringResource(R.string.product_image),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                Text(product.name)

                Text(product.priceText)

                Text(product.description)

                Spacer(modifier = Modifier.size(48.dp))

                Button(modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        //show bottom sheet
                        showBottomSheet = true
                    }

                ) {
                    Text(stringResource(R.string.add_to_cart))
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {},
                    enabled = cartSize.value > 0
                ) {
                    Text(stringResource(R.string.buy_now))
                }

            }

        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                //Sheet content
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
                    Text(stringResource(R.string.add_item_to_cart))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(onClick = {
                            viewModel.removeFromCart()
                        }, enabled = productState.value.canBeDecreased) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_remove_24),
                                contentDescription = stringResource(R.string.add)
                            )
                        }

                        Text(
                            "${productState.value.count}",
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        IconButton(onClick = {
                            viewModel.addToCart()
                        }, enabled = productState.value.canBeIncreased) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_add_24),
                                contentDescription = stringResource(R.string.remove)
                            )
                        }
                    }

                    Button(modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showBottomSheet = false
                                }
                            }
                        }) {
                        Text("Done")
                    }

                    Spacer(modifier = Modifier.size(80.dp))
                }
            }
        }

    }
}