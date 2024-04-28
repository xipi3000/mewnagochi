package com.projecte.mewnagochi.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.projecte.mewnagochi.login.LoginViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Preview
@Composable
fun RegisterFinished() {
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
    }
}
@Preview
@Composable
fun RegisterScreen(
    viewModel: LoginViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.uiState

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    )
    {
        if (uiState.loginFinished) {
            RegisterFinished()
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