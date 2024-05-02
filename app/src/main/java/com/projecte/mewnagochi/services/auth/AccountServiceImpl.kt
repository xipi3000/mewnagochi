package com.projecte.mewnagochi.services.auth

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.projecte.mewnagochi.screens.login.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow




data class UserRegisterData(
    val username: String,
    val password: String,
    val verificationPassword: String,
    val email : String,
    val country: String,

)

class AccountServiceImpl  : AccountService {
    override val currentEmail: String
        get() = Firebase.auth.currentUser?.email.toString()
    override val currentUser: Flow<User>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    Log.i("Auth",auth.currentUser.toString())
                    this.trySend(auth.currentUser?.let {
                        User(it.uid, it.isAnonymous, it.email!!,it.displayName!!

                        )
                   } ?: User())
                }
            Firebase.auth.addAuthStateListener(listener)
            awaitClose { Firebase.auth.removeAuthStateListener(listener) }
        }


    override fun createAccount(email: String, password: String,username: String, onResult: (Throwable?) -> Unit) {
        val profileUpdates = userProfileChangeRequest {
            displayName = username
        }
        try {
            Firebase.auth.createUserWithEmailAndPassword(email, password)

                .addOnCompleteListener { task ->
                    if (task.isComplete) {
                        try {

                            task.result.user!!.updateProfile(profileUpdates).addOnCompleteListener {
                                if (it.isComplete) {
                                    task.result.user!!.sendEmailVerification()
                                        .addOnCompleteListener {
                                            onResult(it.exception)
                                        }
                                } else {
                                    onResult(it.exception)
                                }
                            }
                        }catch (e:Exception){
                            onResult(e)
                        }


                    } else {
                        onResult(task.exception)
                    }
                }

        }
        catch (e: Exception){
            onResult(e)
        }

    }

    override fun authenticate(email: String, password: String, onResult: (Throwable?) -> Unit) {
        try {
            Firebase.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { onResult(it.exception) }
        }
        catch (e: Exception){
            onResult(e.cause)
        }

    }

    override fun linkAccount(email: String, password: String, onResult: (Throwable?) -> Unit) {
        val credential = EmailAuthProvider.getCredential(email, password)

        Firebase.auth.currentUser!!.linkWithCredential(credential)
            .addOnCompleteListener { onResult(it.exception) }
    }
    override fun verifyEmail() : Boolean{


          return  Firebase.auth.currentUser!!.isEmailVerified


    }

    override fun changePassword(email: String, onResult: (Throwable?) -> Unit ) {
        Firebase.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { onResult(it.exception) }
    }

}