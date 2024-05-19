package com.projecte.mewnagochi.screens.forgot_password

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.projecte.mewnagochi.ui.theme.EmailTextField

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel = viewModel(),
    onSentFinished: () -> Unit = {},
){
    val emailSent by viewModel.emailSent
    val email by viewModel.email
    val errorMessage by viewModel.errorMessage
    Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxHeight()
        ) {


    if(emailSent) {
        Text("A email has been sent to $email")
        Button(onClick = onSentFinished) {
            Text(text = "Go back")
        }
    }
    else{
        EmailTextField(email = email, onValueChange = viewModel::onEmailChange)
        Text(text = errorMessage, color = Color.Red)
        Row(horizontalArrangement = Arrangement.SpaceAround,modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = onSentFinished) {
                Text("Cancel")
            }
            Button(onClick = viewModel::sendEmail) {
                Text(text = "Send")
            }
        }
    }
    }
}