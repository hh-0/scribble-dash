package dev.haihuynh.scribbledash.drawing

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.haihuynh.scribbledash.R
import dev.haihuynh.scribbledash.components.GradientBackground
import dev.haihuynh.scribbledash.components.ScribbleDashTopAppBarExitButton
import org.koin.androidx.compose.koinViewModel
import kotlin.math.abs

@Composable
fun DrawingScreenRoot(
    onExit: () -> Unit = {},
    viewModel: DrawingViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(null) {
        viewModel.loadSampleDrawing(context = context, resourceId = R.drawable.alien)
    }

    DrawingScreen(
        state = state,
        onAction = viewModel::onAction,
        onExit = onExit
    )
}

@Composable
private fun DrawingScreen(
    state: DrawingState,
    onAction: (DrawingAction) -> Unit = {},
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
                    modifier = Modifier.height(64.dp).weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0DD280),
                        contentColor = Color.White,
                        disabledContentColor = Color.White.copy(alpha = 0.4f),
                    ),
                    border = BorderStroke(width = 6.dp, color = Color.White),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    onClick = { onAction(DrawingAction.OnClearCanvasClick) },
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
    ) { innerPadding ->
        GradientBackground {
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .padding(bottom = 64.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Time to draw!",
                    style = MaterialTheme.typography.headlineLarge
                )
                DrawingCanvas(
                    paths = state.paths,
                    currentPath = state.currentPath,
                    samplePaths = state.samplePaths.asComposePaths(),
                    onAction = onAction,
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
    samplePaths: List<Path>,
    onAction: (DrawingAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Canvas(
            modifier = modifier
                .padding(12.dp)
                .clip(RoundedCornerShape(16.dp))
                .clipToBounds()
                .border(width = 1.dp, color = Color.Black.copy(alpha = 0.05f), shape = RoundedCornerShape(16.dp))
                .background(Color.White)
                .pointerInput(true) {
                    detectDragGestures(
                        onDragStart = {
                            onAction(DrawingAction.OnNewPathStart)
                        },
                        onDragEnd = {
                            onAction(DrawingAction.OnPathEnd)
                        },
                        onDrag = { change, _ ->
                            onAction(DrawingAction.OnDraw(change.position))
                        },
                    )
                }.drawBehind {
                    val cellWidth = size.width / 3
                    val cellHeight = size.height / 3

                    // Draw horizontal lines
                    drawLine(
                        color = Color.Black.copy(alpha = 0.05f),
                        start = Offset(x = 0f, y = cellHeight),
                        end = Offset(x = size.width, y = cellHeight),
                        strokeWidth = 2f
                    )
                    drawLine(
                        color = Color.Black.copy(alpha = 0.05f),
                        start = Offset(x = 0f, y = cellHeight * 2),
                        end = Offset(x = size.width, y = cellHeight * 2),
                        strokeWidth = 2f
                    )
                    // Draw vertical lines
                    drawLine(
                        color = Color.Black.copy(alpha = 0.05f),
                        start = Offset(x = cellWidth, y = 0f),
                        end = Offset(x = cellWidth, y = size.height),
                        strokeWidth = 2f
                    )
                    drawLine(
                        color = Color.Black.copy(alpha = 0.05f),
                        start = Offset(x = cellWidth * 2, y = 0f),
                        end = Offset(x = cellWidth * 2, y = size.height),
                        strokeWidth = 2f
                    )
                }
        ) {
            paths.fastForEach { pathData ->
                drawPath(
                    path = pathData.path,
                    color = pathData.color
                )
            }
            currentPath?.let {
                drawPath(
                    path = it.path,
                    color = it.color
                )
            }

            val sampleDrawingBounds = getDrawingBounds(samplePaths)
            sampleDrawingBounds?.let { bounds ->
                val scaleX = size.width / bounds.width
                val scaleY = size.height / bounds.height
                val scaleFactor = minOf(scaleX, scaleY)

                withTransform(
                    {
                        translate(
                            (size.width - bounds.width * scaleFactor * 0.7f) / 2f,
                            (size.height - bounds.height * scaleFactor * 0.7f) / 2f
                        )
                        scale(
                            scaleFactor * 0.70f,
                            scaleFactor * 0.70f,
                            pivot = Offset(0f, 0f)
                        )
                        translate(
                            left = -bounds.left,
                            top = -bounds.top
                        )
                    }
                ) {
                    samplePaths.fastForEach { path ->
                        drawPath(
                            path = path,
                            color = Color.Black,
                            style = Stroke(
                                width = 10f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }

            }
        }
    }
}

private fun getDrawingBounds(paths: List<Path>): Rect? {
    if (paths.isEmpty()) {
        return null
    }

    var minLeft = Float.POSITIVE_INFINITY
    var minTop = Float.POSITIVE_INFINITY
    var maxRight = Float.NEGATIVE_INFINITY
    var maxBottom = Float.NEGATIVE_INFINITY

    for (path in paths) {
        val pathBounds = path.getBounds()
        minLeft = minOf(minLeft, pathBounds.left)
        minTop = minOf(minTop, pathBounds.top)
        maxRight = maxOf(maxRight, pathBounds.right)
        maxBottom = maxOf(maxBottom, pathBounds.bottom)
    }

    if (minLeft == Float.POSITIVE_INFINITY) return null // Handle empty paths case

    return Rect(
        left = minLeft,
        top = minTop,
        right = maxRight,
        bottom = maxBottom,
    )
}

private fun DrawScope.drawPath(
    path: List<Offset>,
    color: Color,
    thickness: Float = 10f
) {
    val smoothedPath = Path().apply {
        if (path.isNotEmpty()) {
            moveTo(path.first().x, path.first().y)

            val smoothness = 5
            for (i in 1 until path.lastIndex) {
                val from = path[i - 1]
                val to = path[i]
                val dx = abs(from.x - to.x)
                val dy = abs(from.y - to.y)

                if (dx >= smoothness || dy >= smoothness) {
                    quadraticTo(
                        x1 = (from.x + to.x) / 2f,
                        y1 = (from.y + to.y) / 2f,
                        x2 = to.x,
                        y2 = to.y
                    )
                }
            }
        }
    }
    drawPath(
        path = smoothedPath,
        color = color,
        style = Stroke(
            width = thickness,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}

@Preview
@Composable
private fun DrawingScreenPreview() {
    DrawingScreen(
        state = DrawingState()
    )
}