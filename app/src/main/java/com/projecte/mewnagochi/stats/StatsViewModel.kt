package com.projecte.mewnagochi.stats

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.health.connect.client.HealthConnectClient
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

    lateinit var healthPermissionLauncher: ActivityResultLauncher<Set<String>>
//    init {
//        createHealthConnectRequest()
//        createHealthConnectCallback()
//        buildHealthConnectSettingsRequest()
//    }

    fun showSnackbar(
        mainText: String,
        actionText: String,
        snackbarHostState: SnackbarHostState,
        scope: CoroutineScope,
        function: () -> Unit
    ) {
        scope.launch {
            val snackbarResult = snackbarHostState.showSnackbar(
                message = mainText,
                actionLabel = actionText,
            )
            when (snackbarResult) {
                SnackbarResult.Dismissed -> {
                    Log.i(ContentValues.TAG, "SnackbarIgnored")
                }
                SnackbarResult.ActionPerformed -> {
                    Log.i(ContentValues.TAG, "SnackbarAction")
                    //Each snackbar will have it's own action defined below it
                    function()
                }
            }
        }
    }

    fun onPermissionResult(healthConnectManager: HealthConnectManager, scope: CoroutineScope, context: Context, grantedPermissions : Set<String>, snackbarHostState: SnackbarHostState, activity: Activity){
        Log.i("permission", "entered onResult")
        val permissions = setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getReadPermission(WeightRecord::class),
            HealthPermission.getReadPermission(HeartRateRecord::class),
            HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        )
        if (grantedPermissions.containsAll(permissions)) {
            Log.i("permission", "Permissions already granted")
            getInitialData(scope, healthConnectManager)
        } else {
            val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                activity, HealthPermission.getReadPermission(StepsRecord::class)
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                activity, HealthPermission.getReadPermission(WeightRecord::class)
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                activity, HealthPermission.getReadPermission(HeartRateRecord::class)
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                activity, HealthPermission.getReadPermission(ExerciseSessionRecord::class)
            )
            if (!shouldProvideRationale){
                // 2 cops -> snackbar explicant que sense permisos no es pot accedir a les dades -> accés a ajustes
                Log.i("permission", "Can't request again, going to settings")
                showSnackbar(
                    "If you want to use this functionality, you need to grant all permissions.",
                    "Go to settings",
                    snackbarHostState = snackbarHostState,
                    scope
                ) {
                    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        Intent(android.health.connect.HealthConnectManager.ACTION_MANAGE_HEALTH_PERMISSIONS)
                            .putExtra(
                                Intent.EXTRA_PACKAGE_NAME,
                                context.packageName
                            )
                    } else {
                        Intent(
                            HealthConnectClient.ACTION_HEALTH_CONNECT_SETTINGS
                        )
                    }
                    startActivity(context, intent, null)
                }
            }else{
                // 1 cop -> snackbar explicant que es necessari -> tornar a demanar permisos
                Log.i("permission", "Requesting permissions again")
                showSnackbar(
                    "All permissions are needed to use this functionality.",
                    "Request again",
                    snackbarHostState = snackbarHostState,
                    scope
                ) {
                    scope.launch {
                        if (!healthConnectManager.hasAllPermissions(permissions)) {
                            healthPermissionLauncher.launch(permissions)
                        }
                    }
                }
            }
        }
    }


    fun requestPermissions(context: Context) {
        if (!checkPermissions(context)){
            healthPermissionLauncher.launch(
                setOf(
                    HealthPermission.getReadPermission(StepsRecord::class),
                    HealthPermission.getReadPermission(WeightRecord::class),
                    HealthPermission.getReadPermission(HeartRateRecord::class),
                    HealthPermission.getReadPermission(ExerciseSessionRecord::class),
                )
            )
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