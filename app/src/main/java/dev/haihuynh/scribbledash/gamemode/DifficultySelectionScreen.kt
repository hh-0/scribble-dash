package dev.haihuynh.scribbledash.gamemode

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.haihuynh.scribbledash.R
import dev.haihuynh.scribbledash.components.ScribbleDashTopAppBarExitButton

@Composable
fun DifficultySelectionScreenRoot() {
    DifficultySelectionScreen()
}

@Composable
private fun DifficultySelectionScreen() {
    Scaffold(
        topBar = {
            ScribbleDashTopAppBarExitButton(
                onExit = {

                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(96.dp))
            Text(
                text = "Start drawing!",
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = "Choose a difficulty setting",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(48.dp))
            GameLevel()
        }
    }
}

@Composable
private fun GameLevel(
    modifier: Modifier = Modifier,
    onLevelSelect: (String) -> Unit = {}
) {
    Row(
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.clickable(
                onClick = {
                    onLevelSelect("Beginner")
                }
            )
        ) {
            Box(
                modifier = Modifier.clip(CircleShape).size(128.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.beginner_level),
                    contentDescription = null
                )
            }
            Text(
                text = "Beginner",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium)
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .offset(y = (-16).dp)
                .clickable(onClick = {
                        onLevelSelect("Challenging")
                    }
                )
        ) {
            Box(
                modifier = Modifier.clip(CircleShape).size(128.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.challenging_level),
                    contentDescription = null
                )
            }
            Text(
                text = "Challenging",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium)
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.clickable(
                onClick = {
                    onLevelSelect("Master")
                }
            )
        ) {
            Box(
                modifier = Modifier.clip(CircleShape).size(128.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.master_level),
                    contentDescription = null
                )
            }
            Text(
                text = "Master",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium)
            )
        }
    }
}

@Preview
@Composable
private fun DifficultySelectionScreenPreview() {
    DifficultySelectionScreen()
}