package dev.haihuynh.scribbledash.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.haihuynh.scribbledash.R
import dev.haihuynh.scribbledash.ui.theme.BagelFatOne

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScribbleDashTopAppBar() {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = Color.Transparent
        ),
        title = {
            Text(
                text = "ScribbleDash",
                fontSize = 26.sp,
                fontFamily = BagelFatOne
            )
        }
    )
}

@Composable
fun ScribbleDashTopAppBarExitButton(
    onExit: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(
            onClick = {
                onExit()
            }
        ) {
            Image(
                painter = painterResource(R.drawable.exit_icon),
                contentDescription = null
            )
        }
    }
}