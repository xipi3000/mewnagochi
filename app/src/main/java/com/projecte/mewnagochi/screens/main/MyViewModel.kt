package com.projecte.mewnagochi.screens.main

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.projecte.mewnagochi.services.auth.AccountServiceImpl
import com.projecte.mewnagochi.screens.login.User
import com.projecte.mewnagochi.screens.profile.ONE_MEGABYTE
import com.projecte.mewnagochi.services.storage.StorageServiceImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class MyViewModel(
    private val savedStateHandle : SavedStateHandle
) :ViewModel() {
    private val storageService = StorageServiceImpl()
    val money: Flow<Long> = storageService.money
    val currentUser : Flow<User> = AccountServiceImpl().currentUser
    val navigationBarSelected = savedStateHandle.getStateFlow("navigationBarSelected",0)

    val profilePicture : Flow<ImageBitmap> get()= flow{
        val refe = storageService.getImage(storageService.getUserPreferences()!!.selectedPfp).getBytes(
            ONE_MEGABYTE
        ).await()
        emit(BitmapFactory.decodeByteArray(refe, 0, refe.size).asImageBitmap())
    }
    fun setNewSelected(index: Int) {
        savedStateHandle["navigationBarSelected"] = index
    }

}