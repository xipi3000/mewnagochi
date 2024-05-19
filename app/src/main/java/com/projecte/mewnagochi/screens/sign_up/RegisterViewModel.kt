package com.projecte.mewnagochi.screens.sign_up

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.projecte.mewnagochi.services.auth.AccountServiceImpl
import com.projecte.mewnagochi.services.storage.StorageServiceImpl

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
            if(uiState.value.email!= "" && uiState.value.password!="" && uiState.value.username!="") {
                val accountService = AccountServiceImpl()
                accountService.createAccount(
                    uiState.value.email,
                    uiState.value.password,
                    uiState.value.username
                ) { error ->
                    if (error == null) {
                        val storegeService = StorageServiceImpl()
                        storegeService.createPreferences(
                            userId = accountService.getUserId(),
                            onSuccess = {
                                onErrorMessage("")
                                loginFinished()
                            },
                            onResult = {
                                onErrorMessage(it?.message!!)
                            }

                        )

                    } else {
                        if(error.message.toString().contains("network")) {
                            onErrorMessage("Internet connection needed")
                        }
                        else{
                            try {
                                onErrorMessage(error.cause?.message!!)
                            } catch (e: Exception) {

                                onErrorMessage(error.message!!)
                            }
                        }
                        // onError(error)
                    }
                }
            }else{
                onErrorMessage("Fields still blank")
            }
        }
        else {
            onErrorMessage("Passwords don't match")
        }
    }
}