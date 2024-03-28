package com.projecte.mewnagochi

import android.content.ContentValues.TAG
import com.projecte.mewnagochi.health_connect.HealthConnectManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.lifecycleScope
import com.projecte.mewnagochi.ui.StatsScreen
import com.projecte.mewnagochi.ui.theme.MewnagochiTheme
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class StatsActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myViewModel = StatsViewModel()
        setContent {
            val healthConnectManager by lazy {HealthConnectManager(this)}
            val scope = rememberCoroutineScope()
            myViewModel.healthPermissionLauncher = rememberLauncherForActivityResult(
                contract = healthConnectManager.requestPermissionsActivityContract()
            ) { grantedPermissions: Set<String> ->
                if (grantedPermissions.contains(HealthPermission.getReadPermission(StepsRecord::class))) {
                    Log.i(TAG, "Permission granted")
                    scope.launch {
                        myViewModel.response = healthConnectManager.healthConnectClient.readRecords(
                            request = ReadRecordsRequest<StepsRecord>(
                                timeRangeFilter = TimeRangeFilter.between(LocalDateTime.of(2024, 3, 1, 0,0,0), LocalDateTime.now())
                            )
                        )
                    }
                } else {
                    Log.i(TAG, "Permission not granted")
                    Log.i(TAG, "Requesting permission again")
                    myViewModel.healthPermissionLauncher.launch(setOf(HealthPermission.getReadPermission(StepsRecord::class)))
                }
            }


            MewnagochiTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    StatsScreen(context = this, healthConnectManager = healthConnectManager, scope = this.lifecycleScope)
                    Log.i(TAG, "Requesting permission")
                    myViewModel.healthPermissionLauncher.launch(setOf(HealthPermission.getReadPermission(StepsRecord::class)))
                }
            }
        }
    }
}