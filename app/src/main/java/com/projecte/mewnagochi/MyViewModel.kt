package com.projecte.mewnagochi

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.projecte.mewnagochi.login.AccountServiceImpl
import com.projecte.mewnagochi.login.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MyViewModel(
    private val savedStateHandle : SavedStateHandle
) :ViewModel() {

    val currentUser : Flow<User> = AccountServiceImpl().currentUser
    val navigationBarSelected = savedStateHandle.getStateFlow("navigationBarSelected",0)


    fun setNewSelected(index: Int) {
        savedStateHandle["navigationBarSelected"] = index
    }

}