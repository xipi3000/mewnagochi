package com.projecte.mewnagochi.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.projecte.mewnagochi.login.RegisterViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.projecte.mewnagochi.R


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
            RegisterFinished(){
                onRegisterFinished()
            }
        } else {
            OutlinedTextField(
                value = uiState.email, 
                onValueChange = viewModel::onEmailChange,
                label = { Text(text = "Email")}
            )
            OutlinedTextField(value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text(text = "Password")})
            OutlinedTextField(value = uiState.repeatedPassword,
                onValueChange = viewModel::onRepeatedPasswordChange,
                label = { Text(text = "Repeat password")})
            OutlinedTextField(value = uiState.username, onValueChange = viewModel::onUsernameChange,
                label = { Text(text = "Username")})
            Text(text = uiState.errorMessage, color = Color.Red)
            Button(onClick = viewModel::createUser) {

                Text("Register")


            }
        }
    }
}
@Preview
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onRegister: () -> Unit = {},

    onLoginFinished: () -> Unit = {},
) {

    val uiState by viewModel.uiState

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,

    )
    {
        if (uiState.loginFinished) {
            onLoginFinished()
        } else {
            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text(text = "Email")}
            )
            OutlinedTextField(value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text(text = "Password")})


            Text(text = stringResource(R.string.forgot_your_password), color = Color.Gray,
                style = MaterialTheme.typography.bodySmall.copy(
                    textDecoration = TextDecoration.Underline
                )
            )
            Text(text = uiState.errorMessage, color = Color.Red)
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
                ){
                OutlinedButton(onClick = onRegister ){
                    Text("Create\naccount")
                }
                Button(onClick = viewModel::loginUser) {
                    Text("LogIn")
                }
            }


        }
    }

}
