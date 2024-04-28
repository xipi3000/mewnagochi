package com.projecte.mewnagochi.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val repeatedPassword: String ="",
    val username: String = "",
    val errorMessage: String ="",
    val loginFinished: Boolean = false,
)
class RegisterViewModel : ViewModel(){
    var uiState = mutableStateOf(RegisterUiState())
        private set

    fun onEmailChange(email : String){
        uiState.value = uiState.value.copy(email = email)
    }
    fun onPasswordChange(password: String){
        uiState.value = uiState.value.copy(password = password)
    }
    fun onUsernameChange(username: String){
        uiState.value = uiState.value.copy(username = username)
    }

    fun onErrorMessage(errorMessage: String){
        uiState.value = uiState.value.copy(errorMessage = errorMessage)
    }
    fun onRepeatedPasswordChange(repeatedPassword: String){
        uiState.value = uiState.value.copy(repeatedPassword = repeatedPassword)
    }
    fun loginFinished(){
        uiState.value = uiState.value.copy(loginFinished = true)
    }
    fun createUser() {
        if(uiState.value.password.equals(uiState.value.repeatedPassword)) {
            val accountService = AccountServiceImpl()
            accountService.createAccount(
                uiState.value.email,
                uiState.value.password,
                uiState.value.username
            ) { error ->
                if (error == null) {
                    onErrorMessage("")
                    loginFinished()
                } else {
                    onErrorMessage(error.cause?.message!!)
                    // onError(error)
                }
            }
        }
        else {
            onErrorMessage("Passwords don't match")
        }
    }
}