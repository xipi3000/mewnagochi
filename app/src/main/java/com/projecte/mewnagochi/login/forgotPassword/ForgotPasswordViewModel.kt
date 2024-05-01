package com.projecte.mewnagochi.login.forgotPassword

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.projecte.mewnagochi.login.AccountServiceImpl


class ForgotPasswordViewModel: ViewModel() {
    var email: MutableState<String> = mutableStateOf("")
        private set
    var emailSent: MutableState<Boolean> = mutableStateOf(false)
        private set

    fun onEmailChange(email : String){
        this.email.value = email
    }
    fun sendEmail() {
        AccountServiceImpl().changePassword(email.value) { error ->
            if (error == null) {
                emailSent.value = true
            } else {
                Log.e("", error.toString())
            }

        }
    }
}