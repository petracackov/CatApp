package com.petracackov.catapp.catCards

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petracackov.catapp.data.CatApi
import com.petracackov.catapp.data.CatModel
import com.petracackov.catapp.utility.CardState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CatCardsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CatCardsUiState(currentCat = null, nextCat = null))
    val uiState: StateFlow<CatCardsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getNewCat()
        }
    }

    private suspend fun getNewCat() {
            try {
                var nextCat = uiState.value.nextCat
                if (nextCat == null) {
                    val randomCat = CatApi.retrofitService.getRandomCat()?.get(0)
                    nextCat = randomCat
                }

                val nextRandomCat = CatApi.retrofitService.getRandomCat()?.get(0)
                _uiState.update { currentState ->
                    currentState.copy(
                        currentCat = nextCat,
                        nextCat = nextRandomCat
                    )
                }

            } catch (e: Exception) {
                println(e.message)
            }

    }

    private fun setupCurrentCat(showDissStatement: Boolean = false) {
        var nextCat = uiState.value.nextCat
        _uiState.update { currentState ->
            currentState.copy(
                currentCat = nextCat,
                dissStatement = if (showDissStatement) DissStatement.values().random() else null
            )
        }
    }

    fun likeCurrentCat() {
        viewModelScope.launch {
            try {
                // TODO: like cat
                val randomCat = setupCurrentCat()
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    fun skipCat() {
        setupCurrentCat(showDissStatement = true)
        viewModelScope.launch {
            try {
                getNewCat()
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    fun setupNextCat() {
        viewModelScope.launch {
            try {
                getNewCat()
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    fun evaluateCardState(cardState: CardState) {
        when (cardState) {
            CardState.LEFT ->  setupCurrentCat(showDissStatement = true)
            CardState.RIGHT -> likeCurrentCat()
            CardState.MIDDLE -> return
        }
    }

}