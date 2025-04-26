package dev.haihuynh.scribbledash.drawing

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Paint
import android.os.Environment
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.toAndroidRectF
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.haihuynh.scribbledash.R
import dev.haihuynh.scribbledash.components.GradientBackground
import dev.haihuynh.scribbledash.components.ScribbleDashTopAppBarExitButton
import dev.haihuynh.scribbledash.createPathsFromPathData
import dev.haihuynh.scribbledash.getPathDataFromVectorDrawable
import org.koin.androidx.compose.koinViewModel
import java.io.IOException
import kotlin.math.abs
import androidx.compose.ui.platform.LocalDensity
import kotlin.collections.first
import kotlin.collections.isNotEmpty
import kotlin.collections.lastIndex
import androidx.core.graphics.createBitmap
import androidx.core.graphics.transform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@Composable
fun DrawingScreenRoot(
    onExit: () -> Unit = {},
    viewModel: DrawingViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

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
    onExit: () -> Unit = {},
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
                val isClearCanvasButtonEnabled = state.paths.isNotEmpty()
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
                    onClick = { onAction(DrawingAction.OnClearCanvasClick) },
                    enabled = isClearCanvasButtonEnabled
                ) {
                    Text(
                        text = "CLEAR CANVAS",
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
        var shouldDrawSample by remember { mutableStateOf(false) }
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
                    text = "Time to draw!",
                    style = MaterialTheme.typography.headlineLarge
                )
                DrawingCanvas(
                    paths = state.paths,
                    currentPath = state.currentPath,
                    onAction = onAction,
                    modifier = Modifier,
                    userBounds = state.userBounds,
                    shouldDrawSample = shouldDrawSample,
                    userStrokeWidth = state.userStrokeWidth,
                    sampleStrokeWidth = state.sampleStrokeWidth
                )
                Button(
                    onClick = {
                        println("on Calculate")
                        onAction(DrawingAction.OnCalculateUserBounds)
                        shouldDrawSample = true
                    }
                ) {
                    Text(text = "Calculate")
                }
            }
        }
    }
}

@SuppressLint("ResourceType")
@Composable
private fun DrawingCanvas(
    modifier: Modifier = Modifier,
    paths: List<PathData>,
    currentPath: PathData?,
    userBounds: Rect?,
    onAction: (DrawingAction) -> Unit,
    shouldDrawSample: Boolean,
    userStrokeWidth: Float,
    sampleStrokeWidth: Float,
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
        val context = LocalContext.current
        val rawPath = getPathDataFromVectorDrawable(context, R.drawable.book)
        val samplePaths = createPathsFromPathData(rawPath)

        val coroutineScope = rememberCoroutineScope()

        Canvas(
            modifier = modifier
                .padding(12.dp)
                .clip(RoundedCornerShape(16.dp))
                .clipToBounds()
                .background(Color.White)
                .border(
                    width = 1.dp,
                    color = Color.Black.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(16.dp)
                )
                .drawBehind {
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
                .padding(4.dp)

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
                }

        ) {

            val canvasHeight = size.height
            val canvasWidth = size.width

            val bounds = calculateBoundsWithStroke(samplePaths, sampleStrokeWidth)?.inset(sampleStrokeWidth / 2f)
            val sampleHeight = bounds!!.height
            val sampleWidth = bounds!!.width
            val scaleX = canvasWidth / sampleWidth
            val scaleY = canvasHeight / sampleHeight
            val scaleFactor = minOf(scaleX, scaleY)

            // Draw sample
            withTransform(
                {
                    scale(scaleFactor, Offset(0f, 0f))
                    translate(-bounds.left, -bounds.top)
                }

            ) {
                if (shouldDrawSample) {
                    samplePaths.fastForEach { samplePath ->
                        drawPath(
                            path = samplePath.asComposePath(),
                            color = Color.Blue,
                            style = Stroke(
                                width = sampleStrokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }

                    bounds?.let {
                        drawRect(
                            color = Color.Red,
                            topLeft = it.topLeft,
                            size = it.size,
                            style = Stroke(
                                width = 10f
                            )
                        )
                    }

                    val sampleBounds = calculateBoundsWithStroke(samplePaths, sampleStrokeWidth)?.inset(sampleStrokeWidth / 2f)

                    val sampleBitmap = createBitmap(canvasHeight.toInt(), canvasWidth.toInt())
                    val canvas = android.graphics.Canvas(sampleBitmap)
                    val redPaint = Paint().apply {
                        color = android.graphics.Color.RED
                        style = Paint.Style.STROKE
                        strokeWidth = sampleStrokeWidth
                        isAntiAlias = true
                    }

                    val bluePaint = Paint().apply {
                        color = android.graphics.Color.BLUE
                        style = Paint.Style.STROKE
                        strokeWidth = sampleStrokeWidth
                        isAntiAlias = true
                    }

                    val normalizedMatrix = Matrix().apply {
                        setTranslate(-sampleBounds!!.left, -sampleBounds!!.top)
                        postScale(scaleFactor, scaleFactor, 0f, 0f)
                    }

                    val normalizedSample = sampleBounds!!.toAndroidRectF().transform(normalizedMatrix)
                    canvas.drawRect(normalizedSample, redPaint)

                    samplePaths.fastForEach { samplePath ->
                        samplePath.transform(normalizedMatrix)
                        canvas.drawPath(samplePath, bluePaint)
                    }

                    coroutineScope.launch(Dispatchers.IO) {
                        saveBitmapToFile(context, "sample_drawing.png", sampleBitmap)
                    }

                    val userBitmap = createBitmap(canvasHeight.toInt(), canvasWidth.toInt())
                    val userCanvas = android.graphics.Canvas(userBitmap)
                    val userBoundsWidth = userBounds!!.width
                    val userBoundsHeight = userBounds!!.height

                    val userScaleX = canvasWidth / userBoundsWidth
                    val userScaleY = canvasHeight / userBoundsHeight
                    val userScaleFactor = minOf(userScaleX, userScaleY)

                    val normalizedUserMatrix = Matrix().apply {
                        setTranslate(-userBounds!!.left, -userBounds!!.top)
                        postScale(userScaleFactor, userScaleFactor, 0f, 0f)
                    }
                    val normalizedUserDrawing = userBounds!!.toAndroidRectF().transform(normalizedUserMatrix)
                    userCanvas.drawRect(normalizedUserDrawing, redPaint)

                    paths.fastForEach { userPath ->
                        val path = getComposePath(userPath.path).asAndroidPath()
                        path.transform(normalizedUserMatrix)
                        userCanvas.drawPath(path, bluePaint)
                    }
                    currentPath?.let { currentPath ->
                        val path = getComposePath(currentPath.path).asAndroidPath()
                        path.transform(normalizedUserMatrix)
                        userCanvas.drawPath(path, bluePaint)
                    }

                    coroutineScope.launch(Dispatchers.IO) {
                        println("Saving user drawing")
                        saveBitmapToFile(context, "user_drawing.png", userBitmap)
                    }
                }
            }

            // Draw User Drawing
            withTransform(
                {
                    if (userBounds != null) {
                        val userWidth = userBounds.width
                        val userHeight = userBounds.height
                        val userScaleX = canvasWidth / userWidth
                        val userScaleY = canvasHeight / userHeight

                        val userScaleFactor = minOf(userScaleX, userScaleY)

                        scale(userScaleFactor, Offset(0f, 0f))
                        translate(-userBounds.left, -userBounds.top)
                    }
                }
            ) {
                userBounds?.let {
                    drawRect(
                        color = Color.Green,
                        topLeft = it.topLeft,
                        size = it.size,
                        style = Stroke(
                            width = 10f
                        )
                    )
                }

                paths.fastForEach { pathData ->
                    drawPath(
                        path = pathData.path,
                        color = pathData.color,
                        thickness = userStrokeWidth
                    )
                }
                currentPath?.let {
                    drawPath(
                        path = it.path,
                        color = it.color,
                        thickness = userStrokeWidth
                    )
                }
            }
        }
    }
}

private fun saveBitmapToFile(context: Context, fileName: String, bitmap: Bitmap) {
    try {
        // Use context.filesDir to get the app's internal storage directory
        val directory = context.filesDir
        val file = File(directory, fileName)
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            println("Bitmap saved to ${file.absolutePath}")
            // Optionally, you can trigger a UI update or show a message here
        }
    } catch (e: IOException) {
        e.printStackTrace()
        println("Error saving bitmap: ${e.message}")
        // Optionally, handle the error in the UI
    }
}

private fun getOutputDirectory(context: Context): File {
    val mediaDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.let {
        File(it, "MyAppImages").apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists())
        mediaDir else context.filesDir
}

@Composable
fun Dp.toPx() = with(LocalDensity.current) { this@toPx.toPx() }

fun Rect.inset(insetPx: Float): Rect {
    return Rect(
        left = this.left - insetPx,
        top = this.top - insetPx,
        right = this.right + insetPx,
        bottom = this.bottom + insetPx
    )
}

fun calculateBoundsWithStroke(paths: List<android.graphics.Path>, strokeWidth: Float): Rect? {
    if (paths.isEmpty()) {
        return null
    }

    val strokeWidthPx = strokeWidth
    var minLeft = Float.POSITIVE_INFINITY
    var minTop = Float.POSITIVE_INFINITY
    var maxRight = Float.NEGATIVE_INFINITY
    var maxBottom = Float.NEGATIVE_INFINITY

    for (path in paths) {
        val pathBounds = path.asComposePath().getBounds()
        minLeft = minOf(minLeft, pathBounds.left)
        minTop = minOf(minTop, pathBounds.top)
        maxRight = maxOf(maxRight, pathBounds.right)
        maxBottom = maxOf(maxBottom, pathBounds.bottom)
    }

    if (minLeft == Float.POSITIVE_INFINITY) return null // Handle empty paths case

    // Expand the bounds to account for the stroke width
    return Rect(
        left = minLeft - strokeWidthPx / 2f,
        top = minTop - strokeWidthPx / 2f,
        right = maxRight + strokeWidthPx / 2f,
        bottom = maxBottom + strokeWidthPx / 2f
    )
}

fun DrawScope.drawPath(
    path: List<Offset>,
    color: Color,
    thickness: Float = 5f,
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

fun getComposePath(path: List<Offset>): Path {
    return Path().apply {
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
}


@Preview
@Composable
private fun DrawingScreenPreview() {
    DrawingScreen(
        state = DrawingState()
    )
}