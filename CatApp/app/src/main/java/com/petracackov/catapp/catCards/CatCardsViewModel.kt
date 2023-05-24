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
            getAndSetupNewCats()
        }
    }

    private suspend fun getAndSetupNewCats() {
        try {
            var currentCat = CatApi.retrofitService.getRandomCat()?.get(0)
            val nextRandomCat = CatApi.retrofitService.getRandomCat()?.get(0)
            _uiState.update { currentState ->
                currentState.copy(
                    currentCat = currentCat,
                    nextCat = nextRandomCat,
                    dissStatement = null
                )
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }

    private fun setupCurrentCat(showDissStatement: Boolean = false) {
        var nextCat = uiState.value.nextCat
        _uiState.update { currentState ->
            val current: String = currentState.currentCat?.id.orEmpty()
            val next: String = currentState.nextCat?.id.orEmpty()
            val new: String = nextCat?.id.orEmpty()
            println("Petra setupCurrentCat Current: $current,Next: $next, newCurrent: $new")
            currentState.copy(
                currentCat = nextCat,
                dissStatement = if (showDissStatement) DissStatement.values().random() else null
            )
        }
    }

    private fun getAndSetupNextCat() {
        viewModelScope.launch {
            try {
                val randomCat = CatApi.retrofitService.getRandomCat()?.get(0)
                _uiState.update { currentState ->
                    val current: String = currentState.currentCat?.id.orEmpty()
                    val next: String = currentState.nextCat?.id.orEmpty()
                    val new: String = randomCat?.id.orEmpty()
                    println("Petra getAndSetupNextCat Current: $current,Next: $next, newNext: $new")
                    currentState.copy(
                        nextCat = randomCat
                    )
                }
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

//    fun likeCurrentCat() {
//        viewModelScope.launch {
//            try {
//                // TODO: like cat
//                val randomCat = setupCurrentCat()
//            } catch (e: Exception) {
//                println(e.message)
//            }
//        }
//    }
//
//    fun skipCat() {
//        setupCurrentCat(showDissStatement = true)
//        viewModelScope.launch {
//            try {
//                getNewCat()
//            } catch (e: Exception) {
//                println(e.message)
//            }
//        }
//    }

    fun evaluateCardStateAfterTransition(cardState: CardState) {
        when (cardState) {
            // Skip: Set next cat as the current cat
            CardState.LEFT -> setupCurrentCat(showDissStatement = true)
            // Skip: Set next cat as the current cat and request the like cat
            CardState.RIGHT -> setupCurrentCat(showDissStatement = false)
            CardState.MIDDLE -> return
        }
    }

    fun evaluateCardStateAfterVisibilityAnimation(cardState: CardState) {
        when (cardState) {
            CardState.LEFT, CardState.RIGHT -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        nextCat = null
                    )
                }
                getAndSetupNextCat()
            }
            CardState.MIDDLE -> return
        }
    }
}