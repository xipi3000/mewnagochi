package com.projecte.mewnagochi.ui

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.projecte.mewnagochi.LabeledIcon
import com.projecte.mewnagochi.MyViewModel
import com.projecte.mewnagochi.R
import com.projecte.mewnagochi.login.LoginScreen
import com.projecte.mewnagochi.login.LoginViewModel
import com.projecte.mewnagochi.login.RegisterScreen
import com.projecte.mewnagochi.login.User
import com.projecte.mewnagochi.login.forgotPassword.ForgotPasswordScreen
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

    Log.i("ROUTE",navController.currentDestination.toString())
    val selectedItem by myViewModel.navigationBarSelected.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val user by myViewModel.currentUser.collectAsState(initial = User())
    Scaffold(
        topBar = {
            if (navigationBarItems.any { it.label == currentRoute }) {
                TopAppBar(title = {
                    UserAppBar(
                        user=user.email,
                        numOfCoins = 100
                    )
                })
            }
        },

        bottomBar = {
            if (navigationBarItems.any { it.label == currentRoute }) {
                NavigationBar {
                    navigationBarItems.forEachIndexed { index, item ->
                        NavigationBarItem(icon = {
                            Icon(
                                item.icon,
                                contentDescription = item.label
                            )
                        },
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
                        onForgotPassword =  {navController.navigate("forgot_password")},
                    )
                }
                composable("register"){
                    RegisterScreen()
                    {
                        navController.navigate("login")
                    }
                }
                composable("forgot_password"){
                    ForgotPasswordScreen()
                    {
                        navController.navigate("login")
                    }
                }
            }

        }
    }
}
@Preview
@Composable
fun UserAppBar(user:String ="user@user.com", modifier: Modifier = Modifier,numOfCoins:Int=100) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = user.split("@")[0],
            style = MaterialTheme.typography.headlineLarge)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painter = painterResource(id = R.drawable.coins), contentDescription = "Coins",Modifier.size(60.dp))
            Text(text = numOfCoins.toString(),
                style = MaterialTheme.typography.headlineLarge)
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


