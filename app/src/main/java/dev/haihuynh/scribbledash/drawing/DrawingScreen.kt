package dev.haihuynh.scribbledash.drawing

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.XmlResourceParser
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
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.core.graphics.PathParser
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.haihuynh.scribbledash.R
import dev.haihuynh.scribbledash.components.GradientBackground
import dev.haihuynh.scribbledash.components.ScribbleDashTopAppBarExitButton
import dev.haihuynh.scribbledash.createPathsFromPathData
import dev.haihuynh.scribbledash.getPathDataFromVectorDrawable
import org.koin.androidx.compose.koinViewModel
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.math.abs
import androidx.compose.ui.platform.LocalDensity

@Composable
fun DrawingScreenRoot(
    onExit: () -> Unit = {},
    viewModel: DrawingViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val resources = context.resources
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
                    modifier = Modifier
                )
            }
        }
    }
}

@SuppressLint("ResourceType")
@Composable
private fun DrawingCanvas(
    paths: List<PathData>,
    currentPath: PathData?,
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
        val context = LocalContext.current
        val rawPath = getPathDataFromVectorDrawable(context, R.drawable.mountains)
        val samplePaths = createPathsFromPathData(rawPath)

        Canvas(
            modifier = modifier
                .padding(12.dp)
                .clip(RoundedCornerShape(16.dp))
                .clipToBounds()
                .border(
                    width = 1.dp,
                    color = Color.Black.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(16.dp)
                )
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
                }
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
        ) {

            val canvasHeight = size.height
            val canvasWidth = size.width





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



            val bounds = calculateBoundsWithStroke(samplePaths, 1f)?.inset(0f)
            val sampleAspectRatio = bounds!!.width / bounds.height
            val sampleHeight = bounds.height
            val sampleWidth = bounds.width
            bounds.left
            bounds.top
            val scaleX = canvasWidth / sampleWidth
            val scaleY = canvasHeight / sampleHeight

            val scaleFactor = minOf(scaleX, scaleY)

            val scaledDrawingWidth = sampleWidth * scaleFactor
            val scaledDrawingHeight = sampleHeight * scaleFactor

//            val centerX = (canvasWidth - scaledDrawingWidth) /2f
//            val centerY = (canvasHeight - scaledDrawingHeight)/ 2f

            println("Bounds height $sampleHeight | Bounds width $sampleWidth")
            println("Canvas Height $canvasHeight | Canvas Width $canvasWidth")
            println("Scale factor $scaleFactor | ratioX $scaleX | ratioY $scaleY")
            println("Scaled drawing height $scaledDrawingHeight | Scaled drawing width $scaledDrawingWidth")
            withTransform(
                {
                    // Centering
//                    val centerX = (size.width - scaledDrawingWidth)/ 2f
//                    val centerY = (size.height - scaledDrawingHeight)/ 2f
//                    println("Center X $centerX | Center Y $centerY")
//                    translate(centerX, centerY)
//
//                    scale(scaleFactor, Offset(0f, 0f))
//                    translate(-bounds!!.left, -bounds!!.top)

                    translate(-bounds.left * scaleFactor, -bounds.top * scaleFactor)
//                    scale(1f, 1f)
                    scale(scaleFactor, scaleFactor, Offset(0f, 0f))
                }

            ) {
                samplePaths.fastForEach { samplePath ->
                    drawPath(
                        path = samplePath.asComposePath(),
                        color = Color.Blue,
                        style = Stroke(
                            width = 2f,
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
                            width = 2f
                        )
                    )
                }

            }
//            translate(
//                left = -bounds!!.topLeft.x,
//                top = -bounds!!.topLeft.y,
//            ) {
//            }


        }
    }
}

@Composable
fun Dp.toPx() = with(LocalDensity.current) { this@toPx.toPx() }

fun Rect.inset(insetPx: Float): Rect {
    return Rect(
        left = this.left + insetPx,
        top = this.top + insetPx,
        right = this.right - insetPx,
        bottom = this.bottom - insetPx
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

fun calculateCombinedBounds(paths: List<android.graphics.Path>): Rect? {
    if (paths.isEmpty()) {
        return null
    }

    var minLeft = Float.POSITIVE_INFINITY
    var minTop = Float.POSITIVE_INFINITY
    var maxRight = Float.NEGATIVE_INFINITY
    var maxBottom = Float.NEGATIVE_INFINITY

    for (path in paths) {
        path.asComposePath()
        val pathBounds = path.asComposePath().getBounds()
        minLeft = minOf(minLeft, pathBounds.left)
        minTop = minOf(minTop, pathBounds.top)
        maxRight = maxOf(maxRight, pathBounds.right)
        maxBottom = maxOf(maxBottom, pathBounds.bottom)
    }

    return Rect(minLeft, minTop, maxRight, maxBottom)
}

//fun calculateCombinedBounds(paths: List<Path>): Rect? {
//    if (paths.isEmpty()) {
//        return null
//    }
//
//    var combinedBounds: Rect? = null
//
//    for (path in paths) {
//        val pathBounds = path.getBounds().
//        if (combinedBounds == null) {
//            combinedBounds = pathBounds
//        } else {
//            combinedBounds = combinedBounds.expandToInclude(pathBounds)
//        }
//    }
//
//    return combinedBounds
//}

fun DrawScope.drawPath(
    path: List<Offset>,
    color: Color,
    thickness: Float = 5f
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

fun getPathDataFromVectorDrawable(context: Context, drawableResId: Int): List<String> {
    val pathDataList = mutableListOf<String>()
    val parser: XmlResourceParser = context.resources.getXml(drawableResId)
    try {
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.name == "path") {
                // Namespace null might be needed depending on how you access attributes
                val pathData = parser.getAttributeValue("http://schemas.android.com/apk/res/android", "pathData")
                // Fallback if namespace doesn't work (less reliable)
                // val pathData = parser.getAttributeValue(null, "android:pathData")
                if (pathData != null) {
                    pathDataList.add(pathData)
                }
            }
            eventType = parser.next()
        }
    } catch (e: XmlPullParserException) {
        e.printStackTrace() // Handle errors appropriately
    } catch (e: IOException) {
        e.printStackTrace() // Handle errors appropriately
    } finally {
        parser.close()
    }
    return pathDataList
}

fun createPathsFromPathData(pathDataList: List<String>): List<android.graphics.Path> {
    val paths = mutableListOf<android.graphics.Path>()
    for (pathData in pathDataList) {
        try {
            val path = PathParser.createPathFromPathData(pathData)
            if (path != null) {
                val transformMatrix = android.graphics.Matrix()
                transformMatrix.setScale(3f, 3f)
                path.transform(transformMatrix)
                paths.add(path)
            } else {
                println("Warning: Could not parse pathData: $pathData")
            }
        } catch (e: Exception) {
            // PathParser can sometimes throw exceptions for malformed data
            println("Error parsing pathData '$pathData': ${e.message}")
        }
    }
    return paths
}

@Preview
@Composable
private fun DrawingScreenPreview() {
    DrawingScreen(
        state = DrawingState()
    )
}