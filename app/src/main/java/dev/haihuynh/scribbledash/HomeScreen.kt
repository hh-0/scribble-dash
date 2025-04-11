package dev.haihuynh.scribbledash

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.haihuynh.scribbledash.components.ScribbleDashBottomAppBar
import dev.haihuynh.scribbledash.components.ScribbleDashTopAppBar
import dev.haihuynh.scribbledash.ui.theme.ScribbleDashTheme

@Composable
fun HomeScreenRoot() {
    HomeScreen()
}

@Composable
private fun HomeScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { ScribbleDashTopAppBar() },
        bottomBar = { ScribbleDashBottomAppBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    ScribbleDashTheme {
        HomeScreen()
    }
}