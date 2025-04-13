package dev.haihuynh.scribbledash.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import dev.haihuynh.scribbledash.ui.theme.ScribbleDashTheme

@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFEFAF6),
                    Color(0xFFFFF1E2)
                )
            )
        )
    ) {
        Column {
            content()
        }
    }
}

@Preview
@Composable
private fun GradientBackgroundPreview() {
    ScribbleDashTheme {
        GradientBackground {

        }
    }
}