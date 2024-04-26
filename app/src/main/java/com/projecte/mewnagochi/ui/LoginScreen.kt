package com.projecte.mewnagochi.ui

import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun LoginScreen(){
    var mail by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    TextField(value = "Mail", onValueChange = {
        mail = it
    } )
    TextField(value = "Password", onValueChange ={
        password = it
    } )
    Button(onClick = {


    }) {


    }
}