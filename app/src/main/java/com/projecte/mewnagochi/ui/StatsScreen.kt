package com.projecte.mewnagochi.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.projecte.mewnagochi.stats.StatsViewModel
import java.util.Locale

@Composable
fun StatsScreen(
    statsViewModel: StatsViewModel,
    snackbarHostState: SnackbarHostState
) {
    val statsUiState by statsViewModel.uiState.collectAsState()
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier.padding(scaffoldPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = String.format(
                    Locale.ENGLISH, "Steps: %s", if (statsUiState.steps == 0L) "N/A" else statsUiState.steps
                )
            )
            Text(
                text = String.format(
                    Locale.ENGLISH, "Weight: %s kgs", if (statsUiState.weight == 0.0) "N/A" else statsUiState.weight
                )
            )
            Text(
                text = String.format(
                    Locale.ENGLISH, "Heart Rate: %s bpm", if (statsUiState.heartRate == 0L) "N/A" else statsUiState.heartRate
                )
            )
            Text(
                text = String.format(
                    Locale.ENGLISH, "Last Exercise Session: %s", statsUiState.lastExerciseSession
                )
            )
        }
    }
}
