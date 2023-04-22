package com.petracackov.catapp.catCards

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petracackov.catapp.data.CatApi
import com.petracackov.catapp.data.CatModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CatCardsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CatCardsUiState(currentCat = null))
    val uiState: StateFlow<CatCardsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getNewCat()
        }
    }

    private suspend fun getNewCat(showDissStatement: Boolean = false) {
            try {
                val randomCat = CatApi.retrofitService.getRandomCat()?.get(0)
                _uiState.update { currentState ->
                    currentState.copy(
                        currentCat = randomCat,
                        dissStatement = if (showDissStatement) DissStatement.values().random() else null
                    )
                }
            } catch (e: Exception) {
                println(e.message)
            }

    }

    fun likeCurrentCat() {
        viewModelScope.launch {
            try {
                // TODO: like cat
                val randomCat = getNewCat()
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    fun skipCat() {
        viewModelScope.launch {
            try {
                val randomCat = getNewCat(showDissStatement = true)
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

}