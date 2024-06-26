package com.projecte.mewnagochi.services.auth

import com.google.firebase.auth.AuthCredential
import com.projecte.mewnagochi.screens.login.User
import kotlinx.coroutines.flow.Flow

interface AccountService {
    val currentEmail : String
    val currentUser: Flow<User>
    val isUserSignedIn: Boolean
    fun createAccount(email: String, password: String,username: String,onResult: (Throwable?) -> Unit)
    fun authenticate(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun authenticateWithGoogle(credential: AuthCredential, onResult: (Throwable?) -> Unit)
    fun linkAccount(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun verifyEmail() : Boolean
    fun changePassword(email: String, onResult: (Throwable?) -> Unit)

    fun getUserId(): String
    fun signOut(onSuccess: () -> Unit,onResult: (Throwable?) -> Unit)

    fun getUserName(): String
}