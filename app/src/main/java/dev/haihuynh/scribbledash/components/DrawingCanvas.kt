package dev.haihuynh.scribbledash.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import dev.haihuynh.scribbledash.drawing.DrawingAction
import kotlin.math.abs

@Composable
fun DrawingCanvas(
    modifier: Modifier = Modifier,
    onAction: (DrawingAction) -> Unit,
    content: DrawScope.() -> Unit
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
                    drawBackgroundGrid()
                }
        ) {
            content()
        }
    }
}

private fun DrawScope.drawBackgroundGrid() {
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

internal fun DrawScope.drawPath(
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

internal fun getDrawingBounds(paths: List<Path>): Rect? {
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