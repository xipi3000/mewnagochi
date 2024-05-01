package com.projecte.mewnagochi.login.forgotPassword

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.projecte.mewnagochi.login.EmailTextField

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel = viewModel(),
    onSentFinished: () -> Unit = {},
){
    val emailSent by viewModel.emailSent
    val email by viewModel.email
    Column {


    if(emailSent) {
        Text("A email has been sent to $email")
        Button(onClick = onSentFinished) {
            Text(text = "Go back")
        }
    }
    else{
        EmailTextField(email = email, onValueChange = viewModel::onEmailChange)
        Button(onClick = viewModel::sendEmail) {
            Text(text = "Send")
        }
    }
    }
}