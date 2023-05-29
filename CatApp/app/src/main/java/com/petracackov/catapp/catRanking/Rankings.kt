package com.petracackov.catapp.catRanking

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.petracackov.catapp.catCards.CatCards
import com.petracackov.catapp.ui.theme.CatAppTheme

@Composable
fun Rankings() {
    Text(text = "rankings")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CatAppTheme {
        Rankings()
    }
}