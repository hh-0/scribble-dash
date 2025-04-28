package dev.haihuynh.scribbledash.drawing

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.util.fastForEach
import dev.haihuynh.scribbledash.components.DrawingCanvas
import dev.haihuynh.scribbledash.components.getDrawingBounds

@Composable
fun SampleCanvas(
    modifier: Modifier = Modifier,
    samplePaths: List<Path>
) {
    DrawingCanvas(
        modifier = modifier,
        onAction = { }
    ) {
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