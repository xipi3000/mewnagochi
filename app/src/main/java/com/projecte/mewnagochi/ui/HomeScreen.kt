package com.projecte.mewnagochi.ui

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.projecte.mewnagochi.MyViewModel
import com.projecte.mewnagochi.PersonViewModel
import com.projecte.mewnagochi.R
import com.projecte.mewnagochi.ui.theme.Person
import com.projecte.mewnagochi.ui.theme.Person1

@Composable
fun HomeScreen(){
    val person1 = Person1()
    Image(
        painter = painterResource(id = R.drawable.phone_backgrounds),
        contentDescription = "Background",
        contentScale = ContentScale.FillBounds,
        modifier = Modifier.fillMaxSize()
    )
    person1.BuildSprite()
    person1.Draw()


}