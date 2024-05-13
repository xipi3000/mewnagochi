package com.projecte.mewnagochi.screens.profile

import android.graphics.BitmapFactory
import android.graphics.Color.parseColor
import android.graphics.ImageDecoder.createSource
import android.graphics.ImageDecoder.decodeBitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.firebase.storage.StorageReference
import com.projecte.mewnagochi.R
import com.projecte.mewnagochi.screens.login.User
import com.projecte.mewnagochi.services.storage.StorageServiceImpl
import com.projecte.mewnagochi.services.storage.UserPreferences
import com.projecte.mewnagochi.ui.theme.PersonState
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onSuccess: () -> Unit = {},
    onResult: (Throwable?) -> Unit = {}
) {
    val currentUser by viewModel.currentUser.collectAsState(initial = User())
    val usersMoney by viewModel.money.collectAsState(initial = 0L)
    val userPreferences by viewModel.userPreferences.collectAsState(initial = UserPreferences())

    val uiState by viewModel.uiState

    //TODO: FOTO DE PERFIL DE USUARI

    //TODO: CHOOSE INTERNET

    when {
        uiState.selectProfilePhoto -> {
            ProfilePictureDialog(
                onDismissRequest = { viewModel.onSelectProfilePhotoChange(false) },
                onConfirmation = {
                    viewModel.onSelectProfilePhotoChange(false)

                },
                dialogTitle = "Logging out",
                dialogText = "You are logging out of the app, your session will be closed",
                icon = Icons.Default.Info
            )
        }
    }
    Column(
        Modifier
            .padding(20.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {

        val profilePictures by viewModel.photoList.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.getProfilePictures()
        }

        // Now you can use the profilePictures list in your UI
        // For example:
        val ONE_MEGABYTE: Long = 1024 * 1024

        profilePictures.forEach {
            var pgoot by remember {
                mutableStateOf<ImageBitmap?>(null)
            }
            var islandRef = it
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {

                pgoot = BitmapFactory.decodeByteArray(it , 0, it.size).asImageBitmap()
            }.addOnFailureListener {
                // Handle any errors
            }


            if(pgoot!=null)
            Image(
                painter = BitmapPainter(pgoot!!),
                contentDescription = "contentDescription",

            )
            var uri: Uri
            /*it.downloadUrl.onSuccessTask {
                uri = it
            }*/
            GlideImage(
                model = it,
                contentDescription = "getString(R.id.picture_of_cat)",

                )

        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier
                .clip(CircleShape)
                .clickable {


                })

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
                    title = "Running Goal:",
                    goal = uiState.runningGoal,
                    onValueChange = viewModel::onRunningGoalChanged,
                    onValueChangeFinished = {
                        viewModel.onRunningGoalSet(
                            userPreferences!!
                        )
                    },
                    maxRangeGoal = 30f,
                    isUnit = false,
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
                        ),
                    painter = painterResource(id = R.drawable.pfp),
                    contentDescription = "adventurer"
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
                        ),
                    painter = painterResource(id = R.drawable.pfp_f), contentDescription = "witch"
                )
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
    title : String,
    goal : Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)?,
    maxRangeGoal: Float,
    isUnit : Boolean,
){
    Column {
        Text(text = title)
        Slider(
            value = goal,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            valueRange = 0f..maxRangeGoal
        )
        if(isUnit) Text(text = "%.0f".format(goal) + "km")
        else Text(text = "%.2f".format(goal) + "km")
    }
}
@Composable
fun ProfilePictureDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String= "Choose your profile picture:",
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
           /* LazyHorizontalGrid(rows = GridCells.Fixed(2)) {
                items(photos) { photo ->
                    PhotoItem(photo)
                }
            }*/
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
