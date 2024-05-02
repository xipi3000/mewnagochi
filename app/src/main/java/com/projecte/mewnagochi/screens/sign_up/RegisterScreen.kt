package com.projecte.mewnagochi.screens.sign_up

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.projecte.mewnagochi.ui.theme.EmailTextField
import com.projecte.mewnagochi.ui.theme.PasswordTextField


@Composable
fun RegisterFinished(
    onRegisterFinished: () -> Unit

) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        Text(
            text = "You have registered successfully, please verify your email",
            style = MaterialTheme.typography.titleLarge
        )
        Icon(
            Icons.Filled.CheckCircle,
            contentDescription = "check",
            tint = Color.Green,
            modifier = Modifier.size(70.dp)
        )
        Button(onClick = onRegisterFinished) {
            Text(text = "LogIn")
        }
    }

}

@Preview
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
    onRegisterFinished: () -> Unit = {},

    ) {
    val uiState by viewModel.uiState

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    )
    {
        if (uiState.loginFinished) {
            RegisterFinished() {
                onRegisterFinished()
            }
        } else {
            EmailTextField(email = uiState.email, onValueChange = viewModel::onEmailChange)
            PasswordTextField(
                password = uiState.password,
                onValueChange = viewModel::onPasswordChange,
            )
            PasswordTextField(
                password = uiState.repeatedPassword,
                onValueChange = viewModel::onRepeatedPasswordChange,
            )

            OutlinedTextField(value = uiState.username,
                onValueChange = viewModel::onUsernameChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                label = { Text(text = "Username") }

            )
            Text(text = uiState.errorMessage, color = Color.Red)
            Button(onClick = viewModel::createUser) {

                Text("Register")


            }
        }
    }
}