package com.petracackov.catapp.catRanking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petracackov.catapp.data.CatApi
import com.petracackov.catapp.data.CatModel
import com.petracackov.catapp.data.PaginationData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RankingsViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(RankingsUiState())
    val uiState: StateFlow<RankingsUiState> = _uiState.asStateFlow()

    init {
        getCats()
    }

    private fun getCats() {
        viewModelScope.launch {
            try {
                val catsResponse = CatApi.retrofitService.getCatsList(page = 3)
                var catsData: List<CatModel> = catsResponse.body() ?: throw Exception()
                val paginationData = PaginationData(header = catsResponse.headers())
                _uiState.update { currentState ->
                    currentState.copy(
                        cats = catsData,
                        paginationData = paginationData
                    )
                }
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }
}