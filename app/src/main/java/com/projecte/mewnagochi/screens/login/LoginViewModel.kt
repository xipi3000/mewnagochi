package com.projecte.mewnagochi.screens.login


import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential
import com.projecte.mewnagochi.services.auth.AccountServiceImpl

data class User(
    val id: String = "",
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
    val  accountService = AccountServiceImpl()
    var uiState = mutableStateOf(LoginUiState())
        private set
    val currentUser = accountService.currentUser
    fun isUserLoggedIn(): Boolean{




        if(accountService.verifyEmail()) {
            return accountService.isUserSignedIn
        }
        return false
    }
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

    fun loginUserWithGoogle(credential: AuthCredential, onSuccess: () -> Unit){
        val accountService = AccountServiceImpl()
        accountService.authenticateWithGoogle(credential){
            error ->
            if(error == null){
                    onSuccess()
            }
            else {
                Log.e("error",error.toString())
            }
        }
    }
    fun getCurrentUser():String{
        val accountService = AccountServiceImpl()
        return accountService.getUserId()
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
                    onErrorMessage(
                        if(error.toString().contains("network"))"Internet connection needed"
                        else error.message.toString())
                    // onError(error)
                }
            }
        }
}
