package com.projecte.mewnagochi.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import io.reactivex.plugins.RxJavaPlugins.onError

data class LoginUiState(
    val email: String = "",
    val password: String = ""
)
class LoginViewModel : ViewModel(){
    var uiState = mutableStateOf(LoginUiState())
        private set

    fun onEmailChange(email : String){
        uiState.value = uiState.value.copy(email = email)
    }
    fun onPasswordChagne(password: String){
        uiState.value = uiState.value.copy(password = password)
    }

    fun createUser() {
        val accountService = AccountServiceImpl()
        accountService.createAccount(uiState.value.email,uiState.value.password){
                error ->
            if (error == null) {

            } else onError(error)
        }
    }
}