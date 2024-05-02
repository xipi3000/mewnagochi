package com.projecte.mewnagochi.screens.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.projecte.mewnagochi.R
import com.projecte.mewnagochi.ui.theme.EmailTextField
import com.projecte.mewnagochi.ui.theme.PasswordTextField


@Preview
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onRegister: () -> Unit = {},
    onForgotPassword: () -> Unit = {},
    onLoginFinished: () -> Unit = {},
) {

    val uiState by viewModel.uiState

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()

        )
    {
        if (uiState.loginFinished) {
            onLoginFinished()
        } else {
            EmailTextField(uiState.email,viewModel::onEmailChange)
            PasswordTextField(uiState.password,viewModel::onPasswordChange)


            Text(
                text = stringResource(R.string.forgot_your_password), color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier.clickable {
                    onForgotPassword()
                }
            )
            Text(text = uiState.errorMessage, color = Color.Red)
            Row(
                horizontalArrangement = Arrangement.SpaceAround,

            ) {
                OutlinedButton(onClick = onRegister) {
                    Text("Create\naccount")
                }
                Button(onClick = viewModel::loginUser) {
                    Text("LogIn")
                }
            }


        }
    }

}
