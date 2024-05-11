package com.projecte.mewnagochi.screens.profile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projecte.mewnagochi.screens.login.LoginUiState
import com.projecte.mewnagochi.screens.login.User
import com.projecte.mewnagochi.services.auth.AccountServiceImpl
import com.projecte.mewnagochi.services.storage.StorageServiceImpl
import com.projecte.mewnagochi.services.storage.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
data class ProfileUiState(
    val openAlertDialog: Boolean = false,
    val runningGoal: Float = 0F,
)

class ProfileViewModel : ViewModel() {
    var uiState = mutableStateOf(ProfileUiState())
        private set
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

    fun onOpenAlertDialogChange(value: Boolean) {
        uiState.value = uiState.value.copy(openAlertDialog =value)
    }

    fun onRunningGoalChanged(value : Float) {
        uiState.value = uiState.value.copy(runningGoal =value)

    }

    fun onRunningGoalSet(preferences: UserPreferences) {
        storageService.updatePreferences(preferences.copy(runningGoal = uiState.value.runningGoal)) {

        }
    }

}
