package com.projecte.mewnagochi.ui.theme

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.projecte.mewnagochi.services.storage.StorageServiceImpl
import com.projecte.mewnagochi.services.storage.UserPreferences
import com.projecte.mewnagochi.ui.theme.PersonState
import kotlinx.coroutines.flow.Flow

class PersonInvitedViewModel (
    private val savedStateHandle : SavedStateHandle
) : ViewModel() {
    private val storageService = StorageServiceImpl()
    val userPreferences: Flow<UserPreferences?> = storageService.userPreferences
    val offsetX = savedStateHandle.getStateFlow("offsetX",0F)
    val personState = savedStateHandle.getStateFlow("personState",PersonState.IDLE)
    fun setOffsetX(value : Float) {
        savedStateHandle["offsetX"] = value
    }

    fun setState(personState : PersonState) {
        savedStateHandle["personState"] = personState
    }
    fun hideDialog(userPreferences: UserPreferences){
        storageService.updatePreferences(userPreferences.copy(notificationText = "")){}
    }
}