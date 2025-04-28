package dev.haihuynh.scribbledash.result

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.haihuynh.scribbledash.components.GradientBackground
import dev.haihuynh.scribbledash.components.ScribbleDashTopAppBarExitButton
import dev.haihuynh.scribbledash.drawing.DrawingsCache
import dev.haihuynh.scribbledash.drawing.SampleCanvas

@Composable
fun ResultScreenRoot(
    onExit: () -> Unit = {}
) {
    ResultScreen(
        userDrawing = DrawingsCache.userDrawing,
        sampleDrawing = DrawingsCache.sampleDrawing,
    )
}

@Composable
private fun ResultScreen(
    userDrawing: List<Path>,
    sampleDrawing: List<Path>,
    onExit: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ScribbleDashTopAppBarExitButton(
                onExit = {
                    onExit()
                }
            )
        },
        bottomBar = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(32.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF238CFF),
                    contentColor = Color.White,
                    disabledContentColor = Color.White.copy(alpha = 0.4f),
                ),
                border = BorderStroke(width = 6.dp, color = Color.White),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                onClick = {  },
                enabled = true
            ) {
                Text(
                    text = "Try Again",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 0.2f.sp
                    )
                )
            }
        }
    ) { innerPadding ->
        GradientBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 64.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "100%",
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 66.sp
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .rotate(-18f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Example",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = MaterialTheme.typography.titleSmall.color.copy(alpha = 0.6f)
                            ),
                            fontWeight = FontWeight.W600
                        )
                        SampleCanvas(
                            samplePaths = sampleDrawing,
                            modifier = Modifier
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .rotate(14f)
                            .offset(y = 26.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Drawing",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = MaterialTheme.typography.titleSmall.color.copy(alpha = 0.6f)
                            ),
                            fontWeight = FontWeight.W600
                        )
                        SampleCanvas(
                            samplePaths = userDrawing,
                            modifier = Modifier
                        )
                    }
                }
                Text(
                    text = "Woohoo!",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "You've officially raised the bar!",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Preview
@Composable
private fun ResultScreenRootPreview() {
    ResultScreen(
        userDrawing = listOf(),
        sampleDrawing = listOf()
    )
}