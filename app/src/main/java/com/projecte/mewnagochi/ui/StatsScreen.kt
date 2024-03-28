package com.projecte.mewnagochi.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.projecte.mewnagochi.StatsViewModel
import com.projecte.mewnagochi.health_connect.HealthConnectManager
import kotlinx.coroutines.CoroutineScope

@Composable
fun StatsScreen(
    myViewModel: StatsViewModel = viewModel(),
    healthConnectManager: HealthConnectManager,
    context: Context,
    scope: CoroutineScope,
) {
    Scaffold(

    ) { scaffoldPadding ->
        Column(
            modifier = Modifier.padding(scaffoldPadding)
        ) {
            val response = if(myViewModel.isResponseInitialized()) {
                myViewModel.response.toString()
            } else {
                "No data"
            }
            Text("Steps: ${response}")
        }
    }
}
