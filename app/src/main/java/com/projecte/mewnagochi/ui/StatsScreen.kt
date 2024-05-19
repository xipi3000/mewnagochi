package com.projecte.mewnagochi.ui

import android.util.Log
import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.auth.User
import com.projecte.mewnagochi.services.storage.UserPreferences
import com.projecte.mewnagochi.stats.HealthConnectAvailability
import com.projecte.mewnagochi.stats.HealthConnectManager
import com.projecte.mewnagochi.stats.StatsViewModel
import java.util.Locale

@Composable
fun StatsScreen(
    statsViewModel: StatsViewModel,
    snackbarHostState: SnackbarHostState,
    healthConnectMannager: HealthConnectManager,
) {
    val statsUiState by statsViewModel.uiState.collectAsState()
    val userPreferences by statsViewModel.userPreferences.collectAsState(initial = UserPreferences())
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
    ) { scaffoldPadding ->
        Log.i("calla", scaffoldPadding.toString())

        if (healthConnectMannager.availability.value == HealthConnectAvailability.NOT_SUPPORTED ||
            healthConnectMannager.availability.value == HealthConnectAvailability.NOT_INSTALLED) {
            Text(
                text = "HealthConnect is not working for this device.",
                modifier = Modifier.padding(16.dp)
            )
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxHeight(1 / 4f)
                        .weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White, contentColor = Color.Black
                    ),
                    elevation = CardDefaults.cardElevation(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            //.fillMaxSize()
                            .padding(16.dp),
                    ) {
                        Text(
                            text = String.format(
                                Locale.ENGLISH,
                                "Steps: %s",
                                if (statsUiState.steps == 0L) "N/A" else statsUiState.steps
                            )
                        )
                    }
                }
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxHeight(1 / 4f)
                        .weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White, contentColor = Color.Black
                    ),
                    elevation = CardDefaults.cardElevation(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                    ) {
                        Text(
                            text = String.format(
                                Locale.ENGLISH,
                                "Weight: %s kgs",
                                if (statsUiState.weight == 0.0) "N/A" else statsUiState.weight
                            )
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxHeight(1 / 4f)
                        .weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White, contentColor = Color.Black
                    ),
                    elevation = CardDefaults.cardElevation(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                    ) {
                        Text(
                            text = String.format(
                                Locale.ENGLISH,
                                "Heart Rate: %s bpm",
                                if (statsUiState.heartRate == 0L) "N/A" else statsUiState.heartRate
                            )
                        )
                    }
                }
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxHeight(1 / 4f)
                        .weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White, contentColor = Color.Black
                    ),
                    elevation = CardDefaults.cardElevation(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                    ) {
                        Text(
                            text = String.format(
                                Locale.ENGLISH,
                                "Last Exercise Session: %s",
                                statsUiState.lastExerciseSession
                            )
                        )
                    }
                }
            }
            val stepsToFinishGoal = ((userPreferences?.currentSteps?:1).toFloat()/(userPreferences?.stepsGoal?:1).toFloat())
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Steps to finish goal",style=MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.size(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = (userPreferences?.currentSteps?:1).toString())

                    LinearProgressIndicator(
                        modifier = Modifier.padding(5.dp),
                        progress = (stepsToFinishGoal),

                        )
                    Text(text = (userPreferences?.stepsGoal?:1).toString())
                }
                if(stepsToFinishGoal>=1){
                    Text(text = "You reached your goal!", color = MaterialTheme.colorScheme.onTertiaryContainer)
                }
            }
            

            Button(onClick = { statsViewModel.getData(healthConnectManager = healthConnectMannager) }) {
                Text(text = "Sync to cloud")
            }
        }
    }
}
