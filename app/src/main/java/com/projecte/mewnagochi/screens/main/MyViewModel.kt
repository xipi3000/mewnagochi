package com.projecte.mewnagochi.screens.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.projecte.mewnagochi.services.auth.AccountServiceImpl
import com.projecte.mewnagochi.screens.login.User
import com.projecte.mewnagochi.services.storage.StorageServiceImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MyViewModel(
    private val savedStateHandle : SavedStateHandle
) :ViewModel() {
    private val storageService = StorageServiceImpl()
    val money: Flow<Long> = storageService.money
    val currentUser : Flow<User> = AccountServiceImpl().currentUser
    val navigationBarSelected = savedStateHandle.getStateFlow("navigationBarSelected",0)


    fun setNewSelected(index: Int) {
        savedStateHandle["navigationBarSelected"] = index
    }

}