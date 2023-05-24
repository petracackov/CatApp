package com.petracackov.catapp.catCards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.petracackov.catapp.catCard.CatCard
import com.petracackov.catapp.ui.theme.CatAppTheme
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import com.petracackov.catapp.R
import com.petracackov.catapp.ui.theme.*
import com.petracackov.catapp.utility.CardState
import com.petracackov.catapp.utility.DraggableComponent

@Composable
fun CatCards(
    modifier: Modifier = Modifier,
    catCardsViewModel: CatCardsViewModel = viewModel()
) {
    val catCardsUiState by catCardsViewModel.uiState.collectAsState()
    var cardState = remember { mutableStateOf(CardState.MIDDLE) }
    Box {
        Row(modifier = modifier
            .background(RodeoDust)
            .fillMaxSize()
        ) {
            Box(modifier = Modifier
                .background(
                    Thatch.copy(alpha = if (cardState.value == CardState.LEFT) 0.6f else 0.0f)
                )
                .weight(1f)
                .fillMaxSize())
            Box(modifier = Modifier
                .background(
                    Thatch.copy(alpha = if (cardState.value == CardState.RIGHT) 0.6f else 0.0f)
                )
                .weight(1f)
                .fillMaxSize())
        }
        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            Box(modifier = Modifier.zIndex(1f)) {
                CatCard(
                    cat = catCardsUiState.nextCat,
                    isBlurred = true,
                    modifier = Modifier.padding(10.dp)
                )

                DraggableComponent(
                    state = cardState,
                    onTransitionAnimationEnd = {
                        catCardsViewModel.evaluateCardStateAfterTransition(cardState = cardState.value)
                    },
                    onVisibilityAnimationEnd = {
                       catCardsViewModel.evaluateCardStateAfterVisibilityAnimation(cardState = cardState.value)
                    },
                ) {
                    CatCard(
                        cat = catCardsUiState.currentCat,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }


            ButtonsRow(catCardsViewModel = catCardsViewModel)

            Spacer(modifier = Modifier.weight(1f))

            DissText(catCardsUiState = catCardsUiState, modifier = Modifier.weight(1f))
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

@Composable
private fun ButtonsRow(catCardsViewModel: CatCardsViewModel) {
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
                    // TODO
                    //catCardsViewModel.skipCat()
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
                    // TODO
                   // catCardsViewModel.likeCurrentCat()
                }
            )
        }
    }
}

@Composable
private fun DissText(catCardsUiState: CatCardsUiState, modifier: Modifier = Modifier) {
    val dissId = catCardsUiState.dissStatement?.textId
    if (dissId != null) {
        Text(text = stringResource(dissId))
        Spacer(modifier = modifier)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CatAppTheme {
        CatCards()
    }
}