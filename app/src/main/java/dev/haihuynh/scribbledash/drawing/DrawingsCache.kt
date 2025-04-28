package dev.haihuynh.scribbledash.drawing

import androidx.compose.ui.graphics.Path

object DrawingsCache {
    lateinit var sampleDrawing: List<Path>
    lateinit var userDrawing: List<Path>
    var accuracy: Int = 0
}