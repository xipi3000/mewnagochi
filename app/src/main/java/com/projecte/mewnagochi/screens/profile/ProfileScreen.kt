package com.projecte.mewnagochi.screens.profile

import android.graphics.Color.parseColor
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.projecte.mewnagochi.R
import com.projecte.mewnagochi.screens.login.User
import com.projecte.mewnagochi.services.storage.UserPreferences
import com.projecte.mewnagochi.ui.theme.PersonState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onSuccess: () -> Unit = {},
    onResult: (Throwable?) -> Unit = {}
)
{
    val currentUser by viewModel.currentUser.collectAsState(initial = User())
    val usersMoney by viewModel.money.collectAsState(initial = 0L)
    val userPreferences by viewModel.userPreferences.collectAsState(initial = UserPreferences())
    var openAlertDialog by remember { mutableStateOf(false) }
    Column {

        Text(text = "Name: "+currentUser.displayName)
        Text(text = "Email: "+currentUser.email)
        Text(text = "Money: $usersMoney")



        Row(modifier = Modifier
            .height(100.dp)
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround){
            Image(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { viewModel.setSkinAdventurer() }
                    .then(
                        if (userPreferences?.selectedSkin == "adventurer") Modifier.border(
                            6.dp,
                            Color(parseColor("#f7b416")),
                            CircleShape
                        ) else Modifier.border(
                            3.dp,
                            Color.LightGray,
                            CircleShape
                        )
                    )
                ,
                painter = painterResource(id = R.drawable.pfp), contentDescription = "adventurer"
            )
            Image(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { viewModel.setSkinWhitch() }
                    .then(
                        if (userPreferences?.selectedSkin == "witch") Modifier.border(
                            6.dp,
                            Color(parseColor("#f7b416")),
                            CircleShape
                        ) else Modifier.border(
                            1.dp,
                            Color.LightGray,
                            CircleShape
                        )
                    )
                ,
                painter = painterResource(id = R.drawable.pfp_f), contentDescription = "witch"
            )
        }
        Button(onClick = {openAlertDialog=true}){
            Icon(Icons.Default.ExitToApp,
                modifier = Modifier.size(30.dp),
                contentDescription = "",
                )
            Text("Log Out")
        }

    }
    when {
        openAlertDialog -> {
            AlertDialogExample(
                onDismissRequest = { openAlertDialog = false },
                onConfirmation = {
                    openAlertDialog = false
                    viewModel.logOut(onSuccess,onResult)
                },
                dialogTitle = "Logging out",
                dialogText = "You are logging out of the app, your session will be closed",
                icon = Icons.Default.Info
            )
        }
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
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            OutlinedButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}
