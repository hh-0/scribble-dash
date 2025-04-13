package dev.haihuynh.scribbledash

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DrawingState(
    val selectedColor: Color = Color.Black,
    val currentPath: PathData? = null,
    val paths: List<PathData> = emptyList(),
    val undoPaths: List<PathData> = emptyList()
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
    data object OnClearCanvasClick: DrawingAction
}

class DrawingViewModel: ViewModel() {
    private val _state = MutableStateFlow(DrawingState())
    val state = _state.asStateFlow()

    fun onAction(action: DrawingAction) {
        when(action) {
            DrawingAction.OnClearCanvasClick -> onClearCanvasClick()
            is DrawingAction.OnColorSelected -> onColorSelected(action.color)
            is DrawingAction.OnDraw -> onDraw(action.offset)
            DrawingAction.OnNewPathStart -> onNewPathStart()
            DrawingAction.OnPathEnd -> onPathEnd()
            DrawingAction.OnRedo -> onRedo()
            DrawingAction.OnUndo -> onUndo()
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
                    path = emptyList()
                )
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

    private fun onClearCanvasClick() {
        _state.update {
            it.copy(
                currentPath = null,
                paths = emptyList()
            )
        }
    }
}