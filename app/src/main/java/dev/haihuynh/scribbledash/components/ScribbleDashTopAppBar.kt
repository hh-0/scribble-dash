package dev.haihuynh.scribbledash.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import dev.haihuynh.scribbledash.ui.theme.BagelFatOne

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScribbleDashTopAppBar() {
    TopAppBar(
        title = {
            Text(
                text = "ScribbleDash",
                fontSize = 26.sp,
                fontFamily = BagelFatOne
            )
        }
    )
}