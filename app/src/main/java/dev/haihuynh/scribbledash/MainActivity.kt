package dev.haihuynh.scribbledash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import dev.haihuynh.scribbledash.drawing.DrawingScreenRoot
import dev.haihuynh.scribbledash.ui.theme.ScribbleDashTheme
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory

class MainActivity : ComponentActivity() {
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DrawingScreenRoot()
//            val navController = rememberNavController()
//            ScribbleDashTheme {
//                NavigationRoot(navController)
//            }
        }
    }
}