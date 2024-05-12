package com.projecte.mewnagochi.screens.profile

import android.graphics.Color.parseColor
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.projecte.mewnagochi.R
import com.projecte.mewnagochi.screens.login.User
import com.projecte.mewnagochi.services.notification.MyFirebaseMessagingService
import com.projecte.mewnagochi.services.storage.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onSuccess: () -> Unit = {},
    onResult: (Throwable?) -> Unit = {},
    mFMS: MyFirebaseMessagingService,
) {
    val currentUser by viewModel.currentUser.collectAsState(initial = User())
    val usersMoney by viewModel.money.collectAsState(initial = 0L)
    val userPreferences by viewModel.userPreferences.collectAsState(initial = UserPreferences())
    val uiState by viewModel.uiState.collectAsState()
    viewModel.mFMS = mFMS

    //PER FER QUE EL SLIDER SLIDEJI; COMENTAR AQUESTES 2 LINIES
    //uiState.stepsGoal = userPreferences?.stepsGoal ?: 0
    //uiState.notificationHour = userPreferences?.notificationHour ?: 20

    //TODO: FOTO DE PERFIL DE USUARI
    //TODO: PREFERENCES sliders
    //TODO: CHOOSE INTERNET
    Column(
        Modifier
            .padding(20.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = currentUser.displayName,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(bottom = 20.dp, top = 10.dp)
                )
                Text(
                    text = "Email: " + currentUser.email,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Text(
                    text = "Wallet: $usersMoney+ coins",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                GoalSlider(
                    title = "Daily Steps Objective:",
                    goal = uiState.stepsGoal.toFloat(),
                    onValueChange = viewModel::onStepsGoalChanged,
                    onValueChangeFinished = {
                        viewModel.onStepsGoalSet(
                            userPreferences!!
                        )
                    },
                    maxRangeGoal = 20000f,
                    isUnit = false,
                    unit = " steps"
                )
                Spacer(modifier = Modifier.height(16.dp))
                GoalSlider(
                    title = "At which time do you want to receive notifications?",
                    goal = uiState.notificationHour.toFloat(),
                    onValueChange = viewModel::onHourNotificationChanged,
                    onValueChangeFinished = {
                        viewModel.onHourNotificationSet(
                            userPreferences!!
                        )
                    },
                    maxRangeGoal = 24f,
                    isUnit = false,
                    unit = " h."
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            Text(
                text = "Select character:",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Row(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Image(modifier = Modifier
                    .clip(CircleShape)
                    .clickable { viewModel.setSkinAdventurer() }
                    .then(
                        if (userPreferences?.selectedSkin == "adventurer") Modifier.border(
                            6.dp, Color(parseColor("#f7b416")), CircleShape
                        ) else Modifier.border(
                            3.dp, Color.LightGray, CircleShape
                        )
                    ),
                    painter = painterResource(id = R.drawable.pfp),
                    contentDescription = "adventurer")
                Image(modifier = Modifier
                    .clip(CircleShape)
                    .clickable { viewModel.setSkinWhitch() }
                    .then(
                        if (userPreferences?.selectedSkin == "witch") Modifier.border(
                            6.dp, Color(parseColor("#f7b416")), CircleShape
                        ) else Modifier.border(
                            1.dp, Color.LightGray, CircleShape
                        )
                    ),
                    painter = painterResource(id = R.drawable.pfp_f),
                    contentDescription = "witch")
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
        Row(Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { viewModel.onOpenAlertDialogChange(true) },
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                )
            ) {
                Icon(
                    Icons.Default.ExitToApp,
                    modifier = Modifier.size(30.dp),
                    contentDescription = "",
                )
                Text("Log Out")
            }
        }
    }
    when {
        uiState.openAlertDialog -> {
            AlertDialogExample(
                onDismissRequest = { viewModel.onOpenAlertDialogChange(false) },
                onConfirmation = {
                    viewModel.onOpenAlertDialogChange(false)
                    viewModel.logOut(onSuccess, onResult)
                },
                dialogTitle = "Logging out",
                dialogText = "You are logging out of the app, your session will be closed",
                icon = Icons.Default.Info
            )
        }
    }
}


@Composable
fun GoalSlider(
    title: String,
    goal: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)?,
    maxRangeGoal: Float,
    isUnit: Boolean,
    unit: String,
) {
    Column {
        Text(text = title)
        Slider(
            value = goal,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            valueRange = 0f..maxRangeGoal
        )
        Text(text = "%d".format(goal.toInt()) + unit)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(icon = {
        Icon(icon, contentDescription = "Example Icon")
    }, title = {
        Text(text = dialogTitle)
    }, text = {
        Text(text = dialogText)
    }, onDismissRequest = {
        onDismissRequest()
    }, confirmButton = {
        OutlinedButton(onClick = {
            onConfirmation()
        }) {
            Text("Confirm")
        }
    }, dismissButton = {
        Button(onClick = {
            onDismissRequest()
        }) {
            Text("Dismiss")
        }
    })
}
