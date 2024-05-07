package com.projecte.mewnagochi.screens.login

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.projecte.mewnagochi.R
import com.projecte.mewnagochi.ui.theme.EmailTextField
import com.projecte.mewnagochi.ui.theme.PasswordTextField


@Preview
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onRegister: () -> Unit = {},
    onForgotPassword: () -> Unit = {},
    onLoginFinished: () -> Unit = {},
) {

    val uiState by viewModel.uiState
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)

            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken,null)
            viewModel.loginUserWithGoogle(credential, onSuccess = onLoginFinished)

    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()

        )
    {
        if (uiState.loginFinished) {
            onLoginFinished()
        } else {
            EmailTextField(uiState.email,viewModel::onEmailChange)
            PasswordTextField(uiState.password,viewModel::onPasswordChange)


            Text(
                text = stringResource(R.string.forgot_your_password), color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier.clickable {
                    onForgotPassword()
                }
            )
            Text(text = uiState.errorMessage, color = Color.Red)
            Row(
                horizontalArrangement = Arrangement.SpaceAround,

            ) {
                OutlinedButton(onClick = onRegister) {
                    Text("Create\naccount")
                }
                Button(onClick = viewModel::loginUser) {
                    Text("LogIn")
                }
            }
            val context =  LocalContext.current
            Button(onClick = {

                val token = "855110736657-n62e0c5ukhnt3f66ughnm0hs5b66bdf6.apps.googleusercontent.com"
                val options = GoogleSignInOptions.Builder(
                    GoogleSignInOptions.DEFAULT_SIGN_IN
                )
                    .requestIdToken(token)
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(context,options)
                launcher.launch(googleSignInClient.signInIntent)

            }) {
                Text(text = "Google")
            }


        }
    }

}
