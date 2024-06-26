package com.projecte.mewnagochi.services.auth

import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.projecte.mewnagochi.screens.login.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow


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


    override val isUserSignedIn: Boolean

        get() = Firebase.auth.currentUser!=null

    override val currentUser: Flow<User>

        get() =
            if(NetworkConnection.isAvailable) {
                callbackFlow {
                    val listener =
                        FirebaseAuth.AuthStateListener { auth ->

                            this.trySend(auth.currentUser?.let {
                                try {
                                    User(
                                        it.uid, it.email!!, it.displayName!!

                                    )
                                } catch (e: Exception) {
                                    User()
                                }
                            } ?: User())
                        }
                    Firebase.auth.addAuthStateListener(listener)
                    awaitClose { Firebase.auth.removeAuthStateListener(listener) }
                }
            }
            else {
                flow{
                    emit(
                        User(
                            Firebase.auth.currentUser!!.uid,
                            Firebase.auth.currentUser!!.email!!, Firebase.auth.currentUser!!.displayName!!
                        ))
                }
            }

    override fun signOut(onSuccess: () -> Unit,onResult: (Throwable?) -> Unit) {
        try{
            Firebase.auth.signOut()

            onSuccess()
        }
        catch (e:Exception){
            onResult(e)
        }
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

                                task.result.user!!.updateProfile(profileUpdates)
                                    .addOnCompleteListener {
                                        if (it.isComplete) {
                                            task.result.user!!.sendEmailVerification()
                                                .addOnCompleteListener {
                                                    task.result.user!!.uid
                                                    onResult(it.exception)
                                                }
                                        } else {
                                            onResult(it.exception)
                                        }
                                    }
                            } catch (e: Exception) {
                                onResult(e)
                            }


                        } else {
                            onResult(task.exception)
                        }
                    }

            } catch (e: Exception) {
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

    override fun authenticateWithGoogle(
        credential: AuthCredential,
        onResult: (Throwable?) -> Unit,
    ) {
        try {
            Firebase.auth.signInWithCredential(credential)
                .addOnCompleteListener {
                    onResult(it.exception)
                }
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
          return  Firebase.auth.currentUser?.isEmailVerified ?: false
    }

    override fun changePassword(email: String, onResult: (Throwable?) -> Unit ) {
        try {
            Firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { onResult(it.exception) }
        }catch (e : Exception){
            onResult(e)
        }
    }

    override fun getUserId(): String {
        return Firebase.auth.currentUser?.uid?:""
    }
    override fun getUserName(): String {
        return Firebase.auth.currentUser?.displayName?:""
    }

}

object NetworkConnection{
    var isAvailable = false
}