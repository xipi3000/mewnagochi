package com.projecte.mewnagochi.login

import kotlinx.coroutines.flow.Flow

interface AccountService {
    val currentEmail : String
    val currentUser: Flow<User>
    fun createAccount(email: String, password: String,username: String,onResult: (Throwable?) -> Unit)
    fun authenticate(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun linkAccount(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun verifyEmail() : Boolean
    fun changePassword(email: String, onResult: (Throwable?) -> Unit)

}