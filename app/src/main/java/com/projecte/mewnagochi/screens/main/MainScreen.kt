package com.projecte.mewnagochi.screens.main

import android.app.Activity
import android.content.Context
import android.graphics.Color.parseColor
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
import com.projecte.mewnagochi.services.storage.FirebaseStorageProvider
import com.projecte.mewnagochi.stats.HealthConnectAvailability
import com.projecte.mewnagochi.stats.HealthConnectManager
import com.projecte.mewnagochi.stats.StatsViewModel
import com.projecte.mewnagochi.ui.StatsScreen
import com.projecte.mewnagochi.ui.theme.LabeledIcon


@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mFMS: MyFirebaseMessagingService,
    myViewModel: MyViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
    context: Context,
    sVM: StatsViewModel,
    sHS: SnackbarHostState,
    hcm: HealthConnectManager,
    navigationBarItems: List<LabeledIcon> = listOf(
        LabeledIcon("Home", Icons.Filled.Home) {
            HomeScreen(mFMS=mFMS)
        },
        LabeledIcon("Stats", Icons.Filled.Info) {
            StatisticsScreen(context = context, sVM = sVM, sHS = sHS, hcm = hcm)
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

    val imageBitmap by myViewModel.profilePicture.collectAsState(initial = ImageBitmap(1,1))
    val networkOnline by    FirebaseStorageProvider.enableFlow.collectAsState(initial = false)
    Scaffold(
        topBar = {
            if (navigationBarItems.any { it.label == currentRoute }&&currentRoute!="Profile") {
                val user by myViewModel.currentUser.collectAsState(initial = User())
                val userMoney by myViewModel.money.collectAsState(initial = null)
                TopAppBar(title = {
                        UserAppBar(
                            user=  user.displayName,
                            numOfCoins = userMoney?:0,
                            imageBitmap = imageBitmap
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

        Box(modifier = Modifier.padding(scaffoldPadding), contentAlignment = Alignment.TopCenter) {
            Column {

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
                            notifLauncher = notificationPermissionLauncher
                        )
                    }
                    composable("register") {
                        RegisterScreen()
                        {
                            navController.navigate("login")
                        }
                    }
                    composable("forgot_password") {
                        ForgotPasswordScreen()
                        {
                            navController.navigate("login")
                        }
                    }
                }

            }
            if(!networkOnline&&navigationBarItems.any { it.label == currentRoute }) {
                Card(
                    modifier = Modifier.padding(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(parseColor("#aff74848")),

                        ),
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(5.dp),
                        text = "Network not available,\nplease reconnect"
                    )
                }
            }


        }

    }
}

@Composable
fun StatisticsScreen(context: Context, sVM: StatsViewModel, sHS: SnackbarHostState, hcm: HealthConnectManager) {
    StatsScreen(statsViewModel = sVM, snackbarHostState = sHS, healthConnectMannager = hcm)
    LaunchedEffect(true) {
        //if no permisions -> request
        if (!sVM.checkPermissions(context) && !(hcm.availability.value == HealthConnectAvailability.NOT_SUPPORTED ||
                    hcm.availability.value == HealthConnectAvailability.NOT_INSTALLED)) {
            Log.i("permission", "first request")
            sVM.requestPermissions(context = context)
        } else if (!(hcm.availability.value == HealthConnectAvailability.NOT_SUPPORTED ||
                    hcm.availability.value == HealthConnectAvailability.NOT_INSTALLED)){
            sVM.getData(hcm)
        }
    }
}

@Composable
fun UserAppBar(user:String ="user", modifier: Modifier = Modifier,numOfCoins:Long=10L,
               imageBitmap : ImageBitmap
               ) {

        Column(modifier = modifier.padding(end = 10.dp)) {


            Row(
                modifier = modifier
                    .fillMaxWidth(),

                verticalAlignment = Alignment.CenterVertically,
            ) {



                Image(
                    painter = BitmapPainter(imageBitmap),
                    contentDescription = "contentDescription",
                    modifier = Modifier
                        .padding(10.dp)
                        .clip(CircleShape)
                        .size(60.dp)


                )
                Text(
                    text = user,
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.weight(1F))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.coins),
                        contentDescription = "Coins",
                        Modifier.size(60.dp)
                    )
                    Text(
                        text = numOfCoins.toString(),
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
    }
}