package com.jawnpaul.shopapp.feature.product.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jawnpaul.shopapp.feature.product.R

@Composable
fun CartUI(modifier: Modifier = Modifier, cartSize: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .drawBehind {
                    drawCircle(
                        color = Color.Red
                    )
                }
        ) {
            val text = if (cartSize > 9) {
                stringResource(R.string.nine_plus)
            } else {
                "$cartSize"
            }

            Text(text = text, modifier = Modifier.align(Alignment.Center), color = Color.White)
        }
    }
}
