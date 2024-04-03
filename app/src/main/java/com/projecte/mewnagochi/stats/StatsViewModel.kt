package com.projecte.mewnagochi.stats

import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class StatsViewModel() : ViewModel() {
    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    lateinit var healthPermissionLauncher: ManagedActivityResultLauncher<Set<String>, Set<String>>

//    init {
//        createHealthConnectRequest()
//        createHealthConnectCallback()
//        buildHealthConnectSettingsRequest()
//    }


    @Composable
    fun requestPermissions(healthConnectManager: HealthConnectManager, scope: CoroutineScope) {
        healthPermissionLauncher = rememberLauncherForActivityResult(
            contract = healthConnectManager.requestPermissionsActivityContract()
        ) { grantedPermissions: Set<String> ->
            val permissions = setOf(
                HealthPermission.getReadPermission(StepsRecord::class),
                HealthPermission.getReadPermission(WeightRecord::class),
                HealthPermission.getReadPermission(HeartRateRecord::class),
                HealthPermission.getReadPermission(ExerciseSessionRecord::class),
            )
            if (grantedPermissions.containsAll(permissions)) {
                Log.i(ContentValues.TAG, "Permissions already granted")
                getInitialData(scope, healthConnectManager)
            } else {
                //aqui hauria d'estar la comprovació de quants cops s'han demanat permisos
                // 1 cop -> snackbar explicant que es necessari -> tornar a demanar permisos
                // 2 cops -> snackbar explicant que sense permisos no es pot accedir a les dades -> accés a ajustes
                Log.i(ContentValues.TAG, "Permissions not granted")
                Log.i(ContentValues.TAG, "Requesting permissions again")

                scope.launch {
                    if (!healthConnectManager.hasAllPermissions(permissions)) {
                        healthPermissionLauncher.launch(permissions)
                    }
                }
            }
        }
    }

    fun getInitialData(scope: CoroutineScope, healthConnectManager: HealthConnectManager) {
        scope.launch {
            //Read all the records
            val stepsResponse = healthConnectManager.healthConnectClient.readRecords(
                request = ReadRecordsRequest<StepsRecord>(
                    timeRangeFilter = TimeRangeFilter.between(
                        LocalDateTime.of(
                            2024, 3, 1, 0, 0, 0
                        ), LocalDateTime.now()
                    )
                )
            )
            val weightResponse = healthConnectManager.healthConnectClient.readRecords(
                request = ReadRecordsRequest<WeightRecord>(
                    timeRangeFilter = TimeRangeFilter.between(
                        LocalDateTime.of(
                            2024, 3, 1, 0, 0, 0
                        ), LocalDateTime.now()
                    )
                )
            )
            val heartRateResponse = healthConnectManager.healthConnectClient.readRecords(
                request = ReadRecordsRequest<HeartRateRecord>(
                    timeRangeFilter = TimeRangeFilter.between(
                        LocalDateTime.of(
                            2024, 3, 1, 0, 0, 0
                        ), LocalDateTime.now()
                    )
                )
            )
            val ExerciseSessionResponse = healthConnectManager.healthConnectClient.readRecords(
                request = ReadRecordsRequest<ExerciseSessionRecord>(
                    timeRangeFilter = TimeRangeFilter.between(
                        LocalDateTime.of(
                            2024, 3, 1, 0, 0, 0
                        ), LocalDateTime.now()
                    )
                )
            )
            //With all records, extract the last record of each type
            val steps = stepsResponse.records.last().count
            val weight = weightResponse.records.last().weight.inKilograms
            val heartRate = heartRateResponse.records.last().samples.last().beatsPerMinute
            val lastExerciseSession = ExerciseSessionResponse.records.last().endTime.toString()
            //Update the UI state accordingly
            _uiState.update { currentState ->
                currentState.copy(
                    steps = steps,
                    weight = weight,
                    heartRate = heartRate,
                    lastExerciseSession = lastExerciseSession
                )
            }
        }
    }

    fun checkPermissions(context: Context): Boolean {
        val stepsPerm = ActivityCompat.checkSelfPermission(
            context, HealthPermission.getReadPermission(StepsRecord::class)
        )
        val weightPerm = ActivityCompat.checkSelfPermission(
            context, HealthPermission.getReadPermission(WeightRecord::class)
        )
        val heartPerm = ActivityCompat.checkSelfPermission(
            context, HealthPermission.getReadPermission(HeartRateRecord::class)
        )
        val exercisePerm = ActivityCompat.checkSelfPermission(
            context, HealthPermission.getReadPermission(ExerciseSessionRecord::class)
        )
        return ((stepsPerm == PackageManager.PERMISSION_GRANTED) ||
                (weightPerm == PackageManager.PERMISSION_GRANTED) ||
                (heartPerm == PackageManager.PERMISSION_GRANTED) ||
                (exercisePerm == PackageManager.PERMISSION_GRANTED))
    }


}