package dev.haihuynh.scribbledash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.haihuynh.scribbledash.drawing.DrawingScreenRoot
import dev.haihuynh.scribbledash.gamemode.DifficultySelectionScreenRoot
import dev.haihuynh.scribbledash.ui.theme.ScribbleDashTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScribbleDashTheme {
                HomeScreenRoot()
//                DrawingScreenRoot()
//                DifficultySelectionScreenRoot()
            }
        }
    }
}