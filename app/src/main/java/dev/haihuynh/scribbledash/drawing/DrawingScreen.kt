package dev.haihuynh.scribbledash.drawing

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.haihuynh.scribbledash.R
import dev.haihuynh.scribbledash.components.GradientBackground
import dev.haihuynh.scribbledash.components.ScribbleDashTopAppBarExitButton
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun DrawingScreenRoot(
    onExit: () -> Unit = {},
    viewModel: DrawingViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var counter by remember { mutableIntStateOf(3) }

    LaunchedEffect(null) {
        viewModel.loadSampleDrawing(context = context, resourceId = R.drawable.whale)
        while (counter > 0) {
            delay(1000)
            counter--
        }
        viewModel.onAction(DrawingAction.OnReady)
    }

    DrawingScreen(
        state = state,
        onAction = viewModel::onAction,
        onExit = onExit,
        counter = counter,
        onCanvasSetUp = viewModel::setUpCanvas
    )
}

@Composable
private fun DrawingScreen(
    state: DrawingState,
    onAction: (DrawingAction) -> Unit = {},
    onExit: () -> Unit = {},
    counter: Int = 3,
    onCanvasSetUp: (Int, Int) -> Unit
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val isUndoButtonEnabled = state.paths.isNotEmpty()
                val isRedoButtonEnabled = state.undoPaths.isNotEmpty()
                val isDoneButtonEnabled = state.paths.isNotEmpty()
                if (state.displaySample) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$counter seconds left",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                } else {
                    IconButton(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                color = if (isRedoButtonEnabled)
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(24.dp)
                            ),
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f), // Color when enabled
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f) // Color when disabled
                        ),
                        onClick = {
                            onAction(DrawingAction.OnUndo)
                        },
                        enabled = isUndoButtonEnabled
                    ) {
                        Image(
                            painter = painterResource(R.drawable.undo_icon),
                            contentDescription = null,
                            alpha = if (isUndoButtonEnabled) 1f else 0.4f
                        )
                    }
                    IconButton(modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = if (isRedoButtonEnabled)
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(24.dp)
                        ),
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        ),
                        onClick = {
                            onAction(DrawingAction.OnRedo)
                        },
                        enabled = isRedoButtonEnabled
                    ) {
                        Image(
                            painter = painterResource(R.drawable.redo_icon),
                            contentDescription = null,
                            alpha = if (isRedoButtonEnabled) 1f else 0.4f
                        )
                    }
                    Button(
                        modifier = Modifier
                            .height(64.dp)
                            .weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0DD280),
                            contentColor = Color.White,
                            disabledContentColor = Color.White.copy(alpha = 0.4f),
                        ),
                        border = BorderStroke(width = 6.dp, color = Color.White),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                        onClick = { onAction(DrawingAction.OnDone) },
                        enabled = isDoneButtonEnabled
                    ) {
                        Text(
                            text = "Done",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Normal,
                                letterSpacing = 0.2f.sp
                            )
                        )
                    }
                }
            }

        }
    ) { innerPadding ->
        GradientBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(bottom = 64.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (state.displaySample) "Ready, set..." else "Time to draw!",
                    style = MaterialTheme.typography.headlineLarge
                )
                DrawingCanvas(
                    paths = state.paths,
                    currentPath = state.currentPath,
                    displaySample = state.displaySample,
                    samplePaths = state.samplePaths.asComposePaths(),
                    onAction = onAction,
                    onCanvasSetUp = onCanvasSetUp,
                    modifier = Modifier
                )
            }
        }
    }
}

fun List<android.graphics.Path>.asComposePaths(): List<Path> {
    return this.map { it.asComposePath() }
}

@Composable
private fun DrawingCanvas(
    paths: List<PathData>,
    currentPath: PathData?,
    displaySample: Boolean,
    samplePaths: List<Path>,
    onAction: (DrawingAction) -> Unit,
    onCanvasSetUp: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (displaySample) {
        SampleCanvas(
            samplePaths = samplePaths,
            modifier = modifier
        )
    } else {
        UserCanvas(
            paths = paths,
            currentPath = currentPath,
            onAction = onAction,
            onCanvasSetUp = onCanvasSetUp,
            modifier = modifier
        )
    }
}

@Preview
@Composable
private fun DrawingScreenPreview() {
    DrawingScreen(
        state = DrawingState(),
        onAction = { },
        onExit = {},
        counter = 3,
        onCanvasSetUp = { _, _ -> }
    )
}