package com.projecte.mewnagochi.screens.login


import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.projecte.mewnagochi.services.auth.AccountServiceImpl

data class User(
    val id: String = "",
    val isAnonymous: Boolean = true,
    val email: String = "",
    val displayName: String = "",
)

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val errorMessage: String = "",
    val loginFinished: Boolean = false,
)

class LoginViewModel : ViewModel() {
    var uiState = mutableStateOf(LoginUiState())
        private set

    fun onEmailChange(email: String) {
        uiState.value = uiState.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        uiState.value = uiState.value.copy(password = password)
    }

    fun onErrorMessage(errorMessage: String) {
        uiState.value = uiState.value.copy(errorMessage = errorMessage)
    }

    fun loginFinished() {
        onErrorMessage("")

        uiState.value = uiState.value.copy(loginFinished = true)
        Log.i("ViewModel",uiState.value.loginFinished.toString())
    }



    fun loginUser() =
        if(uiState.value.email.isBlank() || uiState.value.password.isBlank()) onErrorMessage("Password or mail is empty")
        else {
            val accountService = AccountServiceImpl()
            accountService.authenticate(
                uiState.value.email,
                uiState.value.password,

                ) { error ->
                if (error == null) {
                    if(accountService.verifyEmail()) {
                        loginFinished()
                    }
                    else{
                        onErrorMessage("Please verify your email")
                    }
                } else {
                    onErrorMessage(error.message.toString())
                    // onError(error)
                }
            }
        }
}
