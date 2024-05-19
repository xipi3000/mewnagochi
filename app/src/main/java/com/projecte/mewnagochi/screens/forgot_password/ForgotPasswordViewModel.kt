package com.projecte.mewnagochi.screens.forgot_password

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.projecte.mewnagochi.services.auth.AccountServiceImpl


class ForgotPasswordViewModel: ViewModel() {
    var email: MutableState<String> = mutableStateOf("")
        private set
    var emailSent: MutableState<Boolean> = mutableStateOf(false)
        private set
    var errorMessage: MutableState<String> = mutableStateOf("")
        private set

    fun onEmailChange(email : String){
        this.email.value = email
    }
    fun sendEmail() {
        AccountServiceImpl().changePassword(email.value) { error ->
            if (error == null) {
                emailSent.value = true
                errorMessage.value= ""
            } else {
                if(email.value=="")errorMessage.value= "Email can't be blank"
                else if(error.toString().contains("network"))errorMessage.value= "Internet connection needed"
                else errorMessage.value= error.toString()

            }

        }
    }
}