package com.petracackov.catapp.catCards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.petracackov.catapp.catCard.CatCard
import com.petracackov.catapp.ui.theme.CatAppTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.petracackov.catapp.R
import com.petracackov.catapp.ui.theme.*
import com.petracackov.catapp.utility.DraggableComponent

@Composable
fun CatCards(
    modifier: Modifier = Modifier,
    catCardsViewModel: CatCardsViewModel = viewModel()
) {
    val catCardsUiState by catCardsViewModel.uiState.collectAsState()
    Column(
        modifier = modifier
            .background(RodeoDust)
            .fillMaxSize()
    ) {
        DraggableComponent(
            onDragEnd = {
                println("End")
            }
        ) {
            CatCard(
                cat = catCardsUiState.currentCat,
                modifier = Modifier.padding(10.dp)
            )
        }

        Box(
            modifier = Modifier
                .padding(10.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                ActionButton(
                    modifier = Modifier
                        .weight(1f),
                    text = stringResource(R.string.skip_button_title),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = RomanCoffee,
                        contentColor = Color.White),
                    onClick = {
                        catCardsViewModel.skipCat()
                    }
                )

                ActionButton(
                    modifier = Modifier
                        .weight(1f),
                    text = stringResource(R.string.like_button_title),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = SummerGreen,
                        contentColor = Color.Black),
                    onClick = {
                        catCardsViewModel.likeCurrentCat()
                    }
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        val dissId = catCardsUiState.dissStatement?.textId
        if (dissId != null) {
            Text(text = stringResource(dissId))
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ActionButton(
    modifier: Modifier = Modifier,
    text: String,
    colors: ButtonColors,
    onClick: () -> Unit,
) {
    Button(
        colors = colors,
        contentPadding = PaddingValues(10.dp),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 3.dp,
            focusedElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp,
            hoveredElevation = 0.dp
        ),
        modifier = modifier
            .fillMaxWidth(),
        onClick = onClick
    ) {
        Text(text = text)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CatAppTheme {
        CatCards()
    }
}