package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R

/** AmaApp emblem (res/drawable/ama_mark.webp), used in the top bar and launcher icon. */
@Composable
fun AmaMark(size: Dp = 38.dp, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ama_mark),
        contentDescription = "AmaApp logo",
        modifier = modifier.size(size),
        contentScale = ContentScale.Fit
    )
}

/** AmaApp full lockup (res/drawable/ama_logo.webp), for the welcome screen. */
@Composable
fun AmaLogo(width: Dp = 200.dp, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ama_logo),
        contentDescription = "AmaApp logo",
        modifier = modifier.width(width),
        contentScale = ContentScale.FillWidth
    )
}
