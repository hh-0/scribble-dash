package dev.haihuynh.scribbledash

import android.content.Context
import android.content.res.XmlResourceParser
import android.graphics.Path
import android.graphics.PathIterator
import android.graphics.PathMeasure
import android.graphics.RectF
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.PathParser
import dev.haihuynh.scribbledash.drawing.drawPath
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException

@Composable
fun TestCanvasScreen() {
    val context = LocalContext.current

    val rawPath = getPathDataFromVectorDrawable(context, R.drawable.alien)
    val paths = createPathsFromPathData(rawPath)

    VectorPathsOnCanvas()

//    Canvas(
//        modifier = Modifier
//            .padding(64.dp)
//            .fillMaxSize()
//            .background(Color.LightGray)
//    ) {
//        paths.forEach { path ->
//            println("Drawing path ------")
//            val coordinates = getCoordinatesFromPath(path, getCoordinatesFromPath(path))
//
//            drawPath(
//                path = coordinates,
//                color = Color.Black
//            )
//        }
//    }
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

fun createPathsFromPathData(pathDataList: List<String>): List<Path> {
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


@Composable
fun VectorPathsOnCanvas() {
    val context = LocalContext.current
    val rawPath = getPathDataFromVectorDrawable(context, R.drawable.snowflake)
    val paths = createPathsFromPathData(rawPath)
    Canvas(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxSize()
    ) {
        paths.forEach { path ->
            drawPath(
                path = path.asComposePath(),
                color = Color.Black,
                style = Stroke(width = 3f)
            )
        }
    }
}

