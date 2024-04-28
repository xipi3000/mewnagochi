package com.projecte.mewnagochi.login

import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.auth.oAuthCredential
import com.google.firebase.auth.userProfileChangeRequest

interface AccountService {
    fun createAccount(email: String, password: String,username: String,onResult: (Throwable?) -> Unit)
    fun authenticate(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun linkAccount(email: String, password: String, onResult: (Throwable?) -> Unit)
}

data class UserRegisterData(
    val username: String,
    val password: String,
    val verificationPassword: String,
    val email : String,
    val country: String,

)

class AccountServiceImpl : AccountService {

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
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { onResult(it.exception) }
    }

    override fun linkAccount(email: String, password: String, onResult: (Throwable?) -> Unit) {
        val credential = EmailAuthProvider.getCredential(email, password)

        Firebase.auth.currentUser!!.linkWithCredential(credential)
            .addOnCompleteListener { onResult(it.exception) }
    }
}