package com.projecte.mewnagochi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.projecte.mewnagochi.screens.main.MainScreen
import com.projecte.mewnagochi.services.notification.MyFirebaseMessagingService
import com.projecte.mewnagochi.ui.theme.MewnagochiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mFMS = MyFirebaseMessagingService()


        setContent {
            MewnagochiTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    //MyNavGraph(context = this, activity = this, scope = this.lifecycleScope)
                    MainScreen(context = this, activity = this, mFMS = mFMS)
                }
            }
        }
    }
}


