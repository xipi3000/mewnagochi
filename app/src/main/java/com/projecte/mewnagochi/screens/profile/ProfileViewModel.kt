package com.projecte.mewnagochi.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projecte.mewnagochi.screens.login.User
import com.projecte.mewnagochi.services.auth.AccountServiceImpl
import com.projecte.mewnagochi.services.storage.StorageServiceImpl
import com.projecte.mewnagochi.services.storage.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    val accountService = AccountServiceImpl()
    val currentUser : Flow<User> = AccountServiceImpl().currentUser
    private val storageService = StorageServiceImpl()
    val money: Flow<Long> = storageService.money
    val userPreferences: Flow<UserPreferences?> = storageService.userPreferences
    fun logOut(onSuccess: () -> Unit,onResult: (Throwable?) -> Unit) {
        accountService.signOut(onSuccess, onResult)
    }
    fun setSkinWhitch(){
        viewModelScope.launch {
            var userPreferences = storageService.getUserPreferences()
            if(userPreferences==null) {
                userPreferences = UserPreferences()
            }
            userPreferences = userPreferences.copy(selectedSkin = "witch")
            storageService.updatePreferences(userPreferences){


            }
        }

    }

    fun setSkinAdventurer() {
        viewModelScope.launch {
            var userPreferences = storageService.getUserPreferences()
            if(userPreferences==null) {
                userPreferences = UserPreferences()
            }
            userPreferences =  userPreferences.copy(selectedSkin = "adventurer")
            storageService.updatePreferences(userPreferences){


            }
        }
    }

}
