package com.petracackov.catapp.catCards

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
    val animationDuration: Long = 300

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

    private suspend fun getAndSetupNextCat() {
        try {
            val randomCat = CatApi.retrofitService.getRandomCat()?.get(0)
            setupNextCat(newNextCat = randomCat)
        } catch (e: Exception) {
            setupNextCat(newNextCat = null)
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

    private fun setupCurrentCat(showDissStatement: Boolean = false, delay: Long = 300) {
        // TODO: figure out something else
        var nextCat = uiState.value.nextCat
        _uiState.update { currentState ->
            currentState.copy(
                currentCat = nextCat,
                dissStatement = if (showDissStatement) DissStatement.values().random() else null
            )
        }
    }

    private fun setupNextCat(newNextCat: CatModel?) {
        // TODO: figure out something else
        Timer().schedule(animationDuration) {
            _uiState.update { currentState ->
                currentState.copy(
                    nextCat = newNextCat
                )
            }
        }
    }

    fun evaluateCardState(cardState: CardState) {
        when (cardState) {
            CardState.LEFT -> skip()
            CardState.RIGHT -> like()
            CardState.MIDDLE -> return
        }
    }

    // Skip: Like a cat, request the new cat and set the next cat as current cat
    fun like() {
        viewModelScope.launch {
            try {
                setupLoadingState(isLoading = true)
                postLikeCat()
                setupCurrentCat()
                getAndSetupNextCat()
                setupLoadingState(isLoading = false)
            } catch (e: Exception) {
                // TODO: handle errors
                println(e)
            }
        }
    }

    // Skip: Set next cat as the current cat and load the next cat
    fun skip() {
        viewModelScope.launch {
            try {
                setupLoadingState(isLoading = true)
                setupCurrentCat(showDissStatement = true)
                getAndSetupNextCat()
                setupLoadingState(isLoading = false)
            } catch (e: Exception) {
                // TODO: handle errors
                println(e.message)
            }
        }
    }
}