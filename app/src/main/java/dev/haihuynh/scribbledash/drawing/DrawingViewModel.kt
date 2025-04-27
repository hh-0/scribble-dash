package dev.haihuynh.scribbledash.drawing

import android.content.Context
import android.content.res.XmlResourceParser
import android.graphics.Paint
import android.graphics.Path
import androidx.annotation.DrawableRes
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidPath
import androidx.core.graphics.PathParser
import androidx.core.graphics.createBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import kotlin.math.abs

data class DrawingState(
    val selectedColor: Color = Color.Black,
    val currentPath: PathData? = null,
    val paths: List<PathData> = emptyList(),
    val undoPaths: List<PathData> = emptyList(),
    val samplePaths: List<Path> = emptyList(),
    val displaySample: Boolean = true,
    val canvasWidth: Int = 0,
    val canvasHeight: Int = 0
)

data class PathData(
    val id: String,
    val color: Color,
    val path: List<Offset>
)

sealed interface DrawingAction {
    data object OnNewPathStart: DrawingAction
    data class OnDraw(val offset: Offset): DrawingAction
    data object OnPathEnd: DrawingAction
    data class OnColorSelected(val color: Color): DrawingAction
    data object OnUndo: DrawingAction
    data object OnRedo: DrawingAction
    data object OnDone: DrawingAction
    data object OnReady: DrawingAction
}

fun List<PathData>.asComposePaths(): List<androidx.compose.ui.graphics.Path> {
    return this.map { pathData ->
        val path = pathData.path
        androidx.compose.ui.graphics.Path().apply {
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
}


class DrawingViewModel: ViewModel() {
    private val _state = MutableStateFlow(DrawingState())
    val state = _state.asStateFlow()

    fun onAction(action: DrawingAction) {
        when(action) {
            DrawingAction.OnDone -> onDone(state.value.canvasWidth, state.value.canvasHeight)
            is DrawingAction.OnColorSelected -> onColorSelected(action.color)
            is DrawingAction.OnDraw -> onDraw(action.offset)
            DrawingAction.OnNewPathStart -> onNewPathStart()
            DrawingAction.OnPathEnd -> onPathEnd()
            DrawingAction.OnRedo -> onRedo()
            DrawingAction.OnUndo -> onUndo()
            DrawingAction.OnReady -> onReady()
        }
    }

    private fun onUndo() {
        if (state.value.paths.isEmpty()) return

        val droppedPath = state.value.paths.last()
        val updatedPaths = state.value.paths.dropLast(1)
        val undoPaths = if (state.value.undoPaths.size > 4) {
            state.value.undoPaths.drop(1) + droppedPath
        } else {
            state.value.undoPaths + droppedPath
        }

        _state.update {
            it.copy(
                currentPath = null,
                paths = updatedPaths,
                undoPaths = undoPaths
            )
        }
    }

    private fun onRedo() {
        if (state.value.undoPaths.isEmpty()) return

        val redoPath = state.value.undoPaths.last()
        val updatedPaths = state.value.paths + redoPath
        val undoPaths = state.value.undoPaths.dropLast(1)

        _state.update {
            it.copy(
                currentPath = null,
                paths = updatedPaths,
                undoPaths = undoPaths
            )
        }
    }

    private fun onPathEnd() {
        val currentPathData = state.value.currentPath ?: return
        _state.update {
            it.copy(
                currentPath = null,
                paths = it.paths + currentPathData
            )
        }
    }

    private fun onNewPathStart() {
        _state.update {
            it.copy(
                currentPath = PathData(
                    id = System.currentTimeMillis().toString(),
                    color = it.selectedColor,
                    path = emptyList(),
                ),
                undoPaths = emptyList()
            )
        }
    }

    private fun onDraw(offset: Offset) {
        val currentPathData = state.value.currentPath ?: return
        _state.update {
            it.copy(
                currentPath = currentPathData.copy(
                    path = currentPathData.path + offset
                )
            )
        }
    }

    private fun onColorSelected(color: Color) {

    }

    private fun onDone(canvasWidth: Int, canvasHeight: Int) {
        val sampleBounds = getDrawingBounds(state.value.samplePaths.asComposePaths())
        if (sampleBounds == null) {
            return // This should not happen, but just in case, we can ask user to try again
        }

        val userBounds = getDrawingBounds(state.value.paths.asComposePaths())
        if (userBounds == null) {
            return // This should not happen, but just in case, we can ask user to try again
        }

        val sampleBitmap = createBitmap(canvasWidth, canvasHeight)
        val sampleCanvas = android.graphics.Canvas(sampleBitmap)
        val sampleScaleX = canvasWidth / sampleBounds.width
        val sampleScaleY = canvasHeight / sampleBounds.height
        val sampleScaleFactor = minOf(sampleScaleX, sampleScaleY)
        val samplePaint = Paint().apply {
            color = android.graphics.Color.BLUE
            style = Paint.Style.STROKE
            strokeWidth = 20f
        }

        val sampleNormalizationMatrix = android.graphics.Matrix().apply {
            setTranslate(-sampleBounds.left, -sampleBounds.top)
            postScale(sampleScaleFactor, sampleScaleFactor, 0f, 0f)
        }

        state.value.samplePaths.forEach { samplePath ->
            samplePath.transform(sampleNormalizationMatrix)
            sampleCanvas.drawPath(samplePath, samplePaint)
        }

        val userBitmap = createBitmap(canvasWidth, canvasHeight)
        val userCanvas = android.graphics.Canvas(userBitmap)
        val userScaleX = canvasWidth / userBounds.width
        val userScaleY = canvasHeight / userBounds.height
        val userScaleFactor = minOf(userScaleX, userScaleY)
        val userPaint = Paint().apply {
            color = android.graphics.Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 10f
        }
        val userNormalizationMatrix = android.graphics.Matrix().apply {
            setTranslate(-userBounds.left, -userBounds.top)
            postScale(userScaleFactor, userScaleFactor, 0f, 0f)
        }

        state.value.paths.asComposePaths().forEach { userPath ->
            val androidUserPath = userPath.asAndroidPath()
            androidUserPath.transform(userNormalizationMatrix)
            userCanvas.drawPath(androidUserPath, userPaint)
        }

        var matchingUserPixelCount = 0
        var visibleUserPixelCount = 0
        var userBitmapPixels = IntArray(userBitmap.width * userBitmap.height)
        var sampleBitmapPixels = IntArray(sampleBitmap.width * sampleBitmap.height)

        sampleBitmap.getPixels(sampleBitmapPixels, 0, sampleBitmap.width, 0, 0, sampleBitmap.width, sampleBitmap.height)
        userBitmap.getPixels(userBitmapPixels, 0, userBitmap.width, 0, 0, userBitmap.width, userBitmap.height)

        viewModelScope.launch(Dispatchers.Default) {
            for (i in 0..userBitmapPixels.size - 1) {
                if (userBitmapPixels[i] != 0) {
                    visibleUserPixelCount++
                }
                if (userBitmapPixels[i] != 0 && sampleBitmapPixels[i] != 0) {
                    matchingUserPixelCount++
                }

            }
        }

        _state.update {
            it.copy(
                currentPath = null,
                paths = emptyList(),
                undoPaths = emptyList()
            )
        }
    }

    private fun onReady() {
        _state.update {
            it.copy(
                displaySample = false
            )
        }
    }

    fun loadSampleDrawing(context: Context, @DrawableRes resourceId: Int) {
        val rawPaths = getPathDataFromVectorDrawable(context, resourceId)
        val samplePaths = createPathsFromPathData(rawPaths)
        _state.update {
            it.copy(
                samplePaths = samplePaths
            )
        }
    }

    fun setUpCanvas(width: Int, height: Int) {
        _state.update {
            it.copy(
                canvasWidth = width,
                canvasHeight = height
            )
        }
    }

    private fun getPathDataFromVectorDrawable(context: Context, drawableResId: Int): List<String> {
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
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            parser.close()
        }
        return pathDataList
    }

    private fun createPathsFromPathData(pathDataList: List<String>): List<Path> {
        val paths = mutableListOf<Path>()
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
}