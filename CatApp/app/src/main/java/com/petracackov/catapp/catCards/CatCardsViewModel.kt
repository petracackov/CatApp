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
import java.sql.Time
import java.util.*
import kotlin.concurrent.schedule

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
            val currentCat = CatApi.retrofitService.getRandomCat()?.get(0)
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
            currentState.copy(
                currentCat = nextCat,
                dissStatement = if (showDissStatement) DissStatement.values().random() else null
            )
        }
    }

    private fun getAndSetupNextCat() {
        viewModelScope.launch {
            try {
                setupLoadingState(isLoading = true)
                val randomCat = CatApi.retrofitService.getRandomCat()?.get(0)
                setupLoadingState(isLoading = false)

                // TODO: figure out something els
                Timer().schedule(300) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            nextCat = randomCat
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        nextCat = null
                    )
                }
                println(e.message)
            }
        }
    }

    private fun setupLoadingState(isLoading: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isLoadingNextCat = isLoading
            )
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

    fun evaluateCardState(cardState: CardState) {
        when (cardState) {
            // Skip: Set next cat as the current cat
            CardState.LEFT -> {
                setupCurrentCat(showDissStatement = true)
                getAndSetupNextCat()
            }
            // Skip: Set next cat as the current cat and request the like cat
            CardState.RIGHT -> {
                setupCurrentCat(showDissStatement = false)
                getAndSetupNextCat()
            }

            CardState.MIDDLE -> return
        }
    }
}