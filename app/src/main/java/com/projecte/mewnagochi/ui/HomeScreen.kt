package com.projecte.mewnagochi.ui

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.projecte.mewnagochi.MyViewModel
import com.projecte.mewnagochi.PersonViewModel
import com.projecte.mewnagochi.ui.theme.Person

@Composable
fun HomeScreen(){
    val person1 = Person()
    person1.BuildSprite()
    person1.Draw()


}