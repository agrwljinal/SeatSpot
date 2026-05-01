package com.spot.app.ui.theme

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush

@Composable
fun AppBackground(): Modifier {
    return Modifier.background(
        Brush.verticalGradient(
            listOf(BgTop, BgBottom)
        )
    )
}

