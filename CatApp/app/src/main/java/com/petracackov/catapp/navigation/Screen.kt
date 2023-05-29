package com.petracackov.catapp.navigation

sealed class Screen() {

    object CatCards: Screen() {
        fun route() = "catCards"
    }

    object Rankings: Screen() {
        fun route() = "rankings"
    }

}