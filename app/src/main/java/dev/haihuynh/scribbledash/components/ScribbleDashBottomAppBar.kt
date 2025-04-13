package dev.haihuynh.scribbledash.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material.icons.rounded.InsertChart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.haihuynh.scribbledash.R

@Composable
fun ScribbleDashBottomAppBar() {
    BottomAppBar(
        containerColor = Color.White,
        actions = {
            IconButton(
                onClick = { },
                modifier = Modifier.weight(1f)
            ) {
                Image(
                    painter = painterResource(R.drawable.summary_icon),
                    contentDescription = "",
                    modifier = Modifier.size(32.dp),
                )
            }
            IconButton(
                onClick = { },
                Modifier.weight(1f)
            ) {
                Image(
                    painter = painterResource(R.drawable.home_icon),
                    contentDescription = "",
                    modifier = Modifier.size(32.dp),
                )
            }
        }
    )
}