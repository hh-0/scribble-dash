package dev.haihuynh.scribbledash

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.haihuynh.scribbledash.components.ScribbleDashBottomAppBar
import dev.haihuynh.scribbledash.components.ScribbleDashTopAppBar
import dev.haihuynh.scribbledash.ui.theme.ScribbleDashTheme

@Composable
fun HomeScreenRoot() {
    HomeScreen()
}

@Composable
private fun HomeScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { ScribbleDashTopAppBar() },
        bottomBar = { ScribbleDashBottomAppBar() }
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
                text = "Select game mode",
                style = MaterialTheme.typography.titleLarge
            )
            GameMode(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun GameMode(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(
                8.dp,
                Color(0xFF0DD280),
                RoundedCornerShape(16.dp)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "One Round Wonder",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
                    .padding(vertical = 16.dp)
                    .padding(start = 24.dp)
            )
            Box(
                modifier = Modifier
            ) {
                Image(
                    painter = painterResource(R.drawable.one_round_wonder),
                    contentDescription = null
                )
            }
        }
    }
    GameLevel()
}

@Composable
private fun GameLevel(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
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
            verticalArrangement = Arrangement.Center
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
            verticalArrangement = Arrangement.Center
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
private fun HomeScreenPreview() {
    ScribbleDashTheme {
        HomeScreen()
    }
}