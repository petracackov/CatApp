package com.petracackov.catapp.catRanking

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.petracackov.catapp.ui.theme.CatAppTheme

@Composable
fun Rankings(navigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(text = "Rankings")
        },
        navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Go Back")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CatAppTheme {
        Rankings(navigateBack = {})
    }
}