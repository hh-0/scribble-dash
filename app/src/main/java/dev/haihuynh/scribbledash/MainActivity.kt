package dev.haihuynh.scribbledash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import dev.haihuynh.scribbledash.drawing.DrawingScreenRoot
import dev.haihuynh.scribbledash.gamemode.DifficultySelectionScreenRoot
import dev.haihuynh.scribbledash.ui.theme.ScribbleDashTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            ScribbleDashTheme {
                NavigationRoot(navController)
//                HomeScreenRoot()
//                DrawingScreenRoot()
//                DifficultySelectionScreenRoot()
            }
        }
    }
}