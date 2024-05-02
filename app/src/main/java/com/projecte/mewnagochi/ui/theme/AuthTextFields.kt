package com.projecte.mewnagochi.ui.theme

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation

@Composable
fun PasswordTextField(password: String, onValueChange: (String) -> Unit){
    OutlinedTextField(
        value = password,
        onValueChange = onValueChange,
        label = { Text(text = "Password") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        visualTransformation = PasswordVisualTransformation(),
    )
}
@Composable
fun EmailTextField(email: String, onValueChange: (String) -> Unit){
    OutlinedTextField(
        value = email,
        onValueChange = onValueChange,
        label = { Text(text = "Email") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done
        ),
    )
}