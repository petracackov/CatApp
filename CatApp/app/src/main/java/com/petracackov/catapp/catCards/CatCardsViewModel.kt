package com.petracackov.catapp.catCards

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.petracackov.catapp.data.CatApi
import com.petracackov.catapp.data.CatModel
import com.petracackov.catapp.utility.CardState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.schedule

class CatCardsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CatCardsUiState(currentCat = null, nextCat = null))
    val uiState: StateFlow<CatCardsUiState> = _uiState.asStateFlow()

    init {
        getAndSetupNewCats()
    }

    private fun getAndSetupNewCats() {
        viewModelScope.launch {
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
    }

    private suspend fun postLikeCat() {
        val catId = uiState.value.currentCat?.id ?: run {
            return
        }
        val formParameters = JsonObject().apply {
            addProperty("image_id", catId)
            addProperty("value", 10)
        }
        CatApi.retrofitService.likeCat(formParameters)
    }

    private suspend fun getAndSetupNextCat(delay: Long = 0) {
        try {
            setupLoadingState(isLoading = true)
            val randomCat = CatApi.retrofitService.getRandomCat()?.get(0)
            setupLoadingState(isLoading = false)
            setupNextCat(newNextCat = randomCat, delay = delay)
        } catch (e: Exception) {
            setupNextCat(newNextCat = null, delay = 0)
            throw e
        }
    }

    private fun setupLoadingState(isLoading: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isLoadingNextCat = isLoading
            )
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

    private fun setupNextCat(newNextCat: CatModel?, delay: Long) {
        // TODO: figure out something els
        Timer().schedule(delay) {
            _uiState.update { currentState ->
                currentState.copy(
                    nextCat = newNextCat
                )
            }
        }
    }


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
            CardState.LEFT -> skip()
            // Skip: Set next cat as the current cat and request the like cat
            CardState.RIGHT -> {
                like()
            }
            CardState.MIDDLE -> return
        }
    }

    fun like() {
        viewModelScope.launch {
            try {
                postLikeCat()
                setupCurrentCat()
                getAndSetupNextCat(delay = 300)
            } catch (e: java.lang.Exception) {
                // TODO: handle errors
                println(e)
            }
        }
    }

    fun skip() {
        viewModelScope.launch {
            try {
                setupCurrentCat(showDissStatement = true)
                getAndSetupNextCat(delay = 300)
            } catch (e: java.lang.Exception) {
                println(e.message)
            }
        }
    }
}