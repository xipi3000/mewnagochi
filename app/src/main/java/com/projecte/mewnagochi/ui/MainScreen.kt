package com.projecte.mewnagochi.ui

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.projecte.mewnagochi.LabeledIcon
import com.projecte.mewnagochi.MyViewModel
import com.projecte.mewnagochi.stats.HealthConnectAvailability
import com.projecte.mewnagochi.stats.HealthConnectManager
import com.projecte.mewnagochi.stats.StatsViewModel
import com.projecte.mewnagochi.ui.store.StoreScreen
import kotlinx.coroutines.CoroutineScope


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    loginViewModel: LoginViewModel = viewModel(),
    myViewModel: MyViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
    context: Context,
    activity: Activity,
    scope: CoroutineScope,
    navigationBarItems: List<LabeledIcon> = listOf(
        LabeledIcon("Home", Icons.Filled.Home) {
            HomeScreen()
        },
        LabeledIcon("Stats", Icons.Filled.Info) {
            StatisticsScreen(context, scope, activity)
        },
        LabeledIcon("Store", Icons.Filled.ShoppingCart) {
            StoreScreen()
        },

    )
) {
    val loggedUser by loginViewModel.uiState
    val selectedItem by myViewModel.navigationBarSelected.collectAsState()
    Scaffold(
        topBar = {
                 TopAppBar(title = {
                     Text(text = loggedUser.email)
                 })
        },

        bottomBar = {
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
                navController = navController, startDestination = "login"
            ) {
                navigationBarItems.forEach { item ->
                    composable(item.label) {
                        item.screen()
                    }
                }
                composable("login"){
                    LoginScreen(
                        onLoginFinished = {navController.navigate("Home")},
                        onRegister = {navController.navigate("register")},
                    )
                }
                composable("register"){
                    RegisterScreen()
                    {
                        navController.navigate("login")
                    }
                }
            }

        }
    }
}

@Composable
fun StatisticsScreen(context: Context, scope: CoroutineScope, activity: Activity) {
    val healthConnectManager by lazy {
        HealthConnectManager(context)
    }
    val statsViewModel = StatsViewModel()
    val snackbarHostState = SnackbarHostState()

    //initialization of healthPermissionLauncher
    statsViewModel.healthPermissionLauncher =
        rememberLauncherForActivityResult(contract = healthConnectManager.requestPermissionsActivityContract(),
            onResult = { grantedPermissions: Set<String> ->
                statsViewModel.onPermissionResult(
                    healthConnectManager,
                    scope,
                    context,
                    grantedPermissions,
                    snackbarHostState,
                    activity
                )
            })
    StatsScreen(statsViewModel = statsViewModel, snackbarHostState, scope, healthConnectManager)
    LaunchedEffect(true) {
        //if no permisions -> request
        if (!statsViewModel.checkPermissions(context) && !(healthConnectManager.availability.value == HealthConnectAvailability.NOT_SUPPORTED ||
                    healthConnectManager.availability.value == HealthConnectAvailability.NOT_INSTALLED)) {
            Log.i("permission", "first request")
            statsViewModel.requestPermissions(context = context)
        } else if (!(healthConnectManager.availability.value == HealthConnectAvailability.NOT_SUPPORTED ||
                    healthConnectManager.availability.value == HealthConnectAvailability.NOT_INSTALLED)){
            statsViewModel.getData(scope, healthConnectManager)
        }
    }
}


