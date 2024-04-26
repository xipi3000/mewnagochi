package com.projecte.mewnagochi.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.projecte.mewnagochi.login.LoginViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(viewModel: LoginViewModel = viewModel()){
    val uiState by viewModel.uiState
    Column()
    {
        TextField(value = uiState.email, onValueChange = viewModel::onEmailChange)
        TextField(value = uiState.password, onValueChange = viewModel::onPasswordChagne)
        Button(onClick = viewModel::createUser) {
            Text("Register")


        }
    }
}