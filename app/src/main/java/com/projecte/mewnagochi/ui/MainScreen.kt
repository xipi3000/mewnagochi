package com.projecte.mewnagochi.ui

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.projecte.mewnagochi.LabeledIcon
import com.projecte.mewnagochi.MyViewModel
import com.projecte.mewnagochi.R
import com.projecte.mewnagochi.StatsViewModel
import com.projecte.mewnagochi.health_connect.HealthConnectManager
import com.projecte.mewnagochi.ui.theme.Person
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime

val homeScreen: @Composable () -> Unit = {
    Person().Draw()
}

val person1 = Person()
@Composable
fun MainScreen(
    myViewModel: MyViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
    context: Context,
    scope: CoroutineScope,
    navigationBarItems : List<LabeledIcon> =listOf(
        LabeledIcon("Home", Icons.Filled.Home){

            person1.Draw()},
        LabeledIcon("Activities",  ImageVector.vectorResource(id = R.drawable.baseline_directions_run_24)) { ActivitiesScreen() },
        LabeledIcon("Chats",ImageVector.vectorResource(id = R.drawable.baseline_forum_24)) { ChatScreen() },
        LabeledIcon("Stats",ImageVector.vectorResource(id = R.drawable.baseline_directions_run_24)) { StatisticsScreen(context, scope) },
    )
) {
    @Composable
    fun MyBox() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Blue)
        )
    }

// Define a variable of type @Composable () -> Unit
    var myComposable: @Composable () -> Unit = { }

// Assign your Box composable to the variable
    myComposable = { MyBox() }

    val selectedItem by myViewModel.navigationBarSelected.collectAsState()

    Scaffold(bottomBar = {
        NavigationBar {
            navigationBarItems.forEachIndexed { index, item ->
                NavigationBarItem(icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label) },
                    selected = selectedItem == index,
                    onClick = {
                        if (selectedItem != index) {
                            myViewModel.setNewSelected(index)
                            navController.navigate(item.label)
                        }
                    })
            }
        }
    }) { scaffoldPadding ->
        Column(
            modifier = Modifier.padding(scaffoldPadding)
        ) {



            NavHost(
                navController = navController, startDestination = "Home"
            ) {


                navigationBarItems.forEach { item ->
                    composable(item.label) {
                        item.screen()
                    }
                }
            }

        }
    }

}




@Composable
fun ActivitiesScreen() {
    Text(text = "ACTIVITIES")
}

@Composable
fun ChatScreen() {
    Text(text = "CHAT")
}

@Composable
fun StatisticsScreen(context: Context, scope: CoroutineScope) {
    val healthConnectManager by lazy {
        HealthConnectManager(context)
    }
    val myViewModel = StatsViewModel()
    myViewModel.healthPermissionLauncher = rememberLauncherForActivityResult(
        contract = healthConnectManager.requestPermissionsActivityContract()
    ) { grantedPermissions: Set<String> ->
        if (grantedPermissions.contains(HealthPermission.getReadPermission(StepsRecord::class))) {
            Log.i(ContentValues.TAG, "Permission granted")
            scope.launch {
                myViewModel.response = healthConnectManager.healthConnectClient.readRecords(
                    request = ReadRecordsRequest<StepsRecord>(
                        timeRangeFilter = TimeRangeFilter.between(
                            LocalDateTime.of(
                                2024, 3, 1, 0, 0, 0
                            ), LocalDateTime.now()
                        )
                    )
                )
            }
        } else {
            Log.i(ContentValues.TAG, "Permission not granted")
            Log.i(ContentValues.TAG, "Requesting permission again")
            myViewModel.healthPermissionLauncher.launch(
                setOf(
                    HealthPermission.getReadPermission(
                        StepsRecord::class
                    )
                )
            )
        }
    }
    StatsScreen(context = context, healthConnectManager = healthConnectManager, scope = scope)
    myViewModel.healthPermissionLauncher.launch(
        setOf(
            HealthPermission.getReadPermission(
                StepsRecord::class
            )
        )
    )
}
