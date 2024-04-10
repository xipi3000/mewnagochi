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
    lateinit var myHealthConnectManager: HealthConnectManager
    lateinit var myScope: CoroutineScope
    lateinit var myContext: Context


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

    fun onPermissionResult(
        healthConnectManager: HealthConnectManager,
        scope: CoroutineScope,
        context: Context,
        grantedPermissions: Set<String>,
        snackbarHostState: SnackbarHostState,
        activity: Activity
    ) {
        myScope = scope
        myHealthConnectManager = healthConnectManager
        myContext = context
        Log.i("permission", "entered onResult")
        val permissions = setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getReadPermission(WeightRecord::class),
            HealthPermission.getReadPermission(HeartRateRecord::class),
            HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        )
        if (grantedPermissions.containsAll(permissions)) {
            Log.i("permission", "Permissions already granted")
            getData(scope, healthConnectManager)
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
            if (!shouldProvideRationale) {
                // 2 cops -> snackbar explicant que sense permisos no es pot accedir a les dades -> accés a ajustes
                Log.i("permission", "Can't request again, going to settings")
                showSnackbar(
                    "If you want to use this functionality, you need to grant all permissions.",
                    "Go to settings",
                    snackbarHostState = snackbarHostState,
                    scope
                ) {
                    val intent =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
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
            } else {
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
        myContext = context
        if (!checkPermissions(context)) {
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

    fun getData(scope: CoroutineScope, healthConnectManager: HealthConnectManager) {
        //In this method, we'll see first the data requests, and then the parsing of the queries
        scope.launch {
            /** STEP 1: Obtain steps info **/
            //Request steps record (only the ones recorded today)
            val stepsResponse = healthConnectManager.healthConnectClient.readRecords(
                request = ReadRecordsRequest<StepsRecord>(
                    //today: between 00:00 and now
                    timeRangeFilter = TimeRangeFilter.between(
                        LocalDateTime.now().withHour(0).withMinute(0).withSecond(0),
                        LocalDateTime.now()
                    )
                )
            )
            //Since we made a good query, we just need to sum all steps, no need to check for day
            var steps = 0L
            for (record in stepsResponse.records) {
                steps += record.count
            }

            /** STEP 2: Obtain weight info **/
            //Request weight records (keep requesting until finding a record)
            lateinit var weightResponse: androidx.health.connect.client.response.ReadRecordsResponse<WeightRecord>
            var rightNow = LocalDateTime.now()
            var firstOfTheMonth = rightNow.withHour(0).withMinute(0).withSecond(0).withDayOfMonth(1)
            do {
                weightResponse = healthConnectManager.healthConnectClient.readRecords(
                    request = ReadRecordsRequest<WeightRecord>(
                        timeRangeFilter = TimeRangeFilter.between(firstOfTheMonth, rightNow)
                    )
                )
                rightNow = firstOfTheMonth
                firstOfTheMonth = firstOfTheMonth.minusMonths(1)
            } while (weightResponse.records.isEmpty() && firstOfTheMonth.year > 2020)
            //For the weight, we just need the most recent record (if any has been found)
            val weight = if (weightResponse.records.isNotEmpty()) weightResponse.records.last().weight.inKilograms else 0.0

            /** STEP 3: Obtain HeartBeat info **/
            //Request heart rate records (keep requesting until finding a record)
            lateinit var heartRateResponse: androidx.health.connect.client.response.ReadRecordsResponse<HeartRateRecord>
            rightNow = LocalDateTime.now()
            firstOfTheMonth = rightNow.withHour(0).withMinute(0).withSecond(0).withDayOfMonth(1)
            do{
                heartRateResponse = healthConnectManager.healthConnectClient.readRecords(
                    request = ReadRecordsRequest<HeartRateRecord>(
                        timeRangeFilter = TimeRangeFilter.between(firstOfTheMonth, rightNow))
                )
                rightNow = firstOfTheMonth
                firstOfTheMonth = firstOfTheMonth.minusMonths(1)
            } while(heartRateResponse.records.isEmpty() && firstOfTheMonth.year > 2020)
            //Same for the heart rate (if any has been found)
            val heartRate = if (heartRateResponse.records.isNotEmpty()) heartRateResponse.records.last().samples.last().beatsPerMinute else 0L

            /** STEP 4: Obtain last ExerciseSession info **/
            //Request exercise session records (we'll just need the most recent one)
            lateinit var ExerciseSessionResponse: androidx.health.connect.client.response.ReadRecordsResponse<ExerciseSessionRecord>
            rightNow = LocalDateTime.now()
            firstOfTheMonth = rightNow.withHour(0).withMinute(0).withSecond(0)
            do {
                ExerciseSessionResponse = healthConnectManager.healthConnectClient.readRecords(
                    request = ReadRecordsRequest<ExerciseSessionRecord>(
                        timeRangeFilter = TimeRangeFilter.between(firstOfTheMonth, rightNow)
                    )
                )
                rightNow = firstOfTheMonth
                firstOfTheMonth = firstOfTheMonth.minusMonths(1)
            }while (ExerciseSessionResponse.records.isEmpty() && firstOfTheMonth.year > 2020)
            //For the exercise session, we just need the most recent one
            val lastExerciseSession = if (ExerciseSessionResponse.records.isNotEmpty()) parseLastExerciseSession(ExerciseSessionResponse.records.last().endTime.toString()) else "No exercise sessions found"

            /** STEP 5: Update data holders **/
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

    private fun parseLastExerciseSession(lastSession: String): String {
        val year = lastSession.substring(0, 4)
        val month = lastSession.substring(5, 7)
        val day = lastSession.substring(8, 10)
        val hour = lastSession.substring(11, 13)
        val minute = lastSession.substring(14, 16)
        return "$hour:$minute at $day/$month/${year.substring(2, 4)}"
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