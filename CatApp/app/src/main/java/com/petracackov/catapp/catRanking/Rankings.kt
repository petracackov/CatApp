package com.petracackov.catapp.catRanking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.petracackov.catapp.ui.theme.CatAppTheme
import com.petracackov.catapp.ui.theme.RodeoDust
import com.petracackov.catapp.ui.theme.RomanCoffee

@Composable
fun Rankings(
    rankingsViewModel: RankingsViewModel = viewModel(),
    navigateBack: () -> Unit
) {
    val cats = rankingsViewModel.cats.collectAsLazyPagingItems()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Rankings")
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Go Back")
                    }
                },
                backgroundColor =  RomanCoffee
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(RodeoDust)
        ) {
            items(items = cats) {cat ->
                Column(modifier = Modifier.padding(10.dp)) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(cat?.url)
                            .crossfade(true)
                            .build(),
                        modifier = Modifier
                            .height(200.dp)
                            .border(width = 3.dp, color = RomanCoffee, shape = RoundedCornerShape(10))
                            .clip(RoundedCornerShape(10)),
                        contentDescription = null,
                        contentScale = ContentScale.Fit
                    )
                    cat?.id?.let { Text(text = it) }
                }
            }
        }
    }
}
