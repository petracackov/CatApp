package com.petracackov.catapp.catRanking

import com.petracackov.catapp.data.CatModel
import com.petracackov.catapp.data.PaginationData

data class RankingsUiState(
    val cats: List<CatModel> = listOf<CatModel>(),
    val paginationData: PaginationData = PaginationData(limit = 0, page = 0, count = 0)
)
