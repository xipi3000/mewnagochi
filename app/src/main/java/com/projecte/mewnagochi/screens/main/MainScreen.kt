package com.projecte.mewnagochi.screens.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.projecte.mewnagochi.R
import com.projecte.mewnagochi.screens.forgot_password.ForgotPasswordScreen
import com.projecte.mewnagochi.screens.home.HomeScreen
import com.projecte.mewnagochi.screens.login.LoginScreen
import com.projecte.mewnagochi.screens.login.User
import com.projecte.mewnagochi.screens.profile.ProfileScreen
import com.projecte.mewnagochi.screens.sign_up.RegisterScreen
import com.projecte.mewnagochi.screens.store.StoreScreen
import com.projecte.mewnagochi.services.notification.MyFirebaseMessagingService
import com.projecte.mewnagochi.stats.HealthConnectAvailability
import com.projecte.mewnagochi.stats.HealthConnectManager
import com.projecte.mewnagochi.stats.StatsViewModel
import com.projecte.mewnagochi.ui.StatsScreen
import com.projecte.mewnagochi.ui.theme.LabeledIcon
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mFMS: MyFirebaseMessagingService,
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
        LabeledIcon("Profile", Icons.Filled.Person) {
            ProfileScreen(onSuccess = {
                myViewModel.setNewSelected(0)
                navController.navigate("login")
            }, onResult = {
                Toast.makeText(context, it?.message, Toast.LENGTH_SHORT).show()
            }, mFMS = mFMS)
        },
    )
) {
    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(),
            onResult = {})

    Log.i("ROUTE", navController.currentDestination.toString())
    val selectedItem by myViewModel.navigationBarSelected.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val user by myViewModel.currentUser.collectAsState(initial = User())
    val userMoney by myViewModel.money.collectAsState(initial = null)
    Scaffold(topBar = {
        if (navigationBarItems.any { it.label == currentRoute } && currentRoute != "Profile") {
            TopAppBar(title = {
                userMoney?.let {
                    UserAppBar(
                        user = user.displayName, numOfCoins = it
                    )
                }
            })
        }
    },
        bottomBar = {
            if (navigationBarItems.any { it.label == currentRoute }) {
                NavigationBar {
                    navigationBarItems.forEachIndexed { index, item ->
                        NavigationBarItem(icon = {
                            Icon(
                                item.icon, contentDescription = item.label
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
                composable("login") {
                    LoginScreen(
                        onLoginFinished = { navController.navigate("Home") },
                        onRegister = { navController.navigate("register") },
                        onForgotPassword = { navController.navigate("forgot_password") },
                    )
                }
                composable("register") {
                    RegisterScreen() {
                        navController.navigate("login")
                    }
                }
                composable("forgot_password") {
                    ForgotPasswordScreen() {
                        navController.navigate("login")
                    }
                }
            }
        }

        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request notification permission
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

    }
}

@Preview
@Composable
fun UserAppBar(user: String = "user", modifier: Modifier = Modifier, numOfCoins: Long = 10L) {
    Column {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = user, style = MaterialTheme.typography.headlineLarge
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.coins),
                    contentDescription = "Coins",
                    Modifier.size(60.dp)
                )
                Text(
                    text = numOfCoins.toString(), style = MaterialTheme.typography.headlineLarge
                )
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
        if (!statsViewModel.checkPermissions(context) && !(healthConnectManager.availability.value == HealthConnectAvailability.NOT_SUPPORTED || healthConnectManager.availability.value == HealthConnectAvailability.NOT_INSTALLED)) {
            Log.i("permission", "first request")
            statsViewModel.requestPermissions(context = context)
        } else if (!(healthConnectManager.availability.value == HealthConnectAvailability.NOT_SUPPORTED || healthConnectManager.availability.value == HealthConnectAvailability.NOT_INSTALLED)) {
            statsViewModel.getData(scope, healthConnectManager)
        }
    }
}