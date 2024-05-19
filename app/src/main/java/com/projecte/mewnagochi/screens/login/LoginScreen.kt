package com.projecte.mewnagochi.screens.login

import android.Manifest
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.projecte.mewnagochi.R
import com.projecte.mewnagochi.ui.theme.EmailTextField
import com.projecte.mewnagochi.ui.theme.PasswordTextField


@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onRegister: () -> Unit = {},
    onForgotPassword: () -> Unit = {},
    onLoginFinished: () -> Unit = {},
    notifLauncher: ManagedActivityResultLauncher<String, Boolean>
) {
    notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    val currentUser by viewModel.currentUser.collectAsState(initial = User())
    if(viewModel.isUserLoggedIn() && currentUser.email!=""){
        onLoginFinished()
    }
    val uiState by viewModel.uiState
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        try{
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        val account = task.getResult(ApiException::class.java)
        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
        viewModel.loginUserWithGoogle(credential, onSuccess = onLoginFinished)
        }
        catch (e : Exception){
            Log.i("LoginScreen","No google account")
        }
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
                modifier = Modifier.fillMaxWidth(),
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
            Column(modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
                ) {
                Text(
                    text = "You can also login with:", color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium

                )
                Box (modifier = Modifier
                    .padding(5.dp)
                    .clip(CircleShape)
                    .background(Color.White)

                )

                    {
                    Image(
                        modifier = Modifier
                            .padding(5.dp)
                            .size(30.dp)
                            .clickable {
                                val token =
                                    "855110736657-n62e0c5ukhnt3f66ughnm0hs5b66bdf6.apps.googleusercontent.com"
                                val options = GoogleSignInOptions
                                    .Builder(
                                        GoogleSignInOptions.DEFAULT_SIGN_IN
                                    )
                                    .requestIdToken(token)
                                    .requestEmail()
                                    .build()
                                val googleSignInClient = GoogleSignIn.getClient(context, options)
                                launcher.launch(googleSignInClient.signInIntent)
                            },
                        painter = painterResource(id = R.drawable.google_ic),
                        contentDescription = ""
                    )
                }


            }
        }
    }

}
