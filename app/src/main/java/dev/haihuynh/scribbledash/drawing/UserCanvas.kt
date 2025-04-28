package dev.haihuynh.scribbledash.drawing

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastForEach
import dev.haihuynh.scribbledash.components.DrawingCanvas
import dev.haihuynh.scribbledash.components.drawPath

@Composable
fun UserCanvas(
    modifier: Modifier = Modifier,
    paths: List<PathData>,
    currentPath: PathData?,
    onAction: (DrawingAction) -> Unit,
    onCanvasSetUp: (Int, Int) -> Unit,
) {
    DrawingCanvas(
        modifier = modifier,
        onAction = onAction,
    ) {
        // TODO: Refactor this getting width and height of canvas
        onCanvasSetUp(size.width.toInt(), size.height.toInt())

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
    }
}