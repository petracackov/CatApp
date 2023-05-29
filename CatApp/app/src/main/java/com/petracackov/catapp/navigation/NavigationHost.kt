package com.petracackov.catapp

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.petracackov.catapp.catCards.CatCards
import com.petracackov.catapp.catRanking.Rankings
import com.petracackov.catapp.navigation.Screen

@Composable
fun NavigationHost() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Screen.CatCards.route()) {
        composable(route = Screen.CatCards.route()) {
            CatCards {
                navController.navigate(Screen.Rankings.route())
            }
        }

        composable(route = Screen.Rankings.route()) {
            Rankings {
                navController.popBackStack()
            }
        }
    }
}