package com.petracackov.catapp.catCards

import com.petracackov.catapp.R
import com.petracackov.catapp.data.CatModel

data class CatCardsUiState(
    val currentCat: CatModel?,
    val nextCat: CatModel?,
    val dissStatement: DissStatement? = null,
    val isLoadingNextCat: Boolean = false
)