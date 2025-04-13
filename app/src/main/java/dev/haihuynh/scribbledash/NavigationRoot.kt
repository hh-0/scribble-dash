package dev.haihuynh.scribbledash

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.haihuynh.scribbledash.drawing.DrawingScreenRoot
import dev.haihuynh.scribbledash.gamemode.DifficultySelectionScreenRoot

@Composable
fun NavigationRoot(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable(route = "home") {
            HomeScreenRoot(
                onGameModeSelect = { gameMode ->
                    navController.navigate("difficulty_selection")
                }
            )
        }
        composable(route = "difficulty_selection") {
            DifficultySelectionScreenRoot()
        }
        composable(route = "drawing") {
            DrawingScreenRoot()
        }
    }
}