package com.projecte.mewnagochi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.projecte.mewnagochi.screens.main.MainScreen
import com.projecte.mewnagochi.services.notification.MyFirebaseMessagingService
import com.projecte.mewnagochi.ui.theme.MewnagochiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mFMS = MyFirebaseMessagingService()
        mFMS.checkToken()
        setContent {
            MewnagochiTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    //MyNavGraph(context = this, activity = this, scope = this.lifecycleScope)
                    MainScreen(context = this, activity = this, scope = this.lifecycleScope)
                }
            }
        }
    }/*@Composable
    fun MyNavGraph(
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
                ProfileScreen()
            },

            ),
        navController: NavHostController = rememberNavController()
    ){
        NavHost(
            navController = navController, startDestination = "login"
        ) {
            navigationBarItems.forEach { item ->
                composable(item.label) {
                    item.screen()
                }
            }
            composable("Start"){
                
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
    }*/
}


